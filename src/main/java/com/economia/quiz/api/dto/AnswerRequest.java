package com.economia.quiz.api.dto;

import jakarta.validation.constraints.NotNull;

public record AnswerRequest(
        @NotNull Long questionId,
        @NotNull Long selectedOptionId
) {
}
