package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for project persistence operations required by Version 1 services.
 * This repository remains limited to storage access and does not contain business logic.
 */
public interface ProjectDao extends JpaRepository<Project, String> {
}
