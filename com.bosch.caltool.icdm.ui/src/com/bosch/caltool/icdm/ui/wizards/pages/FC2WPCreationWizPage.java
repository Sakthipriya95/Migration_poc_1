/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizard;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizardData;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author bru2cob
 */
public class FC2WPCreationWizPage extends WizardPage {

  /**
   * constant for strating from scratch
   */
  private static final String START_FROM_SCRATCH_MSG = "No,start from scratch";
  private FC2WPAssignmentWizardData fc2wpWizData;
  private HashMap<String, FC2WPDef> fc2wpMap;
  private Combo fc2wpCombo;

  /**
   * @param pageName
   */
  public FC2WPCreationWizPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("Create FC2WP from strach or select from all existing FC2WP lists for selected division");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    this.fc2wpWizData = ((FC2WPAssignmentWizard) getWizard()).getWizData();
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
    new Label(createFc2wpComp, SWT.NONE).setText("Do you want to use an older FC2WP list as a template?");
    this.fc2wpCombo = new Combo(createFc2wpComp, SWT.READ_ONLY);
    this.fc2wpCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.fc2wpCombo.add(START_FROM_SCRATCH_MSG);

    this.fc2wpMap = new HashMap<String, FC2WPDef>();

    this.fc2wpCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        int index = FC2WPCreationWizPage.this.fc2wpCombo.getSelectionIndex();
        String selVal = FC2WPCreationWizPage.this.fc2wpCombo.getItem(index);
        FC2WPDef fc2wp = FC2WPCreationWizPage.this.fc2wpMap.get(selVal);
        if (fc2wp != null) {
          FC2WPCreationWizPage.this.fc2wpWizData.setCreateNewFC2WP(false);
          FC2WPCreationWizPage.this.fc2wpWizData.setExistingFC2WP(fc2wp);
        }
        else if (selVal.equalsIgnoreCase(START_FROM_SCRATCH_MSG)) {
          FC2WPCreationWizPage.this.fc2wpWizData.setCreateNewFC2WP(true);
        }
      }
    });
  }

  /**
   * @param fc2wpSet
   * @param selDiv
   */
  public void setComboInput() {
    this.fc2wpCombo.removeAll();
    this.fc2wpCombo.add(START_FROM_SCRATCH_MSG);
    int FIRST_INDEX = 0;
    this.fc2wpCombo.select(FIRST_INDEX);
    Set<FC2WPDef> fc2wpSet = this.fc2wpWizData.getFc2wpDefSet();
    String selDiv = this.fc2wpWizData.getSelDivision().getName();
    SortedSet<FC2WPDef> fc2wpSortedSet = new TreeSet<FC2WPDef>(fc2wpSet);
    for (FC2WPDef fc2wp : fc2wpSortedSet) {
      if (fc2wp.getDivisionName().equalsIgnoreCase(selDiv)) {
        this.fc2wpMap.put(fc2wp.getName(), fc2wp);
        this.fc2wpCombo.add(fc2wp.getName());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    return false;
  }

  /**
   *
   */
  public void backPressed() {
    this.fc2wpCombo.clearSelection();
    FC2WPCreationWizPage.this.fc2wpWizData.setCreateNewFC2WP(false);
    FC2WPCreationWizPage.this.fc2wpWizData.setExistingFC2WP(null);
  }

}
