package com.economia.quiz.api.dto;

import com.economia.quiz.domain.AttemptStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AttemptResponse(
        Long id,
        Long quizId,
        String quizTitle,
        String participantId,
        String participantName,
        AttemptStatus status,
        Integer score,
        Integer maxScore,
        BigDecimal percentage,
        Boolean passed,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<AttemptAnswerResponse> answers
) {
}
