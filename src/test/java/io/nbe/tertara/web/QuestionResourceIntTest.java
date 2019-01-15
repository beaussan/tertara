package io.nbe.tertara.web;

import com.google.common.base.Strings;
import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Question;
import io.nbe.tertara.repository.QuestionRepository;
import io.nbe.tertara.ressource.QuestionRessource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TertaraApplication.class)
public class QuestionResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAA";

    private static final String DEFAULT_DESCRIPTION = "AAAAAA";


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    private MockMvc restQuestionMockMvc;

    private Question question;

    @Autowired
    private EntityManager em;

    @Before()
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuestionRessource questionRessource = new QuestionRessource(questionService);
        this.restQuestionMockMvc = MockMvcBuilders.standaloneSetup(questionRessource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Question createQuestion() {
        Question question = Question.builder()
                .description(DEFAULT_DESCRIPTION)
                .title(DEFAULT_TITLE)
                .build();
        return question;
    }

    @Before
    public void initTests() {
        question = createQuestion();
    }

    @Test
    @Transactional
    public void updateQuestion() throws Exception {
        // Initialize the database
        questionRepository.save(question);

        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Update the question
        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        // Disconnect from session so that the updates on updatedQuestion are not directly saved in db
        assertThat(updatedQuestion.isTerminated()).isEqualTo(false);

        restQuestionMockMvc.perform(post("/questions/{questionId}/terminate", question.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionRepository.findById(updatedQuestion.getId()).get();
        assertThat(testQuestion.isTerminated()).isEqualTo(true);
    }


}
