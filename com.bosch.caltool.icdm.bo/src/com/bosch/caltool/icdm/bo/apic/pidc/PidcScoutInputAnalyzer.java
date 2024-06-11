/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;

class PidcScoutInputAnalyzer extends AbstractSimpleBusinessObject {

  /**
   * Type of search input
   *
   * @author bne4cob
   */
  enum SCOUT_INPUT_TYPE {
                         /**
                          * no conditions
                          */
                         NO_CONDITIONS(false),
                         /**
                          * contains used flag 'not defined' or NEW
                          */
                         NOT_DEFINED(false),
                         /**
                          * contains used flag YES or NO or values
                          */
                         ATTR_IDS(true),
                         /**
                          * contains only used flag NO
                          */
                         USED_NO(true),
                         /**
                          * contains only used flag YES
                          */
                         USED_YES(true),
                         /**
                          * contains only values
                          */
                         VALUE_IDS(true),
                         /**
                          * contains only boolean values
                          */
                         BOOLEAN_ONLY(true);


    private boolean conditionBased;

    SCOUT_INPUT_TYPE(final boolean conditionBased) {
      this.conditionBased = conditionBased;
    }

    /**
     * @return the conditionBased
     */
    public boolean isConditionBased() {
      return this.conditionBased;
    }

  }

  private final Set<PidcSearchCondition> refinedConditions;

  private SCOUT_INPUT_TYPE inputType;

  private PidcSearchCondition pidcNameCondition;

  private PidcSearchCondition varNameCondition;

  private PidcSearchCondition svarNameCondition;

  private final Set<Long> booleanAttrIds = new HashSet<>();


  PidcScoutInputAnalyzer(final ServiceData serviceData, final PidcSearchInput input) {
    super(serviceData);
    this.refinedConditions = new HashSet<>(input.getSearchConditions());
  }

  public void analyze() throws DataException {

    if (this.refinedConditions.isEmpty()) {
      this.inputType = SCOUT_INPUT_TYPE.NO_CONDITIONS;
      return;
    }
    validateIds();
    checkNameAttributes();
    loadBooleanConditions();
    boolean notDef = false;
    boolean usedYes = false;
    boolean usedNo = false;
    boolean values = false;
    for (PidcSearchCondition con : this.refinedConditions) {
      // skip conditions boolean type attributes since they are handled in @link PidcVersionSearch
      if (checkIfBooleanAttr(con)) {
        continue;
      }
      if (PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equals(con.getUsedFlag()) ||
          PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType().equals(con.getUsedFlag())) {
        notDef = true;
      }
      else if (PROJ_ATTR_USED_FLAG.NO.getUiType().equals(con.getUsedFlag())) {
        usedNo = true;
      }
      else if (PROJ_ATTR_USED_FLAG.YES.getUiType().equals(con.getUsedFlag())) {
        usedYes = true;
      }
      else if (con.getUsedFlag() != null) {
        // Unknown used flag
        throw new InvalidInputException("Invalid used flag in search condition - " + con.getUsedFlag());
      }
      else if (con.getAttributeValueIds().isEmpty()) {
        // Unknown condition
        throw new InvalidInputException("Incomplete search condition. Either used flag or value should be provided.");
      }
      else {
        values = true;
      }

    }

    if (isNameAttrFound()) {
      if (!this.refinedConditions.isEmpty()) {
        this.inputType = calculateInputType(notDef, usedYes, usedNo, values);
      }
    }
    else {
      this.inputType = calculateInputType(notDef, usedYes, usedNo, values);
    }

    getLogger().debug("Search input type = {}", this.inputType);

  }

  private boolean checkIfBooleanAttr(final PidcSearchCondition con) {
    return this.booleanAttrIds.contains(con.getAttributeId());
  }

  private void validateIds() throws InvalidInputException {
    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    AttributeValueLoader valLdr = new AttributeValueLoader(getServiceData());

    for (PidcSearchCondition con : this.refinedConditions) {
      attrLdr.validateId(con.getAttributeId());
      for (Long vId : con.getAttributeValueIds()) {
        valLdr.validateId(vId);
      }
    }
  }

  private void checkNameAttributes() throws DataException {
    Iterator<PidcSearchCondition> iterator = this.refinedConditions.iterator();
    while (iterator.hasNext()) {
      PidcSearchCondition con = iterator.next();

      Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(con.getAttributeId());
      int attrLevel = attr.getLevel() == null ? 0 : attr.getLevel().intValue();

      boolean notDef = PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equals(con.getUsedFlag()) ||
          PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType().equals(con.getUsedFlag());

      if (attrLevel == ApicConstants.PROJECT_NAME_ATTR) {
        checkPidcNameAttr(con, attr);
      }
      else if (attrLevel == ApicConstants.VARIANT_CODE_ATTR) {
        checkVarNameAttr(con, notDef, attr);
      }
      else if (attrLevel == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        checkSubVarNameAttr(con, notDef, attr);
      }

      if (isNameAttrFound()) {
        // If this is a name attribute condition, it can be removed from the compplete conditions list, since specific
        // queries are written for name attributes
        iterator.remove();
      }
    }

  }

