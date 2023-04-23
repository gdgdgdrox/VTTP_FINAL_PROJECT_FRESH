import { SearchService } from './../search.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-search-deal',
  templateUrl: './search-deal.component.html',
  styleUrls: ['./search-deal.component.css']
})
export class SearchDealComponent {
  constructor(private searchSvc: SearchService, private router: Router){}

  searchByCategory(category: string){
    this.searchSvc.searchDealsByCategory(category);
    this.router.navigate(['deals', `${category}`]);
}

  searchByKeyword(form: NgForm){
    const keyword = form.value.keyword;
    this.searchSvc.searchDealsByKeyword(keyword);
    this.router.navigate(['deals'], {queryParams : {keyword}});
  }
}
