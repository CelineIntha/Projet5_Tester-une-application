import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    id: 1,
    username: 'john.doe',
    firstName: 'John',
    lastName: 'Doe',
    admin: true,
    token: 'mock-token',
    type: 'Bearer',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have initial state as not logged in', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should log in a user and update the state', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('should log out a user and reset the state', () => {
    service.logIn(mockUser); // Simule la connexion d'un utilisateur
    service.logOut(); // Déconnexion

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  // ------------------------ //
  //     Integration Tests    //
  // ------------------------ //

  describe('Integration Tests', () => {
    it('should handle a complete login and logout flow', () => {
      // Initial state
      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();

      // Log in
      service.logIn(mockUser);
      expect(service.isLogged).toBe(true);
      expect(service.sessionInformation).toEqual(mockUser);

      // Log out
      service.logOut();
      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
    });
  });
});
