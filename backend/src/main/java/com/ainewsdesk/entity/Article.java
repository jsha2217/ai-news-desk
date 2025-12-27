package com.ainewsdesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles", indexes = {
    @Index(name = "idx_crawled_at", columnList = "crawled_at DESC"),
    @Index(name = "idx_source_type", columnList = "source_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(length = 100)
    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(nullable = false)
    private Integer priority = 3;

    @Column(length = 50)
    private String category;

    @Column(length = 500)
    private String thumbnailUrl;

    private LocalDateTime publishedAt;

    @Column(name = "crawled_at", nullable = false)
    private LocalDateTime crawledAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        crawledAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }

    public enum SourceType {
        OFFICIAL,      // 공식 출처
        PROFESSIONAL,  // 전문 언론
        GENERAL        // 일반 출처
    }
}
