describe('Me spec with Account navigation', () => {
  const fakeToken = 'fake-bearer-token';

  beforeEach(() => {
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

    cy.intercept('GET', '/api/user/1', (req) => {
      expect(req.headers.authorization).to.equal(`Bearer ${fakeToken}`);
      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          email: 'yoga@studio.com',
          firstName: 'Yoga',
          lastName: 'Studio',
          admin: true,
          createdAt: '2025-01-04T16:59:26',
          updatedAt: '2025-01-05T16:59:10',
        },
      });
    }).as('getUser');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");
    cy.url().should('include', '/sessions');
  });

  it('Displays user info correctly on /me', () => {
    cy.contains('Account').click();
    cy.wait('@getUser');
    cy.get('[data-cy="user-name"]').should('contain', 'Name: Yoga STUDIO');
    cy.get('[data-cy="user-email"]').should('contain', 'Email: yoga@studio.com');
    cy.get('[data-cy="user-admin"]').should('contain', 'You are admin');
  });

  it('Should navigate back when back button is clicked', () => {
    cy.contains('Account').click();
    cy.wait('@getUser');

    cy.get('[data-cy="back-button"]').click();

    cy.url().should('not.include', '/me');
  });

  it('Should not display delete button for an admin user', () => {
    cy.contains('Account').click();
    cy.wait('@getUser');

    cy.get('[data-cy="delete-account-button"]').should('not.exist');
  });

  it('Should not display user information if user is undefined', () => {
    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: null,
    }).as('getUserWithoutData');

    cy.contains('Account').click();
    cy.wait('@getUserWithoutData');

    cy.get('[data-cy="user-name"]').should('not.exist');
    cy.get('[data-cy="user-email"]').should('not.exist');
    cy.get('[data-cy="user-admin"]').should('not.exist');
  });

  it('Should display delete button for a non-admin user', () => {
    cy.intercept('GET', '/api/user/1', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          email: 'yoga@studio.com',
          firstName: 'Yoga',
          lastName: 'Studio',
          admin: false,
          createdAt: '2025-01-04T16:59:26',
          updatedAt: '2025-01-05T16:59:10',
        },
      });
    }).as('getUserNonAdmin');

    cy.contains('Account').click();
    cy.wait('@getUserNonAdmin');

    cy.get('[data-cy="delete-account-button"]').should('exist');
  });

  it('Should trigger delete function when delete button is clicked', () => {
    cy.intercept('DELETE', '/api/user/1', (req) => {
      req.reply({
        statusCode: 200,
        body: { message: 'Account deleted successfully' },
      });
    }).as('deleteAccount');

    cy.intercept('GET', '/api/user/1', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          id: 1,
          email: 'yoga@studio.com',
          firstName: 'Yoga',
          lastName: 'Studio',
          admin: false,
          createdAt: '2025-01-04T16:59:26',
          updatedAt: '2025-01-05T16:59:10',
        },
      });
    }).as('getUserNonAdmin');

    cy.contains('Account').click();
    cy.wait('@getUserNonAdmin');

    cy.get('[data-cy="delete-account-button"]').click();
    cy.wait('@deleteAccount');
  });


});
