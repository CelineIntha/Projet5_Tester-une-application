describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', (req) => {
      const { email, firstName, lastName, password } = req.body;

      if (email && firstName && lastName && password) {
        req.reply({ statusCode: 201 });
      } else {
        req.reply({ statusCode: 400, body: { message: 'Validation error' } });
      }
    }).as('register');
  });

  it('should register successfully', () => {
    // Je remplis le formulaire
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@example.com');
    cy.get('input[formControlName=password]').type('test1234');

    // Je soumets le formulaire
    cy.get('button[type=submit]').click();

    // Je vérifie la redirection et l'absence d'erreurs
    cy.wait('@register').its('response.statusCode').should('eq', 201);
    cy.url().should('include', '/login');
    cy.get('.error').should('not.exist');
  });

  it('should show validation errors for empty fields', () => {
    // Je vérifie que le bouton est désactivé lorsque le formulaire est vide
    cy.get('button[type=submit]').should('be.disabled');

    // Je vérifie que les champs affichent des erreurs
    cy.get('input[formControlName=firstName]').should('have.class', 'ng-invalid');
    cy.get('input[formControlName=lastName]').should('have.class', 'ng-invalid');
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('input[formControlName=password]').should('have.class', 'ng-invalid');
  });

  it('should show an error message if the registration fails', () => {
    // Je simule une erreur côté serveur avec intercept
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Registration failed' },
    }).as('registerFail');

    // Remplir le formulaire avec des données valides
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@email.com');
    cy.get('input[formControlName=password]').type('azerty12345');

    cy.get('button[type=submit]').click();

    // Vérifier la réponse et l'affichage du message d'erreur
    cy.wait('@registerFail').its('response.statusCode').should('eq', 400);
    cy.get('.error').should('contain', 'An error occurred');
  });

  it('should disable the submit button if the form is invalid', () => {
    cy.get('input[formControlName=firstName]').type('Jo'); // Trop court
    cy.get('input[formControlName=email]').type('invalid-email');

    // Vérifier que le bouton est désactivé
    cy.get('button[type=submit]').should('be.disabled');
  });
});
