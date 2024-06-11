/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards.pages;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.ui.sorters.FC2WPGridTabViewerSorter;
import com.bosch.caltool.icdm.ui.table.filters.Fc2wpDefFilter;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizard;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizardData;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;

/**
 * FC2WP selection page
 *
 * @author bru2cob
 */
public class FC2WPSelectionWizardPage extends WizardPage {

  /**
   *
   */
  private static final int FIRST_INDEX = 0;

  /**
   * GridTableViewer instance for FC2WP
   */
  private GridTableViewer fc2wpTabViewer;

  /**
   * Defines AbstractViewerSorter - FC2WP GridTableViewer sorter
   */
  private AbstractViewerSorter fc2wpTabSorter;

  /**
   * Filter text instance
   */
  private Text fc2wpFilterTxt;

  private FC2WPAssignmentWizardData fc2wpWizData;

  private Text fc2wpName;

  private FormToolkit toolkit;
  /**
   * NewUsersFilter instance
   */
  private Fc2wpDefFilter fc2wpDefFilters;

  /**
   * @param pageName
   */
  public FC2WPSelectionWizardPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("New FC2WP list can be created or can be chosen from all the existing list");
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    this.fc2wpWizData = ((FC2WPAssignmentWizard) getWizard()).getWizData();
    final Composite workArea = new Composite(parent, SWT.NONE);
    // create layout for composite
    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // set control
    setControl(workArea);
    this.toolkit = new FormToolkit(parent.getDisplay());

    // create section to display existing fc2wp list
    final Section sectionFc2wp =
        this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionFc2wp.setDescription("Select the FC2WP to work on and press Finish");
    sectionFc2wp.getDescriptionControl().setEnabled(false);

    sectionFc2wp.setLayoutData(GridDataUtil.getInstance().getGridData());
    sectionFc2wp.setText("Existing FC2WP");
    final Composite chooseFc2wpComp = this.toolkit.createComposite(sectionFc2wp, SWT.NONE);
    chooseFc2wpComp.setLayout(new GridLayout());
    chooseFc2wpComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    sectionFc2wp.setClient(chooseFc2wpComp);

    new Label(chooseFc2wpComp, SWT.NONE).setText("Choose the list you want to work on ");
    new Label(chooseFc2wpComp, SWT.NONE).setText("");

    // create fc2wp table viewer
    this.fc2wpTabSorter = new FC2WPGridTabViewerSorter();

    // Create Filter text
    createWPTabFilterTxt(chooseFc2wpComp);
    GridData tableGridData = GridDataUtil.getInstance().getGridData();
    this.fc2wpTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(chooseFc2wpComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, tableGridData);
    // Create GridViewerColumns
    createFc2wpGridViewerColumns();

    this.fc2wpDefFilters = new Fc2wpDefFilter();
    // Add PIDC Attribute TableViewer filter
    this.fc2wpTabViewer.addFilter(this.fc2wpDefFilters);
    // review 236648
    tableGridData.heightHint =
        (7 * this.fc2wpTabViewer.getGrid().getItemHeight()) + this.fc2wpTabViewer.getGrid().getHeaderHeight();


    // Set content provider
    this.fc2wpTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.fc2wpTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        setFc2WPToOpen();
      }
    });

    this.fc2wpTabViewer.setComparator(this.fc2wpTabSorter);

    // Adds double click selection listener
    addDoubleClickListener();
  }

  /**
   *
   */
  private void addDoubleClickListener() {
    this.fc2wpTabViewer.addDoubleClickListener(event -> {
      Display.getDefault().asyncExec(() -> {
        setFc2WPToOpen();
        ((FC2WPAssignmentWizard) getWizard()).performFinish();
        ((FC2WPAssignmentWizard) getWizard()).getShell().close();
      });
    });
  }

  /**
   * Create wp table filter text
   *
   * @param createFc2wpComp
   */
  private void createWPTabFilterTxt(final Composite createFc2wpComp) {
    this.fc2wpFilterTxt = TextUtil.getInstance().createFilterText(this.toolkit, createFc2wpComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    this.fc2wpFilterTxt.addModifyListener(event -> {
      final String text = FC2WPSelectionWizardPage.this.fc2wpFilterTxt.getText().trim();
      FC2WPSelectionWizardPage.this.fc2wpDefFilters.setFilterText(text);
      FC2WPSelectionWizardPage.this.fc2wpTabViewer.refresh();
    });

    this.fc2wpFilterTxt.setFocus();

  }

  /**
   * @return true if table has data
   */
  public boolean canEnableFinish() {
    return this.fc2wpTabViewer.getInput() != null;
  }

  /**
   * Create GridViewerColumns
   */
  private void createFc2wpGridViewerColumns() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fc2wpTabViewer, "FC2WP Name", 200);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        FC2WPDef fc2wp = (FC2WPDef) element;
        return fc2wp.getName();
      }
    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.fc2wpTabSorter, this.fc2wpTabViewer));


    final GridViewerColumn divisionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fc2wpTabViewer, "Division", 200);

    divisionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        FC2WPDef fc2wp = (FC2WPDef) element;
        return fc2wp.getDivisionName();
      }
    });
    // Add column selection listener
    divisionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(divisionColumn.getColumn(), 1, this.fc2wpTabSorter, this.fc2wpTabViewer));

  }

  /**
   *
   */
  public void nextPressed() {
    if (FC2WPSelectionWizardPage.this.fc2wpWizData.getSelDivision() != null) {
      ((FC2WPAssignmentWizard) getWizard()).getFc2wpCreationPage().setComboInput();
    }
    if (!this.fc2wpName.getText().isEmpty()) {
      this.fc2wpWizData.setFc2wpName(this.fc2wpName.getText());
    }
  }


  /**
   * @return
   */
  private boolean validateFields() {
    return !this.fc2wpName.getText().isEmpty() && (this.fc2wpWizData.getSelDivision() != null);
  }

  /**
   * This method is to enable the next button
   */
  public void checkNextBtnEnable() {
    setPageComplete(validateFields());
  }

  /**
   */
  public void setTableInput() {
    this.fc2wpTabViewer.setInput(this.fc2wpWizData.getFc2wpDefSet());
    this.fc2wpTabViewer.getGrid().select(FIRST_INDEX);
    setFc2WPToOpen();
    this.fc2wpTabViewer.refresh();
  }

  /**
   *
   */
  private void setFc2WPToOpen() {
    final IStructuredSelection selection =
        (IStructuredSelection) FC2WPSelectionWizardPage.this.fc2wpTabViewer.getSelection();
    if (!selection.isEmpty()) {
      FC2WPSelectionWizardPage.this.fc2wpWizData.setOpenExistingFC2WP((FC2WPDef) selection.getFirstElement());
    }
  }

}
