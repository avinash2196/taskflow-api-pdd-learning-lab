Goal: The goal is to create an implementation plan for DTO, API, Domain model and Service contract Creation based on Plan.md

Role: You are a senior backend architect.

Input: docs/.ai/Plan.md
Task: Read Plan.md and 001_API_Contract.md and create 003_Implementation_Plan_API_Model_Service_Plan.md file.
- Follow the API_Contract.md to find the API contracts
- This implementation plan should satisfy Milestone 3 planning
- Review the current state before writing the plan to add changes accordingly.

- Only create or update this file. Do not create or modify anything else.

Output Format: 
- Milestone description
- Files to be updated/changed
- Exact code changes with comments 
- Out of scope
- Success criteria


Rules:
- Only include items explicitly required by Milestone 3 or explicitly requested in this prompt.
- Use Standard Java naming conventions for files as well as folders
- Show both Before and After Changes for comparison
- Make sure the code readability is maintained in preview mode.
- Follow docs/.ai/Plan.md strictly.
- Make sure comments are readable and explain the purpose of each class or method when comments are needed.
- Do not invent requirements or non-functional requirements.
- Do not create or update actual source code files yet.
- Code/config snippets are allowed only inside this implementation plan to show the intended skeleton structure.
- Do not create controller, service, repository or config layers.
- Do not create .gitkeep files or placeholder files just to force empty folders into Git.
- Success criteria should be for implementation not plan.
- Do not assume. Ask clarifying questions if required.


After creating the file, respond only with:

Created 003_Implementation_Plan_API_Model_Service_Plan.md for review.