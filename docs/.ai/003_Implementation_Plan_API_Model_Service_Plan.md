# Milestone 3 Implementation Plan: API Behavior, Domain Model, and Service Contract Planning

## Milestone description

Milestone 3 plans the DTO/API models, domain model, and service contracts required to support the approved Version 1 API behavior described in `docs/.ai/Plan.md` and `docs/.ai/API_Contract_Plan.md`.

This milestone is planning-only. No controller behavior, service implementation, repository contract, validation handler, or configuration work is included. The output below describes the exact source changes that should be made during implementation for Milestone 3 while keeping the current repository structure in view.

Current state review:
- `ProjectController` and `TaskController` already exist as placeholder controllers.
- `ProjectService` and `TaskService` already exist as empty service contracts.
- `ProjectServiceImpl`, `TaskServiceImpl`, `ProjectDao`, and `TaskDao` exist as placeholders but must not be changed in this milestone plan because implementation is out of scope here.
- No DTO package, domain model package, or shared status enum exists yet.

## Files to be updated/changed

Planned source files for Milestone 3 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/TaskStatus.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/CreateProjectRequest.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/ProjectResponse.java`
8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/CreateTaskRequest.java`
9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/TaskResponse.java`
10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskAssigneeRequest.java`
11. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskStatusRequest.java`

No other files should be created or changed for Milestone 3 implementation.

## Exact code changes with comments

### 1. Update `ProjectService.java`

#### Before
```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

/**
 * Placeholder service contract for future project-related business behavior.
 * Service methods are intentionally deferred to later planning and RED/GREEN milestones.
 */
public interface ProjectService {
    // No methods are defined in this milestone.
}
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;

/**
 * Defines project-related business operations required by the Version 1 API contract.
 * This contract isolates controller behavior from later service implementation details.
 */
public interface ProjectService {

    /**
     * Creates a new project using the approved project creation request contract.
     *
     * @param request carries the required project name and optional description
     * @return the created project response payload defined by the API contract
     */
    ProjectResponse createProject(CreateProjectRequest request);
}
```

Change summary:
- Replace the placeholder contract with a single method for API 1: Create Project.
- Keep the service surface limited to Milestone 3 scope only.

### 2. Update `TaskService.java`

#### Before
```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

/**
 * Placeholder service contract for future task-related business behavior.
 * Service methods are intentionally deferred to later planning and RED/GREEN milestones.
 */
public interface TaskService {
    // No methods are defined in this milestone.
}
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.util.List;

/**
 * Defines task-related business operations required by the Version 1 API contract.
 * This contract maps each approved task API to a controller-facing service method.
 */
public interface TaskService {

    /**
     * Creates a new task under an existing project.
     *
     * @param projectId identifies the owning project from the request path
     * @param request carries task creation inputs from the request body
     * @return the created task response payload defined by the API contract
     */
    TaskResponse createTask(String projectId, CreateTaskRequest request);

    /**
     * Updates the assignee email for an existing task.
     *
     * @param taskId identifies the task from the request path
     * @param request carries the replacement assignee email
     * @return the updated task response payload
     */
    TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request);

    /**
     * Updates the workflow status for an existing task.
     *
     * @param taskId identifies the task from the request path
     * @param request carries the replacement task status
     * @return the updated task response payload
     */
    TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request);

    /**
     * Returns all tasks that belong to a specific project.
     *
     * @param projectId identifies the project from the request path
     * @return tasks stored under the specified project
     */
    List<TaskResponse> getTasksByProjectId(String projectId);

    /**
     * Returns all tasks that match a single status filter.
     *
     * @param status identifies the requested task status filter
     * @return tasks matching the provided status
     */
    List<TaskResponse> getTasksByStatus(TaskStatus status);

    /**
     * Returns all tasks assigned to the provided email address.
     *
     * @param assigneeEmail identifies the requested assignee filter
     * @return tasks matching the provided assignee email
     */
    List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail);
}
```

Change summary:
- Replace the placeholder contract with six methods matching APIs 2 through 7.
- Use `TaskStatus` as the typed status filter to align the API and domain model.
- Keep controller-facing method names explicit and narrow.

### 3. Create `Project.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.model;

import java.time.LocalDateTime;

/**
 * Represents the core project domain state required by Version 1.
 * This model captures business data only and remains independent from transport concerns.
 */
public class Project {

    /** System-generated identifier used as the project business identity. */
    private String id;

    /** Required project name supplied by the API consumer. */
    private String name;

    /** Optional project description supplied by the API consumer. */
    private String description;

    /** Timestamp captured when the project is first created. */
    private LocalDateTime createdDate;

    /** Timestamp captured when the project is last changed. */
    private LocalDateTime updatedDate;

    // Constructors, getters, setters, and optional equals/hashCode/toString to be added in implementation.
}
```

### 4. Create `Task.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the core task domain state required by Version 1.
 * This model stores task ownership, workflow state, and scheduling data.
 */
public class Task {

    /** System-generated identifier used as the task business identity. */
    private String id;

    /** Identifier of the project that owns this task. */
    private String projectId;

    /** Required task title supplied by the API consumer. */
    private String title;

    /** Optional task description supplied by the API consumer. */
    private String description;

    /** Current workflow status for the task. */
    private TaskStatus status;

    /** Optional email address of the user currently assigned to the task. */
    private String assigneeEmail;

    /** Optional due date that must be today or in the future when provided. */
    private LocalDate dueDate;

    /** Timestamp captured when the task is first created. */
    private LocalDateTime createdDate;

