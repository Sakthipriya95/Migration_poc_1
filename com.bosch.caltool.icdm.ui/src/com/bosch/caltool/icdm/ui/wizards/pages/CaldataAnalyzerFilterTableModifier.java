/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.wizard.WizardPage;

import com.bosch.caltool.icdm.model.cda.FunctionFilter;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFilterTableModifier implements ICellModifier {

  private final WizardPage page;


  /**
   * @param page - instance of filter page
   */
  public CaldataAnalyzerFilterTableModifier(final WizardPage page) {
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canModify(final Object arg0, final String arg1) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getValue(final Object object, final String arg1) {
    if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      FunctionFilter function = (FunctionFilter) object;
      return function;
    }
    return object;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void modify(final Object element, final String property, final Object value) {

    if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      FunctionFilter function = (FunctionFilter) element;
      String functionVersion = (String) value;
      function.setFunctionVersion(functionVersion);

    }
  }

}
