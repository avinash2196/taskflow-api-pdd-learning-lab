# Milestone 13 Implementation Plan: Repository Tests

## Milestone description

Milestone 13 is the RED phase for data access behavior defined in `docs/.ai/Plan.md`. The scope is limited to planning repository test classes and test-only support code that define the expected persistence behavior for the real `ProjectDao` and `TaskDao` contracts created in Milestone 9. No production repository implementation, entity mapping changes, schema changes, controller changes, or service changes are included in this milestone plan.

Current state review based on the repository before Milestone 13 implementation:

- `ProjectDao` and `TaskDao` currently exist as interface-only repository contracts in `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/da`.
- `ProjectServiceImpl` and `TaskServiceImpl` already depend on those DAO contracts and assume repository behavior exists for save and lookup operations.
- `Project` and `Task` are currently plain domain classes and do not yet contain persistence annotations or repository-backed implementation details.
- The project already includes `spring-boot-starter-data-jpa` and `h2`, so repository-slice test planning can stay aligned to the selected persistence stack without introducing a new testing approach.
- There are controller and service tests already, but there are no repository tests and no repository test support helpers under `src/test/java`.

Milestone 13 planning should therefore add failing repository tests that target the existing DAO contracts directly, define the persistence behavior required by Version 1, and remain red until Milestone 14 introduces the concrete data access implementation.

## Files to be updated/changed

Planned files for Milestone 13 implementation:

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDaoTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDaoTest.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/support/RepositoryTestDataFactory.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/TaskStatus.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
8. `pom.xml`

No other files should be created or changed for Milestone 13 implementation.

## Exact code changes with comments

### 1. Add `ProjectDaoTest.java`

#### Before changes
```java
// File does not exist.
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.dao.support.RepositoryTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED-phase repository tests for project persistence behavior.
 * These tests define the save and lookup behavior required by Version 1 services.
 */
@DataJpaTest
class ProjectDaoTest {

  @Autowired
  private ProjectDao projectDao;

  @Test
  @DisplayName("save persists a project and returns generated identity and timestamps")
  void savePersistsProjectAndReturnsGeneratedIdentityAndTimestamps() {
    Project project = RepositoryTestDataFactory.newProject(
            "TaskFlow",
            "Repository milestone project");

    Project savedProject = projectDao.save(project);

    assertThat(savedProject.getId()).isNotBlank();
    assertThat(savedProject.getName()).isEqualTo("TaskFlow");
    assertThat(savedProject.getDescription()).isEqualTo("Repository milestone project");
    assertThat(savedProject.getCreatedDate()).isNotNull();
    assertThat(savedProject.getUpdatedDate()).isNotNull();
  }

  @Test
  @DisplayName("save keeps optional description null when a project is created without one")
  void saveKeepsOptionalDescriptionNull() {
    Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow Lite", null));

    assertThat(savedProject.getId()).isNotBlank();
    assertThat(savedProject.getDescription()).isNull();
    assertThat(savedProject.getCreatedDate()).isNotNull();
    assertThat(savedProject.getUpdatedDate()).isNotNull();
  }

  @Test
  @DisplayName("findById returns the persisted project when it exists")
  void findByIdReturnsPersistedProjectWhenItExists() {
    Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject(
            "TaskFlow",
            "Existing project"));

    Optional<Project> loadedProject = projectDao.findById(savedProject.getId());

    assertThat(loadedProject).isPresent();
    assertThat(loadedProject.get().getId()).isEqualTo(savedProject.getId());
    assertThat(loadedProject.get().getName()).isEqualTo("TaskFlow");
    assertThat(loadedProject.get().getDescription()).isEqualTo("Existing project");
  }

  @Test
  @DisplayName("findById returns empty when the project does not exist")
  void findByIdReturnsEmptyWhenProjectDoesNotExist() {
    Optional<Project> loadedProject = projectDao.findById("missing-project-id");

    assertThat(loadedProject).isEmpty();
  }

  @Test
  @DisplayName("save updates an existing project and refreshes updatedDate without changing createdDate")
  void saveUpdatesExistingProjectAndRefreshesUpdatedDate() {
    Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject(
            "TaskFlow",
            "Original description"));

    savedProject.setDescription("Updated description");

    Project updatedProject = projectDao.save(savedProject);

    assertThat(updatedProject.getId()).isEqualTo(savedProject.getId());
    assertThat(updatedProject.getName()).isEqualTo("TaskFlow");
    assertThat(updatedProject.getDescription()).isEqualTo("Updated description");
    assertThat(updatedProject.getCreatedDate()).isEqualTo(savedProject.getCreatedDate());
    assertThat(updatedProject.getUpdatedDate()).isAfterOrEqualTo(updatedProject.getCreatedDate());
  }
}
```

