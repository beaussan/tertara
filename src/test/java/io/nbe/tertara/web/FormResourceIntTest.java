package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Form;
import io.nbe.tertara.repository.FormRepository;
import io.nbe.tertara.ressource.FormResource;
import io.nbe.tertara.service.FormService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.HashSet;

import static io.nbe.tertara.web.TestUtil.createFormattingConversionService;

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

    private MockMvc restFormMockMvc;

    private Form form;

    @Autowired
    private EntityManager em;

    @Before()
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FormResource formResource = new FormResource(formService);
        this.restFormMockMvc = MockMvcBuilders.standaloneSetup(formResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Form createForm() {
        Form form = Form.builder().over(false).questions(new HashSet<>(Arrays.asList(QuestionResourceIntTest.createQuestion()))).build();
        return form;
    }
}
