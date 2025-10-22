---
id: task-3
title: 'RFC 7480 — HTTP usage: fondamenta server'
status: Done
assignee: []
created_date: '2025-10-22 14:27'
updated_date: '2025-10-22 14:36'
labels:
  - rdap
  - rfc-7480
  - backend
  - http
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare la base HTTP per RDAP secondo RFC 7480:
- Metodi, status code, header, content negotiation
- Content-Type: application/rdap+json
- Gestione cache e redirect dove previsto
Rif: https://www.rfc-editor.org/rfc/rfc7480.html
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Tutte le risposte RDAP hanno Content-Type application/rdap+json
- [x] #2 Status code HTTP conformi a 7480 per success/error
- [x] #3 Header cache/Expires/ETag coerenti con esempi RFC
<!-- AC:END -->
