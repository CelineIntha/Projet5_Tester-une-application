import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';

import { LoginComponent } from './login.component';
import { SessionService } from 'src/app/services/session.service';
import { Router } from '@angular/router';

import { expect } from '@jest/globals';


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let routerMock: jest.Mocked<Router>;
  let sessionServiceMock: jest.Mocked<SessionService>;

  beforeEach(async () => {
    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    sessionServiceMock = {
      logIn: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        HttpClientModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  describe('Unit Tests', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize the form with default values', () => {
      expect(component.form.value).toEqual({ email: '', password: '' });
    });

    it('should mark the form as invalid when fields are empty', () => {
      expect(component.form.valid).toBeFalsy();
    });

    it('should mark the form as valid when valid inputs are provided', () => {
      component.form.setValue({ email: 'test@example.com', password: 'password123' });
      expect(component.form.valid).toBeTruthy();
    });

    it('should toggle password visibility', () => {
      expect(component.hide).toBe(true);
      component.hide = !component.hide;
      expect(component.hide).toBe(false);
    });
  });

  // ----------------------- //
  //    Integration Tests    //
  // ----------------------- //

});
