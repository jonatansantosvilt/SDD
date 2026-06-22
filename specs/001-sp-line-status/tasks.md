---
description: "Task list for São Paulo Metrô & CPTM Line Status"
---

# Tasks: São Paulo Metrô & CPTM Line Status

**Input**: Design documents from `/specs/001-sp-line-status/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: REQUIRED. The project constitution mandates Test-Driven Development for Java (Principle II,
NON-NEGOTIABLE — write a failing JUnit test before implementation) and a passing Playwright test for
every UI-affecting change (Principle III, NON-NEGOTIABLE). Test tasks below are therefore mandatory
and must be written and observed to FAIL before their implementation tasks.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies on incomplete tasks)
- **[Story]**: User story the task belongs to (US1, US2, US3)
- All paths are repository-relative.

## Path Conventions

- Backend (Java 21, Spring Boot): `backend/src/main/java/com/viltgroup/statusmetro/linestatus/...`,
  tests in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/...`
- Frontend (React + TS): `frontend/src/...`
- Playwright (TS): `playwright/tests/...`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Repository hygiene, project scaffolding, and prototype reference — prerequisites before
any code (Constitution Principles V, VI, VII).

- [ ] T001 [P] Create root `.editorconfig` (UTF-8, LF; sections for Java, XML, JS, HTML, CSS, TS) at `.editorconfig`
- [ ] T002 [P] Create root `.gitignore` (Java + popular IDEs incl. VS Code, Maven `target/`, Node `node_modules/`, Playwright artifacts) at `.gitignore`
- [ ] T003 Initialize Spring Boot 4.0.3 backend with Maven Wrapper and dependencies (spring-boot-starter-web, scheduling, springdoc-openapi-starter-webmvc-ui, JUnit 5, WireMock) in `backend/pom.xml`, `backend/mvnw`, `backend/.mvn/`
- [ ] T004 [P] Initialize React 18 + TypeScript + styled-components app (Vite, npm) in `frontend/package.json` and `frontend/src/`
- [ ] T005 [P] Initialize Playwright TypeScript project in `playwright/package.json` and `playwright/playwright.config.ts`
- [ ] T006 [P] Create `backend/CLAUDE.md` documenting backend purpose, stack, build/run/test commands
- [ ] T007 [P] Create `frontend/CLAUDE.md` documenting frontend purpose, stack, dev/build/test commands
- [ ] T008 [P] Create `playwright/CLAUDE.md` documenting how to run the UI test suite
- [ ] T009 [P] Create Google Stitch HTML prototype of the status page (line list, status badges, operator grouping, freshness banner) in `prototypes/status-page.html`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: The shared backend data pipeline (upstream client → normalize → cache → poll) and
frontend/Playwright scaffolding that ALL user stories depend on.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

**Backend domain & DTOs**

- [ ] T010 [P] Create `StatusCategory` enum (NORMAL, DISRUPTED, UNKNOWN) with ordering rank in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/domain/StatusCategory.java`
- [ ] T011 [P] Create domain models `Operator`, `Line`, `LineStatus`, `StatusSnapshot` in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/domain/`
- [ ] T012 [P] Create upstream DTOs (envelope, `Concessao`, `Linha`, `StatusLinha`, logo/icone `_path`) in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/client/dto/`

**Status mapping (test-first)**

- [ ] T013 Write FAILING JUnit test for status-code → `StatusCategory` mapping (OperacaoNormal→NORMAL; known disruption→DISRUPTED; unrecognized/blank→UNKNOWN) in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/service/StatusCategoryMapperTest.java`
- [ ] T014 Implement `StatusCategoryMapper` to pass T013 in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/service/StatusCategoryMapper.java`

**Normalization (test-first)**

- [ ] T015 Write FAILING JUnit test for `NormalizationService` (DTO→domain, `corRgb` validation→null on bad format, logo/icon `_path`→absolute URL, empty concessoes→available=false, subset→partial=true) in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/service/NormalizationServiceTest.java`
- [ ] T016 Implement `NormalizationService` to pass T015 in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/service/NormalizationService.java`

**Ordering (test-first)**

- [ ] T017 Write FAILING JUnit test for disrupted-first ordering (category rank → operator → ascending line number) producing `orderedLines` in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/service/LineOrderingTest.java`
- [ ] T018 Implement line ordering in `NormalizationService` (populate `StatusSnapshot.orderedLines`) to pass T017

