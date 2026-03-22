# TaskFlow — Employee Task Management System

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1-purple)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)
![CI/CD](https://img.shields.io/badge/CI/CD-GitHub_Actions-black)
![Docker](https://img.shields.io/badge/Container-Docker-blue)

A production-ready Employee Task Management System where managers assign tasks to employees, track progress, and measure efficiency using AI-powered analytics.

🔗 **Live Demo:** [Swagger UI](https://taskflow-api-huarctduebcrbed3.centralindia-01.azurewebsites.net/swagger-ui/index.html)

## Features

- 🔐 **Role-based Authentication** — ADMIN and EMPLOYEE roles with JWT
- 📋 **Task Assignment** — Admins create and assign tasks to employees
- 🔄 **Task Lifecycle** — ASSIGNED → IN_PROGRESS → UNDER_REVIEW → COMPLETED
- 📜 **Audit Trail** — Every status change recorded with timestamp and comments
- 📊 **Efficiency Analytics** — Automated efficiency scoring per employee
- 🏆 **Team Reports** — Top performer, completion rates, overdue tracking
- 🤖 **AI Integration** — Gemini AI for task priority suggestions
- 📖 **Swagger UI** — Interactive API documentation
- 🚀 **CI/CD** — Automated build and test with GitHub Actions
- 🐳 **Docker** — Containerized for easy deployment

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.1 |
| Framework | Spring Boot 4.0 |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Authentication | JWT + Spring Security |
| Documentation | Swagger / OpenAPI 3 |
| CI/CD | GitHub Actions |
| Container | Docker |
| Deployment | Railway |

## Getting Started

### Prerequisites
- Java 21
- Docker Desktop

### Run Locally

1. Clone the repository
```bash
   git clone https://github.com/divyanshu-tyagi/employee-task-manager.git
   cd employee-task-manager
```

2. Start PostgreSQL
```bash
   docker-compose up -d
```

3. Set environment variables in IntelliJ:
```
   JWT_SECRET=your-secret-key-min-32-chars
   JWT_EXPIRATION=86400000
```

4. Run the application
```bash
   ./gradlew bootRun
```

5. Open Swagger UI
```
   http://localhost:8080/swagger-ui/index.html
```

## API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register as ADMIN or EMPLOYEE |
| POST | `/api/auth/login` | Login and get JWT token |

### Tasks
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/tasks` | ADMIN | Create and assign task |
| GET | `/api/tasks` | ALL | Get my tasks |
| GET | `/api/tasks/assigned` | ADMIN | Get tasks I assigned |
| GET | `/api/tasks/{id}` | ALL | Get task by ID |
| PUT | `/api/tasks/{id}` | ADMIN | Update task details |
| PATCH | `/api/tasks/{id}/status` | EMPLOYEE | Update task status |
| GET | `/api/tasks/{id}/history` | ALL | Get task audit trail |
| DELETE | `/api/tasks/{id}` | ADMIN | Delete task |

### Analytics
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/api/analytics/me` | ALL | My efficiency score |
| GET | `/api/analytics/employee/{id}` | ADMIN | Employee efficiency |
| GET | `/api/analytics/team` | ADMIN | Full team report |

### Users
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/api/users/employees` | ADMIN | List all employees |
| GET | `/api/users/employees/{id}` | ADMIN | Get employee by ID |

## Efficiency Score Formula
```
Completion Rate = (Completed Tasks / Total Assigned) × 100
Overdue Rate    = (Overdue Tasks / Total Assigned) × 100
Efficiency Score = (Completion Rate × 0.7) + ((100 - Overdue Rate) × 0.3)
```

## CI/CD Pipeline

Every push to `Main` branch automatically:
1. Sets up JDK 21
2. Builds the application
3. Runs all tests
4. Uploads build artifact

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `JWT_SECRET` | Secret key for JWT (min 32 chars) | ✅ Yes |
| `JWT_EXPIRATION` | Token expiry in ms (default: 86400000) | No |
| `PGHOST` | PostgreSQL host | Production |
| `PGPORT` | PostgreSQL port | Production |
| `PGUSER` | PostgreSQL username | Production |
| `PGPASSWORD` | PostgreSQL password | Production |
| `PGDATABASE` | PostgreSQL database name | Production |

## License
MIT