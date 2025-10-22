---
id: task-4
title: 'RDAP + JSContact (draft): rappresentazione contatti'
status: To Do
assignee: []
created_date: '2025-10-22 14:51'
labels:
  - rdap
  - jscontact
  - backend
  - json
  - rfc-9083
  - draft
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Implementare l’estensione RDAP che rappresenta le info di contatto con JSContact al posto di jCard, come da draft-ietf-regext-rdap-jscontact.
Include percorso di transizione jCard→JSContact, mapping dei campi, e segnalazione di conformità.
Rif: draft-ietf-regext-rdap-jscontact (Experimental), dipendenze su RFC 9083; richiami a RFC 9553 (JSContact) e JSONPath RFC 9535.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Le entity RDAP possono essere restituite con payload JSContact valido (RFC 9553)
- [ ] #2 Presente segnalazione di conformance dell’estensione (valore in rdapConformance come da draft)
- [ ] #3 Disponibile modalità di transizione: server supporta jCard e JSContact e fornisce indicazioni per il client
<!-- AC:END -->
