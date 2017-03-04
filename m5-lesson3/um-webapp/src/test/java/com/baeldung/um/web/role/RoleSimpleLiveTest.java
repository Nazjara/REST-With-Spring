package com.baeldung.um.web.role;

import static com.baeldung.common.spring.util.Profiles.CLIENT;
import static com.baeldung.common.spring.util.Profiles.TEST;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.baeldung.common.web.WebConstants;
import com.baeldung.test.common.util.IDUtil;
import com.baeldung.um.client.template.RoleSimpleApiClient;
import com.baeldung.um.persistence.model.Privilege;
import com.baeldung.um.persistence.model.Role;
import com.baeldung.um.spring.CommonTestConfig;
import com.baeldung.um.spring.UmClientConfig;
import com.baeldung.um.spring.UmLiveTestConfig;
import com.google.common.collect.Sets;
import com.jayway.restassured.response.Response;

@ActiveProfiles({ CLIENT, TEST })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UmLiveTestConfig.class, UmClientConfig.class, CommonTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class RoleSimpleLiveTest {

    private final static String JSON = MediaType.APPLICATION_JSON.toString();

    @Autowired
    private RoleSimpleApiClient api;

    // find - one

    @Test
    public final void whenNonExistingResourceIsRetrieved_then404IsReceived() {
        final Response response = getApi().findOneAsResponse(IDUtil.randomPositiveLong());

        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    public final void whenResourceIsRetrievedByNonNumericId_then400IsReceived() {
        // When
        final Response res = getApi().read(getUri() + WebConstants.PATH_SEP + randomAlphabetic(6));

        // Then
        assertThat(res.getStatusCode(), is(400));
    }

    @Test
    public final void givenResourceForIdExists_whenResourceOfThatIdIsRetrieved_then200IsRetrieved() {
        // Given
        final String uriForResourseCreation = getApi().createAsResponse(createNewResource()).getHeader(HttpHeaders.LOCATION);
        
        //when
        final Response response = getApi().read(uriForResourseCreation);

        // Then
        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    public final void whenResourceIsCreated_thenResourceIsCorrectlyRetrieved() {
        // Given, When
        final Role newResource = createNewResource();
        final Role createdResource = getApi().create(newResource);

        // Then
        assertEquals(createdResource, newResource);
    }
    
    // find - all

    @Test
    public final void whenAllResourcesAreRetrieved_then200IsReceived() {
        // When
        final Response response = getApi().read(getUri());

        // Then
        assertThat(response.getStatusCode(), is(200));
    }
    
    // create

    @Test
    public final void whenResourceIsCreated_then201IsReceived() {
        // When
        final Response response = getApi().createAsResponse(createNewResource());

        // Then
        assertThat(response.getStatusCode(), is(201));
    }

    @Test
    public final void givenResourceHasNameWithSpace_whenResourceIsCreated_then201IsReceived() {
        final Role newResource = createNewResource();
        newResource.setName(randomAlphabetic(4) + " " + randomAlphabetic(4));

        // When
        final Response createAsResponse = getApi().createAsResponse(newResource);

        // Then
        assertThat(createAsResponse.getStatusCode(), is(201));
    }

    @Test
    public final void whenResourceWithUnsupportedMediaTypeIsCreated_then415IsReceived() {
        // When
        final Response response = getApi().givenAuthenticated().contentType("unknown").post(getUri());

        // Then
        assertThat(response.getStatusCode(), is(415));
    }

    @Test
    public final void whenResourceIsCreatedWithNonNullId_then409IsReceived() {
        final Role resourceWithId = createNewResource();
        resourceWithId.setId(5l);

        // When
        final Response response = getApi().createAsResponse(resourceWithId);

        // Then
        assertThat(response.getStatusCode(), is(409));
    }

    @Test
    public final void whenResourceIsCreated_thenResponseContainsTheLocationHeader() {
        // When
        final Response response = getApi().createAsResponse(createNewResource());

        // Then
        assertNotNull(response.getHeader(HttpHeaders.LOCATION));
    }

    @Test
    public final void givenResourceExsits_whenResourceWithSameAttributeIsCreated_then409IsReceived(){
        //Given
        final Role newEntity = createNewResource();
        getApi().createAsResponse(newEntity);
        
        //when
        final Response response =  getApi().createAsResponse(newEntity);
        
        // Then
        assertThat(response.getStatusCode(), is(409));
    }
    // UTIL

    private final String getUri() {
        return getApi().getUri() + WebConstants.PATH_SEP;
    }

    private final RoleSimpleApiClient getApi() {
        return api;
    }

    private final Role createNewResource() {
        return new Role(randomAlphabetic(8), Sets.newHashSet());
    }

}