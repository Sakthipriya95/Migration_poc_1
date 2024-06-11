/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author bne4cob
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CnsDataProducerServiceClientTest.class,
    CnsDataConsumerServiceClientTest.class,
    CnsAdminServiceClientTest.class })
public class AllSuite {
  // All tests
}

