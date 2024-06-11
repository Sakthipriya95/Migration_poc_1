package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

/**
 * @author dmo5cob ICDM 395
 */
public class PidcAddStructAttrTableLabelProvider implements ITableLabelProvider {

  protected String result;
  /**
   * PidcVersionHandler
   */
  private final PidcVersionBO pidcVersionBO;

  /**
   * @param pidcVersionBO
   */
  public PidcAddStructAttrTableLabelProvider(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
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

    String columnText = "";
    if (element instanceof IProjectAttribute) {

      IProjectAttribute pidcAttr = (IProjectAttribute) element;
      Attribute attr = this.pidcVersionBO.getPidcDataHandler().getAttributeMap().get(pidcAttr.getAttrId());

      switch (columnIndex) {
        case 0:
          columnText = attr.getName();
          break;
        case 1:
          columnText = attr.getDescription();
          break;

        default:
          columnText = "";
          break;
      }
    }
    return columnText;
  }


}
