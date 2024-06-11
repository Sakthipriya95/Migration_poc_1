/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service.test;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bosch.boot.ssd.api.service.CreateSSDUserService;

/**
 * @author TUD1COB
 *
 */


class CreateSSDUserServiceTest {
 
    @Mock
    private CreateSSDUserService createSSDUserService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    
    @Test
    public void testDoCreateUser(){
     createSSDUserService.doCreateUser();
     assertNotNull("Object should not be null",createSSDUserService);
    }
    
}

