/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test Client class for Meta Data Collection
 *
 * @author rgo7cob
 */
public class PidcRmDefClientTest extends AbstractRestClientTest {

  private final static Long PIDC_VERS_ID = 1236683020L;

  /**
   * Test method for {@link PidcRmDefClient#getPidRmDefList(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidRmDefList() throws ApicWebServiceException {
    PidcRmDefClient servClient = new PidcRmDefClient();
    List<PidcRmDefinition> pidcRmDefList = servClient.getPidRmDefList(PIDC_VERS_ID);
    assertFalse("List should not be empty", pidcRmDefList.isEmpty());
    LOG.info("Size: {}", pidcRmDefList.size());
    testOutput(pidcRmDefList);
  }


  /**
   * Test method for {@link PidcRmDefClient#isPidcRmEmpty(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testIsPidcRmEmpty() throws ApicWebServiceException {
    PidcRmDefClient servClient = new PidcRmDefClient();
    boolean isPidcRmListEmpty = servClient.isPidcRmEmpty(1274091770L);
    assertFalse(isPidcRmListEmpty);// Pidc risk management is applicable for this pidc

  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final List<PidcRmDefinition> pidcRmDefList) {
    LOG.info("Pidc Rm MetaDefinition data obtained");
    assertNotNull(pidcRmDefList + ": object not null");
    assertNotNull(pidcRmDefList.get(0) + ": Size not Empty not null");
    assertNotNull(pidcRmDefList.get(0).getName() + ": First element name is not null");
  }
}
