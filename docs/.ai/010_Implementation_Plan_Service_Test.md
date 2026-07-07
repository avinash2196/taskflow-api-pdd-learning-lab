# Milestone 10 Implementation Plan: Service Tests Using Mocked Repository Contract

## Milestone description

Milestone 10 is the RED phase for service-level behavior defined in `docs/.ai/Plan.md`. The scope is limited to planning service test classes and test-only support code that define the expected business behavior for `ProjectServiceImpl` and `TaskServiceImpl` while using Mockito mocks for the real DAO interfaces created in Milestone 9.

Current state review based on the repository before Milestone 10 implementation:

- `ProjectService` and `TaskService` already define the approved Version 1 service contract.
- `ProjectServiceImpl` and `TaskServiceImpl` already exist, but every service method currently throws `UnsupportedOperationException`.
- `ProjectDao` and `TaskDao` now exist as interface-only repository contracts and are the correct dependencies to mock in service tests.
- The repository currently has controller tests only. There are no service test classes and no service test support helpers under `src/test/java`.
- DTOs, domain models, status enum, and not-found exceptions already exist and should be reused by the planned tests instead of introducing placeholder types.

Milestone 10 planning should therefore add failing unit tests that target the existing service implementation classes directly, mock only the DAO layer, and cover the main positive and negative service flows required by Version 1. No production code changes are part of this milestone plan.

## Files to be updated/changed

