package io.nbe.tertara.ressource;

import io.nbe.tertara.exception.ResourceNotFoundException;
import io.nbe.tertara.model.Answer;
import io.nbe.tertara.model.QuestionAnswerPossibility;
import io.nbe.tertara.service.AnswerService;
import io.nbe.tertara.service.QuestionService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
public class AnswerRessource {

    private AnswerService answerService;

    private QuestionService questionService;

    public AnswerRessource(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }


    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getAnswersByQuestionId(@PathVariable Long questionId) {
        if (!this.questionService.existsById(questionId)) {
            throw new ResourceNotFoundException("Question was not found");
        }
        return this.answerService.getAnswersByQuestionId(questionId);
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Answer> addAnswer(@PathVariable Long questionId,
                            @Valid @RequestBody QuestionAnswerPossibility answer) {
        return this.answerService.addAnswer(questionId, answer)
                .map(answerMade -> ResponseEntity.status(HttpStatus.CREATED).body(answerMade))
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }


}
