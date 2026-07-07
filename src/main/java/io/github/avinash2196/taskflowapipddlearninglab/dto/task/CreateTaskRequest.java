package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Carries request body data for task creation under an existing project.
 */
public class CreateTaskRequest {

    /** Required task title from the API request body. */
    @NotBlank(message = "Task title is required.")
    private String title;

    /** Optional task description from the API request body. */
    private String description;

    /** Optional assignee email from the API request body. */
    @Email(message = "Assignee email must be a valid email address.")
    private String assigneeEmail;

    /** Optional due date from the API request body. */
    @FutureOrPresent(message = "Due date must be today or a future date.")
    private LocalDate dueDate;

    /** Optional initial task status from the API request body. */
    private TaskStatus status;

    public CreateTaskRequest() {
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
