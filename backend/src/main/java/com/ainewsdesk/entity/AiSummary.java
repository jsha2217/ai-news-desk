package com.ainewsdesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_summaries", indexes = {
    @Index(name = "idx_period_start", columnList = "summary_period_start DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
