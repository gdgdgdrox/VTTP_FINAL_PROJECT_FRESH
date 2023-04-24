import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../user.service';
import { Deal } from '../models';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';

@Component({
  selector: 'app-saved-deals',
  templateUrl: './saved-deals.component.html',
  styleUrls: ['./saved-deals.component.css']
})
export class SavedDealsComponent implements OnInit{
  email = '';
  userDeals: Deal[] = [];
  deleteErrorMessage = '';
  isLoading = true;

  constructor(
    private http: HttpClient, 
    private userService: UserService,
    public sanitizer: DomSanitizer,
    private router: Router
    ){}

  ngOnInit(): void{
    this.email = this.userService.getLoggedInUserEmail();
    setTimeout(() => this.getUserDeal(this.email) ,3*1000)
  }

  getUserDeal(email: string){
    this.userService.getUserDeal(email).subscribe({
      next: (resp) => {
        this.userDeals = resp;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.isLoading = false;
      }
    })
  }

  deleteDeal(uuid: string){
    this.userService.deleteUserDeal(this.email, uuid)
    .then(resp => {
        this.userDeals = this.userDeals.filter(deal => deal.uuid !== uuid);
      })
    .catch(error => {
      console.error(error);
      this.deleteErrorMessage = error.message;
    });
    
  }
}
