/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;

import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerCustFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerECUPlatWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerFunctionFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerParamFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerSysConFilterWizardPage;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFilterContentProvider implements IStructuredContentProvider {

  private final WizardPage page;

  /**
   * @param page - page instance
   */
  public CaldataAnalyzerFilterContentProvider(final WizardPage page) {
    this.page = page;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object arg0) {
    if (this.page instanceof CaldataAnalyzerParamFilterWizardPage) {
      return ((List<ParameterFilterLabel>) arg0).toArray();
    }
    if (this.page instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      return ((List<FunctionFilter>) arg0).toArray();
    }
    if (this.page instanceof CaldataAnalyzerSysConFilterWizardPage) {
      return ((List<SystemConstantFilter>) arg0).toArray();
    }
    if (this.page instanceof CaldataAnalyzerCustFilterWizardPage) {
      return ((List<CustomerFilter>) arg0).toArray();
    }
    if (this.page instanceof CaldataAnalyzerECUPlatWizardPage) {
      return ((List<PlatformFilter>) arg0).toArray();
    }
    return new Object[1];
  }

  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}

}
