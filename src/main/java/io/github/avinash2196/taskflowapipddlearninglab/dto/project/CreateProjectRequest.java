package io.github.avinash2196.taskflowapipddlearninglab.dto.project;

import jakarta.validation.constraints.NotBlank;

/**
 * Carries request body data for the project creation API.
 */
public class CreateProjectRequest {

    /** Required project name from the API request body. */
    @NotBlank(message = "Project name is required.")
    private String name;

    /** Optional project description from the API request body. */
    private String description;

    public CreateProjectRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
