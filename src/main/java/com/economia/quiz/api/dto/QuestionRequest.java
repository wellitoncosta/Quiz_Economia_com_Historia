package com.economia.quiz.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record QuestionRequest(
        @NotBlank @Size(max = 1000) String statement,
        @Size(max = 1000) String explanation,
        @Positive Integer position,
        @PositiveOrZero Integer points,
        Boolean active,
        @NotEmpty @Size(min = 2, max = 8) List<@Valid OptionRequest> options
) {
}
