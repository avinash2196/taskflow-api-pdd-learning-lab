Goal: The goal is to create an implementation plan for Controller  based on Plan.md and Controller tests created.

Role: You are a senior backend architect.

Input : docs/.ai/Plan.md
Task : Read Plan.md,001_API_Contract.md and create an 007_Implementation_Plan_Controller_Validation.md file.
- Follow the 007_Implementation_Plan_Controller_Validation.md and  Plan.md to find the Controller validation tests and other exception test
- This implementation plan should satisfy Milestone 7 planning
- Review the current state before writing plan to add changes accordingly.
- Make sure we are following plan and creating code only to turn test green not any other validations or anything which was not defined or approved
- Make sure Controller follow thin controller principle.
- Add Global Exception handler and other shared exception classes if needed
- Make sure code compiles and all test cases are passed after implementation of created plan
- If there are any static classes which are used for testing but needs to be pulled from main src now , then update that as well . Change test files only for this ,if required
- Only create or update this file. Do not create or modify anything else.

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 7 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Follow docs/.ai/Plan.md strictly.
- Show both Before and After Changes for comparison.
- Make sure the code readability is maintained in preview mode.
- Do not plan to change any test files
- Make sure comments are readable and explain the purpose of each class or method when comments are needed.
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Do not create .gitkeep files or placeholder files just to force empty folders into Git.
- Success criteria should be for implementation not plan.
- Do not assume. Ask clarifying questions if required.


After creating the file, respond only with:

Created 007_Implementation_Plan_Controller_Validation.md for review.