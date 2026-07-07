# Milestone 15 Implementation Plan: Integration Tests and Wiring Review

## Milestone description

Milestone 15 is the integration-test and wiring-review milestone defined in `docs/.ai/Plan.md`. The scope is limited to planning the minimum test and configuration changes required to verify that the already implemented Version 1 Spring Boot application works correctly as a fully wired system.

Current state review based on the repository before Milestone 15 implementation:

- Controller slice coverage already exists in `ProjectControllerTest` and `TaskControllerTest`, but those tests use mocked service beans and therefore do not validate real controller-to-service-to-DAO wiring.
- Service coverage already exists in `ProjectServiceImplTest` and `TaskServiceImplTest`, but those tests use mocked repository contracts and therefore do not validate real Spring Data JPA bean wiring or H2 persistence behavior.
- Repository coverage already exists in `ProjectDaoTest` and `TaskDaoTest`, but those tests do not validate HTTP routing, controller request mapping, exception translation, or cross-layer flows.
- The application already has concrete Spring beans for controllers, services, exception handling, and JPA repositories, so Milestone 15 should verify those beans load together in one application context.
- `application.yml` already points to an in-memory H2 datasource, but Milestone 15 still needs a dedicated test configuration plan so integration tests run predictably and independently of local runtime settings.
- `TaskServiceImpl` verifies project existence before create and project-based list operations, which makes create/list/update wiring scenarios suitable for end-to-end integration testing without introducing new business rules.
- Current persistence design stores `Task.projectId` as a scalar field rather than as a JPA object relationship, so the integration plan must verify service-to-DAO connectivity through repository queries instead of planning entity relationship navigation.

Milestone 15 implementation should therefore add only the integrated verification required by the approved milestone and this prompt:

- start the full Spring application context
- verify the main beans load correctly
- verify controller routes call the real service layer
- verify service methods use the real DAO layer
- verify create, list, and update flows work together against H2
- verify consistent H2-backed test configuration
- catch wiring defects that controller-slice, service-unit, and repository-slice tests may miss

No new API behavior, no production feature work, and no final-review scope belongs in this milestone.

## Files to be updated/changed

Planned files for Milestone 15 implementation:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/integration/TaskflowApplicationIntegrationTest.java`
2. `src/test/resources/application-test.yml`

Possible production files to review and update only if the integration tests expose a real wiring defect:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/GlobalExceptionHandler.java`
6. `src/main/resources/application.yml`

Reviewed but intentionally not planned for direct change in this milestone:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectControllerTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDaoTest.java`
4. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDaoTest.java`
5. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImplTest.java`
6. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImplTest.java`
7. `pom.xml`

No other files should be created or changed for Milestone 15 unless the integration-test implementation proves a real wiring defect that blocks the approved flows.

## Exact code changes with comments

### 1. Add `TaskflowApplicationIntegrationTest.java`

#### Before changes
```text
No full-context integration test class exists. The test suite is currently split across:
- Web MVC slice tests with mocked services
- Service tests with mocked repositories
- JPA repository slice tests
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.integration;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full application integration tests that verify Spring wiring, HTTP routing,
 * service delegation, repository access, and H2-backed persistence together.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskflowApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TaskDao taskDao;

    @Test
    @DisplayName("Spring application context loads all required Version 1 beans")
    void contextLoadsRequiredBeans() {
        assertThat(mockMvc).isNotNull();
        assertThat(projectService).isNotNull();
        assertThat(taskService).isNotNull();
        assertThat(projectDao).isNotNull();
        assertThat(taskDao).isNotNull();
    }

    @Test
    @DisplayName("Create project and task flow works across controller, service, and DAO layers")
    void createProjectAndTaskFlowWorksAcrossLayers() throws Exception {
        // create project through HTTP
        // create task through HTTP using returned project id
        // assert persisted rows exist through repositories
        // assert controller response fields match stored data
    }

    @Test
    @DisplayName("Update assignee and status flows persist changes and remain visible in list queries")
    void updateFlowsWorkAcrossLayers() throws Exception {
        // seed project and task through public HTTP flow
        // update assignee through HTTP PATCH
        // update status through HTTP PATCH
        // query by project, status, and assignee through HTTP GET
        // assert repository state and response payloads stay aligned
    }

    @Test
    @DisplayName("Project task listing returns not found for missing project through real exception mapping")
    void getTasksByProjectIdReturnsNotFoundForMissingProject() throws Exception {
        // call GET /api/projects/{projectId}/tasks with unknown id
        // assert 404 response from GlobalExceptionHandler in full context
    }
}
```

