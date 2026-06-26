package com.economia.quiz.api.dto;

public record OptionResponse(
        Long id,
        String text,
        Integer position,
        Boolean correct
) {
}
