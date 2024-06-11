/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;


/**
 * @author rgo7cob Icdm-702 first page of the export wizard
 */
public class PreCalSourceSelectionPage extends WizardPage {

  /**
   *
   */
  private Button cdrRuleSel;
  private Button seriesStatSel;
  private Button ruleSetSel;

  /**
   * @param pageName page Name
   * @param a2lFileName a2l File Name
   */
  public PreCalSourceSelectionPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    setDescription(CommonUiUtils.getAdditionalHint() + "\n" + "Please select the data source");
    setPageComplete(false);
  }

  /**
   * @return the ruleSetSel
   */
  public Button getRuleSetSel() {
    return this.ruleSetSel;
  }

  /**
   * @return the cdrRuleSel
   */
  public Button getCdrRuleSel() {
    return this.cdrRuleSel;
  }

  /**
   * @return the seriesStatSel
   */
  public Button getSeriesStatSel() {
    return this.seriesStatSel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    final Composite myComp = new Composite(parent, SWT.NONE);
    myComp.setLayout(new GridLayout());
    myComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    createRadioButton(myComp, toolkit);
    setControl(myComp);
  }

  /**
   * @param myComp Composite
   * @param toolkit FormToolkit
   */
  private void createRadioButton(final Composite myComp, final FormToolkit toolkit) {

    // ICDM-880
    final Section sectionDataSrc = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionDataSrc.setText("Select data source");
    sectionDataSrc.getDescriptionControl().setEnabled(false);

    final Composite compDataSrc = toolkit.createComposite(sectionDataSrc);

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    sectionDataSrc.setLayoutData(gridData);

    compDataSrc.setLayoutData(GridDataUtil.getInstance().getGridData());
    final GridLayout layout = new GridLayout();
    compDataSrc.setLayout(layout);

    this.cdrRuleSel = new Button(compDataSrc, SWT.RADIO);

    this.cdrRuleSel.setText("Common Rules");
    this.cdrRuleSel.setSelection(true);
    this.cdrRuleSel.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getContainer().updateButtons();
        if (PreCalSourceSelectionPage.this.cdrRuleSel.getSelection()) {
          PreCalSourceSelectionPage.this.seriesStatSel.setSelection(false);
          PreCalSourceSelectionPage.this.ruleSetSel.setSelection(false);
          getDataHandler().setFlagChanged(true);
          getDataHandler().setPreCalSourceType(PRECAL_SOURCE_TYPE.COMMON_RULES);
        }

      }
    });
    LabelUtil.getInstance().createEmptyLabel(compDataSrc);
    this.ruleSetSel = new Button(compDataSrc, SWT.RADIO);
    this.ruleSetSel.setText("Rule Set");
    this.ruleSetSel.setSelection(false);
    this.ruleSetSel.setEnabled(true);

    this.ruleSetSel.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getContainer().updateButtons();
        getDataHandler().setFlagChanged(true);
        if (PreCalSourceSelectionPage.this.ruleSetSel.getSelection()) {
          PreCalSourceSelectionPage.this.cdrRuleSel.setSelection(false);
          PreCalSourceSelectionPage.this.seriesStatSel.setSelection(false);
          getDataHandler().setFlagChanged(true);
          getDataHandler().setPreCalSourceType(PRECAL_SOURCE_TYPE.RULESET_RULES);
        }
      }
    });

    LabelUtil.getInstance().createEmptyLabel(compDataSrc);

    this.seriesStatSel = new Button(compDataSrc, SWT.RADIO);
    this.seriesStatSel.setText("Series Statistics");
    this.seriesStatSel.setSelection(false);
    this.seriesStatSel.setEnabled(true);

    this.seriesStatSel.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getContainer().updateButtons();
        getDataHandler().setFlagChanged(true);
        if (PreCalSourceSelectionPage.this.seriesStatSel.getSelection()) {
          PreCalSourceSelectionPage.this.cdrRuleSel.setSelection(false);
          PreCalSourceSelectionPage.this.ruleSetSel.setSelection(false);
          getDataHandler().setFlagChanged(true);
          getDataHandler().setPreCalSourceType(PRECAL_SOURCE_TYPE.SERIES_STATISTICS);
        }
      }
    });

    LabelUtil.getInstance().createEmptyLabel(compDataSrc);
    LabelUtil.getInstance().createEmptyLabel(compDataSrc);

    sectionDataSrc.setClient(compDataSrc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    // If any one selection is made then return true
    return this.cdrRuleSel.getSelection() || this.seriesStatSel.getSelection() || this.ruleSetSel.getSelection();
  }

  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }
}
