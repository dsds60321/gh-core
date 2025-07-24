# gh-core

Spring REST Docs에서 AsciiDoc 파일을 생성하려면 다음 명령어를 사용하세요:
## 1. 테스트 실행 및 문서 생성
``` bash
# 테스트를 실행하여 스니펫 생성
./gradlew test

# 또는 특정 테스트만 실행
./gradlew test --tests "dev.gunho.api.bingous.v1.handler.AuthHandlerTest"
```
## 2. AsciiDoc 문서 생성
``` bash
# AsciiDoc 문서 생성
./gradlew asciidoctor
```
## 3. 전체 빌드 (테스트 + 문서 생성 + 복사)
``` bash
# 전체 빌드 (테스트, 문서 생성, static 디렉토리 복사 모두 포함)
./gradlew build
```
## 4. 문서만 생성하고 복사
``` bash
# 문서 생성 후 static 디렉토리로 복사
./gradlew copyDocument
```
## 생성되는 파일 위치
- **스니펫 파일**: `build/generated-snippets/` 디렉토리
- **AsciiDoc 문서**: `build/docs/asciidoc/` 디렉토리
- **최종 HTML 문서**: `src/main/resources/static/docs/` 디렉토리

## 추가 설정이 필요한 경우
현재 에는 AsciiDoc 플러그인이 설정되어 있지만, 실제 `.adoc` 소스 파일이 필요합니다. 다음 위치에 AsciiDoc 파일을 생성해야 합니다: `build.gradle`
``` 
src/docs/asciidoc/index.adoc
```
예시 파일: `index.adoc`
``` adoc
= API 문서
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 2
:sectlinks:

== 인증 API

=== 회원가입

operation::auth/sign-up[snippets='request-fields,response-fields,http-request,http-response']

=== 이메일 인증 코드 전송

operation::auth/email-verify[snippets='request-fields,response-fields,http-request,http-response']

=== 이메일 인증 코드 확인

operation::auth/email-confirm[snippets='request-fields,response-fields,http-request,http-response']

=== 회원가입 검증 오류

operation::auth/sign-up-validation-error[snippets='request-fields,response-fields,http-request,http-response']
```
이 파일을 생성한 후 `./gradlew build` 명령어를 실행하면 완전한 HTML 문서가 생성됩니다.
