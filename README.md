# TaskFlow API PDD Learning Lab

TaskFlow API PDD Learning Lab is a Spring Boot 3 sample project that demonstrates how to build a small backend API using **Prompt-Driven Development (PDD)**, requirement-first planning, implementation plans, automated tests, and human review.

This repository is intentionally small. The goal is not to build a complete project-management platform. The goal is to demonstrate a repeatable engineering workflow in which an AI coding assistant works from clear requirements, reviewed plans, tests, and controlled milestones.

The repository also preserves how the workflow evolved. The original prompt trail remains unchanged, while later refinements are documented separately.

## What this project demonstrates

This repository shows how to move from a requirement document to working code through a reviewable PDD workflow:

1. Start with a business requirement document.
2. Create a project-level engineering plan.
3. Define the externally visible API contract.
4. Divide the work into reviewable milestones.
5. Create an implementation plan before each milestone.
6. Write tests for the expected behavior.
7. Implement the approved scope in small steps.
8. Review the final implementation against the requirements and contract.
9. Separate stable repository guidance from milestone-specific task context without rewriting the original prompt history.

## Version 1 scope

The TaskFlow API supports the following Version 1 capabilities:

| Capability | Status |
|---|---|
| Create a project | Implemented |
| Create a task under a project | Implemented |
| Assign or update a task assignee | Implemented |
| Change task status | Implemented |
| List tasks by project | Implemented |
| List tasks by status | Implemented |
| List tasks by assignee | Implemented |
| Consistent validation and error responses | Implemented |

## Out of scope

Version 1 intentionally does not include:

- Authentication or authorization
- User profile management
- A UI or frontend application
- Notifications
- Comments or attachments
- Audit logging or event streaming
- External integrations
- Production deployment
- Advanced monitoring
- Task deletion or project deletion

## Tech stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Validation
- Spring Data JPA
- H2 database
- JUnit 5
- Mockito
- Maven

## Project structure

```text
.
├── .github
│   ├── copilot-instructions.md
│   └── workflows
├── docs
│   ├── .ai
│   │   ├── Plan.md
│   │   ├── 001_API_Contract.md
│   │   ├── 002_Implementation_Plan_Project_Skeleton.md
│   │   ├── ...
│   │   └── 016_Final_review.md
│   ├── evolution
│   │   └── 001-persistent-context
│   │       ├── README.md
│   │       └── comparisons
│   │           └── 011_02_Service_Implementation_With_Persistent_Context.md
│   ├── prompts
│   ├── review-log
│   └── Requirement.docx
├── src
│   ├── main
│   │   └── java
│   └── test
│       └── java
├── .gitignore
├── LICENSE
├── pom.xml
└── README.md
```

## PDD documentation flow

The documentation trail is a central part of this repository.

| Document area | Purpose |
|---|---|
| `docs/Requirement.docx` | Original business requirement used as the starting point |
| `docs/.ai/Plan.md` | Living engineering plan, milestone order, and project state |
| `docs/.ai/001_API_Contract.md` | Reviewed contract for externally visible API behavior |
| `docs/.ai/002...016 milestone files` | Milestone-specific implementation plans and final review |
| `docs/prompts/` | Original prompt history used during the PDD workflow |
| `.github/copilot-instructions.md` | Stable repository-wide guidance for AI-assisted work |
| `docs/evolution/` | Refinements introduced after reviewing the original workflow |
| `docs/review-log/` | Independent review notes and implementation validation |
| `src/test/` | Executable evidence of expected behavior |
| `src/main/` | Production implementation created through the reviewed milestones |

The final code is therefore not presented in isolation. The repository retains the requirements, clarification decisions, plans, prompts, tests, implementation, and reviews that produced it.

Later refinements are documented separately so they do not rewrite the original project history.

## Evolution of the workflow

The original TaskFlow API workflow kept important architectural, scope, and process constraints directly inside each milestone prompt.

That approach was intentional.

It made every prompt self-contained, kept task boundaries visible, and helped identify which instructions remained stable across planning, testing, implementation, refactoring, and review milestones.

The repository state documented through the first three Prompt-Driven Development articles is preserved by the Git tag:

```text
pdd-article-3-workflow-snapshot
```

The original prompt history remains unchanged under:

```text
docs/prompts/
```

After reviewing the completed workflow, the repeated instructions were divided into two categories.

### Stable repository context

Examples include:

- Approved sources of truth
- Architectural responsibility boundaries
- Scope-control expectations
- Build and test commands
- The general PDD workflow
- Rules against unrelated changes
- Completion and validation checks

These stable working agreements now live in:

