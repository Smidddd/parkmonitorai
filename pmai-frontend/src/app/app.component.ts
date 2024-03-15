import { Component, inject, TemplateRef, ViewEncapsulation } from '@angular/core';
import { NgbModalRef, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from './common/service/user.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from "@angular/forms";

import { User } from './common/model/user.model';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None,
})

export class AppComponent {
  title = 'Park Monitor AI';

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
  
  getSessionRole() {
    return sessionStorage.getItem("role");
  }

  logout() {
    sessionStorage.removeItem("role");
  }
  //Area and camera logic
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
      <label><b>E-mail</b></label>
      <input type="text" formControlName="email" placeholder="Enter E-mail">
      <p></p>
      <label><b>Password</b></label>
      <input type="password" formControlName="password" placeholder="Enter Password">
      <p></p>
    </div>

    <button class="button3" type="submit" style="color: black;float: right; border-radius: 1px; ">
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

  constructor(private service: UserService, private router: Router) {
    this.formLogin = new FormGroup({
      email: new FormControl<string | null>(null, [Validators.required, Validators.email]),
      password: new FormControl<string | null>(null, Validators.required)
    })
  }

  loginUser(): void{
    this.service.verifyPassword({userEmail: this.formLogin.controls['email'].value, userPassword: this.formLogin.controls['password'].value}).subscribe((response: boolean) => {
      if (response){
        this.service.getUserByEmail(this.formLogin.controls['email'].value).subscribe((user: User) =>{
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
    if(this.user?.role == "ADMIN"){
      sessionStorage.setItem('role', "ADMIN")
    }
    if(this.user?.role == "USER"){
      sessionStorage.setItem('role', this.user?.role)
    }
  }

}