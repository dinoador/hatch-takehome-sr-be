package com.example.takehome;

import com.example.takehome.dto.response.*;
import com.example.takehome.service.CountryService;
import com.example.takehome.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class TakehomeApplicationTests {

    private CountryService countryService;

    @Autowired
    private Environment env;

    @BeforeEach
    public void BeforeAll() {
        countryService = new CountryServiceImpl();
        ReflectionTestUtils.setField(
            countryService, "countriesUrl", env.getProperty("countries.trevorblades.url"));
    }

    @Test
    public void getCountry_should_returnCountry_when_CountryFound() {
        final String countryCode = "CA";
        final Mono<GraphQlResponseDto> mono = countryService.getCountry(countryCode);

        final GraphQlResponseDto graphQlResponseDto =
            GraphQlResponseDto
                .builder()
                .data(
                    DataDto
                        .builder()
                        .country(this.mockCACountryDto())
                        .build())
                .build();

        StepVerifier.create(mono)
            .expectNext(graphQlResponseDto)
            .expectComplete()
            .verify();
    }

    @Test
    public void getCountry_should_returnNoCountry_when_CountryNotFound() {
        final String countryCode = "ZZ";
        final Mono<GraphQlResponseDto> mono = countryService.getCountry(countryCode);

        GraphQlResponseDto graphQlResponseDto =
            GraphQlResponseDto
                .builder()
                .data(
                    DataDto
                        .builder()
                        .build())
                .build();

        StepVerifier.create(mono)
            .expectNext(graphQlResponseDto)
            .expectComplete()
            .verify();
    }

    @Test
    public void getCountries_should_returnCountries_when_CountriesFound() {
        final String countryCodes = "US,CA";
        final Mono<GraphQlResponseDto> mono = countryService.getCountries(countryCodes);

        GraphQlResponseDto graphQlResponseDto =
            GraphQlResponseDto
                .builder()
                .data(
                    DataDto
                        .builder()
                        .countries(Arrays.asList(this.mockCACountryDto(), this.mockUSCountryDto()))
                        .build())
                .build();

        StepVerifier.create(mono)
            .expectNext(graphQlResponseDto)
            .expectComplete()
            .verify();
    }

    @Test
    public void getCountries_should_returnNoCountries_when_NoCountriesFound() {
        final String countryCodes = "YY,ZZ";
        final Mono<GraphQlResponseDto> mono = countryService.getCountries(countryCodes);

        final GraphQlResponseDto graphQlResponseDto =
            GraphQlResponseDto
                .builder()
                .data(
                    DataDto
                        .builder()
                        .countries(Collections.emptyList())
                        .build())
                .build();

        StepVerifier.create(mono)
            .expectNext(graphQlResponseDto)
            .expectComplete()
            .verify();
    }

    @Test
    public void getContinents_should_returnContinent_when_ContinentFound() {
        final String countryCodes = "US,CA";
        final Flux<ContinentDto> flux = countryService.getContinentsByCountry(countryCodes);

        StepVerifier.create(flux)
            .expectNext(this.mockNAContinentDto())
            .expectNext(this.mockNAContinentDto())
            .expectComplete()
            .verify();
    }

    @Test
    public void getUniqueContinentsByCountry_should_returnUniqueContinents_when_UniqueContinentsFound() {
        final String countryCodes = "US,CA";
        final Mono<GraphQlResponseDto> mono = countryService.getUniqueContinentsByCountry(countryCodes);

        final GraphQlResponseDto graphQlResponseDto =
            GraphQlResponseDto
                .builder()
                .continent(Collections.singletonList(this.mockCustomContinentDto()))
                .build();

        StepVerifier.create(mono)
                .expectNext(graphQlResponseDto)
                .expectComplete()
                .verify();
    }

    @Test
    public void getUniqueContinentsByCountry_should_returnContinentEmptyArray_when_UniqueContinentsNotFound() {
        final String countryCodes = "ZZ";
        final Mono<GraphQlResponseDto> mono = countryService.getUniqueContinentsByCountry(countryCodes);

        final GraphQlResponseDto graphQlResponseDto =
                GraphQlResponseDto
                    .builder()
                    .continent(Collections.emptyList())
                    .build();

        StepVerifier.create(mono)
                .expectNext(graphQlResponseDto)
                .expectComplete()
                .verify();
    }

    private CountryDto mockCACountryDto() {
        return
            CountryDto
                .builder()
                .code("CA")
                .name("Canada")
                .continent(this.mockNAContinentDto())
                .build();
    }

    private CountryDto mockUSCountryDto() {
        return
            CountryDto
                .builder()
                .code("US")
                .name("United States")
                .continent(this.mockNAContinentDto())
                .build();
    }

    private ContinentDto mockNAContinentDto(){
        return
            ContinentDto
                .builder()
                    .code("NA")
                    .name("North America")
                    .build();
    }

    private CustomContinentDto mockCustomContinentDto() {
        return
            CustomContinentDto
                .builder()
                .countries(Arrays.asList("US", "CA"))
                .name("North America")
                .otherCountries(Arrays.asList(
                    "AG", "AI", "AW", "BB", "BL", "BM", "BQ", "BS", "BZ", "CR", "CU", "CW", "DM", "DO", "GD",
                    "GL", "GP", "GT", "HN", "HT", "JM", "KN", "KY", "LC", "MF", "MQ", "MS", "MX", "NI", "PA",
                    "PM", "PR", "SV", "SX", "TC", "TT", "VC", "VG", "VI"))
                .build();
    }
}