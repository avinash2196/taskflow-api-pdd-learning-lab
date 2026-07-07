package io.github.avinash2196.taskflowapipddlearninglab.dto.project;

import java.time.LocalDateTime;

/**
 * Returns the project payload defined by the Version 1 create project success contract.
 */
public class ProjectResponse {

    /** System-generated project identifier returned to the API consumer. */
    private String id;

    /** Required project name returned to the API consumer. */
    private String name;

    /** Optional project description returned to the API consumer. */
    private String description;

    /** Project creation timestamp returned to the API consumer. */
    private LocalDateTime createdDate;

    /** Project last update timestamp returned to the API consumer. */
    private LocalDateTime updatedDate;

    public ProjectResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
