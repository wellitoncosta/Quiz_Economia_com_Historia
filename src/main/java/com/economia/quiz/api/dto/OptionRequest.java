package com.economia.quiz.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OptionRequest(
        @NotBlank @Size(max = 500) String text,
        @NotNull Boolean correct,
        @Positive Integer position
) {
}
