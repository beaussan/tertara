package io.nbe.tertara.ressource;

import io.nbe.tertara.exception.ResourceNotFoundException;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionRessource {

    private QuestionService questionService;

    public QuestionRessource(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/questions/{questionId}/terminate")
    public Question updateQuestion(@PathVariable Long questionId) {
        return this.questionService.setQuestionTerminated(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }
}
