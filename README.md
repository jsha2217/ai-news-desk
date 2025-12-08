# 🚀 AI News Desk - 최종 프로젝트 기획서

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **프로젝트명** | AI News Desk |
| **목표** | AI 산업 뉴스 자동 수집 및 AI 기반 요약 서비스 |
| **개발 기간** | 8주 (2주 × 4 Phase) |
| **배포 환경** | AWS Free Tier (EC2 t2.micro, RDS) |
| **개발 비용** | 0원 (12개월 무료) |
| **기술 스택** | Java Spring Boot, MySQL/MariaDB, React, Python |

---

## 🎯 서비스 요구사항

### 기능 요구사항

1. **뉴스 자동 수집**
   - 공식 채널 (1순위): 시간마다 수집
   - 전문 미디어 (2순위): 2시간마다 수집
   - 일반 채널 (3순위): 4시간마다 수집

2. **AI 기반 요약**
   - 5시간 주기 (00:00, 05:00, 10:00, 15:00, 20:00 KDT)
   - Google Gemini API 활용
   - 핵심 내용 5개 + 산업 분석 + 개발자 영향도

3. **사용자 기능**
   - 회원가입/로그인 (JWT)
   - 뉴스 조회/검색/필터링
   - 북마크 기능
   - 마이페이지

### 비기능 요구사항

- 응답 시간: 2초 이내
- 서버 가용성: 99% 이상
- 데이터 보안: 비밀번호 암호화, HTTPS
- 확장성: 뉴스 출처 추가 가능한 구조

---

## 🔄 개발 Phase 분류

| Phase | 기간 | 목표 | 구분 |
|-------|------|------|------|
| **Phase 1** | 2주 | 백엔드 기초 구축 | Infrastructure |
| **Phase 2** | 2주 | 핵심 API 개발 | Development |
| **Phase 3** | 2주 | 프론트엔드 & 크롤링 | Integration |
| **Phase 4** | 2주 | 배포 & 최적화 | Deployment |

---

## 📊 Phase 1: 백엔드 기초 구축 (2주)

### 목표
Spring Boot 기반 REST API 기초 완성 및 데이터베이스 설계

### 상세 작업 계획

#### Week 1-1: 프로젝트 초기 설정

**할 일:**
- [ ] GitHub 저장소 생성 및 로컬 클론
- [ ] VS Code에서 Spring Boot 프로젝트 생성 (Maven)
- [ ] pom.xml 의존성 설정
  - Spring Web
  - Spring Data JPA
  - MySQL Driver
  - Spring Security
  - JWT (jjwt)
  - Lombok
  - Validation
- [ ] application.yml 설정 (DB 연결 정보)
- [ ] MySQL 데이터베이스 생성 (ai_news_desk)
- [ ] 애플리케이션 실행 테스트
- [ ] GitHub 첫 커밋: `feat: Spring Boot 프로젝트 초기 설정`

**산출물:**
- pom.xml
- application.yml
- 실행 가능한 Spring Boot 애플리케이션

---

#### Week 1-2: Entity 클래스 & Repository 작성

**할 일:**
- [ ] 데이터베이스 ERD 설계 (Workbench)
- [ ] User Entity 작성
  - id, email, password_hash, username, verified, createdAt
- [ ] Article Entity 작성
  - id, title, description, content, url, source_name, source_type, priority, category, thumbnailUrl, publishedAt, crawledAt
- [ ] AiSummary Entity 작성
  - id, summary_period_start, summary_period_end, title, content, key_highlights, related_articles_count, status
- [ ] Bookmark Entity 작성
  - id, user_id, article_id
- [ ] UserRepository 인터페이스 작성
- [ ] ArticleRepository 인터페이스 작성
- [ ] AiSummaryRepository 인터페이스 작성
- [ ] BookmarkRepository 인터페이스 작성
- [ ] @PrePersist, @PreUpdate 어노테이션으로 자동 타임스탬프 처리
- [ ] Workbench에서 자동 생성된 테이블 확인
- [ ] GitHub 커밋: `feat: Entity 클래스 및 Repository 작성`

**산출물:**
- 4개의 Entity 클래스
- 4개의 Repository 인터페이스
- MySQL 자동 생성 테이블

---

### Phase 1 완료 조건

✅ Spring Boot 프로젝트 정상 실행  
✅ MySQL 데이터베이스 정상 연동  
✅ 4개 Entity 테이블 자동 생성 확인  
✅ Repository 기본 CRUD 메서드 사용 가능  

---

## 🔧 Phase 2: 핵심 API 개발 (2주)

### 목표
사용자 인증, 뉴스 관리, 북마크 기능의 REST API 완성

### 상세 작업 계획

#### Week 2-1: 인증 시스템 (JWT) 구현

