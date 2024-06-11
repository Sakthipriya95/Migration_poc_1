/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizard;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizardData;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPDefinitionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author bru2cob
 */
public class FC2WPWizardPage extends WizardPage {

  private Button fc2WpRadio;
  private Button createWpRadio;
  private FC2WPAssignmentWizardData wizData;
  private FC2WPAssignmentWizard wizard;

  /**
   * @param pageName
   */
  public FC2WPWizardPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("Assignment Functions to Workpackges");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    // create layout for composite
    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    setControl(workArea);

    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    final Section sectionDataSrc = toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionDataSrc.setText("FC2WP");
    sectionDataSrc.getDescriptionControl().setEnabled(false);
    final GridData gridData1 = GridDataUtil.getInstance().getGridData();
    gridData1.grabExcessVerticalSpace = false;
    sectionDataSrc.setLayoutData(gridData1);

    final Composite createFc2wpComp = toolkit.createComposite(sectionDataSrc, SWT.NONE);
    createFc2wpComp.setLayout(new GridLayout());
    createFc2wpComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    sectionDataSrc.setClient(createFc2wpComp);
    this.wizard = (FC2WPAssignmentWizard) getWizard();
    final Group manageFc2wpGrp = new Group(createFc2wpComp, SWT.READ_ONLY);
    manageFc2wpGrp.setLayout(new GridLayout());
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    manageFc2wpGrp.setLayoutData(gridData1);
    this.wizData = ((FC2WPAssignmentWizard) getWizard()).getWizData();
    this.fc2WpRadio = new Button(manageFc2wpGrp, SWT.RADIO);
    this.fc2WpRadio.setText("Work on existing FC2WP list");
    this.fc2WpRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (FC2WPWizardPage.this.fc2WpRadio.getSelection()) {
          FC2WPWizardPage.this.wizData.setCreateWP(false);
        }
      }
    });
    new Label(manageFc2wpGrp, SWT.NONE);
    this.createWpRadio = new Button(manageFc2wpGrp, SWT.RADIO);
    this.createWpRadio.setText("Maintain WorkPackages");
    this.createWpRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (FC2WPWizardPage.this.createWpRadio.getSelection()) {
          FC2WPWizardPage.this.wizData.setCreateWP(true);
        }
      }
    });
  }

  /**
   *
   */
  public void nextPressed() {
    try {
      if (this.wizData.isCreateWP()) {
        // load wp's
        final WorkPackageServiceClient wpServiceClient = new WorkPackageServiceClient();
        Set<WorkPkg> wpSet = wpServiceClient.findAll();
        if ((wpSet != null) && !wpSet.isEmpty()) {
          this.wizard.getWpCreationWizPage().setTableInput(wpSet);
          this.wizard.getWpCreationWizPage().getWpDivTabViewer().setInput(null);
        }
      }
      else {

        // create a webservice client
        final FC2WPDefinitionServiceClient fc2wpClient = new FC2WPDefinitionServiceClient();
        // Load the contents using webservice calls
        Set<FC2WPDef> fc2wpDefSet = fc2wpClient.getAll();
        if ((fc2wpDefSet != null) && !fc2wpDefSet.isEmpty()) {
          this.wizData.setFc2wpDefSet(fc2wpDefSet);
          this.wizard.getFc2wpSelWizPage().setTableInput();
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

}
