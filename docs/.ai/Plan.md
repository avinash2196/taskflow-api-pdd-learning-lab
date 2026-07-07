# TaskFlow API Plan

## 1. Project Overview

TaskFlow API is a lightweight backend service for managing projects and tasks through REST-style APIs. Version 1 is intended to support project creation, task creation under a project, task assignment, task status tracking, and task listing by project, status, and assignee. This document translates the approved product requirements into an implementation-oriented delivery plan without defining code structure or implementation details that were not confirmed in the requirements.

## 2. Business Goal

The business goal for Version 1 is to provide a minimal backend capability that allows small teams and API consumers to create projects, track tasks under those projects, update assignees and statuses, and retrieve tasks through simple query flows. The scope is intentionally limited so product behavior can be validated before broader platform concerns are introduced.

## 3. Version 1 Scope

- Create a project with name and optional description.
- Create a task under an existing project.
- Store task title, description, status, assignee email, due date, created date, and updated date.
- Support task statuses: `TODO`, `IN_PROGRESS`, `BLOCKED`, and `DONE`.
- Default task status to `TODO` when status is not provided during task creation.
- Assign or update the assignee email for an existing task.
- Change the status of an existing task.
- List tasks by project.
- List tasks by status.
- List tasks by assignee.
- Return consistent success and error responses for supported operations.
- Cover main success and failure paths with automated tests.

## 4. Out of Scope

- User authentication and authorization.
- User profile management.
- User interface or frontend application.
- Email, Slack, or push notifications.
- Task comments, attachments, or file uploads.
- Audit logging and event streaming.
- External integrations.
- Production deployment, monitoring, and infrastructure automation.
- Pagination, sorting, and advanced search.
- PostgreSQL deployment profile and production configuration.

## 5. Functional Requirements

- Allow an API consumer to create a project using a project name and optional description.
- Return the project identifier, name, description, created date, and updated date when project creation succeeds.
- Allow an API consumer to create a task under an existing project.
- Require a task title during task creation.
- Allow task creation to include optional description, optional assignee email, optional due date, and optional initial status.
- Reject task creation when the referenced project does not exist.
- Allow an API consumer to assign or update the assignee email for an existing task.
- Reject invalid assignee email format.
- Allow an API consumer to change the status of an existing task to one of the supported statuses.
- Reject unsupported status values.
- Allow an API consumer to retrieve tasks for a specific project.
- Return not-found when the requested project does not exist for project-based task retrieval.
- Allow an API consumer to retrieve tasks filtered by status.
- Allow an API consumer to retrieve tasks filtered by assignee email.
- Return consistent error responses for validation errors, missing resources, and unsupported operations.
- Include a machine-readable error code, a human-readable message, and enough context for the caller to identify the failing input in error responses.

## 6. Domain & Business Rules

### Core domain concepts

- A project has a system-generated project identifier, name, optional description, created date, and updated date.
- A task has a system-generated task identifier and belongs to a project.
- A task stores title, description, status, assignee email, due date, created date, and updated date.

### Business rules

- Project identity is based on the system-generated project ID, not the project name.
- Project names do not need to be globally unique in Version 1.
- Task identity is based on the system-generated task ID, not the task title.
- Task titles do not need to be unique within a project in Version 1.
- A task may be created without an assignee and assigned later.
- If task status is not provided during creation, the default status is `TODO`.
- A task status may be changed to `TODO`, `IN_PROGRESS`, `BLOCKED`, or `DONE`.
- Any transition between the supported statuses is allowed in Version 1.
- A task remains editable after reaching `DONE` in Version 1.

### Validation rules

- Project creation requires a project name.
- Project description is optional.
- Task creation requires an existing project reference.
- Task creation requires a task title.
- Task description is optional.
- Assignee email is optional during task creation.
- If assignee email is provided, invalid email format must be rejected.
- Due date is optional.
- If due date is provided, past dates must be rejected.
- Due date must be today or a future date when provided.
- If status is provided, it must be one of `TODO`, `IN_PROGRESS`, `BLOCKED`, or `DONE`.
- Unsupported status values must be rejected for creation, update, and filtering operations.
- Invalid email format must be rejected for assignee-based filtering.

### Error handling expectations

- Validation failures must return consistent validation errors.
- Missing project or task references must return not-found errors.
- Unsupported operations must return consistent error responses.
- Error responses must include a machine-readable error code, a human-readable message, and enough context for the caller to identify the failing input.

### Data considerations

