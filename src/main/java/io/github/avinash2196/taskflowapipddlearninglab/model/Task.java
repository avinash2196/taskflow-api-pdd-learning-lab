package io.github.avinash2196.taskflowapipddlearninglab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents stored task data required by Version 1.
 * The entity keeps persistence mapping local while business behavior stays in the service layer.
 */
@Entity
@Table(name = "tasks")
public class Task {

    /** System-generated identifier used as the task business identity. */
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    /** Identifier of the project that owns this task. */
    @Column(nullable = false)
    private String projectId;

    /** Required task title supplied by the API consumer. */
    @Column(nullable = false)
    private String title;

    /** Optional task description supplied by the API consumer. */
    @Column
    private String description;

    /** Current workflow status for the task. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /** Optional email address of the user currently assigned to the task. */
    @Column
    private String assigneeEmail;

    /** Optional due date that must be today or in the future when provided. */
    @Column
    private LocalDate dueDate;

    /** Timestamp captured when the task is first created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /** Timestamp captured when the task is last changed. */
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    public Task() {
    }

    /**
     * Assigns generated persistence values when a new task is first stored.
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
     * Refreshes the update timestamp whenever an existing task row is changed.
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
