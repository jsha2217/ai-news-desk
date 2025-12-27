package com.ainewsdesk.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * YouTube API 설정 클래스
 * <p>YouTube Data API v3 사용 설정</p>
 */
@Configuration
public class YouTubeConfig {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.max-results:10}")
    private int maxResults;

    @Value("${youtube.api.channels.openai:}")
    private String openaiChannelId;

    @Value("${youtube.api.channels.google-deepmind:}")
    private String deepmindChannelId;

    @Value("${youtube.api.channels.anthropic:}")
    private String anthropicChannelId;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "AI News Desk";

    /**
     * YouTube API 클라이언트 Bean
     */
    @Bean
    public YouTube youTube() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new YouTube.Builder(httpTransport, JSON_FACTORY, request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * YouTube API Key 반환
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 최대 결과 수 반환
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * AI 관련 채널 ID 맵 반환
     */
    public Map<String, String> getAiChannels() {
        Map<String, String> channels = new HashMap<>();

        if (openaiChannelId != null && !openaiChannelId.isEmpty()) {
            channels.put("OpenAI", openaiChannelId);
        }
        if (deepmindChannelId != null && !deepmindChannelId.isEmpty()) {
            channels.put("Google DeepMind", deepmindChannelId);
        }
        if (anthropicChannelId != null && !anthropicChannelId.isEmpty()) {
            channels.put("Anthropic", anthropicChannelId);
        }

        return channels;
    }
}
