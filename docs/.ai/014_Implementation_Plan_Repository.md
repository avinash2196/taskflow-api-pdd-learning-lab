# Milestone 14 Implementation Plan: Data Access Implementation

## Milestone description

Milestone 14 is the GREEN phase for data access behavior defined in `docs/.ai/Plan.md`. The scope is limited to implementing only the repository and persistence changes required to make the existing Milestone 13 repository tests pass while keeping repository classes focused on data extraction and persistence behavior only.

Current state review based on the repository before Milestone 14 implementation:

- `ProjectDaoTest` and `TaskDaoTest` already define the approved repository behavior for save, lookup, and filter operations.
- `ProjectDao` and `TaskDao` currently exist as interface-only contracts and do not provide a Spring Data repository bean implementation.
- `Project` and `Task` are still plain domain classes and do not yet contain JPA mapping metadata required by `@DataJpaTest`.
- `Task` stores `projectId` as a scalar field, and the repository tests assert filtering by that value rather than by an object relationship.
- `spring-boot-starter-data-jpa` and `h2` are already present in `pom.xml`, so no dependency change is required for this milestone.
- Service and controller milestones are already green, so this plan must not introduce validation, business rules, controller logic, or service behavior beyond what the repository tests require.

Milestone 14 implementation should therefore introduce only the minimum persistence mapping and Spring Data repository wiring needed for these approved repository behaviors:

- save and reload `Project`
- save and reload `Task`
- update existing `Project` and `Task` rows through `save(...)`
- query `Task` by `projectId`
- query `Task` by `status`
- query `Task` by `assigneeEmail`
- preserve created timestamp on update and refresh updated timestamp on each save

No additional validation, relationship navigation logic, cascade behavior, custom SQL, or integration-test work is part of this milestone.

## Files to be updated/changed

Planned files for Milestone 14 implementation:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Project.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/Task.java`

Reviewed but intentionally not changed in this milestone:

1. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model/TaskStatus.java`
2. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
4. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDaoTest.java`
5. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDaoTest.java`
6. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/dao/support/RepositoryTestDataFactory.java`
7. `pom.xml`

No other files should be created or changed for Milestone 14 implementation.

## Exact code changes with comments

### 1. Update `ProjectDao.java`

#### Before changes

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

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for project persistence operations required by Version 1 services.
 * This repository remains limited to storage access and does not contain business logic.
 */
public interface ProjectDao extends JpaRepository<Project, String> {
}
```

Planned change notes:

- Convert the existing contract into a Spring Data JPA repository so `@DataJpaTest` can create the real bean required by Milestone 13 tests.
- Keep the repository surface minimal because inherited `save(...)` and `findById(...)` already satisfy the approved tests.
- Do not add custom query methods, default methods, or repository-side validation because they are not required by the current tests.

### 2. Update `TaskDao.java`

#### Before changes

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

#### After changes

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for task persistence operations required by Version 1 services.
 * This repository exposes only storage-oriented query methods needed by the approved tests.
 */
public interface TaskDao extends JpaRepository<Task, String> {

    /**
     * Loads stored tasks by owning project identifier.
     *
     * @param projectId project identifier stored on the task row
     * @return all tasks for the provided project identifier
     */
    List<Task> findByProjectId(String projectId);

    /**
     * Loads stored tasks by workflow status.
     *
     * @param status stored task status value
     * @return all tasks matching the provided status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Loads stored tasks by assignee email.
     *
     * @param assigneeEmail stored assignee email value
     * @return all tasks assigned to the provided email
     */
    List<Task> findByAssigneeEmail(String assigneeEmail);
}
```

Planned change notes:

- Convert the existing contract into a Spring Data JPA repository to provide the repository bean and inherited CRUD behavior needed by the tests.
- Keep only the explicit derived query methods already defined by Milestone 9 and exercised by Milestone 13 tests.
- Do not add extra filters, sorting, pagination, or custom JPQL because none of those behaviors are approved for this milestone.

### 3. Update `Project.java`

#### Before changes
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

    public Project() {
    }

    // getters and setters
}
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents stored project data required by Version 1.
 * The entity keeps persistence metadata local to the model while business rules stay in higher layers.
 */
@Entity
@Table(name = "projects")
public class Project {

    /** System-generated identifier used as the project business identity. */
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    /** Required project name supplied by the API consumer. */
    @Column(nullable = false)
    private String name;

    /** Optional project description supplied by the API consumer. */
    @Column
    private String description;

    /** Timestamp captured when the project is first created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /** Timestamp captured when the project is last changed. */
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    public Project() {
    }

    /**
     * Assigns generated persistence values when a new project is first stored.
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdDate == null) {
            createdDate = now;
        }
        updatedDate = now;
    }

    /**
     * Refreshes the update timestamp whenever an existing project row is changed.
     */
    @PreUpdate
    void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // existing getters and setters remain unchanged
}
```

