# Yoga App!

## 🚀 Installation

To install and run the project, follow these steps:

1. **Set up a local MySQL server** using MAMP, XAMPP, or WAMP. Ensure your MySQL database is running and update the database connection details in `application.properties`.


2. **Configure your MySQL database:**
   - Open the `application.properties` file and update the connection settings:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/yoga?allowPublicKeyRetrieval=true
     spring.datasource.username=your_db_username
     spring.datasource.password=your_db_password
     ```
   ⚠️ Ensure that the MySQL port is set to **3306**.


3. **Install dependencies and build the project:**
    ```
    mvn clean install
    ```

4. **Run the application:**
    ```
    mvn spring-boot:run
    ```

---

## 🛠 Tests

### Running Tests and Generating Code Coverage

1. **Run unit tests and generate Jacoco code coverage:**
    ```
    mvn clean test
    ```
   or
    ```
    mvn test
    ```

2. **Run only integration tests:**
    ```
    mvn integration-test
    ```

3. **Generate full code coverage report:**
    ```
    mvn verify
    ```

4. **Code coverage reports will be available at:**



   - **Unit tests coverage:**
     ```
     back/target/jacoco-ut-report/index.html
     ```
   - **Integration tests coverage:**
     ```
     back/target/jacoco-it-report/index.html
     ```
   - **Global coverage report:**
     ```
     back/target/jacoco-merged-report/index.html
     ```

