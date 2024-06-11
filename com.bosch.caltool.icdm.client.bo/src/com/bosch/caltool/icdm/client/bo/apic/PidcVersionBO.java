/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcDetStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedAttrValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class PidcVersionBO extends AbstractProjectObjectBO {


  private static final int STR_BUILDER_SIZE = 65;

  /**
   * Key name for PIDC revision field in object details
   */
  public static final String FLD_PIDC_REVISION = "PIDC_REVISION";
  /**
   * Key name for PIDC version state field in object details
   */
  public static final String FLD_PIDC_VRSN_STATE = "PIDC_VRSN_STATE";
  /**
   * Key name for PIDC version name field in object details
   */
  public static final String FLD_PIDC_VRSN_NAME = "FLD_PIDC_VRSN_NAME";
  /**
   * Key name for PIDC version desc eng field in object details
   */
  public static final String FLD_PIDC_VRSN_DESC_ENG = "FLD_PIDC_VRSN_DESC_ENG";
  /**
   * Key name for PIDC version desc eng field in object details
   */
  public static final String FLD_PIDC_VRSN_DESC_GER = "FLD_PIDC_VRSN_DESC_GER";

  /**
   * Map of deleted attributes
   */
  private Map<Long, PidcVersionAttribute> deletedAttrsMap = new HashMap<>();

  /**
   * Used attributes
   */
  private Map<Long, PidcVersionAttribute> usedPIDCAttrMap;

  /**
   * Not used attributes
   */
  private Map<Long, PidcVersionAttribute> notUsedPIDCAttrMap;

  /**
   * Project attributes, defined at variant level
   */
  private Map<Long, PidcVersionAttribute> variantAttrMap;

  private Map<Long, PidcVariant> variantMap;


  private Map<Long, PidcVersionAttribute> allAttrMap;

  private PIDCVersionStatistics pidcVersionStatistics;

  /**
   * Not defined attributes
   */
  private Map<Long, PidcVersionAttribute> usedNotDefindPIDCAttrMap;

  private Map<Long, PidcVersionAttribute> attrMap = new ConcurrentHashMap<>();

  /**
   * The grouped attributes of a particular version is available in this set
   */
  private final Set<IProjectAttribute> groupedAttrs = new HashSet<>();

  private final ConcurrentMap<IProjectAttribute, IProjectAttribute> predefAttrGrpAttrMap = new ConcurrentHashMap<>();

  Map<Long, PidcA2lFileExt> allPidcA2lMap = new HashMap<>();

  private final PidcDetailsNodeHandler pidcDetailsNodeHandler = new PidcDetailsNodeHandler(this);

  /**
   * PidcVariant/PidcVersion/PidcSubvariant - this field is to set the selection for object which has undefined mand/uc
   * attr
   */
  private Object selectedObjForFiltering;

  /**
   * @param pidcVersion
   * @param pidcpidcDataHandler
   * @param allAttrMap
   */
  public PidcVersionBO(final PidcVersion pidcVersion, final PidcDataHandler pidcDataHandler) {

    super(null, pidcVersion, pidcDataHandler);

    this.pidcVersion = pidcVersion;

    this.attrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.deletedAttrsMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.variantAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.usedPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.notUsedPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.usedNotDefindPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());

    this.allAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PidcVersionAttribute>());
    this.allAttrMap.putAll(this.pidcDataHandler.getPidcVersAttrMap());


  }

  /**
   * @return true if variant can be created for the PIDC
   */
  public boolean canCreateVariant() {

    return isModifiable() &&
        CommonUtils.isEqual(this.pidcVersion.getPidStatus(), PidcVersionStatus.IN_WORK.getDbStatus());

  }

  /**
   * @return true if sub variant can be created for the PIDC
   */
  public boolean canCreateSubVariant() {
    return isModifiable() &&
        CommonUtils.isEqual(this.pidcVersion.getPidStatus(), PidcVersionStatus.IN_WORK.getDbStatus());
  }


  /**
   * @param refresh - TO RELOA
   * @return
   * @throws ApicWebServiceException
   */
  @Override
  public Map<Long, PidcVersionAttribute> getAttributes(final boolean refresh) {

    try {
      // Reload all attribute collections when (a) input flag is true, or (b) not loaded before.
      if (refresh || this.childrenLoaded) {
        this.allAttrMap.clear();
        this.attrMap.clear();

        // fetch attributes
        fillAllAttributes();

        this.attrMap.putAll(this.allAttrMap);

        // Thread dead lock heppens when pidcversion getAttributes is locked by thread 1 and pidc varaint getAttribute
        // is
        // locked by thread 2.
        // here thread 1 again itreates through the variants and calls getAttributes so dead lock occurs.
        if (!CommonUtils.isStartedFromWebService()) {
          fillPredefinedAttributes();
        }

        this.childrenLoaded = true;

      }
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    // return complete list
    return this.allAttrMap;
  }

  /**
   * @return
   */
  public boolean isHidden() {

    try {
      if (this.currentUser.hasApicWriteAccess()) {
        return false;
      }
      NodeAccess nodeAccess = this.currentUser.getNodeAccessRight(this.pidcVersion.getPidcId());
      if ((nodeAccess == null) || !nodeAccess.isRead()) {
        return true;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return false;
  }


  private void fillDefinedAttributes() {


    // add attributes with or without values
    // exactly: all attributes defined in TabV_ProjectAttr
    for (PidcVersionAttribute pidcVersAttr : this.allAttrMap.values()) {

      // skip attributes marked as deleted
      if (this.pidcDataHandler.getAttributeMap().get(pidcVersAttr.getAttrId()).isDeleted()) {
        continue;
      }

      // remove the empty attribute
      this.allAttrMap.remove(pidcVersAttr.getAttrId());

      // add the attribute
      this.allAttrMap.put(pidcVersAttr.getAttrId(), pidcVersAttr);
    }
  }


  /**
   * To check if attr level - version/variant/sub variant
   *
   * @param grpAttr
   */
  private void checkGrpAttrLvl(final IProjectAttribute grpAttr) {


    if (grpAttr != null) {
      // Check if the grouped attribute is in variant level
      if (grpAttr.isAtChildLevel()) {
        checkForVariantLvl(grpAttr);
      }
      else {
        getPredefinedAttr(grpAttr);
      }
    }
  }

  /**
   * To check if attr is in variant level
   *
   * @param grpAttr
   */
  private void checkForVariantLvl(final IProjectAttribute grpAttr) {

    for (PidcVariant variant : this.pidcDataHandler.getVariantMap().values()) {

      for (PidcVariantAttribute pidcVarAttr : this.pidcDataHandler.getVariantAttributeMap().get(variant.getId())
          .values()) {

        // Check if the grouped attribute is in sub variant level
        checkForSubVarLvl(grpAttr, pidcVarAttr);
      }
    }
  }

  /**
   * To check if attr is in sub variant level
   *
   * @param grpAttr
   * @param pidcVarAttr
   */
  private void checkForSubVarLvl(final IProjectAttribute grpAttr, final PidcVariantAttribute pidcVarAttr) {

    // Check if the grouped attribute is in sub variant level
    if (grpAttr.isAtChildLevel()) {

      for (PidcSubVariant subVariant : this.pidcDataHandler.getSubVariantMap().values()) {
        for (PidcSubVariantAttribute pidcSubVar : this.pidcDataHandler.getSubVariantAttributeMap()
            .get(subVariant.getId()).values()) {

          getPredefinedAttr(pidcSubVar);
        }
      }
    }
    else {
      getPredefinedAttr(pidcVarAttr);
    }
  }


  /**
   * @param grpAttr
   */
  private void getPredefinedAttr(final IProjectAttribute grpAttr) {

    if (grpAttr instanceof PidcVersionAttribute) {
      PidcVersionAttribute grpdAttr = (PidcVersionAttribute) grpAttr;

      if ((null != grpdAttr.getValueId()) &&
          (null != this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId()))) {

        getPredefAttrLevels(grpdAttr);
      }
    }
    else if (grpAttr instanceof PidcVariantAttribute) {
      getPreDefAttrForPidcVarAttr(grpAttr);
    }
    else if (grpAttr instanceof PidcSubVariantAttribute) {
      getPreDefAttrForPidcSubVarAttr(grpAttr);
    }
  }

  /**
   * @param grpAttr
   */
  private void getPreDefAttrForPidcSubVarAttr(final IProjectAttribute grpAttr) {
    PidcSubVariantAttribute grpdAttr = (PidcSubVariantAttribute) grpAttr;
    if ((null != grpdAttr.getValueId()) &&
        (null != this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId()))) {

      for (PredefinedAttrValue predefAttrVal : this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId())
          .values()) {

        PidcVersionAttribute predefAttr =
            this.pidcDataHandler.getPidcVersAttrMap().get(predefAttrVal.getPredefinedAttrId());
        if (null != predefAttr) {
          // Check if predef attr is in variant
          if (predefAttr.isAtChildLevel()) {

            PidcVariantBO pidcVarHandler = new PidcVariantBO(this.pidcVersion,
                this.pidcDataHandler.getVariantMap()
                    .get(this.pidcDataHandler.getSubVariantMap().get(grpdAttr.getSubVariantId()).getPidcVariantId()),
                this.pidcDataHandler);

            if (null != pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId())) {

              PidcVariantAttribute predefAttrVar = pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId());
              if (null != predefAttrVar) {
                // Check if predef attr is in sub variant
                if (predefAttrVar.isAtChildLevel()) {

                  PidcSubVariantBO pidcSubVarhandler = new PidcSubVariantBO(this.pidcVersion,
                      this.pidcDataHandler.getSubVariantMap().get(grpdAttr.getSubVariantId()), this.pidcDataHandler);

                  if (null != pidcSubVarhandler.getAttributesAll().get(predefAttr.getAttrId())) {
                    PidcSubVariantAttribute predefAttrSubVar =
                        pidcSubVarhandler.getAttributesAll().get(predefAttr.getAttrId());

                    this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                  }
                }
                else {

                  this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                }
              }
            }
          }
          else {

            this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
          }
        }
      }
    }
  }

  /**
   * @param grpAttr
   */
  private void getPreDefAttrForPidcVarAttr(final IProjectAttribute grpAttr) {
    PidcVariantAttribute grpdAttr = (PidcVariantAttribute) grpAttr;
    if ((null != grpdAttr.getValueId()) &&
        (null != this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId()))) {

      for (PredefinedAttrValue predefAttrVal : this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId())
          .values()) {

        PidcVersionAttribute predefAttr =
            this.pidcDataHandler.getPidcVersAttrMap().get(predefAttrVal.getPredefinedAttrId());
        if (null != predefAttr) {
          // Check if predef attr is in variant
          if (predefAttr.isAtChildLevel()) {

            PidcVariantBO pidcVarHandler = new PidcVariantBO(this.pidcVersion,
                this.pidcDataHandler.getVariantMap().get(grpdAttr.getVariantId()), this.pidcDataHandler);


            if (null != pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId())) {


              PidcVariantAttribute predefAttrVar = pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId());
              if (null != predefAttrVar) {
                // Check if predef attr is in sub variant
                if (predefAttrVar.isAtChildLevel()) {

                  for (PidcSubVariant pidcSubVar : pidcVarHandler.getSubVariantsMap().values()) {
                    PidcSubVariantBO pidcSubVarHandler =
                        new PidcSubVariantBO(this.pidcVersion, pidcSubVar, this.pidcDataHandler);

                    if (null != pidcSubVarHandler.getAttributesAll().get(predefAttr.getAttrId())) {


                      PidcSubVariantAttribute predefAttrSubVar =
                          pidcSubVarHandler.getAttributesAll().get(predefAttr.getAttrId());

                      this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                    }
                  }
                }
                else {
                  this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                }
              }
            }
          }
          else {
            this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
          }
        }
      }
    }
  }


  /**
   * @param grpdAttr
   */
  private void getPredefAttrLevels(final PidcVersionAttribute grpdAttr) {
    for (PredefinedAttrValue predefAttrVal : this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId())
        .values()) {

      PidcVersionAttribute predefAttr =
          this.pidcDataHandler.getPidcVersAttrMap().get(predefAttrVal.getPredefinedAttrId());

      if (null != predefAttr) {
        // Check if predef attr is in variant
        if (predefAttr.isAtChildLevel()) {
          for (PidcVariant pidcVar : this.pidcDataHandler.getVariantMap().values()) {
            PidcVariantBO pidcVarHandler = new PidcVariantBO(this.pidcVersion, pidcVar, this.pidcDataHandler);

            if (null != pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId())) {
              PidcVariantAttribute predefAttrVar = pidcVarHandler.getAttributesAll().get(predefAttr.getAttrId());

              if (null != predefAttrVar) {
                // Check if predef attr is in sub variant
                if (predefAttrVar.isAtChildLevel()) {


                  for (PidcSubVariant pidcSubVar : pidcVarHandler.getSubVariantsMap().values()) {
                    PidcSubVariantBO pidcSubVarHandler =
                        new PidcSubVariantBO(this.pidcVersion, pidcSubVar, this.pidcDataHandler);

                    if (null != pidcSubVarHandler.getAttributesAll().get(predefAttr.getAttrId())) {

                      PidcSubVariantAttribute predefAttrSubVar =
                          pidcSubVarHandler.getAttributesAll().get(predefAttr.getAttrId());
                      this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                    }
                  }
                }
                else {
                  this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                }
              }
            }
          }
        }
        else {
          this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
        }
      }
    }
  }


  /**
   * @param refresh - flag to indicate if map must be refreshed
   * @return set of grouped attrs in the pidcversion
   */
  public Set<IProjectAttribute> fillApplicableGroupedAttributes(final boolean refresh) {

    if (CommonUtils.isNullOrEmpty(this.groupedAttrs) || refresh) {
      if (refresh) {
        this.groupedAttrs.clear();
      }


      for (PidcVersionAttribute pidcVersAttr : this.pidcDataHandler.getPidcVersAttrMap().values()) {
        if (this.pidcDataHandler.getAttributeMap().get(pidcVersAttr.getAttrId()).isGroupedAttr()) {
          checkGrpAttrLevel(pidcVersAttr);
        }
      }
    }
    return this.groupedAttrs;
  }

  /**
   * @param pidcVersAttr
   */
  private void checkGrpAttrLevel(final PidcVersionAttribute grpAttr) {
    // Check if the grouped attribute is in variant level
    if (grpAttr.isAtChildLevel()) {
      checkForVariantLevel(grpAttr);
    }
    else {
      this.groupedAttrs.add(grpAttr);
    }
  }

  /**
   * @param grpAttr
   */
  private void checkForVariantLevel(final PidcVersionAttribute grpAttr) {

    for (PidcVariant pidcVar : this.pidcDataHandler.getVariantMap().values()) {
      PidcVariantAttribute grpAttrVar =
          this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId()).get(grpAttr.getAttrId());
      if (null != grpAttrVar) {
        // Check if the grouped attribute is in sub variant level
        checkForSubVarLevel(grpAttr, grpAttrVar);
      }
    }
  }

  /**
   * @param grpAttr
   * @param pidcVar
   * @param grpAttrVar
   */
  private void checkForSubVarLevel(final PidcVersionAttribute grpAttr, final PidcVariantAttribute grpAttrVar) {
    // Check if the grouped attribute is in sub variant level
    if (grpAttrVar.isAtChildLevel()) {
      for (PidcSubVariant pidcSubVar : this.pidcDataHandler.getSubVariantMap().values()) {
        PidcSubVariantAttribute grpAttrSubVar =
            this.pidcDataHandler.getSubVariantAttributeMap().get(pidcSubVar.getId()).get(grpAttr.getAttrId());
        if (grpAttrSubVar != null) {
          this.groupedAttrs.add(grpAttrSubVar);
        }
      }
    }
    else {
      this.groupedAttrs.add(grpAttrVar);
    }
  }

  /**
   * @param attrValue value for which validity in pidc needs to be checked
   * @param predefinedValidityMap
   * @return boolean flag :checks the validity of grouped attribute in a pidcversion
   */
  public boolean checkGroupedAttrValueValidity(final AttributeValue attrValue,
      final Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap) {
    if (attrValue != null) {
      for (PidcVersionAttribute levelAttr : this.pidcDataHandler.getPidcVersionInfo().getLevelAttrMap().values()) {

        if ((null == predefinedValidityMap.get(attrValue.getId())) ||
            ((null != predefinedValidityMap.get(attrValue.getId())) && (null != levelAttr) &&
                isPredefinedValidityDefined(predefinedValidityMap.get(attrValue.getId()), levelAttr))) {
          return true;
        }
      }
    }

    return false;

  }

  /**
   * Method to check if the predefined validity condition (validity attr id and validity value id) are same as level
   * attribute is attr id and value id
   *
   * @param predefinedValidityMap
   * @param levelAttr
   * @return true if predefined validity condition is presemt
   */
  private boolean isPredefinedValidityDefined(final Map<Long, PredefinedValidity> predefinedValidityMap,
      final PidcVersionAttribute levelAttr) {
    for (PredefinedValidity predefValidity : predefinedValidityMap.values()) {
      if (predefValidity.getValidityAttrId().equals(levelAttr.getAttrId()) &&
          predefValidity.getValidityValueId().equals(levelAttr.getValueId())) {
        return true;
      }
    }
    return false;
  }


  /**
   * Gets the root level nodes of this project ID card. If virtual structure is not defined for this project, the method
   * returns null
   *
   * @param includeDeletedVarSubVars true to include deleted variants and sub variants
   * @param onlyUncleared true if variants with uncleared / missing mandatory values required
   * @return PIDC Nodes at the root level
   */
  public SortedSet<PIDCDetailsNode> getRootVirtualNodes(final boolean includeDeletedVarSubVars,
      final boolean onlyUncleared) {

    this.pidcDetailsNodeHandler.setIncludeDeletedVarSubVars(includeDeletedVarSubVars);
    this.pidcDetailsNodeHandler.setShowOnlyUncleared(onlyUncleared);
    return this.pidcDetailsNodeHandler.getRootVirtualNodes();
  }

  /**
   * Refresh the nodes. To be used if attribute values are changed.
   *
   * @param includeDeleted include deleted flag
   */
  public void refreshNodes(final boolean includeDeleted) {
    this.pidcDetailsNodeHandler.setIncludeDeletedVarSubVars(includeDeleted);
    this.pidcDetailsNodeHandler.refreshNodes();
  }

  /**
   * Checks whether this PIDC has virtual structure
   *
   * @return true/false
   */
  public boolean hasVirtualStructure() {
    return !getPidcpidcDataHandler().getPidcDetStructureMap().isEmpty();
  }


  /**
   * Get a map of the structure attrs
   *
   * @return virtual level attributes with key as 'level' and value as 'structure object'
   */
  public Map<Long, PidcDetStructure> getVirtualLevelAttrs() {
    return getPidcpidcDataHandler().getPidcDetStructureMap();
  }

  /**
   * @return set of grouped attrs
   */
  public Set<IProjectAttribute> getApplicableGroupedAttributes() {
    return this.groupedAttrs;
  }

  /**
   * @return attr value set for Icdm Questionnaire Config attribute
   */
  public AttributeValue getQnaireConfigValue() {
    Long attrId;
    try {
      attrId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      Long valId = this.pidcDataHandler.getPidcVersAttrMap().get(attrId).getValueId();
      return this.pidcDataHandler.getAttributeValue(valId);
    }
    catch (NumberFormatException | ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return null;
    }
  }

  /**
   * @return is load fc2wp mapping in a2l
   */
  public boolean isQnaireConfigBEG() {
    AttributeValue qnaireConfigValue = getQnaireConfigValue();
    try {
      return (null != qnaireConfigValue) && CommonUtils.isEqual(qnaireConfigValue.getId(),
          Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID)));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @return the pidc attr
   */
  public PidcVersionAttribute getSSDProjNodeAttr() {
    long wpAttrId;
    try {
      wpAttrId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.SSD_PROJ_NODE_ATTR_ID));
      return getAttributes().get(wpAttrId);
    }
    catch (NumberFormatException | ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return null;
    }
  }


  /**
   * Method to fill attributes
   */
  private void fillAllAttributes() {

    try {
      PidcVersionAttribute pidcVersionAttr;

      // add attributes to the list
      for (Attribute attribute : this.pidcDataHandler.getAttributeMap().values()) {

        if (attribute != null) {

          if (attribute.isDeleted()) {
            pidcVersionAttr = this.pidcDataHandler.getPidcVersAttrMap().get(attribute.getId());
            if (pidcVersionAttr == null) {
              this.deletedAttrsMap.put(attribute.getId(), pidcVersionAttr);
            }
          }

          // skip attributes which are marked as deleted ,and also attributes
          // having levels -1,-2 and -3 which are
          // attributes Project Name,Variant Code and VariantCoding Code respectively.
          if (attribute.isDeleted() || (attribute.getLevel() == ApicConstants.PROJECT_NAME_ATTR) ||
              (attribute.getLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
              (attribute.getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
            if (this.allAttrMap.containsKey(attribute.getId())) {
              this.allAttrMap.remove(attribute.getId());
            }
            continue;
          }

          pidcVersionAttr = this.pidcDataHandler.getPidcVersAttrMap().get(attribute.getId());


          this.allAttrMap.put(attribute.getId(), pidcVersionAttr);
        }

      }

    }
    catch (Exception e) {

      CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
  }


  /**
   * Get a MAP of the PIDCs Variants including deleted variants
   *
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PidcVariant> getVariantsMap() {
    return this.pidcDataHandler.getVariantMap();
  }

  /**
   * ICDM-789 Get a MAP of the PIDCs Variants
   *
   * @param deleteNeeded deletedNeeded true or false if deleted variants are required
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PidcVariant> getVariantsMap(final boolean deleteNeeded) {

    return getVariantsMap(deleteNeeded, false);
  }


  /**
   * Get a MAP of the PIDCs Variants
   *
   * @param deleteNeeded deletedNeeded true or false if deleted variants are required
   * @param showOnlyUncleared true or false if variants with only uncleared / missing mandatory values to be displayed
   *          showOnlyUncleared = true --> Only variants with uncleared / missing mandatory values will be displayed in
   *          PIDC structure view
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PidcVariant> getVariantsMap(final boolean deleteNeeded, final boolean showOnlyUncleared) {

    final Map<Long, PidcVariant> pidcVariantsMapIterate = getVariantsMap();

    final Map<Long, PidcVariant> pidcVariantsMap = new ConcurrentHashMap<>();

    for (PidcVariant pidcVariant : pidcVariantsMapIterate.values()) {

      // Check if uncleared variants need to be displayed
      if (showOnlyUncleared) {
        PidcVariantBO varHandler = new PidcVariantBO(this.pidcVersion, pidcVariant, this.pidcDataHandler);

        // If uncleared variants are needed, check if it has invalid attributes and mandatory attributes are not defined
        if (varHandler.hasInvalidAttrValues() || (!varHandler.isAllMandatoryAttrDefined())) {
          checkIfDeletedNeeded(pidcVariantsMap, pidcVariant);
        }
      }
      else {
        checkIfDeletedNeeded(pidcVariantsMap, pidcVariant);
      }
      // Check whether deleted variants are needed
      if (deleteNeeded && pidcVariant.isDeleted()) {
        pidcVariantsMap.put(pidcVariant.getId(), pidcVariant);
      }
    }

    return pidcVariantsMap;
  }


  /**
   * This method checks for whether all mandatory attrs are defined in PIDC
   *
   * @return boolean defines whether all mandatory attrs are defined in PIDC
   */
  @Override
  public final boolean isAllMandatoryAttrDefined() {

    boolean allManAttrDefined;
    // Get PIDC all attributes
    final Collection<PidcVersionAttribute> pidcAttrs = this.getAttributes(false).values();
    // Validate Pidc attributes
    allManAttrDefined = checkManProjAttributes(pidcAttrs);
    if (allManAttrDefined) {
      allManAttrDefined = canReleaseVariantAttrs();
    }
    return allManAttrDefined;
  }

  /**
   * @return the isProjUseCaseAttrNotDefined
   */
  public boolean isProjUseCaseAttrNotDefined() {
    if (this.pidcVersionStatistics != null) {
      return this.pidcVersionStatistics.isProjUseCaseAttrNotDefined();
    }
    return false;
  }

  /**
   * This method validates PIDC/Variant/Sub-Variant attributes
   *
   * @param <P>
   * @param projAttrs
   * @return boolean
   */
  private <P extends IProjectAttribute> boolean checkManProjAttributes(final Collection<P> projAttrs) {
    boolean canRelease = true;
    for (P attribute : projAttrs) {

      canRelease = isMandatoryAttrDefined(attribute);
      if (!canRelease) {
        break;
      }
    }
    return canRelease;
  }

  /**
   * @return true if all quotation relevant attributes are defined at pidc version , variant and subvariant level
   */
  public boolean isAllQuotationAttrDefined() {

    boolean canRelease = true;
    for (IProjectAttribute attribute : this.getAttributes(false).values()) {

      canRelease = isQuotationAttrDefined(attribute);
      if (!canRelease) {
        break;
      }
    }
    return canRelease;

  }


  /**
   * This method checks for whether PIDC Variant attributes can do release or not
   *
   * @return boolean
   */
  // ICDM-329
  // ICDM-179
  private boolean canReleaseVariantAttrs() {
    boolean canRelease = true;
    // Get PIDC variants
    for (PidcVariant pidcVariant : getVariantsMap().values()) {
      // Check if variant is not deleted
      if (!pidcVariant.isDeleted()) {
        // Get Variant all attributes
        PidcVariantBO varHandler = new PidcVariantBO(this.pidcVersion, pidcVariant, this.pidcDataHandler);
        final Collection<PidcVariantAttribute> varaAttrs = varHandler.getAttributes(false, false).values();
        canRelease = checkManProjAttributes(varaAttrs);
        if (canRelease) {
          canRelease = checkManSubVarAttrs(pidcVariant);
          if (!canRelease) {
            break;
          }
        }
        else {
          break;
        }
      }
    }
    return canRelease;
  }


  /**
   * This method checks for whether PIDC Sub-Variant attributes can do release or not
   *
   * @param pidcVariant instance
   * @return boolean
   */
  // ICDM-329
  // ICDM-179
  private boolean checkManSubVarAttrs(final PidcVariant pidcVariant) {
    boolean canRelease = true;

    PidcVariantBO variantHandler = new PidcVariantBO(this.pidcVersion, pidcVariant, this.pidcDataHandler);


    // Get Variant Sub-variants
    for (PidcSubVariant pidcSubVariant : variantHandler.getSubVariantsMap().values()) {
      // Check if sub-variant is not deleted
      if (!pidcSubVariant.isDeleted()) {
        // Get Sub-variant all attributes

        PidcSubVariantBO pidcSubVariantBO =
            new PidcSubVariantBO(this.pidcVersion, pidcSubVariant, this.pidcDataHandler);
        final Collection<PidcSubVariantAttribute> subVarAttrs = pidcSubVariantBO.getAttributes(false, false).values();
        canRelease = checkManProjAttributes(subVarAttrs);
        if (!canRelease) {
          return canRelease;
        }
      }
    }
    return canRelease;
  }

  /**
   * @return the entire structure information of pidcversion
   */
  public String getPidcVersionPath(final PidcTreeNode pidcTreeNode) {
    int level;
    StringBuilder name = new StringBuilder(ApicConstants.PIDC_VERSION_STRING_SIZE);
    String levelName = "";
    PidcTreeNodeHandler handler = new PidcTreeNodeHandler();

    // get max level
    ApicDataBO apicBO = new ApicDataBO();
    int maxLevel = apicBO.getPidcStructMaxLvl();

    for (level = 1; level <= maxLevel; level++) {
      Iterator<PidcVersionAttribute> attrIterate = pidcTreeNode.getPidcVerInfo().getLevelAttrMap().values().iterator();
      while (attrIterate.hasNext()) {
        PidcVersionAttribute attr = attrIterate.next();
        levelName = attr.getName();
      }
      name.append(getNodeName(levelName, pidcTreeNode, handler));
      name.append("->");
      levelName = "";
    }
    return name.toString();
  }

  /**
   * Returns pidc node name for specified level
   *
   * @param attrName level attr name.
   * @param nodeObj pidc tree node
   * @param nodeHandler pidc tree node handler
   * @return node name of pidc corresponding to level
   */
  public String getNodeName(final String attrName, final PidcTreeNode nodeObj, final PidcTreeNodeHandler nodeHandler) {
    PidcTreeNode node = nodeObj;
    while (node != null) {
      if (node.getName().equalsIgnoreCase(attrName)) {
        return node.getName();
      }
      node = nodeHandler.getNodeIdNodeMap().get(nodeObj.getParentNodeId());
    }
    return "";
  }


  /**
   * @param pidcVariantsMap
   * @param pidcVariant
   */
  private void checkIfDeletedNeeded(final Map<Long, PidcVariant> pidcVariantsMap, final PidcVariant pidcVariant) {
    // Check whether the variant is not deleted, else deleted variants will get added irrespective of the 'show deleetd'
    // flag
    if (!pidcVariant.isDeleted()) {
      pidcVariantsMap.put(pidcVariant.getId(), pidcVariant);
    }
  }

  /**
   * This method checks for whether PIDC mandatory attribute is defined or not
   *
   * @param projAttr defines Project Attribute
   * @return boolean defines mandatory attribute is defined or not
   */
  public <P extends IProjectAttribute> boolean isMandatoryAttrDefined(final P projAttr) {

    AbstractProjectAttributeBO projAttrBo = getProjAttrBo(projAttr);

    // Check for attribute is not deleted, mandatory, not a variant
    return !(projAttrBo.isMandatory() &&
        !this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
        projAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !projAttrBo.isValueDefined(projAttr));

  }


  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   */
  public SortedSet<PidcVersionAttribute> getAllAttributes(final int sortColumn) {
    SortedSet<PidcVersionAttribute> resultSet =
        new TreeSet<>((p1, p2) -> PidcVersionBO.this.compare(p1, p2, sortColumn));

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }

  /**
   * Returns the defined attributes of this PIDC. This includes the variant attributes and the structure attributes Key
   * is the attribute ID and value is an instance of PIDCAttribute
   *
   * @return the defined attributes
   */
  protected synchronized Map<Long, PidcVersionAttribute> getDefinedAttributes() {
    ConcurrentMap<Long, PidcVersionAttribute> definedAttrMap = new ConcurrentHashMap<>();
    for (PidcVersionAttribute pidcAttr : getAttributes().values()) {
      if (pidcAttr.getId() != null) {
        definedAttrMap.put(pidcAttr.getAttrId(), pidcAttr);
      }
    }
    return definedAttrMap;
  }


  /**
   * Get a sorted set of the PIDCs Variants
   *
   * @return variants
   */
  public synchronized SortedSet<PidcVariant> getVariantsSet() {
    return new TreeSet<PidcVariant>(getVariantsMap().values());
  }


  /**
   * Method also checks for deleted variants. If all variants are deleted retrun false
   *
   * @param strictMode flag to make the check in strict manner
   * @return true if the strict mode is false and pidc version has varaints. If the strictMode is true then return true
   *         if atleast one of the variants is not deleted.
   */
  public boolean hasVariants(final boolean strictMode) {
    if (strictMode) {
      for (PidcVariant variant : getVariantsMap().values()) {
        if (!variant.isDeleted()) {
          return true;
        }
      }
      return false;
    }
    return !getVariantsMap().isEmpty();
  }

  /**
   * ICDM-789 Get a sorted set of the PIDCs Variants
   *
   * @param deletedNeeded true or false if deleted variants are required
   * @return variants SortedSet of variants
   */
  public SortedSet<PidcVariant> getVariantsSet(final boolean deletedNeeded) {
    final SortedSet<PidcVariant> resultSet = new TreeSet<>();
    resultSet.addAll(getVariantsMap(deletedNeeded).values());
    return resultSet;
  }


  /**
   * Get a sorted set of the PIDCs Variants
   *
   * @param deletedNeeded true or false if deleted variants are required
   * @param unclearedNeeded true or false if variants with uncleared / missing mandatory values required
   * @return variants SortedSet of variants
   */
  public SortedSet<PidcVariant> getVariantsSet(final boolean deletedNeeded, final boolean unclearedNeeded) {
    final SortedSet<PidcVariant> resultSet = new TreeSet<>();
    resultSet.addAll(getVariantsMap(deletedNeeded, unclearedNeeded).values());
    return resultSet;
  }

  /**
   * Method also checks for deleted variants. If all variants are deleted retrun false
   *
   * @return whether the pidc has variants
   */
  public boolean hasVariants() {
    return hasVariants(true);
  }


  /**
   * This method returns all variant attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  public Map<Long, PidcVersionAttribute> getAllVariantAttributes() {
    this.variantAttrMap.clear();
    // Get all variant PIDC attributes
    for (PidcVersionAttribute attribute : this.allAttrMap.values()) {
      if (attribute.isAtChildLevel()) {
        this.variantAttrMap.put(attribute.getAttrId(), attribute);
      }

    }
    return this.variantAttrMap;
  }

  /**
   * This method returns used PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PidcVersionAttribute> getAttributesUsed() {
    this.usedPIDCAttrMap.clear();
    if (!CommonUtils.isNotEmpty(this.allAttrMap)) {
      getAttributes();
    }
    // Get all used PIDC attributes
    for (PidcVersionAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()))) {
        this.usedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }

    return this.usedPIDCAttrMap;
  }

  /**
   * This method returns not used PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PidcVersionAttribute> getAttributesNotUsed() {
    this.notUsedPIDCAttrMap.clear();
    // Get all not used PIDC attributes
    for (PidcVersionAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()))) {
        this.notUsedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.notUsedPIDCAttrMap;
  }


  /**
   * This method returns used not defined PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PidcVersionAttribute> getAttributesNotDefined() {
    this.usedNotDefindPIDCAttrMap.clear();
    // Get all used not defined PIDC attributes
    for (PidcVersionAttribute attribute : this.allAttrMap.values()) {
      if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType(), attribute.getUsedFlag()) ||
          isValueInvalid(this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()))) {
        this.usedNotDefindPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.usedNotDefindPIDCAttrMap;
  }


  /**
   * @return summary of data
   */
  public Map<String, String> getObjectDetails() {
    ConcurrentMap<String, String> summaryMap = new ConcurrentHashMap<String, String>();

    // Name, description, deleted flag etc. is not added to the map as this
    // is available in the attribute value
    summaryMap.put(FLD_PIDC_REVISION, String.valueOf(this.pidcVersion.getProRevId()));
    if (null != this.pidcVersion.getPidStatus()) {
      summaryMap.put(FLD_PIDC_VRSN_STATE, this.pidcVersion.getPidStatus());
    }
    summaryMap.put(FLD_PIDC_VRSN_NAME, this.pidcVersion.getName());
    if (null != this.pidcVersion.getVersDescEng()) {
      summaryMap.put(FLD_PIDC_VRSN_DESC_ENG, this.pidcVersion.getVersDescEng());
    }
    if (null != this.pidcVersion.getVersDescGer()) {
      summaryMap.put(FLD_PIDC_VRSN_DESC_GER, this.pidcVersion.getVersDescGer());
    }
    return summaryMap;
  }


  /**
   * @return tooltip with name & description
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(STR_BUILDER_SIZE);
    Pidc pidc = this.pidcDataHandler.getPidcVersionInfo().getPidc();
    tooltip.append("PIDC : ").append(pidc.getName()).append("\nPIDC Description : ").append(pidc.getDescription())
        .append("\nVersion Name : ").append(this.pidcVersion.getVersionName());
    if (CommonUtils.isNotNull(this.pidcVersion.getDescription())) {
      tooltip.append("\nVersion Description : ").append(this.pidcVersion.getVersDescEng());
    }
    return tooltip.toString();
  }

  /**
   * @param attrValue
   * @return
   */
  public boolean isValueInvalid(final AttributeValue attrValue) {
    return CommonUtils.isNotNull(attrValue) && attrValue.isDeleted();

  }

  /**
   * @param projAttr - project attribute
   * @return String value based on whether it is used,defined
   */
  public <P extends IProjectAttribute> String getIsUsed(final P projAttr) {

    try {
      if (projAttr.isAttrHidden() && !this.currentUser.hasNodeReadAccess(this.pidcVersion.getPidcId())) {
        return "";
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return null;
    }
    String returnValue;

    if (projAttr.isAtChildLevel()) {
      // variant and sub-variant attributes are always "used"
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType();
    }
    else if (getIsUsedEnum(projAttr) != null) {
      returnValue = getIsUsedEnum(projAttr).getUiType();
    }
    else {
      returnValue = ApicConstants.USED_INVALID_DISPLAY;
    }

    return returnValue;
  }


  /**
   * Method to fill all predefined attributes
   */
  public void fillPredefinedAttributes() {
    this.predefAttrGrpAttrMap.clear();
    if (CommonUtils.isNullOrEmpty(this.groupedAttrs)) {
      fillApplicableGroupedAttributes(false);
    }
    Set<IProjectAttribute> grpdAttrs = this.groupedAttrs;
    if (CommonUtils.isNotEmpty(grpdAttrs)) {
      for (IProjectAttribute grpAttr : grpdAttrs) {
        checkGrpAttrLvl(grpAttr);
      }
    }
  }


  /**
   * @return the pidcVersion
   */
  @Override
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @return the pidcpidcDataHandler
   */
  public PidcDataHandler getPidcpidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVersionAttribute> getAttributes() {
    return getAttributes(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <V extends IProjectAttribute> Map<Long, V> getAttributes(final boolean refresh, final boolean includeDeleted) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <E extends IProjectAttribute> SortedSet<E> getAttributes(final int sortColumn) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVersionAttribute> getAttributesAll() {
    getAttributes();
    return this.attrMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasInvalidAttrValues() {
    boolean ret = false;
    final Map<Long, PidcVersionAttribute> allAttributes = getAttributes(false);
    // Check attributes at PIDC Version level first
    for (PidcVersionAttribute pidcAttr : allAttributes.values()) {
      PidcVersionAttributeBO handler = new PidcVersionAttributeBO(pidcAttr, this);
      if (!pidcAttr.isAtChildLevel() && handler.isValueInvalid()) {
        ret = true;
        break;
      }
    }
    if (!ret) {
      // If not invalid values found, also check attributes at variant level
      for (PidcVariant variant : getVariantsSet()) {
        if (!variant.isDeleted()) {
          PidcVariantBO varHandler = new PidcVariantBO(getPidcVersion(), variant, getPidcDataHandler());
          ret = varHandler.hasInvalidAttrValues();
          if (ret) {
            break;
          }
        }
      }
    }
    return ret;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeFromMap() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeInvisibleAttributes() {
    // TODO Auto-generated method stub
  }


  /**
   * Get all DEPENDENCIES defined for this attribute, pass parameter to include deleted items
   *
   * @param includeDeleted - if TRUE includes items which are even marked as deleted, FALSE otherwise
   * @param attrId
   * @return List<AttrDependency>
   */
  public List<AttrNValueDependency> getAttrDependencies(final boolean includeDeleted, final Long attrId) {
    // create the result object
    List<AttrNValueDependency> attrDependencies = new ArrayList<AttrNValueDependency>();

    // iterate over all dependencies defined for this attribute
    for (AttrNValueDependency attrDependency : this.pidcDataHandler.getAttrDependenciesMap().get(attrId)) {

      if (includeDeleted) {
        // add all dependencies which are also marked as deleted
        attrDependencies.add(attrDependency);
      }
      else {
        // check if the dependency is deleted
        if (!attrDependency.isDeleted()) {
          // add only not deleted dependencies to the results list
          attrDependencies.add(attrDependency);
        }
      }

    }
    return attrDependencies;
  }


  /**
   * Method is used only for statistics, not in predefined filters {@inheritDoc}
   */
  @Override
  public <O extends IProjectAttribute> boolean isValueDefined(final O projAttr) {
    boolean isVisible = new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, this).isVisible();
    boolean isValueDefined=false;

    if (projAttr.isAtChildLevel()) {
      for (PidcVariant variant : getVariantsSet(false)) {
        PidcVariantBO variantHandler = new PidcVariantBO(this.pidcVersion, variant, getPidcDataHandler());
        PidcVariantAttribute pidcVarAttr = variantHandler.getAttributes(false).get(projAttr.getAttrId());

        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered as 'Not defined'
        boolean isVarAttrVisible =
            (null != pidcVarAttr) && new PidcVariantAttributeBO(pidcVarAttr, variantHandler).isVisible();
        if (!isVarAttrVisible) {
          continue;
        }
        isValueDefined= variantHandler.isValueDefined(pidcVarAttr);
        if(!isValueDefined) {
          return false;
        }
      }
      return true;
    }
      return isVisibleAndValueDefined(projAttr, isVisible);
  }

  /**
   * An attribute is considered as defined if used flag = Y and has value, or if used flag= N Since invisible attributes
   * are not relevant for the project, the method returns true if an attribute is invisible
   * 
   * @param projAttr
   * @param isVisible
   * @return
   */
  private <O extends IProjectAttribute> boolean isVisibleAndValueDefined(final O projAttr, final boolean isVisible) {
    return (!isVisible) || ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(projAttr.getUsedFlag()) &&
        (projAttr.getValueId() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(projAttr.getUsedFlag())));
  }


  /**
   * @param handler Focus Matrix Data Handler
   * @param pidcVersionBO Pidc Version BO
   * @return Focus Matrix Version
   */
  public FocusMatrixVersion getFocusMatrixWorkingSetVersion(final FocusMatrixDataHandler handler,
      final PidcVersionBO pidcVersionBO) {
    return handler.getSelFmVersion().getFmVersion();
  }

  /**
   * @return the deletedAttrsMap
   */
  public Map<Long, PidcVersionAttribute> getDeletedAttrsMap() {
    return this.deletedAttrsMap;
  }


  /**
   * @param deletedAttrsMap the deletedAttrsMap to set
   */
  public void setDeletedAttrsMap(final Map<Long, PidcVersionAttribute> deletedAttrsMap) {
    this.deletedAttrsMap = deletedAttrsMap;
  }

  /**
   * @return the usedPIDCAttrMap
   */
  public Map<Long, PidcVersionAttribute> getUsedPIDCAttrMap() {
    return this.usedPIDCAttrMap;
  }


  /**
   * @param usedPIDCAttrMap the usedPIDCAttrMap to set
   */
  public void setUsedPIDCAttrMap(final Map<Long, PidcVersionAttribute> usedPIDCAttrMap) {
    this.usedPIDCAttrMap = usedPIDCAttrMap;
  }


  /**
   * @return the notUsedPIDCAttrMap
   */
  public Map<Long, PidcVersionAttribute> getNotUsedPIDCAttrMap() {
    return this.notUsedPIDCAttrMap;
  }


  /**
   * @param notUsedPIDCAttrMap the notUsedPIDCAttrMap to set
   */
  public void setNotUsedPIDCAttrMap(final Map<Long, PidcVersionAttribute> notUsedPIDCAttrMap) {
    this.notUsedPIDCAttrMap = notUsedPIDCAttrMap;
  }


  /**
   * @return the usedNotDefindPIDCAttrMap
   */
  public Map<Long, PidcVersionAttribute> getUsedNotDefindPIDCAttrMap() {
    return this.usedNotDefindPIDCAttrMap;
  }


  /**
   * @param usedNotDefindPIDCAttrMap the usedNotDefindPIDCAttrMap to set
   */
  public void setUsedNotDefindPIDCAttrMap(final Map<Long, PidcVersionAttribute> usedNotDefindPIDCAttrMap) {
    this.usedNotDefindPIDCAttrMap = usedNotDefindPIDCAttrMap;
  }


  /**
   * @return the allAttrMap
   */
  public Map<Long, PidcVersionAttribute> getAllAttrMap() {
    return this.allAttrMap;
  }


  /**
   * @return the attrMap
   */
  public Map<Long, PidcVersionAttribute> getAttrMap() {
    return this.attrMap;
  }

  /**
   * @return the groupedAttrs
   */
  public Set<IProjectAttribute> getGroupedAttrs() {
    return this.groupedAttrs;
  }


  /**
   * @return the predefAttrGrpAttrMap
   */
  public ConcurrentMap<IProjectAttribute, IProjectAttribute> getPredefAttrGrpAttrMap() {
    return this.predefAttrGrpAttrMap;
  }


  /**
   * @return the variantAttrMap
   */
  public Map<Long, PidcVersionAttribute> getVariantAttrMap() {
    return this.variantAttrMap;
  }


  /**
   * @return the variantMap
   */
  public Map<Long, PidcVariant> getVariantMap() {
    return this.variantMap;
  }


  /**
   * @param variantAttrMap the variantAttrMap to set
   */
  public void setVariantAttrMap(final Map<Long, PidcVersionAttribute> variantAttrMap) {
    this.variantAttrMap = variantAttrMap;
  }


  /**
   * @param variantMap the variantMap to set
   */
  public void setVariantMap(final Map<Long, PidcVariant> variantMap) {
    this.variantMap = variantMap;
  }


  /**
   * @param allAttrMap the allAttrMap to set
   */
  public void setAllAttrMap(final Map<Long, PidcVersionAttribute> allAttrMap) {
    this.allAttrMap = allAttrMap;
  }


  /**
   * @param attrMap the attrMap to set
   */
  public void setAttrMap(final Map<Long, PidcVersionAttribute> attrMap) {
    this.attrMap = attrMap;
  }

  /**
   * @param chData
   * @return
   */
  boolean isPidcA2lChanged(final ChangeData<?> chData) {
    PidcA2l pidcA2l = (PidcA2l) CnsUtils.getModel(chData);
    return CommonUtils.isEqual(getPidc().getId(), pidcA2l.getProjectId());
  }

  /**
   * @return PIDC object in this data object
   */
  public Pidc getPidc() {
    return getPidcDataHandler().getPidcVersionInfo().getPidc();
  }


  /**
   * @param PidcVersion pidcVrsn
   */
  public void upToDate(final PidcVersion pidcVrsn) {
    PidcVersionServiceClient client = new PidcVersionServiceClient();
    PidcVersion pidVersObj;
    try {
      pidVersObj = client.editPidcVersion(pidcVrsn.clone());
      setPidcVersion(pidVersObj);
    }
    catch (ApicWebServiceException | CloneNotSupportedException exc) {
      CDMLogger.getInstance().error(exc.getMessage(), exc, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param chData ChangeData<?>
   */
  public void refreshPidcVersion(final ChangeData<?> chData) {


    // set the new pidc version in this class and PIDCVersionInfo class
    setPidcVersion((PidcVersion) chData.getNewData());
    getPidcDataHandler().getPidcVersionInfo().setPidcVersion(this.pidcVersion);


  }


  /**
   * @param chDataInfoMap
   */
  public void refreshPidc(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    PidcVersionServiceClient client = new PidcVersionServiceClient();
    try {
      Long pidcVersSet[] = { this.pidcVersion.getId() };
      Map<Long, PidcVersionInfo> updatedPidcVersionInfo =
          client.getPidcVersionsWithStructure(new HashSet<Long>(Arrays.asList(pidcVersSet)));
      if (updatedPidcVersionInfo != null) {
        setPidcVersion(updatedPidcVersionInfo.get(this.pidcVersion.getId()).getPidcVersion());
        getPidcDataHandler().setPidcVersionInfo(updatedPidcVersionInfo.get(this.pidcVersion.getId()));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param chData
   */
  public void refreshPidcLocal(final ChangeData<?> chData) {
    if (chData.getChangeType() == CHANGE_OPERATION.UPDATE) {
      Pidc updatedPidc = (Pidc) chData.getNewData();
      getPidcDataHandler().getPidcVersionInfo().setPidc(updatedPidc);
    }
  }

  /**
   * @param chData
   */
  public void refreshVariantMap(final ChangeData<?> chData) {

    if (chData.getChangeType() == CHANGE_OPERATION.UPDATE) {
      PidcVariant updatedPidcVariant = (PidcVariant) chData.getNewData();
      getPidcDataHandler().getVariantMap().put(chData.getObjId(), updatedPidcVariant);
      getPidcDataHandler().getVariantMap().put(chData.getObjId(), updatedPidcVariant);
      Collection<PIDCDetailsNode> pidcDetNodes = getPidcDataHandler().getPidcDetNodes().values();
      for (PIDCDetailsNode detailsNode : pidcDetNodes) {
        if (detailsNode.isVariantNode() && detailsNode.getPidcVariant().getId().equals(updatedPidcVariant.getId())) {
          detailsNode.setVariant(updatedPidcVariant);
        }
      }
    }
    else {
      refreshWholeModel(null);
    }

  }

  /**
   * @param chDataInfoMap
   */
  public void refreshWholeModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    PidcDetailsLoader loader = new PidcDetailsLoader(getPidcDataHandler());
    loader.loadDataModel(getPidcVersion().getId());
    setPidcVersion(getPidcDataHandler().getPidcVersionInfo().getPidcVersion());
  }

  /**
   * @param chDataInfoMap
   */
  void refreshDetStructMap(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    PidcDetStructureServiceClient pidcDetStructClient = new PidcDetStructureServiceClient();
    try {
      Map<Long, PidcDetStructure> pidcDetStructureMap =
          pidcDetStructClient.getPidcDetStructForVersion(this.pidcVersion.getId());
      this.pidcDataHandler.setPidcDetStructureMap(pidcDetStructureMap);

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * refresh sub variant map
   */
  void refreshSubVariantMap(final ChangeData<?> chData) {
    if (chData.getChangeType() == CHANGE_OPERATION.UPDATE) {
      getPidcDataHandler().getSubVariantMap().put(chData.getObjId(), (PidcSubVariant) chData.getNewData());
    }
    else {
      refreshWholeModel(null);
    }
  }

  /**
   * @param chData
   */
  public void refreshProjAttr(final ChangeData<?> chData) {

    if (chData.getChangeType() == CHANGE_OPERATION.INVISIBLE) {
      PidcVersionAttribute invisbleAttr = (PidcVersionAttribute) chData.getOldData();
      if (invisbleAttr.getPidcVersId().equals(this.pidcVersion.getId())) {
        getAttributesAll().remove(invisbleAttr.getAttrId());
        getPidcDataHandler().getPidcVersInvisibleAttrSet().add(invisbleAttr.getAttrId());
        if (getPidcDataHandler().getPidcVersAttrMap().containsKey(invisbleAttr.getAttrId())) {
          getPidcDataHandler().getPidcVersAttrMap().put(invisbleAttr.getAttrId(), invisbleAttr);
        }
      }
    }
    else if (chData.getChangeType() == CHANGE_OPERATION.VISIBLE) {
      PidcVersionAttribute visbleAttr = (PidcVersionAttribute) chData.getNewData();
      if (visbleAttr.getPidcVersId().equals(this.pidcVersion.getId())) {
        getAttributesAll().put(visbleAttr.getAttrId(), visbleAttr);
        getPidcDataHandler().getPidcVersInvisibleAttrSet().remove(visbleAttr.getAttrId());
        getPidcDataHandler().getPidcVersAttrMap().put(visbleAttr.getAttrId(), visbleAttr);
      }
    }
    else {
      Long attrId = null;
      PidcVersionAttribute newValue = (PidcVersionAttribute) chData.getNewData();
      PidcVersionAttribute existingValue = (PidcVersionAttribute) chData.getOldData();
      if (newValue.getPidcVersId().equals(this.pidcVersion.getId())) {
        if (existingValue == null) {
          attrId = newValue.getAttrId();
          getAttributesAll().put(newValue.getAttrId(), newValue);
          getPidcDataHandler().getPidcVersAttrMap().put(newValue.getAttrId(), newValue);
          // changes to update defined attr map, this is used by project statistics section in pidc attr page
          getPidcDataHandler().getPidcVersAttrMapDefined().put(newValue.getAttrId(), newValue);
        }
        else {
          existingValue = new PidcVersionAttribute();
          CommonUtils.shallowCopy(existingValue, (PidcVersionAttribute) chData.getOldData());
          attrId = existingValue.getAttrId();
          CommonUtils.shallowCopy(existingValue, newValue);
          getAttributesAll().put(existingValue.getAttrId(), existingValue);
          getPidcDataHandler().getPidcVersAttrMap().put(existingValue.getAttrId(), existingValue);
          // changes to update defined attr map, this is used by project statistics section in pidc attr page
          getPidcDataHandler().getPidcVersAttrMapDefined().put(existingValue.getAttrId(), existingValue);
        }
      }
      // update attribute value map
      AttributeValue attrValue = updateAttributeValue(newValue, existingValue);

      // update links for attribute value if applicable
      if (attrValue != null) {
        Set<Link> links = new AttributeValueClientBO(attrValue).getLinks();
        if (!links.isEmpty()) {
          this.pidcDataHandler.getAttValLinks().add(attrValue.getId());
        }
      }

      if ((attrValue != null) && (null != attrId) &&
          new AttributeClientBO(getPidcDataHandler().getAttribute(attrId)).isGrouped()) {
        // fill all the predefined values corresponding to values
        fillPredefinedvalues(attrValue);
      }
      if (null != attrId) {
        if (this.pidcDataHandler.getAttributeMap().get(attrId).getLevel()
            .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR))) {
          refreshPidcA2lMap();
        }
      }
    }
    // update grouped attributes
    fillApplicableGroupedAttributes(true);
    fillPredefinedAttributes();


  }

  /**
   *
   */
  private void refreshPidcA2lMap() {
    this.allPidcA2lMap.clear();
    getAllA2lByPidc(this.pidcVersion.getPidcId());
  }

  /**
   * @param chData
   */
  public void refreshVarAttr(final ChangeData<?> chData) {

    if (chData.getChangeType() == CHANGE_OPERATION.INVISIBLE) {
      refreshInvisibleVarAttr(chData);
    }
    else if (chData.getChangeType() == CHANGE_OPERATION.VISIBLE) {
      refreshVisibleVarAttr(chData);
    }
    else {
      refreshOtherVarAttr(chData);
    }
    // update grouped attributes
    fillApplicableGroupedAttributes(true);
    fillPredefinedAttributes();


  }

  /**
   * @param chData
   */
  private void refreshOtherVarAttr(final ChangeData<?> chData) {
    PidcVariantAttribute newValue = (PidcVariantAttribute) chData.getNewData();
    PidcVariantAttribute existingValue = (PidcVariantAttribute) chData.getOldData();
    Long attrId = null;
    Long pidcVersionId = null == newValue ? existingValue.getPidcVersionId() : newValue.getPidcVersionId();
    if (pidcVersionId.equals(this.pidcVersion.getId())) {
      Long variantId = null == newValue ? existingValue.getVariantId() : newValue.getVariantId();


      // get variant attribute map
      Map<Long, PidcVariantAttribute> map = getPidcDataHandler().getVariantAttributeMap().get(variantId);

      // if new value is null, remove from map
      if (null == newValue) {
        attrId = existingValue.getAttrId();
        map.remove(existingValue.getAttrId());
        this.variantAttrMap.remove(existingValue.getAttrId());
      }
      // new value is not null
      else {
        // if old value is null, put new value in map
        if (existingValue == null) {
          if (null != map) {
            map.put(newValue.getAttrId(), newValue);
            attrId = newValue.getAttrId();
          }

        }
        // if old value is not , put new value in place of old value
        else {
          attrId = existingValue.getAttrId();
          CommonUtils.shallowCopy(existingValue, newValue);
          map.put(existingValue.getAttrId(), existingValue);
        }
      }
      getPidcDataHandler().getVariantAttributeMap().put(variantId, map);
    }

    // update attribute value map
    AttributeValue attrValue = updateAttributeValue(newValue, existingValue);

    // update links for attribute value if applicable
    if (attrValue != null) {
      Set<Link> links = new AttributeValueClientBO(attrValue).getLinks();
      if (!links.isEmpty()) {
        this.pidcDataHandler.getAttValLinks().add(attrValue.getId());
      }
    }

    if ((attrValue != null) && (null != attrId) &&
        new AttributeClientBO(getPidcDataHandler().getAttribute(attrId)).isGrouped()) {
      // fill all the predefined values corresponding to values
      fillPredefinedvalues(attrValue);
    }
    if (null != attrId) {
      if (this.pidcDataHandler.getAttributeMap().get(attrId).getLevel()
          .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR))) {
        refreshPidcA2lMap();
      }
    }
  }

  /**
   * @param chData
   */
  private void refreshVisibleVarAttr(final ChangeData<?> chData) {
    PidcVariantAttribute visbleAttr = (PidcVariantAttribute) chData.getNewData();
    if (visbleAttr.getPidcVersionId().equals(this.pidcVersion.getId())) {

      Set<Long> invisibleIds = getPidcDataHandler().getVariantInvisbleAttributeMap().get(visbleAttr.getVariantId());
      if (CommonUtils.isNotEmpty(invisibleIds)) {
        invisibleIds.remove(visbleAttr.getAttrId());
        getPidcDataHandler().getVariantInvisbleAttributeMap().put(visbleAttr.getVariantId(), invisibleIds);
      }

      if ((getPidcDataHandler().getVariantAttributeMap().get(visbleAttr.getVariantId())
          .get(visbleAttr.getAttrId()) == null) &&
          !getPidcDataHandler().getVariantInvisbleAttributeMap().get(visbleAttr.getVariantId())
              .contains(visbleAttr.getAttrId())) {
        getPidcDataHandler().getVariantAttributeMap().get(visbleAttr.getVariantId()).put(visbleAttr.getAttrId(),
            visbleAttr);
      }
    }
  }

  /**
   * @param chData
   */
  private void refreshInvisibleVarAttr(final ChangeData<?> chData) {
    PidcVariantAttribute invisbleAttr = (PidcVariantAttribute) chData.getOldData();
    if (invisbleAttr.getPidcVersionId().equals(this.pidcVersion.getId())) {
      Set<Long> set = getPidcDataHandler().getVariantInvisbleAttributeMap().get(invisbleAttr.getVariantId());
      if (getPidcDataHandler().getVariantAttributeMap().get(invisbleAttr.getVariantId())
          .containsKey(invisbleAttr.getAttrId())) {
        getPidcDataHandler().getVariantAttributeMap().get(invisbleAttr.getVariantId()).put(invisbleAttr.getAttrId(),
            invisbleAttr);
      }
      if ((getPidcDataHandler().getVariantAttributeMap().get(invisbleAttr.getVariantId()) != null) &&
          (getPidcDataHandler().getVariantAttributeMap().get(invisbleAttr.getVariantId())
              .get(invisbleAttr.getAttrId()) == null)) {
        getPidcDataHandler().getVariantAttributeMap().get(invisbleAttr.getVariantId()).put(invisbleAttr.getAttrId(),
            invisbleAttr);
      }

      if (CommonUtils.isNotEmpty(set)) {
        set.add(invisbleAttr.getAttrId());
        getPidcDataHandler().getVariantInvisbleAttributeMap().put(invisbleAttr.getVariantId(), set);
      }
      else {
        Set<Long> newinvisibleIds = new HashSet<>();
        newinvisibleIds.add(invisbleAttr.getAttrId());
        getPidcDataHandler().getVariantInvisbleAttributeMap().put(invisbleAttr.getVariantId(), newinvisibleIds);
      }
    }
  }

  private AttributeValue updateAttributeValue(final IProjectAttribute newValue, final IProjectAttribute existingValue) {

    Map<Long, AttributeValue> attrValueMap = this.pidcDataHandler.getAttributeValueMap();

    // if new value is null, remove from map
    if (null == newValue) {
      attrValueMap.remove(existingValue.getValueId());
    }
    // new value is not null
    else {

      AttributeValue newAttrValue = null;
      AttributeValue existingAttrValue = null;
      try {
        if ((newValue.getValueId() != null) && (attrValueMap.get(newValue.getValueId()) == null)) {
          newAttrValue = new AttributeValueServiceClient().getById(newValue.getValueId());
        }
        else {
          newAttrValue = attrValueMap.get(newValue.getValueId());
        }
        if (null != existingValue) {
          existingAttrValue = attrValueMap.get(existingValue.getValueId());
        }


        // if old value is null, put new value in map
        if (existingAttrValue == null) {
          if (null != newAttrValue) {
            attrValueMap.put(newAttrValue.getId(), newAttrValue);
          }
        }
        // if old value is not , put new value in place of old value
        else {
          CommonUtils.shallowCopy(existingAttrValue, newAttrValue);
          attrValueMap.put(existingAttrValue.getId(), existingAttrValue);
        }

      }

      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      }
      return newAttrValue;
    }
    return null;

  }

  /**
   * @param chDataInfoMap
   */
  public void refreshPidcA2l(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        updatePidcA2lMap(data);
      }
    }
  }

  /**
   * @param data
   */
  private void updatePidcA2lMap(final ChangeDataInfo data) {
    PidcA2lServiceClient pidcA2lSerClient = new PidcA2lServiceClient();
    try {
      PidcA2lFileExt pidcA2lExt = pidcA2lSerClient.getPidcA2LFileDetails(data.getObjId());
      this.allPidcA2lMap.put(pidcA2lExt.getA2lFile().getId(), pidcA2lExt);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param chData
   */
  public void refreshSubVarAttr(final ChangeData<?> chData) {
    if (chData.getChangeType() == CHANGE_OPERATION.INVISIBLE) {
      PidcSubVariantAttribute visbleAttr = (PidcSubVariantAttribute) chData.getOldData();
      if (visbleAttr.getPidcVersionId().equals(this.pidcVersion.getId())) {
        getPidcDataHandler().getSubVariantAttributeMap().get(visbleAttr.getSubVariantId()).put(visbleAttr.getAttrId(),
            visbleAttr);
        Set<Long> invisibleIds =
            getPidcDataHandler().getSubVariantInvisbleAttributeMap().get(visbleAttr.getSubVariantId());
        if (CommonUtils.isNotEmpty(invisibleIds)) {
          invisibleIds.add(visbleAttr.getAttrId());
          getPidcDataHandler().getSubVariantInvisbleAttributeMap().put(visbleAttr.getSubVariantId(), invisibleIds);
        }
        else {
          Set<Long> newinvisibleIds = new HashSet<>();
          newinvisibleIds.add(visbleAttr.getAttrId());
          getPidcDataHandler().getSubVariantInvisbleAttributeMap().put(visbleAttr.getSubVariantId(), newinvisibleIds);
        }
      }
    }
    else if (chData.getChangeType() == CHANGE_OPERATION.VISIBLE) {
      PidcSubVariantAttribute invisbleAttr = (PidcSubVariantAttribute) chData.getNewData();
      if (invisbleAttr.getPidcVersionId().equals(this.pidcVersion.getId())) {

        if ((getPidcDataHandler().getSubVariantAttributeMap().get(invisbleAttr.getSubVariantId()) != null) &&
            (getPidcDataHandler().getSubVariantAttributeMap().get(invisbleAttr.getSubVariantId())
                .get(invisbleAttr.getAttrId()) == null)) {
          getPidcDataHandler().getSubVariantAttributeMap().get(invisbleAttr.getSubVariantId())
              .put(invisbleAttr.getAttrId(), invisbleAttr);
        }


        Set<Long> set = getPidcDataHandler().getSubVariantInvisbleAttributeMap().get(invisbleAttr.getSubVariantId());
        if (CommonUtils.isNotEmpty(set)) {
          set.remove(invisbleAttr.getAttrId());
        }
        Map<Long, PidcSubVariantAttribute> map =
            getPidcDataHandler().getSubVariantAttributeMap().get(invisbleAttr.getSubVariantId());
        if (null != map) {
          map.put(invisbleAttr.getAttrId(), invisbleAttr);
        }
      }
    }
    else {
      Long attrId = null;
      PidcSubVariantAttribute newValue = (PidcSubVariantAttribute) chData.getNewData();
      PidcSubVariantAttribute existingValue = (PidcSubVariantAttribute) chData.getOldData();
      Long subVariantId = (null == newValue) ? existingValue.getSubVariantId() : newValue.getSubVariantId();
      Map<Long, PidcSubVariantAttribute> map = getPidcDataHandler().getSubVariantAttributeMap().get(subVariantId);

      Long pidcVersionId = (null == newValue) ? existingValue.getPidcVersionId() : newValue.getPidcVersionId();
      if (pidcVersionId.equals(this.pidcVersion.getId())) {
        if (null == newValue) {
          map.remove(existingValue.getAttrId());
          attrId = existingValue.getAttrId();
        }
        else {
          if (existingValue == null) {
            map.put(newValue.getAttrId(), newValue);
            attrId = newValue.getAttrId();
          }
          else {
            CommonUtils.shallowCopy(existingValue, newValue);
            map.put(existingValue.getAttrId(), existingValue);
            attrId = existingValue.getAttrId();
          }
        }
      }

      // update attribute value map
      AttributeValue attrValue = updateAttributeValue(newValue, existingValue);

      // update links for attribute value if applicable
      if (attrValue != null) {
        Set<Link> links = new AttributeValueClientBO(attrValue).getLinks();
        if (!links.isEmpty()) {
          this.pidcDataHandler.getAttValLinks().add(attrValue.getId());
        }
      }

      if ((attrValue != null) && (null != attrId) &&
          new AttributeClientBO(getPidcDataHandler().getAttribute(attrId)).isGrouped()) {
        // fill all the predefined values corresponding to values
        fillPredefinedvalues(attrValue);
      }
      if (null != attrId) {
        if (this.pidcDataHandler.getAttributeMap().get(attrId).getLevel()
            .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR))) {
          refreshPidcA2lMap();
        }
      }
    }

    // update grouped attributes
    fillApplicableGroupedAttributes(true);
    fillPredefinedAttributes();


  }

  /**
   * @param attrValue
   */
  private void fillPredefinedvalues(final AttributeValue attrValue) {
    try {
      this.pidcDataHandler.getPreDefAttrValMap().put(attrValue.getId(),
          new PredefinedAttrValueServiceClient().getByValueId(attrValue.getId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  @Override
  public ProjectObjectStatistics<?> getProjectStatistics(final UseCaseDataHandler ucDataHandler,
      final FocusMatrixDataHandler focusMatrixDataHandler) {
    this.pidcVersionStatistics =
        new PIDCVersionStatistics(getPidcVersion(), this, ucDataHandler, focusMatrixDataHandler);
    return this.pidcVersionStatistics;
  }

  /**
   * Compares two project attributes
   *
   * @param pAttr1 project attribute 1
   * @param pAttr2 project attribute 2
   * @param sortColumn sort column
   * @return compare result
   */
  public int compare(final PidcVersionAttribute pAttr1, final PidcVersionAttribute pAttr2, final int sortColumn) {

    int ret = 0;

    if (sortColumn == ApicConstants.SORT_TRANSFERVCDM) {
      ret = ApicUtil.compare(pAttr1.isTransferToVcdm(), pAttr2.isTransferToVcdm());
    }
    else if (sortColumn == ApicConstants.SORT_FM_RELEVANT) {
      ret = ApicUtil.compare(pAttr1.isFocusMatrixApplicable(), pAttr2.isFocusMatrixApplicable());
    }

    // All other sort columns, and '0' state for above checks
    if (ret == 0) {
      ret = super.compare(pAttr1, pAttr2, sortColumn);
    }

    return ret;
  }

  /**
   * @param pidcA2lId
   * @return
   */
  public boolean isCDRResultsPresent(final Long pidcA2lId) {
    try {
      return CommonUtils.isNullOrEmpty(new CDRReviewResultServiceClient().getCDRResultsByPidcA2l(pidcA2lId));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }


  /**
   * @return the allPidcA2lMap
   */
  public Map<Long, PidcA2lFileExt> getAllPidcA2lMap() {
    return this.allPidcA2lMap;
  }


  /**
   * @param allPidcA2lMap the allPidcA2lMap to set
   */
  public void setAllPidcA2lMap(final Map<Long, PidcA2lFileExt> allPidcA2lMap) {
    this.allPidcA2lMap = allPidcA2lMap;
  }

  /**
   * @param pidcId
   * @return
   */
  public Map<Long, PidcA2lFileExt> getAllA2lByPidc(final Long pidcId) {
    PidcA2lServiceClient pidcA2lServiceClient = new PidcA2lServiceClient();
    try {
      this.allPidcA2lMap = pidcA2lServiceClient.getAllA2lByPidc(pidcId);
      return this.allPidcA2lMap;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return Collections.emptyMap();
    }
  }


  /**
   * @param pidcA2ls
   */
  public Set<PidcA2l> updatePidcA2l(final Set<PidcA2l> pidcA2ls) {
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    try {
      return client.update(pidcA2ls);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param pidcA2lsToCreate
   */
  public Set<PidcA2l> createPidcA2l(final Set<PidcA2l> pidcA2lsToCreate) {
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    try {
      return client.create(pidcA2lsToCreate);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @return the pidcDetailsNodeHandler
   */
  public PidcDetailsNodeHandler getPidcDetailsNodeHandler() {
    return this.pidcDetailsNodeHandler;
  }


  /**
   * @return the selectedObjForFiltering
   */
  public Object getSelectedObjForFiltering() {
    return this.selectedObjForFiltering;
  }


  /**
   * @param selectedObjForFiltering the selectedObjForFiltering to set
   */
  public void setSelectedObjForFiltering(final Object selectedObjForFiltering) {
    this.selectedObjForFiltering = selectedObjForFiltering;
  }

  /**
   * Get a MAP of the PIDCs Sub Variants including deleted variants
   *
   * @return map of sub variants, with key as sub variant id and value as sub variant object
   */
  public Map<Long, PidcSubVariant> getSubVariantsMap() {
    return this.pidcDataHandler.getSubVariantMap();
  }

  /**
   * Get a MAP of the PIDCs Sub Variants
   *
   * @param variantId pidc variant id
   * @param isDeletedSubVariantNeeded if true, deleted sub variant will also be added
   * @return map of active sub variants, with key as sub variant id and value as sub variant object
   */
  public SortedSet<PidcSubVariant> getSubVariantsforSelVariant(final Long variantId,
      final boolean isDeletedSubVariantNeeded) {

    final Map<Long, PidcSubVariant> pidcSubVariantsMapIterate = getSubVariantsMap();
    final SortedSet<PidcSubVariant> pidcSubVariants = new TreeSet<>();

    for (PidcSubVariant pidcSubVariant : pidcSubVariantsMapIterate.values()) {
      if (!isDeletedSubVariantNeeded && pidcSubVariant.isDeleted()) {
        continue;
      }
      if ((pidcSubVariant.getPidcVariantId() != null) && (pidcSubVariant.getPidcVariantId().equals(variantId))) {
        pidcSubVariants.add(pidcSubVariant);
      }
    }

    return pidcSubVariants;
  }

}
