import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../user.service';
import { NewUser } from '../models';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {
  form!: FormGroup;
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private userService: UserService){}

  ngOnInit(): void {
    this.form = this.createForm();
  }

  createForm(){
    return this.fb.group({
      firstName: new FormControl('', [Validators.required]),
      lastName: new FormControl('', [Validators.required]),
      // TO DO : use a custom validator to ensure chosen date > current date
      dob: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      receiveUpdate: new FormControl(false)
    })
  }

  register(){
    this.isLoading = true;
    const user = this.form.value as NewUser;
    // console.log('user > ', user);
    this.userService.register(user)
      .then(response => {
        setTimeout(() => {
          this.userService.onUser.next(response.email);
          this.userService.loggedInUserEmail = response.email;
          this.userService.isLoggedIn = true;
          this.isLoading=false;
          this.successMessage = 'Account was successfully created!'
          this.errorMessage = '';
        },2*1000)
      })
      .catch(error => {
        console.log(error);
        setTimeout(() => {
          this.isLoading=false;
          this.errorMessage = error.error.message;
        }, 2*1000)
      });
  }

  // dateValidator (control: FormControl) {
  //   const date = new Date(control.value);
  //   const today = new Date();
  //   if (date > today) {
  //     return { futureDate: true };
  //   }
  //   return null;
  // }
}
  


