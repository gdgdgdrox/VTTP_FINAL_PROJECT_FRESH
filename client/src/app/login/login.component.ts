import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../user.service';
import { ExistingUser } from '../models';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = 'defaultuser@gmail.com';
  password = 'defaultuser';
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private userService: UserService){}

  login(form: NgForm){
    if (form.invalid){
      return;
    }
    this.isLoading = true;
    const existingUser = form.value as ExistingUser
    this.userService.login(existingUser).then(resp => {
                                          setTimeout(() => {
                                            this.userService.onUser.next(resp.email);
                                            this.userService.loggedInUserEmail = resp.email;
                                            this.userService.isLoggedIn = true;
                                            this.isLoading=false;
                                            this.successMessage = 'Successfully logged in!';
                                            this.errorMessage = '';
                                          }, 2*1000);
                                        })
                                        .catch(error => {
                                          if (error.status === 401){
                                            console.error(error);
                                            setTimeout(()=>{
                                              this.isLoading=false;
                                              this.errorMessage = error.error.message;
                                            },2*1000)
                                          }
                                        })
  }
}
