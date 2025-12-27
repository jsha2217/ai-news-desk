# AI News Desk API Documentation

백엔드 API의 정적 Swagger 문서입니다.

## 파일 구조

```
backend/docs/
├── index.html      # Swagger UI HTML
└── openapi.json    # OpenAPI 3.0 스펙
```

## 사용 방법

### 1. jsDelivr로 바로 보기 (권장)

GitHub에 푸시 후 바로 접속:

**Swagger UI로 보기:**
```
https://petstore.swagger.io/?url=https://cdn.jsdelivr.net/gh/jsha2217/ai-news-desk@main/backend/docs/openapi.json
```

**또는 커스텀 HTML로 보기:**
```
https://cdn.jsdelivr.net/gh/jsha2217/ai-news-desk@main/backend/docs/index.html
```

> 참고: GitHub에 푸시 후 jsDelivr CDN 캐시가 업데이트되는데 1-2분 소요될 수 있습니다.

### 2. 로컬에서 열기

브라우저에서 직접 `index.html` 파일을 열거나, 간단한 HTTP 서버로 실행:

```bash
cd backend/docs
python3 -m http.server 8000
```

그 다음 브라우저에서 접속:
```
http://localhost:8000
```

### 3. GitHub Pages로 배포

1. 이 디렉토리를 GitHub에 푸시
2. Repository Settings → Pages
3. Source를 `main` 브랜치의 `/backend/docs` 폴더로 설정
4. 자동으로 배포된 URL로 접근 가능

## OpenAPI 스펙 업데이트

백엔드 API가 변경되면 다음 명령으로 OpenAPI 스펙을 업데이트:

```bash
# 백엔드 서버가 실행 중일 때
curl http://localhost:8080/api/v3/api-docs -o backend/docs/openapi.json
```

## 기능

- 전체 API 엔드포인트 문서화
- Try it out 기능 (실제 API 호출 테스트)
- JWT 인증 지원
- 요청/응답 스키마 확인
- 다운로드 가능한 OpenAPI 스펙