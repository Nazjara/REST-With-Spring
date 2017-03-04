package com.baeldung.um.web.role;

import com.baeldung.um.client.template.GenericSimpleApiClient;
import com.baeldung.um.client.template.RoleSimpleApiClient;
import com.baeldung.um.persistence.model.Role;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public final class RoleSimpleLiveTest extends GenericSimpleLiveTest<Role> {

    @Autowired
    private RoleSimpleApiClient api;

    @Override
    protected GenericSimpleApiClient<Role> getApi() {
        return api;
    }

//    @Override
//    @Test
//    @Ignore("Operation is not supported")
//    protected void whenResourceIsCreated_then201IsReceived() {
//    }

    @Override
    protected Role createNewResource() {
        return new Role(randomAlphabetic(8), Sets.newHashSet());
    }

}