package io.github.avinash2196.taskflowapipddlearninglab.exception;

/**
 * Signals that the requested project identifier does not exist.
 */
public class ProjectNotFoundException extends RuntimeException {

    private final String projectId;

    public ProjectNotFoundException(String projectId) {
        super("Project not found: " + projectId);
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
