import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { of } from 'rxjs';
import { Router } from '@angular/router';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { expect } from '@jest/globals';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  let sessionServiceMock: Partial<SessionService>;
  let routerMock: jest.Mocked<Router>;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)), // Mock de l'état connecté
      logOut: jest.fn(),
    };

    routerMock = {
      navigate: jest.fn(),
      routerState: {
        root: {},
      },
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule,
      ],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should call $isLogged on sessionService', () => {
    component.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(true);
    });
    expect(sessionServiceMock.$isLogged).toHaveBeenCalled();
  });

  it('should log out and navigate to the home page', () => {
    component.logout();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should display "Sessions" and "Account" links when logged in', () => {
    sessionServiceMock.$isLogged = jest.fn().mockReturnValue(of(true));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.link[routerLink="sessions"]')).toBeTruthy();
    expect(compiled.querySelector('.link[routerLink="me"]')).toBeTruthy();
    expect(compiled.querySelector('.link[routerLink="login"]')).toBeFalsy();
  });

  it('should display "Login" and "Register" links when not logged in', () => {
    sessionServiceMock.$isLogged = jest.fn().mockReturnValue(of(false));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.link[routerLink="login"]')).toBeTruthy();
    expect(compiled.querySelector('.link[routerLink="register"]')).toBeTruthy();
    expect(compiled.querySelector('.link[routerLink="sessions"]')).toBeFalsy();
  });
});