**Upstream client (test-first)**

- [ ] T019 Write FAILING JUnit test for `CcrLineStatusClient` using WireMock (success payload, HTTP 5xx, timeout, malformed body, `status:false`) in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/client/CcrLineStatusClientTest.java`
- [ ] T020 Implement `CcrLineStatusClient` (Spring `RestClient`) + HTTP config to pass T019 in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/client/CcrLineStatusClient.java` and `backend/src/main/java/com/viltgroup/statusmetro/linestatus/config/HttpClientConfig.java`

**Snapshot cache (test-first)**

- [ ] T021 Write FAILING JUnit test for `SnapshotCache` (first success → available, not stale; later failure → retains prior, stale=true; failure with no prior → unavailable) in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/service/SnapshotCacheTest.java`
- [ ] T022 Implement thread-safe `SnapshotCache` holder to pass T021 in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/service/SnapshotCache.java`

**Polling & API docs config**

- [ ] T023 Implement `@Scheduled` poller (60s) wiring client→normalize→cache, plus `application.yml` (upstream URL, `linestatus.poll-interval=60s`, CCR host for asset URLs) in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/service/LineStatusPoller.java` and `backend/src/main/resources/application.yml`
- [ ] T024 [P] Configure springdoc/OpenAPI + Swagger UI metadata in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/config/OpenApiConfig.java`

**Frontend & Playwright scaffolding**

- [ ] T025 [P] Create TypeScript types mirroring the API contract (`StatusSnapshot`, `Operator`, `Line`, `LineStatus`, `StatusCategory`) in `frontend/src/api/types.ts`
- [ ] T026 [P] Implement typed API client `getLineStatus()` (GET `/api/v1/line-status`) in `frontend/src/api/lineStatusClient.ts`
- [ ] T027 [P] Define styled-components theme (colors, spacing, mobile-first breakpoints) in `frontend/src/theme/theme.ts`
- [ ] T028 Create app shell + `StatusPage` placeholder and mount in `frontend/src/pages/StatusPage.tsx` and `frontend/src/main.tsx`
- [ ] T029 [P] Add Playwright API mocking helper/fixtures (sample snapshot, stale, unavailable, partial) in `playwright/tests/fixtures/lineStatus.ts`

**Checkpoint**: Backend serves a tested, normalized, ordered, cached snapshot via the poller;
frontend and Playwright scaffolding ready. User stories can now begin.

---

## Phase 3: User Story 1 - See current status of all lines (Priority: P1) 🎯 MVP

**Goal**: A commuter opens the app and sees every SP Metrô and CPTM line with a human-readable
status, with disrupted lines visually distinguished from normal ones.

**Independent Test**: With the data source reachable, load the app and confirm all lines are listed,
each with a plain-language status; a disrupted line is visually distinct from a normal one.

### Tests for User Story 1 (write FIRST, must FAIL) ⚠️

