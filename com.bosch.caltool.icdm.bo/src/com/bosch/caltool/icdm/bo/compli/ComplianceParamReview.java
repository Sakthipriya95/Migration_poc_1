/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDOutputData;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.FeatureLoader;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.bo.cdr.review.RulesValidator;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CompliCheckSSDInputModel;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;

/**
 * ICDM-2440 class to handle review of compliance parameters
 * <p>
 *
 * @author mkl2cob
 */
public class ComplianceParamReview {

  /**
   * VALUE not DEFINED error message constant
   */
  public static final String VALUE_NOT_DEFINED = "VALUE NOT DEFINED => please define a value in the PIDC";

  /**
   * List of ComPkgBcModel to be sent to SSD
   */
  private final Set<ComPkgBcModel> bcSet = new HashSet<>();
  /**
   * compliReviewData
   */
  private final CompliReviewData compliReviewData;
  /**
   * FeatureAttributeAdapter
   */
  private final FeatureAttributeAdapterNew faAdapter;
  /**
   *
   */
  private Map<Long, Feature> featuresFromSSD;
  /**
   *
   */
  private HashSet<AttributeValueModel> attrValueModSet;

  /**
   * set the valu not set attr
   */
  private final SortedSet<AttributeValueModel> valueNotSetAttr = new TreeSet<>();

  private Map<String, String> compliLabelUnitMap;

  /**
   * compli param set with no rules
   */
  private Set<String> paramsWithNoRules;

  private final Set<String> compliParamSet;

  CompliReviewSummary reviewSummary;

  private final ServiceData serviceData;


  private final List<String> unmappedBcs = new ArrayList<>();

  private String bcFcError;
  PidcVersionAttributeModel pidcDetails;

  private final String workDirectory;

  private Long subVarId;

  private final Map<String, AttributeValue> subVarAttrMap = new HashMap<>();

  private Map<String, List<CDRRule>> ssdRuleMap;

  private final boolean isQSSDOnlyRelease;

  /**
   * Instantiates a new compliance param review.
   *
   * @param compliReviewData the compli review data
   * @param faAdapter feature adapter object
   * @param compliParamSet the compli param set
   * @param serviceData service data object
   * @param pidcDetails pidc version details
   * @param workDirectory output directory
   * @param isQSSDOnlyRelease true when only qssd labels are present
   */
  public ComplianceParamReview(final CompliReviewData compliReviewData, final FeatureAttributeAdapterNew faAdapter,
      final Set<String> compliParamSet, final ServiceData serviceData, final PidcVersionAttributeModel pidcDetails,
      final String workDirectory, final boolean isQSSDOnlyRelease) {
    this.faAdapter = faAdapter;
    this.compliParamSet = compliParamSet;
    this.compliReviewData = compliReviewData;
    this.serviceData = serviceData;
    this.reviewSummary = new CompliReviewSummary(compliReviewData);
    this.pidcDetails = pidcDetails;
    this.workDirectory = workDirectory;
    this.isQSSDOnlyRelease = isQSSDOnlyRelease;
  }


  /**
   * create SSD file for compliance parameters.
   *
   * @param isNonSDOMBCRelease
   * @param sdomPverName
   * @throws IcdmException Exception in case of failure
   */
  public void invokeSSDReleaseForCompli(final List<Boolean> isNonSDOMBCRelease) throws IcdmException {

    if (CommonUtils.isNotEmpty(this.compliParamSet)) {
      // identify bc info only if there are compliance parameters
      if (!this.isQSSDOnlyRelease) {
        identifyBCInfo();
      }

      invokeSSDMethods(isNonSDOMBCRelease);
    }
  }

