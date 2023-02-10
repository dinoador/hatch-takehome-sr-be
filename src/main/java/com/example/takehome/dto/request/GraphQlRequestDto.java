package com.example.takehome.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GraphQlRequestDto {

    private String query;

    private Object variables;

}

