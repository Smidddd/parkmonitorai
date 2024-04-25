import { Component, inject, numberAttribute, OnDestroy, TemplateRef, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from './common/service/user.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";

import { Role, User } from './common/model/user.model';
import { Camera } from './common/model/camera.model';
import { CameraService } from './common/service/camera.service';
import { Area } from './common/model/area.model';
import { AreaService } from './common/service/area.service';
import { ToastService } from './common/service/toast.service';
import { empty } from 'rxjs';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormField, MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatSelect, MatSelectModule } from '@angular/material/select';
import { AppModule } from './app.module';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None,
})

export class AppComponent{
  //Areas offcanvas
  private offcanvasService = inject(NgbOffcanvas);

  openOffCan(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { backdrop: 'static', panelClass: "offcanvas" });
  }

  //Login modal 
  modalService = inject(NgbModal);
  user?: User;

  //Login logic
  openLogin() {
    this.modalService.open(NgbdModalContent);
  }

  navigateConfig() {
    this.router.navigate(["config"]);
  }

  getSessionRole() {
    return sessionStorage.getItem("role");
  }

  deleteCamera(cameraId: number){
    if (window.confirm("Do you want to delete this camera ?")){
      this.cameraService.deleteCamera(cameraId).subscribe(() => {
        location.reload();
        alert("Produkt bol uspesne vymazany")
      });

    } else {
      alert("Produkt nebol vymazany")
    }
  }

  logout() {
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('userId');

    this.router.navigate([""])
  }
  //Area and camera logic
  cameras?: Camera[];
  areas?: Area[];


  constructor(private cameraService: CameraService, private areaService: AreaService, private router: Router, private userService: UserService, public dialog: MatDialog) {
    this.cameraService.getCameras().subscribe((cameras: Camera[]) => {
      this.cameras = cameras;
    });
    this.areaService.getAreas().subscribe((areas: Area[]) => {
      this.areas = areas;
    });
    if(sessionStorage.getItem('userId')){
      //@ts-ignore
      let userId = +sessionStorage.getItem('userId')
      this.userService.getUser(userId).subscribe((user: User) => {
        this.user = user;
      })
    }

  }

  navigateView(cameraId: Number){
    location.href = 'view/'+cameraId;
  }
  //@ts-ignore
  areaContainsUser(area: Area): boolean{
    //@ts-ignore
    return area.userEntities.some(({id}) => id === this.user?.id);
  }

  openModal(cameraId: Number) {
    this.offcanvasService.dismiss();
    localStorage.setItem('cameraId', cameraId.toString());
    const dialogRef = this.dialog.open(DialogContentExampleDialog);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }  
}





@Component({
  selector: 'ngbd-modal-content',
  standalone: false,
  template: `
		<div class="modal-body">
    <form [formGroup]="formLogin" (submit)="loginUser()">
    <div class="container" >
      <h1 style="padding-bottom: 1rem">Login</h1>
      <hr>
      <div style="display: flex; justify-content: space-between;">
        <label><b>E-mail</b></label>
        <input type="text" formControlName="email" placeholder="Enter E-mail">
      </div>
      <div style="display: flex; justify-content: space-between;">
        <label><b>Password</b></label>
        <input type="password" formControlName="password" placeholder="Enter Password">
      </div>
    </div>

    <button mat-raised-button type="submit" style="float: right;">
      Log in
    </button>

  </form>
		</div>
	`,
})
export class NgbdModalContent {
  activeModal = inject(NgbActiveModal);

  //Login logic
  formLogin: FormGroup
  user?: User

  constructor(private service: UserService) {
    this.formLogin = new FormGroup({
      email: new FormControl<string | null>(null, [Validators.required, Validators.email]),
      password: new FormControl<string | null>(null, Validators.required)
    })
  }

