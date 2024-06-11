/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.FeatureAttributeAdapter;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.cdr.jpa.bo.CDRResult;
import com.bosch.caltool.cdr.jpa.bo.SSDFeaICDMAttrComp;
import com.bosch.caltool.cdr.jpa.bo.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDRelease;

/**
 * @author rgo7cob
 */
public class SSDReleaseService {


  private final ApicDataProvider apicDataProvider;

  private final PIDCVersion version;

  private final PIDCVariant variant;

  private final SSDRelease selSSDRel;


  /**
   * @param apicDataProvider
   * @param version
   * @param variant
   * @param selSSDRel
   */
  public SSDReleaseService(final ApicDataProvider apicDataProvider, final PIDCVersion version,
      final PIDCVariant variant, final SSDRelease selSSDRel) {
    this.apicDataProvider = apicDataProvider;
    this.version = version;
    this.variant = variant;
    this.selSSDRel = selSSDRel;
  }


  /**
   * @param ssdRelesesBySwVersionId
   * @param mappedSSDReleases
   * @param adapter
   */
  private void setMappedSSDRel(final List<SSDRelease> ssdRelesesBySwVersionId, final List<SSDRelease> mappedSSDReleases,
      final FeatureAttributeAdapter adapter) {
    for (SSDRelease ssdRelease : ssdRelesesBySwVersionId) {
      List<FeatureValueModel> dependencyList = ssdRelease.getDependencyList();
      boolean hasErrors = false;
      for (FeatureValueModel featureValueModel : dependencyList) {
        try {
          adapter.getAttrValModel(featureValueModel, this.variant, this.version);
        }
        catch (IcdmException exp) {
          hasErrors = true;
          CDMLogger.getInstance().error("The feature value to attr value conversion is failed for the feature " +
              featureValueModel.getFeatureText() + "because of " + exp);
        }

      }
      if (!hasErrors) {
        mappedSSDReleases.add(ssdRelease);
      }
    }
  }

  /**
   * @param cdrReviewData cdrReviewData
   * @param calDataReview calDataReview
   * @return
   */
  public List<SSDRelease> getSSDRelForVersion(final CalibrationDataReview calDataReview) {
    List<SSDRelease> ssdRelesesBySwVersionId = calDataReview.getSSDRelesesBySwVersionId();
    List<SSDRelease> mappedSSDReleases = new ArrayList<>();
    FeatureAttributeAdapter adapter = new FeatureAttributeAdapter(this.apicDataProvider);
    if (ssdRelesesBySwVersionId != null) {
      setMappedSSDRel(ssdRelesesBySwVersionId, mappedSSDReleases, adapter);
    }
    return mappedSSDReleases;
  }


  /**
   * @param dependencyList dependencyList
   * @return the list of fea val Dpenedency
   */
  public List<SSDFeatureICDMAttrModel> resolveFeaValDep(final List<FeatureValueModel> dependencyList) {
    List<SSDFeatureICDMAttrModel> ssdFeaAttrList = new ArrayList<>();

    for (FeatureValueModel featureValueModel : dependencyList) {
      FeatureAttributeAdapter adapter = new FeatureAttributeAdapter(this.apicDataProvider);
      try {
        AttributeValueModel attrValModel = adapter.getAttrValModel(featureValueModel, this.variant, this.version);
        SSDFeatureICDMAttrModel ssdFeaAttr = new SSDFeatureICDMAttrModel(featureValueModel, attrValModel);
        ssdFeaAttrList.add(ssdFeaAttr);

      }
      catch (Exception exp) {
        CDMLogger.getInstance().error("The feature value to attr value conversion is failed for the feature " +
            featureValueModel.getFeatureText() + "because of " + exp);
      }
    }

    Collections.sort(ssdFeaAttrList, new SSDFeaICDMAttrComp());
    return ssdFeaAttrList;
  }


  /**
   * @param calDataReview calDataReview
   * @param ssdSoftwareVersionID ssdSoftwareVersionID
   * @throws Exception Exception
   */
  public void setRulesForSSDRel(final CalibrationDataReview calDataReview, final Long ssdSoftwareVersionID)
      throws Exception {

    List<CDRRule> cdrRules = calDataReview.readRuleForRelease(this.selSSDRel.getReleaseId());
    calDataReview.getCdrData().setSelSSDRelease(this.selSSDRel);
    // String ssdFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + "ssdRelease";
    String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String ssdFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + CDRConstants.SECONDARY_REVIEW +
        CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.SSD_RULES + CDRConstants.SSD_FILE_PATH_SEPARATOR +
        currentDate;
    String reprtPath = calDataReview.getReleaseReportsByReleaseId(ssdFilePath, this.selSSDRel.getReleaseId());
    ReviewRuleSetData ruleData = new ReviewRuleSetData();
    ruleData.setSSDFilePath(reprtPath);
    ruleData.setSsdReleaseID(this.selSSDRel.getReleaseId().longValue());
    ruleData.setSsdVersionID(ssdSoftwareVersionID);
    setRules(ruleData, cdrRules);
    calDataReview.getCdrData().getRuleSetDataList().add(ruleData);
  }

  /**
   * @param ruleData
   * @param cdrRules
   */
  private void setRules(final ReviewRuleSetData ruleData, final List<CDRRule> cdrRules) {
    if (null != cdrRules) {
      Map<String, List<CDRRule>> ssdRulesMap = new HashMap<>();

      for (CDRRule cdrRule : cdrRules) {
        List<CDRRule> listOfRules = ssdRulesMap.get(cdrRule.getParameterName());
        if (null == listOfRules) {
          listOfRules = new ArrayList<>();
        }
        listOfRules.add(cdrRule);
        ssdRulesMap.put(cdrRule.getParameterName(), listOfRules);
      }

      if ((ssdRulesMap.keySet() != null)) {
        ruleData.setSSDRules(ssdRulesMap);
      }
    }

  }


  /**
   * @param calDataReview calDataReview
   * @param selSSDPath selSSDPath
   */
  public void setRuleDataForSSDFile(final CalibrationDataReview calDataReview, final String selSSDPath) {
    ReviewRuleSetData ruleData = new ReviewRuleSetData();
    ruleData.setSSDFilePath(selSSDPath);
    ruleData.setSSDFileReview(true);
    calDataReview.getCdrData().getRuleSetDataList().add(ruleData);

  }


  /**
   * Download the file to the local folder
   *
   * @param parentCdrResult parentCdrResult
   * @return the download file path
   */
  public String downloadDBFiles(final CDRResult parentCdrResult) {

    SortedSet<IcdmFile> outputFiles = parentCdrResult.getRuleFile();
    IcdmFile fileToDownload = null;
    if (outputFiles != null) {
      // Select the Input file from the fun / Lab file
      for (IcdmFile icdmFile : outputFiles) {
        if (icdmFile.getFileName().contains("ssdRelease")) {
          fileToDownload = icdmFile;
          break;
        }
      }
      try {
        if (fileToDownload != null) {
          final String fileNameStr = System.getProperty("java.io.tmpdir") + fileToDownload.getFileName();
          final byte[] parentFileBytes = fileToDownload.getFiles().get(fileToDownload.getFileName());
          final FileOutputStream fos = new FileOutputStream(fileNameStr);
          fos.write(parentFileBytes);
          fos.close();
          return fileNameStr;
        }
      }
      catch (IOException excep) {
        CDMLogger.getInstance().debug(excep.getMessage(), excep);
        return null;
      }

    }
    return null;
  }

}
