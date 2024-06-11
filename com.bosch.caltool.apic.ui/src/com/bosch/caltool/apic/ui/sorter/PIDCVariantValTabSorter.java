/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import java.util.Map;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.dialogs.PIDCVariantValueDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
// ICDM-1023
public class PIDCVariantValTabSorter extends AbstractViewerSorter {

  /**
   * constant for string compare true
   */
  private static final int EQUAL = 0;

  /**
   * Part Num col index
   */
  private static final int PART_NUM_COL = 8;

  /**
   * Spec Link col index
   */
  private static final int SPEC_LINK_COL = 9;
  /**
   * Comments col index
   */
  private static final int COMM_COL = 10;

  /**
   * attr val col index
   */
  private static final int ATTR_VAL_COL = 7;
  /**
   * Used flag diff column index
   */
  private static final int DIFF_COL = 5;
  /**
   * attr summary col index
   */
  private static final int ATTR_SUMMARY_COL = 6;
  /**
   * attr used yes col index
   */
  private static final int ATTR_USED_COL = 4;
  /**
   * attr used no col index
   */
  private static final int ATTR_NOT_USED_COL = 3;
  /**
   * attr not defined col index
   */
  private static final int ATTR_NOTDEF_COL = 2;
  /**
   * attr desc col index
   */
  private static final int ATTR_DESC_COL = 1;
  /**
   * attr name col index
   */
  private static final int ATTR_NAME_COL = 0;
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  // Default diraction ascending
  private int direction = ASCENDING;

  /**
   * Instance of Pidcvariant dialog
   */
  private final PIDCVariantValueDialog pidcVarVal;

  /**
   * ipidc attribtues to be compared
   */
  private IProjectAttribute pidcAttr1, pidcAttr2;

  /**
   * Part num Map
   */
  private final Map<IProjectAttribute, String> partNumMAp;
  /**
   * Commente map
   */
  private final Map<IProjectAttribute, String> commMap;
  /**
   * spec Link maop
   */
  private final Map<IProjectAttribute, String> specLinkMap;