    /** Timestamp captured when the task is last changed. */
    private LocalDateTime updatedDate;

    // Constructors, getters, setters, and optional equals/hashCode/toString to be added in implementation.
}
```

### 5. Create `TaskStatus.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.model;

/**
 * Defines the supported task workflow states approved for Version 1.
 */
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    DONE
}
```

Change summary for domain model:
- Add a `model` package because Milestone 3 explicitly requires domain model planning.
- Keep the domain model limited to fields confirmed by `Plan.md`.
- Store dates using `LocalDate` and `LocalDateTime` to match the approved contract shapes.

### 6. Create `CreateProjectRequest.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.project;

/**
 * Carries request body data for the project creation API.
 */
public class CreateProjectRequest {

    /** Required project name from the API request body. */
    private String name;

    /** Optional project description from the API request body. */
    private String description;

    // Getters and setters to be added in implementation.
}
```

### 7. Create `ProjectResponse.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.project;

import java.time.LocalDateTime;

/**
 * Returns the project payload defined by the Version 1 create project success contract.
 */
public class ProjectResponse {

    /** System-generated project identifier returned to the API consumer. */
    private String id;

    /** Required project name returned to the API consumer. */
    private String name;

    /** Optional project description returned to the API consumer. */
    private String description;

    /** Project creation timestamp returned to the API consumer. */
    private LocalDateTime createdDate;

    /** Project last update timestamp returned to the API consumer. */
    private LocalDateTime updatedDate;

    // Getters and setters to be added in implementation.
}
```

### 8. Create `CreateTaskRequest.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;

/**
 * Carries request body data for task creation under an existing project.
 */
public class CreateTaskRequest {

    /** Required task title from the API request body. */
    private String title;

    /** Optional task description from the API request body. */
    private String description;

    /** Optional assignee email from the API request body. */
    private String assigneeEmail;

    /** Optional due date from the API request body. */
    private LocalDate dueDate;

    /** Optional initial task status from the API request body. */
    private TaskStatus status;

    // Getters and setters to be added in implementation.
}
```

### 9. Create `TaskResponse.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Returns the task payload defined by the Version 1 task success contracts.
 * The same response shape is reused across create, update, and list operations.
 */
public class TaskResponse {

    /** System-generated task identifier returned to the API consumer. */
    private String id;

    /** Identifier of the owning project returned to the API consumer. */
    private String projectId;

    /** Required task title returned to the API consumer. */
    private String title;

    /** Optional task description returned to the API consumer. */
    private String description;

    /** Current workflow status returned to the API consumer. */
    private TaskStatus status;

    /** Optional assignee email returned to the API consumer. */
    private String assigneeEmail;

    /** Optional due date returned to the API consumer. */
    private LocalDate dueDate;

    /** Task creation timestamp returned to the API consumer. */
    private LocalDateTime createdDate;

    /** Task last update timestamp returned to the API consumer. */
    private LocalDateTime updatedDate;

    // Getters and setters to be added in implementation.
}
```

### 10. Create `UpdateTaskAssigneeRequest.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

/**
 * Carries request body data for updating an existing task assignee.
 */
public class UpdateTaskAssigneeRequest {

    /** Replacement assignee email from the API request body. */
    private String assigneeEmail;

    // Getters and setters to be added in implementation.
}
```

### 11. Create `UpdateTaskStatusRequest.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

/**
 * Carries request body data for updating an existing task workflow status.
 */
public class UpdateTaskStatusRequest {

    /** Replacement task status from the API request body. */
    private TaskStatus status;

    // Getters and setters to be added in implementation.
}
```

## Planned API-to-service mapping

| API Contract | Planned Service Method |
| ------------ | ---------------------- |
| `POST /api/projects` | `ProjectService#createProject(CreateProjectRequest request)` |
| `POST /api/projects/{projectId}/tasks` | `TaskService#createTask(String projectId, CreateTaskRequest request)` |
| `PATCH /api/tasks/{taskId}/assignee` | `TaskService#updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request)` |
| `PATCH /api/tasks/{taskId}/status` | `TaskService#updateTaskStatus(String taskId, UpdateTaskStatusRequest request)` |
| `GET /api/projects/{projectId}/tasks` | `TaskService#getTasksByProjectId(String projectId)` |
| `GET /api/tasks?status=...` | `TaskService#getTasksByStatus(TaskStatus status)` |
| `GET /api/tasks?assigneeEmail=...` | `TaskService#getTasksByAssigneeEmail(String assigneeEmail)` |

## Out of scope

- Any controller method implementation or controller refactoring.
- Any service implementation in `service/impl`.
- Any repository or DAO contract or implementation changes.
- Any validation annotations, exception types, or exception handlers.
- Any error response DTO or shared error-handling code.
- Any test creation or updates.
- Any persistence mapping, database schema, or H2 data access behavior.
- Any configuration, dependency, or build file updates.

## Success criteria

Milestone 3 implementation is complete when:

1. `ProjectService` and `TaskService` expose method signatures that cover all approved Version 1 APIs within Milestone 3 scope.
2. DTO classes exist for each approved request and success response shape required by the API contract.
3. Domain model classes exist for `Project`, `Task`, and `TaskStatus` with fields aligned to `docs/.ai/Plan.md`.
4. The planned model fields include only approved Version 1 data points and no unapproved additions.
5. No controller behavior, service implementation, repository contract, validation handler, or configuration work is introduced.
6. The code remains readable, uses standard Java naming conventions, and includes purpose-focused class and field comments.
