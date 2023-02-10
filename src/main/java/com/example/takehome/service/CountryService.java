package com.example.takehome.service;

import com.example.takehome.dto.response.ContinentDto;
import com.example.takehome.dto.response.GraphQlResponseDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface CountryService {

    Mono<GraphQlResponseDto> getCountry(String countryCode);

    Mono<GraphQlResponseDto> getCountries(String countryCodes);

    Flux<ContinentDto> getContinentsByCountry(String countryCodes);

    Mono<GraphQlResponseDto> getUniqueContinentsByCountry(String countryCodes);
}
