package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestAssertions;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestJsonFactory;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.CreateTaskRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskAssigneeRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.task.UpdateTaskStatusRequest;
import io.github.avinash2196.taskflowapipddlearninglab.exception.ProjectNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.exception.TaskNotFoundException;
import io.github.avinash2196.taskflowapipddlearninglab.model.TaskStatus;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-slice tests for task endpoints.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String PROJECT_TASKS_URL = "/api/projects/%s/tasks";
    private static final String TASK_ASSIGNEE_URL = "/api/tasks/%s/assignee";
    private static final String TASK_STATUS_URL = "/api/tasks/%s/status";
    private static final String TASKS_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 201 Created with the created task payload")
    void createTaskReturnsCreatedTaskResponse() throws Exception {
        given(taskService.createTask(eq("project-100"), any()))
                .willReturn(ControllerTestDataFactory.defaultTaskResponse());

        mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.defaultCreateTaskRequest()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.projectId").value("project-100"))
                .andExpect(jsonPath("$.title").value("Write controller tests"))
                .andExpect(jsonPath("$.description").value("Milestone 4 RED tests"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.assigneeEmail").value("owner@example.com"))
                .andExpect(jsonPath("$.dueDate").value("2026-07-10"))
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T11:00:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:00:00"));

        ArgumentCaptor<CreateTaskRequest> requestCaptor = ArgumentCaptor.forClass(CreateTaskRequest.class);
        verify(taskService).createTask(eq("project-100"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTitle()).isEqualTo("Write controller tests");
        assertThat(requestCaptor.getValue().getDescription()).isEqualTo("Milestone 4 RED tests");
        assertThat(requestCaptor.getValue().getAssigneeEmail()).isEqualTo("owner@example.com");
        assertThat(requestCaptor.getValue().getDueDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 200 OK with the updated task payload")
    void updateTaskAssigneeReturnsUpdatedTaskResponse() throws Exception {
        given(taskService.updateTaskAssignee(eq("task-200"), any()))
                .willReturn(ControllerTestDataFactory.taskResponseWithUpdatedAssignee(
                        "task-200",
                        "updated.owner@example.com",
                        "2026-07-05T11:30:00"));

        mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.assigneeRequest("updated.owner@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.assigneeEmail").value("updated.owner@example.com"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:30:00"));

        ArgumentCaptor<UpdateTaskAssigneeRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTaskAssigneeRequest.class);
        verify(taskService).updateTaskAssignee(eq("task-200"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAssigneeEmail()).isEqualTo("updated.owner@example.com");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 200 OK with the updated task payload")
    void updateTaskStatusReturnsUpdatedTaskResponse() throws Exception {
        given(taskService.updateTaskStatus(eq("task-200"), any()))
                .willReturn(ControllerTestDataFactory.taskResponseWithUpdatedStatus(
                        "task-200",
                        TaskStatus.IN_PROGRESS,
                        "2026-07-05T11:45:00"));

        mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.statusRequest("IN_PROGRESS")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-200"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T11:45:00"));

        ArgumentCaptor<UpdateTaskStatusRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateTaskStatusRequest.class);
        verify(taskService).updateTaskStatus(eq("task-200"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks returns 200 OK with project task list")
    void getTasksByProjectIdReturnsTaskList() throws Exception {
        given(taskService.getTasksByProjectId("project-100"))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(PROJECT_TASKS_URL.formatted("project-100")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].projectId").value("project-100"))
                .andExpect(jsonPath("$[0].status").value("TODO"));

        verify(taskService).getTasksByProjectId("project-100");
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO returns 200 OK with status-filtered task list")
    void getTasksByStatusReturnsTaskList() throws Exception {
        given(taskService.getTasksByStatus(TaskStatus.TODO))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(TASKS_URL).param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].status").value("TODO"));

        verify(taskService).getTasksByStatus(TaskStatus.TODO);
    }

    @Test
    @DisplayName("GET /api/tasks?assigneeEmail=owner@example.com returns 200 OK with assignee-filtered task list")
    void getTasksByAssigneeEmailReturnsTaskList() throws Exception {
        given(taskService.getTasksByAssigneeEmail("owner@example.com"))
                .willReturn(List.of(ControllerTestDataFactory.defaultTaskResponse()));

        mockMvc.perform(get(TASKS_URL).param("assigneeEmail", "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("task-200"))
                .andExpect(jsonPath("$[0].assigneeEmail").value("owner@example.com"));

        verify(taskService).getTasksByAssigneeEmail("owner@example.com");
    }

    @Test
    @DisplayName("GET /api/tasks without supported query parameters remains a negative controller contract case")
    void getTasksWithoutRequiredFilterRemainsNegativeCaseForControllerPlanning() throws Exception {
        mockMvc.perform(get(TASKS_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when title is missing")
    void createTaskReturnsBadRequestWhenTitleIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.createTaskWithoutTitle("Missing title"))),
                "title");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when assignee email format is invalid")
    void createTaskReturnsBadRequestWhenAssigneeEmailIsInvalid() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.createTaskWithAssignee(
                                "Write tests",
                                "invalid-email"))),
                "assigneeEmail");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when due date is in the past")
    void createTaskReturnsBadRequestWhenDueDateIsInThePast() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.createTaskWithDueDate(
                                "Write tests",
                                "2026-07-04"))),
                "dueDate");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 400 Bad Request when status is unsupported")
    void createTaskReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.createTaskWithStatus(
                                "Write tests",
                                "NOT_A_REAL_STATUS"))),
                "status");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks returns 404 Not Found when project does not exist")
    void createTaskReturnsNotFoundWhenProjectDoesNotExist() throws Exception {
        given(taskService.createTask(eq("project-999"), any()))
                .willThrow(new ProjectNotFoundException("project-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.minimalCreateTaskRequest("Write tests"))),
                "PROJECT_NOT_FOUND",
                "projectId");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 400 Bad Request when assignee email is missing")
    void updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.emptyObject())),
                "assigneeEmail");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 400 Bad Request when assignee email format is invalid")
    void updateTaskAssigneeReturnsBadRequestWhenAssigneeEmailIsInvalid() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.assigneeRequest("invalid-email"))),
                "assigneeEmail");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/assignee returns 404 Not Found when task does not exist")
    void updateTaskAssigneeReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        given(taskService.updateTaskAssignee(eq("task-999"), any()))
                .willThrow(new TaskNotFoundException("task-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted("task-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.assigneeRequest("owner@example.com"))),
                "TASK_NOT_FOUND",
                "taskId");
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 400 Bad Request when status is missing")
    void updateTaskStatusReturnsBadRequestWhenStatusIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.emptyObject())),
                "status");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 400 Bad Request when status is unsupported")
    void updateTaskStatusReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-200"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.statusRequest("NOT_A_REAL_STATUS"))),
                "status");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("PATCH /api/tasks/{taskId}/status returns 404 Not Found when task does not exist")
    void updateTaskStatusReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        given(taskService.updateTaskStatus(eq("task-999"), any()))
                .willThrow(new TaskNotFoundException("task-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(patch(TASK_STATUS_URL.formatted("task-999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.statusRequest("DONE"))),
                "TASK_NOT_FOUND",
                "taskId");
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks returns 404 Not Found when project does not exist")
    void getTasksByProjectIdReturnsNotFoundWhenProjectDoesNotExist() throws Exception {
        given(taskService.getTasksByProjectId("project-999"))
                .willThrow(new ProjectNotFoundException("project-999"));

        ControllerTestAssertions.assertNotFoundError(
                mockMvc.perform(get(PROJECT_TASKS_URL.formatted("project-999"))),
                "PROJECT_NOT_FOUND",
                "projectId");
    }

    @Test
    @DisplayName("GET /api/tasks?status=INVALID returns 400 Bad Request with consistent error response")
    void getTasksByStatusReturnsBadRequestWhenStatusIsUnsupported() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(get(TASKS_URL).param("status", "INVALID")),
                "status");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("GET /api/tasks?assigneeEmail=invalid-email returns 400 Bad Request with consistent error response")
    void getTasksByAssigneeEmailReturnsBadRequestWhenEmailIsInvalid() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(get(TASKS_URL).param("assigneeEmail", "invalid-email")),
                "assigneeEmail");

        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks allows omitted optional fields and defaults status behavior to later layers")
    void createTaskAllowsOptionalFieldsWhenTitleIsValid() throws Exception {
        given(taskService.createTask(eq("project-100"), any()))
                .willReturn(ControllerTestDataFactory.minimalTaskResponse(
                        "task-201",
                        "project-100",
                        "Minimal task",
                        "2026-07-05T12:15:00"));

        mockMvc.perform(post(PROJECT_TASKS_URL.formatted("project-100"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.minimalCreateTaskRequest("Minimal task")))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("task-201"))
                .andExpect(jsonPath("$.projectId").value("project-100"))
                .andExpect(jsonPath("$.title").value("Minimal task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.assigneeEmail").doesNotExist())
                .andExpect(jsonPath("$.dueDate").doesNotExist())
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T12:15:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T12:15:00"));

        ArgumentCaptor<CreateTaskRequest> requestCaptor = ArgumentCaptor.forClass(CreateTaskRequest.class);
        verify(taskService).createTask(eq("project-100"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTitle()).isEqualTo("Minimal task");
        assertThat(requestCaptor.getValue().getDescription()).isNull();
        assertThat(requestCaptor.getValue().getAssigneeEmail()).isNull();
        assertThat(requestCaptor.getValue().getDueDate()).isNull();
        assertThat(requestCaptor.getValue().getStatus()).isNull();
    }
}
