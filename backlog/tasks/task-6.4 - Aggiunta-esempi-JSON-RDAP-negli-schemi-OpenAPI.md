---
id: task-6.4
title: Aggiunta esempi JSON RDAP negli schemi OpenAPI
status: To Do
assignee: []
created_date: '2025-10-22 14:56'
labels:
  - rdap
  - swagger
  - examples
  - docs
dependencies: []
parent_task_id: task-6
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Popolare la documentazione con esempi completi di risposte RDAP conformi:
- domain/entity/nameserver/autnum/ip/help (RFC 9083)
- redacted (RFC 9537)
- JSContact (draft-ietf-regext-rdap-jscontact)
- error body standard (code, title, description, links)
Usare `@ExampleObject` e includere snippet realistici.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Swagger-UI mostra esempi RDAP completi per ciascun endpoint
- [ ] #2 Schemi JSON coerenti con i modelli DTO e le risposte reali del servizio
<!-- AC:END -->
