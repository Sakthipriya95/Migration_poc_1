/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Represents a single PIDC Sub variant definition
 *
 * @author adn1cob
 */
@Deprecated
public class PIDCSubVariant extends AbstractProjectObject<PIDCAttributeSubVar>
    implements Comparable<PIDCSubVariant>, IPastableItem {


  /**
   * All attributes of PIDC Sub variant
   */

  private final ConcurrentMap<Long, PIDCAttributeSubVar> allAttrMap = new ConcurrentHashMap<>();
  /**
   * Invisible attributes of PIDC Sub variant
   */
  private final SortedSet<Attribute> invisibleAttributes = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

  private final ConcurrentMap<Long, PIDCAttributeSubVar> invsblePIDCSubvarAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeSubVar> usedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeSubVar> notUsedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeSubVar> usedNotDefPidcAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeSubVar> subVarAttrs = new ConcurrentHashMap<>();

  /**
   * PIDCSubVariant - Constructor
   *
   * @param apicDataProvider the data provider
   * @param subVariantId sub variant id
   */
  public PIDCSubVariant(final ApicDataProvider apicDataProvider, final Long subVariantId) {
    super(apicDataProvider, subVariantId);
    apicDataProvider.getDataCache().getAllPidcSubVariants().put(subVariantId, this);
  }

  /**
   * {@inheritDoc} returns true if the user has write access for the PIDC Sub variant
   */
  @Override
  public boolean isModifiable() {

    final NodeAccessRight currUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();

    if ((currUserAccRight != null) && currUserAccRight.hasWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * @return PIDC Version
   */
  @Override
  public PIDCVersion getPidcVersion() {
    // get the projectid card, which is latest
    PIDCVersion pidcVersion = getDataCache()
        .getPidcVersion(getEntityProvider().getDbPidcSubVariant(getSubVariantID()).getTPidcVersion().getPidcVersId());
    return pidcVersion;

  }

  /**
   * @return PIDCVariant
   */
  public PIDCVariant getPidcVariant() {
    return getDataCache().getPidcVaraint(
        getEntityProvider().getDbPidcSubVariant(getSubVariantID()).getTabvProjectVariant().getVariantId());
  }

  /**
   * @return SubVariantName
   */
  public String getSubVariantName() {

    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getTextvalueEng();
  }

  /**
   * Get Subvariant Description
   *
   * @return Description
   */
  public String getSubVariantDesc() {

    if (getDataCache().getLanguage() == Language.ENGLISH) {
      return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueDescEng();
    }
    if (getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getTextvalueGer() != null) {
      return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueDescGer();
    }
    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueDescEng();
  }

  /**
   * @return Attribute
   */
  public Attribute getSubVariantNameAttr() {
    return getDataCache().getAttribute(
        getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getTabvAttribute().getAttrId());

  }

  /**
   * @return SubVariantID
   */
  public long getSubVariantID() {
    return getID();
  }


  /**
   * @return Revision
   */
  public long getRevision() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getTPidcVersion().getProRevId().longValue();
  }

  /**
   * @return SubVariantNameEng
   */
  public String getSubVariantNameEng() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getTextvalueEng();
  }

  /**
   * @return SubVariantNameGer
   */
  public String getSubVariantNameGer() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getTextvalueGer();
  }

  /**
   * @return SubVariantDescEng
   */
  public String getSubVariantDescEng() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueDescEng();
  }

  /**
   * @return SubVariantDescGer
   */
  public String getSubVariantDescGer() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueDescGer();
  }

  /**
   * @return CreatedDate
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcSubVariant(getID()).getCreatedDate());
  }

  /**
   * @return ModifiedDate
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcSubVariant(getID()).getModifiedDate());
  }

  /**
   * @return CreatedUser
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getCreatedUser();
  }

  /**
   * @return ModifiedUser
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getModifiedUser();
  }

  /**
   * Returns the corresponding attrValue linked for the PIDCSubvariant's name
   *
   * @return AttributeValue
   */
  public AttributeValue getNameValue() {
    return getDataCache()
        .getAttrValue(getEntityProvider().getDbPidcSubVariant(getID()).getTabvAttrValue().getValueId());
  }

  /**
   * @return deleted flag
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbPidcSubVariant(getID()).getDeletedFlag().equals(ApicConstants.YES);
  }

  /**
   * Returns all attributes of this SubVariant ( does not include deleted attrs)
   *
   * @return PIDCAttributeSubVar map
   */
  @Override
  public synchronized Map<Long, PIDCAttributeSubVar> getAttributes() {
    return getAttributes(true, false);
  }


  /**
   * @return all atrributes
   * @param refresh value true loads freshly, refresh false takes existing attrmap
   * @param includeDeleted whether to include deleted attributes or not
   */
  // ICDM-179
  public synchronized Map<Long, PIDCAttributeSubVar> getAttributes(final boolean refresh,
      final boolean includeDeleted) {

    if (refresh || !this.childrenLoaded) {

      this.allAttrMap.clear();
      this.subVarAttrs.clear();

      fillAllSubVariantAttributes(includeDeleted);

      this.subVarAttrs.putAll(this.allAttrMap);


      // ICDM-196
      removeInvisibleAttributes();
      this.childrenLoaded = true;
    }

    return this.allAttrMap;
  }

  /**
   * get all the sub variant attributes which includes both visible and invisble attrs except deleted
   * <p>
   * {@inheritDoc}
   *
   * @return attrsmap
   */
  // ICDM-479
  @Override
  public Map<Long, PIDCAttributeSubVar> getAttributesAll() {
    getAttributes();
    return this.subVarAttrs;

  }

  /**
   * Fill Variant attributes ICDM-196
   *
   * @param includeDeleted
   */
  private void fillAllSubVariantAttributes(final boolean includeDeleted) {

    if (CommonUtils.isNotNull(getEntityProvider().getDbPidcSubVariant(getID()).getTabvProjSubVariantsAttrs())) { // null
                                                                                                                 // check

      for (TabvProjSubVariantsAttr dbPidcSubVarAttr : getEntityProvider().getDbPidcSubVariant(getID())
          .getTabvProjSubVariantsAttrs()) {
        final long pidcSubVarAttrID = dbPidcSubVarAttr.getSubVarAttrId();

        // skip attributes marked as deleted
        if (!includeDeleted &&
            getDataCache().getAttribute(dbPidcSubVarAttr.getTabvAttribute().getAttrId()).isDeleted()) {
          continue;
        }

        final PIDCAttributeSubVar subvarAttr =
            new PIDCAttributeSubVar(getDataCache().getDataProvider(), pidcSubVarAttrID);
        getDataCache().getAllPidcSubVarAttrMap().put(pidcSubVarAttrID, subvarAttr);

        this.allAttrMap.put(dbPidcSubVarAttr.getTabvAttribute().getAttrId(), subvarAttr);

      }
    }
  }

  /**
   * Remove the attributes which are not visible based on the attribute dependencies ICDM-196
   */
  private void removeInvisibleAttributes() {

    // clear the list of invisible attributes
    this.invisibleAttributes.clear();
    this.invsblePIDCSubvarAttrMap.clear();

    // iterate over all attributes and check if they are valid
    // based on the dependencies

    // Add invisible attributes at the Sub Variant Level
    for (PIDCAttributeSubVar pidcAttribute : this.allAttrMap.values()) {
      // check the attributes dependencies based on the list of attribute values
      if (!pidcAttribute.getAttribute().isValid(this.allAttrMap)) {
        this.invisibleAttributes.add(pidcAttribute.getAttribute());
        this.invsblePIDCSubvarAttrMap.put(pidcAttribute.getAttribute().getAttributeID(), pidcAttribute);
      }

    }
    removeFromMap();

    // Add invisible attributes at the Variant Level
    final Map<Long, PIDCAttributeVar> allPidcVarAttr = getPidcVariant().getAttributes(false, false);
    for (PIDCAttributeSubVar svarAttr : this.allAttrMap.values()) {
      // check the attributes dependencies based on the list of attribute values
      if (!svarAttr.getAttribute().isValid(allPidcVarAttr)) {
        this.invisibleAttributes.add(svarAttr.getAttribute());
        this.invsblePIDCSubvarAttrMap.put(svarAttr.getAttribute().getAttributeID(), svarAttr);
      }

    }
    removeFromMap();
    this.invisibleAttributes.clear();
    // Add invisible attributes at the PIDC attribute Level
    final Map<Long, PIDCAttribute> allPidcAttributes = getPidcVersion().getAttributes(false);
    for (PIDCAttributeSubVar svarAttr : this.allAttrMap.values()) {
      // check the attributes dependencies based on the list of attribute values
      if (!svarAttr.getAttribute().isValid(allPidcAttributes)) {
        this.invisibleAttributes.add(svarAttr.getAttribute());
        this.invsblePIDCSubvarAttrMap.put(svarAttr.getAttribute().getAttributeID(), svarAttr);
      }

    }
    removeFromMap();

  }

  /**
   * Remove the attributes from map ICDM-196
   */
  private void removeFromMap() {

    // remove all invisible attributes
    for (Attribute attribute : this.invisibleAttributes) {
      final Long invisibleAttrID = attribute.getAttributeID();

      this.allAttrMap.remove(invisibleAttrID);
    }

  }

  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   */
  public synchronized SortedSet<PIDCAttributeSubVar> getAttributes(final int sortColumn) {
    SortedSet<PIDCAttributeSubVar> resultSet = new TreeSet<>(new Comparator<PIDCAttributeSubVar>() {

      @Override
      public int compare(final PIDCAttributeSubVar svarAttr1, final PIDCAttributeSubVar svarAttr2) {
        return svarAttr1.compareTo(svarAttr2, sortColumn);
      }
    });

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCSubVariant subVariant) {
    int compareResult = ApicUtil.compare(getName(), subVariant.getName());
    if (compareResult == 0) {
      compareResult = ApicUtil.compare(getID(), subVariant.getID());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Identify the sub-variant attribute for the input attribute ID
   *
   * @param attrID attribute ID
   * @return the sub variant attribute for given attribute ID
   */
  public synchronized PIDCAttributeSubVar getSubVarAttribute(final long attrID) {
    for (PIDCAttributeSubVar varAttr : getAttributes().values()) {
      if (varAttr.attrID.longValue() == attrID) {
        return varAttr;
      }
    }
    return null;
  }

  // ICDM-1780
  /**
   * Identify the sub-variant attribute for the newly created sub-var attribute ID
   *
   * @param attrID attribute ID
   * @return the sub variant attribute for given attribute ID
   */
  public synchronized PIDCAttributeSubVar getAllSubVarAttribute(final long attrID) {
    for (PIDCAttributeSubVar varAttr : getAttributesAll().values()) {
      if (varAttr.attrID.longValue() == attrID) {
        return varAttr;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc} true if the copied object can be pasted
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {

    if (copiedObj instanceof PIDCSubVariant) {
      // Get the copied sub-variant
      final PIDCSubVariant pidcSubVar = (PIDCSubVariant) copiedObj;
      if (selectedObj instanceof PIDCVariant) {
        if ((pidcSubVar.getPidcVariant().getVariantID() == ((PIDCVariant) selectedObj).getVariantID()) &&
            !((PIDCVariant) selectedObj).isDeleted() && !(((PIDCVariant) selectedObj).getPidcVersion()).isDeleted() &&
            ((PIDCVariant) selectedObj).isModifiable()) {
          // true only if PIDC variants are same & selected PIDC,Variant are not deleted
          return true;
        }
      }
      else if (selectedObj instanceof PIDCDetailsNode) {
        if ((pidcSubVar.getPidcVariant().getVariantID() == ((PIDCDetailsNode) selectedObj).getPidcVariant()
            .getVariantID()) && !((PIDCDetailsNode) selectedObj).getPidcVariant().isDeleted() &&
            !(((PIDCDetailsNode) selectedObj).getPidcVariant().getPidcVersion()).isDeleted() &&
            ((PIDCDetailsNode) selectedObj).getPidcVariant().isModifiable()) {
          // true only if PIDC variants are same & selected PIDC,Variant are not deleted
          return true;
        }
      }
      else if (selectedObj instanceof PIDCSubVariant) {
        if ((pidcSubVar.getSubVariantID() != ((PIDCSubVariant) selectedObj).getSubVariantID()) &&
            (pidcSubVar.getPidcVariant().getID().longValue() == ((PIDCSubVariant) selectedObj).getPidcVariant().getID()
                .longValue()) &&
            !((PIDCSubVariant) selectedObj).isDeleted() &&
            !((PIDCSubVariant) selectedObj).getPidcVariant().isDeleted() &&
            !((PIDCSubVariant) selectedObj).getPidcVersion().isDeleted() &&
            ((PIDCSubVariant) selectedObj).isModifiable()) {
          // true only if PIDC subvariants are not same, PIDC variants are same & selected
          // PIDC,Variant,subvariant are not deleted
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This method returns used PIDC Sub Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeSubVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeSubVar> getAttributesUsed() {
    this.usedPIDCAttrMap.clear();
    // Get all used PIDC attributes
    for (PIDCAttributeSubVar attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.usedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.usedPIDCAttrMap;
  }

  /**
   * This method returns not used PIDC Sub Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeSubVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeSubVar> getAttributesNotUsed() {
    this.notUsedPIDCAttrMap.clear();
    // Get all not used PIDC attributes
    for (PIDCAttributeSubVar attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.notUsedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.notUsedPIDCAttrMap;
  }

  /**
   * This method returns used not defined PIDC Sub Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeSubVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeSubVar> getAttributesNotDefined() {
    this.usedNotDefPidcAttrMap.clear();
    // Get all used not defined PIDC attributes
    for (PIDCAttributeSubVar attribute : this.allAttrMap.values()) {
      if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType(), attribute.getIsUsed()) ||
          attribute.isValueInvalid()) {
        this.usedNotDefPidcAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.usedNotDefPidcAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getSubVariantName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.SUB_VARIANT;
  }

  /**
   * Gets the invisible attributes
   *
   * @return the invisiblePIDCSubvarAttributesMap with AttributeID as key
   */
  public Map<Long, PIDCAttributeSubVar> getInvisiblePIDCSubvarAttributesMap() {
    return this.invsblePIDCSubvarAttrMap;
  }

  /**
   * ICDM-1107
   *
   * @return tooltip with name & description
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(ApicConstants.TOOLTIP_STRING_SIZE);
    tooltip.append("Sub-Variant : ").append(getName()).append("\n").append("Description : ")
        .append(getSubVariantDesc());
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
  public String getDescription() {
    return getSubVariantDesc();
  }

  /**
   * @return whether the variant has attributes which are not cleared or deleted
   */
  @Override
  public boolean hasInvalidAttrValues() {
    for (PIDCAttributeSubVar subVarAttr : getAttributes(false, false).values()) {
      if (subVarAttr.isValueInvalid()) {
        return true;
      }
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAllMandatoryAttrDefined() {
    for (PIDCAttributeSubVar subVarAttr : getAttributes(false, false).values()) {
      if ((null != subVarAttr) && (!getPidcVersion().isMandatoryAttrDefined(subVarAttr))) {
        return false;
      }
    }
    return true;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProjectObjectStatistics<PIDCSubVariant> getProjectStatistics() {
    return new ProjectObjectStatistics<PIDCSubVariant>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PIDCAttributeSubVar> getAttributes(final boolean refresh) {
    return getAttributes(refresh, false);
  }

  /**
   * @param pidcAttributeSubVar pidcAttributeSubVar
   * @return true is value is defined
   */
  public boolean isValueDefined(final PIDCAttributeSubVar pidcAttributeSubVar) {
    boolean ret = false;
    if (((ApicConstants.PROJ_ATTR_USED_FLAG.YES == pidcAttributeSubVar.getIsUsedEnum()) &&
        (pidcAttributeSubVar.getAttributeValue() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO == pidcAttributeSubVar.getIsUsedEnum())) {
      ret = true;
    }
    return ret;
  }

}
