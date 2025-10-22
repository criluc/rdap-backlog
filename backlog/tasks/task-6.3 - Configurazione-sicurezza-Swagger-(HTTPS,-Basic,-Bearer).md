---
id: task-6.3
title: 'Configurazione sicurezza Swagger (HTTPS, Basic, Bearer)'
status: To Do
assignee: []
created_date: '2025-10-22 14:56'
labels:
  - springdoc
  - swagger
  - security
dependencies: []
parent_task_id: task-6
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Integrare gli schemi di sicurezza (Basic e Bearer Token) nella documentazione OpenAPI tramite `SecurityScheme` e `SecurityRequirement`.
Esempio:
```java
.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
.components(new Components().addSecuritySchemes("bearerAuth",
  new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
```
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Swagger-UI mostra autenticazione via Basic e Bearer
- [ ] #2 HTTPS obbligatorio indicato nella sezione servers[]
<!-- AC:END -->
