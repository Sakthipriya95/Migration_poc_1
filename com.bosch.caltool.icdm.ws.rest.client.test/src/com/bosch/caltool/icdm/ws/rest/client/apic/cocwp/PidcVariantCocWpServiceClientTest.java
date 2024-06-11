package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PidcVersCocWp
 *
 * @author UKT1COB
 */
public class PidcVariantCocWpServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String EXCEPTION_NOT_THROWN = "Expected exception not thrown";

  /**
   *
   */
  private static final String OBJECT_NOT_NULL = "object not null";
  // link to the pidc and variant used - icdm:pidvid,25442713481 , icdm:pidvarid,25442713481-25442713492
  private static final Long PIDCVARCOCWP_ID = 27536113295L;

  private static final Long INVALID_PIDCVARCOCWP_ID = 111L;

  private static final Long VARIANT_ID = 22402126481L;

  private static final Long WP_DIV_ID = 24352597877L;


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVariantCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {

    PidcVariantCocWpServiceClient servClient = new PidcVariantCocWpServiceClient();
    PidcVariantCocWp pidcVarCocWp = servClient.getbyId(PIDCVARCOCWP_ID);
    Long variantId = 25442713492L;
    assertFalse("Response should not be null", (pidcVarCocWp == null));
    assertEquals("WpDivId is equal", WP_DIV_ID, pidcVarCocWp.getWPDivId());
    assertTrue("IsAtChildLevel is equal", pidcVarCocWp.isAtChildLevel());
    assertEquals("PidcVariantId is equal", variantId, pidcVarCocWp.getPidcVariantId());
    assertEquals("UsedFlag is equal", CoCWPUsedFlag.YES.getDbType(), pidcVarCocWp.getUsedFlag());
    assertNotNull("CreatedDate is not null", pidcVarCocWp.getCreatedDate());


  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVariantCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithInvalidPidcVers() throws ApicWebServiceException {

    PidcVariantCocWpServiceClient servClient = new PidcVariantCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant CoC Work Package with ID '" + INVALID_PIDCVARCOCWP_ID + "' not found");
    servClient.getbyId(INVALID_PIDCVARCOCWP_ID);
    fail(EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVariantCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithNullAsPidcVers() throws ApicWebServiceException {

    PidcVariantCocWpServiceClient servClient = new PidcVariantCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant CoC Work Package with ID 'null' not found");
    servClient.getbyId(null);
    fail(EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcVariantCocWpServiceClientTest#create()}
   *
   * @throws ApicWebServiceException exception
   */
  @Ignore
  @Test
  public void testCreateDelete() throws ApicWebServiceException {

    // Create
    PidcVersCocWpServiceClient servClient = new PidcVersCocWpServiceClient();
    PidcVariantCocWp pidcVarCocWp = new PidcVariantCocWp();
    pidcVarCocWp.setPidcVariantId(VARIANT_ID);
    pidcVarCocWp.setWPDivId(797220865L);
    pidcVarCocWp.setUsedFlag(CoCWPUsedFlag.NO.getDbType());
    pidcVarCocWp.setAtChildLevel(false);

    Map<Long, PidcVariantCocWp> pidcVarCreationMap = new HashMap<>();
    pidcVarCreationMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarMap = new HashMap<>();
    pidcVarMap.put(VARIANT_ID, pidcVarCreationMap);


    PidcVariantCocWp pidcVarCocWpToBeDel = new PidcVariantCocWpServiceClient().getbyId(25041656028L);
    Map<Long, PidcVariantCocWp> pidcVarDelMap = new HashMap<>();
    pidcVarDelMap.put(pidcVarCocWpToBeDel.getWPDivId(), pidcVarCocWpToBeDel);
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarDelMap1 = new HashMap<>();
    pidcVarDelMap1.put(pidcVarCocWpToBeDel.getPidcVariantId(), pidcVarDelMap);


    PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
    pidcCocWpUpdationInputModel.setPidcVarCocWpCreationMap(pidcVarMap);
    pidcCocWpUpdationInputModel.setPidcVarCocWpDeletionMap(pidcVarDelMap1);

    PIDCCocWpUpdationModel updatedModel = servClient.updatePidcCocWPs(pidcCocWpUpdationInputModel);

    PidcVariantCocWp createdPidcVarCocWp = updatedModel.getPidcVarCocWpCreationMap().get(VARIANT_ID).get(797220865L);
    assertNotNull(OBJECT_NOT_NULL, createdPidcVarCocWp);
    assertEquals("PidcVariantId is equal", VARIANT_ID, pidcVarCocWp.getPidcVariantId());
    assertFalse("IsAtChildLevel is equal", pidcVarCocWp.isAtChildLevel());
    Optional.ofNullable(createdPidcVarCocWp).ifPresent(this::testOutput);


    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PidcVariantCocWp with ID '25041656028' not found");
    new PidcVariantCocWpServiceClient().getbyId(25041656028L);
    fail(EXCEPTION_NOT_THROWN);
  }


  /**
   * test output data
   */
  private void testOutput(final PidcVariantCocWp pidcVarCocWp) {

    assertEquals("UsedFlag is equal", CoCWPUsedFlag.NO.getDbType(), pidcVarCocWp.getUsedFlag());
    assertNotNull("CreatedDate is not null", pidcVarCocWp.getCreatedDate());
    assertEquals("CreatedUser is equal", "DGS_ICDM", pidcVarCocWp.getCreatedUser());
  }

}
