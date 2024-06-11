/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.ProjectSubvariantConsInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;


/**
 * Project sub-variant consistency evaluation
 *
 * @author bne4cob
 */
public class SubvariantEvaluator implements IValidator<ProjectSubvariantConsInfo> {

  /**
   * PIDC Sub-Variant Entity
   */
  private final TabvProjectSubVariant dbSubVariant;

  /**
   * Resultset
   */
  private final SortedSet<ProjectSubvariantConsInfo> resultSet = new TreeSet<>();

  /**
   * Attributes defined at subvariant level in parent PIDC Variant
   */
  private final ConcurrentMap<Long, String> svarAttrIDMap;

  /**
   * All variant attributes of this variant
   */
  private final ConcurrentMap<Long, String> allSubVarAttrIDMap = new ConcurrentHashMap<>();

  /**
   * All sub-variant attribute entities of this sub-variant
   */
  private final ConcurrentMap<Long, TabvProjSubVariantsAttr> allSubVarAttrEntMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @param dbSubVariant PIDC Sub-Variant Entity
   * @param svarAttrIDMap Attributes defined at subvariant level in parent PIDC Variant
   */
  public SubvariantEvaluator(final TabvProjectSubVariant dbSubVariant,
      final ConcurrentMap<Long, String> svarAttrIDMap) {
    this.dbSubVariant = dbSubVariant;
    this.svarAttrIDMap = svarAttrIDMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {
    findAttributes();
    validateSvarAttrs();
  }

  /**
   * Validate sub variant attributes
   */
  private void validateSvarAttrs() {
    ProjectSubvariantConsInfo invalidItem;

    // Missing attribute definitions
    for (Entry<Long, String> missingEntry : CommonUtils.getDifference(this.svarAttrIDMap, this.allSubVarAttrIDMap)
        .entrySet()) {
      invalidItem = getNewProjectSubvariantInfo(missingEntry.getKey(), missingEntry.getValue(), ErrorType.ATTR_MISSING);
      this.resultSet.add(invalidItem);
    }

    // Extra attribute definitions
    for (Entry<Long, String> extraEntry : CommonUtils.getDifference(this.allSubVarAttrIDMap, this.svarAttrIDMap)
        .entrySet()) {
      invalidItem = getNewProjectSubvariantInfo(extraEntry.getKey(), extraEntry.getValue(), ErrorType.ATTR_EXTRA);
      this.resultSet.add(invalidItem);
    }

  }

  /**
   * Create error object
   *
   * @param attrID attr ID
   * @param attrName attr Name
   * @param errorType error Type
   * @return new ProjectSubvariantInfo
   */
  private ProjectSubvariantConsInfo getNewProjectSubvariantInfo(final Long attrID, final String attrName,
      final ErrorType errorType) {
    ProjectSubvariantConsInfo invalidItem = new ProjectSubvariantConsInfo();

    invalidItem.setAttrID(attrID);
    invalidItem.setAttrName(attrName);

    invalidItem.setSubvariantID(this.dbSubVariant.getSubVariantId());
    invalidItem.setSubvariantName(this.dbSubVariant.getTabvAttrValue().getTextvalueEng());

    invalidItem.setVariantID(this.dbSubVariant.getTabvProjectVariant().getVariantId());
    invalidItem.setVariantName(this.dbSubVariant.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng());

    invalidItem.setPidcVersID(this.dbSubVariant.getTPidcVersion().getPidcVersId());
    invalidItem.setPidcVersName(this.dbSubVariant.getTPidcVersion().getVersName());

    invalidItem.setProjectID(this.dbSubVariant.getTPidcVersion().getTabvProjectidcard().getProjectId());
    invalidItem.setProjectName(
        this.dbSubVariant.getTPidcVersion().getTabvProjectidcard().getTabvAttrValue().getTextvalueEng());

    invalidItem.setErrorType(errorType);
    invalidItem.setErrorLevel(ErrorLevel.SUB_VARIANT_ATTR);

    if (errorType == ErrorType.ATTR_EXTRA) {
      TabvProjSubVariantsAttr dbVarAttr = this.allSubVarAttrEntMap.get(attrID);
      invalidItem.setCreatedUser(dbVarAttr.getCreatedUser());
      invalidItem.setCreatedDate(ApicUtil.timestamp2calendar(dbVarAttr.getCreatedDate()));
      invalidItem.setLastModifiedUser(dbVarAttr.getModifiedUser());
      invalidItem.setLastModifiedDate(ApicUtil.timestamp2calendar(dbVarAttr.getModifiedDate()));
    }

    // Set the parent sub-variant details
    invalidItem.setCreatedUserParent(this.dbSubVariant.getCreatedUser());
    invalidItem.setCreatedDateParent(ApicUtil.timestamp2calendar(this.dbSubVariant.getCreatedDate()));
    invalidItem.setLastModifiedUserParent(this.dbSubVariant.getModifiedUser());
    invalidItem.setLastModifiedDateParent(ApicUtil.timestamp2calendar(this.dbSubVariant.getModifiedDate()));

    return invalidItem;
  }

  /**
   * Find attributes defined for the sub-variant
   */
  private void findAttributes() {
    TabvAttribute dbAttr;
    for (TabvProjSubVariantsAttr dbSvarAttr : this.dbSubVariant.getTabvProjSubVariantsAttrs()) {
      dbAttr = dbSvarAttr.getTabvAttribute();
      this.allSubVarAttrIDMap.put(dbAttr.getAttrId(), dbAttr.getAttrNameEng());
      this.allSubVarAttrEntMap.put(dbAttr.getAttrId(), dbSvarAttr);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<ProjectSubvariantConsInfo> getResult() {
    return this.resultSet;
  }

}
