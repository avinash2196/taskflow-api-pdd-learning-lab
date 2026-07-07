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

    public static final String DEFAULT_PROJECT_ID = "project-100";
    public static final String DEFAULT_TASK_ID = "task-200";
    public static final String DEFAULT_PROJECT_NAME = "TaskFlow";
    public static final String DEFAULT_PROJECT_DESCRIPTION = "Project description";
    public static final String DEFAULT_TASK_TITLE = "Write service tests";
    public static final String DEFAULT_TASK_DESCRIPTION = "Milestone 10 RED tests";
    public static final String DEFAULT_ASSIGNEE_EMAIL = "owner@example.com";
    public static final String DEFAULT_DUE_DATE = "2026-07-10";

    private ServiceTestDataFactory() {
    }

    public static CreateProjectRequest createDefaultProjectRequest() {
        return createProjectRequest(DEFAULT_PROJECT_NAME, DEFAULT_PROJECT_DESCRIPTION);
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

    public static CreateTaskRequest createDefaultTaskRequest(TaskStatus status) {
        return createTaskRequest(
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                DEFAULT_ASSIGNEE_EMAIL,
                DEFAULT_DUE_DATE,
                status);
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
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                TaskStatus.TODO,
                DEFAULT_ASSIGNEE_EMAIL,
                DEFAULT_DUE_DATE,
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
