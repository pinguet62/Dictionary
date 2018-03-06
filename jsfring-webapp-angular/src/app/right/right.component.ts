import {Component} from '@angular/core';
import {RightService} from './right.service';

@Component({
    selector: 'p62-right',
    template: `
        <p62-datatable [service]="rightService" [(selectedValue)]="selectedRight">

            <p62-datatable-columns>
                <p-column field="code" header="Code" sortable="true" filter="true" filterMatchMode="contains"></p-column>
                <p-column field="title" header="Title" sortable="true" filter="true" filterMatchMode="contains"></p-column>
            </p62-datatable-columns>

            <p62-datatable-dialog>
                <ng-template [ngIf]="selectedRight">
                    <mat-form-field>
                        <input matInput placeholder="Code" [(value)]="selectedRight.code" disabled>
                    </mat-form-field>
                    <br>
                    <mat-form-field>
                        <input matInput placeholder="Title" [(value)]="selectedRight.title">
                    </mat-form-field>
                </ng-template>
            </p62-datatable-dialog>

        </p62-datatable>`
})
export class RightComponent {

    selectedRight: any;

    constructor(public rightService: RightService) {
    }

}
