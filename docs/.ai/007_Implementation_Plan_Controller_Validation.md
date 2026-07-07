# Milestone 7 Implementation Plan: Controller Validation and Exception Handling

## Milestone description

Milestone 7 is the GREEN phase for the validation and exception-handling behavior defined in [Plan.md](/C:/Users/avina/Downloads/avinash-repo/taskflow-api/docs/.ai/Plan.md) and [001_API_Contract.md](/C:/Users/avina/Downloads/avinash-repo/taskflow-api/docs/.ai/001_API_Contract.md).

This plan is limited to the controller validation and exception behavior explicitly required by Milestone 7 and already exercised by the existing controller tests created in Milestone 6. The implementation target is to make those tests pass while preserving the thin controller principle and avoiding any new validations or non-approved behavior.

Current state review:
- `ProjectControllerTest` and `TaskControllerTest` already contain the Milestone 6 RED coverage for:
  - required request-body fields
  - invalid assignee email formats
  - past due dates
  - unsupported status values
  - project not found
  - task not found
  - consistent error response structure with `errorCode`, `message`, and `context.field`
- `ProjectController` already exists and only handles `POST /api/projects`.
- `TaskController` already exists and only handles the approved Milestone 4 and 5 task endpoints.
- Controller request DTOs already exist and are the correct place to keep Bean Validation rules so controllers stay thin.
- Shared exception classes and a global exception handler are required in `src/main/java` because the approved behavior includes consistent `404` and `400` error payloads.
- Test-local `ProjectNotFoundException` and `TaskNotFoundException` currently appear in `TaskControllerTest`; Milestone 7 production code must provide shared equivalents so application behavior is aligned with the contract. This plan does not require any test-file change.

Milestone 7 implementation must therefore be limited to:
1. activating validation at controller boundaries,
2. adding only the DTO validation rules explicitly covered by the current tests and approved contract,
3. adding shared exception classes needed by the not-found cases,
4. adding one global exception handler that translates validation and not-found failures into the approved error shape.

## Files to be updated/changed

Only the following production files are in scope for Milestone 7 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/CreateProjectRequest.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/CreateTaskRequest.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskAssigneeRequest.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskStatusRequest.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/ApiErrorResponse.java`
8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/ProjectNotFoundException.java`
9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/TaskNotFoundException.java`
10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/GlobalExceptionHandler.java`

No test files are planned for change.

## Exact code changes with comments

### 1. Update `CreateProjectRequest.java`

#### Before changes
```java
/**
 * Carries request body data for the project creation API.
 */
public class CreateProjectRequest {

    /** Required project name from the API request body. */
    private String name;

    /** Optional project description from the API request body. */
    private String description;
}
```

#### After changes

```java
import jakarta.validation.constraints.NotBlank;

/**
 * Carries request body data for the project creation API.
 * Bean Validation keeps the required-field rule close to the request model.
 */
public class CreateProjectRequest {

    /** Required project name from the API request body. */
    @NotBlank(message = "Project name is required.")
    private String name;

    /** Optional project description from the API request body. */
    private String description;
}
```

Implementation comments:
- Add only `@NotBlank` for `name`.

### 2. Update `CreateTaskRequest.java`

#### Before changes
```java
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
}
```

#### After changes

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

public class CreateTaskRequest {

    /** Required task title from the API request body. */
    @NotBlank(message = "Task title is required.")
    private String title;

    /** Optional task description from the API request body. */
    private String description;

    /** Optional assignee email from the API request body. */
    @Email(message = "Assignee email must be a valid email address.")
    private String assigneeEmail;

    /** Optional due date from the API request body. */
    @FutureOrPresent(message = "Due date must be today or a future date.")
    private LocalDate dueDate;

    /** Optional initial task status from the API request body. */
    private TaskStatus status;
}
```

Implementation comments:
- Add only the validations exercised by existing tests: required `title`, valid optional `assigneeEmail`, and non-past optional `dueDate`.
- Do not add a custom validator for `status`. Unsupported values should be handled by request binding plus shared exception mapping.

### 3. Update `UpdateTaskAssigneeRequest.java`

#### Before changes
```java
public class UpdateTaskAssigneeRequest {

    /** Replacement assignee email from the API request body. */
    private String assigneeEmail;
}
```

#### After changes
```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskAssigneeRequest {

    /** Replacement assignee email from the API request body. */
    @NotNull(message = "Assignee email is required.")
    @Email(message = "Assignee email must be a valid email address.")
    private String assigneeEmail;
}
```

Implementation comments:
- Add only the required-field and email-format validations already covered by the tests.

### 4. Update `UpdateTaskStatusRequest.java`

#### Before changes
```java
public class UpdateTaskStatusRequest {

