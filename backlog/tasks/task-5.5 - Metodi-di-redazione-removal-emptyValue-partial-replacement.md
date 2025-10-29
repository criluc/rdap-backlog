---
id: task-5.5
title: 'Metodi di redazione: removal / emptyValue / partial / replacement'
status: To Do
assignee: []
created_date: '2025-10-29 14:58'
labels:
  - rdap
  - privacy
  - jsonpath
dependencies: []
parent_task_id: task-5
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare i metodi: 
- removal (default)
- emptyValue (obbligatorio per campi jCard dove l’indice è semantico, es. fn)
- partial (mascheramento porzioni)
- replacement (valore sostitutivo dove previsto)
Validare con esempi dello standard.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Rimozione di oggetti interi dove consentito; non rimuovere elementi posizionali jCard
- [ ] #2 Redazione di fn jCard con emptyValue come da RFC
<!-- AC:END -->
