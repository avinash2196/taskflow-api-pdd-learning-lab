Goal: The goal is to create an implementation plan for Controller and controller test refactor based on Plan.md

Role: You are a senior backend architect.

Input: docs/.ai/Plan.md
Task: Read Plan.md,004_Implementation_Plan_Controller_Test.md and 005_Implementation_Plan_Controller.md and create 008_Implementation_Plan_Controller_Refactor.md file.
- Follow the plans and make sure we suggest changes to improve the readability and performance without impacting actual functionality. 
- Make sure we follow the order while planning - refactor code and verify if tests still pass and then refactor tests and reverify.
- Ensure to provide complete code.
- This implementation plan should satisfy Milestone 8 planning.
- Review the current state before writing the plan to add changes accordingly.
- Review the controller tests and controller classes for duplicate code. If common code exists, refactor it into shared helper methods. Create helper files only when they are necessary.
- Make sure the controller follows the thin controller principle.
- Plan to remove unnecessary or outdated comments as well. 
- Do not try to add any changes in main code files. The scope is limited to planning only.
- Only create or update this file. Do not create or modify anything else.

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 8 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders.
- Follow docs/.ai/Plan.md strictly.
- Show both Before and After Changes for comparison.
- Make sure the code readability is maintained in preview mode.
- Make sure comments are readable and explain the purpose of each class or method when comments are needed.
- Use Mockito to mock the other layers if needed. Do not write mock implementations. 
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Do not create .gitkeep files or placeholder files just to force empty folders into Git.
- Success criteria should be for implementation not plan.
- Do not pull future milestone work into this plan.
- Do not assume. Ask clarifying questions if required.


After creating the file, respond only with:

Created 008_Implementation_Plan_Controller_Refactor.md for review.
