/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.SortedSet;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;

/**
 * @author mkl2cob
 */
public abstract class IUseCaseItemClientBO implements IDataObject, Comparable<IUseCaseItemClientBO> {

  /**
   * Initial capacity of tooltip String builder
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;
  /**
   * IUseCaseItem
   */
  protected IUseCaseItem ucItem;


  IUseCaseItemClientBO(final IUseCaseItem ucItem) {
    this.ucItem = ucItem;
  }

  /**
   * @return String
   */
  public String getName() {
    return this.ucItem.getName();
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
   * @return the set of child nodes, that are relevant for focus matrix
   */
  public abstract <U extends IUseCaseItemClientBO> SortedSet<U> getChildFocusMatrixItems();

  /**
   * This method returns the immediate parent of the Use case item
   *
   * @return IUseCaseItemClientBO icdm-368
   */
  public abstract IUseCaseItemClientBO getParent();

  /**
   * ICDM-1092
   *
   * @return sorted set of immediate child usecase items
   */
  public abstract SortedSet<IUseCaseItemClientBO> getChildUCItems();

  /**
   * @return true, if any if its parent is deleted icdm-368
   */
  public boolean isParentLevelDeleted() {
    IUseCaseItemClientBO parentGrp = getParent();
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
   * @return SortedSet<IUseCaseItemClientBO>
   */
  public abstract SortedSet<IUseCaseItemClientBO> getMappableItems();

  /**
   * @return boolean
   */
  public abstract boolean isModifyCellAllowed();

  /**
   * @return true if it is modifiable
   */
  public abstract boolean isModifiable();

  /**
   * @return Long
   */
  public Long getID() {
    return this.ucItem.getId();
  }

  /**
   * Return true if focus matrix content available directly for given attribute while Inserting use case / use case
   * items
   *
   * @param attribute attribute
   * @return boolean
   */
  public abstract boolean isFocusMatrixAvailableWhileMapping(final AttributeClientBO attribute);

  /**
   * @param attribute Attribute
   * @return boolean
   */
  public abstract boolean isFocusMatrixAvailableWhileUnMapping(final AttributeClientBO attribute);


  /**
   * @return the ucItem
   */
  public IUseCaseItem getUcItem() {
    return this.ucItem;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final IUseCaseItemClientBO other) {
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
    if (obj instanceof IUseCaseItemClientBO) {
      return this.ucItem.equals(((IUseCaseItemClientBO) obj).getUcItem());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.ucItem.hashCode();
  }
}
