package com.ainewsdesk.scheduler;

import com.ainewsdesk.crawler.PlaywrightOpenAIBlogCrawler;
import com.ainewsdesk.crawler.YouTubeAICrawler;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 크롤러 자동 실행 스케줄러
 */
@Component
public class CrawlerScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduler.class);

    private final YouTubeAICrawler youtubeCrawler;
    private final PlaywrightOpenAIBlogCrawler playwrightCrawler;
    private final ArticleRepository articleRepository;

    public CrawlerScheduler(
            YouTubeAICrawler youtubeCrawler,
            PlaywrightOpenAIBlogCrawler playwrightCrawler,
            ArticleRepository articleRepository) {
        this.youtubeCrawler = youtubeCrawler;
        this.playwrightCrawler = playwrightCrawler;
        this.articleRepository = articleRepository;
    }

    /**
     * YouTube 크롤러 스케줄 실행 - 매일 00시, 12시
     */
    @Scheduled(cron = "0 0 0,12 * * *", zone = "Asia/Seoul")
    public void runYouTubeCrawler() {
        log.info("==== [스케줄] YouTube 크롤러 자동 실행 시작 ==== (실행 시간: {})", LocalDateTime.now());

        try {
            // 크롤링 실행
            List<Article> newArticles = youtubeCrawler.crawl();

            // DB에 저장
            if (!newArticles.isEmpty()) {
                List<Article> savedArticles = articleRepository.saveAll(newArticles);
                log.info("[스케줄] YouTube 크롤링 완료 - {}개 새로운 동영상 저장됨", savedArticles.size());
            } else {
                log.info("[스케줄] YouTube 크롤링 완료 - 새로운 동영상 없음");
            }

        } catch (Exception e) {
            log.error("[스케줄] YouTube 크롤러 실행 중 오류 발생: {}", e.getMessage(), e);
        }

        log.info("==== [스케줄] YouTube 크롤러 자동 실행 종료 ==== (종료 시간: {})", LocalDateTime.now());
    }

    /**
     * Playwright 크롤러 스케줄 실행 - 매일 00시10분, 12시10분
     */
    @Scheduled(cron = "0 10 0,12 * * *", zone = "Asia/Seoul")
    public void runPlaywrightCrawler() {
        log.info("==== [스케줄] Playwright 크롤러 자동 실행 시작 ==== (실행 시간: {})", LocalDateTime.now());

        try {
            // 크롤링 실행
            List<Article> newArticles = playwrightCrawler.crawl();

            // DB에 저장
            if (!newArticles.isEmpty()) {
                List<Article> savedArticles = articleRepository.saveAll(newArticles);
                log.info("[스케줄] Playwright 크롤링 완료 - {}개 새로운 기사 저장됨", savedArticles.size());
            } else {
                log.info("[스케줄] Playwright 크롤링 완료 - 새로운 기사 없음");
            }

        } catch (Exception e) {
            log.error("[스케줄] Playwright 크롤러 실행 중 오류 발생: {}", e.getMessage(), e);
        }

        log.info("==== [스케줄] Playwright 크롤러 자동 실행 종료 ==== (종료 시간: {})", LocalDateTime.now());
    }
}
