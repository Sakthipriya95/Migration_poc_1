/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class PidcVariantBO extends AbstractProjectObjectBO {

  private final PidcVariant pidcVariant;

  /**
   * All attributes of PIDC Variant, includes sub variant attrs
   */
  private final ConcurrentMap<Long, PidcVariantAttribute> allAttrMap = new ConcurrentHashMap<>();
  /**
   * Invisible attributes of PIDC Variant
   */
  private final SortedSet<Attribute> invisibleAttributes = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

  private final ConcurrentMap<Long, PidcVariantAttribute> invsblePIDCVarAttrsMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcVariantAttribute> usedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcVariantAttribute> notUsedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcVariantAttribute> usedNotDefinedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcVariantAttribute> varAttrs = new ConcurrentHashMap<>();

  /**
   * @param pidcVersion
   * @param pidcVariant
   * @param functionality
   */
  public PidcVariantBO(final PidcVersion pidcVersion, final PidcVariant pidcVariant,
      final PidcDataHandler pidcDataHandler) {
    super(pidcVersion, pidcVariant, pidcDataHandler);
    this.pidcVariant = pidcVariant;
  }

  /**
   * @return the pidcVersion
   */
  @Override
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  @Override
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * Get a MAP of the PIDCs Sub Variants
   *
   * @param deletedNeeded deletedNeeded true or false if deleted variants are required
   * @return PIDCSubVariant Map
   */
  public synchronized Map<Long, PidcSubVariant> getSubVariantsMap(final boolean deletedNeeded) {

    return getSubVariantsMap(deletedNeeded, false);
  }

  /**
   * Get a MAP of the PIDCs Sub Variants including deleted sub variants
   *
   * @return PIDCSubVariant
   */
  public synchronized Map<Long, PidcSubVariant> getSubVariantsMap() {

    final Map<Long, PidcSubVariant> subVariantsMap = new ConcurrentHashMap<>();

    for (PidcSubVariant subVar : this.pidcDataHandler.getSubVariantMap().values()) {

      if (subVar.getPidcVariantId().equals(this.pidcVariant.getId())) {
        subVariantsMap.put(subVar.getId(), subVar);
      }
    }

    this.childrenLoaded = true;
    return subVariantsMap;
  }

  /**
   * Get a MAP of the PIDCs Sub Variants
   *
   * @param deletedNeeded deletedNeeded true or false if deleted variants are required
   * @param showOnlyUncleared true or false if subvariants with only uncleared / missing mandatory values to be
   *          displayed showOnlyUncleared = true --> Only subvariants with uncleared / missing mandatory values will be
   *          displayed in PIDC structure view
   * @return PIDCSubVariant Map
   */
  public synchronized Map<Long, PidcSubVariant> getSubVariantsMap(final boolean deletedNeeded,
      final boolean showOnlyUncleared) {

    final Map<Long, PidcSubVariant> subVariantsMap = new ConcurrentHashMap<>();

    final Map<Long, PidcSubVariant> subVariantsMapIterate = getSubVariantsMap();

    if (subVariantsMapIterate == null) {
      return subVariantsMap;
    }

    for (PidcSubVariant pidcSubVariant : subVariantsMapIterate.values()) {
      // Check if uncleared subvariants need to be displayed
      if (showOnlyUncleared) {
        // If uncleared sub variants are needed, check if it has invalid attributes and mandatory attributes are not
        // defined
        PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion, pidcSubVariant, this.pidcDataHandler);

        if (subVarHandler.hasInvalidAttrValues() || !subVarHandler.isAllMandatoryAttrDefined()) {
          checkIfDeletedNeeded(subVariantsMap, pidcSubVariant);
        }
      }
      else {
        checkIfDeletedNeeded(subVariantsMap, pidcSubVariant);
      }
      // Check whether deleted subvariants are needed
      if (deletedNeeded && pidcSubVariant.isDeleted()) {
        subVariantsMap.put(pidcSubVariant.getId(), pidcSubVariant);
      }
    }

    return subVariantsMap;
  }

  /**
   * @param subVariantsMap
   * @param pidcSubVariant
   */
  private void checkIfDeletedNeeded(final Map<Long, PidcSubVariant> subVariantsMap,
      final PidcSubVariant pidcSubVariant) {
    // Check whether the variant is not deleted, else deleted variants will get added irrespective of the 'show deleetd'
    // flag
    if (!pidcSubVariant.isDeleted()) {
      subVariantsMap.put(pidcSubVariant.getId(), pidcSubVariant);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributes(final boolean refresh) {
    return getAttributes(refresh, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributes() {
    return getAttributes(true, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributes(final boolean refresh, final boolean includeDeleted) {

    if (refresh || !isChildrenLoaded()) {

      this.allAttrMap.clear();
      this.varAttrs.clear();

      fillAllVariantAttributes(includeDeleted);

      this.varAttrs.putAll(this.allAttrMap);

      this.childrenLoaded = true;
    }

    return this.allAttrMap;
  }

  /**
   * Fill Variant attributes
   */
  private void fillAllVariantAttributes(final boolean includeDeleted) {

    for (Attribute attribute : this.pidcDataHandler.getAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (!includeDeleted && attribute.isDeleted()) {
          continue;
        }
        Map<Long, PidcVariantAttribute> varAttrmap =
            this.pidcDataHandler.getVariantAttributeMap().get(this.pidcVariant.getId());

        try {
          if (null == varAttrmap) {
            PidcVariantAttributeServiceClient pidcVarAttrClient = new PidcVariantAttributeServiceClient();
            varAttrmap = pidcVarAttrClient.getVarAttrsForVariant(this.pidcVariant.getId());
            this.pidcDataHandler.getVariantAttributeMap().put(this.pidcVariant.getId(), varAttrmap);

          }
          addToAttrMap(attribute, varAttrmap);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    }
  }

  /**
   * @param attribute
   * @param varAttrmap
   */
  private void addToAttrMap(final Attribute attribute, final Map<Long, PidcVariantAttribute> varAttrmap) {
    PidcVariantAttribute pidcVariantAttr = varAttrmap.get(attribute.getId());
    if (pidcVariantAttr != null) {
      this.allAttrMap.put(pidcVariantAttr.getAttrId(), pidcVariantAttr);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<PidcVariantAttribute> getAttributes(final int sortColumn) {
    final SortedSet<PidcVariantAttribute> resultSet =
        new TreeSet<>((p1, p2) -> PidcVariantBO.this.compare(p1, p2, sortColumn));

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }

  /**
   * Get a sorted set of the PIDCs Sub Variants
   *
   * @return PIDCSubVariant sorted set
   */
  public synchronized SortedSet<PidcSubVariant> getSubVariantsSet() {

    final SortedSet<PidcSubVariant> resultSet = new TreeSet<>();

    resultSet.addAll(getSubVariantsMap().values());

    return resultSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributesAll() {
    getAttributes();
    return this.varAttrs;
  }

  /**
   * ICDM-789 Get a sorted set of the PIDCs Sub Variants
   *
   * @param deletedNeeded true or false if deleted sub variants are required
   * @return PIDCSubVariant sorted set of sub variants
   */
  public SortedSet<PidcSubVariant> getSubVariantsSet(final boolean deletedNeeded) {

    final SortedSet<PidcSubVariant> resultSet = new TreeSet<>();

    resultSet.addAll(getSubVariantsMap(deletedNeeded).values());

    return resultSet;
  }

  /**
   * Get a sorted set of the PIDCs Sub Variants
   *
   * @param deletedNeeded true or false if deleted sub variants are required
   * @param unclearedNeeded true or false if variants with uncleared / missing mandatory values required
   * @return PIDCSubVariant sorted set of sub variants
   */
  public SortedSet<PidcSubVariant> getSubVariantsSet(final boolean deletedNeeded, final boolean unclearedNeeded) {

    final SortedSet<PidcSubVariant> resultSet = new TreeSet<>();

    resultSet.addAll(getSubVariantsMap(deletedNeeded, unclearedNeeded).values());

    return resultSet;
  }

  /**
   * Identify the variant attribute for the input attribute ID
   *
   * @param attrID the attribute's ID
   * @return the variant attribute of input attribute
   */
  public PidcVariantAttribute getVarAttribute(final long attrID) {
    for (PidcVariantAttribute varAttr : getAttributes().values()) {
      if (varAttr.getAttrId().longValue() == attrID) {
        return varAttr;
      }
    }
    return null;
  }

  /**
   * Identify the variant attribute for the newly created var attribute ID
   *
   * @param attrID the attribute's ID
   * @return the variant attribute of input attribute
   */
  public PidcVariantAttribute getAllVarAttribute(final long attrID) {
    for (PidcVariantAttribute varAttr : getAttributesAll().values()) {
      if (varAttr.getAttrId().longValue() == attrID) {
        return varAttr;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributesNotDefined() {
    this.usedNotDefinedPIDCAttrMap.clear();
    // Get all used not defined PIDC attributes
    for (PidcVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType().equals(attribute.getUsedFlag()) ||
          isValueInvalid(attribute)) {
        this.usedNotDefinedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.usedNotDefinedPIDCAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributesNotUsed() {
    this.notUsedPIDCAttrMap.clear();
    // Get all not used PIDC attributes
    for (PidcVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(attribute)) {
        this.notUsedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.notUsedPIDCAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcVariantAttribute> getAttributesUsed() {
    this.usedPIDCAttrMap.clear();
    if (!CommonUtils.isNotEmpty(this.allAttrMap)) {
      getAttributes();
    }
    // Get all used PIDC attributes
    for (PidcVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(attribute)) {
        this.usedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.usedPIDCAttrMap;
  }

  /**
   * @param attribute
   * @return This method returns whether the value set is not cleared or deleted
   */
  public boolean isValueInvalid(final PidcVariantAttribute attribute) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
        (this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()) != null) &&
        (this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()).isDeleted());
  }

  /**
   * Get the variant description
   *
   * @return variant description
   */
  public String getVariantDesc() {

    // need to check
    return this.pidcVariant.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(ApicConstants.TOOLTIP_STRING_SIZE);
    tooltip.append("Variant : ").append(this.pidcVariant.getName()).append("\n").append("Description : ")
        .append(getVariantDesc());
    if (hasInvalidAttrValues()) {
      tooltip.append("\n").append("Has uncleared values !");
    }
    if (!isAllMandatoryAttrDefined()) {
      tooltip.append("\n").append("Has missing values for mandatory attributes !");
    }
    return tooltip.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasInvalidAttrValues() {
    boolean ret = false;

    // Check attributes at Variant level first
    for (PidcVariantAttribute varAttr : getAttributes(false, false).values()) {
      if (!varAttr.isAtChildLevel() && isValueInvalid(varAttr)) {
        ret = true;
        break;
      }
    }
    if (!ret) {
      // If not invalid values found, also check attributes at sub-variant level
      for (PidcSubVariant subVar : getSubVariantsSet(false)) {

        PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion, subVar, this.pidcDataHandler);
        ret = subVarHandler.hasInvalidAttrValues();
        if (ret) {
          break;
        }
      }
    }
    return ret;
  }


  /**
   * @return true if all quotation relevant attributes are defined at variant level and subvariant level
   */
  public boolean isAllQuotationAttrDefined() {
    for (PidcVariantAttribute varAttr : getAttributes(false, false).values()) {
      if ((null != varAttr) && varAttr.isAtChildLevel()) {
        // getting undeleted sub-variants
        SortedSet<PidcSubVariant> subVariantsSet = getSubVariantsSet(false);
        for (PidcSubVariant subVar : subVariantsSet) {
          PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion, subVar, this.pidcDataHandler);
          boolean hasUndefndQuotAttrs = subVarHandler.isAllQuotationAttrDefined();
          if (!hasUndefndQuotAttrs) {
            return hasUndefndQuotAttrs;
          }
        }
      }
      else {
        if ((null != varAttr) && !isQuotationAttrDefined(varAttr)) {
          return false;
        }
      }
    }
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAllMandatoryAttrDefined() {
    for (PidcVariantAttribute varAttr : getAttributes(false, false).values()) {
      if ((null != varAttr) && varAttr.isAtChildLevel()) {
        // Task 233411, getting undeleted sub-variants
        SortedSet<PidcSubVariant> aliveSubVariantsSet = getSubVariantsSet(false);

        // if the variant has only deleted sub-variants
        if (aliveSubVariantsSet.isEmpty()) {

          AbstractProjectAttributeBO varAttrBo = new ProjectAttributeUtil().getProjectAttributeHandler(varAttr, this);

          if (varAttrBo.isMandatory()) {
            return false;
          }
        }
        else {
          for (PidcSubVariant subVar : aliveSubVariantsSet) {
            PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion, subVar, this.pidcDataHandler);
            boolean hasUndefndMandatAttrs = subVarHandler.isAllMandatoryAttrDefined();
            if (!hasUndefndMandatAttrs) {
              return hasUndefndMandatAttrs;
            }
          }
        }
      }
      else {
        if ((null != varAttr) && !isMandatoryAttrDefined(varAttr)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * This method checks for whether PIDC mandatory attribute is defined or not
   *
   * @param projAttr defines Project Attribute
   * @return boolean defines mandatory attribute is defined or not
   */
  // ICDM-329
  // ICDM-179
  public <P extends IProjectAttribute> boolean isMandatoryAttrDefined(final P projAttr) {
    // Check for attribute is not deleted, mandatory, not a variant

    AbstractProjectAttributeBO varAttrBo = new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, this);
    return !(varAttrBo.isMandatory() && !this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
        varAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !varAttrBo.isValueDefined(projAttr));

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
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {

    if (copiedObj instanceof PidcVariant) {
      // Get the copied variant
      final PidcVariant pidcVar = (PidcVariant) copiedObj;
      if (selectedObj instanceof PidcVersion) {
        if (isPasteAllowedOnPidcVersNode(selectedObj)) {
          // true if PIDC are same & selected PIDC not deleted
          return true;
        }
      }
      else if ((selectedObj instanceof PidcVariant) && isPasteAllowedOnVarNode(selectedObj, pidcVar)) {
        // true if PIDC variants are not same, PIDC are same & selected PIDC,Variant not deleted
        return true;
      }
    }
    return false;
  }

  /**
   * @param selectedObj
   * @param pidcVar
   * @return
   */
  private boolean isPasteAllowedOnVarNode(final Object selectedObj, final PidcVariant pidcVar) {

    return isRelevantPidcVar(selectedObj, pidcVar) && isPidcVarModifiable(selectedObj);
  }

  /**
   * @param selectedObj
   * @param varHandler
   * @return
   */
  private boolean isPidcVarModifiable(final Object selectedObj) {
    return !((PidcVariant) selectedObj).isDeleted() &&
        !this.pidcDataHandler.getPidcVersionInfo().getPidc().isDeleted() &&
        new PidcVariantBO(this.pidcVersion, (PidcVariant) selectedObj, this.pidcDataHandler).isModifiable();
  }

  /**
   * @param selectedObj
   * @param pidcVar
   * @return
   */
  private boolean isRelevantPidcVar(final Object selectedObj, final PidcVariant pidcVar) {
    return !(pidcVar.getId().equals(((PidcVariant) selectedObj).getId())) &&
        (this.pidcVersion.getId().longValue() == ((PidcVariant) selectedObj).getPidcVersionId().longValue());
  }

  /**
   * @param selectedObj
   * @param pidcVar
   * @return
   */
  private boolean isPasteAllowedOnPidcVersNode(final Object selectedObj) {
    PidcVersionBO versHandler = new PidcVersionBO((PidcVersion) selectedObj, this.pidcDataHandler);

    return (this.pidcVersion.getId().longValue() == ((PidcVersion) selectedObj).getId().longValue()) &&
        !((PidcVersion) selectedObj).isDeleted() && (versHandler.isModifiable());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeFromMap() {

    // remove all invisible attributes
    for (Attribute attribute : this.invisibleAttributes) {
      final Long invisibleAttrID = attribute.getId();
      this.allAttrMap.remove(invisibleAttrID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeInvisibleAttributes() {

    // clear the list of invisible attributes
    this.invisibleAttributes.clear();
    this.invsblePIDCVarAttrsMap.clear();


    // iterate over all attributes and check if they are valid
    // based on the dependencies

    // Add invisible attributes at the Variant Level
    for (PidcVariantAttribute pidcVarAttribute : this.allAttrMap.values()) {

      AttributeClientBO attrClientBo =
          new AttributeClientBO(this.pidcDataHandler.getAttributeMap().get(pidcVarAttribute.getAttrId()));
      // check the attributes dependencies based on the list of attribute values
      if (!attrClientBo.isValid(this.allAttrMap, pidcVarAttribute.getAttrId(), this.pidcDataHandler)) {
        this.invisibleAttributes.add(this.pidcDataHandler.getAttributeMap().get(pidcVarAttribute.getAttrId()));
        this.invsblePIDCVarAttrsMap.put(pidcVarAttribute.getAttrId(), pidcVarAttribute);
      }

    }
    removeFromMap();
    this.invisibleAttributes.clear();
    // Add invisible attributes at the PIDC attribute Level

    PidcVersionBO handler = new PidcVersionBO(this.pidcVersion, this.pidcDataHandler);
    final Map<Long, PidcVersionAttribute> allPidcAttributes = handler.getAttributes(false);
    for (PidcVariantAttribute varAttr : this.allAttrMap.values()) {
      AttributeClientBO attrClientBo =
          new AttributeClientBO(this.pidcDataHandler.getAttributeMap().get(varAttr.getAttrId()));
      // check the attributes dependencies based on the list of attribute values
      if (!attrClientBo.isValid(allPidcAttributes, varAttr.getAttrId(), this.pidcDataHandler)) {
        this.invisibleAttributes.add(this.pidcDataHandler.getAttributeMap().get(varAttr.getAttrId()));
        this.invsblePIDCVarAttrsMap.put(varAttr.getAttrId(), varAttr);
      }

    }
    removeFromMap();

  }


  /**
   * @param nameEng English name of sub variant
   * @param nameGer name in German
   * @return true if the name already exists [Task : 278578]
   */
  public boolean checkIfSubVarNameExists(final String nameEng, final String nameGer) {
    boolean alreadyExists = false;
    // get all subvariants
    Map<Long, PidcSubVariant> subVarsMap = getSubVariantsMap();
    for (PidcSubVariant subVar : subVarsMap.values()) {
      if (subVar.getName().equalsIgnoreCase(nameEng)) {
        alreadyExists = true;
        break;
      }
      if (CommonUtils.isNotEmptyString(nameGer) && subVar.getName().equalsIgnoreCase(nameGer)) {
        alreadyExists = true;
        break;
      }
      if (subVar.getName().equalsIgnoreCase(nameEng)) {
        alreadyExists = true;
        break;
      }

    }
    return alreadyExists;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <O extends IProjectAttribute> boolean isValueDefined(final O pidcAttributeVar) {
    boolean isValueDefined = false;
    boolean isVisible = new ProjectAttributeUtil().getProjectAttributeHandler(pidcAttributeVar, this).isVisible();
    if (pidcAttributeVar.isAtChildLevel()) {
      for (PidcSubVariant subVar : getSubVariantsSet(false)) {

        PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersion, subVar, this.pidcDataHandler);

        PidcSubVariantAttribute pidcSubVarAttr =
            subVarHandler.getAttributes(false, false).get(pidcAttributeVar.getAttrId());

        // if attr is visible at variant and invisble at sub variant, then attr is not relevant and cannot be considered
        // as 'Not defined'

        boolean isSubVarAttrVisible =
            (null != pidcSubVarAttr) && new PidcSubVariantAttributeBO(pidcSubVarAttr, subVarHandler).isVisible();
        if (!isSubVarAttrVisible) {
          continue;
        }
        isValueDefined = subVarHandler.isValueDefined(pidcSubVarAttr);
        if (!isValueDefined) {
          return false;
        }
      }
      return true;
    }
    return isVisibleAndValueDefined(pidcAttributeVar, isVisible);
  }

  /**
   * An attribute is considered as defined if used flag = Y and has value, or if used flag= N Since invisible attributes
   * are not relevant for the project, the method returns true if an attribute is invisible
   *
   * @param pidcAttributeVar
   * @param isVisible
   * @return
   */
  private <O extends IProjectAttribute> boolean isVisibleAndValueDefined(final O pidcAttributeVar,
      final boolean isVisible) {
    return !isVisible ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttributeVar.getUsedFlag()) &&
            (pidcAttributeVar.getValue() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttributeVar.getUsedFlag()));
  }


  /**
   * @return the allAttrMap
   */
  public ConcurrentMap<Long, PidcVariantAttribute> getAllAttrMap() {
    return this.allAttrMap;
  }


  /**
   * @return the invisibleAttributes
   */
  public SortedSet<Attribute> getInvisibleAttributes() {
    return this.invisibleAttributes;
  }


  /**
   * @return the invsblePIDCVarAttrsMap
   */
  public Set<Long> getInvisiblePIDCVarAttrSet() {

    Set<Long> invisibleAttrIds = this.pidcDataHandler.getVariantInvisbleAttributeMap().get(this.pidcVariant.getId());

    return invisibleAttrIds == null ? new HashSet<>() : invisibleAttrIds;
  }


  /**
   * @return the usedPIDCAttrMap
   */
  public ConcurrentMap<Long, PidcVariantAttribute> getUsedPIDCAttrMap() {
    return this.usedPIDCAttrMap;
  }


  /**
   * @return the notUsedPIDCAttrMap
   */
  public ConcurrentMap<Long, PidcVariantAttribute> getNotUsedPIDCAttrMap() {
    return this.notUsedPIDCAttrMap;
  }


  /**
   * @return the usedNotDefinedPIDCAttrMap
   */
  public ConcurrentMap<Long, PidcVariantAttribute> getUsedNotDefinedPIDCAttrMap() {
    return this.usedNotDefinedPIDCAttrMap;
  }


  /**
   * @return the varAttrs
   */
  public ConcurrentMap<Long, PidcVariantAttribute> getVarAttrs() {
    return this.varAttrs;
  }


  /**
   * @return the childrenLoaded
   */
  public boolean isChildrenLoaded() {
    return this.childrenLoaded;
  }


  /**
   * @param childrenLoaded the childrenLoaded to set
   */
  public void setChildrenLoaded(final boolean childrenLoaded) {
    this.childrenLoaded = childrenLoaded;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ProjectObjectStatistics<PidcVariant> getProjectStatistics(final UseCaseDataHandler ucDataHandler,
      final FocusMatrixDataHandler focusMatrixDataHandler) {
    return new ProjectObjectStatistics<>(getPidcVariant(), this);
  }


}
