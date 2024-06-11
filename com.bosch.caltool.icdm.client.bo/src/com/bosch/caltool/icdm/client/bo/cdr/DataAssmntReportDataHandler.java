/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_STATUS;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author hnu1cob
 */
public class DataAssmntReportDataHandler extends AbstractClientDataHandler {


  private final DataAssessmentReport dataAssessmentReport;

  /**
   * cdrReportDataHandler 1. will be null, while opening Baseline 2. will be filled during Data Assessment Report
   */
  private final CompHexWithCDFxDataHandler compHexWithCdfxDataHandler;

  private final Map<String, A2LParameter> a2lParamMap;

  private final Map<String, Characteristic> characteristicsMap;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.DA_DATA_ASSESSMENT, chData -> {
      
      Long pidcA2lId = ((DaDataAssessment) CnsUtils.getModel(chData)).getPidcA2lId().longValue();
      return getDataAssessmentReport().getPidcA2lId().equals(pidcA2lId);
      
    });
    
    registerCnsActionLocal(this::refreshDataAssessment, MODEL_TYPE.DA_DATA_ASSESSMENT);
    registerCnsAction(this::refreshDataAssessmentRemote, MODEL_TYPE.DA_DATA_ASSESSMENT);
  }

  private void refreshDataAssessment(final ChangeData<?> chData) {
    if (chData.getChangeType() == CHANGE_OPERATION.UPDATE) {
      DaDataAssessment newData = (DaDataAssessment) chData.getNewData();
      getDataAssessmentReport().getDataAssmntBaselines().remove(newData);
      getDataAssessmentReport().getDataAssmntBaselines().add(newData);
    }
    else if (chData.getChangeType() == CHANGE_OPERATION.CREATE) {
      DaDataAssessment newData = (DaDataAssessment) chData.getNewData();
      getDataAssessmentReport().getDataAssmntBaselines().add(newData);
    }
  }
  
  
  private void refreshDataAssessmentRemote(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    
    for (ChangeDataInfo chData : chDataInfoMap.values()) {
      DaDataAssessment newData = null;
      try {
        newData = new DaDataAssessmentServiceClient().get(chData.getObjId());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
          
      if (chData.getChangeType() == CHANGE_OPERATION.UPDATE) {
        getDataAssessmentReport().getDataAssmntBaselines().remove(newData);
        getDataAssessmentReport().getDataAssmntBaselines().add(newData);
      }
      else if(chData.getChangeType() == CHANGE_OPERATION.CREATE) {
        getDataAssessmentReport().getDataAssmntBaselines().add(newData);
      }
      
    }
    
    
  }

  /**
   * @param dataAssessmentReport Data Assessment Report Model
   * @param compHexWithCdfxDataHandler CompHexWithCdfxDataHandler, will be filled only for Data Assessment Report not
   *          for baseline
   * @param a2lParamMap Map of Parameter Name and A2LParameter Object
   * @param characteristicsMap Map of Characteristics name and Characteristics model
   */
  public DataAssmntReportDataHandler(final DataAssessmentReport dataAssessmentReport,
      final CompHexWithCDFxDataHandler compHexWithCdfxDataHandler, final Map<String, A2LParameter> a2lParamMap,
      final Map<String, Characteristic> characteristicsMap) {
    this.dataAssessmentReport = dataAssessmentReport;
    this.compHexWithCdfxDataHandler = compHexWithCdfxDataHandler;
    this.a2lParamMap = a2lParamMap;
    this.characteristicsMap = characteristicsMap;
  }

  /**
   * @return the dataAssessmentReport
   */
  public DataAssessmentReport getDataAssessmentReport() {
    return this.dataAssessmentReport;
  }

  /**
   * @param dataAssessmentReport data assessment report
   */
  public static void fillQnairesRvdUserDisplayName(final DataAssessmentReport dataAssessmentReport) {
    if (CommonUtils.isNotEmpty(dataAssessmentReport.getDataAssmntQnaires())) {
      Set<String> reviewedByUsers =
          dataAssessmentReport.getDataAssmntQnaires().stream().map(DataAssessmentQuestionnaires::getQnaireReviewedUser)
              .filter(CommonUtils::isNotNull).collect(Collectors.toSet());
      try {
        if (CommonUtils.isNotEmpty(reviewedByUsers)) {
          Map<String, User> users = new UserServiceClient().getApicUserByUsername(new ArrayList<>(reviewedByUsers));
          if (CommonUtils.isNotEmpty(users)) {
            dataAssessmentReport.getDataAssmntQnaires().stream().forEach(
                qnaire -> qnaire.setQnaireRvdUserDisplayName(!CommonUtils.isEmptyString(qnaire.getQnaireReviewedUser())
                    ? users.get(qnaire.getQnaireReviewedUser()).getDescription() : ""));
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param selectedElement DaDataAssessment
   * @return fileName
   */
  public static String getFileName(final DaDataAssessment selectedElement) {
    String fileName;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(selectedElement.getBaselineName());
    stringBuilder.append(selectedElement.getCreatedDate());
    stringBuilder.append("Archieved.zip");
    fileName = FileNameUtil.formatFileName(stringBuilder.toString(), ApicConstants.SPL_CHAR_PTRN);
    if (fileName.length() > 30) {
      fileName = StringUtils.abbreviateMiddle(fileName, "_", 30);
    }
    return fileName;
  }

  /**
   * Type of assessment UI text
   *
   * @param typeOfAssessmentCode assessment type code
   * @return UI text
   */
  public static String getTypeOfAssessmentDisplayText(final String typeOfAssessmentCode) {
    return CommonUtils.isEqual(typeOfAssessmentCode, "D") ? "Development" : "Series release";
  }

  /**
   * @param daCompHexParam DaCompareHexParam
   * @return display qnaire status
   */
  public String getQnaireStatus(final DaCompareHexParam daCompHexParam) {
    String result;

    String qnaireRespVersStatus = daCompHexParam.getQnaireStatus();
    // questionnaire version status
    if (CommonUtils.isNotEmptyString(qnaireRespVersStatus) &&
        CommonUtils.isNotNull(CDRConstants.DA_QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus))) {

      if (CommonUtils.isEqual(CDRConstants.DA_QS_STATUS_TYPE.N_A.getDbType(), qnaireRespVersStatus)) {
        result = CDRConstants.RVW_QNAIRE_STATUS_N_A;
      }
      else {
        result = CDRConstants.DA_QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus).getUiType();
      }
    }
    else {
      result = qnaireRespVersStatus;
    }
    return result;
  }

  /**
   * @param daCompHexParam DaCompareHexParam
   * @return display qnaire status
   */
  public String getQnaireStatusTooltip(final DaCompareHexParam daCompHexParam) {
    String result;

    String qnaireRespVersStatus = daCompHexParam.getQnaireStatus();
    // questionnaire version status
    if (CommonUtils.isNotEmptyString(qnaireRespVersStatus) &&
        CommonUtils.isNotNull(CDRConstants.DA_QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus))) {
      result = CDRConstants.DA_QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus).getUiType();
    }
    else {
      result = qnaireRespVersStatus;
    }
    return result;
  }

  /**
   * @param daCompHexParam param in Data assessment report
   * @return Result compli result
   */
  public String getResult(final DaCompareHexParam daCompHexParam) {
    String result;
    if (daCompHexParam.isQssdParameter()) {
      if (daCompHexParam.isCompli() && !CompliResValues.OK.equals(daCompHexParam.getCompliResult())) {
        result = daCompHexParam.getCompliResult().getUiValue();
      }
      else {
        result = daCompHexParam.getQssdResult().getUiValue();
      }
    }
    else {
      result = daCompHexParam.getCompliResult().getUiValue();
    }
    return result;
  }

  /**
   * Gets the equal status.
   *
   * @param daCompHexParam the compared obj
   * @return the equal status
   */
  public String getEqualStatus(final DaCompareHexParam daCompHexParam) {
    if (daCompHexParam.isNeverReviewed()) {
      return ApicConstants.EMPTY_STRING;
    }
    return daCompHexParam.isEqual() ? CommonUtils.concatenate(RESULT_STATUS.EQUAL.getText(), "  ")
        : RESULT_STATUS.NOT_EQUAL.getText();
  }

  /**
   * Gets the reviewed status.
   *
   * @param daCompHexParam the compared obj
   * @return the reviewed status
   */
  public String getReviewedStatus(final DaCompareHexParam daCompHexParam) {
    if (daCompHexParam.isNeverReviewed()) {
      return ApicConstants.NEVER_REVIEWED;
    }
    if (daCompHexParam.isReviewed()) {
      // Space is appended to differentiate the filter text 'Reviewed' from other similar texts like 'Never Reviewed'
      return CommonUtils.concatenate(ApicConstants.REVIEWED, "  ");
    }
    return ApicConstants.NOT_FINALLY_REVIEWED;
  }

  /**
   * @param calData CalData Object
   * @return CalData SimpleDisplayValue
   */
  public String getSimpleDisplayValueFromCalData(final CalData calData) {
    if ((calData == null) || (calData.getCalDataPhy() == null)) {
      return "";
    }
    return calData.getCalDataPhy().getSimpleDisplayValue();
  }

  /**
   * @param calDataInByte CalData in Byte Array
   * @return simpleDisplayValue of CalData
   */
  public String getCalDataStringFromBytes(final byte[] calDataInByte) {
    String calData = "";
    try {
      calData = getSimpleDisplayValueFromCalData(CalDataUtil.getCalDataObj(calDataInByte));
    }
    catch (ClassNotFoundException | IOException exp) {
      CDMLogger.getInstance().error("Exception occured while creating byte array for caldata obj" + exp.getMessage(),
          exp);
    }
    return calData;
  }

  /**
   * @return the compHexWithCdfxDataHandler
   */
  public CompHexWithCDFxDataHandler getCompHexWithCdfxDataHandler() {
    return this.compHexWithCdfxDataHandler;
  }

  /**
   * @return the a2lParamMap
   */
  public Map<String, A2LParameter> getA2lParamMap() {
    return this.a2lParamMap;
  }

  /**
   * @return the characteristicsMap
   */
  public Map<String, Characteristic> getCharacteristicsMap() {
    return this.characteristicsMap;
  }
}
