# Upstream Contract: CCR Mobility Line-Status (SP)

External dependency consumed by the backend. We do not control this contract; the backend isolates
the rest of the system from it (see research.md §1, §3).

## Request

```
GET https://webapi.grupoccr.com.br/v1/mobility/public/line-status/current/state/SP
```

- Public, no authentication.
- Polled by the backend every 60 seconds (not on the user request path).

## Response envelope

```jsonc
{
  "status": true,        // boolean success flag
  "message": "",         // string, error message when status is false
  "errorCode": "",       // string error code when applicable
  "data": {
    "dataAtualizacao": "2026-06-22T18:47:59",   // ISO-8601 local time, no offset
    "concessoes": [ Concessao, ... ]
  }
}
```

### Concessao (operator)

```jsonc
{
  "uid": "METRO",
  "nome": "Metro SP",
  "estados": "SP",
  "logo": { "_path": "/content/dam/.../logo.svg" },   // relative to CCR host
  "linhas": [ Linha, ... ]
}
```

### Linha (line)

```jsonc
{
  "uid": "CCRVQ-L4",
  "numero": "4",
  "nome": "Amarela",
  "icone": { "_path": "/content/dam/.../pin.svg" },    // relative to CCR host; may be absent
  "corRgb": "#FCC540",
  "statusLinha": {
    "codigo": "OperacaoNormal",       // machine code, PascalCase
    "status": "Operação Normal",      // PT-BR display text
    "descricao": ""                    // optional extra detail
  }
}
```

## Observed values (2026-06-22)

- Operators (6): `CCRVQ` Motiva Linha Quatro, `CCRVM5e17` ViaMobilidade 5, `CCRVM8e9` ViaMobilidade
  8 e 9, `METRO` Metro SP, `CPTM` CPTM, `TICTRENS` TIC Trens.
- Lines (13): Amarela, Lilás, Diamante, Esmeralda, Azul, Verde, Vermelha, Prata, Turquesa, Coral,
  Safira, Jade, Rubi.
- Status codes seen: `OperacaoNormal` only. Disruption codes (reduced speed, partial, stopped,
  closed) are expected but were not present in the sample — the backend maps any unrecognized code to
  `UNKNOWN`.

## Failure handling expectations (backend)

- HTTP error, timeout, connection failure, or `status: false` → treat as a failed fetch: retain the
  previous snapshot and mark it `stale`; if none exists, expose "unavailable".
- Missing/empty `concessoes` with `status: true` → `available: false` snapshot (FR / edge case "no
  lines returned").
- A subset of expected operators/lines → set `partial: true` (FR-010).
- Unknown `corRgb` format → null the color (UI falls back to a neutral swatch).

## Notes for tests

- JUnit upstream-client tests SHOULD use MockWebServer/WireMock fixtures captured from the real
  payload (success, failure, empty, partial, unknown-status variants).
- Treat `dataAtualizacao` as a naive local timestamp (no timezone offset in the source).
