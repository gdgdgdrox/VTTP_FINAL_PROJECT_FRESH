import { AfterViewInit, Component, ElementRef, Input} from '@angular/core';

@Component({
  selector: 'app-location-map',
  templateUrl: './location-map.component.html',
  styleUrls: ['./location-map.component.css']
})
export class LocationMapComponent implements AfterViewInit{

  @Input()
  latitude!: number;
  @Input()
  longitude!: number;

  constructor(private elementRef: ElementRef){}

  ngAfterViewInit(): void {
    if (this.latitude !== 0 && this.longitude !==0){
      const map = new google.maps.Map(this.elementRef.nativeElement.querySelector('#mapContainer'), {
        center: { lat: this.latitude, lng: this.longitude },
        zoom: 15,
      });
    
      const marker = new google.maps.Marker({
        position: { lat: this.latitude, lng: this.longitude },
        map,
      });
    }
  }
}
