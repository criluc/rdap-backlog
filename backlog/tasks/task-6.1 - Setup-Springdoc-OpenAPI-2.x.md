---
id: task-6.1
title: Setup Springdoc OpenAPI 2.x
status: To Do
assignee: []
created_date: '2025-10-22 14:56'
labels:
  - springdoc
  - swagger
  - config
dependencies: []
parent_task_id: task-6
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Aggiungere la dipendenza Maven/Gradle `springdoc-openapi-starter-webmvc-ui`.
Configurare il bean `OpenAPI` per titolo, versione e descrizione RDAP.
Esempio:
```java
@Bean
public OpenAPI rdapOpenAPI() {
  return new OpenAPI()
    .info(new Info()
      .title("RDAP API")
      .version("1.0.0")
      .description("Implementazione RDAP conforme agli RFC 7480, 9082, 9083, 7481, 9537 e JSContact"));
}
```
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Springdoc attivo e raggiungibile su /swagger-ui.html
- [ ] #2 Titolo e descrizione OpenAPI personalizzati per RDAP
<!-- AC:END -->
