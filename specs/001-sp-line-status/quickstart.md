# Quickstart & Validation: São Paulo Metrô & CPTM Line Status

A run/validation guide proving the feature works end-to-end. Implementation details live in
`tasks.md` (after `/speckit.tasks`) and the code itself; this file is how you verify it.

## Prerequisites

- Java 21 (JDK) on PATH
- Node 20 LTS + npm
- Outbound internet access to `https://webapi.grupoccr.com.br` (or recorded fixtures for offline runs)
- Root `.editorconfig` and `.gitignore` present (Constitution Principle VII)

## 1. Backend — Spring Boot REST API

```bash
cd backend
./mvnw test          # TDD gate: JUnit suite (must be green; tests written before impl)
./mvnw spring-boot:run
```

Validate:

- `GET http://localhost:8080/api/v1/line-status` returns a `StatusSnapshot` JSON with
  `available: true` and a non-empty `orderedLines` when upstream is reachable.
- Swagger UI is served (e.g. `http://localhost:8080/swagger-ui.html`) and matches
  `contracts/line-status-api.yaml`.
- Expected: 6 operators / 13 lines today; all `status.category` = `NORMAL` while the source reports
  "Operação Normal".

Resilience checks (use a fixture/mock upstream — see `contracts/upstream-ccr-contract.md`):

- Upstream fails **after** a good fetch → response has `stale: true` and retains the prior
  `lastUpdated` (FR-009).
- Upstream fails with **no** prior success → `503` with `{ available: false, message }` (FR-009a).
- Upstream returns empty `concessoes` → `available: false` (no-lines edge case).
- A line with an unmapped `codigo` → `status.category: "UNKNOWN"` and original `label` preserved
  (FR-011).
- Ordering: a disrupted line appears in `orderedLines` before any `NORMAL` line (FR-014).

## 2. Frontend — React SPA

```bash
cd frontend
npm install
npm run dev          # serves the status page (points at the backend API base)
```

Validate in a browser (mobile viewport):

- All lines render grouped by operator, each with its name, number, and color (US1, US2).
- Disrupted lines appear first; normal lines below (FR-014, SC-005).
- "Last updated" time is visible; a manual refresh control re-fetches (US3, FR-006/FR-007).
- The page auto-refreshes every 60 s (FR-008).
- When the backend reports `stale: true`, a staleness banner is shown; when `503`, a friendly
  "temporarily unavailable" message is shown — never a blank page or raw error (SC-004).
- No horizontal scroll at a typical phone width (SC-006).

## 3. UI functional tests — Playwright (MUST pass before "done", Principle III)

```bash
cd playwright
npm install
npx playwright install
npx playwright test
```

Covers (against the backend with mocked/stubbed API responses where needed):

- All 13 lines listed with status, name, number, color.
- Operator grouping and disrupted-first ordering.
- Freshness label + manual refresh + 60 s auto-refresh.
- Stale-data banner and unavailable-message states.
- Mobile-width layout has no horizontal overflow.

## Definition of Done (traceability)

| Check | Requirement |
|-------|-------------|
| JUnit green, written test-first | Constitution II |
| Playwright green for all UI behavior | Constitution III, FR-002…FR-014 |
| Swagger matches authored contract | Constitution IV, FR-001 |
| UI follows `/prototypes` Stitch designs | Constitution V |
| `backend`, `frontend`, `playwright` each have `CLAUDE.md` | Constitution VI |
| Root `.editorconfig` + `.gitignore` present before code | Constitution VII |
