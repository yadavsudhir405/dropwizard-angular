import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import {DummyModule} from "./dummy/dummy.module";


@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        DummyModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
