package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PidcVersCocWp
 *
 * @author UKT1COB
 */
public class PidcVersCocWpServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";

  /**
   *
   */
  private static final String VERSION_IS_EQUAL = "version is equal";

  /**
   *
   */
  private static final String OBJECT_NOT_NULL = "object not null";

  private static final Long PIDCVERSCOCWP_ID = 24987459327L;

  private static final Long PIDCVERS_ID_WITH_DIV_ID = 13387154735L;

  // link to the below pidc version id - icdm:pidvid,27536113281
  private static final Long PIDCVERS_ID_WITHOUT_DIV_ID = 27536113281L;

  private static final Long INVALID_PIDCVERSCOCWP_ID = 111L;


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpByPidcVersId()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllCocWpByPidcVersId() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    Map<Long, PidcVersCocWp> pidcVersCoCWpMap =
        servClient.getAllCocWpByPidcVersId(PIDCVERS_ID_WITH_DIV_ID).getPidcVersCocWpMap();
    assertFalse("Response should not be null or empty", ((pidcVersCoCWpMap == null) || pidcVersCoCWpMap.isEmpty()));
  }


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpByPidcVersId()} Trying to
   * get data from Pidc Version whose division attribute value is null
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCocWpByPidcVersWithoutDivId() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Division attribute value is not filled for the PIDC version '" + PIDCVERS_ID_WITHOUT_DIV_ID + "'");

    servClient.getAllCocWpByPidcVersId(PIDCVERS_ID_WITHOUT_DIV_ID).getPidcVersCocWpMap();
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpByPidcVersId()} Passing
   * null as input
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCocWpWithNullAsPidcVers() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID 'null' is invalid for PIDC Version");

    servClient.getAllCocWpByPidcVersId(null).getPidcVersCocWpMap();
  }


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpExternal()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllCocWpExternal() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    Map<Long, PidcVersCocWp> pidcVersCoCWpMap =
        servClient.getAllCocWpExternal(PIDCVERS_ID_WITH_DIV_ID).getPidcVersCocWpMap();
    assertFalse("Response should not be null or empty", ((pidcVersCoCWpMap == null) || pidcVersCoCWpMap.isEmpty()));
  }


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpExternal()} Trying to get
   * data from Pidc Version whose division attribute value is null
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCocWpExtWithPIDCVersWithoutDivId() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Division attribute value is not filled for the PIDC version '" + PIDCVERS_ID_WITHOUT_DIV_ID + "'");

    servClient.getAllCocWpExternal(PIDCVERS_ID_WITHOUT_DIV_ID).getPidcVersCocWpMap();
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#getAllCocWpExternal()} Passing null
   * as input
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCocWpExtWithNullAsPidcVers() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID 'null' is invalid for PIDC Version");

    servClient.getAllCocWpExternal(null).getPidcVersCocWpMap();
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    PidcVersCocWp pidcVersCocWp = servClient.get(PIDCVERSCOCWP_ID);
    assertFalse("Response should not be null", (pidcVersCocWp == null));
    assertEquals("WpDivId is equal", Long.valueOf(795413976), pidcVersCocWp.getWPDivId());

    Optional.ofNullable(pidcVersCocWp).ifPresent(this::testOutput);
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithInvalidPidcVers() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version CoC Work Package with ID '" + INVALID_PIDCVERSCOCWP_ID + "' not found");

    // Invoke get service
    servClient.get(INVALID_PIDCVERSCOCWP_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithNullAsPidcVers() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version CoC Work Package with ID 'null' not found");
    servClient.get(null);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#updatePidcCocWP()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdatePidcVersCocWp() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    PidcVersCocWp pidcVersCocWp = servClient.get(24987459377L);
    String usedFlagToBeUpdate = getUsedFlagToBeUpdated(pidcVersCocWp.getUsedFlag());
    pidcVersCocWp.setUsedFlag(usedFlagToBeUpdate);

    PidcVersCocWp pidcVersCocWp1 = servClient.get(25169424277L);
    String usedFlagToBeUpdate1 = getUsedFlagToBeUpdated(pidcVersCocWp1.getUsedFlag());
    pidcVersCocWp1.setUsedFlag(usedFlagToBeUpdate1);

    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    pidcCocWpUpdationInputModel.getPidcVersCocWpBeforeUpdate().put(pidcVersCocWp.getWPDivId(), pidcVersCocWp);
    pidcCocWpUpdationInputModel.getPidcVersCocWpBeforeUpdate().put(pidcVersCocWp1.getWPDivId(), pidcVersCocWp1);

    // Invoke update method
    PIDCCocWpUpdationModel updatedPidcCocWpupdateModel = servClient.updatePidcCocWPs(pidcCocWpUpdationInputModel);

    PidcVersCocWp updatedPidcVersCocWp =
        updatedPidcCocWpupdateModel.getPidcVersCocWpAfterUpdate().get(pidcVersCocWp.getWPDivId());
    assertNotNull(OBJECT_NOT_NULL, updatedPidcCocWpupdateModel);
    assertEquals(VERSION_IS_EQUAL, Long.valueOf(pidcVersCocWp.getVersion() + 1), updatedPidcVersCocWp.getVersion());

    PidcVersCocWp updatedPidcVersCocWp1 =
        updatedPidcCocWpupdateModel.getPidcVersCocWpAfterUpdate().get(pidcVersCocWp1.getWPDivId());
    assertEquals(VERSION_IS_EQUAL, Long.valueOf(pidcVersCocWp1.getVersion() + 1), updatedPidcVersCocWp1.getVersion());
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#updatePidcCocWP()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdatePidcVarCocWp() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();

    PidcVariantCocWpServiceClient varServClient = new PidcVariantCocWpServiceClient();
    PidcVariantCocWp pidcVarCocWp = varServClient.getbyId(25041655177L);
    pidcVarCocWp.setUsedFlag(getUsedFlagToBeUpdated(pidcVarCocWp.getUsedFlag()));


    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    Map<Long, PidcVariantCocWp> wpDivVarCocWpMap = new HashMap<>();
    wpDivVarCocWpMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
    pidcCocWpUpdationInputModel.getPidcVarCocWpMapBeforeUpdate().put(pidcVarCocWp.getPidcVariantId(), wpDivVarCocWpMap);

    // Invoke update method
    PIDCCocWpUpdationModel updatedPidcCocWpupdateModel = servClient.updatePidcCocWPs(pidcCocWpUpdationInputModel);
    PidcVariantCocWp updatedPidcVarCocWp = updatedPidcCocWpupdateModel.getPidcVarCocWpMapAfterUpdate()
        .get(pidcVarCocWp.getPidcVariantId()).get(pidcVarCocWp.getWPDivId());

    assertNotNull(OBJECT_NOT_NULL, updatedPidcVarCocWp);
    assertEquals(VERSION_IS_EQUAL, Long.valueOf(pidcVarCocWp.getVersion() + 1), updatedPidcVarCocWp.getVersion());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#updatePidcCocWP()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdatePidcSubVarCocWp() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();

    PidcSubVarCocWp pidcSubVarCocWp = new PidcSubVarCocWpServiceClient().getbyId(25041655927L);
    pidcSubVarCocWp.setUsedFlag(getUsedFlagToBeUpdated(pidcSubVarCocWp.getUsedFlag()));


    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    Map<Long, PidcSubVarCocWp> wpDivSubVarCocWpMap = new HashMap<>();
    wpDivSubVarCocWpMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);
    pidcCocWpUpdationInputModel.getPidcSubVarCocWpBeforeUpdateMap().put(pidcSubVarCocWp.getPidcSubVarId(),
        wpDivSubVarCocWpMap);

    // Invoke update method
    PIDCCocWpUpdationModel updatedPidcCocWpupdateModel = servClient.updatePidcCocWPs(pidcCocWpUpdationInputModel);
    PidcSubVarCocWp updatedPidcSubVarCocWp = updatedPidcCocWpupdateModel.getPidcSubVarCocWpAfterUpdateMap()
        .get(pidcSubVarCocWp.getPidcSubVarId()).get(pidcSubVarCocWp.getWPDivId());

    assertNotNull(OBJECT_NOT_NULL, updatedPidcSubVarCocWp);
    assertEquals(VERSION_IS_EQUAL, Long.valueOf(pidcSubVarCocWp.getVersion() + 1), updatedPidcSubVarCocWp.getVersion());
  }

  /**
   * @return
   */
  private String getUsedFlagToBeUpdated(final String usedFlag) {
    String usedFlagNo = CoCWPUsedFlag.NO.getDbType();
    return usedFlag.equals(usedFlagNo) ? CoCWPUsedFlag.NOT_DEFINED.getDbType() : usedFlagNo;
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVersCocWpServiceClientTest#updatePidcCocWP()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdateCocWpWithInvalidInput() throws ApicWebServiceException {

    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Invalid input for Coc WP update.");

    // Invoke updatePidcCocWP method
    servClient.updatePidcCocWPs(null);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test output data
   */
  private void testOutput(final PidcVersCocWp pidcVersCocWp) {

    assertEquals("PidcVersId is equal", PIDCVERS_ID_WITH_DIV_ID, pidcVersCocWp.getPidcVersId());
    assertEquals("UsedFlag is equal", CoCWPUsedFlag.NOT_DEFINED.getDbType(), pidcVersCocWp.getUsedFlag());
    assertTrue("IsAtChildLevel is equal", pidcVersCocWp.isAtChildLevel());
    assertNotNull("CreatedDate is not null", pidcVersCocWp.getCreatedDate());
    assertEquals("CreatedUser is equal", "DGS_ICDM", pidcVersCocWp.getCreatedUser());
  }

}