- Created date and updated date are part of the required project response for successful project creation.
- Created date and updated date are part of the task data stored by the system.
- Task retrieval supports filtering by project, status, and assignee only within Version 1 scope.

## 7. API Contract Summary

Version 1 must expose REST-style capabilities for:

- Creating a project.
- Creating a task under an existing project.
- Assigning or updating a task assignee.
- Changing task status.
- Listing tasks by project.
- Listing tasks by status.
- Listing tasks by assignee.
- Returning consistent success and error responses.

The exact endpoint names, request and response model definitions, controller design, and package structure are intentionally deferred to later planning milestones.

## 8. Constraints & Assumptions

- This plan is based on `docs/Requirement.docx` and the confirmed clarifications for Version 1 behavior.
- Version 1 must remain intentionally small so product behavior is easy to validate.
- Authentication, notifications, audit logging, and deployment concerns are outside Version 1 scope.
- Exact endpoint names, DTO names, controller design, and package structure are not yet defined and must be planned later.
- Implementation must not begin until this Plan.md has been reviewed for scope alignment and hallucinations.

## 9. TDD Strategy

The delivery approach for Version 1 follows a strict RED -> GREEN -> REFACTOR cycle at the controller, service, and data access layers.

- RED: Define the expected externally visible behavior through failing tests for the current milestone.
- GREEN: Implement only the minimum behavior required to satisfy the failing tests for the current milestone.
- REFACTOR: Improve internal structure after tests pass without changing approved behavior.
- Validation and error handling must be driven by failing tests before implementation.
- Service behavior must be verified against mocked repository contracts before repository implementation begins.
- Integration tests are reserved for end-to-end wiring review after the unit-level behavior is in place.

## 10. Milestone Breakdown

### 1. API Contract Plan

**Goal**  
Establish the approved Version 1 API behavior plan from the requirement baseline and confirmed clarifications.

**Expected outcome**  
A reviewed contract planning baseline exists for Version 1 behavior, supported operations, error expectations, and deferred items.

**What is not included**  
Endpoint path definitions, DTO definitions, implementation design, repository design, or code changes.

**Completion criteria**  
Version 1 behavior, scope boundaries, validation rules, and known constraints are documented and aligned with the approved requirements.

> Plan.md must be updated after milestone completion.

### 2. Project Skeleton

**Goal**  
Prepare the base project structure required to support the planned delivery sequence.

**Expected outcome**  
The project is ready for planned implementation work to begin in later milestones.

**What is not included**  
Business behavior implementation, repository implementation, or completed API behavior.

**Completion criteria**  
The base project skeleton exists and is sufficient to support controller, service, repository contract, and test work in subsequent milestones.

> Plan.md must be updated after milestone completion.

### 3. API Behavior, Domain Model, and Service Contract Planning

**Goal**  
Plan the API behavior, domain model, and service contract required to implement approved Version 1 behavior.

**Expected outcome**  
The planning output covers:
- DTO/API models
- Domain model
- Service interfaces
- High-level mapping from API operations to service interface methods

**What is not included**  
Service implementation, repository implementation, data access implementation, or completed controller behavior.

**Completion criteria**  
The planned API behavior, domain model expectations, and service contract are documented and aligned to the approved requirements and Version 1 rules.

> Plan.md must be updated after milestone completion.

### 4. Controller Tests (RED)

**Goal**  
Define failing controller-level tests for approved Version 1 API behavior.

**Expected outcome**  
Controller tests fail because controller behavior is not yet implemented.

**What is not included**  
Passing controller behavior, service implementation, repository design, or integration wiring.

**Completion criteria**  
Failing controller tests cover the main success and failure paths required by Version 1 scope.

> Plan.md must be updated after milestone completion.

### 5. Controller Implementation Using Service Contract (GREEN)

**Goal**  
Implement controller behavior against the planned service contract until controller tests pass.

**Expected outcome**  
Controller behavior satisfies the approved contract and existing controller tests pass.

**What is not included**  
Validation and exception handling completion beyond what is explicitly required to satisfy this milestone, repository implementation, or integration completion.

**Completion criteria**  
Controller tests from Milestone 4 pass using the approved service contract.

> Plan.md must be updated after milestone completion.

### 6. Validation and Error Handling Tests (RED)

**Goal**  
Define failing tests for Version 1 validation behavior and consistent error handling.

**Expected outcome**  
Validation and error handling tests fail because the supporting behavior is not yet fully implemented.

**What is not included**  
Passing validation handling, repository implementation, or data access behavior.

**Completion criteria**  
Failing tests exist for required validation failures, not-found behavior, and consistent error response expectations.

