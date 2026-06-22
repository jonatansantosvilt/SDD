# Phase 1 Data Model: São Paulo Metrô & CPTM Line Status

This model is the backend's **normalized domain** (what our REST API exposes), derived from the spec
entities and the upstream CCR shape (see research.md §1). It is held in memory only.

## Enum: StatusCategory

Classifies a line's operational condition for visual treatment and ordering.

| Value | Meaning | Upstream `codigo` examples |
|-------|---------|----------------------------|
| `NORMAL` | Operating normally | `OperacaoNormal` |
| `DISRUPTED` | Any service problem | reduced speed, partial operation, stopped, closed |
| `UNKNOWN` | Unrecognized/absent code | anything not mapped above |

**Ordering rank**: `DISRUPTED` = 0, `UNKNOWN` = 1, `NORMAL` = 2 (lower sorts first).

## Entity: LineStatus

The operational condition of a line.

| Field | Type | Source / Rule |
|-------|------|---------------|
| `code` | string | upstream `statusLinha.codigo` (raw, preserved) |
| `label` | string | upstream `statusLinha.status` (PT-BR display text) |
| `description` | string (nullable) | upstream `statusLinha.descricao`; empty → null |
| `category` | StatusCategory | derived from `code` (research.md §2) |

**Rules**: `label` is always shown as provided (FR-012). Unknown `code` → `category = UNKNOWN`, label
still rendered (FR-011).

## Entity: Line

A single subway or train line.

| Field | Type | Source / Rule |
|-------|------|---------------|
| `uid` | string | upstream `linhas[].uid` (stable identity) |
| `number` | string | upstream `numero` (kept as string; e.g. "4", "8") |
| `name` | string | upstream `nome` (e.g. "Amarela", "Vermelha") |
| `colorRgb` | string | upstream `corRgb` (e.g. "#FCC540"); validated as `#RRGGBB`, else null |
| `iconUrl` | string (nullable) | upstream `icone._path` resolved against the CCR host; null if absent |
| `status` | LineStatus | see above |
| `operatorUid` | string | parent operator `uid` (back-reference) |

**Relationships**: each Line belongs to exactly one Operator.

**Identity/uniqueness**: `uid` is unique across the snapshot.

## Entity: Operator

A transit operator (concessão) responsible for one or more lines.

| Field | Type | Source / Rule |
|-------|------|---------------|
| `uid` | string | upstream `concessoes[].uid` (e.g. "METRO", "CPTM") |
| `name` | string | upstream `nome` (e.g. "Metro SP", "CPTM") |
| `state` | string | upstream `estados` (expected "SP") |
| `logoUrl` | string (nullable) | upstream `logo._path` resolved against the CCR host |
| `lines` | List&lt;Line&gt; | upstream `linhas[]`, ordered by line number |

**Relationships**: an Operator has many Lines (1..N).

## Entity: StatusSnapshot

The full normalized result retrieved together at a point in time (the cached object).

| Field | Type | Source / Rule |
|-------|------|---------------|
| `lastUpdated` | timestamp (ISO-8601) | upstream `data.dataAtualizacao` |
| `fetchedAt` | timestamp (ISO-8601) | when our backend last fetched successfully |
| `stale` | boolean | true when the most recent fetch attempt failed and this is a retained prior snapshot |
| `available` | boolean | true when at least one operator/line is present |
| `partial` | boolean | true when upstream returned successfully but with an incomplete set (FR-010) |
| `operators` | List&lt;Operator&gt; | normalized operators |
| `orderedLines` | List&lt;Line&gt; | all lines flattened and ordered disrupted-first (FR-014) for convenient rendering |

**Lifecycle / state transitions**:

```
(no snapshot) --fetch ok--> AVAILABLE(stale=false)
AVAILABLE --fetch ok-------> AVAILABLE(stale=false)        # refreshed
AVAILABLE --fetch fails----> AVAILABLE(stale=true)         # serve last-known-good (FR-009)
(no snapshot) --fetch fails-> UNAVAILABLE                  # error message only (FR-009a)
```

**Ordering rule (`orderedLines`)**: sort by `status.category` rank, then `operatorUid`, then numeric
value of `number` ascending (FR-014).

## Validation summary (traceability)

- `category` derivation and unknown handling → FR-004, FR-011.
- `stale` / `available` transitions → FR-009, FR-009a, SC-004.
- `partial` flag → FR-010.
- `orderedLines` ordering → FR-014, SC-005.
- `lastUpdated` exposure → FR-006, SC-003.
