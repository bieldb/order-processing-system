# Order Processing System

A backend system built to explore how a real-world order processing application can evolve from a simple Java application into a production-oriented backend.

This project started with a simple question:

> **What does it actually take to build a reliable order processing system?**

Instead of starting directly with frameworks and infrastructure, the project is being developed incrementally. Each phase introduces a new layer of complexity and focuses on a specific set of backend engineering concepts.

The goal is not only to make the application work, but to understand **why each technical decision is made, what problems it solves, and what trade-offs it introduces.**

---

## About the Project

The system represents a simplified order processing domain involving customers, products, orders, inventory, and order items.

At its core, an order belongs to a customer and contains one or more products. Products have prices and stock, while orders follow a defined lifecycle:

```text
CREATED → PROCESSING → COMPLETED
    │          │
    └──────────┴────→ CANCELLED
```

The initial implementation focuses on modeling these business rules using Java itself.

As the project evolves, the same domain will be exposed through a REST API, persisted in PostgreSQL, tested with a professional testing stack, and eventually prepared for concerns such as concurrency, transactions, observability, containerization, scalability, and cloud deployment.

---

## Project Goals

This project is primarily a learning and portfolio project focused on backend engineering.

The main goals are:

* Strengthen Java fundamentals through a real domain problem
* Practice object-oriented design and encapsulation
* Model business rules explicitly
* Understand data structures and algorithmic complexity
* Build REST APIs with Spring Boot
* Work with relational databases and SQL
* Apply validation and exception handling
* Write automated tests
* Understand transactions and concurrency
* Explore database locking and consistency
* Containerize applications with Docker
* Practice Git and debugging workflows
* Introduce observability into the application
* Study system design and scalability
* Understand how a backend evolves toward a production environment

More importantly, the project is intended to develop **engineering judgment**, rather than simply demonstrate knowledge of a particular framework.

---

# Project Evolution

The project is divided into six phases.

Each phase introduces new technical challenges while building on top of the previous one.

```text
┌─────────────────────────────────────────────┐
│       Order Processing System               │
└─────────────────────────────────────────────┘
                      │
                      ▼
        ┌──────────────────────────┐
        │ Phase 1                  │
        │ Java Core & Domain       │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ Phase 2                  │
        │ Spring Boot & PostgreSQL │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ Phase 3                  │
        │ Testing & Clean Code     │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ Phase 4                  │
        │ Concurrency & Transactions│
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ Phase 5                  │
        │ Docker & Observability   │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ Phase 6                  │
        │ System Design & Cloud    │
        └──────────────────────────┘
```

---

## Phase 1 — Java Core & Domain Modeling

The first phase was intentionally developed without Spring Boot or a database.

The focus was on understanding the problem before introducing infrastructure.

The domain was modeled around:

* `Customer`
* `Order`
* `OrderItem`
* `Product`
* `OrderStatus`

Business rules such as order lifecycle transitions, inventory operations, product pricing, order totals, and invalid operations were implemented directly in Java.

Data structures were also deliberately chosen according to the problem being solved, including `HashMap`, `List`, `Optional`, streams, and UUID-based identifiers.

The phase also introduced custom domain exceptions and a first set of tests using plain Java.

**Status:** Completed

📄 Detailed documentation: `docs/phase-1-java-core.md`

---

## Phase 2 — Spring Boot, REST & PostgreSQL

The second phase moves the existing domain into a real backend application.

The main objective is to introduce infrastructure without losing the business rules established in the first phase.

This phase will introduce:

* Spring Boot
* REST API
* Spring Data JPA
* PostgreSQL
* DTOs
* Repositories
* Controllers
* Services
* Request validation
* Persistence
* API testing

The application will transition from in-memory storage to a relational database while keeping the domain behavior explicit and understandable.

**Status:** Planned / In Progress

📄 Detailed documentation: `docs/phase-2-spring-rest-postgresql.md`

---

## Phase 3 — Testing, Error Handling & Clean Code

Once the API and persistence layer are established, the next step is to make the application safer to change.

This phase focuses on:

* Unit testing
* Integration testing
* Mockito
* JUnit
* Global exception handling
* Validation
* Error responses
* Clean Code
* Separation of responsibilities
* Refactoring
* Code quality

The goal is to move from "the application works" to:

> **"The application works, and I can confidently change it."**

**Status:** Planned

📄 Detailed documentation: `docs/phase-3-testing-clean-code.md`

---

## Phase 4 — Concurrency, Transactions & Locking

Order processing becomes significantly more interesting when multiple requests can modify the same resources at the same time.

This phase will explore problems such as:

* Race conditions
* Concurrent order processing
* Inventory consistency
* Transaction boundaries
* Database isolation
* Optimistic locking
* Pessimistic locking
* Concurrent requests
* Atomic operations

The objective is to understand what happens when the application is no longer executing one operation at a time.

**Status:** Planned

📄 Detailed documentation: `docs/phase-4-concurrency-transactions.md`

---

## Phase 5 — Docker, Git, Debugging & Observability

A backend application is more than its source code.

This phase focuses on the operational side of the system.

Topics include:

* Docker
* Docker Compose
* Application configuration
* Environment variables
* Git workflows
* Debugging
* Logging
* Metrics
* Health checks
* Observability
* Troubleshooting

The objective is to understand how developers interact with an application when something goes wrong outside of their IDE.

**Status:** Planned

📄 Detailed documentation: `docs/phase-5-docker-observability.md`

---

## Phase 6 — System Design, Cloud & Scalability

The final phase takes a step back from the implementation and looks at the system as a whole.

The goal is to understand how the architecture could evolve if the number of users, orders, and requests increased significantly.

Topics will include:

