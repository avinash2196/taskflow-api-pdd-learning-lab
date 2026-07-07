package io.github.avinash2196.taskflowapipddlearninglab.service.support;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared assertion helpers for service-layer unit tests.
 */
public final class ServiceTestAssertions {

    private ServiceTestAssertions() {
    }

    public static void assertProjectToSave(Project project, String name, String description) {
        assertThat(project.getId()).isNull();
        assertThat(project.getName()).isEqualTo(name);
        assertThat(project.getDescription()).isEqualTo(description);
    }

    public static void assertProjectResponse(ProjectResponse response, Project project) {
        assertThat(response.getId()).isEqualTo(project.getId());
        assertThat(response.getName()).isEqualTo(project.getName());
        assertThat(response.getDescription()).isEqualTo(project.getDescription());
        assertThat(response.getCreatedDate()).isEqualTo(project.getCreatedDate());
        assertThat(response.getUpdatedDate()).isEqualTo(project.getUpdatedDate());
    }

    public static void assertTaskToSave(
            Task task,
            String projectId,
            String title,
            String description,
            String assigneeEmail,
            String dueDate,
            TaskStatus status) {
        assertThat(task.getId()).isNull();
        assertThat(task.getProjectId()).isEqualTo(projectId);
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getAssigneeEmail()).isEqualTo(assigneeEmail);
        assertThat(task.getDueDate()).isEqualTo(dueDate == null ? null : LocalDate.parse(dueDate));
        assertThat(task.getStatus()).isEqualTo(status);
    }

    public static void assertExistingTaskToSave(
            Task task,
            String taskId,
            String projectId,
            String title,
            String description,
            String assigneeEmail,
            String dueDate,
            TaskStatus status) {
        assertThat(task.getId()).isEqualTo(taskId);
        assertThat(task.getProjectId()).isEqualTo(projectId);
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getAssigneeEmail()).isEqualTo(assigneeEmail);
        assertThat(task.getDueDate()).isEqualTo(dueDate == null ? null : LocalDate.parse(dueDate));
        assertThat(task.getStatus()).isEqualTo(status);
    }

    public static void assertTaskResponse(TaskResponse response, Task task) {
        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getProjectId()).isEqualTo(task.getProjectId());
        assertThat(response.getTitle()).isEqualTo(task.getTitle());
        assertThat(response.getDescription()).isEqualTo(task.getDescription());
        assertThat(response.getStatus()).isEqualTo(task.getStatus());
        assertThat(response.getAssigneeEmail()).isEqualTo(task.getAssigneeEmail());
        assertThat(response.getDueDate()).isEqualTo(task.getDueDate());
        assertThat(response.getCreatedDate()).isEqualTo(task.getCreatedDate());
        assertThat(response.getUpdatedDate()).isEqualTo(task.getUpdatedDate());
    }
}
