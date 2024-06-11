/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.model.cdr.AbstractParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;


/**
 * @author dmo5cob Properties view source class to display properties of Paramter Attribute
 */
public class CDRParamAttrPropertiesViewSource extends AbstractDataObjectPropertySource<AbstractParameterAttribute> {


  /**
   * Unit
   */
  private static final String UNIT = "Unit";

  private final ParameterDataProvider<IParameterAttribute, IParameter> paramDataProvider;

  /**
   * Constructor
   *
   * @param adaptableObject instance of CDRFuncParameter for which properties have to be displayed
   */
  public CDRParamAttrPropertiesViewSource(final IParameterAttribute adaptableObject,
      final ParameterDataProvider<IParameterAttribute, IParameter> paramDataProvider) {
    super((AbstractParameterAttribute) adaptableObject);
    this.paramDataProvider = paramDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {

    return new String[] { UNIT };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String descField) {


    if (UNIT.equals(descField)) {
      return this.paramDataProvider.getAttribute(getDataObject()).getUnit();
    }

    return "";


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    return "PAR ATTRIBUTE : " + getDataObject().getName();
  }

}
