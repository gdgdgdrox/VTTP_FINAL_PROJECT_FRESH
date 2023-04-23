import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';

const MATERIALS = [MatCardModule, MatFormFieldModule, MatIconModule]

@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  exports : [
    MATERIALS
  ]
})
export class MaterialModule { }