Planned change notes:

- Use `@SpringBootTest` so the real application context starts and all beans are created through normal Spring wiring.
- Use `@AutoConfigureMockMvc` so route verification still happens through HTTP requests without starting an external server.
- Use `@ActiveProfiles("test")` so integration tests run against dedicated H2 test configuration rather than relying on the default local runtime profile.
- Keep one integration test class for Milestone 15 because the prompt requires wiring review, not another large test pyramid branch.
- Inject service and DAO beans only to assert bean loading and, where necessary, confirm persistence state after HTTP operations; the primary behavior checks should still go through controller endpoints so route wiring is genuinely exercised.
- Keep assertions focused on approved Version 1 flows: create project, create task, update assignee, update status, list by project, list by status, and list by assignee.
- Keep helper logic private inside the test class if needed so the test remains readable without introducing extra helper files that are not required by this milestone.

### 2. Add `application-test.yml`

#### Before changes
```yaml
# src/test/resources/application-test.yml does not exist.
# Integration tests would currently inherit the default application.yml values.
```

#### After changes
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:taskflow-integration;MODE=LEGACY;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    open-in-view: false

  h2:
    console:
      enabled: false
```

Planned change notes:

- Add a dedicated test profile file so Milestone 15 explicitly verifies the H2 integration-test setup rather than depending on shared runtime defaults.
- Use `create-drop` for test schema lifecycle so each test run starts from a predictable database state without changing production configuration.
- Keep H2-only settings in test resources so the production `application.yml` remains focused on application runtime defaults.
- Do not add PostgreSQL, Testcontainers, Flyway, Liquibase, or multi-profile environment work because none of that is required by Milestone 15.

### 3. Define the integration scenarios that validate route-to-service-to-DAO wiring

#### Before changes
```text
Current automated coverage verifies each layer mostly in isolation:
- controller route + request validation with mocked services
- service behavior with mocked repositories
- repository behavior with JPA slice tests

No test currently proves that a real HTTP request reaches the real service, persists through the real DAO, and can be observed again through later HTTP reads.
```

#### After changes
```text
Planned integration scenarios:

1. Application context wiring review
   - Start the Spring Boot test context.
   - Assert the controller path stack is usable through MockMvc.
   - Assert required service and DAO beans are present.

2. Create project -> create task flow
   - POST /api/projects
   - POST /api/projects/{projectId}/tasks
   - Assert project and task are persisted through ProjectDao and TaskDao.
   - Assert default TODO status is preserved when omitted.

3. Update task assignee flow
   - PATCH /api/tasks/{taskId}/assignee
   - Assert updated value is returned in HTTP response.
   - Assert updated value is persisted in TaskDao.

4. Update task status flow
   - PATCH /api/tasks/{taskId}/status
   - Assert updated status is returned in HTTP response.
   - Assert updated status is persisted in TaskDao.

5. List flows after writes
   - GET /api/projects/{projectId}/tasks
   - GET /api/tasks?status=...
   - GET /api/tasks?assigneeEmail=...
   - Assert the created and updated task is returned by each supported filter.

6. Full-context not-found mapping
   - GET /api/projects/{missingProjectId}/tasks
   - Optionally POST or PATCH with missing IDs if needed for one representative path
   - Assert 404 response body matches approved error contract.
