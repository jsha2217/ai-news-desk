# AI News Desk

AI 뉴스 자동 수집 및 요약 시스템

## 프로젝트 구조

```
ai-news-desk/
├── backend/              # Spring Boot 백엔드 서버
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ainewsdesk/
│   │   │   │   └── BackendApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/
│   │   └── test/
│   └── pom.xml
├── frontend/             # 프론트엔드 (React/Vue/Angular)
├── crawler/              # 뉴스 크롤러 모듈
└── README.md
```

## 시작하기

### Backend (Spring Boot)

1. backend 디렉토리로 이동:
   ```bash
   cd backend
   ```

2. Maven 의존성 다운로드 및 빌드:
   ```bash
   ./mvnw clean install
   ```

3. 애플리케이션 실행:
   ```bash
   ./mvnw spring-boot:run
   ```

4. 서버 접속: http://localhost:8080

### 기술 스택

- **Backend**: Spring Boot 3.2.1, Java 17, JPA, H2/MySQL
- **Frontend**: TBD
- **Crawler**: TBD

## 개발 환경

- Java 17 이상
- Maven 3.6 이상
- IDE: IntelliJ IDEA, Eclipse, VS Code

## 기능

- [ ] 뉴스 자동 수집
- [ ] AI 기반 뉴스 요약
- [ ] 뉴스 카테고리 분류
- [ ] 사용자 인터페이스