/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;


// ICDM - 293
/**
 * This class is used to get the use case item properties that are to be displayed in the property sheet
 *
 * @author mkl2cob
 */
public class UseCaseItemPropertiesViewSource extends AbstractDataObjectPropertySource<IUseCaseItemClientBO> {

  /**
   * Key for Relevant to Focus Matrix
   */
  private static final String PROP_FOCUS_MATRIX_RELVNT = "Relevant to Focus Matrix";

  /**
   * The parameterized constructor
   *
   * @param useCaseItem use case item instance
   */
  public UseCaseItemPropertiesViewSource(final IUseCaseItemClientBO useCaseItem) {
    super(useCaseItem);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String value = "";
    if (PROP_FOCUS_MATRIX_RELVNT.equals(propKey)) {
      value = getDataObject().getFocusMatrixRelevantStr();
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    IUseCaseItemClientBO ucItem = getDataObject();

    String prefix = "";
    if (ucItem instanceof UseCaseGroupClientBO) {// UseCaseGroup
      prefix = "UCGROUP : ";
    }
    else if (ucItem instanceof UsecaseClientBO) { // UseCase
      prefix = "USECASE : ";
    }
    else if (ucItem instanceof UseCaseSectionClientBO) { // UseCaseSection
      prefix = "UCSECTION : ";
    }
    return prefix + ucItem.getName();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    return new String[] { PROP_FOCUS_MATRIX_RELVNT };
  }

}
