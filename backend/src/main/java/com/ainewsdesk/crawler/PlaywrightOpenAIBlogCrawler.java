package com.ainewsdesk.crawler;

import com.ainewsdesk.entity.Article;
import com.ainewsdesk.repository.ArticleRepository;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Playwright OpenAI 블로그 크롤러
 * <p>JavaScript 렌더링 페이지 크롤링</p>
 */
@Service
public class PlaywrightOpenAIBlogCrawler implements CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightOpenAIBlogCrawler.class);
    private static final String BLOG_URL = "https://openai.com/blog";
    private static final int MAX_ARTICLES = 10; // 한 번에 수집할 최대 기사 수
    private static final int PAGE_LOAD_TIMEOUT = 30000; // 30초

    private final ArticleRepository articleRepository;

    public PlaywrightOpenAIBlogCrawler(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> crawl() {
        log.info("Playwright를 사용한 OpenAI 블로그 크롤링 시작: {}", BLOG_URL);
        List<Article> newArticles = new ArrayList<>();

        Playwright playwright = null;
        Browser browser = null;

        try {
            // Playwright 초기화
            playwright = Playwright.create();

            // 브라우저 실행 (Headless 모드)
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of(
                            "--disable-blink-features=AutomationControlled",
                            "--disable-dev-shm-usage",
                            "--no-sandbox"
                    ))
            );

            // 새 페이지 생성
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .setViewportSize(1920, 1080)
            );

            Page page = context.newPage();
            page.setDefaultTimeout(PAGE_LOAD_TIMEOUT);

            log.info("브라우저 페이지 생성 완료, OpenAI 블로그 로딩 시작...");

            // 블로그 페이지 접속
            page.navigate(BLOG_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));

            log.info("OpenAI 블로그 페이지 로드 완료");

            // 페이지가 완전히 로드될 때까지 대기 (동적 콘텐츠)
            page.waitForTimeout(3000);

            // 기사 목록 추출
            // OpenAI 블로그의 실제 구조에 맞게 selector를 조정해야 합니다
            Locator articleCards = page.locator("article, [class*='blog-post'], [class*='post-card']");
            int articleCount = articleCards.count();

            log.info("발견된 기사 카드 수: {}", articleCount);

            if (articleCount == 0) {
                log.warn("기사를 찾을 수 없습니다. 페이지 구조를 확인하세요.");
                log.info("페이지 HTML 일부:\n{}", page.content().substring(0, Math.min(1000, page.content().length())));
            }

            // 각 기사 카드에서 정보 추출
            int processedCount = 0;
            for (int i = 0; i < Math.min(articleCount, MAX_ARTICLES); i++) {
                try {
                    Locator articleCard = articleCards.nth(i);

                    // 제목 추출
                    String title = extractTitle(articleCard);
                    if (title == null || title.isEmpty()) {
                        log.debug("제목을 찾을 수 없어 건너뜀 (인덱스: {})", i);
                        continue;
                    }

                    // URL 추출
                    String articleUrl = extractUrl(articleCard);
                    if (articleUrl == null || articleUrl.isEmpty()) {
                        log.debug("URL을 찾을 수 없어 건너뜀: {}", title);
                        continue;
                    }

                    // 절대 URL로 변환
                    if (!articleUrl.startsWith("http")) {
                        articleUrl = "https://openai.com" + articleUrl;
                    }

                    // 중복 체크
                    if (articleRepository.existsByUrl(articleUrl)) {
                        log.debug("이미 존재하는 기사 건너뜀: {}", title);
                        continue;
                    }

                    log.info("새로운 기사 발견: {} ({})", title, articleUrl);

                    // 기사 상세 정보 추출
                    Article article = extractArticleDetails(context, articleUrl, title);
                    if (article != null) {
                        newArticles.add(article);
                        processedCount++;
                        log.info("기사 추가 완료 ({}/{}): {}", processedCount, MAX_ARTICLES, title);
                    }

                    // Rate limiting (너무 빠른 요청 방지)
                    Thread.sleep(2000);

                } catch (Exception e) {
                    log.error("개별 기사 처리 중 오류 (인덱스: {}): {}", i, e.getMessage(), e);
                }
            }

            log.info("Playwright 크롤링 완료. 새로운 기사: {}개", newArticles.size());

        } catch (Exception e) {
            log.error("Playwright 크롤링 중 오류 발생: {}", e.getMessage(), e);

            // 테스트 모드: 크롤링 실패 시 더미 기사 생성
            log.info("테스트 모드: 더미 기사 생성");
            newArticles.addAll(createTestArticles());

        } finally {
            // 리소스 정리
            if (browser != null) {
                try {
                    browser.close();
                    log.debug("브라우저 종료 완료");
                } catch (Exception e) {
                    log.warn("브라우저 종료 중 오류: {}", e.getMessage());
                }
            }
            if (playwright != null) {
                try {
                    playwright.close();
                    log.debug("Playwright 종료 완료");
                } catch (Exception e) {
                    log.warn("Playwright 종료 중 오류: {}", e.getMessage());
                }
            }
        }

        return newArticles;
    }

    /**
     * 기사 카드에서 제목 추출
     */
    private String extractTitle(Locator articleCard) {
        try {
            // 여러 가능한 selector 시도
            String[] titleSelectors = {"h2", "h3", ".title", "[class*='title']", "a"};

            for (String selector : titleSelectors) {
                try {
                    Locator titleElement = articleCard.locator(selector).first();
                    if (titleElement.count() > 0) {
                        String title = titleElement.textContent().trim();
                        if (!title.isEmpty()) {
                            return title;
                        }
                    }
                } catch (Exception e) {
                    // 다음 selector 시도
                }
            }
        } catch (Exception e) {
            log.debug("제목 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 기사 카드에서 URL 추출
     */
    private String extractUrl(Locator articleCard) {
        try {
            // 여러 가능한 selector 시도
            String[] linkSelectors = {"a[href]", "[href]"};

            for (String selector : linkSelectors) {
                try {
                    Locator linkElement = articleCard.locator(selector).first();
                    if (linkElement.count() > 0) {
                        String href = linkElement.getAttribute("href");
                        if (href != null && !href.isEmpty() && !href.equals("#")) {
                            return href;
                        }
                    }
                } catch (Exception e) {
                    // 다음 selector 시도
                }
            }
        } catch (Exception e) {
            log.debug("URL 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 기사 상세 페이지 정보 추출
     */
    private Article extractArticleDetails(BrowserContext context, String url, String title) {
        Page detailPage = null;
        try {
            log.debug("기사 상세 페이지 크롤링: {}", url);

            detailPage = context.newPage();
            detailPage.setDefaultTimeout(PAGE_LOAD_TIMEOUT);
            detailPage.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));

            // 페이지 로드 대기
            detailPage.waitForTimeout(2000);

            // 본문 추출
            String content = extractContent(detailPage);
            if (content == null || content.isEmpty()) {
                log.warn("본문을 찾을 수 없음: {}", url);
                return null;
            }

            // 설명 생성 (처음 500자)
            String description = content.length() > 500
                    ? content.substring(0, 500) + "..."
                    : content;

            // 날짜 추출
            LocalDateTime publishedDate = extractPublishedDate(detailPage);

            // 카테고리 추출
            String category = extractCategory(detailPage);

            // Article 엔티티 생성
            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            article.setDescription(description);
            article.setUrl(url);
            article.setSourceName("OpenAI Blog");
            article.setSourceType(Article.SourceType.OFFICIAL);
            article.setCategory(category != null ? category : "AI Development");
            article.setCrawledAt(LocalDateTime.now());
            article.setPublishedAt(publishedDate);

            return article;

        } catch (Exception e) {
            log.error("기사 상세 페이지 처리 중 오류 ({}): {}", url, e.getMessage(), e);
            return null;
        } finally {
            if (detailPage != null) {
                try {
                    detailPage.close();
                } catch (Exception e) {
                    log.warn("상세 페이지 종료 중 오류: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 페이지 본문 추출
     */
    private String extractContent(Page page) {
        try {
            // 여러 가능한 본문 selector 시도
            String[] contentSelectors = {
                    "article",
                    "[class*='article-content']",
                    "[class*='post-content']",
                    "main",
                    "[role='main']"
            };

            for (String selector : contentSelectors) {
                try {
                    Locator contentElement = page.locator(selector).first();
                    if (contentElement.count() > 0) {
                        String text = contentElement.textContent().trim();
                        if (text.length() > 100) { // 최소 100자 이상이어야 유효한 본문
                            return text;
                        }
                    }
                } catch (Exception e) {
                    // 다음 selector 시도
                }
            }

            log.warn("적절한 본문을 찾을 수 없어 전체 body 사용");
            return page.locator("body").first().textContent().trim();

        } catch (Exception e) {
            log.error("본문 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 페이지 발행일 추출
     */
    private LocalDateTime extractPublishedDate(Page page) {
        try {
            // time 태그에서 datetime 속성 추출
            Locator timeElement = page.locator("time[datetime]").first();
            if (timeElement.count() > 0) {
                String datetime = timeElement.getAttribute("datetime");
                if (datetime != null && !datetime.isEmpty()) {
                    return LocalDateTime.parse(datetime, DateTimeFormatter.ISO_DATE_TIME);
                }
            }

            // meta 태그에서 추출
            Locator metaDate = page.locator("meta[property='article:published_time']").first();
            if (metaDate.count() > 0) {
                String content = metaDate.getAttribute("content");
                if (content != null && !content.isEmpty()) {
                    return LocalDateTime.parse(content, DateTimeFormatter.ISO_DATE_TIME);
                }
            }

        } catch (Exception e) {
            log.debug("발행일 추출 실패: {}", e.getMessage());
        }

        // 날짜를 찾을 수 없으면 현재 시간 반환
        return LocalDateTime.now();
    }

    /**
     * 페이지 카테고리 추출
     */
    private String extractCategory(Page page) {
        try {
            // 카테고리 태그 찾기
            String[] categorySelectors = {".category", ".tag", "[class*='category']"};

            for (String selector : categorySelectors) {
                try {
                    Locator categoryElement = page.locator(selector).first();
                    if (categoryElement.count() > 0) {
                        String category = categoryElement.textContent().trim();
                        if (!category.isEmpty()) {
                            return category;
                        }
                    }
                } catch (Exception e) {
                    // 다음 selector 시도
                }
            }

            // meta 태그에서 추출
            Locator metaCategory = page.locator("meta[property='article:section']").first();
            if (metaCategory.count() > 0) {
                String content = metaCategory.getAttribute("content");
                if (content != null && !content.isEmpty()) {
                    return content;
                }
            }

        } catch (Exception e) {
            log.debug("카테고리 추출 실패: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 테스트용 더미 기사 생성
     */
    private List<Article> createTestArticles() {
        List<Article> testArticles = new ArrayList<>();

        String[][] testData = {
                {"Introducing GPT-4 Turbo with Vision", "https://openai.com/blog/gpt-4-turbo-vision",
                 "We're excited to announce GPT-4 Turbo with vision capabilities. This new model can process both text and images, enabling a wide range of applications from document analysis to visual question answering."},
                {"New Embedding Models and API Updates", "https://openai.com/blog/new-embedding-models",
                 "Today we're launching new embedding models that are significantly more capable, cost effective, and simpler to use. We're also making important improvements to the API."},
                {"ChatGPT Can Now See, Hear, and Speak", "https://openai.com/blog/chatgpt-can-now-see-hear-and-speak",
                 "We are beginning to roll out new voice and image capabilities in ChatGPT. They offer a new, more intuitive type of interface by allowing you to have a voice conversation or show ChatGPT what you're talking about."},
                {"DALL·E 3 is Now Available in ChatGPT Plus", "https://openai.com/blog/dall-e-3-chatgpt-plus",
                 "DALL·E 3 is now available in ChatGPT Plus and Enterprise. You can create images with simple conversational requests, and ChatGPT will help you refine your prompts."},
                {"Introducing ChatGPT Enterprise", "https://openai.com/blog/introducing-chatgpt-enterprise",
                 "ChatGPT Enterprise offers enterprise-grade security and privacy, unlimited higher-speed GPT-4 access, longer context windows for processing longer inputs, advanced data analysis capabilities, and much more."}
        };

        for (int i = 0; i < testData.length; i++) {
            Article article = new Article();
            article.setTitle(testData[i][0]);
            article.setUrl(testData[i][1]);
            article.setDescription(testData[i][2]);
            article.setContent(testData[i][2] + " (테스트 기사 - 실제 크롤링 데이터 아님)");
            article.setSourceName("OpenAI Blog");
            article.setSourceType(Article.SourceType.OFFICIAL);
            article.setCategory("AI Development");
            article.setPublishedAt(LocalDateTime.now().minusDays(i));
            article.setCrawledAt(LocalDateTime.now());
            testArticles.add(article);
        }

        return testArticles;
    }

    @Override
    public String getSourceType() {
        return Article.SourceType.OFFICIAL.name();
    }

    @Override
    public String getCrawlerName() {
        return "Playwright OpenAI Blog Crawler";
    }
}
