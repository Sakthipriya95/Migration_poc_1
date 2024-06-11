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

import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;


/**
 * @author rvu1cob
 */
public class FCFormPagePropertiesViewSource implements IPropertySource {

  private final Function function;

  private final String[] propDescFields = new String[] {
      "Name",
      "LongName",
      "Version",
      "No of DEF_Characteristics",
      "No of REF_Characteristics",
      "No of SUB_Functions",
      "No of IN_Measurements",
      "No of OUT_Measurements",
      "No of LOC_Measurements" };

  /**
   * @param function Function
   */
  public FCFormPagePropertiesViewSource(final Function function) {
    this.function = function;

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
      return this.function.getName();
    }
    else if ("LongName".equals(objId)) {
      return this.function.getLongIdentifier();
    }
    else if ("Version".equals(objId)) {
      return this.function.getFunctionVersion();
    }
    else if ("No of DEF_Characteristics".equals(objId)) {
      if (this.function.getDefCharRefList() != null) {

        return this.function.getDefCharRefList().size();
      }
    }
    else if ("No of REF_Characteristics".equals(objId)) {
      if (this.function.getRefCharRefList() != null) {
        return this.function.getRefCharRefList().size();
      }
    }
    else if ("No of SUB_Functions".equals(objId)) {

      if (this.function.getSubFuncRefList() != null) {
        return this.function.getSubFuncRefList().size();
      }
    }
    else if ("No of IN_Measurements".equals(objId)) {

      LabelList inMeasure = this.function.getLabelList(LabelType.IN_MEASUREMENT);
      if (inMeasure != null) {

        return inMeasure.size();
      }

    }
    else if ("No of OUT_Measurements".equals(objId)) {
      LabelList out = this.function.getLabelList(LabelType.OUT_MEASUREMENT);

      if (out != null) {
        return out.size();
      }
    }
    else if ("No of LOC_Measurements".equals(objId)) {
      LabelList loc = this.function.getLabelList(LabelType.LOC_MEASUREMENT);
      if (loc != null) {
        return loc.size();
      }
    }
    // ICDM-54
    if ("Title".equals(objId)) {
      String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "FC : " + this.function.getName() + "- A2L : " + tilteName;
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
