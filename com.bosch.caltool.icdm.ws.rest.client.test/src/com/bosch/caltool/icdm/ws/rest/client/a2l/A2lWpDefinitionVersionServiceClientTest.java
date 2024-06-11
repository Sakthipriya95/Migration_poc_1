package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.CopyA2lWpRespResponse;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.a2l.WpImportFromFuncInput;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTakeOverA2lWrapper;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lWpDefinitionVersion
 *
 * @author pdh2cob
 */
public class A2lWpDefinitionVersionServiceClientTest extends AbstractRestClientTest {


  /**
   * icdm:a2lid,27189277089-685723390
   */
  private static final long PIDC_A2L_ID_FOR_FINISHED_UPDATE = 27189277089L;

  /**
   *
   */
  private static final String WP_DEFN_VERS_BEFORE_AND_AFTER_COPY =
      "Wp Definition Versions before copy and after copy : {}, {}";

  /**
   *
   */
  private static final String WP_DEFN_VERSION_NOT_COPIED = "Wp defn version not copied";

  /**
   *
   */
  private static final long PIDC_A2L_ID_4 = 18501917440L;

  private static final String JUNIT_UPDATED_VERSION = "Junit_Updated_Version_";

  private static final String JUNIT_VERSION = "Junit_Version_";

  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->X_TEST_COPY_WP->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final long PIDC_A2L_ID_1 = 3126246223L;

  private static final Long WP_DEF_VERSION_ID = 1575042378L;

  private static final Long PIDC_A2L_ID_2 = 1525386229L;
  /**
   * BMW -> Diesel Engine ->PC-Passenger Car -> MD1-C -> PK_PAR2WP_COPY_1 -> MMD114A0CC1788_MD00.A2L
   */
  private static final long PIDC_A2L_ID_3 = 13063283373L;

  /**
   * A2L File: Chrysler->Diesel Engine->PC - Passenger Car->MD1-C->test_muthu_3
   * (v1)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L <br>
   * <br>
   * Link : icdm:a2lid,3203765171-2068555001
   */
  /**
   * Working Set Wp Definition Version Id
   */
  private static final Long WP_DEFN_WKGSET_VERS_ID = 18536378587L;
  /**
   * Wp Definition Version Id -Not a Working Set version
   */
  private static final Long WP_DEFN_VERS_ID = 18536378589L;

  /**
   * pidcA2l used:: icdm:a2lid,23240558053-2189855001
   */
  private static final Long PIDC_A2L_ID_5 = 23240558053L;

  /**
   * pidcA2l used:: icdm:a2lid,23283034564-2844409930
   */
  private static final Long PIDC_A2L_ID_6 = 23283034564L;


  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    A2lWpDefnVersion ret = servClient.get(WP_DEF_VERSION_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#get(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    this.thrown.expectMessage("A2l WP Definition Version with ID '" + -1L + "' not found");
    servClient.get(-1L);
    fail("Expected exception not thrown");
  }

