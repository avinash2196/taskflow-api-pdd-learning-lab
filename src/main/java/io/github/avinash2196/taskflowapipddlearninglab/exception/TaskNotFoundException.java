package io.github.avinash2196.taskflowapipddlearninglab.exception;

/**
 * Signals that the requested task identifier does not exist.
 */
public class TaskNotFoundException extends RuntimeException {

    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
