package io.github.avinash2196.taskflowapipddlearninglab.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.model.Task;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full application integration tests that verify Spring wiring, HTTP routing,
 * service delegation, repository access, and H2-backed persistence together.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskflowApplicationIntegrationTest {

    private static final String PROJECTS_URL = "/api/projects";
    private static final String PROJECT_TASKS_URL = "/api/projects/%s/tasks";
    private static final String TASK_ASSIGNEE_URL = "/api/tasks/%s/assignee";
    private static final String TASK_STATUS_URL = "/api/tasks/%s/status";
    private static final String TASKS_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TaskDao taskDao;

    @BeforeEach
    void clearDatabase() {
        taskDao.deleteAll();
        projectDao.deleteAll();
    }

    @Test
    @DisplayName("Spring application context loads all required Version 1 beans")
    void contextLoadsRequiredBeans() {
        assertThat(mockMvc).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(projectService).isNotNull();
        assertThat(taskService).isNotNull();
        assertThat(projectDao).isNotNull();
        assertThat(taskDao).isNotNull();
    }

    @Test
    @DisplayName("Create project and task flow works across controller, service, and DAO layers")
    void createProjectAndTaskFlowWorksAcrossLayers() throws Exception {
        MvcResult createProjectResult = mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Integration Project",
                                  "description": "Created through full application flow"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.name").value("Integration Project"))
                .andExpect(jsonPath("$.description").value("Created through full application flow"))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.updatedDate").isNotEmpty())
                .andReturn();

        String projectId = responseField(createProjectResult, "id");

        MvcResult createTaskResult = mockMvc.perform(post(PROJECT_TASKS_URL.formatted(projectId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Wire all layers",
                                  "description": "Verify full stack integration",
                                  "assigneeEmail": "owner@example.com",
                                  "dueDate": "%s"
                                }
                                """.formatted(LocalDate.now().plusDays(5))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.title").value("Wire all layers"))
                .andExpect(jsonPath("$.description").value("Verify full stack integration"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.assigneeEmail").value("owner@example.com"))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(5).toString()))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.updatedDate").isNotEmpty())
                .andReturn();

        String taskId = responseField(createTaskResult, "id");

        assertThat(projectDao.findById(projectId)).isPresent();

        Optional<Task> persistedTask = taskDao.findById(taskId);
        assertThat(persistedTask).isPresent();
        assertThat(persistedTask.get().getProjectId()).isEqualTo(projectId);
        assertThat(persistedTask.get().getTitle()).isEqualTo("Wire all layers");
        assertThat(persistedTask.get().getDescription()).isEqualTo("Verify full stack integration");
        assertThat(persistedTask.get().getStatus().name()).isEqualTo("TODO");
        assertThat(persistedTask.get().getAssigneeEmail()).isEqualTo("owner@example.com");
        assertThat(persistedTask.get().getDueDate()).isEqualTo(LocalDate.now().plusDays(5));
    }

    @Test
    @DisplayName("Update assignee and status flows persist changes and remain visible in list queries")
    void updateFlowsWorkAcrossLayers() throws Exception {
        String projectId = createProject("Query Integration Project");
        String taskId = createTask(projectId, """
                {
                  "title": "Integrated query task",
                  "description": "Created for update and list verification",
                  "dueDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(2)));

        mockMvc.perform(patch(TASK_ASSIGNEE_URL.formatted(taskId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assigneeEmail": "updated.owner@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.assigneeEmail").value("updated.owner@example.com"));

        mockMvc.perform(patch(TASK_STATUS_URL.formatted(taskId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(get(PROJECT_TASKS_URL.formatted(projectId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].projectId").value(projectId))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].assigneeEmail").value("updated.owner@example.com"));

        mockMvc.perform(get(TASKS_URL).param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));

        mockMvc.perform(get(TASKS_URL).param("assigneeEmail", "updated.owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].assigneeEmail").value("updated.owner@example.com"));

        Optional<Task> persistedTask = taskDao.findById(taskId);
        assertThat(persistedTask).isPresent();
        assertThat(persistedTask.get().getAssigneeEmail()).isEqualTo("updated.owner@example.com");
        assertThat(persistedTask.get().getStatus().name()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("Project task listing returns not found for missing project through real exception mapping")
    void getTasksByProjectIdReturnsNotFoundForMissingProject() throws Exception {
        mockMvc.perform(get(PROJECT_TASKS_URL.formatted("missing-project-id")))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("PROJECT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Project not found: missing-project-id"))
                .andExpect(jsonPath("$.context.field").value("projectId"));
    }

    private String createProject(String projectName) throws Exception {
        MvcResult result = mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s"
                                }
                                """.formatted(projectName)))
                .andExpect(status().isCreated())
                .andReturn();
        return responseField(result, "id");
    }

    private String createTask(String projectId, String requestBody) throws Exception {
        MvcResult result = mockMvc.perform(post(PROJECT_TASKS_URL.formatted(projectId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        return responseField(result, "id");
    }

    private String responseField(MvcResult result, String fieldName) throws Exception {
        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        return responseBody.get(fieldName).asText();
    }
}
