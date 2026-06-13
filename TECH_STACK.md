# TECH_STACK.md

# Cliniqo AI — Tech Stack & Dependency Reference

> Pinned versions, Maven/npm dependencies, project coordinates, and library choices.
> This is the **build recipe** — everything an agent needs to generate `pom.xml` and `package.json`.
> For architecture decisions behind these choices, see **ARCHITECTURE.md §16 (ADRs)**.
> For coding standards, see **AGENTS.md**.

---

## 1. Project Coordinates

### Backend (Maven)

| Property     | Value                          |
| ------------ | ------------------------------ |
| Group ID     | `com.cliniqo`               |
| Artifact ID  | `cliniqo-api`               |
| Version      | `0.1.0-SNAPSHOT`               |
| Package      | `com.cliniqo`               |
| Java Version | `25`                           |
| Packaging    | `jar`                          |

**Base package structure:**

```
com.cliniqo/
├── CliniqoApplication.java          ← @SpringBootApplication (main class)
├── config/                             ← Global configs (Security, CORS, OpenAPI)
├── common/                             ← Shared code (ApiResponse, BaseEntity, exceptions)
│   ├── dto/
│   │   └── ApiResponse.java
│   ├── entity/
│   │   └── BaseEntity.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ConflictException.java
│   │   ├── UnauthorizedException.java
│   │   └── ForbiddenException.java
│   └── security/
│       ├── JwtTokenProvider.java
│       ├── JwtAuthenticationFilter.java
│       ├── TenantContext.java
│       ├── TenantFilter.java
│       └── UserPrincipal.java
├── auth/
├── clinic/
├── doctor/
├── patient/
├── appointment/
├── whatsapp/
├── ai/
├── notification/
└── analytics/
```

### Frontend (npm)

| Property     | Value                          |
| ------------ | ------------------------------ |
| Package name | `cliniqo-frontend`          |
| Version      | `0.1.0`                        |
| Node         | `>=18.0.0`                     |
| Type         | `module`                       |

---

## 2. Backend Dependencies (pom.xml)

### Spring Boot Version

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>
```

### Core Dependencies

```xml
<!-- Web & REST API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JPA / Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Validation (Jakarta) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Security (JWT + BCrypt) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Actuator (Health checks) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Database

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Flyway Migrations -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>10.17.0</version>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>10.17.0</version>
</dependency>
```

### JWT

```xml
<!-- JJWT (JSON Web Token library) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### OpenAI API Client

> **No third-party SDK.** Use Spring's built-in `RestTemplate` to call the OpenAI REST API directly. No additional HTTP client dependency is needed. See **§7** for the integration pattern.

### OpenAPI / Swagger

