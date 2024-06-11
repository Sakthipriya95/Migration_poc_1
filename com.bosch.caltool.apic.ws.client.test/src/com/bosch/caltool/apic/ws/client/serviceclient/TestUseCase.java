/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.UseCaseType;
import com.bosch.caltool.apic.ws.client.usecase.output.StringUseCaseOutput;

/**
 * @author rgo7cob
 */
public class TestUseCase extends AbstractSoapClientTest {

  /**
   *
   */
  private static final long[] TEST_03_UC_ID_ARR = new long[] { 760349359L };

  private static final long[] TEST_02_UC_ID_ARR = new long[] {
      // BAT-Battery
      760569216L,
      // DI-Diagnosis
      518118L,
      // ED-EM&DRIVE
      490726L };

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * @throws Exception service error
   */
  @Test
  public void testGetUseCases01All() throws Exception {

    UseCaseType[] useCases = this.stub.getUseCases();
    assertNotNull(useCases);

    LOG.info("use case count : {}", useCases.length);
    assertTrue("use cases retrieved > 0", useCases.length > 0);

    int toUseCount = useCases.length > 5 ? 5 : useCases.length;
    UseCaseType[] useCasesToUse = new UseCaseType[toUseCount];
    System.arraycopy(useCases, 0, useCasesToUse, 0, toUseCount);

    // Run output showing for the first fiew items
    showOutput(new StringUseCaseOutput(useCasesToUse));

  }

  // 760569216|BAT-Battery 518118|DI-Diagnosis 490726|ED-EM&DRIVE
  /**
   * @throws Exception service error
   */
  @Test
  public void testGetUseCases02ById() throws Exception {
    UseCaseType[] useCases = this.stub.getUseCases(TEST_02_UC_ID_ARR);

    assertNotNull(useCases);

    LOG.info("use cases retrieved : {}", useCases.length);
    assertEquals("use cases retrieved = 0", TEST_02_UC_ID_ARR.length, useCases.length);
  }

  /**
   * Fetch use case for a single ID
   *
   * @throws Exception service error
   */
  @Test
  public void testGetUseCases03ById2() throws Exception {
    UseCaseType[] useCases = this.stub.getUseCases(TEST_03_UC_ID_ARR);

    assertNotNull(useCases);

    LOG.info("use cases : {}", useCases.length);
    assertEquals("use cases retrieved = 0", TEST_03_UC_ID_ARR.length, useCases.length);
    assertEquals("Test use case name ", "ECU Testing", useCases[0].getNameE());

    showOutput(new StringUseCaseOutput(useCases));
  }


}
