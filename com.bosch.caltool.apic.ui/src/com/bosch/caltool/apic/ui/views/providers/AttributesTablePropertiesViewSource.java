/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.icdm.client.bo.apic.AttributesDataHandler;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * class for Showing the Attribute in the properties View.
 *
 * @author rvu1cob
 */
// ICDM-83
public class AttributesTablePropertiesViewSource extends AbstractDataObjectPropertySource<Attribute> {

  /**
   * Attribute characteristic definition
   */
  private static final String PROP_CHARACTERISTIC = ApicConstants.CHARACTERISTIC;
  /**
   * Number format property
   */
  private static final String PROP_NUMBER_FORMAT = "Number Format";
  /**
   * Is normalised property
   */
  private static final String PROP_NORMALIZED = "Is Normalized";
  /**
   * Is Mandatory property
   */
  private static final String PROP_MANDATORY = "Is Mandatory(Default)";
  /**
   * Super Group property
   */
  private static final String PROP_SUPER_GROUP = "Super Group";
  /**
   * Group property
   */
  private static final String PROP_GROUP = "Group";
  /**
   * Unit property
   */
  private static final String PROP_UNIT = "Unit";
  /**
   * Value Type property
   */
  private static final String PROP_VALUE_TYPE = "Value Type";
  // ICDM-374
  /**
   * Has PartNumber property
   */
  private static final String PROP_PART_NUMBER = "Has PartNumber";
  /**
   * Has Specification property
   */
  private static final String PROP_SPECIFICATION = "Has Specification";

  /**
   * Is External Attribute property
   */
  private static final String PROP_ATTR_EXT = "Is External Attribute";

  /**
   * Is External Value property
   */
  private static final String PROP_VAL_EXT = "Is External Value";
  /**
   * Change Comment property
   */
  private static final String PROP_CHANGE_CMT = "Change Comment";
  /**
   * EADM Name property
   */
  private static final String PROP_EADM_NAME = "EADM Name";
  /**
   * Add Value property to restrict all users from adding values for some attribute
   */
  private static final String PROP_ADD_VAL_FLAG = "Values Can Be Created By Users";

  /**
   * Constructor
   * 
   * @param attribute instance
   */
  public AttributesTablePropertiesViewSource(final Attribute attribute) {
    super(attribute);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = getAttrGeneralProps(propKey);
    if (result != null) {
      return result;
    }

    result = getAttrParentProps(propKey);
    if (result != null) {
      return result;
    }

    return getAttrValTypeProps(propKey);

  }

  /**
   * Attribute group, super group
   *
   * @param propKey property key
   * @return property value. null, if key is not applicable
   */
  private String getAttrParentProps(final String propKey) {
    String result = null;
    Attribute attribute = getDataObject();
    AttrGroupModel attrGrpModel = getAttrGrpModel();
    if (null != attrGrpModel) {
      if (PROP_GROUP.equals(propKey)) {
        result = attrGrpModel.getAllGroupMap().get(attribute.getAttrGrpId()).getName();
      }
      else if (PROP_SUPER_GROUP.equals(propKey)) {
        Long superGrpId = attrGrpModel.getAllGroupMap().get(attribute.getAttrGrpId()).getSuperGrpId();
        result = attrGrpModel.getAllSuperGroupMap().get(superGrpId).getName();
      }
    }
    return result;
  }

  /**
   * General attribute properties
   *
   * @param propKey property key
   * @return property value. null, if key is not applicable
   */
  private String getAttrGeneralProps(final String propKey) {
    Attribute attribute = getDataObject();

    String result = null;
    if (PROP_MANDATORY.equals(propKey)) {
      result = attribute.isMandatory() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    // ICDM-374
    else if (PROP_PART_NUMBER.equals(propKey)) {
      result = attribute.isWithPartNumber() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    else if (PROP_SPECIFICATION.equals(propKey)) {
      result = attribute.isWithSpecLink() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    else if (PROP_ATTR_EXT.equals(propKey)) {
      result = attribute.isExternal() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    else if (PROP_CHARACTERISTIC.equals(propKey)) {
      result = attribute.getCharStr();
    }
    // ICDM-1397
    else if (PROP_CHANGE_CMT.equals(propKey)) {
      if (null != attribute.getChangeComment()) {
        result = attribute.getChangeComment().replace("\n", " ");
      }
    }
    // ICDM-1560
    else if (PROP_EADM_NAME.equals(propKey)) {
      result = attribute.getEadmName();
    }
    else if (PROP_ADD_VAL_FLAG.equals(propKey)) {
      result = attribute.isAddValByUserFlag() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    return result;
  }


  /**
   * 491575 - Defect Fix - Failing Test Case "INT:TC31:325288:Validate Group and Super Group Values are not displayed in
   * Properties view of an attribute and attribute editor." Contain all group and super group map
   *
   * @return
   */
  public AttrGroupModel getAttrGrpModel() {
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (wbWindows.length == 0) {
      return null;
    }
    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();
    for (IEditorReference editor : editorRefArr) {
      if (editor.getPart(false) instanceof AttributesEditor) {
        AttributesDataHandler dataHandler =
            (AttributesDataHandler) ((AttributesEditor) editor.getPart(false)).getAttrPage().getDataHandler();
        return dataHandler.getAttrGroupModel();
      }
    }
    return null;
  }


  /**
   * Attribute value related defining properties
   *
   * @param propKey property key
   * @return property value. null, if key is not applicable
   */
  private String getAttrValTypeProps(final String propKey) {
    Attribute attribute = getDataObject();

    String result = null;

    if (PROP_VALUE_TYPE.equals(propKey)) {
      result = attribute.getValueType();
    }
    else if (PROP_UNIT.equals(propKey)) {
      result = attribute.getUnit();
    }
    else if (PROP_NUMBER_FORMAT.equals(propKey) && (attribute.getFormat() != null)) {
      result = attribute.getFormat();
    }
    else if (PROP_VAL_EXT.equals(propKey)) {
      result = attribute.isExternal() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    if (PROP_NORMALIZED.equals(propKey)) {
      result = attribute.isNormalized() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    return new String[] {
        PROP_EADM_NAME,
        PROP_VALUE_TYPE,
        PROP_UNIT,
        PROP_GROUP,
        PROP_SUPER_GROUP,
        PROP_MANDATORY,
        PROP_NORMALIZED,
        PROP_PART_NUMBER,
        PROP_SPECIFICATION,
        PROP_NUMBER_FORMAT,
        PROP_ATTR_EXT,
        PROP_VAL_EXT,
        PROP_CHARACTERISTIC,
        PROP_CHANGE_CMT,
        PROP_ADD_VAL_FLAG };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    return "ATTR : " + getDataObject().getName();
  }

}
