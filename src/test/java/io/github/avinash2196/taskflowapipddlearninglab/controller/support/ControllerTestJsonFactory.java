package io.github.avinash2196.taskflowapipddlearninglab.controller.support;

/**
 * Shared JSON payload builders for controller tests.
 */
public final class ControllerTestJsonFactory {

    private ControllerTestJsonFactory() {
    }

    /**
     * Creates a project request payload.
     *
     * @param name optional project name
     * @param description optional project description
     * @return JSON payload string
     */
    public static String projectRequest(String name, String description) {
        return """
                {
                  "name": %s,
                  "description": %s
                }
                """.formatted(asJsonValue(name), asJsonValue(description));
    }

    /**
     * Creates a project payload without the name field.
     *
     * @param description optional project description
     * @return JSON payload string
     */
    public static String projectWithoutName(String description) {
        return """
                {
                  "description": %s
                }
                """.formatted(asJsonValue(description));
    }

    /**
     * Creates the default create-task request payload.
     *
     * @return JSON payload string
     */
    public static String defaultCreateTaskRequest() {
        return """
                {
                  "title": "Write controller tests",
                  "description": "Milestone 4 RED tests",
                  "assigneeEmail": "owner@example.com",
                  "dueDate": "2026-07-10",
                  "status": "TODO"
                }
                """;
    }

    /**
     * Creates a minimal create-task payload with only the title.
     *
     * @param title task title
     * @return JSON payload string
     */
    public static String minimalCreateTaskRequest(String title) {
        return """
                {
                  "title": %s
                }
                """.formatted(asJsonValue(title));
    }

    /**
     * Creates a create-task payload without the title field.
     *
     * @param description optional task description
     * @return JSON payload string
     */
    public static String createTaskWithoutTitle(String description) {
        return """
                {
                  "description": %s
                }
                """.formatted(asJsonValue(description));
    }

    /**
     * Creates a create-task payload with only an invalid or explicit assignee value.
     *
     * @param title task title
     * @param assigneeEmail assignee email value
     * @return JSON payload string
     */
    public static String createTaskWithAssignee(String title, String assigneeEmail) {
        return """
                {
                  "title": %s,
                  "assigneeEmail": %s
                }
                """.formatted(asJsonValue(title), asJsonValue(assigneeEmail));
    }

    /**
     * Creates a create-task payload with only an explicit due date.
     *
     * @param title task title
     * @param dueDate due date value
     * @return JSON payload string
     */
    public static String createTaskWithDueDate(String title, String dueDate) {
        return """
                {
                  "title": %s,
                  "dueDate": %s
                }
                """.formatted(asJsonValue(title), asJsonValue(dueDate));
    }

    /**
     * Creates a create-task payload with only an explicit status.
     *
     * @param title task title
     * @param status task status value
     * @return JSON payload string
     */
    public static String createTaskWithStatus(String title, String status) {
        return """
                {
                  "title": %s,
                  "status": %s
                }
                """.formatted(asJsonValue(title), asJsonValue(status));
    }

    /**
     * Creates an assignee update payload.
     *
     * @param assigneeEmail assignee email value
     * @return JSON payload string
     */
    public static String assigneeRequest(String assigneeEmail) {
        return """
                {
                  "assigneeEmail": %s
                }
                """.formatted(asJsonValue(assigneeEmail));
    }

    /**
     * Creates an empty JSON object payload.
     *
     * @return JSON payload string
     */
    public static String emptyObject() {
        return """
                {
                }
                """;
    }

    /**
     * Creates a status update payload.
     *
     * @param status task status value
     * @return JSON payload string
     */
    public static String statusRequest(String status) {
        return """
                {
                  "status": %s
                }
                """.formatted(asJsonValue(status));
    }

    private static String asJsonValue(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
}
