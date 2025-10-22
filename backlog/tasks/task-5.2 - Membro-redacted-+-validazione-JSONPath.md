---
id: task-5.2
title: Membro redacted + validazione JSONPath
status: To Do
assignee: []
created_date: '2025-10-22 14:55'
labels:
  - rdap
  - privacy
  - jsonpath
dependencies: []
parent_task_id: task-5
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Aggiungere membro "redacted" per ogni risposta con campi redatti; includere perPath/prePath e method.
Validare le espressioni JSONPath sui payload non redatti prima dell’applicazione.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 JSONPath assoluto a partire da $ (es. $.handle)
- [ ] #2 Pre-validazione: l’espressione seleziona il campo atteso prima della redazione
<!-- AC:END -->
