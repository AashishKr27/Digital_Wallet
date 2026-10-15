
# Digital Wallet & Banking System

A backend banking application built with **Java, Spring Boot, Spring Security, JWT, and PostgreSQL**. The project simulates essential digital wallet operations, including user registration, authentication, account management, deposits, withdrawals, transaction history, and money transfers.

The goal is to build a secure, reliable, and maintainable REST API while learning backend development concepts used in real-world financial applications.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Implemented Features](#implemented-features)
- [Upcoming Features](#upcoming-features)
- [Project Architecture](#project-architecture)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Authentication and Security](#authentication-and-security)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Testing the API](#testing-the-api)
- [Learning Objectives](#learning-objectives)
- [Future Roadmap](#future-roadmap)

## Overview

Digital Wallet is a backend project designed to simulate basic banking operations through RESTful APIs.

Users can register, authenticate using JWT, access their accounts, deposit and withdraw money, view transaction history, and transfer funds to other users.

The project also focuses on important backend engineering concepts such as database transactions, concurrency control, input validation, exception handling, pagination, and secure access to user-specific resources.

**Current account model:** Each user has one account.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4 | Backend application framework |
| Spring Web | REST API development |
| Spring Security | Authentication and request security |
| JSON Web Token (JWT) | Stateless authentication |
| BCrypt | Password hashing |
| Spring Data JPA | Database access and persistence |
| Hibernate | ORM implementation |
| PostgreSQL 18 | Relational database |
| Maven | Dependency management and build automation |
| Jakarta Bean Validation | Request validation |
| Postman | API testing |
| Git and GitHub | Version control and source code hosting |
| IntelliJ IDEA | Development environment |

## Implemented Features

### 1. User Management

- User registration.
- Password hashing using BCrypt.
- User login and authentication.
- JWT generation after successful authentication.

### 2. Authentication and Authorization

- JWT-based authentication.
- Protected API endpoints.
- Extraction of the authenticated user's identity from the JWT.
- Generic authentication failure responses for invalid tokens.

### 3. Account Management

- Account creation for registered users.
- One account per user.
- Retrieval of the authenticated user's account.
- Automatic account identification using the authenticated user's identity.

### 4. Deposits and Withdrawals

- Deposit money into an account.
- Withdraw money from an account.
- Update account balances after successful transactions.
- Prevent withdrawals that exceed the available balance.
- Persist transaction records in PostgreSQL.
- Use database transactions to maintain consistency.

### 5. Transaction History

- Retrieve transaction history for the authenticated user's account.
- Pagination of transaction results.
- Sorting transactions by creation time in descending order.
- Filter transactions by transaction type.
- Filter transactions using date ranges.
- Combine supported filters.
- Validate date-range inputs.

### 6. Money Transfers

- Transfer money between two users.
- Identify the sender through JWT authentication.
- Identify the recipient using their user ID.
- Prevent transfers to the sender's own account.
- Validate the recipient and account availability.
- Prevent transfers when the sender has insufficient funds.
- Deduct money from the sender and credit the recipient.
- Record the sender's withdrawal and the recipient's deposit.
- Execute the transfer within a database transaction.
- Use pessimistic locking to protect account updates from concurrent modifications.
- Acquire account locks in a consistent order to reduce deadlock risk.

### 7. Validation and Exception Handling

- Request validation using Jakarta Bean Validation.
- Custom exceptions for business-rule violations.
- Appropriate HTTP error responses for common failures.
- Validation for positive transaction amounts and recipient IDs.

## Upcoming Features

The following features are planned or still under development. They should not be considered implemented until completed and tested.

### 1. Idempotency and Retry Safety

- Add idempotency keys for money transfers.
- Prevent duplicate transfers when clients retry requests.
- Handle repeated requests safely after network failures.
- Store and retrieve the result of previously processed requests.

### 2. Automated Testing

- Unit tests for service-layer business logic.
- Integration tests for REST APIs and database operations.
- Tests for successful and failed transfers.
- Tests for insufficient balance, self-transfers, and invalid input.
- Tests for transaction rollback.
- Concurrency tests for simultaneous deposits, withdrawals, and transfers.
- Tests for idempotency and duplicate requests once implemented.

### 3. API Documentation

- Integrate OpenAPI and Swagger UI.
- Document request and response schemas.
- Describe authentication requirements.
- Document possible HTTP status codes and error responses.

### 4. Containerization

- Create a Dockerfile for the Spring Boot application.
- Configure PostgreSQL and the application using Docker Compose.
- Externalize environment-specific configuration.
- Make local setup and deployment more consistent.

### 5. Additional Improvements

- Improve logging and error diagnostics.
- Add database migration management.
- Review transaction boundaries and concurrency behavior.
- Improve configuration and secret management.
- Add CI workflows for automated builds and tests.
- Explore deployment to a cloud platform.

## Project Architecture

The application follows a layered architecture to separate HTTP handling, business logic, and database operations.

```text
Client (Postman / Frontend)
          |
          v
     REST Controllers
          |
          v
       Services
          |
          v
      Repositories
          |
          v
      PostgreSQL
```

### Layer Responsibilities

- **Controller:** Receives HTTP requests, validates request bodies, and returns HTTP responses.
- **Service:** Implements business rules, authentication-related operations, and transaction workflows.
- **Repository:** Accesses and updates persistent data through Spring Data JPA.
- **Entity:** Represents database-persisted objects.
- **DTO:** Defines request and response structures.
- **Security:** Handles JWT authentication and protection of API endpoints.
- **Exception Handling:** Converts application exceptions into HTTP error responses.

## Database Design

The application currently uses PostgreSQL with the following core tables.

### Users

Stores registered user information and authentication-related data.

Typical responsibilities:
- Store user identity.
- Store unique login information.
- Store hashed passwords.

### Accounts

Stores account information and current balances.

Key characteristics:
- Each account belongs to a user.
- The user relationship is enforced through a foreign key.
- Account IDs are generated by the database.
- Account balances are constrained to non-negative values.
- An index on the user ID supports account lookup.

### Transactions

Stores financial transaction records.

Each transaction records information such as:
- Transaction ID.
- Associated account ID.
- Transaction type, such as `DEPOSIT` or `WITHDRAWAL`.
- Transaction amount.
- Creation timestamp.

Transfers are represented by two transaction records: a withdrawal for the sender and a deposit for the recipient.

## API Endpoints

The following endpoints describe the application's current API structure. Confirm the exact request fields and response schemas against the corresponding controller and DTO classes.

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/users/signup` | Register a user |
| POST | `/api/users/login` | Authenticate a user and obtain a JWT |
| POST | `/api/accounts` | Create an account |
| GET | `/api/accounts` | Retrieve the authenticated user's account |
| POST | `/api/transactions` | Create a deposit or withdrawal transaction |
| GET | `/api/transactions` | Retrieve paginated transaction history |
| POST | `/api/transfers` | Transfer money to another user |

**Note:** Authentication and request requirements depend on the endpoint and the current security configuration. The account and transaction endpoints use the authenticated user's identity to restrict access to their own resources.

### Example: Money Transfer

**Request**

`POST /api/transfers`

```json
{
  "recipientUserId": 21,
  "amount": 2000.00
}
```

Include the JWT in the request's `Authorization` header:

```text
Authorization: Bearer <your-jwt-token>
```

**Example response**

```json
{
  "senderTransactionId": 24,
  "recipientTransactionId": 25,
  "recipientUserId": 21,
  "amount": 2000.00,
  "transferredAt": "2026-10-14T11:22:06.503382"
}
```

The IDs and timestamp above are illustrative; actual values are generated by the application.

## Authentication and Security

The application uses JWT-based authentication.

1. A user registers with the application.
2. The password is hashed using BCrypt before storage.
3. The user logs in with their credentials.
4. The application generates a JWT after successful authentication.
5. The client sends the token with protected requests.
6. The security filter validates the token and establishes the authenticated identity.
7. Services use that identity to access the appropriate user's resources.

### Security Considerations

- Never store plain-text passwords.
- Do not commit JWT signing secrets or database credentials.
- Use environment variables or an appropriate secret-management solution for sensitive configuration.
- Validate user input before processing financial operations.
- Keep balance changes and their corresponding transaction records within a single database transaction.
- Apply database locking where needed to prevent conflicting concurrent balance updates.
- Ensure users cannot access or modify another user's account by supplying arbitrary account IDs.

This project is intended for learning and demonstration. It is not a production-ready banking platform.

## Getting Started

### Prerequisites

Install the following:

- Java 21
- PostgreSQL 18
- Maven, or use the included Maven Wrapper
- Git
- Postman (optional, for API testing)

### 1. Clone the Repository

```bash
git clone <your-github-repository-url>
cd digital-wallet
```

Replace the repository URL with the actual URL of your GitHub repository.

### 2. Create the Database

Create a PostgreSQL database named:

```sql
CREATE DATABASE digital_wallet;
```

Configure the database connection using your local application configuration.

The required tables and constraints must be available before running the application. Use the project's existing schema setup.

### 3. Configure Environment Variables

Configure your database connection and JWT signing secret according to the properties used in your application.

Example environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/digital_wallet
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_secure_jwt_secret
```

These names are examples. Map them to the actual property names referenced by your application's configuration.

**Do not use these placeholders as real credentials or commit your actual secrets to GitHub.**

### 4. Run the Application

Using the Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, with Maven installed:

```bash
mvn spring-boot:run
```

The application runs on port `8080` by default unless configured otherwise.

### 5. Test the APIs

Open Postman and test the registration, login, account, transaction, and transfer endpoints.

For protected endpoints, obtain a JWT by logging in and include it as a Bearer token.

## Testing the API

The current implementation has been manually tested using Postman for key workflows, including:

- User registration and JWT authentication.
- Account creation and retrieval.
- Deposits and withdrawals.
- Transaction history and filtering.
- Successful transfers between users.
- Self-transfer rejection.
- Insufficient-balance rejection.
- Account balance updates following transfers.

Automated test coverage is planned and will be documented as it is implemented.

## Learning Objectives

This project is being developed to gain practical experience with:

- REST API design using Spring Boot.
- Java backend application architecture.
- JWT authentication and Spring Security.
- PostgreSQL schema design and constraints.
- Spring Data JPA and Hibernate.
- Database transactions and rollback behavior.
- Concurrency control and pessimistic locking.
- Financial transaction consistency.
- Pagination, sorting, and filtering.
- Input validation and exception handling.
- Idempotent API design.
- Automated testing and API documentation.
- Docker-based development and deployment.

## Future Roadmap

The project will continue to evolve through the following stages:

- [x] User registration and login
- [x] Password hashing
- [x] JWT-based authentication
- [x] Account creation and retrieval
- [x] Deposits and withdrawals
- [x] Transaction history, pagination, and filtering
- [x] Money transfers
- [x] Basic concurrency control for account updates
- [ ] Idempotency and retry-safe transfers
- [ ] Comprehensive automated testing
- [ ] OpenAPI / Swagger documentation
- [ ] Database migration management
- [ ] Docker and Docker Compose
- [ ] CI pipeline
- [ ] Deployment and production-readiness improvements

## Disclaimer

This project is an educational backend application intended to demonstrate software engineering and database concepts. It is not designed or certified for handling real money or production banking operations.

## Author

**Ashish**

Built as a hands-on project to strengthen Java, Spring Boot, PostgreSQL, backend engineering, and financial transaction processing skills.