```text
.github/copilot-instructions.md
```

### Milestone-specific context

Examples include:

- The active milestone
- Exact input documents
- The approved implementation plan
- Files permitted to change
- Current RED or GREEN test state
- Feature-specific acceptance criteria
- Milestone-specific success criteria
- Required completion response

These instructions remain inside the individual task prompt.

The distinction is:

> Repository instructions define how the AI assistant should work inside the codebase. The task prompt defines what it should work on next.

The objective is not merely to create shorter prompts. It is to give each type of engineering context a clearer owner.

The refinement is documented in:

```text
docs/evolution/001-persistent-context/README.md
```

A comparison based on the original service implementation prompt is available in:

```text
docs/evolution/001-persistent-context/comparisons/
011_02_Service_Implementation_With_Persistent_Context.md
```

The original prompt remains available at:

```text
docs/prompts/011_02_Service_Implementation.md
```

Repository instructions do not replace:

- Business requirements
- Clarification decisions
- `Plan.md`
- The API contract
- Milestone implementation plans
- Task-specific prompts
- Automated tests
- Continuous integration
- Human engineering review

They provide persistent project context, but the generated result must still be validated against the approved artifacts.

## API overview

Base path:

```text
/api
```

Main capabilities:

| Operation | Example endpoint |
|---|---|
| Create project | `POST /api/projects` |
| Create task under project | `POST /api/projects/{projectId}/tasks` |
| List tasks by project | `GET /api/projects/{projectId}/tasks` |
| Assign or update task assignee | `PATCH /api/tasks/{taskId}/assignee` |
| Change task status | `PATCH /api/tasks/{taskId}/status` |
| List tasks by status | `GET /api/tasks?status=TODO` |
| List tasks by assignee | `GET /api/tasks?assigneeEmail=user@example.com` |

For the complete endpoint definitions, request and response models, validation behavior, status codes, and error contract, review:

```text
docs/.ai/001_API_Contract.md
```

## Task statuses

Supported task statuses:

```text
TODO
IN_PROGRESS
BLOCKED
DONE
```

## Validation behavior

The API validates the main Version 1 rules:

| Rule | Behavior |
|---|---|
| Project name is required | Validation error |
| Task title is required | Validation error |
| Task must belong to an existing project | Not-found error |
| Task status must be supported | Validation error |
| Assignee email must be valid when provided | Validation error |
| Due date is optional | When provided, it must be today or a future date |

## Error response approach

The API returns consistent error responses for validation failures and missing resources.

Error responses are designed to include:

- A machine-readable error code
- A human-readable message
- Context about the failing input

The precise response format is defined in the API contract.

## Running the project locally

### Prerequisites

```text
Java 17
Maven
```

### Run the tests

```bash
mvn test
```

### Run the application

```bash
mvn spring-boot:run
```

The application uses H2 for local development.

## Test coverage

The test suite covers:

- Controller behavior
- Request validation
- Error handling
- Service behavior
- DAO and repository behavior
- Integration paths for the Version 1 API

Run the complete suite with:

```bash
mvn test
```

A successful build is necessary, but it is not the only completion condition. The implementation must also remain aligned with the approved requirements, API contract, implementation plans, and milestone scope.

## Why this repository exists

This project is a companion learning repository for explaining **Prompt-Driven Development**.

The main idea is:

> Do not ask an AI coding assistant to generate production code directly from a vague request. First establish the requirements, resolve important ambiguities, define the API contract, plan the implementation, write the relevant tests, and review the proposed change. Then allow implementation only inside the approved milestone.

This creates a more controlled workflow in which AI accelerates engineering work without taking ownership of architecture, scope, validation, or final approval.

## Suggested learning path

Review the original workflow in this order:

1. `docs/Requirement.docx`
2. `docs/.ai/PlanClarifyingQuestions.md`
3. `docs/.ai/Plan.md`
4. `docs/.ai/001_API_Contract.md`
5. Milestone implementation plans under `docs/.ai/`
6. Original prompts under `docs/prompts/`
7. Tests under `src/test/`
8. Production code under `src/main/`
9. Review notes under `docs/review-log/`
10. Final review in `docs/.ai/016_Final_review.md`

Then review the persistent-context refinement:

11. `.github/copilot-instructions.md`
12. `docs/evolution/001-persistent-context/README.md`
13. `docs/evolution/001-persistent-context/comparisons/011_02_Service_Implementation_With_Persistent_Context.md`

To inspect the repository exactly as it existed through the first three articles, use:

```text
pdd-article-3-workflow-snapshot
```

## License

This project is licensed under the MIT License.