    /** Replacement task status from the API request body. */
    private TaskStatus status;
}
```

#### After changes
```java
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {

    /** Replacement task status from the API request body. */
    @NotNull(message = "Task status is required.")
    private TaskStatus status;
}
```

Implementation comments:
- Add only the required-field rule needed by the current missing-status test.

### 5. Update `ProjectController.java`

#### Before changes
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProjectResponse createProject(@RequestBody CreateProjectRequest request) {
    return projectService.createProject(request);
}
```

#### After changes
```java
import jakarta.validation.Valid;

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request) {
    // Thin controller: framework validation runs before the service is called.
    return projectService.createProject(request);
}
```

Implementation comments:
- Add `@Valid` only.
- Do not add manual validation branches inside the controller.

### 6. Update `TaskController.java`

#### Before changes
```java
@PostMapping("/api/projects/{projectId}/tasks")
@ResponseStatus(HttpStatus.CREATED)
public TaskResponse createTask(
        @PathVariable String projectId,
        @RequestBody CreateTaskRequest request) {
    return taskService.createTask(projectId, request);
}

@PatchMapping("/api/tasks/{taskId}/assignee")
public TaskResponse updateTaskAssignee(
        @PathVariable String taskId,
        @RequestBody UpdateTaskAssigneeRequest request) {
    return taskService.updateTaskAssignee(taskId, request);
}

@PatchMapping("/api/tasks/{taskId}/status")
public TaskResponse updateTaskStatus(
        @PathVariable String taskId,
        @RequestBody UpdateTaskStatusRequest request) {
    return taskService.updateTaskStatus(taskId, request);
}

@GetMapping(value = "/api/tasks", params = "assigneeEmail")
public List<TaskResponse> getTasksByAssigneeEmail(@RequestParam String assigneeEmail) {
    return taskService.getTasksByAssigneeEmail(assigneeEmail);
}
```

#### After changes
```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.validation.annotation.Validated;

/**
 * Handles task API endpoints by mapping HTTP inputs to the existing task service contract.
 * Validation stays annotation-driven so the controller remains thin.
 */
@RestController
@Validated
public class TaskController {

    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable String projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    @PatchMapping("/api/tasks/{taskId}/assignee")
    public TaskResponse updateTaskAssignee(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskAssigneeRequest request) {
        return taskService.updateTaskAssignee(taskId, request);
    }

    @PatchMapping("/api/tasks/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }

    @GetMapping(value = "/api/tasks", params = "assigneeEmail")
    public List<TaskResponse> getTasksByAssigneeEmail(
            @RequestParam
            @Email(message = "Assignee email must be a valid email address.")
            String assigneeEmail) {
        return taskService.getTasksByAssigneeEmail(assigneeEmail);
    }
}
```

Implementation comments:
- Add `@Valid` on request bodies used in validation tests.
- Add `@Validated` only because method-parameter validation is needed for the assignee-email query parameter.
- Keep all business decisions in the service layer.

### 7. Add `ApiErrorResponse.java`

#### Before changes
```java
// No shared production error response DTO exists yet.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.exception;

import java.util.Map;

/**
 * Shared API error payload used by controller exception handling.
 * The shape is limited to the approved contract fields only.
 */
public class ApiErrorResponse {

    private String errorCode;
    private String message;
    private Map<String, String> context;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String errorCode, String message, Map<String, String> context) {
        this.errorCode = errorCode;
        this.message = message;
        this.context = context;
    }

    // Standard getters and setters.
}
```

Implementation comments:
- Keep the payload limited to `errorCode`, `message`, and `context`.
- Do not add extra fields such as timestamp, path, or trace ID.

### 8. Add shared not-found exceptions

#### Before changes
```java
// No production project/task not-found exception classes exist yet.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.exception;

/**
 * Signals that the requested project identifier does not exist.
 */
public class ProjectNotFoundException extends RuntimeException {

    private final String projectId;

    public ProjectNotFoundException(String projectId) {
        super("Project not found: " + projectId);
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
```

```java
package io.github.avinash2196.taskflowapipddlearninglab.exception;

/**
 * Signals that the requested task identifier does not exist.
 */
public class TaskNotFoundException extends RuntimeException {

    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
```

Implementation comments:
- These classes support only the not-found cases already covered by tests.
- Do not introduce additional shared exception types unless a current Milestone 6 test requires them.

### 9. Add `GlobalExceptionHandler.java`

