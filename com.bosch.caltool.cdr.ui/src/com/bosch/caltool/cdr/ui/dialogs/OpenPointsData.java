/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;


/**
 * @author bru2cob
 */
public class OpenPointsData {

  /**
   * Open point string
   */
  private String openPoint;
  /**
   * measures string
   */
  private String measures;
  /**
   * responsible id
   */
  private Long responsibleId;
  /**
   * responsible name
   */
  private String responsibleName;
  /**
   * date string
   */
  private String date;

  private boolean result;

  private final RvwQnaireAnswerOpl ansOpenPointObj;

  /**
   * operation type whether it is add/edit/delete characterised by the first letter
   */
  private char oprType;

  /**
   * @return the ansOpenPointObj
   */
  public RvwQnaireAnswerOpl getAnsOpenPointObj() {
    return this.ansOpenPointObj;
  }


  /**
   * @param ansOpenPointObj RvwQnaireAnswerOpl
   */
  public OpenPointsData(final RvwQnaireAnswerOpl ansOpenPointObj) {
    super();
    this.ansOpenPointObj = ansOpenPointObj;
    if (CommonUtils.isNotNull(this.ansOpenPointObj)) {
      this.openPoint = this.ansOpenPointObj.getOpenPoints();
      this.measures = this.ansOpenPointObj.getMeasure();
      this.responsibleId = this.ansOpenPointObj.getResponsible();
      this.responsibleName = this.ansOpenPointObj.getResponsibleName();
      this.date = this.ansOpenPointObj.getCompletionDate();
      this.result = isResultSet();
    }
  }

  /**
   * @return true if result
   */
  public boolean isResultSet() {
    return CommonUtils.getBooleanType(getResult());
  }

  /**
   * @return String
   */
  private String getResult() {
    return CommonUtils.isNotNull(this.ansOpenPointObj) ? this.ansOpenPointObj.getResult() : "";
  }


  /**
   * @return the openPoint
   */
  public String getOpenPoint() {
    return this.openPoint;
  }


  /**
   * @param openPoint the openPoint to set
   */
  public void setOpenPoint(final String openPoint) {
    this.openPoint = openPoint;
  }


  /**
   * @return the measures
   */
  public String getMeasures() {
    return this.measures;
  }


  /**
   * @param measures the measures to set
   */
  public void setMeasures(final String measures) {
    this.measures = measures;
  }


  /**
   * @return the date
   */
  public String getDate() {
    return this.date;
  }

  /**
   * @return String
   */
  public String getDateUIString() {
    java.text.DateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
        Locale.getDefault(Locale.Category.FORMAT));
    java.text.DateFormat df09 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_09,
        Locale.getDefault(Locale.Category.FORMAT));
    Date outputdate = null;
    String outputDateStr = null;
    if ((null != this.ansOpenPointObj) && (null != this.ansOpenPointObj.getCompletionDate()) &&
        this.date.equals(this.ansOpenPointObj.getCompletionDate())) {
      try {
        outputdate = df15.parse(getAnsOpenPointObj().getCompletionDate());
        outputDateStr = df09.format(outputdate);
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), Activator.PLUGIN_ID);
      }
    }
    else {
      outputDateStr = this.date;
    }
    return outputDateStr;
  }

  /**
   * @param date the date to set
   */
  public void setDate(final String date) {
    this.date = date;
  }


  /**
   * @return the responsible id
   */
  public Long getResponsibleId() {
    return this.responsibleId;
  }


  /**
   * @param responsible the responsible id to set
   */
  public void setResponsibleId(final Long responsible) {
    this.responsibleId = responsible;
  }


  /**
   * @return the responsibleName
   */
  public String getResponsibleName() {
    return this.responsibleName;
  }


  /**
   * @param responsibleName the responsibleName to set
   */
  public void setResponsibleName(final String responsibleName) {
    this.responsibleName = responsibleName;
  }


  /**
   * @return the isResult
   */
  public boolean isResult() {
    return this.result;
  }


  /**
   * @return String
   */
  public String resultString() {
    return this.result ? "Y" : "N";
  }

  /**
   * @param isResult the isResult to set
   */
  public void setResult(final boolean isResult) {
    this.result = isResult;
  }


  /**
   * @return the oprType
   */
  public char getOprType() {
    return this.oprType;
  }


  /**
   * @param oprType the oprType to set
   */
  public void setOprType(final char oprType) {
    this.oprType = oprType;
  }


}
