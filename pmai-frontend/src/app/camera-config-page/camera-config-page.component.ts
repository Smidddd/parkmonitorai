import { AfterViewInit, ApplicationRef, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, NgZone, OnDestroy, TemplateRef, ViewChild, ViewEncapsulation, inject } from '@angular/core';
import { Area } from '../common/model/area.model';
import { AreaService } from '../common/service/area.service';
import { Router } from '@angular/router';
import { CameraService } from '../common/service/camera.service';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Camera } from '../common/model/camera.model';
import { Parklot, Point } from '../common/model/parklot.model';
import { ParklotService } from '../common/service/parklot.service';
import { ToastService } from '../common/service/toast.service';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-camera-config-page',
  templateUrl: './camera-config-page.component.html',
  styleUrls: ['./camera-config-page.component.css'],
})
export class CameraConfigPageComponent implements AfterViewInit {
  
  constructor(
    private cameraService: CameraService,
    private areaService: AreaService,
    private parklotService: ParklotService,
    private router: Router,
    private formBuilder: FormBuilder
    ) {
    //get all areas for selection when creating camera
    this.areaService.getCameras().subscribe((areas: Area[]) => {
      this.areas = areas;
    });
    //init camera form
    this.formCamera = new FormGroup({
      name: new FormControl<string | null>(null, [Validators.required]),
      lattitude: new FormControl<number | null>(null, Validators.required),
      longitude: new FormControl<number | null>(null, Validators.required),
      source: new FormControl<string | null>(null, [Validators.required]),
      areaId: new FormControl<Area | null>(null, [Validators.required]),
    });
    //get camera from backend by camId stored in session
    if (sessionStorage.getItem("cameraId") != null) {
      // @ts-ignore
      let cameraId = +sessionStorage.getItem("cameraId")
      this.cameraService.getCamera(cameraId).subscribe((camera: Camera) => {
        this.camera = camera;
        console.log("camera saved")
        this.loadImage();
      });
    }
    //init parklot form
    this.formParklots = this.formBuilder.group({
      parklotsArray: this.formBuilder.array([])
    });
  }
  //parklot form array logic
  parklots: Parklot[] = []
  formParklots!: FormGroup

  addItem() {
    const parklotsForm = this.formBuilder.group({
      latitude: ['', Validators.required],
      longitude: ['', Validators.required]
    });

    this.items.push(parklotsForm);
  }

  removeItem(index: number) {
    this.items.removeAt(index);
    this.parklots.splice(index, 1);
  }

  saveParklots() {
    if (this.formParklots.valid) {
      const parklotsArray = this.formParklots.controls['parklotsArray'] as FormArray;
      const parklotsData = parklotsArray.value;

      // Process and send parklotsData to backend
      this.createParklots(this.prepareParklots(parklotsData));
    } else {
      console.error('Form is invalid');
    }
  }

  prepareParklots(parklotsData: any[]): Parklot[] {
    console.log("prepare parklots");
    for(let i=0;i<this.parklots.length; i++){
      this.parklots[i].latitude = parklotsData[i].latitude;
      this.parklots[i].longitude = parklotsData[i].longitude;
    }

    return this.parklots;
  }

  createParklots(parklots: Parklot[]): void{
    this.parklotService.createParklots(parklots).subscribe((count: number) => {
      console.log("created "+count+" parklots successfully");
      sessionStorage.removeItem("cameraId");
      this.router.navigate(['']);
    })
  }

  get items() {
    return this.formParklots.controls['parklotsArray'] as FormArray;
  }
  //helper function
  getFormGroup(control: AbstractControl) { return control as FormGroup; }

  // camera form logic
  areas?: Area[]
  camera!: Camera
  formCamera: FormGroup

  saveCamera(): void {
    console.log("submit")
    if (this.formCamera.valid) {
      console.log("valid")
      this.createCamera(this.prepareCamera());
    }
  }
  private prepareCamera(id?: number): Camera {
    console.log("prepare camera")
    return {
      id: id !== undefined ? id : Date.now(),
      name: this.formCamera.controls['name'].value,
      lattitude: this.formCamera.controls['lattitude'].value,
      longitude: this.formCamera.controls['longitude'].value,
      source: this.formCamera.controls['source'].value,
      areaId: this.formCamera.controls['areaId'].value,
      status: 0
    };
  }
  createCamera(camera: Camera): void {
    console.log("create product")
    this.cameraService.createCamera(camera).subscribe((cameraId: number) => {
      console.log('Camera saved succesfully.');
      sessionStorage.setItem("cameraId", cameraId.toString())
      location.reload();
    })
  }
  getSessionCameraId() {
    return sessionStorage.getItem("cameraId");
  }

  //canvas logic
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D;
  private points: Point[] = [];
  private img!: HTMLImageElement;
  private scaleFactorX = 1;
  private scaleFactorY = 1;

  ngAfterViewInit() {
    // @ts-ignore
    this.ctx = this.canvasRef.nativeElement.getContext('2d');
    this.canvasRef.nativeElement.addEventListener('click', this.handleCanvasClick.bind(this));
  }

  loadImage() {
    this.img = new Image();
    this.img.src = this.camera.source;
    this.img.onload = () => {
      if (this.canvasRef.nativeElement) {
        this.canvasRef.nativeElement.width = this.img.width;
        this.canvasRef.nativeElement.height = this.img.height;
        this.ctx.drawImage(this.img, 0, 0, this.img.width, this.img.height);
        this.calculateScaleFactors(); 
      }
    };
  }

  calculateScaleFactors() {
    if (this.canvasRef) {
      const canvas = this.canvasRef.nativeElement;
      const rect = canvas.getBoundingClientRect();
      this.scaleFactorX = canvas.width / rect.width;
      this.scaleFactorY = canvas.height / rect.height;
    }
  }

  addParklot(parklot: Parklot) {
    this.addItem();
    console.log(this.formParklots.value)
    this.parklots.push(parklot);
  }

  removeParklot(index: number) {
    this.removeItem(index);
    this.drawRectangles();
  }

  handleCanvasClick(event: MouseEvent) {
    if (this.canvasRef.nativeElement) {

      const rect = this.canvasRef.nativeElement.getBoundingClientRect();
      const point = {
        x: Math.round((event.clientX - rect.left) * this.scaleFactorX),
        y: Math.round((event.clientY - rect.top) * this.scaleFactorY)
      }
      console.log(point)

      this.points.push(point);
      console.log(this.points)
      if (this.points.length === 4) {

        const parklot = {
          geometry: this.points,
          latitude: 0,
          longitude: 0,
          camera: this.camera
        }
        this.addParklot(parklot);
        console.log(this.parklots)
        this.drawRectangles();
        this.points = []
      }
    }
  }
  drawRectangles() {
    this.ctx.clearRect(0, 0, this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height);
    this.ctx.drawImage(this.img, 0, 0, this.img.width, this.img.height);

    this.parklots.forEach(parklot => {
      this.ctx.beginPath();
      this.ctx.moveTo(parklot.geometry[0].x, parklot.geometry[0].y);
      this.ctx.lineTo(parklot.geometry[1].x, parklot.geometry[1].y);
      this.ctx.lineTo(parklot.geometry[2].x, parklot.geometry[2].y);
      this.ctx.lineTo(parklot.geometry[3].x, parklot.geometry[3].y);
      this.ctx.closePath();
      this.ctx.strokeStyle = 'red';
      this.ctx.lineWidth = 2;
      this.ctx.stroke();
    });
  }




}
