/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.ProjectVariantConsInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;


/**
 * Project variant consistency evaluation
 *
 * @author bne4cob
 */
public class VariantEvaluator implements IValidator<ProjectVariantConsInfo> {

  /**
   * PIDC Variant Entity
   */
  private final TabvProjectVariant dbVariant;

  /**
   * Resultset
   */
  private final SortedSet<ProjectVariantConsInfo> resultSet = new TreeSet<>();

  /**
   * Attributes defined at variant level in parent PIDC Version
   */
  private final ConcurrentMap<Long, String> varAttrIDMap;

  /**
   * All variant attributes of this variant
   */
  private final ConcurrentMap<Long, String> allVarAttrIDMap = new ConcurrentHashMap<>();

  /**
   * All variant attribute entities of this variant
   */
  private final ConcurrentMap<Long, TabvVariantsAttr> allVarAttrEntMap = new ConcurrentHashMap<>();

  /**
   * All attributes of this variant defined at sub-variant level
   */
  private final ConcurrentMap<Long, String> svarAttrIDMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @param dbVariant PIDC Variant Entity
   * @param varAttrIDMap Attributes defined at variant level in parent PIDC Version
   */
  public VariantEvaluator(final TabvProjectVariant dbVariant, final ConcurrentMap<Long, String> varAttrIDMap) {
    this.dbVariant = dbVariant;
    this.varAttrIDMap = varAttrIDMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {

    findAttributes();

    validateVarAttrs();

    for (TabvProjectSubVariant dbSubVariant : this.dbVariant.getTabvProjectSubVariants()) {
      SubvariantEvaluator varEval = new SubvariantEvaluator(dbSubVariant, this.svarAttrIDMap);
      varEval.validate();
      this.resultSet.addAll(varEval.getResult());
    }

  }

  /**
   * Validate variant attributes
   */
  private void validateVarAttrs() {
    ProjectVariantConsInfo invalidItem;

    // Missing attribute definitions
    for (Entry<Long, String> missingEntry : CommonUtils.getDifference(this.varAttrIDMap, this.allVarAttrIDMap)
        .entrySet()) {
      invalidItem = getNewProjectVariantInfo(missingEntry.getKey(), missingEntry.getValue(), ErrorType.ATTR_MISSING);
      this.resultSet.add(invalidItem);
    }

    // Extra attribute definitions
    for (Entry<Long, String> extraEntry : CommonUtils.getDifference(this.allVarAttrIDMap, this.varAttrIDMap)
        .entrySet()) {
      invalidItem = getNewProjectVariantInfo(extraEntry.getKey(), extraEntry.getValue(), ErrorType.ATTR_EXTRA);
      this.resultSet.add(invalidItem);
    }

  }

  /**
   * Create error object
   *
   * @param attrID attr ID
   * @param attrName attr Name
   * @param errorType error Type
   * @return new ProjectVariantInfo
   */
  private ProjectVariantConsInfo getNewProjectVariantInfo(final Long attrID, final String attrName,
      final ErrorType errorType) {
    ProjectVariantConsInfo invalidItem = new ProjectVariantConsInfo();

    invalidItem.setAttrID(attrID);
    invalidItem.setAttrName(attrName);

    invalidItem.setVariantID(this.dbVariant.getVariantId());
    invalidItem.setVariantName(this.dbVariant.getTabvAttrValue().getTextvalueEng());

    invalidItem.setPidcVersID(this.dbVariant.getTPidcVersion().getPidcVersId());
    invalidItem.setPidcVersName(this.dbVariant.getTPidcVersion().getVersName());

    invalidItem.setProjectID(this.dbVariant.getTPidcVersion().getTabvProjectidcard().getProjectId());
    invalidItem
        .setProjectName(this.dbVariant.getTPidcVersion().getTabvProjectidcard().getTabvAttrValue().getTextvalueEng());

    invalidItem.setErrorType(errorType);
    invalidItem.setErrorLevel(ErrorLevel.VARIANT_ATTR);

    if (errorType == ErrorType.ATTR_EXTRA) {
      TabvVariantsAttr dbVarAttr = this.allVarAttrEntMap.get(attrID);
      invalidItem.setCreatedUser(dbVarAttr.getCreatedUser());
      invalidItem.setCreatedDate(ApicUtil.timestamp2calendar(dbVarAttr.getCreatedDate()));
      invalidItem.setLastModifiedUser(dbVarAttr.getModifiedUser());
      invalidItem.setLastModifiedDate(ApicUtil.timestamp2calendar(dbVarAttr.getModifiedDate()));
    }

    // Set parent variant's details
    invalidItem.setCreatedUserParent(this.dbVariant.getCreatedUser());
    invalidItem.setCreatedDateParent(ApicUtil.timestamp2calendar(this.dbVariant.getCreatedDate()));
    invalidItem.setLastModifiedUserParent(this.dbVariant.getModifiedUser());
    invalidItem.setLastModifiedDateParent(ApicUtil.timestamp2calendar(this.dbVariant.getModifiedDate()));

    return invalidItem;
  }

  /**
   * Find attributes defined for the variant
   */
  private void findAttributes() {
    TabvAttribute dbAttr;
    for (TabvVariantsAttr dbVarAttr : this.dbVariant.getTabvVariantsAttrs()) {
      dbAttr = dbVarAttr.getTabvAttribute();
      this.allVarAttrIDMap.put(dbAttr.getAttrId(), dbAttr.getAttrNameEng());
      this.allVarAttrEntMap.put(dbAttr.getAttrId(), dbVarAttr);
      if (CommonUtilConstants.CODE_YES.equals(dbVarAttr.getIsSubVariant())) {
        this.svarAttrIDMap.put(dbAttr.getAttrId(), dbAttr.getAttrNameEng());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<ProjectVariantConsInfo> getResult() {
    return this.resultSet;
  }

}
