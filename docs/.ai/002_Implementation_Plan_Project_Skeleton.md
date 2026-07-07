# Implementation Plan: Project Skeleton

## Milestone Description

Milestone 2 prepares the minimum Spring Boot project skeleton required for later TDD-driven implementation work. This milestone is limited to base Maven setup, Java 17 and Spring Boot 3 configuration, H2 support, Swagger/OpenAPI readiness, base package structure, placeholder application wiring, and placeholder layer classes so the project can compile while controller behavior, service behavior, validation, exception handling, repository contracts, and tests are deferred to later milestones.

The implementation in this milestone must keep package boundaries clean under the base package `io.github.avinash2196.taskflowapipddlearninglab` and must avoid endpoint methods, business logic, validation logic, data access methods, database schema creation, and tests.

## Files To Be Updated/Changed

1. `pom.xml`
2. `.gitignore`
3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/TaskflowApiPddLearningLabApplication.java`
4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/config/AppConfig.java`
5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`
6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`
7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`
8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`
9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`
10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`
11. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`
12. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`
13. `src/main/resources/application.yml`

Recommended folders to add now for standard Java project organization, without creating empty placeholder files:

14. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/model`
15. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dto`
16. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/exception`
17. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab`
18. `src/test/resources`

## Exact Code Changes With Comments

### 1. `pom.xml`

Purpose:
- Establish Maven project coordinates.
- Configure Java 17 and Spring Boot 3.
- Add the minimum dependencies required for skeleton readiness.
- Support H2 for later data access milestones.
- Add Swagger/OpenAPI dependency so API documentation is available once endpoints are introduced.
- Include test dependency without adding tests yet.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <!-- Spring Boot parent manages dependency versions and common plugin defaults. -->
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.2</version>
        <relativePath/>
    </parent>

    <groupId>io.github.avinash2196</groupId>
    <artifactId>taskflow-api-pdd-learning-lab</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>taskflow-api-pdd-learning-lab</name>
    <description>TaskFlow API learning lab project skeleton</description>

    <properties>
        <!-- Java 17 is the required runtime level for this project skeleton. -->
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Base web dependency for Spring MVC controllers and application startup. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Base validation dependency is added for later milestones; validation logic is not implemented now. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- JPA dependency is added so DAO/repository work can begin in later milestones. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 is the in-memory database selected for local development and later testable persistence work. -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Swagger/OpenAPI support is added for API documentation visibility during later controller milestones. -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.6.0</version>
        </dependency>

        <!-- Test starter is included so later RED milestones can add tests without dependency churn. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <!-- Spring Boot Maven plugin supports packaging and application execution. -->
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. `.gitignore`

Purpose:
- Ignore Maven build output, IDE files, logs, and local database artifacts.
- Keep repository clean for source-only commits.

```gitignore
# Maven build output
/target/

# Maven wrapper logs if added later
.mvn/wrapper/maven-wrapper.jar

# IDE files
.idea/
*.iml
.classpath
.project
.settings/

# OS files
.DS_Store
Thumbs.db

# Logs
*.log

# Local H2 database files
*.mv.db
*.trace.db
```

### 3. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/TaskflowApiPddLearningLabApplication.java`

Purpose:
- Provide the Spring Boot entry point.
- Enable component scanning from the application base package downward.

```java
package io.github.avinash2196.taskflowapipddlearninglab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for the TaskFlow API project skeleton.
 * This class starts the Spring Boot application and enables package scanning.
 */
@SpringBootApplication
public class TaskflowApiPddLearningLabApplication {

    /**
     * Starts the Spring Boot application runtime.
     *
     * @param args standard application startup arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskflowApiPddLearningLabApplication.class, args);
    }
}
```

### 4. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/config/AppConfig.java`

Purpose:
- Reserve a standard configuration package for future shared bean definitions.
- Keep configuration structure explicit from the start.

```java
package io.github.avinash2196.taskflowapipddlearninglab.config;

import org.springframework.context.annotation.Configuration;

/**
 * Base application configuration class.
 * Additional shared bean definitions can be introduced here in later milestones.
 */
@Configuration
public class AppConfig {
    // No custom beans are required in this milestone.
}
```

### 5. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/ProjectController.java`

Purpose:
- Create a placeholder controller class only.
- Use constructor-based dependency injection.
- Do not define endpoint methods in this milestone.

```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for future project-related API behavior.
 * Endpoint methods are intentionally deferred until controller tests are written.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param projectService service placeholder for future project use cases
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
}
```

### 6. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskController.java`

Purpose:
- Create a placeholder controller class only.
- Use constructor-based dependency injection.
- Do not define endpoint methods in this milestone.

```java
package io.github.avinash2196.taskflowapipddlearninglab.controller;

import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for future task-related API behavior.
 * Endpoint methods are intentionally deferred until controller tests are written.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param taskService service placeholder for future task use cases
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
}
```

### 7. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/ProjectService.java`

Purpose:
- Define a service layer placeholder contract.
- Keep controller wiring valid without implementing business operations.

```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

/**
 * Placeholder service contract for future project-related business behavior.
 * Service methods are intentionally deferred to later planning and RED/GREEN milestones.
 */
public interface ProjectService {
    // No methods are defined in this milestone.
}
```

### 8. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/TaskService.java`

Purpose:
- Define a service layer placeholder contract.
- Keep controller wiring valid without implementing business operations.

```java
package io.github.avinash2196.taskflowapipddlearninglab.service;

/**
 * Placeholder service contract for future task-related business behavior.
 * Service methods are intentionally deferred to later planning and RED/GREEN milestones.
 */
public interface TaskService {
    // No methods are defined in this milestone.
}
```

### 9. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/ProjectServiceImpl.java`

