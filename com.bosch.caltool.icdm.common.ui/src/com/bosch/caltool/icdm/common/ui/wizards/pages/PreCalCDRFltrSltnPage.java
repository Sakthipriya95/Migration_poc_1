/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author rgo7cob Icdm-694
 */
public class PreCalCDRFltrSltnPage extends WizardPage {

  /**
   * check box -Recommended Value
   */
  private Button chRecomValue;
  /**
   * check box - Exact match only
   */
  private Button chExactMatchOnly;

  /**
   * @param pageName page Name
   * @param a2lFileName a2l File Name
   */
  public PreCalCDRFltrSltnPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    // set description
    setDescription("Please select the data to be fetched and filter criteria");
  }


  /**
   * {@inheritDoc} createControl
   */
  @Override
  public void createControl(final Composite parent) {
    final Composite myComp = new Composite(parent, SWT.NONE);
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    // Data to be fetched section
    createDataFecthGrp(myComp, toolkit);

    myComp.setLayout(new GridLayout());
    myComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    setControl(myComp);

  }


  /**
   * @param myComp Composite
   * @param toolkit FormToolkit
   */
  private void createDataFecthGrp(final Composite myComp, final FormToolkit toolkit) {
    final GridLayout gridLayout = new GridLayout();

    // icdm-880
    final Section sectionDataSrc = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionDataSrc.setText("Data to be fetched");
    sectionDataSrc.getDescriptionControl().setEnabled(false);
    sectionDataSrc.setLayout(gridLayout);
    sectionDataSrc.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite dataComposite = toolkit.createComposite(sectionDataSrc);
    dataComposite.setLayout(gridLayout);
    dataComposite.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.chRecomValue = new Button(dataComposite, SWT.CHECK);
    this.chRecomValue.setText("Reference Value");
    this.chRecomValue.setSelection(true);
    this.chRecomValue.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getContainer().updateButtons();
      }
    });

    sectionDataSrc.setClient(dataComposite);

    final Section sectFltrCriteria = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectFltrCriteria.setText("Filter criteria");
    sectFltrCriteria.getDescriptionControl().setEnabled(false);
    sectFltrCriteria.setLayout(gridLayout);
    sectFltrCriteria.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite fltrCriteriaComp = toolkit.createComposite(sectFltrCriteria);
    fltrCriteriaComp.setLayout(gridLayout);
    fltrCriteriaComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.chExactMatchOnly = new Button(fltrCriteriaComp, SWT.CHECK);
    this.chExactMatchOnly.setText(CDRConstants.EXACT_MATCH_TO_REFERENCE_VALUE);
    this.chExactMatchOnly.setSelection(false);
    this.chExactMatchOnly.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        getDataHandler().setFlagChanged(true);
        getDataHandler().getCalDataMap().clear();
        getContainer().updateButtons();
        getDataHandler().setFlagChanged(true);
      }
    });
    sectFltrCriteria.setClient(fltrCriteriaComp);
  }

  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }

  /**
   * @param shell Shell
   */
  public void nextPressed(final Shell shell) {
    getDataHandler().loadProjAttrValDetails();
    PreCalAttrValResponse prjAttrValDet = getDataHandler().getProjAttrValDetails();
    if (prjAttrValDet == null) {
      return;
    }

    // If dependent attrs map is empty
    if (prjAttrValDet.getAttrMap().isEmpty()) {
      getShell().pack();
      getShell().setMinimized(true);
    } // update tableviewer
    else {
      updateAttrTableViewer();
    }
  }


  /**
   *
   */
  private void updateAttrTableViewer() {
    final PreCalDataExportWizard wizard = getWizard();
    final PreCalAttrValWizardPage attrValsPage = (PreCalAttrValWizardPage) wizard.getNextPage(PreCalCDRFltrSltnPage.this);
    attrValsPage.updateAttrValTableViewer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canFlip = false;
    if (this.chRecomValue.getSelection()) {
      canFlip = true;
    }
    return canFlip;
  }

  /**
   * @return selection state of exact match only check box
   */
  public boolean getExactMatchOnlySelectionState() {
    return this.chExactMatchOnly.getSelection();
  }

}
