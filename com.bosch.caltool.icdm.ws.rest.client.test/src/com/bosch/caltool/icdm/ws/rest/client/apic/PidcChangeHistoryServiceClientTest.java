/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PidcChangeHistory
 *
 * @author dmr1cob
 */
public class PidcChangeHistoryServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long OLD_PIDC_CHANGE_NUMBER = 0L;
  /**
   *
   */
  private static final long NEW_PIDC_CHANGE_NUMBER = -1L;
  /**
   *
   */
  private static final long PIDC_VERS_ID = 791409920L;
  private final static Long PIDC_ID = 791409917L;

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcChangeHistoryServiceClientTest#getPidcDiffs}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetPidcDiffs() throws ApicWebServiceException {
    PidcChangeHistoryServiceClient servClient = new PidcChangeHistoryServiceClient();
    PidcDiffsType pidcDiff = new PidcDiffsType();
    pidcDiff.setPidcId(PIDC_ID);
    pidcDiff.setOldPidcChangeNumber(OLD_PIDC_CHANGE_NUMBER);
    pidcDiff.setNewPidcChangeNumber(NEW_PIDC_CHANGE_NUMBER);
    PidcDiffsResponseType retMap = servClient.getPidcDiffs(pidcDiff);
    assertFalse("Response should not be null or empty", ((retMap == null)));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcChangeHistoryServiceClientTest#getPidcAttrDiffForVersion}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetPidcAttrDiffForVersion() throws ApicWebServiceException {
    PidcChangeHistoryServiceClient servClient = new PidcChangeHistoryServiceClient();
    PidcDiffsForVersType pidcDiffsForVersType = new com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType();
    pidcDiffsForVersType.setPidcId(PIDC_ID);
    pidcDiffsForVersType.setPidcVersionId(PIDC_VERS_ID);
    pidcDiffsForVersType.setOldPidcChangeNumber(OLD_PIDC_CHANGE_NUMBER);
    pidcDiffsForVersType.setNewPidcChangeNumber(NEW_PIDC_CHANGE_NUMBER);
    List<AttrDiffType> pidcAttrDiffForVersion = servClient.getPidcAttrDiffForVersion(pidcDiffsForVersType);
    assertFalse("Response should not be null or empty", ((pidcAttrDiffForVersion == null)));

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcChangeHistoryServiceClientTest#getAllPidcDiffForVersion}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetAllPidcDiffForVersion() throws ApicWebServiceException {
    PidcChangeHistoryServiceClient servClient = new PidcChangeHistoryServiceClient();
    PidcDiffsForVersType pidcDiffsForVersType = new com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType();
    pidcDiffsForVersType.setPidcId(PIDC_ID);
    pidcDiffsForVersType.setPidcVersionId(PIDC_VERS_ID);
    pidcDiffsForVersType.setOldPidcChangeNumber(OLD_PIDC_CHANGE_NUMBER);
    pidcDiffsForVersType.setNewPidcChangeNumber(NEW_PIDC_CHANGE_NUMBER);
    PidcDiffsResponseType allPidcDiffForVersion = servClient.getAllPidcDiffForVersion(pidcDiffsForVersType);
    assertFalse("Response should not be null or empty", ((allPidcDiffForVersion == null)));
  }
}
