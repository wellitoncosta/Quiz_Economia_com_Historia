package com.economia.quiz.api.dto;

import com.economia.quiz.domain.QuizStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuizResponse(
        Long id,
        String title,
        String description,
        String externalReference,
        QuizStatus status,
        BigDecimal passPercentage,
        Integer timeLimitMinutes,
        Integer maxScore,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<QuestionResponse> questions
) {
}
