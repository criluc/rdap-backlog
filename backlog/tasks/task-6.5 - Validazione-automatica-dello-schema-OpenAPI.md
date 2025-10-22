---
id: task-6.5
title: Validazione automatica dello schema OpenAPI
status: To Do
assignee: []
created_date: '2025-10-22 14:56'
labels:
  - rdap
  - swagger
  - testing
  - ci
dependencies: []
parent_task_id: task-6
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Integrare validazione OpenAPI nel CI/CD (es. tramite `swagger-cli validate` o plugin Maven `openapi-generator`).
Obiettivo: garantire che la documentazione sia coerente con gli endpoint esposti e priva di errori sintattici.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Pipeline CI fallisce se lo schema OpenAPI non è valido
- [ ] #2 Documento generato aggiornato automaticamente ad ogni build
<!-- AC:END -->
