package com.ServStatusBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.yml")
public class BotConfig {
    @Value("${name}")
    String botName;

    @Value("${token}")
    String token;

    @Value("${token_ai}")
    String tokenAi;
}
