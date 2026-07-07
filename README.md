# TaskFlow API PDD Learning Lab

TaskFlow API PDD Learning Lab is a Spring Boot 3 sample project that demonstrates how to build a small backend API using **Prompt-Driven Development**, requirement-first planning, implementation plans, automated tests, and human review.

This repository is intentionally small. The goal is not to build a full project management platform. The goal is to show a repeatable engineering workflow where an AI coding assistant is guided by clear requirements, reviewed plans, tests, and controlled milestones.

## What this project demonstrates

This repo shows how to move from a requirement document to working code using a reviewable PDD workflow:

1. Start with a business requirement document.
2. Create a project-level engineering plan.
3. Define an API contract.
4. Create milestone-specific implementation plans.
5. Write tests for expected behavior.
6. Implement the API in small reviewed steps.
7. Review final implementation against the requirement.

## MVP scope

The TaskFlow API supports the following Version 1 capabilities:

| Capability                                | Status      |
| ----------------------------------------- | ----------- |
| Create project                            | Implemented |
| Create task under project                 | Implemented |
| Assign or update task assignee            | Implemented |
| Change task status                        | Implemented |
| List tasks by project                     | Implemented |
| List tasks by status                      | Implemented |
| List tasks by assignee                    | Implemented |
| Consistent validation and error responses | Implemented |

## Out of scope

The MVP intentionally does not include:

* Authentication or authorization
* User profile management
* UI/frontend application
* Notifications
* Comments or attachments
* Audit logging or event streaming
* External integrations
* Production deployment
* Advanced monitoring
* Task deletion or project deletion

## Tech stack

* Java 17
* Spring Boot 3
* Spring Web
* Spring Validation
* Spring Data JPA
* H2 Database
* JUnit 5
* Mockito
* Maven

## Project structure

```text
.
├── docs
│   ├── .ai
│   │   ├── Plan.md
│   │   ├── 001_API_Contract.md
│   │   └── implementation-plans
│   ├── prompts
│   └── review-log
├── src
│   ├── main
│   │   └── java
│   └── test
│       └── java
├── pom.xml
├── README.md
└── LICENSE
```

## PDD documentation flow

The important part of this repository is the documentation trail.

| Document area                    | Purpose                                                   |
| -------------------------------- | --------------------------------------------------------- |
| `docs/.ai/Plan.md`               | Living engineering plan based on the requirement document |
| `docs/.ai/001_API_Contract.md`   | API behavior contract before implementation               |
| `docs/.ai/implementation-plans/` | Milestone-specific implementation plans                   |
| `docs/prompts/`                  | Prompt history used during the PDD workflow               |
| `docs/review-log/`               | Review notes and implementation validation                |

This structure makes the repo useful as a learning example for AI-assisted engineering because the final code is not shown in isolation. The planning and review process is visible.

## API overview

Base path:

```text
/api
```

Main capabilities:

| Operation                 | Example endpoint                                |
| ------------------------- | ----------------------------------------------- |
| Create project            | `POST /api/projects`                            |
| Create task under project | `POST /api/projects/{projectId}/tasks`          |
| List tasks by project     | `GET /api/projects/{projectId}/tasks`           |
| Assign task               | `PATCH /api/tasks/{taskId}/assignee`            |
| Change task status        | `PATCH /api/tasks/{taskId}/status`              |
| List tasks by status      | `GET /api/tasks?status=TODO`                    |
| List tasks by assignee    | `GET /api/tasks?assigneeEmail=user@example.com` |

## Task statuses

Supported task statuses:

```text
TODO
IN_PROGRESS
BLOCKED
DONE
```

## Validation behavior

The API validates the main MVP rules:

| Rule                                       | Behavior                                    |
| ------------------------------------------ | ------------------------------------------- |
| Project name is required                   | Validation error                            |
| Task title is required                     | Validation error                            |
| Task must belong to an existing project    | Not-found error                             |
| Task status must be supported              | Validation error                            |
| Assignee email must be valid when provided | Validation error                            |
| Due date is optional                       | No additional due-date business rule in MVP |

## Error response approach

The API returns consistent error responses for validation failures and missing resources. Error responses are designed to include:

* Machine-readable error code
* Human-readable message
* Context about the failing input

## Running the project locally

Prerequisite:

```text
Java 17
Maven
```

Run tests:

```bash
mvn test
```

Run the application:

```bash
mvn spring-boot:run
```

The application uses H2 for local development.

## Running tests

```bash
mvn test
```

The test suite covers controller behavior, service behavior, validation, error handling, DAO/repository behavior, and integration paths for the MVP API.

## Why this repo exists

This project is a companion learning repo for explaining **Prompt-Driven Development**.

The main idea is simple:

> Do not ask AI to directly generate production code from a vague request. First create requirements, plans, API contracts, implementation plans, and tests. Then allow code generation only inside a reviewed milestone.

This creates a more controlled workflow where the developer remains responsible for architecture, scope, validation, and final review.

## Suggested learning path

Review the repo in this order:

1. Requirement document
2. `docs/.ai/Plan.md`
3. `docs/.ai/001_API_Contract.md`
4. Milestone implementation plans
5. Tests
6. Source code
7. Review log

## License

This project is licensed under the MIT License.
