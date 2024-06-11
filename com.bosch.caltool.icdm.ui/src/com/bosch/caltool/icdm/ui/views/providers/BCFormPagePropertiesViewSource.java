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

import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;

/**
 * @author dmo5cob
 */
public class BCFormPagePropertiesViewSource implements IPropertySource {


  private final A2LBaseComponents bcObject;

  private final String[] propDescFields = new String[] { "Name", "Version", "Revision", "State", "Description" };

  /**
   * @param bcObject bc instance
   */
  public BCFormPagePropertiesViewSource(final A2LBaseComponents bcObject) {
    this.bcObject = bcObject;
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
    // set property description title
    String categoryTitle = " ";
    String title = "Title";
    PropertyDescriptor descriptorTitle = createTxtDesciptor(title, title, categoryTitle);
    propertyDescriptorsList.add(descriptorTitle);

    String category = "Info";

    // set property description fields
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
      return this.bcObject.getBcName();
    }
    if ("Version".equals(objId)) {
      return this.bcObject.getBcVersion();
    }
    if ("Revision".equals(objId)) {
      return this.bcObject.getRevision();
    }
    if ("State".equals(objId)) {
      return this.bcObject.getState();
    }
    if ("Description".equals(objId)) {
      return this.bcObject.getLongName();
    }
    if ("Title".equals(objId)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "BC : " + this.bcObject.getBcName() + "- A2L : " + tilteName;
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
