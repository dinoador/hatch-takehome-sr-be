package com.example.takehome.service.impl;

import com.example.takehome.dto.request.GraphQlRequestDto;
import com.example.takehome.dto.response.ContinentDto;
import com.example.takehome.dto.response.CountryDto;
import com.example.takehome.dto.response.CustomContinentDto;
import com.example.takehome.dto.response.GraphQlResponseDto;
import com.example.takehome.service.CountryService;
import com.example.takehome.util.StringUtil;
import com.example.takehome.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryServiceImpl implements CountryService {

    @Value("${countries.trevorblades.url}")
    private String countriesUrl;

    @Override
    public Mono<GraphQlResponseDto> getCountry(final String countryCode) {

        String countryQuery = """
            query {
                country(code: $0) {
                    code
                    name
                    continent {
                        code
                        name
                    }
                }
            }
        """;

        countryQuery = countryQuery.replace("$0", StringUtil.sanitizeString(countryCode));

        final WebClient webClient = WebClientUtil.getWebClient(countriesUrl);
        final GraphQlRequestDto graphQLRequestBody =
            GraphQlRequestDto
                .builder()
                .query(countryQuery)
                .build();

        return webClient.post()
                .bodyValue(graphQLRequestBody)
                .retrieve()
                .bodyToMono(GraphQlResponseDto.class);
    }

    @Override
    public Mono<GraphQlResponseDto> getCountries(final String countryCodes) {

        String queryCountries = """
            query {
                countries(filter: {code: {in: $0}}) {
                    code
                    name
                    continent {
                        code
                        name
                    }
                }
            }
        """;

        queryCountries =
            queryCountries.replace("$0", StringUtil.sanitizeList(Arrays.asList(countryCodes.split(","))));

        final WebClient webClient = WebClientUtil.getWebClient(countriesUrl);
        final GraphQlRequestDto graphQLRequestBody =
            GraphQlRequestDto
                .builder()
                .query(queryCountries)
                .build();

        return webClient.post()
            .bodyValue(graphQLRequestBody)
            .retrieve()
            .bodyToMono(GraphQlResponseDto.class);
    }

    public Mono<GraphQlResponseDto> getContinents(final String continentCodes) {

        String queryContinents = """
            query {
                continents(filter: {code: {in: $0}}) {
                    code
                    name
                    countries {
                        code
                        name
                    }
                }
            }
        """;

        queryContinents =
            queryContinents.replace("$0", StringUtil.sanitizeList(Arrays.asList(continentCodes.split(","))));

        final WebClient webClient = WebClientUtil.getWebClient(countriesUrl);
        final GraphQlRequestDto graphQLRequestBody =
            GraphQlRequestDto
                .builder()
                .query(queryContinents)
                .build();

        return webClient.post()
            .bodyValue(graphQLRequestBody)
            .retrieve()
            .bodyToMono(GraphQlResponseDto.class);
    }

    private GraphQlResponseDto getBlockingCountry(final String countryCode) {
        return getCountry(countryCode).block();
    }

    private GraphQlResponseDto getBlockingCountries(final String countryCodes) {
        return getCountries(countryCodes).block();
    }

    @Override
    public Flux<ContinentDto> getContinentsByCountry(final String countryCodes) {
        final List<String> countryCodeList = Arrays.asList(countryCodes.split(","));
        return Flux.create((emitter) -> {
            CompletableFuture<List<ContinentDto>> future =
                CompletableFuture.supplyAsync(() ->
                    countryCodeList
                        .stream()
                        .map(this::getBlockingCountry)
                        .map(obj -> obj.getData().getCountry().getContinent())
                        .collect(Collectors.toList()));
            future.whenComplete((vehicleDtoList, exception) -> {
                if (exception == null) {
                    vehicleDtoList.forEach(emitter::next);
                }
                emitter.complete();
            });
        });
    }

    private Map<String, ContinentDto> getCountryMap(final GraphQlResponseDto countries){
        return
            countries
                .getData()
                .getCountries()
                .stream()
                .collect(
                    Collectors.toMap(
                        CountryDto::getCode,
                        CountryDto::getContinent,
                        (cd1, cd2) -> cd1));
    }

    private Map<String, ContinentDto> getContinentMap(final GraphQlResponseDto countries) {
        return
            countries
                .getData()
                .getCountries()
                .stream()
                .map(CountryDto::getContinent)
                .collect(
                    Collectors.toMap(
                        ContinentDto::getCode,
                        v -> v,
                        (cd1, cd2) -> cd1));
    }

    private GraphQlResponseDto getUniqueContinentsResponseDto(final String countryCodes) {
        final GraphQlResponseDto graphQlResponseDto = getBlockingCountries(countryCodes);
        return
            Optional.of(graphQlResponseDto)
                .map(dto ->
                    dto.getData().getCountries())
                .filter(countryDtoList ->
                    countryDtoList.size() > 0)
                .map(countryDtoList ->
                    buildUniqueContinentsResponseDto(graphQlResponseDto))
                .orElseGet(() ->
                    GraphQlResponseDto
                        .builder()
                        .continent(new ArrayList<>())
                        .build());
    }

    private GraphQlResponseDto buildUniqueContinentsResponseDto(final GraphQlResponseDto countries) {

        final Map<String, ContinentDto> countryMap = this.getCountryMap(countries);
        final Map<String, ContinentDto> continentMap = this.getContinentMap(countries);

        final String continentCodes = String.join(",", new ArrayList<>(continentMap.keySet()));
        final Optional<GraphQlResponseDto> continents =
            Optional.ofNullable(this.getContinents(continentCodes).block());

        final List<CustomContinentDto> uniqueContinentsByCountry =
            continents
                .map(responseDto -> responseDto.getData().getContinents())
                .map(list -> list
                    .stream()
                    .map(continentDto -> CustomContinentDto
                        .builder()
                        .countries(
                            countryMap
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().getCode().equals(continentDto.getCode()))
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList()))
                        .name(continentDto.getName())
                        .otherCountries(
                            continentDto
                                .getCountries()
                                .stream()
                                .map(CountryDto::getCode)
                                .filter(f -> !countryMap.containsKey(f))
                                .collect(Collectors.toList()))
                        .build())
                    .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        return
            GraphQlResponseDto
                .builder()
                .continent(uniqueContinentsByCountry)
                .build();
    }

    @Override
    public Mono<GraphQlResponseDto> getUniqueContinentsByCountry(final String countryCodes) {
        return Mono.create((emitter) -> {
            CompletableFuture<GraphQlResponseDto> future =
                CompletableFuture.supplyAsync(() ->
                    this.getUniqueContinentsResponseDto(countryCodes));
            future.whenComplete((responseDto, exception) -> {
                emitter.success(responseDto);
            });
        });
    }

}
