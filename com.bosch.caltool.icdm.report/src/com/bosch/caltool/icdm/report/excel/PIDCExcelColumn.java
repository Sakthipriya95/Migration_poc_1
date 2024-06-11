/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;


/**
 * @author rvu1cob
 */
public enum PIDCExcelColumn {

                             /**
                              * Unique Instance for Single class implementation
                              */
                             INSTANCE;

  private static final int ATTR_SUPER_GROUP_NAME_COL_NO = 1;
  private static final int ATTR_GROUP_NAME_COL_NO = 2;
  private static final int ATTR_NAME_COL_NO = 3;
  private static final int ATTR_DESC_COL_NO = 4;
  private static final int IS_USED_COL_NO = 5;
  private static final int VAR_ATTR_VAL_COL_NO = 6;
  private static final int VAR_ATTR_PART_NO_COL_NO = 7;
  private static final int VAR_ATTR_SPEC_LINK_COL_NO = 8;
  private static final int VAR_ATTR_ADD_INFO_COL_NO = 9;


  /**
   * Columns for PIDC attr sheet display Icdm-1004 added two new Columns
   */
  public static final String[] SHT_HDR_PIDC = new String[] {
      ExcelClientConstants.RH_SUPER_GROUP,
      ExcelClientConstants.RH_GROUP,
      ExcelClientConstants.RH_NAME,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.RH_USED,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.RH_UNIT,
      ExcelClientConstants.RH_PART_NUMBER,
      ExcelClientConstants.RH_SPEC_LINK,
      ExcelClientConstants.RH_ADDITIONAL_INFO_DESC,
      ExcelClientConstants.RH_ATTR_CREATED_DATE };
  /**
   * Columns for var and subvar attr sheet display
   */
  public static final String[] SHT_HDR_VAR_SVAR = new String[] {
      ExcelClientConstants.RH_SUB_VARIANT,
      ExcelClientConstants.RH_SUPER_GROUP,
      ExcelClientConstants.RH_GROUP,
      ExcelClientConstants.RH_NAME,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.RH_USED,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.RH_PART_NUMBER,
      ExcelClientConstants.RH_SPEC_LINK,
      ExcelClientConstants.RH_ADDITIONAL_INFO_DESC };

  /**
   * Columns for attr sheet display
   */
  public static final String[] SHT_HDR_ATTR = new String[] {

      ExcelClientConstants.ATTRIBUTE_RH_ATTR_NAME,
      ExcelClientConstants.ATTRIBUTE_RH_ATTR_DESCRIPTION,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_DESCRIPTION,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_TYPE,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.ATTRIBUTE_NORMALISED,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_ID };

  /**
   * Used column drop down values
   */
  public static final String[] USED_COL_VALS = new String[] {
      ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType() };


  /**
   * Auto filter range for PIDC attribute sheet Icdm-1004 added Filter for two more Columns
   */
  public static final String PID_AUTOFLTRRNG = "A1:L";

  /**
   * Auto filter range for variant/subvariant sheet
   */
  public static final String SVAR_AUTOFLTRRNG = "A1:L";

  /**
   * Success message
   */
  final static String EXPORT_SUCCESS = "Project ID card Exported successfully!";


