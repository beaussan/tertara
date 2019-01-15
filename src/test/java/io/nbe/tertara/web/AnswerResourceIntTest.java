package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Answer;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.model.QuestionAnswerPossibility;
import io.nbe.tertara.repository.AnswerRepository;
import io.nbe.tertara.repository.QuestionAnswerPossibilityRepository;
import io.nbe.tertara.repository.QuestionRepository;
import io.nbe.tertara.ressource.AnswerRessource;
import io.nbe.tertara.service.AnswerService;
import io.nbe.tertara.service.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static io.nbe.tertara.web.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TertaraApplication.class)
public class AnswerResourceIntTest {

    private static final String DEFAULT_TEXT = "AAAAAA";
    private static final String UPDATED_TEXT = "BBBBBB";

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionAnswerPossibilityRepository questionAnswerPossibilityRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restAnswerMockMvc;

    private Answer answer;

    private QuestionAnswerPossibility answerPossibility;

    private Question question;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnswerRessource answerRessource = new AnswerRessource(answerService, questionService);
        this.restAnswerMockMvc = MockMvcBuilders.standaloneSetup(answerRessource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }


    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createAnswer(EntityManager em, Question question) {
        Answer answer = Answer.builder()
                .question(question)
                .answerValue(createAndwerPossibility())
                .build();
        return answer;
    }

    public static QuestionAnswerPossibility createAndwerPossibility() {
        return QuestionAnswerPossibility.builder().value(DEFAULT_TEXT).build();
    }


    @Before
    public void initTest() {
        question = QuestionResourceIntTest.createQuestion();
        answer = createAnswer(em, question);
        answerPossibility = answer.getAnswerValue();
    }

    @Test
    @Transactional
    public void createAnswerOnNonExistingQuestion() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();

        restAnswerMockMvc.perform(post("/questions/{questionId}/answers", Long.MAX_VALUE)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer)))
                .andExpect(status().isNotFound());

        List<Answer> questionList = answerRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void createAnswer() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();

        questionAnswerPossibilityRepository.save(answerPossibility);
        question.setAnswerPossibilities(new HashSet<>(Arrays.asList(answerPossibility)));
        questionRepository.save(question);

        restAnswerMockMvc.perform(post("/questions/{questionId}/answers", question.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answerPossibility)))
                .andExpect(status().isCreated());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest + 1);
    }

    @Test
    @Transactional
    public void getAllAnswerOfNotExistingQuestion() throws Exception {
        restAnswerMockMvc.perform(get("/questions/{questionId}/answers", Long.MAX_VALUE)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getAllAnswerOfQuestion() throws Exception {
        questionAnswerPossibilityRepository.save(answerPossibility);
        question.setAnswerPossibilities(new HashSet<>(Arrays.asList(answerPossibility)));
        questionRepository.save(question);
        answerService.addAnswer(question.getId(), answerPossibility);

        restAnswerMockMvc.perform(get("/questions/{questionId}/answers", question.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].answerValue.value").value(hasItem(DEFAULT_TEXT)));
    }

}
