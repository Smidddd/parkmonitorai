import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {LoginRequest, User} from "../model/user.model";
import {Observable} from "rxjs";
@Injectable({
  providedIn: 'root'
})
export class UserService {

  private url = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.url);
  }

  getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.url}/${userId}`);
  }

  getUserByEmail(userEmail: string): Observable<User> {
    return this.http.get<User>(`${this.url}/email/${userEmail}`);
  }

  verifyPassword(loginRequest: LoginRequest): Observable<boolean>{
    return this.http.post<boolean>(`${this.url}/verify`, loginRequest);
  }
}