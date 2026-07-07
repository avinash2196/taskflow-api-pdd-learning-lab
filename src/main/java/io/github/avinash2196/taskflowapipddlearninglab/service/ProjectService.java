package io.github.avinash2196.taskflowapipddlearninglab.service;

import io.github.avinash2196.taskflowapipddlearninglab.dto.project.CreateProjectRequest;
import io.github.avinash2196.taskflowapipddlearninglab.dto.project.ProjectResponse;

/**
 * Defines project-related business operations required by the Version 1 API contract.
 * This contract isolates controller behavior from later service implementation details.
 */
public interface ProjectService {

    /**
     * Creates a new project using the approved project creation request contract.
     *
     * @param request carries the required project name and optional description
     * @return the created project response payload defined by the API contract
     */
    ProjectResponse createProject(CreateProjectRequest request);
}
