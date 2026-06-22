# Backend — Status Metrô REST API

Spring Boot REST API that polls the public CCR mobility source for São Paulo Metrô & CPTM line
status, normalizes it, caches the last-known-good snapshot, and serves it as JSON.

## Stack

- Java 21, Spring Boot 4.0.3 (spring-boot-starter-web, scheduling)
- springdoc-openapi (Swagger UI) 3.0.3
- Build: Maven Wrapper (`./mvnw`)
- Tests: JUnit 5 + Spring Boot Test (MockRestServiceServer for the upstream client)

## Layout

```
src/main/java/com/viltgroup/statusmetro/
  StatusMetroApplication.java        # entry point (@EnableScheduling)
  linestatus/
    client/        # CcrLineStatusClient + dto/ (upstream shape)
    domain/        # Operator, Line, LineStatus, StatusSnapshot, StatusCategory
    service/       # StatusCategoryMapper, NormalizationService, SnapshotCache, LineStatusPoller
    api/           # LineStatusController + dto/ (response shape)
    config/        # HttpClientConfig, OpenApiConfig
src/main/resources/application.yml   # upstream URL, 60s poll interval, timeouts
```

## Commands

```bash
./mvnw test                 # run JUnit suite (TDD: write failing test first — Constitution II)
./mvnw spring-boot:run      # start API on http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# Endpoint:   GET http://localhost:8080/api/v1/line-status
```

## Conventions

- **TDD is mandatory** (Constitution Principle II): write a failing JUnit test, see it fail, then
  implement, then green. Models before services before controllers.
- The REST contract is authored at `specs/001-sp-line-status/contracts/line-status-api.yaml`; keep
  Swagger output consistent with it (Principle IV).
- The endpoint never calls upstream synchronously — it serves the cached snapshot maintained by the
  scheduled poller. On upstream failure it serves last-known-good with `stale: true`; only a
  never-loaded state yields 503.
