/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;


/**
 * @author rvu1cob
 */
// ICDM-81
public class SysConstFormPagePropertiesViewSource implements IPropertySource {


  private final A2LSystemConstantValues systemConstant;

  private final String[] propDescFields = new String[] { "Name", "Long Name", "Value", "Value Description" };

  /**
   * @param systemConstant
   */
  public SysConstFormPagePropertiesViewSource(final A2LSystemConstantValues systemConstant) {
    this.systemConstant = systemConstant;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getEditableValue() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    List<IPropertyDescriptor> propertyDescriptorsList = new ArrayList<IPropertyDescriptor>();

    // ICDM-54
    String categoryTitle = " ";
    String title = "Title";
    PropertyDescriptor descriptorTitle = createTxtDesciptor(title, title, categoryTitle);
    propertyDescriptorsList.add(descriptorTitle);

    String category = "Info";

    for (String propDesc : this.propDescFields) {
      PropertyDescriptor descriptor = createTxtDesciptor(propDesc, propDesc, category);
      propertyDescriptorsList.add(descriptor);
    }

    IPropertyDescriptor[] propDescriptors = new IPropertyDescriptor[propertyDescriptorsList.size()];
    propertyDescriptorsList.toArray(propDescriptors);

    return propDescriptors;
  }

  /**
   * Create TextPropertyDescriptor
   *
   * @param objId
   * @param displayName
   * @param category
   * @return
   */
  private PropertyDescriptor createTxtDesciptor(final Object objId, final String displayName, final String category) {
    PropertyDescriptor txtDescriptor = new PropertyDescriptor(objId, displayName);
    if ((category != null) && (category.length() > 0)) {
      txtDescriptor.setCategory(category);
    }

    return txtDescriptor;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getPropertyValue(final Object objId) {
    if ("Name".equals(objId)) {
      return this.systemConstant.getSysconName();
    }
    if ("Value".equals(objId)) {
      return this.systemConstant.getValue();
    }
    if ("Long Name".equals(objId)) {
      return this.systemConstant.getSysconLongName();
    }
    if ("Value Description".equals(objId)) {
      return this.systemConstant.getValueDescription();
    }
    // ICDM-54
    if ("Title".equals(objId)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "SYSCONST : " + this.systemConstant.getSysconName() + "- A2L : " + tilteName;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPropertySet(final Object objId) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resetPropertyValue(final Object objId) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPropertyValue(final Object objId, final Object value) {
    // TODO Auto-generated method stub

  }


}