* System design
* Scalability
* Load balancing
* Caching
* Asynchronous processing
* Message queues
* Database scaling
* Availability
* Reliability
* Cloud architecture
* Bottleneck analysis
* Architectural trade-offs

The focus will not be on adding complexity for its own sake.

Instead, each architectural decision will be connected to a specific problem.

**Status:** Planned

📄 Detailed documentation: `docs/phase-6-system-design-cloud.md`

---

# Technology Stack

The stack evolves throughout the project.

### Current / Core

* **Java 21**
* Object-Oriented Programming
* Java Collections
* Streams
* `BigDecimal`
* `UUID`
* `LocalDateTime`
* Exception handling

### Backend

* **Spring Boot**
* Spring Web
* Spring Data JPA
* Bean Validation
* Hibernate

### Database

* **PostgreSQL**
* SQL
* Relational data modeling
* Transactions
* Database constraints
* Locking

### Testing

* **JUnit**
* **Mockito**
* Unit tests
* Integration tests

### Infrastructure

* **Docker**
* Docker Compose
* Git
* Logging
* Observability

### Architecture & Design

* REST
* Layered architecture
* Domain modeling
* DTOs
* System Design
* Asynchronous processing
* Scalability

---

# Architecture

The architecture will evolve together with the project.

The initial implementation intentionally keeps the system simple:

```text
Domain
  │
  ├── Customer
  ├── Order
  ├── OrderItem
  ├── Product
  └── OrderStatus

Service
  │
  └── OrderService
```

As infrastructure is introduced, the application will gradually evolve toward a layered backend architecture:

```text
                 Client
                   │
                   ▼
              REST API
                   │
                   ▼
              Controller
                   │
                   ▼
               Service
                   │
          ┌────────┴────────┐
          ▼                 ▼
       Domain           Repository
                              │
                              ▼
                         PostgreSQL
```

The architecture is intentionally allowed to evolve instead of being over-engineered from the beginning.

---

# Engineering Principles

Throughout the project, technical decisions will be guided by a few principles.

### Start with the problem

Technology should solve a problem, not become the problem.

### Keep business rules explicit

Important rules should be easy to find, understand, and test.

### Prefer simple solutions first

Complexity should be introduced when there is a reason for it.

### Understand trade-offs

There is rarely a universally "best" solution. Different approaches solve different problems.

### Design for change

The code should be structured so that new requirements can be introduced without unnecessary modifications across the entire system.

### Measure before optimizing

Performance improvements should be driven by actual bottlenecks rather than assumptions.

### Learn from failures

Bugs, design mistakes, and implementation problems are treated as part of the learning process.

---

# Project Structure

The repository is organized so that implementation and technical documentation evolve together.

```text
order-processing-system/
│
├── src/
│   ├── main/
│   │   └── java/
│   │
│   └── test/
│       └── java/
│
├── docs/
│   ├── phase-1-java-core.md
│   ├── phase-2-spring-rest-postgresql.md
│   ├── phase-3-testing-clean-code.md
│   ├── phase-4-concurrency-transactions.md
│   ├── phase-5-docker-observability.md
│   └── phase-6-system-design-cloud.md
│
├── .gitignore
├── pom.xml
└── README.md
```

The `README.md` provides the high-level view of the project.

The documents inside `docs/` contain the detailed technical history of each phase.

---

# Technical Documentation

Each completed phase has its own technical document.

These documents are intended to explain not only **what was implemented**, but also:

* Why a decision was made
* What alternatives were considered
* What problems appeared
* How those problems were solved
* What was learned
* What could be improved
* What trade-offs were involved

This makes the repository more than a collection of source files.

It becomes a record of the engineering process behind the application.

| Phase | Topic                                | Status      |
| ----- | ------------------------------------ | ----------- |
| 1     | Java Core & Domain Modeling          | ✅ Completed |
| 2     | Spring Boot, REST & PostgreSQL       | 🔄 Next     |
| 3     | Testing, Error Handling & Clean Code | ⏳ Planned   |
| 4     | Concurrency, Transactions & Locking  | ⏳ Planned   |
| 5     | Docker, Debugging & Observability    | ⏳ Planned   |
| 6     | System Design, Cloud & Scalability   | ⏳ Planned   |

---

# What I Am Trying to Learn

This project is not intended to demonstrate that I can simply create endpoints or perform CRUD operations.

The larger goal is to understand the reasoning behind backend engineering.

I want to be able to look at a problem and ask:

* What are the actual business rules?
* Where should each responsibility live?
* Which data structure makes sense here?
* What happens when two requests arrive at the same time?
* How should the database guarantee consistency?
* How should failures be handled?
* How do I know when the system is healthy?
* Where could the system become a bottleneck?
* When does a simple architecture stop being enough?
* What are the trade-offs behind a more complex solution?

By answering these questions throughout the project, the application becomes a practical way to study backend engineering from the inside out.

---

# Current Status

**Phase 1 — Completed**

The core domain and business rules have been implemented using Java 21, including order lifecycle management, inventory operations, order calculations, custom exceptions, in-memory persistence, and initial tests.

**Next step: Phase 2 — Spring Boot + REST + PostgreSQL**

The next objective is to take the domain that was built in plain Java and turn it into a real backend application with an HTTP API and persistent storage.

---

# Final Goal

By the end of the project, the objective is to have a complete backend system that demonstrates the evolution from:

```text
Java fundamentals
       ↓
Domain modeling
       ↓
REST API
       ↓
Relational persistence
       ↓
Automated testing
       ↓
Concurrency & transactions
       ↓
Docker & observability
       ↓
System design
       ↓
Scalability & cloud
```

More than the final application itself, the project is about understanding **how and why a backend system evolves as its requirements become more demanding.**

That is the real purpose of this project.
