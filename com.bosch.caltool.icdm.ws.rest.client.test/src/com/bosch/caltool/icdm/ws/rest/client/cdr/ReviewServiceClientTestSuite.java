/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author bne4cob
 */
@RunWith(Suite.class)
@SuiteClasses({
    // SSD Compliance checks
    CompliReviewServiceClientTest.class,
    CompliReviewServiceClientTestNegative.class,

    // Data Reviews
    ReviewServiceClientRuleTypesTest.class,
    ReviewServiceClientDeltaTest.class,
    ReviewServiceClientNormalTest.class,
    ReviewServiceClientProjectDataDeltaTest.class,
    ReviewServiceClientCancelledReviewTest.class,

    // MoniCa Report uploads
    MonicaReviewServiceClientTest.class })
public class ReviewServiceClientTestSuite {
  //Nothing required here
}
