package com.moviebooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("电影预约APP API文档")
                        .version("1.0.0")
                        .description("电影预约APP后端服务API文档")
                        .contact(new Contact()
                                .name("电影预约团队")
                                .email("contact@moviebooking.com")
                                .url("https://moviebooking.com")
                        )
                );
    }
}
