---
id: task-7
title: Allineare base dati RDAP con risposta ufficiale RDAP per nic.it
status: To Do
assignee: []
created_date: '2025-10-29 15:11'
updated_date: '2025-10-29 15:15'
labels:
  - rdap
  - nic.it
  - data-model
  - tests
  - snapshot
  - dto
  - versioning
  - redacted
  - jsonpath
  - rfc-9537
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
**Contesto:**  
L’implementazione attuale del servizio RDAP utilizza una struttura dati interna non ancora allineata alle risposte reali fornite dall’istanza RDAP di nic.it.  
Per garantire correttezza, interoperabilità e aderenza agli standard, è necessario utilizzare come base di riferimento la risposta RDAP ufficiale ottenuta da nic.it
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 base coerente per la logica del servizio service RdapDataStore
- [ ] #2 input/output per i test automatici (unit test e integration test
<!-- AC:END -->
