# User Story: São Paulo Metrô & CPTM Line Status

**ID**: US-001
**Feature**: `001-sp-line-status`
**Spec**: [specs/001-sp-line-status/spec.md](../specs/001-sp-line-status/spec.md)
**Created**: 2026-06-22
**Status**: Draft
**Priority**: P1 (with P2/P3 enhancements)

---

## Title

As a São Paulo commuter, I want to see the current operational status of every Metrô and CPTM
(train) line in one place so that I can decide whether my line is running before I travel.

## Context & Background

São Paulo's rail network is operated by several entities (Metrô SP, CPTM, and concessionaires such
as ViaMobilidade, Motiva Linha Quatro, and TIC Trens). Commuters need a quick, trustworthy way to
check whether their line is running normally or disrupted. Status data is published by the official
CCR mobility public endpoint for the state of São Paulo.

## Data Source

- **Endpoint**: `https://webapi.grupoccr.com.br/v1/mobility/public/line-status/current/state/SP`
- **Access**: Public, no authentication required.
- **Shape (observed 2026-06-22)**:
  - `status` (boolean) — request success flag
  - `dataAtualizacao` (timestamp) — when the data was last updated
  - `concessoes[]` — operators
    - `uid`, `nome` (operator name), `estados`, `logo`
    - `linhas[]` — lines
      - `numero` (line number), `nome` (line name, usually a color), `corRgb` (display color)
      - `statusLinha` (human-readable status), `codigo` (status classification code)
- **Observed operators (6)**: Motiva Linha Quatro, ViaMobilidade 5, ViaMobilidade 8 e 9, Metro SP,
  CPTM, TIC Trens.
- **Observed lines (13)**: Amarela, Lilás, Diamante, Esmeralda, Azul, Verde, Vermelha, Prata,
  Turquesa, Coral, Safira, Jade, Rubi.
- **Observed status**: "Operação Normal" (all lines, at time of capture). Other states (reduced
  speed, partial operation, stopped, closed) must be handled when they occur.

## Acceptance Criteria

### P1 — Core line status (MVP)
1. **Given** the data source is reachable, **When** the user opens the app, **Then** all SP Metrô
   and CPTM lines are shown, each with a human-readable status.
2. **Given** a line is operating normally, **When** displayed, **Then** it uses a positive/neutral
   visual treatment.
3. **Given** a line is disrupted, **When** displayed, **Then** it is visually distinguished from a
   normal line.

### P2 — Line identity & operator grouping
4. **Given** the list is shown, **When** the user views a line, **Then** its name, number, and color
   are presented together.
5. **Given** multiple operators, **When** the list is shown, **Then** lines are grouped/labeled by
   operator (e.g., Metrô vs CPTM).

### P3 — Freshness & refresh
6. **Given** data is loaded, **When** the user views the page, **Then** the "last updated" time is
   shown.
7. **Given** the user requests a refresh, **When** triggered, **Then** statuses and the timestamp
   re-fetch and update.
8. **Given** the app stays open, **When** the refresh interval elapses, **Then** data refreshes
   automatically.

### Edge cases
9. Source unavailable → friendly "temporarily unavailable" message (no raw error).
10. Partial data → show available lines and indicate the list may be incomplete.
11. Unknown status value → neutral "unknown" treatment showing the original text.
12. No lines returned → explicit "no lines available" message.

## Out of Scope

- Authentication / user accounts / personalization.
- Historical status, predictions, alerts/notifications, journey planning, maps.
- States other than São Paulo (SP).
- Localization beyond the Portuguese text provided by the source.

## Implementation Notes (to be refined in `/speckit.plan`)

- Per the project constitution: backend exposes a REST API (Spring Boot, documented via Swagger)
  that fetches and normalizes the upstream data; the React frontend consumes it.
- TDD for Java (JUnit, test-first); Playwright (TypeScript) tests for all UI-affecting behavior.
- UI should follow the Google Stitch prototypes in `/prototypes`.

## Traceability

- Functional requirements: FR-001 … FR-013 (see spec).
- Success criteria: SC-001 … SC-006 (see spec).

## References

- Feature spec: `specs/001-sp-line-status/spec.md`
- Project constitution: `.specify/memory/constitution.md`
