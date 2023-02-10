package com.example.takehome.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientUtil {

    public static WebClient getWebClient(final String url) {
        return WebClient
            .builder()
            .baseUrl(url)
            .filter(logRequest())
            .filter(logResponse())
            .build();
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("WebClient Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest
                .headers()
                .forEach((name, values) ->
                    values.forEach(value ->
                        log.info("WebClient Request Header: {}={}", name, value)));
            log.info("WebClient Request Body: {}", clientRequest.body().toString());
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("WebClient Response status: {}", clientResponse.statusCode());
            clientResponse
                .headers()
                .asHttpHeaders()
                .forEach((name, values) ->
                    values.forEach(value ->
                        log.info("WebClient Response Header: {}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }
}
