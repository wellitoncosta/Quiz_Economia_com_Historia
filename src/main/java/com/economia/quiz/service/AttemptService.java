package com.economia.quiz.service;

import com.economia.quiz.api.dto.AnswerRequest;
import com.economia.quiz.api.dto.StartAttemptRequest;
import com.economia.quiz.domain.AnswerOption;
import com.economia.quiz.domain.AttemptAnswer;
import com.economia.quiz.domain.AttemptStatus;
import com.economia.quiz.domain.Question;
import com.economia.quiz.domain.Quiz;
import com.economia.quiz.domain.QuizAttempt;
import com.economia.quiz.domain.QuizStatus;
import com.economia.quiz.repository.AnswerOptionRepository;
import com.economia.quiz.repository.QuestionRepository;
import com.economia.quiz.repository.QuizAttemptRepository;
import com.economia.quiz.repository.QuizRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttemptService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;

    public AttemptService(
            QuizRepository quizRepository,
            QuizAttemptRepository attemptRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository optionRepository
    ) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public QuizAttempt start(StartAttemptRequest request) {
        Quiz quiz = resolveQuiz(request);
        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new BusinessException("Apenas quizzes publicados podem ser respondidos");
        }
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setParticipantId(request.participantId().trim());
        attempt.setParticipantName(trimToNull(request.participantName()));
        attempt.setMaxScore(quiz.maxScore());
        return attemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public Quiz getAttemptQuiz(Long attemptId) {
        QuizAttempt attempt = findAttempt(attemptId);
        return quizRepository.findWithQuestionsById(attempt.getQuiz().getId())
                .orElseThrow(() -> new NotFoundException("Quiz nao encontrado: " + attempt.getQuiz().getId()));
    }

    @Transactional
    public QuizAttempt answer(Long attemptId, AnswerRequest request) {
        QuizAttempt attempt = findAttempt(attemptId);
        ensureInProgress(attempt);

        Question question = questionRepository.findWithOptionsById(request.questionId())
                .orElseThrow(() -> new NotFoundException("Pergunta nao encontrada: " + request.questionId()));
        if (!question.getQuiz().getId().equals(attempt.getQuiz().getId())) {
            throw new BusinessException("A pergunta nao pertence ao quiz desta tentativa");
        }
        AnswerOption selectedOption = optionRepository.findById(request.selectedOptionId())
                .orElseThrow(() -> new NotFoundException("Opcao nao encontrada: " + request.selectedOptionId()));
        if (!selectedOption.getQuestion().getId().equals(question.getId())) {
            throw new BusinessException("A opcao selecionada nao pertence a pergunta informada");
        }
        boolean alreadyAnswered = attempt.getAnswers().stream()
                .anyMatch(answer -> answer.getQuestion().getId().equals(question.getId()));
        if (alreadyAnswered) {
            throw new BusinessException("Esta pergunta ja foi respondida nesta tentativa");
        }

        AttemptAnswer answer = new AttemptAnswer();
        answer.setQuestion(question);
        answer.setSelectedOption(selectedOption);
        answer.setCorrect(selectedOption.isCorrect());
        answer.setPointsAwarded(selectedOption.isCorrect() ? question.getPoints() : 0);
        attempt.addAnswer(answer);
        recalculate(attempt);
        return attempt;
    }

    @Transactional
    public QuizAttempt submit(Long attemptId) {
        QuizAttempt attempt = findAttempt(attemptId);
        ensureInProgress(attempt);
        recalculate(attempt);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        return attempt;
    }

    @Transactional(readOnly = true)
    public QuizAttempt result(Long attemptId) {
        return attemptRepository.findWithDetailsById(attemptId)
                .orElseThrow(() -> new NotFoundException("Tentativa nao encontrada: " + attemptId));
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> history(String participantId) {
        return attemptRepository.findWithQuizByParticipantIdOrderByStartedAtDesc(participantId);
    }

    private Quiz resolveQuiz(StartAttemptRequest request) {
        if (request.quizId() != null) {
            return quizRepository.findWithQuestionsById(request.quizId())
                    .orElseThrow(() -> new NotFoundException("Quiz nao encontrado: " + request.quizId()));
        }
        return quizRepository.findByExternalReference(request.externalReference().trim())
                .orElseThrow(() -> new NotFoundException("Quiz nao encontrado para externalReference: " + request.externalReference()));
    }

    private QuizAttempt findAttempt(Long attemptId) {
        return attemptRepository.findWithDetailsById(attemptId)
                .orElseThrow(() -> new NotFoundException("Tentativa nao encontrada: " + attemptId));
    }

    private void ensureInProgress(QuizAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BusinessException("Tentativa ja finalizada");
        }
        Integer limit = attempt.getQuiz().getTimeLimitMinutes();
        if (limit != null && attempt.getStartedAt().plusMinutes(limit).isBefore(LocalDateTime.now())) {
            throw new BusinessException("Tempo limite da tentativa expirou");
        }
    }

    private void recalculate(QuizAttempt attempt) {
        int score = attempt.getAnswers().stream().mapToInt(AttemptAnswer::getPointsAwarded).sum();
        int maxScore = attempt.getQuiz().maxScore();
        BigDecimal percentage = maxScore == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(score)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(maxScore), 2, RoundingMode.HALF_UP);
        attempt.setScore(score);
        attempt.setMaxScore(maxScore);
        attempt.setPercentage(percentage);
        attempt.setPassed(percentage.compareTo(attempt.getQuiz().getPassPercentage()) >= 0);
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
