# Feature Specification: São Paulo Metrô & CPTM Line Status

**Feature Branch**: `001-sp-line-status`

**Created**: 2026-06-22

**Status**: Draft

**Input**: User description: "Build an application that can show the line status for São Paulo Metrô and CPTM (train service). You should fetch the data from https://webapi.grupoccr.com.br/v1/mobility/public/line-status/current/state/SP. Remember to create a new user story in the folder user_stories with the detailed implementation."

## Clarifications

### Session 2026-06-22

- Q: When the upstream source is unreachable, what should the user see? → A: Serve the last-known-good data with a visible staleness warning; show a full error screen only if no data has ever been successfully loaded.
- Q: What is the automatic refresh interval? → A: 60 seconds (1 minute).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See current status of all lines (Priority: P1)

A São Paulo commuter opens the web application and immediately sees a list of every Metrô and
CPTM (train) line with its current operational status, so they can decide whether their line is
running normally before they leave.

**Why this priority**: This is the core reason the product exists. Without an at-a-glance view of
every line's current status, the application delivers no value. It is a complete, shippable MVP on
its own.

**Independent Test**: Load the application with the data source reachable and confirm that all
lines for São Paulo are listed, each showing a human-readable status. Fully delivers value on its
own (the user can answer "is my line running?").

**Acceptance Scenarios**:

1. **Given** the data source is available, **When** the user opens the application, **Then** every
   São Paulo Metrô and CPTM line is displayed with its current status in plain language.
2. **Given** a line is operating normally, **When** the user views it, **Then** its status is shown
   as normal operation in a clearly positive/neutral visual treatment.
3. **Given** a line is experiencing a disruption (reduced speed, partial or full stoppage, closed),
   **When** the user views it, **Then** its status is shown with a visual treatment that
   distinguishes it from a normally operating line.

---

### User Story 2 - Identify and group lines by operator and identity (Priority: P2)

A commuter who knows their line by its color/name or by the operator (Metrô vs CPTM vs the
concessionaires) can quickly locate it among all lines, so they don't have to read every entry.

**Why this priority**: Improves the speed and ease of the core task once the list exists. Valuable
but not required for the MVP to deliver its primary value.

**Independent Test**: Load the application and confirm each line shows its identifying name/number
and color, and that lines are grouped or labeled by their operator. Can be verified independently of
auto-refresh or detail behavior.

**Acceptance Scenarios**:

1. **Given** the line list is displayed, **When** the user looks at a line, **Then** the line's
   identifying name and number are shown together with its associated color.
2. **Given** multiple operators serve São Paulo, **When** the user views the list, **Then** lines
   are organized so the user can tell which operator runs each line (e.g., Metrô vs CPTM).
3. **Given** the user is looking for a specific line, **When** they scan the list, **Then** they can
   locate it by color and name without reading every status.

---

### User Story 3 - Know how fresh the data is and refresh it (Priority: P3)

A commuter wants to trust that the status shown is current, so they can see when the information was
last updated and trigger an update on demand.

**Why this priority**: Builds trust and handles the time-sensitive nature of transit status, but the
core value (seeing status) is already delivered by P1.

**Independent Test**: Load the application, confirm a "last updated" time is shown, trigger a
refresh, and confirm the displayed information and timestamp update.

**Acceptance Scenarios**:

1. **Given** status data has been loaded, **When** the user views the page, **Then** the time the
   data was last updated is clearly displayed.
2. **Given** the user wants the latest information, **When** they request a refresh, **Then** the
   line statuses and the "last updated" time are re-fetched and updated on screen.
3. **Given** the application has been open for a while, **When** time passes, **Then** the data is
   refreshed automatically so the displayed status does not become stale.

---

### Edge Cases

- **Data source unavailable (data previously loaded)**: When a live fetch fails but a previous
  successful snapshot exists, the user is shown the last-known-good statuses together with a clear
  staleness indicator (e.g., "data may be outdated — last updated at HH:MM").
- **Data source unavailable (no data ever loaded)**: When a live fetch fails and no successful
  snapshot has ever been obtained, the user is shown a clear, friendly message explaining that
  status is temporarily unavailable, rather than a blank page or a raw technical error.
- **Partial data**: When the data source returns some but not all lines, the available lines are
  displayed and the user is informed the list may be incomplete.
- **Unknown / unexpected status value**: When a line reports a status the application does not
  recognize, it is displayed using a neutral "unknown" treatment and the raw status text, never
  hidden.
