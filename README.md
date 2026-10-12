
# Banking Management System

A RESTful banking backend application developed with **Java 21, Spring Boot 4.1.1, Spring Data JPA, and H2 Database**.

The project demonstrates backend software engineering concepts, including REST API development, layered architecture, transaction management, database persistence, optimistic locking, and automated unit testing.

> **Note:** This is an educational portfolio project and is not intended for real banking operations.

## Features

- Create and manage bank accounts
- Retrieve all accounts or search by ID/account number
- Deposit money into an account
- Withdraw money with balance validation
- Transfer money between bank accounts
- Prevent overdrafts and invalid transactions
- Validate monetary amounts using `BigDecimal`
- Manage database transactions with `@Transactional`
- Detect concurrent modifications using optimistic locking (`@Version`)
- Handle malformed requests and selected errors
- Persist account information using Spring Data JPA
- Automated unit tests with JUnit 5 and Mockito

## Technologies

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming |
| Spring Boot 4.1.1 | Application framework |
| Spring Web MVC | REST API development |
| Spring Data JPA | Data access |
| Hibernate | Object-relational mapping |
| H2 Database | Persistent local database |
| Maven | Build and dependency management |
| JUnit 5 | Automated unit testing |
| Mockito | Mocking dependencies |
| Git & GitHub | Version control |

## Architecture

The project follows a layered architecture that separates API handling, business logic, and database access.

```text
banking-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/banking_api/
│   │   │   ├── BankingApiApplication.java
│   │   │   ├── Account.java
│   │   │   ├── AccountRepository.java
│   │   │   ├── AccountService.java
│   │   │   ├── AccountController.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── HealthController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/example/banking_api/
│       ├── AccountServiceTest.java
│       └── BankingApiApplicationTests.java
├── pom.xml
└── README.md
```

### Application Layers

- **Controller:** Exposes RESTful API endpoints and processes HTTP requests.
- **Service:** Implements account management, transfers, validation, and transaction handling.
- **Repository:** Provides database operations through Spring Data JPA.
- **Entity:** Represents persistent bank account information.
- **Exception Handler:** Provides structured responses for selected API errors.

## REST API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check application status |
| POST | `/api/accounts` | Create a new bank account |
| GET | `/api/accounts` | Retrieve all accounts |
| GET | `/api/accounts/{id}` | Retrieve account by ID |
| GET | `/api/accounts/number/{accountNumber}` | Retrieve account by account number |
| POST | `/api/accounts/{id}/deposit` | Deposit money |
| POST | `/api/accounts/{id}/withdraw` | Withdraw money |
| POST | `/api/accounts/transfer` | Transfer money between accounts |

## API Examples

### 1. Create a Bank Account

**POST** `/api/accounts`

```json
{
  "accountNumber": "DEMO10001",
  "accountHolder": "Max Mustermann",
  "balance": 1000.00
}
```

### 2. Deposit Money

**POST** `/api/accounts/1/deposit`

```json
{
  "amount": 200.00
}
```

### 3. Withdraw Money

**POST** `/api/accounts/1/withdraw`

```json
{
  "amount": 150.00
}
```

### 4. Transfer Money

**POST** `/api/accounts/transfer`

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 250.00
}
```

**Successful response:**

```json
{
  "status": "SUCCESS",
  "message": "Transfer completed successfully"
}
```

### 5. Retrieve an Account

**GET** `/api/accounts/1`

Example response:

```json
{
  "id": 1,
  "version": 1,
  "accountNumber": "DEMO10001",
  "accountHolder": "Max Mustermann",
  "balance": 810.00
}
```

The account balance and version depend on the transactions performed.

## Transaction Management

Money transfers are executed using Spring's `@Transactional` annotation.

Both account updates occur within a single database transaction. If the operation fails before committing, the transaction is rolled back.

The application also uses **optimistic locking** through JPA's `@Version` annotation. This allows Hibernate to detect conflicting updates when multiple transactions modify the same account.

Automatic retries and comprehensive concurrent transaction handling are not implemented.

## Validation and Error Handling

The application validates financial operations to prevent invalid account updates.

Examples include:

- Rejecting negative or zero transaction amounts
- Rejecting amounts with more than two decimal places
- Preventing withdrawals with insufficient funds
- Preventing transfers to the same account
- Rejecting transfers involving missing accounts

A global exception handler processes selected errors, including malformed JSON requests and optimistic locking conflicts.

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK) 21
- Git
- An IDE such as IntelliJ IDEA or Visual Studio Code

Maven can be executed using the included Maven Wrapper.

### 1. Clone the Repository

```bash
git clone https://github.com/erenkrsln/java-spring-boot-banking-api.git
cd java-spring-boot-banking-api
```

### 2. Start the Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The application starts at:

`http://localhost:8080`

### 3. Access the H2 Database Console

Open:

`http://localhost:8080/h2-console`

Use the following connection settings:

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/bankingdb` |
| Username | `sa` |
| Password | Leave empty |

The H2 database is stored locally. Generated database files are excluded from version control.

## Automated Testing

The project includes **11 automated tests**, covering the banking service logic and Spring Boot application context.

Test scenarios include:

- Creating bank accounts
- Processing deposits
- Processing withdrawals
- Successful money transfers
- Preventing withdrawals with insufficient funds
- Rejecting transfers with insufficient funds
- Preventing transfers to the same account
- Rejecting negative deposits
- Rejecting zero-value withdrawals
- Rejecting transfers to non-existent accounts
- Verifying the Spring Boot application context

### Run Tests

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

**Latest verified test result:**

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

## Current Limitations

This application is designed for educational and portfolio purposes.

Production banking systems require additional capabilities, including authentication, authorization, immutable transaction records, audit logging, database migration management, stronger concurrency guarantees, and more extensive integration testing.

## Future Improvements

- Spring Security and JWT authentication
- PostgreSQL integration
- Transaction history and audit logs
- OpenAPI / Swagger documentation
- Docker containerization
- Flyway database migrations
- Integration and concurrency tests
- Improved API error handling

## Author

**Eren Karaarslan**

Media Engineering (B.Eng.)  
Technische Hochschule Nürnberg

GitHub: [erenkrsln](https://github.com/erenkrsln)
