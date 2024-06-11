/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.bosch.ssd.icdm.common.utility.CommonUtil;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.entity.VLdb2Pavast;
import com.bosch.ssd.icdm.entity.VLdb2Ssd2;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDRelease;
import com.bosch.ssd.icdm.model.SoftwareVersion;


/**
 * This Utility class contains the conversion of DB Entity & Object to Interface models
 *
 * @author SSN9COB
 */
public class DBToModelUtil {

  private DBToModelUtil() {
    // hidden constructor
  }
  
/**
   * Method to convert SQLResult [readRulesMapping] in @VLdb2SSD2 class to CDRRule
   *
   * @param dbObject cdbObject
   * @param isReviewRule reviewRule
   * @return CDRRule
   */
  public static CDRRule convertToCDRRule(final Object[] dbObject, final boolean isReviewRule) {
    /*
     * Order of Mapping in the Object[] 1. VLDB2SSD2 2. VLDB2PAVAST 3. Label Name 4. DCM String 5. Feature Values 6.
     * Maturity 7. Rule Owner 8. CoC
     */
    CDRRule rule = new CDRRule();

    if (Objects.nonNull(dbObject) && (dbObject.length > 0)) {
      setRuleInformation(rule, dbObject[0]);
      setLabelInformation(rule, dbObject[1]);
      setICDMLabelName(rule, dbObject[2]);
      setDCMInformation(rule, dbObject[3], isReviewRule);
      setFeatureValueDependencies(rule, dbObject[4]);
      setMaturiyInformation(rule, dbObject[5]);
    }
    return rule;
  }

  /**
   * Method to convert SQLResult [readRulesMapping] in VLdb2SSD2 class to CDRRuleExt
   *
   * @param dbObject cdbObject
   * @return CDRRule
   */
  public static CDRRuleExt convertToCDRRuleExt(final Object[] dbObject) {
    CDRRuleExt rule = new CDRRuleExt();

    if (Objects.nonNull(dbObject) && (dbObject.length > 0)) {
      // get normal rule infor
      CDRRule mainRule = convertToCDRRule(dbObject, false);
      // copy the value from CDRRule to CDRRuleExt used by iCDM
      copyValueToExtRule(rule, mainRule);
      // set contact & rule Description value
      setContactInformation(rule, dbObject[6], dbObject[7]);
      setRuleDescriptionInfo(rule, dbObject[0]);
    }
    return rule;
  }

  /**
   * method to copy values from CDRRule to CDRRuleExt instance
   *
   * @param rule CDRRuleExt
   * @param mainRule CDRRule
   */
  private static void copyValueToExtRule(final CDRRuleExt rule, final CDRRule mainRule) {
    rule.setParameterName(mainRule.getParameterName());
    rule.setMaturityLevel(mainRule.getMaturityLevel());
    rule.setRefValueDCMString(mainRule.getRefValueDCMString());
    rule.setDependencyList(mainRule.getDependencyList());
    rule.setValueType(mainRule.getValueType());
    rule.setHint(mainRule.getHint());
    rule.setLabelId(mainRule.getLabelId());
    rule.setLowerLimit(mainRule.getLowerLimit());
    rule.setRefValue(mainRule.getRefValue());
    rule.setUpperLimit(mainRule.getUpperLimit());
    rule.setRevId(mainRule.getRevId());
    rule.setRuleId(mainRule.getRuleId());
    rule.setFormulaDesc(mainRule.getFormulaDesc());
    rule.setFormula(mainRule.getFormula());
    rule.setReviewMethod(mainRule.getReviewMethod());
    rule.setUnit(mainRule.getUnit());
    rule.setRuleCreatedDate(mainRule.getRuleCreatedDate());
    rule.setRuleCreatedUser(mainRule.getRuleCreatedUser());
    rule.setSsdCase(mainRule.getSsdCase());
    rule.setDcm2ssd(mainRule.isDcm2ssd());
  }

  /**
   * Check the object instance and set Rule Description information
   *
   * @param rule
   * @param object
   */
  private static void setRuleDescriptionInfo(final CDRRuleExt rule, final Object ssdObj) {
    if (Objects.nonNull(ssdObj) && (ssdObj instanceof VLdb2Ssd2)) {
      VLdb2Ssd2 ssd = (VLdb2Ssd2) ssdObj;
      rule.setDataDescription(ssd.getDataDescr());
      rule.setInternalAdaptationDescription(ssd.getDescription());
      rule.setHistorieDescription(ssd.getHistorieDescr());
    }
  }

