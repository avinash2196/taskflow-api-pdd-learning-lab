# Milestone 5 Implementation Plan: Controller Implementation Using Service Contract

## Milestone description

Milestone 5 implements only the controller behavior required to make the existing controller RED tests from `docs/.ai/004_Implementation_Plan_Controller_Test.md` pass.

This milestone is GREEN only. The controller layer must translate approved HTTP requests into calls to the already-defined `ProjectService` and `TaskService` contracts, then return the expected HTTP status codes and response bodies described in `docs/.ai/Plan.md` and `docs/.ai/001_API_Contract.md`.

Current state review:
- `ProjectController` exists as a placeholder with constructor injection and class-level `@RequestMapping("/api/projects")`, but no endpoint methods.
- `TaskController` exists as a placeholder with constructor injection and class-level `@RequestMapping("/api/tasks")`, but no endpoint methods.
- `ProjectControllerTest` and `TaskControllerTest` already define the Milestone 4 controller behavior that must now be made green.
- `ProjectService` and `TaskService` interfaces already expose the exact service methods required by the current controller tests.
- Request/response DTO classes already exist under `dto.project` and `dto.task`.
- Validation, not-found handling, consistent error payloads, and method-not-allowed contract coverage remain deferred to Milestone 6 and Milestone 7 unless a minimal controller signature is required for current tests to pass.
- The controller implementation must remain thin: request binding, delegation to service, and response status selection only.

## Files to be updated/changed

Planned files for Milestone 5 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`

No test files should be changed in this milestone.

## Exact code changes with comments

### 1. Update `ProjectController.java`

#### Before
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for future project-related API behavior.
 * Endpoint methods are intentionally deferred until controller tests are written.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param projectService service placeholder for future project use cases
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
}
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles project API endpoints by binding HTTP requests and delegating business work
 * to the project service contract.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param projectService service contract used by project endpoints
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Creates a project from the request body and returns the created payload.
     *
     * @param request request body mapped from the incoming JSON payload
     * @return created project response returned by the service layer
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }
}
```

Change summary:
- Add the single project creation endpoint required by the current controller test.
- Bind the JSON request body directly to `CreateProjectRequest`.
- Delegate immediately to `ProjectService#createProject`.
- Return `201 Created` with `@ResponseStatus(HttpStatus.CREATED)`.
- Keep the controller thin by avoiding validation logic, mapping layers, or exception translation in this milestone.

### 2. Update `TaskController.java`

