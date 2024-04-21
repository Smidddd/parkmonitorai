import { Component, inject, OnDestroy, TemplateRef, ViewEncapsulation } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from './common/service/user.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from "@angular/forms";

import { User } from './common/model/user.model';
import { Camera } from './common/model/camera.model';
import { CameraService } from './common/service/camera.service';
import { Area } from './common/model/area.model';
import { AreaService } from './common/service/area.service';
import { ToastService } from './common/service/toast.service';


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

  logout() {
    sessionStorage.clear();
    this.router.navigate([""]);
  }
  //Area and camera logic
  cameras?: Camera[];
  areas?: Area[];

  constructor(private cameraService: CameraService, private areaService: AreaService, private router: Router) {
    this.cameraService.getCameras().subscribe((cameras: Camera[]) => {
      this.cameras = cameras;
    });
    this.areaService.getCameras().subscribe((areas: Area[]) => {
      this.areas = areas;
    });

  }

  navigateView(cameraId: Number){
    location.href = 'view/'+cameraId;
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

  loginUser(): void {
    this.service.verifyPassword({ userEmail: this.formLogin.controls['email'].value, userPassword: this.formLogin.controls['password'].value }).subscribe((response: boolean) => {
      if (response) {
        this.service.getUserByEmail(this.formLogin.controls['email'].value).subscribe((user: User) => {
          this.user = user;
          this.authorizeUser();
          this.activeModal.close('Close click')
        });
      } else {
        alert("Incorrect e-mail or password!");
      }
    });

  }
  authorizeUser(): void {
    if (this.user?.role == "ADMIN") {
      sessionStorage.setItem('role', "ADMIN")
    }
    if (this.user?.role == "USER") {
      sessionStorage.setItem('role', this.user?.role)
    }
  }

}