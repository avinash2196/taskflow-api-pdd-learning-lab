# API Contract Plan

## 1. Contract Scope

This contract covers Version 1 REST-style APIs for:

- Creating a project
- Creating a task under an existing project
- Assigning or updating a task assignee
- Changing task status
- Listing tasks by project
- Listing tasks by status
- Listing tasks by assignee
- Returning consistent success and error responses for supported operations

## 2. Out of Scope

- User authentication and authorization
- User profile management
- User interface or frontend application
- Email, Slack, or push notifications
- Task comments, attachments, or file uploads
- Audit logging and event streaming
- External integrations
- Production deployment, monitoring, and infrastructure automation
- Pagination, sorting, and advanced search
- PostgreSQL deployment profile and production configuration

## 3. API Capabilities

### API 1: Create Project

- API purpose: Create a project with a required name and an optional description.
- Related functional requirement ID: FR-001
- Proposed endpoint: `/api/projects`
- HTTP method: `POST`
- Required inputs:
  - `name`
- Optional inputs:
  - `description`
- success status code: `201 Created`
- Success response body:
```json
{
  "id": "string",
  "name": "string",
  "description": "string or null",
  "createdDate": "date-time",
  "updatedDate": "date-time"
}
```
- failure status codes:
  - `400 Bad Request`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `name` is required.
  - `description` is optional.

### API 2: Create Task Under Existing Project

- API purpose: Create a task under an existing project.
- Related functional requirement ID: FR-002
- Proposed endpoint: `/api/projects/{projectId}/tasks`
- HTTP method: `POST`
- Required inputs:
  - `projectId` as path parameter
  - `title`
- Optional inputs:
  - `description`
  - `assigneeEmail`
  - `dueDate`
  - `status`
- success status code: `201 Created`
- Success response body:
```json
{
  "id": "string",
  "projectId": "string",
  "title": "string",
  "description": "string or null",
  "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
  "assigneeEmail": "string or null",
  "dueDate": "date or null",
  "createdDate": "date-time",
  "updatedDate": "date-time"
}
```
- failure status codes:
  - `400 Bad Request`
  - `404 Not Found`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `projectId` must reference an existing project.
  - `title` is required.
  - `description` is optional.
  - `assigneeEmail` is optional.
  - If `assigneeEmail` is provided, invalid email format must be rejected.
  - `dueDate` is optional.
  - If `dueDate` is provided, past dates must be rejected.
  - `dueDate` must be today or a future date when provided.
  - If `status` is not provided, the default value is `TODO`.
  - If `status` is provided, it must be one of `TODO`, `IN_PROGRESS`, `BLOCKED`, or `DONE`.

### API 3: Assign or Update Task Assignee

- API purpose: Assign or update the assignee email for an existing task.
- Related functional requirement ID: FR-003
- Proposed endpoint: `/api/tasks/{taskId}/assignee`
- HTTP method: `PATCH`
- Required inputs:
  - `taskId` as path parameter
  - `assigneeEmail`
- Optional inputs:
  - None
- success status code: `200 OK`
- Success response body:
```json
{
  "id": "string",
  "projectId": "string",
  "title": "string",
  "description": "string or null",
  "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
  "assigneeEmail": "string",
  "dueDate": "date or null",
  "createdDate": "date-time",
  "updatedDate": "date-time"
}
```
- failure status codes:
  - `400 Bad Request`
  - `404 Not Found`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `taskId` must reference an existing task.
  - `assigneeEmail` must be a valid email format.

### API 4: Change Task Status

- API purpose: Change the status of an existing task.
- Related functional requirement ID: FR-004
- Proposed endpoint: `/api/tasks/{taskId}/status`
- HTTP method: `PATCH`
- Required inputs:
  - `taskId` as path parameter
  - `status`
- Optional inputs:
  - None
- success status code: `200 OK`
- Success response body:
```json
{
  "id": "string",
  "projectId": "string",
  "title": "string",
  "description": "string or null",
  "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
  "assigneeEmail": "string or null",
  "dueDate": "date or null",
  "createdDate": "date-time",
  "updatedDate": "date-time"
}
```
- failure status codes:
  - `400 Bad Request`
  - `404 Not Found`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `taskId` must reference an existing task.
  - `status` must be one of `TODO`, `IN_PROGRESS`, `BLOCKED`, or `DONE`.

### API 5: List Tasks by Project

- API purpose: Retrieve tasks for a specific project.
- Related functional requirement ID: FR-005
- Proposed endpoint: `/api/projects/{projectId}/tasks`
- HTTP method: `GET`
- Required inputs:
  - `projectId` as path parameter
- Optional inputs:
  - None
- success status code: `200 OK`
- Success response body:
```json
[
  {
    "id": "string",
    "projectId": "string",
    "title": "string",
    "description": "string or null",
    "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
    "assigneeEmail": "string or null",
    "dueDate": "date or null",
    "createdDate": "date-time",
    "updatedDate": "date-time"
  }
]
```
- failure status codes:
  - `404 Not Found`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `projectId` must reference an existing project.

### API 6: List Tasks by Status

- API purpose: Retrieve tasks filtered by status.
- Related functional requirement ID: FR-006
- Proposed endpoint: `/api/tasks`
- HTTP method: `GET`
- Required inputs:
  - `status` as query parameter
- Optional inputs:
  - None
- success status code: `200 OK`
- Success response body:
```json
[
  {
    "id": "string",
    "projectId": "string",
    "title": "string",
    "description": "string or null",
    "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
    "assigneeEmail": "string or null",
    "dueDate": "date or null",
    "createdDate": "date-time",
    "updatedDate": "date-time"
  }
]
```
- failure status codes:
  - `400 Bad Request`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `status` must be one of `TODO`, `IN_PROGRESS`, `BLOCKED`, or `DONE`.

### API 7: List Tasks by Assignee

- API purpose: Retrieve tasks filtered by assignee email.
- Related functional requirement ID: FR-007
- Proposed endpoint: `/api/tasks`
- HTTP method: `GET`
- Required inputs:
  - `assigneeEmail` as query parameter
- Optional inputs:
  - None
- success status code: `200 OK`
- Success response body:
```json
[
  {
    "id": "string",
    "projectId": "string",
    "title": "string",
    "description": "string or null",
    "status": "TODO | IN_PROGRESS | BLOCKED | DONE",
    "assigneeEmail": "string or null",
    "dueDate": "date or null",
    "createdDate": "date-time",
    "updatedDate": "date-time"
  }
]
```
- failure status codes:
  - `400 Bad Request`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - `assigneeEmail` must be a valid email format.

## 4. Consistent Error Response Contract

- API purpose: Define the shared failure contract for validation errors, missing resources, and unsupported operations.
- Related functional requirement ID: FR-008
- Proposed endpoint: Applies to all Version 1 endpoints
- HTTP method: Applies to all supported methods
- Required inputs:
  - A failing request
- Optional inputs:
  - None
- success status code: Not applicable
- Success response body: Not applicable
- failure status codes:
  - `400 Bad Request`
  - `404 Not Found`
  - `405 Method Not Allowed`
- Failure response body:
```json
{
  "errorCode": "string",
  "message": "string",
  "context": {
    "field": "string"
  }
}
```
- Validation rules:
  - Error responses must include a machine-readable error code.
  - Error responses must include a human-readable message.
  - Error responses must include enough context for the caller to identify the failing input.
