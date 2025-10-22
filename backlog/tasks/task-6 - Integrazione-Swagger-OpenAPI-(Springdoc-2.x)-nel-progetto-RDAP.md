---
id: task-6
title: Integrazione Swagger / OpenAPI (Springdoc 2.x) nel progetto RDAP
status: To Do
assignee: []
created_date: '2025-10-22 14:56'
updated_date: '2025-10-22 14:56'
labels:
  - rdap
  - swagger
  - openapi
  - docs
  - backend
  - spring
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Integrare Swagger / OpenAPI (Springdoc 2.x) nel servizio RDAP per generare e visualizzare automaticamente la documentazione di tutti gli endpoint implementati secondo gli RFC:
- RFC 7480 (HTTP usage)
- RFC 9082 (query format)
- RFC 9083 (JSON responses)
- RFC 7481 (security)
- RFC 9537 (redacted fields)
- draft-ietf-regext-rdap-jscontact (JSContact)

**Obiettivi principali:**
- Esposizione automatica di /v3/api-docs e interfaccia Swagger-UI su /swagger-ui.html
- Annotazione dei controller RDAP con @Operation e @Schema per ogni tipo di oggetto (domain, entity, nameserver, autnum, ip, help)
- Includere esempi di risposte RDAP JSON, error body e parametri di ricerca (lookup/search)
- Documentare meccanismi di autenticazione (Basic e Bearer Token) e HTTPS obbligatorio
- Inserire tag e gruppi coerenti con le sezioni RFC (lookup, search, security, redaction, jscontact)
- Aggiornare la documentazione in modo dinamico durante il build (springdoc-openapi-starter-webmvc-ui)

**Rif:**
- https://springdoc.org/
- RFC 9083 (modelli JSON)
- RFC 7481 (security)
- RFC 9537 (privacy)
- draft-ietf-regext-rdap-jscontact (estensione contatti)
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Swagger-UI accessibile su /swagger-ui.html e /v3/api-docs
- [ ] #2 Tutti gli endpoint RDAP (domain, entity, nameserver, autnum, ip, help) documentati con schemi JSON conformi agli RFC
- [ ] #3 Endpoint JSContact e redacted documentati con esempi dedicati
- [ ] #4 Schema di sicurezza HTTPS + Basic e Bearer Token incluso nel documento OpenAPI
- [ ] #5 Esempi di richieste e risposte RDAP presenti in Swagger-UI
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1) Aggiungere dipendenza springdoc-openapi-starter-webmvc-ui
2) Configurare bean OpenAPI con info RDAP
3) Annotare controller con @Operation e @Schema
4) Integrare sicurezza Basic/Bearer
5) Aggiungere esempi RDAP JSON per ogni RFC
6) Validare documentazione in CI/CD
<!-- SECTION:PLAN:END -->