  /**
   * @param pidcVariantValueDialog pidc varaint dialog instace
   * @param varFlag true if variants are selected
   * @param subVarFlag true if sub variants are selected
   * @param subVariant2 subVariant
   * @param variant2 variant
   * @param commMap2 commMap
   * @param specLinkMap2 specLinkMap
   * @param partNumMap2 partNumMap
   */
  public PIDCVariantValTabSorter(final PIDCVariantValueDialog pidcVariantValueDialog,
      final Map<IProjectAttribute, String> partNumMap2, final Map<IProjectAttribute, String> specLinkMap2,
      final Map<IProjectAttribute, String> commMap2) {
    this.pidcVarVal = pidcVariantValueDialog;
    this.partNumMAp = partNumMap2;
    this.specLinkMap = specLinkMap2;
    this.commMap = commMap2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }

  }

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    this.pidcAttr1 = (IProjectAttribute) object1;
    this.pidcAttr2 = (IProjectAttribute) object2;
    int compResult;
    switch (this.index) {
      // Sort pidc Attr name
      case ATTR_NAME_COL:
        compResult = ApicUtil.compare(this.pidcAttr1.getName(), this.pidcAttr2.getName());
        break;
      // Sort pidc Attr desc
      case ATTR_DESC_COL:
        compResult = ApicUtil.compare(this.pidcAttr1.getDescription(), this.pidcAttr2.getDescription());
        break;
      // Sort Not defined.
      case ATTR_NOTDEF_COL:
        compResult = getUsedState(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
        break;
      // Sort Not Used.
      case ATTR_NOT_USED_COL:
        compResult = getUsedState(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
        break;
      // Sort Used Yes.
      case ATTR_USED_COL:
        compResult = getUsedState(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
        break;
      // Sort used summary.
      case DIFF_COL:
      case ATTR_SUMMARY_COL:
        boolean attr1 = false;
        boolean attr2 = false;
        if (this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr1) == null) {
          attr1 = true;
        }
        if (this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr2) == null) {
          attr2 = true;
        }
        compResult = ApicUtil.compareBoolean(attr1, attr2);
        break;
      // Sort Value.
      case ATTR_VAL_COL:
        // compare attr value
        compResult = attrValCompare();
        break;
      case PART_NUM_COL:
        compResult = partCompare();
        break;
      case SPEC_LINK_COL:
        compResult = specLinkCompare();
        break;
      case COMM_COL:
        compResult = commCompare();
        break;
      default:
        compResult = 0;
        break;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compResult = -compResult;
    }

    if (compResult == EQUAL) {
      // compare result is equal, compare the attribute name
      compResult = this.pidcAttr1.compareTo(this.pidcAttr2);
    }
    return compResult;
  }


  /**
   * @return
   */
  private int commCompare() {
    String val1;
    String val2;
    if (CommonUtils.isNotNull(this.commMap.get(this.pidcAttr1))) {
      val1 = this.commMap.get(this.pidcAttr1);
    }
    else {
      if (this.pidcVarVal.getOldCommentMap().get(this.pidcAttr1) == null) {
        val1 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val1 = this.pidcVarVal.getOldCommentMap().get(this.pidcAttr1);
      }
    }

    if (CommonUtils.isNotNull(this.commMap.get(this.pidcAttr2))) {
      val2 = this.commMap.get(this.pidcAttr2);
    }
    else {
      if (this.pidcVarVal.getOldCommentMap().get(this.pidcAttr2) == null) {
        val2 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val2 = this.pidcVarVal.getOldCommentMap().get(this.pidcAttr2);
      }
    }
    return val1.compareTo(val2);
  }


  /**
   * @return
   */
  private int specLinkCompare() {
    String val1;
    String val2;
    if (CommonUtils.isNotNull(this.specLinkMap.get(this.pidcAttr1))) {
      val1 = this.specLinkMap.get(this.pidcAttr1);
    }
    else {
      if (this.pidcVarVal.getOldSpecLinkMap().get(this.pidcAttr1) == null) {
        val1 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val1 = this.pidcVarVal.getOldSpecLinkMap().get(this.pidcAttr1);
      }
    }

    if (CommonUtils.isNotNull(this.specLinkMap.get(this.pidcAttr2))) {
      val2 = this.specLinkMap.get(this.pidcAttr2);
    }
    else {
      if (this.pidcVarVal.getOldSpecLinkMap().get(this.pidcAttr2) == null) {
        val2 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val2 = this.pidcVarVal.getOldSpecLinkMap().get(this.pidcAttr2);
      }
    }
    return val1.compareTo(val2);
  }


  /**
   * @return
   */
  private int partCompare() {
    String val1;
    String val2;
    if (CommonUtils.isNotNull(this.partNumMAp.get(this.pidcAttr1))) {
      val1 = this.partNumMAp.get(this.pidcAttr1);
    }
    else {
      if (this.pidcVarVal.getOldPartNumMap().get(this.pidcAttr1) == null) {
        val1 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val1 = this.pidcVarVal.getOldPartNumMap().get(this.pidcAttr1);
      }
    }

    if (CommonUtils.isNotNull(this.partNumMAp.get(this.pidcAttr2))) {
      val2 = this.partNumMAp.get(this.pidcAttr2);
    }
    else {
      if (this.pidcVarVal.getOldPartNumMap().get(this.pidcAttr2) == null) {
        val2 = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
      }
      else {
        val2 = this.pidcVarVal.getOldPartNumMap().get(this.pidcAttr2);
      }
    }
    return val1.compareTo(val2);
  }

  /**
   * Compares variant values
   */
  private int attrValCompare() {
    String val1;
    String val2;
    if (CommonUtils.isNotNull(this.pidcVarVal.getVarValMap().get(this.pidcAttr1))) {
      val1 = this.pidcVarVal.getVarValMap().get(this.pidcAttr1).getName();
    }
    else {
      val1 = this.pidcVarVal.getOldAttrValMap().get(this.pidcAttr1);
    }

    if (CommonUtils.isNotNull(this.pidcVarVal.getVarValMap().get(this.pidcAttr2))) {
      val2 = this.pidcVarVal.getVarValMap().get(this.pidcAttr2).getName();
    }
    else {
      val2 = this.pidcVarVal.getOldAttrValMap().get(this.pidcAttr2);
    }

    return ApicUtil.compare(val1, val2);

  }

  /**
   * Compares the used flag's of attr
   *
   * @param type used-yes/no/undefined
   * @return result of compare
   */
  private int getUsedState(final String type) {
    boolean attr1 = false;
    boolean attr2 = false;
    if (((this.pidcVarVal.getAttrUsedMap().get(this.pidcAttr1) != null) &&
        this.pidcVarVal.getAttrUsedMap().get(this.pidcAttr1).equalsIgnoreCase(type)) ||
        ((this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr1) != null) &&
            this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr1).equalsIgnoreCase(type))) {
      attr1 = true;
    }
    if (((this.pidcVarVal.getAttrUsedMap().get(this.pidcAttr2) != null) &&
        this.pidcVarVal.getAttrUsedMap().get(this.pidcAttr2).equalsIgnoreCase(type)) ||
        ((this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr2) != null) &&
            this.pidcVarVal.getOldAttrUsedMap().get(this.pidcAttr2).equalsIgnoreCase(type))) {
      attr2 = true;
    }
    return ApicUtil.compareBoolean(attr1, attr2);
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}