package io.github.avinash2196.taskflowapipddlearninglab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents stored project data required by Version 1.
 * The entity keeps persistence metadata local to the model while business rules stay in higher layers.
 */
@Entity
@Table(name = "projects")
public class Project {

    /** System-generated identifier used as the project business identity. */
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    /** Required project name supplied by the API consumer. */
    @Column(nullable = false)
    private String name;

    /** Optional project description supplied by the API consumer. */
    @Column
    private String description;

    /** Timestamp captured when the project is first created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /** Timestamp captured when the project is last changed. */
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    public Project() {
    }

    /**
     * Assigns generated persistence values when a new project is first stored.
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdDate == null) {
            createdDate = now;
        }
        updatedDate = now;
    }

    /**
     * Refreshes the update timestamp whenever an existing project row is changed.
     */
    @PreUpdate
    void onUpdate() {
        updatedDate = LocalDateTime.now();
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
