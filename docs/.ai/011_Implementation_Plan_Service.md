# Milestone 11 Implementation Plan: Service Implementation

## Milestone description

Milestone 11 is the GREEN phase for service behavior defined in `docs/.ai/Plan.md`. The scope is limited to implementing only the business behavior required to make the existing Milestone 10 service tests pass while keeping persistence concerns behind the existing DAO contracts.

Current state review based on the repository before Milestone 11 implementation:

- `ProjectServiceImpl` exists and `createProject(...)` still throws `UnsupportedOperationException`.
- `TaskServiceImpl` exists and all task methods still throw `UnsupportedOperationException`.
- `ProjectDao` and `TaskDao` already define the repository contracts the service layer must use in this milestone.
- `ProjectServiceImplTest` covers project request-to-domain mapping and persisted domain-to-response mapping.
- `TaskServiceImplTest` covers task creation, default `TODO` status, assignee update, status update, list-by-project, list-by-status, list-by-assignee, and not-found paths.
- The current `TaskServiceImplTest` uses `ProjectDao` stubbing in test cases, but `setUp()` still constructs `TaskServiceImpl` with only `TaskDao`. That constructor wiring must be aligned during implementation so the service can use both repository contracts required by the approved tests. This is a setup-only correction, not a test behavior change.
- Controller validation and error-response formatting were already handled in earlier milestones, so this milestone must not add new validation rules or HTTP concerns beyond what the service tests already require.

Milestone 11 should therefore implement only these approved service responsibilities:

- map request DTOs into domain models before DAO persistence
- default task status to `TODO` when omitted on task creation
- verify project existence before task creation and task lookup by project
- verify task existence before assignee and status updates
- map persisted or retrieved domain models back into response DTOs
- delegate task queries to the DAO layer and map results

No repository implementation, no new validation behavior, and no non-approved business rules are part of this milestone.

## Files to be updated/changed

