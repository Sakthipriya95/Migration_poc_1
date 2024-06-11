/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespGrpsResponse;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespResponse;
import com.bosch.caltool.icdm.model.a2l.Par2Wp;
import com.bosch.caltool.icdm.model.a2l.VarGrp2Wp;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author and4cob
 */
public class ImportA2lWpRespServiceClientTest extends AbstractRestClientTest {

  /**
   * Input validation error message
   */
  private static final String MISSING_PARAMETERS_IN_INPUT = "Missing parameter(s) in input";

  /**
   * Pidc version Id
   */
  private static final long PIDC_VERS_ID = 773510915L;

  /**
   * Wp definition version Id
   */
  private static final long WP_DEF_VERS_ID = 6602417805L;

  /**
   * Variant Group Id
   */
  private static final long VAR_GRP_ID = 2141545780L;

  /**
   * A2L File Id
   */
  private static final long A2L_FILE_ID = 285439497L;
  /**
   *
   */
  private static final String RESPONSE_NOT_NULL_MSG = "Response should not be null";
  /**
   *
   */
  private static final String FAIL_MSG = "Expected exception not thrown";
  private static final Long FC2WP_DIV_VALUE_ID = 787372417L;
  private static final String PARAM_NAME1 = "ABKKATTAB";
  private static final String PARAM_NAME2 = "ABMM4";
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * 230671 - FC2WP Gen2 - generic
   */
  private static final String T05_FC2_WP_NAME = "FC2WP Gen2 - generic";

