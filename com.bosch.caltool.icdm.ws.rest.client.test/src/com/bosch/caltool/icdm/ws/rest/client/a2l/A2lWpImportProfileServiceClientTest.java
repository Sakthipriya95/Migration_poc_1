package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lImportProfileDetails;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lWpImportProfile
 *
 * @author and4cob
 */
public class A2lWpImportProfileServiceClientTest extends AbstractRestClientTest {

  private static final Long IMPORT_PROFILE_ID = 5177315427L;


  /**
   * Test method for {@link A2lWpImportProfileServiceClient#getAll()}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    A2lWpImportProfileServiceClient servClient = new A2lWpImportProfileServiceClient();
    Map<Long, A2lWpImportProfile> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    A2lWpImportProfile a2lWpImportProfile = retMap.get(IMPORT_PROFILE_ID);
    testA2lImportProfile(a2lWpImportProfile);
  }

  /**
   * test output data
   */
  private void testA2lImportProfile(final A2lWpImportProfile a2lWpImportProfile) {
    assertEquals("ProfileName is equal", "P.A.L. Excel File (Parameter based)", a2lWpImportProfile.getProfileName());
    assertEquals("ProfileOrder is equal", Long.valueOf(1), a2lWpImportProfile.getProfileOrder());
    testImportProfileDetails(a2lWpImportProfile.getProfileDetails());
    assertNotNull("CreatedDate is not null", a2lWpImportProfile.getCreatedDate());
    assertEquals("CreatedUser is equal", "DGS_ICDM", a2lWpImportProfile.getCreatedUser());
  }

  /**
   * @param profileDetails
   */
  private void testImportProfileDetails(final A2lImportProfileDetails profileDetails) {
    assertEquals("HeadingRowNum is equal", Long.valueOf(2), profileDetails.getHeadingRowNum());
    assertEquals("ImportMode is equal", "P", profileDetails.getImportMode());
    assertEquals("LabelColumn is equal", "label name", profileDetails.getLabelColumn());
    assertEquals("PrefixForResp is equal", "_RESP__", profileDetails.getPrefixForResp());
    assertEquals("PrefixForWp is equal", "_WP__", profileDetails.getPrefixForWp());
    assertEquals("RespColumn is equal", "responsibilities", profileDetails.getRespColumn());
    assertEquals("SheetName is equal", "PAL", profileDetails.getSheetName());
    assertEquals("WPColumn is equal", "workpackage", profileDetails.getWpColumn());
    assertEquals("FileType is equal", "Excel", profileDetails.getFileType().getType());
  }

}