  /**
   * @param rule
   * @param paramName
   */
  private static void setICDMLabelName(final CDRRule rule, final Object paramName) {
    if (Objects.nonNull(paramName)) {
      // SSD-333
      // This is used to add label as the same from iCDM (eg: Variant Coded Label)
      rule.setParameterName(paramName.toString());
    }
  }

  /**
   * Check the object instance and set contact information
   *
   * @param rule
   * @param ruleOwner rule onwer
   * @param coc coc
   */
  private static void setContactInformation(final CDRRuleExt rule, final Object ruleOwner, final Object coc) {
    if (Objects.nonNull(ruleOwner)) {
      rule.setRuleOwner(ruleOwner.toString());
    }
    if (Objects.nonNull(coc)) {
      rule.setCoc(coc.toString());
    }

  }

  /**
   * Check the object instance and set maturity information
   *
   * @param rule
   * @param maturity
   */
  private static void setMaturiyInformation(final CDRRule rule, final Object maturity) {
    if (Objects.nonNull(maturity)) {
      rule.setMaturityLevel(maturity.toString());
    }
  }

  /**
   * Check the object instance and set the DCM information
   *
   * @param rule
   * @param dcmData
   * @param isReviewRule
   */
  private static void setDCMInformation(final CDRRule rule, final Object dcmData, final boolean isReviewRule) {

    // Commented the below conditions in IF Block - For Export in iCDM Data Review
    if (Objects.nonNull(dcmData) /* && rule.isDcm2ssd() && isReviewRule */) {
      rule.setRefValueDCMString(dcmData.toString());
    }
  }

  /**
   * Check the object instance and set the Feature value information
   *
   * @param rule
   * @param feaValDependencies
   * @throws SSDiCDMInterfaceException 
   */
  private static void setFeatureValueDependencies(final CDRRule rule, final Object feaValDependencies) {
    if (Objects.nonNull(feaValDependencies)) {
      ArrayList<FeatureValueModel> featureValueList = new ArrayList<>();
 
      // Split the multiple Feature & Values by ';'
      String[] feaValueList = feaValDependencies.toString().split(";");
      for (String str : feaValueList) {
        // Split the individual Feaval with ','
        String[] feaVal = str.split(",");
        FeatureValueModel model = new FeatureValueModel();
        model.setFeatureId(new BigDecimal(feaVal[0]));
        model.setValueId(new BigDecimal(feaVal[1]));
        if(feaVal.length == 3) {
          model.setOperatorId(new BigDecimal(feaVal[2]));
        }
              
        featureValueList.add(model);
      }
      rule.setDependencyList(featureValueList);
    }
  }

  /**
   * Check the object instance and set the label information
   *
   * @param rule
   * @param pavastObj
   */
  private static void setLabelInformation(final CDRRule rule, final Object pavastObj) {
    if (Objects.nonNull(pavastObj) && (pavastObj instanceof VLdb2Pavast)) {
      VLdb2Pavast pavast = (VLdb2Pavast) pavastObj;
      rule.setParameterName(pavast.getLabel());
      rule.setValueType(CommonUtil.getValueTypeFromCategory(pavast.getCategory()));
    }
  }

  /**
   * Check the object instance and set the rule information
   *
   * @param rule cdrrule
   * @param ssdObj vldb2ssd2
   */
  private static void setRuleInformation(final CDRRule rule, final Object ssdObj) {
    if (Objects.nonNull(ssdObj) && (ssdObj instanceof VLdb2Ssd2)) {
      VLdb2Ssd2 ssd = (VLdb2Ssd2) ssdObj;
      rule.setHint(ssd.getDataDescr());
      rule.setLabelId(ssd.getLabLabId());
      rule.setLowerLimit(ssd.getMin());
      rule.setRefValue(ssd.getTyp());
      rule.setUpperLimit(ssd.getMax());
      rule.setRevId(ssd.getRevId());
      rule.setRuleId(ssd.getLabObjId());
      setFormulaDescription(rule, ssd);
      if ((ssd.getFormula() != null) && !ssd.getFormula().isEmpty()) {
        rule.setFormula(ssd.getFormula());
      }
      if (ssd.getState().longValue() == 5) {
        rule.setReviewMethod("A");
      }
      else {
        rule.setReviewMethod("M");
      }
      rule.setUnit(ssd.getDim());
      rule.setRuleCreatedDate(ssd.getCreDate());
      rule.setRuleCreatedUser(ssd.getCreUser());
      // ALM-526562 - Set the Case enum for the CDR RUle instance from SSD2
      rule.setSsdCase(SSDCase.getType(ssd.getCases()));
      String dcm2ssd = ssd.getDcm2ssd();
      rule.setDcm2ssd((dcm2ssd != null) && "Y".equals(dcm2ssd));
    }
  }

