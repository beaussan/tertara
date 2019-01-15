package io.nbe.tertara.repository;

import io.nbe.tertara.model.QuestionAnswerPossibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionAnswerPossibilityRepository extends JpaRepository<QuestionAnswerPossibility, Long> {
}
