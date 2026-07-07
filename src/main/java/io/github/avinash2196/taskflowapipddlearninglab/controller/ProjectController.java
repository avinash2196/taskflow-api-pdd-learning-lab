package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles project API endpoints by binding HTTP requests and delegating business work
 * to the project service contract.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param projectService service contract used by project endpoints
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Creates a project from the request body and returns the created payload.
     *
     * @param request request body mapped from the incoming JSON payload
     * @return created project response returned by the service layer
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }
}