> Plan.md must be updated after milestone completion.

### 7. Validation and Exception Handling Implementation (GREEN)

**Goal**  
Implement the validation and exception handling behavior required to satisfy the failing tests.

**Expected outcome**  
Validation failures, not-found behavior, and consistent error responses behave as approved in the requirements.

**What is not included**  
Controller refactoring beyond what is necessary to satisfy behavior, repository implementation, or service data access implementation.

**Completion criteria**  
Validation and error handling tests from Milestone 6 pass.

> Plan.md must be updated after milestone completion.

### 8. Controller Refactor (REFACTOR)

**Goal**  
Improve controller-level internal structure without changing approved behavior.

**Expected outcome**  
Controller code is cleaner while preserving passing behavior and existing expectations.

**What is not included**  
New behavior, changed requirements, repository implementation, or service data access implementation.

**Completion criteria**  
Controller tests and validation/error handling tests continue to pass after refactoring.

> Plan.md must be updated after milestone completion.

### 9. Repository Contract

**Goal**  
Define repository interfaces needed by the approved service behavior.

**Expected outcome**  
Repository contracts exist at the interface level only.

**What is not included**  
Repository implementation or data access behavior.

**Completion criteria**  
Repository interfaces are defined and aligned to the approved service behavior with no repository implementation added.

> Plan.md must be updated after milestone completion.

### 10. Service Tests Using Mocked Repository Contract (RED)

**Goal**  
Define failing service-level tests using mocked repository contracts.

**Expected outcome**  
Service tests fail because service behavior is not yet implemented.

**What is not included**  
Passing service implementation, repository implementation, or integration wiring.

**Completion criteria**  
Failing service tests cover the main service success and failure paths required by Version 1.

> Plan.md must be updated after milestone completion.

### 11. Service Implementation (GREEN)

**Goal**  
Implement service behavior required to satisfy the approved service tests.

**Expected outcome**  
Service behavior supports the planned controller behavior and passes the service tests.

**What is not included**  
Repository implementation details, data access behavior, or integration completion.

**Completion criteria**  
Service tests from Milestone 10 pass using mocked repository contracts.

> Plan.md must be updated after milestone completion.

### 12. Service Refactor (REFACTOR)

**Goal**  
Improve service-level internal structure without changing approved behavior.

**Expected outcome**  
Service code is cleaner while preserving existing passing behavior.

**What is not included**  
New requirements, repository implementation changes beyond contract alignment, or integration completion.

**Completion criteria**  
Service tests continue to pass after refactoring.

> Plan.md must be updated after milestone completion.

### 13. Data Access Tests (RED)

**Goal**  
Define failing data access tests for the repository behavior required by Version 1.

**Expected outcome**  
Data access tests fail because repository behavior is not yet implemented.

**What is not included**  
Passing data access implementation or full integration sign-off.

**Completion criteria**  
Failing tests exist for repository behavior needed to support approved Version 1 operations.

> Plan.md must be updated after milestone completion.

### 14. Data Access Implementation (GREEN)

**Goal**  
Implement data access behavior required to satisfy the repository tests.

**Expected outcome**  
Repository behavior supports the approved service needs and data access tests pass.

**What is not included**  
New behavior outside the approved scope or final integration sign-off.

**Completion criteria**  
Data access tests from Milestone 13 pass.

> Plan.md must be updated after milestone completion.

### 15. Integration Tests and Wiring Review

**Goal**  
Verify end-to-end wiring and integrated behavior across the implemented Version 1 scope.

**Expected outcome**  
Integrated behavior is validated across the main success and failure paths.

**What is not included**  
New feature scope, future considerations, or production deployment work.

**Completion criteria**  
Integration tests and wiring review confirm that approved Version 1 behavior is functioning consistently across layers.

> Plan.md must be updated after milestone completion.

### 16. Final Review

**Goal**  
Complete the final scope, behavior, and milestone review for Version 1 delivery.

**Expected outcome**  
Version 1 delivery status, scope alignment, and outstanding notes are finalized.

**What is not included**  
New feature additions, future-phase design, or production rollout work.

**Completion criteria**  
All planned milestones are reviewed, Plan.md reflects the final project state, and Version 1 delivery is confirmed against the approved requirements.

> Plan.md must be updated after milestone completion.

## 11. Plan Update Rules

Plan.md is the project's source of truth. It must be updated after every completed milestone. Milestone definitions must remain unchanged. As the project progresses, only the Project Execution Status table should be updated to reflect milestone progress, completion dates, and notes.

## 12. Project Execution Status

