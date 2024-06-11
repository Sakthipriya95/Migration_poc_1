/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;


/**
 * Super Group Property View Source
 *
 * @author bne4cob
 */
public class AttrSuperGrpPropViewSource implements IPropertySource {

  /**
   * Title
   */
  private static final String TITLE = "Title";

  /**
   * Name
   */
  private static final String NAME = "Name";
  /**
   * Description
   */
  private static final String DESCRIPTION = "Description";
  /**
   * Created User
   */
  private static final String CREATED_USER = "Created User";
  /**
   * Created Date
   */
  private static final String CREATED_DATE = "Created Date";
  /**
   * Modified User
   */
  private static final String MODIFIED_USER = "Modified User";
  /**
   * Modified Date
   */
  private static final String MODIFIED_DATE = "Modified Date";


  /**
   * Properties field
   */
  private static final String[] PROP_DESC_FIELDS =
      new String[] { NAME, DESCRIPTION, CREATED_USER, CREATED_DATE, MODIFIED_USER, MODIFIED_DATE };

  /**
   * Super Group
   */
  private final AttrSuperGroup superGrp;

  /**
   * Constructor
   *
   * @param superGrp Super Group
   */
  public AttrSuperGrpPropViewSource(final AttrSuperGroup superGrp) {
    this.superGrp = superGrp;
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
      result = this.superGrp.getNameEng();
    }
    if (DESCRIPTION.equals(objID)) {
      result = this.superGrp.getDescriptionEng();
    }
    if (CREATED_USER.equals(objID)) {
      result = this.superGrp.getCreatedUser();
    }
    if (CREATED_DATE.equals(objID)) {
      result = this.superGrp.getCreatedDate().toString();
    }
    if (MODIFIED_USER.equals(objID)) {
      result = this.superGrp.getModifiedUser();
    }
    if (MODIFIED_DATE.equals(objID) && (this.superGrp.getModifiedDate() != null)) {
      result = this.superGrp.getModifiedDate().toString();
    }
    if (TITLE.equals(objID)) {
      result = "SUPRGRP : " + this.superGrp.getName();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getEditableValue() {
    // Not applicable
    return null;
  }

}
