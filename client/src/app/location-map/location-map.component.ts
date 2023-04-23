import { AfterViewInit, Component, ElementRef, Input} from '@angular/core';

@Component({
  selector: 'app-location-map',
  templateUrl: './location-map.component.html',
  styleUrls: ['./location-map.component.css']
})
export class LocationMapComponent implements AfterViewInit{

  //TO DO : refactor to a single object
  @Input()
  latitude!: number;
  @Input()
  longitude!: number;
  // map!: {latitude: number, longitude: number};

  constructor(private elementRef: ElementRef){}

  ngAfterViewInit(): void {
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
