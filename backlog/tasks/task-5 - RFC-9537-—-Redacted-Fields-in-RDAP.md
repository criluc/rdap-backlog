---
id: task-5
title: RFC 9537 — Redacted Fields in RDAP
status: To Do
assignee: []
created_date: '2025-10-22 14:55'
labels:
  - rdap
  - security
  - privacy
  - epic
  - redacted
  - jsonpath
  - rfc-9537
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare l’estensione di redazione dei campi RDAP con JSONPath, metodi: removal, emptyValue, partial, replacement.
Aggiungere membro "redacted" nelle risposte e valore "redacted" in rdapConformance.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 rdapConformance include "redacted"
- [ ] #2 Presente membro "redacted" quando almeno un campo è redatto
- [ ] #3 Supportati i 4 metodi di redazione con JSONPath valido
<!-- AC:END -->
