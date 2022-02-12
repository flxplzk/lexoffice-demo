package de.nordakademie.iaa.examsurvey.integration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.SurveyStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SurveyIntegrationTest {
    private static final Type TYPE_LIST_OF_SURVEYS =
            new TypeToken<List<Survey>>() {
            }.getType();
    private static final Type TYPE_LIST_OF_OPTIONS =
            new TypeToken<List<Option>>() {
            }.getType();
    private static final Type TYPE_LIST_OF_PARTICIPATIONS =
            new TypeToken<List<Participation>>() {
            }.getType();
    private static final String SURVEYS_API_PATH = "/api/surveys";
    private static final String SURVEYS_OPTIONS_API_PATH = "/api/surveys/%s/options";
    private static final String SURVEYS_PARTICIPATIONS_API_PATH = "/api/surveys/%s/participations";
    private static final String USER_FELIX = "felix@plazek.de";
    private static final String USER_STEFAN = "stefan@reichert.de";
    private static final SurveyStatus[] ALLOWED_FOR_INITIATOR = SurveyStatus.values();
    private static final SurveyStatus[] ALLOWED_FOR_NON_INITIATOR = {SurveyStatus.CLOSED, SurveyStatus.OPEN};
    private static final long SURVEY_FELIX_OPEN = -7;
    private static final long SURVEY_FELIX_CLOSED = -8;
    private static final long SURVEY_FELIX_PRIVATE = -14;
    private static final Gson GSON = new Gson();
    private static final long SURVEY_STEFAN = -10;
    private static final long SURVEY_SASCHA = -5;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void loadAllSurveys_withoutAuthorisation() throws Exception {
        mockMvc.perform(get(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllSurveys_withAuthorisation() throws Exception {
        mockMvc.perform(get(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllSurveys_withAuthorisationWithanotherUser() throws Exception {
        mockMvc.perform(get(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllSurveys_withAuthorisationWithValidResponseData() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        List<Survey> surveys = convertResponseToListOfSurveys(mvcResult);
        requireAllowedSurveys(surveys, USER_STEFAN);
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllSurveys_withAuthorisationWithValidResponseDataAnotherUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        List<Survey> surveys = convertResponseToListOfSurveys(mvcResult);
        requireAllowedSurveys(surveys, USER_FELIX);
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllOptions_forPrivateSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_PRIVATE))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(0));
    }


    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllOptions_forPrivateSurveyWithNonInitiator() throws Exception {
        mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_PRIVATE))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllOptions_forOpenSurveyWithNonInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(5));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllOptions_forOpenSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(5));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllOptions_forClosedSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_CLOSED))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(5));
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllOptions_forClosedSurveyWithNonInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(5));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllParticipations_forPrivateSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_PRIVATE))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> surveys = convertResponseToListOfParticipations(mvcResult);
        assertThat(surveys.size(), is(0));
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllParticipations_forPrivateSurveyWithNonInitiator() throws Exception {
        mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_PRIVATE))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllParticipations_forOpenSurveyWithNonInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> surveys = convertResponseToListOfParticipations(mvcResult);
        assertThat(surveys.size(), is(2));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllParticipations_forOpenSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> surveys = convertResponseToListOfParticipations(mvcResult);
        assertThat(surveys.size(), is(2));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void loadAllParticipations_forClosedSurveyWithInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> surveys = convertResponseToListOfParticipations(mvcResult);
        assertThat(surveys.size(), is(2));
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void loadAllParticipations_forClosedSurveyWithNonInitiator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_CLOSED))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> surveys = convertResponseToListOfParticipations(mvcResult);
        assertThat(surveys.size(), is(2));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void createSurvey_UserFelix() throws Exception {
        final String requestJson = "{\"description\": \"mit beschreibung\", " +
                "\"options\": [{\"dateTime\": \"2018-12-12T09:52:57.497Z\"}, {\"dateTime\": \"2018-12-05T09:53:13.358Z\"}], " +
                "\"surveyStatus\": \"OPEN\", " +
                "\"title\": \"fuer Tests\"}";

        MvcResult mvcResult = mockMvc.perform(post(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        final Survey createdSurvey = GSON.fromJson(mvcResult.getResponse().getContentAsString(), Survey.class);
        assertThat(createdSurvey.getTitle(), is("fuer Tests"));
        assertThat(createdSurvey.getDescription(), is("mit beschreibung"));
        assertThat(createdSurvey.getSurveyStatus(), is(SurveyStatus.OPEN));
        assertThat(createdSurvey.getInitiator().getUsername(), is(USER_FELIX));
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void createSurvey_UserStefan() throws Exception {
        final String requestJson = "{\"description\": \"mit beschreibung\", " +
                "\"options\": [{\"dateTime\": \"2018-12-12T09:52:57.497Z\"}, {\"dateTime\": \"2018-12-05T09:53:13.358Z\"}], " +
                "\"surveyStatus\": \"OPEN\", " +
                "\"title\": \"Neue umfrage fuer Tests\"}";

        MvcResult mvcResult = mockMvc.perform(post(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        final Survey createdSurvey = GSON.fromJson(mvcResult.getResponse().getContentAsString(), Survey.class);
        assertThat(createdSurvey.getTitle(), is("Neue umfrage fuer Tests"));
        assertThat(createdSurvey.getDescription(), is("mit beschreibung"));
        assertThat(createdSurvey.getSurveyStatus(), is(SurveyStatus.OPEN));
        assertThat(createdSurvey.getInitiator().getUsername(), is(USER_STEFAN));
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void createSurvey_twice() throws Exception {
        final String requestJson = "{\"description\": \"mit beschreibung\", " +
                "\"options\": [{\"dateTime\": \"2018-12-12T09:52:57.497Z\"}, {\"dateTime\": \"2018-12-05T09:53:13.358Z\"}], " +
                "\"surveyStatus\": \"OPEN\", " +
                "\"title\": \"Titel, wollen ja keine Konflikte\"}";

        MvcResult mvcResult = mockMvc.perform(post(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        final Survey createdSurvey = GSON.fromJson(mvcResult.getResponse().getContentAsString(), Survey.class);
        assertThat(createdSurvey.getTitle(), is("Titel, wollen ja keine Konflikte"));
        assertThat(createdSurvey.getDescription(), is("mit beschreibung"));
        assertThat(createdSurvey.getSurveyStatus(), is(SurveyStatus.OPEN));
        assertThat(createdSurvey.getInitiator().getUsername(), is(USER_FELIX));

        mockMvc.perform(post(SURVEYS_API_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void updateSurvey_withInitiator() throws Exception {
        // GIVEN
        // get Options before update
        MvcResult mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Option> options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(5));

        // get Participations before update
        mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        List<Participation> participations = convertResponseToListOfParticipations(mvcResult);
        assertThat(participations.size(), is(2));

        // WHEN
        final String requestJSON = "{\"title\":\"Test Survey Felix OPEN\"," +
                "\"description\":\"Mit neuer Beschreibung\"," +
                "\"options\": [{\"dateTime\": \"2019-03-23T10:34:43.196+0000\",\"_id\": -35}, " +
                "{\"dateTime\": \"2019-03-22T10:34:43.194+0000\",\"_id\": -34}, " +
                "{\"dateTime\": \"2019-03-20T10:34:43.193+0000\",\"_id\": -32}, " +
                "{\"dateTime\": \"2019-03-19T10:34:43.193+0000\",\"_id\": -31}]," +
                "\"surveyStatus\":\"OPEN\"}";
        mvcResult = mockMvc.perform(put(String.format(SURVEYS_API_PATH + "/%s", SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJSON))
                .andExpect(status().isOk())
                .andReturn();
        final Survey updatedSurvey = GSON.fromJson(mvcResult.getResponse().getContentAsString(), Survey.class);
        assertThat(updatedSurvey.getTitle(), is("Test Survey Felix OPEN"));
        assertThat(updatedSurvey.getDescription(), is("Mit neuer Beschreibung"));
        assertThat(updatedSurvey.getSurveyStatus(), is(SurveyStatus.OPEN));
        assertThat(updatedSurvey.getInitiator().getUsername(), is(USER_FELIX));

        // THEN
        // get Options after update
        mvcResult = mockMvc.perform(get(String.format(SURVEYS_OPTIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        options = convertResponseToListOfOptions(mvcResult);
        assertThat(options.size(), is(4));

        // get Participations after update
        mvcResult = mockMvc.perform(get(String.format(SURVEYS_PARTICIPATIONS_API_PATH, SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        participations = convertResponseToListOfParticipations(mvcResult);
        assertThat(participations.size(), is(0));

    }

    @Test
    @WithMockUser(USER_FELIX)
    public void updateSurvey_withInitiatorAndNoOptions() throws Exception {
        final String requestJSON = "{\"title\":\"Test Survey Felix OPEN\"," +
                "\"description\":\"Mit neuer Beschreibung\"," +
                "\"surveyStatus\":\"OPEN\"}";
        mockMvc.perform(put(String.format(SURVEYS_API_PATH + "/%s", SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void updateSurvey_withNonInitiator() throws Exception {
        // WHEN
        final String requestJSON = "{\"title\":\"Test Survey Felix OPEN\"," +
                "\"description\":\"Mit neuer Beschreibung\"," +
                "\"options\": [{\"dateTime\": \"2019-03-23T10:34:43.196+0000\",\"_id\": -35}, " +
                "{\"dateTime\": \"2019-03-22T10:34:43.194+0000\",\"_id\": -34}, " +
                "{\"dateTime\": \"2019-03-20T10:34:43.193+0000\",\"_id\": -32}, " +
                "{\"dateTime\": \"2019-03-19T10:34:43.193+0000\",\"_id\": -31}]," +
                "\"surveyStatus\":\"OPEN\"}";
        mockMvc.perform(put(String.format(SURVEYS_API_PATH + "/%s", SURVEY_FELIX_OPEN))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void updateSurvey__notFound() throws Exception {
        // WHEN
        final String requestJSON = "{\"title\":\"Test Survey Felix OPEN\"," +
                "\"description\":\"Mit neuer Beschreibung\"," +
                "\"options\": [{\"dateTime\": \"2019-03-23T10:34:43.196+0000\",\"_id\": -35}, " +
                "{\"dateTime\": \"2019-03-22T10:34:43.194+0000\",\"_id\": -34}, " +
                "{\"dateTime\": \"2019-03-20T10:34:43.193+0000\",\"_id\": -32}, " +
                "{\"dateTime\": \"2019-03-19T10:34:43.193+0000\",\"_id\": -31}]," +
                "\"surveyStatus\":\"OPEN\"}";
        mockMvc.perform(put(String.format(SURVEYS_API_PATH + "/%s", "-10000000000000"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(USER_STEFAN)
    public void deleteSurvey_withNonInitiator() throws Exception {
        mockMvc.perform(delete(String.format(SURVEYS_API_PATH + "/%s", SURVEY_STEFAN)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void deleteSurvey_withInitiator() throws Exception {
        mockMvc.perform(delete(String.format(SURVEYS_API_PATH + "/%s", SURVEY_SASCHA)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(USER_FELIX)
    public void deleteSurvey_notFound() throws Exception {
        mockMvc.perform(delete(String.format(SURVEYS_API_PATH + "/%s", "-1000000000")))
                .andExpect(status().isNotFound());
    }

    private void requireAllowedSurveys(List<Survey> surveys, String user) {
        for (Survey survey : surveys) {
            if (user.equals(survey.getInitiator().getUsername())) {
                assertThat(survey.getSurveyStatus(), isIn(ALLOWED_FOR_INITIATOR));
            } else {
                assertThat(survey.getSurveyStatus(), isIn(ALLOWED_FOR_NON_INITIATOR));
            }
        }
    }

    private List<Participation> convertResponseToListOfParticipations(MvcResult mvcResult) throws UnsupportedEncodingException {
        String jsonString = mvcResult.getResponse().getContentAsString();
        return GSON.fromJson(jsonString, TYPE_LIST_OF_PARTICIPATIONS);
    }

    private List<Option> convertResponseToListOfOptions(MvcResult mvcResult) throws UnsupportedEncodingException {
        String jsonString = mvcResult.getResponse().getContentAsString();
        return GSON.fromJson(jsonString, TYPE_LIST_OF_OPTIONS);
    }

    private List<Survey> convertResponseToListOfSurveys(MvcResult mvcResult) throws UnsupportedEncodingException {
        String jsonString = mvcResult.getResponse().getContentAsString();
        return GSON.fromJson(jsonString, TYPE_LIST_OF_SURVEYS);
    }
}
