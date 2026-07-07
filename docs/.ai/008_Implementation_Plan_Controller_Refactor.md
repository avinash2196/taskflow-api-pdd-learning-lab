# Milestone 8 Implementation Plan: Controller Refactor

## Milestone description

Milestone 8 is the REFACTOR phase defined in `docs/.ai/Plan.md` for controller-level work. For the current repository state, the refactor scope should remain limited to controller test classes and test-only helper files. The production controllers must be reviewed first, but no production controller code should be changed as part of this milestone plan because the current controllers already follow the thin controller principle:

- request binding only
- validation delegated to annotations and Spring MVC
- service delegation only
- shared error translation delegated to `GlobalExceptionHandler`

Current state review based on the existing code:

- `ProjectController` is already thin and contains one endpoint with direct delegation to `ProjectService`.
- `TaskController` is already thin and contains endpoint mappings, validation annotations, and direct delegation to `TaskService`.
- `ProjectControllerTest` and `TaskControllerTest` contain the main refactor opportunities:
  - repeated JSON request payloads
  - repeated success fixture construction
  - repeated validation error assertions
  - repeated not-found error assertions
  - repeated endpoint strings
  - outdated RED-phase class comments that no longer match the green state
  - test-local `ProjectNotFoundException` and `TaskNotFoundException` classes in `TaskControllerTest` even though production exception classes already exist

Milestone 8 implementation order must follow this sequence:

1. Verify the current controller and validation test baseline is green.
2. Review `ProjectController` and `TaskController` and confirm no production refactor is required.
3. Re-run controller tests after the controller review step to confirm no behavior changed.
4. Refactor controller test classes and extract shared test-only helpers where duplication exists.
5. Re-run controller tests after the test refactor.
6. Re-run the broader Maven test suite to confirm behavior remains unchanged.

This plan does not introduce new controller behavior, new validation rules, or any change to production source files.

## Files to be updated/changed

Planned files for Milestone 8 implementation:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectControllerTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestDataFactory.java`
4. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestAssertions.java`
5. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestJsonFactory.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`

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
    void createProjectReturnsCreatedProjectResponse() throws Exception {
        ProjectResponse response = new ProjectResponse();
        response.setId("project-100");
        response.setName("TaskFlow");
        response.setDescription("Project description");
        response.setCreatedDate(LocalDateTime.of(2026, 7, 5, 10, 15, 0));
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 10, 15, 0));

        given(projectService.createProject(any())).willReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "TaskFlow",
                                  "description": "Project description"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("project-100"));
    }

    @Test
    void createProjectReturnsBadRequestWhenNameIsMissing() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Project without a name"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.context.field").value("name"));
    }
}
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestAssertions;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestJsonFactory;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-slice tests for project endpoints.
 * Shared test helpers keep request setup and repeated error assertions readable.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    private static final String PROJECTS_URL = "/api/projects";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("POST /api/projects returns 201 Created with the created project payload")
    void createProjectReturnsCreatedProjectResponse() throws Exception {
        given(projectService.createProject(any()))
                .willReturn(ControllerTestDataFactory.projectResponse(
                        "project-100",
                        "TaskFlow",
                        "Project description",
                        "2026-07-05T10:15:00"));

        mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest(
                                "TaskFlow",
                                "Project description")))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("project-100"))
                .andExpect(jsonPath("$.name").value("TaskFlow"))
                .andExpect(jsonPath("$.description").value("Project description"))
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T10:15:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T10:15:00"));

        ArgumentCaptor<CreateProjectRequest> requestCaptor = ArgumentCaptor.forClass(CreateProjectRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("TaskFlow");
        assertThat(requestCaptor.getValue().getDescription()).isEqualTo("Project description");
    }

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is missing")
    void createProjectReturnsBadRequestWhenNameIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectWithoutName("Project without a name"))),
                "name");

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is null")
    void createProjectReturnsBadRequestWhenNameIsNull() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest(
                                null,
                                "Project without a valid name"))),
                "name");

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("POST /api/projects allows optional description when name is valid")
    void createProjectAllowsOptionalDescription() throws Exception {
        given(projectService.createProject(any()))
                .willReturn(ControllerTestDataFactory.projectResponse(
                        "project-101",
                        "TaskFlow Lite",
                        null,
                        "2026-07-05T12:00:00"));

        mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest("TaskFlow Lite", null)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("project-101"))
                .andExpect(jsonPath("$.name").value("TaskFlow Lite"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T12:00:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T12:00:00"));

        ArgumentCaptor<CreateProjectRequest> requestCaptor = ArgumentCaptor.forClass(CreateProjectRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("TaskFlow Lite");
        assertThat(requestCaptor.getValue().getDescription()).isNull();
    }
}
```

