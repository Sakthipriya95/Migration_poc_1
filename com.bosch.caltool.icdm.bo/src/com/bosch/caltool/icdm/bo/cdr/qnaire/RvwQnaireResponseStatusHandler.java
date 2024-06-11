/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;

/**
 * @author say8cob
 */
public class RvwQnaireResponseStatusHandler extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData as input
   */
  public RvwQnaireResponseStatusHandler(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Method used to calculate the status of Review Questionnaire Response
   *
   * @param pidcVersId as pidc Version Id
   * @param variantId as Pidc Variant Id
   * @param qnaireVersId as Questionnaire Version ID
   * @return the status of Questionnaire Response
   * @throws IcdmException as exception
   */
  public String getQnaireRespStatus(final Long pidcVersId, final Long variantId, final Long qnaireVersId)
      throws IcdmException {


    // Constructing 'RvwQnaireResponseModel', as this model is required to resolve dependencies
    RvwQnaireResponseModel qnaireRespModel = new RvwQnaireResponseModel();
    qnaireRespModel.setPidcVersion(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersId));
    if (CommonUtils.isNotNull(variantId)) {
      qnaireRespModel.setPidcVariant(new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId));
    }

    // Resolve the dependencies
    QnaireRespVersDataResolver quesRespEvaluator =
        new QnaireRespVersDataResolver(new QuesRespDataProviderServer(getServiceData(), qnaireVersId, qnaireRespModel));
    Map<Long, Question> visibleQuestions = quesRespEvaluator.loadMainQuestionsForWorkingSet(qnaireRespModel);

    return calculateStatus(visibleQuestions, qnaireVersId);
  }

  /**
   * @param visibleQuestions
   * @param qnaireVersId
   * @throws DataException
   */
  private String calculateStatus(final Map<Long, Question> visibleQuestions, final Long qnaireVersId)
      throws DataException {

    Long notAnswered = 2L;
    Long answered = 1L;
    String status = CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType();
    // Set to calculate Status
    Set<Long> resultSet = new HashSet<>();

    // Get Questionnaire Version
    QuestionnaireVersion qnaireVers = new QuestionnaireVersionLoader(getServiceData()).getDataObjectByID(qnaireVersId);
    // Iterate through visible questions
    visibleQuestions.values().removeIf(question -> question.getHeadingFlag() || question.getDeletedFlag());
    for (Question ques : visibleQuestions.values()) {
      // Get Question Config for each question
      QuestionConfig questionConfig =
          new QuestionConfigLoader(getServiceData()).getDataObjectByID(ques.getQuestionConfigId());
      // check for mandatory fields
      if (!checkForMandatory(questionConfig, qnaireVers)) {
        resultSet.add(answered);
      }
      else {
        resultSet.add(notAnswered);
      }
    }
    if (!resultSet.contains(notAnswered)) {
      status = CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType();
    }
    return status;
  }


  /**
   *
   */
  private boolean checkForMandatory(final QuestionConfig questionConfig, final QuestionnaireVersion qnaireVers) {
    // check if 'Series' is 'Relavant for the Qnaire Version and if it is defined as 'Mandatory' in Question
    String mandatory = QUESTION_CONFIG_TYPE.MANDATORY.getDbType();
    boolean seriesMandatory = CommonUtils.isEqual(questionConfig.getSeries(), mandatory) &&
        CommonUtils.getBooleanType(qnaireVers.getSeriesRelevantFlag());
    // check if 'Measurement' is 'Relavant for the Qnaire Version and if it is defined as 'Mandatory' in Question
    boolean measurementMandatory = CommonUtils.isEqual(questionConfig.getMeasurement(), mandatory) &&
        CommonUtils.getBooleanType(qnaireVers.getMeasurementRelevantFlag());
    // check if 'Remarks' is 'Relavant for the Qnaire Version and if it is defined as 'Mandatory' in Question
    boolean remarksMandatory = CommonUtils.isEqual(questionConfig.getRemark(), mandatory) &&
        CommonUtils.getBooleanType(qnaireVers.getRemarkRelevantFlag());
    // check if 'Answer' is 'Relavant for the Qnaire Version and if it is defined as 'Mandatory' in Question
    boolean resultMandatory = CommonUtils.isEqual(questionConfig.getResult(), mandatory) &&
        CommonUtils.getBooleanType(qnaireVers.getResultRelevantFlag());
    // check if 'Link' is 'Relavant for the Qnaire Version and if it is defined as 'Mandatory' in Question
    boolean linkMandatory = CommonUtils.isEqual(questionConfig.getLink(), mandatory) &&
        CommonUtils.getBooleanType(qnaireVers.getLinkRelevantFlag());
    // return true, if atleast one field is mandatory
    return seriesMandatory || measurementMandatory || linkMandatory || remarksMandatory || resultMandatory;
  }

}
