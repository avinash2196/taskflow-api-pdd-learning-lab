package io.github.avinash2196.taskflowapipddlearninglab.dao;

import io.github.avinash2196.taskflowapipddlearninglab.dao.support.RepositoryTestDataFactory;
import io.github.avinash2196.taskflowapipddlearninglab.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED-phase repository tests for project persistence behavior.
 * These tests define the save and lookup behavior required by Version 1 services.
 */
@DataJpaTest
class ProjectDaoTest {

    @Autowired
    private ProjectDao projectDao;

    @Test
    @DisplayName("save persists a project and returns generated identity and timestamps")
    void savePersistsProjectAndReturnsGeneratedIdentityAndTimestamps() {
        Project project = RepositoryTestDataFactory.newProject(
                "TaskFlow",
                "Repository milestone project");

        Project savedProject = projectDao.save(project);

        assertThat(savedProject.getId()).isNotBlank();
        assertThat(savedProject.getName()).isEqualTo("TaskFlow");
        assertThat(savedProject.getDescription()).isEqualTo("Repository milestone project");
        assertThat(savedProject.getCreatedDate()).isNotNull();
        assertThat(savedProject.getUpdatedDate()).isNotNull();
    }

    @Test
    @DisplayName("save keeps optional description null when a project is created without one")
    void saveKeepsOptionalDescriptionNull() {
        Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject("TaskFlow Lite", null));

        assertThat(savedProject.getId()).isNotBlank();
        assertThat(savedProject.getDescription()).isNull();
        assertThat(savedProject.getCreatedDate()).isNotNull();
        assertThat(savedProject.getUpdatedDate()).isNotNull();
    }

    @Test
    @DisplayName("findById returns the persisted project when it exists")
    void findByIdReturnsPersistedProjectWhenItExists() {
        Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject(
                "TaskFlow",
                "Existing project"));

        Optional<Project> loadedProject = projectDao.findById(savedProject.getId());

        assertThat(loadedProject).isPresent();
        assertThat(loadedProject.get().getId()).isEqualTo(savedProject.getId());
        assertThat(loadedProject.get().getName()).isEqualTo("TaskFlow");
        assertThat(loadedProject.get().getDescription()).isEqualTo("Existing project");
    }

    @Test
    @DisplayName("findById returns empty when the project does not exist")
    void findByIdReturnsEmptyWhenProjectDoesNotExist() {
        Optional<Project> loadedProject = projectDao.findById("missing-project-id");

        assertThat(loadedProject).isEmpty();
    }

    @Test
    @DisplayName("save updates an existing project and refreshes updatedDate without changing createdDate")
    void saveUpdatesExistingProjectAndRefreshesUpdatedDate() {
        Project savedProject = projectDao.save(RepositoryTestDataFactory.newProject(
                "TaskFlow",
                "Original description"));

        savedProject.setDescription("Updated description");

        Project updatedProject = projectDao.save(savedProject);

        assertThat(updatedProject.getId()).isEqualTo(savedProject.getId());
        assertThat(updatedProject.getName()).isEqualTo("TaskFlow");
        assertThat(updatedProject.getDescription()).isEqualTo("Updated description");
        assertThat(updatedProject.getCreatedDate()).isEqualTo(savedProject.getCreatedDate());
        assertThat(updatedProject.getUpdatedDate()).isAfterOrEqualTo(updatedProject.getCreatedDate());
    }
}
