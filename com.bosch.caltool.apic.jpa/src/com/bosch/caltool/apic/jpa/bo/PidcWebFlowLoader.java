/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TWebFlowElement;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;


/**
 * @author rgo7cob
 */
public class PidcWebFlowLoader {

  /**
   *
   */
  private static final String INVALID_ELE_ID_MSG =
      "The element ID is not existing. Please provide an existing element ID";
  /**
   *
   */
  private static final String WEBFLOW_ALIAS_ID = "WEBFLOW_ALIAS_ID";
  /**
   * Instance of ApicDataProvider
   */
  private final ApicDataProvider attrDataProvider;
  /**
   * apic User for the request
   */
  private final ApicUser apicUser;

  private final PidcWebFlowDataCreator webflowCreator;

  // Story 221726
  private ConcurrentHashMap<Long, Long> allAlternateAttrMap = new ConcurrentHashMap<Long, Long>();


  /**
   * @param attrDataProvider attrDataProvider
   * @param apicUser apicUser
   * @param adapter adapter
   */
  public PidcWebFlowLoader(final ApicDataProvider attrDataProvider, final ApicUser apicUser,
      final PidcWebFlowAdapter adapter) {
    super();
    this.attrDataProvider = attrDataProvider;
    this.apicUser = apicUser;
    // Story 221726
    this.allAlternateAttrMap = attrDataProvider.getAllAlternateAttrs();
    this.webflowCreator = new PidcWebFlowDataCreator(adapter, this.allAlternateAttrMap, apicUser);
  }


  /**
   * @param elementID pidcVersionID
   * @param user user
   * @return the pidc web flow details
   * @throws IcdmException IcdmException
   */
  public PidcWebFlowData fetchDataForWebFlow(final Long elementID) throws IcdmException {


    String elementType = getElementType(elementID);
    if (CommonUtils.isEmptyString(elementType)) {
      throw new IcdmException("Not valid pidc or variant ID");
    }

    PidcWebFlowData pidcWebFlowData = null;
    AliasDefinition aliasDef = getWebFlowAliasDef();
    if (WsCommonConstants.WEBFLOW_VARIANT.equalsIgnoreCase(elementType)) {
      PIDCVariant pidcVariant = this.attrDataProvider.getPidcVaraint(elementID);
      pidcWebFlowData = getDataForVariant(pidcVariant, aliasDef);
    }
    else if (WsCommonConstants.WEBFLOW_PIDC.equalsIgnoreCase(elementType)) {
      PIDCVersion pidcVersion = this.attrDataProvider.getPidcVersion(elementID);
      pidcWebFlowData = getDataForVersion(pidcVersion, aliasDef);
    }


    return pidcWebFlowData;
  }

