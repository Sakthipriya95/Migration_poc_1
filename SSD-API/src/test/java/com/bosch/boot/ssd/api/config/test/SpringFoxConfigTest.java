/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.config.test;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.boot.ssd.api.config.SpringFoxConfig;

import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author TUD1COB
 *
 */
public class SpringFoxConfigTest {

    /**
     * Test formation for Swagger API
     */
    @Test
    public void testApi() {
      SpringFoxConfig docketTest = new SpringFoxConfig();
      Docket docket = docketTest.api();
      assertNotNull(docket);
    }
    
}

