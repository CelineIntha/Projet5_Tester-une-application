import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { SessionService } from 'src/app/services/session.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

import { expect } from '@jest/globals';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let routerMock: jest.Mocked<Router>;
  let sessionServiceMock: jest.Mocked<SessionService>;
  let authServiceMock: jest.Mocked<AuthService>;

  const mockSessionInformation: SessionInformation = {
    token: 'fake-token',
    type: 'Bearer',
    id: 123,
    username: 'john.doe',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
  };

  beforeEach(async () => {
    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    sessionServiceMock = {
      logIn: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    authServiceMock = {
      login: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: authServiceMock }
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
      component.form.setValue({ email: 'john@email.com', password: 'password' });
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

  describe('Integration Tests', () => {
    it('should call AuthService login with correct values on submit', () => {
      const loginRequest = { email: 'john@email.com', password: 'password' };
      component.form.setValue(loginRequest);

      authServiceMock.login.mockReturnValue(of(mockSessionInformation));
      component.submit();

      expect(authServiceMock.login).toHaveBeenCalledWith(loginRequest);
    });

    it('should navigate to /sessions on successful login', () => {
      authServiceMock.login.mockReturnValue(of(mockSessionInformation));

      component.submit();

      expect(sessionServiceMock.logIn).toHaveBeenCalledWith(mockSessionInformation);
      expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    });

    it('should display error message on login failure', () => {
      authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

      component.submit();

      expect(component.onError).toBeTruthy();
    });

    it('should disable the submit button when form is invalid', () => {
      const button = fixture.nativeElement.querySelector('button[type="submit"]');
      component.form.setValue({ email: '', password: '' });
      fixture.detectChanges();

      expect(button.disabled).toBeTruthy();

      component.form.setValue({ email: 'john@email.com', password: 'password' });
      fixture.detectChanges();

      expect(button.disabled).toBeFalsy();
    });
  });
});
