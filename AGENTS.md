# Repository Guidelines
1. 코드는 동작 가능하게 완성본으로 제공
2. 한글로 항상 의사소통을 진행
3. 불명확한 요구사항은 먼저 질문
4. 성능 위험이 있으면 즉시 경고 + 대안 제시

내 기존 프로젝트 스타일 유지 (Java/Spring + React + SQL 최적화)
## Project Structure & Module Organization
Source is split by reactive domain packages under `src/main/java/dev/gunho/api`, with `global` utilities (filters, models, exceptions) and feature folders like `bingous/v1`. Configuration, templates, and static docs live in `src/main/resources`, while REST Docs assets are staged in `src/docs/asciidoc` and copied to `src/main/resources/static/docs`. Tests mirror production code in `src/test/java` and share package names for 1:1 coverage. Generated artifacts land in `build/generated-snippets` and `build/docs/asciidoc`; treat everything in `build/` as disposable.

## Build, Test, and Development Commands
Use `./gradlew test` to run the JUnit/WebTestClient suite and refresh REST Docs snippets. `./gradlew asciidoctor` converts snippets plus `.adoc` sources into HTML guides. Run `./gradlew copyDocument` to move docs into `src/main/resources/static/docs` for serving, and `./gradlew build` for the full pipeline (tests + docs + bootable jar). `./gradlew bootRun` works for local API smoke tests when environment variables (DB, Redis, mail) are exported.

## Coding Style & Naming Conventions
Stick to Java 17, 4-space indentation, and Lombok for boilerplate (non-Lombok classes should keep explicit getters). Keep package names lowercase (`dev.gunho.api.feature`) and class names descriptive (`AuthHandler`, `EmailService`). DTOs end with `Request`, `Response`, or `Result`, entities with `Entity`, and filters with `Filter`. Prefer constructor injection, Reactor types (Mono/Flux) for async flows, and MapStruct mappers for translation while relying on IDE formatting plus static analysis in the Spring Boot toolchain.

## Testing Guidelines
Add unit or slice tests under matching packages, e.g., `src/test/java/dev/gunho/api/bingous/v1/handler`. Use `@WebFluxTest` + `WebTestClient` for handler specs and Mockito for service isolation. Name tests `<ClassName>Test` with descriptive `@DisplayName`s, and ensure success/edge cases capture REST Docs snippets. Keep coverage high on auth, session, and email flows; reject PRs that drop coverage or leave undocumented endpoints.

## Commit & Pull Request Guidelines
Follow the existing `type : summary` convention (`feat : 회원가입`, `fix : 세션 검증`). Each commit should group a logical change set and include English or Korean context as long as it is specific. PRs need: problem statement, high-level solution, testing proof (`./gradlew test` output), updated docs/snippets if behavior changes, and linked issues or screenshots for user-facing work. Request reviews early when schema or contract shifts might affect downstream services.

## Security & Configuration Tips
Never commit secrets; rely on `.env` or local profile overrides. `processResources` injects `applicationVersion` and `applicationName`, so keep `application.yml` placeholders intact. Update email, JWT, and database credentials via environment variables or secure config servers, and rotate tokens before cutting releases.
