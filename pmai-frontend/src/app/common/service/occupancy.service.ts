import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import { Camera } from '../model/camera.model';
import { Parklot } from '../model/parklot.model';
import { Occupancy } from '../model/occupancy.model';
@Injectable({
  providedIn: 'root'
})
export class OccupancyService {

  private url = 'http://localhost:8080/api/occupancy';

  constructor(private http: HttpClient) { }

  getCurrentOccupancy(cameraId: Number): Observable<Occupancy[]> {
    return this.http.get<Occupancy[]>(`${this.url}/${cameraId}`);
  }
}