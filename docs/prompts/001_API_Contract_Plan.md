Goal: The goal is to create  clear API contract based on Plan.md

Role: You are a senior backend architect .

Input : docs/.ai/Plan.md

Task : Read Plan.md and create 001_API_Contract_Plan.md
- Make sure it contains all the APIs that needs to be created 
- It should contain endpoint , method, input params, success response, success response code,
failure response, failure response code 


Output file: docs/.ai/001_API_Contract_Plan.md and Plan.md

- Only create or update this file. Do not create or modify anything else.

Output Format: For each API capability from Plan.md, include:

- API purpose
- Related functional requirement ID
- Proposed endpoint
- HTTP method
- Required inputs
- Optional inputs
- Success response code
- Success response body
- Failure response codes
- Failure response body
- Validation rules

Also include:

1. Contract scope
2. Out of scope

Rules:

- Follow docs/.ai/Plan.md strictly.
- Do not invent new requirements.
- Do not generate code.
- Do not generate tests.
- Do not hallucinate.
- Update Plan.md once it passes the success criteria
- Only update milestone completion section in Plan.md
- Do not invent extra requirements or validations or improvements or observability 
- Do not create DTOs, controllers, services, repositories, database schema, README, ADR, or API spec files.
- Ask clarifying questions if any.

Success criteria:

- The contract matches docs/.ai/Plan.md.
- Each proposed API maps to a functional requirement.

After creating the file, respond only with:

Created 001_API_Contract_Plan.md for review.