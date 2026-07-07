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
import io.github.avinash2196.taskflowapipddlearninglab.service.support.ServiceTestAssertions;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        Project existingProject = ServiceTestDataFactory.project(
                ServiceTestDataFactory.DEFAULT_PROJECT_ID,
                ServiceTestDataFactory.DEFAULT_PROJECT_NAME,
                ServiceTestDataFactory.DEFAULT_PROJECT_DESCRIPTION);
        CreateTaskRequest request = ServiceTestDataFactory.createDefaultTaskRequest(TaskStatus.IN_PROGRESS);
        Task persistedTask = ServiceTestDataFactory.task(
                ServiceTestDataFactory.DEFAULT_TASK_ID,
                ServiceTestDataFactory.DEFAULT_PROJECT_ID,
                ServiceTestDataFactory.DEFAULT_TASK_TITLE,
                ServiceTestDataFactory.DEFAULT_TASK_DESCRIPTION,
                TaskStatus.IN_PROGRESS,
                ServiceTestDataFactory.DEFAULT_ASSIGNEE_EMAIL,
                ServiceTestDataFactory.DEFAULT_DUE_DATE,
                "2026-07-05T11:00:00",
                "2026-07-05T11:00:00");

        given(projectDao.findById(ServiceTestDataFactory.DEFAULT_PROJECT_ID)).willReturn(Optional.of(existingProject));
        given(taskDao.save(any(Task.class))).willReturn(persistedTask);

        TaskResponse response = taskService.createTask(ServiceTestDataFactory.DEFAULT_PROJECT_ID, request);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).save(taskCaptor.capture());
        ServiceTestAssertions.assertTaskToSave(
                taskCaptor.getValue(),
                ServiceTestDataFactory.DEFAULT_PROJECT_ID,
                ServiceTestDataFactory.DEFAULT_TASK_TITLE,
                ServiceTestDataFactory.DEFAULT_TASK_DESCRIPTION,
                ServiceTestDataFactory.DEFAULT_ASSIGNEE_EMAIL,
                ServiceTestDataFactory.DEFAULT_DUE_DATE,
                TaskStatus.IN_PROGRESS);
        ServiceTestAssertions.assertTaskResponse(response, persistedTask);
    }

    @Test
    @DisplayName("createTask defaults status to TODO when request status is omitted")
    void createTaskDefaultsStatusToTodo() {
        given(projectDao.findById("project-100"))
                .willReturn(Optional.of(ServiceTestDataFactory.project("project-100", ServiceTestDataFactory.DEFAULT_PROJECT_NAME, null)));
        given(taskDao.save(any(Task.class)))
                .willReturn(ServiceTestDataFactory.taskWithDefaultTodoStatus("task-201", "project-100"));

        TaskResponse response = taskService.createTask(
                "project-100",
                ServiceTestDataFactory.createTaskRequest("Write service tests", null, null, null, null));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).save(taskCaptor.capture());
        ServiceTestAssertions.assertTaskToSave(
                taskCaptor.getValue(),
                "project-100",
                ServiceTestDataFactory.DEFAULT_TASK_TITLE,
                null,
                null,
                null,
                TaskStatus.TODO);
        ServiceTestAssertions.assertTaskResponse(
                response,
                ServiceTestDataFactory.taskWithDefaultTodoStatus("task-201", "project-100"));
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

        verify(taskDao, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskAssignee saves the updated task and preserves existing task state")
    void updateTaskAssigneeSavesUpdatedTask() {
        given(taskDao.findById("task-200"))
                .willReturn(Optional.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));
        given(taskDao.save(any(Task.class)))
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
        ServiceTestAssertions.assertExistingTaskToSave(
                taskCaptor.getValue(),
                "task-200",
                "project-100",
                ServiceTestDataFactory.DEFAULT_TASK_TITLE,
                ServiceTestDataFactory.DEFAULT_TASK_DESCRIPTION,
                "updated.owner@example.com",
                ServiceTestDataFactory.DEFAULT_DUE_DATE,
                TaskStatus.TODO);
        ServiceTestAssertions.assertTaskResponse(
                response,
                ServiceTestDataFactory.taskWithUpdatedAssignee(
                        "task-200",
                        "project-100",
                        "updated.owner@example.com",
                        "2026-07-05T11:30:00"));
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
        given(taskDao.save(any(Task.class)))
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
        ServiceTestAssertions.assertExistingTaskToSave(
                taskCaptor.getValue(),
                "task-200",
                "project-100",
                ServiceTestDataFactory.DEFAULT_TASK_TITLE,
                ServiceTestDataFactory.DEFAULT_TASK_DESCRIPTION,
                ServiceTestDataFactory.DEFAULT_ASSIGNEE_EMAIL,
                ServiceTestDataFactory.DEFAULT_DUE_DATE,
                TaskStatus.DONE);
        ServiceTestAssertions.assertTaskResponse(
                response,
                ServiceTestDataFactory.taskWithUpdatedStatus(
                        "task-200",
                        "project-100",
                        TaskStatus.DONE,
                        "2026-07-05T11:45:00"));
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
        ServiceTestAssertions.assertTaskResponse(
                response.get(0),
                ServiceTestDataFactory.defaultTask("task-200", "project-100"));
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
        ServiceTestAssertions.assertTaskResponse(
                response.get(0),
                ServiceTestDataFactory.defaultTask("task-200", "project-100"));
    }

    @Test
    @DisplayName("getTasksByAssigneeEmail returns mapped tasks from the DAO filter")
    void getTasksByAssigneeEmailReturnsMappedTasks() {
        given(taskDao.findByAssigneeEmail("owner@example.com"))
                .willReturn(List.of(ServiceTestDataFactory.defaultTask("task-200", "project-100")));

        List<TaskResponse> response = taskService.getTasksByAssigneeEmail("owner@example.com");

        verify(taskDao).findByAssigneeEmail("owner@example.com");
        assertThat(response).hasSize(1);
        ServiceTestAssertions.assertTaskResponse(
                response.get(0),
                ServiceTestDataFactory.defaultTask("task-200", "project-100"));
    }
}