```

Planned change notes:

- These scenarios satisfy the prompt without expanding into new failure matrices already covered by controller and service unit tests.
- The list scenarios are the best proof that service-to-DAO integration works because they depend on persisted writes being visible through repository query methods.
- The not-found scenario verifies `GlobalExceptionHandler` inside the full context, which controller-slice tests cover partially but not with the real DAO-backed service implementation.

### 4. Preserve SOLID boundaries while adding the integration tests

#### Before changes
```text
The production code already follows the milestone structure:
- controllers handle HTTP mapping
- services handle business flow
- repositories handle persistence

Milestone 15 has not yet defined how integration tests will verify those boundaries without introducing test-driven production refactors.
```

#### After changes
```text
SOLID-aligned implementation constraints for Milestone 15:

- Single Responsibility:
  - Integration test code should verify wiring and integrated behavior only.
  - It should not become a second unit-test suite for DTO validation internals or repository query permutations.

- Open/Closed:
  - Prefer adding integration coverage without changing production class responsibilities.
  - Change production code only when the new tests expose a real wiring defect.

- Liskov Substitution:
  - Continue testing through the existing `ProjectService` and `TaskService` contracts rather than binding tests to implementation-only behavior.

- Interface Segregation:
  - Do not widen service or DAO interfaces just to make tests easier.

- Dependency Inversion:
  - Keep controllers depending on service interfaces and services depending on DAO interfaces.
  - Integration tests should confirm the Spring container provides the concrete implementations behind those abstractions.
```

Planned change notes:

- This milestone is a wiring review, so SOLID compliance matters mainly as a constraint on how the tests are introduced.
- No test helper should force production-only seams or visibility changes that are not otherwise needed by the design.

### 5. Verification after implementation

#### Before changes
```text
There is no Milestone 15 verification yet for full application wiring with H2-backed integrated flows.
```

#### After changes
```text
Run the Maven test suite after Milestone 15 implementation to confirm:
- the Spring Boot application context starts under the test profile
- required controllers, services, and repositories are created as beans
- the integration test class passes against H2
- create, list, and update flows work together across controller, service, and DAO layers
- existing controller, service, and repository tests remain green
```

Planned change notes:

- Verification must include the full suite because Milestone 15 is specifically intended to catch wiring regressions across existing green layers.
- Implementation should stop after the integrated flows are green and any real wiring defects exposed by them are fixed.

## Out of scope

- Any new endpoint, DTO, domain model, repository method, or business rule not already approved in Version 1
- Expanding integration coverage to every validation permutation already covered by controller-slice tests
- Replacing the existing unit and slice tests with integration tests
- Introducing Testcontainers, PostgreSQL integration, Flyway, Liquibase, or external infrastructure
- Refactoring `Task.projectId` into a JPA entity association as part of this milestone
- Performance testing, concurrency testing, load testing, or production deployment checks
- Final milestone review or any Plan.md completion update that marks Milestone 15 done before implementation is actually completed

## Success criteria

Milestone 15 implementation is complete when:

1. A full Spring Boot integration test class starts the application context successfully under a dedicated test profile.
2. The integration test suite verifies that the required Version 1 controller, service, and DAO beans load correctly.
3. The suite proves that `POST /api/projects` and `POST /api/projects/{projectId}/tasks` work together through the real controller, service, and DAO layers.
4. The suite proves that `PATCH /api/tasks/{taskId}/assignee` and `PATCH /api/tasks/{taskId}/status` persist updates through the real application stack.
5. The suite proves that `GET /api/projects/{projectId}/tasks`, `GET /api/tasks?status=...`, and `GET /api/tasks?assigneeEmail=...` return data created and updated during the same integrated test flow.
6. The suite verifies at least one representative not-found path through the real `GlobalExceptionHandler` with the approved error response shape.
7. H2-backed integration-test configuration is isolated in test resources and works consistently during the full test run.
8. Any production-code changes made during implementation are limited to fixing real wiring defects exposed by the new integration tests.
9. Existing controller, service, and repository tests remain green after Milestone 15 implementation.
