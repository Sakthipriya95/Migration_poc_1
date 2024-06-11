/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.propertysource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * Abstract class to property source. This implementation provides a separate section called 'Title' to show a title of
 * the object being explained. Also disables the facility to modify of properties by users.
 * 
 * @author bne4cob
 */
// ICDM-1314
public abstract class AbstractPropertySource implements IPropertySource {

  /**
   * Title category
   */
  public static final String CATEGORY_TITLE = " ";
  /**
   * Title property
   */
  public static final String PROP_TITLE = "Title";
  /**
   * Info category
   */
  public static final String CATEGORY_INFO = "Info";

  /**
   * Empty size for arrays
   */
  private static final int EMPTY_ARR_SIZE = 0;


  /**
   * Description fields List
   */
  private final List<String> descFieldsList = new ArrayList<>();

  /**
   * Constructor
   * 
   * @param descFields property description fields for the implementation. The fields will be displayed in the
   *          properties in the given order
   */
  protected AbstractPropertySource(final String... descFields) {
    if (descFields != null) {
      this.descFieldsList.addAll(Arrays.asList(descFields));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    final List<IPropertyDescriptor> propDescList = new ArrayList<IPropertyDescriptor>();

    final PropertyDescriptor descriptorTitle = createTxtDesciptor(PROP_TITLE, PROP_TITLE, CATEGORY_TITLE);
    propDescList.add(descriptorTitle);

    // To support dynamic definition of description fields, the method implementation is also considered
    String[] descFieldFromImpl = getDescFields();
    // if array is not empty, use these as the descriptor fields.
    if (descFieldFromImpl.length > 0) {
      this.descFieldsList.clear();
      this.descFieldsList.addAll(Arrays.asList(descFieldFromImpl));
    }

    PropertyDescriptor descriptor;
    for (String propDescField : this.descFieldsList) {
      descriptor = createTxtDesciptor(propDescField, propDescField, CATEGORY_INFO);
      propDescList.add(descriptor);
    }

    final IPropertyDescriptor[] propDescriptors = new IPropertyDescriptor[propDescList.size()];
    propDescList.toArray(propDescriptors);

    return propDescriptors;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getPropertyValue(final Object propID) {
    if (PROP_TITLE.equals(propID)) {
      return getTitle();
    }
    return getStrPropertyValue((String) propID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getEditableValue() {
    // not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isPropertySet(final Object arg0) {
    // not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void resetPropertyValue(final Object arg0) {
    // not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void setPropertyValue(final Object arg0, final Object arg1) {
    // not applicable
  }

  /**
   * Utility method to create TextPropertyDescriptor instance using the given inputs
   * 
   * @param objID id of the property descriptor
   * @param displayName Display name of the property
   * @param category Category of the property
   * @return PropertyDescriptor instance
   */
  protected final PropertyDescriptor createTxtDesciptor(final Object objID, final String displayName,
      final String category) {
    final PropertyDescriptor txtDescriptor = new PropertyDescriptor(objID, displayName);
    if ((category != null) && (category.length() > EMPTY_ARR_SIZE)) {
      txtDescriptor.setCategory(category);
    }
    return txtDescriptor;

  }

  /**
   * Returns the property value for the given key. The possible keys are those given by the implementation, during the
   * object construction.
   * 
   * @param propKey property key
   * @return property value
   */
  protected abstract Object getStrPropertyValue(String propKey);

  /**
   * @return title of the object
   */
  protected abstract String getTitle();

  /**
   * Returs the description fields. NOTE The method should be overridden if the description fields are defined
   * dynamically ie. based on some condition
   * 
   * @return the description fields. The fields will be displayed in the properties in the given order
   */
  protected String[] getDescFields() {
    return new String[] {};
  }

}
