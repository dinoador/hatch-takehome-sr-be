package com.example.takehome.controller;

import com.example.takehome.dto.response.ContinentDto;
import com.example.takehome.dto.response.GraphQlResponseDto;
import com.example.takehome.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/country")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mono<GraphQlResponseDto>> getCountry(@RequestParam("countryCode") String countryCode) {
        final Mono<GraphQlResponseDto> countryDto = countryService.getCountry(countryCode);
        return new ResponseEntity<>(countryDto, HttpStatus.OK);
    }

    @GetMapping("/countries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mono<GraphQlResponseDto>> getCountries(@RequestParam("countryCodes") String countryCodes) {
        final Mono<GraphQlResponseDto> countryDto = countryService.getCountries(countryCodes);
        return new ResponseEntity<>(countryDto, HttpStatus.OK);
    }

    @GetMapping("/continentsByCountry")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Flux<ContinentDto>> getContinentsByCountry(@RequestParam("countryCodes") String countryCodes) {
        final Flux<ContinentDto> continents = countryService.getContinentsByCountry(countryCodes);
        return new ResponseEntity<>(continents, HttpStatus.OK);
    }

    @GetMapping("/uniqueContinentsByCountry")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mono<GraphQlResponseDto>> getUniqueContinentsByCountry(@RequestParam("countryCodes") String countryCodes) {
        final Mono<GraphQlResponseDto> continents = countryService.getUniqueContinentsByCountry(countryCodes);
        return new ResponseEntity<>(continents, HttpStatus.OK);
    }
}
