---
id: task-3.2
title: Mappatura status code RDAP↔HTTP
status: Done
assignee: []
created_date: '2025-10-22 14:28'
updated_date: '2025-10-22 14:35'
labels:
  - rdap
  - rfc-7480
  - backend
  - http
dependencies: []
parent_task_id: task-3
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Mappare errori RDAP su HTTP: 400,401,403,404,429,500… con body RDAP.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 404 per oggetto inesistente con problem details RDAP
- [x] #2 429 con Retry-After se rate limit scatta
<!-- AC:END -->
