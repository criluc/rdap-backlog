---
id: task-4.3
title: Negoziazione formato (jCard vs JSContact) e media-type extensions
status: Done
assignee: []
created_date: '2025-10-22 14:52'
updated_date: '2025-10-22 15:09'
labels:
  - rdap
  - api
  - content-negotiation
  - backend
dependencies: []
parent_task_id: task-4
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare selezione del formato contatti tramite Accept / parametri media-type (estensioni) come previsto dai riferimenti del draft (x-media-type) e pubblicare il valore in rdapConformance.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Client può ottenere JSContact senza rompere clienti jCard
- [x] #2 Valori di conformance coerenti (rdapConformance include l’estensione JSContact)
<!-- AC:END -->
