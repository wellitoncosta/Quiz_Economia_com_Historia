package com.economia.quiz.repository;

import com.economia.quiz.domain.Quiz;
import com.economia.quiz.domain.QuizStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByStatus(QuizStatus status);

    Optional<Quiz> findByExternalReference(String externalReference);

    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Quiz> findWithQuestionsById(Long id);
}
