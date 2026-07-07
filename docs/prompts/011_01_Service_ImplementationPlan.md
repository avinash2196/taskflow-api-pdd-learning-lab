Goal: The goal is to create an implementation plan for service implementation based on Plan.md and service tests already created.

Role: You are a senior backend architect.

Input: docs/.ai/Plan.md
Task: Read Plan.md and 001_API_Contract.md and create 011_Implementation_Plan_Service.md file.
- Follow the 010_Implementation_Plan_Service_Test.md and Plan.md to find the service validation tests and other tests
- This implementation plan should satisfy Milestone 11 planning
- Review the current state before writing the plan to add changes accordingly.
- Make sure we are following plan and creating only enough code to turn the tests green not any other validations or anything which was not defined or approved
- Make sure code compiles and all test cases pass after implementation of created plan
- Make sure the code follows SOLID principles and the service layer contains only business rules.
- Only create or update this file. Do not create or modify anything else.

Output file: 011_Implementation_Plan_Service.md and updated Plan.md

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 11 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Follow docs/.ai/Plan.md strictly.
- Show both Before and After Changes for comparison.
- Make sure the code readability is maintained in preview mode.
- Do not plan to change any test files other than setup if needed.
- Make sure comments are readable and explain the purpose of each class or method when comments are needed.
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Do not create .gitkeep files or placeholder files just to force empty folders into Git.
- Success criteria should be for implementation not plan.
- Do not assume. Ask clarifying questions if required.


After creating the file, respond only with:

Created 011_Implementation_Plan_Service.md for review.