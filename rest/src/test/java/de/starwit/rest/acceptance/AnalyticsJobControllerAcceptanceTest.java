package de.starwit.rest.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import de.starwit.persistence.entity.AnalyticsJobEntity;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsJobControllerAcceptanceTest extends AbstractControllerAcceptanceTest<AnalyticsJobEntity> {

    final static Logger LOG = LoggerFactory.getLogger(AnalyticsJobControllerAcceptanceTest.class);
    private static final String data = "testdata/analytics-job/";
    private static final String restpath = "/api/analytics-job/";

    private JacksonTester<AnalyticsJobEntity> jsonTester;

    @Override
    public Class<AnalyticsJobEntity> getEntityClass() {
        return AnalyticsJobEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    @Override
    public JacksonTester<AnalyticsJobEntity> getJsonTester() {
        return jsonTester;
    }

    //@Test
    public void canCreate() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "new_job.json");
  
        // when
        MockHttpServletResponse response = create(entity);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        AnalyticsJobEntity entityresult = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(entityresult.getId()).isNotNull();
    }

    //@Test
    public void isValidated() throws Exception {
        // given
//        AnalyticsJobEntity entity = readFromFile(data + "ObjectClass-wrong.json");
  
        // when
//        MockHttpServletResponse response = create(entity);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    //@Test
    public void canNotFindById() throws Exception {
        // when
        MockHttpServletResponse response = mvc
                .perform(get(getRestPath() + "/4242").contentType(MediaType.APPLICATION_JSON)).andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    //@Test
    public void canRetrieveById() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "new_job.json");
        MockHttpServletResponse response = create(entity);
        AnalyticsJobEntity responseEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);

        // when
        response = retrieveById(responseEntity.getId());

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        AnalyticsJobEntity retrievedEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(retrievedEntity.getId()).isEqualTo(responseEntity.getId());
        assertThat(retrievedEntity.getName()).isEqualTo(entity.getName());
    }

    @Test
    public void canUpdate() throws Exception {

        // given
//        AnalyticsJobEntity entity = readFromFile(data + "ObjectClass.json");
//        MockHttpServletResponse response = create(entity);
//        AnalyticsJobEntity entity2 = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);

        // when
//        response = update(entity2);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        AnalyticsJobEntity entityresult = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
//        assertThat(dtoresult.getBranch()).isEqualTo("v2");
    }

    @Override
    @Test
    public void canDelete() throws Exception {
        // given
//        AnalyticsJobEntity entity = readFromFile(data + "ObjectClass.json");
//        MockHttpServletResponse response = create(entity);
//        AnalyticsJobEntity entity2 = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
//        response = retrieveById(entity2.getId());
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // when
//        delete(entity2.getId());

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        response = retrieveById(entity2.getId());
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}