  private boolean isNameAttrFound() {
    return isPidcNameIncluded() || isVarNameIncluded() || isSvarNameIncluded();
  }

  private void checkSubVarNameAttr(final PidcSearchCondition con, final boolean notDef, final Attribute attr)
      throws InvalidInputException {
    if (notDef) {
      throw new InvalidInputException("Used flag cannot be '???' for '" + attr.getName() + "'");
    }
    if (isPidcNameIncluded() || isVarNameIncluded()) {
      throw new InvalidInputException(
          "Only one search condition should be present with " + attr.getName() + " attribute");
    }

    this.svarNameCondition = con;
    getLogger().debug("Sub-Variant Name is part of input conditions :{}", con);
  }

  private void checkVarNameAttr(final PidcSearchCondition con, final boolean notDef, final Attribute attr)
      throws InvalidInputException {
    if (notDef) {
      throw new InvalidInputException("Used flag cannot be '???' for '" + attr.getName() + "' attribute.");
    }
    if (isPidcNameIncluded() || isSvarNameIncluded()) {
      throw new InvalidInputException("Only one condition should be present with '" + attr.getName() + "' attribute");
    }

    this.varNameCondition = con;
    getLogger().debug("Variant Name is part of input conditions :{}", con);
  }

  /**
   * @param con
   * @param values
   * @param attr
   * @throws InvalidInputException
   */
  private void checkPidcNameAttr(final PidcSearchCondition con, final Attribute attr) throws InvalidInputException {
    if (con.getAttributeValueIds().isEmpty()) {
      throw new InvalidInputException(
          "For '" + attr.getName() + "' attribute, only value(s) must be provided in condition");
    }
    if (isVarNameIncluded() || isSvarNameIncluded()) {
      throw new InvalidInputException("Only one condition should be present with '" + attr.getName() + "' attribute");
    }

    this.pidcNameCondition = con;
    getLogger().debug("Pidc Name is part of input conditions :{}", con);
  }

  private SCOUT_INPUT_TYPE calculateInputType(final boolean notDef, final boolean usedYes, final boolean usedNo,
      final boolean values) {

    if (notDef) {
      return SCOUT_INPUT_TYPE.NOT_DEFINED;
    }
    if (usedYes && !usedNo && !values) {
      return SCOUT_INPUT_TYPE.USED_YES;
    }
    if (!usedYes && usedNo && !values) {
      return SCOUT_INPUT_TYPE.USED_NO;
    }
    if (!usedYes && !usedNo && values) {
      return SCOUT_INPUT_TYPE.VALUE_IDS;
    }
    // It will enter only if the values is false
    if (!usedYes && !usedNo) {
      return SCOUT_INPUT_TYPE.BOOLEAN_ONLY;
    }
    return SCOUT_INPUT_TYPE.ATTR_IDS;
  }


  /**
   * @return the inputType
   */
  public SCOUT_INPUT_TYPE getInputType() {
    return this.inputType;
  }

  /**
   * @param inputType the inputType to set
   */
  public void setInputType(final SCOUT_INPUT_TYPE inputType) {
    this.inputType = inputType;
  }

  /**
   * @return the pidcNameIncluded
   */
  public boolean isPidcNameIncluded() {
    return this.pidcNameCondition != null;
  }


  /**
   * @return the varNameIncluded
   */
  public boolean isVarNameIncluded() {
    return this.varNameCondition != null;
  }


  /**
   * @return the svarNameIncluded
   */
  public boolean isSvarNameIncluded() {
    return this.svarNameCondition != null;
  }


  /**
   * @return the pidcNameCondition
   */
  PidcSearchCondition getPidcNameCondition() {
    return this.pidcNameCondition;
  }


  /**
   * @return the varNameCondition
   */
  PidcSearchCondition getVarNameCondition() {
    return this.varNameCondition;
  }


  /**
   * @return the svarNameCondition
   */
  PidcSearchCondition getSvarNameCondition() {
    return this.svarNameCondition;
  }

  /**
   * @return the conditions
   */
  public Set<PidcSearchCondition> getRefinedConditions() {
    return new HashSet<>(this.refinedConditions);
  }

  /**
   */
  private void loadBooleanConditions() {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    this.refinedConditions.forEach(condition -> {
      try {
        if (attrLoader.getDataObjectByID(condition.getAttributeId()).getValueType()
            .equals(AttributeValueType.BOOLEAN.getDisplayText())) {
          this.booleanAttrIds.add(condition.getAttributeId());
        }
      }
      catch (DataException exp) {
        getLogger().error(exp.getLocalizedMessage(), exp);
      }
    });

  }

  /**
   * @return the booleanAttrIds
   */
  public Set<Long> getBooleanAttrIds() {
    return new HashSet<>(this.booleanAttrIds);
  }
}