Planned change notes:

- Use `@DataJpaTest` so Milestone 13 defines a repository-slice test boundary instead of mocking the DAO layer again.
- Autowire the real `ProjectDao` contract because Milestone 13 must target the actual repository contract already created.
- Cover positive persistence behavior required by Version 1:
  - create project with description
  - create project without description
  - load an existing project by ID
  - update an existing project
- Cover the negative repository lookup path required by the service layer:
  - `findById` returns empty for a missing project ID
- Keep the tests focused on repository behavior only. Do not add API validation assertions because those belong to controller validation milestones, not the data access layer.

### 2. Add `TaskDaoTest.java`

#### Before changes
```java
// File does not exist.
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.dao.support.RepositoryTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED-phase repository tests for task persistence behavior.
 * These tests define the create, update, and query behavior required by Version 1 services.
 */
@DataJpaTest
class TaskDaoTest {

  @Autowired
  private ProjectDao projectDao;

  @Autowired
  private TaskDao taskDao;

  @Test
  @DisplayName("save persists a task with all supported fields")
  void savePersistsTaskWithAllSupportedFields() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", "Parent project"));
    Task task = RepositoryTestDataFactory.newTask(
            project.getId(),
            "Write repository tests",
            "Cover DAO behavior",
            TaskStatus.IN_PROGRESS,
            "owner@example.com",
            LocalDate.now().plusDays(5));

    Task savedTask = taskDao.save(task);

    assertThat(savedTask.getId()).isNotBlank();
    assertThat(savedTask.getProjectId()).isEqualTo(project.getId());
    assertThat(savedTask.getTitle()).isEqualTo("Write repository tests");
    assertThat(savedTask.getDescription()).isEqualTo("Cover DAO behavior");
    assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    assertThat(savedTask.getAssigneeEmail()).isEqualTo("owner@example.com");
    assertThat(savedTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(5));
    assertThat(savedTask.getCreatedDate()).isNotNull();
    assertThat(savedTask.getUpdatedDate()).isNotNull();
  }

  @Test
  @DisplayName("save persists a task with optional fields omitted")
  void savePersistsTaskWithOptionalFieldsOmitted() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow Lite", null));
    Task savedTask = taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Draft DAO tests",
            null,
            TaskStatus.TODO,
            null,
            null));

    assertThat(savedTask.getId()).isNotBlank();
    assertThat(savedTask.getDescription()).isNull();
    assertThat(savedTask.getAssigneeEmail()).isNull();
    assertThat(savedTask.getDueDate()).isNull();
    assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.TODO);
  }

  @Test
  @DisplayName("findById returns the persisted task when it exists")
  void findByIdReturnsPersistedTaskWhenItExists() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));
    Task savedTask = taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Write repository tests",
            null,
            TaskStatus.TODO,
            null,
            null));

    Optional<Task> loadedTask = taskDao.findById(savedTask.getId());

    assertThat(loadedTask).isPresent();
    assertThat(loadedTask.get().getId()).isEqualTo(savedTask.getId());
    assertThat(loadedTask.get().getProjectId()).isEqualTo(project.getId());
    assertThat(loadedTask.get().getTitle()).isEqualTo("Write repository tests");
  }

  @Test
  @DisplayName("findById returns empty when the task does not exist")
  void findByIdReturnsEmptyWhenTaskDoesNotExist() {
    Optional<Task> loadedTask = taskDao.findById("missing-task-id");

    assertThat(loadedTask).isEmpty();
  }

  @Test
  @DisplayName("save updates assignee and status while preserving task identity")
  void saveUpdatesAssigneeAndStatusWhilePreservingTaskIdentity() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));
    Task savedTask = taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Write repository tests",
            "Initial description",
            TaskStatus.TODO,
            null,
            LocalDate.now().plusDays(3)));

    savedTask.setAssigneeEmail("updated.owner@example.com");
    savedTask.setStatus(TaskStatus.DONE);

    Task updatedTask = taskDao.save(savedTask);

    assertThat(updatedTask.getId()).isEqualTo(savedTask.getId());
    assertThat(updatedTask.getProjectId()).isEqualTo(project.getId());
    assertThat(updatedTask.getAssigneeEmail()).isEqualTo("updated.owner@example.com");
    assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.DONE);
    assertThat(updatedTask.getCreatedDate()).isEqualTo(savedTask.getCreatedDate());
    assertThat(updatedTask.getUpdatedDate()).isAfterOrEqualTo(updatedTask.getCreatedDate());
  }

  @Test
  @DisplayName("findByProjectId returns only tasks for the requested project")
  void findByProjectIdReturnsOnlyTasksForRequestedProject() {
    Project targetProject = projectDao.save(RepositoryTestDataFactory.newProject("Target project", null));
    Project otherProject = projectDao.save(RepositoryTestDataFactory.newProject("Other project", null));

    taskDao.save(RepositoryTestDataFactory.newTask(
            targetProject.getId(),
            "Target task one",
            null,
            TaskStatus.TODO,
            "owner@example.com",
            null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            targetProject.getId(),
            "Target task two",
            null,
            TaskStatus.BLOCKED,
            null,
            null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            otherProject.getId(),
            "Other task",
            null,
            TaskStatus.TODO,
            null,
            null));

    List<Task> tasks = taskDao.findByProjectId(targetProject.getId());

    assertThat(tasks).hasSize(2);
    assertThat(tasks).allMatch(task -> task.getProjectId().equals(targetProject.getId()));
  }

  @Test
  @DisplayName("findByProjectId returns an empty list when the project has no tasks")
  void findByProjectIdReturnsEmptyListWhenProjectHasNoTasks() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("Empty project", null));

    List<Task> tasks = taskDao.findByProjectId(project.getId());

    assertThat(tasks).isEmpty();
  }

  @Test
  @DisplayName("findByStatus returns only tasks matching the requested status")
  void findByStatusReturnsOnlyTasksMatchingRequestedStatus() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));

    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Todo task",
            null,
            TaskStatus.TODO,
            null,
            null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Done task",
            null,
            TaskStatus.DONE,
            null,
            null));

    List<Task> tasks = taskDao.findByStatus(TaskStatus.DONE);

    assertThat(tasks).hasSize(1);
    assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.DONE);
    assertThat(tasks.get(0).getTitle()).isEqualTo("Done task");
  }

  @Test
  @DisplayName("findByStatus returns an empty list when no tasks match the requested status")
  void findByStatusReturnsEmptyListWhenNoTasksMatchRequestedStatus() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Todo task",
            null,
            TaskStatus.TODO,
            null,
            null));

    List<Task> tasks = taskDao.findByStatus(TaskStatus.BLOCKED);

    assertThat(tasks).isEmpty();
  }

  @Test
  @DisplayName("findByAssigneeEmail returns only tasks assigned to the requested email")
  void findByAssigneeEmailReturnsOnlyTasksAssignedToRequestedEmail() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));

    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Assigned task one",
            null,
            TaskStatus.TODO,
            "owner@example.com",
            null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Assigned task two",
            null,
            TaskStatus.IN_PROGRESS,
            "owner@example.com",
            null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Different assignee task",
            null,
            TaskStatus.TODO,
            "other@example.com",
            null));

    List<Task> tasks = taskDao.findByAssigneeEmail("owner@example.com");

    assertThat(tasks).hasSize(2);
    assertThat(tasks).allMatch(task -> "owner@example.com".equals(task.getAssigneeEmail()));
  }

  @Test
  @DisplayName("findByAssigneeEmail returns an empty list when no tasks match the requested email")
  void findByAssigneeEmailReturnsEmptyListWhenNoTasksMatchRequestedEmail() {
    Project project = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow", null));
    taskDao.save(RepositoryTestDataFactory.newTask(
            project.getId(),
            "Assigned task",
            null,
            TaskStatus.TODO,
            "owner@example.com",
            null));

    List<Task> tasks = taskDao.findByAssigneeEmail("missing@example.com");

    assertThat(tasks).isEmpty();
  }
}
```

