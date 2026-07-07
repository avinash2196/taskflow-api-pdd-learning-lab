package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestAssertions;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.controller.support.ControllerTestJsonFactory;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-slice tests for project endpoints.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    private static final String PROJECTS_URL = "/api/projects";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("POST /api/projects returns 201 Created with the created project payload")
    void createProjectReturnsCreatedProjectResponse() throws Exception {
        given(projectService.createProject(any()))
                .willReturn(ControllerTestDataFactory.projectResponse(
                        "project-100",
                        "TaskFlow",
                        "Project description",
                        "2026-07-05T10:15:00"));

        mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest(
                                "TaskFlow",
                                "Project description")))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("project-100"))
                .andExpect(jsonPath("$.name").value("TaskFlow"))
                .andExpect(jsonPath("$.description").value("Project description"))
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T10:15:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T10:15:00"));

        ArgumentCaptor<CreateProjectRequest> requestCaptor = ArgumentCaptor.forClass(CreateProjectRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("TaskFlow");
        assertThat(requestCaptor.getValue().getDescription()).isEqualTo("Project description");
    }

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is missing")
    void createProjectReturnsBadRequestWhenNameIsMissing() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectWithoutName("Project without a name"))),
                "name");

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("POST /api/projects returns 400 Bad Request when name is null")
    void createProjectReturnsBadRequestWhenNameIsNull() throws Exception {
        ControllerTestAssertions.assertValidationError(
                mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest(
                                null,
                                "Project without a valid name"))),
                "name");

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("POST /api/projects allows optional description when name is valid")
    void createProjectAllowsOptionalDescription() throws Exception {
        given(projectService.createProject(any()))
                .willReturn(ControllerTestDataFactory.projectResponse(
                        "project-101",
                        "TaskFlow Lite",
                        null,
                        "2026-07-05T12:00:00"));

        mockMvc.perform(post(PROJECTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ControllerTestJsonFactory.projectRequest("TaskFlow Lite", null)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("project-101"))
                .andExpect(jsonPath("$.name").value("TaskFlow Lite"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.createdDate").value("2026-07-05T12:00:00"))
                .andExpect(jsonPath("$.updatedDate").value("2026-07-05T12:00:00"));

        ArgumentCaptor<CreateProjectRequest> requestCaptor = ArgumentCaptor.forClass(CreateProjectRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("TaskFlow Lite");
        assertThat(requestCaptor.getValue().getDescription()).isNull();
    }
}
