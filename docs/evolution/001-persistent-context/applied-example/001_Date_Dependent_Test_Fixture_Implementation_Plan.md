Milestone description

Fix the controller-test fixture regression caused by hard-coded due dates that were future-valid on July 5, 2026 but became invalid by Thursday, July 16, 2026. The failing path is `TaskControllerTest.createTaskReturnsCreatedTaskResponse`, where the request payload still sends `2026-07-10` and now correctly triggers the `@FutureOrPresent` validation in `CreateTaskRequest`, producing `400 Bad Request` instead of the expected `201 Created`.

Files to be updated/changed

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`
2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestJsonFactory.java`
3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestDataFactory.java`

Exact code changes with comments

1. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/TaskControllerTest.java`

Before:

```java
.andExpect(jsonPath("$.dueDate").value("2026-07-10"))

assertThat(requestCaptor.getValue().getDueDate()).isEqualTo(LocalDate.of(2026, 7, 10));
```

After:

```java
LocalDate expectedDueDate = LocalDate.now().plusDays(7);

.andExpect(jsonPath("$.dueDate").value(expectedDueDate.toString()))

assertThat(requestCaptor.getValue().getDueDate()).isEqualTo(expectedDueDate);
```

Comments:
- Replace the fixed assertion date with a runtime-generated future date.
- Keep the test intent unchanged: the controller should accept a valid non-past due date and pass it through to the service layer.
- Reuse one local variable inside the test so request and response assertions stay aligned.

2. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestJsonFactory.java`

Before:

```java
public static String defaultCreateTaskRequest() {
    return """
            {
              "title": "Write controller tests",
              "description": "Milestone 4 RED tests",
              "assigneeEmail": "owner@example.com",
              "dueDate": "2026-07-10",
              "status": "TODO"
            }
            """;
}
```

After:

```java
public static String defaultCreateTaskRequest() {
    return createTaskRequestWithExplicitDueDate(LocalDate.now().plusDays(7).toString());
}

private static String createTaskRequestWithExplicitDueDate(String dueDate) {
    return """
            {
              "title": "Write controller tests",
              "description": "Milestone 4 RED tests",
              "assigneeEmail": "owner@example.com",
              "dueDate": %s,
              "status": "TODO"
            }
            """.formatted(asJsonValue(dueDate));
}
```

Comments:
- Move the default success payload off the fixed string date and build it from a future date at execution time.
- Keep the JSON shape unchanged so controller coverage remains the same.
- Use a small helper only if needed to avoid duplicating the default payload structure.

3. `src/test/java/io/github/avinash2196/taskflowapipddlearninglab/controller/support/ControllerTestDataFactory.java`

Before:

```java
response.setDueDate(LocalDate.of(2026, 7, 10));
```

After:

```java
response.setDueDate(LocalDate.now().plusDays(7));
```

Comments:
- Make the default response fixture match the request fixture so the success-path expectations remain internally consistent.
- Leave the created and updated timestamps unchanged because the failure is driven by `dueDate` validation only, not timestamp handling.

Out of scope

- Any production code change under `src/main/java`
- Changes to validation rules or `@FutureOrPresent` behavior
- Changes to negative due-date validation tests other than preserving their past-date intent
- Refactoring unrelated controller, service, repository, or integration tests
- `docs/.ai/Plan.md` updates

Success criteria

1. `mvn test` passes after the fixture changes.
2. `TaskControllerTest.createTaskReturnsCreatedTaskResponse` returns to `201 Created` using a generated future due date.
3. `TaskControllerTest.createTaskReturnsBadRequestWhenDueDateIsInThePast` still fails for a past due date relative to Thursday, July 16, 2026.
4. Only the three test files listed above are changed during implementation.
5. No production behavior, validation rule, or milestone scope is altered.
