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

import de.starwit.persistence.entity.ObjectClassEntity;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
public class ObjectClassControllerAcceptanceTest extends AbstractControllerAcceptanceTest<ObjectClassEntity> {


    final static Logger LOG = LoggerFactory.getLogger(ObjectClassControllerAcceptanceTest.class);
    private static final String data = "testdata/objectclass/";
    private static final String restpath = "/api/objectclasss/";

    private JacksonTester<ObjectClassEntity> jsonTester;

    @Override
    public Class<ObjectClassEntity> getEntityClass() {
        return ObjectClassEntity.class;
    }

    @Override
    public String getRestPath() {
        return restpath;
    }

    @Override
    public JacksonTester<ObjectClassEntity> getJsonTester() {
        return jsonTester;
    }

    @Test
    public void canCreate() throws Exception {
        // given
//        ObjectClassEntity entity = readFromFile(data + "ObjectClass.json");
  
        // when
//        MockHttpServletResponse response = create(entity);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        ObjectClassEntity entityresult = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);
//        assertThat(entityresult.getBranch()).isEqualTo("v2");
    }

    @Test
    public void isValidated() throws Exception {
        // given
//        ObjectClassEntity entity = readFromFile(data + "ObjectClass-wrong.json");
  
        // when
//        MockHttpServletResponse response = create(entity);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void canNotFindById() throws Exception {
        // when
        MockHttpServletResponse response = mvc
                .perform(get(getRestPath() + "/4242").contentType(MediaType.APPLICATION_JSON)).andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void canRetrieveById() throws Exception {
        // given
//        ObjectClassEntity entity = readFromFile(data + "ObjectClass.json");
//        MockHttpServletResponse response = create(entity);
//        ObjectClassEntity entity2 = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);

        // when
//        response = retrieveById(entity2.getId());

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        ObjectClassEntity entityresult = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);
//        assertThat(dtoresult.getBranch()).isEqualTo("v2");
    }

    @Test
    public void canUpdate() throws Exception {

        // given
//        ObjectClassEntity entity = readFromFile(data + "ObjectClass.json");
//        MockHttpServletResponse response = create(entity);
//        ObjectClassEntity entity2 = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);

        // when
//        response = update(entity2);

        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        ObjectClassEntity entityresult = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);
//        assertThat(dtoresult.getBranch()).isEqualTo("v2");
    }

    @Override
    @Test
    public void canDelete() throws Exception {
        // given
//        ObjectClassEntity entity = readFromFile(data + "ObjectClass.json");
//        MockHttpServletResponse response = create(entity);
//        ObjectClassEntity entity2 = mapper.readValue(response.getContentAsString(), ObjectClassEntity.class);
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
