package com.economia.quiz.api;

import com.economia.quiz.api.dto.AnswerRequest;
import com.economia.quiz.api.dto.AttemptResponse;
import com.economia.quiz.api.dto.QuizResponse;
import com.economia.quiz.api.dto.StartAttemptRequest;
import com.economia.quiz.service.AttemptService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping
    public ResponseEntity<AttemptResponse> start(@Valid @RequestBody StartAttemptRequest request) {
        AttemptResponse response = QuizMapper.toAttemptResponse(attemptService.start(request), false);
        return ResponseEntity.created(URI.create("/api/attempts/" + response.id())).body(response);
    }

    @GetMapping("/{attemptId}/questions")
    public QuizResponse questions(@PathVariable Long attemptId) {
        return QuizMapper.toQuizResponse(attemptService.getAttemptQuiz(attemptId), false);
    }

    @PostMapping("/{attemptId}/answers")
    public AttemptResponse answer(@PathVariable Long attemptId, @Valid @RequestBody AnswerRequest request) {
        return QuizMapper.toAttemptResponse(attemptService.answer(attemptId, request), false);
    }

    @PostMapping("/{attemptId}/submit")
    public AttemptResponse submit(@PathVariable Long attemptId) {
        return QuizMapper.toAttemptResponse(attemptService.submit(attemptId), true);
    }

    @GetMapping("/{attemptId}/result")
    public AttemptResponse result(@PathVariable Long attemptId) {
        return QuizMapper.toAttemptResponse(attemptService.result(attemptId), true);
    }

    @GetMapping
    public List<AttemptResponse> history(@RequestParam @NotBlank String participantId) {
        return attemptService.history(participantId).stream()
                .map(attempt -> QuizMapper.toAttemptResponse(attempt, false))
                .toList();
    }
}