- [ ] T030 [P] [US1] Write FAILING JUnit contract/integration test for `GET /api/v1/line-status` returning 200 with `operators` and `orderedLines` matching `contracts/line-status-api.yaml` in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/api/LineStatusControllerTest.java`
- [ ] T031 [P] [US1] Write FAILING Playwright test: all lines render, each with a human-readable status; disrupted vs normal visually distinct in `playwright/tests/all-lines.spec.ts`

### Implementation for User Story 1

- [ ] T032 [US1] Create response DTOs matching the contract (`StatusSnapshotResponse`, `OperatorResponse`, `LineResponse`, `LineStatusResponse`) in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/api/dto/`
- [ ] T033 [US1] Implement `LineStatusController` `GET /api/v1/line-status` returning the cached snapshot (200) in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/api/LineStatusController.java`
- [ ] T034 [P] [US1] Implement `StatusBadge` component with NORMAL/DISRUPTED/UNKNOWN visual treatments in `frontend/src/components/StatusBadge.tsx`
- [ ] T035 [US1] Implement `LineCard` (status + label) and render the full line list in `frontend/src/pages/StatusPage.tsx` consuming `getLineStatus()`
- [ ] T036 [US1] Add loading state and initial error fallback to `StatusPage` in `frontend/src/pages/StatusPage.tsx`
- [ ] T037 [US1] Run T030 and T031; make them pass (Red→Green)

**Checkpoint**: User Story 1 is fully functional and independently testable (MVP).

---

## Phase 4: User Story 2 - Identify and group lines by operator and identity (Priority: P2)

**Goal**: Each line shows its name, number, and color, and lines are grouped by operator with
disrupted lines surfaced first, so a commuter can quickly locate their line.

**Independent Test**: Load the app and confirm each line shows name/number/color, lines are grouped
by operator, and disrupted lines appear before normal ones.

### Tests for User Story 2 (write FIRST, must FAIL) ⚠️

- [ ] T038 [P] [US2] Write FAILING Playwright test: lines grouped by operator; each line shows name, number, and color; disrupted-first ordering verified in `playwright/tests/grouping-ordering.spec.ts`

### Implementation for User Story 2

- [ ] T039 [P] [US2] Extend `LineCard` to display line number, name, and color swatch from `colorRgb` in `frontend/src/components/LineCard.tsx`
- [ ] T040 [P] [US2] Implement `OperatorGroup` component (operator name/logo + its lines) in `frontend/src/components/OperatorGroup.tsx`
- [ ] T041 [US2] Update `StatusPage` to render disrupted-first ordering grouped by operator using `orderedLines`/`operators` in `frontend/src/pages/StatusPage.tsx`
- [ ] T042 [US2] Run T038; make it pass (backend `orderedLines` already produced in Phase 2 — verify ordering surfaces in UI)

**Checkpoint**: User Stories 1 and 2 both work independently.

---

## Phase 5: User Story 3 - Know how fresh the data is and refresh it (Priority: P3)

**Goal**: The app shows when data was last updated, refreshes on demand and automatically every 60s,
shows a staleness warning when serving last-known-good, and a friendly message when nothing is
available.

**Independent Test**: Load the app, confirm a "last updated" time, trigger manual refresh, observe
60s auto-refresh; simulate upstream-down to see the staleness banner and the unavailable message.

### Tests for User Story 3 (write FIRST, must FAIL) ⚠️

- [ ] T043 [P] [US3] Write FAILING JUnit test: controller returns 503 with `{available:false, message}` when no snapshot has ever loaded in `backend/src/test/java/com/viltgroup/statusmetro/linestatus/api/LineStatusUnavailableTest.java`
- [ ] T044 [P] [US3] Write FAILING Playwright test: last-updated shown; manual refresh re-fetches; 60s auto-refresh; stale banner on `stale:true`; unavailable message on 503 in `playwright/tests/freshness-refresh.spec.ts`

### Implementation for User Story 3

- [ ] T045 [US3] Add 503 unavailable branch + `Unavailable` DTO to `LineStatusController` in `backend/src/main/java/com/viltgroup/statusmetro/linestatus/api/LineStatusController.java`
- [ ] T046 [US3] Implement `useLineStatus` hook (initial fetch, 60s auto-refresh, manual `refresh()`, exposes `stale`/`error`/`lastUpdated`) in `frontend/src/hooks/useLineStatus.ts`
- [ ] T047 [P] [US3] Implement `FreshnessIndicator` (last-updated time) and manual refresh control in `frontend/src/components/FreshnessIndicator.tsx`
- [ ] T048 [P] [US3] Implement `StalenessBanner` and `Unavailable` message components in `frontend/src/components/StalenessBanner.tsx` and `frontend/src/components/Unavailable.tsx`
- [ ] T049 [US3] Wire hook + freshness/stale/unavailable states into `StatusPage` in `frontend/src/pages/StatusPage.tsx`
- [ ] T050 [US3] Run T043 and T044; make them pass

**Checkpoint**: All user stories independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Quality, accessibility, and end-to-end validation across all stories.

- [ ] T051 [P] Mobile responsiveness pass — verify no horizontal scroll at phone widths across components (SC-006) in `frontend/src/`
- [ ] T052 [P] Verify the running Swagger UI matches `contracts/line-status-api.yaml` (Constitution IV)
- [ ] T053 [P] Accessibility/contrast check for status colors and badges (frontend)
- [ ] T054 [P] Refresh `backend/CLAUDE.md`, `frontend/CLAUDE.md`, `playwright/CLAUDE.md` with final run/test instructions
- [ ] T055 Run full `quickstart.md` validation end-to-end (`./mvnw test`, frontend dev, `npx playwright test`)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately.
- **Foundational (Phase 2)**: Depends on Setup — BLOCKS all user stories.
- **User Stories (Phase 3–5)**: All depend on Foundational. US1 is the MVP; US2 and US3 build on the
  rendered list but are independently testable.
- **Polish (Phase 6)**: Depends on the desired user stories being complete.

### User Story Dependencies

- **US1 (P1)**: Depends only on Foundational. Delivers the MVP.
- **US2 (P2)**: Depends on Foundational; extends the US1 list (grouping/identity/ordering). Backend
  ordering is already produced in Phase 2, so US2 is largely frontend.
- **US3 (P3)**: Depends on Foundational; adds freshness/refresh/stale/unavailable. The 503 branch
  builds on the US1 controller.

### Within Each User Story

- JUnit/Playwright tests are written and MUST FAIL before implementation (Constitution II & III).
- Backend: DTOs → controller; Frontend: components → page wiring.

### Parallel Opportunities

- Setup: T001, T002, T004, T005, T006, T007, T008, T009 can run in parallel (T003 before backend code).
- Foundational: T010, T011, T012 parallel; T024, T025, T026, T027, T029 parallel; test→impl pairs are
  sequential (T013→T014, T015→T016/T018, T019→T020, T021→T022).
- US1: T030 and T031 (tests) parallel; T034 parallel with backend T032/T033.
- US3: T047 and T048 parallel.

---

## Parallel Example: User Story 1

```bash
# Write both failing tests first, in parallel:
Task: "JUnit contract test for GET /api/v1/line-status in backend/.../api/LineStatusControllerTest.java"  # T030
Task: "Playwright all-lines test in playwright/tests/all-lines.spec.ts"                                    # T031

# Then frontend StatusBadge can proceed in parallel with backend controller work:
Task: "StatusBadge component in frontend/src/components/StatusBadge.tsx"   # T034
```

---

## Implementation Strategy

### MVP First (User Story 1 only)

1. Phase 1: Setup (incl. `.editorconfig`, `.gitignore`, prototype).
2. Phase 2: Foundational (CRITICAL — blocks all stories).
3. Phase 3: User Story 1 → **STOP and VALIDATE** (all lines + status visible).
4. Deploy/demo the MVP.

### Incremental Delivery

1. Setup + Foundational → backend snapshot pipeline ready.
2. US1 → MVP (all lines with status).
3. US2 → grouping, identity, disrupted-first ordering.
4. US3 → freshness, refresh, stale/unavailable resilience.

---

## Notes

- [P] = different files, no dependency on incomplete tasks.
- [Story] label maps each task to its user story for traceability.
- Every Java change starts with a failing JUnit test; every UI-affecting change ends with a passing
  Playwright test (Constitution Principles II & III, NON-NEGOTIABLE).
- Commit after each task or logical group.
