# Yoga

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 14.1.0.


### 1. Go inside this folder:

> cd yoga

### 2. Install dependencies:

> npm install

### 3. Launch Front-end:


> ng serve

or via npm

> npm run start

## Project Resources


### 📌 1. Postman Collection

You can import the Postman collection available at:

> ressources/postman/yoga.postman_collection.json 

Follow the official documentation for import instructions:  
🔗 [Postman Import Guide](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman)



### 🗄 2. Database (MySQL)

To set up the database schema, run the SQL script available at:

> ressources/sql/script.sql

**Default admin account credentials:**
- **Email**: `yoga@studio.com`
- **Password**: `test!1234`


# 🛠 Tests

## Unit & Integration Tests (Jest)

### 1️⃣ Run Tests

> ng test

or

> npm run test

for following change:

> npm run test:watch


### 2️⃣ Generate Code Coverage

>  npx jest --coverage

### 3️⃣ Coverage report is available at:

> front/coverage/jest/lcov-report/index.html


## 🚀 End-to-End (E2E) Tests

### 1️⃣ Generate Instrumented Files for E2E

> npm run instrument

📌 This will generate files in:

> front/instrument


### ⚠️ Important Note
If you run `npm run test` for unit tests, delete the instrument folder first to avoid test conflicts because some tests won't pass.


### 2️⃣ Run E2E Tests with Cypress

> npm run e2e

### 3️⃣ Generate E2E Coverage Report

> npm run e2e:ci

And then
> npm run e2e:coverage


### 📌 E2E coverage report is available at:

> front/coverage/lcov-report/index.html
