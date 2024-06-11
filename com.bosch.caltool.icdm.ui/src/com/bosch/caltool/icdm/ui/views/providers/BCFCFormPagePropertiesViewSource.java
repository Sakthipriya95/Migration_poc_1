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

import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;

/**
 * @author dmo5cob
 */
public class BCFCFormPagePropertiesViewSource implements IPropertySource {


  private final A2LBaseComponentFunctions bcFCObj;

  private final String[] propDescFields = new String[] { "Name", "Version", "Long Name", "Module ID", "SDOMBCID" };

  /**
   * @param a2lGroup group instance
   */
  public BCFCFormPagePropertiesViewSource(final A2LBaseComponentFunctions bcFCObj) {
    this.bcFCObj = bcFCObj;
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
    // Create property descriptor file
    PropertyDescriptor descriptorTitle = createTxtDesciptor(title, title, categoryTitle);
    propertyDescriptorsList.add(descriptorTitle);

    String category = "Info";

    // Set property description fields
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
      return this.bcFCObj.getName();
    }
    if ("Long Name".equals(objId)) {
      return this.bcFCObj.getLongidentifier();
    }

    if ("Version".equals(objId)) {
      return this.bcFCObj.getFunctionversion();
    }
    if ("Module ID".equals(objId)) {
      return this.bcFCObj.getModuleId();
    }
    if ("SDOMBCID".equals(objId)) {
      return this.bcFCObj.getSdomBcId();
    }
    if ("Title".equals(objId)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "FC : " + this.bcFCObj.getName() + "- A2L : " + tilteName;
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
