/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * NAT table input for focus matrix
 *
 * @author mkl2cob
 */
public class FocusMatrixAttribute implements Comparable<FocusMatrixAttribute> {


  /**
   * column index three
   */
  private static final int COLUMN_INDEX_THREE = 3;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @author mkl2cob
   */
  public static enum SortColumn {
                                 /**
                                  * sort column for attribute name
                                  */
                                 ATTRIBUTE_NAME(0),
                                 /**
                                  * sort column for attribute description
                                  */
                                 ATTRIBUTE_DESC(1),
                                 /**
                                  * sort column for attribute value
                                  */
                                 ATTRIBUTE_VALUE(2),
                                 /**
                                  * sort column for usecase items
                                  */
                                 UC_ITEM_COLS(3);

    int colIndex;

    SortColumn(final int colIndex) {
      this.colIndex = colIndex;
    }


    /**
     * @param colIndex int
     * @return SortColumn
     */
    public static SortColumn getType(final int colIndex) {
      SortColumn ret;

      if (colIndex == ApicConstants.COLUMN_INDEX_0) {
        ret = ATTRIBUTE_NAME;
      }
      else if (colIndex == ApicConstants.COLUMN_INDEX_1) {
        ret = ATTRIBUTE_DESC;
      }
      else if (colIndex == ApicConstants.COLUMN_INDEX_2) {
        ret = ATTRIBUTE_VALUE;
      }
      else {
        ret = UC_ITEM_COLS;
      }

      return ret;

    }
  }


  /**
   * Set of use case items
   */
  private final Set<FocusMatrixUseCaseItem> fmUseCaseItemsSet = new TreeSet<>();

  /**
   * List of use case items
   */
  private List<FocusMatrixUseCaseItem> fmUseCaseItemList;


  /**
   * Attribute instance ( one row in the table)
   */
  private final PIDCAttribute pidcAttr;

  // ICDM-2569
  private final Attribute attr;

  // ICDM-2569
  private final Set<FocusMatrixVersionAttr> fmVersAttrSet = new HashSet<>();

  // ICDM-2569
  private final FocusMatrix fMatrix;

  /**
   * @param fMatrix Focus matrix
   * @param pidcAttr PIDCAttribute instnace
   */
  // ICDM-2569
  public FocusMatrixAttribute(final FocusMatrix fMatrix, final PIDCAttribute pidcAttr) {

    this.fMatrix = fMatrix;
    this.pidcAttr = pidcAttr;

    this.attr = pidcAttr.getAttribute();

  }

