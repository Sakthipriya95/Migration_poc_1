package com.bosch.caltool.icdm.common.ui.views.providers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;


/**
 * @author dmo5cob
 */
public class WPFormPageTableLabelProvider implements ITableLabelProvider {


  Map<WpRespModel, List<Long>> wpRespModelMap = new HashMap<>();


  /**
   * constructor
   *
   * @param wpRespModelMap as input
   */
  public WPFormPageTableLabelProvider(final Map<WpRespModel, List<Long>> wpRespModelMap) {
    this.wpRespModelMap = wpRespModelMap;
  }

  @Override
  public void addListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  @Override
  public void dispose() {
    // TO-DO
  }

  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {

    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String result = "";
    // ICDM-209 and ICDM-210
    if (element instanceof WpRespModel) {
      WpRespModel wpRespModel = (WpRespModel) element;
      switch (columnIndex) {
        case 0:
          result = wpRespModel.getWpName();
          break;
        case 1:
          result = wpRespModel.getWpRespName();
          break;
        case 2:
          result = A2lResponsibilityCommon.getRespType(wpRespModel.getA2lResponsibility()).getDispName();
          break;
        case 3:
          result = wpRespModel.getParamCount().toString();
          break;
        default:
          result = "";
          break;
      }

    }

    return result;
  }


}
