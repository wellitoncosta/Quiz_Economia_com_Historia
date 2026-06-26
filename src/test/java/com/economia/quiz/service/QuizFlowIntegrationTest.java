package com.economia.quiz.service;

import com.economia.quiz.api.dto.AnswerRequest;
import com.economia.quiz.api.dto.OptionRequest;
import com.economia.quiz.api.dto.QuestionRequest;
import com.economia.quiz.api.dto.QuizRequest;
import com.economia.quiz.api.dto.StartAttemptRequest;
import com.economia.quiz.domain.Quiz;
import com.economia.quiz.domain.QuizAttempt;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class QuizFlowIntegrationTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private AttemptService attemptService;

    @Test
    void createsPublishesAnswersAndSubmitsQuiz() {
        Quiz quiz = quizService.create(sampleQuiz());
        quizService.publish(quiz.getId());

        QuizAttempt attempt = attemptService.start(new StartAttemptRequest(
                quiz.getId(),
                null,
                "aluno-001",
                "Ana"
        ));

        Long questionId = quiz.getQuestions().getFirst().getId();
        Long correctOptionId = quiz.getQuestions().getFirst().correctOption().getId();
        attemptService.answer(attempt.getId(), new AnswerRequest(questionId, correctOptionId));
        QuizAttempt result = attemptService.submit(attempt.getId());

        assertThat(result.getScore()).isEqualTo(10);
        assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    void rejectsQuestionWithoutExactlyOneCorrectOption() {
        QuizRequest invalid = new QuizRequest(
                "Quiz invalido",
                null,
                "QUIZ_INVALIDO",
                BigDecimal.valueOf(60),
                null,
                List.of(new QuestionRequest(
                        "Pergunta?",
                        null,
                        1,
                        1,
                        true,
                        List.of(
                                new OptionRequest("A", false, 1),
                                new OptionRequest("B", false, 2)
                        )
                ))
        );

        assertThatThrownBy(() -> quizService.create(invalid))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exatamente uma opcao correta");
    }

    private static QuizRequest sampleQuiz() {
        return new QuizRequest(
                "Economia com historia",
                "Quiz de validacao de aprendizagem",
                "ECONOMIA_HISTORIA",
                BigDecimal.valueOf(70),
                30,
                List.of(new QuestionRequest(
                        "O que e escassez em economia?",
                        "Escassez ocorre quando recursos sao limitados diante de necessidades ilimitadas.",
                        1,
                        10,
                        true,
                        List.of(
                                new OptionRequest("Recursos limitados para necessidades ilimitadas", true, 1),
                                new OptionRequest("Excesso permanente de recursos", false, 2),
                                new OptionRequest("Ausencia total de escolhas", false, 3)
                        )
                ))
        );
    }
}
