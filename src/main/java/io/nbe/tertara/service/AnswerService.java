package io.nbe.tertara.service;

import io.nbe.tertara.model.Answer;
import io.nbe.tertara.repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private AnswerRepository answerRepository;

    private QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }


    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return this.answerRepository.findByQuestionId(questionId);
    }

    public Optional<Answer> addAnswer(Long questionId, Answer answer) {
        return this.questionService.findOneById(questionId)
                .map(question -> {
                    answer.setQuestion(question);
                    return answerRepository.save(answer);
                });
    }

    public Optional<Answer> updateAnswer(Long questionId,Long answerId, Answer answerRequest) {
        return answerRepository.findById(answerId)
                .map(answer -> {
                    answer.setText(answerRequest.getText());
                    return answerRepository.save(answer);
                });
    }

    public Optional<Answer> deleteAnswer(Long questionId, Long answerId) {
        return answerRepository.findById(answerId)
                .map(answer -> {
                    answerRepository.delete(answer);
                    return answer;
                });
    }

}
