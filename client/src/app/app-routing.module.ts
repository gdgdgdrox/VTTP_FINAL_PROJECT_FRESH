import { SearchDealComponent } from './search-deal/search-deal.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SearchResultComponent } from './search-result/search-result.component';
import { SavedDealsComponent } from './saved-deals/saved-deals.component';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';
import { GuardService } from './guard.service';

const routes: Routes = [
  {path:'', component: SearchDealComponent},
  {path: 'deals', component: SearchResultComponent},
  {path: 'deals/:category', component: SearchResultComponent},
  {path: 'login', component: LoginComponent},
  {path: 'saved-deals', canActivate: [GuardService], component: SavedDealsComponent},
  {path: 'register', component: RegistrationComponent},
  {path: '**', redirectTo: '/', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash:true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
