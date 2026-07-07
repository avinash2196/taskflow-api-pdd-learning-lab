package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles task API endpoints by mapping HTTP inputs to the existing task service contract.
 */
@RestController
@Validated
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param taskService service contract used by task endpoints
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a task under the provided project identifier.
     *
     * @param projectId owning project identifier from the URL path
     * @param request request body mapped from the incoming JSON payload
     * @return created task payload returned by the service layer
     */
    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable String projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    /**
     * Updates the assignee email for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the assignee email
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/api/tasks/{taskId}/assignee")
    public TaskResponse updateTaskAssignee(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskAssigneeRequest request) {
        return taskService.updateTaskAssignee(taskId, request);
    }

    /**
     * Updates the workflow status for an existing task.
     *
     * @param taskId task identifier from the URL path
     * @param request request body containing the new status
     * @return updated task payload returned by the service layer
     */
    @PatchMapping("/api/tasks/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }

    /**
     * Returns all tasks for a specific project.
     *
     * @param projectId project identifier from the URL path
     * @return list of tasks returned by the service layer
     */
    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProjectId(@PathVariable String projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    /**
     * Returns tasks filtered by status using the approved status query parameter.
     *
     * @param status status filter from the query string
     * @return list of tasks returned by the service layer
     */
    @GetMapping(value = "/api/tasks", params = "status")
    public List<TaskResponse> getTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    /**
     * Returns tasks filtered by assignee email using the approved assignee query parameter.
     *
     * @param assigneeEmail assignee email from the query string
     * @return list of tasks returned by the service layer
     */
    @GetMapping(value = "/api/tasks", params = "assigneeEmail")
    public List<TaskResponse> getTasksByAssigneeEmail(
            @RequestParam
            @Email(message = "Assignee email must be a valid email address.")
            String assigneeEmail) {
        return taskService.getTasksByAssigneeEmail(assigneeEmail);
    }
}
