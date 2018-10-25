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

import javax.transaction.Transactional;

import java.util.List;

import static io.nbe.tertara.web.TestUtil.createFormattingConversionService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TertaraApplication.class)
public class QuestionRessourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAA";
    private static final String UPDATED_TITLE = "AAAAAA";

    private static final String DEFAULT_DESCRIPTION = "AAAAAA";
    private static final String UPDATED_DESCRIPTION = "AAAAAA";


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

    @Before()
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuestionRessource questionRessource = new QuestionRessource(questionService);
        this.restQuestionMockMvc = MockMvcBuilders.standaloneSetup(questionRessource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Question createEntity() {
        Question question = Question.builder()
                .description(DEFAULT_DESCRIPTION)
                .title(DEFAULT_TITLE)
                .build();
        return question;
    }

    @Before
    public void initTests() {
        question = createEntity();
    }


    @Test
    @Transactional
    public void createQuestion() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question
        restQuestionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(question)))
                .andExpect(status().isCreated());

        // Validate the Question in the database
        List<Question> ranomDataList = questionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeCreate + 1);
        Question testQuestion = ranomDataList.get(ranomDataList.size() - 1);
        assertThat(testQuestion.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testQuestion.getTitle()).isEqualTo(DEFAULT_TITLE);
    }


    @Test
    @Transactional
    public void createQuestionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question with an existing ID
        question.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(question)))
                .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionTest = questionRepository.findAll();
        assertThat(questionTest).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        // set the field null
        question.setTitle(null);

        // Create the question, which fails.
        restQuestionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(question)))
                .andExpect(status().isBadRequest());

        List<Question> ranomDataList = questionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleMinLength() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        // set the field null
        question.setTitle("AA");

        // Create the question, which fails.
        restQuestionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(question)))
                .andExpect(status().isBadRequest());

        List<Question> ranomDataList = questionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleMaxLength() throws Exception {
        int databaseSizeBeforeTest = questionRepository.findAll().size();
        // set the field null
        question.setTitle(Strings.repeat("A", 101));

        // Create the question, which fails.
        restQuestionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(question)))
                .andExpect(status().isBadRequest());

        List<Question> ranomDataList = questionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get all the questions
        restQuestionMockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get the questions
        restQuestionMockMvc.perform(get("/questions/{questionId}", question.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(question.getId().intValue()))
                .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQuestion() throws Exception {
        // Get the questions
        restQuestionMockMvc.perform(get("/questions/{questionId}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }


}