**할 일:**
- [ ] JWT 유틸리티 클래스 작성
  - Token 생성 메서드
  - Token 검증 메서드
  - Token에서 사용자 정보 추출
- [ ] SecurityConfig 클래스 작성
  - CORS 설정
  - JWT 필터 등록
  - 보안 규칙 설정
- [ ] DTO 클래스 작성
  - RegisterRequest (이메일, 비밀번호, 사용자명)
  - LoginRequest (이메일, 비밀번호)
  - LoginResponse (토큰, 사용자 정보)
  - UserDto (사용자 정보 응답)
- [ ] AuthController 작성
  - POST /api/auth/register - 회원가입
  - POST /api/auth/login - 로그인
  - GET /api/auth/me - 현재 사용자 정보
- [ ] UserService 작성
  - 사용자 등록 로직
  - 비밀번호 암호화 (BCryptPasswordEncoder)
  - 사용자 조회 로직
- [ ] 회원가입 & 로그인 API 테스트 (Postman 또는 REST Client)
- [ ] GitHub 커밋: `feat: JWT 기반 사용자 인증 시스템 구현`

**산출물:**
- JwtTokenProvider 클래스
- SecurityConfig 클래스
- AuthController 클래스
- UserService 클래스
- 회원가입/로그인 API

---

#### Week 2-2: 뉴스 & 북마크 API 구현

**할 일:**
- [ ] ArticleDto, ArticleResponse 클래스 작성
- [ ] ArticleService 작성
  - 전체 뉴스 조회 (페이지네이션)
  - 출처별 뉴스 조회
  - 카테고리별 뉴스 조회
  - 뉴스 검색
  - 뉴스 상세 조회
  - 뉴스 저장 (크롤러에서 호출)
- [ ] ArticleController 작성
  - GET /api/articles - 전체 뉴스 조회
  - GET /api/articles?source=official - 출처별 조회
  - GET /api/articles?category=model - 카테고리별 조회
  - GET /api/articles/search?keyword=AI - 검색
  - GET /api/articles/{id} - 상세 조회
  - POST /api/articles - 뉴스 저장 (크롤러용)
- [ ] BookmarkService 작성
  - 북마크 저장
  - 북마크 조회
  - 북마크 삭제
  - 사용자의 모든 북마크 조회
- [ ] BookmarkController 작성
  - POST /api/bookmarks - 북마크 추가
  - GET /api/bookmarks - 내 북마크 조회
  - DELETE /api/bookmarks/{id} - 북마크 삭제
- [ ] 페이지네이션 구현 (Page, Pageable)
- [ ] 뉴스 API 테스트
- [ ] GitHub 커밋: `feat: 뉴스 조회 및 북마크 기능 API 구현`

**산출물:**
- ArticleService, ArticleController
- BookmarkService, BookmarkController
- 뉴스 조회/검색/필터 API
- 북마크 CRUD API

---

### Phase 2 완료 조건

✅ 회원가입/로그인 정상 작동  
✅ JWT 토큰 기반 인증 작동  
✅ 뉴스 조회/검색/필터 API 정상 작동  
✅ 북마크 CRUD API 정상 작동  
✅ Postman으로 전체 API 테스트 완료  

---

## 🌐 Phase 3: 프론트엔드 & 크롤링 시스템 (2주)

### 목표
React 기반 웹 UI 및 Python 뉴스 크롤러 완성

### 상세 작업 계획

#### Week 3-1: React 프론트엔드 개발

**할 일:**
- [ ] React 프로젝트 생성 (Create React App 또는 Vite)
- [ ] 기본 폴더 구조 설정
  - components/
  - pages/
  - hooks/
  - services/
  - styles/
- [ ] API 통신 설정 (Axios 또는 Fetch)
  - API Base URL 설정
  - JWT 토큰 자동 포함
  - 요청/응답 인터셉터
- [ ] 주요 페이지 개발
  - LoginPage (로그인)
  - RegisterPage (회원가입)
  - HomePage (뉴스 피드)
  - SummaryPage (AI 요약)
  - MyPage (마이페이지)
- [ ] 컴포넌트 개발
  - Header (네비게이션)
  - NewsCard (뉴스 카드)
  - SearchBar (검색)
  - Filter (필터)
  - Pagination (페이지네이션)
- [ ] 상태 관리 (Context API 또는 Zustand)
  - 사용자 인증 상태
  - 뉴스 데이터
  - UI 상태
- [ ] 스타일링 (Tailwind CSS 또는 CSS-in-JS)
  - 부드러운 다크 모드 (이전 디자인)
  - 반응형 디자인
- [ ] Vercel에 배포
- [ ] GitHub 커밋: `feat: React 프론트엔드 개발 및 Vercel 배포`

