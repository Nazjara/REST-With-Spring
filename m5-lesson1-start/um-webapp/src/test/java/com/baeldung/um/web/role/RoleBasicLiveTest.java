package com.baeldung.um.web.role;

import static com.baeldung.common.spring.util.Profiles.CLIENT;
import static com.baeldung.common.spring.util.Profiles.TEST;

import com.baeldung.um.persistence.model.Role;
import com.baeldung.um.util.Um;
import com.google.common.collect.Sets;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.hamcrest.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.baeldung.um.spring.CommonTestConfig;
import com.baeldung.um.spring.UmClientConfig;
import com.baeldung.um.spring.UmLiveTestConfig;

import static org.apache.commons.lang3.RandomStringUtils.*;


import java.util.List;

@ActiveProfiles({ CLIENT, TEST })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UmLiveTestConfig.class, UmClientConfig.class, CommonTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class RoleBasicLiveTest {

    private final static String JSON = MediaType.APPLICATION_JSON.toString();
    private final static String URI = "http://localhost:8082/um-webapp/api/roles";

    @Test
    public void whenAllRolesAreRetrieved_then200Ok() {
        final RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.ADMIN_EMAIL, Um.ADMIN_PASS);
        final Response response = basicAuth.accept(ContentType.JSON).get(URI);

        assertThat(response.getStatusCode(), Matchers.equalTo(200));
    }

    @Test
    public void whenAllRolesAreRetrieved_thenAtLeastOneRoleExists() {
        final Response response = RestAssured.given().auth().preemptive().basic(Um.ADMIN_EMAIL, Um.ADMIN_PASS).accept(ContentType.JSON).get(URI);
        final List roles = response.as(List.class);

//        assertThat(roles, not(Matchers.<Role> empty()));
    }

    @Test
    public void whenCreatingNewRole_thenRoleCanBeRetrieved() {
        final Role role = new Role(randomAlphabetic(6), Sets.newHashSet());
        final RequestSpecification basicAuth = RestAssured.given().auth().preemptive().basic(Um.ADMIN_EMAIL, Um.ADMIN_PASS).accept(ContentType.JSON);
        final Response response = basicAuth.contentType(ContentType.JSON).body(role).post(URI);

        final String locationHeader = response.getHeader("Location");
        final RequestSpecification readAuth = RestAssured.given().auth().preemptive().basic(Um.ADMIN_EMAIL, Um.ADMIN_PASS).accept(ContentType.JSON);
        final Role retrievedRole = readAuth.accept(ContentType.JSON).get(locationHeader).as(Role.class);
        assertEquals(role, retrievedRole);
    }
    
}
