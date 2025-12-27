package com.ainewsdesk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정
 * <p>API 문서화 Swagger UI 설정</p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정 - JWT 인증 포함
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI News Desk API")
                        .version("1.0.0")
                        .description("AI 뉴스 자동 수집 및 요약 시스템의 REST API 문서\n\n" +
                                "주요 기능:\n" +
                                "- 뉴스 기사 조회, 검색, 필터링\n" +
                                "- 사용자 회원가입, 로그인, 계정 관리\n" +
                                "- 기사 북마크 관리\n" +
                                "- AI 요약 조회 및 관리")
                        .contact(new Contact()
                                .name("jsha2217")
                                .url("https://github.com/jsha2217"))
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}