**산출물:**
- React 애플리케이션 (pages, components)
- Vercel 배포 URL
- 사용자 UI 완성

---

#### Week 3-2: Python 크롤러 & AI 요약 시스템

**할 일:**
- [ ] Python 크롤러 프로젝트 구조 설정
  - crawlers/ (각 출처별 크롤러)
  - schedulers/ (스케줄링)
  - processors/ (데이터 처리)
- [ ] 크롤러 작성 (BeautifulSoup, Selenium)
  - OpenAI Blog 크롤러
  - Google AI Blog 크롤러
  - Anthropic Blog 크롤러
  - TechCrunch 크롤러
  - YouTube API 크롤러
- [ ] 데이터 정제 로직
  - HTML 파싱
  - 중복 제거
  - 필드 추출 (제목, 설명, URL, 이미지 등)
- [ ] Spring Boot API 연동
  - POST /api/articles로 데이터 저장
- [ ] Gemini API 통합
  - API 키 설정
  - 요약 생성 함수
  - 에러 처리
- [ ] 스케줄러 구현 (APScheduler 또는 schedule)
  - 공식 채널: 1시간 주기
  - 전문 미디어: 2시간 주기
  - 일반 채널: 4시간 주기
  - AI 요약: 5시간 주기 (00:00, 05:00, 10:00, 15:00, 20:00)
- [ ] 로깅 설정
- [ ] requirements.txt 작성
- [ ] GitHub 커밋: `feat: Python 크롤러 및 Gemini AI 요약 시스템 구현`

**산출물:**
- Python 크롤러 (BeautifulSoup, Selenium)
- Gemini API 통합 모듈
- APScheduler 기반 스케줄러
- 자동 크롤링 및 요약 시스템

---

### Phase 3 완료 조건

✅ React 앱 정상 빌드 및 배포  
✅ 프론트엔드에서 백엔드 API 통신 확인  
✅ 크롤러가 뉴스 수집 정상 작동  
✅ Gemini API로 요약 생성 확인  
✅ 스케줄러가 자동 실행 확인  

---

## 🚀 Phase 4: 배포 & 최적화 (2주)

### 목표
AWS에 완전히 배포된 운영 가능한 서비스 완성

### 상세 작업 계획

#### Week 4-1: AWS 배포 설정

**할 일:**
- [ ] AWS 계정 생성 (Free Tier)
- [ ] EC2 인스턴스 설정
  - t2.micro 인스턴스 생성
  - Ubuntu 22.04 LTS 선택
  - 보안 그룹 설정 (HTTP, HTTPS, SSH)
  - Elastic IP 할당
- [ ] RDS MySQL 설정
  - db.t2.micro 인스턴스 생성
  - 데이터베이스명: ai_news_desk
  - 마스터 사용자명/비밀번호 설정
  - 백업 설정
- [ ] EC2에 필요한 소프트웨어 설치
  - Java 17
  - MySQL Client
  - Python 3.9+
  - Git
- [ ] Spring Boot JAR 빌드
  - `mvn clean package`
- [ ] EC2에 배포
  - SCP로 JAR 파일 업로드
  - SystemD 서비스 설정 (자동 시작)
  - 로그 설정
- [ ] GitHub 커밋: `infra: AWS EC2, RDS 배포 설정`

**산출물:**
- 실행 중인 AWS EC2 인스턴스
- RDS MySQL 데이터베이스
- 배포된 Spring Boot 애플리케이션

---

#### Week 4-2: 최적화 & 모니터링

**할 일:**
- [ ] 데이터베이스 인덱스 추가
  - articles 테이블: crawledAt, sourceType
  - users 테이블: email
  - bookmarks 테이블: userId, articleId
- [ ] Spring Boot 성능 최적화
  - Connection Pool 설정
  - 캐싱 설정 (Redis 또는 로컬)
  - 쿼리 최적화
- [ ] 로깅 설정
  - SLF4J + Logback
  - 로그 레벨 설정 (INFO, DEBUG)
- [ ] 에러 처리 & 예외 클래스
  - GlobalExceptionHandler
  - 커스텀 예외 클래스
- [ ] API 문서화 (Swagger/Springdoc OpenAPI)
  - `/api-docs` 엔드포인트
  - 전체 API 명세서
