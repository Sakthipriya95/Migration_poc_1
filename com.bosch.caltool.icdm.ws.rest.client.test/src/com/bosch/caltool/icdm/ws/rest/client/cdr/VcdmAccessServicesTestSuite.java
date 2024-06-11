/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileDownloadServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileUploadServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVcdmTransferServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmAvailabilityCheckServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmCalDataServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmFileDownloadServiceClientTest;

/**
 * IMPORTANT !!! : RUN this suite ONLY after clearing the server work directory
 *
 * @author bne4cob
 */
@RunWith(Suite.class)
@SuiteClasses({
    // A2L download from vCDM
    A2LFileDownloadServiceClientTest.class,
    // A2L upload to vCDM
    A2LFileUploadServiceClientTest.class,
    // Includes test using HEX file from vCDM
    CompHexWithCDFxServiceClientTest.class,
    // Includes test using HEX file from vCDM
    CompliReviewServiceClientTest.class,
    // PIDC vCDM Transfer tests
    PidcVcdmTransferServiceClientTest.class,
    // Data review
    ReviewServiceClientRuleTypesTest.class,
    // vCDM Availablity check, with current user
    VcdmAvailabilityCheckServiceClientTest.class,
    // Retrieval of cal data from vCDM
    VcdmCalDataServiceClientTest.class,
    // Includes get HEX file test
    VcdmDataSetServiceClientTest.class,
    // Download HEX, DST, A2L etc. from vCDM
    VcdmFileDownloadServiceClientTest.class })
public class VcdmAccessServicesTestSuite {
  // Nothing required here
}
