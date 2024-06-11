/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author TRL1COB
 */
public class AbstractPidcVarSubVarBO {

  private final PidcVersionWithDetails pidcVersionWithDetails;


  /**
   * @param pidcVersionWithDetails
   */
  public AbstractPidcVarSubVarBO(final PidcVersionWithDetails pidcVersionWithDetails) {
    super();
    this.pidcVersionWithDetails = pidcVersionWithDetails;
  }


  /**
   * Compares two project attributes
   *
   * @param pAttr1 project attribute 1
   * @param pAttr2 project attribute 2
   * @param sortColumn sort column
   * @return compare result
   */
  public int compare(final IProjectAttribute pAttr1, final IProjectAttribute pAttr2, final int sortColumn) {

    int ret = 0;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
      case ApicConstants.SORT_ATTRDESCR:
      case ApicConstants.SORT_SUPERGROUP:
      case ApicConstants.SORT_GROUP:
      case ApicConstants.SORT_LEVEL:
      case ApicConstants.SORT_UNIT:
      case ApicConstants.SORT_VALUETYPE:
      case ApicConstants.SORT_CHAR:
        // use compare method of Attribute class
        ret = compare(this.pidcVersionWithDetails.getAllAttributeMap().get(pAttr1.getAttrId()),
            this.pidcVersionWithDetails.getAllAttributeMap().get(pAttr2.getAttrId()), sortColumn);
        break;

      case ApicConstants.SORT_VALUE:
        // compare default values
        ret = ApicUtil.compare(pAttr1.getValue(), pAttr2.getValue());
        break;

      case ApicConstants.SORT_USED:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getUsedFlag(), pAttr2.getUsedFlag());
        break;

      case ApicConstants.SORT_USED_NOT_DEF:
        // compare only NOT_DEFINED used information
        ret = ApicUtil.compareBoolean(
            pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()));
        break;

      case ApicConstants.SORT_USED_NO:
        // compare only NO used information
        ret = ApicUtil.compareBoolean(pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()));
        break;

      case ApicConstants.SORT_USED_YES:
        // compare only YES used information
        ret = ApicUtil.compareBoolean(pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()));
        break;

      case ApicConstants.SORT_PART_NUMBER:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getPartNumber(), pAttr2.getPartNumber());
        break;

      case ApicConstants.SORT_SPEC_LINK:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getSpecLink(), pAttr2.getSpecLink());
        break;

      case ApicConstants.SORT_DESC:
      case ApicConstants.SORT_SUMMARY_DESC:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getAdditionalInfoDesc(), pAttr2.getAdditionalInfoDesc());
        break;

      case ApicConstants.SORT_MODIFIED_DATE:
        // compare last modified date information
        String sDate1 = getProjAttrLastModifiedDate(pAttr1);
        String sDate2 = getProjAttrLastModifiedDate(pAttr2);
        ret = ApicUtil.compare(parseDate(sDate1), parseDate(sDate2));
        break;

      case ApicConstants.SORT_CHAR_VAL:
        // Sort for Char Value
        ret = ApicUtil.compare(getCharValStr(pAttr1), getCharValStr(pAttr2));
        break;

      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        // compare created date information
        ret = ApicUtil.compare(pAttr1.getCreatedDate(), pAttr2.getCreatedDate());
        break;

      default:
        ret = 0;
        break;
    }
    if (ret == 0) {
      // compare result is equal, compare the attribute name
      ret = compare(this.pidcVersionWithDetails.getAllAttributeMap().get(pAttr1.getAttrId()),
          this.pidcVersionWithDetails.getAllAttributeMap().get(pAttr2.getAttrId()), ApicConstants.SORT_ATTRNAME);
    }

    return ret;

  }

  /**
   * Compares two attributes based on sort columns
   *
   * @param attr1
   * @param attr2
   * @param sortColumn
   * @return
   */
  private int compare(final Attribute attr1, final Attribute attr2, final int sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
        break;
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(attr1.getDescription(), attr2.getDescription());
        break;
      case ApicConstants.SORT_SUPERGROUP:
        compareResult = compareAttrSuperGroup(attr1, attr2);
        break;
      case ApicConstants.SORT_GROUP:
        compareResult = compareAttrGroup(attr1, attr2);
        break;
      case ApicConstants.SORT_LEVEL:
        compareResult = ApicUtil.compare(attr1.getLevel(), attr2.getLevel());
        break;
      case ApicConstants.SORT_UNIT:
        compareResult = ApicUtil.compare(attr1.getUnit(), attr2.getUnit());
        break;
      case ApicConstants.SORT_VALUETYPE:
        compareResult = ApicUtil.compare(attr1.getValueType(), attr2.getValueType());
        break;
      case ApicConstants.SORT_CHAR:
      default:
        compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
        break;
    }

    if (compareResult == 0) {
      compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
    }
    return compareResult;
  }

  /**
   * Compares two Attribute Super groups
   *
   * @param attr1
   * @param attr2
   * @return
   */
  private int compareAttrSuperGroup(final Attribute attr1, final Attribute attr2) {
    Map<Long, AttrGroup> attrGrpMap = this.pidcVersionWithDetails.getAllAttributeGroupMap();
    Map<Long, AttrSuperGroup> attrSuperGrpMap = this.pidcVersionWithDetails.getAllAttributeSuperGroup();

    AttrGroup grp1 = attrGrpMap.get(attr1.getAttrGrpId());
    AttrGroup grp2 = attrGrpMap.get(attr2.getAttrGrpId());
    AttrSuperGroup sgrp1 = attrSuperGrpMap.get(grp1.getSuperGrpId());
    AttrSuperGroup sgrp2 = attrSuperGrpMap.get(grp2.getSuperGrpId());

    int ret = ApicUtil.compare(sgrp1.getName(), sgrp2.getName());
    return ret == 0 ? ApicUtil.compare(grp1.getName(), grp2.getName()) : ret;
  }

  /**
   * Compares two Attribute groups
   *
   * @param attr1
   * @param attr2
   * @return
   */
  private int compareAttrGroup(final Attribute attr1, final Attribute attr2) {
    Map<Long, AttrGroup> allAttrGrpMap = this.pidcVersionWithDetails.getAllAttributeGroupMap();
    AttrGroup grp1 = allAttrGrpMap.get(attr1.getAttrGrpId());
    AttrGroup grp2 = allAttrGrpMap.get(attr2.getAttrGrpId());
    return ApicUtil.compare(grp1.getName(), grp2.getName());
  }

  /**
   * Get last modified date of attribute
   *
   * @return
   */
  private String getProjAttrLastModifiedDate(final IProjectAttribute pAttr) {
    return pAttr.getModifiedDate() == null ? pAttr.getCreatedDate() : pAttr.getModifiedDate();
  }


  /**
   * Parse String to date
   *
   * @param dateStr
   * @return
   */
  private Date parseDate(final String dateStr) {
    Date date = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
    if (dateStr != null) {
      try {
        date = dateFormat.parse(dateStr);
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return date;
  }

  /**
   * Get characteristic value name
   *
   * @param pAttr
   * @return
   */
  private String getCharValStr(final IProjectAttribute pAttr) {
    String ret = null;
    if (pAttr.getValueId() != null) {
      AttributeValue value = this.pidcVersionWithDetails.getAttributeValueMap().get(pAttr.getValueId());
      Long chrValId = value.getCharacteristicValueId();
      if (chrValId != null) {
        CharacteristicValue charVal = this.pidcVersionWithDetails.getAllCharactersiticValueMap().get(chrValId);
        ret = charVal.getName();
      }
    }
    return ret;
  }

}
