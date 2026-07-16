Role: You are a senior backend architect and developer.

Input: docs/.ai/Plan.md and  011_Implementation_Plan_Service.md

Goal: Complete implementation for 011_Implementation_Plan_Service.md
Output file: Code files defined in 011_Implementation_Plan_Service.md and updated Plan.md

Output Format: As defined in 011_Implementation_Plan_Service.md

Task: Read Plan.md and 011_Implementation_Plan_Service.md.
Implement the changes from 011_Implementation_Plan_Service.md

Rules:

- Refer to Plan.md and make changes as per 011_Implementation_Plan_Service.md only.
- Do not hallucinate.
- Do not invent requirements or non-functional requirements other than defined in 011_Implementation_Plan_Service.md
- Update test files only if needed for setup fix.
- Do not add any additional files or plans or description docs or other docs other than those described in the implementation plan.
- Update Plan.md after the contract passes the success criteria.
- Only update milestone completion section in Plan.md
- Do not rewrite or improve Plan.md

Success criteria:
- It implements the files defined in 011_Implementation_Plan_Service.md
- Project should build/compile and test cases should pass as it's the green state.
- We should be able to follow Success criteria defined in 011_Implementation_Plan_Service.md

Once completed, respond only with:

Implementation completed for  011_Implementation_Plan_Service.md