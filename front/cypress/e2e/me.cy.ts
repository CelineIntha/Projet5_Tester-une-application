describe('Me spec with Account navigation', () => {
  it('Login successful and user info is displayed on /me', () => {
    const fakeToken = 'fake-bearer-token';

    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true,
          token: fakeToken,
        },
      });
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('session');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");

    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/user/1', (req) => {
      expect(req.headers.authorization).to.equal(`Bearer ${fakeToken}`);

      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          email: 'yoga@studio.com',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true,
          createdAt: '2025-01-04T16:59:26',
          updatedAt: '2025-01-05T16:59:10',
        },
      });
    }).as('getUser');

    cy.contains('Account').click();

    cy.wait('@getUser');

    cy.get('[data-cy="user-name"]').should('contain', 'Name: firstName LASTNAME');

    cy.get('[data-cy="user-email"]').should('contain', 'Email: yoga@studio.com');

    cy.get('[data-cy="user-admin"]').should('contain', 'You are admin');
  });
});
