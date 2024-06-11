/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;


/**
 * Group Property View Source
 *
 * @author bne4cob
 */
public class AttrGroupPropViewSource implements IPropertySource {

  /**
   * Title
   */
  private static final String TITLE = "Title";
  /**
   * Modified Date
   */
  private static final String MODIFIED_DATE = "Modified Date";
  /**
   * Modified User
   */
  private static final String MODIFIED_USER = "Modified User";
  /**
   * Created Date
   */
  private static final String CREATED_DATE = "Created Date";
  /**
   * Created User
   */
  private static final String CREATED_USER = "Created User";
  /**
   * Description
   */
  private static final String DESCRIPTION = "Description";
  /**
   * Name
   */
  private static final String NAME = "Name";
  /**
   * Super Group
   */
  private static final String SUPER_GROUP = "Super Group";

  /**
   * Properties field
   */
  private static final String[] PROP_DESC_FIELDS =
      new String[] { NAME, DESCRIPTION, SUPER_GROUP, CREATED_USER, CREATED_DATE, MODIFIED_USER, MODIFIED_DATE };

  /**
   * Super Group
   */
  private final AttrGroup attrGroup;

  /**
   * Constructor
   *
   * @param attrGroup Group
   */
  public AttrGroupPropViewSource(final AttrGroup attrGroup) {
    this.attrGroup = attrGroup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isPropertySet(final Object objID) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resetPropertyValue(final Object objID) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPropertyValue(final Object objID, final Object value) {
    // Not applicable
  }

  @Override
  public final IPropertyDescriptor[] getPropertyDescriptors() {
    return CommonUiUtils.createPropertyDescFields(PROP_DESC_FIELDS);
  }

  @Override
  public final Object getPropertyValue(final Object objID) {
    String result = "";
    if (NAME.equals(objID)) {
      result = this.attrGroup.getNameEng();
    }
    if (DESCRIPTION.equals(objID)) {
      result = this.attrGroup.getDescriptionEng();
    }
    if (SUPER_GROUP.equals(objID)) {
      result = new AttrGroupClientBO(this.attrGroup).getSuperGroup().getName();
    }
    if (CREATED_USER.equals(objID)) {
      result = this.attrGroup.getCreatedUser();
    }
    if (CREATED_DATE.equals(objID)) {
      result = this.attrGroup.getCreatedDate().toString();
    }
    if (MODIFIED_USER.equals(objID)) {
      result = this.attrGroup.getModifiedUser();
    }
    if (MODIFIED_DATE.equals(objID) && (this.attrGroup.getModifiedDate() != null)) {
      result = this.attrGroup.getModifiedDate().toString();
    }
    if (TITLE.equals(objID)) {
      result = "GRP : " + this.attrGroup.getName();
    }
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getEditableValue() {
    // TODO Auto-generated method stub
    return null;
  }


}
