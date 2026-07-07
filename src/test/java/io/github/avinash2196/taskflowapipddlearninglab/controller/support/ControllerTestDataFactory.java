package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared controller-test fixtures.
 */
public final class ControllerTestDataFactory {

    private ControllerTestDataFactory() {
    }

    /**
     * Creates a project response fixture with matching created and updated timestamps.
     *
     * @param id project identifier
     * @param name project name
     * @param description optional project description
     * @param timestamp ISO-8601 timestamp used for created and updated dates
     * @return populated project response fixture
     */
    public static ProjectResponse projectResponse(
            String id,
            String name,
            String description,
            String timestamp) {
        LocalDateTime dateTime = LocalDateTime.parse(timestamp);

        ProjectResponse response = new ProjectResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setCreatedDate(dateTime);
        response.setUpdatedDate(dateTime);
        return response;
    }

    /**
     * Creates the default task response fixture used by controller tests.
     *
     * @return populated default task response fixture
     */
    public static TaskResponse defaultTaskResponse() {
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

    /**
     * Creates a task response variant with a changed assignee and updated timestamp.
     *
     * @param taskId task identifier
     * @param assigneeEmail updated assignee email
     * @param updatedTimestamp ISO-8601 updated timestamp
     * @return populated task response fixture
     */
    public static TaskResponse taskResponseWithUpdatedAssignee(
            String taskId,
            String assigneeEmail,
            String updatedTimestamp) {
        TaskResponse response = defaultTaskResponse();
        response.setId(taskId);
        response.setAssigneeEmail(assigneeEmail);
        response.setUpdatedDate(LocalDateTime.parse(updatedTimestamp));
        return response;
    }

    /**
     * Creates a task response variant with a changed status and updated timestamp.
     *
     * @param taskId task identifier
     * @param status updated task status
     * @param updatedTimestamp ISO-8601 updated timestamp
     * @return populated task response fixture
     */
    public static TaskResponse taskResponseWithUpdatedStatus(
            String taskId,
            TaskStatus status,
            String updatedTimestamp) {
        TaskResponse response = defaultTaskResponse();
        response.setId(taskId);
        response.setStatus(status);
        response.setUpdatedDate(LocalDateTime.parse(updatedTimestamp));
        return response;
    }

    /**
     * Creates a minimal task response fixture that represents omitted optional input fields.
     *
     * @param taskId task identifier
     * @param projectId owning project identifier
     * @param title task title
     * @param timestamp ISO-8601 timestamp used for created and updated dates
     * @return populated minimal task response fixture
     */
    public static TaskResponse minimalTaskResponse(
            String taskId,
            String projectId,
            String title,
            String timestamp) {
        LocalDateTime dateTime = LocalDateTime.parse(timestamp);

        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setProjectId(projectId);
        response.setTitle(title);
        response.setDescription(null);
        response.setStatus(TaskStatus.TODO);
        response.setAssigneeEmail(null);
        response.setDueDate(null);
        response.setCreatedDate(dateTime);
        response.setUpdatedDate(dateTime);
        return response;
    }
}
