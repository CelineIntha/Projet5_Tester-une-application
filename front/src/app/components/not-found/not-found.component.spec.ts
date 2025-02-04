import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotFoundComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ------------------------ //
  //        Unit Tests        //
  // ------------------------ //

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the "Page not found" message', () => {
    const compiled = fixture.nativeElement;
    const messageElement = compiled.querySelector('h1');

    expect(messageElement).toBeTruthy();
    expect(messageElement.textContent).toContain('Page not found !');
  });

  it('should apply the correct CSS classes to the container', () => {
    const compiled = fixture.nativeElement;
    const containerElement = compiled.querySelector('div');

    expect(containerElement).toBeTruthy();
    expect(containerElement.classList).toContain('flex');
    expect(containerElement.classList).toContain('justify-center');
    expect(containerElement.classList).toContain('mt3');
  });
});