  /**
   * @param fMatrix Focus matrix
   * @param attr Attribute instnace
   */
  // ICDM-2569
  public FocusMatrixAttribute(final FocusMatrix fMatrix, final Attribute attr) {
    this.fMatrix = fMatrix;
    this.attr = attr;

    this.pidcAttr = null;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixAttribute other) {
    return ApicUtil.compare(getAttribute().getAttributeName(), other.getAttribute().getAttributeName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getAttribute() == null) ? 0 : getAttribute().getAttributeName().hashCode());
   return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FocusMatrixAttribute other = (FocusMatrixAttribute) obj;
    return CommonUtils.isEqual(getAttribute().getAttributeName(), other.getAttribute().getAttributeName());
  }

  /**
   * @param other FocusMatrixAttribute
   * @param column SortColumn
   * @param columnIndex int
   * @return compare result
   */
  public int compareTo(final FocusMatrixAttribute other, final SortColumn column, final int columnIndex) {
    int ret;
    switch (column) {

      case ATTRIBUTE_DESC:
        ret = getAttribute().getDescription().compareTo(other.getAttribute().getDescription());
        break;

      case ATTRIBUTE_VALUE:
        // ICDM-2569
        ret = ApicUtil.compare(getValueDisplay(), other.getValueDisplay());
        break;

      case UC_ITEM_COLS:
        ret = compareUcItem(other, columnIndex);
        break;
      default:
        ret = getAttribute().getName().compareTo(other.getAttribute().getName());
        break;
    }
    return ret;
  }

  /**
   * @return get the display text of the value
   */
  // ICDM-2569
  public String getValueDisplay() {
    if (isHiddenToUser()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    return isWorkingSetFocusMatrix() ? getValueDisplayFromProjAttr() : getValueDisplayFromFmVersAttr();
  }

  /**
   * @return the tooltip of the value
   */
  // ICDM-2569
  public String getValueToolTip() {
    if (isHiddenToUser()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    return isWorkingSetFocusMatrix() ? getValueTooltipFromProjAttr() : getValueTooltipFromFmVersAttr();
  }

  /**
   * @return
   */
  // ICDM-2569
  private String getValueTooltipFromFmVersAttr() {
    return isVariantLevel() ? getFmAttrTooltipFromVar() : getValueTooltipFromFmVersLevelAttr();
  }


  /**
   * @return
   */
  // ICDM-2569
  private String getFmAttrTooltipFromVar() {

    SortedSet<String> ttSet = new TreeSet<>();

    Set<PIDCVariant> varWithoutSvarSet = new HashSet<>();
    for (FocusMatrixVersionAttr fmVersAttr : this.fmVersAttrSet) {
      if (fmVersAttr.getSubVariant() != null) {
        varWithoutSvarSet.add(fmVersAttr.getVariant());
      }
    }

    for (FocusMatrixVersionAttr fmVersAttr : this.fmVersAttrSet) {
      PIDCVariant variant = fmVersAttr.getVariant();
      PIDCSubVariant subVariant = fmVersAttr.getSubVariant();
      String value = fmVersAttr.getValue() == null ? "" : fmVersAttr.getValue().getValue(true);
      if (subVariant == null) {
        if (!varWithoutSvarSet.contains(variant)) {
          ttSet.add(variant.getName() + " \t: " + fmVersAttr.getUsedFlag().getUiType() + " \t: " + value);
        }
      }
      else {
        ttSet.add(variant.getName() + " >> " + subVariant.getName() + " \t: " + fmVersAttr.getUsedFlag().getUiType() +
            " \t: " + value);
      }
    }
    StringJoiner joiner = new StringJoiner("\n");
    joiner.add("Variant\t: Used Flag\t: Value");
    for (String ttip : ttSet) {
      joiner.add(ttip);
    }

    return joiner.toString();
  }

  /**
   * @return
   */
  // ICDM-2569
  private String getValueTooltipFromProjAttr() {
    String tooltip;
    if (this.pidcAttr.isVariant()) {
      SortedSet<String> ttSet = new TreeSet<>();
      for (PIDCVariant var : this.pidcAttr.getPidcVersion().getVariantsSet(false)) {
        PIDCAttributeVar varAttr = var.getAttributes(false).get(this.attr.getID());
        if (varAttr == null) {
          continue;
        }
        if (varAttr.isVariant()) {
          for (PIDCSubVariant subVar : getDataProvider().getPidcVaraint(varAttr.getVariantId())
              .getSubVariantsSet(false)) {
            PIDCAttributeSubVar svarAttr = subVar.getAttributes(false).get(this.attr.getID());
            if (svarAttr == null) {
              continue;
            }
            ttSet.add(var.getName() + " >> " + subVar.getName() + "\t: " + svarAttr.getIsUsed() + "\t: " +
                svarAttr.getDefaultValueDisplayName(true));
          }
        }
        else {
          ttSet.add(var.getName() + "\t: " + varAttr.getIsUsed() + "\t: " + varAttr.getDefaultValueDisplayName(true));
        }
      }
      StringJoiner joiner = new StringJoiner("\n");
      joiner.add("Variant\t: Used Flag\t: Value");
      for (String ttip : ttSet) {
        joiner.add(ttip);
      }

      tooltip = joiner.toString();

    }
    else {
      tooltip = "Used : " + this.pidcAttr.getIsUsed() + "\tValue : " + this.pidcAttr.getDefaultValueDisplayName(true);
    }
    return tooltip;

  }

  /**
   * @return apic data provider
   */
  private ApicDataProvider getDataProvider() {
    return this.attr.getDataCache().getDataProvider();
  }

  /**
   * @return true if attrbute is defined at variant level
   */
  public boolean isVariantLevel() {
    return isWorkingSetFocusMatrix() ? this.pidcAttr.isVariant() : this.fmVersAttrSet.size() > 1;
  }

  /**
   * FM value tooltip for history rivision when attribute was defined at 'PIDC Version' level
   *
   * @return value tooltip
   */
  private String getValueTooltipFromFmVersLevelAttr() {
    FocusMatrixVersionAttr versAttr = this.fmVersAttrSet.iterator().next();
    AttributeValue attrVal = versAttr.getValue();

    return "Used : " + versAttr.getUsedFlag().getUiType() + "\tValue : " +
        (attrVal == null ? "" : attrVal.getValue(true));
  }

  /**
   * @return
   */
  private String getValueDisplayFromFmVersAttr() {
    if (isVariantLevel()) {
      return ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
    }
    AttributeValue attrVal = this.fmVersAttrSet.iterator().next().getValue();

    return attrVal == null ? "" : attrVal.getValue(true);
  }

  /**
   * @return true if attribute details are hidden to the current user
   */
  public boolean isHiddenToUser() {
    if (isWorkingSetFocusMatrix()) {
      return this.pidcAttr.isHiddenToUser();
    }
    // Find the PIDC attribute corresponding to the fm attibute, and return the staus
    PIDCAttribute fmPidcAttr = this.fMatrix.getPidcVersion().getAttributes().get(this.attr.getID());
    if (fmPidcAttr != null) {
      return fmPidcAttr.isHiddenToUser();
    }
    return false;
  }

  /**
   * @return true if the attribute is marked as applicable for focus matrix
   */
  public boolean isFocusMatrixApplicable() {
    return isWorkingSetFocusMatrix() ? this.pidcAttr.isFocusMatrixApplicable() : true;
  }

  /**
   * @return true, if attribute is visisble in the project version
   */
  public boolean isVisible() {
    return isWorkingSetFocusMatrix() ? this.pidcAttr.isVisible() : true;
  }

  /**
   * @return
   */
  private String getValueDisplayFromProjAttr() {
    return this.pidcAttr.getDefaultValueDisplayName(true);
  }

  private boolean isWorkingSetFocusMatrix() {
    return this.fMatrix.getFocusMatrixVersion().isWorkingSet();
  }

  /**
   * @param other
   * @param columnIndex
   * @return
   */
  private int compareUcItem(final FocusMatrixAttribute other, final int columnIndex) {
    int ret;
    FocusMatrixUseCaseItem ucItem1 = getFocusmatrixUseCaseItem(columnIndex - COLUMN_INDEX_THREE);
    FocusMatrixUseCaseItem ucItem2 = other.getFocusmatrixUseCaseItem(columnIndex - COLUMN_INDEX_THREE);

    boolean isMapped1 = ucItem1.getUseCaseItem().isMapped(getAttribute()) || ucItem1.isMapped(getAttribute());
    boolean isMapped2 =
        ucItem2.getUseCaseItem().isMapped(other.getAttribute()) || ucItem2.isMapped(other.getAttribute());


    String colorCode1 = ucItem1.getColorCode(getAttribute()).getColor();
    String colorCode2 = ucItem2.getColorCode(other.getAttribute()).getColor();

    ret = ApicUtil.compare(isMapped1, isMapped2);

    if (ret == 0) {
      ret = ApicUtil.compare(FocusMatrixColorCode.getColor(colorCode1).getOrder(),
          FocusMatrixColorCode.getColor(colorCode2).getOrder());
    }
    return ret;
  }

  /**
   * @return the fmUseCaseItemsSet
   */
  public Set<FocusMatrixUseCaseItem> getFmUseCaseItemsSet() {
    return this.fmUseCaseItemsSet;
  }

  /**
   * @param colIndex index of the particular column
   * @return AbstractUseCaseItem
   */
  public FocusMatrixUseCaseItem getFocusmatrixUseCaseItem(final int colIndex) {
    return getFmUseCaseItemList().get(colIndex);
  }


  /**
   * @return the fmUseCaseItemList
   */
  public List<FocusMatrixUseCaseItem> getFmUseCaseItemList() {
    if (this.fmUseCaseItemList == null) {
      this.fmUseCaseItemList = new ArrayList<>(this.fmUseCaseItemsSet);
    }
    return this.fmUseCaseItemList;
  }


  /**
   * @return the pidcAttr
   */
  public PIDCAttribute getPidcAttr() {
    return this.pidcAttr;
  }

  /**
   * @return the pidcAttr
   */
  public Attribute getAttribute() {
    return this.attr;
  }

  /**
   * @return the fmVersAttrSet
   */
  // ICDM-2569
  Set<FocusMatrixVersionAttr> getFmVersAttrSet() {
    return this.fmVersAttrSet;
  }

}
