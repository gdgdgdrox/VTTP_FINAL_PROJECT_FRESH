import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Deal } from './models';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  onSearch = new Subject<any>();
  
  constructor(private httpClient: HttpClient) { }

  searchDealsByCategory(category: string){
    const headers = new HttpHeaders().set('accept', 'application/json');
    this.httpClient.get<Deal[]>(`https://mirthful-coal-production.up.railway.app/api/deals/${category}`, {headers}).subscribe({
      next: (data) => {
        this.onSearch.next(data);
      },
      error: (error) => this.onSearch.next(error),
    });
  }
  
  searchDealsByKeyword(keyword: string){
    const headers = new HttpHeaders().set('accept', 'application/json');
    this.httpClient.get<Deal[]>(`https://mirthful-coal-production.up.railway.app/api/deals`, {headers, params: {keyword}}).subscribe({
      next: (data) => {
        this.onSearch.next(data);
      },
      error: (error) => this.onSearch.next(error)
    });
  }
}

