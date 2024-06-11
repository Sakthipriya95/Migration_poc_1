package com.bosch.caltool.icdm.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;

/**
 * @author dmo5cob
 */
public class FCFormPageTableLabelProvider implements ITableLabelProvider {

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
    if (element instanceof Function) {
      Function function = (Function) element;
      switch (columnIndex) {
        case 0:
          result = function.getName();
          break;
        case 1:
          result = function.getFunctionVersion();
          break;
        case 2:
          result = function.getLongIdentifier();
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

}
