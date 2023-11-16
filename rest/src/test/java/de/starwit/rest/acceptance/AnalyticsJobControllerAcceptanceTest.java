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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.config.PrimaryDataSourceConfig;
import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.rest.RestApplication;
import de.starwit.service.datasource.SaeDataSource;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsJobControllerAcceptanceTest extends AbstractControllerAcceptanceTest<AnalyticsJobEntity> {

    @MockBean
    SaeDataSource saeDataSource;

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
        assertThat(retrievedEntity.getGeometryPoints().get(0).getX()).isEqualTo(0.1);
        assertThat(retrievedEntity.getGeometryPoints().get(1).getX()).isEqualTo(0.9);
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

}
