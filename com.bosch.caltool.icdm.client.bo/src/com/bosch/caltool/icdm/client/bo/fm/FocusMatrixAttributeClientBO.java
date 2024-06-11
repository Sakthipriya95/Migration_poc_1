/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * NAT table input for focus matrix
 *
 * @author mkl2cob
 */
public class FocusMatrixAttributeClientBO implements Comparable<FocusMatrixAttributeClientBO> {


  /**
   * 
   */
  private static final String TAB = " \t: ";
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
  private final PidcVersionAttributeBO pidcAttr;

  // ICDM-2569
  // attribute instance
  private final Attribute attr;

  // ICDM-2569
  // focus matrix attribute set
  private final Set<FocusMatrixVersionAttrClientBO> fmVersAttrSet = new HashSet<>();

  // ICDM-2569
  // instnace of focus matrix version client bo
  private final FocusMatrixVersionClientBO fMatrix;
  // instance of data handler
  private final FocusMatrixDataHandler fmDataHandler;

  /**
   * @param fMatrix Focus matrix
   * @param pidcVersionAttribute PIDCAttribute instnace
   * @param attr Attribute
   * @param fmDataHandler FocusMatrixDataHandler
   */
  // ICDM-2569
  public FocusMatrixAttributeClientBO(final FocusMatrixVersionClientBO fMatrix,
      final PidcVersionAttribute pidcVersionAttribute, final Attribute attr,
      final FocusMatrixDataHandler fmDataHandler) {

    this.fMatrix = fMatrix;

    this.pidcAttr = new PidcVersionAttributeBO(pidcVersionAttribute, fmDataHandler.getPidcVersionBO());

    this.attr = attr;
    this.fmDataHandler = fmDataHandler;

  }