#### Before changes
```java
// No production @RestControllerAdvice exists yet.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Centralizes controller error mapping so controllers stay focused on delegation
 * and all approved error responses use one shared contract.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().get(0);
        return buildBadRequest(fieldError.getDefaultMessage(), fieldError.getField());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
        String propertyPath = violation.getPropertyPath().toString();
        String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        return buildBadRequest(violation.getMessage(), fieldName);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        ParameterValidationResult result = exception.getAllValidationResults().get(0);
        String fieldName = result.getMethodParameter().getParameterName();
        MessageSourceResolvable error = result.getResolvableErrors().get(0);
        return buildBadRequest(error.getDefaultMessage(), fieldName);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return buildBadRequest("Request contains an unsupported value.", resolveInvalidFieldName(exception));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return buildBadRequest("Request contains an unsupported value.", exception.getName());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProjectNotFound(ProjectNotFoundException exception) {
        return buildNotFound("PROJECT_NOT_FOUND", exception.getMessage(), "projectId");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTaskNotFound(TaskNotFoundException exception) {
        return buildNotFound("TASK_NOT_FOUND", exception.getMessage(), "taskId");
    }

    private ResponseEntity<ApiErrorResponse> buildBadRequest(String message, String fieldName) {
        ApiErrorResponse response = new ApiErrorResponse(
                "VALIDATION_ERROR",
                message,
                Map.of("field", fieldName)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<ApiErrorResponse> buildNotFound(String errorCode, String message, String fieldName) {
        ApiErrorResponse response = new ApiErrorResponse(
                errorCode,
                message,
                Map.of("field", fieldName)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private String resolveInvalidFieldName(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException
                && !invalidFormatException.getPath().isEmpty()) {
            JsonMappingException.Reference reference = invalidFormatException.getPath().get(0);
            if (reference.getFieldName() != null) {
                return reference.getFieldName();
            }
        }
        return "status";
    }
}
```

Implementation comments:
- One shared handler is sufficient for all Milestone 7 controller error translation.
- `MethodArgumentNotValidException` covers invalid request bodies.
- `ConstraintViolationException` or `HandlerMethodValidationException` covers method-level validation depending on Spring validation flow.
- `HttpMessageNotReadableException` and `MethodArgumentTypeMismatchException` cover unsupported enum-style values from JSON bodies or query parameters.
- Not-found mapping is limited to `ProjectNotFoundException` and `TaskNotFoundException`.
- Do not add a generic catch-all handler as part of Milestone 7.

### 10. Implementation sequence

1. Add Bean Validation annotations to `CreateProjectRequest`, `CreateTaskRequest`, `UpdateTaskAssigneeRequest`, and `UpdateTaskStatusRequest`.
2. Add `@Valid` and `@Validated` to the existing controllers where needed.
3. Add `ApiErrorResponse`, `ProjectNotFoundException`, and `TaskNotFoundException` in `src/main/java`.
4. Add `GlobalExceptionHandler` that maps:
   - `MethodArgumentNotValidException` to `400 VALIDATION_ERROR`
   - `ConstraintViolationException` and/or `HandlerMethodValidationException` to `400 VALIDATION_ERROR`
   - `HttpMessageNotReadableException` and `MethodArgumentTypeMismatchException` to `400 VALIDATION_ERROR` for unsupported values
   - `ProjectNotFoundException` to `404 PROJECT_NOT_FOUND`
   - `TaskNotFoundException` to `404 TASK_NOT_FOUND`
5. Compile the code and run the existing test suite to verify only the approved Milestone 7 behavior was added and all tests are green.

## Out of scope

- Any change to controller test files
- Any repository contract, repository implementation, or data access behavior
- Any service implementation change unrelated to throwing the shared not-found exceptions already required by controller tests
- Any controller refactor beyond adding validation hooks needed for Milestone 7
- Any validation rule not already defined in Milestone 6 tests or approved contract text
- Blank-string rejection, trimming, normalization, localization, or multi-field validation rules
- Additional error payload fields such as timestamp, path, stack trace, or request ID
- Authentication, authorization, pagination, sorting, filtering expansion, or new endpoints
- Any `Plan.md` status update as part of this planning file

## Success criteria

Milestone 7 implementation is successful when:

1. The application compiles after the production changes listed in this plan.
2. `ProjectControllerTest` passes without modification.
3. `TaskControllerTest` passes without modification.
4. Validation failures return `400 Bad Request` with:
   - `errorCode = "VALIDATION_ERROR"`
   - a non-empty `message`
   - `context.field` matching the failing request input
5. Not-found controller cases return `404 Not Found` with:
   - `errorCode = "PROJECT_NOT_FOUND"` and `context.field = "projectId"` for missing projects
   - `errorCode = "TASK_NOT_FOUND"` and `context.field = "taskId"` for missing tasks
6. Validation failures stop at the controller boundary so the mocked services are not invoked in validation-failure tests.
7. Controllers remain thin and continue to delegate business behavior to the service layer.
