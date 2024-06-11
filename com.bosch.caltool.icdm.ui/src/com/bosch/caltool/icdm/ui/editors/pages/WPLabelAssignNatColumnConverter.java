/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.ui.Activator;
/**
 * @author apj4cob
 */
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * Column Converter class for nattable
 *
 * @author apj4cob
 */
public class WPLabelAssignNatColumnConverter extends AbstractNatInputToColumnConverter {

  private final A2LWPInfoBO a2lWpInfoBO;

  /**
   * @param a2lWPInfoBO A2LWPInfoBO
   */
  public WPLabelAssignNatColumnConverter(final A2LWPInfoBO a2lWPInfoBO) {
    this.a2lWpInfoBO = a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof A2LWpParamInfo) {
      result = getData((A2LWpParamInfo) evaluateObj, colIndex);
    }
    return result;
  }

  private Object getData(final A2LWpParamInfo a2lWpParamInfo, final int colIndex) {
    Object result = null;
    A2lWpParamMapping a2lWpParamMapping = getA2lWpParamMapping(a2lWpParamInfo);
    switch (colIndex) {
      case 1:
        result = a2lWpParamInfo.getParamName();
        break;
      case 2:
        result = a2lWpParamInfo.getFuncName();
        break;
      case 3:
        result = a2lWpParamInfo.getFunctionVer();
        break;
      case 4:
        result = a2lWpParamInfo.getBcName();
        break;
      // WorkPackage Name
      case 5:
        result = this.a2lWpInfoBO.getWPName(a2lWpParamInfo);
        break;
      // Inherited from WP
      case 6:
        result = this.a2lWpInfoBO.getWpRespInherited(a2lWpParamInfo);
        break;
      // Responsibility Type
      case 7:
        result = this.a2lWpInfoBO.getWpRespType(a2lWpParamInfo);
        break;
      // Responsibility
      case 8:
        result = this.a2lWpInfoBO.getWpRespUser(a2lWpParamInfo);
        break;
      // Name at Customer
      case 9:
        result = this.a2lWpInfoBO.getWpNameCust(a2lWpParamInfo);
        break;
      // Created Date
      case 10:
        if ((null != a2lWpParamMapping) && (null != a2lWpParamMapping.getCreatedDate())) {
          result = setDateFormat(a2lWpParamMapping.getCreatedDate());
        }
        break;
      // Modified Date
      case 11:
        if ((null != a2lWpParamMapping) && (null != a2lWpParamMapping.getModifiedDate())) {
          result = setDateFormat(a2lWpParamMapping.getModifiedDate());
        }
        break;
      case 12:
        if ((null != a2lWpParamMapping) && (null != a2lWpParamMapping.getCreatedUser())) {
          result = a2lWpParamMapping.getCreatedUser();
        }
        break;
      default:
        break;
    }
    return result;

  }

  /**
   * @param a2lWpParamInfo a2lWpParamInfo
   * @return A2lWpParamMapping
   */
  public A2lWpParamMapping getA2lWpParamMapping(final A2LWpParamInfo a2lWpParamInfo) {
    return (null != this.a2lWpInfoBO.getA2lWpParamMappingModel()) ? this.a2lWpInfoBO.getA2lWpParamMappingModel()
        .getA2lWpParamMapping().get(a2lWpParamInfo.getA2lWpParamMappingId()) : null;
  }

  /**
   * @param dateInStr String
   * @return String
   */

  private String setDateFormat(final String dateInStr) {
    SimpleDateFormat formatter =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_04, Locale.getDefault(Locale.Category.FORMAT));
    try {
      Date date = formatter.parse(dateInStr);
      String formattedDate = "";
      if (date != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
      }
      return formattedDate;
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error("Date not valid", Activator.PLUGIN_ID);
    }

    return "";
  }
}

