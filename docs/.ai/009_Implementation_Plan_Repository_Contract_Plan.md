# Milestone 9 Implementation Plan: Repository Contract

## Milestone description

Milestone 9 is the repository contract milestone defined in `docs/.ai/Plan.md`. The scope is limited to defining interface-only data access contracts that the service layer can mock in Milestone 10 and use in later implementation milestones. No repository behavior, persistence logic, controller changes, service logic, or configuration changes are included in this milestone.

Current state review based on the existing repository:

- `ProjectService` and `TaskService` already define the approved Version 1 service surface.
- `ProjectServiceImpl` currently depends on `ProjectDao`.
- `TaskServiceImpl` currently depends on `TaskDao`.
- `ProjectDao` and `TaskDao` currently exist as placeholder `@Repository` classes with no methods.
- The current package name for data access is `io.github.avinash2196.taskflowapipddlearninglab.dao`, so Milestone 9 should stay within that package instead of introducing a new package structure.

Milestone 9 implementation should convert the current placeholder DAO classes into repository-style interfaces so the service layer in Milestone 10 can mock explicit contracts without introducing persistence implementation.

## Files to be updated/changed

Planned files for Milestone 9 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/TaskStatus.java`

No other files should be created or changed for Milestone 9 implementation.

## Exact code changes with comments

### 1. Update `ProjectDao.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import org.springframework.stereotype.Repository;

/**
 * Placeholder data access component for project persistence concerns.
 * Data access methods are intentionally deferred to a later milestone.
 */
@Repository
public class ProjectDao {
  // No data access methods are defined in this milestone.
}
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Project;

import java.util.Optional;

/**
 * Repository contract for project persistence operations required by Version 1 services.
 * This interface defines only the data access methods that later service tests will mock.
 */
public interface ProjectDao {

  /**
   * Persists a project aggregate for the create project use case.
   *
   * @param project carries the project domain data prepared by the service layer
   * @return the persisted project state including generated identifiers and timestamps
   */
  Project save(Project project);

  /**
   * Returns a project when the service needs to verify that a referenced project exists.
   *
   * @param projectId identifies the project referenced by task-oriented service operations
   * @return the matching project when found, otherwise an empty result
   */
  Optional<Project> findById(String projectId);
}
```

Planned change notes:

- Replace the placeholder class with an interface because Milestone 9 requires contract-only work.
- Remove the `@Repository` annotation at this stage because there is still no concrete bean implementation in scope for this milestone.
- Keep the contract limited to methods required by approved Version 1 service behavior:
  - `save(Project)` for project creation
  - `findById(String)` for project existence checks required by task creation and task listing by project
- Use `Optional<Project>` so the later service layer can express not-found behavior without inventing null-based rules.

### 2. Update `TaskDao.java`

#### Before changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import org.springframework.stereotype.Repository;

/**
 * Placeholder data access component for task persistence concerns.
 * Data access methods are intentionally deferred to a later milestone.
 */
@Repository
public class TaskDao {
  // No data access methods are defined in this milestone.
}
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract for task persistence operations required by Version 1 services.
 * This interface remains persistence-technology-neutral and contains no implementation logic.
 */
public interface TaskDao {

  /**
   * Persists a task aggregate for create and update service operations.
   *
   * @param task carries the task domain data prepared or modified by the service layer
   * @return the persisted task state after storage
   */
  Task save(Task task);

  /**
   * Returns a single task when the service needs to update an existing task.
   *
   * @param taskId identifies the task requested by the API consumer
   * @return the matching task when found, otherwise an empty result
   */
  Optional<Task> findById(String taskId);

  /**
   * Returns all tasks that belong to a specific project.
   *
   * @param projectId identifies the owning project
   * @return all stored tasks for the specified project
   */
  List<Task> findByProjectId(String projectId);

  /**
   * Returns all tasks that match a workflow status filter.
   *
   * @param status identifies the requested task status
   * @return all stored tasks with the specified status
   */
  List<Task> findByStatus(TaskStatus status);

  /**
   * Returns all tasks assigned to a specific email address.
   *
   * @param assigneeEmail identifies the requested assignee filter
   * @return all stored tasks assigned to the provided email address
   */
  List<Task> findByAssigneeEmail(String assigneeEmail);
}
```

Planned change notes:

- Replace the placeholder class with an interface so Milestone 10 can mock task persistence behavior directly.
- Remove the `@Repository` annotation because bean creation belongs to the later repository implementation milestone.
- Use one `save(Task)` contract for:
  - task creation
  - assignee update
  - status update
- Add `findById(String)` for task-not-found handling in task update flows.
- Add list-query methods that align exactly to the approved API filters:
  - `findByProjectId(String)`
  - `findByStatus(TaskStatus)`
  - `findByAssigneeEmail(String)`

## Planned service-to-repository mapping

| Service Contract | Planned Repository Contract |
| ---------------- | --------------------------- |
| `ProjectService#createProject(CreateProjectRequest request)` | `ProjectDao#save(Project project)` |
| `TaskService#createTask(String projectId, CreateTaskRequest request)` | `ProjectDao#findById(String projectId)` and `TaskDao#save(Task task)` |
| `TaskService#updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request)` | `TaskDao#findById(String taskId)` and `TaskDao#save(Task task)` |
| `TaskService#updateTaskStatus(String taskId, UpdateTaskStatusRequest request)` | `TaskDao#findById(String taskId)` and `TaskDao#save(Task task)` |
| `TaskService#getTasksByProjectId(String projectId)` | `ProjectDao#findById(String projectId)` and `TaskDao#findByProjectId(String projectId)` |
| `TaskService#getTasksByStatus(TaskStatus status)` | `TaskDao#findByStatus(TaskStatus status)` |
| `TaskService#getTasksByAssigneeEmail(String assigneeEmail)` | `TaskDao#findByAssigneeEmail(String assigneeEmail)` |

## Out of scope

- Any concrete repository or DAO implementation class
- Any JPA, JDBC, in-memory storage, H2 schema, or persistence framework wiring
- Any modification to controller classes, controller tests, validation behavior, or exception handling
- Any service implementation logic in `service/impl`
- Any model, DTO, or API contract changes
- Any configuration, dependency, or build file updates
- Any integration tests or service tests
- Any package rename from `da` to another package name

## Success criteria

Milestone 9 implementation is complete when:

1. `ProjectDao` and `TaskDao` are defined as interface-only repository contracts.
2. The repository contracts expose only the methods required by approved Version 1 service behavior.
3. `ProjectDao` supports project creation persistence and project lookup required by task-related service flows.
4. `TaskDao` supports task creation, task update lookup, and the three approved task retrieval filters.
5. No repository implementation, persistence annotation wiring, or storage behavior is introduced.
6. The planned repository contracts are suitable for mocking in Milestone 10 service tests.
7. The code remains readable, uses standard Java naming conventions, and includes purpose-focused class and method comments.
