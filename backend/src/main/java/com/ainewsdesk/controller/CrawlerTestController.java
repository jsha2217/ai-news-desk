package com.ainewsdesk.controller;

import com.ainewsdesk.crawler.PlaywrightOpenAIBlogCrawler;
import com.ainewsdesk.crawler.YouTubeAICrawler;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 크롤러 테스트용 컨트롤러
 */
@RestController
@RequestMapping("/test/crawler")
public class CrawlerTestController {

    private static final Logger log = LoggerFactory.getLogger(CrawlerTestController.class);

    private final PlaywrightOpenAIBlogCrawler playwrightCrawler;
    private final YouTubeAICrawler youtubeCrawler;
    private final ArticleRepository articleRepository;

    public CrawlerTestController(
            PlaywrightOpenAIBlogCrawler playwrightCrawler,
            YouTubeAICrawler youtubeCrawler,
            ArticleRepository articleRepository) {
        this.playwrightCrawler = playwrightCrawler;
        this.youtubeCrawler = youtubeCrawler;
        this.articleRepository = articleRepository;
    }

    /**
     * Playwright 크롤러 테스트 엔드포인트
     * POST /api/test/crawler/playwright
     */
    @PostMapping("/playwright")
    public ResponseEntity<Map<String, Object>> runPlaywrightCrawler() {
        log.info("==== Playwright 크롤러 테스트 시작 ====");

        Map<String, Object> response = new HashMap<>();

        try {
            // 크롤링 실행
            List<Article> newArticles = playwrightCrawler.crawl();

            log.info("크롤링 완료. 수집된 기사: {}개", newArticles.size());

            // DB에 저장
            if (!newArticles.isEmpty()) {
                List<Article> savedArticles = articleRepository.saveAll(newArticles);
                log.info("DB 저장 완료: {}개", savedArticles.size());

                response.put("success", true);
                response.put("message", "Playwright 크롤링 및 저장 완료");
                response.put("crawler", "Playwright OpenAI Blog");
                response.put("articleCount", savedArticles.size());
                response.put("articles", savedArticles);
            } else {
                response.put("success", true);
                response.put("message", "새로운 기사 없음");
                response.put("crawler", "Playwright OpenAI Blog");
                response.put("articleCount", 0);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Playwright 크롤러 테스트 중 오류 발생: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            response.put("error", e.toString());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * YouTube 크롤러 테스트 엔드포인트
     * POST /api/test/crawler/youtube
     */
    @PostMapping("/youtube")
    public ResponseEntity<Map<String, Object>> runYouTubeCrawler() {
        log.info("==== YouTube AI 채널 크롤러 테스트 시작 ====");

        Map<String, Object> response = new HashMap<>();

        try {
            // 크롤링 실행
            List<Article> newArticles = youtubeCrawler.crawl();

            log.info("크롤링 완료. 수집된 동영상: {}개", newArticles.size());

            // DB에 저장
            if (!newArticles.isEmpty()) {
                List<Article> savedArticles = articleRepository.saveAll(newArticles);
                log.info("DB 저장 완료: {}개", savedArticles.size());

                response.put("success", true);
                response.put("message", "YouTube 크롤링 및 저장 완료");
                response.put("crawler", "YouTube AI Channels");
                response.put("articleCount", savedArticles.size());
                response.put("articles", savedArticles);
            } else {
                response.put("success", true);
                response.put("message", "새로운 동영상 없음");
                response.put("crawler", "YouTube AI Channels");
                response.put("articleCount", 0);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("YouTube 크롤러 테스트 중 오류 발생: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", "크롤링 실패: " + e.getMessage());
            response.put("error", e.toString());

            return ResponseEntity.status(500).body(response);
        }
    }
}
