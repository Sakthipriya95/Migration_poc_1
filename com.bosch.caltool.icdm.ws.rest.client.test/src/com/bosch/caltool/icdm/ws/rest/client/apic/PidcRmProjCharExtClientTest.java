/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Test Client class for Meta Data Collection
 *
 * @author rgo7cob
 */
public class PidcRmProjCharExtClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmDefClient#getPidcRmOutput()}.
   */
  @Test
  public void testGetRmOutputMatrix() {

    PidcRmProjCharClient client = new PidcRmProjCharClient();


    try {
      Set<PidcRmProjCharacterExt> pidRmOutput = client.getPidcRmProjcharExt(1209555215l);
      testOutput(pidRmOutput);
    }
    catch (Exception e) {
      LOG.error("Error in WS call", e);
      assertNull("Error in WS call", e);
    }
  }


  /**
   * @param pidcRmDefList
   */
  private void testOutput(final Set<PidcRmProjCharacterExt> pidRmOutput) {
    LOG.info("Pidc Rm MetaDefinition data obtained");
    assertNotNull(pidRmOutput + ": object not null");
    for (PidcRmProjCharacterExt rmMatrixOutput : pidRmOutput) {
      assertNotNull(rmMatrixOutput.getPidRmChar() + ": pidc rm char is not null");
      assertNotNull(rmMatrixOutput.getCategoryRiskMap().size() + ": pidc rm char is not null");

    }
  }


}
