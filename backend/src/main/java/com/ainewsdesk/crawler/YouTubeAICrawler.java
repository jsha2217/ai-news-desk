package com.ainewsdesk.crawler;

import com.ainewsdesk.config.YouTubeConfig;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.repository.ArticleRepository;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YouTube Data API AI 뉴스 크롤러
 * <p>AI 기업 공식 YouTube 채널 최신 동영상 수집</p>
 */
@Service
public class YouTubeAICrawler implements CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(YouTubeAICrawler.class);

    private final YouTube youtube;
    private final YouTubeConfig youtubeConfig;
    private final ArticleRepository articleRepository;

    public YouTubeAICrawler(YouTube youtube, YouTubeConfig youtubeConfig, ArticleRepository articleRepository) {
        this.youtube = youtube;
        this.youtubeConfig = youtubeConfig;
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> crawl() {
        log.info("YouTube AI 채널 크롤링 시작");
        List<Article> newArticles = new ArrayList<>();

        Map<String, String> aiChannels = youtubeConfig.getAiChannels();

        if (aiChannels.isEmpty()) {
            log.warn("설정된 YouTube 채널이 없습니다.");
            return newArticles;
        }

        for (Map.Entry<String, String> channel : aiChannels.entrySet()) {
            String channelName = channel.getKey();
            String channelId = channel.getValue();

            try {
                log.info("채널 크롤링 시작: {} (ID: {})", channelName, channelId);
                List<Article> channelArticles = crawlChannel(channelName, channelId);
                newArticles.addAll(channelArticles);
                log.info("채널 크롤링 완료: {} - {}개 수집", channelName, channelArticles.size());

                // Rate limiting (API quota 절약)
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("채널 크롤링 중 오류 발생: {} - {}", channelName, e.getMessage(), e);
            }
        }

        log.info("YouTube AI 채널 크롤링 완료. 총 {}개 기사 수집", newArticles.size());
        return newArticles;
    }

    /**
     * 특정 채널 최신 동영상 크롤링
     */
    private List<Article> crawlChannel(String channelName, String channelId) throws Exception {
        List<Article> articles = new ArrayList<>();

        // Step 1: 채널의 최신 동영상 검색
        YouTube.Search.List search = youtube.search()
                .list(List.of("id", "snippet"))
                .setKey(youtubeConfig.getApiKey())
                .setChannelId(channelId)
                .setType(List.of("video"))
                .setOrder("date")  // 최신순 정렬
                .setMaxResults((long) youtubeConfig.getMaxResults());

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResults = searchResponse.getItems();

        if (searchResults == null || searchResults.isEmpty()) {
            log.info("채널에 동영상이 없습니다: {}", channelName);
            return articles;
        }

        // Step 2: 동영상 ID 추출
        List<String> videoIds = new ArrayList<>();
        for (SearchResult result : searchResults) {
            String videoId = result.getId().getVideoId();
            videoIds.add(videoId);
        }

        // Step 3: 동영상 상세 정보 가져오기
        YouTube.Videos.List videoRequest = youtube.videos()
                .list(List.of("snippet", "contentDetails", "statistics"))
                .setKey(youtubeConfig.getApiKey())
                .setId(videoIds);

        VideoListResponse videoResponse = videoRequest.execute();
        List<Video> videos = videoResponse.getItems();

        // Step 4: Article 엔티티로 변환
        for (Video video : videos) {
            try {
                String videoId = video.getId();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                // 중복 체크
                if (articleRepository.existsByUrl(videoUrl)) {
                    log.debug("이미 존재하는 동영상 건너뜀: {}", video.getSnippet().getTitle());
                    continue;
                }

                Article article = convertVideoToArticle(video, channelName);
                articles.add(article);
                log.info("새로운 동영상 발견: {}", article.getTitle());

            } catch (Exception e) {
                log.error("동영상 변환 중 오류: {} - {}", video.getSnippet().getTitle(), e.getMessage());
            }
        }

        return articles;
    }

    /**
     * YouTube Video → Article 엔티티 변환
     */
    private Article convertVideoToArticle(Video video, String channelName) {
        Article article = new Article();

        // 기본 정보
        article.setTitle(video.getSnippet().getTitle());
        article.setDescription(video.getSnippet().getDescription());
        article.setUrl("https://www.youtube.com/watch?v=" + video.getId());

        // 본문 (설명 + 통계)
        StringBuilder content = new StringBuilder();
        content.append(video.getSnippet().getDescription());
        content.append("\n\n");
        content.append("[ 동영상 정보 ]\n");
        content.append("조회수: ").append(formatNumber(video.getStatistics().getViewCount())).append("\n");
        content.append("좋아요: ").append(formatNumber(video.getStatistics().getLikeCount())).append("\n");
        content.append("댓글: ").append(formatNumber(video.getStatistics().getCommentCount())).append("\n");
        content.append("채널: ").append(channelName);

        article.setContent(content.toString());

        // 출처 정보
        article.setSourceName(channelName + " YouTube");
        article.setSourceType(Article.SourceType.OFFICIAL);

        // 카테고리
        article.setCategory(determineCategory(video.getSnippet().getTitle(), video.getSnippet().getDescription()));

        // 썸네일
        if (video.getSnippet().getThumbnails() != null
                && video.getSnippet().getThumbnails().getHigh() != null) {
            article.setThumbnailUrl(video.getSnippet().getThumbnails().getHigh().getUrl());
        }

        // 날짜 정보
        article.setCrawledAt(LocalDateTime.now());

        if (video.getSnippet().getPublishedAt() != null) {
            Instant instant = Instant.ofEpochMilli(video.getSnippet().getPublishedAt().getValue());
            article.setPublishedAt(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        } else {
            article.setPublishedAt(LocalDateTime.now());
        }

        return article;
    }

    /**
     * 제목/설명 기반 카테고리 결정
     */
    private String determineCategory(String title, String description) {
        String combined = (title + " " + description).toLowerCase();

        if (combined.contains("gpt") || combined.contains("chatgpt") || combined.contains("language model")) {
            return "Language Models";
        } else if (combined.contains("dall-e") || combined.contains("image") || combined.contains("vision")) {
            return "Computer Vision";
        } else if (combined.contains("sora") || combined.contains("video")) {
            return "Video Generation";
        } else if (combined.contains("api") || combined.contains("developer")) {
            return "API & Tools";
        } else if (combined.contains("research") || combined.contains("paper")) {
            return "AI Research";
        } else if (combined.contains("safety") || combined.contains("alignment")) {
            return "AI Safety";
        } else if (combined.contains("announcement") || combined.contains("release")) {
            return "Product Announcements";
        } else {
            return "AI Development";
        }
    }

    /**
     * 숫자 포맷팅 - 천 단위 콤마
     */
    private String formatNumber(java.math.BigInteger number) {
        if (number == null) {
            return "0";
        }
        return String.format("%,d", number.longValue());
    }

    @Override
    public String getSourceType() {
        return Article.SourceType.OFFICIAL.name();
    }

    @Override
    public String getCrawlerName() {
        return "YouTube AI Channels Crawler";
    }
}
