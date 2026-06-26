package com.economia.quiz.api.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String statement,
        String explanation,
        Integer position,
        Integer points,
        Boolean active,
        List<OptionResponse> options
) {
}
