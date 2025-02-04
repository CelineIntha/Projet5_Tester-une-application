describe('Detail spec', () => {
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

    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'Morning Yoga',
        description: 'A relaxing yoga session.',
        date: '2025-01-20T08:00:00',
        teacher_id: 1,
        users: [2, 3],
        createdAt: '2025-01-01T10:00:00',
        updatedAt: '2025-01-10T12:00:00',
      },
    }).as('getSession');

    cy.intercept('GET', '/api/teacher/1', {
      statusCode: 200,
      body: {
        id: 1,
        firstName: 'Hélène',
        lastName: 'THIERCELIN',
      },
    }).as('getTeacher');
  });

  it('Should navigate to session details when clicking "Detail" button', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/session');
    cy.wait('@getSessions');

    cy.get('[data-cy="session-detail-button"]').first().click();

    cy.url().should('include', '/detail/1');
    cy.wait('@getSession');
    cy.wait('@getTeacher');

    cy.get('[data-cy="session-name"]').should('contain', 'Morning Yoga');
    cy.get('[data-cy="teacher-info"]').should('contain', 'Hélène THIERCELIN');
    cy.get('[data-cy="attendees-info"]').should('contain', '2 attendees');
    cy.get('[data-cy="session-date"]').should('contain', 'January 20, 2025');
    cy.get('[data-cy="session-description"]').should('contain', 'A relaxing yoga session.');
    cy.get('[data-cy="created-at"]').should('contain', 'January 1, 2025');
    cy.get('[data-cy="updated-at"]').should('contain', 'January 10, 2025');
  });

  it('Should allow admin to delete a session from details page', () => {
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200,
    }).as('deleteSession');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.get('[data-cy="session-detail-button"]').first().click();

    cy.url().should('include', '/detail/1');
    cy.wait('@getSession');
    cy.wait('@getTeacher');

    cy.get('[data-cy="delete-button"]').should('be.visible').click();

    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
    cy.contains('Session deleted !').should('be.visible');
  });

});
