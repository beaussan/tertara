package io.nbe.tertara.service;

import io.nbe.tertara.model.Form;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.model.QuestionAnswerPossibility;
import io.nbe.tertara.repository.FormRepository;
import io.nbe.tertara.repository.QuestionAnswerPossibilityRepository;
import io.nbe.tertara.repository.QuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class FormService {
    private QuestionRepository questionRepository;
    private FormRepository formRepository;
    private QuestionAnswerPossibilityRepository questionAnswerPossibilityRepository;

    public FormService(QuestionRepository questionRepository, FormRepository formRepository, QuestionAnswerPossibilityRepository questionAnswerPossibilityRepository) {
        this.questionRepository = questionRepository;
        this.formRepository = formRepository;
        this.questionAnswerPossibilityRepository = questionAnswerPossibilityRepository;
    }

    public Form resetView() {
        Question q1 = generateFirstQuestion();
        Question q2 = generateSecondQuestion();
        Question q3 = generateThridQuestion();
        Question q4 = generateFourthQuestion();
        Question q5 = generateLasthQuestion();

        return formRepository.save(
                Form.builder()
                .over(false)
                .questions(new HashSet<>(Arrays.asList(q1, q2, q3, q4, q5)))
                .started(false)
                .build()
        );
    }

    private Question generateFirstQuestion() {
        return this.genderedYesNoQuestion("Croyez-vous qu’il sera possible un jour de connaître l’avenir, de modifier le passé ?",0);

    }

    private Question generateSecondQuestion() {
        return this.genderedYesNoQuestion("Voudriez-vous un jour voyager à travers une faille temporelle et retourner à l’époque où le LOSC était en Ligue 1 ?",1);
    }

    private Question generateThridQuestion() {
        QuestionAnswerPossibility one = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("10[bitcoin]").build());
        QuestionAnswerPossibility two = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("100[bitcoin]").build());
        QuestionAnswerPossibility three = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("1000[bitcoin]").build());

        return this.questionRepository.save(Question.builder()
                .title("Quel prix seriez-vous prêt à débourser pour discuter avec vos ancêtres ?")
                .answerPossibilities(new HashSet<>(Arrays.asList(one, two, three)))
                .position(2)
                .ignoreResponse(false)
                .terminated(false)
                .build());
    }

    private Question generateFourthQuestion() {
        QuestionAnswerPossibility one = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("Innovation").build());
        QuestionAnswerPossibility two = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("Gagner la guerre").build());
        QuestionAnswerPossibility three = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("Gagner au loto").build());
        QuestionAnswerPossibility four = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("Connaitre son avenir").build());
        QuestionAnswerPossibility five = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("Corriger le passé").build());

        return this.questionRepository.save(Question.builder()
                .title("Quelle est la raison principale pour laquelle ce genre de technologie doit voir le jour ?")
                .answerPossibilities(new HashSet<>(Arrays.asList(one, two, three, four, five)))
                .position(3)
                .ignoreResponse(false)
                .terminated(false)
                .build());
    }

    private Question generateLasthQuestion() {
        QuestionAnswerPossibility one = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("BRAVO").build());
        return this.questionRepository.save(Question.builder()
                .title("Donner le mot qui résume pour vous cette technologie.")
                .answerPossibilities(new HashSet<>(Arrays.asList(one)))
                .position(4)
                .ignoreResponse(true)
                .terminated(false)
                .build());
    }



    private Question genderedYesNoQuestion(String question, int position) {
        QuestionAnswerPossibility yes = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("OUI").build());
        QuestionAnswerPossibility no = this.questionAnswerPossibilityRepository.save(QuestionAnswerPossibility.builder().value("NON").build());
        return this.questionRepository.save(Question.builder()
                .title(question)
                .answerPossibilities(new HashSet<>(Arrays.asList(yes, no)))
                .position(position)
                .ignoreResponse(false)
                .terminated(false)
                .build());
    }

    public Optional<Form> getLatestForm() {
        List<Form> list = this.formRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
        if (list.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    public Optional<Form> startForm(Long id) {
        return formRepository.findById(id)
                .map(form -> {
                    form.setStarted(true);
                    return formRepository.save(form);
                });
    }

    public Optional<Form> setFormFinished(Long id) {
        return formRepository.findById(id)
                .map(form -> {
                    form.setOver(true);
                    return formRepository.save(form);
                });
    }


}
