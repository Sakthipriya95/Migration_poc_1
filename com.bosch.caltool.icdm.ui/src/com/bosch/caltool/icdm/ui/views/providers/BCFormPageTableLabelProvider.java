package com.bosch.caltool.icdm.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;


/**
 * @author dmo5cob
 */
public class BCFormPageTableLabelProvider implements ITableLabelProvider {


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
    if (element instanceof A2LBaseComponents) {
      A2LBaseComponents bcObj = (A2LBaseComponents) element;
      switch (columnIndex) {
        case 0:
          result = bcObj.getBcName();
          break;
        case 1:
          result = bcObj.getBcVersion();
          break;
        case 2:
          result = bcObj.getRevision();
          break;
        case 3:
          result = bcObj.getState();
          break;
        case 4:
          result = bcObj.getLongName();
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

}
