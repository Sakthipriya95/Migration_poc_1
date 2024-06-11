/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.comphex;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class CompHexWithCDFxServiceClientTest.
 *
 * @author gge6cob
 */
public class CompHexWithCDFxServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String COMPHEX_OUTPUT_NOT_NULL = "comphex output not null";

  /**
   * The hex file name
   */
  private static final String HEX_FILE_NAME = "HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * The hex file path
   */
  private static final String HEX_FILE_PATH = "testdata/comphex/HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * The hex file name for no report generated
   */
  private static final String HEX_FILE = "HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * The hex file path for no report generated
   */
  private static final String HEX_PATH = "testdata/comphex/HEX_MMD114A0CC1788_MC50_DISCR_LC.hex";

  /**
   * The hex file name with no compli param
   */
  private static final String HEX_FILE_NO_COMPLI_PARAM = "HEX_P1867_VC1CP107STSC_SC000.hex";

  /**
   * The hex file path with no compli param
   */
  private static final String HEX_PATH_NO_COMPLI_PARAM = "testdata/comphex/HEX_P1867_VC1CP107STSC_SC000.hex";

  /**
   * The vcdm DST source
   */
  private static final String VCDM_DST_SOURCE =
      "242-FPT_CURSOR_NEF_MD1 : Cursor_Offroad_216kW_stageV_Wheel_Loader . p1603v351 ; 1";

  /**
   * * The VCDM_DST_VERS_NO
   */
  private static final long VCDM_DST_VERS_NO = 20836626L;

  /**
   * PIDC Variant: <br>
   * CNHi->Diesel Engine->HD - Heavy Duty->MD1-C->CNHi_StageV_HD (StageV)->P1603
   */
  private static final long VAR_ID_VCDM_HEX = 1214274865L;

  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * A2L File: CNHi->Diesel Engine->HD - Heavy Duty->MD1-C->CNHi_StageV_HD 
   * (StageV)->P1603->p1603v351r09_iCDM.A2L 
   */
  private static final long PIDC_A2L_ID_VCDM_HEX = 1256501516L;

  /**
   * PIDC Version: <br>
   * CNHi->Diesel Engine->HD - Heavy Duty->MD1-C->CNHi_StageV_HD (StageV)
   */
  private static final long PIDC_VERS_ID_VCDM_HEX = 1127315720L;

  /**
   * A2L File: <br>
   * A2L File: CNHi->Diesel Engine->HD - Heavy Duty->MD1-C->CNHi_StageV_HD 
   * (StageV)->P1603->p1603v351r09_iCDM.A2L 
   */
  private static final long A2L_ID_VCDM_HEX = 2158205001L;

  /**
   * The hex file name within vcdm
   */
  private static final String HEX_FILE_VCDM_HEX = "Mgmbgh02_cw8_20836626.hex";

  /**
   * PIDC Variant: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1 (V1_0)->MMD114A0CC1788
   */
  private static final long VAR_ID = 1293613521L;
  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * A2L File: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1
   * (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final long PIDC_A2L_ID = 1165057193L;

  /**
   * PIDC Version: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1 (V1_0)
   */
  private static final long PIDC_VERS_ID = 1165057178L;

  /**
   * A2L File: <br>
   * A2L File: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1
   * (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final long A2L_ID = 2068555001L;

  /**
   * The invalid reference Id passed to download Files
   */
  private static final String INVALID_REF_ID = "-1";

  private static final Long PIDC_A2L_FOR_ONLY_QSSD_LABELS = 22670260328L;

  private static final Long VAR_ID_FOR_ONLY_QSSD_LABELS = 22648326578L;

  private static final Long A2L_FILE_ID_FOR_ONLY_QSSD_LABELS = 1233875001L;

  private static final Long PIDC_VERS_ID_FOR_ONLY_QSSD_LABELS = 4502134331L;

  private static final String HEX_FOR_ONLY_QSSD_LABELS = "ME17971EF_16S11_Validation_1.hex";

  private static final String HEX_FILE_PATH_ONLY_QSSD_LABELS = "testdata/cdr/" + HEX_FOR_ONLY_QSSD_LABELS;

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#getCompHexResult(CompHexMetaData) }
   *
   * @throws ApicWebServiceException the apic web service exception
   */
  @Test
  public void test0GetCompHexResultWithHex() throws ApicWebServiceException {
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(PIDC_A2L_ID);
    pidcData.setSelPIDCVariantId(VAR_ID);
    pidcData.setA2lFileId(A2L_ID);
    pidcData.setSourcePidcVerId(PIDC_VERS_ID);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(HEX_FILE_NAME);
    metaData.setSrcHexFilePath(HEX_FILE_PATH);

    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    CompHexResponse response = client.getCompHexResult(metaData);
    assertNotNull(COMPHEX_OUTPUT_NOT_NULL, response);
    checkResult(response);
    String referenceId = response.getReferenceId();

    // Download Excel
    testDownloadCompHexResultExcel(referenceId);

    // Download PDF
    testDownloadCompHexResultPdf(referenceId);

    testDownloadAll(referenceId);
  }


  /**
   * Test method for performing compare hex with a2l that contains only qssd labels
   *
   * @throws ApicWebServiceException error from compare hex service
   */
  @Test
  public void testGetCompHexResultForOnlyQssdLabels() throws ApicWebServiceException {

    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(PIDC_A2L_FOR_ONLY_QSSD_LABELS);
    pidcData.setSelPIDCVariantId(VAR_ID_FOR_ONLY_QSSD_LABELS);
    pidcData.setA2lFileId(A2L_FILE_ID_FOR_ONLY_QSSD_LABELS);
    pidcData.setSourcePidcVerId(PIDC_VERS_ID_FOR_ONLY_QSSD_LABELS);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(HEX_FOR_ONLY_QSSD_LABELS);
    metaData.setSrcHexFilePath(HEX_FILE_PATH_ONLY_QSSD_LABELS);

    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    CompHexResponse response = client.getCompHexResult(metaData);
    assertNotNull(COMPHEX_OUTPUT_NOT_NULL, response);
    checkResult(response);
    String referenceId = response.getReferenceId();

    // Download Excel
    testDownloadCompHexResultExcel(referenceId);

    // Download PDF
    testDownloadCompHexResultPdf(referenceId);

    testDownloadAll(referenceId);


  }


  /**
   * @throws ApicWebServiceException error during webservice call
   */
  @Test
  public void test02CompHex() throws ApicWebServiceException {
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(3276788015L);
    pidcData.setSelPIDCVariantId(3283034337L);
    pidcData.setA2lFileId(2068555001);
    pidcData.setSourcePidcVerId(3276788006L);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(HEX_FILE);
    metaData.setSrcHexFilePath(HEX_PATH);

    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    CompHexResponse response = client.getCompHexResult(metaData);
    assertNotNull(COMPHEX_OUTPUT_NOT_NULL, response);
    checkResult(response);
    String referenceId = response.getReferenceId();

    assertTrue(response.isCompliCheckFailed());
    LOG.debug("Compli review failed . so Report was not genereated");
    testDownloadAll(referenceId);
  }

  /**
   * @throws ApicWebServiceException - error during webservice call
   */
  @Test
  public void testCompHexNoCompliParam() throws ApicWebServiceException {
    // No compli params in the A2LFile
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(5029865428L);
    pidcData.setSelPIDCVariantId(1508257519L);
    pidcData.setA2lFileId(1793665479L);
    pidcData.setSourcePidcVerId(1502201192L);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(HEX_FILE_NO_COMPLI_PARAM);
    metaData.setSrcHexFilePath(HEX_PATH_NO_COMPLI_PARAM);

    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    CompHexResponse response = client.getCompHexResult(metaData);
    assertNotNull(COMPHEX_OUTPUT_NOT_NULL, response);
    checkResult(response);
    // download the results
    // since there are no compli params in the A2LFile, checkSSD report will not be generated
    testDownloadAll(response.getReferenceId());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#getCompHexResult(CompHexMetaData) }
   *
   * @throws ApicWebServiceException the apic web service exception
   */
  @Test
  public void test0GetCompHexResultWithVcdm() throws ApicWebServiceException {
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(PIDC_A2L_ID_VCDM_HEX);
    pidcData.setSelPIDCVariantId(VAR_ID_VCDM_HEX);
    pidcData.setA2lFileId(A2L_ID_VCDM_HEX);
    pidcData.setSourcePidcVerId(PIDC_VERS_ID_VCDM_HEX);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(HEX_FILE_VCDM_HEX);
    metaData.setHexFromVcdm(true);
    metaData.setVcdmDstSource(VCDM_DST_SOURCE);
    metaData.setVcdmDstVersId(VCDM_DST_VERS_NO);
    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    CompHexResponse response = client.getCompHexResult(metaData);
    assertNotNull(COMPHEX_OUTPUT_NOT_NULL, response);
    checkResult(response);
    String referenceId = response.getReferenceId();

    // Download Excel
    testDownloadCompHexResultExcel(referenceId);

    // Download PDF
    testDownloadCompHexResultPdf(referenceId);

    testDownloadAll(referenceId);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#getCompHexResult(CompHexMetaData) }
   *
   * @throws ApicWebServiceException the apic web service exception
   */
  @Test
  public void testGetCompHexResultWithoutHex() throws ApicWebServiceException {
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(PIDC_A2L_ID);
    pidcData.setSelPIDCVariantId(VAR_ID);
    pidcData.setA2lFileId(A2L_ID);
    pidcData.setSourcePidcVerId(PIDC_VERS_ID);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Input HEX file missing");

    new CompHexWithCDFxServiceClient().getCompHexResult(metaData);

    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#downloadAllFiles(String,String) }
   *
   * @throws ApicWebServiceException the apic web service exception
   */
  @Test
  public void testDownloadAllFilesNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error downloading output files for reference ID : " + INVALID_REF_ID);

    testDownloadAll(INVALID_REF_ID);

    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#downloadCompHexReport(String, String, String, String)}
   *
   * @param referenceId the reference id
   * @throws ApicWebServiceException the apic web service exception
   */
  private void testDownloadCompHexResultExcel(final String referenceId) throws ApicWebServiceException {
    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    String response = client.downloadCompHexReport(referenceId, ApicConstants.REPORT_TYPE_EXCEL,
        "CompareHex_JunitReport.xlsx", CommonUtils.getICDMTmpFileDirectoryPath());

    assertNotNull("comphex report(EXCEL) downloaded", response);

    LOG.debug("comphex report(EXCEL) downloaded : {}", response);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient#downloadCompHexReport(String, String, String, String)}
   *
   * @param referenceId the reference id
   * @throws ApicWebServiceException the apic web service exception
   */
  private void testDownloadCompHexResultPdf(final String referenceId) throws ApicWebServiceException {
    CompHexWithCDFxServiceClient client = new CompHexWithCDFxServiceClient();
    String response = client.downloadCompHexReport(referenceId, ApicConstants.REPORT_TYPE_PDF,
        "CompareHex_JunitReport.pdf", CommonUtils.getICDMTmpFileDirectoryPath());

    assertNotNull("comphex report (PDF) downloaded", response);

    LOG.debug("comphex report (PDF) downloaded : {}", response);
  }

  /**
   * @param referenceId ref ID
   * @throws ApicWebServiceException the apic web service exception
   */
  private void testDownloadAll(final String referenceId) throws ApicWebServiceException {
    String tempDir = System.getProperty("java.io.tmpdir");
    new CompHexWithCDFxServiceClient().downloadAllFiles(referenceId, tempDir);

    String filePath = tempDir + File.separator + referenceId + ".zip";
    assertTrue("All files downloaded", new File(filePath).exists());
  }

  /**
   * Check result.
   *
   * @param response the response
   */
  private void checkResult(final CompHexResponse response) {


    assertNotNull("comphex result not null", response.getCompHexResult());
    assertNotNull("comphex cdr-report-result not null", response.getCdrReport());
    assertNotNull("comphex statistics not null", response.getCompHexStatistics());

    LOG.debug("CompHex result : ");
    LOG.debug("    No  of params                  - {}", response.getCompHexResult().size());
    LOG.debug("    Reference-Id for files         - {}", response.getReferenceId());
    LOG.debug("    A2l File name                  - {}", response.getCdrReport().getA2lFile().getFilename());
    LOG.debug("    Pidc version name              - {}", response.getCdrReport().getPidcVersion().getName());
    LOG.debug("    No. of compli                  - {}", response.getCompHexStatistics().getStatCompliParamInA2L());
    LOG.debug("    Has Compli Errors ?            - {}", response.isCompliCheckFailed());
    LOG.debug("    No. of compli passed           - {}", response.getCompHexStatistics().getStatCompliParamPassed());
    LOG.debug("    No. of reviewed param          - {}", response.getCompHexStatistics().getStatFilteredParamRvwd());

    if (CommonUtils.isNotEmpty(response.getErrorMsgSet())) {
      response.getErrorMsgSet().forEach(e -> LOG.debug("    Error :                - {}", e));
    }
  }

}
