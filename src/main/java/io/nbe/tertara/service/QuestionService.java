package io.nbe.tertara.service;

import io.nbe.tertara.exception.ResourceNotFoundException;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Optional<Question> findOneById(Long id) {
        return this.questionRepository.findById(id);
    }

    public Optional<Question> setQuestionTerminated(Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setTerminated(true);
                    return questionRepository.save(question);
                });
    }

    public boolean existsById(Long questionId) {
        return this.questionRepository.existsById(questionId);
    }
}
