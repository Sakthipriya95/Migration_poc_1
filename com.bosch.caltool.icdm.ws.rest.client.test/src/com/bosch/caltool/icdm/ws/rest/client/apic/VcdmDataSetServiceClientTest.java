/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author EMS4KOR
 */
public class VcdmDataSetServiceClientTest extends AbstractRestClientTest {

  private static final Long PIDC_ID = 774555767L;
  private static final Long INVALID_PIDC_ID = -1L;
  private static final Long EASEEDST_ID = 20065034L;
  private static final String SDOM_PVER_NAME = "D171XA07C000";
  private static final String SDOM_PVER_VAR = "MY12I00";
  private static final String SDOM_PVER_REVISION = "0";
  private static final String INVALID_SDOM_PVER_NAME = "-1";
  private static final String INVALID_SDOM_PVER_VAR = "-1";
  private static final String INVALID_SDOM_PVER_REVISION = "-1";
  private static final String PRODUCT_KEY = "A41IVA";
  private static final String PROGRAM_KEY = "D171XA07C000_MY12I00";
  private static final BigDecimal REVISION_NO = new BigDecimal("0");


  /**
   *
   */
  private static final String LOG_MSG_RESP_NOT_NULL = "Response should not be null";


  /**
   * Test method for {@link VcdmDataSetServiceClient#getStatisticsByPidcId(Long, int)}
   *
   * @throws ApicWebServiceException web service error
   */

  @Test
  public void testGetStatisticsByPidcId() throws ApicWebServiceException {
    Set<VcdmDataSet> retSet = new VcdmDataSetServiceClient().getStatisticsByPidcId(PIDC_ID, 0);

    assertNotNull(LOG_MSG_RESP_NOT_NULL, retSet);

    boolean available = false;

    for (VcdmDataSet vcdmDataSet : retSet) {
      if (vcdmDataSet.getId().equals(EASEEDST_ID)) {
        testOutput(vcdmDataSet);
        available = true;
        break;
      }

    }

    assertTrue("ID must be available", available);

  }

  /**
   * Test method for {@link VcdmDataSetServiceClient#getStatisticsByPidcId(Long,int)}
   *
   * @throws ApicWebServiceException web service error
   */

  @Test
  public void testGetStatisticsByPidcIdNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    new VcdmDataSetServiceClient().getStatisticsByPidcId(INVALID_PIDC_ID, 0);
    fail("Expected Exception not thrown");

  }

  /**
   * Test method for {@link VcdmDataSetServiceClient#getDataSet(String,String,String)}
   *
   * @throws ApicWebServiceException web error
   */


  @Test
  public void testGetDataSet() throws ApicWebServiceException {
    Set<VCDMApplicationProject> retSet =
        new VcdmDataSetServiceClient().getDataSet(SDOM_PVER_NAME, SDOM_PVER_VAR, SDOM_PVER_REVISION);

    assertNotNull(LOG_MSG_RESP_NOT_NULL, retSet);

    assertFalse("Application projects not empty", retSet.isEmpty());
    VCDMApplicationProject vcdmAppProject = retSet.iterator().next();
    assertEquals("Element id must be equal", "005-EA888-B8-LAENGS", vcdmAppProject.getAprjName());

    Map<String, VCDMProductKey> vcdmVariants = vcdmAppProject.getVcdmVariants();
    VCDMProductKey vcdmVariant = vcdmVariants.get(PRODUCT_KEY);
    assertNotNull(LOG_MSG_RESP_NOT_NULL, vcdmVariant);
    assertEquals("Variant Name must be equal", PRODUCT_KEY, vcdmVariant.getVariantName());

    Map<String, VCDMProgramkey> programKeys = vcdmVariant.getProgramKeys();
    VCDMProgramkey programKey = programKeys.get(PROGRAM_KEY);
    assertNotNull(LOG_MSG_RESP_NOT_NULL, programKey);
    assertEquals("ProgramKey Name must be equal", PROGRAM_KEY, programKey.getProgramKeyName());

    Map<BigDecimal, VCDMDSTRevision> vCDMDSTRevisions = programKey.getvCDMDSTRevisions();
    VCDMDSTRevision vCDMDSTRevision = vCDMDSTRevisions.get(REVISION_NO);
    assertNotNull("Response should not be null or empty", vCDMDSTRevision);
    testVCDMDSTRevision(vCDMDSTRevision);
  }


  /**
   * Test method for testGetDataSetNegative
   *
   * @throws ApicWebServiceException web service error
   */


  @Test
  public void testGetDataSetNegative() throws ApicWebServiceException {
    Set<VCDMApplicationProject> dataSet = new VcdmDataSetServiceClient().getDataSet(INVALID_SDOM_PVER_NAME,
        INVALID_SDOM_PVER_VAR, INVALID_SDOM_PVER_REVISION);

    assertNotNull(LOG_MSG_RESP_NOT_NULL, dataSet);
    assertTrue("Application project is empty", dataSet.isEmpty());
  }

  /**
   * Test output Data
   */
  private void testOutput(final VcdmDataSet data) {
    assertEquals("AprjId must be equal", "1682133", data.getAprjId().toString());
    assertEquals("aprjName must be equal", "003-P1274_EA288", data.getAprjName());
    assertEquals("Product Key must be equal", "UBCJ", data.getProductKey());
    assertEquals("Program Key must be equal", "DN958_04L906026EH_3620", data.getProgramKey());
    assertEquals("Revision No must be equal", "1", data.getRevisionNo().toString());
    assertEquals("Checked Labels must be equal", "0", data.getCheckedLabels().toString());
    assertEquals("Created User must be same", "K5ESK_VILLA", data.getCreatedUser());
    assertEquals("Created Date must be same", "23-Sep-2016",
        DateFormat.formatDateToString(data.getCreatedDate(), DateFormat.DATE_FORMAT_09));
  }

  /**
   * @param vCDMDSTRevision
   */
  private void testVCDMDSTRevision(final VCDMDSTRevision vCDMDSTRevision) {
    assertEquals("DstId must be equal", Long.valueOf("16692050"), vCDMDSTRevision.getDstID());
    assertEquals("Version Name must be equal", "A41IVA.D171XA07C000_MY12I00", vCDMDSTRevision.getVersionName());
    assertEquals("Created Date must be equal", "06-Jun-2013",
        DateFormat.formatDateToString(vCDMDSTRevision.getCreatedDate(), DateFormat.DATE_FORMAT_09));
    assertEquals("RevisionNo must be equal", "0", vCDMDSTRevision.getRevisionNo().toString());
  }


  /**
   * Test method for {@link VcdmDataSetServiceClient#loadHexFile(long, String) }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testloadHexFile() throws ApicWebServiceException {
    String path = new VcdmDataSetServiceClient().loadHexFile(16692050L, CommonUtils.getICDMTmpFileDirectoryPath());
    LOG.info("file path {}", path);
    assertFalse("Response should not be null or empty", (path == null) || (path.length() == 0));
  }

  /**
   * Negative Test method for {@link VcdmDataSetServiceClient#loadHexFile(long, String) } <br>
   * Uses an invalid destination path
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testloadHexFileNagative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Error while downloading file A57HXEMY12I00RB01_16692050.hex : ####\\A57HXEMY12I00RB01_16692050.hex (The system cannot find the path specified)");
    new VcdmDataSetServiceClient().loadHexFile(16692050, "####");
    fail("Expected exception not thrown");

  }


}
