# Phase 0 Research: São Paulo Metrô & CPTM Line Status

All Technical Context items were resolvable; no `NEEDS CLARIFICATION` remained after `/speckit.clarify`
and the planning decisions. This document records the key decisions and the evidence behind them.

## 1. Upstream data source shape

**Decision**: Treat the CCR endpoint as the single source of truth and normalize its nested
envelope into our own domain model.

**Evidence** (fetched 2026-06-22 from
`https://webapi.grupoccr.com.br/v1/mobility/public/line-status/current/state/SP`):

```jsonc
{
  "status": true, "message": "", "errorCode": "",
  "data": {
    "dataAtualizacao": "2026-06-22T18:47:59",
    "concessoes": [
      {
        "uid": "CCRVQ", "nome": "Motiva Linha Quatro", "estados": "SP",
        "logo": { "_path": "/content/.../LogoViaQuatro.svg" },
        "linhas": [
          {
            "uid": "CCRVQ-L4", "numero": "4", "nome": "Amarela",
            "icone": { "_path": "/content/.../pin_integracao_viaquatro4.svg" },
            "corRgb": "#FCC540",
            "statusLinha": { "codigo": "OperacaoNormal", "status": "Operação Normal", "descricao": "" }
          }
        ]
      }
    ]
  }
}
```

- 6 operators / 13 lines observed: Motiva Linha Quatro (1), ViaMobilidade 5 (1), ViaMobilidade 8 e 9
  (2), Metro SP (4), CPTM (4), TIC Trens (1).
- Per-line status lives in `statusLinha.codigo` (machine code, PascalCase), `statusLinha.status`
  (PT-BR display), `statusLinha.descricao` (optional detail).
- Logo/icon are objects with a `_path` relative to the CCR DAM host.

**Rationale**: The envelope (`status`/`message`/`errorCode`/`data`) and the verbose nested shape are
fragile to depend on directly from the UI. Normalizing in the backend isolates the frontend from
upstream changes and lets us apply ordering and a stable contract.

**Alternatives considered**: Calling the upstream API directly from the browser — rejected: exposes
the UI to CORS, upstream shape changes, and gives no place to cache last-known-good or shield the
source.

## 2. Status classification

**Decision**: Map each upstream `codigo` to a `StatusCategory` of `NORMAL`, `DISRUPTED`, or
`UNKNOWN`. `OperacaoNormal` → `NORMAL`. Recognized disruption codes (e.g. velocidade reduzida,
operação parcial, paralisada, operação encerrada) → `DISRUPTED`. Any unrecognized code → `UNKNOWN`,
displayed neutrally with the original `status` text (never hidden).

**Rationale**: Only `OperacaoNormal` was observable today, but the spec (FR-004, FR-011) and clarify
session require distinguishing disruption and gracefully handling unknown values. A category enum
keeps UI treatment and ordering logic simple and testable, while the original text is always
preserved.

**Alternatives considered**: Branching UI logic on raw `codigo` strings — rejected: scatters
upstream vocabulary across the frontend and breaks on new codes. The unknown-tolerant mapping in one
place is more robust.

## 3. Polling + last-known-good cache (resilience)

**Decision**: A `@Scheduled` poller fetches upstream every 60 s and atomically replaces an in-memory
snapshot holder. REST requests are served from that holder and never call upstream synchronously. On
a failed fetch, the holder keeps the previous snapshot and marks it `stale`; if no fetch has ever
succeeded, the API reports "unavailable".

**Rationale**: Satisfies the clarified behavior (serve last-known-good with a staleness indicator;
error only when nothing ever loaded — FR-009/FR-009a) and the 60 s refresh decision (FR-008). It also
decouples request latency from upstream and shields the public source from per-client traffic.

**Alternatives considered**: Fetch-on-request with a short TTL cache — rejected: couples user
latency to upstream health and complicates the "serve stale on failure" requirement.

## 4. No persistent storage / JPA for this feature

**Decision**: Hold only the single latest snapshot in memory; do not use Spring Data JPA or a
database for v1.

**Rationale**: The only state is the most recent successful fetch (volatile by nature). History,
audit, and trends are out of scope (spec Assumptions). See plan Complexity Tracking. JPA remains a
sanctioned stack technology to adopt when a durable-data feature appears.

## 5. Frontend stack & auto-refresh

**Decision**: React 18 + styled-components SPA. A `useLineStatus` hook fetches the backend on mount
and on a 60 s interval, exposes `{ snapshot, loading, error, stale, lastUpdated, refresh() }`, and a
manual refresh control re-fetches on demand (FR-007, FR-008).

**Rationale**: Matches the constitution stack. styled-components supports the per-line color theming
(`corRgb`) and status-based visual treatment cleanly. A single hook centralizes fetch/refresh and is
straightforward to drive in Playwright.

**Alternatives considered**: Server-side Vaadin UI in the backend — rejected by planning decision and
the constitution's React principle. A data-fetching library (e.g. React Query) — optional later; a
small custom hook keeps v1 dependency-light and the behavior explicit for tests.

## 6. Line ordering

**Decision**: Order by category rank (DISRUPTED, then UNKNOWN, then NORMAL), then by operator, then
by ascending line number. Computed in the backend so the contract is deterministic and testable.

**Rationale**: Clarify decision "disrupted first" directly serves SC-005 (spot problems at a glance).
Computing it server-side makes ordering assertable in both JUnit and Playwright.

## 7. API documentation (Swagger)

**Decision**: Use springdoc-openapi to publish OpenAPI 3 + Swagger UI; keep
`contracts/line-status-api.yaml` as the authored source of truth, validated against the running app.

**Rationale**: Satisfies Constitution Principle IV (API-first, documented) and gives the frontend a
stable, inspectable contract.
