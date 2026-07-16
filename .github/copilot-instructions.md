# TaskFlow API Copilot Instructions

## Project Purpose

TaskFlow API PDD Learning Lab is a Spring Boot sample project that
demonstrates Prompt-Driven Development.

The objective is not only to produce working code. The requirements,
planning, testing, implementation, and review trail must remain visible
and consistent with the approved project documents.

## Sources of Truth

Before planning or implementing a task, review the relevant project
artifacts:

- `docs/Requirement.docx` contains the original Version 1 requirements.
- `docs/.ai/Plan.md` contains the reviewed project direction, milestone
  order, and current state.
- `docs/.ai/001_API_Contract.md` defines the externally visible API
  behavior.
- The active milestone implementation plan defines the approved changes
  for the current task.
- Existing tests define behavior already expected by the project.

Do not use conversation history as the primary source of project state.

When project artifacts conflict or a material requirement is unclear,
identify the conflict and request clarification instead of silently
choosing an interpretation.

## Prompt-Driven Development Workflow

Follow the milestone order defined in `docs/.ai/Plan.md`.

For planning work:

1. Review the current repository state.
2. Read the relevant requirement, plan, contract, tests, and source files.
3. Identify ambiguities or conflicts before proposing changes.
4. Create or update only the requested planning artifact.
5. Do not modify production code or test code unless explicitly requested.

For implementation work:

1. Review the current repository state.
2. Read the approved implementation plan.
3. Inspect the related tests and source files.
4. Implement only the approved milestone scope.
5. Run the relevant tests.
6. Compare the result with the implementation plan and API contract.
7. Update only the milestone-completion section of `Plan.md` when the
   active task explicitly requires it and all success criteria have passed.

Do not combine planning and implementation unless the task explicitly
requests both.

## Scope Discipline

- Do not invent requirements, business rules, validation rules, endpoints,
  or non-functional requirements.
- Do not add functionality outside Version 1.
- Modify only files required by the active task or approved implementation
  plan.
- Do not create placeholder controllers, services, repositories, DAOs,
  entities, DTOs, or test doubles when real project classes already exist.
- Do not introduce speculative abstractions or future milestone work.
- Do not update unrelated documentation or source files.
- Do not weaken or remove tests merely to make the build pass.
- Report unresolved conflicts rather than hiding them.

## Architecture

Preserve the existing responsibility boundaries:

- Controllers handle HTTP concerns, request validation, delegation, and
  response construction.
- Controllers must not contain business logic or direct persistence logic.
- Services contain business behavior and orchestration.
- DAOs and repositories contain persistence concerns.
- DTOs represent API request and response data.
- Domain and persistence models must not be exposed directly as API
  responses unless the approved contract explicitly requires it.
- Exception handling must remain consistent with the API contract.
- Tests should remain separated by controller, service, data-access, and
  integration responsibilities.

Follow existing project patterns before introducing new abstractions.

## Technology

The project uses:

- Java 17
- Spring Boot 3
- Spring Web
- Spring Validation
- Spring Data JPA
- H2
- JUnit 5
- Mockito
- Maven

Do not introduce additional frameworks or dependencies unless an approved
implementation plan explicitly requires them.

## Testing and Validation

Follow the RED, GREEN, and REFACTOR milestone order documented in
`docs/.ai/Plan.md`.

Use the appropriate level of testing:

- Controller tests verify HTTP behavior, validation, delegation, and
  response mapping.
- Service tests verify business behavior.
- Data-access tests verify persistence behavior.
- Integration tests verify end-to-end application behavior.

Run the complete test suite with:

```bash
mvn test