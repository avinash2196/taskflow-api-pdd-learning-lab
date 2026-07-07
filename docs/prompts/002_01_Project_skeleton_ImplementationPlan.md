Goal: The goal is to create an implementation plan for Project Skeleton based on Plan.md

Role: You are a senior backend architect .

Input : docs/.ai/Plan.md
Task : Read Plan.md and create an 002_Implementation_Plan_Project_Skeleton.md file.



Details :

- Maven project setup using pom.xml
- Java 17 configuration
- Spring Boot 3 configuration
- H2 Database
- Add swagger
- Required base dependencies for skeleton readiness
- Main Spring Boot application class
- Base application configuration file
- Source folder structure under src/main/java
- Test folder structure under src/test/java, but no tests yet
- use group id : io.github.avinash2196
- use artifact Id : taskflow-api-pdd-learning-lab
- Recommend if any other folder also needed as per java standards
- Provide details about exact file and code that needs to be added/updated with exact folder name as per  project specs in Plan.md 
- Create thin controller,use dependency injections and other project files like service/dao so that it act as placeholders so that we can work on them one by one but project keeps on building.
- Controllers must be created in this milestone as placeholders only. 
- Controller endpoint methods must not be created in this milestone. 
- Real controller behavior must be added later after controller tests are written.

Output file: docs/.ai/002_Implementation_Plan_Project_Skeleton.md
- Plan to add gitignore file also
- Only create or update this file. Do not create or modify anything else.

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 2 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Follow docs/.ai/Plan.md strictly.
- Make sure comments are readable and define the usage and purpose of code.( class /method.logic)
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Keep package boundaries clean: controller, service, da, model, dto, config, and exception should remain separate top-level packages under the application base package.
- Do not implement endpoints.
- Do not implement service methods.
- Do not implement validation.
- Do not create models/DTOs if not required for project compilation after implementation of this phase
- Do not implement exception handling behavior.
- Do not implement dao methods.
- Do not create database schema.
- Do not create tests.
- Do not add business logic.
- Do not create .gitkeep/package-info files or placeholder files just to force empty folders into Git.
- Do not introduce User-based classes or concepts.
- Success criteria should be for implementation not plan.
- Do not assume, Ask clarifying questions if required.


After creating the file, respond only with:

Created 002_Implementation_Plan_Project_Skeleton.md for review.