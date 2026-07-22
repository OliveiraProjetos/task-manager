package io.github.oliveiraprojetos.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("API para gerenciamento de tarefas desenvolvida com Spring Boot.")
                        .version("1.0.0"));

    }

}