  /**
   * @param elementID
   * @return
   * @throws IcdmException
   */
  public Set<PidcWebFlowData> fetchDataForWebFlowElement(final long elementID) throws IcdmException {


    Set<PidcWebFlowData> pidcWebFlowDataSet = new HashSet<>();
    AliasDefinition aliasDef = getWebFlowAliasDef();


    List<TWebFlowElement> dbWebFlowElements = getWebFlowElements(elementID);
    if (dbWebFlowElements.isEmpty()) {
      String elementType = getElementType(elementID);
      if (!CommonUtils.isEmptyString(elementType)) {
        if (WsCommonConstants.WEBFLOW_VARIANT.equalsIgnoreCase(elementType)) {
          PIDCVariant pidcVariant = this.attrDataProvider.getPidcVaraint(elementID);
          pidcWebFlowDataSet.add(getDataForVariant(pidcVariant, aliasDef));
          CDMLogger.getInstance().info("Input Id " + elementID + " is a Pidc Variant Id");
        }
        else if (WsCommonConstants.WEBFLOW_PIDC.equalsIgnoreCase(elementType)) {
          PIDCVersion pidcVersion = this.attrDataProvider.getPidcVersion(elementID);
          pidcWebFlowDataSet.add(getDataForVersion(pidcVersion, aliasDef));
          CDMLogger.getInstance().info("Input Id " + elementID + " is a Pidc Version Id");
        }
        else {
          throw new IcdmException("Not a valid pidc or variant ID");
        }
      }
      else {
        throw new IcdmException(INVALID_ELE_ID_MSG);
      }
    }
    else {
      if (validateWebFlowElements(dbWebFlowElements)) {
        for (TWebFlowElement webflowEle : dbWebFlowElements) {

          long variantID = webflowEle.getVariantID();
          String eleType = getValidVariant(variantID);
          if (CommonUtils.isEmptyString(eleType)) {
            throw new IcdmException(INVALID_ELE_ID_MSG);
          }

          if (WsCommonConstants.WEBFLOW_VARIANT.equalsIgnoreCase(eleType)) {
            PIDCVariant pidcVariant = this.attrDataProvider.getPidcVaraint(variantID);
            PidcWebFlowData pidcWebFlowData = getDataForVariant(pidcVariant, aliasDef);
            pidcWebFlowDataSet.add(pidcWebFlowData);
          }
        }
        CDMLogger.getInstance().info("Input Id " + elementID + " is an Element Id for multiple variants");
      }
      else {
        throw new IcdmException("The element ID is marked as deleted. Only not deleted element IDs are accepted.");
      }
    }
    return pidcWebFlowDataSet;
  }

  /**
   * @param elementID
   * @return element Type
   */
  private String getElementType(final Long elementID) {
    String elementType = "";
    try {
      PIDCVersion pidcVersion = this.attrDataProvider.getPidcVersion(elementID, true);

      if (pidcVersion.isValid()) {
        elementType = "pidc";
      }
      else {
        elementType = getValidVariant(elementID);
      }
    }
    catch (DataException e) {
      try {
        elementType = getValidVariant(elementID);
      }
      catch (IcdmException | javax.persistence.NoResultException e1) {
        CDMLogger.getInstance().error(e1.getMessage(), e1, Activator.PLUGIN_ID);
      }
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    catch (IcdmException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);

    }
    return elementType;
  }


  /**
   * @param elementID
   * @param elementType
   * @return
   */
  private String getValidVariant(final Long elementID) throws IcdmException {
    String elementType = "";
    PIDCVariant pidcVariant = this.attrDataProvider.getPidcVariantAndVersion(elementID);

    if ((pidcVariant != null) && pidcVariant.isValid()) {
      elementType = "Variant";
    }
    return elementType;
  }


  /**
   * @return
   */
  private AliasDefinition getWebFlowAliasDef() {
    AliasDefinition aliasDef = null;
    // get the id of the web flow alias
    String parameterValue = this.attrDataProvider.getParameterValue(WEBFLOW_ALIAS_ID);
    // get all alias definition
    SortedSet<AliasDefinition> sortedAliasDef = this.attrDataProvider.getSortedAliasDef();
    for (AliasDefinition aliasDefinition : sortedAliasDef) {
      // Return the id corresponding
      if (aliasDefinition.getID().equals(Long.valueOf(parameterValue))) {
        aliasDef = aliasDefinition;
      }
    }
    return aliasDef;
  }


