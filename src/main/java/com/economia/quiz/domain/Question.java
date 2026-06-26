package com.economia.quiz.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 1000)
    private String statement;

    @Column(length = 1000)
    private String explanation;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private int points = 1;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnswerOption> options = new LinkedHashSet<>();

    public void addOption(AnswerOption option) {
        option.setQuestion(this);
        options.add(option);
    }

    public List<AnswerOption> orderedOptions() {
        return options.stream()
                .sorted(Comparator.comparingInt(AnswerOption::getPosition))
                .toList();
    }

    public AnswerOption correctOption() {
        return options.stream()
                .filter(AnswerOption::isCorrect)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Question must have one correct option"));
    }

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AnswerOption> getOptions() {
        return orderedOptions();
    }
}