  /**
   * @param ret
   */
  private void testOutput(final A2lWpDefnVersion ret) {
    assertEquals("Version desc is equal", "Desc", ret.getDescription());
    assertEquals("Pidc A2L Id is equal", Long.valueOf(1525386229L), ret.getPidcA2lId());
    assertFalse("Param Mapping Allowed", ret.isParamLevelChgAllowedFlag());
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#getWPDefnVersForPidcA2l(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWPDefnVersForPidcA2l() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    Map<Long, A2lWpDefnVersion> retMap = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_2);
    assertFalse("Response should not be null or empty", retMap.isEmpty());
    testOutput(retMap.get(WP_DEF_VERSION_ID));
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#getWPDefnVersForPidcA2l(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWPDefnVersForPidcA2lNegative() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    Map<Long, A2lWpDefnVersion> retMap = servClient.getWPDefnVersForPidcA2l(-1L);
    assertTrue("Response should be null or empty", retMap.isEmpty());
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#create(A2lWpDefnVersion, PidcA2l) } ,
   * {@link A2lWpDefinitionVersionServiceClient#update(List)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    PidcA2lServiceClient client = new PidcA2lServiceClient();

    A2lWpDefnVersion wpdefver = new A2lWpDefnVersion();
    wpdefver.setVersionName(JUNIT_VERSION + getRunId());
    wpdefver.setDescription(JUNIT_VERSION + getRunId());
    wpdefver.setActive(false);
    wpdefver.setPidcA2lId(1577924978L);
    wpdefver.setParamLevelChgAllowedFlag(false);
    // create
    A2lWpDefnVersion createdObj = servClient.create(wpdefver, client.getById(1577924978L));
    // validate
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, createdObj);
    assertEquals("Version name is equal", JUNIT_VERSION + getRunId(), createdObj.getVersionName());
    assertEquals("Is active is equal", false, createdObj.isActive());
    assertEquals("Pidc A2l Id is equal", Long.valueOf(1577924978L), createdObj.getPidcA2lId());
    assertEquals("Param mapping allowed is equal", false, createdObj.isParamLevelChgAllowedFlag());
    // update
    createdObj.setName(JUNIT_UPDATED_VERSION + getRunId());
    createdObj.setDescription(JUNIT_UPDATED_VERSION + getRunId());
    createdObj.setActive(true);

    Set<A2lWpDefnVersion> updatedSet = servClient.update(Arrays.asList(createdObj));
    A2lWpDefnVersion updatedObj = updatedSet.iterator().next();
    // validate
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, updatedObj);
    assertEquals("Version desc is equal", JUNIT_UPDATED_VERSION + getRunId(), updatedObj.getDescription());
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#copyA2lWpResp(List)}.Negative test
   *
   * @throws ApicWebServiceException exception when there are no changes between source and dest a2l wp def
   */
  @Test
  public void testNegavitveCopyA2lWpRespOnlyDefaultAssignments() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();

