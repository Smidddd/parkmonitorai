import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CameraViewPageComponent } from './camera-view-page.component';

describe('CameraViewPageComponent', () => {
  let component: CameraViewPageComponent;
  let fixture: ComponentFixture<CameraViewPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CameraViewPageComponent]
    });
    fixture = TestBed.createComponent(CameraViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
