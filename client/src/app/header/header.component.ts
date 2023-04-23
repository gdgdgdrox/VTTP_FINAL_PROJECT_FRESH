import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit{
  loggedInEmail = '';

  constructor(private userService: UserService){}

  ngOnInit(): void{
    this.userService.onUser.subscribe(email => {
      console.log(`email : ${email}`);
      this.loggedInEmail = email;
    });
  }


  logout(){
    console.log(`logging out ${this.loggedInEmail}`);
    //check if user has any deals that he wanted to save. if yes, save deals before logging out.
    if (this.userService.isLoggedIn && this.userService.dealIDsForSaving.length > 0){
      this.userService.saveUserDeal(this.userService.getLoggedInUserEmail(), this.userService.dealIDsForSaving);
    }
    this.loggedInEmail = '';
    this.userService.logout();
  }


}
