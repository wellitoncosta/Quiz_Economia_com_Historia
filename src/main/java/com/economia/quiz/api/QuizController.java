package com.economia.quiz.api;

import com.economia.quiz.api.dto.QuizRequest;
import com.economia.quiz.api.dto.QuizResponse;
import com.economia.quiz.domain.QuizStatus;
import com.economia.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizResponse> create(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = QuizMapper.toQuizResponse(quizService.create(request), true);
        return ResponseEntity.created(URI.create("/api/quizzes/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public QuizResponse update(@PathVariable Long id, @Valid @RequestBody QuizRequest request) {
        return QuizMapper.toQuizResponse(quizService.update(id, request), true);
    }

    @PatchMapping("/{id}/publish")
    public QuizResponse publish(@PathVariable Long id) {
        return QuizMapper.toQuizResponse(quizService.publish(id), true);
    }

    @GetMapping("/{id}")
    public QuizResponse findById(@PathVariable Long id) {
        return QuizMapper.toQuizResponse(quizService.findWithQuestions(id), true);
    }

    @GetMapping
    public List<QuizResponse> list(@RequestParam(required = false) QuizStatus status) {
        return quizService.list(status).stream()
                .map(QuizMapper::toQuizSummary)
                .toList();
    }

    @PatchMapping("/{id}/archive")
    public QuizResponse archive(@PathVariable Long id) {
        return QuizMapper.toQuizResponse(quizService.archive(id), true);
    }

    @DeleteMapping("/{id}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        quizService.delete(id);
    }
}
