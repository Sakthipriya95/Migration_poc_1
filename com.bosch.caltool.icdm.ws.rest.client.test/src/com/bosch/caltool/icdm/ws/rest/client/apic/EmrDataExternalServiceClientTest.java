/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.model.emr.EmrDataExternalResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrDataExternalServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class EmrDataExternalServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String APIC_READ_USER = "MGA1COB";

  /**
  *
  */
  private static final String VALID_USER = "BNE4COB";

  /**
  *
  */
  private static final String EMR_ADMIN = "IMI2SI";

  /**
   * Pidc verison ID
   */
  private static final Long PID_VERS_ID = 2978946975L;

  /**
   * Pidc verison ID
   */
  private static final Long PID_VERS_ID2 = 6437630281L;

  /**
   * Pidc variant ID
   */
  private static final Long PID_VAR_ID = 4676103128L;

  /**
   * Pidc variant ID
   */
  private static final Long PID_VAR_ID2 = 4652729328L;

  /**
   * Invalid pidc version ID
   */
  private static final Long INVALID_PID_VERS_ID = -1L;

  /**
   * Invalid pidc variant ID
   */
  private static final Long INVALID_PID_VAR_ID = -100L;

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataExternal() throws ApicWebServiceException {

    EmrDataExternalResponse response = new EmrDataExternalServiceClient().fetchEmrDataExternal(PID_VERS_ID, PID_VAR_ID);
    assertNotNull(response);
    assertTrue(!response.getEmrFileDetailsMap().isEmpty() && !response.getEmsEmrVariantsMap().isEmpty());
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataExternalWithEmrData() throws ApicWebServiceException {

    EmrDataExternalResponse response = new EmrDataExternalServiceClient().fetchEmrDataExternal(PID_VERS_ID2, null);
    assertNotNull(response);
    assertTrue(!response.getEmrFileDetailsMap().isEmpty() && response.getEmsEmrVariantsMap().isEmpty() && !response.getEmrDataMap().isEmpty());
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataWithoutVarId() throws ApicWebServiceException {

    EmrDataExternalResponse response = new EmrDataExternalServiceClient().fetchEmrDataExternal(PID_VERS_ID, null);
    assertNotNull(response);
    assertTrue(!response.getEmrFileDetailsMap().isEmpty() && !response.getEmsEmrVariantsMap().isEmpty());
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataExternalInvalidPidVersId() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_PID_VERS_ID + "' is invalid for PIDC Version");

    new EmrDataExternalServiceClient().fetchEmrDataExternal(INVALID_PID_VERS_ID, null);
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataExternalInvalidPidVarId() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant with ID '" + INVALID_PID_VAR_ID + "' not found");

    new EmrDataExternalServiceClient().fetchEmrDataExternal(PID_VERS_ID, INVALID_PID_VAR_ID);
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataExternalPidcVerPidVarMismatch() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Input PIDC Variant ID : " + PID_VAR_ID2 + " does not belong to input PIDC Version ID : " + PID_VERS_ID);

    new EmrDataExternalServiceClient().fetchEmrDataExternal(PID_VERS_ID, PID_VAR_ID2);
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataWithReadAccessToPidc() throws ApicWebServiceException {

    EmrDataExternalServiceClient emrDataExternalServiceClient = new EmrDataExternalServiceClient();
    emrDataExternalServiceClient.setClientConfiguration(createClientConfigTestUser(VALID_USER));

    EmrDataExternalResponse response = emrDataExternalServiceClient.fetchEmrDataExternal(PID_VERS_ID, null);

    assertNotNull(response);
    assertTrue(!response.getEmrFileDetailsMap().isEmpty() && !response.getEmsEmrVariantsMap().isEmpty());
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataApicReadUser() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Insufficient access rights to view EMR Data for PIDC Version with ID : " + PID_VERS_ID);

    EmrDataExternalServiceClient emrDataExternalServiceClient = new EmrDataExternalServiceClient();
    emrDataExternalServiceClient.setClientConfiguration(createClientConfigTestUser(APIC_READ_USER));

    emrDataExternalServiceClient.fetchEmrDataExternal(PID_VERS_ID, null);
  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testFetchEmrDataEmrAdminNoPidcReadAccess() throws ApicWebServiceException {

    EmrDataExternalServiceClient emrDataExternalServiceClient = new EmrDataExternalServiceClient();
    emrDataExternalServiceClient.setClientConfiguration(createClientConfigTestUser(EMR_ADMIN));

    EmrDataExternalResponse response = emrDataExternalServiceClient.fetchEmrDataExternal(PID_VERS_ID, null);

    assertNotNull(response);
    assertTrue(!response.getEmrFileDetailsMap().isEmpty() && !response.getEmsEmrVariantsMap().isEmpty());
  }
}
