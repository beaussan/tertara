package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.ressource.PingRessource;
import io.nbe.tertara.ressource.SubscriptionRessource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;

import static io.nbe.tertara.web.TestUtil.createFormattingConversionService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TertaraApplication.class)
public class PingRessourceIntTest {

    private MockMvc restsubscriptionMockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PingRessource pingRessource = new PingRessource();
        this.restsubscriptionMockMvc = MockMvcBuilders.standaloneSetup(pingRessource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    @Transactional
    public void pingShouldPong() throws Exception {
        restsubscriptionMockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"pong\""));
    }
}
