import { MaterialModule } from './material/material.module';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchDealComponent } from './search-deal/search-deal.component';
import { SearchResultComponent } from './search-result/search-result.component';
import { LocationMapComponent } from './location-map/location-map.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HeaderComponent } from './header/header.component';
import { SavedDealsComponent } from './saved-deals/saved-deals.component';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';
import { SpinnerComponent } from './shared/spinner/spinner.component';
import { ServiceWorkerModule } from '@angular/service-worker';

@NgModule({
  declarations: [
    AppComponent,
    SearchDealComponent,
    SearchResultComponent,
    LocationMapComponent,
    HeaderComponent,
    SavedDealsComponent,
    RegistrationComponent,
    LoginComponent,
    SpinnerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    ServiceWorkerModule.register('ngsw-worker.js'),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
