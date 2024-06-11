/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author rgo7cob
 */
@RunWith(Suite.class)
@SuiteClasses({
    TestLogin.class,
    TestAttrs.class,
    TestPidc.class,
    TestUseCase.class,
    TestPidcAccessRights.class,
    TestPidcDifferences.class,
    TestVcdmLabelStat.class,
    TestReviewResult.class,
    TestVersionStat.class })
public class SoapTestSuite {

}

