import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import { Camera } from '../model/camera.model';
import { Parklot } from '../model/parklot.model';
@Injectable({
  providedIn: 'root'
})
export class ParklotService {

  private url = 'http://localhost:8080/api/parklots';

  constructor(private http: HttpClient) { }

  getParklots(): Observable<Parklot[]> {
    return this.http.get<Parklot[]>(this.url);
  }

  getParklotsForCamera(cameraId: Number): Observable<Parklot[]> {
    return this.http.get<Parklot[]>(`${this.url}/camera/${cameraId}`)
  }

  createParklots(parklots: Parklot[]): Observable<number> {
    return this.http.post<number>(this.url, parklots);
  }
}