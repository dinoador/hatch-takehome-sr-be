package com.example.takehome.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphQlResponseDto {

    private DataDto data;

    private List<ContinentDto> continents;

    private List<CustomContinentDto> continent;

}