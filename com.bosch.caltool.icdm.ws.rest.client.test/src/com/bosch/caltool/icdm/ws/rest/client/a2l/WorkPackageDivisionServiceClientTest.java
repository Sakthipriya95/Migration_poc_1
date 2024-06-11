/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class WorkPackageDivisionServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String RESPONSE_EMPTY = "Response empty";
  /**
   *
   */
  private static final String RESPONSE_ITEM = "Response item";
  /**
   *
   */
  private static final String RESPONSE_SIZE = "Response size = {}";
  /**
   * BEG - Division
   */
  private static final Long DIV_ID = 787372419L;
  /**
   * Diagnose 2-Punkt Lambdasonde - BEG
   */
  private static final Long WP_DIV_ID = 797221615L;
  /**
   * Diagnose 2-Punkt Lambdasonde
   */
  private static final Long WP_ID = 797213265L;

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#getWPDivisionsByByDivID(Long, boolean)}
   * .
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWPDivisionsByDivID() throws ApicWebServiceException {
    Set<WorkPackageDivision> retSet = new WorkPackageDivisionServiceClient().getWPDivisionsByByDivID(DIV_ID, false);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput(RESPONSE_ITEM, retSet.iterator().next());
  }

  /**
   * Test method to get WP Divisions including deleted items for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#getWPDivisionsByByDivID(Long, boolean)}
   * .
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWPDivisionsByDivIDIncludeDeleted() throws ApicWebServiceException {
    Set<WorkPackageDivision> retSet = new WorkPackageDivisionServiceClient().getWPDivisionsByByDivID(DIV_ID, true);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());
    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput(RESPONSE_ITEM, retSet.iterator().next());
    boolean deletedRecordFound = false;
    for (WorkPackageDivision workPackageDivision : retSet) {
      if (ApicConstants.CODE_YES.equals(workPackageDivision.getDeleted())) {
        deletedRecordFound = true;
        break;
      }
    }
    // Check if deleted Wp divs are included in the response
    assertTrue("Deleted entries not found for the Division ", deletedRecordFound);
  }


  /**
   * @param string
   * @param wpDiv
   */
  private void testOutput(final String string, final WorkPackageDivision wpDiv) {
    assertNotNull(string + ": Object null", wpDiv);
    LOG.info("{}: Version = {}; {}", string, wpDiv.getWpName(), wpDiv);
    assertFalse(string + ": Name empty", (wpDiv.getWpName() == null) || wpDiv.getWpName().isEmpty());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#update(com.bosch.caltool.icdm.model.wp.WorkPackageDivision)}
   * .
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {
    WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();
    Set<WorkPackageDivision> retSet = servClient.getWPDivisionsByByDivID(DIV_ID, false);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    WorkPackageDivision wpDiv = retSet.iterator().next();
    testOutput(RESPONSE_ITEM, wpDiv);
    String comment = "Comment_" + getRunId();
    wpDiv.setWpdComment(comment);
    wpDiv.setCrpObdRelevantFlag(CommonUtils.getBooleanCode(!CommonUtils.getBooleanType(wpDiv.getCrpObdRelevantFlag())));
    wpDiv.setCrpObdComment("Crp_Obd_Comment_" + getRunId());
    wpDiv.setCrpEmissionRelevantFlag(
        CommonUtils.getBooleanCode(!CommonUtils.getBooleanType(wpDiv.getCrpEmissionRelevantFlag())));
    wpDiv.setCrpEmissionComment("Crp_Emission_Comment_" + getRunId());
    wpDiv.setCrpSoundRelevantFlag(
        CommonUtils.getBooleanCode(!CommonUtils.getBooleanType(wpDiv.getCrpSoundRelevantFlag())));
    wpDiv.setCrpSoundComment("Crp_Sound_Comment_" + getRunId());

    // Service for WorkpackageDivision update
    WorkPackageDivision wpDivUpdated = servClient.update(wpDiv);

    testOutput(RESPONSE_ITEM, wpDivUpdated);
    assertEquals("Verify Comment Updated", comment, wpDivUpdated.getWpdComment());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#create(com.bosch.caltool.icdm.model.wp.WorkPackageDivision)}
   * .
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testCreate() throws ApicWebServiceException {
    WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();
    WorkPackageDivision wpDetails = new WorkPackageDivision();
    wpDetails.setWpId(WP_ID);
    wpDetails.setContactPersonId(15462952128L); // Prem Kumar S R
    wpDetails.setContactPersonSecondId(15462952128L); // Prem Kumar S R
    wpDetails.setDivAttrValId(787372417L); // PS-EC (formerly DGS-EC)
    wpDetails.setWpResId(927896665L); // AF_BCAL
    wpDetails.setWpIdMcr("Junit_TestMCR_" + getRunId());
    wpDetails.setWpdComment("Comment on Test MCR");
    wpDetails.setCrpObdComment("Crp Obd Comment");
    wpDetails.setCrpEmissionComment("Crp Emission Comment");
    wpDetails.setCrpSoundComment("Crp Sound Comment");


    // Service for WorkpackageDivision create
    WorkPackageDivision wpDetailCreated = servClient.create(wpDetails);
    testOutput(RESPONSE_ITEM, wpDetailCreated);

    String wpIdMcr = "Junit_TestMCR_Updated_" + getRunId();
    wpDetailCreated.setWpIdMcr(wpIdMcr);

    // Service for WorkpackageDivision update
    WorkPackageDivision wpDetailUpdated = servClient.update(wpDetailCreated);
    testOutput(RESPONSE_ITEM, wpDetailUpdated);
    assertEquals("Verify WpIDMcr Updated", wpIdMcr, wpDetailUpdated.getWpIdMcr());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#get(java.lang.Long)}.
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    WorkPackageDivision ret = new WorkPackageDivisionServiceClient().get(WP_DIV_ID);
    testOutput(RESPONSE_ITEM, ret);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#get(java.lang.Long)}.
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWpDiByWpId() throws ApicWebServiceException {
    Set<WorkPackageDivision> retSet = new WorkPackageDivisionServiceClient().getByWpId(WP_ID);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput("Response item, check first item", retSet.iterator().next());
  }


  /**
   * Test method with ICC Relevant Flag as No - for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#getWPDivisionsByDivIDandIccRelevantFlag(Long, boolean, String)}.
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWPDivisionsByDivIDandIccRelevantFlagN() throws ApicWebServiceException {

    Set<WorkPackageDivision> retSet = new WorkPackageDivisionServiceClient()
        .getWPDivisionsByDivIDandIccRelevantFlag(DIV_ID, true, ApicConstants.CODE_NO);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput(RESPONSE_ITEM, retSet.iterator().next());

    assertFalse("ICC Relevant Flag is Yes, it is invalid",
        CommonUtils.getBooleanType(retSet.iterator().next().getIccRelevantFlag()));
  }

  /**
   * Test method with ICC Relevant Flag as Yes - for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#getWPDivisionsByDivIDandIccRelevantFlag(Long, boolean, String)}.
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWPDivisionsByDivIDandIccRelevantFlagY() throws ApicWebServiceException {

    Set<WorkPackageDivision> retSet = new WorkPackageDivisionServiceClient()
        .getWPDivisionsByDivIDandIccRelevantFlag(DIV_ID, true, ApicConstants.CODE_YES);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput(RESPONSE_ITEM, retSet.iterator().next());

    assertTrue("ICC Relevant Flag is No, it is invalid",
        CommonUtils.getBooleanType(retSet.iterator().next().getIccRelevantFlag()));
  }

  /**
   * Test method for all ICC Relevant Flag values - for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient#getWPDivisionsByDivIDandIccRelevantFlag(Long, boolean, String)}.
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testGetWPDivisionsByDivIDandIccRelevantFlagAll() throws ApicWebServiceException {

    Set<WorkPackageDivision> retSet =
        new WorkPackageDivisionServiceClient().getWPDivisionsByDivIDandIccRelevantFlag(DIV_ID, true, null);

    assertFalse(RESPONSE_EMPTY, retSet.isEmpty());

    LOG.info(RESPONSE_SIZE, retSet.size());
    testOutput(RESPONSE_ITEM, retSet.iterator().next());

    boolean iccRelevantFlagYFound = false;
    boolean iccRelevantFlagNFound = false;
    for (WorkPackageDivision workPackageDivision : retSet) {
      if (CommonUtils.isEqualIgnoreCase(workPackageDivision.getIccRelevantFlag(), "Y")) {
        iccRelevantFlagYFound = true;
      }
      else if (CommonUtils.isEqualIgnoreCase(workPackageDivision.getIccRelevantFlag(), "N")) {
        iccRelevantFlagNFound = true;
      }
      // If ICC flag Yes and No are found break
      if (iccRelevantFlagYFound && iccRelevantFlagNFound) {
        break;
      }
    }
    // Check if both combination of ICC Relevant flag are found
    assertTrue("All possible combination of ICC Relevant Flag not found for the Division ",
        iccRelevantFlagYFound && iccRelevantFlagNFound);
  }

}
