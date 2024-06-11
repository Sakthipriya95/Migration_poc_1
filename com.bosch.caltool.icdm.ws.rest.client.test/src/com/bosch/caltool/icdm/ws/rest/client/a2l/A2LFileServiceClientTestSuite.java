/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.bosch.caltool.icdm.ws.rest.client.cdr.CompliReviewServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewServiceClientRuleTypesTest;

/**
 * @author bne4cob
 */
@RunWith(Suite.class)
@SuiteClasses({
    // A2L File services
    A2LFileInfoServiceClientTest.class,
    A2LFileDownloadServiceClientTest.class,
    A2LFileExportServiceClientTest.class,
    A2LFileUploadServiceClientTest.class,

    // Compli Reviews
    CompliReviewServiceClientTest.class,

    // Data Reviews
    ReviewServiceClientRuleTypesTest.class })
public class A2LFileServiceClientTestSuite {
  // Nothing required here
}
