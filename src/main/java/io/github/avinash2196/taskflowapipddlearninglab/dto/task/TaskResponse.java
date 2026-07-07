package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Returns the task payload defined by the Version 1 task success contracts.
 * The same response shape is reused across create, update, and list operations.
 */
public class TaskResponse {

    /** System-generated task identifier returned to the API consumer. */
    private String id;

    /** Identifier of the owning project returned to the API consumer. */
    private String projectId;

    /** Required task title returned to the API consumer. */
    private String title;

    /** Optional task description returned to the API consumer. */
    private String description;

    /** Current workflow status returned to the API consumer. */
    private TaskStatus status;

    /** Optional assignee email returned to the API consumer. */
    private String assigneeEmail;

    /** Optional due date returned to the API consumer. */
    private LocalDate dueDate;

    /** Task creation timestamp returned to the API consumer. */
    private LocalDateTime createdDate;

    /** Task last update timestamp returned to the API consumer. */
    private LocalDateTime updatedDate;

    public TaskResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
