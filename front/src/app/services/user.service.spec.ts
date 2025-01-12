import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { expect } from '@jest/globals';


describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Cela permet de vérifie qu'aucune requête HTTP n'est en attente
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET and return a user by ID', () => {
    const mockUser: User = {
      admin: false,
      createdAt: new Date('2025-01-01T00:00:00Z'),
      lastName: "",
      password: "",
      id: 1,
      firstName: 'John Doe',
      email: 'john.doe@email.com'
    };

    service.getById('1').subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should call DELETE and remove a user by ID', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toBeNull()
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });


});
