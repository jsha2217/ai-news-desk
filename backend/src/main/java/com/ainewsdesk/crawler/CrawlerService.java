package com.ainewsdesk.crawler;

import com.ainewsdesk.entity.Article;
import java.util.List;

/**
 * 웹 크롤러 서비스 인터페이스
 */
public interface CrawlerService {

    /**
     * 크롤링 수행 - 새 기사 목록 반환
     */
    List<Article> crawl();

    /**
     * 출처 타입 반환
     */
    String getSourceType();

    /**
     * 크롤러 이름 반환
     */
    String getCrawlerName();
}