Planned change notes:

- Replace repeated inline `ProjectResponse` construction with a shared test fixture factory.
- Replace repeated validation error assertions with a shared assertion helper.
- Replace repeated project request JSON with a shared JSON helper.
- Remove outdated RED-phase comments and keep comments aligned to current usage and purpose.
- Keep request-to-service mapping assertions inside the test class because they remain readable and specific.

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

    private static final class ProjectNotFoundException extends RuntimeException {
        private ProjectNotFoundException(String projectId) {
            super(projectId);
        }
    }

    private static final class TaskNotFoundException extends RuntimeException {
        private TaskNotFoundException(String taskId) {
            super(taskId);
        }
    }

    private TaskResponse buildTaskResponse() {
        TaskResponse response = new TaskResponse();
        response.setId("task-200");
        response.setProjectId("project-100");
        response.setTitle("Write controller tests");
        response.setDescription("Milestone 4 RED tests");
        response.setStatus(TaskStatus.TODO);
        response.setAssigneeEmail("owner@example.com");
        response.setDueDate(LocalDate.of(2026, 7, 10));
        response.setCreatedDate(LocalDateTime.of(2026, 7, 5, 11, 0, 0));
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 11, 0, 0));
        return response;
    }
}
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestAssertions;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestJsonFactory;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-slice tests for task endpoints.
 * Shared helpers keep fixtures, request payloads, and error assertions consistent.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String PROJECT_TASKS_URL = "/api/projects/%s/tasks";
    private static final String TASK_ASSIGNEE_URL = "/api/tasks/%s/assignee";
    private static final String TASK_STATUS_URL = "/api/tasks/%s/status";
    private static final String TASKS_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 201 Created with the created task payload")
    void createTaskReturnsCreatedTaskResponse() throws Exception {
        given(taskService.createTask(eq("project-100"), any()))
                .willReturn(ControllerTestDataFactory.defaultTaskResponse());

        mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.defaultCreateTaskRequest()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.projectId").value("project-100"))
                .andExpect(jsonPath("$.title").value("Write controller tests"))
                .andExpect(jsonPath("$.description").value("Milestone 4 RED tests"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.assigneeEmail").value("owner@example.com"))
                .andExpect(jsonPath("$.dueDate").value("2026-07-10"))
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T11:00:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:00:00"));

        ArgumentCaptor<CreateTaskRequest> requestCaptor = ArgumentCaptor.forClass(CreateTaskRequest.class);
        verify(taskService).createTask(eq("project-100"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTitle()).isEqualTo("Write controller tests");
        assertThat(requestCaptor.getValue().getDescription()).isEqualTo("Milestone 4 RED tests");
        assertThat(requestCaptor.getValue().getAssigneeEmail()).isEqualTo("owner@example.com");
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 200 OK with the updated task payload")
    void updateTaskAssigneeReturnsUpdatedTaskResponse() throws Exception {
        given(taskService.updateTaskAssignee(eq("task-200"), any()))
                .willReturn(ControllerTestDataFactory.taskResponseWithUpdatedAssignee(
                        "task-200",
                        "updated.owner@example.com",
                        "2026-07-05T11:30:00"));

        mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.assigneeRequest("updated.owner@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.assigneeEmail").value("updated.owner@example.com"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:30:00"));

        ArgumentCaptor<UpdateTaskAssigneeRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTaskAssigneeRequest.class);
        verify(taskService).updateTaskAssignee(eq("task-200"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAssigneeEmail()).isEqualTo("updated.owner@example.com");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 200 OK with the updated task payload")
    void updateTaskStatusReturnsUpdatedTaskResponse() throws Exception {
        given(taskService.updateTaskStatus(eq("task-200"), any()))
                .willReturn(ControllerTestDataFactory.taskResponseWithUpdatedStatus(
                        "task-200",
                        TaskStatus.IN_PROGRESS,
                        "2026-07-05T11:45:00"));

        mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.statusRequest("IN_PROGRESS")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:45:00"));

        ArgumentCaptor<UpdateTaskStatusRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTaskStatusRequest.class);
        verify(taskService).updateTaskStatus(eq("task-200"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks returns 200 OK with project task list")
    void getTasksByProjectIdReturnsTaskList() throws Exception {
        given(taskService.getTasksByProjectId("project-100"))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(PROJECT_TASKS_URL.formatted("project-100")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].projectId").value("project-100"))
                .andExpect(jsonPath("$[0].status").value("TODO"));

        verify(taskService).getTasksByProjectId("project-100");
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO returns 200 OK with status-filtered task list")
    void getTasksByStatusReturnsTaskList() throws Exception {
        given(taskService.getTasksByStatus(TaskStatus.TODO))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(TASKS_URL).param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].status").value("TODO"));

        verify(taskService).getTasksByStatus(TaskStatus.TODO);
    }

    @Test
    @DisplayName("GET /api/tasks?assigneeEmail=owner@example.com returns 200 OK with assignee-filtered task list")
    void getTasksByAssigneeEmailReturnsTaskList() throws Exception {
        given(taskService.getTasksByAssigneeEmail("owner@example.com"))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(TASKS_URL).param("assigneeEmail", "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].assigneeEmail").value("owner@example.com"));

        verify(taskService).getTasksByAssigneeEmail("owner@example.com");
    }

    @Test
    @DisplayName("GET /api/tasks without supported query parameters remains a negative controller contract case")
    void getTasksWithoutRequiredFilterRemainsNegativeCaseForControllerPlanning() throws Exception {
        mockMvc.perform(get(TASKS_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when title is missing")
    void createTaskReturnsBadRequestWhenTitleIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.createTaskWithoutTitle("Missing title"))),
                "title");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 404 Not Found when project does not exist")
    void createTaskReturnsNotFoundWhenProjectDoesNotExist() throws Exception {
        given(taskService.createTask(eq("project-999"), any()))
                .willThrow(new ProjectNotFoundException("project-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.minimalCreateTaskRequest("Write tests"))),
                "PROJECT_NOT_FOUND",
                "projectId");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 404 Not Found when task does not exist")
    void updateTaskAssigneeReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        given(taskService.updateTaskAssignee(eq("task-999"), any()))
                .willThrow(new TaskNotFoundException("task-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.assigneeRequest("owner@example.com"))),
                "TASK_NOT_FOUND",
                "taskId");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 404 Not Found when task does not exist")
    void updateTaskStatusReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        given(taskService.updateTaskStatus(eq("task-999"), any()))
                .willThrow(new TaskNotFoundException("task-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.statusRequest("DONE"))),
                "TASK_NOT_FOUND",
                "taskId");
    }
}
```

Planned change notes:

- Remove duplicated task fixture construction by using a shared factory.
- Remove test-local exception classes and use the real production exceptions already present in the codebase.
- Replace repeated error assertions with a shared assertion helper.
- Replace repeated JSON payloads with a shared JSON helper.
- Replace repeated endpoint literals with named constants.
- Remove outdated RED-phase comments and keep usage comments readable at class and helper level.

### 3. Add `ControllerTestDataFactory.java`

#### Before changes
```java
// File does not exist.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared controller-test fixtures.
 * Keeps response object setup in one place so test methods stay focused on behavior.
 */
public final class ControllerTestDataFactory {

    private ControllerTestDataFactory() {
    }

    public static ProjectResponse projectResponse(
            String id,
            String name,
            String description,
            String timestamp) {
        LocalDateTime dateTime = LocalDateTime.parse(timestamp);

        ProjectResponse response = new ProjectResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setCreatedDate(dateTime);
        response.setUpdatedDate(dateTime);
        return response;
    }

    public static TaskResponse defaultTaskResponse() {
        TaskResponse response = new TaskResponse();
        response.setId("task-200");
        response.setProjectId("project-100");
        response.setTitle("Write controller tests");
        response.setDescription("Milestone 4 RED tests");
        response.setStatus(TaskStatus.TODO);
        response.setAssigneeEmail("owner@example.com");
        response.setDueDate(LocalDate.of(2026, 7, 10));
        response.setCreatedDate(LocalDateTime.of(2026, 7, 5, 11, 0, 0));
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 11, 0, 0));
        return response;
    }

    public static TaskResponse taskResponseWithUpdatedAssignee(
            String taskId,
            String assigneeEmail,
            String updatedTimestamp) {
        TaskResponse response = defaultTaskResponse();
        response.setId(taskId);
        response.setAssigneeEmail(assigneeEmail);
        response.setUpdatedDate(LocalDateTime.parse(updatedTimestamp));
        return response;
    }

    public static TaskResponse taskResponseWithUpdatedStatus(
            String taskId,
            TaskStatus status,
            String updatedTimestamp) {
        TaskResponse response = defaultTaskResponse();
        response.setId(taskId);
        response.setStatus(status);
        response.setUpdatedDate(LocalDateTime.parse(updatedTimestamp));
        return response;
    }
}
```

Planned change notes:

- Centralize controller response fixture creation used across both controller test classes.
- Keep the helper narrow and limited to the current controller test suite.
- Avoid moving assertions into the factory.

### 4. Add `ControllerTestAssertions.java`

#### Before changes
```java
// File does not exist.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared assertions for repeated controller error response checks.
 * This keeps validation and not-found expectations consistent across test classes.
 */
public final class ControllerTestAssertions {

    private ControllerTestAssertions() {
    }

    public static void assertValidationError(ResultActions resultActions, String fieldName) throws Exception {
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.context.field").value(fieldName));
    }

    public static void assertNotFoundError(
            ResultActions resultActions,
            String errorCode,
            String fieldName) throws Exception {
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.context.field").value(fieldName));
    }
}
```

Planned change notes:

- Extract common error response assertions used by both controller test classes.
- Keep the helper specific to the current API error contract.
- Do not generalize beyond what Milestone 8 needs.

### 5. Add `ControllerTestJsonFactory.java`

#### Before changes
```java
// File does not exist.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

/**
 * Shared JSON payload builders for controller tests.
 * These helpers remove repeated text blocks while keeping payload intent explicit.
 */
public final class ControllerTestJsonFactory {

    private ControllerTestJsonFactory() {
    }

    public static String projectRequest(String name, String description) {
        return """
                {
                  "name": %s,
                  "description": %s
                }
                """.formatted(asJsonValue(name), asJsonValue(description));
    }

    public static String projectWithoutName(String description) {
        return """
                {
                  "description": %s
                }
                """.formatted(asJsonValue(description));
    }

    public static String defaultCreateTaskRequest() {
        return """
                {
                  "title": "Write controller tests",
                  "description": "Milestone 4 RED tests",
                  "assigneeEmail": "owner@example.com",
                  "dueDate": "2026-07-10",
                  "status": "TODO"
                }
                """;
    }

    public static String minimalCreateTaskRequest(String title) {
        return """
                {
                  "title": %s
                }
                """.formatted(asJsonValue(title));
    }

    public static String createTaskWithoutTitle(String description) {
        return """
                {
                  "description": %s
                }
                """.formatted(asJsonValue(description));
    }

    public static String assigneeRequest(String assigneeEmail) {
        return """
                {
                  "assigneeEmail": %s
                }
                """.formatted(asJsonValue(assigneeEmail));
    }

    public static String statusRequest(String status) {
        return """
                {
                  "status": %s
                }
                """.formatted(asJsonValue(status));
    }

    private static String asJsonValue(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
}
```

Planned change notes:

- Extract repeated JSON text blocks into one test-only helper.
- Keep payload methods explicit instead of creating a generic builder that hurts readability.
- Preserve preview readability by keeping formatted text blocks small and purpose-specific.

## Out of scope

- Any modification to `src/main/java` production controller files
- Any change to service interfaces, service implementations, repositories, or domain models
- Any new controller behavior, endpoint, validation rule, or response contract
- Any change to `GlobalExceptionHandler` or production exception classes
- Any refactor outside controller tests and their test-only helper files
- Any mock implementation classes beyond Mockito mocks
- Any `docs/.ai/Plan.md` update
- Any future milestone work from repository, service, or integration phases

## Success criteria

Milestone 8 implementation is complete when:

1. The current controller and validation test baseline is verified before refactor work begins.
2. `ProjectController` and `TaskController` are reviewed first and left unchanged because they already satisfy the thin controller principle.
3. `ProjectControllerTest` and `TaskControllerTest` are refactored to remove repeated JSON payloads, repeated fixture setup, repeated error assertions, repeated endpoint literals, outdated comments, and test-local duplicate exception classes.
4. Shared controller test code is extracted only into test-scope helper classes using standard Java naming conventions.
5. Mockito remains the only mocking approach for controller dependencies.
6. Controller tests pass after the controller review verification step.
7. Controller tests pass again after the controller test refactor is complete.
8. The broader Maven test suite passes after the Milestone 8 refactor.
9. Controller behavior, validation behavior, and error response behavior remain unchanged.
10. The final state improves readability and maintainability of controller tests without impacting actual functionality.
