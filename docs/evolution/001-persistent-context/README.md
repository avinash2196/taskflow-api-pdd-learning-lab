# Evolution 001: Persistent Repository Context

## Purpose

The original Prompt-Driven Development workflow kept important
architectural, process, and scope constraints directly inside each
milestone prompt.

That approach was intentional. It made each prompt self-contained and
made the boundaries of every task visible during the initial development
of the workflow.

After the complete prompt trail was reviewed, a repeated pattern became
clear: several instructions remained stable across planning,
implementation, testing, refactoring, and final-review milestones.

This refinement gives those stable instructions a repository-level home
in:

```text
.github/copilot-instructions.md