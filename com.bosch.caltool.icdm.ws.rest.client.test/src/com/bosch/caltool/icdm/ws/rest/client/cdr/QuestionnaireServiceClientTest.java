/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Questionnaire
 *
 * @author bne4cob
 */
public class QuestionnaireServiceClientTest extends AbstractRestClientTest {

  // General questionnaire
  private final static Long QUESTIONNAIRE_ID = 795091696L;
  private final static Long QNAIRE_VERS_ID = 795091697L;
  private static final Long WP_DIV_ID = 795414041L;


  /**
   * Test method for {@link QuestionnaireServiceClient#getAll(boolean)}
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    Map<Long, Questionnaire> retMap = servClient.getAll(true, true);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    testOutput(retMap.get(795091696l));
  }

  /**
   * Test method for {@link QuestionnaireServiceClient#getAllVersions(Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllVersions() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    SortedSet<QuestionnaireVersion> retSet = servClient.getAllVersions(QUESTIONNAIRE_ID);
    assertFalse("Response should not be null or empty", ((retSet == null) || retSet.isEmpty()));
    for (QuestionnaireVersion qversion : retSet) {
      if (qversion.getId() == QNAIRE_VERS_ID) {
        testOutput(qversion);
      }
    }
  }


  /**
   * test output data
   */
  private void testOutput(final Questionnaire obj) {
    assertEquals("Questionnaire Id is equal", Long.valueOf(795091696), obj.getId());
    assertEquals("DescEng is equal", "Questionnaire for DSM: Scheduling, System Interlockings", obj.getDescEng());
    assertNull("DescGer is null", obj.getDescGer());
    assertNotNull("CreatedUser is not null", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertNotNull("Deleted is not null", obj.getDeleted());
    assertEquals("WpDivId is equal", Long.valueOf(795091695), obj.getWpDivId());

  }

  /**
   * Test method for {@link QuestionnaireServiceClient#getAllVersions(Long)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllVersionsNegative() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getAllVersions(-1L);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link QuestionnaireServiceClient#get(Long) }
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    Questionnaire ret = servClient.get(QUESTIONNAIRE_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }


  /**
   * Test method for {@link QuestionnaireServiceClient#getWorkingSet(Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetWorkingSet() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    QuestionnaireVersion ret = servClient.getWorkingSet(QUESTIONNAIRE_ID);
    assertNotNull("Response should not be null ", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link QuestionnaireServiceClient#getWorkingSet(Long)}.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetWorkingSetNegative() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getWorkingSet(-12L);
    fail("Expected exception not thrown");
  }

  /**
   * test output data
   */
  private void testOutput(final QuestionnaireVersion obj) {
    assertEquals("Questionnaire version Id is equal", Long.valueOf(795091697), obj.getId());
    assertEquals("Questionnaire Id is equal", Long.valueOf(795091696), obj.getQnaireId());
    assertEquals("DescEng is equal null", "Working Set", obj.getDescEng());
    assertNull("DescGer is null", obj.getDescGer());
    assertNotNull("CreatedUser is not null", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertNotNull("Active flag is not null", obj.getActiveFlag());
    assertEquals("Major Version Number is equal", Long.valueOf(0), obj.getMajorVersionNum());
    assertNull("Minor version number is null", obj.getMinorVersionNum());
  }

  /**
   * Test method for {@link QuestionnaireServiceClient#create(Questionnaire) ,
   * QuestionnaireServiceClient#update(Questionnaire)}
   *
   * @throws ApicWebServiceException servcie call error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();

    Questionnaire obj = new Questionnaire();
    obj.setNameEng("Junit_" + getRunId() + " testCreateUpdate");
    obj.setNameGer("Junit 1");
    obj.setDescEng("Junit 1");
    obj.setDescGer("Junit 1");
    obj.setDeleted(false);
    obj.setWpDivId(WP_DIV_ID);

    QuestionnaireVersion qnaireVersion = new QuestionnaireVersion();
    qnaireVersion.setQnaireId(obj.getId());
    qnaireVersion.setMajorVersionNum(0L);
    qnaireVersion.setMinorVersionNum(null);
    qnaireVersion.setDescEng("Working Set");
    qnaireVersion.setDescGer(null);

    NodeAccess nodeAccess = new NodeAccess();
    nodeAccess.setUserId(230016L);
    nodeAccess.setName("BNE4COB");
    nodeAccess.setOwner(true);
    nodeAccess.setGrant(true);
    nodeAccess.setOwner(true);
    nodeAccess.setWrite(true);
    nodeAccess.setRead(true);
    nodeAccess.setNodeType(MODEL_TYPE.QUESTIONNAIRE.getTypeCode());

    WorkPackageDivision wrkpkg = new WorkPackageDivision();
    wrkpkg.setName("29) Transient Compensation");
    wrkpkg.setDescription(null);
    wrkpkg.setDivAttrValId(787372417L);
    wrkpkg.setId(WP_DIV_ID); // wp div id
    wrkpkg.setWpId(789117415L); // wp id
    wrkpkg.setDivName("PS-EC (formerly DGS-EC)");

    QnaireCreationModel objModel = new QnaireCreationModel();
    objModel.setQnaire(obj);
    objModel.setQnaireVersion(qnaireVersion);
    objModel.setNodeAccess(nodeAccess);
    objModel.setWpDiv(wrkpkg);


    // Invoke create method
    Questionnaire createdObj = servClient.createQnaireAndVersion(objModel).getDataCreated();

    LOG.info("Questionnaire Name After Create : {} ", createdObj.getNameEng());

    // validate create
    assertNotNull("object not null", createdObj);
    assertEquals("NameEng is equal", "Junit_" + getRunId() + " testCreateUpdate", createdObj.getNameEng());

    // Invoke update method
    createdObj.setDescEng("Junit_" + getRunId() + " Updated");
    Questionnaire updatedObj = servClient.update(createdObj);
    LOG.info("Questionnaire Name After Update : {} ", updatedObj.getNameEng());

    // validate update
    assertNotNull("object not null", updatedObj);
    assertEquals("NameEng is equal", "Junit_" + getRunId() + " testCreateUpdate", updatedObj.getNameEng());
    assertEquals("DescEng is equal", "Junit_" + getRunId() + " Updated", updatedObj.getDescEng());
    assertEquals("DescGer is equal", "Junit 1", updatedObj.getDescGer());
    assertNotNull("Deleted is not null", updatedObj.getDeleted());
    assertEquals("WpDivId is equal", Long.valueOf(WP_DIV_ID), updatedObj.getWpDivId());

  }

  /**
   * Test method for {@link QuestionnaireServiceClient#createQnaireAndVersion(QnaireCreationModel)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testCreateQnaireAndVersion() throws ApicWebServiceException {
    QuestionnaireServiceClient servClient = new QuestionnaireServiceClient();

    Questionnaire obj = new Questionnaire();
    obj.setNameEng("Junit_" + getRunId() + " testCreateQnaireAndVersion");
    obj.setNameGer("Junit 1");
    obj.setDescEng("Junit 1");
    obj.setDescGer("Junit 1");
    obj.setDeleted(false);
    obj.setWpDivId(WP_DIV_ID);

    QuestionnaireVersion qnaireVersion = new QuestionnaireVersion();
    qnaireVersion.setQnaireId(obj.getId());
    qnaireVersion.setMajorVersionNum(0L);
    qnaireVersion.setMinorVersionNum(null);
    qnaireVersion.setDescEng("Working Set");
    qnaireVersion.setDescGer(null);

    NodeAccess nodeAccess = new NodeAccess();
    nodeAccess.setUserId(230016L);
    nodeAccess.setName("BNE4COB");
    nodeAccess.setOwner(true);
    nodeAccess.setGrant(true);
    nodeAccess.setOwner(true);
    nodeAccess.setWrite(true);
    nodeAccess.setRead(true);
    nodeAccess.setNodeType(MODEL_TYPE.QUESTIONNAIRE.getTypeCode());

    WorkPackageDivision wrkpkg = new WorkPackageDivision();
    wrkpkg.setName("29) Transient Compensation");
    wrkpkg.setDescription(null);
    wrkpkg.setDivAttrValId(787372417L);
    wrkpkg.setId(WP_DIV_ID); // wp div id
    wrkpkg.setWpId(789117415L); // wp id
    wrkpkg.setDivName("PS-EC (formerly DGS-EC)");

    QnaireCreationModel objModel = new QnaireCreationModel();
    objModel.setQnaire(obj);
    objModel.setQnaireVersion(qnaireVersion);
    objModel.setNodeAccess(nodeAccess);
    objModel.setWpDiv(wrkpkg);

    // Invoke create method
    DataCreationModel<Questionnaire> createdObj = servClient.createQnaireAndVersion(objModel);
    // validate
    Questionnaire retObj = createdObj.getDataCreated();
    assertNotNull("object not null", createdObj);
    assertEquals("DescEng is equal", "Junit 1", retObj.getDescEng());
    assertEquals("DescGer is equal", "Junit 1", retObj.getDescGer());
    assertNotNull("Deleted is not null", retObj.getDeleted());
    assertEquals("WpDivId is equal", Long.valueOf(WP_DIV_ID), retObj.getWpDivId());
  }

}

