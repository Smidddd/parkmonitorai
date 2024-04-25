import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import { Camera } from '../model/camera.model';
@Injectable({
  providedIn: 'root'
})
export class CameraService {

  private url = 'http://localhost:8080/api/cameras';

  constructor(private http: HttpClient) { }

  getCameras(): Observable<Camera[]> {
    return this.http.get<Camera[]>(this.url);
  }

  getCamera(cameraId: number): Observable<Camera> {
    return this.http.get<Camera>(`${this.url}/${cameraId}`);
  }

  createCamera(camera: Camera): Observable<number> {
    return this.http.post<number>(this.url, camera);
  }

  updateCamera(camera: Camera): Observable<Camera> {
    return this.http.put<Camera>
    (`${this.url}/${camera.id}`, camera);
  }

  deleteCamera(cameraId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${cameraId}`);
  }
}