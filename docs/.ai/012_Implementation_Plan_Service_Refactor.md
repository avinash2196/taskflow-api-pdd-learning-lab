# Milestone 12 Implementation Plan: Service Refactor

## Milestone description

Milestone 12 is the REFACTOR phase for the service layer defined in `docs/.ai/Plan.md`. The scope of this milestone is limited to internal cleanup that improves readability, maintainability, and small-scale execution efficiency without changing approved Version 1 behavior.

Current state review based on the repository before Milestone 12 implementation:

- `ProjectServiceImpl` and `TaskServiceImpl` are in GREEN state and pass the current Milestone 10 service tests.
- `ProjectServiceImpl` contains inline request-to-domain mapping and a private response mapper.
- `TaskServiceImpl` contains repeated save-and-map flow, repeated list-to-response mapping flow, a default-status expression embedded in `createTask(...)`, and private guard methods for project/task existence.
- `ProjectServiceImplTest` and `TaskServiceImplTest` are readable, but `TaskServiceImplTest` still contains repeated `ArgumentCaptor` setup and repeated response assertions that can be centralized into test-only helpers.
- `ServiceTestDataFactory` already centralizes test fixture creation and is the correct place to absorb additional shared test builders if refactor needs them.
- Existing service comments are partly milestone-oriented rather than usage-oriented. Milestone 12 should remove outdated comments and keep only comments that explain business-purpose or helper-purpose clearly.

Milestone 12 must preserve the TDD order required by `docs/.ai/Plan.md` and this prompt:

1. Refactor service production code first.
2. Verify existing service tests and full Maven tests still pass.
3. Refactor service tests and test helpers second.
4. Reverify service tests and the full Maven test suite again.

No new business rules, no controller changes, no DAO contract changes, and no repository implementation are part of this milestone.

## Files to be updated/changed

Planned files for Milestone 12 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImplTest.java`
4. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImplTest.java`
5. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/support/ServiceTestDataFactory.java`
6. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/support/ServiceTestAssertions.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/TaskStatus.java`
8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/CreateProjectRequest.java`
9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/project/ProjectResponse.java`
10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/CreateTaskRequest.java`
11. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/TaskResponse.java`
12. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskAssigneeRequest.java`
13. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto/task/UpdateTaskStatusRequest.java`
14. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/ProjectNotFoundException.java`
15. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception/TaskNotFoundException.java`

No other files should be created or changed for Milestone 12 implementation.

## Exact code changes with comments

