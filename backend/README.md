# Cliniqo API

Backend foundation for F01 Foundation, Tenancy, Auth, Audit.

## Requirements

- Java 21
- Maven 3.9+
- Docker for Testcontainers PostgreSQL

## Run Tests

```bash
mvn test
```

The `test` profile runs against PostgreSQL through Testcontainers and applies Flyway migrations.
When using Colima on macOS, export the Colima Docker socket and disable Ryuk because Colima cannot
bind-mount its host socket path into the Ryuk sidecar:

```bash
DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true mvn test
```

Latest local validation: `DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true JAVA_HOME=/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home PATH=/usr/local/opt/openjdk@21/bin:$PATH mvn test` passed with PostgreSQL 16 Testcontainers and Flyway migrations: 27 tests, 0 failures, 0 errors, 0 skipped on 2026-06-14.

## Run Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The backend starts on port `8080` by default. All responses use the global API envelope with
`meta.requestId`. Logs include request, clinic, and user context where safe and must not include PHI,
raw credentials, raw tokens, patient message bodies, or stack traces in client responses.

## F01 Scope Boundaries

- Backend foundation only.
- No frontend auth shell.
- No clinic onboarding UI.
- No appointments.
- No WhatsApp or OpenAI integration.
- No analytics screens.
- No F17 SUPER_ADMIN cross-clinic support workflows beyond the explicit route-context placeholder.

## Fixture Credentials

The Flyway test fixture migration includes users for local/integration validation:

- `superadmin@cliniqo.test`
- `admin-a@cliniqo.test`
- `admin-b@cliniqo.test`

Fixture password: `Password123!`
