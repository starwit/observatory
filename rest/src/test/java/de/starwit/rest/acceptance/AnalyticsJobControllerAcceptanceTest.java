package de.starwit.rest.acceptance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsJobControllerAcceptanceTest extends AbstractControllerAcceptanceTest<AnalyticsJobEntity> {

    final static Logger LOG = LoggerFactory.getLogger(AnalyticsJobControllerAcceptanceTest.class);
    private static final String data = "testdata/analytics-job/";
    private static final String restpath = "/api/analytics-job";

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

    @Test
    public void canCreate() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");

        // when
        MockHttpServletResponse response = create(entity);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        AnalyticsJobEntity entityresult = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(entityresult.getId()).isNotNull();
    }

    @Test
    public void createIsValidated() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1_with_id.json");

        // when
        MockHttpServletResponse response = create(entity);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void canRetrieveById() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");
        MockHttpServletResponse response = create(entity);
        AnalyticsJobEntity responseEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);

        // when
        response = retrieveById(responseEntity.getId());

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        AnalyticsJobEntity retrievedEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(retrievedEntity.getId()).isEqualTo(responseEntity.getId());
        assertThat(retrievedEntity.getName()).isEqualTo(entity.getName());
        assertThat(retrievedEntity.getGeoReferenced()).isTrue();
        assertThat(retrievedEntity.getGeometryPoints().get(0).getX().doubleValue()).isEqualTo(0.1);
        assertThat(retrievedEntity.getGeometryPoints().get(0).getLatitude().doubleValue()).isEqualTo(52.1);
        assertThat(retrievedEntity.getGeometryPoints().get(0).getLongitude().doubleValue()).isEqualTo(10.2);
        assertThat(retrievedEntity.getGeometryPoints().get(1).getX().doubleValue()).isEqualTo(0.9);
    }

    @Test
    public void canUpdate() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");
        MockHttpServletResponse response = create(entity);
        AnalyticsJobEntity responseEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);

        // when
        response = update(responseEntity);

        MockHttpServletResponse checkResponse = retrieveById(responseEntity.getId());

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        AnalyticsJobEntity retrievedEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(retrievedEntity.getId()).isEqualTo(responseEntity.getId());
        assertThat(retrievedEntity.getName()).isEqualTo(entity.getName());

        AnalyticsJobEntity checkEntity = mapper.readValue(checkResponse.getContentAsString(), AnalyticsJobEntity.class);
        assertThat(checkEntity.getName()).isEqualTo(entity.getName());
        assertThat(checkEntity.getEnabled()).isEqualTo(entity.getEnabled());
        assertThat(checkEntity.getGeometryPoints().size()).isEqualTo(entity.getGeometryPoints().size());
    }

    @Override
    @Test
    public void canDelete() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");
        MockHttpServletResponse response = create(entity);
        AnalyticsJobEntity responseEntity = mapper.readValue(response.getContentAsString(), AnalyticsJobEntity.class);

        // when
        response = delete(responseEntity.getId());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        MockHttpServletResponse checkResponse = retrieveById(responseEntity.getId());
        assertThat(checkResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
    
    @Test
    public void canDeleteAll() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");
        MockHttpServletResponse response1 = create(entity);
        MockHttpServletResponse response2 = create(entity);
    
        // when
        MockHttpServletResponse deleteResponse = deleteAll();
    
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    
        MockHttpServletResponse checkResponse = retrieveAll();
        assertThat(checkResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        AnalyticsJobEntity[] entities = mapper.readValue(checkResponse.getContentAsString(), AnalyticsJobEntity[].class);
        assertThat(entities.length).isEqualTo(0);
    }

    @Test
    public void canDeleteByObservationAreaId() throws Exception {
        // given
        AnalyticsJobEntity entity = readFromFile(data + "job1.json");
        entity.setObservationAreaId(1L);
        MockHttpServletResponse response1 = create(entity);
        entity.setObservationAreaId(2L);
        MockHttpServletResponse response2 = create(entity);
    
        // when
        MockHttpServletResponse deleteResponse = deleteByObservationAreaId(1L);
    
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    
        MockHttpServletResponse checkResponse = retrieveAll();
        assertThat(checkResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        AnalyticsJobEntity[] entities = mapper.readValue(checkResponse.getContentAsString(), AnalyticsJobEntity[].class);
        assertThat(entities.length).isEqualTo(1);
        assertThat(entities[0].getObservationAreaId()).isEqualTo(2);
    }

}
    