### 1. Refactor `ProjectServiceImpl.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Implements project business rules required by Milestone 11 service tests.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    /**
     * Creates the service with the project repository contract.
     *
     * @param projectDao project persistence dependency used by the service
     */
    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        Project projectToSave = new Project();
        projectToSave.setName(request.getName());
        projectToSave.setDescription(request.getDescription());

        Project savedProject = projectDao.save(projectToSave);
        return toProjectResponse(savedProject);
    }

    /**
     * Maps a persisted project domain model to the API response contract.
     *
     * @param project persisted project returned by the repository
     * @return project response used by the controller layer
     */
    private ProjectResponse toProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedDate(project.getCreatedDate());
        response.setUpdatedDate(project.getUpdatedDate());
        return response;
    }
}
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for project business operations.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        return toProjectResponse(projectDao.save(toProject(request)));
    }

    /**
     * Builds the project entity used by the repository save operation.
     */
    private Project toProject(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return project;
    }

    /**
     * Builds the response returned to upper layers from the saved entity.
     */
    private ProjectResponse toProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedDate(project.getCreatedDate());
        response.setUpdatedDate(project.getUpdatedDate());
        return response;
    }
}
```

Planned change notes:

- Extract request-to-entity mapping into `toProject(...)` so `createProject(...)` reads as pure business orchestration.
- Keep mapping private to avoid introducing a new abstraction that only one service uses today.
- Replace milestone-specific class comments with usage-oriented comments.
- Remove comments that only describe implementation history instead of purpose.

### 2. Refactor `TaskServiceImpl.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements task business rules required by Milestone 11 service tests.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final ProjectDao projectDao;
    private final TaskDao taskDao;

    /**
     * Creates the service with repository contracts required by task business rules.
     *
     * @param projectDao project repository used for project existence checks
     * @param taskDao task repository used for task persistence and task queries
     */
    public TaskServiceImpl(ProjectDao projectDao, TaskDao taskDao) {
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    @Override
    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        requireProject(projectId);

        Task taskToSave = new Task();
        taskToSave.setProjectId(projectId);
        taskToSave.setTitle(request.getTitle());
        taskToSave.setDescription(request.getDescription());
        taskToSave.setAssigneeEmail(request.getAssigneeEmail());
        taskToSave.setDueDate(request.getDueDate());
        taskToSave.setStatus(request.getStatus() == null ? TaskStatus.TODO : request.getStatus());

        Task savedTask = taskDao.save(taskToSave);
        return toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setAssigneeEmail(request.getAssigneeEmail());

        Task savedTask = taskDao.save(existingTask);
        return toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setStatus(request.getStatus());

        Task savedTask = taskDao.save(existingTask);
        return toTaskResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getTasksByProjectId(String projectId) {
        requireProject(projectId);
        return taskDao.findByProjectId(projectId)
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskDao.findByStatus(status)
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail) {
        return taskDao.findByAssigneeEmail(assigneeEmail)
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    /**
     * Ensures the project required by the task operation exists.
     *
     * @param projectId project identifier referenced by the caller
     */
    private void requireProject(String projectId) {
        projectDao.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    /**
     * Loads the existing task required by update operations.
     *
     * @param taskId task identifier referenced by the caller
     * @return existing stored task
     */
    private Task requireTask(String taskId) {
        return taskDao.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    /**
     * Maps a task domain model to the shared task response contract.
     *
     * @param task persisted or queried task domain object
     * @return task response used by create, update, and list flows
     */
    private TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setProjectId(task.getProjectId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setAssigneeEmail(task.getAssigneeEmail());
        response.setDueDate(task.getDueDate());
        response.setCreatedDate(task.getCreatedDate());
        response.setUpdatedDate(task.getUpdatedDate());
        return response;
    }
}
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for task business operations.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final ProjectDao projectDao;
    private final TaskDao taskDao;

    public TaskServiceImpl(ProjectDao projectDao, TaskDao taskDao) {
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    @Override
    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        requireProject(projectId);
        return saveAndMap(toTask(projectId, request));
    }

    @Override
    public TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setAssigneeEmail(request.getAssigneeEmail());
        return saveAndMap(existingTask);
    }

    @Override
    public TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setStatus(request.getStatus());
        return saveAndMap(existingTask);
    }

    @Override
    public List<TaskResponse> getTasksByProjectId(String projectId) {
        requireProject(projectId);
        return mapTasks(taskDao.findByProjectId(projectId));
    }

    @Override
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return mapTasks(taskDao.findByStatus(status));
    }

    @Override
    public List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail) {
        return mapTasks(taskDao.findByAssigneeEmail(assigneeEmail));
    }

    /**
     * Builds a new task entity for create flow while keeping default-status logic in one place.
     */
    private Task toTask(String projectId, CreateTaskRequest request) {
        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeEmail(request.getAssigneeEmail());
        task.setDueDate(request.getDueDate());
        task.setStatus(resolveStatus(request));
        return task;
    }

    /**
     * Resolves task status for create flow without changing approved default behavior.
     */
    private TaskStatus resolveStatus(CreateTaskRequest request) {
        return request.getStatus() == null ? TaskStatus.TODO : request.getStatus();
    }

    /**
     * Persists the task and maps the saved entity to the response contract.
     */
    private TaskResponse saveAndMap(Task task) {
        return toTaskResponse(taskDao.save(task));
    }

    /**
     * Maps repository query results once so list methods stay focused on business intent.
     */
    private List<TaskResponse> mapTasks(List<Task> tasks) {
        return tasks.stream()
                .map(this::toTaskResponse)
                .toList();
    }

    private void requireProject(String projectId) {
        projectDao.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    private Task requireTask(String taskId) {
        return taskDao.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    /**
     * Builds the response used by create, update, and query operations.
     */
    private TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setProjectId(task.getProjectId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setAssigneeEmail(task.getAssigneeEmail());
        response.setDueDate(task.getDueDate());
        response.setCreatedDate(task.getCreatedDate());
        response.setUpdatedDate(task.getUpdatedDate());
        return response;
    }
}
```

Planned change notes:

- Extract entity construction into `toTask(...)` so `createTask(...)` expresses business flow clearly.
- Extract default-status decision into `resolveStatus(...)` so the `TODO` rule lives in one method.
- Extract repeated `taskDao.save(...)` plus response mapping into `saveAndMap(...)`.
- Extract repeated list stream mapping into `mapTasks(...)` to reduce duplicated stream pipelines.
- Keep `requireProject(...)` and `requireTask(...)` private because they are service-internal business guards.
- Remove comments that are milestone-history comments and retain comments that explain method purpose.
- This refactor improves readability and avoids duplicated response-mapping invocation logic without changing functionality.

### 3. Refactor `ProjectServiceImplTest.java`

#### Before changes
```java
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

    given(projectDao.save(any(Project.class))).willReturn(persistedProject);

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
```

#### After changes
```java
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

    given(projectDao.save(any(Project.class))).willReturn(persistedProject);

    ProjectResponse response = projectService.createProject(request);

    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    verify(projectDao).save(projectCaptor.capture());
    ServiceTestAssertions.assertProjectToSave(
            projectCaptor.getValue(),
            "TaskFlow",
            "Project description");
    ServiceTestAssertions.assertProjectResponse(response, persistedProject);
}
```

Planned change notes:

- Replace repeated low-level field assertions with a shared test assertion helper.
- Keep `ArgumentCaptor` local in the test so interaction verification remains visible.
- Preserve explicit scenario names and Mockito usage.

### 4. Refactor `TaskServiceImplTest.java`

#### Before changes
```java
ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
verify(taskDao).save(taskCaptor.capture());
assertThat(taskCaptor.getValue().getId()).isNull();
assertThat(taskCaptor.getValue().getProjectId()).isEqualTo("project-100");
assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Write service tests");
assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
assertThat(response.getId()).isEqualTo("task-200");
assertThat(response.getProjectId()).isEqualTo("project-100");
assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
```

#### After changes
```java
ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
verify(taskDao).save(taskCaptor.capture());
ServiceTestAssertions.assertTaskToSave(
        taskCaptor.getValue(),
        "project-100",
        "Write service tests",
        "Milestone 10 RED tests",
        "owner@example.com",
        "2026-07-10",
        TaskStatus.IN_PROGRESS);