- **Stale data**: When data has not refreshed within the expected interval (e.g., the source is
  briefly unreachable), the displayed "last updated" time makes the staleness visible to the user.
- **No lines returned**: When the source responds successfully but contains no lines, the user sees
  an explicit "no lines available" message.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST retrieve current line-status information for São Paulo Metrô and CPTM
  (train) lines from the official São Paulo mobility data source.
- **FR-002**: System MUST display every available line with a human-readable operational status.
- **FR-003**: System MUST present each line's identity, including its name, line number, and
  associated color.
- **FR-004**: System MUST visually distinguish normal operation from disrupted operation (e.g.,
  reduced speed, partial stoppage, full stoppage, closed).
- **FR-005**: System MUST indicate which operator is responsible for each line (e.g., Metrô SP,
  CPTM, and the relevant concessionaires).
- **FR-006**: System MUST display the time at which the status information was last updated.
- **FR-007**: Users MUST be able to refresh the status information on demand.
- **FR-008**: System MUST refresh status information automatically every 60 seconds while the
  application is open.
- **FR-009**: System MUST retain the most recent successfully retrieved status snapshot and, when a
  live fetch fails, continue to display that last-known-good snapshot together with a visible
  staleness indicator and its original "last updated" time.
- **FR-009a**: System MUST display a clear, user-friendly message (without exposing raw technical
  errors) only when status information cannot be retrieved and no successful snapshot has ever been
  obtained.
- **FR-010**: System MUST display lines even when the data set is partial, and indicate that the
  list may be incomplete.
- **FR-011**: System MUST display any unrecognized status value using a neutral treatment and the
  original status text rather than omitting the line.
- **FR-012**: System MUST present line status in Portuguese as provided by the data source, with the
  interface usable by a São Paulo commuter.
- **FR-013**: System MUST be usable on a mobile-sized screen, as commuters typically check status on
  a phone.

### Key Entities *(include if feature involves data)*

- **Operator (Concessão)**: A transit operator responsible for one or more lines (e.g., Metrô SP,
  CPTM, ViaMobilidade, Motiva Linha Quatro, TIC Trens). Attributes: name, logo/branding, the state
  it serves, and the set of lines it operates.
- **Line (Linha)**: A single subway or train line. Attributes: line number, name (typically a
  color, e.g., "Azul", "Vermelha"), display color, current status, and the operator it belongs to.
- **Line Status (Status da Linha)**: The current operational condition of a line. Attributes: a
  status code/classification and a human-readable description (e.g., "Operação Normal", reduced
  speed, partial operation, stopped, closed).
- **Status Snapshot**: The full set of operator/line statuses retrieved together at a point in time.
  Attributes: the "last updated" timestamp and the collection of operators and their lines.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A first-time user can determine whether a specific line is running normally within
  10 seconds of opening the application.
- **SC-002**: 100% of São Paulo Metrô and CPTM lines reported by the data source are displayed when
  the source is reachable.
- **SC-003**: The displayed status reflects the data source's most recent update, and the "last
  updated" time is never more than 60 seconds behind the source's own timestamp under normal
  connectivity.
- **SC-004**: When the data source is unreachable, users never see a blank screen or technical
  error: 100% of the time they see either the last-known-good statuses with a staleness indicator
  (if a prior snapshot exists) or a friendly "temporarily unavailable" message (if none does).
- **SC-005**: Users can distinguish a disrupted line from a normally operating line at a glance,
  confirmed by 90% of test users correctly identifying disrupted lines in usability testing.
- **SC-006**: The application is fully usable on a typical mobile phone screen width without
  horizontal scrolling.

## Assumptions

- The application is read-only for end users; there is no authentication, account, or
  personalization in this scope.
- The official São Paulo mobility data source (the CCR mobility public line-status endpoint for
  state SP) is the single source of truth and is publicly accessible without credentials.
- Status text is provided by the source in Portuguese and is displayed as-is; full localization to
  other languages is out of scope for this version.
- Possible line statuses include at least: normal operation, reduced speed, partial operation, full
  stoppage, and closed/out of service; the application treats any other value as "unknown".
- The automatic refresh interval is 60 seconds (see Clarifications); it may be tuned later without
  changing scope.
- Historical status, predictions, notifications/alerts, journey planning, and maps are out of scope
  for this version (current status only).
- Only São Paulo state lines are in scope, consistent with the data source path for SP.

## Dependencies

- Availability of the upstream public line-status data source for São Paulo. If the source changes
  its data shape or becomes unavailable, the displayed status is affected accordingly.