  /**
   * @param isNonSDOMBCRelease
   * @param sdomPverName
   * @throws IcdmException
   */
  private void invokeSSDMethods(final List<Boolean> isNonSDOMBCRelease) throws IcdmException {
    // Used the new framework code to get the Compli label unit.
    SSDMessageOptions messageOpts;
    SSDServiceHandler ssdHandler = new SSDServiceHandler(this.serviceData);

    try {

      this.compliLabelUnitMap = createLabelUnitMap(this.compliReviewData.getCalDataMap(), this.compliParamSet);

      messageOpts = ssdHandler.invokeComplianceRelease(this.compliLabelUnitMap, this.bcSet, this.isQSSDOnlyRelease,
          isNonSDOMBCRelease.get(0));

      // display the list of BCs for which SSD nodes are not mapped.
      if (CommonUtils.isNotEmpty(messageOpts.getNoNodeBcList())) {
        CDMLogger.getInstance().error("The BaseComponents that not mapped to SSD nodes : " +
            messageOpts.getNoNodeBcList().stream().collect(Collectors.joining(",  ")), Activator.PLUGIN_ID);
        this.unmappedBcs.addAll(messageOpts.getNoNodeBcList());
      }
      if (messageOpts.getSsdMessage().equals(SSDMessage.CONTINUERELEASE)) {
        this.featuresFromSSD = getFeatureValuesFromSSD(ssdHandler);
        // create the attr set from the features
        SortedSet<IProjectAttribute> attrSet = createAttrSetFromFeatures(this.featuresFromSSD);

        // fill the iCDM data into feature value model
        fillAttrValueFromICDM(attrSet, ssdHandler);

        // This method handles the exception : The attribute is not associated with a feature and
        // Value of attribute is not associated to a feature value
        Map<AttributeValueModel, FeatureValueModel> mapOfFeaVals =
            this.faAdapter.createFeaValModel(this.attrValueModSet);

        // Dependent attributes
        this.compliReviewData.setAttrValueModSet(this.attrValueModSet);

        // If the above exceptions does not occur then continue wid release
        setValues(mapOfFeaVals, ssdHandler);

        this.ssdRuleMap = invokeSSDRelease(ssdHandler);

        // sub var attr map is set to new HashMap so cannot be null
        if (!this.subVarAttrMap.isEmpty()) {
          callRelForSubVariants(ssdHandler, attrSet, isNonSDOMBCRelease.get(0));


        }
      }
      else {
        StringJoiner errorMsg = new StringJoiner("\n");
        errorMsg.add(messageOpts.getSsdMessage().getDescription());
        // if Mapping SSD node for BC version missing , add the bc list to error
        if (messageOpts.getSsdMessage() == SSDMessage.SSDNODEMISSING) {
          messageOpts.getNoNodeBcList().forEach(errorMsg::add);
        }
        throw new IcdmException(errorMsg.toString(), messageOpts.getSsdMessage().getCode());
      }
    }
    catch (IcdmException exp) {
      ssdHandler.cancelRelease();
      throw exp;
    }
    catch (Exception exp) {
      ssdHandler.cancelRelease();
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      throw new IcdmException(exp.getMessage(), 1002);
    }
  }


  /**
   * @param ssdHandler
   * @param attrSet
   * @param boolean1
   * @throws IcdmException
   */
  private void callRelForSubVariants(final SSDServiceHandler ssdHandler, final SortedSet<IProjectAttribute> attrSet,
      final boolean isNONSDOMBCRelease)
      throws IcdmException {
    Map<AttributeValueModel, FeatureValueModel> mapOfFeaVals;
    List<Long> subVarList = new ArrayList<>();
    subVarList.add(this.subVarId);
    RulesValidator validator = new RulesValidator();

    // get the sub var map entry set
    Set<String> entrySet = this.subVarAttrMap.keySet();

    for (String subVarKey : entrySet) {
      String[] split = subVarKey.split("-");
      // get the sub var id
      Long subVarIdFromKey = Long.valueOf(split[0]);

      // sub var id should not be same as the one in the feature adapter class
      if (!subVarList.contains(subVarIdFromKey)) {
        this.subVarId = subVarIdFromKey;
        // create release for each variant

        ssdHandler.invokeComplianceRelease(this.compliLabelUnitMap, this.bcSet, this.isQSSDOnlyRelease,
            isNONSDOMBCRelease);

        fillAttrValueFromICDM(attrSet, ssdHandler);
        mapOfFeaVals = this.faAdapter.createFeaValModel(this.attrValueModSet);

        // Dependent attributes
        this.compliReviewData.setAttrValueModSet(this.attrValueModSet);
        Map<String, List<CDRRule>> ssdRuleMapForSubVar = null;
        // If the above exceptions does not occur then continue wid release

        // If there are errors then icdm
        setValues(mapOfFeaVals, ssdHandler);
        ssdRuleMapForSubVar = invokeSSDRelease(ssdHandler);


        boolean validateRules = validator.validateRules(this.ssdRuleMap, ssdRuleMapForSubVar);

        if (!validateRules) {
          // clear all the Rules map incase of any issues.
          this.compliReviewData.getSsdRulesForCompliance().clear();
          this.compliReviewData.getSsdRulesForComplianceCaseIgnore().clear();
          throw new IcdmException("Rules defined at Sub variant level are not same");


        }

      }

    }
  }

