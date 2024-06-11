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

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;

/**
 * @author rvu1cob
 */
// ICDM-57
public class ParameterPropertiesViewSource implements IPropertySource {

  /**
   * Instance of Characteristic
   */
  private final A2LParameter param;

  /**
   * Property Desciptor field display value //Icdm-633 changes
   */
  private final String[] propDescFields = new String[] {
      "Name",
      "Type",
      "Unit",
      "Lower limit",
      "Upper limit",
      "Extended Lower limit",
      "Extended Upper limit",
      "Class",
      "Codeword" };

  /**
   * Property Desciptor field display value //Icdm-633 changes
   */
  private final String[] swHistoryPropDescFields = new String[] {
      "Value",
      "Status",
      "User",
      "Date",
      "Work Package",
      "Project Info",
      "Destination Variant",
      "Test Object",
      "Program Identifier",
      "Data Identifier",
      "Notes" };


  /**
   * @param parametersFormPageData
   */
  @SuppressWarnings("javadoc")
  public ParameterPropertiesViewSource(final A2LParameter parametersFormPageData) {

    this.param = parametersFormPageData;
  }


  @Override
  public final Object getEditableValue() {
    return this;
  }


  @Override
  public final IPropertyDescriptor[] getPropertyDescriptors() {

    List<IPropertyDescriptor> propertyDescriptorsList = new ArrayList<IPropertyDescriptor>();

    // ICDM-54
    String categoryTitle = " ";

    String title = "Title";

    PropertyDescriptor descriptorTitle = createTxtDesciptor(title, title, categoryTitle);
    propertyDescriptorsList.add(descriptorTitle);

    String category = "Info";
    for (String propDescField : this.propDescFields) {
      PropertyDescriptor descriptor = createTxtDesciptor(propDescField, propDescField, category);
      propertyDescriptorsList.add(descriptor);
    }

    if (this.param.getCalData() != null) {
      String swHistoryCategory = "SW-HISTORY";
      for (String propDescField : this.swHistoryPropDescFields) {
        PropertyDescriptor descriptor = createTxtDesciptor(propDescField, propDescField, swHistoryCategory);
        propertyDescriptorsList.add(descriptor);
      }
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

  @Override
  public final Object getPropertyValue(final Object objVal) {
    // Icdm-633 Changes Using A2LParameter Obj
    if ("Name".equals(objVal)) {
      return this.param.getName();
    }
    if ("Type".equals(objVal)) {
      return this.param.getType();
    }
    if ("Unit".equals(objVal)) {
      return this.param.getUnit();
    }
    if ("Lower limit".equals(objVal)) {
      return this.param.getCharacteristic().getLowerLimit();
    }
    if ("Upper limit".equals(objVal)) {
      return this.param.getCharacteristic().getUpperLimit();
    }
    if ("Extended Lower limit".equals(objVal)) {
      return this.param.getCharacteristic().getExtLowerLimit();
    }
    if ("Extended Upper limit".equals(objVal)) {
      return this.param.getCharacteristic().getExtUpperLimit();
    }
    // Icdm-633
    if ("Class".equals(objVal)) {
      return this.param.getPclassString();
    }
    if ("Codeword".equals(objVal)) {
      return this.param.getCodeWordString();
    }
    // ICDM-54
    if ("Title".equals(objVal)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "PAR : " + this.param.getName() + "- A2L : " + tilteName;
    }

    return getSWHistoryData(objVal);
  }


  /**
   * @param objId
   */
  private Object getSWHistoryData(final Object objId) {
    // ICDM-868

    if (this.param.getCalData() == null) {
      return null;
    }

    if ("Value".equals(objId)) {
      return (this.param.getCalData().getCalDataPhy() == null) ? ""
          : this.param.getCalData().getCalDataPhy().getSimpleDisplayValue();
    }

    if (this.param.getLatestHistoryEntry() == null) {
      return null;
    }

    if ("Status".equals(objId)) {
      return this.param.getStatus();
    }

    if ("User".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getPerformedBy() == null) ? ""
          : this.param.getLatestHistoryEntry().getPerformedBy().getValue();
    }
    if ("Date".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getDate() == null) ? ""
          : this.param.getLatestHistoryEntry().getDate().getValue();
    }
    if ("Work Package".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getContext() == null) ? ""
          : this.param.getLatestHistoryEntry().getContext().getValue();
    }
    if ("Project Info".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getProject() == null) ? ""
          : this.param.getLatestHistoryEntry().getProject().getValue();
    }
    if ("Destination Variant".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getTargetVariant() == null) ? ""
          : this.param.getLatestHistoryEntry().getTargetVariant().getValue();
    }
    if ("Test Object".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getTestObject() == null) ? ""
          : this.param.getLatestHistoryEntry().getTestObject().getValue();
    }
    if ("Program Identifier".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getProgramIdentifier() == null) ? ""
          : this.param.getLatestHistoryEntry().getProgramIdentifier().getValue();
    }
    if ("Data Identifier".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getDataIdentifier() == null) ? ""
          : this.param.getLatestHistoryEntry().getDataIdentifier().getValue();
    }
    if ("Notes".equals(objId)) {
      return (this.param.getLatestHistoryEntry().getRemark() == null) ? ""
          : this.param.getLatestHistoryEntry().getRemark().getValue();
    }

    return null;
  }

  @Override
  public final boolean isPropertySet(final Object objId) {
    return false;
  }

  @Override
  public void resetPropertyValue(final Object objId) {
    // not applicable
  }

  @Override
  public void setPropertyValue(final Object objId, final Object value) {
    // not applicable
  }

  /**
   * {@inheritDoc}
   */

}
