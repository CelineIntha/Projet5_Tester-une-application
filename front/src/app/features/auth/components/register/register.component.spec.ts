import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
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

    it('should have an invalid form on load', () => {
      expect(component.form.valid).toBeFalsy();
    });

    it('should validate the form with correct values', () => {
      component.form.setValue({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        password: 'password',
      });
      expect(component.form.valid).toBeTruthy();
    });

    it('should disable the submit button if form is invalid', () => {
      const button = fixture.nativeElement.querySelector('button[type="submit"]');
      expect(button.disabled).toBeTruthy();
    });
  });

  // ----------------------- //
  //    Integration Tests    //
  // ----------------------- //

  describe('Integration Tests', () => {
    it('should display an error message if registration fails', () => {
      component.onError = true;
      fixture.detectChanges();

      const errorMessage = fixture.nativeElement.querySelector('.error');
      expect(errorMessage).toBeTruthy();
      expect(errorMessage.textContent).toContain('An error occurred');
    });

    it('should call the submit method when form is submitted', () => {
      jest.spyOn(component, 'submit');

      component.form.setValue({
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        password: 'password',
      });

      const formElement = fixture.nativeElement.querySelector('form');
      formElement.dispatchEvent(new Event('submit'));

      expect(component.submit).toHaveBeenCalled();
    });
  });
});
