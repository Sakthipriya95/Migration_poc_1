/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class A2LFileInfoServiceClientTest extends AbstractRestClientTest {


  /**
   * <code>
   * (from TA2L_FILEINFO) :
   * FILENAME = DB840R4000000.A2L
   * PROJECTNAME= P866
   * SDOM_PVER_NAME = M1764VDBC866
   * SDOM_PVER_VARIANT = M8400
   * SDOM_PVER_VERSID = 17645038
   *
   * Sample Mapping to PIDC :
   * A2L File: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_ICDM_RBEI_01 (Version 1)->M1764VDBC866->DB840R4000000.A2L
   * icdm:a2lid,774402427-679409131
   *
   * </code>
   */
  private static final long A2L_FILE_ID_DOWNLOAD_SER = 679409131L;

  private static final String BC_NAME = "CB";
  private static final String INVALID_BC_NAME = "INVALID_BC_NAME";

  private static final String FC_NAME = "AC_DataAcq";
  private static final String INVALID_FC_NAME = "INVALID_FC_NAME";

  private static final Long VCDM_A2LFILE_ID = 18853612L;

  private static final String SYSKON_NAME = "SY_BBBO";

  private static final Long A2LFILE_ID = 768766884L;
  private static final Long INVALID_A2LFILE_ID = -100L;

  private static final String A2L_CHECKSUM = "819194184BCACE9636379669C3702223";
  private static final String INVALID_A2L_CHECKSUM = "INVALID_A2L_CHECKSUM";

  private static final String PVER_NAME = "HON1793A1";

  /**
   * A2L file Id - icdm:a2lid,22134770597-335248984<br>
   * A2L file assigned to a new pidc and vcdm file id set to null for testing
   */
  private static final Long A2L_FILE_ID_VCDM_FILE_ID_NULL = 335248984L;


  /**
   * Test method for {@link A2LFileInfoServiceClient#getAllA2LSysConstants()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllA2LSysConstants() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    Map<String, A2LSystemConstant> retMap = servClient.getAllA2LSysConstants();
    assertFalse("Response should not be null or empty", (retMap == null) || retMap.isEmpty());
    A2LSystemConstant a2lSystemConstant = retMap.get(SYSKON_NAME);
    assertNotNull("response should not be null", a2lSystemConstant);
    testA2LSysConstant(a2lSystemConstant);
  }

  /**
   * @param a2lSystemConstant
   */
  private void testA2LSysConstant(final A2LSystemConstant a2lSystemConstant) {
    assertEquals("Name is equal", "Activation of detection of fuel in oil", a2lSystemConstant.getLongName());
    assertEquals("English Description is equal", "Activation of detection of fuel in oil",
        a2lSystemConstant.getLongNameEng());
    assertEquals("German Description is equal", "Aktivierung Erkennung Kraftstoff im Öl",
        a2lSystemConstant.getLongNameGer());
  }


  /**
   * Test method for {@link A2LFileInfoServiceClient#getA2LBaseComponents(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LBaseComponents() throws ApicWebServiceException {
    A2LFileInfoServiceClient serviceClient = new A2LFileInfoServiceClient();
    Map<String, A2LBaseComponents> retMap = serviceClient.getA2LBaseComponents(A2LFILE_ID);
    assertFalse("Response should not be null or empty", (retMap == null) || retMap.isEmpty());
    A2LBaseComponents a2lBaseComponent = retMap.get(BC_NAME);// for A2LFILE_ID = 768766884, BC_NAME = "CB":
                                                             // Version = 233.1.0 = VARIANT in DB
    assertNotNull("Response should not be null", a2lBaseComponent);
    testA2LBaseComponets(a2lBaseComponent);
  }

  /**
   * @param a2lBaseComponent
   */
  private void testA2LBaseComponets(final A2LBaseComponents a2lBaseComponent) {
    assertEquals("Name is equal", "CB", a2lBaseComponent.getBcName());
    assertEquals("Version is equal", "233.1.0", a2lBaseComponent.getBcVersion());
    assertEquals("State is equal", "INCOMPLETE", a2lBaseComponent.getState());
  }

  /**
   * Negative Test method for {@link A2LFileInfoServiceClient#getA2LBaseComponents(Long)}. (For some wrong A2LFILE_ID)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LBaseComponentsNegative() throws ApicWebServiceException {
    A2LFileInfoServiceClient serviceClient = new A2LFileInfoServiceClient();
    Map<String, A2LBaseComponents> retMap = serviceClient.getA2LBaseComponents(INVALID_A2LFILE_ID);
    assertTrue("Response should be null or empty", (retMap == null) || retMap.isEmpty());
  }


  /**
   * Test method for {@link A2LFileInfoServiceClient#getBCUsage(String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetBCUsage() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    List<FCBCUsage> fcbcUsageList = servClient.getBCUsage(BC_NAME);
    assertFalse("Response should not empty", fcbcUsageList.isEmpty());
    LOG.info("Size of items in FCBCUsageView: {}", fcbcUsageList.size());

  }

  /**
   * Negative Test method for {@link A2LFileInfoServiceClient#getBCUsage(String)}. (For some invalid BC name)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetBCUsageNegative() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    List<FCBCUsage> fcbcUsageList = servClient.getBCUsage(INVALID_BC_NAME);
    assertTrue("Response should be empty", fcbcUsageList.isEmpty());
  }


  /**
   * Test method for {@link A2LFileInfoServiceClient#getFCUsage(String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFCUsage() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    List<FCBCUsage> fcbcUsageList = servClient.getFCUsage(FC_NAME);
    assertFalse("Response should not empty", fcbcUsageList.isEmpty());
    LOG.info("Size of items in FCBCUsageView: {}", fcbcUsageList.size());
  }

  /**
   * Negative Test method for {@link A2LFileInfoServiceClient#getFCUsage(String)}. (For some invalid FC name)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFCUsageNegative() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    List<FCBCUsage> fcbcUsageList = servClient.getBCUsage(INVALID_FC_NAME);
    assertTrue("Response should be empty", fcbcUsageList.isEmpty());
  }


  /**
   * Test method for {@link A2LFileInfoServiceClient#getVcdmDataSets(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetVcdmDataSets() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    List<VCDMApplicationProject> datasets = servClient.getVcdmDataSets(VCDM_A2LFILE_ID);
    assertFalse("Response should not be empty", datasets.isEmpty());
    LOG.info("Size of items: {}", datasets.size());
    VCDMApplicationProject vcdmApplicationProject = datasets.get(0); // there can be only one entry for a particular
                                                                     // vCDM A2L FileID !
    testVCDMApplicationProject(vcdmApplicationProject);
  }

  /**
   * @param vcdmApplicationProject
   */
  private void testVCDMApplicationProject(final VCDMApplicationProject vcdmApplicationProject) {
    assertEquals("Aprj Name is equal", "159-HONDA_XDRA_4CYL_2.0L_GDI_TC", vcdmApplicationProject.getAprjName());
  }

  /**
   * Test method for {@link A2LFileInfoServiceClient#getVCDMA2LFileDetails(String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetVCDMA2LFileDetails() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    Set<VCDMA2LFileDetail> retSet = servClient.getVCDMA2LFileDetails(A2L_CHECKSUM);
    assertFalse("Response should not be empty", retSet.isEmpty());
    LOG.info("Size: {}", retSet.size());

    boolean a2lAvailable = false;
    for (VCDMA2LFileDetail vcdma2lFileDetail : retSet) {
      if (vcdma2lFileDetail.getOriginalFileName().equals("MED41-15B-FRG_050_00-STAR23-xx-M26Xxx.a2l")) {
        a2lAvailable = true;
        testVCDmA2LFileDetail(vcdma2lFileDetail);
        break;
      }
    }
    assertTrue("A2LFile is available", a2lAvailable);
  }

  /**
   * @param vcdma2lFileDetail
   */
  private void testVCDmA2LFileDetail(final VCDMA2LFileDetail vcdma2lFileDetail) {
    assertEquals("PST is equal", "D177_4A_0205D", vcdma2lFileDetail.getPst());
    assertEquals("Original date is equal", "2015-09-03 21:18:54 000", vcdma2lFileDetail.getOriginalDate());
  }


  /**
   * Negative Test method for {@link A2LFileInfoServiceClient#getVCDMA2LFileDetails(String)}.(For invalid a2l checksum)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetVCDMA2LFileDetailsNegative() throws ApicWebServiceException {
    A2LFileInfoServiceClient servClient = new A2LFileInfoServiceClient();
    Set<VCDMA2LFileDetail> retSet = servClient.getVCDMA2LFileDetails(INVALID_A2L_CHECKSUM);
    assertTrue("Response should be empty", retSet.isEmpty());
  }


  /**
   * Test method for {@link A2LFileInfoServiceClient#getA2LFilePVERVars(String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFilePVERVars() throws ApicWebServiceException {
    A2LFileInfoServiceClient serviceClient = new A2LFileInfoServiceClient();
    SortedSet<String> varSet = serviceClient.getA2LFilePVERVars(PVER_NAME);
    LOG.info("Size :{}", varSet.size());
    boolean pverAvailable = false;
    for (String pver : varSet) {
      if (pver.equals("I100R1")) {
        pverAvailable = true;
        break;
      }
    }
    assertTrue("PVER is available", pverAvailable);
  }

  /**
   * Test method for {@link A2LFileInfoServiceClient#getA2LFileInfoSerialized(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileInfoSerialized() throws ApicWebServiceException {

    String serFileName = A2L_FILE_ID_DOWNLOAD_SER + "_" + getRunId() + ".ser";
    String a2lFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + serFileName;

    new A2LFileInfoServiceClient().getA2LFileInfoSerialized(A2L_FILE_ID_DOWNLOAD_SER, serFileName,
        CommonUtils.getSystemUserTempDirPath());

    assertTrue("A2L file downloaded successfully", new File(a2lFilePath).exists());
  }

  /**
   * Test method for {@link A2LFileInfoServiceClient#getA2LFileInfoSerialized(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileInfoSerializedInvalidId() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L File with ID \'" + INVALID_A2LFILE_ID + "\' not found");

    String serFileName = INVALID_A2LFILE_ID + "_" + getRunId() + ".ser";

    new A2LFileInfoServiceClient().getA2LFileInfoSerialized(INVALID_A2LFILE_ID, serFileName,
        CommonUtils.getSystemUserTempDirPath());
  }

  /**
   * Test method for {@link A2LFileInfoServiceClient#getA2LFileInfoSerialized(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileInfoSerializedVcdmA2lFileIdNull() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Incomplete vCDM file information in iCDM for A2L file ID : 335.248.984. Please contact iCDM hotline.");

    String serFileName = A2L_FILE_ID_VCDM_FILE_ID_NULL + "_" + getRunId() + ".ser";

    new A2LFileInfoServiceClient().getA2LFileInfoSerialized(A2L_FILE_ID_VCDM_FILE_ID_NULL, serFileName,
        CommonUtils.getSystemUserTempDirPath());
  }
}
