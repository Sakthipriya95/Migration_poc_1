/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.bosch.caltool.icdm.ws.rest.client.a2l.precal.PreCalDataServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.ssd.RuleDescriptionServiceClientTest;

/**
 * @author bne4cob
 */
@RunWith(Suite.class)
@SuiteClasses({
    // Rule Maintenance tests
    ReviewRuleServiceClientTest.class,
    RuleSetRuleServiceClientTest.class,

    // SSD Compliance checks
    CompliReviewServiceClientTest.class,
    CompliReviewServiceClientTestNegative.class,

    // Data Reviews
    ReviewServiceClientRuleTypesTest.class,
    ReviewServiceClientNormalTest.class,

    // Pre-Cal Tests
    PreCalibrationDataServiceClientTest.class,
    PreCalDataServiceClientTest.class,

    // OEM Rule descriptions
    RuleDescriptionServiceClientTest.class })
public class SsdIfIcdmServicesSuite {
  // Nothing required here
}