    CopyPar2WpFromA2lInput input = new CopyPar2WpFromA2lInput();
    input.setSourceWpDefVersId(3609400786L);
    input.setDescPidcA2lId(3609400785L);
    input.setOverOnlyDefaultAssigments(true);
    input.setOverWriteAssigments(false);
    servClient.copyA2lWpResp(Arrays.asList(input));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#copyA2lWpResp(List)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCopyA2lWpRespOnlyDefaultAssignments() throws ApicWebServiceException {
    // create a new var grp in source a2l
    A2lVariantGroup a2lVarGroup = new A2lVariantGroup();
    String runId = String.valueOf(System.currentTimeMillis());
    a2lVarGroup.setDescription("Variant group Desc");
    a2lVarGroup.setName("JunitVG_" + runId);
    a2lVarGroup.setWpDefnVersId(3132442853L);
    new A2lVariantGroupServiceClient().create(a2lVarGroup, new PidcA2l());
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    Map<Long, A2lWpDefnVersion> retMap = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_1);
    int noOfwpDefVersBeforeCopy = retMap.size();
    CopyPar2WpFromA2lInput input = new CopyPar2WpFromA2lInput();
    input.setSourceWpDefVersId(3132442853L);
    input.setDescPidcA2lId(PIDC_A2L_ID_1);
    input.setOverOnlyDefaultAssigments(true);
    input.setOverWriteAssigments(false);
    servClient.copyA2lWpResp(Arrays.asList(input));

    Map<Long, A2lWpDefnVersion> retMapAfterCopy = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_1);
    int noOfwpDefVersAfterCopy = retMapAfterCopy.size();
    assertEquals(WP_DEFN_VERSION_NOT_COPIED, (noOfwpDefVersBeforeCopy + 1), noOfwpDefVersAfterCopy);
    if (noOfwpDefVersAfterCopy == (noOfwpDefVersBeforeCopy + 1)) {
      LOG.info(WP_DEFN_VERS_BEFORE_AND_AFTER_COPY, noOfwpDefVersBeforeCopy, noOfwpDefVersAfterCopy);
    }
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#copyA2lWpResp(List)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCopyA2lWpRespOverWriteAssignmentsNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);

    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    Map<Long, A2lWpDefnVersion> retMap = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_1);
    int noOfwpDefVersBeforeCopy = retMap.size();
    CopyPar2WpFromA2lInput input = new CopyPar2WpFromA2lInput();
    input.setSourceWpDefVersId(3132442853L);
    input.setDescPidcA2lId(PIDC_A2L_ID_1);
    input.setOverOnlyDefaultAssigments(false);
    input.setOverWriteAssigments(true);
    servClient.copyA2lWpResp(Arrays.asList(input));
    Map<Long, A2lWpDefnVersion> retMapAfterCopy = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_1);
    int noOfwpDefVersAfterCopy = retMapAfterCopy.size();
    assertEquals(WP_DEFN_VERSION_NOT_COPIED, (noOfwpDefVersBeforeCopy + 1), noOfwpDefVersAfterCopy);
    if (noOfwpDefVersAfterCopy == (noOfwpDefVersBeforeCopy + 1)) {
      LOG.info(WP_DEFN_VERS_BEFORE_AND_AFTER_COPY, noOfwpDefVersBeforeCopy, noOfwpDefVersAfterCopy);
    }
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#copyA2lWpResp(List)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCopyA2lWpRespOverWriteAssignments() throws ApicWebServiceException {

    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    CopyPar2WpFromA2lInput input = new CopyPar2WpFromA2lInput();
    // BMW -> Diesel Engine ->PC-Passenger Car -> MD1-C -> PK_PAR2WP_COPY_1 -> MMD114A0CC1788_MD50.A2L -> Working Set
    input.setSourceWpDefVersId(13446653381L);
    input.setDescPidcA2lId(PIDC_A2L_ID_3);
    input.setOverOnlyDefaultAssigments(false);
    input.setOverWriteAssigments(true);
    servClient.copyA2lWpResp(Arrays.asList(input));

    LOG.info("Reset Destination Wp Definition Version using default wp definition version of that a2l");
    Map<Long, A2lWpDefnVersion> retMap = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_3);
    int noOfwpDefVersBeforeCopy = retMap.size();
    input = new CopyPar2WpFromA2lInput();
    input.setSourceWpDefVersId(13063283376L);
    input.setDescPidcA2lId(PIDC_A2L_ID_3);
    input.setOverOnlyDefaultAssigments(false);
    input.setOverWriteAssigments(true);
    servClient.copyA2lWpResp(Arrays.asList(input));
    Map<Long, A2lWpDefnVersion> retMapAfterCopy = servClient.getWPDefnVersForPidcA2l(PIDC_A2L_ID_3);
    int noOfwpDefVersAfterCopy = retMapAfterCopy.size();
    assertEquals(WP_DEFN_VERSION_NOT_COPIED, (noOfwpDefVersBeforeCopy + 1), noOfwpDefVersAfterCopy);
    if (noOfwpDefVersAfterCopy == (noOfwpDefVersBeforeCopy + 1)) {
      LOG.info(WP_DEFN_VERS_BEFORE_AND_AFTER_COPY, noOfwpDefVersBeforeCopy, noOfwpDefVersAfterCopy);
    }
  }


  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#hasActiveVersion(Long) }
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testHasActiveVersion() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    boolean hasActive = servClient.hasActiveVersion(1577924978L);
    assertEquals("Active version available for a2l", true, hasActive);
  }

  /**
   * Removed @Test annotation since there is no delete mechanism for the created record
   *
   * @throws ApicWebServiceException Exception from webservice
   */

  public void testCreatePidcA2landTakeOverFromA2l() throws ApicWebServiceException {
    long destinationWpDefVerId = 0;
    Set<PidcA2l> pidcA2lsToCreate = new HashSet<>();
    PidcA2l pidcA2l = new PidcA2l();
    pidcA2l.setProjectId(14085165379L);
    pidcA2l.setPidcVersId(14085165381L);
    pidcA2l.setSdomPverName("1730761");
    pidcA2l.setSdomPverRevision(0L);
    pidcA2l.setA2lFileId(688511187L);
    pidcA2l.setActive(true);
    pidcA2l.setWorkingSetModified(true);
    pidcA2lsToCreate.add(pidcA2l);

    PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper = new PidcTakeOverA2lWrapper();
    pidcTakeOverA2lWrapper.setSourceWpDefVersId(19307689583L);
    pidcTakeOverA2lWrapper.setPidcA2lsToCreate(pidcA2lsToCreate);

    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    CopyA2lWpRespResponse copyA2lWp = servClient.createPidcA2landTakeOverFromA2l(pidcTakeOverA2lWrapper);

    Set<PidcA2l> pls = copyA2lWp.getPidcA2lSet();
    for (PidcA2l pidA2lResp : pls) {
      assertEquals("PVER  name is not same", pidA2lResp.getSdomPverName(), pidcA2l.getSdomPverName());
      assertEquals("A2l file ids are not equal", pidA2lResp.getA2lFileId(), pidcA2l.getA2lFileId());
      assertEquals("PIDC version  ids are not equal", pidA2lResp.getPidcVersId(), pidcA2l.getPidcVersId());
      assertEquals("SDOM PVER Revision is not equal", pidA2lResp.getSdomPverRevision(), pidcA2l.getSdomPverRevision());
    }

    A2lWpResponsibilityServiceClient servClient1 = new A2lWpResponsibilityServiceClient();
    Set<A2lWpDefnVersion> des = copyA2lWp.getWpDefSet();
    for (A2lWpDefnVersion A2lWpDefnVersionResp : des) {
      destinationWpDefVerId = A2lWpDefnVersionResp.getId();
    }

    A2lWpDefinitionModel a2lWpDefinitionModelSource = servClient1.getA2lWpRespForWpDefnVers(19307689583L);
    A2lWpDefinitionModel a2lWpDefinitionModelDestination = servClient1.getA2lWpRespForWpDefnVers(destinationWpDefVerId);

    A2lWpParamMappingServiceClient servClient2 = new A2lWpParamMappingServiceClient();
    A2lWpParamMappingModel a2lWpParamMappingModelSource = servClient2.getAllByWpDefVersId(19307689583L);
    A2lWpParamMappingModel a2lWpParamMappingModelDestination = servClient2.getAllByWpDefVersId(destinationWpDefVerId);

    validateSorceNDestWps(a2lWpDefinitionModelSource, a2lWpDefinitionModelDestination);
    validateSorceNDestWpParam(a2lWpParamMappingModelSource, a2lWpParamMappingModelDestination);
  }


  /**
   * @param a2lWpParamMappingModelSource
   * @param a2lWpParamMappingModelDestination
   */

  private void validateSorceNDestWpParam(final A2lWpParamMappingModel a2lWpParamMappingModelSource,
      final A2lWpParamMappingModel a2lWpParamMappingModelDestination) {

    Map<Long, Map<Long, String>> sourceWpParamRespMap = a2lWpParamMappingModelSource.getParamIdWithWpAndRespMap();
    Map<Long, Map<Long, String>> destWpParamRespMap = a2lWpParamMappingModelDestination.getParamIdWithWpAndRespMap();

    // the test A2L does not have variant groups
    for (Entry<Long, Map<Long, String>> sourceEntry : sourceWpParamRespMap.entrySet()) {
      Long paramId = sourceEntry.getKey();

      Map<Long, String> sourceWpIdRespMap = sourceEntry.getValue();
      Entry<Long, String> sourceWpIdRespEntry = sourceWpIdRespMap.entrySet().iterator().next();

      // get the wpIdRespMap for destination A2L using the parameter ID (same for source and destination)
      Map<Long, String> destWpIdRespMap = destWpParamRespMap.get(paramId);
      Entry<Long, String> destWpIdRespEntry = destWpIdRespMap.entrySet().iterator().next();

      // validating WP ID
      assertEquals("Wp mapped to the parameter in source and destination A2L should be equal",
          sourceWpIdRespEntry.getKey(), destWpIdRespEntry.getKey());

      // validating Responsibility Name
      assertEquals("Resp name mapped to the parameter in source and destination A2L should be equal",
          sourceWpIdRespEntry.getValue(), destWpIdRespEntry.getValue());
    }
  }


  /**
   * @param a2lWpDefinitionModelSource
   * @param a2lWpDefinitionModelDestination
   */
  private void validateSorceNDestWps(final A2lWpDefinitionModel a2lWpDefinitionModelSource,
      final A2lWpDefinitionModel a2lWpDefinitionModelDestination) {
    Map<Long, A2lWpResponsibility> sourceWpRespMap = a2lWpDefinitionModelSource.getWpRespMap();

    Map<Long, A2lWpResponsibility> destWpRespMap = a2lWpDefinitionModelDestination.getWpRespMap();


    for (A2lWpResponsibility a2lWpResp : sourceWpRespMap.values()) {

      Long sourceA2lWpId = a2lWpResp.getA2lWpId();
      String sourceWpName = a2lWpResp.getName();

      for (A2lWpResponsibility destA2lWpResp : destWpRespMap.values()) {
        if (sourceA2lWpId.equals(destA2lWpResp.getA2lWpId())) {
          assertEquals("Source and destination wp name is not equal", sourceWpName, destA2lWpResp.getName());
        }
      }
    }
  }


  /**
   * Removed @Test annotation since there is no delete mechanism for the created record
   *
   * @throws ApicWebServiceException Exception from webservice
   */

  public void testNegativeCreatePidcA2landTakeOverFromA2l() throws ApicWebServiceException {
    Set<PidcA2l> pidcA2lsToCreate = new HashSet<>();
    PidcA2l pidcA2l = new PidcA2l();
    pidcA2l.setProjectId(14085165379L);
    pidcA2l.setPidcVersId(14085165381L);
    pidcA2l.setSdomPverName("1798DCG6");
    pidcA2l.setSdomPverRevision(0L);
    pidcA2l.setA2lFileId(41087962L);
    pidcA2l.setActive(true);
    pidcA2l.setWorkingSetModified(true);
    pidcA2lsToCreate.add(pidcA2l);


    PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper = new PidcTakeOverA2lWrapper();
    pidcTakeOverA2lWrapper.setSourceWpDefVersId(14085168535L);
    pidcTakeOverA2lWrapper.setPidcA2lsToCreate(pidcA2lsToCreate);


    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();

    servClient.createPidcA2landTakeOverFromA2l(pidcTakeOverA2lWrapper);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Record already exists. CONSTRAINT_VIOLATION");

    fail("expected exception not thrown");


  }


  /**
   * Removed @Test annotation since there is no delete mechanism for the created record
   *
   * @throws ApicWebServiceException Exception from webservice
   */

  public void testNegativeEmptyPIDCCreatePidcA2landTakeOverFromA2l() throws ApicWebServiceException {
    Set<PidcA2l> pidcA2lsToCreate = new HashSet<>();
    PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper = new PidcTakeOverA2lWrapper();
    pidcTakeOverA2lWrapper.setPidcA2lsToCreate(pidcA2lsToCreate);
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();

    servClient.createPidcA2landTakeOverFromA2l(pidcTakeOverA2lWrapper);
    this.thrown.expect(ApicWebServiceException.class);
    fail("expected exception not thrown");

  }

  /**
   * @throws ApicWebServiceException Exception from webservice
   */
  @Test
  public void testNegativeLoadFC2WPAssignmentsinA2LWithoutQnaireConfig() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "FC2WP mapping incomplete for the PIDC. Please assign value for the following attribute in your PIDC : iCDM Questionnaire Config");

    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

    a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
    a2lWpDefnVersion.setActive(false);
    a2lWpDefnVersion.setWorkingSet(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
    a2lWpDefnVersion.setPidcA2lId(PIDC_A2L_ID_4);
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();

    client.create(a2lWpDefnVersion, new PidcA2lServiceClient().getById(PIDC_A2L_ID_4));
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#isDefaultWpRespLabelAssignmentExist(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsDefaultWpRespLabelAssignmentExist() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    boolean isDefaultWpRespLabelAssignmentExist = servClient.isDefaultWpRespLabelAssignmentExist(PIDC_A2L_ID_5);
    assertEquals("_DEFAULT_WP_ assignments are available in the working set labels", true,
        isDefaultWpRespLabelAssignmentExist);
  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#isDefaultWpRespLabelAssignmentExist(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsDefaultWpRespLabelAssignmentExistNegative() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    boolean isDefaultWpRespLabelAssignmentExist = servClient.isDefaultWpRespLabelAssignmentExist(PIDC_A2L_ID_6);
    assertEquals("_DEFAULT_WP_ assignments are not available in the working set labels", false,
        isDefaultWpRespLabelAssignmentExist);
  }

  /**
   * {@link A2lWpDefinitionVersionServiceClient#importWpFromFunctions(WpImportFromFuncInput)}
   *
   * @throws ApicWebServiceException error from service
   */
  @Ignore
   @Test
  public void testImportWpFromFunc() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient defnVersServClient = new A2lWpDefinitionVersionServiceClient();

    A2lWpDefnVersion a2lWpDefnVersBeforeImport = defnVersServClient.get(WP_DEFN_WKGSET_VERS_ID);

    WpImportFromFuncInput importWpFromFuncInput = new WpImportFromFuncInput();
    importWpFromFuncInput.setWpDefVersId(WP_DEFN_WKGSET_VERS_ID);
    importWpFromFuncInput.setDeleteUnusedWPs(false);
    importWpFromFuncInput.setKeepExistingResp(false);
    // Import Wp from Functions
    CopyA2lWpRespResponse copyA2lWpRespResponse = defnVersServClient.importWpFromFunctions(importWpFromFuncInput);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, copyA2lWpRespResponse);

    // CopyA2lWpRespResponse - return 2 A2l Wp Definition Versions one is working set, the other one is Backup version
    // created
    for (A2lWpDefnVersion defnVersion : copyA2lWpRespResponse.getWpDefSet()) {
      if (defnVersion.isWorkingSet()) {
        assertEquals("Working Set's name should be equal", ApicConstants.WORKING_SET_NAME,
            defnVersion.getVersionName());
        Long versNumberAfterImport = a2lWpDefnVersBeforeImport.getVersion() + 1;
        assertEquals("Version should be updated", versNumberAfterImport, defnVersion.getVersion());
      }
      else {
        assertEquals("Backup version's name should be equal", "Backup of Working Set before WP creation from Func",
            defnVersion.getVersionName());
        assertEquals("Version should be 1", Long.valueOf(1l), defnVersion.getVersion());
      }
    }

  }

  /**
   * {@link A2lWpDefinitionVersionServiceClient#importWpFromFunctions(WpImportFromFuncInput)}
   *
   * @throws ApicWebServiceException error from service
   */
  @Test
  public void testImportWpFromFuncNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("WP creation from Functions is possible only in Working Set WP Definition version");

    WpImportFromFuncInput importWpFromFuncInput = new WpImportFromFuncInput();
    importWpFromFuncInput.setWpDefVersId(WP_DEFN_VERS_ID);
    importWpFromFuncInput.setDeleteUnusedWPs(false);
    importWpFromFuncInput.setKeepExistingResp(false);
    // Import Wp from Functions
    new A2lWpDefinitionVersionServiceClient().importWpFromFunctions(importWpFromFuncInput);
    fail(EXPECTED_MESSAGE_NOT_THROWN);

  }

  /**
   * Test method for {@link A2lWpDefinitionVersionServiceClient#updateWorkpackageStatus(long, A2lWpDefnVersion) }
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdateWorkpackageStatus() throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient servClient = new A2lWpDefinitionVersionServiceClient();
    // Creating new active Wp definition version 1st
    A2lWpDefnVersion wpdefver = new A2lWpDefnVersion();
    wpdefver.setVersionName(JUNIT_VERSION + getRunId());
    wpdefver.setDescription(JUNIT_VERSION + getRunId());
    wpdefver.setActive(false);
    wpdefver.setPidcA2lId(PIDC_A2L_ID_FOR_FINISHED_UPDATE);
    wpdefver.setParamLevelChgAllowedFlag(false);
    wpdefver.setActive(true);
    // create
    A2lWpDefnVersion createdObj =
        servClient.create(wpdefver, new PidcA2lServiceClient().getById(PIDC_A2L_ID_FOR_FINISHED_UPDATE));
    // validate
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, createdObj);
    assertEquals("Version name is equal", JUNIT_VERSION + getRunId(), createdObj.getVersionName());
    assertEquals("Is active is equal", true, createdObj.isActive());
    assertEquals("Pidc A2l Id is equal", Long.valueOf(PIDC_A2L_ID_FOR_FINISHED_UPDATE), createdObj.getPidcA2lId());
    assertEquals("Param mapping allowed is equal", false, createdObj.isParamLevelChgAllowedFlag());

    // Updating the finished status of created active wp defnition version by passing any previous active version Id
    servClient.updateWorkpackageStatus(27507287080L, createdObj);

  }
}
