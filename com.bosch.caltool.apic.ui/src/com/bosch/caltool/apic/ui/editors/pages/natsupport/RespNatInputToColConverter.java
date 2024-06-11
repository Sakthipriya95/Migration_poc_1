/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author apj4cob
 */
public class RespNatInputToColConverter extends AbstractNatInputToColumnConverter {

  private final WorkPkgResponsibilityBO workPkgResponsibilityBO;

  /**
   * @param workPkgResponsibilityBO A2lRespBO
   */
  public RespNatInputToColConverter(final WorkPkgResponsibilityBO workPkgResponsibilityBO) {
    this.workPkgResponsibilityBO = workPkgResponsibilityBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    // A2lResponsibility represents a row in the table viewer
    if (evaluateObj instanceof A2lResponsibility) {
      result = getData((A2lResponsibility) evaluateObj, colIndex);
    }
    return result;
  }

  private Object getData(final A2lResponsibility a2lResp, final int colIndex) {
    Object result = null;

    switch (colIndex) {
      // Responsibility Type
      case 0:
        result = WpRespType.getType(a2lResp.getRespType()).getDispName();
        break;
      // Responsible User
      case 1:
        result = a2lResp.getName();
        break;
      // First Name
      case 2:
        result = this.workPkgResponsibilityBO.getRespFirstName(a2lResp);
        break;
      // Last Name
      case 3:
        result = this.workPkgResponsibilityBO.getRespLastName(a2lResp);
        break;
      // Department
      case 4:
        result = this.workPkgResponsibilityBO.getRespDeptName(a2lResp);
        break;
      // Alias Name
      case 5:
        result = a2lResp.getAliasName();
        break;
      // Deleted Flag
      case 6:
        result = a2lResp.isDeleted() ? Boolean.TRUE : Boolean.FALSE;
        break;
      // Created Date
      case 7:
        if (CommonUtils.isNotNull(a2lResp.getCreatedDate())) {
          result = setDateFormat(a2lResp.getCreatedDate());
        }
        break;
      // Modified Date
      case 8:
        if (CommonUtils.isNotNull(a2lResp.getModifiedDate())) {
          result = setDateFormat(a2lResp.getModifiedDate());
        }
        break;
      // Created User
      case 9:
        if (CommonUtils.isNotNull(a2lResp.getCreatedUser())) {
          result = a2lResp.getCreatedUser();
        }
        break;
      default:
        break;
    }
    return result;
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
