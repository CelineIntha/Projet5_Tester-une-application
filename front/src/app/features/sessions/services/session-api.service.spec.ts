import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { expect } from '@jest/globals';


describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Session 1',
    description: 'Description pour la session 1',
    date: new Date('2025-12-12'),
    createdAt: new Date('2025-01-01'),
    updatedAt: new Date('2025-06-01'),
    teacher_id: 2,
    users: [1, 2, 3],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all sessions', () => {
    const mockSessions: Session[] = [mockSession];

    service.all().subscribe((sessions) => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should fetch session details', () => {
    service.detail('1').subscribe((session) => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('should delete a session', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should create a new session', () => {
    service.create(mockSession).subscribe((session) => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockSession);
    req.flush(mockSession);
  });

  it('should update an existing session', () => {
    const updatedSession = { ...mockSession, name: 'Updated Session' };

    service.update('1', updatedSession).subscribe((session) => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession);
    req.flush(updatedSession);
  });

  it('should participate in a session', () => {
    service.participate('1', '1').subscribe((response) => {
      expect(response).toBeUndefined(); // La réponse est vide (void)
    });

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull(); // Corps vide pour une participation
    req.flush({});
  });

  it('should un-participate from a session', () => {
    service.unParticipate('1', '1').subscribe((response) => {
      expect(response).toBeUndefined(); // La réponse est vide (void)
    });

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  // ------------------------ //
  //    Integration Tests     //
  // ------------------------ //

  describe('Integration Tests', () => {
    it('should handle a full CRUD lifecycle for sessions', () => {
      // 1. Fetch all sessions
      service.all().subscribe((sessions) => {
        expect(sessions).toEqual([mockSession]);
      });

      let req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      req.flush([mockSession]);

      // 2. Fetch session details
      service.detail('1').subscribe((session) => {
        expect(session).toEqual(mockSession);
      });

      req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);

      // 3. Create a session
      service.create(mockSession).subscribe((session) => {
        expect(session).toEqual(mockSession);
      });

      req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      req.flush(mockSession);

      // 4. Update the session
      const updatedSession = { ...mockSession, name: 'Updated Session' };

      service.update('1', updatedSession).subscribe((session) => {
        expect(session).toEqual(updatedSession);
      });

      req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('PUT');
      req.flush(updatedSession);

      // 5. Delete the session
      service.delete('1').subscribe((response) => {
        expect(response).toBeTruthy();
      });

      req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });
});
