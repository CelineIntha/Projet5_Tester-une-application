describe('List spec', () => {
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

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        {
          id: 1,
          name: 'Morning Yoga',
          description: 'A relaxing yoga session.',
          date: '2025-01-20T08:00:00',
          teacher_id: 1,
          users: [2, 3],
          createdAt: '2025-01-01T10:00:00',
          updatedAt: '2025-01-10T12:00:00',
        },
      ],
    }).as('getSessions');
  });

  it('Should display the list of available rentals', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/session');

    cy.wait('@getSessions');
    cy.get('[data-cy="rentals-title"]').should('contain', 'Rentals available');
    cy.get('[data-cy="session-item"]').should('have.length', 1);
  });

  it('Should display no sessions when the list is empty', () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('getNoSessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/session');

    cy.wait('@getNoSessions');
    cy.get('[data-cy="session-item"]').should('not.exist');
  });

});
