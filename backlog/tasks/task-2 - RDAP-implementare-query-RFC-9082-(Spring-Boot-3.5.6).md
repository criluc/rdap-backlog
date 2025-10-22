---
id: task-2
title: 'RDAP: implementare query RFC 9082 (Spring Boot 3.5.6)'
status: Done
assignee: []
created_date: '2025-10-22 11:20'
updated_date: '2025-10-22 11:37'
labels:
  - rdap
  - backend
  - api
  - standards
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare un servizio RDAP base conforme a RFC 9082 (query format) con risposte JSON secondo RFC 9083. Include lookup (/domain, /nameserver, /entity, /ip, /autnum, /help) e search (/domains, /nameservers, /entities) minime.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Le URL di lookup e search rispettano i path e la semantica RFC 9082.
- [x] #2 Le risposte JSON sono compatibili con RFC 9083 (con campi minimi e link).
- [x] #3 Help endpoint disponibile e documentato.
- [x] #4 Test di integrazione per ogni endpoint base (2-3 casi per tipo).
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
RFC 9082: https://www.rfc-editor.org/rfc/rfc9082 ; RFC 9083: https://www.rfc-editor.org/rfc/rfc9083
<!-- SECTION:NOTES:END -->
