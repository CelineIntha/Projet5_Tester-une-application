import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockRouter: any;
  let mockSessionService: any;
  let mockUserService: any;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn()
    };

    mockSessionService = {
      sessionInformation: {
        admin: true,
        id: 1
      },
      logOut: jest.fn()
    };

    mockUserService = {
      getById: jest.fn().mockReturnValue(of({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        admin: true,
        createdAt: new Date(),
        updatedAt: new Date()
      })),
      delete: jest.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  describe('Unit Tests', () => {
    it('should create', () => {
    expect(component).toBeTruthy();
    });

  it('should display "You are admin" if user is admin', () => {
    component.user = { admin: true } as any;
    fixture.detectChanges();

    const adminMessage = fixture.debugElement.query(By.css('.my2'));
    expect(adminMessage).toBeTruthy();
    expect(adminMessage.nativeElement.textContent).toContain('You are admin');
  });

  it('should display delete button if user is not admin', () => {
    component.user = { admin: false } as any;
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.query(By.css('button[color="warn"]'));
    expect(deleteButton).toBeTruthy();
  });

  it('should call back() when back button is clicked', () => {
    const backSpy = jest.spyOn(component, 'back');
    const backButton = fixture.debugElement.query(By.css('button[mat-icon-button]'));
    backButton.triggerEventHandler('click', null);

    expect(backSpy).toHaveBeenCalled();
  });

  it('should call delete() when delete button is clicked', () => {
    component.user = { admin: false } as any;
    fixture.detectChanges();

    const deleteSpy = jest.spyOn(component, 'delete');
    const deleteButton = fixture.debugElement.query(By.css('button[color="warn"]'));
    deleteButton.triggerEventHandler('click', null);

    expect(deleteSpy).toHaveBeenCalled();
  });
});
  // ------------------------ //
  //     Integration Tests    //
  // ------------------------ //

  describe('Integration Tests', () => {
    it('should fetch user details on initialization', () => {
    expect(mockUserService.getById).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(component.user).toBeTruthy();
    expect(component.user!.firstName).toBe('John');
  });
    it('should navigate back when back() is called', () => {
    jest.spyOn(window.history, 'back');
    component.back();
    expect(window.history.back).toHaveBeenCalled();
    });
  });
});
