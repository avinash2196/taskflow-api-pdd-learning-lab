_Goal: The goal is to create an implementation plan for Controller test based on Plan.md

Role: You are a senior backend architect .

Input : docs/.ai/Plan.md
Task : Read Plan.md,001_API_Contract.md and create an 004_Implementation_Plan_Controller_Test.md file.
- Follow the API_Contract.md and Plan.md to find the Controller validation tests and other test
- This implementation plan should satisfy Milestone 4 planning
- Review the current state before writing plan to add changes accordingly.
- Make sure you cover all negative and positive cases to ensure our code is created later to handle all cases and update plan
- Do not try to add any changes in main code files . Its scope is limited to Test classes planning only.
- Target the real controllers classes already created.Do not generate placeholder controllers.
- Only create or update this file. Do not create or modify anything else.

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 4 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Follow docs/.ai/Plan.md strictly.
- Show both Before and After Changes for comparison
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

Created 004_Implementation_Plan_Controller_Test.md for review.