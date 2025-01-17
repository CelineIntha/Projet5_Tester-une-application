describe('Page Not Found Tests', () => {
  it('should display "Page not found !" message', () => {
    cy.visit('/page-inconnue', { failOnStatusCode: false });

    cy.get('.flex.justify-center.mt3').should('be.visible');

    cy.get('.flex.justify-center.mt3 h1').should('have.text', 'Page not found !');
  });
});