  /**
   * Method moved to this class to be used by review with stand alone and web service.
   *
   * @param calDataMap calDataMap
   * @param compliParams compliParams
   * @return the label unit map
   */
  public Map<String, String> createLabelUnitMap(final Map<String, CalData> calDataMap, final Set<String> compliParams) {
    Map<String, String> labellist = new HashMap<>();
    for (String name : compliParams) {
      CalData calData = calDataMap.get(name);
      if (calData != null) {
        labellist.put(name, calData.getCalDataPhy().getUnit());
      }
    }
    return labellist;
  }

  /**
   * invoke final release
   *
   * @return
   * @throws IcdmException
   */
  private Map<String, List<CDRRule>> invokeSSDRelease(final SSDServiceHandler ssdHandler) throws IcdmException {
    // Using the new class compliSSDInvoker.
    CompliSSDInvoker compliSSDInvoker = new CompliSSDInvoker();
    Map<String, List<CDRRule>> ssdRulesForQssdCompli = new HashMap<>();
    Map<String, List<CDRRule>> ssdRulesForCompliance = null;
    Map<String, List<CDRRule>> ssdRulesForQssd = null;
    // Not null modiified to not empty
    if (!this.unmappedBcs.isEmpty()) {
      compliSSDInvoker.setUnMappedBcs(this.unmappedBcs.stream().collect(Collectors.joining(", ")));
    }
    compliSSDInvoker.setBcFcError(this.bcFcError);
    try {
      String compliFilePath = compliSSDInvoker.invokeSSDRelease(this.workDirectory, ssdHandler);
      this.compliReviewData.setCompliSSDFilePath(compliFilePath + CDRConstants.SSD_FILE_EXT);
      this.compliReviewData.setSsdErrorPath(compliSSDInvoker.getErrorFilePath());
      this.compliReviewData.setReleaseErrorString(compliSSDInvoker.getReleseErrorMessage());
      List<CDRRule> ruleList = ssdHandler.readRuleForCompliRelease();
      if (null != ruleList) {
        ssdRulesForCompliance = compliSSDInvoker.getRulesMap(ruleList);
        ssdRulesForQssd = compliSSDInvoker.getQssdRulesMap(ruleList);
        createRuleListAndSetToCDRData(ssdRulesForCompliance);
        createQssdRuleListAndSetToCDRData(ssdRulesForQssd);
        ssdRulesForQssdCompli.putAll(ssdRulesForCompliance);
        ssdRulesForQssdCompli.putAll(ssdRulesForQssd);
      }
    }
    catch (IcdmException exp) {
      throw new IcdmException("SSD.RELEASE_ERROR", exp, exp.getMessage());
    }

    return ssdRulesForCompliance;

  }

