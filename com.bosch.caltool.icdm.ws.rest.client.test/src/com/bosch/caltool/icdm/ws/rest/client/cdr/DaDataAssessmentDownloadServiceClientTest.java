package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service Client Test for DaDataAssessment
 *
 * @author KWB1COB
 */
public class DaDataAssessmentDownloadServiceClientTest extends AbstractRestClientTest {


  /*
   * dataAssessmentId PIDC - icdm:pidvid,31259559181
   */
  private static final Long DA_DATAASSESSMENT_ID = 34695223878L;
  /*
   * File_name
   */
  private static final String FILE_NAME = "DaDataAssessmentJunitTestDownload.zip";
  /*
   * destdir
   */
  private static final String DEST_DIR = CommonUtils.getSystemUserTempDirPath();

  /**
   * Test method for {@link DaDataAssessmentDownloadServiceClient#checkFileAvailability(Long)}
   * 
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testCheckFileAvailability() throws ApicWebServiceException {
    String status = new DaDataAssessmentDownloadServiceClient().checkFileAvailability(DA_DATAASSESSMENT_ID);
    assertEquals("C", status);
  }

  /**
   * Test method for {@link DaDataAssessmentDownloadServiceClient#getDataAssessementReport(Long,String,String)}
   * 
   * @throws ApicWebServiceException ApicWebServiceException
   * @throws IOException IOException
   */
  @Test
  public void testGetDataAssessementReport() throws ApicWebServiceException, IOException {
    DaDataAssessmentDownloadServiceClient client = new DaDataAssessmentDownloadServiceClient();
    client.getDataAssessementReport(DA_DATAASSESSMENT_ID, FILE_NAME, DEST_DIR);
    Path path = Paths.get(DEST_DIR + FILE_NAME);
    long size = Files.size(path);
    assertNotEquals(0, size);
    Files.delete(path);
  }
}