```xml
<!-- Springdoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### Utility

```xml
<!-- Lombok (optional — for reducing boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Jsoup (HTML sanitization for AI output) -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.18.1</version>
</dependency>
```

### Mapping

```xml
<!-- MapStruct — annotation-processor DTO mapping -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0</version>
</dependency>
```

Add the MapStruct processor to `maven-compiler-plugin` `<annotationProcessorPaths>`:

```xml
<path>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.6.0</version>
</path>
```

### Cache

```xml
<!-- Caffeine — in-process cache for per-clinic config -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>
```

### Resilience

```xml
<!-- Resilience4j — circuit breaker for OpenAI + WhatsApp calls -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-reactor</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Testing

```xml
<!-- Spring Boot Test (JUnit 5 + MockMvc) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 Database (for integration tests) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Testing — Integration

```xml
<!-- TestContainers — Postgres-backed integration tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.1</version>
    <scope>test</scope>
</dependency>
```

### Backend Logging — logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <conversionRule conversionWord="redact"
        converterClass="com.cliniqo.shared.logging.PhiRedactionConverter" />

    <appender name="STDOUT_LOCAL" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%X{requestId:-}][%X{clinicId:-}][%X{userId:-}] %logger{40} - %redact(%msg)%n</pattern>
        </encoder>
    </appender>

    <appender name="STDOUT_JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"cliniqo-api"}</customFields>
            <includeMdcKeyName>requestId</includeMdcKeyName>
            <includeMdcKeyName>clinicId</includeMdcKeyName>
            <includeMdcKeyName>userId</includeMdcKeyName>
            <includeMdcKeyName>action</includeMdcKeyName>
        </encoder>
    </appender>

    <springProfile name="local">
        <root level="INFO">
            <appender-ref ref="STDOUT_LOCAL" />
        </root>
        <logger name="com.cliniqo" level="DEBUG" />
    </springProfile>

    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="STDOUT_JSON" />
        </root>
        <logger name="com.cliniqo" level="INFO" />
        <logger name="org.springframework.security" level="WARN" />
    </springProfile>
</configuration>
```

Add `net.logstash.logback:logstash-logback-encoder:7.4` to backend dependencies. The `PhiRedactionConverter` is a custom Logback converter that scrubs patterns matching: phone numbers (E.164), email addresses, BCrypt hashes, JWTs, and any fields whose JSON key is one of: `password`, `accessToken`, `refreshToken`, `apiKey`, `appSecret`, `verifyToken`. Reference: SECURITY_RULES.md §4A.5.

### Complete pom.xml Properties

```xml
<properties>
    <java.version>21</java.version>
    <jjwt.version>0.12.6</jjwt.version>
    <springdoc.version>2.6.0</springdoc.version>
</properties>
```

---

## 3. Frontend Dependencies (package.json)

### Core

```json
{
  "dependencies": {
    "@tanstack/react-query": "^5.59.0",
    "@tanstack/react-query-devtools": "^5.59.0",
    "axios": "^1.7.7",
    "clsx": "^2.1.1",
    "date-fns": "^4.1.0",
    "lucide-react": "^0.447.0",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-router-dom": "^6.26.2",
    "recharts": "^2.12.7",
    "zod": "^3.23.8"
  }
}
```

Zod provides runtime schema validation for forms and API responses. @tanstack/react-query handles HTTP cache, retries, and stale-while-revalidate per-clinic. Wrap the SPA in `<QueryClientProvider>` and define one `QueryClient` instance in `src/services/queryClient.ts`.

### Dev Dependencies

```json
{
  "devDependencies": {
    "@types/react": "^18.3.11",
    "@types/react-dom": "^18.3.1",
    "@vitejs/plugin-react": "^4.3.2",
    "autoprefixer": "^10.4.20",
    "postcss": "^8.4.47",
    "tailwindcss": "^3.4.13",
    "typescript": "^5.6.3",
    "vite": "^5.4.8",
    "vitest": "^2.1.2",
    "@testing-library/react": "^16.0.1",
    "@testing-library/jest-dom": "^6.5.0"
  }
}
```

### Library Choices (Locked)

| Need                  | Library            | Why                                           |
| --------------------- | ------------------ | --------------------------------------------- |
| Routing               | `react-router-dom` v6 | Standard React routing, role-based guards   |
| HTTP client           | `axios`            | Interceptors for JWT, clean API layer          |
| Charts                | `recharts`         | Lightweight, React-native, composable          |
| Icons                 | `lucide-react`     | Tree-shakeable, consistent stroke width        |
| CSS                   | `tailwindcss` v3   | Utility-first, matches UI_UX_GUIDE.md          |
| Date formatting       | `date-fns`         | Lightweight, tree-shakeable, no moment.js      |
| Class names           | `clsx`             | Conditional className merging                  |
| State management      | React Context API  | Simple, built-in — no Redux/Zustand for MVP    |
| Testing               | `vitest`           | Vite-native, fast, Jest-compatible             |

**Forbidden frontend libraries (MVP):**

* Redux, Zustand, MobX — Context API is sufficient
* Moment.js — use `date-fns` instead
* jQuery — never
* CSS Modules — use TailwindCSS
* Next.js — this is a Vite SPA, not SSR

### Frontend HTTP Client — Axios Setup

```typescript
// src/services/api.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/stores/authStore';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 30_000,
});

// Request interceptor — attach JWT + request id
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().accessToken;
    if (token) config.headers.Authorization = `Bearer ${token}`;
    config.headers['X-Request-Id'] = crypto.randomUUID();
    return config;
});

// Response interceptor — auto-refresh on 401
let refreshing: Promise<string> | null = null;
api.interceptors.response.use(
    (r) => r,
    async (error: AxiosError) => {
        const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
        if (error.response?.status === 401 && !original._retry) {
            original._retry = true;
            try {
                refreshing ??= useAuthStore.getState().refresh();
                const newToken = await refreshing;
                refreshing = null;
                original.headers.Authorization = `Bearer ${newToken}`;
                return api(original);
            } catch (e) {
                useAuthStore.getState().logout();
                window.location.assign('/login');
                return Promise.reject(e);
            }
        }
        return Promise.reject(error);
    }
);

export default api;
```

All API calls go through this client. Errors are normalized via a `useApiError(err)` hook that maps the canonical error catalog (`API_CONTRACTS.md §19`) to toast messages.

---

## 4. Shared Code Definitions

### 4.1 ApiResponse Wrapper (Backend)

Every API response uses this wrapper. See **API_CONTRACTS.md §1** for usage.

```java
package com.cliniqo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    // --- Static factory methods ---

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.data = data;
        return response;
    }

    // --- Getters ---
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}
```

### 4.2 Paginated Response Wrapper

```java
package com.cliniqo.common.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PaginatedResponse<T> from(org.springframework.data.domain.Page<T> springPage) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.content = springPage.getContent();
        response.page = springPage.getNumber();
        response.size = springPage.getSize();
        response.totalElements = springPage.getTotalElements();
        response.totalPages = springPage.getTotalPages();
        response.last = springPage.isLast();
        return response;
    }

    // --- Getters ---
    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}
```

### 4.3 BaseEntity (Complete)

> Soft delete is enforced at the JPA layer via Hibernate `@SQLDelete` + `@Where`.
> Calling `repository.delete(entity)` is rewritten into an `UPDATE` that sets
> `deleted_at` and `deleted_by`. Every find/JPQL/criteria query automatically
> filters `deleted_at IS NULL`. Hard delete is intentionally not exposed through
> this class — purge operations must use a native query and bypass the @Where filter.
> For schema details, partial indexes, and cascade semantics, see **DATABASE_SCHEMA.md §1, §5, §8**.

```java
package com.cliniqo.common.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.*;
import java.time.Instant;
import java.util.UUID;

@FilterDef(
    name = "tenantFilter",
    parameters = @ParamDef(name = "clinicId", type = UUID.class)
)
@Filter(name = "tenantFilter", condition = "clinic_id = :clinicId")
@SQLDelete(sql = "UPDATE #{table_name} SET deleted_at = NOW(), deleted_by = :deletedBy WHERE id = ? AND deleted_at IS NULL")
@Where(clause = "deleted_at IS NULL")
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "clinic_id", nullable = false, updatable = false)
    private UUID clinicId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // actor channel: whatsapp-ai | manual-receptionist | clinic-admin | super-admin | system
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // actor channel: whatsapp-ai | manual-receptionist | clinic-admin | super-admin | system
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    // softDelete() / restore() helpers
    public void softDelete(String actorChannel) {
        this.deletedAt = Instant.now();
        this.deletedBy = actorChannel;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
```

**Enabling the tenantFilter.** In a per-request interceptor (after JWT auth resolves the clinic), call:

```java
Session session = entityManager.unwrap(Session.class);
session.enableFilter("tenantFilter").setParameter("clinicId", tenantContext.getClinicId());
```

SUPER_ADMIN routes MUST NOT enable the filter — they require cross-tenant visibility. Note that `clinics`, users-with-null-clinic (SUPER_ADMIN), `refresh_tokens`, `audit_events`, and `idempotency_keys` do NOT extend `BaseEntity` — they use a separate `AuditableEntity` base without the tenant filter.

### 4.4 Custom Exception Classes

```java
// ResourceNotFoundException.java
package com.cliniqo.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
    }
}

// ConflictException.java
package com.cliniqo.common.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

// UnauthorizedException.java
package com.cliniqo.common.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

// ForbiddenException.java
package com.cliniqo.common.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
```

---

## 5. Frontend Shared Types

### 5.1 API Response Types

```typescript
// types/api.ts

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
```

### 5.2 Auth Types

```typescript
// types/auth.ts

export type UserRole = 'SUPER_ADMIN' | 'CLINIC_ADMIN' | 'RECEPTIONIST' | 'DOCTOR';

export interface AuthUser {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  clinicId: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: AuthUser;
}
```

### 5.3 Enum Types (Mirror Backend)

```typescript
// types/enums.ts

export type AppointmentStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW' | 'RESCHEDULED';
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
export type ConversationStatus = 'ACTIVE' | 'ESCALATED' | 'RESOLVED' | 'CLOSED';
export type MessageDirection = 'INBOUND' | 'OUTBOUND';
export type MessageSenderType = 'PATIENT' | 'AI' | 'RECEPTIONIST' | 'SYSTEM';
export type NotificationType = 'ADVANCE_REMINDER' | 'FINAL_REMINDER' | 'BOOKING_CONFIRMATION' | 'CANCELLATION_CONFIRMATION' | 'RESCHEDULE_CONFIRMATION';
export type NotificationStatus = 'PENDING' | 'SENT' | 'DELIVERED' | 'READ' | 'FAILED';
export type FaqCategory = 'TIMINGS' | 'LOCATION' | 'SERVICES' | 'FEES' | 'DOCTORS' | 'INSURANCE' | 'GENERAL';
```

---

## 6. Vite Configuration

```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### Tailwind Configuration

```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#EFF6FF',
          500: '#2563EB',
          600: '#1D4ED8',
          700: '#1E40AF',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
```

---

## 7. OpenAI API Integration Pattern

No third-party SDK. Use Spring's `RestTemplate` to call the OpenAI REST API directly:

```java
package com.cliniqo.ai.service;

@Service
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String chat(String systemPrompt, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, request, Map.class);

        // Extract content from response
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
```

> System prompts are loaded from `/docs/prompts/` files at startup. See prompt files for expected input/output formats.

---

## 8. WhatsApp API Integration Pattern

Use `RestTemplate` to call the WhatsApp Business API:

```java
package com.cliniqo.whatsapp.service;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api-url}")
    private String apiUrl;

    @Value("${whatsapp.api-token}")
    private String apiToken;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate;

    public WhatsAppService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void sendTextMessage(String recipientPhone, String message) {
        String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "to", recipientPhone,
            "type", "text",
            "text", Map.of("body", message)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, Map.class);
    }
}
```

---

## 9. Project Initialization Commands

An AI agent should bootstrap the project with these exact commands:

### Backend

```bash
# Option 1: Spring Initializr (web)
# Go to https://start.spring.io with:
#   Project: Maven, Language: Java, Spring Boot: 3.3.5
#   Group: com.cliniqo, Artifact: cliniqo-api
#   Java: 21, Packaging: Jar
#   Dependencies: Spring Web, Spring Data JPA, Spring Security,
#                 Validation, PostgreSQL Driver, Flyway Migration,
#                 Spring Boot Actuator, Lombok

