package com.economia.quiz.service;

import com.economia.quiz.api.dto.OptionRequest;
import com.economia.quiz.api.dto.QuestionRequest;
import com.economia.quiz.api.dto.QuizRequest;
import com.economia.quiz.domain.AnswerOption;
import com.economia.quiz.domain.Question;
import com.economia.quiz.domain.Quiz;
import com.economia.quiz.domain.QuizStatus;
import com.economia.quiz.repository.QuizRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public Quiz create(QuizRequest request) {
        Quiz quiz = new Quiz();
        applyRequest(quiz, request);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz update(Long id, QuizRequest request) {
        Quiz quiz = findWithQuestions(id);
        applyRequest(quiz, request);
        return quizRepository.save(quiz); // explícito para garantir flush seguro
    }

    @Transactional
    public Quiz archive(Long id) {
        Quiz quiz = findWithQuestions(id);
        quiz.setStatus(QuizStatus.ARCHIVED);
        return quizRepository.save(quiz);
    }

    @Transactional
    public void delete(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz nao encontrado: " + id));
        if (quiz.getStatus() == QuizStatus.PUBLISHED) {
            throw new BusinessException("Nao e possivel eliminar um quiz publicado. Arquive-o primeiro.");
        }
        quizRepository.delete(quiz);
    }

    @Transactional
    public Quiz publish(Long id) {
        Quiz quiz = findWithQuestions(id);
        validateQuiz(quiz);
        quiz.setStatus(QuizStatus.PUBLISHED);
        return quiz;
    }

    @Transactional(readOnly = true)
    public Quiz findWithQuestions(Long id) {
        return quizRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new NotFoundException("Quiz nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Quiz> list(QuizStatus status) {
        if (status == null) {
            return quizRepository.findAll();
        }
        return quizRepository.findByStatus(status);
    }

    private void applyRequest(Quiz quiz, QuizRequest request) {
        quiz.setTitle(request.title().trim());
        quiz.setDescription(trimToNull(request.description()));
        quiz.setExternalReference(trimToNull(request.externalReference()));
        quiz.setPassPercentage(request.passPercentage() == null ? BigDecimal.valueOf(60) : request.passPercentage());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setStatus(quiz.getStatus() == null ? QuizStatus.DRAFT : quiz.getStatus());

        List<Question> questions = request.questions().stream()
                .sorted(Comparator.comparingInt(QuestionRequest::position))
                .map(this::toQuestion)
                .toList();
        quiz.replaceQuestions(questions);
        validateQuiz(quiz);
    }

    private Question toQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setStatement(request.statement().trim());
        question.setExplanation(trimToNull(request.explanation()));
        question.setPosition(request.position());
        question.setPoints(request.points() == null || request.points() == 0 ? 1 : request.points());
        question.setActive(request.active() == null || request.active());
        request.options().stream()
                .sorted(Comparator.comparingInt(OptionRequest::position))
                .map(this::toOption)
                .forEach(question::addOption);
        return question;
    }

    private AnswerOption toOption(OptionRequest request) {
        AnswerOption option = new AnswerOption();
        option.setText(request.text().trim());
        option.setCorrect(Boolean.TRUE.equals(request.correct()));
        option.setPosition(request.position());
        return option;
    }

    private void validateQuiz(Quiz quiz) {
        if (quiz.getQuestions().isEmpty()) {
            throw new BusinessException("O quiz deve conter pelo menos uma pergunta");
        }
        Set<Integer> questionPositions = new HashSet<>();
        for (Question question : quiz.getQuestions()) {
            if (!questionPositions.add(question.getPosition())) {
                throw new BusinessException("Posicao de pergunta duplicada: " + question.getPosition());
            }
            if (question.getOptions().size() < 2) {
                throw new BusinessException("Cada pergunta deve conter pelo menos duas opcoes");
            }
            long correctCount = question.getOptions().stream().filter(AnswerOption::isCorrect).count();
            if (correctCount != 1) {
                throw new BusinessException("Cada pergunta deve ter exatamente uma opcao correta");
            }
            Set<Integer> optionPositions = new HashSet<>();
            for (AnswerOption option : question.getOptions()) {
                if (!optionPositions.add(option.getPosition())) {
                    throw new BusinessException("Posicao de opcao duplicada na pergunta " + question.getPosition());
                }
            }
        }
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
