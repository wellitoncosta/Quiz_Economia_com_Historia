package com.economia.quiz.repository;

import com.economia.quiz.domain.Question;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "options")
    Optional<Question> findWithOptionsById(Long id);
}
