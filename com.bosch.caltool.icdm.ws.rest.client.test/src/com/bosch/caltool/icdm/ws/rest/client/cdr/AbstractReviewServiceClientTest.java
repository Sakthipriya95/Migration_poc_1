/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_USER_TYPE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.review.FileData;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.cdr.review.ResultData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.cdr.review.RulesData;
import com.bosch.caltool.icdm.model.cdr.review.UserData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public abstract class AbstractReviewServiceClientTest extends AbstractRestClientTest {

  /**
   * Create a basic reusable review input
   *
   * @param rvwName review name, will be added to the description of the review
   * @param reviewFile reviewFile
   * @param sourceType sourceType
   * @param funcName funcName
   * @param pidcA2lId pidcA2lId
   * @param varID varID
   * @return ReviewData
   */
  protected static ReviewInput sCreateInput(final String rvwName, final String reviewFile, final String sourceType,
      final String funcName, final Long pidcA2lId, final Long varID) {

    ReviewInput ret = new ReviewInput();

    // Previous result - parent/cancelled
    ResultData resultData = new ResultData();
    ret.setResultData(resultData);

    // Genereal review info
    ret.setDescription("JUnit - " + rvwName);
    ret.setReviewType("S");

    // Project ID card, variant and A2L file
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(pidcA2lId);
    pidcData.setSelPIDCVariantId(varID);
    ret.setPidcData(pidcData);

    // Participating users
    UserData userData = new UserData();
    userData.setSelAuditorId(787631716L); // Aberle, Werner (PS-EC/ESD4)
    userData.setSelCalEngineerId(664016L); // Brundha, Rudramoorthy (RBEI/ETB5)
    ret.setUserData(userData);

    // Rules to use
    RulesData rulesData = new RulesData();
    rulesData.setCommonRulesPrimary(true);
    ret.setRulesData(rulesData);

    // Functions to review
    SortedSet<String> funcNames = new TreeSet<>();
    if ((funcName != null) && !funcName.isEmpty()) {
      String[] funNames = funcName.split(",");
      for (String name : funNames) {
        funcNames.add(name.trim());
      }
    }
    ret.setSelReviewFuncs(funcNames);

    // Files to review
    Set<String> reviewFilesPath = new TreeSet<>();
    if ((reviewFile != null) && !reviewFile.isEmpty()) {
      String[] fileNames = reviewFile.split(",");
      for (String name : fileNames) {
        reviewFilesPath.add(name.trim());
      }
    }
    FileData fileData = new FileData();
    fileData.setSelFilesPath(reviewFilesPath);
    ret.setFileData(fileData);
    ret.setSourceType(sourceType);


    return ret;
  }

  /**
   * Create a basic reusable review input
   *
   * @param reviewFile reviewFile
   * @param sourceType sourceType
   * @param funcName funcName
   * @param pidcA2lId pidcA2lId
   * @param varID varID
   * @return ReviewData
   */
  protected ReviewInput createInput(final String reviewFile, final String sourceType, final String funcName,
      final Long pidcA2lId, final Long varID) {

    return sCreateInput(this.testMethodName.getMethodName(), reviewFile, sourceType, funcName, pidcA2lId, varID);
  }

  /**
   * Execute data review and print output
   *
   * @param reviewInput review input
   * @return review output
   * @throws ApicWebServiceException error while executing service call
   */
  protected ReviewOutput executeReview(final ReviewInput reviewInput) throws ApicWebServiceException {
    ReviewOutput reviewSummary = new ReviewServiceClient().performReview(reviewInput);
    checkReviewSummary(reviewSummary);
    CDRReviewResultServiceClient servClient = new CDRReviewResultServiceClient();
    ReviewResultEditorData editorData = servClient.getRvwResultEditorData(reviewSummary.getCdrResult().getId(), null);
    validateReviewResults(reviewInput, editorData);
    validateRvwPartcipant(reviewInput, editorData);
    return reviewSummary;
  }

  /**
   * @param reviewSummary response from review process service
   */
  protected static void checkReviewSummary(final ReviewOutput reviewSummary) {

    assertNotNull("review output not null", reviewSummary);
    assertNotNull("review result not null", reviewSummary.getCdrResult());

    LOG.debug("Review summary : ");
    LOG.debug("    Result id                      - {}", reviewSummary.getCdrResult().getId());
    LOG.debug("    A2l file name                  - {}", reviewSummary.getA2lFileName());
    LOG.debug("    Auditor name                   - {}", reviewSummary.getAuditorName());
    LOG.debug("    Calibration Eng name           - {}", reviewSummary.getCalEngineerName());
    LOG.debug("    Pidc variant name              - {}", reviewSummary.getPidcVariantName());
    LOG.debug("    Pidc version name              - {}", reviewSummary.getPidcVersion().getName());
    LOG.debug("    Group wp name                  - {}", reviewSummary.getWorkPackageGroupName());
    LOG.debug("    No. of reviewed funcs          - {}", reviewSummary.getNoOfReviewedFunctions());
    LOG.debug("    No. of reviewed param          - {}", reviewSummary.getNoOfReviewedParam());
    LOG.debug("    Params not reviewed in a2l     - {}", reviewSummary.getParamsNotReviewedInA2l());
    LOG.debug("    Params not reviewed in ruleset - {}", reviewSummary.getParamsNotRvwdInRuleset());
    LOG.debug("    Params without rule            - {}", reviewSummary.getParamsNotRvwdWithoutRule());

  }

  /**
   * Verify review results info
   *
   * @param reviewInput review input
   * @param editorData review result editor data
   */
  protected static void validateReviewResults(final ReviewInput reviewInput, final ReviewResultEditorData editorData) {

    assertNotNull("A2L File name should not be null", editorData.getA2lFileName());
    assertFalse("Participants map should not be empty", editorData.getParticipantsMap().isEmpty());
    assertNotNull("Created User should not be null", editorData.getCreatedUser());
    assertNotNull("PIDC should not be null", editorData.getPidc());
    assertNotNull("PIDC A2L should not be null", editorData.getPidcA2l());
    assertNotNull("PIDC Version should not be null", editorData.getPidcVers());

    assertFalse("Param map should not be null or empty",
        ((editorData.getParamMap() == null) || editorData.getParamMap().isEmpty()));

    assertFalse("Internal files map should not be empty", editorData.getIcdmFiles().isEmpty());

    // if variant is available
    if (reviewInput.getPidcData().getSelPIDCVariantId() != null) {
      assertNotNull("Pidc variant should not be null", editorData.getFirstVariant());
      assertEquals("Pidc variant id is equal", reviewInput.getPidcData().getSelPIDCVariantId(),
          editorData.getFirstVariant().getId());
    }

    assertFalse("A2L Responsibility map should not be empty", editorData.getA2lResponsibilityMap().isEmpty());
    assertFalse("A2L workpackage map should not be empty", editorData.getA2lWpMap().isEmpty());
  }


  /**
   * Validate review participants in the review result
   *
   * @param reviewInput review input
   * @param editorData review result editor's input data
   */
  protected static void validateRvwPartcipant(final ReviewInput reviewInput, final ReviewResultEditorData editorData) {
    Map<String, Long> partcipantMap = new HashMap<>();
    Set<Long> addlPartSet = new HashSet<>();

    editorData.getParticipantsMap().values().stream().forEach(part -> {
      if (part.getActivityType().equals(REVIEW_USER_TYPE.AUDITOR.getDbType())) {
        partcipantMap.put(CDRConstants.REVIEW_USER_TYPE.AUDITOR.getDbType(), part.getUserId());
      }
      if (part.getActivityType().equals(REVIEW_USER_TYPE.CAL_ENGINEER.getDbType())) {
        partcipantMap.put(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER.getDbType(), part.getUserId());
      }
      if (part.getActivityType().equals(REVIEW_USER_TYPE.ADDL_PARTICIPANT.getDbType())) {
        addlPartSet.add(part.getUserId());
      }
    });

    assertEquals("Auditor's user id should be equal", reviewInput.getUserData().getSelAuditorId(),
        partcipantMap.get(CDRConstants.REVIEW_USER_TYPE.AUDITOR.getDbType()));
    assertEquals("Calibration Engineer's user id should be equal", reviewInput.getUserData().getSelCalEngineerId(),
        partcipantMap.get(CDRConstants.REVIEW_USER_TYPE.CAL_ENGINEER.getDbType()));

    // TODO this equals will fail, even if the data is same
//    assertEquals("Additional Participant's id should be equal", reviewInput.getUserData().getSelParticipantsIds(),
//        addlPartSet);
  }


}