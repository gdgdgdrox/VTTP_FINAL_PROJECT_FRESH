import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Deal, ExistingUser, NewUser } from './models';
import { Subject, firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  isLoggedIn = false;
  // consider refactoring to only use loggedInUserEmail as an indicator of whether user is logged in (default '' is false) and to get the user email
  loggedInUserEmail = '';
  onUser = new Subject<string>();
  dealIDsForSaving: string[] = [];
  constructor(private http: HttpClient, private router: Router) { }

  register(newUser: NewUser){
    return firstValueFrom(this.http.post<any>('https://mirthful-coal-production.up.railway.app/api/user/register', newUser));
  }

  login(existingUser: ExistingUser){
    return firstValueFrom(this.http.post<any>('https://mirthful-coal-production.up.railway.app/api/user/login', existingUser));
  }

  logout(){
    this.isLoggedIn = false;
    this.loggedInUserEmail = '';
    this.router.navigate(['/']);
  }

  getLoggedInUserEmail(){
    return this.loggedInUserEmail;
  }
  
  addDeal(uuid: string){
    this.dealIDsForSaving.push(uuid);
    console.log(this.dealIDsForSaving.length);
  }

  removeDeal(uuid: string){
    const index = this.dealIDsForSaving.findIndex(dealUUID => dealUUID === uuid);
    if (index !== -1) {
      this.dealIDsForSaving.splice(index, 1);
    }
    console.log(this.dealIDsForSaving.length);
  }

  saveUserDeal(email: string, dealIDsForSaving: string[]){
    const payload = {
      email: email,
      dealIDs: dealIDsForSaving
    };
    this.http.post('https://mirthful-coal-production.up.railway.app/api/user/deal/save', payload).subscribe({
      next: resp => {
        console.log(resp);
    }, error: err => {
        console.log(err);
    }});
  }

  getUserDeal(email: string){
    return this.http.post<Deal[]>('https://mirthful-coal-production.up.railway.app/api/user/deal/get', {email});
  }


  deleteUserDeal(email: string, dealID: string) {
    const url = 'https://mirthful-coal-production.up.railway.app/api/user/deal/delete';
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      body: {
        email,
        dealID,
      },
    };
    return firstValueFrom(this.http.delete(url, options));
  }
  
}
