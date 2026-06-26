package com.economia.quiz.repository;

import com.economia.quiz.domain.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}
