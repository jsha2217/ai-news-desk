package com.ainewsdesk.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "bookmarks",
    indexes = {
        @Index(name = "idx_user_article", columnList = "user_id, article_id", unique = true)
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_article", columnNames = {"user_id", "article_id"})
    }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Bookmark() {
    }

    public Bookmark(Long id, Long userId, Long articleId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.articleId = articleId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
