# Milestone 4 Implementation Plan: Controller Tests

## Milestone description

Milestone 4 defines failing controller-level tests for the approved Version 1 API behavior described in `docs/.ai/Plan.md` and `docs/.ai/001_API_Contract.md`.

This milestone is RED only. The implementation plan must cover controller-slice tests that target the real existing `ProjectController` and `TaskController` classes, mock the service layer with Mockito, and verify the externally visible controller contract before controller behavior is implemented.

Current state review:
- `ProjectController` exists as a placeholder controller with only class-level `@RequestMapping("/api/projects")`.
- `TaskController` exists as a placeholder controller with only class-level `@RequestMapping("/api/tasks")`.
- `ProjectService` and `TaskService` interfaces already exist with the Version 1 service methods needed by the controller layer.
- DTO classes already exist for project creation, task creation, task update, and task response payloads.
- No controller test classes currently exist under `src/test/java`.
- Validation and exception handling behavior belong to Milestone 6 and Milestone 7, so this plan must not pull that work forward beyond identifying the tests that should remain deferred.

## Files to be updated/changed

Planned files for Milestone 4 implementation:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectControllerTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`

No source file under `src/main/java` should be changed in Milestone 4 implementation.

## Exact code changes with comments

### 1. Create `ProjectControllerTest.java`

#### Before
```java
// File does not exist.
```

#### After
```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        // Arrange a mocked service response that matches the API contract shape.
        ProjectResponse response = new ProjectResponse();
        response.setId("project-100");
        response.setName("TaskFlow");
        response.setDescription("Project description");
        response.setCreatedDate(LocalDateTime.of(2026, 7, 5, 10, 15, 0));
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 10, 15, 0));

        given(projectService.createProject(any())).willReturn(response);

        // Act and assert the controller contract: status code, content type, and JSON body.
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
                .andExpect(jsonPath("$.id").value("project-100"))
                .andExpect(jsonPath("$.name").value("TaskFlow"))
                .andExpect(jsonPath("$.description").value("Project description"))
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T10:15:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T10:15:00"));

        // Capture the request forwarded to the service so request mapping is tested, not just response rendering.
        ArgumentCaptor<CreateProjectRequest> requestCaptor = ArgumentCaptor.forClass(CreateProjectRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("TaskFlow");
        assertThat(requestCaptor.getValue().getDescription()).isEqualTo("Project description");
    }
}
```

Change summary:
- Add a focused `@WebMvcTest` for the real `ProjectController`.
- Mock `ProjectService` with Mockito rather than introducing any fake service implementation.
- Cover the positive path required by API 1 in Milestone 4.
- Keep validation and error-response assertions out of this file because they belong to Milestone 6 and Milestone 7.

### 2. Create `TaskControllerTest.java`

#### Before
```java
// File does not exist.
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        TaskResponse response = buildTaskResponse();

        given(taskService.createTask(eq("project-100"), any())).willReturn(response);

        mockMvc.perform(post("/api/projects/project-100/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Write controller tests",
                                  "description": "Milestone 4 RED tests",
                                  "assigneeEmail": "owner@example.com",
                                  "dueDate": "2026-07-10",
                                  "status": "TODO"
                                }
                                """))
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
        assertThat(requestCaptor.getValue().getDueDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 200 OK with the updated task payload")
    void updateTaskAssigneeReturnsUpdatedTaskResponse() throws Exception {
        TaskResponse response = buildTaskResponse();
        response.setAssigneeEmail("updated.owner@example.com");
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 11, 30, 0));

        given(taskService.updateTaskAssignee(eq("task-200"), any())).willReturn(response);

        mockMvc.perform(patch("/api/tasks/task-200/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assigneeEmail": "updated.owner@example.com"
                                }
                                """))
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
        TaskResponse response = buildTaskResponse();
        response.setStatus(TaskStatus.IN_PROGRESS);
        response.setUpdatedDate(LocalDateTime.of(2026, 7, 5, 11, 45, 0));

        given(taskService.updateTaskStatus(eq("task-200"), any())).willReturn(response);

        mockMvc.perform(patch("/api/tasks/task-200/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
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
        given(taskService.getTasksByProjectId("project-100")).willReturn(List.of(buildTaskResponse()));

        mockMvc.perform(get("/api/projects/project-100/tasks"))
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
        given(taskService.getTasksByStatus(TaskStatus.TODO)).willReturn(List.of(buildTaskResponse()));

        mockMvc.perform(get("/api/tasks").param("status", "TODO"))
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
                .willReturn(List.of(buildTaskResponse()));

        mockMvc.perform(get("/api/tasks").param("assigneeEmail", "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].assigneeEmail").value("owner@example.com"));

        verify(taskService).getTasksByAssigneeEmail("owner@example.com");
    }

    @Test
    @DisplayName("GET /api/tasks without supported query parameters remains a negative controller contract case")
    void getTasksWithoutRequiredFilterRemainsNegativeCaseForControllerPlanning() throws Exception {
        // This negative case belongs in Milestone 4 because the controller must not silently accept
        // an unsupported unfiltered listing route when only status and assignee filters are approved.
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().is4xxClientError());
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

Change summary:
- Add a focused `@WebMvcTest` for the real `TaskController`.
- Cover positive controller paths for APIs 2 through 7.
- Include one Milestone 4 negative controller-routing case for unsupported unfiltered `GET /api/tasks` usage because the approved contract only allows status-filtered or assignee-filtered retrieval.
- Use Mockito argument capture to verify request-body and path/query parameter mapping.
- Keep validation-body failure assertions, not-found error-body assertions, and consistent exception payload assertions deferred to Milestone 6 and Milestone 7.

## Planned test coverage by API contract

| API Contract | Planned Test Method | Type |
| ------------ | ------------------- | ---- |
| `POST /api/projects` | `createProjectReturnsCreatedProjectResponse` | Positive |
| `POST /api/projects/{projectId}/tasks` | `createTaskReturnsCreatedTaskResponse` | Positive |
| `PATCH /api/tasks/{taskId}/assignee` | `updateTaskAssigneeReturnsUpdatedTaskResponse` | Positive |
| `PATCH /api/tasks/{taskId}/status` | `updateTaskStatusReturnsUpdatedTaskResponse` | Positive |
| `GET /api/projects/{projectId}/tasks` | `getTasksByProjectIdReturnsTaskList` | Positive |
| `GET /api/tasks?status=TODO` | `getTasksByStatusReturnsTaskList` | Positive |
| `GET /api/tasks?assigneeEmail=owner@example.com` | `getTasksByAssigneeEmailReturnsTaskList` | Positive |
| `GET /api/tasks` without approved filter | `getTasksWithoutRequiredFilterRemainsNegativeCaseForControllerPlanning` | Negative |

## Negative cases intentionally deferred to later milestones

The following cases are required by the overall Version 1 scope but should not be implemented in Milestone 4 because `Plan.md` separates controller contract RED tests from validation and error-handling RED tests:

- Missing project `name` on project creation.
- Missing task `title` on task creation.
- Invalid `assigneeEmail` format on task creation, assignee update, or assignee filtering.
- Past `dueDate` on task creation.
- Unsupported `status` on task creation, status update, or status filtering.
- Missing project or task references that should return `404 Not Found`.
- Consistent error response body assertions for `errorCode`, `message`, and `context`.
- `405 Method Not Allowed` contract coverage.

These cases belong to Milestone 6 and Milestone 7 and should be listed there rather than pulled into Milestone 4 implementation.

## Out of scope

- Any change to `ProjectController` or `TaskController`.
- Any change to service interfaces, service implementations, DAO classes, DTO classes, model classes, or configuration.
- Any controller implementation code required to make the tests pass.
- Any validation annotation, validator, exception class, or global exception handler.
- Any repository, persistence, or integration test work.
- Any changes to `docs/.ai/Plan.md`.
- Any mock implementation classes; Mockito mocks only.

## Success criteria

Milestone 4 implementation is complete when:

1. `ProjectControllerTest` and `TaskControllerTest` exist under the standard controller test package.
2. The tests target the real `ProjectController` and `TaskController` classes with `@WebMvcTest`.
3. The service layer is mocked with Mockito, with no fake or handwritten mock implementations.
4. The tests cover all approved positive controller contract paths for APIs 1 through 7.
5. The tests include the approved Milestone 4 negative controller-routing case for unsupported unfiltered `GET /api/tasks`.
6. Validation and error-handling negative cases remain deferred to Milestone 6 and Milestone 7.
7. The tests are expected to fail initially because the controller endpoint methods are not yet implemented.
8. No production source file is changed as part of Milestone 4 implementation.
