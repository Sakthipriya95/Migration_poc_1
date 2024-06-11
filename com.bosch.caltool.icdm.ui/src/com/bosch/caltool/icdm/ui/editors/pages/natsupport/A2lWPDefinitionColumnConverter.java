
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * Column Converter class for nattable
 *
 * @author pdh2cob
 */
public class A2lWPDefinitionColumnConverter extends AbstractNatInputToColumnConverter {

  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * Column converter
   *
   * @param wpInfoBo A2LWPInfoBO
   */
  public A2lWPDefinitionColumnConverter(final A2LWPInfoBO wpInfoBo) {
    wpInfoBo.getA2lWpDefnModel();
    this.a2lWpInfoBo = wpInfoBo;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof A2lWpResponsibility) {
      result = getData((A2lWpResponsibility) evaluateObj, colIndex);
    }
    return result;
  }


  private Object getData(final A2lWpResponsibility a2lWpResp, final int colIndex) {
    Object result = null;
    switch (colIndex) {
      case IUIConstants.COLUMN_INDEX_WP:
        result = a2lWpResp.getName();
        break;
      case IUIConstants.COLUMN_INDEX_VAR_GROUP:
        result = getVarGrpData(a2lWpResp);
        break;
      case IUIConstants.COLUMN_INDEX_RESP_TYPE:
        result = this.a2lWpInfoBo.getRespTypeName(a2lWpResp);
        break;
      case IUIConstants.COLUMN_INDEX_RESP:
        result = getWpResp(a2lWpResp);
        break;
      case IUIConstants.COLUMN_INDEX_WP_CUST:
        result = a2lWpResp.getWpNameCust();
        break;
      case IUIConstants.COLUMN_INDEX_CREATED_DATE:
        result = setDateFormat(a2lWpResp.getCreatedDate());
        break;
      case IUIConstants.COLUMN_INDEX_MODIFIED_DATE:
        result = setDateFormat(a2lWpResp.getModifiedDate());
        break;
      case IUIConstants.COLUMN_INDEX_CREATED_USER:
        result = a2lWpResp.getCreatedUser();
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * @param a2lWpResp
   * @param result
   * @return
   */
  private Object getVarGrpData(final A2lWpResponsibility a2lWpResp) {
    Map<Long, A2lVariantGroup> a2lVariantGrpMap = this.a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap();
    String result = null;
    if ((a2lWpResp.getVariantGrpId() != null) && (a2lVariantGrpMap != null) &&
        (a2lVariantGrpMap.get(a2lWpResp.getVariantGrpId()) != null)) {
      result = a2lVariantGrpMap.get(a2lWpResp.getVariantGrpId()).getName();
    }
    return result;
  }

  private String getWpResp(final A2lWpResponsibility a2lWpResponsibility) {
    if (a2lWpResponsibility.getA2lRespId() != null) {
      A2lResponsibility a2lResp = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .get(a2lWpResponsibility.getA2lRespId());
      return a2lResp.getName();
    }
    return "";
  }


  /**
   * @param dateInStr String
   * @return String
   */

  private String setDateFormat(final String dateInStr) {
    SimpleDateFormat formatter =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_04, Locale.getDefault(Locale.Category.FORMAT));
    if (null != dateInStr) {
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
    }

    return "";
  }


}
