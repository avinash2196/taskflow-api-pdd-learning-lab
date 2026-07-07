package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for project business operations.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        return toProjectResponse(projectDao.save(toProject(request)));
    }

    /**
     * Builds the project entity used by the repository save operation.
     */
    private Project toProject(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return project;
    }

    /**
     * Builds the response returned to upper layers from the saved entity.
     */
    private ProjectResponse toProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedDate(project.getCreatedDate());
        response.setUpdatedDate(project.getUpdatedDate());
        return response;
    }
}
