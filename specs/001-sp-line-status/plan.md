# Implementation Plan: São Paulo Metrô & CPTM Line Status

**Branch**: `001-sp-line-status` | **Date**: 2026-06-22 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-sp-line-status/spec.md`

## Summary

Provide a mobile-first web UI that shows the current operational status of all São Paulo Metrô and
CPTM (train) lines. A Spring Boot backend polls the public CCR mobility endpoint every 60 seconds,
normalizes the response into a stable domain model, retains the last-known-good snapshot (so the UI
stays useful during upstream outages), and exposes it through a Swagger-documented REST API. A React
+ styled-components frontend consumes that API, renders lines grouped by operator with disrupted
lines surfaced first, shows data freshness, and auto-refreshes. Playwright (TypeScript) verifies the
UI; JUnit verifies the backend, test-first.

## Technical Context

**Language/Version**: Java 21 (backend); TypeScript 5.x on Node 20 LTS (frontend + Playwright).

**Primary Dependencies**: Spring Boot 4.0.3 (Spring Web, Spring Scheduling, RestClient/WebClient),
springdoc-openapi (Swagger UI/OpenAPI 3); React 18 with styled-components; Playwright Test. Build via
Maven Wrapper (backend) and npm (frontend, playwright).

**Storage**: In-memory, volatile last-known-good snapshot held by the backend (single most-recent
successful fetch). No relational persistence required for this feature; Spring Data JPA (a sanctioned
stack technology) is intentionally NOT used here because there is no durable data to model — see
Complexity Tracking and research.md.

**Testing**: JUnit 5 (+ Spring Boot Test, MockWebServer/WireMock for the upstream client) for the
backend, written test-first per Constitution Principle II; Playwright (TypeScript) for all
UI-affecting behavior per Principle III.

**Target Platform**: Modern web browsers, mobile-first responsive layout; backend runs on the JVM.

**Project Type**: Web application (separate `backend/` REST API + `frontend/` React SPA).

**Performance Goals**: Backend serves the cached snapshot in < 200 ms p95 (no upstream call on the
request path); first meaningful render of the status page < 2 s on broadband; upstream polled every
60 s.

**Constraints**: Mobile-first, no horizontal scroll at typical phone widths; display text remains in
Portuguese as provided by the source; resilient to upstream outage via last-known-good snapshot;
public data, no authentication.

**Scale/Scope**: Small and bounded — 6 operators, 13 lines today; read-mostly. Backend caching
shields the upstream API regardless of concurrent UI clients. ~1 primary screen.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| # | Principle | Plan compliance |
|---|-----------|-----------------|
| I | Spec-Driven Development & User Stories | User story recorded at `user_stories/001-sp-line-status.md`; this plan derives from `spec.md`. ✅ |
| II | Test-Driven Development for Java (NON-NEGOTIABLE) | All backend code follows Red-Green-Refactor with JUnit; tasks will order failing test → implementation. ✅ |
| III | UI Verified by Playwright (NON-NEGOTIABLE) | Every UI-affecting story gets a Playwright test that must pass before "done". ✅ |
| IV | API-First & Documented Interfaces | Backend exposes REST documented via springdoc/Swagger; contract authored in `contracts/` before consumers. ✅ |
| V | Prototype-Guided UI | UI follows Google Stitch HTML prototypes in `/prototypes`; prototype creation is an explicit setup task before frontend build. ✅ (prototypes to be produced) |
| VI | Living Documentation per Module | `backend/`, `frontend/`, `playwright/` each get a maintained `CLAUDE.md`. ✅ |
| VII | Consistent, Portable Source Hygiene | Root `.editorconfig` (UTF-8, LF) and `.gitignore` (Java + IDEs incl. VS Code) created before any code. ✅ |

**Initial gate**: PASS (no violations). One conscious deviation — not using Spring Data JPA for this
feature — is documented in Complexity Tracking as a simplification, not a violation.

**Post-Design re-check (after Phase 1)**: PASS. The data-model, REST contract, and quickstart keep
the design API-first, test-first, and prototype-guided. No new violations introduced.

## Project Structure

### Documentation (this feature)

```text
specs/001-sp-line-status/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (REST + upstream contracts)
│   ├── line-status-api.yaml
│   └── upstream-ccr-contract.md
├── checklists/
│   └── requirements.md
└── tasks.md             # Phase 2 output (/speckit.tasks — NOT created here)
```

### Source Code (repository root)

```text
backend/                         # Spring Boot REST API (Java 21, Maven Wrapper)
├── mvnw, mvnw.cmd, .mvn/
├── pom.xml
├── CLAUDE.md
└── src/
    ├── main/java/.../linestatus/
    │   ├── client/              # Upstream CCR client + DTOs
    │   ├── domain/              # Operator, Line, LineStatus, StatusSnapshot, StatusCategory
    │   ├── service/             # Normalization, ordering, snapshot cache, scheduled poller
    │   ├── api/                 # REST controller + response DTOs
    │   └── config/              # HTTP client, scheduling, OpenAPI config
    ├── main/resources/          # application.yml
    └── test/java/.../linestatus/  # JUnit: client, service, api (test-first)

frontend/                        # React + styled-components SPA (TypeScript, npm)
├── package.json
├── CLAUDE.md
└── src/
    ├── api/                     # typed client for the backend REST API
    ├── components/              # LineCard, OperatorGroup, StatusBadge, StalenessBanner, ...
    ├── pages/                   # StatusPage
    ├── hooks/                   # useLineStatus (fetch + 60s auto-refresh)
    └── theme/                   # styled-components theme

playwright/                      # Playwright UI tests (TypeScript, npm)
├── package.json
├── playwright.config.ts
├── CLAUDE.md
└── tests/                       # all-lines, ordering, freshness/refresh, error & stale states

prototypes/                      # Google Stitch HTML prototypes (UI reference)
user_stories/                    # 001-sp-line-status.md (already created)
.editorconfig                    # root, before any code (Principle VII)
.gitignore                       # root, before any code (Principle VII)
```

**Structure Decision**: Web application layout (Option 2). Matches the constitution's mandated
directories: `/backend` (Spring Boot REST), `/frontend` (React), `/playwright` (TS tests),
`/prototypes` (Stitch), `/user_stories`. Vaadin is **not** used — the frontend is the React SPA, per
the constitution's stack and the planning decision.

## Complexity Tracking

| Deviation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|--------------------------------------|
| Spring Data JPA omitted for this feature | The feature is a read-only normalizing proxy over a live upstream API; the only state is the most-recent successful snapshot, which is volatile and fits an in-memory holder. Adding JPA + a database would introduce schema, migrations, and infrastructure with no functional benefit. | Persisting snapshots was rejected because v1 has no history/audit requirement (explicitly out of scope in spec Assumptions). JPA can be introduced later if status history becomes a feature. |
