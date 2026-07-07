Project names do not need to be globally unique in Version 1.
Projects will be identified by their system-generated project ID, not by name.

Task titles do not need to be unique within the same project in Version 1.
Tasks will be identified by their system-generated task ID.

dueDate is optional.
If dueDate is provided, past dates should be rejected.
dueDate must be today or a future date.

assigneeEmail is optional during task creation.
A task can be created without an assignee and assigned later.

A task can still be edited after it reaches DONE in Version 1.
DONE does not make the task immutable.

Any status transition is allowed in Version 1 as long as the target status is one of:
TODO, IN_PROGRESS, BLOCKED, DONE.
