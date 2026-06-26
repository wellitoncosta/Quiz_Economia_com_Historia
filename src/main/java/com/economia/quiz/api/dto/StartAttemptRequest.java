package com.economia.quiz.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StartAttemptRequest(
        Long quizId,
        @Size(max = 100) String externalReference,
        @NotBlank @Size(max = 120) String participantId,
        @Size(max = 160) String participantName
) {

    @AssertTrue(message = "Informe quizId ou externalReference")
    public boolean hasQuizIdentifier() {
        return quizId != null || (externalReference != null && !externalReference.isBlank());
    }
}