Planned files for Milestone 10 implementation:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImplTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImplTest.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/support/ServiceTestDataFactory.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`
9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/CreateProjectRequest.java`
10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/ProjectResponse.java`
11. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/CreateTaskRequest.java`
12. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/TaskResponse.java`
13. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskAssigneeRequest.java`
14. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskStatusRequest.java`
15. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/ProjectNotFoundException.java`
16. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/TaskNotFoundException.java`

No other files should be created or changed for Milestone 10 implementation.

## Exact code changes with comments

### 1. Add `ProjectServiceImplTest.java`

#### Before changes
```java
// File does not exist.
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.service.support.ServiceTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * RED-phase unit tests for project service behavior.
 * These tests define how the service maps project requests to DAO calls and response payloads.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

  @Mock
  private ProjectDao projectDao;

  private ProjectServiceImpl projectService;

  @BeforeEach
  void setUp() {
    projectService = new ProjectServiceImpl(projectDao);
  }

  @Test
  @DisplayName("createProject saves a mapped project and returns the persisted response")
  void createProjectSavesMappedProjectAndReturnsResponse() {
    CreateProjectRequest request = ServiceTestDataFactory.createProjectRequest(
            "TaskFlow",
            "Project description");
    Project persistedProject = ServiceTestDataFactory.project(
            "project-100",
            "TaskFlow",
            "Project description",
            "2026-07-05T10:15:00",
            "2026-07-05T10:15:00");

    given(projectDao.save(org.mockito.ArgumentMatchers.any(Project.class))).willReturn(persistedProject);

    ProjectResponse response = projectService.createProject(request);

    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    verify(projectDao).save(projectCaptor.capture());
    assertThat(projectCaptor.getValue().getId()).isNull();
    assertThat(projectCaptor.getValue().getName()).isEqualTo("TaskFlow");
    assertThat(projectCaptor.getValue().getDescription()).isEqualTo("Project description");

    assertThat(response.getId()).isEqualTo("project-100");
    assertThat(response.getName()).isEqualTo("TaskFlow");
    assertThat(response.getDescription()).isEqualTo("Project description");
    assertThat(response.getCreatedDate()).isEqualTo(persistedProject.getCreatedDate());
    assertThat(response.getUpdatedDate()).isEqualTo(persistedProject.getUpdatedDate());
  }

  @Test
  @DisplayName("createProject keeps optional description null when the request omits it")
  void createProjectKeepsOptionalDescriptionNull() {
    CreateProjectRequest request = ServiceTestDataFactory.createProjectRequest("TaskFlow Lite", null);
    Project persistedProject = ServiceTestDataFactory.project(
            "project-101",
            "TaskFlow Lite",
            null,
            "2026-07-05T12:00:00",
            "2026-07-05T12:00:00");

    given(projectDao.save(org.mockito.ArgumentMatchers.any(Project.class))).willReturn(persistedProject);

    ProjectResponse response = projectService.createProject(request);

    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    verify(projectDao).save(projectCaptor.capture());
    assertThat(projectCaptor.getValue().getDescription()).isNull();
    assertThat(response.getDescription()).isNull();
  }
}
```

Planned change notes:

- Use `ProjectServiceImpl` directly instead of mocking the service interface because Milestone 10 verifies service behavior.
- Mock `ProjectDao` only, using the real DAO interface from Milestone 9.
- Verify request-to-domain mapping before persistence and domain-to-response mapping after persistence.
- Keep coverage focused on approved Version 1 project service behavior:
  - successful project creation with description
  - successful project creation without description
- Do not add validation tests here because controller validation already covers request-shape validation and Plan.md limits Milestone 10 to service behavior against mocked repository contracts.

### 2. Add `TaskServiceImplTest.java`

#### Before changes
```java
// File does not exist.
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.support.ServiceTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * RED-phase unit tests for task service behavior.
 * These tests define the expected service orchestration across project and task DAO contracts.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock
  private ProjectDao projectDao;

  @Mock
  private TaskDao taskDao;

  private TaskServiceImpl taskService;

  @BeforeEach
  void setUp() {
    taskService = new TaskServiceImpl(projectDao, taskDao);
  }

  @Test
  @DisplayName("createTask saves a task for an existing project and returns the persisted response")
  void createTaskSavesTaskForExistingProject() {
    Project existingProject = ServiceTestDataFactory.project("project-100", "TaskFlow", "Project description");
    CreateTaskRequest request = ServiceTestDataFactory.createTaskRequest(
            "Write service tests",
            "Milestone 10 RED tests",
            "owner@example.com",
            "2026-07-10",
            TaskStatus.IN_PROGRESS);
    Task persistedTask = ServiceTestDataFactory.task(
            "task-200",
            "project-100",
            "Write service tests",
            "Milestone 10 RED tests",
            TaskStatus.IN_PROGRESS,
            "owner@example.com",
            "2026-07-10",
            "2026-07-05T11:00:00",
            "2026-07-05T11:00:00");

    given(projectDao.findById("project-100")).willReturn(Optional.of(existingProject));
    given(taskDao.save(org.mockito.ArgumentMatchers.any(Task.class))).willReturn(persistedTask);

    TaskResponse response = taskService.createTask("project-100", request);

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskDao).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getId()).isNull();
    assertThat(taskCaptor.getValue().getProjectId()).isEqualTo("project-100");
    assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Write service tests");
    assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    assertThat(response.getId()).isEqualTo("task-200");
    assertThat(response.getProjectId()).isEqualTo("project-100");
    assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
  }

  @Test
  @DisplayName("createTask defaults status to TODO when request status is omitted")
  void createTaskDefaultsStatusToTodo() {
    given(projectDao.findById("project-100"))
            .willReturn(Optional.of(ServiceTestDataFactory.project("project-100", "TaskFlow", null)));
    given(taskDao.save(org.mockito.ArgumentMatchers.any(Task.class)))
            .willReturn(ServiceTestDataFactory.taskWithDefaultTodoStatus("task-201", "project-100"));

    TaskResponse response = taskService.createTask(
            "project-100",
            ServiceTestDataFactory.createTaskRequest("Write service tests", null, null, null, null));

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskDao).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
    assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
  }

  @Test
  @DisplayName("createTask throws ProjectNotFoundException when the project does not exist")
  void createTaskThrowsProjectNotFoundWhenProjectDoesNotExist() {
    given(projectDao.findById("project-999")).willReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.createTask(
            "project-999",
            ServiceTestDataFactory.createTaskRequest("Write service tests", null, null, null, null)))
            .isInstanceOf(ProjectNotFoundException.class)
            .hasMessageContaining("project-999");

    verify(taskDao, never()).save(org.mockito.ArgumentMatchers.any(Task.class));
  }

  @Test
  @DisplayName("updateTaskAssignee saves the updated task and preserves existing task state")
  void updateTaskAssigneeSavesUpdatedTask() {
    given(taskDao.findById("task-200"))
            .willReturn(Optional.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));
    given(taskDao.save(org.mockito.ArgumentMatchers.any(Task.class)))
            .willReturn(ServiceTestDataFactory.taskWithUpdatedAssignee(
                    "task-200",
                    "project-100",
                    "updated.owner@example.com",
                    "2026-07-05T11:30:00"));

    TaskResponse response = taskService.updateTaskAssignee(
            "task-200",
            ServiceTestDataFactory.updateTaskAssigneeRequest("updated.owner@example.com"));

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskDao).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getId()).isEqualTo("task-200");
    assertThat(taskCaptor.getValue().getAssigneeEmail()).isEqualTo("updated.owner@example.com");
    assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Write service tests");
    assertThat(response.getAssigneeEmail()).isEqualTo("updated.owner@example.com");
  }

  @Test
  @DisplayName("updateTaskAssignee throws TaskNotFoundException when the task does not exist")
  void updateTaskAssigneeThrowsTaskNotFoundWhenTaskDoesNotExist() {
    given(taskDao.findById("task-999")).willReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.updateTaskAssignee(
            "task-999",
            ServiceTestDataFactory.updateTaskAssigneeRequest("owner@example.com")))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("task-999");
  }

  @Test
  @DisplayName("updateTaskStatus saves the updated task and returns the persisted response")
  void updateTaskStatusSavesUpdatedTask() {
    given(taskDao.findById("task-200"))
            .willReturn(Optional.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));
    given(taskDao.save(org.mockito.ArgumentMatchers.any(Task.class)))
            .willReturn(ServiceTestDataFactory.taskWithUpdatedStatus(
                    "task-200",
                    "project-100",
                    TaskStatus.DONE,
                    "2026-07-05T11:45:00"));

    TaskResponse response = taskService.updateTaskStatus(
            "task-200",
            ServiceTestDataFactory.updateTaskStatusRequest(TaskStatus.DONE));

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskDao).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.DONE);
    assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Write service tests");
    assertThat(response.getStatus()).isEqualTo(TaskStatus.DONE);
  }

  @Test
  @DisplayName("updateTaskStatus throws TaskNotFoundException when the task does not exist")
  void updateTaskStatusThrowsTaskNotFoundWhenTaskDoesNotExist() {
    given(taskDao.findById("task-999")).willReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.updateTaskStatus(
            "task-999",
            ServiceTestDataFactory.updateTaskStatusRequest(TaskStatus.BLOCKED)))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("task-999");
  }

  @Test
  @DisplayName("getTasksByProjectId verifies project existence and returns mapped tasks")
  void getTasksByProjectIdReturnsMappedTasks() {
    given(projectDao.findById("project-100"))
            .willReturn(Optional.of(ServiceTestDataFactory.project("project-100", "TaskFlow", null)));
    given(taskDao.findByProjectId("project-100"))
            .willReturn(List.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));

    List<TaskResponse> response = taskService.getTasksByProjectId("project-100");

    verify(taskDao).findByProjectId("project-100");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).getProjectId()).isEqualTo("project-100");
    assertThat(response.get(0).getId()).isEqualTo("task-200");
  }

  @Test
  @DisplayName("getTasksByProjectId throws ProjectNotFoundException when the project does not exist")
  void getTasksByProjectIdThrowsProjectNotFoundWhenProjectDoesNotExist() {
    given(projectDao.findById("project-999")).willReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTasksByProjectId("project-999"))
            .isInstanceOf(ProjectNotFoundException.class)
            .hasMessageContaining("project-999");

    verify(taskDao, never()).findByProjectId("project-999");
  }

  @Test
  @DisplayName("getTasksByStatus returns mapped tasks from the DAO filter")
  void getTasksByStatusReturnsMappedTasks() {
    given(taskDao.findByStatus(TaskStatus.TODO))
            .willReturn(List.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));

    List<TaskResponse> response = taskService.getTasksByStatus(TaskStatus.TODO);

    verify(taskDao).findByStatus(TaskStatus.TODO);
    assertThat(response).hasSize(1);
    assertThat(response.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
  }

  @Test
  @DisplayName("getTasksByAssigneeEmail returns mapped tasks from the DAO filter")
  void getTasksByAssigneeEmailReturnsMappedTasks() {
    given(taskDao.findByAssigneeEmail("owner@example.com"))
            .willReturn(List.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));

    List<TaskResponse> response = taskService.getTasksByAssigneeEmail("owner@example.com");

    verify(taskDao).findByAssigneeEmail("owner@example.com");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).getAssigneeEmail()).isEqualTo("owner@example.com");
  }
}
```

Planned change notes:

- Instantiate `TaskServiceImpl` directly and mock `ProjectDao` plus `TaskDao`.
- Cover the approved task service success paths:
  - create task for an existing project
  - default task status to `TODO` when omitted
  - update assignee
  - update status
  - list by project
  - list by status
  - list by assignee
- Cover the required failure paths:
  - create task when project is missing
  - update assignee when task is missing
  - update status when task is missing
  - list by project when project is missing
- Keep negative coverage aligned to Plan.md and the API contract. Do not add service tests for invalid email, invalid enum parsing, or past due date because those are request validation concerns already handled before the service layer receives typed DTO values.
- The planned constructor usage assumes `TaskServiceImpl` will be updated in Milestone 11 to accept both `ProjectDao` and `TaskDao`, because the repository contract in Milestone 9 requires project existence checks for task creation and list-by-project behavior.

### 3. Add `ServiceTestDataFactory.java`

#### Before changes
```java
// File does not exist.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.service.support;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared service-test fixtures.
 * Keeps request and model setup centralized so service tests stay focused on behavior.
 */
