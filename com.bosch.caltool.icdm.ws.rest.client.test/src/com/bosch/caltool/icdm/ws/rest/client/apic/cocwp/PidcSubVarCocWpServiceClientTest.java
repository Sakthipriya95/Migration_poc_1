package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PidcSubVarCocWp
 *
 * @author UKT1COB
 */
public class PidcSubVarCocWpServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String EXCEPTION_NOT_THROWN = "Expected exception not thrown";

  /**
   * link to the pidc and variant and subvariant used 
   * icdm:pidvid,25442713481 , icdm:pidvarid,25442713481-25442713492, icdm:pidcsubvarid,25442713481-25442713499
   */

  private static final Long PIDCSUBVARCOCWP_ID = 27536113298L;

  private static final Long INVALID_PIDCSUBVARCOCWP_ID = 111L;

  private static final Long SUB_VARIANT_ID = 25442713499L;

  private static final Long WP_DIV_ID = 24352597877L;


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcSubVarCocWpServiceClientTest#get()}
   */
  @Test
  public void testGet() throws ApicWebServiceException {

    PidcSubVarCocWpServiceClient servClient = new PidcSubVarCocWpServiceClient();
    PidcSubVarCocWp pidcSubVarCocWp = servClient.getbyId(PIDCSUBVARCOCWP_ID);
    assertFalse("Response should not be null", (pidcSubVarCocWp == null));
    assertEquals("WpDivId is equal", WP_DIV_ID, pidcSubVarCocWp.getWPDivId());

    Optional.ofNullable(pidcSubVarCocWp).ifPresent(this::testOutput);
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcSubVarCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithInvalidPidcVers() throws ApicWebServiceException {

    PidcSubVarCocWpServiceClient servClient = new PidcSubVarCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown
        .expectMessage("PIDC Sub Variant CoC Work Package with ID '" + INVALID_PIDCSUBVARCOCWP_ID + "' not found");
    servClient.getbyId(INVALID_PIDCSUBVARCOCWP_ID);
    fail(EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.apic.PidcSubVarCocWpServiceClientTest#get()}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWithNullAsPidcVers() throws ApicWebServiceException {

    PidcSubVarCocWpServiceClient servClient = new PidcSubVarCocWpServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Sub Variant CoC Work Package with ID 'null' not found");
    servClient.getbyId(null);
    fail(EXCEPTION_NOT_THROWN);
  }


  /**
   * test output data
   */
  private void testOutput(final PidcSubVarCocWp pidcSubVarCocWp) {

    assertEquals("PidcSubVarId is equal", SUB_VARIANT_ID, pidcSubVarCocWp.getPidcSubVarId());
    assertEquals("UsedFlag is equal", CoCWPUsedFlag.YES.getDbType(), pidcSubVarCocWp.getUsedFlag());
    assertNotNull("CreatedDate is not null", pidcSubVarCocWp.getCreatedDate());
  }

}
