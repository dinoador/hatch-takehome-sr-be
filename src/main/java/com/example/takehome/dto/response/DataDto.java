package com.example.takehome.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDto {

    private CountryDto country;

    private List<CountryDto> countries;

    private List<ContinentDto> continents;

}
