/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.util.Map;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;


/**
 * @author dmo5cob
 */
public enum AttributesExcelColumn {

                                   /**
                                    * Unique Instance for Single class implementation
                                    */
                                   INSTANCE;

  private static final int ATTR_SUPER_GROUP_NAME_COL_NO = 0;
  private static final int ATTR_GROUP_NAME_COL_NO = 1;
  private static final int ATTR_NAME_COL_NO = 2;
  private static final int ATTR_DESC_COL_NO = 3;
  private static final int VAL_TYPE_COL_NO = 4;
  private static final int IS_NORMALIZED_COL_NO = 5;
  private static final int IS_MANDATORY_COL_NO = 6;
  private static final int UNIT_COL_NO = 7;
  private static final int FORMAT_COL_NO = 8;
  private static final int PART_NO_COL_NO = 9;
  private static final int SPEC_LINK_COL_NO = 10;
  private static final int IS_DELETED_COL_NO = 11;
  private static final int CHAR_STRING_COL_NO = 12;
  private static final int IS_ATTR_EXIST_COL_NO = 13;
  private static final int IS_ATTR_VAL_EXIST_COL_NO = 14;
  private static final int ATTR_ID_COL_NO = 15;
  private static final int ATTR_VAL_NAME_COL_NO = 0;
  private static final int ATTR_VAL_VAL_COL_NO = 1;
  private static final int ATTR_VAL_UNIT_COL_NO = 2;
  private static final int ATTR_VAL_DESC_COL_NO = 3;
  private static final int ATTR_VAL_CLEARING_COL_NO = 4;
  private static final int ATTR_VAL_IS_DELETED_COL_NO = 5;
  private static final int ATTR_VAL_CHAR_VAL_COL_NO = 6;
  private static final int ATTR_VAL_ID_COL_NO = 7;

  /**
   * @return the unique instance of this class
   */
  public static AttributesExcelColumn getInstance() {
    return INSTANCE;
  }

  /**
   * Columns for attr sheet display // Icdm-893 new column in attr export
   */
  public static final String[] SHT_HDR_ATTR = new String[] {
      ExcelClientConstants.H_SUPER_GROUP,
      ExcelClientConstants.H_GROUP,
      ExcelClientConstants.ATTRIBUTE_RH_ATTR_NAME,
      ExcelClientConstants.ATTRIBUTE_RH_ATTR_DESCRIPTION,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_TYPE,
      ExcelClientConstants.ATTRIBUTE_NORMALISED,
      ExcelClientConstants.ATTRIBUTE_RH_MANDATORY,
      ExcelClientConstants.RH_UNIT,
      ExcelClientConstants.ATTRIBUTE_FORMAT,
      ExcelClientConstants.RH_PART_NUMBER,
      ExcelClientConstants.RH_SPEC_LINK,
      ExcelClientConstants.DELETED_FLAG,
      ExcelClientConstants.CHARACTERISTIC,
      ExcelClientConstants.ATTR_EXT,
      ExcelClientConstants.VAL_EXT,
      ExcelClientConstants.ATTRIBUTE_RH_ATTR_ID };

  /**
   * Columns for attr values sheet display // Icdm-893 new column in attr export
   */
  public static final String[] SHT_HDR_ATTR_VAL = new String[] {
      ExcelClientConstants.ATTRIBUTE_RH_ATTR_NAME,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.RH_UNIT,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_DESCRIPTION,
      ExcelClientConstants.RH_VALUE_CLEARING_STATUS,
      ExcelClientConstants.DELETED_FLAG,
      ExcelClientConstants.CHARVALUE,
      ExcelClientConstants.ATTRIBUTE_RH_VALUE_ID };

  /**
   * Auto filter range for attribute sheet
   */
  public static final String ATTR_AUTOFLTRRNG = "A1:P";
  /**
   * Auto filter range for values sheet
   */
  public static final String VAL_AUTOFLTRRNG = "A1:H";

  /**
   * Success message
   */
  final static String EXPORT_SUCCESS = "Attributes Exported successfully!";


  /**
   * Column text of Attrs table viewer
   *
   * @param columnIndex
   * @param attr
   * @param orangeStyle
   * @param styleToUse
   * @param exportModel
   * @return String
   */
  @SuppressWarnings("javadoc")
  public String getColTxtFromAttribute(final int columnIndex, final Attribute attr, final AttrExportModel exportModel) {

    String result = "";

    switch (columnIndex) {
      case ATTR_SUPER_GROUP_NAME_COL_NO:
        result = exportModel.getAttrGroup().getAllSuperGroupMap()
            .get(exportModel.getAttrGroup().getAllGroupMap().get(attr.getAttrGrpId()).getSuperGrpId()).getName();
        break;
      case ATTR_GROUP_NAME_COL_NO:
        result = exportModel.getAttrGroup().getAllGroupMap().get(attr.getAttrGrpId()).getName();
        break;
      case ATTR_NAME_COL_NO:
        result = attr.getName();
        break;
      case ATTR_DESC_COL_NO:
        result = attr.getDescription();
        break;
      case VAL_TYPE_COL_NO:
        result = attr.getValueType();
        break;
      case IS_NORMALIZED_COL_NO:
        result = attr.isNormalized() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case IS_MANDATORY_COL_NO:
        result = attr.isMandatory() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case UNIT_COL_NO:
        result = attr.getUnit();
        break;
      case FORMAT_COL_NO:
        result = attr.getFormat();
        break;
      case PART_NO_COL_NO:
        result = attr.isWithPartNumber() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case SPEC_LINK_COL_NO:
        result = attr.isWithSpecLink() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case IS_DELETED_COL_NO:
        result = attr.isDeleted() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case CHAR_STRING_COL_NO:
        result = attr.getCharStr();
        break;
      case IS_ATTR_EXIST_COL_NO:
        result = attr.isExternal() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case IS_ATTR_VAL_EXIST_COL_NO:
        result = attr.isExternalValue() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      case ATTR_ID_COL_NO:
        result = attr.getId().toString();
        break;
      default:
        result = ExcelCommonConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Column text of values table viewer
   *
   * @param columnIndex
   * @param val
   * @param orangeStyle
   * @param styleToUse
   * @param attrMap
   * @return String
   */
  @SuppressWarnings("javadoc")
  public String getColTxtFromAttributeValue(final int columnIndex, final AttributeValue val,
      final Map<Long, Attribute> attrMap) {

    String result = "";

    switch (columnIndex) {
      case ATTR_VAL_NAME_COL_NO:
        result = attrMap.get(val.getAttributeId()).getNameEng();
        break;
      case ATTR_VAL_VAL_COL_NO:
        result = val.getNameRaw();
        break;
      case ATTR_VAL_UNIT_COL_NO:
        result = val.getUnit();
        break;
      case ATTR_VAL_DESC_COL_NO:
        result = val.getDescription();
        break;
      case ATTR_VAL_CLEARING_COL_NO:
        result = val.getClearingStatus();
        break;
      case ATTR_VAL_IS_DELETED_COL_NO:
        result = val.isDeleted() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
        break;
      // Icdm-893 new column in attr export
      case ATTR_VAL_CHAR_VAL_COL_NO:
        result = val.getCharStr();
        break;
      case ATTR_VAL_ID_COL_NO:
        result = val.getId().toString();
        break;
      default:
        result = ExcelCommonConstants.EMPTY_STRING;
        break;
    }
    return result;
  }
}
