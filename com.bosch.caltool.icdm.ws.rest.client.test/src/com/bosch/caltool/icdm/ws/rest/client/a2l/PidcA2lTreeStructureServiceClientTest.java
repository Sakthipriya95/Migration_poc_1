/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.PidcA2lTreeStructureModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class PidcA2lTreeStructureServiceClientTest extends AbstractRestClientTest {

  /**
   * icdm:a2lid,28387997341-2189855001
   */
  private static final long PIDC_A2L_ID1 = 28387997341l;

  /**
   * icdm:a2lid,28015487031-2189855001
   */
  private static final long PIDC_A2L_ID2 = 28015487031l;

  /**
   * icdm:a2lid,28227002557-2155255001
   */
  private static final long PIDC_A2L_ID3_1 = 28227002557l;

  /**
   * icdm:a2lid,28217682666-2189855001
   */
  private static final long PIDC_A2L_ID3_2 = 28217682666l;

  /**
   * icdm:a2lid,28309656893-2155255001
   */
  private static final long PIDC_A2L_ID4_1 = 28309656893l;

  /**
   * icdm:a2lid,28300315739-2189855001
   */
  private static final long PIDC_A2L_ID4_2 = 28300315739l;

  /**
   * Testcase to validate the PidcA2lTreeStructure Service to fetch the A2l Structure Model for A2l with Both Normal and
   * Virtual Records Pidc Id : icdm:pidvid,28387997332
   *
   * @throws ApicWebServiceException sa exception
   */
  @Test
  public void testGetPidcA2lTreeStructuresModel1() throws ApicWebServiceException {
    // icdm:a2lid,28387997341-2189855001
    PidcA2lTreeStructureServiceClient pidcA2lTreeStructureServiceClient = new PidcA2lTreeStructureServiceClient();
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModel =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID1);
    assertTrue(!pidcA2lTreeStructuresModel.getPidcVariantMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModel.getA2lRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModel.getA2lWpMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModel.getRvwQnaireRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModel.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModel.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModel);
  }

  /**
   * Testcase to test a2l with no-variant and only monica review with no-questionniares
   * icdm:a2lid,28015487031-2189855001 - a2l file link
   *
   * @throws ApicWebServiceException sa exception
   */
  @Test
  public void testGetPidcA2lTreeStructuresModel2() throws ApicWebServiceException {
    // icdm:a2lid,28015487031-2189855001
    PidcA2lTreeStructureServiceClient pidcA2lTreeStructureServiceClient = new PidcA2lTreeStructureServiceClient();
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModel =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID2);
    assertTrue(pidcA2lTreeStructuresModel.getPidcVariantMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModel.getA2lRespMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModel.getA2lWpMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModel.getRvwQnaireRespMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModel.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModel.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModel);
  }


  /**
   * Testcase to test a2l with variant on default and variant group level contains normal and monica review with
   * questionnairees icdm:a2lid,28227002557-2155255001 - a2l file link
   *
   * @throws ApicWebServiceException sa exception
   */
  @Test
  public void testGetPidcA2lTreeStructuresModel3() throws ApicWebServiceException {
    // PIDC Name : Junit_Test_Project_A2l_Structure_MonicaReview_withVariant_DONTUSEIT (v1)
    // PIDC Link : icdm:pidvid,28217682657
    // a2l name : MMD114A0CC1788_MD00.A2L
    // a2l Link : icdm:a2lid,28227002557-2155255001
    PidcA2lTreeStructureServiceClient pidcA2lTreeStructureServiceClient = new PidcA2lTreeStructureServiceClient();
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModelForA2l1 =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID3_1);
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getPidcVariantMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getA2lRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getA2lWpMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getRvwQnaireRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModelForA2l1);

    // a2l name : MMD114A0CC1788_MD00_withGroups.A2L
    // a2l Link : icdm:a2lid,28217682666-2189855001
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModelForA2l2 =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID3_2);
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getPidcVariantMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getA2lRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getA2lWpMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getRvwQnaireRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l2.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModelForA2l2);
  }

  /**
   * Testcase to test a2l with no-variant on default and variant group level contains normal review with questionnairees
   *
   * @throws ApicWebServiceException sa exception
   */
  @Test
  public void testGetPidcA2lTreeStructuresModel4() throws ApicWebServiceException {
    // PIDC Name : Junit_Test_Project_A2l_Structure_No_Var_DONTUSEIT (v1)
    // PIDC Link : icdm:pidvid,28300315731
    // a2l name : MMD114A0CC1788_MD00.A2L
    // a2l Link : icdm:a2lid,28309656893-2155255001
    PidcA2lTreeStructureServiceClient pidcA2lTreeStructureServiceClient = new PidcA2lTreeStructureServiceClient();
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModelForA2l1 =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID4_1);
    // variant map will be empty for no-variant case
    assertTrue(pidcA2lTreeStructuresModelForA2l1.getPidcVariantMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getA2lRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getA2lWpMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getRvwQnaireRespMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(!pidcA2lTreeStructuresModelForA2l1.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModelForA2l1);

    // a2l name : MMD114A0CC1788_MD00_withGroups.A2L
    // a2l Link : icdm:a2lid,28300315739-2189855001
    PidcA2lTreeStructureModel pidcA2lTreeStructuresModelForA2l2 =
        pidcA2lTreeStructureServiceClient.getPidcA2lTreeStructuresModel(PIDC_A2L_ID4_2);
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getPidcVariantMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getA2lRespMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getA2lWpMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getRvwQnaireRespMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getRvwQnaireRespVersMap().isEmpty());
    assertTrue(pidcA2lTreeStructuresModelForA2l2.getVarRespWpQniareMap().isEmpty());
    printVarRespWpQnaireData(pidcA2lTreeStructuresModelForA2l2);
  }

  /**
   * @param pidcA2lTreeStructuresModel
   */
  private void printVarRespWpQnaireData(final PidcA2lTreeStructureModel pidcA2lTreeStructuresModel) {
    if (!pidcA2lTreeStructuresModel.getVarRespWpQniareMap().isEmpty() &&
        !pidcA2lTreeStructuresModel.getPidcVariantMap().isEmpty()) {
      for (Entry<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQnaireEntrySet : pidcA2lTreeStructuresModel
          .getVarRespWpQniareMap().entrySet()) {
        PidcVariant pidcVariant = pidcA2lTreeStructuresModel.getPidcVariantMap().get(varRespWpQnaireEntrySet.getKey());
        CDMLogger.getInstance().info("---------------------------------------------------");
        CDMLogger.getInstance().info("Pidc Variant : " + pidcVariant.getName());
        for (Entry<Long, Map<Long, Set<Long>>> respWpQnaireEntrySet : varRespWpQnaireEntrySet.getValue().entrySet()) {
          A2lResponsibility a2lResponsibility =
              pidcA2lTreeStructuresModel.getA2lRespMap().get(respWpQnaireEntrySet.getKey());
          CDMLogger.getInstance().info("Responsible : " + a2lResponsibility.getName());
          for (Entry<Long, Set<Long>> wpQnaireEntrySet : respWpQnaireEntrySet.getValue().entrySet()) {
            A2lWorkPackage a2lWorkPackage = pidcA2lTreeStructuresModel.getA2lWpMap().get(wpQnaireEntrySet.getKey());
            CDMLogger.getInstance().info("Workpackage : " + a2lWorkPackage.getName());
            for (Long qnaireRespId : wpQnaireEntrySet.getValue()) {
              if (CommonUtils.isNotNull(qnaireRespId) &&
                  CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId)) {
                RvwQnaireResponse rvwQnaireResponse =
                    pidcA2lTreeStructuresModel.getRvwQnaireRespMap().get(qnaireRespId);
                CDMLogger.getInstance().info("Questionnaire Response : " + rvwQnaireResponse.getName());
              }
            }
          }
        }
      }
    }
  }

}
