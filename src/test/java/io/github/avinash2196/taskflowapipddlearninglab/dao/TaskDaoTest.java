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