# Option 2: CLI (if Spring CLI installed)
spring init --dependencies=web,data-jpa,security,validation,postgresql,flyway,actuator,lombok \
  --java-version=21 --group-id=com.cliniqo --artifact-id=cliniqo-api \
  --name=CliniqoApplication --package-name=com.cliniqo \
  backend
```

### Frontend

```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install react-router-dom axios recharts lucide-react clsx date-fns
npm install -D tailwindcss postcss autoprefixer @testing-library/react @testing-library/jest-dom vitest
npx tailwindcss init -p
```

---

## 10. Version Compatibility Matrix

| Component         | Version  | Compatible With                      |
| ----------------- | -------- | ------------------------------------ |
| Java              | 21       | Spring Boot 3.3.x                   |
| Spring Boot       | 3.3.5    | Spring Security 6.3.x, Hibernate 6.5.x |
| PostgreSQL        | 15+      | Flyway 10.x, Hibernate dialect      |
| JJWT              | 0.12.6   | Java 21, Spring Security 6          |
| Springdoc OpenAPI | 2.6.0    | Spring Boot 3.3.x                   |
| React             | 18.3.x   | React Router 6.x, Vite 5.x         |
| Vite              | 5.4.x    | React 18, TypeScript 5.x            |
| TailwindCSS       | 3.4.x    | PostCSS 8.x, Vite 5.x              |
| Node.js           | 18+      | npm 9+, Vite 5.x                    |
