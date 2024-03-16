import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import { Area } from '../model/area.model';
@Injectable({
  providedIn: 'root'
})
export class AreaService {

  private url = 'http://localhost:8080/api/areas';

  constructor(private http: HttpClient) { }

  getCameras(): Observable<Area[]> {
    return this.http.get<Area[]>(this.url);
  }

  getCamera(areaId: number): Observable<Area> {
    return this.http.get<Area>(`${this.url}/${areaId}`);
  }

  createArea(area: Area): Observable<number> {
    return this.http.post<number>(this.url, area);
  }
}