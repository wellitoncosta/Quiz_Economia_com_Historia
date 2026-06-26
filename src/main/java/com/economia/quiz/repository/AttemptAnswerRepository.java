package com.economia.quiz.repository;

import com.economia.quiz.domain.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {
}
