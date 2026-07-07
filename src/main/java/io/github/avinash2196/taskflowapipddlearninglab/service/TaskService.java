package io.github.avinash2196.taskflowapipddlearninglab.service;

import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;

import java.util.List;

/**
 * Defines task-related business operations required by the Version 1 API contract.
 * This contract maps each approved task API to a controller-facing service method.
 */
public interface TaskService {

    /**
     * Creates a new task under an existing project.
     *
     * @param projectId identifies the owning project from the request path
     * @param request carries task creation inputs from the request body
     * @return the created task response payload defined by the API contract
     */
    TaskResponse createTask(String projectId, CreateTaskRequest request);

    /**
     * Updates the assignee email for an existing task.
     *
     * @param taskId identifies the task from the request path
     * @param request carries the replacement assignee email
     * @return the updated task response payload
     */
    TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request);

    /**
     * Updates the workflow status for an existing task.
     *
     * @param taskId identifies the task from the request path
     * @param request carries the replacement task status
     * @return the updated task response payload
     */
    TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request);

    /**
     * Returns all tasks that belong to a specific project.
     *
     * @param projectId identifies the project from the request path
     * @return tasks stored under the specified project
     */
    List<TaskResponse> getTasksByProjectId(String projectId);

    /**
     * Returns all tasks that match a single status filter.
     *
     * @param status identifies the requested task status filter
     * @return tasks matching the provided status
     */
    List<TaskResponse> getTasksByStatus(TaskStatus status);

    /**
     * Returns all tasks assigned to the provided email address.
     *
     * @param assigneeEmail identifies the requested assignee filter
     * @return tasks matching the provided assignee email
     */
    List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail);
}
