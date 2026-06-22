<!--
SYNC IMPACT REPORT
==================
Version change: (template, unversioned) → 1.0.0
Bump rationale: Initial ratification of the project constitution (no prior version).

Modified principles: N/A (initial adoption)

Added sections:
  - Core Principles (7 principles):
      I.   Spec-Driven Development & User Stories
      II.  Test-Driven Development for Java (NON-NEGOTIABLE)
      III. UI Verified by Playwright (NON-NEGOTIABLE)
      IV.  API-First & Documented Interfaces
      V.   Prototype-Guided User Interface
      VI.  Living Documentation per Module
      VII. Consistent, Portable Source Hygiene
  - Technology Stack & Project Structure
  - Development Workflow & Quality Gates
  - Governance

Removed sections: None.

Templates requiring updates:
  ✅ .specify/templates/plan-template.md   — Constitution Check gates align (no edit required)
  ✅ .specify/templates/spec-template.md   — User-story + testable-requirements model consistent
  ✅ .specify/templates/tasks-template.md  — TDD ordering & per-story tests consistent
  ✅ .specify/extensions.yml               — No constitution hooks defined (no action)

Follow-up TODOs: None. All placeholders resolved.
-->

# Status Metrô Constitution

## Core Principles

### I. Spec-Driven Development & User Stories

Every new use case MUST begin as a written user story before implementation, using the
template stored in `/user_stories`. The only exception is purely architectural change
(infrastructure, build, or cross-cutting refactors) that delivers no new end-user behavior.
Each story MUST be independently testable and serve as the durable reference for the work,
so that implementation can resume correctly even after loss of working context.

**Rationale**: A written, templated story is the single source of truth for intent. It keeps
implementation aligned with user value and protects against drift when context is lost.

### II. Test-Driven Development for Java (NON-NEGOTIABLE)

All Java code MUST be developed test-first with JUnit. The cycle is mandatory and strict:
write the JUnit test, run it and observe it FAIL, then write the minimum implementation, then
run the test and observe it PASS. Implementation code MUST NOT be written before a failing
test exists for it. Refactoring is permitted only while tests remain green.

**Rationale**: Red-Green-Refactor guarantees every behavior is specified and verified before
it ships, and makes regressions visible immediately.

### III. UI Verified by Playwright (NON-NEGOTIABLE)

Any change that affects the user interface MUST have a corresponding Playwright test case
(TypeScript, in `/playwright`) that is written and executed and passing BEFORE the work is
reported as complete. A UI change is never "done" on the basis of manual inspection alone.

**Rationale**: The product is a web UI for checking line status; automated functional tests
are the only reliable proof that user-facing behavior works and keeps working.

### IV. API-First & Documented Interfaces

The backend MUST expose its capabilities as a REST API, and that API MUST be documented via
Swagger/OpenAPI. New or changed endpoints MUST be reflected in the Swagger documentation as
part of the same change. The contract is defined before or alongside consumers, never after.

**Rationale**: A documented, stable contract lets the frontend, tests, and external consumers
integrate predictably and makes the system inspectable.

### V. Prototype-Guided User Interface

UI implementation MUST be guided by the HTML prototypes in `/prototypes` produced with Google
Stitch. Visual and interaction work SHOULD trace back to an agreed prototype rather than being
invented ad hoc during coding; deviations from the prototype MUST be intentional and recorded.

**Rationale**: Prototypes align stakeholders on look-and-feel before code, reducing rework and
keeping the delivered UI faithful to the agreed design.

### VI. Living Documentation per Module

Every project directory (e.g. `/backend`, `/frontend`, `/playwright`) MUST contain its own
`CLAUDE.md` describing that module's purpose, stack, structure, and how to build, run, and
test it. These files MUST be kept current as the module evolves; detail is encouraged.

**Rationale**: Per-module guidance keeps both humans and coding agents productive and prevents
knowledge from living only in someone's head or a single root document.

### VII. Consistent, Portable Source Hygiene

Before any code is implemented, the repository root MUST contain:
- a `.editorconfig` covering Java, XML, JavaScript, HTML, CSS, and TypeScript files, enforcing
  UTF-8 encoding and LF line endings as defaults; and
- a `.gitignore` with sensible defaults for Java and popular Java IDEs, including VS Code.

All source files MUST be UTF-8 with LF line endings. These files are prerequisites, not
afterthoughts.

**Rationale**: Consistent encoding, line endings, and ignore rules prevent noisy diffs,
cross-platform breakage, and accidental commits of build/IDE artifacts.

## Technology Stack & Project Structure

The project's sanctioned technology stack is:

- **Backend**: Java 21, Maven (via the Maven Wrapper), Spring Boot 4.0.3, Spring Data JPA;
  REST API documented with Swagger/OpenAPI. Testing with JUnit.
- **Frontend**: Node/npm application using React with styled-components.
- **Prototyping**: HTML prototypes generated with Google Stitch.
- **Functional UI testing**: Playwright (TypeScript).

The repository structure is:

- `/backend` — REST API implementation (Spring Boot, Spring Data JPA).
- `/frontend` — Node frontend application (React, styled-components).
- `/user_stories` — user stories and the story template used for new use cases.
- `/prototypes` — HTML prototypes built with Google Stitch.
- `/playwright` — TypeScript Playwright application for UI testing.

Introducing a new language, framework, runtime, or top-level directory is an architectural
change and MUST be justified against this section before adoption (see Governance).

## Development Workflow & Quality Gates

The following gates apply to every change and MUST pass before a change is considered complete:

1. **Story gate**: For new use cases, a user story exists in `/user_stories` (Principle I).
2. **Java test gate**: New/changed Java behavior is covered by JUnit tests written test-first,
   and the suite is green (Principle II).
3. **UI gate**: UI-affecting changes have passing Playwright tests in `/playwright`
   (Principle III).
4. **API doc gate**: New/changed endpoints are reflected in Swagger (Principle IV).
5. **Docs gate**: Affected modules' `CLAUDE.md` files are updated (Principle VI).
6. **Hygiene gate**: `.editorconfig` and `.gitignore` exist and files are UTF-8 + LF
   (Principle VII).

"Complete" is reported only after the relevant gates above are satisfied and their tests have
been run and observed to pass.

## Governance

This constitution supersedes other development practices for the Status Metrô project. Where a
practice conflicts with a principle here, the principle prevails.

**Amendments**: Changes to this constitution MUST be proposed in writing, justified, and
recorded by updating this file together with its version and dates. Dependent templates and
module `CLAUDE.md` files MUST be reviewed for consistency as part of any amendment.

**Versioning policy** (semantic):
- **MAJOR**: Backward-incompatible governance changes or removal/redefinition of a principle.
- **MINOR**: Addition of a new principle or section, or materially expanded guidance.
- **PATCH**: Clarifications, wording, and non-semantic refinements.

**Compliance review**: All reviews and pull requests MUST verify adherence to these principles,
especially the NON-NEGOTIABLE testing principles (II and III). Any unavoidable deviation MUST
be documented and justified in the change itself (e.g. a plan's Complexity Tracking section).
Module `CLAUDE.md` files provide runtime development guidance and MUST remain consistent with
this constitution.

**Version**: 1.0.0 | **Ratified**: 2026-06-22 | **Last Amended**: 2026-06-22
