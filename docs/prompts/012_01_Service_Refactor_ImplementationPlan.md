Goal: The goal is to create an implementation plan for service and service test refactor based on Plan.md

Role: You are a senior backend architect .

Input : docs/.ai/Plan.md
Task : Read Plan.md,010_Implementation_Plan_Service_Test.md,011_Implementation_Plan_Service.md and create an 012_Implementation_Plan_Service_Refactor.md file.
- Follow the plans and make sure we suggest changes to improve the readability and performance with impacting actual functionality. 
- Make sure we follow the order while planning - refactor code and verify if tests still pass and them refactor tests and reverify
- Ensure to provide complete code.
- This implementation plan should satisfy Milestone 12 planning
- Review the current state before writing plan to add changes accordingly.
- Make sure common codes if exists in service test and service , then refactor it in common method .Create Helper files if needed.
- Make sure code follows SOLID principle and service contains only business rules. 
- Plan to remove un-necessary or outdated comments as well. 
- Do not try to add any changes in main code files . Its scope is limited to Test classes planning only.
- Only create or update this file. Do not create or modify anything else.

Output file: 012_Implementation_Plan_Service_Refactor.md and updated Plan.md

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 8 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Follow docs/.ai/Plan.md strictly.
- Show both Before and After Changes for comparison.
- Make sure the code readability is maintained in preview mode.
- Make sure comments are readable and define the usage and purpose of code.( class /method.logic)
- Use Mockito to mock the other layers if needed. Do not write mock implementations 
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Do not create .gitkeep files or placeholder files just to force empty folders into Git.
- Success criteria should be for implementation not plan.
- Do not pull future milestone work into this plan
- Do not assume, Ask clarifying questions if required.


After creating the file, respond only with:

Created 012_Implementation_Plan_Service_Refactor.md for review._