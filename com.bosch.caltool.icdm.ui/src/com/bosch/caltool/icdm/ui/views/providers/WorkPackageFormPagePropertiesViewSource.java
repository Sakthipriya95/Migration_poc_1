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

import com.bosch.caltool.icdm.model.a2l.A2lWpObj;

/**
 * @author dmo5cob
 */
public class WorkPackageFormPagePropertiesViewSource implements IPropertySource {


  private final A2lWpObj workPackage;

  private final String[] propDescFields = new String[] { "Name", "Group Name", "WP Number" };

  /**
   * @param a2lGroup group instance
   */
  public WorkPackageFormPagePropertiesViewSource(final A2lWpObj workPackage) {
    this.workPackage = workPackage;
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
      return this.workPackage.getWpName();
    }
    if ("Group Name".equals(objId)) {
      return this.workPackage.getWpGroupName();
    }
    if ("WP Number".equals(objId)) {
      return this.workPackage.getWpNumber();
    }
    if ("Title".equals(objId)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "WP : " + this.workPackage.getWpName() + "- A2L : " + tilteName;
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
