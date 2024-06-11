/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author HNU1COB
 */
public class RvwFileServiceClientTest extends AbstractRestClientTest {

  private final static Long RESULT_ID = 769458878l;
  private final static Long INV_RESULT_ID = -769458878l;
  private static Long RVW_FILE_ID[] = { 766292008l, 766292005l, 766292014l };
  private final static Set<Long> RVW_FILE_ID_SET = new HashSet<>(Arrays.asList(RVW_FILE_ID));
  private static Long INV_RVW_FILE_ID[] = { -766292008l, -766292005l, -766292014l };
  private final static Set<Long> INV_RVW_FILE_ID_SET = new HashSet<>(Arrays.asList(INV_RVW_FILE_ID));


  /**
   * Test method for {@link RvwFileServiceClient#getByResultId(Long)}. input: result_id , output: Map<RvwFile Id ,
   * RvwFile>
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByResultId() throws ApicWebServiceException {
    RvwFileServiceClient client = new RvwFileServiceClient();
    Map<Long, RvwFile> retMap = client.getByResultId(RESULT_ID);
    assertFalse("Response should not be null or empty", retMap.isEmpty());
    LOG.info("Number of review files loaded : {}", retMap.size());
  }

  /**
   * Test method for {@link RvwFileServiceClient#getByResultId(Long)}. Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByResultIdNegative() throws ApicWebServiceException {
    RvwFileServiceClient client = new RvwFileServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    client.getByResultId(INV_RESULT_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link RvwFileServiceClient#create(RvwFile)}, {@link RvwFileServiceClient#delete(Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {

    // create
    RvwFile rvwFile = new RvwFile();
    rvwFile.setResultId(760231516L);
    rvwFile.setName("SSD_Review_CUSTOM_REVIEW_.err");
    rvwFile.setFileType(CDRConstants.REVIEW_FILE_TYPE.RVW_ADDL_FILE.getDbType());
    rvwFile.setNodeType(MODEL_TYPE.CDR_RESULT.getTypeCode());
    rvwFile.setFilePath(CommonUtils.getSystemUserTempDirPath() + File.separator + "SSD_Review_CUSTOM_REVIEW_.err");
    RvwFileServiceClient client = new RvwFileServiceClient();
    RvwFile createdFile = client.create(rvwFile);
    LOG.info(" File downloaded : {}", createdFile);
    // validate create
    assertNotNull("Created object should not be null", createdFile);
    assertEquals("Result Id is equal", Long.valueOf(760231516), createdFile.getResultId());
    // delete
    client.delete(new HashSet<>(client.getMultiple(new HashSet<>(Arrays.asList(createdFile.getId()))).values()));

  }

  /**
   * Test method for {@link RvwFileServiceClient#downloadEmrFile(Long,String,String)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testDownloadEmrFile() throws ApicWebServiceException {
    RvwFileServiceClient client = new RvwFileServiceClient();
    String file =
        client.downloadEmrFile(766292006L, "SSD_Review_CUSTOM_REVIEW_.err", CommonUtils.getSystemUserTempDirPath());
    LOG.info(" File downloaded : {}", file);
    assertNotNull("Response should not  be null", file);
  }

  /**
   * Test method for {@link RvwFileServiceClient#getMultiple(Set)}. input: review_file_id , output: Map<RvwFile Id ,
   * RvwFile>
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMultiple() throws ApicWebServiceException {
    RvwFileServiceClient client = new RvwFileServiceClient();
    Map<Long, RvwFile> retMap = client.getMultiple(RVW_FILE_ID_SET);
    assertFalse("Response should not be null or empty", retMap.isEmpty());
    LOG.info("Number of review files loaded : {}", retMap.size());
  }


  /**
   * Test method for {@link RvwFileServiceClient#getMultiple(Set)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMultipleNegative() throws ApicWebServiceException {
    RvwFileServiceClient client = new RvwFileServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review File with ID ");
    client.getMultiple(INV_RVW_FILE_ID_SET);
    fail("Expected exception not thrown");
  }
}