  /**
   * Test method for {@link ImportA2lWpRespServiceClient#importA2lWpRespFromFC2WP(ImportA2lWpRespInput, PidcA2l)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportA2lWpRespFromFC2WP() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespInput importA2lWpRespInput = new ImportA2lWpRespInput();
    importA2lWpRespInput.setA2lFileId(A2L_FILE_ID);
    importA2lWpRespInput.setVariantGrpId(VAR_GRP_ID);

    FC2WPVersionServiceClient fc2wpVerServClient = new FC2WPVersionServiceClient();
    FC2WPVersion actVers = fc2wpVerServClient.getActiveVersionByValueID(T05_FC2_WP_NAME, FC2WP_DIV_VALUE_ID);

    importA2lWpRespInput.setFc2wpVersId(actVers.getId());
    importA2lWpRespInput.setWpDefVersId(WP_DEF_VERS_ID);
    importA2lWpRespInput.setPidcVersionId(PIDC_VERS_ID);
    importA2lWpRespInput.setCreateParamMapping(true);

    ImportA2lWpRespResponse importA2lWprespResponse =
        servClient.importA2lWpRespFromFC2WP(importA2lWpRespInput, new PidcA2l());
    assertNotNull(RESPONSE_NOT_NULL_MSG, importA2lWprespResponse);

    Set<A2lWpResponsibility> wpRespPalSet = importA2lWprespResponse.getWpRespPalSet();
    Set<Long> idSet = new HashSet<>();
    wpRespPalSet.forEach(wpResp -> idSet.add(wpResp.getId()));
    PidcA2l pidcA2l = new PidcA2lServiceClient().getById(3814682378L);
    new A2lWpResponsibilityServiceClient().delete(idSet, pidcA2l);
  }

  /**
   * Test method for {@link ImportA2lWpRespServiceClient#importA2lWpRespFromExcel(ImportA2lWpRespData, PidcA2l)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportA2lWpRespFromExcelPar2Wp() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespData excelInput = new ImportA2lWpRespData();
    excelInput.setWpDefVersId(6599836953L);
    excelInput.setA2lFileId(279244598L);
    excelInput.setPidcVersionId(3760576978L);

    Par2Wp param1 = new Par2Wp();
    param1.setA2lParamId(385771265L);
    param1.setParamName(PARAM_NAME1);
    param1.setWpName("JUnit_testImportA2lFromExcelWPResp1" + getRunId());
    param1.setRespName("_RESP__Messy__Sven" + getRunId());
    param1.setRespTypeCode(WpRespType.RB.getCode());
    param1.setFuncName("BBKHAKT");

    Par2Wp param2 = new Par2Wp();
    param2.setA2lParamId(411654465L);
    param2.setParamName(PARAM_NAME2);
    param2.setWpName("JUnit_testImportA2lFromExcelWPResp2" + getRunId());
    param2.setRespName("_RESP__Hammer__Michael" + getRunId());
    param2.setRespTypeCode(WpRespType.RB.getCode());
    param2.setFuncName("AEVAB");

    Map<String, Par2Wp> paramWpRespMap = new HashMap<>();
    paramWpRespMap.put(PARAM_NAME1, param1);
    paramWpRespMap.put(PARAM_NAME2, param2);
    excelInput.setParamWpRespMap(paramWpRespMap);

    ImportA2lWpRespResponse importA2lWprespResponse = servClient.importA2lWpRespFromExcel(excelInput, new PidcA2l());
    assertNotNull(RESPONSE_NOT_NULL_MSG, importA2lWprespResponse);

    Set<A2lWpResponsibility> a2lWpRespSet = importA2lWprespResponse.getWpRespPalSet();
    A2lWpResponsibilityServiceClient a2lWpRespServClient = new A2lWpResponsibilityServiceClient();
    PidcA2lServiceClient pidcA2lServClient = new PidcA2lServiceClient();
    PidcA2l pidcA2l = pidcA2lServClient.getById(3814682378L);

    Set<Long> idSet = new HashSet<>();
    a2lWpRespSet.forEach(wpResp -> idSet.add(wpResp.getId()));
    a2lWpRespServClient.delete(idSet, pidcA2l);
  }

  /**
   * Test
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testImportA2lWpRespFromExcelWpResp() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespData excelInput = new ImportA2lWpRespData();
    excelInput.setWpDefVersId(6602417805L);
    excelInput.setA2lFileId(285439497L);
    excelInput.setPidcVersionId(773510915L);

    VarGrp2Wp varGrp1 = new VarGrp2Wp();
    varGrp1.setRespName("_RESP__Messy__Sven");
    // varGrp1.setVarGrpId(2141545780L);
    varGrp1.setWpName("JUnit_" + getRunId() + "_testImportA2lFromExcelWPResp1");
    varGrp1.setRespTypeCode(WpRespType.RB.getCode());

    Set<VarGrp2Wp> varGrp2WpRespSet = new HashSet<>();
    varGrp2WpRespSet.add(varGrp1);

    excelInput.setVarGrp2WpRespSet(varGrp2WpRespSet);

    ImportA2lWpRespResponse response = servClient.importA2lWpRespFromExcel(excelInput, new PidcA2l());
    assertNotNull(RESPONSE_NOT_NULL_MSG, response);

    Set<A2lWpResponsibility> a2lWpRespSet = response.getWpRespPalSet();
    A2lWpResponsibilityServiceClient a2lWpRespServClient = new A2lWpResponsibilityServiceClient();
    PidcA2lServiceClient pidcA2lServClient = new PidcA2lServiceClient();
    PidcA2l pidcA2l = pidcA2lServClient.getById(2068069779L);

    Set<Long> idSet = new HashSet<>();
    a2lWpRespSet.forEach(wpResp -> idSet.add(wpResp.getId()));
    a2lWpRespServClient.delete(idSet, pidcA2l);
  }

  /**
   * Test throwErrorWithDuplicateAliasNames
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testA2LWpRespImportWithDuplicateAliasNames() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespData excelInput = new ImportA2lWpRespData();
    excelInput.setWpDefVersId(17778308704L);
    excelInput.setA2lFileId(2488397648L);
    excelInput.setPidcVersionId(14085165381L);

    Par2Wp param1 = new Par2Wp();
    param1.setA2lParamId(422799315L);
    param1.setParamName("AC_pACThresHi1_C");
    param1.setWpName("WP_Test1");
    param1.setRespName("r1");
    param1.setRespTypeCode(WpRespType.RB.getCode());
    param1.setFuncName("AC_Demand");

    Par2Wp param2 = new Par2Wp();
    param2.setA2lParamId(387919115L);
    param2.setParamName("AC_pACThresHi2_C");
    param2.setWpName("WP_Test2");
    param2.setRespName("r2");
    param2.setRespTypeCode(WpRespType.CUSTOMER.getCode());
    param2.setFuncName("AC_Demand");

    Map<String, Par2Wp> paramWpRespMap = new HashMap<>();
    paramWpRespMap.put("AC_pACThresHi1_C", param1);
    paramWpRespMap.put("AC_pACThresHi2_C", param2);
    excelInput.setParamWpRespMap(paramWpRespMap);
    this.thrown.expectMessage(
        "Import aborted. One or more responsibilities already exist in the PIDC with different alias names.");
    servClient.importA2lWpRespFromExcel(excelInput, new PidcA2l());
    fail(FAIL_MSG);


  }

  /**
   * Case - 1 : Positive scenario - wp defn vers is Working set and does not contain wp resp
   * mappings{@link ImportA2lWpRespServiceClient#a2lWpRespGrpsImport(PidcA2l,Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testA2lWpRespGrpsImport() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();
    PidcA2l pidcA2l = new PidcA2lServiceClient().getById(2552177874L);
    ImportA2lWpRespGrpsResponse a2lWpRespGrpsresponse = servClient.a2lWpRespGrpsImport(pidcA2l, 4885522281L);
    assertNotNull(RESPONSE_NOT_NULL_MSG, a2lWpRespGrpsresponse);

    // getting workpackage - responsibility created
    Set<A2lWpResponsibility> a2lWpRespSet = a2lWpRespGrpsresponse.getWpRespPalSet();

    Set<Long> idSet = new HashSet<>();
    a2lWpRespSet.forEach(wpResp -> idSet.add(wpResp.getId()));
    // skip the Default WP
    idSet.remove(4885522282L);

    // delete newly imported workpackage - responsibilities
    new A2lWpResponsibilityServiceClient().delete(idSet, pidcA2l);
  }

  // /**
  // * Negative test - when the pidc a2l already contain wp-resp mappings
  // * {@link ImportA2lWpRespServiceClient#a2lWpRespGrpsImport(PidcA2l,Long)}.
  // *
  // * @throws ApicWebServiceException web service error
  // */
  // @Test
  // public void testA2lWpRespGrpsImportNeg() throws ApicWebServiceException {
  // ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();
  //
  // this.thrown.expect(ApicWebServiceException.class);
  // this.thrown.expectMessage(
  // "Import is not possible since PIDC A2L with ID '" + "' already contain WP-Responsibilty Mappings.");
  // servClient.a2lWpRespGrpsImport(new PidcA2lServiceClient().getById(3203765170L), 4912607278L);
  // fail("Expected exception not thrown");
  // }

