package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.service.support.ServiceTestAssertions;
import io.github.avinash2196.taskflowapipddlearninglab.service.support.ServiceTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectDao projectDao;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectDao);
    }

    @Test
    @DisplayName("createProject saves a mapped project and returns the persisted response")
    void createProjectSavesMappedProjectAndReturnsResponse() {
        CreateProjectRequest request = ServiceTestDataFactory.createDefaultProjectRequest();
        Project persistedProject = ServiceTestDataFactory.project(
                ServiceTestDataFactory.DEFAULT_PROJECT_ID,
                ServiceTestDataFactory.DEFAULT_PROJECT_NAME,
                ServiceTestDataFactory.DEFAULT_PROJECT_DESCRIPTION,
                "2026-07-05T10:15:00",
                "2026-07-05T10:15:00");

        given(projectDao.save(any(Project.class))).willReturn(persistedProject);

        ProjectResponse response = projectService.createProject(request);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectDao).save(projectCaptor.capture());
        ServiceTestAssertions.assertProjectToSave(
                projectCaptor.getValue(),
                ServiceTestDataFactory.DEFAULT_PROJECT_NAME,
                ServiceTestDataFactory.DEFAULT_PROJECT_DESCRIPTION);
        ServiceTestAssertions.assertProjectResponse(response, persistedProject);
    }

    @Test
    @DisplayName("createProject keeps optional description null when the request omits it")
    void createProjectKeepsOptionalDescriptionNull() {
        CreateProjectRequest request = ServiceTestDataFactory.createProjectRequest("TaskFlow Lite", null);
        Project persistedProject = ServiceTestDataFactory.project(
                "project-101",
                "TaskFlow Lite",
                null,
                "2026-07-05T12:00:00",
                "2026-07-05T12:00:00");

        given(projectDao.save(any(Project.class))).willReturn(persistedProject);

        ProjectResponse response = projectService.createProject(request);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectDao).save(projectCaptor.capture());
        ServiceTestAssertions.assertProjectToSave(projectCaptor.getValue(), "TaskFlow Lite", null);
        ServiceTestAssertions.assertProjectResponse(response, persistedProject);
    }
}