Planned change notes:

- Add only the JPA annotations required for persistence and test execution.
- Use a local lifecycle callback to generate the string identifier and timestamps because the tests expect repository-generated values and the current model uses `String` IDs.
- Preserve the existing field names and getters/setters so service and controller code remain unchanged.
- Do not add equals/hashCode, builders, validation annotations, or relationship collections because those changes are not required by Milestone 14 tests.

### 4. Update `Task.java`

#### Before changes
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

    public Task() {
    }

    // getters and setters
}
```

#### After changes
```java
package io.github.avinash2196.taskflowapipddlearninglab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents stored task data required by Version 1.
 * The entity keeps persistence mapping local while business behavior stays in the service layer.
 */
@Entity
@Table(name = "tasks")
public class Task {

    /** System-generated identifier used as the task business identity. */
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    /** Identifier of the project that owns this task. */
    @Column(nullable = false)
    private String projectId;

    /** Required task title supplied by the API consumer. */
    @Column(nullable = false)
    private String title;

    /** Optional task description supplied by the API consumer. */
    @Column
    private String description;

    /** Current workflow status for the task. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /** Optional email address of the user currently assigned to the task. */
    @Column
    private String assigneeEmail;

    /** Optional due date that must be today or in the future when provided. */
    @Column
    private LocalDate dueDate;

    /** Timestamp captured when the task is first created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /** Timestamp captured when the task is last changed. */
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    public Task() {
    }

    /**
     * Assigns generated persistence values when a new task is first stored.
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdDate == null) {
            createdDate = now;
        }
        updatedDate = now;
    }

    /**
     * Refreshes the update timestamp whenever an existing task row is changed.
     */
    @PreUpdate
    void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // existing getters and setters remain unchanged
}
```

Planned change notes:

- Add only the JPA metadata required to persist the current task shape used by the tests.
- Persist `TaskStatus` as `STRING` so the stored values stay aligned with the approved API enum names and repository query semantics.
- Keep `projectId` as a scalar column because the current tests and service layer operate on the project identifier directly; no object relationship is needed to make Milestone 13 tests pass.
- Use lifecycle callbacks for generated ID and timestamps to satisfy the repository test assertions without moving business logic into the repository interface.

### 5. Verification after implementation

#### Before changes
```text
Repository tests remain RED because no repository bean implementation or JPA entity mapping exists yet.
```

#### After changes
```text
Run the Maven test suite after repository implementation to confirm:
- production code compiles with JPA repository types and entity annotations
- ProjectDaoTest passes
- TaskDaoTest passes
- previously green controller and service tests remain green
```

Planned change notes:

- Verification is part of this plan because the prompt explicitly requires compilation and passing tests after implementation.
- Implementation should stop once the existing repository tests and current suite are green.

## Out of scope

- Any controller, DTO, exception-handler, or service-layer behavior changes
- Any changes to repository test assertions or test scenarios
- Any validation rules for email, due date, status transitions, or request formatting
- Any custom repository implementation classes, JDBC code, native SQL, or query optimization work
- Any explicit JPA relationship mapping between `Task` and `Project`
- Any schema migration tooling, seed data, or profile-specific configuration changes
- Any new pagination, sorting, search, or filtering behavior
- Any refactor work outside the minimum repository implementation needed to turn current tests green
- Any `Plan.md` milestone completion update beyond recording this implementation plan work

## Success criteria

Milestone 14 implementation is complete when:

1. `ProjectDao` is implemented as a Spring Data JPA repository bean that supports the existing `save(...)` and `findById(...)` repository tests.
2. `TaskDao` is implemented as a Spring Data JPA repository bean that supports the existing `save(...)`, `findById(...)`, `findByProjectId(...)`, `findByStatus(...)`, and `findByAssigneeEmail(...)` repository tests.
3. `Project` persists with generated string ID, stored name and optional description, created timestamp, and updated timestamp.
4. `Task` persists with generated string ID, stored `projectId`, title, optional description, `TaskStatus`, optional assignee email, optional due date, created timestamp, and updated timestamp.
5. Saving an existing `Project` or `Task` preserves `createdDate`, keeps the same identifier, and refreshes `updatedDate` so the update tests pass.
6. Repository code remains limited to persistence behavior and data extraction only, with no controller logic, service logic, or additional validation added.
7. No files outside `ProjectDao.java`, `TaskDao.java`, `Project.java`, and `Task.java` are changed during implementation.
8. The code compiles and the full Maven test suite passes after implementation.