  /**
   * @param rule
   * @param ssd
   */
  private static void setFormulaDescription(final CDRRule rule, final VLdb2Ssd2 ssd) {
    if ((ssd.getFormulaDesc() != null) && !ssd.getFormulaDesc().isEmpty()) {
      if (rule.getParameterName() != null) {
        // SSD-383
        if (rule.getParameterName().contains((SSDiCDMInterfaceConstants.VARCODE_SYM)) &&
            ssd.getFormulaDesc().contains(",BIT")) {
          /*
           * rule.setFormulaDesc(ssd.getFormulaDesc()); String varCodedLbl =
           * rule.getParameterName().replace(rule.getParameterName() .subSequence(rule.getParameterName().indexOf('['),
           * rule.getParameterName().indexOf(']') + 1), ""); // rule.getParameterName().indexOf('[')
           * rule.setFormulaDesc(ssd.getFormulaDesc().replace(varCodedLbl, rule.getParameterName())); // ing
           * rule.setFormulaDesc(ssd.getFormulaDesc().replace(ssd.getFormulaDesc()., replacement))
           */
          rule.setBitWiseRule(ssd.getFormulaDesc());
        }
        else {
          rule.setFormulaDesc(ssd.getFormulaDesc());
        }
      }
      else {
        rule.setFormulaDesc(ssd.getFormulaDesc());
      }
    }
  }

  /**
   * Convert DB object to SSD Release object - Named Query: VLdb2ProjectReleaseEdc17.getReleaseBySwVersion
   *
   * @param dbObject db object
   * @return model
   */
  public static SSDRelease convertToSSDRelease(final Object[] dbObject) {
    SSDRelease ssdRelease = new SSDRelease();
    ssdRelease.setReleaseId((BigDecimal) dbObject[0]);
    ssdRelease.setRelease(dbObject[1].toString());
    ssdRelease.setLabelListDesc(dbObject[2].toString());
    ssdRelease.setReleaseDesc(dbObject[3].toString());
    ssdRelease.setReleaseDate((Date) dbObject[4]);
    ssdRelease.setCreatedUser(dbObject[5].toString());
    ssdRelease.setExternalRelease(dbObject[6].toString());
    ssdRelease.setErrors(dbObject[7].toString());
    ssdRelease.setGlobalByPass(dbObject[8].toString());
    ssdRelease.setBuggyDate((Date) dbObject[9]);
    return ssdRelease;
  }

  /**
   * Convert DB object to FeatureValueModel object - Named Query: VLdb2ProjectReleaseEdc17.FeatureValue
   *
   * @param object dboject
   * @return model
   */
  public static FeatureValueModel convertToFeatureValueModelModel(final Object[] object)  {
    FeatureValueModel fvModel = new FeatureValueModel();
    
    fvModel.setFeatureId((BigDecimal) object[0]);
    fvModel.setFeatureText(object[1].toString());
    fvModel.setValueId((BigDecimal) object[2]);
    fvModel.setValueText(object[3].toString());

    return fvModel;
  }

  /**
   * Convert DB Object to SoftwareVersion - Named Query: VLdb2VillaSwver.VillaSwver
   *
   * @param object object
   * @param swProjNodeId nodeid
   * @return swversion
   */
  public static SoftwareVersion convertToSoftwareVersionModel(final Object[] object, final BigDecimal swProjNodeId) {
    SoftwareVersion swVersion = new SoftwareVersion();
    swVersion.setSwVersNodeId((BigDecimal) object[0]);
    swVersion.setSwVersionNumber(object[1].toString());
    swVersion.setSwVersionName(object[2].toString());
    swVersion.setSwVersionDesc(object[3] == null ? null : object[3].toString());
    swVersion.setSwProjNodeId(swProjNodeId);
    swVersion.setSwVersId((BigDecimal) object[4]);
    return swVersion;
  }
}
