# Frontend — Status Metrô UI

React + styled-components single-page app that shows the current status of São Paulo Metrô & CPTM
lines, grouped by operator with disrupted lines first, with freshness and auto-refresh.

## Stack

- React 18 + TypeScript, styled-components 6
- Build/dev: Vite (npm)
- Consumes the backend REST API at `/api/v1/line-status` (dev: proxied to `:8080` via `vite.config.ts`)

## Layout

```
src/
  api/         # types.ts (mirrors the REST contract) + lineStatusClient.ts
  hooks/       # useLineStatus.ts (fetch + 60s auto-refresh + manual refresh)
  components/  # StatusBadge, LineCard, OperatorGroup, FreshnessIndicator, StalenessBanner, Unavailable
  pages/       # StatusPage.tsx
  theme/       # theme.ts (styled-components theme)
  main.tsx     # app entry
```

## Commands

```bash
npm install
npm run dev         # http://localhost:5173 (proxies /api to the backend)
npm run build
npm run typecheck
```

## Conventions

- Mobile-first; no horizontal scroll at phone widths (SC-006).
- Display text stays in Portuguese exactly as the source provides it (FR-012).
- UI follows the Google Stitch prototype in `/prototypes` (Constitution Principle V).
- Any UI-affecting change requires a passing Playwright test in `/playwright` before "done"
  (Constitution Principle III).
- Unknown line status → neutral treatment, original label preserved (FR-011); never hide a line.
