# ExpenseTracker

A production-style RESTful expense management API built with Spring Boot, Spring Security, and JWT authentication.

ExpenseTracker enables authenticated users to manage personal expenses end-to-end — creating, searching, filtering, and paginating records, as well as attaching and retrieving receipts — behind a secure, stateless authentication layer.

## Features

* User registration and login with secure password handling
* Stateless JWT-based authentication and authorization
* Per-user data isolation — every expense is scoped to its owner
* Full CRUD support for expense records
* Search expenses by title
* Category-based filtering
* Paginated retrieval
* Receipt upload with file type and size validation
* Receipt download
* Centralized exception handling
* Request validation using Jakarta Bean Validation
* Interactive API documentation using Swagger/OpenAPI
* Docker and Docker Compose support
* Unit and integration tests

## Tech Stack

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java              | Core language                    |
| Spring Boot       | REST API framework               |
| Spring Security   | Authentication and authorization |
| JWT               | Stateless authentication         |
| Spring Data JPA   | Data access layer                |
| Hibernate         | ORM                              |
| MySQL             | Relational database              |
| Maven             | Build and dependency management  |
| Docker            | Containerization                 |
| Swagger / OpenAPI | API documentation                |
| JUnit             | Testing                          |

## Architecture

The application follows a layered architecture:

```text
Client
  |
  v
REST Controllers
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
MySQL Database
```

Protected requests pass through JWT authentication:

```text
Client
  |
  | Authorization: Bearer <JWT>
  v
JWT Authentication Filter
  |
  v
Spring Security
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
Database
```

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/project1/ExpenseTracker/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/project1/ExpenseTracker/
```

## API Endpoints

### Authentication

| Method | Endpoint         | Description                    |
| ------ | ---------------- | ------------------------------ |
| POST   | `/auth/register` | Register a new user            |
| POST   | `/auth/login`    | Authenticate and receive a JWT |

### Expenses

| Method | Endpoint                           | Description                                      |
| ------ | ---------------------------------- | ------------------------------------------------ |
| POST   | `/expenses`                        | Create a new expense                             |
| GET    | `/expenses`                        | Retrieve all expenses for the authenticated user |
| GET    | `/expenses/{id}`                   | Retrieve a single expense                        |
| PUT    | `/expenses/{id}`                   | Update an existing expense                       |
| DELETE | `/expenses/{id}`                   | Delete an expense                                |
| GET    | `/expenses/search?title=Food`      | Search expenses by title                         |
| GET    | `/expenses/category?category=Food` | Filter expenses by category                      |
| GET    | `/expenses/page?page=0&size=10`    | Retrieve paginated expenses                      |
| POST   | `/expenses/{id}/receipt`           | Upload a receipt                                 |
| GET    | `/expenses/{id}/receipt`           | Download a receipt                               |

## Authentication

The API is secured using JWT Bearer tokens. Once a user logs in, the returned token must be included on subsequent requests to protected endpoints:

```text
Authorization: Bearer <JWT_TOKEN>
```

All expense endpoints require a valid token and are scoped to the requesting user.

## Validation

Expense requests are validated using Jakarta Bean Validation:

* Title must not be blank
* Amount is required and must be greater than zero
* Category must not be blank
* Date is required

Invalid requests return a `400 Bad Request`.

## Exception Handling

A global exception handler provides consistent error responses for:

* Resource not found
* Duplicate resources
* Validation failures
* Invalid arguments
* File upload size violations
* Unexpected server errors

Example:

```json
{
  "timestamp": "2026-09-11T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Expense not found",
  "path": "/expenses/1"
}
```

## Receipt Management

Receipts can be attached to and retrieved from individual expenses.

**Supported file types:**

```text
PDF, JPG, JPEG, PNG
```

**Maximum file size:**

```text
10 MB
```

Uploaded files are assigned unique filenames and validated before storage.

## API Documentation

Once the application is running, Swagger documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Getting Started

### Prerequisites

* Java (JDK 17+ recommended)
* Maven
* MySQL
* Docker (optional)

### Clone the repository

```bash
git clone https://github.com/akshitap30/ExpenseTracker.git
cd ExpenseTracker
```

### Configure the database

Create a MySQL database and configure the connection properties. Keep credentials and secrets out of version control.

### Run with Maven

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

### Run with Docker

```bash
docker compose up --build
```

## Testing

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

## Security

* Passwords are hashed and never stored in plain text
* JWT secrets and database credentials are kept out of the repository
* Protected expense endpoints require authentication
* Expense data is scoped to the authenticated user
* Uploaded files are validated for size and content type
* File paths are normalized to mitigate path traversal risks

## Roadmap

* Expense summary and analytics dashboard
* Monthly spending reports
* Budget management and alerts
* Category-wise spending charts
* Advanced date-range filtering
* Sorting support
* Refresh token mechanism
* Role-based authorization
* Cloud-based receipt storage
* CI/CD pipeline
* Production deployment

## Status

Actively developed and maintained.

## Author

**Akshita Pardeshi**
[GitHub](https://github.com/akshitap30)