  /**
   * @param ruleList
   * @param ssdRulesForCompliance ssdRulesForCompliance
   * @throws IcdmException
   */
  private void createRuleListAndSetToCDRData(final Map<String, List<CDRRule>> ssdRulesForCompliance) {
    if (CommonUtils.isNotEmpty(ssdRulesForCompliance)) {
      // set the data in cdr data
      this.compliReviewData.setSsdRulesForCompliance(ssdRulesForCompliance);

      for (Entry<String, List<CDRRule>> ruleEntry : ssdRulesForCompliance.entrySet()) {
        this.compliReviewData.getSsdRulesForComplianceCaseIgnore().put(ruleEntry.getKey().toUpperCase(),
            ruleEntry.getValue());
      }
    }
    if (!ssdRulesForCompliance.keySet().containsAll(this.compliLabelUnitMap.keySet())) {
      this.paramsWithNoRules =
          CommonUtils.getDifference(this.compliLabelUnitMap.keySet(), ssdRulesForCompliance.keySet());
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append("The following compliance parameters do not have rules.\n");
      for (String param : this.paramsWithNoRules) {
        strBuilder.append(param).append("\n");
      }
      this.compliReviewData.setParamsWithNoRules(this.paramsWithNoRules);
    }

  }

  /**
   * @param ruleList
   * @param qssdRulesForCompliance ssdRulesForCompliance
   * @throws IcdmException
   */
  private void createQssdRuleListAndSetToCDRData(final Map<String, List<CDRRule>> qssdRulesForCompliance) {
    if (CommonUtils.isNotEmpty(qssdRulesForCompliance)) {
      // set the data in cdr data
      this.compliReviewData.setSsdRulesForQssd(qssdRulesForCompliance);

      for (Entry<String, List<CDRRule>> ruleEntry : qssdRulesForCompliance.entrySet()) {
        this.compliReviewData.getSsdRulesforQssdIgnore().put(ruleEntry.getKey().toUpperCase(), ruleEntry.getValue());
      }
    }
    if (!qssdRulesForCompliance.keySet().containsAll(this.compliLabelUnitMap.keySet())) {
      Set<String> qssdParamsWithNoRules =
          CommonUtils.getDifference(this.compliLabelUnitMap.keySet(), qssdRulesForCompliance.keySet());
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append("The following qssd parameters do not have rules.\n");
      for (String param : qssdParamsWithNoRules) {
        strBuilder.append(param).append("\n");
      }
      this.compliReviewData.setQssdParamsWithNoRules(qssdParamsWithNoRules);
    }

  }

  /**
   * @param mapOfFeaVals
   * @throws IcdmException
   */
  private void setValues(final Map<AttributeValueModel, FeatureValueModel> mapOfFeaVals,
      final SSDServiceHandler ssdHandler)
      throws IcdmException {

    boolean incorrectValue = false;

    List<Attribute> valueReset = new ArrayList<>();
    for (Entry<AttributeValueModel, FeatureValueModel> attrValModelMap : mapOfFeaVals.entrySet()) {
      Long ssdValId = attrValModelMap.getValue().getValueId().longValue();
      FeaValModel ssdFv =
          ssdHandler.getFeaValueForSelection().get(mapOfFeaVals.get(attrValModelMap.getKey()).getFeatureId());
      boolean isSSDValueUsedByRule = ssdFv.setSelValueIdFromIcdm(BigDecimal.valueOf(ssdValId));
      if (!isSSDValueUsedByRule) {
        valueReset.add(attrValModelMap.getKey().getAttr());
        incorrectValue = true;
      }
    }
    if (incorrectValue) {
      StringBuilder attrs = new StringBuilder();
      for (Attribute attribute : valueReset) {
        attrs.append("[" + attribute.getName() + "]");
        attrs.append("\n");
      }
      throw new IcdmException("SSD.MULTIPLE_RULE", attrs.toString());
    }

  }

  /**
   * @param attrSet
   * @throws IcdmException
   */
  private void fillAttrValueFromICDM(final SortedSet<IProjectAttribute> attrSet, final SSDServiceHandler ssdHandler)
      throws IcdmException {
    // Invisible attr present will always throw exception when there is are attr not present
    invisibleAttrsPresent(attrSet, ssdHandler);


    this.attrValueModSet = new HashSet<>();
    List<Attribute> subVarAttrs = new ArrayList<>();

    for (IProjectAttribute attribute : attrSet) {
      addToAttrValueModel(subVarAttrs, attribute);
    }

    // temporary change to include all attr val model - includes attr values of all subvariants of a variant
    this.compliReviewData.addToAttrValModelSetInclSubVar(this.attrValueModSet);


  }

