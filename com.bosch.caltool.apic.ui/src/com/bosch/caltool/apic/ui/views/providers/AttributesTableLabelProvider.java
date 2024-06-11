package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author dmo5cob
 */
public class AttributesTableLabelProvider implements ITableLabelProvider, IColorProvider {

  private final AttributesPage attributesPage;


  /**
   * The parameterized constructor
   *
   * @param attributesPage instance
   */
  public AttributesTableLabelProvider(final AttributesPage attributesPage) {

    this.attributesPage = attributesPage;
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
    if (element instanceof com.bosch.caltool.icdm.model.apic.attr.Attribute) {

      com.bosch.caltool.icdm.model.apic.attr.Attribute attr =
          (com.bosch.caltool.icdm.model.apic.attr.Attribute) element;
      switch (columnIndex) {
        case 0:
          result = attr.getName();
          break;
        case 1:
          result = attr.getDescription();
          break;
        case 2:
          result = attr.getValueType();
          break;
        default:
          result = "";
          break;
      }
    }
    else if (element instanceof AttrNValueDependency) {
      AttrNValueDependency attrDep = (AttrNValueDependency) element;
      switch (columnIndex) {
        case 0:
          result = attrDep.getName();
          break;
        case 1:
          result = attrDep.getValue();
          break;

        default:
          result = "";
          break;
      }
    }
    else if (element instanceof AttributeValue) {
      AttributeValue attrVal = (AttributeValue) element;
      switch (columnIndex) {
        case 0:// value
          result = attrVal.getName();
          break;
        // TODO - Deepthi
        case 1:// unit
          result = "";
          break;
        case 2:// description
          result = attrVal.getDescription();
          break;
        default:
          result = "";
          break;
      }
    }
    else if ((element != null) && (element instanceof AttrNValueDependency)) {
      AttrNValueDependency attrVal = (AttrNValueDependency) element;

      switch (columnIndex) {
        case 0:
          result = attrVal.getName();
          break;
        case 1:
          result = attrVal.getValue();
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
    if (element instanceof Attribute) {
      Attribute attr = (Attribute) element;
      if (attr.isDeleted()) {
        return this.attributesPage.getAttrTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

      }
      return this.attributesPage.getAttrTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
    }
    else if (element instanceof AttributeValue) {
      AttributeValue attrVal = (AttributeValue) element;
      if (attrVal.isDeleted()) {
        return this.attributesPage.getValueTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

      }
      return this.attributesPage.getValueTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
    }
    else if (element instanceof AttrNValueDependency) {
      AttrNValueDependency attrDep = (AttrNValueDependency) element;
      if (attrDep.isDeleted()) {
        return this.attributesPage.getAttrDepTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

      }
      return this.attributesPage.getAttrDepTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
    }
    else if (element instanceof AttrNValueDependency) {
      AttrNValueDependency attrValDep = (AttrNValueDependency) element;
      if (attrValDep.isDeleted()) {
        return this.attributesPage.getAttrDepTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
      }
      return this.attributesPage.getAttrDepTabViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
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