- [ ] 보안 강화
  - application.yml 환경 변수로 관리
  - HTTPS/SSL 인증서 (Let's Encrypt)
  - CORS 설정 확인
  - SQL 인젝션 방지
- [ ] 모니터링 설정
  - CloudWatch (AWS)
  - 에러 모니터링 (Sentry 또는 로컬)
  - 로그 수집
- [ ] 전체 시스템 테스트
  - 회원가입/로그인 테스트
  - 뉴스 수집 및 표시 테스트
  - AI 요약 생성 테스트
  - 북마크 기능 테스트
  - 성능 테스트 (부하 테스트)
- [ ] README.md 작성
  - 프로젝트 설명
  - 기술 스택
  - 설치 방법
  - 사용 방법
  - API 명세서 링크
- [ ] GitHub 커밋: `fix: 성능 최적화 및 모니터링 설정`

**산출물:**
- 최적화된 데이터베이스
- 성능 개선된 Spring Boot API
- API 문서화 (Swagger)
- 모니터링 대시보드
- 완성된 README.md

---

#### 최종 검증

**할 일:**
- [ ] 전체 서비스 정상 작동 확인
- [ ] 크롤러 자동 실행 확인 (1주일 모니터링)
- [ ] AI 요약 생성 확인 (5시간 주기)
- [ ] 사용자 피드백 수집
- [ ] 버그 수정
- [ ] 포트폴리오 정리
  - GitHub 저장소 정리
  - 프로젝트 사진/스크린샷
  - 프로젝트 소개 블로그 글
- [ ] 최종 커밋: `docs: 프로젝트 완성 및 배포`

**산출물:**
- 완전히 운영 가능한 서비스
- 포트폴리오용 GitHub 저장소
- API 문서화
- 프로젝트 소개 자료

---

### Phase 4 완료 조건

✅ AWS EC2에서 Spring Boot 애플리케이션 실행 중  
✅ RDS에 데이터 정상 저장  
✅ Python 크롤러가 자동으로 실행 중  
✅ Gemini API로 5시간마다 요약 생성 중  
✅ React 프론트엔드가 모든 기능 정상 작동  
✅ 전체 서비스 1주일 정상 운영 확인  

---

## 📈 전체 일정표

| 주차 | Phase | 세부 작업 | 산출물 |
|------|-------|---------|--------|
| 1주 | 1 | 프로젝트 초기 설정, 의존성 관리 | Spring Boot 프로젝트 |
| 2주 | 1 | Entity, Repository 작성 | 4개 엔티티 + Repository |
| 3주 | 2 | JWT 인증 시스템 | 회원가입/로그인 API |
| 4주 | 2 | 뉴스 & 북마크 API | 뉴스 CRUD + 북마크 API |
| 5주 | 3 | React 프론트엔드 | 웹 UI + Vercel 배포 |
| 6주 | 3 | Python 크롤러 | 자동 뉴스 수집 + AI 요약 |
| 7주 | 4 | AWS 배포 | EC2 + RDS 배포 |
| 8주 | 4 | 최적화 & 운영 | 최적화 + API 문서화 |

---

## 💾 GitHub 커밋 전략

**각 Phase마다 최소 2-3회 커밋:**

```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
docs: 문서 작성
infra: 인프라 설정
```

**예시:**
```bash
git commit -m "feat: User Entity 및 UserRepository 작성"
git commit -m "feat: JWT TokenProvider 클래스 구현"
git commit -m "fix: 페이지네이션 쿼리 최적화"
```

---

## 🎓 학습 성과

### Phase 1 배우는 것
- Spring Boot 프로젝트 구조
- JPA/ORM 패턴
- 데이터베이스 설계

### Phase 2 배우는 것
- REST API 설계
- JWT 인증 시스템
- Spring Security

### Phase 3 배우는 것
- React 상태 관리
- API 통신
- Python 크롤링
- 외부 API 연동 (Gemini)

### Phase 4 배우는 것
- AWS 인프라
- 배포 프로세스
- 모니터링 & 로깅
- 성능 최적화

---

## 📊 성공 지표

- ✅ 8주 내 4개 Phase 완료
- ✅ 전체 API 테스트 통과율 100%
- ✅ GitHub 저장소 15+ 커밋
- ✅ AWS Free Tier 내에서 배포 성공
- ✅ Python 크롤러 정상 작동 (1주일 이상)
- ✅ Gemini API 요약 자동 생성 확인
- ✅ 포트폴리오용 완성된 프로젝트

---

## 📞 주의사항

1. **보안:**
   - application.yml 민감한 정보는 환경 변수로 관리
   - JWT 시크릿 키는 충분히 길게 (32자 이상)
   - 비밀번호는 반드시 암호화 후 저장

2. **성능:**
   - 데이터베이스 인덱스 필수
   - 페이지네이션으로 메모리 효율화
   - API 응답 시간 모니터링

3. **운영:**
   - 로깅으로 문제 추적
   - 정기적인 데이터베이스 백업
   - 크롤러 실패 시 재시도 로직

---

**이 기획서는 프로젝트 진행 중 상황에 따라 조정될 수 있습니다.**