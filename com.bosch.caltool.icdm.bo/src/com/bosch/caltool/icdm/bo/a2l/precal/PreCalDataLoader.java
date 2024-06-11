/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l.precal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetRuleManager;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.QicatUserLoader;
import com.bosch.caltool.icdm.bo.util.CalDataBOUtil;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalData;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.QicatUser;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * @author bne4cob
 */
public class PreCalDataLoader extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String PRELIM_CALIBRATED = "prelimCalibrated";
  private String preLibCalTxt;

  /**
   * @param serviceData Service Data
   */
  public PreCalDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param input input
   * @return project attribute value details
   * @throws IcdmException error while retrieving data
   */
  public PreCalAttrValResponse getPreCalAttrVals(final PreCalInputData input) throws IcdmException {
    PreCalProcessData processData = createProcessData(input);

    PreCalAttrValResponse ret = new PreCalAttrValResponse();
    ret.setAttrMap(processData.getAttrMap());
    ret.setPidcAttrMap(processData.getPidcAttrMap());
    ret.setPidcVarAttrMap(processData.getPidcVarAttrMap());
    ret.setAttrValMap(processData.getAttrValMap());

    return ret;
  }

  /**
   * @param input                input
   * @param loadParamAttrDetails
   * @return project attribute value details
   * @throws IcdmException error while retrieving data
   */
  private PreCalProcessData createProcessData(final PreCalInputData input) throws IcdmException {

    PreCalProcessData processData = new PreCalProcessData(input);

    validateInputs(processData);
    PRECAL_SOURCE_TYPE sourceType = processData.getSourceType();

    processData.getParamBaseParamMap().putAll(ApicUtil.getBaseParamMap(input.getParamSet()));

    Set<String> paramsToProcessSet = processData.getParamsToProcessSet();
    input.getParamSet().forEach(par -> {
      String basePar = processData.getParamBaseParamMap().get(par);
      paramsToProcessSet.add(basePar == null ? par : basePar);
    });
    processData.setParamsToProcessSet(paramsToProcessSet);

    Set<Long> allAttrIdSet = new HashSet<>();
    if (sourceType == PRECAL_SOURCE_TYPE.COMMON_RULES) {
      allAttrIdSet.addAll(fetchCommonRuleAttrDeps(processData));
    }
    else if (sourceType == PRECAL_SOURCE_TYPE.RULESET_RULES) {
      allAttrIdSet.addAll(fetchRulesetParamAttrDeps(processData));
    }
    else {
      throw new InvalidInputException("This source type is not supported now - " + sourceType);
    }

    loadProjectAttrDetails(processData, allAttrIdSet);

    return processData;
  }

  private Set<Long> fetchRulesetParamAttrDeps(final PreCalProcessData processData) {
    Set<Long> retSet = new HashSet<>();
    String parName;

    for (TRuleSetParam dbRsParam : new RuleSetLoader(getServiceData())
        .getEntityObject(processData.getInput().getRuleSetId()).getTRuleSetParams()) {

      parName = dbRsParam.getTParameter().getName();

      if (processData.getParamsToProcessSet().contains((parName))) {
        processData.getParamIdMap().put(parName, dbRsParam.getTParameter().getId());

        Set<Long> attrIdSet = fetchRsParamAttrs(dbRsParam);
        if (!attrIdSet.isEmpty() && !checkVarCodeExclusion(processData, parName)) {
          retSet.addAll(attrIdSet);
          processData.getParamAttrMap().put(dbRsParam.getTParameter().getName(), attrIdSet);
        }
      }
    }

    return retSet;
  }

  /**
   * @param input
   * @param ret
   * @param attrIdSet
   */
  private Set<Long> fetchCommonRuleAttrDeps(final PreCalProcessData processData) {
    final Set<Long> retSet = new HashSet<>();

    try (ServiceData sdata = new ServiceData()) {
      getServiceData().copyTo(sdata, true);

      sdata.getEntMgr().getTransaction().begin();

      Long id = 0L;
      for (String par : processData.getParamsToProcessSet()) {
        GttObjectName temp = new GttObjectName();
        temp.setObjName(par);
        temp.setId(id++);
        sdata.getEntMgr().persist(temp);
      }

      // Fetch parameter IDs
      TypedQuery<TParameter> qryPar =
          sdata.getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAMS_BY_NAME, TParameter.class);
      for (TParameter dbParam : qryPar.getResultList()) {
        processData.getParamIdMap().put(dbParam.getName(), dbParam.getId());
      }

      // Fetch parameter attribute dependencies
      String parName;
      Long attrId;
      TypedQuery<Object[]> qry = sdata.getEntMgr().createNamedQuery(TParamAttr.NQ_GET_ATTR_ID_BY_PARAM, Object[].class);
      for (Object[] row : qry.getResultList()) {
        parName = row[0].toString();
        attrId = Long.valueOf(row[1].toString());
        if (!checkVarCodeExclusion(processData, parName)) {
          processData.getParamAttrMap().computeIfAbsent(parName, k -> new HashSet<>()).add(attrId);
          retSet.add(attrId);
        }
      }

      sdata.getEntMgr().getTransaction().rollback();
    }

    return retSet;

  }

  /**
   * Exclude variant coded labels with dependencies from pre-cal data fetch
   *
   * @param processData
   * @param basePar
   * @return
   */
  private boolean checkVarCodeExclusion(final PreCalProcessData processData, final String basePar) {
    boolean ret = false;

    Set<String> varCodeParamsToExcludeSet = processData.getVarCodeParamsToExcludeSet();
    for (Entry<String, String> entry : processData.getParamBaseParamMap().entrySet()) {
      // Add all variant coded labels with given base parameter name to exclusion list
      if (CommonUtils.isEqual(basePar, entry.getValue())) {
        varCodeParamsToExcludeSet.add(entry.getKey());
        ret = true;
      }
    }
    processData.setVarCodeParamsToExcludeSet(varCodeParamsToExcludeSet);
    return ret;
  }

  /**
   * @param input
   * @param attrIdSet
   * @throws IcdmException
   */
  private void loadProjectAttrDetails(final PreCalProcessData processData, final Set<Long> attrIdSet)
      throws IcdmException {

    if (!attrIdSet.isEmpty()) {
      Long pidcVersId = new PidcA2lLoader(getServiceData()).getEntityObject(processData.getInput().getPidcA2lId())
          .getTPidcVersion().getPidcVersId();
      Long varId = processData.getInput().getVariantId();
      LOAD_LEVEL loadLvl =
          processData.getInput().getVariantId() == null ? LOAD_LEVEL.L1_PROJ_ATTRS : LOAD_LEVEL.L3_VAR_ATTRS;
      PidcVersionAttributeModel projAttrMdl =
          new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, loadLvl, false);
      processData.setProjAttrModel(projAttrMdl);

      AttributeLoader attrLdr = new AttributeLoader(getServiceData());
      for (Long attrId : attrIdSet) {
        Attribute attr = projAttrMdl.getAttribute(attrId);
        if (attr == null) {
          attr = attrLdr.getDataObjectByID(attrId);
        }
        processData.getAttrMap().put(attrId, attr);

        getProjAttr(processData, varId, attrId);

      }
    }

  }

  private void getProjAttr(final PreCalProcessData processData, final Long varId, final Long attrId)
      throws DataException {

    PidcVersionAttribute pidcVersAttr = processData.getProjAttrModel().getPidcVersAttr(attrId);
    if (pidcVersAttr != null) {
      if (pidcVersAttr.isAtChildLevel() && (varId != null)) {
        PidcVariantAttribute varAttr = processData.getProjAttrModel().getVariantAttribute(varId, attrId);
        if (varAttr != null) {
          processData.getPidcVarAttrMap().put(attrId, varAttr);
          addAttrValue(processData, varAttr);
        }
      }
      processData.getPidcAttrMap().put(attrId, pidcVersAttr);
      addAttrValue(processData, pidcVersAttr);
    }
  }

  /**
   * @param varAttr
   * @throws DataException
   */
  private void addAttrValue(final PreCalProcessData processData, final IProjectAttribute projAttr)
      throws DataException {
    Long valId = projAttr.getValueId();
    if ((valId != null) && !processData.getAttrValMap().containsKey(valId)) {
      processData.getAttrValMap().put(valId, new AttributeValueLoader(getServiceData()).getDataObjectByID(valId));
    }

  }

  private Set<Long> fetchRsParamAttrs(final TRuleSetParam dbRsParam) {
    final Set<Long> attrIdSet = new HashSet<>();
    for (TRuleSetParamAttr dbPrmAttr : dbRsParam.getTRuleSetParamAttrs()) {
      if (!CommonUtils.isEqual("Y", dbPrmAttr.getTabvAttribute().getDeletedFlag())) {
        attrIdSet.add(dbPrmAttr.getTabvAttribute().getAttrId());
      }
    }

    return attrIdSet;
  }

  /**
   * @param processData
   * @param sourceType
   * @throws InvalidInputException
   */
  private void validateInputs(final PreCalProcessData processData) throws InvalidInputException {
    PRECAL_SOURCE_TYPE sourceType = PRECAL_SOURCE_TYPE.getType(processData.getInput().getSourceType());
    if (sourceType == null) {
      throw new InvalidInputException("Pre-calibration data source type is mandatory");
    }

    processData.setSourceType(sourceType);

  }


  /**
   * Fetch pre-calibration data for the given input details
   *
   * @param input input
   * @return pre calibration data for the given input
   * @throws IcdmException any error while retrieving data
   */
  public PreCalData getPreCalData(final PreCalInputData input) throws IcdmException {

    PreCalProcessData processData = createProcessData(input);

    fetchRulesForParAttrVal(processData);

    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(input.getPidcA2lId());
    processData.setPidcA2l(new PidcA2lLoader(getServiceData()).getDataObjectByID(input.getPidcA2lId()));

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());

    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(pidcA2l.getPidcVersId());
    processData.setPidcVers(pidcVers);

    // TODO get path directly from proj attr, instead of using extended name
    String path = pidcVersLdr.getExtendedName(pidcA2l.getPidcVersId());
    path = path.substring(path.indexOf(':') + 1).trim();
    String[] projInfoArr = path.split("->");
    String projInfo = projInfoArr[0] + "--->" + pidcVers.getName();
    processData.setProjInfo(projInfo);

    this.preLibCalTxt =
        new MessageLoader(getServiceData()).getMessage("CDFX_EXPORT_DIALOG", "PRELIM_CALIBRATED_REMARK_TEXT");

    filterValuesCdr(processData);

    PreCalData ret = new PreCalData();
    ret.getPreCalDataMap().putAll(processData.getPreCalDataMap());

    return ret;


  }

  /**
   * Send the label names and the attrValueModSet (Convert it to Feature Val Model and Invoke SSD)
   *
   * @param input
   * @param sourceType
   * @param labelNames
   * @param attrValueModSet
   * @throws Exception
   */
  private void fetchRulesForParAttrVal(final PreCalProcessData processData) throws IcdmException {

    Set<AttributeValueModel> attrValueModSet = createAttrValModels(processData);

    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());
    Map<String, List<CDRRule>> ruleDepMap = null;

    Map<AttributeValueModel, FeatureValueModel> feaValModels = adapter.createFeaValModel(attrValueModSet);
    SSDServiceHandler ssdHandler = new SSDServiceHandler(getServiceData());

    final List<String> labelNames = new ArrayList<>(processData.getInput().getParamSet());

    Iterator<String> itr = labelNames.iterator();
    while (itr.hasNext()) {
      String lbl = itr.next();
      if (processData.getVarCodeParamsToExcludeSet().contains(lbl)) {
        itr.remove();
      }
    }

    // Check if the Rule set is selected for Pre cal data
    if (processData.getSourceType() == PRECAL_SOURCE_TYPE.COMMON_RULES) {
      ruleDepMap =
          ssdHandler.readReviewRuleForDependency(labelNames, new ArrayList<FeatureValueModel>(feaValModels.values()));
    }
    else {
      IRuleManager ruleManager = new RuleSetRuleManager(ssdHandler,
          new RuleSetLoader(getServiceData()).getDataObjectByID(processData.getInput().getRuleSetId()),getServiceData());
      ruleDepMap =
          ruleManager.readRuleForDependency(labelNames, new ArrayList<FeatureValueModel>(feaValModels.values()));
    }

    ruleDepMap.forEach((par, ruleList) -> {
      if (CommonUtils.isNotEmpty(ruleList)) {
        processData.getCdrRuleMap().put(par, ruleList.get(0));
      }
    });

  }

  /**
   * @param projAttrValDet2
   * @return the Attr Val Model Set for Searching rules
   * @throws DataException
   */
  private Set<AttributeValueModel> createAttrValModels(final PreCalProcessData processData) throws IcdmException {

    final Set<AttributeValueModel> retSet = new HashSet<>();
    AttributeValueLoader valLdr = new AttributeValueLoader(getServiceData());
    AttributeValueModel attrValModel = null;

    for (Attribute attr : processData.getAttrMap().values()) {

      IProjectAttribute projAttr = processData.getPidcVarAttrMap().containsKey(attr.getId())
          ? processData.getPidcVarAttrMap().get(attr.getId()) : processData.getPidcAttrMap().get(attr.getId());

      validateProjAttr(attr, projAttr);

      // If value is available
      if (projAttr.getValueId() == null) {
        attrValModel = setModelIfUsedNotUsed(valLdr, attr, projAttr);
      }
      else {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(attr);
        attrValModel.setValue(valLdr.getDataObjectByID(projAttr.getValueId()));
      }

      if (attrValModel == null) {
        throw new DataException("Missing definition for attribute '" + attr.getName() + "' in project");
      }

      retSet.add(attrValModel);
    }

    return retSet;
  }

  /**
   * @param attr
   * @param paramAttr
   * @throws DataException
   */
  private void validateProjAttr(final Attribute attr, final IProjectAttribute paramAttr) throws DataException {
    if (paramAttr == null) {
      throw new DataException("Missing definition for attribute '" + attr.getName() + "' in project");
    }
    if (ApicConstants.HIDDEN_VALUE.equals(paramAttr.getValue())) {
      throw new DataException("Attribute '" + attr.getName() + "' is hidden in project");
    }
    if (ApicConstants.VARIANT_ATTR_DISPLAY_NAME.equals(paramAttr.getValue())) {
      throw new DataException("The attribute '" + attr.getName() +
          "' is defined at variant level for the selected project, but appropriate variant attribute not found. Data generation is not supported.");
    }
    if (ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME.equals(paramAttr.getValue())) {
      throw new DataException("Attribute '" + attr.getName() +
          "' is defined at sub-variant level for the selected variant. Data generation is not supported.");
    }
  }


  /**
   * @param valLdr
   * @param attrValModel
   * @param gridItem
   * @param paramAttr
   * @return
   */
  private AttributeValueModel setModelIfUsedNotUsed(final AttributeValueLoader valLdr, final Attribute attr,
      final IProjectAttribute paramAttr) {

    AttributeValueModel attrValModel = null;

    // If the attribute is not used ,then create a dummy object (AttributeValueNotUsed)and set in the model
    if (ApicConstants.CODE_NO.equals(paramAttr.getUsedFlag())) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(valLdr.createAttrValNotUsedObject(attr.getId()));

    } // If the attribute is used ,then create a dummy object (AttributeValueUsed )and set in the model
    else if (ApicConstants.CODE_YES.equals(paramAttr.getUsedFlag())) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(valLdr.createAttrValUsedObject(attr.getId()));
    }
    return attrValModel;
  }

  /**
   * Filter for caldata Objects of CDR source
   *
   * @param preCalData
   * @param recomValfilter
   * @param wizard
   * @param valueMap
   * @param rulesForParAttrVal
   * @param attrValueModSet
   * @param projAttrValDet
   * @param projInfo
   * @param key
   * @throws DataException
   */
  private void filterValuesCdr(final PreCalProcessData processData) throws IcdmException {

    RuleSet ruleSet = processData.getInput().getRuleSetId() == null ? null
        : new RuleSetLoader(getServiceData()).getDataObjectByID(processData.getInput().getRuleSetId());
    processData.setRuleSet(ruleSet);

    // 421600

    CommonParamLoader paramLoader = new CommonParamLoader(getServiceData());
    NodeAccess nodeAccess = new NodeAccessLoader(getServiceData()).getAllNodeAccessForCurrentUser()
        .get(Long.valueOf(paramLoader.getValue(CommonParamKey.PRE_CAL_FULL)));

    boolean hasAccess = (null != nodeAccess);

    Map<String, QicatUser> userDeptMap = getUserDivision(processData);
    for (Entry<String, CDRRule> paramRuleEntry : processData.getCdrRuleMap().entrySet()) {
      filterValuesForEachParam(processData, paramRuleEntry, hasAccess, userDeptMap);
    }
  }

  /**
   * @param processData
   * @return Map<String,String> - key - username , value - department
   */
  private Map<String, QicatUser> getUserDivision(final PreCalProcessData processData) {
    List<String> userNameList =
        processData.getCdrRuleMap().values().stream().map(CDRRule::getRuleCreatedUser).collect(Collectors.toList());
    return new QicatUserLoader(getServiceData()).getQicatUsersByNtId(userNameList);
  }


  /**
   * @param userDivMap
   * @param projAttrValDet
   * @param projInfo
   * @param recomValfilter
   * @param wizard
   * @param rulesForParAttrVal
   * @param cdrFuncParam
   * @throws DataException
   * @throws IcdmException
   */
  private void filterValuesForEachParam(final PreCalProcessData processData,
      final Entry<String, CDRRule> paramRuleEntry, final boolean hasAccess, final Map<String, QicatUser> userDeptMap)
      throws IcdmException {

    if (CommonUtils.isNotNull(paramRuleEntry.getValue())) {
      ReviewRule rule =
          new ReviewRuleAdapter(getServiceData()).createReviewRule(paramRuleEntry.getValue());
      if (!processData.getInput().isOnlyExactMatch() || rule.isDcm2ssd()) {
        CalData calData = ReviewRuleUtil.getRefValue(rule);
        if (null != calData) {

          if ((null != calData.getCalDataPhy()) &&
              ((null == calData.getCalDataPhy().getUnit()) || "null".equals(calData.getCalDataPhy().getUnit()))) {
            // Icdm-797 null value for Unit in cal data phy object
            calData.getCalDataPhy().setUnit("");
          }
          String par = paramRuleEntry.getKey();
          String basePar = processData.getParamBaseParamMap().get(par);
          basePar = basePar == null ? par : basePar;
          Parameter param =
              new ParameterLoader(getServiceData()).getDataObjectByID(processData.getParamIdMap().get(basePar));

          calData = CalDataBOUtil.getCalDataHistoryDetails(getServiceData().getUsername(), calData, rule, param,
              processData.getParamAttrMap(), processData.getRuleSet());

          fillHistory(param, calData, processData, hasAccess, rule, userDeptMap);

          processData.getPreCalDataMap().put(paramRuleEntry.getKey(),
              CalDataUtil.convertCalDataToZippedByteArr(calData, getLogger()));
        }
      }
    }
  }

  /**
   * Fill history.
   *
   * @param rule
   * @param userDivMap
   * @param projAttrValDet
   * @param projInfo
   * @param calDtaMap      the cal data map
   * @throws IcdmException
   * @throws DataException
   */
  private void fillHistory(final Parameter param, final CalData calDataObj, final PreCalProcessData processData,
      final boolean hasAccess, final ReviewRule rule, final Map<String, QicatUser> userDeptMap) {

    if (calDataObj.getCalDataHistory() == null) {
      calDataObj.setCalDataHistory(new CalDataHistory());
    }
    addReviewHistEntry(processData, param, calDataObj, hasAccess, rule, userDeptMap);
  }


  /**
   * Gets the review hist entry.
   *
   * @param projInfo   the proj info
   * @param calData    the param checked val clone
   * @param rule
   * @param userDivMap
   * @return the review hist entry
   * @throws DataException
   */
  private HistoryEntry addReviewHistEntry(final PreCalProcessData processData, final Parameter param,
      final CalData calData, final boolean hasAccess, final ReviewRule rule, final Map<String, QicatUser> userDeptMap) {

    HistoryEntry historyEntry;


    // 421600 - if user does not have rights, provide a basic history info
    if ((processData.getSourceType() == PRECAL_SOURCE_TYPE.COMMON_RULES) && !hasAccess) {
      calData.getCalDataHistory().getHistoryEntryList().clear();

      historyEntry = new HistoryEntry();
      historyEntry.setState(getDataElement(PRELIM_CALIBRATED));
      historyEntry.setRemark(getDataElement(this.preLibCalTxt));
      historyEntry.setDate(getDataElement(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_04)));
      historyEntry.setPerformedBy(getDataElement(getServiceData().getUsername()));
      calData.getCalDataHistory().getHistoryEntryList().add(historyEntry);
    }
    else {
      if (calData.getCalDataHistory().getHistoryEntryList().isEmpty()) {
        calData.getCalDataHistory().getHistoryEntryList().add(new HistoryEntry());
      }
      historyEntry = calData.getCalDataHistory().getHistoryEntryList().get(0);
      historyEntry.setProject(getDataElement(processData.getProjInfo()));
      String a2lLabel = processData.getPidcA2l().getSdomPverVarName() + " : " + processData.getPidcA2l().getName();
      historyEntry.setProgramIdentifier(getDataElement(a2lLabel));
      setTargetInfo(param, calData.getShortName(), historyEntry, processData, rule);
    }
    if ((processData.getSourceType() == PRECAL_SOURCE_TYPE.COMMON_RULES) ||
        (processData.getSourceType() == PRECAL_SOURCE_TYPE.RULESET_RULES)) {
      addCreatorDivToRemark(historyEntry, userDeptMap, rule);
    }
    return historyEntry;
  }


  private void addCreatorDivToRemark(final HistoryEntry historyEntry, final Map<String, QicatUser> userDeptMap,
      final ReviewRule rule) {
    StringBuilder remark = new StringBuilder();
    if (null != historyEntry.getRemark()) {
      remark.append(historyEntry.getRemark().getValue());
    }
    if (null != userDeptMap.get(rule.getRuleCreatedUser())) {
      // The first part of the department of rule creator is added as the division
      String createdUser =
          CommonUtils.splitWithDelimiter(userDeptMap.get(rule.getRuleCreatedUser()).getCiDepartment(), "/")[0];
      remark.append("\nRule created by division: ").append(createdUser);
    }
    historyEntry.setRemark(getDataElement(remark.toString()));
  }


  /**
   * Sets the target info.
   *
   * @param paramName    the param name
   * @param historyEntry the history entry
   * @param rule         review rule
   * @param param        the a 2 l param
   * @param processData  {@link PreCalProcessData}
   */
  private void setTargetInfo(final Parameter param, final String paramName, final HistoryEntry historyEntry,
      final PreCalProcessData processData, final ReviewRule rule) {
    StringBuilder targetInfoStr = new StringBuilder();
    targetInfoStr.append(param.getpClassText());

    boolean hasNoDependencyRule = (null != rule) && rule.getDependencyList().isEmpty();

    if (hasNoDependencyRule) {
      targetInfoStr = targetInfoStr.length() > 0 ? targetInfoStr.append(",") : targetInfoStr;
      targetInfoStr.append("Default Rule");
    }

    String result = targetInfoStr.toString();
    Set<Long> paramAttrDepSet = processData.getParamAttrMap().get(paramName);
    if (paramAttrDepSet != null) {
      targetInfoStr = getAttrValString(processData, targetInfoStr, hasNoDependencyRule, paramAttrDepSet);
      if (!CommonUtils.isEmptyString(targetInfoStr.toString()) && targetInfoStr.toString().contains(";")) {
        result = targetInfoStr.substring(0, targetInfoStr.length() - 4).trim();
      }
      if (hasNoDependencyRule) {
        result += "]";
      }
    }

    historyEntry.setTargetVariant(getDataElement(result));
  }

  private StringBuilder getAttrValString(final PreCalProcessData processData, StringBuilder targetInfoStr,
      final boolean hasNoDependencyRule, final Set<Long> paramAttrDepSet) {
    for (Long attrId : paramAttrDepSet) {
      IProjectAttribute projAttr = processData.getPidcVarAttrMap().containsKey(attrId)
          ? processData.getPidcVarAttrMap().get(attrId) : processData.getPidcAttrMap().get(attrId);
      targetInfoStr = hasNoDependencyRule ? targetInfoStr.append(" [") : targetInfoStr.append(",");
      targetInfoStr.append(projAttr.getName());
      targetInfoStr.append("  --> ");
      targetInfoStr.append(getAttrVal(projAttr));
      targetInfoStr.append("  ;  ");
    }
    return targetInfoStr;
  }

  /**
   * Gets the attr val.
   *
   * @param pidcAttr the pidc attr
   * @return the attr val
   */
  private String getAttrVal(final IProjectAttribute pidcAttr) {
    if (ApicConstants.CODE_NO.equals(pidcAttr.getUsedFlag())) {
      return ApicConstants.NOT_USED;
    }
    return pidcAttr.getValue() == null ? ApicConstants.ATTR_NOT_DEFINED : pidcAttr.getValue();
  }

  /**
   * Gets the data element.
   *
   * @param value the value
   * @return the data element
   */
  private DataElement getDataElement(final String value) {
    DataElement dataElement = new DataElement();
    dataElement.setValue(CommonUtils.checkNull(value));

    return dataElement;
  }

}
