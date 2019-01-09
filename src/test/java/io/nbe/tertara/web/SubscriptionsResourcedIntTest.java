package io.nbe.tertara.web;

import io.nbe.tertara.TertaraApplication;
import io.nbe.tertara.model.Subscription;
import io.nbe.tertara.repository.SubscriptionRepository;
import io.nbe.tertara.ressource.SubscriptionRessource;
import io.nbe.tertara.service.SubscriptionService;
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
public class SubscriptionsResourcedIntTest {

    private static final String DEFAULT_EMAIL = "toto@nbe.io";
    private static final String UPDATED_EMAIL = "tata@nbe.io";

    private static final Boolean DEFAULT_AGGREED = false;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restsubscriptionMockMvc;

    private Subscription subscription;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SubscriptionRessource subscriptionRessource = new SubscriptionRessource(this.subscriptionService);
        this.restsubscriptionMockMvc = MockMvcBuilders.standaloneSetup(subscriptionRessource)
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
    public static Subscription createEntity(EntityManager em) {
        Subscription subscription = Subscription.builder()
                .email(DEFAULT_EMAIL)
                .newsletter(DEFAULT_AGGREED)
                .build();
        return subscription;
    }


    @Before
    public void initTest() {
        subscription = createEntity(em);
    }


    @Test
    @Transactional
    public void createSubscription() throws Exception {
        int databaseSizeBeforeCreate = subscriptionRepository.findAll().size();

        // Create the subscription
        restsubscriptionMockMvc.perform(post("/subscription")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscription)))
                .andExpect(status().isCreated());

        // Validate the subscription in the database
        List<Subscription> ranomDataList = subscriptionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeCreate + 1);
        Subscription subscription = ranomDataList.get(ranomDataList.size() - 1);
        assertThat(subscription.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(subscription.isNewsletter()).isEqualTo(DEFAULT_AGGREED);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionRepository.findAll().size();
        // set the field null
        subscription.setEmail(null);

        // Create the subscription, which fails.
        restsubscriptionMockMvc.perform(post("/subscription")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscription)))
                .andExpect(status().isBadRequest());

        List<Subscription> ranomDataList = subscriptionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsEmail() throws Exception {
        int databaseSizeBeforeTest = subscriptionRepository.findAll().size();
        // set the field null
        subscription.setEmail("tata");

        // Create the subscription, which fails.
        restsubscriptionMockMvc.perform(post("/subscription")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscription)))
                .andExpect(status().isBadRequest());

        List<Subscription> ranomDataList = subscriptionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void createQuestionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = subscriptionRepository.findAll().size();

        // Create the Question with an existing ID
        subscription.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restsubscriptionMockMvc.perform(post("/questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscription)))
                .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Subscription> subsriptionTest = subscriptionRepository.findAll();
        assertThat(subsriptionTest).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDuplicateEmail() throws Exception {
        subscription.setEmail(UPDATED_EMAIL);
        subscriptionRepository.saveAndFlush(subscription);
        em.detach(subscription);
        System.out.println(subscription);

        int databaseSizeBeforeTest = subscriptionRepository.findAll().size();

        subscription = Subscription.builder()
                .email(UPDATED_EMAIL)
                .newsletter(DEFAULT_AGGREED)
                .build();
        System.out.println(subscription);


        // Create the subscription, which fails.
        restsubscriptionMockMvc.perform(post("/subscription")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscription)))
                .andExpect(status().isConflict());

        List<Subscription> ranomDataList = subscriptionRepository.findAll();
        assertThat(ranomDataList).hasSize(databaseSizeBeforeTest);
    }


    @Test
    @Transactional
    public void getAllsubscription() throws Exception {
        // Initialize the database
        subscriptionRepository.saveAndFlush(subscription);

        // Get all the ranomDataList
        restsubscriptionMockMvc.perform(get("/subscription"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(subscription.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].newsletter").value(hasItem(DEFAULT_AGGREED.booleanValue())));
    }


}
