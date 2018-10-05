package io.nbe.tertara.ressource;

import io.nbe.tertara.service.AnswerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class AndwerRessource {

    private AnswerService answerService;

    public AndwerRessource(AnswerService answerService) {
        this.answerService = answerService;
    }

    @RequestMapping("/")
    public String simple() {
        return this.answerService.getAll();
    }

}
