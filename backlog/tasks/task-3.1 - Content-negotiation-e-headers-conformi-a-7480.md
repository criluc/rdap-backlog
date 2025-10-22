---
id: task-3.1
title: Content negotiation e headers conformi a 7480
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
Implementare Accept, charset, ETag/Last-Modified, Cache-Control secondo 7480.
Rif: RFC 7480.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Richieste con Accept errato rispondono 406 con body RDAP error
- [x] #2 ETag/If-None-Match gestiti su risorse statiche (help) e dinamiche
<!-- AC:END -->