  /**
   * @param fMatrix Focus matrix
   * @param attr Attribute instnace
   */
  // ICDM-2569
  public FocusMatrixAttributeClientBO(final FocusMatrixVersionClientBO fMatrix, final Attribute attr,
      final FocusMatrixDataHandler fmDataHandler) {
    this.fMatrix = fMatrix;
    this.attr = attr;

    this.fmDataHandler = fmDataHandler;
    this.pidcAttr = null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixAttributeClientBO other) {
    return ApicUtil.compare(getAttribute().getName(), other.getAttribute().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getAttribute() == null) ? 0 : getAttribute().getName().hashCode());
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
    FocusMatrixAttributeClientBO other = (FocusMatrixAttributeClientBO) obj;
    return CommonUtils.isEqual(getAttribute().getName(), other.getAttribute().getName());
  }

  /**
   * @param other FocusMatrixAttribute
   * @param column SortColumn
   * @param columnIndex int
   * @return compare result
   */
  public int compareTo(final FocusMatrixAttributeClientBO other, final SortColumn column, final int columnIndex) {
    int ret;
    switch (column) {
      case ATTRIBUTE_DESC:
        ret = ApicUtil.compare(getAttribute().getDescription(), other.getAttribute().getDescription());
        break;

      case ATTRIBUTE_VALUE:
        // ICDM-2569
        ret = ApicUtil.compare(getValueDisplay(), other.getValueDisplay());
        break;

      case UC_ITEM_COLS:
        ret = compareUcItem(other, columnIndex);
        break;
      default:
        ret = ApicUtil.compare(getAttribute().getName(), other.getAttribute().getName());
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
   * @return get the display text of the value
   */
  // ICDM-2569
  public String getRemarksDisplay() {
    if (isHiddenToUser()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    return isWorkingSetFocusMatrix() ? getRemarkDisplayFromProjAttr() : getRemarkDisplayFromFmVersAttr();
  }

  /**
   * Gives the remark display for version attribute
   * 
   * @return the remark
   */
  private String getRemarkDisplayFromFmVersAttr() {
    if (isVariantLevel()) {
      return ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
    }
    String attrVal = this.fmVersAttrSet.iterator().next().getRemarks();
    return attrVal;
  }

  /**
   * Gives the remark display for project attribute
   * 
   * @return
   */
  private String getRemarkDisplayFromProjAttr() {
    PidcVersionAttribute pidcVersionAttribute =
        this.fmDataHandler.getPidcDataHandler().getPidcVersAttrMap().get(getAttribute().getId());
    return pidcVersionAttribute.getFmAttrRemark();
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


    Set<PidcVariantBO> varWithoutSvarSet = new HashSet<>();
    for (FocusMatrixVersionAttrClientBO fmVersAttr : this.fmVersAttrSet) {
      if (fmVersAttr.getSubVariant() != null) {
        PidcVariantBO varHandler = new PidcVariantBO(this.fmDataHandler.getPidcVersion(), fmVersAttr.getVariant(),
            this.fmDataHandler.getPidcDataHandler());
        varWithoutSvarSet.add(varHandler);
      }
    }

    for (FocusMatrixVersionAttrClientBO fmVersAttr : this.fmVersAttrSet) {
      PidcVariant variant = fmVersAttr.getVariant();
      PidcSubVariant subVariant = fmVersAttr.getSubVariant();
      String value = fmVersAttr.getValue() == null ? "" : fmVersAttr.getValue().getName();
      if (subVariant == null) {
        if (!varWithoutSvarSet.contains(variant)) {
          ttSet.add(variant.getName() + TAB + fmVersAttr.getUsedFlag().getUiType() + TAB + value);
        }
      }
      else {
        ttSet.add(variant.getName() + " >> " + subVariant.getName() + TAB + fmVersAttr.getUsedFlag().getUiType() +
            TAB + value);
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

    if (this.pidcAttr.getProjectAttr().isAtChildLevel()) {

      // variant attribute
      PidcDataHandler pidcDataHandler = this.fmDataHandler.getPidcDataHandler();

      SortedSet<String> ttSet = new TreeSet<>();
      for (Entry<Long, Map<Long, PidcVariantAttribute>> var : pidcDataHandler.getVariantAttributeMap().entrySet()) {
        PidcVariantAttribute varAttr = var.getValue().get(this.attr.getId());
        if (varAttr == null) {
          continue;
        }

        PidcVariant pidcVariant = pidcDataHandler.getVariantMap().get(var.getKey());
        PidcVariantBO varHandler = new PidcVariantBO(this.fmDataHandler.getPidcVersion(), pidcVariant, pidcDataHandler);
        PidcVariantAttributeBO pidcVarAttrHandler = new PidcVariantAttributeBO(varAttr, varHandler);
        if (varAttr.isAtChildLevel()) {
          // sub variant level
          for (Entry<Long, Map<Long, PidcSubVariantAttribute>> subVar : pidcDataHandler.getSubVariantAttributeMap()
              .entrySet()) {
            PidcSubVariantAttribute svarAttr = subVar.getValue().get(this.attr.getId());
            if (svarAttr == null) {
              continue;
            }
            PidcSubVariant pidcSubVariant = pidcDataHandler.getSubVariantMap().get(subVar.getKey());
            PidcSubVariantBO pidcSubVariantBO =
                new PidcSubVariantBO(this.fmDataHandler.getPidcVersion(), pidcSubVariant, pidcDataHandler);
            PidcSubVariantAttributeBO pidcSubVarAttrHandler = new PidcSubVariantAttributeBO(svarAttr, pidcSubVariantBO);
            ttSet.add(pidcVariant.getName() + " >> " + pidcSubVariant.getName() + "\t: " +
                ApicConstants.PROJ_ATTR_USED_FLAG.getType(svarAttr.getUsedFlag()).getUiType() + "\t: " +
                pidcSubVarAttrHandler.getDefaultValueDisplayName(true));
          }
        }
        else {
          // variant level used flag and value
          ttSet.add(pidcVariant.getName() + "\t: " +
              ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag()).getUiType() + "\t: " +
              pidcVarAttrHandler.getDefaultValueDisplayName(true));
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
      // for project level attribute
      tooltip = "Used : " + this.pidcAttr.getIsUsed() + "\tValue : " + this.pidcAttr.getDefaultValueDisplayName(true);
    }
    return tooltip;

  }


  /**
   * @return true if attrbute is defined at variant level
   */
  public boolean isVariantLevel() {
    return isWorkingSetFocusMatrix() ? this.pidcAttr.getPidcVersAttr().isAtChildLevel() : this.fmVersAttrSet.size() > 1;
  }

  /**
   * FM value tooltip for history rivision when attribute was defined at 'PIDC Version' level
   *
   * @return value tooltip
   */
  private String getValueTooltipFromFmVersLevelAttr() {

    FocusMatrixVersionAttrClientBO versAttr = this.fmVersAttrSet.iterator().next();
    AttributeValue attrVal = versAttr.getValue();

    return "Used : " + versAttr.getUsedFlag().getUiType() + "\tValue : " + (attrVal == null ? "" : attrVal.getName());

  }

  /**
   * @return
   */
  private String getValueDisplayFromFmVersAttr() {
    if (isVariantLevel()) {
      return ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
    }
    AttributeValue attrVal = this.fmVersAttrSet.iterator().next().getValue();

    return attrVal == null ? "" : attrVal.getName();
  }

  /**
   * @return true if attribute details are hidden to the current user
   */
  public boolean isHiddenToUser() {
    if (isWorkingSetFocusMatrix()) {
      return this.pidcAttr.getPidcVersAttr().isAttrHidden() &&
          !(this.pidcAttr.isReadable() || this.pidcAttr.isModifiable());
    }
    // Find the PIDC attribute corresponding to the fm attibute, and return the staus
    PidcVersionBO pidcVersionBO =
        new PidcVersionBO(this.fmDataHandler.getPidcVersion(), this.fmDataHandler.getPidcDataHandler());

    PidcVersionAttribute pidcVersionAttribute =
        this.fmDataHandler.getPidcDataHandler().getPidcVersAttrMap().get(this.attr.getId());

    PidcVersionAttributeBO handler = new PidcVersionAttributeBO(pidcVersionAttribute, pidcVersionBO);
    return handler.isHiddenToUser();
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
    return isWorkingSetFocusMatrix() ? !this.pidcAttr.getPidcVersAttr().isAttrHidden() : true;
  }

  /**
   * @return
   */
  private String getValueDisplayFromProjAttr() {
    return this.pidcAttr.getDefaultValueDisplayName(true);
  }

  private boolean isWorkingSetFocusMatrix() {
    return this.fMatrix.isWorkingSet();
  }

  /**
   * @param other
   * @param columnIndex
   * @return
   */
  private int compareUcItem(final FocusMatrixAttributeClientBO other, final int columnIndex) {
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
  public PidcVersionAttributeBO getPidcAttr() {
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
  Set<FocusMatrixVersionAttrClientBO> getFmVersAttrSet() {
    return this.fmVersAttrSet;
  }

}
