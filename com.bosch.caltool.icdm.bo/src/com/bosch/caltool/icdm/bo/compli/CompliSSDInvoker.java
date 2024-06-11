/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDMessage;

/**
 * @author rgo7cob
 */
public class CompliSSDInvoker {


  /**
   * Hex file name
   */
  private String hexFileName;
  private String errorFilePath;
  private String releseErrorMessage;

  private String bcFcError;

  private String unMappedBcs;

  /**
   * invoke final release
   *
   * @param baseFilePath can be server path or the temp path
   * @param serviceHandler serviceHandler
   * @return the file path
   * @throws IcdmException IcdmException
   */
  public String invokeSSDRelease(final String baseFilePath, final SSDServiceHandler serviceHandler)
      throws IcdmException {


    SSDMessage ssdMessage = serviceHandler.contReleaseWithfeaValSelection(serviceHandler.getFeaValueForSelection());

    // on getting success message from this step, get error model and file path

    if (ssdMessage.equals(SSDMessage.RELEASECREATED)) {


      Map<String, SortedSet<String>> releaseErrors = serviceHandler.getReleaseErrors();
      if (CommonUtils.isNotEmpty(releaseErrors)) {
        StringBuilder strBuilder = new StringBuilder();
        for (Entry<String, SortedSet<String>> entry : releaseErrors.entrySet()) {
          strBuilder.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        this.releseErrorMessage = strBuilder.toString();
      }


    }
    else {
      throw new IcdmException(ssdMessage.getDescription());
    }

    Map<String, SortedSet<String>> releaseErrors = serviceHandler.getReleaseErrors();


    createSSDErrorFile(releaseErrors, baseFilePath);

    String ssdFilePath = modifyFilePath(baseFilePath);
    serviceHandler.getReleaseReportsForCompli(ssdFilePath);
    return ssdFilePath;
  }


  /**
   * @param releaseErrors
   * @param baseFilePath
   * @throws IcdmException
   */
  private void createSSDErrorFile(final Map<String, SortedSet<String>> releaseErrors, final String baseFilePath)
      throws IcdmException {
    if ((releaseErrors != null) && !releaseErrors.isEmpty()) {
      String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
      String filePath = baseFilePath + File.separator + "SSDError" + currentDate + ".err";

      this.errorFilePath = filePath;

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {

        for (Entry<String, SortedSet<String>> entry : releaseErrors.entrySet()) {
          writer.append(entry.getKey()).append(":");
          int valSize = entry.getValue().size();
          int count = 1;
          for (String errStr : entry.getValue()) {
            writer.append(errStr);

            if (valSize > count) {
              writer.append(",");
            }
            count++;
          }
          writer.append("\n");
        }

        if (this.bcFcError != null) {
          writer.append(this.bcFcError);
        }
        writer.append("\n");
        if (this.unMappedBcs != null) {
          writer.append(this.unMappedBcs);
        }
        writer.append("\n");
      }
      catch (IOException exp) {
        throw new IcdmException(exp.getMessage(), exp);
      }


    }

  }


  /**
   * @param baseFilePath
   * @return the SSD File path
   */
  private String modifyFilePath(final String baseFilePath) {
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    String ssdFilePath;
    if (CommonUtils.isNotNull(this.hexFileName)) {
      ssdFilePath =
          baseFilePath + File.separator + (this.hexFileName) + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate;
    }
    else {
      ssdFilePath = baseFilePath + File.separator + CDRConstants.COMPLI_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          currentDate;
    }
    return ssdFilePath;
  }


  /**
   * @param ruleList ruleList
   * @return the map of param and List<CDRRule>
   */
  public Map<String, List<CDRRule>> getRulesMap(final List<CDRRule> ruleList) {
    Map<String, List<CDRRule>> ssdRulesForCompliance = new HashMap<>();

    for (CDRRule cdrRule : ruleList) {
      if ((cdrRule.getSsdCase() == SSDCase.CSSD) || (cdrRule.getSsdCase() == SSDCase.SSD2RV)) {
        List<CDRRule> listOfRules = ssdRulesForCompliance.get(cdrRule.getParameterName());
        if (null == listOfRules) {
          listOfRules = new ArrayList<>();
        }
        listOfRules.add(cdrRule);
        ssdRulesForCompliance.put(cdrRule.getParameterName(), listOfRules);
      }
    }
    return ssdRulesForCompliance;
  }

  /**
   * @param ruleList ruleList
   * @return the map of param and List<CDRRule>
   */
  public Map<String, List<CDRRule>> getQssdRulesMap(final List<CDRRule> ruleList) {
    Map<String, List<CDRRule>> ssdRulesForQssd = new HashMap<>();

    for (CDRRule cdrRule : ruleList) {
      if ((cdrRule.getSsdCase() == SSDCase.QSSD)) {
        List<CDRRule> listOfRules = ssdRulesForQssd.get(cdrRule.getParameterName());
        if (null == listOfRules) {
          listOfRules = new ArrayList<>();
        }
        listOfRules.add(cdrRule);
        ssdRulesForQssd.put(cdrRule.getParameterName(), listOfRules);
      }
    }
    return ssdRulesForQssd;
  }

  /**
   * @param hexFileName hexFileName
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName == null ? "" : hexFileName;
  }


  /**
   * @return the errorFilePath
   */
  public String getErrorFilePath() {
    return this.errorFilePath;
  }


  /**
   * @return the releseErrorMessage
   */
  public String getReleseErrorMessage() {
    return this.releseErrorMessage;
  }


  /**
   * @param bcFcError the bcFcError to set
   */
  public void setBcFcError(final String bcFcError) {
    this.bcFcError = bcFcError;
  }


  /**
   * @param unMappedBcs the unMappedBcs to set
   */
  public void setUnMappedBcs(final String unMappedBcs) {
    this.unMappedBcs = unMappedBcs;
  }


}
