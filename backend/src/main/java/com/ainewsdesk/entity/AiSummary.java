package com.ainewsdesk.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_summaries", indexes = {
    @Index(name = "idx_period_start", columnList = "summary_period_start DESC")
})
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_period_start", nullable = false)
    private LocalDateTime summaryPeriodStart;

    @Column(name = "summary_period_end", nullable = false)
    private LocalDateTime summaryPeriodEnd;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "key_highlights", columnDefinition = "LONGTEXT")
    private String keyHighlights;

    @Column(name = "related_articles_count")
    private Integer relatedArticlesCount;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SummaryStatus status = SummaryStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }

    public enum SummaryStatus {
        DRAFT,      // 초안
        PUBLISHED   // 발행됨
    }

    // Constructors
    public AiSummary() {
    }

    public AiSummary(Long id, LocalDateTime summaryPeriodStart, LocalDateTime summaryPeriodEnd,
                    String title, String content, String keyHighlights, Integer relatedArticlesCount,
                    LocalDateTime generatedAt, SummaryStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.summaryPeriodStart = summaryPeriodStart;
        this.summaryPeriodEnd = summaryPeriodEnd;
        this.title = title;
        this.content = content;
        this.keyHighlights = keyHighlights;
        this.relatedArticlesCount = relatedArticlesCount;
        this.generatedAt = generatedAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSummaryPeriodStart() {
        return summaryPeriodStart;
    }

    public void setSummaryPeriodStart(LocalDateTime summaryPeriodStart) {
        this.summaryPeriodStart = summaryPeriodStart;
    }

    public LocalDateTime getSummaryPeriodEnd() {
        return summaryPeriodEnd;
    }

    public void setSummaryPeriodEnd(LocalDateTime summaryPeriodEnd) {
        this.summaryPeriodEnd = summaryPeriodEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeyHighlights() {
        return keyHighlights;
    }

    public void setKeyHighlights(String keyHighlights) {
        this.keyHighlights = keyHighlights;
    }

    public Integer getRelatedArticlesCount() {
        return relatedArticlesCount;
    }

    public void setRelatedArticlesCount(Integer relatedArticlesCount) {
        this.relatedArticlesCount = relatedArticlesCount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public SummaryStatus getStatus() {
        return status;
    }

    public void setStatus(SummaryStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
