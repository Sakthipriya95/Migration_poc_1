package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;


/**
 * Text Filter Class
 *
 * @author elm1cob
 */
public class AvailableWorkPackagesFilter extends AbstractViewerFilter {


  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param wpInfoBo
   */
  public AvailableWorkPackagesFilter(final A2LWPInfoBO wpInfoBo) {
    super();
    this.a2lWpInfoBo = wpInfoBo;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean selectElement(final Object element) {

    if (element instanceof A2lWpResponsibility) {

      A2lWpResponsibility selWpResp = (A2lWpResponsibility) element;

      if (matchText(this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(selWpResp.getA2lRespId())
          .getName())) {
        return true;
      }

      if (matchText(selWpResp.getName())) {
        return true;
      }

      if (matchText(this.a2lWpInfoBo.getVarGrpName(selWpResp))) {
        return true;
      }

      if (matchText(this.a2lWpInfoBo.getRespTypeName(selWpResp))) {
        return true;
      }

      if (matchText(selWpResp.getWpNameCust())) {
        return true;
      }
    }

    return false;
  }

}
