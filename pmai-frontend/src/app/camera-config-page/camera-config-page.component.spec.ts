import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CameraConfigPageComponent } from './camera-config-page.component';

describe('CameraConfigPageComponent', () => {
  let component: CameraConfigPageComponent;
  let fixture: ComponentFixture<CameraConfigPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CameraConfigPageComponent]
    });
    fixture = TestBed.createComponent(CameraConfigPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
