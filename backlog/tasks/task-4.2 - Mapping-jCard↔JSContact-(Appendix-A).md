---
id: task-4.2
title: Mapping jCard↔JSContact (Appendix A)
status: To Do
assignee: []
created_date: '2025-10-22 14:52'
labels:
  - rdap
  - jscontact
  - mapping
  - jsonpath
dependencies: []
parent_task_id: task-4
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare il mapping più comune: jCard fn/n, email, tel, adr ⇄ JSContact name/components, emails, phones, addresses.
Usare gli indici jCard corretti e i percorsi JSONPath indicati nell’appendice di mapping.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Campi jCard fn/given/family mappati su name.full e name.components (JSContact)
- [ ] #2 Suite di test con esempi del draft (JSONPath verificati sugli esempi)
<!-- AC:END -->
