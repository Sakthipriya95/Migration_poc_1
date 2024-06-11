/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

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
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class PidcSubVariantBO extends AbstractProjectObjectBO {


  private final PidcSubVariant pidcSubVariant;

  private final ConcurrentMap<Long, PidcSubVariantAttribute> usedSubVarAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcSubVariantAttribute> notUsedSubVarAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcSubVariantAttribute> usedNotDefinedSubVarAttrMap = new ConcurrentHashMap<>();


  /**
   * All attributes of PIDC Sub variant
   */

  private final ConcurrentMap<Long, PidcSubVariantAttribute> allAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcSubVariantAttribute> subVarAttrs = new ConcurrentHashMap<>();

  /**
   * @param pidcVersion
   * @param pidcSubVariant
   * @param function
   */
  public PidcSubVariantBO(final PidcVersion pidcVersion, final PidcSubVariant pidcSubVariant,
      final PidcDataHandler pidcDataHandler) {
    super(pidcVersion, pidcSubVariant, pidcDataHandler);
    this.pidcSubVariant = pidcSubVariant;
  }


  /**
   * @return the pidcSubVariant
   */
  public PidcSubVariant getPidcSubVariant() {
    return this.pidcSubVariant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributes(final boolean refresh) {
    return getAttributes(refresh, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributes() {
    return getAttributes(true, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributes(final boolean refresh, final boolean includeDeleted) {

    if (refresh || !this.childrenLoaded) {

      this.allAttrMap.clear();
      this.subVarAttrs.clear();

      fillAllSubVariantAttributes(includeDeleted);

      this.subVarAttrs.putAll(this.allAttrMap);

      this.childrenLoaded = true;
    }

    return this.allAttrMap;
  }

  /**
   * Fill Variant attributes ICDM-196
   *
   * @param includeDeleted
   */
  private void fillAllSubVariantAttributes(final boolean includeDeleted) {

    for (Attribute attribute : this.pidcDataHandler.getAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (!includeDeleted && attribute.isDeleted()) {
          continue;
        }
        Map<Long, PidcSubVariantAttribute> subvarAttrMap =
            this.pidcDataHandler.getSubVariantAttributeMap().get(this.pidcSubVariant.getId());
        try {
          if (null == subvarAttrMap) {
            PidcSubVariantAttributeServiceClient pidcVarAttrClient = new PidcSubVariantAttributeServiceClient();
            subvarAttrMap = pidcVarAttrClient.getSubVarAttrForSubVar(this.pidcSubVariant.getId());
            this.pidcDataHandler.getSubVariantAttributeMap().put(this.pidcSubVariant.getId(), subvarAttrMap);

          }
          addTOAttrMap(attribute, subvarAttrMap);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    }

  }


  /**
   * @param attribute
   * @param subvarAttrMap
   */
  private void addTOAttrMap(final Attribute attribute, final Map<Long, PidcSubVariantAttribute> subvarAttrMap) {
    PidcSubVariantAttribute pidcSubVariantAttr = subvarAttrMap.get(attribute.getId());
    if (pidcSubVariantAttr != null) {
      this.allAttrMap.put(pidcSubVariantAttr.getAttrId(), pidcSubVariantAttr);
    }
  }


  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   */
  @Override
  public SortedSet<PidcSubVariantAttribute> getAttributes(final int sortColumn) {
    SortedSet<PidcSubVariantAttribute> resultSet =
        new TreeSet<>((p1, p2) -> PidcSubVariantBO.this.compare(p1, p2, sortColumn));

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributesAll() {
    getAttributes();
    return this.subVarAttrs;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributesNotDefined() {
    this.usedNotDefinedSubVarAttrMap.clear();
    // Get all used PIDC attributes
    for (PidcSubVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(attribute)) {
        this.usedNotDefinedSubVarAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.usedNotDefinedSubVarAttrMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributesNotUsed() {
    this.notUsedSubVarAttrMap.clear();
    // Get all used PIDC attributes
    for (PidcSubVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(attribute)) {
        this.notUsedSubVarAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.notUsedSubVarAttrMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariantAttribute> getAttributesUsed() {
    this.usedSubVarAttrMap.clear();
    if (!CommonUtils.isNotEmpty(this.allAttrMap)) {
      getAttributes();
    }
    // Get all used PIDC attributes
    for (PidcSubVariantAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(attribute)) {
        this.usedSubVarAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return this.usedSubVarAttrMap;
  }


  /**
   * @param attribute
   * @return This method returns whether the value set is not cleared or deleted
   */
  public boolean isValueInvalid(final PidcSubVariantAttribute attribute) {
    return attribute.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType()) &&
        (this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()) != null) &&
        (this.pidcDataHandler.getAttributeValueMap().get(attribute.getValueId()).isDeleted());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(ApicConstants.TOOLTIP_STRING_SIZE);
    tooltip.append("Sub-Variant : ").append(this.pidcSubVariant.getName()).append("\n").append("Description : ")
        .append(this.pidcSubVariant.getDescription());
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
    for (PidcSubVariantAttribute subVarAttr : getAttributes(false, false).values()) {
      PidcSubVariantAttributeBO handler = new PidcSubVariantAttributeBO(subVarAttr, this);
      if (handler.isValueInvalid()) {
        return true;
      }
    }
    return false;

  }

  /**
   * @return true if all quotation relevant attributes are defined at subvariant level
   */
  public boolean isAllQuotationAttrDefined() {

    for (PidcSubVariantAttribute subVarAttr : getAttributes(false, false).values()) {
      if ((null != subVarAttr) && (!isQuotationAttrDefined(subVarAttr))) {
        return false;
      }
    }
    return true;


  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAllMandatoryAttrDefined() {
    for (PidcSubVariantAttribute subVarAttr : getAttributes(false, false).values()) {
      if ((null != subVarAttr) && (!isMandatoryAttrDefined(subVarAttr))) {
        return false;
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

    AbstractProjectAttributeBO subVarAttrBo = new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, this);
    // Check for attribute is not deleted, mandatory, not a variant
    return !(subVarAttrBo.isMandatory() &&
        !this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
        subVarAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !subVarAttrBo.isValueDefined(projAttr));

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
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeFromMap() {
    // NA

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeInvisibleAttributes() {
    // NA

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValueDefined(final IProjectAttribute projAttr) {
    PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) projAttr;
    boolean ret = false;
    boolean isVisible = new ProjectAttributeUtil().getProjectAttributeHandler(projAttr, this).isVisible();
    PidcSubVariantAttributeBO handler = new PidcSubVariantAttributeBO(subVarAttr, this);
    if (!isVisible ||
        ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.equals(handler.getIsUsedEnum()) && (subVarAttr.getValueId() != null)) ||
            (ApicConstants.PROJ_ATTR_USED_FLAG.NO.equals(handler.getIsUsedEnum())))) {
      ret = true;
    }
    return ret;
  }

  /**
   * @return the invsblePIDCVarAttrsMap
   */
  public Set<Long> getInvisiblePIDCSubVarAttrSet() {

    Set<Long> invisibleAttrIds =
        this.pidcDataHandler.getSubVariantInvisbleAttributeMap().get(this.pidcSubVariant.getId());

    return invisibleAttrIds == null ? new HashSet<>() : invisibleAttrIds;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ProjectObjectStatistics<PidcSubVariant> getProjectStatistics(final UseCaseDataHandler ucDataHandler,
      final FocusMatrixDataHandler focusMatrixDataHandler) {
    return new ProjectObjectStatistics<>(getPidcSubVariant(), this);
  }


}
