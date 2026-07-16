# Evolution 001: Persistent Repository Context

## Goal

Move the common instructions repeated across milestone prompts into:

```text
.github/copilot-instructions.md
```

The task-specific instructions will continue to remain in each prompt.

## Original approach

The original prompts were intentionally self-contained.

Each prompt included the goal, input files, task, rules, success criteria, and expected completion response.

Several rules were repeated across planning and implementation prompts:

- Review the current state before making changes.
- Follow `docs/.ai/Plan.md` strictly.
- Refer to the approved implementation plan.
- Do not hallucinate.
- Do not invent requirements or non-functional requirements.
- Do not pull future milestone work into the current task.
- Do not add files other than those required by the implementation plan.
- Do not assume. Ask clarifying questions if required.
- Only update the milestone completion section in `Plan.md`.
- Do not rewrite or improve `Plan.md`.

This repetition made each prompt independently understandable and easy to review.

## Why this change was added

After completing all milestones, it became clear that some rules were common to the complete repository and were not specific to one task.

These common rules now live in:

```text
.github/copilot-instructions.md
```

This keeps the same instructions available across tasks without copying all of them into every new prompt.

## What remains in the prompt

Each prompt should still include:

- Goal
- Role
- Input
- Task
- Output file
- Output format
- Milestone-specific rules
- Success criteria
- Completion response

The prompt must continue to define the exact task and the exact files that may be changed.

Important rules can still be repeated when they are critical for the active milestone.

## Original workflow

The repository state used for the first three Prompt-Driven Development articles is preserved by the tag:

```text
pdd-article-3-workflow-snapshot
```

The original prompts remain unchanged under:

```text
docs/prompts/
```

They represent the actual prompts used during the original implementation.

## Comparison

Original service implementation prompt:

```text
docs/prompts/011_02_Service_Implementation.md
```

Updated comparison:

```text
docs/evolution/001-persistent-context/comparisons/
011_02_Service_Implementation_With_Persistent_Context.md
```

The comparison file was created after the original implementation.

It shows how the same prompt can be written after moving common repository rules into `copilot-instructions.md`.

## Important

`copilot-instructions.md` does not replace:

- `docs/Requirement.docx`
- `docs/.ai/Plan.md`
- `docs/.ai/001_API_Contract.md`
- Milestone implementation plans
- Task-specific prompts
- Tests
- Human review

The instructions provide common repository context.

The active prompt and approved implementation plan still define what should be changed.