  /**
   * @param pidcVersion
   * @param aliasDef
   * @param user
   * @return
   */
  private PidcWebFlowData getDataForVersion(final PIDCVersion pidcVersion, final AliasDefinition aliasDef)
      throws IcdmException {

    if (pidcVersion.getPidcVersion().isHidden(this.apicUser)) {
      throw new IcdmException("The pidc version is hidden to user");
    }

    // Create web flow data object
    PidcWebFlowData pidcWebFlowData =
        new PidcWebFlowData(pidcVersion.getID(), pidcVersion.getPidc().getName(), "", pidcVersion.getVersion());
    // Iterate the attributes and get the attr and value alias
    Map<Long, PIDCAttribute> allAttributes = pidcVersion.getAttributes();

    // Task 232488
    Map<Long, PIDCAttribute> allPidcAttr = new HashMap<Long, PIDCAttribute>();
    allPidcAttr.putAll(allAttributes);
    allPidcAttr.putAll(pidcVersion.getInvisiblePIDCAttributesMap());

    // Review 238922
    Map<Long, PIDCAttributeVar> allPidcVarAttr = new HashMap<Long, PIDCAttributeVar>();
    Set<WebFlowAttribute> createPidcAttrForVar = new HashSet<>();

    for (PIDCVariant pidcVar : pidcVersion.getVariantsMap().values()) {
      allPidcVarAttr.putAll(pidcVar.getAttributes());
      allPidcVarAttr.putAll(pidcVar.getInvisiblePIDCVarAttributesMap());
      createPidcAttrForVar.addAll(this.webflowCreator.createPidcAttrForVar(pidcVar, aliasDef, allPidcVarAttr));
    }

    Attribute aprjNameAttr =
        this.attrDataProvider.getDataCache().getSpecificAttributeMap().get(ApicConstants.VCDM_APRJ_NAME_ATTR);
    IPIDCAttribute pidcAttribute = allAttributes.get(aprjNameAttr.getAttributeID());

    if (pidcAttribute.isHiddenToUser(this.apicUser)) {
      pidcWebFlowData.setVcdmElementName("hidden");
    }
    else {
      setVcdmElementName(pidcWebFlowData, pidcAttribute, aliasDef);
    }
    // Story 221726
    Set<WebFlowAttribute> createPidcAtrrForPidc =
        this.webflowCreator.createPidcAtrrForPidc(aliasDef, allPidcAttr, this.allAlternateAttrMap);
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAtrrForPidc);
    // Review 238922
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAttrForVar);
    return pidcWebFlowData;
  }

  /**
   * @param pidcWebFlowData
   * @param aliasDef
   * @param allAttributes
   */
  private void setVcdmElementName(final PidcWebFlowData pidcWebFlowData, final IPIDCAttribute pidcAttribute,
      final AliasDefinition aliasDef) {
    if (pidcAttribute != null) {
      AttributeValue attributeValue = pidcAttribute.getAttributeValue();
      if (attributeValue != null) {
        pidcWebFlowData.setVcdmElementName(pidcAttribute.getValueAliasForAliasDef(aliasDef));
      }
    }
  }


  /**
   * @param pidcVariant
   * @param aliasDef
   * @param user
   * @return
   */
  private PidcWebFlowData getDataForVariant(final PIDCVariant pidcVariant, final AliasDefinition aliasDef)
      throws IcdmException {

    PIDCVersion pidcVersion = pidcVariant.getPidcVersion();

    if (pidcVersion.isHidden(this.apicUser)) {
      throw new IcdmException("The pidc version is hidden to user");
    }


    // Create web flow data object
    // String name = "PIDC:" + pidcVersion.getName() + "/Variant:" + pidcVariant.getName();
    PidcWebFlowData pidcWebFlowData = new PidcWebFlowData(pidcVariant.getID(), pidcVersion.getName(),
        pidcVariant.getName(), pidcVariant.getVersion());

    // get all the attributes of the variant
    Map<Long, PIDCAttributeVar> allAttributes = pidcVariant.getAttributes();

    // Task 232488
    Map<Long, PIDCAttribute> allPidcAttr = new HashMap<Long, PIDCAttribute>();
    allPidcAttr.putAll(pidcVersion.getAttributes());
    allPidcAttr.putAll(pidcVersion.getInvisiblePIDCAttributesMap());
    // Task 232488
    Map<Long, PIDCAttributeVar> allPidcVarAttr = new HashMap<Long, PIDCAttributeVar>();
    allPidcVarAttr.putAll(allAttributes);
    allPidcVarAttr.putAll(pidcVariant.getInvisiblePIDCVarAttributesMap());

    // for the aprj attrname use the pric version attr
    Attribute aprjNameAttr =
        this.attrDataProvider.getDataCache().getSpecificAttributeMap().get(ApicConstants.VCDM_APRJ_NAME_ATTR);
    IPIDCAttribute pidcAttribute = pidcVersion.getAttributes().get(aprjNameAttr.getAttributeID());

    if (pidcAttribute.isHiddenToUser(this.apicUser)) {
      pidcWebFlowData.setVcdmElementName("hidden");
    }
    else {
      setVcdmElementName(pidcWebFlowData, pidcAttribute, aliasDef);
    }

    Map<Long, IPIDCAttribute> allIPICAttr = new HashMap<>();
    allIPICAttr.putAll(allPidcAttr);
    allIPICAttr.putAll(allPidcVarAttr);

    Set<Attribute> aliasAttrs = new HashSet<>();
    setAliasAttrs(aliasDef, aliasAttrs, allIPICAttr);
    Set<IPIDCAttribute> pidcRemAttr = getPidcRemAttr(aliasAttrs, pidcVersion.getAttributes());


    this.webflowCreator.populateRemAliasAttrinResp(pidcRemAttr, aliasDef, pidcWebFlowData, allAttributes);

    Set<WebFlowAttribute> createPidcAttrForVar =
        this.webflowCreator.createPidcAttrForVar(pidcVariant, aliasDef, allPidcVarAttr);
    // Review 238922
    Set<WebFlowAttribute> createPidcAttr =
        this.webflowCreator.createPidcAtrrForPidcVar(pidcVariant, aliasDef, allPidcAttr, this.allAlternateAttrMap);
    createPidcAttrForVar.addAll(createPidcAttr);
    pidcWebFlowData.getWebFlowAttr().addAll(createPidcAttrForVar);

    return pidcWebFlowData;
  }


  /**
   * @param aliasAttrs
   * @param allAttributes
   */
  private Set<IPIDCAttribute> getPidcRemAttr(final Set<Attribute> aliasAttrs,
      final Map<Long, PIDCAttribute> allAttributes) {
    Set<IPIDCAttribute> remPidcAttr = new HashSet<>();
    for (Attribute attribute : aliasAttrs) {
      remPidcAttr.add(allAttributes.get(attribute.getAttributeID()));
    }
    return remPidcAttr;
  }


  /**
   * @param aliasDef
   * @param aliasAttrs
   * @param allAttributes
   */
  private void setAliasAttrs(final AliasDefinition aliasDef, final Set<Attribute> aliasAttrs,
      final Map<Long, IPIDCAttribute> allAttributes) {
    Collection<AliasDetail> details = aliasDef.getAliasDetAttrMap().values();
    // get all alias details info
    for (AliasDetail aliasDetail : details) {
      // Attr should not be in variant attr
      if ((aliasDetail.getAttribute() != null) &&
          (allAttributes.get(aliasDetail.getAttribute().getAttributeID()) == null)) {
        aliasAttrs.add(aliasDetail.getAttribute());
      }

    }
  }

  /**
   * @param dbWebFlowElements
   * @return
   * @throws IcdmException
   */
  private boolean validateWebFlowElements(final List<TWebFlowElement> dbWebFlowElements) throws IcdmException {
    for (TWebFlowElement webflowElement : dbWebFlowElements) {
      if (webflowElement.getIsDeleted().equals("Y")) {
        return false;
      }
    }
    return true;
  }


  /**
   * @param elementID
   * @return
   */
  private List<TWebFlowElement> getWebFlowElements(final long elementID) {
    final EntityManager entMgr = this.attrDataProvider.getEntityProvider().getEm();
    TypedQuery<TWebFlowElement> tQuery =
        entMgr.createNamedQuery(TWebFlowElement.NQ_GET_VAR_BY_ELE_ID, TWebFlowElement.class);
    tQuery.setParameter("elementId", elementID);
    return tQuery.getResultList();
  }
}
