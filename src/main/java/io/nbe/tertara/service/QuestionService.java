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
    public List<Question> findAll() {
        return this.questionRepository.findAll();
    }

    public Question save(Question question) {
        return this.questionRepository.save(question);
    }

    public Optional<Question> updateQuestion(Long questionId, Question questionRequest) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setTitle(questionRequest.getTitle());
                    question.setDescription(questionRequest.getDescription());
                    return questionRepository.save(question);
                });
    }

    public Optional<?> deleteQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return question;
                });
    }

    public boolean existsById(Long questionId) {
        return this.questionRepository.existsById(questionId);
    }
}
