# Playwright — Status Metrô UI tests

Functional UI tests (TypeScript) for the Status Metrô frontend. Required by Constitution Principle
III: every UI-affecting change must have a passing Playwright test before it is "done".

## Stack

- @playwright/test
- Targets the frontend dev server (`webServer` in `playwright.config.ts` starts it automatically).
- Backend `/api` responses are **mocked per test** via `page.route()` using fixtures in
  `tests/fixtures/lineStatus.ts`, so behavior (normal, stale, unavailable, partial, disrupted) is
  deterministic without a live backend.

## Layout

```
tests/
  fixtures/lineStatus.ts        # sample snapshots + a route-mocking helper
  all-lines.spec.ts             # US1: all lines listed with status
  grouping-ordering.spec.ts     # US2: operator grouping + disrupted-first ordering + identity
  freshness-refresh.spec.ts     # US3: last-updated, manual + 60s auto refresh, stale, unavailable
```

## Commands

```bash
npm install
npx playwright install         # one-time browser download
npx playwright test            # run all UI tests
npx playwright test --headed   # watch them run
npm run report                 # open the HTML report
```

## Conventions

- Default project is a mobile viewport (Pixel 7) to enforce mobile-first (SC-006); a desktop project
  also runs.
- Prefer role/text selectors and `data-testid` over brittle CSS.
- Mock the API at the network boundary; do not depend on the live upstream CCR source.
