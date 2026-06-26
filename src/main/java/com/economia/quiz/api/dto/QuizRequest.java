package com.economia.quiz.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record QuizRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 500) String description,
        @Size(max = 100) String externalReference,
        @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal passPercentage,
        @Positive Integer timeLimitMinutes,
        @NotEmpty List<@Valid QuestionRequest> questions
) {
}