  getUser(): User{
    //@ts-ignore
    return this.user;
  }
  loginUser(): void {
    this.service.verifyPassword({ userEmail: this.formLogin.controls['email'].value, userPassword: this.formLogin.controls['password'].value }).subscribe((response: boolean) => {
      if (response) {
        this.service.getUserByEmail(this.formLogin.controls['email'].value).subscribe((user: User) => {
          this.user = user;
          this.authorizeUser();
          this.activeModal.close('Close click')
          location.reload();
        });
      } else {
        alert("Incorrect e-mail or password!");
      }
    });

  }
  authorizeUser(): void {
    if (this.user?.role == "ADMIN") {
      sessionStorage.setItem('role', "ADMIN")
      sessionStorage.setItem('userId', this.user.id.toString());
    }
    if (this.user?.role == "USER") {
      sessionStorage.setItem('role', this.user?.role)
    }
  }

}

@Component({
  selector: 'dialog-content-example-dialog',
  template: `
                  <mat-dialog-content>
                    <form [formGroup]="formUpdate" (submit)="saveUpdate(camera.id)" class="example-form">
                      <div class="form">
                      <mat-form-field class="example-full-width">
                        <mat-label>Name</mat-label>
                        <input [(ngModel)]="camera.name" matInput formControlName="name">
                      </mat-form-field>
                  
                      <mat-form-field class="example-full-width">
                        <mat-label>Latitude</mat-label>
                        <input [(ngModel)]="camera.lattitude" matInput formControlName="lattitude">
                      </mat-form-field>
                  
                      <mat-form-field class="example-full-width">
                        <mat-label>Longitude</mat-label>
                        <input [(ngModel)]="camera.longitude" matInput formControlName="longitude">
                      </mat-form-field>
                  
                          <mat-form-field class="example-full-width">
                        <mat-label>Source</mat-label>
                        <input [(ngModel)]="camera.source" matInput formControlName="source">
                      </mat-form-field>
                  
                          <mat-form-field >
                              <mat-label>Area</mat-label>
                              <mat-select formControlName="areaId" >
                                  <mat-option *ngFor="let area of areas" [value]="area" >{{area.name}}</mat-option>
                                </mat-select>
                          </mat-form-field>
                  
                          <button mat-raised-button type="submit">
                              Save
                          </button>
                        </div>
                    </form>
                  </mat-dialog-content>
                  <mat-dialog-actions>
                    <button mat-dialog-close mat-icon-button color="warn">
                      X
                    </button>
                    <button (click)="editParklots(camera.id)" mat-dialog-close mat-button color="warn">
                      Edit Parklots
                    </button>
                  </mat-dialog-actions>
		                
	`,
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatSelectModule, ReactiveFormsModule, CommonModule, MatInputModule],
})
export class DialogContentExampleDialog {
  areas!: Area[]
  formUpdate: FormGroup;
  camera!: Camera;

  constructor(private cameraService: CameraService, private areaService: AreaService){
    this.formUpdate = new FormGroup({
      name: new FormControl<string | null>(null, [Validators.required]),
      lattitude: new FormControl<number | null>(null, Validators.required),
      longitude: new FormControl<number | null>(null, Validators.required),
      source: new FormControl<string | null>(null, [Validators.required]),
      areaId: new FormControl<Area | null>(null, [Validators.required]),
    });
    this.cameraService.getCamera(Number(localStorage.getItem('cameraId'))).subscribe((camera: Camera) => {
      this.camera = camera
    });
    this.areaService.getAreas().subscribe((areas: Area[]) => {
      this.areas = areas;
    });
    
  }

  editParklots(cameraId: Number){
    sessionStorage.setItem('cameraId', cameraId.toString())
    location.href = '/config';
  }

  saveUpdate(cameraId: number): void{
    console.log("submit")
    if (this.formUpdate.valid) {
      console.log("valid")
      this.updateProduct(this.prepareUpdate(cameraId));
    }
  }
  private prepareUpdate(cameraId?: number): Camera {
    console.log("prepare update")
    return {
      id: Number(cameraId),
      name: this.formUpdate.controls['name'].value,
      lattitude: this.formUpdate.controls['lattitude'].value,
      longitude: this.formUpdate.controls['longitude'].value,
      source: this.formUpdate.controls['source'].value,
      areaId: this.formUpdate.controls['areaId'].value,
      status: 0
    };
  }
  updateProduct(camera: Camera): void{
    console.log("update product")
    this.cameraService.updateCamera(camera).subscribe(() => {
      location.reload();
    })
  }
}