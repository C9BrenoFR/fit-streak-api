package br.ufjf.fsapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fit Streak API")
                        .description("""
                            API do sistema.
                    
                            Equipe:
                            - Breno Furtado Rosado (breno.rosado@estudante.ufjf.br)
                            - Breno Reis Machado (brenomachado.3064@gmail.com)
                            """)
                        .version("1.0")
                );
    }
}