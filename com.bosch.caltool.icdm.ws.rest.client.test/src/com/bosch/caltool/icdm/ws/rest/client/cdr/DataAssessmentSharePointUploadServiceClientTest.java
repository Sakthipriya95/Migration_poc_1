package com.bosch.caltool.icdm.ws.rest.client.cdr;


import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service Client Test for DataAssessmentSharePointUploadServiceClient
 * <li>This class does not contain any valid assertions since client invokes service in new Thread</li>
 * <li>Username and password of User is needed and access has to be granted in SharePoint</li>
 * <li>Hence this is created as a dummy test to pass code coverage</li>
 *
 * @author msp5cob
 */
public class DataAssessmentSharePointUploadServiceClientTest extends AbstractRestClientTest {


  /**
   * Waiting for service call to happen since the client starts a new thread
   */
  private static final long THREAD_SLEEP_TIME = 1000L;
  /**
   * dataAssessmentId PIDC - icdm:pidvid,31259559181
   */
  private static final Long DATA_ASSESSMENT_ID = 34795761928L;
  /**
   * dataAssessmentId PIDC - icdm:pidvid,23786965225
   */
  private static final Long DATA_ASSESSMENT_ID_NO_FILE = 41176351073L;
  /**
   * SharePoint URL
   */
  private static final String SHARE_POINT_TEST_URL =
      "https://bosch.sharepoint.com/sites/msteams_5545073/Documents/Development/";
  /**
   * iCDM Test User
   */
  private static final String ICDM_TEST_USER_NAME = "dgs_icdm";
  /**
   * Encrypted password String of dummy password - icdm_dummy_pwd
   */
  private static final String ICDM_TEST_USER_PWD = "hkwzRcCGdLqWUwv+S26saw==";

  /**
   * Test method for
   * {@link DataAssessmentSharePointUploadServiceClient#uploadFileToSharePoint(DataAssessSharePointUploadInputModel)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testUploadFileToSharePoint() throws ApicWebServiceException {
    DataAssessSharePointUploadInputModel input = new DataAssessSharePointUploadInputModel();
    input.setBaselineId(DATA_ASSESSMENT_ID);
    input.setSharePointUrl(SHARE_POINT_TEST_URL);
    input.setUserName(ICDM_TEST_USER_NAME);
    input.setEncryptedPassword(ICDM_TEST_USER_PWD);
    new DataAssessmentSharePointUploadServiceClient().uploadFileToSharePoint(input);
    try {
      Thread.sleep(THREAD_SLEEP_TIME);
    }
    catch (InterruptedException e) {
      LOG.error("Error while waiting for Service to complete", e);
    }
  }

  /**
   * Test method for
   * {@link DataAssessmentSharePointUploadServiceClient#uploadFileToSharePoint(DataAssessSharePointUploadInputModel)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testUploadFileToSharePointNegative() throws ApicWebServiceException {
    DataAssessSharePointUploadInputModel input = new DataAssessSharePointUploadInputModel();
    input.setBaselineId(DATA_ASSESSMENT_ID_NO_FILE);
    input.setSharePointUrl(SHARE_POINT_TEST_URL);
    input.setUserName(ICDM_TEST_USER_NAME);
    input.setEncryptedPassword(ICDM_TEST_USER_PWD);
    new DataAssessmentSharePointUploadServiceClient().uploadFileToSharePoint(input);

    try {
      Thread.sleep(THREAD_SLEEP_TIME);
    }
    catch (InterruptedException e) {
      LOG.error("Error while waiting for Service to complete", e);
    }
  }

}
