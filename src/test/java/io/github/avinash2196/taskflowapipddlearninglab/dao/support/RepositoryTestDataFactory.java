package io.github.avinash2196.taskflowapipddlearninglab.dao.support;

import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;

/**
 * Shared repository-test fixtures.
 * Keeps project and task setup centralized so repository tests stay focused on persistence behavior.
 */
public final class RepositoryTestDataFactory {

    private RepositoryTestDataFactory() {
    }

    /**
     * Builds a new unsaved project for repository create and update scenarios.
     */
    public static Project newProject(String name, String description) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        return project;
    }

    /**
     * Builds a new unsaved task for repository create and query scenarios.
     */
    public static Task newTask(
            String projectId,
            String title,
            String description,
            TaskStatus status,
            String assigneeEmail,
            LocalDate dueDate) {
        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setAssigneeEmail(assigneeEmail);
        task.setDueDate(dueDate);
        return task;
    }
}
