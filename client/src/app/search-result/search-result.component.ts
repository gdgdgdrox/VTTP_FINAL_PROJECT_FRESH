import { SearchService } from './../search.service';
import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Deal } from '../models';
import { DomSanitizer } from '@angular/platform-browser';
import { UserService } from '../user.service';


@Component({
  selector: 'app-search-result',
  templateUrl: './search-result.component.html',
  styleUrls: ['./search-result.component.css']
})
export class SearchResultComponent implements OnInit{
  dealCategory!: string;
  deals!: Deal[];
  searchError!: any;


  dealIDsForSaving: string[] = [];
  isLoading = false;

  constructor(
    private activatedRoute: ActivatedRoute, 
    private searchSvc: SearchService, 
    public sanitizer: DomSanitizer,
    private userService: UserService,
    private router: Router
    ){}

  ngOnInit(): void {
    this.isLoading = true;
    this.searchSvc.onSearch.subscribe({
      next: (data) => {
        this.isLoading = false;
        this.deals = data;
      },
      error: (error) => {
        this.isLoading = false;
        this.searchError = error;
      },
    });
    this.dealCategory = this.activatedRoute.snapshot.params['category'];
  }

  ngOnDestroy(): void {
    if (this.userService.isLoggedIn && this.dealIDsForSaving.length > 0){
      this.saveDeal(this.userService.getLoggedInUserEmail(), this.dealIDsForSaving);
    }
  }

  toggleSave(uuid:string, idx: number) {
    // check if user is login. if not logged in, dont allow them to toggle and direct them to login page instead
    if (!this.userService.isLoggedIn){
      console.log('user is not logged in. directing to login page.');
      this.router.navigate(['/login']);
    }
    else{
      const deal = this.deals[idx];
      deal.saved = !deal.saved
      if (deal.saved) {
        console.log(`saving ${uuid}`);
        this.dealIDsForSaving.push(deal.uuid);
        this.userService.addDeal(deal.uuid);
      } else {
        console.log(`removing ${uuid}`);
        const index = this.dealIDsForSaving.findIndex(dealUUID => dealUUID === uuid);
        this.userService.removeDeal(uuid);
        if (index !== -1) {
          this.dealIDsForSaving.splice(index, 1);
        }
      }
      console.log(this.dealIDsForSaving.length);
    }
  }

  saveDeal(email: string, dealIDsForSaving: string[]){
    this.userService.saveUserDeal(email, dealIDsForSaving); 
  }

  shareDeal(deal:Deal){
    console.log(deal);
    if (navigator.share) {
      navigator.share({
        title: 'Hey check out this deal',
        text: deal.name,
        url: deal.websiteURL
      })
      .then(() => {
        console.log('Deal shared successfully');
      })
      .catch((error) => {
        console.error('Failed to share deal:', error);
      });
    } else {
      // Web Share API is not supported, show an error message or fallback option
      console.error('Web Share API is not supported in this browser');
    }
  }


}
