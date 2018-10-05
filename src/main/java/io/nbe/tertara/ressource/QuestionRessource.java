package io.nbe.tertara.ressource;

import io.nbe.tertara.exception.ResourceNotFoundException;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class QuestionRessource {

    private QuestionService questionService;

    public QuestionRessource(QuestionService questionService) {
        this.questionService = questionService;
    }


    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return this.questionService.findAll();
    }


    @PostMapping("/questions")
    public Question createQuestion(@Valid @RequestBody Question question) {
        return this.questionService.save(question);
    }

    @PutMapping("/questions/{questionId}")
    public Question updateQuestion(@PathVariable Long questionId,
                                   @Valid @RequestBody Question questionRequest) {
        return this.questionService.updateQuestion(questionId, questionRequest)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }


    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        return this.questionService.deleteQuestion(questionId)
                .map(question -> ResponseEntity.ok().build()).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }
}
