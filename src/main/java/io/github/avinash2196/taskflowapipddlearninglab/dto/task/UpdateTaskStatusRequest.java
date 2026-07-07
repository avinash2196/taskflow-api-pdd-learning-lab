package io.github.avinash2196.taskflowapipddlearninglab.dto.task;

import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Carries request body data for updating an existing task workflow status.
 */
public class UpdateTaskStatusRequest {

    /** Replacement task status from the API request body. */
    @NotNull(message = "Task status is required.")
    private TaskStatus status;

    public UpdateTaskStatusRequest() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