  /**
   * Column text of Project id card table viewer from project attribute
   *
   * @param columnIndex
   * @param pidcAttr
   * @param orangeStyle
   * @param styleToUse
   * @return String
   */
  @SuppressWarnings("javadoc")
  public String getColTxtFromPrjAttr(final PidcVersionBO pidcVersHndlr, final int columnIndex,
      final IProjectAttribute pidcAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {

    Attribute attr = pidcVersHndlr.getPidcDataHandler().getAttribute(pidcAttr.getAttrId());

    String result;
    switch (columnIndex) {
      case ExcelClientConstants.COLUMN_NUM_ZERO:
        result = getAttrSuperGroupName(pidcVersHndlr, attr);
        break;
      case ExcelClientConstants.COLUMN_NUM_ONE:
        result = getAttrGroupName(pidcVersHndlr, attr);
        break;
      case ExcelClientConstants.COLUMN_NUM_TWO:
        result = pidcAttr.getName();
        break;
      case ExcelClientConstants.COLUMN_NUM_THREE:
        result = attr.getDescription();
        break;
      case ExcelClientConstants.COLUMN_NUM_FIVE:
        result = getProjAttrValue(pidcVersHndlr, pidcAttr, redStyle, orangeStyle);
        break;
      case ExcelClientConstants.COLUMN_NUM_SIX:
        result = attr.getUnit();
        break;
      case ExcelClientConstants.COLUMN_NUM_SEVEN:
        result = pidcAttr.getPartNumber();
        break;
      case ExcelClientConstants.COLUMN_NUM_EIGHT:
        result = pidcAttr.getSpecLink();
        break;
      // Icdm-1004 new Columns for Attr Class and Value Class
      case ExcelClientConstants.COLUMN_NUM_NINE:
        result = pidcAttr.getAdditionalInfoDesc();
        break;
      default:
        result = ExcelCommonConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  private static String getAttrSuperGroupName(final PidcVersionBO pidcVersHndlr, final Attribute attr) {
    AttrGroup grp = pidcVersHndlr.getPidcDataHandler().getAttrGroup(attr.getAttrGrpId());
    AttrSuperGroup superGrp = pidcVersHndlr.getPidcDataHandler().getAttrSuperGroup(grp.getSuperGrpId());
    return superGrp.getName();
  }

  private static String getAttrGroupName(final PidcVersionBO pidcVersHndlr, final Attribute attr) {
    AttrGroup grp = pidcVersHndlr.getPidcDataHandler().getAttrGroup(attr.getAttrGrpId());
    return grp.getName();
  }

  private String getProjAttrValue(final PidcVersionBO pidcVersHndlr, final IProjectAttribute projAttr,
      final StringBuilder redStyle, final StringBuilder orangeStyle) {

    if (!projAttr.isAtChildLevel() && (projAttr.getValueId() != null) && (projAttr.getValueId() != 0)) {
      Attribute attr = pidcVersHndlr.getPidcDataHandler().getAttribute(projAttr.getAttrId());
      AttributeValue value = pidcVersHndlr.getPidcDataHandler().getAttributeValue(projAttr.getValueId());
      // icdm 494
      redStyle.delete(0, redStyle.length()).append(value.isDeleted());
      // Icdm-833 orange Style
      CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(value.getClearingStatus());
      orangeStyle.delete(0, orangeStyle.length()).append(clStatus == CLEARING_STATUS.CLEARED);
      String valueStr = projAttr.getValue();
      // Remove the trailing unit from the value
      if ((valueStr != null) && AttributeValueType.NUMBER.getDisplayText().equals(attr.getValueType()) &&
          valueStr.endsWith(attr.getUnit())) {
        valueStr = valueStr.substring(0, valueStr.length() - attr.getUnit().length() - 1);
      }
      return valueStr;
    }
    if (projAttr.isAtChildLevel()) {
      return projAttr.getValue();
    }
    return ExcelCommonConstants.EMPTY_STRING;
  }

  /**
   * Column text of Project id card table viewer from variant attribute
   *
   * @param columnIndex
   * @param varAttr
   * @param orangeStyle
   * @return String
   */
  @SuppressWarnings("javadoc")
  public String getColTxtFromVarAttr(final PidcVersionBO pidcVersHndlr, final int columnIndex,
      final IProjectAttribute varAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {
    String result;
    Attribute attr = pidcVersHndlr.getPidcDataHandler().getAttribute(varAttr.getAttrId());
    switch (columnIndex) {
      case ATTR_SUPER_GROUP_NAME_COL_NO:
        result = getAttrSuperGroupName(pidcVersHndlr, attr);
        break;
      case ATTR_GROUP_NAME_COL_NO:
        result = getAttrGroupName(pidcVersHndlr, attr);
        break;
      case ATTR_NAME_COL_NO:
        result = varAttr.getName();
        break;
      case ATTR_DESC_COL_NO:
        result = attr.getDescription();
        break;
      case IS_USED_COL_NO:
        result = ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag()).getUiType();
        break;
      case VAR_ATTR_VAL_COL_NO:
        result = getProjAttrValue(pidcVersHndlr, varAttr, redStyle, orangeStyle);
        break;
      case VAR_ATTR_PART_NO_COL_NO:
        result = varAttr.getPartNumber();
        break;
      case VAR_ATTR_SPEC_LINK_COL_NO:
        result = varAttr.getSpecLink();
        break;
      case VAR_ATTR_ADD_INFO_COL_NO:
        result = varAttr.getAdditionalInfoDesc();
        break;
      default:
        result = ExcelCommonConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Column text of Project id card table viewer from variant attribute
   *
   * @param columnIndex
   * @param subVarAttr
   * @param orangeStyle
   * @return String
   */
  @SuppressWarnings("javadoc")
  public String getColTxtFromSubVarAttr(final PidcVersionBO pidcVersHndlr, final int columnIndex,
      final IProjectAttribute subVarAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {
    String result;
    Attribute attr = pidcVersHndlr.getPidcDataHandler().getAttribute(subVarAttr.getAttrId());
    switch (columnIndex) {
      case ATTR_SUPER_GROUP_NAME_COL_NO:
        result = getAttrSuperGroupName(pidcVersHndlr, attr);
        break;
      case ATTR_GROUP_NAME_COL_NO:
        result = getAttrGroupName(pidcVersHndlr, attr);
        break;
      case ATTR_NAME_COL_NO:
        result = subVarAttr.getName();
        break;
      case ATTR_DESC_COL_NO:
        result = attr.getDescription();
        break;
      case IS_USED_COL_NO:
        result = ApicConstants.PROJ_ATTR_USED_FLAG.getType(subVarAttr.getUsedFlag()).getUiType();
        break;
      case VAR_ATTR_VAL_COL_NO:
        result = getProjAttrValue(pidcVersHndlr, subVarAttr, redStyle, orangeStyle);
        break;
      case VAR_ATTR_PART_NO_COL_NO:
        result = subVarAttr.getPartNumber();
        break;
      case VAR_ATTR_SPEC_LINK_COL_NO:
        result = subVarAttr.getSpecLink();
        break;
      case VAR_ATTR_ADD_INFO_COL_NO:
        result = subVarAttr.getAdditionalInfoDesc();
        break;
      default:
        result = ExcelCommonConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * @return the unique instance of this class
   */
  public static PIDCExcelColumn getInstance() {
    return INSTANCE;
  }
}
