package com.economia.quiz.api.dto;

import java.time.LocalDateTime;

public record AttemptAnswerResponse(
        Long questionId,
        Long selectedOptionId,
        Boolean correct,
        Integer pointsAwarded,
        LocalDateTime answeredAt
) {
}