  /**
   * case 2 :Negative test - when the input A2l WP Definition Version id is null
   * {@link ImportA2lWpRespServiceClient#a2lWpRespGrpsImport(PidcA2l,Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testA2lWpRespGrpsImportNeg1() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();
    this.thrown.expectMessage("A2l WP Definition Version with ID 'null' not found");
    servClient.a2lWpRespGrpsImport(new PidcA2l(), null);
    fail(FAIL_MSG);
  }

  /**
   * case 3: Negative test - when the input a2l wp defn version is not working set
   * {@link ImportA2lWpRespServiceClient#a2lWpRespGrpsImport(PidcA2l,Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testA2lWpRespGrpsImportNeg2() throws ApicWebServiceException {
    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();
    this.thrown.expectMessage("Import is possible only in Working Set WP Definition version.");
    servClient.a2lWpRespGrpsImport(new PidcA2lServiceClient().getById(2552177874L), 4892943927L);
    fail(FAIL_MSG);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testNegativeImportA2lWpRespFromFC2WPPidcVersNull() throws ApicWebServiceException {
    this.thrown.expectMessage(MISSING_PARAMETERS_IN_INPUT);

    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespInput importA2lWpRespInput = new ImportA2lWpRespInput();
    importA2lWpRespInput.setA2lFileId(285439497L);
    importA2lWpRespInput.setVariantGrpId(2141545780L);

    FC2WPVersionServiceClient fc2wpVerServClient = new FC2WPVersionServiceClient();
    FC2WPVersion actVers = fc2wpVerServClient.getActiveVersionByValueID(T05_FC2_WP_NAME, FC2WP_DIV_VALUE_ID);

    importA2lWpRespInput.setFc2wpVersId(actVers.getId());
    importA2lWpRespInput.setWpDefVersId(6602417805L);
    importA2lWpRespInput.setPidcVersionId(null);
    importA2lWpRespInput.setCreateParamMapping(true);

    servClient.importA2lWpRespFromFC2WP(importA2lWpRespInput, new PidcA2l());
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testNegativeImportA2lWpRespFromFC2WPA2lIdNull() throws ApicWebServiceException {
    this.thrown.expectMessage(MISSING_PARAMETERS_IN_INPUT);

    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespInput importA2lWpRespInput = new ImportA2lWpRespInput();
    importA2lWpRespInput.setA2lFileId(null);
    importA2lWpRespInput.setVariantGrpId(VAR_GRP_ID);

    FC2WPVersionServiceClient fc2wpVerServClient = new FC2WPVersionServiceClient();
    FC2WPVersion actVers = fc2wpVerServClient.getActiveVersionByValueID(T05_FC2_WP_NAME, FC2WP_DIV_VALUE_ID);

    importA2lWpRespInput.setFc2wpVersId(actVers.getId());
    importA2lWpRespInput.setWpDefVersId(WP_DEF_VERS_ID);
    importA2lWpRespInput.setPidcVersionId(PIDC_VERS_ID);
    importA2lWpRespInput.setCreateParamMapping(true);

    servClient.importA2lWpRespFromFC2WP(importA2lWpRespInput, new PidcA2l());
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testNegativeImportA2lWpRespFromFC2WPWpDefVersIdNull() throws ApicWebServiceException {
    this.thrown.expectMessage(MISSING_PARAMETERS_IN_INPUT);

    ImportA2lWpRespServiceClient servClient = new ImportA2lWpRespServiceClient();

    ImportA2lWpRespInput importA2lWpRespInput = new ImportA2lWpRespInput();
    importA2lWpRespInput.setA2lFileId(A2L_FILE_ID);
    importA2lWpRespInput.setVariantGrpId(VAR_GRP_ID);

    FC2WPVersionServiceClient fc2wpVerServClient = new FC2WPVersionServiceClient();
    FC2WPVersion actVers = fc2wpVerServClient.getActiveVersionByValueID(T05_FC2_WP_NAME, FC2WP_DIV_VALUE_ID);

    importA2lWpRespInput.setFc2wpVersId(actVers.getId());
    importA2lWpRespInput.setWpDefVersId(null);
    importA2lWpRespInput.setPidcVersionId(PIDC_VERS_ID);
    importA2lWpRespInput.setCreateParamMapping(true);

    servClient.importA2lWpRespFromFC2WP(importA2lWpRespInput, new PidcA2l());
  }
}
