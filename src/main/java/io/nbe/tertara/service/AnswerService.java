package io.nbe.tertara.service;

import io.nbe.tertara.exception.BadRequestBody;
import io.nbe.tertara.model.Answer;
import io.nbe.tertara.model.QuestionAnswerPossibility;
import io.nbe.tertara.repository.AnswerRepository;
import io.nbe.tertara.repository.QuestionAnswerPossibilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private AnswerRepository answerRepository;

    private QuestionService questionService;

    private QuestionAnswerPossibilityRepository questionAnswerPossibilityRepository;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService, QuestionAnswerPossibilityRepository questionAnswerPossibilityRepository) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.questionAnswerPossibilityRepository = questionAnswerPossibilityRepository;
    }


    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return this.answerRepository.findByQuestionId(questionId);
    }

    public Optional<Answer> addAnswer(Long questionId, QuestionAnswerPossibility answer) {
        return this.questionService.findOneById(questionId)
                .flatMap(question -> {
                    if (question.isTerminated()) {
                        throw new BadRequestBody("You cannot add a answer to a terminated question");
                    }
                    if (!question.getAnswerPossibilities().contains(answer)) {
                        return Optional.empty();
                    }

                    return Optional.of(answerRepository.save(Answer.builder().answerValue(answer).question(question).build()));
                });
    }

}
