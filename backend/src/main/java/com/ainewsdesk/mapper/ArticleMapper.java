package com.ainewsdesk.mapper;

import com.ainewsdesk.dto.ArticleDetailDto;
import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.entity.Article;
import org.springframework.stereotype.Component;

/**
 * Article 엔티티 ↔ DTO 변환
 */
@Component
public class ArticleMapper {

    /**
     * Article → ArticleDto 변환
     */
    public ArticleDto toDto(Article article) {
        if (article == null) {
            return null;
        }

        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setSourceName(article.getSourceName());
        dto.setSourceType(article.getSourceType() != null ? article.getSourceType().name() : null);
        dto.setCategory(article.getCategory());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCrawledAt(article.getCrawledAt());

        return dto;
    }

    /**
     * Article → ArticleDetailDto 변환
     */
    public ArticleDetailDto toDetailDto(Article article) {
        if (article == null) {
            return null;
        }

        ArticleDetailDto dto = new ArticleDetailDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setContent(article.getContent());
        dto.setUrl(article.getUrl());
        dto.setSourceName(article.getSourceName());
        dto.setSourceType(article.getSourceType() != null ? article.getSourceType().name() : null);
        dto.setCategory(article.getCategory());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCrawledAt(article.getCrawledAt());

        return dto;
    }
}