Planned change notes:

- Use `@DataJpaTest` and autowire both real DAO contracts because task repository behavior depends on persisted project context and the existing DAO surface.
- Cover the positive repository behaviors required by Version 1:
  - create task with all supported fields
  - create task with optional fields omitted
  - load task by ID
  - update task state through save
  - filter tasks by project
  - filter tasks by status
  - filter tasks by assignee email
- Cover the negative and empty-result behaviors required by the service layer and API contract:
  - `findById` returns empty for a missing task ID
  - `findByProjectId` returns an empty list when a project has no tasks
  - `findByStatus` returns an empty list when no tasks match
  - `findByAssigneeEmail` returns an empty list when no tasks match
- Avoid adding repository tests for invalid email format, invalid status strings, or past due date because those are request validation concerns already covered before persistence.

### 3. Add `RepositoryTestDataFactory.java`

#### Before changes
```java
// File does not exist.
```

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao.support;

import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;

/**
 * Shared repository-test fixtures.
 * Keeps project and task setup centralized so repository tests stay focused on persistence behavior.
 */
public final class RepositoryTestDataFactory {

  private RepositoryTestDataFactory() {
  }

  /**
   * Builds a new unsaved project for repository create and update scenarios.
   */
  public static Project newProject(String name, String description) {
    Project project = new Project();
    project.setName(name);
    project.setDescription(description);
    return project;
  }