Purpose:
- Create a concrete service placeholder bean for dependency injection.
- Depend on DAO placeholder only.
- Do not implement business methods.

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.ProjectDao;
import io.github.avinash2196.taskflowapipddlearninglab.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation for the project service layer.
 * Business behavior is intentionally deferred to a later milestone.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;

    /**
     * Creates the service with its required data access dependency.
     *
     * @param projectDao placeholder data access dependency
     */
    public ProjectServiceImpl(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }
}
```

### 10. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/service/impl/TaskServiceImpl.java`

Purpose:
- Create a concrete service placeholder bean for dependency injection.
- Depend on DAO placeholder only.
- Do not implement business methods.

```java
package io.github.avinash2196.taskflowapipddlearninglab.service.impl;

import io.github.avinash2196.taskflowapipddlearninglab.dao.TaskDao;
import io.github.avinash2196.taskflowapipddlearninglab.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation for the task service layer.
 * Business behavior is intentionally deferred to a later milestone.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;

    /**
     * Creates the service with its required data access dependency.
     *
     * @param taskDao placeholder data access dependency
     */
    public TaskServiceImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
}
```

### 11. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/ProjectDao.java`

Purpose:
- Create a DAO placeholder bean so service placeholders can be instantiated.
- Keep the data access package available without defining repository behavior yet.

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import org.springframework.stereotype.Repository;

/**
 * Placeholder data access component for project persistence concerns.
 * Data access methods are intentionally deferred to a later milestone.
 */
@Repository
public class ProjectDao {
    // No data access methods are defined in this milestone.
}
```

### 12. `src/main/java/io/github/avinash2196/taskflowapipddlearninglab/dao/TaskDao.java`

Purpose:
- Create a DAO placeholder bean so service placeholders can be instantiated.
- Keep the data access package available without defining repository behavior yet.

```java
package io.github.avinash2196.taskflowapipddlearninglab.dao;

import org.springframework.stereotype.Repository;

/**
 * Placeholder data access component for task persistence concerns.
 * Data access methods are intentionally deferred to a later milestone.
 */
@Repository
public class TaskDao {
    // No data access methods are defined in this milestone.
}
```

### 13. `src/main/resources/application.yml`

Purpose:
- Provide base Spring Boot configuration.
- Configure application name.
- Configure H2 datasource for local runtime readiness.
- Keep Swagger/OpenAPI UI available with default Springdoc behavior.
- Avoid schema-generation and business-specific configuration in this milestone.

```yaml
spring:
  application:
    # Logical application name for logs and runtime identification.
    name: taskflow-api-pdd-learning-lab

  datasource:
    # In-memory H2 configuration for local skeleton readiness.
    url: jdbc:h2:mem:taskflowdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      # H2 console enabled for local development only.
      enabled: true

  jpa:
    hibernate:
      # No schema creation should be introduced in this milestone.
      ddl-auto: none
    open-in-view: false

server:
  port: 8080
```

Notes:
- No custom Swagger configuration class is required in this milestone.
- With the Springdoc dependency present, the default documentation endpoints will be available later when controller methods are added.

## Recommended Folder Structure

The following structure keeps standard Java and Spring package boundaries ready for later milestones while avoiding unnecessary placeholder files:

```text
src/
  main/
    java/
      io/github/avinash2196/taskflowapipddlearninglab/
        TaskflowApiPddLearningLabApplication.java
        config/
          AppConfig.java
        controller/
          ProjectController.java
          TaskController.java
        service/
          ProjectService.java
          TaskService.java
          impl/
            ProjectServiceImpl.java
            TaskServiceImpl.java
        dao/
          ProjectDao.java
          TaskDao.java
        model/
        dto/
        exception/
    resources/
      application.yml
  test/
    java/
      io/github/avinash2196/taskflowapipddlearninglab/
    resources/
```

Notes:
- `service.impl` is recommended so contracts and implementations remain separate.
- `model`, `dto`, and `exception` should exist as planned top-level package targets, but no files should be added in this milestone because they are not required for compilation yet.
- `src/test/java` and `src/test/resources` should be added so later RED milestones can place tests cleanly, but no tests should be created now.

## Out Of Scope

The following items must not be implemented in this milestone:

- Any controller endpoint method.
- Any detailed Swagger customization beyond dependency-based enablement.
- Any request or response DTO.
- Any domain model class.
- Any validation annotation or validation behavior.
- Any exception handler or exception behavior.
- Any service method or business logic.
- Any DAO method or repository contract behavior.
- Any JPA entity mapping.
- Any database schema or seed data.
- Any tests.
- Any integration wiring beyond what is required for the application to start and compile.
- Any user-related classes or concepts.

## Success Criteria

Implementation of this plan is successful when all of the following are true:

1. The Maven project builds successfully with `groupId` `io.github.avinash2196` and `artifactId` `taskflow-api-pdd-learning-lab`.
2. Java 17 and Spring Boot 3 are configured in `pom.xml`.
3. H2 runtime configuration exists in `application.yml`.
4. Swagger/OpenAPI dependency is present in `pom.xml` for documentation readiness.
5. The main Spring Boot application class exists and starts component scanning from the base package.
6. The package structure cleanly separates `controller`, `service`, `da`, `model`, `dto`, `config`, and `exception`.
7. Placeholder controller classes exist with constructor-based dependency injection only and no endpoint methods.
8. Placeholder service contracts and service implementation classes exist with no business methods.
9. Placeholder DAO classes exist with no data access methods.
10. Test source folders exist, but no tests are added in this milestone.
11. `.gitignore` exists and excludes standard Java/Maven/IDE/local database artifacts.
12. No business behavior, validation, exception handling behavior, schema creation, or tests are introduced.
