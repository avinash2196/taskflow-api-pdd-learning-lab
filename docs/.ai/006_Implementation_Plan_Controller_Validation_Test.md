# Milestone 6 Implementation Plan: Controller Validation and Error Handling Tests

## Milestone description

Milestone 6 is the RED phase for controller validation and consistent error handling defined in [Plan.md](/C:/Users/avina/Downloads/avinash-repo/taskflow-api/docs/.ai/Plan.md) and [001_API_Contract.md](/C:/Users/avina/Downloads/avinash-repo/taskflow-api/docs/.ai/001_API_Contract.md).

The plan is limited to controller-slice test planning for the real existing controllers:
- `ProjectController`
- `TaskController`

Current state review:
- `ProjectController` currently exposes `POST /api/projects`.
- `TaskController` currently exposes:
  - `POST /api/projects/{projectId}/tasks`
  - `PATCH /api/tasks/{taskId}/assignee`
  - `PATCH /api/tasks/{taskId}/status`
  - `GET /api/projects/{projectId}/tasks`
  - `GET /api/tasks?status=...`
  - `GET /api/tasks?assigneeEmail=...`
- `ProjectControllerTest` already contains:
  - positive create-project contract coverage
  - validation RED coverage for missing or null `name`
  - positive coverage for optional `description`
- `TaskControllerTest` already contains:
  - positive contract coverage for all planned task endpoints
  - negative coverage for unsupported unfiltered `GET /api/tasks`
  - validation RED coverage for create, assignee update, status update, and query-filter failures
  - not-found RED coverage for unknown project/task cases
  - positive coverage for create-task optional-field handling
- Request DTO classes still have fields and accessors only. No Bean Validation annotations are present yet.
- No production exception classes, shared error-response DTO, or `@ControllerAdvice` types exist yet under `src/main/java`.
- `TaskControllerTest` currently uses inner test-only `ProjectNotFoundException` and `TaskNotFoundException` classes to keep Milestone 6 scoped to tests.

Milestone 6 planning therefore stays focused on the test classes only. Production exception classes are not required in this milestone plan because the current RED tests already isolate that concern inside the test source set.

## Files to be updated/changed

