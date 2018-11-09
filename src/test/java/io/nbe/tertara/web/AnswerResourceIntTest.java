package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Answer;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.repository.AnswerRepository;
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
    private AnswerService answerService;

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
                .text(DEFAULT_TEXT)
                .question(question)
                .build();
        return answer;
    }


    @Before
    public void initTest() {
        question = QuestionRessourceIntTest.createQuestion();
        answer = createAnswer(em, question);
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

        questionService.save(question);

        restAnswerMockMvc.perform(post("/questions/{questionId}/answers", question.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer)))
                .andExpect(status().isCreated());

        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeTest + 1);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getText()).isEqualTo(DEFAULT_TEXT);
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
        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        restAnswerMockMvc.perform(get("/questions/{questionId}/answers", question.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));
    }

    @Test
    public void upateAnswerOfQuestion() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();

        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        em.detach(answer);
        answer.setText(UPDATED_TEXT);

        restAnswerMockMvc.perform(put("/questions/{questionId}/answers/{answerId}", question.getId(), answer.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer)))
                .andExpect(status().isOk());


        List<Answer> questionList = answerRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest + 1);
        Answer testAnswer = questionList.get(questionList.size() - 1);
        assertThat(testAnswer.getText()).isEqualTo(UPDATED_TEXT);
    }


    @Test
    public void upateAnswerOfQuestionOfNotExistingAnswer() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();

        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        em.detach(answer);
        answer.setText(UPDATED_TEXT);

        restAnswerMockMvc.perform(put("/questions/{questionId}/answers/{answerId}", question.getId(), Long.MAX_VALUE)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer)))
                .andExpect(status().isNotFound());


        List<Answer> questionList = answerRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest + 1);
        Answer testAnswer = questionList.get(questionList.size() - 1);
        assertThat(testAnswer.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    public void upateAnswerOfQuestionOfNotExistingQuestion() throws Exception {
        int databaseSizeBeforeTest = answerRepository.findAll().size();
        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        em.detach(answer);
        answer.setText(UPDATED_TEXT);

        restAnswerMockMvc.perform(put("/questions/{questionId}/answers/{answerId}", Long.MAX_VALUE, answer.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer)))
                .andExpect(status().isNotFound());

        List<Answer> questionList = answerRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeTest + 1);
        Answer testAnswer = questionList.get(questionList.size() - 1);
        assertThat(testAnswer.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void deleteAnswer() throws Exception {
        // Initialize the database
        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Get the question
        restAnswerMockMvc.perform(delete("/questions/{questionId}/answers/{answerId}", question.getId(), answer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Answer> answerTest = answerRepository.findAll();
        assertThat(answerTest).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteAnswerOfNotFounQuestion() throws Exception {
        // Initialize the database
        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Get the question
        restAnswerMockMvc.perform(delete("/questions/{questionId}/answers/{answerId}", Long.MAX_VALUE, answer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        // Validate the database is empty
        List<Answer> answerTest = answerRepository.findAll();
        assertThat(answerTest).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void deleteAnswerOfNotFounAnswer() throws Exception {
        // Initialize the database
        questionService.save(question);
        answerService.addAnswer(question.getId(), answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Get the question
        restAnswerMockMvc.perform(delete("/questions/{questionId}/answers/{answerId}", question.getId(), Long.MAX_VALUE)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        // Validate the database is empty
        List<Answer> answerTest = answerRepository.findAll();
        assertThat(answerTest).hasSize(databaseSizeBeforeDelete);
    }
}
