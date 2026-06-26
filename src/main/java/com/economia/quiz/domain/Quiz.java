package com.economia.quiz.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "external_reference", length = 100, unique = true)
    private String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(name = "pass_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal passPercentage = BigDecimal.valueOf(60);

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions = new LinkedHashSet<>();

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void replaceQuestions(List<Question> newQuestions) {
        questions.clear();
        newQuestions.forEach(this::addQuestion);
    }

    public void addQuestion(Question question) {
        question.setQuiz(this);
        questions.add(question);
    }

    public int maxScore() {
        return questions.stream()
                .filter(Question::isActive)
                .mapToInt(Question::getPoints)
                .sum();
    }

    public List<Question> orderedActiveQuestions() {
        return questions.stream()
                .filter(Question::isActive)
                .sorted(Comparator.comparingInt(Question::getPosition))
                .toList();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }

    public BigDecimal getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(BigDecimal passPercentage) {
        this.passPercentage = passPercentage;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Question> getQuestions() {
        return questions.stream()
                .sorted(Comparator.comparingInt(Question::getPosition))
                .toList();
    }
}
