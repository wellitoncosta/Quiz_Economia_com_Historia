package com.economia.quiz.repository;

import com.economia.quiz.domain.QuizAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByParticipantIdOrderByStartedAtDesc(String participantId);

    @EntityGraph(attributePaths = "quiz")
    List<QuizAttempt> findWithQuizByParticipantIdOrderByStartedAtDesc(String participantId);

    @EntityGraph(attributePaths = {"quiz", "answers", "answers.question", "answers.selectedOption"})
    java.util.Optional<QuizAttempt> findWithDetailsById(Long id);
}
