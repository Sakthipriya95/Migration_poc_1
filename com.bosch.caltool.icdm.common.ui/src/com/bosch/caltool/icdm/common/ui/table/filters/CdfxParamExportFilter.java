package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.CdfxExportParameter;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * Icdm-857 Filter for CDFX table Viewer
 */
public class CdfxParamExportFilter extends AbstractViewerFilter { // NOPMD by dmo5cob on 4/2/14 2:45 PM


  /**
   * {@inheritDoc}
   */
  // ICDM-209 and ICDM-210
  @Override
  protected boolean selectElement(final Object element) {

    if (element instanceof CdfxExportParameter) {
      final CdfxExportParameter expParam = (CdfxExportParameter) element;
      if (matchText(expParam.getA2lparam().getName())) {
        // if the text matches with a2l param name
        return true;
      }

      if (matchText(expParam.isFilteredAsStr())) {
        // if the text matches with filtered string
        return true;
      }
    }


    return false;
  }


}