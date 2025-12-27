package com.ainewsdesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "bookmarks",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_article", columnList = "user_id, article_id"),
        @Index(name = "idx_user_summary", columnList = "user_id, ai_summary_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "bookmark_type", nullable = false)
    private BookmarkType bookmarkType;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "ai_summary_id")
    private Long aiSummaryId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum BookmarkType {
        ARTICLE,
        AI_SUMMARY
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
