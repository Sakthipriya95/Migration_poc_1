package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Focus Matrix Version
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionServiceClientTest extends AbstractRestClientTest {

  private final static Long FOCUSMATRIXVERSION_ID = 1503185335L;
  /**
   * Constant for pidc version id
   */
  private final static Long PIDC_VERSION_ID = 1503179032L;

  private final static Long INVALID_ID = -100L;


  /**
   * Test method for {@link FocusMatrixVersionServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient servClient = new FocusMatrixVersionServiceClient();

    FocusMatrixVersion focusMatrixVersion = servClient.getById(FOCUSMATRIXVERSION_ID);
    assertNotNull("Response should not be null", focusMatrixVersion);
    testFocusMatrixVersion(focusMatrixVersion);

  }

  /**
   * Negative Test method for {@link FocusMatrixVersionServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient servClient = new FocusMatrixVersionServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Focus Matrix Version with ID '" + INVALID_ID + "' not found");
    servClient.getById(INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @param focusMatrixVersion
   */

  private void testFocusMatrixVersion(final FocusMatrixVersion focusMatrixVersion) {
    assertEquals("Pidc Version Id is equal", Long.valueOf(1503179032L), focusMatrixVersion.getPidcVersId());
    assertEquals("Review Status is equal", "Y", focusMatrixVersion.getRvwStatus());
    assertEquals("Status is equal", "O", focusMatrixVersion.getStatus());
  }

  /**
   * Test method for {@link FocusMatrixVersionServiceClient#update(FocusMatrixVersion)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient servClient = new FocusMatrixVersionServiceClient();

    FocusMatrixVersion focusMatrixVersion = getFocusMatrixVersionObj(servClient);
    // invoke update
    FocusMatrixVersion updatedFocusMatrixVersion = servClient.update(focusMatrixVersion);
    // validate update
    validateFocusMatrixVersionObj(updatedFocusMatrixVersion);
  }

  /**
   * @param updatedFocusMatrixVersion
   */
  private void validateFocusMatrixVersionObj(final FocusMatrixVersion focusMatrixVersion) {
    assertEquals("Name is equal", "JUnit_" + getRunId(), focusMatrixVersion.getName());
    assertEquals("Link is equal", "JUnit_TestLink_" + getRunId(), focusMatrixVersion.getLink());
    assertEquals("Remark is equal", "JUnit_TestRemark_" + getRunId(), focusMatrixVersion.getRemark());
  }

  /**
   * @param servClient
   * @return
   * @throws ApicWebServiceException
   */
  private FocusMatrixVersion getFocusMatrixVersionObj(final FocusMatrixVersionServiceClient servClient)
      throws ApicWebServiceException {
    FocusMatrixVersion focusMatrixVersion = servClient.getById(FOCUSMATRIXVERSION_ID);
    focusMatrixVersion.setName("JUnit_" + getRunId());
    focusMatrixVersion.setLink("JUnit_TestLink_" + getRunId());
    focusMatrixVersion.setRemark("JUnit_TestRemark_" + getRunId());
    return focusMatrixVersion;
  }

  /**
   * Test method for getting focus matrix versions for pidc version
   * {@link FocusMatrixVersionServiceClient#getFocusMatrixVersionForPidcVers(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFocusMatrixVersionForPidcVers() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient servClient = new FocusMatrixVersionServiceClient();

    Map<Long, FocusMatrixVersion> focusMatrixVersionsMap = servClient.getFocusMatrixVersionForPidcVers(PIDC_VERSION_ID);
    assertFalse("Response should not be null or empty",
        ((focusMatrixVersionsMap == null) || focusMatrixVersionsMap.isEmpty()));
    LOG.info("Size : {}", focusMatrixVersionsMap.size());
    FocusMatrixVersion focusMatrixVersion = focusMatrixVersionsMap.get(FOCUSMATRIXVERSION_ID);
    testFocusMatrixVersion(focusMatrixVersion);
  }

  /**
   * Test method for creating and deleting focus matrix versions
   * {@link FocusMatrixVersionServiceClient#create(Object) }.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient serviceClient = new FocusMatrixVersionServiceClient();
    FocusMatrixVersion newFocusMatrixVersion = serviceClient.create(getFocusMatrixVersionObj(serviceClient));
    // To validate created Objects - create() method
    validateFocusMatrixVersionObj(newFocusMatrixVersion);
    LOG.info("create method test completed");

    FocusMatrixVersionAttrServiceClient attrSrvcClient = new FocusMatrixVersionAttrServiceClient();
    Map<Long, FocusMatrixVersionAttr> attributes = new HashMap<>();
    Long focusMatrxVersnId = newFocusMatrixVersion.getId();
    try {
      // Fetch all attrbute records for the created focus matrix
      attributes = attrSrvcClient.getFocusMatrixAttrForVersion(focusMatrxVersnId);
      // Delete all child attribute records for the created focus matrix
      if (CommonUtils.isNotEmpty(attributes)) {
        for (Entry<Long, FocusMatrixVersionAttr> entry : attributes.entrySet()) {
          Long objectID = entry.getValue().getId();
          attrSrvcClient.delete(objectID);
        }
      }
    }
    catch (Exception excep) {
      LOG.error("Error while fetching Focus Matric Version Attributes");
    }
    LOG.info("Attributes deleted successfully");

    // Delete the created Focus Matrix version - delete() method
    serviceClient.delete(focusMatrxVersnId);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Focus Matrix Version with ID '" + focusMatrxVersnId + "' not found");
    serviceClient.getById(focusMatrxVersnId);
    LOG.info("Delete method test completed");
  }


}
