# TaskFlow API Copilot Instructions

## Role

Act as a senior backend architect and developer.

## Input

Before starting a milestone task, review the current repository state and the relevant files:

- `docs/Requirement.docx`
- `docs/.ai/Plan.md`
- `docs/.ai/001_API_Contract.md`
- The implementation plan for the active milestone
- Existing source code and tests related to the task
- For repository documentation or cleanup tasks, review only the files required by the active prompt.

## Task

Follow the active prompt and implementation plan.

Make changes only for the current milestone.

## Rules

- Do not hallucinate.
- Do not invent requirements or non-functional requirements.
- Do not invent business rules or validation rules.
- Do not pull future milestone work into the current task.
- Do not assume. Ask clarifying questions if required.
- Do not add any additional files, plans, description documents, or other documents unless requested by the prompt or implementation plan.
- Only create or update the files required by the active task.
- Do not create placeholder classes when real project classes already exist.
- Do not introduce new dependencies unless required by the approved implementation plan.
- Do not rewrite or improve completed prompts, plans, or review files unless the active prompt explicitly requests it.
- When the active prompt requests documentation of a later workflow improvement, add it under `docs/evolution/`.

## Planning tasks

- Use Standard Java naming conventions for files as well as folders.
- Follow docs/.ai/Plan.md strictly.
- For existing files, show both Before and After Changes for comparison.
- For new files, show the complete proposed content.
- Make sure the code readability is maintained in preview mode.
- Make sure comments are readable and explain the purpose of each class or method when comments are needed.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Success criteria should be for implementation not plan.

## Implementation tasks

- Implement the changes from the approved implementation plan.
- Implement only the files defined in the implementation plan.


## Plan.md update rules

- Update `Plan.md` only after the milestone passes its success criteria.
- Only update the milestone completion section in `Plan.md`.
- Do not rewrite or improve `Plan.md`.
- Do not change milestone definitions.

## Testing

Run the complete test suite with:

```bash
mvn test
```

For a RED milestone:

- The project should build or compile.
- The expected tests may fail only because the planned actual code behavior is not implemented.

For a GREEN milestone:

- The project should build or compile.
- The approved test cases should pass.

For a REFACTOR milestone:

- The existing test cases should continue to pass.

## Success criteria

Before completing a task, confirm that:

- The files defined in the active implementation plan were created or updated.
- Only the approved files were changed.
- The project builds or compiles.
- The required tests were executed.
- The success criteria defined in the active implementation plan were satisfied.
- No future milestone work was added.
- No requirement or non-functional requirement was invented.
- Any `Plan.md` change is limited to the active milestone completion section.
