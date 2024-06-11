/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

import java.util.SortedSet;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;

/**
 * @author dja7cob
 */
public abstract class IUsecaseItemCommonBO implements IDataObject, Comparable<IUsecaseItemCommonBO> {

  /**
   * Initial capacity of tooltip String builder
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;
  /**
   * IUseCaseItem
   */
  protected IUseCaseItem ucItem;

  /**
   * @param useCase
   */
  public IUsecaseItemCommonBO(final IUseCaseItem ucItem) {
    this.ucItem = ucItem;
  }

  /**
   * @return Long
   */
  public Long getID() {
    return this.ucItem.getId();
  }

  /**
   * This method returns the immediate parent of the Use case item
   *
   * @return IUsecaseItemCommonBO icdm-368
   */
  public abstract IUsecaseItemCommonBO getParent();

  /**
   * @return true, if any if its parent is deleted icdm-368
   */
  public boolean isParentLevelDeleted() {
    IUsecaseItemCommonBO parentGrp = getParent();
    while (parentGrp != null) {
      if (parentGrp.isDeleted()) {
        return true;
      }
      parentGrp = parentGrp.getParent();
    }
    return false;
  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.ucItem.isDeleted();
  }

  /**
   * @param ucEditorModel UsecaseEditorModel
   * @param attribute Attribute
   * @return Attribute
   */
  public abstract boolean isMapped(final Attribute attribute);

  /**
   * @return SortedSet<IUsecaseItemCommonBO>
   */
  public abstract SortedSet<IUsecaseItemCommonBO> getMappableItems();

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final IUsecaseItemCommonBO other) {
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
    if (obj instanceof IUsecaseItemCommonBO) {
      return this.ucItem.equals(((IUsecaseItemCommonBO) obj).getUcItem());
    }
    return false;
  }

  /**
   * @return
   */
  public IUseCaseItem getUcItem() {
    return this.ucItem;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.ucItem.hashCode();
  }

  /**
   * @return String
   */
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    toolTip.append("Name : ").append(this.ucItem.getName());

    String desc = this.ucItem.getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }

    toolTip.append("\nFocus Matrix Relevant : ").append(getFocusMatrixRelevantStr());

    if (this.ucItem.isDeleted()) {
      toolTip.append("\nDeleted : ").append("Yes");
    }

    return toolTip.toString();
  }

  /**
   * @return String
   */
  public String getFocusMatrixRelevantStr() {
    String focusMatrixRelvt = ApicConstants.USED_NO_DISPLAY;
    if ((this.ucItem instanceof UseCase) || (this.ucItem instanceof UseCaseSection)) {
      if (isFocusMatrixRelevant(false)) {
        focusMatrixRelvt = ApicConstants.USED_YES_DISPLAY;
      }
      else {
        if (isFocusMatrixRelevant(true)) {
          focusMatrixRelvt = ApicConstants.USED_YES_DISPLAY + "(by inheritance)";
        }
        else {
          focusMatrixRelvt = ApicConstants.USED_NO_DISPLAY;
        }
      }
    }
    return focusMatrixRelvt;
  }

  /**
   * @param checkParent if true then this checks whether the parent is focus matrix relevant
   * @return true, if this node is relevant for Focus Matrix
   */
  public abstract boolean isFocusMatrixRelevant(boolean checkParent);

  /**
   * ICDM-1092
   *
   * @return sorted set of immediate child usecase items
   */
  public abstract SortedSet<IUsecaseItemCommonBO> getChildUCItems();

}
