describe('MeComponent E2E Tests', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        admin: true,
        createdAt: '2025-01-01T00:00:00Z',
        updatedAt: '2025-01-02T00:00:00Z'
      }
    }).as('getUser');

    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
      body: {}
    }).as('deleteUser');

    cy.visit('/me');
  });


  it('should navigate back when back button is clicked', () => {
    cy.get('button[mat-icon-button]').click();
    cy.window().its('history').invoke('back');
  });

});
