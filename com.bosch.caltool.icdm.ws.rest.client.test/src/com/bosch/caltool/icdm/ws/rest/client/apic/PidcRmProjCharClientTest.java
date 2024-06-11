/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test Client class for Meta Data Collection
 *
 * @author rgo7cob
 */
public class PidcRmProjCharClientTest extends AbstractRestClientTest {

  private final static Long PIDC_RM_ID = 1209555215L;

  /**
   * Test method for {@link PidcRmProjCharClient#create(PidcRmProjCharacter)},
   * {@link PidcRmProjCharClient#update(PidcRmProjCharacter)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    PidcRmProjCharClient servClient = new PidcRmProjCharClient();

    PidcVersionServiceClient pidcVerServClient = new PidcVersionServiceClient();
    PidcVersion parentPidcVersion = pidcVerServClient.getById(1274091770L);
    PidcVersion newPidcVersion = new PidcVersion();
    newPidcVersion.setPidcId(parentPidcVersion.getPidcId());
    newPidcVersion.setParentPidcVerId(parentPidcVersion.getId());
    newPidcVersion.setName("JUnit_ProjChar_" + getRunId());
    newPidcVersion.setVersionName("V1" + getRunId());
    newPidcVersion.setVersDescEng("JUnit_ProjChar_DescEng" + getRunId());
    newPidcVersion.setVersDescGer("JUnit_ProjChar_DescEng" + getRunId());
    PidcVersion createdPidcVerObj = pidcVerServClient.createNewRevisionForPidcVers(newPidcVersion);

    PidcRmDefClient pidcRmDefServClient = new PidcRmDefClient();
    List<PidcRmDefinition> pidcRmDefinitionList = pidcRmDefServClient.getPidRmDefList(createdPidcVerObj.getId());
    PidcRmDefinition pidcRmDefinition = pidcRmDefinitionList.get(0);

    PidcRmProjCharacter pidcRmProjCharacter = new PidcRmProjCharacter();
    pidcRmProjCharacter.setRmDefId(pidcRmDefinition.getId());
    pidcRmProjCharacter.setProjCharId(870916715L);
    pidcRmProjCharacter.setRbShareId(862557865L);
    pidcRmProjCharacter.setRbDataId(862557965L);
    pidcRmProjCharacter.setRelevant("Y");

    // invoke create
    PidcRmProjCharacter createdObj = servClient.create(pidcRmProjCharacter);
    // validate create
    assertNotNull("Created Object is not null", createdObj);
    assertEquals("ProjCharId is equal", Long.valueOf(870916715), createdObj.getProjCharId());
    assertEquals("Relevant flag is equal", "Y", createdObj.getRelevant());

    createdObj.setRelevant("N");
    // invoke update
    PidcRmProjCharacter updatedObj = servClient.update(createdObj);
    // validate update
    assertNotNull("Updated Object is not null", updatedObj);
    assertEquals("ProjCharId is equal", Long.valueOf(870916715), updatedObj.getProjCharId());
    assertEquals("Relevant flag is equal", "N", updatedObj.getRelevant());
  }


  /**
   * Test method for {@link PidcRmProjCharClient#getPidcRmProjcharExt(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcRmProjcharExt() throws ApicWebServiceException {
    PidcRmProjCharClient servClient = new PidcRmProjCharClient();

    Set<PidcRmProjCharacterExt> pidRmOutput = servClient.getPidcRmProjcharExt(PIDC_RM_ID);
    assertFalse("Response should not be empty", pidRmOutput.isEmpty());
    LOG.info("Size : {}", pidRmOutput.size());
    boolean projCharAvailable = false;
    for (PidcRmProjCharacterExt pidcRmProjCharacterExt : pidRmOutput) {
      if (pidcRmProjCharacterExt.getPidRmChar().getProjCharId().equals(870905165L)) {
        projCharAvailable = true;
        testPidcRmProjCharacter(pidcRmProjCharacterExt.getPidRmChar());
        break;
      }
    }
    assertTrue("PidcRmProjChar is available", projCharAvailable);
  }

  /**
   * @param pidRmChar
   */
  private void testPidcRmProjCharacter(final PidcRmProjCharacter pidRmChar) {
    assertEquals("RB_Data Id is equal", Long.valueOf(862557965), pidRmChar.getRbDataId());
    assertEquals("RB_Share Id is equal", Long.valueOf(862557865), pidRmChar.getRbShareId());
    assertEquals("Relevant Flag is equal", "N", pidRmChar.getRelevant());
  }

}