  /**
   * @param subVarAttrs
   * @param attribute
   * @throws IcdmException
   */
  private void addToAttrValueModel(final List<Attribute> subVarAttrs, final IProjectAttribute pidcAttr)
      throws IcdmException {
    AttributeValueModel attrValModel;
    // Create the Attr Model If the Variant is selected
    if (CommonUtils.isNotNull(this.compliReviewData.getSelPIDCVariant())) {
      attrValModel = createModelForVarAttr(pidcAttr, subVarAttrs);
    }
    // Create the Attr Model If No Variant is present
    else {
      attrValModel = createModelForPIDCAttr(pidcAttr);
    }
    this.attrValueModSet.add(attrValModel);

  }

  /**
   * @param pidcAttr
   * @return
   * @throws IcdmException
   */
  private AttributeValueModel createModelForPIDCAttr(final IProjectAttribute pidcAttr) throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(this.serviceData);
    Attribute attr = attrLoader.getDataObjectByID(pidcAttr.getAttrId());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(this.serviceData);
    assertAttrNotNull(pidcAttr, attr);
    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of
    // AttributeValueUsed, AttributeValueNotUsed
    AttributeValueModel attrValModel;
    if (CommonUtils.isNull(pidcAttr.getValue())) {
      if (PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(attrValLoader.createAttrValNotUsedObject(attr.getId()));
        return attrValModel;
      }
      else if (PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttr.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(attrValLoader.createAttrValUsedObject(attr.getId()));
        return attrValModel;

      }
    }
    assertValueNotNull(pidcAttr, attr);
    attrValModel = new AttributeValueModel();
    attrValModel.setAttr(attr);
    attrValModel.setValue(attrValLoader.getDataObjectByID(pidcAttr.getValueId()));
    return attrValModel;
  }

  /**
   * @param subVarAttrs
   * @param pidcAttr
   * @return
   * @throws IcdmException
   */
  private AttributeValueModel createModelForVarAttr(final IProjectAttribute projAttr, final List<Attribute> subVarAttrs)
      throws IcdmException {
    IProjectAttribute pidcAttr = projAttr;
    AttributeLoader attrLoader = new AttributeLoader(this.serviceData);
    AttributeValueLoader attrValLoader = new AttributeValueLoader(this.serviceData);
    Attribute attr = attrLoader.getDataObjectByID(pidcAttr.getAttrId());
    assertAttrNotNull(pidcAttr, attr);
    // Check if variant
    if (pidcAttr.isAtChildLevel()) {
      // Refresh of attributes disabled, since refresh is done in createAttrValModSet() before iterations
      pidcAttr = this.pidcDetails.getAllVariantAttributeMap().get(this.compliReviewData.getSelPIDCVariant().getId())
          .get(pidcAttr.getAttrId());
      assertAttrNotNull(pidcAttr, attr);
      // check if sub-variant
      if (pidcAttr.isAtChildLevel()) {
        subVarAttrs.add(attr);
        String returnKey = getSubVarAttrValues(pidcAttr, attr, attrValLoader);
        AttributeValueModel attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(this.subVarAttrMap.get(returnKey));
        return attrValModel;
      }
    }
    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of AttributeValueUsed,
    // AttributeValueNotUsed
    AttributeValueModel attrValModel;
    if (CommonUtils.isNull(pidcAttr.getValue())) {
      if (PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(attrValLoader.createAttrValNotUsedObject(attr.getId()));
        return attrValModel;
      }
      else if (PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttr.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(attrValLoader.createAttrValUsedObject(attr.getId()));
        return attrValModel;
      }
    }
    // check attr value is null
    assertValueNotNull(pidcAttr, attr);
    attrValModel = new AttributeValueModel();
    attrValModel.setAttr(attr);
    attrValModel.setValue(attrValLoader.getDataObjectByID(pidcAttr.getValueId()));
    return attrValModel;
  }

  private String getSubVarAttrValues(final IProjectAttribute pidcAttr, final Attribute attr,
      final AttributeValueLoader attrValLoader)
      throws IcdmException {
    // fill all the Sub var Attr values for the Variant attribute
    PidcVariantAttribute pidcVarAttr = (PidcVariantAttribute) pidcAttr;
    PidcVariantLoader varAttrLoader = new PidcVariantLoader(this.serviceData);
    TabvProjectVariant subVarEntity = varAttrLoader.getEntityObject(pidcVarAttr.getVariantId());
    List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs = subVarEntity.getTabvProjSubVariantsAttrs();
    String returnKey = "";
    for (TabvProjSubVariantsAttr tabvProjSubVariantsAttr : tabvProjSubVariantsAttrs) {
      if (tabvProjSubVariantsAttr.getTabvProjectVariant().getDeletedFlag().equals("N") &&
          tabvProjSubVariantsAttr.getTabvProjectSubVariant().getDeletedFlag().equals("N")) {
        AttributeValue attrVal = getAttributeValue(pidcAttr, attr, tabvProjSubVariantsAttr, attrValLoader);
        // set the sub var id when it is null
        if (this.subVarId == null) {
          this.subVarId = tabvProjSubVariantsAttr.getTabvProjectSubVariant().getSubVariantId();
        }
        String subVarAttrKey =
            tabvProjSubVariantsAttr.getTabvProjectSubVariant().getSubVariantId() + "-" + pidcVarAttr.getAttrId();
        if ((this.subVarAttrMap.get(subVarAttrKey) == null) && (null != attrVal) &&
            attrVal.getAttributeId().equals(attr.getId())) {
          this.subVarAttrMap.put(subVarAttrKey, attrVal);
        }
        String[] splitKey = subVarAttrKey.split("-");
        Long subVariantId = Long.valueOf(splitKey[0]);
        // use the return key as the Sub variant of the current iteration.
        if (subVariantId.equals(this.subVarId) &&
            attr.getId().equals(tabvProjSubVariantsAttr.getTabvAttribute().getAttrId())) {
          returnKey = subVarAttrKey;
        }
      }
    }
    return returnKey;
  }

  private AttributeValue getAttributeValue(final IProjectAttribute pidcAttr, final Attribute attr,
      final TabvProjSubVariantsAttr tabvProjSubVariantsAttr, final AttributeValueLoader attrValLoader)
      throws IcdmException {
    AttributeValue attrVal = null;
    if (tabvProjSubVariantsAttr.getTabvAttrValue() == null) {
      Long attributeId = tabvProjSubVariantsAttr.getTabvAttribute().getAttrId();
      if (PROJ_ATTR_USED_FLAG.NO.getDbType().equals(tabvProjSubVariantsAttr.getUsed())) {
        if (attributeId != null) {
          attrVal = attrValLoader.createAttrValNotUsedObject(attributeId);
        }
        else {
          attrVal = attrValLoader.createAttrValNotUsedObject(attr.getId());
        }
      }
      else if (PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttr.getUsedFlag())) {
        if (attributeId != null) {
          attrVal = attrValLoader.createAttrValUsedObject(attributeId);
        }
        else {
          attrVal = attrValLoader.createAttrValUsedObject(attr.getId());
        }
      }
    }
    else {
      attrVal = attrValLoader.getDataObjectByID(tabvProjSubVariantsAttr.getTabvAttrValue().getValueId());
    }
    return attrVal;
  }


  /**
   * Assert value not null
   *
   * @param value attribute value
   * @param attr attribute
   * @param usedFlag
   * @throws IcdmException if value is null
   */
  private void assertValueNotNull(final IProjectAttribute projAttr, final Attribute attr) throws IcdmException {
    AttributeValueLoader attrValLdr = new AttributeValueLoader(this.serviceData);
    AttributeValue value = projAttr.getValueId() == null ? null : attrValLdr.getDataObjectByID(projAttr.getValueId());
    String usedFlag = projAttr.getUsedFlag();
    if (CommonUtils.isNull(value) && (usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()) ||
        usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()))) {
      // Value Id is null. Consider for used flag based resolution
      AttributeValueModel attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(null);
      attrValModel.setErrorMsg(VALUE_NOT_DEFINED);
      this.valueNotSetAttr.add(attrValModel);
      throw new IcdmException("FEAVAL.ATTR_VALUE_NOT_DEFINED", attr.getName());
    }
  }

  /**
   * Assert projAttr not null
   *
   * @param projAttr IPIDCAttribute
   * @param attr attribute
   * @throws IcdmException if value is null
   */
  private void assertAttrNotNull(final IProjectAttribute projAttr, final Attribute attr) throws IcdmException {
    if (CommonUtils.isNull(projAttr)) {
      AttributeValueModel attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(null);
      this.valueNotSetAttr.add(attrValModel);
      throw new IcdmException("FEAVAL.ATTR_VALUE_NOT_DEFINED", attr.getName());
    }
  }

  /**
   * @param attrSet
   * @throws IcdmException
   */
  private void invisibleAttrsPresent(final SortedSet<IProjectAttribute> attrsSet, final SSDServiceHandler ssdHandler)
      throws IcdmException {

    if ((null != attrsSet) && (attrsSet.size() != ssdHandler.getFeaValueForSelection().size())) {
      java.util.List<PidcVersionAttribute> attrNotPresent = new ArrayList<>();
      java.util.List<String> attrNotMapped = new ArrayList<>();

      computeAttrsNotMappedOrPresent(attrNotMapped);

      if (!attrNotPresent.isEmpty()) {
        StringBuilder attrs = new StringBuilder();
        for (PidcVersionAttribute pidcAttr : attrNotPresent) {
          attrs.append("[" + pidcAttr.getName() + "]");
          attrs.append("\n");
        }
        ssdHandler.cancelRelease();
        throw new IcdmException("COMPLI_REVIEW.ATTR_INVISIBLE", attrs.toString());
      }
      if (!attrNotMapped.isEmpty()) {
        StringBuilder attrs = new StringBuilder();
        for (String feature : attrNotMapped) {
          attrs.append("[" + feature + "]");
          attrs.append("\n");
        }

        ssdHandler.cancelRelease();
        throw new IcdmException("FEAVAL.FEATURE_VAL_MISSING", attrs.toString());

      }

    }

  }


  /**
   * @param attrNotPresent
   * @param attrNotMapped
   */
  private void computeAttrsNotMappedOrPresent(final java.util.List<String> attrNotMapped) {
    for (Feature feature : this.featuresFromSSD.values()) {
      if (null == feature.getAttrId()) {
        attrNotMapped.add(feature.getName());
      }

    }
  }

  /**
   * invoke check SSD.
   *
   * @param a2lFileContents the a 2 l file contents
   * @param compliSSDFilePath the primary ssd file path
   * @param hexFileName hexfile name
   * @return the compli review summary
   * @throws IcdmException error during checkssd invocation
   */
  public CompliReviewSummary invokeCheckSSD(final A2LFileInfo a2lFileContents, final String compliSSDFilePath,
      final String hexFileName)
      throws IcdmException {


    if ((compliSSDFilePath != null) && (CommonUtils.isNotEmpty(this.compliReviewData.getSsdRulesForCompliance()) ||
        CommonUtils.isNotEmpty(this.compliReviewData.getSsdRulesForQssd()))) {
      File file = new File(this.workDirectory);
      if (!file.exists()) {
        file.mkdir();
      }
      CompliCheckSSDInputModel compliCheckSSDInputModel = new CompliCheckSSDInputModel();
      compliCheckSSDInputModel.setDatafileoption(ExcelReportTypeEnum.ONEFILECHECK);
      compliCheckSSDInputModel.setPredecessorCheck(false);
      compliCheckSSDInputModel.setHexFileName(hexFileName);

      CompliCheckSSDInput createcompliInput = CompliResultUtil.getInstance().createcompliInput(
          this.compliReviewData.getCalDataMap(), this.compliReviewData.parserWarningsMap(), a2lFileContents,
          compliSSDFilePath, file.getAbsolutePath(), this.compliParamSet, compliCheckSSDInputModel);
      CheckSSDOutputData checkSSDOutData = CompliParamCheckSSDInvoker.getInstance().runCheckSSD(createcompliInput);
      this.reviewSummary.setCheckSSDOutData(checkSSDOutData);
    }

    if (this.compliReviewData.getReleaseErrorString() != null) {
      CDMLogger.getInstance().error(this.compliReviewData.getReleaseErrorString(), Activator.PLUGIN_ID);
    }

    return this.reviewSummary;
  }


  /**
   * Creates the attr set from features.
   *
   * @param ssdFeatures the features from SSD
   * @return the sorted set
   */
  private SortedSet<IProjectAttribute> createAttrSetFromFeatures(final Map<Long, Feature> ssdFeatures) {
    SortedSet<IProjectAttribute> attrsSet = new TreeSet<>();
    for (Feature feature : ssdFeatures.values()) {
      if ((null != feature.getAttrId()) && this.pidcDetails.getPidcVersAttrMap().containsKey(feature.getAttrId())) {
        attrsSet.add(this.pidcDetails.getPidcVersAttrMap().get(feature.getAttrId()));
      }
    }
    return attrsSet;

  }

  /**
   * @param featureValModelWithDepn
   * @return
   * @throws DataException
   */
  private Map<Long, Feature> getFeatureValuesFromSSD(final SSDServiceHandler ssdHandler) throws IcdmException {
    Map<BigDecimal, FeaValModel> mapOfFeatures = ssdHandler.getFeaValueForSelection();
    java.util.Set<Long> featureIds = new TreeSet<>();
    for (BigDecimal ids : mapOfFeatures.keySet()) {
      featureIds.add(ids.longValue());
    }
    FeatureLoader feaLoader = new FeatureLoader(this.serviceData);
    return feaLoader.getDataObjectByID(featureIds);

  }


  /**
   * Identify BC info.
   */
  private void identifyBCInfo() {
    ParameterLoader paramLoader = new ParameterLoader(this.serviceData);

    List<String> paramList = new ArrayList<>();
    Map<String, ComPkgBcModel> bcMap = new HashMap<>();
    for (String param : this.compliParamSet) {
      if (paramLoader.isCompliParameter(param)) {
        paramList.add(param);
      }
    }

    for (A2LBaseComponents a2lBC : this.compliReviewData.getBcInfo()) {
      // iterate until the list is empty
      for (Iterator<String> iterator = paramList.iterator(); iterator.hasNext();) {
        String param = iterator.next();
        Characteristic characteristic = this.compliReviewData.getA2lCharMap().get(param);
        String defFuncName = characteristic.getDefFunction().getName();

        if (a2lBC.getFunctionMap().keySet().contains(defFuncName)) {
          // initailise ComPkgBcModel
          if (!bcMap.containsKey(a2lBC.getBcName())) {
            ComPkgBcModel model = new ComPkgBcModel();
            model.setBcName(a2lBC.getBcName());
            model.setBvVersion(a2lBC.getBcVersion());
            model.setLevel(new BigDecimal(1));
            bcMap.put(a2lBC.getBcName(), model);
          }
          // Remove the current element from the iterator and the list.
          iterator.remove();
        }
      }
    }
    // Add relevant parameters and report others
    this.bcSet.addAll(bcMap.values());
    if (!paramList.isEmpty()) {
      this.bcFcError = "Parameter(s) without FC-BC mapping : " + paramList.stream().collect(Collectors.joining(", "));
      CDMLogger.getInstance().error(this.bcFcError, Activator.PLUGIN_ID);
    }
  }

  /**
   * Checks if is compli params present.
   *
   * @return true, if is compli params present
   */
  public boolean isCompliParamsPresent() {
    return !this.compliParamSet.isEmpty();
  }

  /**
   * Gets the params with no rules.
   *
   * @return the params with no rules
   */
  public Set<String> getParamsWithNoRules() {
    return this.paramsWithNoRules;
  }


}
