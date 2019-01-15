package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Form;
import io.nbe.tertara.repository.FormRepository;
import io.nbe.tertara.repository.QuestionRepository;
import io.nbe.tertara.ressource.FormResource;
import io.nbe.tertara.service.FormService;
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

import static io.nbe.tertara.web.TestUtil.createFormattingConversionService;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TertaraApplication.class)
public class FormResourceIntTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private FormService formService;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private MockMvc restFormMockMvc;

    private Form form;

    @Autowired
    private EntityManager em;

    public static Form createForm() {
        Form form = Form.builder().over(false).questions(new HashSet<>(Arrays.asList(QuestionResourceIntTest.createQuestion()))).build();
        return form;
    }

    @Before()
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FormResource formResource = new FormResource(formService);
        this.restFormMockMvc = MockMvcBuilders.standaloneSetup(formResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before()
    public void initTests() {
        form = createForm();
    }

    @Test
    @Transactional
    public void latestShouldReturnErrorWhenNoFormExists() throws Exception {
        formRepository.deleteAll();
        formRepository.flush();

        restFormMockMvc.perform(get("/forms/latest", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    public void latestShouldReturnTheLatest() throws Exception {
        formRepository.deleteAll();
        questionRepository.saveAll(form.getQuestions());
        formRepository.saveAndFlush(form);
        em.detach(form);

        restFormMockMvc.perform(get("/forms/latest", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(form.getId().intValue()));

        formRepository.saveAndFlush(form);

        restFormMockMvc.perform(get("/forms/latest", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(form.getId().intValue()));
    }

    @Test
    @Transactional
    public void shouldCreateFormOnRequest() throws Exception {
        restFormMockMvc.perform(post("/forms/reset", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.questions", hasSize(5)))
                .andExpect(jsonPath("$.questions[*].title", containsInAnyOrder(
                        "Croyez-vous qu’il sera possible un jour de connaître l’avenir, de modifier le passé ?",
                        "Voudriez-vous un jour voyager à travers une faille temporelle et retourner à l’époque où le LOSC était en Ligue 1 ?",
                        "Quel prix seriez-vous prêt à débourser pour discuter avec vos ancêtres ?",
                        "Quelle est la raison principale pour laquelle ce genre de technologie doit voir le jour ?",
                        "Donner le mot qui résume pour vous cette technologie."
                        )));
    }

    @Test
    @Transactional
    public void startFormWithValidId() throws Exception {
        formRepository.save(form);
        assertThat(form.isStarted()).isEqualTo(false);

        restFormMockMvc.perform(post("/forms/{formId}/start", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        Form fromDb = formRepository.findById(form.getId()).get();
        assertThat(fromDb.isStarted()).isEqualTo(true);
    }


    @Test
    @Transactional
    public void startFormWithInvalidId() throws Exception {
        formRepository.save(form);
        assertThat(form.isStarted()).isEqualTo(false);

        restFormMockMvc.perform(post("/forms/{formId}/start", Long.MAX_VALUE)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        Form fromDb = formRepository.findById(form.getId()).get();
        assertThat(fromDb.isStarted()).isEqualTo(false);
    }


    @Test
    @Transactional
    public void finishFormWithValidId() throws Exception {
        formRepository.save(form);
        assertThat(form.isOver()).isEqualTo(false);

        restFormMockMvc.perform(post("/forms/{formId}/terminate", form.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        Form fromDb = formRepository.findById(form.getId()).get();
        assertThat(fromDb.isOver()).isEqualTo(true);
    }


    @Test
    @Transactional
    public void finishFormWithInvalidId() throws Exception {
        formRepository.save(form);
        assertThat(form.isOver()).isEqualTo(false);

        restFormMockMvc.perform(post("/forms/{formId}/terminate", Long.MAX_VALUE)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        Form fromDb = formRepository.findById(form.getId()).get();
        assertThat(fromDb.isOver()).isEqualTo(false);
    }


}
