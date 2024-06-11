/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Super class for all Use case items
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractUseCaseItem extends ApicObject implements Comparable<AbstractUseCaseItem> {

  /**
   * Deleted Flag
   */
  private static final String FLD_DEL_FLAG = "DELETED_FLAG";

  /**
   * Name English
   */
  private static final String FLD_NAME_ENG = "NAME_ENG";

  /**
   * Name German
   */
  private static final String FLD_NAME_GER = "NAME_GER";

  /**
   * Description English
   */
  private static final String FLD_DSC_ENG = "DESC_ENG";

  /**
   * Description German
   */
  private static final String FLD_DSC_GER = "DESC_GER";

  /**
   * Focus Matrix relevance flag
   */
  private static final String FLD_FOCUS_MATRIX = "FOCUS_MATRIX_FLAG";

  /**
   * Initial capacity of tooltip String builder
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param itemID unique id of this Use Case Item
   */
  public AbstractUseCaseItem(final ApicDataProvider apicDataProvider, final Long itemID) {
    super(apicDataProvider, itemID);
  }

  /**
   * @return the name of the item
   */
  @Override
  public final String getName() {

    String returnValue;

    if (getDataCache().getLanguage() == Language.GERMAN) {
      returnValue = getNameGer();
      if (CommonUtils.isEmptyString(returnValue)) {
        returnValue = getNameEng();
      }
    }
    else {
      returnValue = getNameEng();
    }

    return returnValue;

  }

  /**
   * @return the description of the item
   */
  @Override
  public final String getDescription() {


    String returnValue;

    if (getDataCache().getLanguage() == Language.GERMAN) {
      returnValue = getDescGer();
      if (returnValue == null) {
        returnValue = getDescEng();
      }

    }
    else {
      returnValue = getDescEng();
    }

    return returnValue;

  }

  /**
   * Check, if the ID is valid. The ID is valid if the related database entity is available.
   *
   * @return TRUE if the ID is valid
   */
  protected abstract boolean isIdValid();


  /**
   * @return Name in English
   */
  public abstract String getNameEng();

  /**
   * @return Name in German
   */
  public abstract String getNameGer();

  /**
   * @return Description in English
   */
  public abstract String getDescEng();

  /**
   * @return Description in German
   */
  public abstract String getDescGer();

  /**
   * @return Last confirmation date
   */
  public abstract Calendar getLastConfirmationDate();

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final AbstractUseCaseItem other) {
    int compResult = ApicUtil.compare(getName(), other.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), other.getID());
    }
    return compResult;
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
   * Checks whether the given attribute is mapped to this use case item or its children. The deleted children of this
   * items are not considered.
   *
   * @param attr the attribute to be checked
   * @return true/false
   */
  public abstract boolean isMapped(final Attribute attr);

  /**
   * Checks whether attributes can be mapped to this object.
   *
   * @return true/false
   */
  protected abstract boolean canMapAttributes();

  /**
   * Returns the set of use case items that can be mapped to an attribute. The collection will contain either this
   * object or the valid child objects.
   *
   * @return use case items as a sorted set
   */
  public abstract SortedSet<AbstractUseCaseItem> getMappableItems();

  /**
   * Returns whether this object is deleted or not by checking the DELETED_FLAG field
   *
   * @return true/false
   */
  public abstract boolean isDeleted();

  /**
   * @return true/false
   */
  public abstract boolean isUpToDate();

  /**
   * This method returns whethere all usecase items are mapped with attribute or not
   *
   * @param attribute instance
   * @return boolean
   */
  // ICDM-301
  protected abstract boolean isAllUCItemsMapped(final Attribute attribute);

  /**
   * This method returns whethere all usecase items are un mapped with attribute or not
   *
   * @param attribute instance
   * @return boolean
   */
  // ICDM-301
  protected abstract boolean isAllUCItemsUnMapped(final Attribute attribute);

  /**
   * This method returns whethere any usecase item is mapped with attribute or not
   *
   * @param attribute instance
   * @return boolean
   */
  // ICDM-301
  protected abstract boolean isAnyUCItemMapped(final Attribute attribute);

  /**
   * This method returns the immediate parent of the Use case item
   *
   * @return AbstractUseCaseItem icdm-368
   */
  public abstract AbstractUseCaseItem getParent();

  /**
   * @return true, if any if its parent is deleted icdm-368
   */
  protected boolean isParentLevelDeleted() {
    AbstractUseCaseItem parentGrp = getParent();
    while (parentGrp != null) {
      if (parentGrp.isDeleted()) {
        return true;
      }
      parentGrp = parentGrp.getParent();
    }
    return false;
  }

  /**
   * @return true or false if the attributes are editable Main implementation done in Use case and Use case section For
   *         Use case Group it is always false
   */
  public abstract boolean isMappingModifiable();

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return CommonUtils.concatenate(this.getClass().getName(), " [ID=", getID(), ", Name=", getName(), ", Description=",
        getDescription(), "]");
  }

  /**
   * ICDM-1092
   *
   * @return sorted set of immediate child usecase items
   */
  public abstract SortedSet<AbstractUseCaseItem> getChildUCItems();

  /**
   * @param checkParent if true then this checks whether the parent is focus matrix relevant
   * @return true, if this node is relevant for Focus Matrix
   */
  public abstract boolean isFocusMatrixRelevant(boolean checkParent);

  /**
   * @return the set of child nodes, that are relevant for focus matrix
   */
  public abstract <U extends AbstractUseCaseItem> SortedSet<U> getChildFocusMatrixItems();


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final ConcurrentMap<String, String> objDetails = new ConcurrentHashMap<String, String>();

    objDetails.put(FLD_DEL_FLAG, String.valueOf(isDeleted()));
    objDetails.put(FLD_NAME_ENG, CommonUtils.checkNull(getNameEng()));
    objDetails.put(FLD_NAME_GER, CommonUtils.checkNull(getNameGer()));
    objDetails.put(FLD_DSC_ENG, CommonUtils.checkNull(getDescEng()));
    objDetails.put(FLD_DSC_GER, CommonUtils.checkNull(getDescGer()));
    objDetails.put(FLD_FOCUS_MATRIX, isFocusMatrixRelevant(false) ? ApicConstants.YES : ApicConstants.CODE_NO);


    return objDetails;
  }

  /**
   * Return true if focus matrix content available directly for given attribute while Inserting use case / use case
   * items
   *
   * @param attribute attribute
   * @return boolean
   */
  public abstract boolean isFocusMatrixAvailableWhileMapping(final Attribute attribute);

  /**
   * Return true if focus matrix content available directly for given attribute while Deleting use case / use case items
   *
   * @param attribute attribute
   * @return boolean
   */
  public abstract boolean isFocusMatrixAvailableWhileUnMapping(final Attribute attribute);

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    toolTip.append("Name : ").append(getName());

    String desc = getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }

    toolTip.append("\nFocus Matrix Relevant : ").append(isFocusMatrixRelevantStr());

    return toolTip.toString();
  }

  /**
   * @return String representation of focus matrix relevance
   */
  public String isFocusMatrixRelevantStr() {
    String relev;
    if (isFocusMatrixRelevant(false)) {
      relev = ApicConstants.USED_YES_DISPLAY;
    }
    else {
      if (isFocusMatrixRelevant(true)) {
        relev = ApicConstants.USED_YES_DISPLAY + "(by inheritance)";
      }
      else {
        relev = ApicConstants.USED_NO_DISPLAY;
      }
    }

    return relev;

  }

  // ICDM-2610
  /**
   * Checks the validation for editing the value in use case editor
   *
   * @return true, the cell value can be modified for Use Case / Use Case Section
   */
  public abstract boolean isModifyCellAllowed();


}
