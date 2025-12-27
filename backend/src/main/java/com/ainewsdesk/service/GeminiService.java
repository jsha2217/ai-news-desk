package com.ainewsdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI API 서비스
 * <p>Google Gemini API 텍스트 생성 및 AI 요약 수행</p>
 */
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-pro}")
    private String model;

    @Value("${gemini.api.temperature:0.7}")
    private double temperature;

    @Value("${gemini.api.max-tokens:2048}")
    private int maxTokens;

    private final RestTemplate restTemplate;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Gemini API 텍스트 생성
     */
    public String generateText(String prompt) {
        try {
            String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            ));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            requestBody.put("generationConfig", generationConfig);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            throw new RuntimeException("Gemini API 응답이 예상 형식과 다릅니다.");

        } catch (Exception e) {
            throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * AI 뉴스 요약 생성
     */
    public Map<String, String> generateNewsSummary(List<Map<String, String>> articles) {
        if (articles == null || articles.isEmpty()) {
            throw new IllegalArgumentException("기사 목록이 비어있습니다.");
        }

        // 프롬프트 구성
        StringBuilder articlesText = new StringBuilder();
        for (int i = 0; i < articles.size(); i++) {
            Map<String, String> article = articles.get(i);
            articlesText.append(String.format("%d. %s\n%s\n\n",
                i + 1,
                article.get("title"),
                article.get("description")
            ));
        }

        String prompt = String.format("당신은 AI 뉴스 전문 요약가입니다. 다음 AI 관련 뉴스 기사들을 분석하여 요약해주세요.\n\n[오늘의 AI 뉴스]\n%s\n\n다음 형식으로 요약을 작성해주세요:\n\n1. 제목 (Title):\n- 오늘의 주요 AI 뉴스를 대표하는 흥미로운 제목 (20자 이내)\n\n2. 주요 하이라이트 (KeyHighlights):\n- 가장 중요한 4-5가지 내용을 불릿 포인트로 요약\n- 각 항목은 한 줄로 간결하게\n\n3. 상세 내용 (Content):\n- **중요: 상세 내용은 반드시 최소 1500자 이상으로 작성해주세요**\n- 전체 기사들의 핵심 내용을 종합적으로 상세히 설명\n- 4-5개 이상의 섹션으로 구성 (각 섹션은 ## 제목으로 시작)\n- 각 섹션마다 충분한 설명과 예시 포함\n- 주요 동향, 기술 발전, 업계 영향, 전망 등을 포함\n- 절대 답변을 중간에 끊지 말고 완전히 작성할 것\n\n응답 형식:\nTITLE: [제목]\nHIGHLIGHTS:\n• [하이라이트1]\n• [하이라이트2]\n• [하이라이트3]\n• [하이라이트4]\n• [하이라이트5]\nCONTENT:\n[상세 내용 - 최소 1500자 이상, 여러 섹션으로 구성]\n\n주의사항: 답변을 절대 중간에 끊지 말고, CONTENT 섹션을 완전히 작성한 후 종료하세요.", articlesText.toString());

        String response = generateText(prompt);

        // 응답 파싱
        return parseSummaryResponse(response);
    }

    /**
     * Gemini 응답 파싱 - 제목/하이라이트/내용 분리
     */
    public Map<String, String> parseSummaryResponse(String response) {
        Map<String, String> result = new HashMap<>();

        try {
            String[] lines = response.split("\n");
            StringBuilder title = new StringBuilder();
            StringBuilder highlights = new StringBuilder();
            StringBuilder content = new StringBuilder();
            String currentSection = "";

            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("TITLE:")) {
                    currentSection = "title";
                    title.append(line.substring(6).trim());
                } else if (line.startsWith("HIGHLIGHTS:")) {
                    currentSection = "highlights";
                } else if (line.startsWith("CONTENT:")) {
                    currentSection = "content";
                } else if (!line.isEmpty()) {
                    switch (currentSection) {
                        case "title":
                            title.append(" ").append(line);
                            break;
                        case "highlights":
                            if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                                highlights.append(line).append("\n");
                            }
                            break;
                        case "content":
                            content.append(line).append("\n");
                            break;
                    }
                }
            }

            result.put("title", title.toString().trim());
            result.put("keyHighlights", highlights.toString().trim());
            result.put("content", content.toString().trim());

            // 파싱 결과가 비어있으면 전체 응답 사용
            if (result.get("title").isEmpty()) {
                result.put("title", "AI 뉴스 요약");
            }
            if (result.get("content").isEmpty()) {
                result.put("content", response);
            }

        } catch (Exception e) {
            // 파싱 실패 시 기본값 설정
            result.put("title", "AI 뉴스 요약");
            result.put("keyHighlights", "");
            result.put("content", response);
        }

        return result;
    }
}
