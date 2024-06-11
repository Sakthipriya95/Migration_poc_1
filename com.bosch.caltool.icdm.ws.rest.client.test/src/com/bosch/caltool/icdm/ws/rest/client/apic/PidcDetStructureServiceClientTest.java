package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PidcDetailsStructure
 *
 * @author AND4COB
 */
public class PidcDetStructureServiceClientTest extends AbstractRestClientTest {

  private final static Long PIDCDETSTRUCTURE_ID = 782028945L;
  private final static Long INVALID_PDS_ID = -100L;
  private final static Long PIDC_VERS_ID = 782020770L;
  private final static Long PIDC_ATTR_LEVEL = 1L;


  /**
   * Test method for {@link PidcDetStructureServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PidcDetStructureServiceClient servClient = new PidcDetStructureServiceClient();
    PidcDetStructure pidcDetStructure = servClient.getById(PIDCDETSTRUCTURE_ID);
    assertNotNull("Response should not be null", (pidcDetStructure == null));
    testPidcDetStructure(pidcDetStructure);
  }

  /**
   * Negative Test method for {@link PidcDetStructureServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PidcDetStructureServiceClient servClient = new PidcDetStructureServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PidcDetailsStructure with ID '" + INVALID_PDS_ID + "' not found");
    servClient.getById(INVALID_PDS_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link PidcDetStructureServiceClient#getPidcDetStructForVersion(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcDetStructForVersion() throws ApicWebServiceException {
    PidcDetStructureServiceClient servClient = new PidcDetStructureServiceClient();
    Map<Long, PidcDetStructure> retMap = servClient.getPidcDetStructForVersion(PIDC_VERS_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    PidcDetStructure pidcDetStructure = retMap.get(PIDC_ATTR_LEVEL);
    testPidcDetStructure(pidcDetStructure);
  }

  /**
   * @param pidcDetStructure
   */
  private void testPidcDetStructure(final PidcDetStructure pidcDetStructure) {
    assertEquals("Pidc Attribute Level is equal", Long.valueOf(1), pidcDetStructure.getPidAttrLevel());
    assertEquals("Attr_Id is equal", Long.valueOf(258), pidcDetStructure.getAttrId());
    assertEquals("Created User is equal", "HEF2FE", pidcDetStructure.getCreatedUser());
    assertNotNull("Created Date is not null", pidcDetStructure.getCreatedDate());
  }


  /**
   * Test method for {@link PidcDetStructureServiceClient#create(PidcDetStructure)},
   * {@link PidcDetStructureServiceClient#update(PidcDetStructure)},
   * {@link PidcDetStructureServiceClient#delete(PidcDetStructure)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateDeletePidcDetStr() throws ApicWebServiceException {
    PidcDetStructureServiceClient servClient = new PidcDetStructureServiceClient();

    PidcDetStructure pidcDetStructure = new PidcDetStructure();
    pidcDetStructure.setPidcVersId(1520294028L);
    pidcDetStructure.setAttrId(973L);
    pidcDetStructure.setPidAttrLevel(2L);

    // invoke create method
    PidcDetStructure createdObj = servClient.create(pidcDetStructure);

    // validate create
    assertNotNull("Created object is not null", createdObj);
    assertEquals("Pidc version id is equal", Long.valueOf(1520294028), createdObj.getPidcVersId());
    assertEquals("Attribute Id is equal", Long.valueOf(973), createdObj.getAttrId());

    // invoke update method
    createdObj.setAttrId(2459L);
    PidcDetStructure updatedObj = servClient.update(createdObj);

    // validate update
    assertEquals("Attribute Id after update", Long.valueOf(2459), updatedObj.getAttrId());

    // invoke delete
    servClient.delete(updatedObj);
  }

}
