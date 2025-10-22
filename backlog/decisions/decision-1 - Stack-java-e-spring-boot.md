---
id: decision-1
title: Stack java e spring boot
date: '2025-10-22 12:04'
status: proposed
---
## Context

Il progetto richiede uno stack backend robusto, scalabile e ampiamente
supportato dalla community per sviluppare servizi RESTful e un layer
di persistenza dati affidabile. È fondamentale standardizzare le
scelte di linguaggio, framework, ORM, database e strumenti di build
per garantire manutenibilità e coerenza.


## Decision

Le seguenti decisioni sono state prese per lo stack backend del
progetto:

| Categoria | Scelta | Versione |
| :--- | :--- | :--- |
| **Linguaggio** | Java | 25 |
| **Framework** | Spring Framework | 3.5 (Ultima Versione) |
| **Persistenza (ORM)**| Hibernate | Ultima Versima Stabile |
| **Database** | MySQL | Standard |
| **Build System** | Maven | Standard |
| **Documentazione API** | OpenAPI e Swagger | Standard |
| **Testing (Mocking)** | Mockito | Standard |

***

### 3. Motivazioni (Rationale) 🧠

* **Java 25:** Offre le ultime ottimizzazioni in termini di
performance e sintassi moderna (es. *Virtual Threads*, *Record*) pur
mantenendo la stabilità e l'ecosistema enterprise di Java.
* **Spring Framework 3.5:** Fornisce una piattaforma completa per lo
sviluppo enterprise, inclusi i moduli per la sicurezza, la gestione
delle transazioni e la creazione di servizi REST. L'ultima versione
garantisce il supporto alle feature moderne di Java.
* **Hibernate:** È il più maturo e diffuso ORM nel mondo Java, si
integra perfettamente con Spring Data JPA, semplificando le operazioni
CRUD e il *mapping* relazionale.
* **MySQL:** Un database relazionale *open-source* solido, affidabile
e ampiamente supportato per la gestione dei dati transazionali.
* **Maven:** È lo strumento di *build* standard de facto in Java.
Garantisce la gestione automatica delle dipendenze e un ciclo di
*build* replicabile.
* **OpenAPI (Swagger):** Standard di settore per la documentazione
delle API REST, che può essere generata automaticamente da Spring e
garantisce una comunicazione chiara tra frontend, backend e AI Agents.
* **Mockito:** La libreria più popolare per il mocking nei test
unitari Java, essenziale per isolare le unità di codice e scrivere
test rapidi ed efficaci.

***

## Consequences

* Tutti i nuovi servizi e moduli di codice backend **devono** essere
sviluppati utilizzando **Java 25** e **Spring Framework 3.5**.
* Il *data access layer* **deve** utilizzare **Hibernate/JPA** e non
SQL nativo, se non strettamente necessario.
* Ogni endpoint REST **deve** essere documentato seguendo le
specifiche **OpenAPI** (spesso implementato tramite annotazioni in
Spring).
* I test unitari **devono** utilizzare **Mockito** per la creazione di
mock e stub.
* Il file `pom.xml` (Maven) sarà la fonte autorevole per la gestione
di tutte le dipendenze e delle versioni menzionate.