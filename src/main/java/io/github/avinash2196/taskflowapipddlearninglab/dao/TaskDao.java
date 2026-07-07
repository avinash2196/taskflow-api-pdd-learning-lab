package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for task persistence operations required by Version 1 services.
 * This repository exposes only storage-oriented query methods needed by the approved tests.
 */
public interface TaskDao extends JpaRepository<Task, String> {

    /**
     * Loads stored tasks by owning project identifier.
     *
     * @param projectId project identifier stored on the task row
     * @return all tasks for the provided project identifier
     */
    List<Task> findByProjectId(String projectId);

    /**
     * Loads stored tasks by workflow status.
     *
     * @param status stored task status value
     * @return all tasks matching the provided status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Loads stored tasks by assignee email.
     *
     * @param assigneeEmail stored assignee email value
     * @return all tasks assigned to the provided email
     */
    List<Task> findByAssigneeEmail(String assigneeEmail);
}
