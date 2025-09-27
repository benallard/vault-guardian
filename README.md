# VaultGuardian
*Subtitle: KassenWächter*

---

## Overview

**VaultGuardian** is a lightweight, embedded event-sourcing application aimed at digitally managing and tracking the contents of a physical locked cash box — a “Kasse.”

It will enable accurate recording of receipts, refills, and manual cash counts, ensuring the expected cash balance is always consistent with recorded events.

---

## Key Features (Planned)

- **Event Sourcing**: Domain events will represent all changes — receipts, refills, counts — making the cash box state fully auditable and replayable.
- **Embedded Event Store**: Considering **Chronicle Queue**, a high-performance, file-based queue, for durable, embedded event storage without external dependencies.
- **Domain Modeling & Event Sourcing Framework**: Evaluating **Occurrent** to model domain events, deciders, and reactive event handling cleanly.
- **Spring Boot**: To serve as the foundational framework for easy deployment, dependency injection, and integration.
- **Vaadin UI**: Planning to use Vaadin for a rich, interactive web UI to manage the cash box seamlessly.

---

## Architectural Decisions (Planned)

### Event Store

We are exploring **Chronicle Queue** for its embedded, low-latency, durable storage capabilities that fit well with an embedded app scenario.

### Domain Modeling & Event Sourcing Framework

**Occurrent** is under consideration for its clean approach to event sourcing, domain modeling, and reactive programming support.

### Backend Framework

**Spring Boot** will provide a stable foundation for dependency management, configuration, and integration between components.

### User Interface Framework

We are considering **Vaadin** for building a JVM-native, component-based web UI that integrates smoothly with Spring Boot.

---

## Next Steps

- Finalize framework selections for event store, domain modeling, and UI.
- Begin prototyping integration layers and sample event flows.
- Implement a minimal viable product that demonstrates event sourcing over an embedded store with a basic UI.

---