package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.TaskResponse;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for task business operations.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final ProjectDao projectDao;
    private final TaskDao taskDao;

    public TaskServiceImpl(ProjectDao projectDao, TaskDao taskDao) {
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    @Override
    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        requireProject(projectId);
        return saveAndMap(toTask(projectId, request));
    }

    @Override
    public TaskResponse updateTaskAssignee(String taskId, UpdateTaskAssigneeRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setAssigneeEmail(request.getAssigneeEmail());
        return saveAndMap(existingTask);
    }

    @Override
    public TaskResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Task existingTask = requireTask(taskId);
        existingTask.setStatus(request.getStatus());
        return saveAndMap(existingTask);
    }

    @Override
    public List<TaskResponse> getTasksByProjectId(String projectId) {
        requireProject(projectId);
        return mapTasks(taskDao.findByProjectId(projectId));
    }

    @Override
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return mapTasks(taskDao.findByStatus(status));
    }

    @Override
    public List<TaskResponse> getTasksByAssigneeEmail(String assigneeEmail) {
        return mapTasks(taskDao.findByAssigneeEmail(assigneeEmail));
    }

    /**
     * Builds a new task entity for create flow while keeping default-status logic in one place.
     */
    private Task toTask(String projectId, CreateTaskRequest request) {
        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeEmail(request.getAssigneeEmail());
        task.setDueDate(request.getDueDate());
        task.setStatus(resolveStatus(request));
        return task;
    }

    /**
     * Resolves task status for create flow without changing approved default behavior.
     */
    private TaskStatus resolveStatus(CreateTaskRequest request) {
        return request.getStatus() == null ? TaskStatus.TODO : request.getStatus();
    }

    /**
     * Persists the task and maps the saved entity to the response contract.
     */
    private TaskResponse saveAndMap(Task task) {
        return toTaskResponse(taskDao.save(task));
    }

    /**
     * Maps repository query results once so list methods stay focused on business intent.
     */
    private List<TaskResponse> mapTasks(List<Task> tasks) {
        return tasks.stream()
                .map(this::toTaskResponse)
                .toList();
    }

    private void requireProject(String projectId) {
        projectDao.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    /**
     * Loads the existing task required by update operations.
     */
    private Task requireTask(String taskId) {
        return taskDao.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    /**
     * Builds the response used by create, update, and query operations.
     */
    private TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setProjectId(task.getProjectId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setAssigneeEmail(task.getAssigneeEmail());
        response.setDueDate(task.getDueDate());
        response.setCreatedDate(task.getCreatedDate());
        response.setUpdatedDate(task.getUpdatedDate());
        return response;
    }
}
