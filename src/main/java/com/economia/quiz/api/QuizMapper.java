package com.economia.quiz.api;

import com.economia.quiz.api.dto.AttemptAnswerResponse;
import com.economia.quiz.api.dto.AttemptResponse;
import com.economia.quiz.api.dto.OptionResponse;
import com.economia.quiz.api.dto.QuestionResponse;
import com.economia.quiz.api.dto.QuizResponse;
import com.economia.quiz.domain.AnswerOption;
import com.economia.quiz.domain.AttemptAnswer;
import com.economia.quiz.domain.Question;
import com.economia.quiz.domain.Quiz;
import com.economia.quiz.domain.QuizAttempt;
import java.util.Comparator;

public final class QuizMapper {

    private QuizMapper() {
    }

    public static QuizResponse toQuizResponse(Quiz quiz, boolean includeCorrectAnswers) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getExternalReference(),
                quiz.getStatus(),
                quiz.getPassPercentage(),
                quiz.getTimeLimitMinutes(),
                quiz.maxScore(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                quiz.getQuestions().stream()
                        .sorted(Comparator.comparingInt(Question::getPosition))
                        .map(question -> toQuestionResponse(question, includeCorrectAnswers))
                        .toList()
        );
    }

    public static QuizResponse toQuizSummary(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getExternalReference(),
                quiz.getStatus(),
                quiz.getPassPercentage(),
                quiz.getTimeLimitMinutes(),
                null,
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                java.util.List.of()
        );
    }

    public static QuestionResponse toQuestionResponse(Question question, boolean includeCorrectAnswers) {
        return new QuestionResponse(
                question.getId(),
                question.getStatement(),
                includeCorrectAnswers ? question.getExplanation() : null,
                question.getPosition(),
                question.getPoints(),
                question.isActive(),
                question.orderedOptions().stream()
                        .map(option -> toOptionResponse(option, includeCorrectAnswers))
                        .toList()
        );
    }

    public static OptionResponse toOptionResponse(AnswerOption option, boolean includeCorrectAnswers) {
        return new OptionResponse(
                option.getId(),
                option.getText(),
                option.getPosition(),
                includeCorrectAnswers ? option.isCorrect() : null
        );
    }

    public static AttemptResponse toAttemptResponse(QuizAttempt attempt, boolean includeAnswers) {
        return new AttemptResponse(
                attempt.getId(),
                attempt.getQuiz().getId(),
                attempt.getQuiz().getTitle(),
                attempt.getParticipantId(),
                attempt.getParticipantName(),
                attempt.getStatus(),
                attempt.getScore(),
                attempt.getMaxScore(),
                attempt.getPercentage(),
                attempt.isPassed(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                includeAnswers ? attempt.getAnswers().stream()
                        .map(QuizMapper::toAttemptAnswerResponse)
                        .toList() : java.util.List.of()
        );
    }

    private static AttemptAnswerResponse toAttemptAnswerResponse(AttemptAnswer answer) {
        return new AttemptAnswerResponse(
                answer.getQuestion().getId(),
                answer.getSelectedOption().getId(),
                answer.isCorrect(),
                answer.getPointsAwarded(),
                answer.getAnsweredAt()
        );
    }
}
