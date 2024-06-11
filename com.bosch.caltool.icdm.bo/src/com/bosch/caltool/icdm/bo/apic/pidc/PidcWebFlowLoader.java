/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.AliasDetailLoader;
import com.bosch.caltool.icdm.bo.apic.WebflowElementLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TWebFlowElement;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.WebflowElement;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowElementDetailsType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowElementRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowElementSelectionType;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author dmr1cob
 */
public class PidcWebFlowLoader extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String PIDC_PREFIX = "PIDC:";
  /**
   *
   */
  private static final String INPUT_ID = "Input Id ";
  private static final String INVALID_ELE_ID_MSG =
      "The element ID is not existing. Please provide an existing element ID";

  /**
   * @param serviceData ServiceData
   */
  public PidcWebFlowLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param elementId pidcVersionID or pidcVariantId
   * @return the pidc web flow details
   * @throws IcdmException IcdmException
   */
  public PidcWebFlowData fetchDataForWebFlow(final Long elementId) throws IcdmException {

    String elementType = getElementType(elementId);
    if (CommonUtils.isEmptyString(elementType)) {
      throw new InvalidInputException(INVALID_ELE_ID_MSG);
    }

    PidcWebFlowData pidcWebFlowData = null;
    AliasDef aliasDef = getWebFlowAliasDef();
    if (ApicConstants.WEBFLOW_VARIANT.equalsIgnoreCase(elementType)) {
      PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
      PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(elementId);
      pidcWebFlowData = getDataForVariant(pidcVariant, aliasDef);
    }
    else if (ApicConstants.WEBFLOW_PIDC.equalsIgnoreCase(elementType)) {
      PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
      PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(elementId);
      pidcWebFlowData = getDataForVersion(pidcVersion, aliasDef, null);
    }
    return pidcWebFlowData;
  }


  /**
   * @param elementId webFlow element ID
   * @return the pidc web flow details
   * @throws IcdmException IcdmException
   */
  public PidcWebFlowData fetchDataForWebFlowV2(final Long elementId) throws IcdmException {

    String elementType = getElementTypeV2(elementId);
    if (CommonUtils.isEmptyString(elementType)) {
      throw new InvalidInputException(INVALID_ELE_ID_MSG);
    }
    PidcWebFlowData pidcWebFlowData = null;
    AliasDef aliasDef = getWebFlowAliasDef();

    if (ApicConstants.WEBFLOW_PIDC.equalsIgnoreCase(elementType)) {
      PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
      PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(elementId);
      pidcWebFlowData = getDataForVersion(pidcVersion, aliasDef, null);
    }
    if (ApicConstants.WEBFLOW_VARIANT.equalsIgnoreCase(elementType)) {
      PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
      PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(elementId);
      pidcWebFlowData = getDataForVariant(pidcVariant, aliasDef);
    }
    else if (ApicConstants.WEBFLOW_ELEMENT.equalsIgnoreCase(elementType)) {
      pidcWebFlowData = getDataForMultiVars(elementId, aliasDef);
    }
    return pidcWebFlowData;
  }

  /**
   * @param elementId
   * @param aliasDef
   * @return PidcWebFlowData
   * @throws IcdmException
   */
  private PidcWebFlowData getDataForMultiVars(final Long elementId, final AliasDef aliasDef) throws IcdmException {

    // get the variants from element ID
    Set<PidcVariant> pidcVarSet = getAllWebFlowVariants(elementId);

    // get the corresponding Pidc Version of the above variants from which the web flow job was started
    Long pidcVersionId = pidcVarSet.iterator().next().getPidcVersionId();
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVersionId);

    // return web flow data object using the pidc version
    return getDataForVersion(pidcVersion, aliasDef, pidcVarSet);
  }

  /**
   * @param elementID PidcVersionId or PidcVariantId
   * @return {@link PidcWebFlowData} Set
   * @throws IcdmException Exception
   */
  public Set<PidcWebFlowData> fetchDataForWebFlowElement(final long elementID) throws IcdmException {

    Set<PidcWebFlowData> pidcWebFlowDataSet = new HashSet<>();

    List<TWebFlowElement> dbWebFlowElements = getWebFlowElements(elementID);
    if (dbWebFlowElements.isEmpty()) {
      pidcWebFlowDataSet.add(fetchDataForWebFlow(elementID));
    }
    else {
      getwebFlowDataIfNotEmpty(elementID, pidcWebFlowDataSet, dbWebFlowElements);
    }
    return pidcWebFlowDataSet;
  }

  /**
   * @param elementID
   * @param pidcWebFlowDataSet
   * @param aliasDef
   * @param dbWebFlowElements
   * @throws IcdmException
   * @throws DataException
   */
  private void getwebFlowDataIfNotEmpty(final long elementID, final Set<PidcWebFlowData> pidcWebFlowDataSet,
      final List<TWebFlowElement> dbWebFlowElements)
      throws IcdmException {
    if (validateWebFlowElements(dbWebFlowElements)) {
      for (TWebFlowElement webflowEle : dbWebFlowElements) {

        long variantID = webflowEle.getVariantID();
        String eleType = getValidVariant(variantID);
        if (CommonUtils.isEmptyString(eleType)) {
          throw new InvalidInputException(INVALID_ELE_ID_MSG);
        }

        if (ApicConstants.WEBFLOW_VARIANT.equalsIgnoreCase(eleType)) {
          PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
          PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(variantID);
          AliasDef aliasDef = getWebFlowAliasDef();
          PidcWebFlowData pidcWebFlowData = getDataForVariant(pidcVariant, aliasDef);
          pidcWebFlowDataSet.add(pidcWebFlowData);
        }
      }
      CDMLogger.getInstance().info(INPUT_ID + elementID + " is an Element Id for multiple variants");
    }
    else {
      throw new IcdmException("The element ID is marked as deleted. Only not deleted element IDs are accepted.");
    }
  }

  /**
   * @param elementId pidcVersionID or pidcVariantId
   * @return element Type
   * @throws IcdmException Exception
   */
  private String getElementType(final Long elementId) {
    String elementType = "";
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    if (pidcVersLoader.isValidId(elementId)) {
      elementType = ApicConstants.WEBFLOW_PIDC;
    }
    else if (pidcVariantLoader.isValidId(elementId)) {
      elementType = ApicConstants.WEBFLOW_VARIANT;
    }
    return elementType;
  }

  /**
   * @param elementId pidcVariantId or webflowElementId
   * @return element Type
   * @throws IcdmException Exception
   */
  public String getElementTypeV2(final Long elementId) throws IcdmException {
    String elementType = "";
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    WebflowElementLoader webFlowElementLoader = new WebflowElementLoader(getServiceData());
    if (pidcVersLoader.isValidId(elementId)) {
      elementType = ApicConstants.WEBFLOW_PIDC;
    }
    else if (pidcVariantLoader.isValidId(elementId)) {
      elementType = ApicConstants.WEBFLOW_VARIANT;
    }
    else {
      List<WebflowElement> webflowElementList = webFlowElementLoader.getWebFlowElements(elementId);
      if (webflowElementList.size() > 1) {
        elementType = ApicConstants.WEBFLOW_ELEMENT;
      }
    }
    return elementType;
  }

  /**
   * @param elementID pidcVersionID or pidcVariantId
   * @return Type
   */
  private String getValidVariant(final Long elementID) {
    String elementType = "";
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    if (pidcVariantLoader.isValidId(elementID)) {
      elementType = ApicConstants.WEBFLOW_VARIANT;
    }
    return elementType;
  }

  /**
   * @return
   * @throws DataException
   */
  private AliasDef getWebFlowAliasDef() throws DataException {
    AliasDef aliasDef = null;
    // get the id of the web flow alias
    String parameterValue = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WEBFLOW_ALIAS_ID);
    AliasDefLoader aliasDefLoader = new AliasDefLoader(getServiceData());
    // get all alias definition
    Map<Long, AliasDef> aliasDefMap = aliasDefLoader.getAll();
    for (AliasDef aliasDefinition : aliasDefMap.values()) {
      // Return the id corresponding
      if (aliasDefinition.getId().equals(Long.valueOf(parameterValue))) {
        aliasDef = aliasDefinition;
      }
    }
    return aliasDef;
  }

  /**
   * @param pidcVariant
   * @param aliasDef
   * @param user
   * @return
   */
  private PidcWebFlowData getDataForVariant(final PidcVariant pidcVariant, final AliasDef aliasDef)
      throws IcdmException {

    PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
    if (versionLoader.isHiddenToCurrentUser(pidcVariant.getPidcVersionId())) {
      throw new IcdmException("The pidc version is hidden to user");
    }
    WebFlowDataCreator webFlowDataCreater = new WebFlowDataCreator();
    ProjectAttributeLoader projAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVersAttrModel = projAttrLoader.createModel(pidcVariant.getPidcVersionId());
    webFlowDataCreater.setPidcVersAttrModel(pidcVersAttrModel);
    webFlowDataCreater.setPidcVariant(pidcVariant);
    webFlowDataCreater.setAliasDef(aliasDef);

    // Create web flow data object
    PidcWebFlowData pidcWebFlowData = new PidcWebFlowData(pidcVariant.getId(),
        pidcVersAttrModel.getPidcVersion().getName(), pidcVariant.getName(), pidcVariant.getVersion());

    // Task 232488
    Map<Long, PidcVersionAttribute> allPidcVersAttr = getAllPidcVersAttribute(pidcVersAttrModel);
    webFlowDataCreater.setAllPidcVersAttr(allPidcVersAttr);

    // Task 232488
    Map<Long, PidcVariantAttribute> allPidcVarAttr = getAllVarAttribute(pidcVariant, pidcVersAttrModel);
    webFlowDataCreater.setAllPidcVarAttr(allPidcVarAttr);

    // for the aprj attrname use the pric version attr
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    com.bosch.caltool.icdm.model.apic.attr.Attribute aprjNameAttr =
        attrLoader.getAllLevelAttributes().get(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR));
    IProjectAttribute pidcAttribute = allPidcVersAttr.get(aprjNameAttr.getId());

    setVcdmElementName(pidcWebFlowData, pidcAttribute, webFlowDataCreater);

    Map<Long, IProjectAttribute> allProjAttr = new HashMap<>();
    allProjAttr.putAll(allPidcVersAttr);
    allProjAttr.putAll(allPidcVarAttr);
    webFlowDataCreater.setAllProjAttr(allProjAttr);

    Set<Attribute> aliasAttrs = new HashSet<>();

    setAliasAttrs(webFlowDataCreater, aliasAttrs);
    Set<IProjectAttribute> pidcRemAttr = getPidcRemAttr(aliasAttrs, pidcVersAttrModel.getPidcVersAttrMap());

    AlternateAttrLoader alternateAttrLoader = new AlternateAttrLoader(getServiceData());
    PidcWebFlowAdapter adapter = new PidcWebFlowAdapter();

    Map<Long, Long> alternateAttrMap = alternateAttrLoader.getAllAlternateAttrs();
    // get all the attributes of the variant
    Map<Long, PidcVariantAttribute> allAttributes = pidcVersAttrModel.getVariantAttributeMap(pidcVariant.getId());

    PidcWebFlowDataCreator pidcWebFlowDataCreator =
        new PidcWebFlowDataCreator(adapter, alternateAttrMap, webFlowDataCreater, getServiceData());
    pidcWebFlowDataCreator.populateRemAliasAttrinResp(pidcRemAttr, aliasDef, pidcWebFlowData, allAttributes);

    Set<WebFlowAttribute> createPidcAttrForVar =
        pidcWebFlowDataCreator.createPidcAttrForVar(pidcVariant, aliasDef, allPidcVarAttr);
    // Review 238922
    Set<WebFlowAttribute> createPidcAttr =
        pidcWebFlowDataCreator.createPidcAtrrForPidcVar(pidcVariant, aliasDef, allPidcVersAttr, alternateAttrMap);
    createPidcAttrForVar.addAll(createPidcAttr);
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAttrForVar);

    return pidcWebFlowData;
  }

  /**
   * @param pidcVariant
   * @param pidcVersAttrModel
   * @param allAttributes
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVariantAttribute> getAllVarAttribute(final PidcVariant pidcVariant,
      final PidcVersionAttributeModel pidcVersAttrModel)
      throws DataException {
    Map<Long, PidcVariantAttribute> allPidcVarAttr = new HashMap<>();
    Map<Long, PidcVariantAttribute> allPidcVarInvisisbleAttr = new HashMap<>();
    // get all the attributes of the variant
    Map<Long, PidcVariantAttribute> allAttributes = pidcVersAttrModel.getVariantAttributeMap(pidcVariant.getId());

    PidcVariantAttributeLoader pidcVariantAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    for (Long attrId : pidcVersAttrModel.getVariantInvisbleAttributeSet(pidcVariant.getId())) {
      allPidcVarInvisisbleAttr.put(attrId, pidcVariantAttrLoader.getVariantAttribute(pidcVariant.getId(), attrId));
    }

    allPidcVarAttr.putAll(allAttributes);
    allPidcVarAttr.putAll(allPidcVarInvisisbleAttr);

    return allPidcVarAttr;
  }

  /**
   * @param pidcVersAttrModel
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionAttribute> getAllPidcVersAttribute(final PidcVersionAttributeModel pidcVersAttrModel)
      throws DataException {
    Map<Long, PidcVersionAttribute> allPidcVersAttr = new HashMap<>();
    Map<Long, PidcVersionAttribute> allPidcInvisisbleAttr = new HashMap<>();
    Map<Long, PidcVersionAttribute> pidcVersAttrMap = new HashMap<>();

    Long pidcVersId = pidcVersAttrModel.getPidcVersion().getId();

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(getServiceData());

    List<TabvProjectAttr> tabvProjectAttrsList = pidcVersLoader.getEntityObject(pidcVersId).getTabvProjectAttrs();
    for (TabvProjectAttr tabvProjectAttr : tabvProjectAttrsList) {
      PidcVersionAttribute pidcVersAttr = pidcVersAttrLoader.createDataObject(tabvProjectAttr);
      pidcVersAttrMap.put(pidcVersAttr.getAttrId(), pidcVersAttr);
    }

    for (Long attrId : pidcVersAttrModel.getPidcVersInvisibleAttrSet()) {
      PidcVersionAttribute pidcVersionAttributeForAttr = null;
      if (null != pidcVersAttrMap.get(attrId)) {
        pidcVersionAttributeForAttr = pidcVersAttrMap.get(attrId);
      }
      // Attribute Missing because of Dependency Attribute not set
      else {
        AttributeLoader attrLoader = new AttributeLoader(getServiceData());
        Attribute attribute = attrLoader.getDataObjectByID(attrId);
        pidcVersionAttributeForAttr = pidcVersAttrLoader.createDataObject(pidcVersId, attribute);
      }
      allPidcInvisisbleAttr.put(attrId, pidcVersionAttributeForAttr);
    }

    allPidcVersAttr.putAll(pidcVersAttrModel.getPidcVersAttrMap());
    allPidcVersAttr.putAll(allPidcInvisisbleAttr);

    return allPidcVersAttr;
  }

  /**
   * @param pidcVersion pidcVersion
   * @param aliasDef {@link AliasDef}
   * @return {@link PidcWebFlowData}
   */
  private PidcWebFlowData getDataForVersion(final PidcVersion pidcVersion, final AliasDef aliasDef,
      Set<PidcVariant> pidcVarSet)
      throws IcdmException {

    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    if (versLoader.isHiddenToCurrentUser(pidcVersion.getId())) {
      throw new IcdmException("The pidc version is hidden to user");
    }

    WebFlowDataCreator webFlowDataCreater = new WebFlowDataCreator();
    ProjectAttributeLoader projAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVersAttrModel = projAttrLoader.createModel(pidcVersion.getId());
    webFlowDataCreater.setPidcVersAttrModel(pidcVersAttrModel);
    webFlowDataCreater.setAliasDef(aliasDef);
    // Create web flow data object
    PidcWebFlowData pidcWebFlowData =
        new PidcWebFlowData(pidcVersion.getId(), pidcVersAttrModel.getPidc().getName(), "", pidcVersion.getVersion());

    // Task 232488
    Map<Long, PidcVersionAttribute> allPidcVersAttribute = getAllPidcVersAttribute(pidcVersAttrModel);
    webFlowDataCreater.setAllPidcVersAttr(allPidcVersAttribute);

    // Review 238922
    Map<Long, PidcVariantAttribute> allPidcVarAttr = new HashMap<>();
    Set<WebFlowAttribute> createPidcAttrForVar = new HashSet<>();

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    com.bosch.caltool.icdm.model.apic.attr.Attribute aprjNameAttr =
        attrLoader.getAllLevelAttributes().get(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR));
    IProjectAttribute pidcAttribute = allPidcVersAttribute.get(aprjNameAttr.getId());

    setVcdmElementName(pidcWebFlowData, pidcAttribute, webFlowDataCreater);

    AlternateAttrLoader alternateAttrLoader = new AlternateAttrLoader(getServiceData());
    PidcWebFlowAdapter adapter = new PidcWebFlowAdapter();

    Map<Long, Long> alternateAttrMap = alternateAttrLoader.getAllAlternateAttrs();
    PidcWebFlowDataCreator pidcWebFlowDataCreator =
        new PidcWebFlowDataCreator(adapter, alternateAttrMap, webFlowDataCreater, getServiceData());

    Map<Long, WebFlowAttribute> pidcAttrForVarMap = new HashMap<>();
    PidcVariantAttributeLoader pidcVariantAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    if (null == pidcVarSet) {
      // if element id is PIDC version ID, then all variants are considered
      pidcVarSet = new HashSet<>(pidcVersAttrModel.getVariantMap().values());
    }
    for (PidcVariant pidcVar : pidcVarSet) {
      allPidcVarAttr.putAll(pidcVersAttrModel.getVariantAttributeMap(pidcVar.getId()));
      Map<Long, PidcVariantAttribute> allPidcVarInvisisbleAttr = new HashMap<>();
      for (Long attrId : pidcVersAttrModel.getVariantInvisbleAttributeSet(pidcVar.getId())) {
        allPidcVarInvisisbleAttr.put(attrId, pidcVariantAttrLoader.getVariantAttribute(pidcVar.getId(), attrId));
      }
      allPidcVarAttr.putAll(allPidcVarInvisisbleAttr);
      webFlowDataCreater.setPidcVariant(pidcVar);
      Set<WebFlowAttribute> pidcAttrForVar =
          pidcWebFlowDataCreator.createPidcAttrForVar(pidcVar, aliasDef, allPidcVarAttr);
      // 5050260 - Defect fix - Validate issue in getPidcWebFlowData() service when moving attribute to variant level
      for (WebFlowAttribute pidcAttr : pidcAttrForVar) {
        if (pidcAttrForVarMap.containsKey(pidcAttr.getAttrID())) {
          pidcAttrForVarMap.get(pidcAttr.getAttrID()).getWebFlowAttrValues().addAll(pidcAttr.getWebFlowAttrValues());
        }
        else {
          pidcAttrForVarMap.put(pidcAttr.getAttrID(), pidcAttr);
        }
      }
    }
    createPidcAttrForVar.addAll(pidcAttrForVarMap.values());
    webFlowDataCreater.setAllPidcVarAttr(allPidcVarAttr);

    // Story 221726
    Map<Long, WebFlowAttribute> createPidcAtrrMapForPidc =
        pidcWebFlowDataCreator.createPidcAtrrMapForPidc(allPidcVersAttribute, alternateAttrMap);
    // 505260 - Defect fix - Validate issue in getPidcWebFlowData() service when moving attribute to variant level
    for (Entry<Long, WebFlowAttribute> webFlowAttr : pidcAttrForVarMap.entrySet()) {
      if (createPidcAtrrMapForPidc.containsKey(webFlowAttr.getKey())) {
        createPidcAtrrMapForPidc.put(webFlowAttr.getKey(), webFlowAttr.getValue());
      }
    }
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAtrrMapForPidc.values());
    // Review 238922
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAttrForVar);
    return pidcWebFlowData;
  }


  /**
   * @param pidcWebFlowData {@link PidcWebFlowData}
   * @param webFlowDataCreater {@link WebFlowDataCreator}
   * @param pidcAttribute {@link IProjectAttribute}
   * @throws DataException Exception
   */
  private void setVcdmElementName(final PidcWebFlowData pidcWebFlowData, final IProjectAttribute pidcAttribute,
      final WebFlowDataCreator webFlowDataCreater)
      throws DataException {
    AliasDetailLoader aliasDetailsloader = new AliasDetailLoader(getServiceData());
    Map<Long, AliasDetail> aliasDetailMap = aliasDetailsloader.getByAdId(webFlowDataCreater.getAliasDef().getId());
    webFlowDataCreater.setAliasDetailMap(aliasDetailMap);
    if ((null != pidcAttribute) && (null != pidcAttribute.getValueId())) {
      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      AttributeValue attributeValue = attrValueLoader.getDataObjectByID(pidcAttribute.getValueId());
      if (attributeValue != null) {
        pidcWebFlowData.setVcdmElementName(getValueAliasForAliasDef(webFlowDataCreater, attributeValue, pidcAttribute));
      }
    }
  }

  /**
   * @param webFlowDataCreater {@link WebFlowDataCreator}
   * @return AliasName
   * @throws DataException Exception
   */
  private String getValueAliasForAliasDef(final WebFlowDataCreator webFlowDataCreater, final AttributeValue attrValue,
      final IProjectAttribute varAttr) {
    String effValAliasName = null;
    // If alias Def is not null and attr Value is not null and type is text then take the alias otherwise take the attr
    // val eng
    if ((webFlowDataCreater.getAliasDef() != null) &&
        (attrValue.getValueType() == AttributeValueType.TEXT.getDisplayText())) {
      effValAliasName = getEffValueAttrAliasName(varAttr, webFlowDataCreater);
    }
    String stringValue;
    switch (AttributeValueType.getType(attrValue.getValueType())) {
      case TEXT:
        stringValue = attrValue.getTextValueEng();
        break;

      case NUMBER:
        stringValue = attrValue.getNumValue().toString();
        break;

      case DATE:
        stringValue = attrValue.getDateValue();
        break;

      case BOOLEAN:
        stringValue = attrValue.getBoolvalue();
        break;

      // iCDM-321
      case HYPERLINK:
        stringValue = attrValue.getTextValueEng();
        break;
      case ICDM_USER:
        stringValue = attrValue.getTextValueEng();
        break;
      default:
        stringValue = "ERROR";
        break;
    }
    return effValAliasName == null ? stringValue : effValAliasName;
  }

  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  private String getEffValueAttrAliasName(final IProjectAttribute varAttr,
      final WebFlowDataCreator webFlowDataCreater) {
    for (AliasDetail aliasDetail : webFlowDataCreater.getAliasDetailMap().values()) {
      if ((null != aliasDetail.getValueId()) && (null != varAttr) && (null != varAttr.getValueId()) &&
          aliasDetail.getValueId().equals(varAttr.getValueId())) {
        return aliasDetail.getAliasName();
      }
    }
    return null;
  }


  private void setAliasAttrs(final WebFlowDataCreator webFlowDataCreator, final Set<Attribute> aliasAttrs) {
    Collection<AliasDetail> details = webFlowDataCreator.getAliasDetailMap().values();
    // get all alias details info
    for (AliasDetail aliasDetail : details) {
      // Attr should not be in variant attr
      Attribute attribute = webFlowDataCreator.getPidcVersAttrModel().getAllAttrMap().get(aliasDetail.getAttrId());
      if ((attribute != null) && (webFlowDataCreator.getAllProjAttr().get(attribute.getId()) == null)) {
        aliasAttrs.add(attribute);
      }
    }
  }

  /**
   * @param aliasAttrs
   * @param allAttributes
   */
  private Set<IProjectAttribute> getPidcRemAttr(final Set<Attribute> aliasAttrs,
      final Map<Long, PidcVersionAttribute> allAttributes) {
    Set<IProjectAttribute> remPidcAttr = new HashSet<>();
    for (Attribute attribute : aliasAttrs) {
      remPidcAttr.add(allAttributes.get(attribute.getId()));
    }
    return remPidcAttr;
  }

  /**
   * @param elementID PidcVersId or PidcVariantId
   * @return {@link TWebFlowElement} List
   * @throws DataException
   */
  public List<TWebFlowElement> getWebFlowElements(final long elementID) throws DataException {
    WebflowElementLoader webFlowElementLoader = new WebflowElementLoader(getServiceData());
    return webFlowElementLoader.getTWebFlowElement(elementID);
  }

  private boolean validateWebFlowElements(final List<TWebFlowElement> dbWebFlowElements) {
    for (TWebFlowElement webflowElement : dbWebFlowElements) {
      if (webflowElement.getIsDeleted().equals("Y")) {
        return false;
      }
    }
    return true;
  }

  public PidcWebFlowElementRespType getPidcWebFlowElementRespType(final Long elementId,
      final Set<PidcWebFlowData> pidcWebFlowDataSet) {
    PidcWebFlowElementRespType pidcWebFlowElementRespType = new PidcWebFlowElementRespType();
    Set<WebFlowAttribute> webFlowAttr = new HashSet<>();
    Set<PidcWebFlowElementDetailsType> detailsTypeSet = new HashSet<>();

    for (PidcWebFlowData webFlowData : pidcWebFlowDataSet) {
      PidcWebFlowElementDetailsType detailsType = new PidcWebFlowElementDetailsType();
      detailsType.setId(webFlowData.getElemementID());
      StringBuilder elementName = new StringBuilder();
      elementName.append(PIDC_PREFIX);
      elementName.append(webFlowData.getName());
      if ((null != webFlowData.getVarName()) && !webFlowData.getVarName().isEmpty()) {
        elementName.append("/Variant:");
        elementName.append(webFlowData.getVarName());
      }
      detailsType.setName(elementName.toString());
      detailsType.setVariantName(webFlowData.getVarName());
      detailsType.setChangeNumber(webFlowData.getChangeNum());

      webFlowAttr.addAll(webFlowData.getWebFlowAttr());
      detailsTypeSet.add(detailsType);
    }
    pidcWebFlowElementRespType.setWebflowAttrSet(webFlowAttr);
    pidcWebFlowElementRespType.setDetailsTypeSet(detailsTypeSet);
    pidcWebFlowElementRespType.setSelectionType(getElementSelType(elementId, pidcWebFlowDataSet));
    return pidcWebFlowElementRespType;
  }

  /**
   * @param elementID
   * @param pidcWebFlowDataSet
   * @param elementSel
   * @return
   */
  private PidcWebFlowElementSelectionType getElementSelType(final long elementID,
      final Set<PidcWebFlowData> pidcWebFlowDataSet) {
    PidcWebFlowElementSelectionType elementSel = new PidcWebFlowElementSelectionType();
    if (!pidcWebFlowDataSet.isEmpty()) {
      PidcWebFlowData webFlowEle = pidcWebFlowDataSet.iterator().next();
      if (pidcWebFlowDataSet.size() > 1) {
        elementSel.setId(elementID);
        elementSel.setName(PIDC_PREFIX + webFlowEle.getName() + "/Multiple variants");
        elementSel.setType("Variant Selection");
        elementSel.setVcdmElementName(webFlowEle.getVcdmElementName());
      }
      else {
        elementSel.setId(elementID);
        if ((null != webFlowEle.getVarName()) && !webFlowEle.getVarName().isEmpty()) {
          elementSel.setName(PIDC_PREFIX + webFlowEle.getName() + "/Variant:" + webFlowEle.getVarName());
          elementSel.setType("Single Variant");
        }
        else {
          elementSel.setName(PIDC_PREFIX + webFlowEle.getName());
          elementSel.setType("Single PID Card");
        }
        elementSel.setVcdmElementName(webFlowEle.getVcdmElementName());
      }
    }
    return elementSel;
  }

  /**
   * @param elementID
   * @return
   * @throws DataException
   */
  public Set<PidcVariant> getAllWebFlowVariants(final long elementID) throws DataException {
    Set<PidcVariant> pidcVarSet = new HashSet<>();
    List<TWebFlowElement> dbWebFlowElements = getWebFlowElements(elementID);
    for (TWebFlowElement tWebFlowElement : dbWebFlowElements) {
      PidcVariant pidcVariant =
          new PidcVariantLoader(getServiceData()).getDataObjectByID(tWebFlowElement.getVariantID());
      pidcVarSet.add(pidcVariant);
    }
    return pidcVarSet;
  }
}