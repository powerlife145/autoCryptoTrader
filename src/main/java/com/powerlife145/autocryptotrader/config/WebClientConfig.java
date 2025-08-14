package com.powerlife145.autocryptotrader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient upbitWebClient(@Value ("${upbit.base-url}") String baseUrl)
    {
        HttpClient httpClient =
                HttpClient.create().responseTimeout(Duration.ofSeconds(5));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs()
                                .maxInMemorySize(4* 1024 * 1024))
                        .build())
                        .defaultHeader("Accept", "application/json")
                        .build();

    }
}