#### Before
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for future task-related API behavior.
 * Endpoint methods are intentionally deferred until controller tests are written.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param taskService service placeholder for future task use cases
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
}
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles task API endpoints by mapping HTTP inputs to the existing task service contract.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param taskService service contract used by task endpoints
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a task under the provided project identifier.
     *
     * @param projectId owning project identifier from the URL path
     * @param request request body mapped from the incoming JSON payload
     * @return created task payload returned by the service layer
     */
    @PostMapping("/../projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable String projectId,
            @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    /**
     * Updates the assignee email for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the assignee email
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/{taskId}/assignee")
    public TaskResponse updateTaskAssignee(
            @PathVariable String taskId,
            @RequestBody UpdateTaskAssigneeRequest request) {
        return taskService.updateTaskAssignee(taskId, request);
    }

    /**
     * Updates the workflow status for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the new status
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }

    /**
     * Returns all tasks for a specific project.
     *
     * @param projectId project identifier from the URL path
     * @return list of tasks returned by the service layer
     */
    @GetMapping("/../projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProjectId(@PathVariable String projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    /**
     * Returns tasks filtered by status or assignee email using the approved query options only.
     *
     * @param status optional status filter
     * @param assigneeEmail optional assignee email filter
     * @return list of tasks for the supplied filter
     */
    @GetMapping(params = "status")
    public List<TaskResponse> getTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    /**
     * Returns tasks filtered by assignee email using the approved query option.
     *
     * @param assigneeEmail assignee email from the query string
     * @return list of tasks for the supplied filter
     */
    @GetMapping(params = "assigneeEmail")
    public List<TaskResponse> getTasksByAssigneeEmail(@RequestParam String assigneeEmail) {
        return taskService.getTasksByAssigneeEmail(assigneeEmail);
    }
}
```

#### After refinement for correct route ownership
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles task API endpoints by mapping HTTP inputs to the existing task service contract.
 */
@RestController
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param taskService service contract used by task endpoints
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a task under the provided project identifier.
     *
     * @param projectId owning project identifier from the URL path
     * @param request request body mapped from the incoming JSON payload
     * @return created task payload returned by the service layer
     */
    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable String projectId,
            @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    /**
     * Updates the assignee email for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the assignee email
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/api/tasks/{taskId}/assignee")
    public TaskResponse updateTaskAssignee(
            @PathVariable String taskId,
            @RequestBody UpdateTaskAssigneeRequest request) {
        return taskService.updateTaskAssignee(taskId, request);
    }

    /**
     * Updates the workflow status for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the new status
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/api/tasks/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }

    /**
     * Returns all tasks for a specific project.
     *
     * @param projectId project identifier from the URL path
     * @return list of tasks returned by the service layer
     */
    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProjectId(@PathVariable String projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    /**
     * Returns tasks filtered by status using the approved status query parameter.
     *
     * @param status status filter from the query string
     * @return list of tasks returned by the service layer
     */
    @GetMapping(value = "/api/tasks", params = "status")
    public List<TaskResponse> getTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    /**
     * Returns tasks filtered by assignee email using the approved assignee query parameter.
     *
     * @param assigneeEmail assignee email from the query string
     * @return list of tasks returned by the service layer
     */
    @GetMapping(value = "/api/tasks", params = "assigneeEmail")
    public List<TaskResponse> getTasksByAssigneeEmail(@RequestParam String assigneeEmail) {
        return taskService.getTasksByAssigneeEmail(assigneeEmail);
    }
}
```

Change summary:
- Replace the placeholder task controller with thin endpoint methods that match the current RED tests exactly.
- Remove the class-level `@RequestMapping("/api/tasks")` so one controller can own both `/api/projects/{projectId}/tasks` and `/api/tasks/...` routes cleanly.
- Bind path variables, request bodies, and query parameters directly to existing DTOs and `TaskStatus`.
- Use `@GetMapping(..., params = "...")` so `GET /api/tasks` without an approved filter remains unmapped and continues to satisfy the current negative test as a 4xx result.
- Return `201 Created` only for task creation; all other mapped task endpoints use the default `200 OK`.
- Keep all business decisions in the service layer and avoid adding validation branches or exception translation here.

## Out of scope

- Any change to `ProjectControllerTest` or `TaskControllerTest`.
- Any new controller tests beyond those already created in Milestone 4.
- Any validation annotations, validation messages, or custom validators.
- Any exception handler, error response DTO, or not-found/error mapping logic.
- Any service interface change or service implementation work.
- Any DTO or domain model change.
- Any repository, DAO, persistence, or integration work.
- Any `Plan.md` execution-status update.
- Any controller refactor beyond the minimum needed to make current tests pass.

## Success criteria

Milestone 5 implementation is complete when:

1. `ProjectController` exposes `POST /api/projects` and returns `201 Created` with the service response body.
2. `TaskController` exposes only the task endpoints required by the current controller tests:
   - `POST /api/projects/{projectId}/tasks`
   - `PATCH /api/tasks/{taskId}/assignee`
   - `PATCH /api/tasks/{taskId}/status`
   - `GET /api/projects/{projectId}/tasks`
   - `GET /api/tasks?status=...`
   - `GET /api/tasks?assigneeEmail=...`
3. Each controller method delegates directly to the existing service contract with correctly bound request inputs.
4. `GET /api/tasks` without `status` or `assigneeEmail` remains unmapped so the current negative controller test stays green.
5. No validation-specific or exception-handling-specific behavior is added beyond what is necessary for compilation and current test execution.
6. The code compiles after controller implementation.
7. All existing controller tests pass after implementation.
8. The controllers remain thin and do not absorb service or validation logic.
