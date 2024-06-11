package com.bosch.caltool.usecase.ui.views.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.usecase.ui.views.AttributeValuesViewPart;


/**
 * @author dmo5cob
 */
public class UseCaseAttributeValuesTableLabelProvider implements ITableLabelProvider, IColorProvider {


  private final AttributeValuesViewPart attributeValuesViewPart;

  /**
   * @param attributeValuesViewPart attributeValuesViewPart
   */
  public UseCaseAttributeValuesTableLabelProvider(final AttributeValuesViewPart attributeValuesViewPart) {
    this.attributeValuesViewPart = attributeValuesViewPart;
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
    if (element instanceof AttributeValue) {
      final AttributeValue paramData = (AttributeValue) element;
      switch (columnIndex) {
        case 0:
          result = paramData.getNameRaw();
          break;
        case 1:
          result = paramData.getUnit();
          break;
        case 2:
          result = paramData.getDescription();
          break;
        default:
          result = "";
          break;
      }
    }


    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {
    if (element instanceof AttributeValue) {
      final AttributeValue attrVal = (AttributeValue) element;
      if (attrVal.isDeleted()) {
        return this.attributeValuesViewPart.getValueTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

      }
      return this.attributeValuesViewPart.getValueTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getBackground(final Object element) {
    return null;
  }

}