Only these test files are in scope for the Milestone 6 implementation described by this plan:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectControllerTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`

No file under `src/main/java` is planned for change in this milestone.

## Exact code changes with comments

### 1. Update `ProjectControllerTest.java`

#### Before changes
```java
/**
 * Controller-slice RED tests for project endpoints.
 * These tests define the expected HTTP contract before controller behavior exists.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("POST /api/projects returns 201 Created with the created project payload")
    void createProjectReturnsCreatedProjectResponse() throws Exception {
        // Existing Milestone 4 success-path contract test.
    }
}
```

#### After changes
```java
/**
 * Controller-slice RED tests for project endpoints.
 * Milestone 6 extends the class with request-validation coverage while keeping
 * the service dependency mocked so controller behavior stays isolated.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is missing")
    void createProjectReturnsBadRequestWhenNameIsMissing() throws Exception {
        // Verifies the required project name validation contract.
        // Confirms the shared error payload shape for validation failures.
    }

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is null")
    void createProjectReturnsBadRequestWhenNameIsNull() throws Exception {
        // Verifies null values are rejected for required fields.
    }

    @Test
    @DisplayName("POST /api/projects allows optional description when name is valid")
    void createProjectAllowsOptionalDescription() throws Exception {
        // Verifies the positive boundary that description remains optional.
    }
}
```

Planned assertions:
- `400 Bad Request` for missing `name`
- `400 Bad Request` for explicit `name: null`
- error payload contains:
  - `errorCode`
  - `message`
  - `context.field = "name"`
- `verifyNoInteractions(projectService)` for validation failures
- `201 Created` still succeeds when `description` is omitted

### 2. Update `TaskControllerTest.java`

#### Before changes
```java
/**
 * Controller-slice RED tests for task endpoints.
 * These tests define the required task HTTP contract before controller behavior exists.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 201 Created with the created task payload")
    void createTaskReturnsCreatedTaskResponse() throws Exception {
        // Existing Milestone 4 success-path contract test.
    }
}
```

#### After changes
```java
/**
 * Controller-slice RED tests for task endpoints.
 * Milestone 6 adds validation and not-found coverage required by the API contract.
 * Mockito continues to mock the service layer only; no handwritten mocks are added.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when title is missing")
    void createTaskReturnsBadRequestWhenTitleIsMissing() throws Exception {
        // Verifies required title validation for task creation.
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when assignee email format is invalid")
    void createTaskReturnsBadRequestWhenAssigneeEmailIsInvalid() throws Exception {
        // Verifies email-format validation during task creation.
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when due date is in the past")
    void createTaskReturnsBadRequestWhenDueDateIsInThePast() throws Exception {
        // Verifies the due-date business validation at the controller boundary.
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when status is unsupported")
    void createTaskReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        // Verifies unsupported enum-style input produces the shared validation error shape.
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 404 Not Found when project does not exist")
    void createTaskReturnsNotFoundWhenProjectDoesNotExist() throws Exception {
        // Uses Mockito to simulate service-level not-found behavior for an unknown project.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 400 Bad Request when assignee email is missing")
    void updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsMissing() throws Exception {
        // Verifies the required assigneeEmail field for assignee updates.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 400 Bad Request when assignee email format is invalid")
    void updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsInvalid() throws Exception {
        // Verifies email-format validation for assignee updates.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 404 Not Found when task does not exist")
    void updateTaskAssigneeReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        // Uses Mockito to simulate task-not-found behavior.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 400 Bad Request when status is missing")
    void updateTaskStatusReturnsBadRequestWhenStatusIsMissing() throws Exception {
        // Verifies the required status field for status updates.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 400 Bad Request when status is unsupported")
    void updateTaskStatusReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        // Verifies unsupported status values are rejected consistently.
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 404 Not Found when task does not exist")
    void updateTaskStatusReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        // Uses Mockito to simulate task-not-found behavior for status updates.
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks returns 404 Not Found when project does not exist")
    void getTasksByProjectIdReturnsNotFoundWhenProjectDoesNotExist() throws Exception {
        // Uses Mockito to simulate unknown project lookup during project-task listing.
    }

    @Test
    @DisplayName("GET /api/tasks?status=INVALID returns 400 Bad Request with consistent error response")
    void getTasksByStatusReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        // Verifies status-filter validation at the request mapping boundary.
    }

    @Test
    @DisplayName("GET /api/tasks?assigneeEmail=invalid-email returns 400 Bad Request with consistent error response")
    void getTasksByAssigneeEmailReturnsBadRequestWhenEmailIsInvalid() throws Exception {
        // Verifies assignee email filter validation.
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks allows omitted optional fields and defaults status behavior to later layers")
    void createTaskAllowsOptionalFieldsWhenTitleIsValid() throws Exception {
        // Verifies the positive boundary for optional description, assigneeEmail,
        // dueDate, and status fields.
    }

    private static final class ProjectNotFoundException extends RuntimeException {
        // Test-local exception placeholder used only to express RED expectations
        // without introducing production classes in this milestone.
    }

    private static final class TaskNotFoundException extends RuntimeException {
        // Test-local exception placeholder used only to express RED expectations
        // without introducing production classes in this milestone.
    }
}
```

Planned assertions:
- create-task validation failures:
  - missing `title`
  - invalid `assigneeEmail`
  - past `dueDate`
  - unsupported `status`
- assignee update validation failures:
  - missing `assigneeEmail`
  - invalid `assigneeEmail`
- status update validation failures:
  - missing `status`
  - unsupported `status`
- query validation failures:
  - unsupported `status` filter
  - invalid `assigneeEmail` filter
- not-found behavior:
  - unknown `projectId` during task creation
  - unknown `taskId` during assignee update
  - unknown `taskId` during status update
  - unknown `projectId` during project-task retrieval
- positive boundaries:
  - omitted optional task create fields still accepted
  - existing success-path tests remain intact
- service isolation:
  - `verifyNoInteractions(taskService)` for request-validation failures
  - Mockito `given(...).willThrow(...)` for not-found scenarios

### Planned Milestone 6 coverage matrix

| API | Planned test method | Case type |
| --- | --- | --- |
| `POST /api/projects` | `createProjectReturnsBadRequestWhenNameIsMissing` | Negative |
| `POST /api/projects` | `createProjectReturnsBadRequestWhenNameIsNull` | Negative |
| `POST /api/projects` | `createProjectAllowsOptionalDescription` | Positive |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsBadRequestWhenTitleIsMissing` | Negative |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsBadRequestWhenAssigneeEmailIsInvalid` | Negative |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsBadRequestWhenDueDateIsInThePast` | Negative |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsBadRequestWhenStatusIsUnsupported` | Negative |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsNotFoundWhenProjectDoesNotExist` | Negative |
| `POST /api/projects/{projectId}/tasks` | `createTaskAllowsOptionalFieldsWhenTitleIsValid` | Positive |
| `PATCH /api/tasks/{taskId}/assignee` | `updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsMissing` | Negative |
| `PATCH /api/tasks/{taskId}/assignee` | `updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsInvalid` | Negative |
| `PATCH /api/tasks/{taskId}/assignee` | `updateTaskAssigneeReturnsNotFoundWhenTaskDoesNotExist` | Negative |
| `PATCH /api/tasks/{taskId}/status` | `updateTaskStatusReturnsBadRequestWhenStatusIsMissing` | Negative |
| `PATCH /api/tasks/{taskId}/status` | `updateTaskStatusReturnsBadRequestWhenStatusIsUnsupported` | Negative |
| `PATCH /api/tasks/{taskId}/status` | `updateTaskStatusReturnsNotFoundWhenTaskDoesNotExist` | Negative |
| `GET /api/projects/{projectId}/tasks` | `getTasksByProjectIdReturnsNotFoundWhenProjectDoesNotExist` | Negative |
| `GET /api/tasks?status=...` | `getTasksByStatusReturnsBadRequestWhenStatusIsUnsupported` | Negative |
| `GET /api/tasks?assigneeEmail=...` | `getTasksByAssigneeEmailReturnsBadRequestWhenEmailIsInvalid` | Negative |

## Out of scope

- Any change to `ProjectController`, `TaskController`, DTO classes, services, DAO classes, or model classes
- Any Bean Validation annotation work in request DTOs
- Any production exception class creation under `src/main/java`
- Any shared error DTO or `@ControllerAdvice` implementation
- Any repository, service, or integration test planning
- Any `Plan.md` update
- Any new endpoint, filter, or behavior beyond Milestone 6 requirements
- Any mock implementation classes outside Mockito configuration

## Success criteria

Milestone 6 implementation is successful when:

1. `ProjectControllerTest` and `TaskControllerTest` contain RED tests for the required validation failures and not-found cases from the approved contract.
2. Every negative controller test asserts the consistent error response structure with `errorCode`, `message`, and `context.field`.
3. Validation-failure tests confirm the mocked service layer is not invoked.
4. Not-found tests use Mockito-driven service exceptions rather than concrete mock implementations.
5. Positive boundary tests remain present for optional request fields explicitly allowed by the contract.
6. The tests target only the existing `ProjectController` and `TaskController` classes.
7. No production source file is changed as part of the Milestone 6 implementation.
8. The resulting tests are expected to fail until Milestone 7 introduces validation and exception handling behavior.
