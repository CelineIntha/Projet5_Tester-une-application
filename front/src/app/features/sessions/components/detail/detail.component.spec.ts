import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { of } from 'rxjs';

import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';

import { expect } from '@jest/globals';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from "@angular/material/card";

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  let sessionServiceMock: Partial<SessionService>;
  let sessionApiServiceMock: Partial<SessionApiService>;
  let teacherServiceMock: Partial<TeacherService>;
  let routerMock: jest.Mocked<Router>;

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Class',
    description: 'A relaxing yoga session.',
    date: new Date('2025-12-12'),
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-06-01'),
    teacher_id: 2,
    users: [1, 2, 3],
  };

  const mockTeacher: Teacher = {
    id: 2,
    firstName: 'Jane',
    lastName: 'Doe',
    createdAt: new Date('2025-01-01'),
    updatedAt: new Date('2025-06-01'),
  };

  const mockSessionInformation = {
    token: 'mock-token',
    type: 'Bearer',
    id: 1,
    username: 'mockuser',
    firstName: 'Mock',
    lastName: 'User',
    admin: true,
  };

  const mockParamMap: ParamMap = {
    has: (key: string) => key === 'id',
    get: (key: string) => (key === 'id' ? '1' : null),
    getAll: (key: string) => (key === 'id' ? ['1'] : []),
    keys: ['id'],
  };

  beforeEach(async () => {
    sessionServiceMock = {
      sessionInformation: mockSessionInformation,
    };

    sessionApiServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({})),
    };

    teacherServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockTeacher)),
    };

    routerMock = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    const activatedRouteMock = {
      snapshot: {
        paramMap: mockParamMap,
      },
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatIconModule,
        MatCardModule,
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should fetch session details on initialization', () => {
      component.ngOnInit();

      expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
      expect(component.session).toEqual(mockSession);
    });

    it('should fetch teacher details for the session', () => {
      component.ngOnInit();

      expect(teacherServiceMock.detail).toHaveBeenCalledWith(mockSession.teacher_id.toString());
      expect(component.teacher).toEqual(mockTeacher);
    });
  });

  describe('back', () => {
    it('should navigate back in browser history', () => {
      jest.spyOn(window.history, 'back').mockImplementation(() => {});

      component.back();

      expect(window.history.back).toHaveBeenCalled();
    });
  });

  // ------------------------ //
  //     Integration Tests    //
  // ------------------------ //

  describe('Integration Tests', () => {
    it('should delete session and navigate back to sessions list', () => {
      component.session = mockSession;

      if (mockSession.id) {
        component.delete();
        expect(sessionApiServiceMock.delete).toHaveBeenCalledWith(mockSession.id.toString());
        expect(window.history.back).toHaveBeenCalled();
      }
    });

  });

});
