package com.bosch.caltool.icdm.common.ui.table.filters;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;


/**
 * WPtableFilters.java This class handles the common filters for the WP page table viewer
 */
public class WPTableFilters extends AbstractViewerFilter { // NOPMD by dmo5cob on 4/2/14 2:45 PM


  private Map<WpRespModel, List<Long>> workPackageRespMap;

  /**
   * {@inheritDoc}
   */
  // ICDM-209 and ICDM-210
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof WpRespModel) {
      // when mapping source is workpackage
      final WpRespModel wpRespModel = (WpRespModel) element;

      if (((wpRespModel.getWpName() != null) && matchText(wpRespModel.getWpName())) ||
          ((wpRespModel.getWpRespName() != null) && matchText(wpRespModel.getWpRespName())) ||
          ((A2lResponsibilityCommon.getRespType(wpRespModel.getA2lResponsibility()).getDispName() != null) &&
              matchText(A2lResponsibilityCommon.getRespType(wpRespModel.getA2lResponsibility()).getDispName()))) {
        // when the filter text matches WP name , group name or number
        return true;
      }
    }
    return false;

  }


  /**
   * @return the workPackageRespMap
   */
  public Map<WpRespModel, List<Long>> getWorkPackageRespMap() {
    return this.workPackageRespMap;
  }


  /**
   * @param workPackageRespMap the workPackageRespMap to set
   */
  public void setWorkPackageRespMap(final Map<WpRespModel, List<Long>> workPackageRespMap) {
    this.workPackageRespMap = workPackageRespMap;
  }


}