describe('Form spec', () => {
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
      body: [],
    }).as('getSessions');

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        { id: 1, firstName: 'Margot', lastName: 'DELAHAYE' },
        { id: 2, firstName: 'Hélène', lastName: 'THIERCELIN' },
      ],
    }).as('getTeachers');

    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: {
        id: 1,
        name: 'Morning Yoga',
        description: 'A relaxing yoga session.',
        date: '2025-01-20',
        teacher_id: 1,
      },
    }).as('createSession');
  });

  it('Should allow admin to access the form and create a session', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');

    cy.wait('@getSessions');

    cy.get('[data-cy="create-button"]').should('be.visible').click();

    cy.url().should('include', '/sessions/create');

    cy.wait('@getTeachers');

    cy.get('input[formControlName=name]').type('Morning Yoga');
    cy.get('input[formControlName=date]').type('2025-01-20');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Margot DELAHAYE').click();
    cy.get('textarea[formControlName=description]').type('A relaxing yoga session.');

    cy.get('button[type="submit"]').click();

    cy.wait('@createSession').its('response.statusCode').should('eq', 201);

    cy.url().should('include', '/sessions');
    cy.contains('Session created !').should('be.visible');
  });

  it('Should prevent non-admin users from accessing the create form', () => {
    cy.intercept('POST', '/api/auth/login', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          id: 2,
          username: 'nonAdminUser',
          firstName: 'John',
          lastName: 'Doe',
          admin: false,
          token: fakeToken,
        },
      });
    }).as('loginNonAdmin');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('john.doe@example.com');
    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');

    cy.wait('@getSessions');

    cy.get('[data-cy="create-button"]').should('not.exist');
  });

  it('Should display validation errors when the form is submitted with invalid data', () => {
    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');

    cy.wait('@getSessions');

    cy.get('[data-cy="create-button"]').should('be.visible').click();

    cy.url().should('include', '/sessions/create');

    // J'envoi un formulaire vide
    cy.get('button[type=submit]').should('be.disabled'); // Le bouton est désactivé

    // Je remplis le formulaire et vérifie les erreurs
    cy.get('input[formControlName=name]').type('Morning Yoga');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=date]').type('2025-01-20');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Margot DELAHAYE').click();
    cy.get('textarea[formControlName=description]').type('A relaxing yoga session.');

    cy.get('button[type=submit]').should('not.be.disabled');

    cy.get('button[type=submit]').click();

    cy.wait('@createSession').its('response.statusCode').should('eq', 201);
  });

});
