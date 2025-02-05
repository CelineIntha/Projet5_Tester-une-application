# YogaApp - Testing an Application (Angular & Java)
This project is a full-stack application consisting of an **Angular front-end** and a **Java back-end**. Each part is contained in its own folder, with its own dependencies and installation instructions.


## Prerequisites

Before getting started, make sure you have installed the following tools:

### Angular
- [Node.js](https://nodejs.org/) 
- [Angular CLI](https://angular.dev/installation)

### Java
- [JDK 11+](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven](https://maven.apache.org/)

## Installation and Set Up

### Project Structure

```
/ project-root
│── front/       # Angular Project
│── back/        # Java Spring Boot Project
│── README.md    # This file
```

### 1. Start the project

Clone the project

> git clone https://github.com/CelineIntha/Projet5_Tester-une-application.git


### 2. Back-end (Java Spring Boot)

📌 **Detailed instructions in** [`/back/README.md`](./back/README.md)

```sh
cd back
mvn clean install
mvn spring-boot:run
```

The back-end will run on `http://localhost:3306`.

### 3. Front-end (Angular)

📌 **Detailed instructions in** [`/front/README.md`](./front/README.md)

```sh
cd front/yoga
npm install
ng serve
```

The Angular application will be accessible at `http://localhost:4200`.

## Tests

### Unit Tests

- **Back-end**: `mvn test` (JUnit / Mockito)
- **Front-end**: `ng test` (Jest)

### Integration Tests

- **Back-end**: `mvn verify` or `mvn clean test`
- **Front-end**: `ng e2e` E2E tests with Cypress

---

📌 Check the specific README files for more instructions:
- [Front-end (Angular)](./front/README.md)
- [Back-end (Java)](./back/README.md)

