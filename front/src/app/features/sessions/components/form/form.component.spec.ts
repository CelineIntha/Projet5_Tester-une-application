import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockRouter: any;
  let mockActivatedRoute: any;
  let mockSessionApiService: any;
  let mockSessionService: any;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn(),
      url: '/create-session' // On est par défaut sur la création d'une session
    };

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of({
        id: 1,
        name: 'Session 1',
        date: '2025-01-01',
        teacher_id: 1,
        description: 'Description pour la session 1'
      })),
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({}))
    };

    mockSessionService = {
      sessionInformation: {
        admin: true
      }
    };

    // Ajout d'un mock pour 'animate' car sinon cela provoque une erreur
    Object.defineProperty(HTMLElement.prototype, 'animate', {
      value: jest.fn(),
      writable: true
    });

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: SessionService, useValue: mockSessionService }
      ],
      declarations: [FormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form in create mode', () => {
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeTruthy();
    expect(component.sessionForm!.controls['name'].value).toBe('');
    expect(component.sessionForm!.controls['date'].value).toBe('');
    expect(component.sessionForm!.controls['teacher_id'].value).toBe('');
    expect(component.sessionForm!.controls['description'].value).toBe('');
  });

  it('should initialize form in update mode', () => {
    mockRouter.url = '/update-session/1'; // Simule la mise à jour
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.onUpdate).toBe(true);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm!.controls['name'].value).toBe('Session 1');
  });

  it('should disable the submit button when form is invalid', () => {
    component.sessionForm!.controls['name'].setValue('');
    fixture.detectChanges();

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(submitButton.properties['disabled']).toBeTruthy();
  });

  it('should enable the submit button when form is valid', () => {
    component.sessionForm!.setValue({
      name: 'Nom de la session',
      date: '2025-01-01',
      teacher_id: '1',
      description: 'Une description valide'
    });
    fixture.detectChanges();

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(submitButton.properties['disabled']).toBeFalsy();
  });

  // ------------------------ //
  //    Integration Tests     //
  // ------------------------ //

  it('should call create() on submit in create mode', () => {
    component.sessionForm!.setValue({
      name: 'Session 1',
      date: '2025-01-01',
      teacher_id: '1',
      description: 'Description pour la session 1'
    });

    component.submit();
    expect(mockSessionApiService.create).toHaveBeenCalledWith(component.sessionForm!.value);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should navigate to sessions page if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    component.ngOnInit();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});