ServiceTestAssertions.assertTaskResponse(response, persistedTask);
```

Planned change notes:

- Centralize repeated task field assertions into a shared service-test helper.
- Keep each test focused on one business behavior and one interaction path.
- Do not merge test scenarios or reduce coverage. This is a readability refactor only.

### 5. Extend `ServiceTestDataFactory.java`

#### Before changes
```java
public final class ServiceTestDataFactory {

    private ServiceTestDataFactory() {
    }

    public static CreateProjectRequest createProjectRequest(String name, String description) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    // existing project(...) and task(...) helpers
}
```

#### After changes
```java
public final class ServiceTestDataFactory {

    public static final String DEFAULT_PROJECT_ID = "project-100";
    public static final String DEFAULT_TASK_ID = "task-200";
    public static final String DEFAULT_PROJECT_NAME = "TaskFlow";
    public static final String DEFAULT_TASK_TITLE = "Write service tests";
    public static final String DEFAULT_TASK_DESCRIPTION = "Milestone 10 RED tests";
    public static final String DEFAULT_ASSIGNEE_EMAIL = "owner@example.com";
    public static final String DEFAULT_DUE_DATE = "2026-07-10";

    private ServiceTestDataFactory() {
    }

    public static CreateProjectRequest createDefaultProjectRequest() {
        return createProjectRequest(DEFAULT_PROJECT_NAME, "Project description");
    }

    public static CreateTaskRequest createDefaultTaskRequest(TaskStatus status) {
        return createTaskRequest(
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                DEFAULT_ASSIGNEE_EMAIL,
                DEFAULT_DUE_DATE,
                status);
    }

    // existing helper methods remain and are reused internally
}
```

Planned change notes:

- Promote repeated literals into named constants to improve readability and reduce accidental divergence across tests.
- Add optional default builders only where they remove real duplication in the current service tests.
- Keep the helper test-scoped and narrow; do not convert it into a generic utility class.

### 6. Add `ServiceTestAssertions.java`

#### Before changes
```java
// File does not exist.
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.service.support;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared assertion helpers for service-layer unit tests.
 */
