[![Java CI](https://github.com/akshitap30/ExpenseTracker/actions/workflows/ci.yml/badge.svg)](https://github.com/akshitap30/ExpenseTracker/actions/workflows/ci.yml)

# ExpenseTracker

A RESTful expense management API built with Spring Boot, Spring Security, and JWT authentication.

Users can register, log in, and securely manage their personal expenses — including creating, updating, searching, filtering, paginating, and attaching receipts — with full data isolation between users.

## Features

- User registration and login with BCrypt password hashing
- Stateless JWT-based authentication
- Per-user data isolation — expense operations are scoped to the authenticated user
- Full CRUD for expense records
- Title-based expense search
- Category-based filtering
- Paginated expense retrieval
- Receipt upload with file type and size validation (PDF, JPG, PNG — max 10 MB)
- Receipt download
- Centralized exception handling with consistent error responses
- Request validation using Jakarta Bean Validation
- Interactive API documentation via Swagger/OpenAPI
- Docker and Docker Compose support
- Unit and integration tests with CI via GitHub Actions

## Tech Stack

| Technology         | Purpose                           |
|--------------------|-----------------------------------|
| Java 21            | Core language                     |
| Spring Boot 3      | REST API framework                |
| Spring Security    | Authentication and authorization  |
| JWT (jjwt 0.11.5)  | Stateless token authentication    |
| Spring Data JPA    | Data access layer                 |
| Hibernate          | ORM                               |
| PostgreSQL 16      | Relational database               |
| H2 (test scope)    | In-memory DB for integration tests|
| Maven              | Build and dependency management   |
| Docker             | Containerization                  |
| Swagger / OpenAPI  | Interactive API documentation     |
| JUnit 5 / Mockito  | Unit and integration testing      |
| GitHub Actions     | CI pipeline                       |
| Spring Mail        | Welcome email on registration     |
| Spring Actuator    | Health endpoint                   |

## Architecture

The application follows a standard layered architecture:

```
Client
  │
  ▼
JWT Authentication Filter
  │  (validates Bearer token, populates SecurityContext)
  ▼
Spring Security
  │
  ▼
REST Controllers  (/auth, /expenses)
  │
  ▼
Service Layer
  │
  ▼
Repository Layer  (Spring Data JPA)
  │
  ▼
PostgreSQL Database
```

## Project Structure

```
src/
├── main/
│   ├── java/com/project1/ExpenseTracker/
│   │   ├── config/          # SecurityConfig, OpenApiConfig
│   │   ├── controller/      # AuthController, ExpenseController
│   │   ├── dto/             # Request and response DTOs
│   │   ├── entity/          # User, Expense JPA entities
│   │   ├── exception/       # GlobalExceptionHandler, custom exceptions
│   │   ├── repository/      # UserRepository, ExpenseRepository
│   │   ├── security/        # JwtService, JwtAuthenticationFilter, CustomUserDetailsService
│   │   └── service/         # AuthService, ExpenseService, EmailService, FileStorageService
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/project1/ExpenseTracker/
    │   ├── ExpenseServiceTest.java         # Unit tests (Mockito)
    │   └── ExpenseTrackerApplicationTests.java  # Spring context integration test
    └── resources/
        └── application.properties         # H2 in-memory config for tests
```

## API Endpoints

### Authentication

| Method | Endpoint         | Description                          |
|--------|------------------|--------------------------------------|
| POST   | `/auth/register` | Register a new user                  |
| POST   | `/auth/login`    | Authenticate and receive a JWT token |

### Expenses (require `Authorization: Bearer <token>`)

| Method | Endpoint                              | Description                              |
|--------|---------------------------------------|------------------------------------------|
| POST   | `/expenses`                           | Create a new expense                     |
| GET    | `/expenses`                           | Get all expenses for the logged-in user  |
| GET    | `/expenses/{id}`                      | Get a single expense by ID               |
| PUT    | `/expenses/{id}`                      | Update an expense                        |
| DELETE | `/expenses/{id}`                      | Delete an expense                        |
| GET    | `/expenses/search?title=Food`         | Search expenses by title                 |
| GET    | `/expenses/category?category=Food`    | Filter expenses by category              |
| GET    | `/expenses/page?page=0&size=10`       | Get paginated expenses                   |
| POST   | `/expenses/{id}/receipt`              | Upload a receipt for an expense          |
| GET    | `/expenses/{id}/receipt`              | Download a receipt                       |

## Authentication

The API uses stateless JWT Bearer authentication.

1. **Register** — `POST /auth/register`
2. **Login** — `POST /auth/login` → receive a JWT token
3. **Use the token** — include it on all expense requests:

```
Authorization: Bearer <JWT_TOKEN>
```

Tokens expire after 24 hours. Each expense endpoint is scoped to the authenticated user.

## Validation & Error Handling

Requests are validated using Jakarta Bean Validation:

- Expense title, category, date — required
- Amount — required, must be positive

Invalid requests return `400 Bad Request`.

All errors follow a consistent JSON format:

```json
{
  "timestamp": "2026-09-12T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Expense not found with id: 42",
  "path": "/expenses/42"
}
```

## Receipt / File Handling

Receipts can be attached to individual expenses.

| Property          | Value                  |
|-------------------|------------------------|
| Supported formats | PDF, JPG, JPEG, PNG    |
| Maximum size      | 10 MB                  |
| Storage           | Local filesystem       |

Files are assigned unique names and validated for type and size before storage. File paths are normalized to prevent path traversal.

## API Documentation

Once the application is running, interactive Swagger documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```

## Screenshots

API documentation screenshots are in [`docs/screenshots/`](docs/screenshots/).

> To add screenshots: start the application, open the Swagger UI, and save screenshots to `docs/screenshots/`.

## Getting Started

### Prerequisites

- Java 21 (JDK)
- Maven (or use the included `mvnw` / `mvnw.cmd` wrapper)
- PostgreSQL 16
- Docker (optional, for containerized setup)

### Clone

```bash
git clone https://github.com/akshitap30/ExpenseTracker.git
cd ExpenseTracker
```

### Configure environment

Create a `.env` file in the project root (this file is not committed):

```env
# PostgreSQL
POSTGRES_DB=expense_tracker
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password

# Application datasource
DB_URL=jdbc:postgresql://localhost:5432/expense_tracker
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key-at-least-32-characters-long

# Email (Mailtrap or similar)
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password

# File uploads
UPLOAD_DIR=uploads
```

### Create the PostgreSQL database

```sql
CREATE DATABASE expense_tracker;
```

### Run with Maven

Windows:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/expense_tracker"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-secret-key-at-least-32-characters-long"
$env:MAIL_USERNAME="your_mail_username"
$env:MAIL_PASSWORD="your_mail_password"
$env:UPLOAD_DIR="uploads"
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/expense_tracker
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key-at-least-32-characters-long
export MAIL_USERNAME=your_mail_username
export MAIL_PASSWORD=your_mail_password
export UPLOAD_DIR=uploads
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`.

### Run with Docker Compose

```bash
docker compose up --build
```

This starts both PostgreSQL and the application together. Environment variables are read from `.env`.

## Testing

Tests use H2 in-memory database — no PostgreSQL instance required.

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

Expected output:

```
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Tests are also run automatically on every push and pull request via GitHub Actions.

## CI

This project uses GitHub Actions for continuous integration.

The CI pipeline:

1. Checks out the repository
2. Sets up Java 21 (Eclipse Temurin)
3. Starts a PostgreSQL 16 service container
4. Runs the full Maven test suite

[![Java CI](https://github.com/akshitap30/ExpenseTracker/actions/workflows/ci.yml/badge.svg)](https://github.com/akshitap30/ExpenseTracker/actions/workflows/ci.yml)

## Security

- Passwords are hashed with BCrypt and never stored in plain text
- JWT secrets and database credentials are provided through environment variables and are not committed to version control
- All expense endpoints require a valid JWT and return only the authenticated user's data
- Uploaded files are validated for type and size
- File paths are normalized to mitigate path traversal risks

## Roadmap

- Monthly spending reports and analytics
- Advanced date-range filtering and sorting
- Refresh token support
- Role-based authorization
- Cloud-based receipt storage (e.g., S3)
- Budget management and alerts

## License

This project is licensed under the [MIT License](LICENSE).

## Author

**Akshita Pardeshi**  
[GitHub](https://github.com/akshitap30)
