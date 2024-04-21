import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OccupancyService } from '../common/service/occupancy.service';
import { CameraService } from '../common/service/camera.service';
import { Camera } from '../common/model/camera.model';
import { Occupancy } from '../common/model/occupancy.model';

@Component({
  selector: 'app-camera-view-page',
  templateUrl: './camera-view-page.component.html',
  styleUrls: ['./camera-view-page.component.css']
})
export class CameraViewPageComponent {
  private cameraId: number;
  private camera!: Camera;
  private occupancies!: Occupancy[];
  private allParklots: Number = 0;
  private vacantParklots: Number = 0;

  constructor(private router: Router, private route: ActivatedRoute, private service: OccupancyService, private cameraService: CameraService){
    this.cameraId=Number(route.snapshot.paramMap.get('cameraId'));
    this.cameraService.getCamera(this.cameraId).subscribe((camera: Camera) => {
     this.camera = camera;
     this.loadImage();
     
    })
    this.service.getCurrentOccupancy(this.cameraId).subscribe((occupancies: Occupancy[]) => {
     this.occupancies = occupancies;
     this.drawRectangles();
     this.allParklots = occupancies.length;
     this.vacantParklots = this.countVacantParklots();
    })
  }

  getAllParklots(){
    return this.allParklots
  }
  getVacantParklots(){
    return this.vacantParklots
  }

  countVacantParklots(){
    var count=0
    this.occupancies.forEach(element => {
      if (element.occupancy){
        count++
      }
    });
    return count
  }

  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D;
  private img!: HTMLImageElement;
  ngAfterViewInit() {
    // @ts-ignore
    this.ctx = this.canvasRef.nativeElement.getContext('2d');
  }

  loadImage() {
    this.img = new Image();
    this.img.src = this.camera.source;
    this.img.onload = () => {
      if (this.canvasRef.nativeElement) {
        this.canvasRef.nativeElement.width = this.img.width;
        this.canvasRef.nativeElement.height = this.img.height;
        this.ctx.drawImage(this.img, 0, 0, this.img.width, this.img.height);
      }
    };
  }

  drawRectangles() {
    this.ctx.clearRect(0, 0, this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height);
    this.ctx.drawImage(this.img, 0, 0, this.img.width, this.img.height);

    this.occupancies.forEach(parklot => {
      if (parklot.occupancy){
        this.ctx.beginPath();
        this.ctx.moveTo(parklot.parklot.geometry[0].x, parklot.parklot.geometry[0].y);
        this.ctx.lineTo(parklot.parklot.geometry[1].x, parklot.parklot.geometry[1].y);
        this.ctx.lineTo(parklot.parklot.geometry[2].x, parklot.parklot.geometry[2].y);
        this.ctx.lineTo(parklot.parklot.geometry[3].x, parklot.parklot.geometry[3].y);
        this.ctx.closePath();
        this.ctx.strokeStyle = 'green';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
      } else {
        this.ctx.beginPath();
        this.ctx.moveTo(parklot.parklot.geometry[0].x, parklot.parklot.geometry[0].y);
        this.ctx.lineTo(parklot.parklot.geometry[1].x, parklot.parklot.geometry[1].y);
        this.ctx.lineTo(parklot.parklot.geometry[2].x, parklot.parklot.geometry[2].y);
        this.ctx.lineTo(parklot.parklot.geometry[3].x, parklot.parklot.geometry[3].y);
        this.ctx.closePath();
        this.ctx.strokeStyle = 'red';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
      }
    });
  }

}