public final class ServiceTestAssertions {

    private ServiceTestAssertions() {
    }

    public static void assertProjectToSave(Project project, String name, String description) {
        assertThat(project.getId()).isNull();
        assertThat(project.getName()).isEqualTo(name);
        assertThat(project.getDescription()).isEqualTo(description);
    }

    public static void assertProjectResponse(ProjectResponse response, Project project) {
        assertThat(response.getId()).isEqualTo(project.getId());
        assertThat(response.getName()).isEqualTo(project.getName());
        assertThat(response.getDescription()).isEqualTo(project.getDescription());
        assertThat(response.getCreatedDate()).isEqualTo(project.getCreatedDate());
        assertThat(response.getUpdatedDate()).isEqualTo(project.getUpdatedDate());
    }

    public static void assertTaskToSave(
            Task task,
            String projectId,
            String title,
            String description,
            String assigneeEmail,
            String dueDate,
            TaskStatus status) {
        assertThat(task.getId()).isNull();
        assertThat(task.getProjectId()).isEqualTo(projectId);
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getAssigneeEmail()).isEqualTo(assigneeEmail);
        assertThat(task.getDueDate()).isEqualTo(dueDate == null ? null : LocalDate.parse(dueDate));
        assertThat(task.getStatus()).isEqualTo(status);
    }

    public static void assertTaskResponse(TaskResponse response, Task task) {
        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getProjectId()).isEqualTo(task.getProjectId());
        assertThat(response.getTitle()).isEqualTo(task.getTitle());
        assertThat(response.getDescription()).isEqualTo(task.getDescription());
        assertThat(response.getStatus()).isEqualTo(task.getStatus());
        assertThat(response.getAssigneeEmail()).isEqualTo(task.getAssigneeEmail());
        assertThat(response.getDueDate()).isEqualTo(task.getDueDate());
        assertThat(response.getCreatedDate()).isEqualTo(task.getCreatedDate());
        assertThat(response.getUpdatedDate()).isEqualTo(task.getUpdatedDate());
    }
}
```

Planned change notes:

- This helper is justified because repeated project/task assertion blocks already exist in the current service tests.
- Keep the helper under `src/test/java` only.
- Use AssertJ consistently and do not introduce mock implementations.

### 7. Verification sequence after refactor

#### Before changes
```text
Service implementation and service tests are green, but there is no milestone-specific refactor verification sequence yet.
```

#### After changes
```text
Step 1: Refactor only production service files and run:
- mvn test

Step 2: Refactor only service test files and helpers and run:
- mvn test

Both verification runs must confirm:
- ProjectServiceImplTest passes
- TaskServiceImplTest passes
- earlier controller and validation tests remain green
- no approved Version 1 behavior changes
```

Planned change notes:

- The prompt explicitly requires service refactor first, verification second, then test refactor, then reverification.
- The implementation must stop once internal cleanup is complete and all tests remain green.

## Out of scope

- Any controller production or controller test changes
- Any DAO interface or repository implementation changes
- Any DTO, model, exception, or API contract changes
- Any new validation rules, status-transition rules, or persistence rules
- Any package restructuring outside the service test helper addition noted above
- Any mocking beyond Mockito-based DAO mocks already used in service tests
- Any production helper or utility class outside the two existing service implementations
- Any work planned for Milestone 13 or later

## Success criteria

Milestone 12 implementation is complete when:

1. `ProjectServiceImpl` and `TaskServiceImpl` are refactored for readability with shared private helper methods where duplication currently exists, without changing service behavior.
2. Service classes continue to contain business rules and orchestration only, with no repository implementation logic added.
3. Outdated or unnecessary milestone-history comments are removed, and remaining comments describe class, method, or helper purpose clearly.
4. After production service refactor, the full Maven test suite passes before any service test refactor begins.
5. `ProjectServiceImplTest` and `TaskServiceImplTest` are then refactored to remove repeated assertion code through test-only helpers while preserving scenario coverage and Mockito interaction checks.
6. `ServiceTestDataFactory` and the planned `ServiceTestAssertions` helper centralize repeated test-only setup and assertions without introducing fake implementations.
7. After service test refactor, the full Maven test suite passes again.
8. No actual functionality, externally visible behavior, or approved Version 1 scope changes are introduced.
