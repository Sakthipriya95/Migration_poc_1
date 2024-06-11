/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * The Class WPInputToColumnConverter.
 *
 * @author rgo7cob
 * @deprecated not used
 */
@Deprecated
public class WPInputToColumnConverter extends AbstractNatInputToColumnConverter {

  private final A2lWpMapping a2lWpMapping;

  /**
   * @param a2lWpMapping
   */
  public WPInputToColumnConverter(final A2lWpMapping a2lWpMapping) {
    this.a2lWpMapping = a2lWpMapping;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    if (evaluateObj instanceof A2LWpRespExt) {
      return getColumnDataForGrpResp((A2LWpRespExt) evaluateObj, colIndex);
    }

    if (evaluateObj instanceof A2LGroup) {
      return getWpColumnDataForGrp((A2LGroup) evaluateObj, colIndex);
    }

    return getColumnDataForWP((A2lWpObj) evaluateObj, colIndex);
  }

  /**
   * Gets the column data for grp resp.
   *
   * @param a2lWpResp the a 2 l wp resp
   * @param colIndex the col index
   * @return the column data for grp resp
   */
  private Object getColumnDataForGrpResp(final A2LWpRespExt a2lWpResp, final int colIndex) {
    // 7/7/14
    Object result;
    switch (colIndex) {
      // Column for SSD class type
      case 0:
        result = a2lWpResp.isA2lGrp() ? a2lWpResp.getIcdmA2lGroup().getGrpName() : a2lWpResp.getWpResource();

        break;
      case 1:
        result = a2lWpResp.isA2lGrp() ? a2lWpResp.getIcdmA2lGroup().getGrpLongName()
            : a2lWpResp.getWorkPackage().getName().trim();
        break;
      case 2:
        int labelCnt = this.a2lWpMapping.getA2lWpRespGrpLabelMap().containsKey(a2lWpResp.getA2lWpResp().getId())
            ? this.a2lWpMapping.getA2lWpRespGrpLabelMap().get(a2lWpResp.getA2lWpResp().getId()).size() : 0;
        result = a2lWpResp.isA2lGrp() ? Integer.toString(labelCnt)
            : a2lWpResp.getWpNumMap().get(a2lWpResp.getWorkPackage().getName());
        break;
      case 3:
        result = a2lWpResp.getA2lWpResp().getWpRespEnum().getDispName();
        break;
      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   * Gets the column data for WP.
   *
   * @param paramData the param data
   * @param colIndex the col index
   * @return the column data for WP
   */
  private Object getColumnDataForWP(final A2lWpObj paramData, final int colIndex) {
    String result = "";

    switch (colIndex) {
      case 0:
        result = paramData.getWpGroupName();
        break;
      case 1:
        result = paramData.getWpName();
        break;
      case 2:
        if (paramData.getSource().equals(ApicConstants.FC_WP_MAPPING)) {
          result = paramData.getWpNumber();
        }
        break;
      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   * Gets the wp column data for grp.
   *
   * @param a2lGrp the a 2 l grp
   * @param colIndex the col index
   * @return the wp column data for grp
   */
  private Object getWpColumnDataForGrp(final A2LGroup a2lGrp, final int colIndex) { // NOPMD by jvi6cob on
    // 7/7/14
    Object result;
    switch (colIndex) {
      // Column for SSD class type
      case 0:
        result = a2lGrp.getGroupName();
        break;
      case 1:
        result = a2lGrp.getGroupLongName();
        break;
      case 2:
        result = Integer.toString(a2lGrp.getLabelMap().size());
        break;
      default:
        result = "";
        break;
    }
    return result;
  }


}
