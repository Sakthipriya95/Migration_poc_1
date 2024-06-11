package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for WpFiles Download
 *
 * @author msp5cob
 */
public class WpFilesDownloadServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long WP_ARCHIVAL_ID = -1L;
  /**
  *
  */
  private static final long WP_ARCHIVAL_ID_INVALID = -100L;
  /*
   * File_name
   */
  private static final String FILE_NAME = "WpArchival_JunitTestDownload.zip";
  /*
   * destdir
   */
  private static final String DEST_DIR = CommonUtils.getSystemUserTempDirPath();


  /**
   * Test method for {@link WpFilesDownloadServiceClient#getWpArchivalFile(Long,String,String)}
   */
  @Test
  public void testWpArchivalFilesDownload() {
    try {
      new WpFilesDownloadServiceClient().getWpArchivalFile(WP_ARCHIVAL_ID, FILE_NAME, DEST_DIR);
      Path path = Paths.get(DEST_DIR + FILE_NAME);
      long size = Files.size(path);
      assertNotEquals(0, size);
      Files.delete(path);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
    }
  }

  /**
   * Test method for {@link WpFilesDownloadServiceClient#getWpArchivalFile(Long,String,String)}
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void testWpArchivalFilesDownloadNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("No Files found for Wp Archival Id : " + WP_ARCHIVAL_ID_INVALID);
    new WpFilesDownloadServiceClient().getWpArchivalFile(WP_ARCHIVAL_ID_INVALID, FILE_NAME, DEST_DIR);
    fail("Expected exception not thrown");
  }


}
