package com.baeldung.um.web.role;

import com.baeldung.common.interfaces.IDto;
import com.baeldung.common.interfaces.INameableDto;
import com.baeldung.common.util.SearchField;
import com.baeldung.common.web.WebConstants;
import com.baeldung.test.common.util.IDUtil;
import com.baeldung.um.client.template.GenericSimpleApiClient;
import com.baeldung.um.client.template.RoleSimpleApiClient;
import com.baeldung.um.spring.CommonTestConfig;
import com.baeldung.um.spring.UmClientConfig;
import com.baeldung.um.spring.UmLiveTestConfig;
import com.google.common.collect.Sets;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static com.baeldung.common.spring.util.Profiles.CLIENT;
import static com.baeldung.common.spring.util.Profiles.TEST;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@ActiveProfiles({ CLIENT, TEST })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UmLiveTestConfig.class, UmClientConfig.class, CommonTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public abstract class GenericSimpleLiveTest <T extends INameableDto> {

    private final static String JSON = MediaType.APPLICATION_JSON.toString();

    public GenericSimpleLiveTest() {
    }

    // find - one

    // find - all

    @Test
    protected void whenAllResourcesAreRetrieved_then200IsReceived() {
        // When
        final Response response = getApi().read(getUri());

        // Then
        assertThat(response.getStatusCode(), is(200));
    }
    
    // find - all - pagination

    @Test
    protected void whenResourcesAreRetrievedPaginated_then200IsReceived() {
        // When
        final Response response = getApi().findAllPaginatedAsResponse(0, 1);

        // Then
        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    protected void whenPageOfResourcesIsRetrievedOutOfBounds_then404IsReceived() {
        // When
        final Response response = getApi().findAllPaginatedAsResponse(Integer.parseInt(randomNumeric(5)), 1);

        // Then
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    protected void whenResourcesAreRetrievedWithNonNumericPage_then400IsReceived() {
        // When
        final Response response = getApi().findByUriAsResponse(getUri() + "?page=" + randomAlphabetic(5).toLowerCase() + "&size=1");

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    protected void whenResourcesAreRetrievedWithNonNumericPageSize_then400IsReceived() {
        // When
        final Response response = getApi().findByUriAsResponse(getUri() + "?page=0" + "&size=" + randomAlphabetic(5));

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    // find - all - sorting

    @Test
    protected void whenResourcesAreRetrievedSorted_then200IsReceived() {
        final Response response = getApi().findAllSortedAsResponse(SearchField.name.name(), Sort.Direction.ASC.name());

        assertThat(response.getStatusCode(), is(200));
    }

    // find - all - pagination and sorting

    @Test
    protected void whenResourcesAreRetrievedPaginatedAndSorted_then200IsReceived() {
        final Response response = getApi().findAllPaginatedAndSortedAsResponse(0, 1, SearchField.name.name(), Sort.Direction.ASC.name());

        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    protected void whenResourcesAreRetrievedByPaginatedAndWithInvalidSorting_then400IsReceived() {
        // When
        final Response response = getApi().findAllPaginatedAndSortedAsResponse(0, 4, "invalid", null);

        // Then
        assertThat(response.getStatusCode(), is(400));
    }
        
    //count
    
    @Test
    protected void whenCountIsPerformed_then200IsReceived() {
        // When
        final Response response = getApi().countAsResponse();

        // Then
        assertThat(response.getStatusCode(), is(200));
    }
    // create

    @Test
    protected void whenResourceIsCreated_then201IsReceived() {
        // When
        final Response response = getApi().createAsResponse(createNewResource());

        // Then
        assertThat(response.getStatusCode(), is(201));
    }

    @Test
    protected void givenResourceHasNameWithSpace_whenResourceIsCreated_then201IsReceived() {
        final T newResource = createNewResource();
        newResource.setName(randomAlphabetic(4) + " " + randomAlphabetic(4));

        // When
        final Response createAsResponse = getApi().createAsResponse(newResource);

        // Then
        assertThat(createAsResponse.getStatusCode(), is(201));
    }

//    @Test
//    protected void whenResourceIsCreatedWithNewAssociation_then409IsReceived() {
//        final T newResource = createNewResource();
//        getAssociations(newResource).add(createNewAssociationResource());
//
//        // When
//        final Response response = getApi().createAsResponse(newResource);
//
//        // Then
//        assertThat(response.getStatusCode(), is(409));
//    }

//    @Test
//    protected void whenResourceIsCreatedWithInvalidAssociation_then409IsReceived() {
//        final Privilege invalidAssociation = createNewAssociationResource();
//        invalidAssociation.setName(null);
//        final Role newResource = createNewResource();
//        getAssociations(newResource).add(invalidAssociation);
//
//        // When
//        final Response response = getApi().createAsResponse(newResource);
//
//        // Then
//        assertThat(response.getStatusCode(), is(409));
//    }
    
    @Test
    protected void whenResourceWithUnsupportedMediaTypeIsCreated_then415IsReceived() {
        // When
        final Response response = getApi().givenAuthenticated().contentType("unknown").post(getUri());

        // Then
        assertThat(response.getStatusCode(), is(415));
    }

    @Test
    protected void whenResourceIsCreatedWithNonNullId_then409IsReceived() {
        final T resourceWithId = createNewResource();
        resourceWithId.setId(5l);

        // When
        final Response response = getApi().createAsResponse(resourceWithId);

        // Then
        assertThat(response.getStatusCode(), is(409));
    }

    @Test
    protected void whenResourceIsCreated_thenResponseContainsTheLocationHeader() {
        // When
        final Response response = getApi().createAsResponse(createNewResource());

        // Then
        assertNotNull(response.getHeader(HttpHeaders.LOCATION));
    }

    @Test
    protected void givenResourceExsits_whenResourceWithSameAttributeIsCreated_then409IsReceived(){
        //Given
        final T newEntity = createNewResource();
        getApi().createAsResponse(newEntity);
        
        //when
        final Response response =  getApi().createAsResponse(newEntity);
        
        // Then
        assertThat(response.getStatusCode(), is(409));
    }
    
 // update

    @Test
    protected void givenResourceExists_whenResourceIsUpdated_then200IsReceived() {
        // Given
        final T existingResource = getApi().create(createNewResource());

        // When
        final Response response = getApi().updateAsResponse(existingResource);

        // Then
        assertThat(response.getStatusCode(), is(200));
    }

    @Test
    protected void givenInvalidResource_whenResourceIsUpdated_then400BadRequestIsReceived() {
        // Given
        final T existingResource = getApi().create(createNewResource());
        existingResource.setName(null);

        // When
        final Response response = getApi().updateAsResponse(existingResource);

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    protected void whenResourceIsUpdatedWithNullId_then400IsReceived() {
        // When
        final Response response = getApi().updateAsResponse(createNewResource());

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    protected void whenNullResourceIsUpdated_then400IsReceived() {
        // When
        final Response response = getApi().givenAuthenticated().contentType(JSON).put(getUri() + "/" + randomAlphanumeric(4));

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    protected void givenResourceDoesNotExist_whenResourceIsUpdated_then404IsReceived() {
        // Given
        final T unpersistedResource = createNewResource();
        unpersistedResource.setId(IDUtil.randomPositiveLong());

        // When
        final Response response = getApi().updateAsResponse(unpersistedResource);

        // Then
        assertThat(response.getStatusCode(), is(404));
    }

    // delete

    @Test
    protected void givenResourceExists_whenResourceIsDeleted_then204IsReceived() {
        // Given
        final long idOfResource = getApi().create(createNewResource()).getId();

        // When
        final Response response = getApi().deleteAsResponse(idOfResource);

        // Then
        assertThat(response.getStatusCode(), is(204));
    }

    @Test
    protected void whenResourceIsDeletedByIncorrectNonNumericId_then400IsReceived() {
        // When
        final Response response = getApi().givenAuthenticated().delete(getUri() + randomAlphabetic(6));

        // Then
        assertThat(response.getStatusCode(), is(400));
    }

    @Test
    protected void givenResourceDoesNotExist_whenResourceIsDeleted_then404IsReceived() {
        // When
        final Response response = getApi().deleteAsResponse(Long.parseLong(randomNumeric(6)));

        // Then
        assertThat(response.getStatusCode(), is(404));
    }

    @Test
    protected void givenResourceExistedAndWasDeleted_whenRetrievingResource_then404IsReceived() {
        // Given
        final long idOfResource = getApi().create(createNewResource()).getId();
        getApi().deleteAsResponse(idOfResource);

        // When
        final Response getResponse = getApi().findOneAsResponse(idOfResource);

        // Then
        assertThat(getResponse.getStatusCode(), is(404));
    }

    // mime

    @Test
    protected void givenRequestAcceptsMime_whenResourceIsRetrievedById_thenResponseContentTypeIsMime() {
        // Given
        final long idOfCreatedResource = getApi().create(createNewResource()).getId();

        // When
        final Response res = getApi().findOneAsResponse(idOfCreatedResource);

        // Then
        assertThat(res.getContentType(), StringContains.containsString(MediaType.APPLICATION_JSON.toString()));
    }

    
    // UTIL

    private final String getUri() {
        return getApi().getUri() + WebConstants.PATH_SEP;
    }

    protected abstract GenericSimpleApiClient <T> getApi();
    
    protected abstract T createNewResource();

}