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

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class is to store the PIDC variant as stored in TABV_PROJECT_VARIANTS database table
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:35
 */
@Deprecated
public class PIDCVariant extends AbstractProjectObject<PIDCAttributeVar>
    implements Comparable<PIDCVariant>, IPastableItem, ILinkableObject {

  /**
   * All attributes of PIDC Variant, includes sub variant attrs
   */
  private final ConcurrentMap<Long, PIDCAttributeVar> allAttrMap = new ConcurrentHashMap<>();
  /**
   * Invisible attributes of PIDC Variant
   */
  private final SortedSet<Attribute> invisibleAttributes = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

  private final ConcurrentMap<Long, PIDCAttributeVar> invsblePIDCVarAttrsMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeVar> usedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeVar> notUsedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeVar> usedNotDefinedPIDCAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PIDCAttributeVar> varAttrs = new ConcurrentHashMap<>();

  /**
   * PIDCVariant Constructoro
   *
   * @param apicDataProvider data provider
   * @param variantID ID
   */
  public PIDCVariant(final ApicDataProvider apicDataProvider, final Long variantID) {

    super(apicDataProvider, variantID);
    apicDataProvider.getDataCache().getAllPidcVariants().put(variantID, this);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {

    NodeAccessRight currUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();

    if ((currUserAccRight != null) && currUserAccRight.hasWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * @return PIDC Version parent of this variant
   */
  @Override
  public PIDCVersion getPidcVersion() {
    // get the projectid card version
    return getDataCache()
        .getPidcVersion(getEntityProvider().getDbPidcVariant(getVariantID()).getTPidcVersion().getPidcVersId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbPidcVariant(getVariantID()).getVersion();
  }

  /**
   * @return VariantName
   */
  public String getVariantName() {
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getTextvalueEng();
  }

  /**
   * Get the variant description
   *
   * @return variant description
   */
  public String getVariantDesc() {
    if (getDataCache().getLanguage() == Language.ENGLISH) {
      return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueDescEng();
    }
    if (getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getTextvalueGer() != null) {
      return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueDescGer();
    }
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueDescEng();
  }

  /**
   * @return VariantNameAttr
   */
  public Attribute getVariantNameAttr() {
    return getDataCache()
        .getAttribute(getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getTabvAttribute().getAttrId());

  }

  /**
   * @return VariantID
   */
  public long getVariantID() {
    return getID();
  }

  /**
   * @return Revision
   */
  public long getRevision() {
    return getEntityProvider().getDbPidcVariant(getID()).getTPidcVersion().getProRevId().longValue();
  }

  /**
   * @return VariantNameEng
   */
  public String getVariantNameEng() {
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getTextvalueEng();
  }

  /**
   * @return VariantNameGer
   */
  public String getVariantNameGer() {
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getTextvalueGer();
  }

  /**
   * @return VariantDescEng
   */
  public String getVariantDescEng() {
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueDescEng();
  }

  /**
   * @return VariantDescGer
   */
  public String getVariantDescGer() {
    return getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueDescGer();
  }

  /**
   * @return CreatedDate
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcVariant(getID()).getCreatedDate());
  }

  /**
   * @return ModifiedDate
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcVariant(getID()).getModifiedDate());
  }

  /**
   * @return CreatedUser
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPidcVariant(getID()).getCreatedUser();
  }

  /**
   * @return ModifiedUser
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPidcVariant(getID()).getModifiedUser();
  }

  /**
   * @return isDeleted
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbPidcVariant(getID()).getDeletedFlag().equals(ApicConstants.YES);
  }


  /**
   * Returns the corresponding attrValue linked for the PIDC Variant's name
   *
   * @return AttributeValue
   */
  public AttributeValue getNameValue() {
    return getDataCache().getAttrValue(getEntityProvider().getDbPidcVariant(getID()).getTabvAttrValue().getValueId());
  }

  /**
   * @return all atrributes
   * @param refresh value true loads freshly, refresh false takes existing attrmap
   * @param includeDeleted if true, then return includes deleted attributes
   */
  public synchronized Map<Long, PIDCAttributeVar> getAttributes(final boolean refresh, final boolean includeDeleted) {

    if (refresh || !isChildrenLoaded()) {

      this.allAttrMap.clear();
      this.varAttrs.clear();

      fillAllVariantAttributes(includeDeleted);

      this.varAttrs.putAll(this.allAttrMap);


      // ICDM-196
      removeInvisibleAttributes();


      this.childrenLoaded = true;
    }

    return this.allAttrMap;
  }

  /**
   * @return all atrributes with refresh it is called when loading PIDC Varaint
   */
  @Override
  public Map<Long, PIDCAttributeVar> getAttributes() {
    return getAttributes(true, false);
  }

  /**
   * get all the variant attributes which includes both visible and invisble attrs except deleted
   * <p>
   * {@inheritDoc}
   *
   * @return attrsmap
   */
  // ICDM-479
  @Override
  public Map<Long, PIDCAttributeVar> getAttributesAll() {
    getAttributes();
    return this.varAttrs;

  }

  /**
   * Fill Variant attributes
   */
  private void fillAllVariantAttributes(final boolean includeDeleted) {

    for (TabvVariantsAttr dbPidcVarAttr : getEntityProvider().getDbPidcVariant(getID()).getTabvVariantsAttrs()) {

      final long pidcVarAttrID = dbPidcVarAttr.getVarAttrId();

      // skip attributes marked as deleted
      if (!includeDeleted && getDataCache().getAttribute(dbPidcVarAttr.getTabvAttribute().getAttrId()).isDeleted()) {
        continue;
      }
      final PIDCAttributeVar varAttr = new PIDCAttributeVar(getDataCache().getDataProvider(), pidcVarAttrID);
      getDataCache().getAllPidcVarAttrMap().put(pidcVarAttrID, varAttr);

      this.allAttrMap.put(dbPidcVarAttr.getTabvAttribute().getAttrId(), varAttr);
    }
  }


  /**
   * Remove the attributes which are not visible based on the attribute dependencies ICDM-196
   */
  private void removeInvisibleAttributes() {

    // clear the list of invisible attributes
    this.invisibleAttributes.clear();
    this.invsblePIDCVarAttrsMap.clear();


    // iterate over all attributes and check if they are valid
    // based on the dependencies

    // Add invisible attributes at the Variant Level
    for (PIDCAttributeVar pidcAttribute : this.allAttrMap.values()) {
      // check the attributes dependencies based on the list of attribute values
      if (!pidcAttribute.getAttribute().isValid(this.allAttrMap)) {
        this.invisibleAttributes.add(pidcAttribute.getAttribute());
        this.invsblePIDCVarAttrsMap.put(pidcAttribute.getAttribute().getAttributeID(), pidcAttribute);
      }

    }
    removeFromMap();
    this.invisibleAttributes.clear();
    // Add invisible attributes at the PIDC attribute Level
    final Map<Long, PIDCAttribute> allPidcAttributes = getPidcVersion().getAttributes(false);
    for (PIDCAttributeVar varAttr : this.allAttrMap.values()) {
      // check the attributes dependencies based on the list of attribute values
      if (!varAttr.getAttribute().isValid(allPidcAttributes)) {
        this.invisibleAttributes.add(varAttr.getAttribute());
        this.invsblePIDCVarAttrsMap.put(varAttr.getAttribute().getAttributeID(), varAttr);
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
   * Get a MAP of the PIDCs Sub Variants including deleted sub variants
   *
   * @return PIDCSubVariant
   */
  public synchronized Map<Long, PIDCSubVariant> getSubVariantsMap() {

    final Map<Long, PIDCSubVariant> subVariantsMap = new ConcurrentHashMap<Long, PIDCSubVariant>();

    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(getID());
    if (dbVariant.getTabvProjectSubVariants() == null) {
      return subVariantsMap;
    }

    for (TabvProjectSubVariant pidcSubVariant : dbVariant.getTabvProjectSubVariants()) {
      subVariantsMap.put(pidcSubVariant.getSubVariantId(),
          getDataCache().getPidcSubVaraint(pidcSubVariant.getSubVariantId()));
    }

    this.childrenLoaded = true;
    return subVariantsMap;
  }

  // ICDM-789

  /**
   * Get a MAP of the PIDCs Sub Variants
   *
   * @param deletedNeeded deletedNeeded true or false if deleted variants are required
   * @return PIDCSubVariant Map
   */
  public synchronized Map<Long, PIDCSubVariant> getSubVariantsMap(final boolean deletedNeeded) {

    return getSubVariantsMap(deletedNeeded, false);
  }

  // ICDM-2198
  /**
   * Get a MAP of the PIDCs Sub Variants
   *
   * @param deletedNeeded deletedNeeded true or false if deleted variants are required
   * @param showOnlyUncleared true or false if subvariants with only uncleared / missing mandatory values to be
   *          displayed showOnlyUncleared = true --> Only subvariants with uncleared / missing mandatory values will be
   *          displayed in PIDC structure view
   * @return PIDCSubVariant Map
   */
  public synchronized Map<Long, PIDCSubVariant> getSubVariantsMap(final boolean deletedNeeded,
      final boolean showOnlyUncleared) {

    final Map<Long, PIDCSubVariant> subVariantsMap = new ConcurrentHashMap<Long, PIDCSubVariant>();

    final Map<Long, PIDCSubVariant> subVariantsMapIterate = getSubVariantsMap();

    if (subVariantsMapIterate == null) {
      return subVariantsMap;
    }

    for (PIDCSubVariant pidcSubVariant : subVariantsMapIterate.values()) {
      // Check if uncleared subvariants need to be displayed
      if (showOnlyUncleared) {
        // If uncleared sub variants are needed, check if it has invalid attributes and mandatory attributes are not
        // defined
        if (pidcSubVariant.hasInvalidAttrValues() || !pidcSubVariant.isAllMandatoryAttrDefined()) {
          checkIfDeletedNeeded(subVariantsMap, pidcSubVariant);
        }
      }
      else {
        checkIfDeletedNeeded(subVariantsMap, pidcSubVariant);
      }
      // Check whether deleted subvariants are needed
      if (deletedNeeded && pidcSubVariant.isDeleted()) {
        subVariantsMap.put(pidcSubVariant.getSubVariantID(), pidcSubVariant);
      }
    }

    return subVariantsMap;
  }

  /**
   * @param subVariantsMap
   * @param pidcSubVariant
   */
  private void checkIfDeletedNeeded(final Map<Long, PIDCSubVariant> subVariantsMap,
      final PIDCSubVariant pidcSubVariant) {
    // Check whether the variant is not deleted, else deleted variants will get added irrespective of the 'show deleetd'
    // flag
    if (!pidcSubVariant.isDeleted()) {
      subVariantsMap.put(pidcSubVariant.getSubVariantID(), pidcSubVariant);
    }
  }

  /**
   * @param nameEng English name of sub variant
   * @param nameGer name in German
   * @return true if the name already exists [Task : 278578]
   */
  public boolean checkIfSubVarNameExists(final String nameEng, final String nameGer) {
    boolean alreadyExists = false;
    // get all subvariants
    Map<Long, PIDCSubVariant> subVarsMap = getSubVariantsMap();
    for (PIDCSubVariant subVar : subVarsMap.values()) {
      if (((ApicDataProvider) getDataProvider()).getLanguage() == Language.ENGLISH) {
        if (subVar.getName().equalsIgnoreCase(nameEng)) {
          alreadyExists = true;
          break;
        }
      }
      else {
        if (CommonUtils.isNotEmptyString(nameGer) && subVar.getName().equalsIgnoreCase(nameGer)) {
          alreadyExists = true;
          break;
        }
        if (subVar.getName().equalsIgnoreCase(nameEng)) {
          alreadyExists = true;
          break;
        }
      }

    }
    return alreadyExists;
  }

  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   */
  public synchronized SortedSet<PIDCAttributeVar> getAttributes(final int sortColumn) {
    final SortedSet<PIDCAttributeVar> resultSet = new TreeSet<PIDCAttributeVar>(new Comparator<PIDCAttributeVar>() {

      @Override
      public int compare(final PIDCAttributeVar pidcAttr1, final PIDCAttributeVar pidcAttr2) {
        return pidcAttr1.compareTo(pidcAttr2, sortColumn);
      }
    });

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }

  /**
   * Get a sorted set of the PIDCs Sub Variants
   *
   * @return PIDCSubVariant sorted set
   */
  public synchronized SortedSet<PIDCSubVariant> getSubVariantsSet() {

    final SortedSet<PIDCSubVariant> resultSet = new TreeSet<PIDCSubVariant>();

    resultSet.addAll(getSubVariantsMap().values());

    return resultSet;
  }

  /**
   * ICDM-789 Get a sorted set of the PIDCs Sub Variants
   *
   * @param deletedNeeded true or false if deleted sub variants are required
   * @return PIDCSubVariant sorted set of sub variants
   */
  public synchronized SortedSet<PIDCSubVariant> getSubVariantsSet(final boolean deletedNeeded) {


    final SortedSet<PIDCSubVariant> resultSet = new TreeSet<PIDCSubVariant>();

    resultSet.addAll(getSubVariantsMap(deletedNeeded).values());

    return resultSet;
  }

  // ICDM-2198
  /**
   * Get a sorted set of the PIDCs Sub Variants
   *
   * @param deletedNeeded true or false if deleted sub variants are required
   * @param unclearedNeeded true or false if variants with uncleared / missing mandatory values required
   * @return PIDCSubVariant sorted set of sub variants
   */
  public synchronized SortedSet<PIDCSubVariant> getSubVariantsSet(final boolean deletedNeeded,
      final boolean unclearedNeeded) {

    final SortedSet<PIDCSubVariant> resultSet = new TreeSet<PIDCSubVariant>();

    resultSet.addAll(getSubVariantsMap(deletedNeeded, unclearedNeeded).values());

    return resultSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCVariant arg0) {
    int compareResult = ApicUtil.compare(getName(), arg0.getName());
    if (compareResult == 0) {
      compareResult = ApicUtil.compare(getID(), arg0.getID());
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
   * Identify the variant attribute for the input attribute ID
   *
   * @param attrID the attribute's ID
   * @return the variant attribute of input attribute
   */
  public synchronized PIDCAttributeVar getVarAttribute(final long attrID) {
    for (PIDCAttributeVar varAttr : getAttributes().values()) {
      if (varAttr.attrID.longValue() == attrID) {
        return varAttr;
      }
    }
    return null;
  }

  // ICDM-1780
  /**
   * Identify the variant attribute for the newly created var attribute ID
   *
   * @param attrID the attribute's ID
   * @return the variant attribute of input attribute
   */
  public synchronized PIDCAttributeVar getAllVarAttribute(final long attrID) {
    for (PIDCAttributeVar varAttr : getAttributesAll().values()) {
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

    if (copiedObj instanceof PIDCVariant) {
      // Get the copied variant
      final PIDCVariant pidcVar = (PIDCVariant) copiedObj;
      if (selectedObj instanceof PIDCVersion) {
        if (isPasteAllowedOnPidcVersNode(selectedObj, pidcVar)) {
          // true if PIDC are same & selected PIDC not deleted
          return true;
        }
      }
      else if (selectedObj instanceof PIDCVariant) {
        if (isPasteAllowedOnVarNode(selectedObj, pidcVar)) {
          // true if PIDC variants are not same, PIDC are same & selected PIDC,Variant not deleted
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param selectedObj
   * @param pidcVar
   * @return
   */
  private boolean isPasteAllowedOnVarNode(final Object selectedObj, final PIDCVariant pidcVar) {
    return (pidcVar.getVariantID() != ((PIDCVariant) selectedObj).getVariantID()) &&
        (pidcVar.getPidcVersion().getID().longValue() == ((PIDCVariant) selectedObj).getPidcVersion().getID()
            .longValue()) &&
        !((PIDCVariant) selectedObj).isDeleted() && !(((PIDCVariant) selectedObj).getPidcVersion()).isDeleted() &&
        ((PIDCVariant) selectedObj).isModifiable();
  }

  /**
   * @param selectedObj
   * @param pidcVar
   * @return
   */
  private boolean isPasteAllowedOnPidcVersNode(final Object selectedObj, final PIDCVariant pidcVar) {
    return (pidcVar.getPidcVersion().getID().longValue() == ((PIDCVersion) selectedObj).getID().longValue()) &&
        !((PIDCVersion) selectedObj).isDeleted() && ((PIDCVersion) selectedObj).isModifiable();
  }

  /**
   * This method returns used Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeVar> getAttributesUsed() {
    this.usedPIDCAttrMap.clear();
    // Get all used PIDC attributes
    for (PIDCAttributeVar attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.usedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.usedPIDCAttrMap;
  }

  /**
   * This method returns not used Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeVar> getAttributesNotUsed() {
    this.notUsedPIDCAttrMap.clear();
    // Get all not used PIDC attributes
    for (PIDCAttributeVar attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.notUsedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.notUsedPIDCAttrMap;
  }

  /**
   * This method returns used not defined Variant attributes
   * <p>
   * {@inheritDoc}
   *
   * @return Map. Key - Long, Value - PIDCAttributeVar
   */
  @Override
  public synchronized Map<Long, PIDCAttributeVar> getAttributesNotDefined() {
    this.usedNotDefinedPIDCAttrMap.clear();
    // Get all used not defined PIDC attributes
    for (PIDCAttributeVar attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equals(attribute.getIsUsed()) ||
          attribute.isValueInvalid()) {
        this.usedNotDefinedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.usedNotDefinedPIDCAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getVariantName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.VARIANT;
  }

  /**
   * Gets the invisible attributes
   *
   * @return the invisiblePIDCVarAttributesMap with AttributeID as key
   */
  public Map<Long, PIDCAttributeVar> getInvisiblePIDCVarAttributesMap() {
    return this.invsblePIDCVarAttrsMap;
  }

  /**
   * ICDM-1107
   *
   * @return tooltip with name & description
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(ApicConstants.TOOLTIP_STRING_SIZE);
    tooltip.append("Variant : ").append(getName()).append("\n").append("Description : ").append(getVariantDesc());
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
    return getVariantDesc();
  }

  /**
   * @return whether the variant has attributes which are not cleared or deleted
   */
  @Override
  public boolean hasInvalidAttrValues() {
    boolean ret = false;

    // Check attributes at Variant level first
    for (PIDCAttributeVar varAttr : getAttributes(false, false).values()) {
      if (!varAttr.isVariant() && varAttr.isValueInvalid()) {
        ret = true;
        break;
      }
    }
    if (!ret) {
      // If not invalid values found, also check attributes at sub-variant level
      for (PIDCSubVariant subVar : getSubVariantsSet(false)) {
        ret = subVar.hasInvalidAttrValues();
        if (ret) {
          break;
        }
      }
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAllMandatoryAttrDefined() {
    for (PIDCAttributeVar varAttr : getAttributes(false, false).values()) {
      if ((null != varAttr) && varAttr.isVariant()) {
        // Task 233411, getting undeleted sub-variants
        SortedSet<PIDCSubVariant> aliveSubVariantsSet = getSubVariantsSet(false);

        // if the variant has only deleted sub-variants
        if (aliveSubVariantsSet.isEmpty()) {
          if (varAttr.isMandatory()) {
            return false;
          }
        }
        else {
          for (PIDCSubVariant subVar : aliveSubVariantsSet) {
            boolean hasUndefndMandatAttrs = subVar.isAllMandatoryAttrDefined();
            if (!hasUndefndMandatAttrs) {
              return hasUndefndMandatAttrs;
            }
          }
        }
      }
      else {
        if ((null != varAttr) && !getPidcVersion().isMandatoryAttrDefined(varAttr)) {
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
  public ProjectObjectStatistics<PIDCVariant> getProjectStatistics() {
    return new ProjectObjectStatistics<PIDCVariant>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PIDCAttributeVar> getAttributes(final boolean refresh) {
    return getAttributes(refresh, false);
  }

  /**
   * @param pidcAttributeVar pidcAttributeVar
   * @return true is value is defined
   */
  public boolean isValueDefined(final PIDCAttributeVar pidcAttributeVar) {
    boolean ret = false;
    if (pidcAttributeVar.isVariant()) {
      for (PIDCSubVariant subVar : getSubVariantsSet(false)) {
        PIDCAttributeSubVar pidcSubVarAttr =
            subVar.getAttributes(false, false).get(pidcAttributeVar.getAttribute().getID());
        // if attr is visible at variant and invisble at sub variant, then attr is not relevant and cannot be considered
        // as 'Not defined'
        if (null == pidcSubVarAttr) {
          continue;
        }
        ret = subVar.isValueDefined(pidcSubVarAttr);
        if (!ret) {
          break;
        }
      }
    }
    if (((ApicConstants.PROJ_ATTR_USED_FLAG.YES == pidcAttributeVar.getIsUsedEnum()) &&
        (pidcAttributeVar.getAttributeValue() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO == pidcAttributeVar.getIsUsedEnum())) {
      ret = true;
    }
    return ret;
  }

}