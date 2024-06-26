import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';  
import { CameraConfigPageComponent } from './camera-config-page/camera-config-page.component';
import { CameraViewPageComponent } from './camera-view-page/camera-view-page.component';

const routes: Routes = [
  {
    path: "view/:cameraId",
    component: CameraViewPageComponent
  },
  {
    path: "",
    component: HomePageComponent
  },
  {
    path: "config",
    component: CameraConfigPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
