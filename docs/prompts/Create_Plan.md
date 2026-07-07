# Create Plan.md

Create `docs/.ai/Plan.md` for the TaskFlow API.

**Input**

`docs/Requirement.docx`

**Role**

Act as a senior backend architect.

## Task

First, review docs/Requirement.docx.

Your first responsibility is to decide whether the requirement document is clear enough to create Plan.md.

If any missing, unclear, contradictory, or open item affects Version 1 scope, business rules, validation rules, API behavior, testing expectations, milestone order, or constraints, do not create or update Plan.md.

Instead, respond with numbered clarification questions only.

If the requirements are clear enough, create or update only:

docs/.ai/Plan.md

---

## Rules

* Do not create any other file.
* Do not generate code. 
* Do not generate tests. 
* Do not invent requirements. 
* Do not invent non-functional requirements. 
* Do not invent business rules. 
* Do not invent validation rules. 
* Do not define endpoint paths, DTOs, entities, service methods, database schema, package structure, or implementation details unless explicitly defined in the requirements. 
* Use Version 1 instead of MVP. 
* Keep the document practical, implementation-oriented, and milestone-driven. 
* If the requirement document contains open questions that affect Version 1 behavior, do not answer them yourself. 
* Do not convert open questions into assumptions, defaults, confirmed scope, confirmed rules, or validation behavior. 
* Ask numbered clarification questions only when clarification is required.

---

# Plan.md Structure

Include the following sections.

## 1. Project Overview

## 2. Business Goal

## 3. Version 1 Scope

## 4. Out of Scope

## 5. Functional Requirements

## 6. Domain & Business Rules

Include only confirmed information:

* Core domain concepts
* Business rules
* Validation rules
* Error handling expectations
* Data considerations

## 7. API Contract Summary

Summarize the approved API behavior only.

Do not define endpoint paths or request/response models unless explicitly stated in the requirements.

## 8. Constraints & Assumptions

Include only confirmed project constraints and clarifications.

Do not invent constraints.

## 9. TDD Strategy

Describe the planned RED → GREEN → REFACTOR workflow.

Do not generate tests.

## 10. Milestone Breakdown

Use the following milestone order.

1. API Contract Plan
2. Project Skeleton
3.  API Behavior, Domain Model, and Service Contract Planning
    * DTO/API models
    * Domain model
    * Service interfaces
    * High-level mapping from API operations to service interface methods
4. Controller Tests (RED)
5. Controller Implementation Using Service Contract (GREEN)
6. Validation and Error Handling Tests (RED)
7. Validation and Exception Handling Implementation (GREEN)
8. Controller Refactor (REFACTOR)
9. Repository Contract
    * Repository interfaces only
    * No repository implementation
10. Service Tests Using Mocked Repository Contract (RED)
11. Service Implementation (GREEN)
12. Service Refactor (REFACTOR)
13. Data Access Tests (RED)
14. Data Access Implementation (GREEN)
15. Integration Tests and Wiring Review
16. Final Review

For every milestone include:

* Goal
* Expected outcome
* What is not included
* Completion criteria

Every milestone must include:

> Plan.md must be updated after milestone completion.

---

## 11. Plan Update Rules

State that Plan.md is the project's source of truth and must be updated after every completed milestone.

---

## 12. Project Execution Status

Create a tracking table.

| Milestone | Status | Completed On | Notes |
| --------- | ------ | ------------ | ----- |

Initialize as:

* Milestone 1 → Current
* Milestones 2–16 → Pending
* Completed On → Not completed
* Notes → Empty

Only this table should be updated as the project progresses.

Milestone definitions must remain unchanged.

---

After creating or updating the file, respond only with:

`Created docs/.ai/Plan.md for review.`
