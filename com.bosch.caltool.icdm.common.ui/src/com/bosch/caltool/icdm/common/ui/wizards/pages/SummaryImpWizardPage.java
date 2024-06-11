/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.bosch.calmodel.a2ldata.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;


/**
 * @author bru2cob
 */
public class SummaryImpWizardPage extends WizardPage {

  /**
   * Status group height
   */
  private static final int STATUS_GRP_HEIGHT = 70;
  /**
   * Number of cols in main group
   */
  private static final int CAL_GRP_COLS = 2;
  /**
   * Title for the Wizard page
   */
  private static final String PAGE_TITLE = "Calibration Data Import Summary";
  /**
   * Description for the wizard page
   */
  private static final String PAGE_DESCRIPTION = "Displays the summary of the data import";
  /**
   * Main group which holds the labels
   */
  private Group importInfoGroup;
  /**
   * Main group which holds the status label
   */
  private Group statusInfoGroup;
  /**
   * Instance of param avail text
   */
  private Text paramAvailText;
  /**
   * Instance of param created text
   */
  private Text paramInvalidText;
  /**
   * Instance of rules created text
   */
  private Text rulesCreatedText;
  /**
   * Instance of rules updated text
   */
  private Text rulesUpdatedText;
  /**
   * Instance of rules duplicated text
   */
  private Text rulesDuplicatedText;

  /**
   * Instance of status desc text
   */
  private Text statusDescText;

  /**
   * Constructor
   *
   * @param pageName title
   */
  public SummaryImpWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG4_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    // create the main comp
    final Composite workArea = new Composite(parent, SWT.NONE);
    // set layout
    workArea.setLayout(new GridLayout());
    // set layout data
    workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

    // create status group
    this.statusInfoGroup = new Group(workArea, SWT.NONE);
    this.statusInfoGroup.setFont(workArea.getFont());
    this.statusInfoGroup.setText("Status:");
    this.statusInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    this.statusInfoGroup.setLayout(new GridLayout(CAL_GRP_COLS, false));

    this.statusDescText = new Text(this.statusInfoGroup, SWT.MULTI | SWT.V_SCROLL);
    this.statusDescText.setEditable(false);

    GridData statusGrid = new GridData(SWT.FILL, SWT.NONE, true, false);
    statusGrid.heightHint = STATUS_GRP_HEIGHT;
    this.statusDescText.setLayoutData(statusGrid);
    // create import info group
    this.importInfoGroup = new Group(workArea, SWT.NONE);
    this.importInfoGroup.setFont(workArea.getFont());
    this.importInfoGroup.setText("Calibration Data Info:");
    this.importInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    this.importInfoGroup.setLayout(new GridLayout(CAL_GRP_COLS, false));
    new Label(this.importInfoGroup, SWT.NONE).setText("Total number of parameters available in the file :");
    this.paramAvailText = new Text(this.importInfoGroup, SWT.SINGLE);
    this.paramAvailText.setEditable(false);
    final GridData projIDCardTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramAvailText.setLayoutData(projIDCardTextData);

    new Label(this.importInfoGroup, SWT.NONE).setText(getParamInvalidLabel());
    this.paramInvalidText = new Text(this.importInfoGroup, SWT.SINGLE);
    this.paramInvalidText.setEditable(false);
    final GridData variantTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramInvalidText.setLayoutData(variantTextData);

    new Label(this.importInfoGroup, SWT.NONE).setText("Total number of rules created :");
    this.rulesCreatedText = new Text(this.importInfoGroup, SWT.SINGLE);
    this.rulesCreatedText.setEditable(false);
    final GridData a2lFileTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.rulesCreatedText.setLayoutData(a2lFileTextData);


    new Label(this.importInfoGroup, SWT.NONE).setText("Total number of rules updated :");
    this.rulesUpdatedText = new Text(this.importInfoGroup, SWT.SINGLE);
    this.rulesUpdatedText.setEditable(false);
    final GridData groupWrkPakgeTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.rulesUpdatedText.setLayoutData(groupWrkPakgeTextData);

    new Label(this.importInfoGroup, SWT.NONE).setText("Number of rules skipped :");
    this.rulesDuplicatedText = new Text(this.importInfoGroup, SWT.SINGLE);
    this.rulesDuplicatedText.setEditable(false);
    final GridData groupTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.rulesDuplicatedText.setLayoutData(groupTextData);

    // set control
    setControl(workArea);

  }

  /**
   * @return
   */
  private String getParamInvalidLabel() {
    ParamCollection wizardData = ((CalDataFileImportWizard) getWizard()).getWizardData().getImportObject();
    return wizardData instanceof RuleSet? 
        "Total number of new parameters imported:":"Total number of invalid parameters :";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getPreviousPage() {
    return null;
  }

  /**
   * Set the input values
   *
   * @param impSummary
   */
  public void refresh(final CalDataImportSummary importSummary) {
    Display.getDefault().syncExec(new Runnable() {

      /**
       * {@inheritDoc} Refresh
       */
      @Override
      public void run() {
        // update the summery page

        SummaryImpWizardPage.this.paramAvailText.setText(String.valueOf(importSummary.getTotalNoOfParams()));
        SummaryImpWizardPage.this.statusDescText.setText(importSummary.getMessage());
        SummaryImpWizardPage.this.paramInvalidText.setText(String.valueOf(importSummary.getParamInvalidSet().size()));
        SummaryImpWizardPage.this.rulesCreatedText
            .setText(String.valueOf(importSummary.getParamRulesCreatedSet().size()));
        SummaryImpWizardPage.this.rulesUpdatedText
            .setText(String.valueOf(importSummary.getParamRulesUpdatedSet().size()));
        SummaryImpWizardPage.this.rulesDuplicatedText.setText(String.valueOf(importSummary.getSkippedParamsCount()));
        if (!("Rules updated successfully").equalsIgnoreCase(importSummary.getMessage())) {
          CDMLogger.getInstance().infoDialog(importSummary.getMessage(), Activator.PLUGIN_ID);
        }
        // enable next button
        setPageComplete(true);
        getShell().setSize(700, 600);
      }
    });
  }

}
