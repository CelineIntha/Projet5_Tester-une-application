# YogaApp - Tester une application (Angular & Java)
Ce projet est une application full-stack composée d'un **front-end Angular** et d'un **back-end Java**. Chaque partie est contenue dans son propre dossier, avec ses propres dépendances et instructions d'installation.

## Structure du projet

```
/ project-root
│── front/       # Projet Angular
│── back/        # Projet Java Spring Boot
│── README.md    # Ce fichier
```

## Prérequis

Avant de commencer, assurez-vous d'avoir installé les outils suivants :

- [Node.js](https://nodejs.org/) (pour Angular)
- [Angular CLI](https://angular.io/cli)
- [JDK 11+](https://www.oracle.com/fr/java/technologies/javase/jdk11-archive-downloads.html) (pour Java)
- [Maven](https://maven.apache.org/)

## Installation et démarrage

### 1. Back-end (Java Spring Boot)

📌 **Instructions détaillées dans** [`/back/README.md`](./back/README.md)

```sh
cd back
mvn clean install
mvn spring-boot:run 
```

Le back-end tournera sur `http://localhost:3306`.

### 2. Front-end (Angular)

📌 **Instructions détaillées dans** [`/front/README.md`](./front/README.md)

```sh
cd front/yoga
npm install
ng serve
```

L'application Angular sera accessible sur `http://localhost:4200`.

## Tests

### Tests unitaires

- **Back-end** : `mvn test` (JUnit / Mockito)
- **Front-end** : `ng test` (Jest)

### Tests d'intégration

- **Back-end** : `mvn verify` 
- **Front-end** : `ng e2e` Tests E2E avec Cypress 

---

📌 Consultez les README spécifiques pour plus d'instructions :
- [Front-end (Angular)](./front/README.md)
- [Back-end (Java)](./back/README.md)