Planned files for Milestone 11 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImplTest.java` - setup only, if the current constructor wiring still uses `new TaskServiceImpl(taskDao)`

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
16. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImplTest.java`
17. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/service/support/ServiceTestDataFactory.java`

No other files should be created or changed for Milestone 11 implementation.

## Exact code changes with comments

### 1. Update `ProjectServiceImpl.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation for the project service layer.
 * Business behavior is intentionally deferred to a later milestone.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectDao projectDao;

  /**
   * Creates the service with its required data access dependency.
   *
   * @param projectDao placeholder data access dependency
   */
  public ProjectServiceImpl(ProjectDao projectDao) {
    this.projectDao = projectDao;
  }

  @Override
  public ProjectResponse createProject(CreateProjectRequest request) {
    throw new UnsupportedOperationException("Project service implementation is not available yet.");
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

Planned change notes:

- Replace the placeholder exception with the smallest implementation required by the service tests.
- Keep the service limited to business mapping and DAO delegation.
- Add one private mapper method only because it keeps the class readable without introducing an extra abstraction not required by this milestone.
- Do not add timestamp generation, uniqueness checks, or extra validation because those behaviors are not defined by Milestone 11.

### 2. Update `TaskServiceImpl.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Placeholder implementation for the task service layer.
 * Business behavior is intentionally deferred to a later milestone.
 */
@Service
public class TaskServiceImpl implements TaskService {

  private final TaskDao taskDao;

  /**
   * Creates the service with its required data access dependency.
   *
   * @param taskDao placeholder data access dependency
   */
  public TaskServiceImpl(TaskDao taskDao) {
    this.taskDao = taskDao;
  }

  @Override
  public TaskResponse createTask(String projectId, CreateTaskRequest request) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
  }

  @Override
  public TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
  }

  @Override
  public TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
  }

  @Override
  public List<TaskResponse> getTasksByProjectId(String projectId) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
  }

  @Override
  public List<TaskResponse> getTasksByStatus(TaskStatus status) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
  }

  @Override
  public List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail) {
    throw new UnsupportedOperationException("Task service implementation is not available yet.");
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

Planned change notes:

- Replace the placeholder exceptions with the smallest implementation required by the service tests.
- Add `ProjectDao` to the task service because project existence checks are explicitly required by Plan.md and exercised by the service tests.
- Keep the class limited to business rules and DTO/domain mapping:
  - require project existence where the tests expect it
  - require task existence for update flows
  - default missing status to `TaskStatus.TODO`
  - preserve existing task fields during partial updates
  - delegate persistence and queries to DAO contracts
  - map domain objects to response DTOs
- Use private helper methods only for repeated business logic and response mapping so the service stays readable and focused.
- Do not add due-date validation, email-format validation, status-transition restrictions, timestamp mutation logic, or repository-specific behavior because none of those are required by Milestone 11.

### 3. Update `TaskServiceImplTest.java` setup only if constructor wiring still mismatches

#### Before changes
```java
@BeforeEach
void setUp() {
    taskService = new TaskServiceImpl(taskDao);
}
```

#### After changes
```java
@BeforeEach
void setUp() {
    taskService = new TaskServiceImpl(projectDao, taskDao);
}
```

Planned change notes:

- This is allowed by the prompt because it is a setup-only change needed to keep the RED tests aligned with the approved service dependency contract.
- No assertion, test scenario, or expected behavior should change.
- Apply this only if the repository still has the one-argument constructor setup when Milestone 11 implementation begins.

### 4. Verification after implementation

#### Before changes
```text
Service implementations compile, but service tests remain RED because the production methods still throw UnsupportedOperationException.
```

#### After changes
```text
Run the Maven test suite after service implementation to confirm:
- production code compiles
- ProjectServiceImplTest passes
- TaskServiceImplTest passes
- previously green controller tests remain green
```

Planned change notes:

- Verification is part of this plan because the prompt explicitly requires compilation and passing tests after implementation.
- The implementation should stop once the existing tests are green and should not extend behavior beyond what those tests require.

## Out of scope

- Any repository implementation, JPA mapping, or database behavior
- Any controller, DTO, exception-handler, or API contract changes
- Any new validation rules in the service layer beyond what existing tests require
- Any changes to service tests other than the constructor setup alignment noted above, if needed
- Any timestamp generation rules, ID generation rules, or persistence defaults not defined by the mocked DAO responses
- Any new helper classes, mapper classes, utility classes, or configuration changes
- Any refactor work reserved for Milestone 12
- Any `Plan.md` milestone status update

## Success criteria

Milestone 11 implementation is complete when:

1. `ProjectServiceImpl#createProject(...)` maps the request into a `Project`, saves it through `ProjectDao`, and returns a correctly mapped `ProjectResponse`.
2. `TaskServiceImpl#createTask(...)` verifies the project exists, defaults missing status to `TaskStatus.TODO`, saves the mapped task through `TaskDao`, and returns a correctly mapped `TaskResponse`.
3. `TaskServiceImpl#updateTaskAssignee(...)` loads the existing task, updates only the assignee field required by the request, saves it, and returns the persisted response.
4. `TaskServiceImpl#updateTaskStatus(...)` loads the existing task, updates only the status field required by the request, saves it, and returns the persisted response.
5. `TaskServiceImpl#getTasksByProjectId(...)`, `getTasksByStatus(...)`, and `getTasksByAssigneeEmail(...)` delegate to the DAO contract and map each returned task to `TaskResponse`.
6. Missing project references throw `ProjectNotFoundException` and missing task references throw `TaskNotFoundException` in the service flows already covered by Milestone 10 tests.
7. No production code outside `ProjectServiceImpl.java` and `TaskServiceImpl.java` is changed, except setup-only alignment in `TaskServiceImplTest.java` if required for constructor wiring.
8. The code compiles and the full Maven test suite passes after implementation.
9. The service layer contains only business rules and mapping required to turn the existing tests green, with no extra validations or repository logic added.