public final class ServiceTestDataFactory {

    private ServiceTestDataFactory() {
    }

    public static CreateProjectRequest createProjectRequest(String name, String description) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    public static Project project(
            String id,
            String name,
            String description,
            String createdDateTime,
            String updatedDateTime) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setCreatedDate(LocalDateTime.parse(createdDateTime));
        project.setUpdatedDate(LocalDateTime.parse(updatedDateTime));
        return project;
    }

    public static Project project(String id, String name, String description) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        return project;
    }

    public static CreateTaskRequest createTaskRequest(
            String title,
            String description,
            String assigneeEmail,
            String dueDate,
            TaskStatus status) {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setAssigneeEmail(assigneeEmail);
        request.setDueDate(dueDate == null ? null : LocalDate.parse(dueDate));
        request.setStatus(status);
        return request;
    }

    public static UpdateTaskAssigneeRequest updateTaskAssigneeRequest(String assigneeEmail) {
        UpdateTaskAssigneeRequest request = new UpdateTaskAssigneeRequest();
        request.setAssigneeEmail(assigneeEmail);
        return request;
    }

    public static UpdateTaskStatusRequest updateTaskStatusRequest(TaskStatus status) {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(status);
        return request;
    }

    public static Task task(
            String id,
            String projectId,
            String title,
            String description,
            TaskStatus status,
            String assigneeEmail,
            String dueDate,
            String createdDateTime,
            String updatedDateTime) {
        Task task = new Task();
        task.setId(id);
        task.setProjectId(projectId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setAssigneeEmail(assigneeEmail);
        task.setDueDate(dueDate == null ? null : LocalDate.parse(dueDate));
        task.setCreatedDate(LocalDateTime.parse(createdDateTime));
        task.setUpdatedDate(LocalDateTime.parse(updatedDateTime));
        return task;
    }

    public static Task defaultTask(String taskId, String projectId) {
        return task(
                taskId,
                projectId,
                "Write service tests",
                "Milestone 10 RED tests",
                TaskStatus.TODO,
                "owner@example.com",
                "2026-07-10",
                "2026-07-05T11:00:00",
                "2026-07-05T11:00:00");
    }

    public static Task taskWithDefaultTodoStatus(String taskId, String projectId) {
        return defaultTask(taskId, projectId);
    }

    public static Task taskWithUpdatedAssignee(
            String taskId,
            String projectId,
            String assigneeEmail,
            String updatedDateTime) {
        Task task = defaultTask(taskId, projectId);
        task.setAssigneeEmail(assigneeEmail);
        task.setUpdatedDate(LocalDateTime.parse(updatedDateTime));
        return task;
    }

    public static Task taskWithUpdatedStatus(
            String taskId,
            String projectId,
            TaskStatus status,
            String updatedDateTime) {
        Task task = defaultTask(taskId, projectId);
        task.setStatus(status);
        task.setUpdatedDate(LocalDateTime.parse(updatedDateTime));
        return task;
    }
}
```

Planned change notes:

- Centralize service test fixture creation for requests and domain objects.
- Keep the helper test-scoped and narrow to Milestone 10 needs.
- Reuse real DTOs, models, and enums only. Do not introduce fake repository types or placeholder domain types.

## Out of scope

- Any modification to `src/main/java` production service or DAO files during Milestone 10
- Any repository implementation, JPA mapping, database behavior, or integration wiring
- Any controller changes, controller test changes, or global exception handling changes
- Any new validation annotations, request parsing behavior, or API contract expansion
- Any service refactor work beyond what is necessary to express RED tests
- Any changes to `pom.xml`, application configuration, or build plugins
- Any Plan.md milestone status update
- Any future milestone work from Milestone 11, 12, 13, 14, 15, or 16

## Success criteria

Milestone 10 implementation is complete when:

1. `ProjectServiceImplTest` and `TaskServiceImplTest` are added as failing unit tests that target the concrete service implementation classes directly.
2. Mockito is used to mock only `ProjectDao` and `TaskDao`, with no mock implementation classes introduced.
3. Project service tests cover successful project creation with and without optional description.
4. Task service tests cover successful task creation, default `TODO` status behavior, assignee update, status update, list-by-project, list-by-status, and list-by-assignee behavior.
5. Task service tests cover the not-found failure cases for missing project references and missing task references required by Version 1.
6. The planned tests use the real DTO, model, enum, and exception classes already present in the repository.
7. The service tests remain in RED state against the current placeholder service implementations until Milestone 11 is implemented.
8. Test code remains readable in preview mode, follows standard Java naming conventions, and includes clear class and method comments describing purpose and usage.