| Milestone | Status | Completed On | Notes |
| --------- | ------ | ------------ | ----- |
| 1. API Contract Plan | Completed | 2026-07-05 | API_Contract_Plan.md created and aligned to approved Version 1 requirements. |
| 2. Project Skeleton | Completed | 2026-07-05 | Base Maven, Spring Boot skeleton, H2 runtime config, placeholder config/controller/service/DAO package structure, and required source/test folders implemented per Implementation_Plan_Project_Skeleton.md. |
| 3. API Behavior, Domain Model, and Service Contract Planning | Completed | 2026-07-05 | DTO classes, domain models, and project/task service contracts implemented per 004_Implementation_Plan_API_Model_Service_Plan.md; source compile verified. |
| 4. Controller Tests (RED) | Completed | 2026-07-05 | ProjectControllerTest and TaskControllerTest added as controller-slice RED tests per Implementation_Plan_Controller_Test.md; project build verified after implementation. |
| 5. Controller Implementation Using Service Contract (GREEN) | Completed | 2026-07-05 | ProjectController and TaskController endpoints implemented per 005_Implementation_Plan_Controller.md; Maven test suite passed in green state. |
| 6. Validation and Error Handling Tests (RED) | Completed | 2026-07-05 | ProjectControllerTest and TaskControllerTest expanded with Milestone 6 RED validation and not-found controller-slice coverage per 006_Implementation_Plan_Controller_Validation_Test.md; test compilation verified. |
| 7. Validation and Exception Handling Implementation (GREEN) | Completed | 2026-07-05 | Controller validation, shared error response and exception handling implemented per 007_Implementation_Plan_Controller_Validation.md; Maven test suite passed in green state. |
| 8. Controller Refactor (REFACTOR) | Completed | 2026-07-05 | Controller test fixtures, JSON payload builders, and shared error assertions refactored into test-only helpers per 008_Implementation_Plan_Controller_Refactor.md; controller tests and full Maven test suite passed with production controllers unchanged. |
| 9. Repository Contract | Completed | 2026-07-05 | ProjectDao and TaskDao converted from placeholder classes into interface-only repository contracts aligned to approved Version 1 service behavior per 009_Implementation_Plan_Repository_Contract_Plan.md; Maven test suite passed in green state. |
| 10. Service Tests Using Mocked Repository Contract (RED) | Completed | 2026-07-05 | ProjectServiceImplTest, TaskServiceImplTest, and ServiceTestDataFactory added per 010_Implementation_Plan_Service_Test.md; compile verified and targeted service tests remain RED against placeholder service implementations. |
| 11. Service Implementation (GREEN) | Completed | 2026-07-05 | ProjectServiceImpl and TaskServiceImpl implemented per 011_Implementation_Plan_Service.md, TaskServiceImplTest constructor wiring aligned to repository contracts, and full Maven test suite passed in green state. |
| 12. Service Refactor (REFACTOR) | Completed | 2026-07-05 | ProjectServiceImpl and TaskServiceImpl refactored per 012_Implementation_Plan_Service_Refactor.md, service test helpers centralized in ServiceTestDataFactory and ServiceTestAssertions, and full Maven test suite passed after both the production-code refactor and the test refactor verification runs. |
| 13. Data Access Tests (RED) | Completed | 2026-07-06 | ProjectDaoTest, TaskDaoTest, and RepositoryTestDataFactory added per 013_Implementation_Plan_Repository_Test.md; `test-compile` verified and targeted repository tests remain RED because no repository bean implementation exists yet. |
| 14. Data Access Implementation (GREEN) | Completed | 2026-07-06 | ProjectDao and TaskDao implemented as Spring Data JPA repositories; Project and Task persistence mappings with generated IDs and timestamps added per 014_Implementation_Plan_Repository.md; full Maven test suite passed in green state. |
| 15. Integration Tests and Wiring Review | Completed | 2026-07-06 | H2-backed full-context integration coverage added per 015_Implementation_Plan_Integration.md with TaskflowApplicationIntegrationTest and dedicated application-test.yml verifying bean loading, create/list/update flows, and full-context not-found error mapping; full Maven test suite passed in green state. |
| 16. Final Review | Completed | 2026-07-06 | Final review documented in 016_Final_review.md. Compile verification succeeded with IntelliJ-bundled Maven, full test execution succeeded using `& 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd' -q test`, Swagger/OpenAPI and H2 console exposure were removed for strict Version 1 scope alignment, and Version 1 was confirmed complete. The remaining `da` package naming note is cosmetic and deferred as a separate cleanup concern. |