  /**
   * Builds a new unsaved task for repository create and query scenarios.
   */
  public static Task newTask(
          String projectId,
          String title,
          String description,
          TaskStatus status,
          String assigneeEmail,
          LocalDate dueDate) {
    Task task = new Task();
    task.setProjectId(projectId);
    task.setTitle(title);
    task.setDescription(description);
    task.setStatus(status);
    task.setAssigneeEmail(assigneeEmail);
    task.setDueDate(dueDate);
    return task;
  }
}
```

Planned change notes:

- Centralize repository test fixture creation so the test classes remain readable in preview mode.
- Keep the helper test-scoped and narrow to Milestone 13 needs only.
- Reuse the real domain models and enum already present in the repository instead of introducing placeholder persistence types or fake DAO implementations.

## Out of scope

- Any modification to `src/main/java` production DAO, model, service, controller, config, or exception files during Milestone 13
- Any concrete repository implementation, Spring Data interface extension, entity annotation work, schema creation, or database seed logic
- Any controller, validation, or service test changes
- Any Mockito-based fake repository implementation classes
- Any `pom.xml` or dependency changes
- Any integration-test or full-application wiring work from Milestone 15
- Any Plan.md milestone completion update beyond the status-table note required by this planning task
- Any future milestone behavior outside repository test planning

## Success criteria

Milestone 13 implementation is complete when:

1. `ProjectDaoTest` and `TaskDaoTest` are added as failing repository-slice tests that target the real `ProjectDao` and `TaskDao` contracts directly.
2. `RepositoryTestDataFactory` is added as shared repository-test support code using the real domain models already present in the repository.
3. Project repository tests cover project save, optional description handling, lookup by ID, missing-project lookup, and update behavior.
4. Task repository tests cover task save with all supported fields, task save with optional fields omitted, lookup by ID, missing-task lookup, update behavior, list-by-project, list-by-status, and list-by-assignee behavior.
5. Repository tests cover the required empty-result paths for project, status, and assignee filters without inventing validation or API-level requirements.
6. The planned tests remain in RED state until Milestone 14 provides the concrete repository implementation and persistence mapping required for them to pass.
7. Test code remains readable in preview mode, follows standard Java naming conventions, and includes clear class and method comments describing purpose and usage.
