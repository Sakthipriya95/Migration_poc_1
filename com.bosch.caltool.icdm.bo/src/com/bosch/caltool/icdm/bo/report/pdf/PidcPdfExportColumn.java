/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;


import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;

/**
 * @author TRL1COB
 */
public enum PidcPdfExportColumn {

                                 /**
                                  * Unique Instance for Single class implementation
                                  */
                                 INSTANCE;

  private static final int ATTR_SUPER_GROUP_NAME_COL_NO = 2;
  private static final int ATTR_GROUP_NAME_COL_NO = 3;
  private static final int ATTR_NAME_COL_NO = 4;
  private static final int ATTR_DESC_COL_NO = 5;
  private static final int IS_USED_COL_NO = 6;
  private static final int VAR_ATTR_VAL_COL_NO = 7;
  private static final int VAR_ATTR_PART_NO_COL_NO = 8;
  private static final int VAR_ATTR_SPEC_LINK_COL_NO = 9;
  private static final int VAR_ATTR_ADD_INFO_COL_NO = 10;
  private static final int VAR_ATTR_ID_COL_NO = 12;


  /**
   * Column headers for PIDC Attributes table in PDF
   */
  protected static final String[] HDR_PIDC_ATTR = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.SUPER_GROUP,
      DataAssessmentReportConstants.GROUP,
      DataAssessmentReportConstants.NAME,
      DataAssessmentReportConstants.DESCRIPTION,
      DataAssessmentReportConstants.USED,
      DataAssessmentReportConstants.VALUE,
      DataAssessmentReportConstants.UNIT,
      DataAssessmentReportConstants.PART_NUMBER,
      DataAssessmentReportConstants.SPEC_LINK,
      DataAssessmentReportConstants.ADDITIONAL_INFO_DESC,
      DataAssessmentReportConstants.ATTR_CREATED_DATE };
  /**
   * Column headers for Variant/Sub-Variants table in PDF
   */
  protected static final String[] HDR_VAR_SVAR = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.VAR_SUBVAR,
      DataAssessmentReportConstants.SUPER_GROUP,
      DataAssessmentReportConstants.GROUP,
      DataAssessmentReportConstants.NAME,
      DataAssessmentReportConstants.DESCRIPTION,
      DataAssessmentReportConstants.USED,
      DataAssessmentReportConstants.VALUE,
      DataAssessmentReportConstants.PART_NUMBER,
      DataAssessmentReportConstants.SPEC_LINK,
      DataAssessmentReportConstants.ADDITIONAL_INFO_DESC,
      DataAssessmentReportConstants.VAR_SUBVAR_ID,
      DataAssessmentReportConstants.ATTR_ID };

  /**
   * Column headers for Attributes table in PDF
   */
  protected static final String[] HDR_ATTR = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.ATTRIBUTE_RH_ATTR_NAME,
      DataAssessmentReportConstants.ATTRIBUTE_RH_ATTR_DESCRIPTION,
      DataAssessmentReportConstants.ATTRIBUTE_RH_VALUE_DESCRIPTION,
      DataAssessmentReportConstants.ATTRIBUTE_RH_VALUE_TYPE,
      DataAssessmentReportConstants.VALUE,
      DataAssessmentReportConstants.ATTRIBUTE_NORMALISED,
      DataAssessmentReportConstants.ATTRIBUTE_RH_VALUE_ID };

  /**
   * Used column drop down values
   */
  protected static final String[] USED_COL_VALS = new String[] {
      ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType() };


  /**
   * Column text of Project id card table viewer from project attribute
   *
   * @param pidcVersWithDtls
   * @param columnIndex
   * @param pidcAttr
   * @param redStyle
   * @param orangeStyle
   * @return
   */
  public String getColTxtFromPrjAttr(final PidcVersionWithDetails pidcVersWithDtls, final int columnIndex,
      final IProjectAttribute pidcAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {

    Attribute attr = pidcVersWithDtls.getAllAttributeMap().get(pidcAttr.getAttrId());

    String result;
    switch (columnIndex) {
      case DataAssessmentReportConstants.COLUMN_NUM_ONE:
        result = getAttrSuperGroupName(pidcVersWithDtls, attr);
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_TWO:
        result = getAttrGroupName(pidcVersWithDtls, attr);
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_THREE:
        result = pidcAttr.getName();
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_FOUR:
        result = attr.getDescription();
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_SIX:
        result = getProjAttrValue(pidcVersWithDtls, pidcAttr, redStyle, orangeStyle);
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_SEVEN:
        result = attr.getUnit();
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_EIGHT:
        result = pidcAttr.getPartNumber();
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_NINE:
        result = pidcAttr.getSpecLink();
        break;
      case DataAssessmentReportConstants.COLUMN_NUM_TEN:
        result = pidcAttr.getAdditionalInfoDesc();
        break;
      default:
        result = DataAssessmentReportConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Column text of Project id card table viewer from variant attribute
   *
   * @param pidcVersWithDtls
   * @param columnIndex
   * @param varAttr
   * @param redStyle
   * @param orangeStyle
   * @return String
   */
  public String getColTxtFromVarAttr(final PidcVersionWithDetails pidcVersWithDtls, final int columnIndex,
      final IProjectAttribute varAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {
    String result;
    Attribute attr = pidcVersWithDtls.getAllAttributeMap().get(varAttr.getAttrId());
    switch (columnIndex) {
      case ATTR_SUPER_GROUP_NAME_COL_NO:
        result = getAttrSuperGroupName(pidcVersWithDtls, attr);
        break;
      case ATTR_GROUP_NAME_COL_NO:
        result = getAttrGroupName(pidcVersWithDtls, attr);
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
        result = getProjAttrValue(pidcVersWithDtls, varAttr, redStyle, orangeStyle);
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
      case VAR_ATTR_ID_COL_NO:
        result = String.valueOf(varAttr.getAttrId());
        break;
      default:
        result = DataAssessmentReportConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Column text of Project id card table viewer from sub variant attribute
   *
   * @param pidcVersWithDtls
   * @param columnIndex
   * @param subVarAttr
   * @param redStyle
   * @param orangeStyle
   * @return
   */
  public String getColTxtFromSubVarAttr(final PidcVersionWithDetails pidcVersWithDtls, final int columnIndex,
      final IProjectAttribute subVarAttr, final StringBuilder redStyle, final StringBuilder orangeStyle) {
    String result;
    Attribute attr = pidcVersWithDtls.getAllAttributeMap().get(subVarAttr.getAttrId());
    switch (columnIndex) {
      case ATTR_SUPER_GROUP_NAME_COL_NO:
        result = getAttrSuperGroupName(pidcVersWithDtls, attr);
        break;
      case ATTR_GROUP_NAME_COL_NO:
        result = getAttrGroupName(pidcVersWithDtls, attr);
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
        result = getProjAttrValue(pidcVersWithDtls, subVarAttr, redStyle, orangeStyle);
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
        result = DataAssessmentReportConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Method to get Super group name of Attribute
   *
   * @param pidcVersWithDtls
   * @param attr
   * @return
   */
  private static String getAttrSuperGroupName(final PidcVersionWithDetails pidcVersWithDtls, final Attribute attr) {
    AttrGroup grp = pidcVersWithDtls.getAllAttributeGroupMap().get(attr.getAttrGrpId());
    AttrSuperGroup superGrp = pidcVersWithDtls.getAllAttributeSuperGroup().get(grp.getSuperGrpId());
    return superGrp.getName();
  }

  /**
   * Method to get Group name of Attribute
   *
   * @param pidcVersWithDtls
   * @param attr
   * @return
   */
  private static String getAttrGroupName(final PidcVersionWithDetails pidcVersWithDtls, final Attribute attr) {
    AttrGroup grp = pidcVersWithDtls.getAllAttributeGroupMap().get(attr.getAttrGrpId());
    return grp.getName();
  }

  /**
   * Method to get project attribute values
   *
   * @param pidcVersWithDtls
   * @param projAttr
   * @param redStyle
   * @param orangeStyle
   * @return
   */
  private String getProjAttrValue(final PidcVersionWithDetails pidcVersWithDtls, final IProjectAttribute projAttr,
      final StringBuilder redStyle, final StringBuilder orangeStyle) {

    if (!projAttr.isAtChildLevel() && (projAttr.getValueId() != null) && (projAttr.getValueId() != 0)) {
      Attribute attr = pidcVersWithDtls.getAllAttributeMap().get(projAttr.getAttrId());
      AttributeValue value = pidcVersWithDtls.getAttributeValueMap().get(projAttr.getValueId());

      redStyle.delete(0, redStyle.length()).append(value.isDeleted());
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
    return DataAssessmentReportConstants.EMPTY_STRING;
  }

  /**
   * @return the unique instance of this class
   */
  public static PidcPdfExportColumn getInstance() {
    return INSTANCE;
  }
}
