import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard {

  http= inject(HttpClient)

  constructor(){
    this.getUser();
  }

  getUser(){
    this.http.get("http://localhost:8080/api/usuarios").subscribe({
      next:(response)=>{

      },
      error:(error)=>{

      }
    })
  }

}
