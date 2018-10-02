import {NgModule} from "@angular/core";
import {DummyComponent} from "./dummyComponet/dummy.component";

@NgModule({
    declarations: [
        DummyComponent
    ],
    exports: [
        DummyComponent
    ]
})
export class DummyModule {
}