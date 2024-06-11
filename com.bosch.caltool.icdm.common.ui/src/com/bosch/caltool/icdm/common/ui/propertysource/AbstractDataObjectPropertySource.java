/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.propertysource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.propertysource.AbstractPropertySource;


/**
 * Abstract class definition of IPropertySource to give support to AbstractDataObject implmentations
 *
 * @author bne4cob
 * @param <D> instance of Abstract Data Object
 */
// ICDM-1314
public abstract class AbstractDataObjectPropertySource<D extends IDataObject>
    extends AbstractPropertySource {

  /**
   * Name property
   */
  public static final String PROP_NAME = "Name";
  /**
   * Description property
   */
  public static final String PROP_DESCRIPTION = "Description";
  /**
   * Created date property
   */
  public static final String PROP_CREATED_DATE = "Created Date";
  /**
   * Created User Property
   */
  public static final String PROP_CREATED_USER = "Created User";
  /**
   * Modified date property
   */
  public static final String PROP_MODIFIED_DATE = "Modified Date";
  /**
   * Modified user property
   */
  public static final String PROP_MODIFIED_USER = "Modified User";

  /**
   * Map keep flags to enable display of common fields
   */
  private final Map<String, Boolean> showCommonFieldsMap = new ConcurrentHashMap<>();

  /**
   * Data object being used
   */
  private D dataObject;

  /**
   * List of property fields used, that are given by implementation
   */
  private final List<String> descFieldsList = new ArrayList<>();

  /**
   * Constructor in which all common fields will be displayed
   *
   * @param dataObject data object
   * @param descFields property description fields for the implementation. The fields will be displayed in the
   *          Properties View in the given order
   */
  protected AbstractDataObjectPropertySource(final D dataObject, final String... descFields) {
    this(dataObject, descFields, true, null);
  }

  /**
   * Constructor in which common fields will be displayed according to the flag for each field given in showCommonFields
   * map. The items not given will not be displayed.
   * <p>
   * The keys to be used for common fields are given below
   * <p>
   * Name - <code>AbstractDataObjectPropertySource.PROP_NAME</code> <br>
   * Description - <code>AbstractDataObjectPropertySource.PROP_DESCRIPTION</code> <br>
   * Created Date - <code>AbstractDataObjectPropertySource.PROP_CREATED_DATE</code> <br>
   * Created User - <code>AbstractDataObjectPropertySource.PROP_CREATED_USER</code> <br>
   * Modified Date - <code>AbstractDataObjectPropertySource.PROP_MODIFIED_DATE</code> <br>
   * Modified User - <code>AbstractDataObjectPropertySource.PROP_MODIFIED_USER</code>
   *
   * @param dataObject data object
   * @param descFields property description fields for the implementation. If given <code>null</code>, the method
   *          <code>getDescFields()</code> should be overridden
   * @param defaultShowCommonFields default flag for all fields
   * @param showCommonFieldStatus flag for the specific fields, to indicate whether the common fields should be
   *          displayed
   */
  protected AbstractDataObjectPropertySource(final D dataObject, final String[] descFields,
      final boolean defaultShowCommonFields, final Map<String, Boolean> showCommonFieldStatus) {

    // The description fields in the parent class is not used. They are separately maintained in this class.
    // No arguments passed due to varargs
    super();

    this.dataObject = dataObject;

    if (descFields != null) {
      this.descFieldsList.addAll(Arrays.asList(descFields));
    }
    this.showCommonFieldsMap.put(PROP_NAME, defaultShowCommonFields);
    this.showCommonFieldsMap.put(PROP_DESCRIPTION, defaultShowCommonFields);
    this.showCommonFieldsMap.put(PROP_CREATED_DATE, defaultShowCommonFields);
    this.showCommonFieldsMap.put(PROP_CREATED_USER, defaultShowCommonFields);
    this.showCommonFieldsMap.put(PROP_MODIFIED_DATE, defaultShowCommonFields);
    this.showCommonFieldsMap.put(PROP_MODIFIED_USER, defaultShowCommonFields);

    if (showCommonFieldStatus != null) {
      this.showCommonFieldsMap.putAll(showCommonFieldStatus);
    }

  }

  /**
   * Provides the property descriptors using the input given by the implementation. The common fields are also included,
   * based on the flags provided.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final IPropertyDescriptor[] getPropertyDescriptors() {
    final List<IPropertyDescriptor> propDescList = new ArrayList<IPropertyDescriptor>();

    final PropertyDescriptor descriptorTitle = createTxtDesciptor(PROP_TITLE, PROP_TITLE, CATEGORY_TITLE);
    propDescList.add(descriptorTitle);

    addCommonProperty(propDescList, PROP_NAME);
    addCommonProperty(propDescList, PROP_DESCRIPTION);

    // To support dynamic definition of description fields, the method implementation is also considered
    String[] descFieldFromImpl = getDescFields();
    // if array is not empty, use these as the descriptor fields.
    if (descFieldFromImpl.length > ApicConstants.EMPTY_ARR_SIZE) {
      this.descFieldsList.clear();
      this.descFieldsList.addAll(Arrays.asList(descFieldFromImpl));
    }

    PropertyDescriptor descriptor;
    for (String propDescField : this.descFieldsList) {
      descriptor = createTxtDesciptor(propDescField, propDescField, CATEGORY_INFO);
      propDescList.add(descriptor);
    }

    addCommonProperty(propDescList, PROP_CREATED_DATE);
    addCommonProperty(propDescList, PROP_CREATED_USER);
    addCommonProperty(propDescList, PROP_MODIFIED_DATE);
    addCommonProperty(propDescList, PROP_MODIFIED_USER);

    final IPropertyDescriptor[] propDescriptors = new IPropertyDescriptor[propDescList.size()];
    propDescList.toArray(propDescriptors);

    return propDescriptors;
  }

  /**
   * Adds text descriptor for the given common property
   *
   * @param propDescList destination
   * @param prop common property
   */
  private void addCommonProperty(final List<IPropertyDescriptor> propDescList, final String prop) {
    if (!this.descFieldsList.contains(prop) && this.showCommonFieldsMap.get(prop)) {
      propDescList.add(createTxtDesciptor(prop, prop, CATEGORY_INFO));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getPropertyValue(final Object propID) {
    Object propValue;
    switch (propID.toString()) {
      case PROP_TITLE:
        propValue = getTitle();
        break;
      case PROP_NAME:
        propValue = CommonUtils.checkNull(this.dataObject.getName());
        break;
      case PROP_DESCRIPTION:
        propValue = CommonUtils.checkNull(this.dataObject.getDescription());
        break;
      case PROP_CREATED_DATE:
        propValue = this.dataObject.getCreatedDate();
        break;
      case PROP_CREATED_USER:
        propValue = getUserDisplayName(this.dataObject.getCreatedUser());
        break;
      case PROP_MODIFIED_DATE:
        propValue = this.dataObject.getModifiedDate();
        break;
      case PROP_MODIFIED_USER:
        propValue = getUserDisplayName(this.dataObject.getModifiedUser());
        break;
      default:
        propValue = getStrPropertyValue((String) propID);
    }
    return propValue;
  }

  /**
   * @return the data object used
   */
  protected final D getDataObject() {
    return this.dataObject;
  }

  /**
   * Get the display name of the user with the given NT ID
   *
   * @param ntName NT ID
   * @return display name
   */
  private String getUserDisplayName(final String ntName) {
    return ntName;
  }
}
