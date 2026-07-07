package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * Carries request body data for updating an existing task assignee.
 */
public class UpdateTaskAssigneeRequest {

    /** Replacement assignee email from the API request body. */
    @NotNull(message = "Assignee email is required.")
    @Email(message = "Assignee email must be a valid email address.")
    private String assigneeEmail;

    public UpdateTaskAssigneeRequest() {
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }
}
