/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards.pages;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ruleseditor.sorters.RulesViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.CDRRuleDefFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * ICDM-1081 This page is to choose an existing rule or to tell whether a new rule is to be created
 *
 * @author mkl2cob
 */
public class ChooseRuleWizardPage extends WizardPage {

  private static final String PAGE_TITLE = "Rule";

  private static final String PAGE_DESCRIPTION = "Please select the rule you want to use or select new rule";

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Section
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  /**
   * filter text for rules table
   */
  private Text filterText;

  /**
   * GridTableViewer table for existing rules
   */
  private GridTableViewer rulesTableViewer;

  /**
   * new rule radio button
   */
  private Button newRuleBtn;

  /**
   * existing rule radio button
   */
  private Button existingRuleBtn;

  /**
   * sorter for rules table
   */
  private RulesViewerSorter rulesSorter;

  /**
   * filter for rules table
   */
  private CDRRuleDefFilter ruleFilter;

  /**
   * @param pageName Name of the page
   */
  public ChooseRuleWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG1_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite composite) {
    initializeDialogUnits(composite);
    final Composite workArea = new Composite(composite, SWT.NONE);
    createSection(workArea);
    createGridLayout(workArea);
  }

  /**
   * @param workArea Composite
   */
  private void createGridLayout(final Composite workArea) {
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    setControl(workArea);
  }

  /**
   * @param workArea Composite
   */
  private void createSection(final Composite workArea) {
    this.section = getFormToolkit().createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Rule Selection");
    this.section.setExpanded(true);
    this.section.getDescriptionControl().setEnabled(true);
    this.section.setDescription("Create a new rule or choose existing one");

    createFormThree();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * creating form with radio buttons & rules table
   */
  private void createFormThree() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = false;
    gridDataFour.verticalAlignment = GridData.FILL;
    gridDataFour.grabExcessVerticalSpace = true;

    this.form = getFormToolkit().createForm(this.section);
    this.newRuleBtn = new Button(this.form.getBody(), SWT.RADIO);

    this.newRuleBtn.setText("Create a new rule");
    this.newRuleBtn.setSelection(true);
    Listener listener = new Listener() {

      @Override
      public void handleEvent(final Event event) {
        if (ChooseRuleWizardPage.this.newRuleBtn.getSelection()) {

          CreateEditRuleWizardPage createEditRulePage =
              (CreateEditRuleWizardPage) ((AddNewConfigWizard) getWizard()).getPage("New Rule");
          createEditRulePage.setPageComplete(false);
          getWizard().getContainer().updateButtons();

          ChooseRuleWizardPage.this.rulesTableViewer.refresh();
        }
      }
    };
    this.newRuleBtn.addListener(SWT.Selection, listener);

    LabelUtil.getInstance().createEmptyLabel(this.form.getBody());

    this.existingRuleBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.existingRuleBtn.setText("Select from existing rules");
    this.existingRuleBtn.setSelection(false);
    this.existingRuleBtn.setEnabled(true);

    Listener listener2 = new Listener() {

      @Override
      public void handleEvent(final Event event) {
        if (ChooseRuleWizardPage.this.existingRuleBtn.getSelection()) {

          CreateEditRuleWizardPage createEditRulePage =
              (CreateEditRuleWizardPage) ((AddNewConfigWizard) getWizard()).getPage("New Rule");
          createEditRulePage.setPageComplete(false);
          getWizard().getContainer().updateButtons();

          ChooseRuleWizardPage.this.rulesTableViewer.refresh();
        }
      }
    };
    this.existingRuleBtn.addListener(SWT.Selection, listener2);

    LabelUtil.getInstance().createEmptyLabel(this.form.getBody());
    this.filterText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterText.setLayoutData(gridData);
    this.filterText.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    GridLayout layout = new GridLayout();
    this.form.getBody().setLayout(layout);

    createAttrsTable(gridDataFour);

  }

  /**
   * adding modify listener for the table text filter
   */
  private void addModifyTextListener(final Text filterTxt, final AbstractViewerFilter filter,
      final GridTableViewer tableViewer) {
    filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterTxt.getText().trim();
        filter.setFilterText(text);
        tableViewer.refresh();
      }
    });
  }

  /**
   * @param gridDataFour
   */
  private void createAttrsTable(final GridData gridDataFour) {
    this.rulesTableViewer =
        new GridTableViewer(this.form.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    this.ruleFilter = new CDRRuleDefFilter();
    this.rulesTableViewer.addFilter(this.ruleFilter);
    addModifyTextListener(this.filterText, this.ruleFilter, this.rulesTableViewer);
    this.rulesSorter = new RulesViewerSorter();


    this.rulesTableViewer.getGrid().setLayoutData(gridDataFour);

    this.rulesTableViewer.getGrid().setLinesVisible(true);
    this.rulesTableViewer.getGrid().setHeaderVisible(true);

    this.rulesTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    createColumns();

    this.rulesTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        getWizard().getContainer().updateButtons();
      }


    });

    this.rulesTableViewer.setComparator(this.rulesSorter);

    this.rulesTableViewer
        .setInput(((AddNewConfigWizard) getWizard()).getWizardData().getParamRulesModel().getRuleDefenitionSet());
    this.rulesTableViewer.getControl().setEnabled(false);
  }

  /**
   * create columns for rules table
   */
  private void createColumns() {
    final GridViewerColumn lowerLmtCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    lowerLmtCol.getColumn().setText("Lower limit");
    lowerLmtCol.getColumn().setWidth(100);

    lowerLmtCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (CommonUtils.isNotNull(((RuleDefinition) element).getLowerLimit())) {
          return ((RuleDefinition) element).getLowerLimit().toString();
        }
        return "";
      }
    });
    // Add column selection listener
    lowerLmtCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        lowerLmtCol.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.rulesSorter, this.rulesTableViewer));

    final GridViewerColumn upperLmtCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    upperLmtCol.getColumn().setText("Upper limit");
    upperLmtCol.getColumn().setWidth(100);

    upperLmtCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (CommonUtils.isNotNull(((RuleDefinition) element).getUpperLimit())) {
          return ((RuleDefinition) element).getUpperLimit().toString();
        }
        return "";
      }
    });
    // Add column selection listener
    upperLmtCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        upperLmtCol.getColumn(), ApicUiConstants.COLUMN_INDEX_2, this.rulesSorter, this.rulesTableViewer));

    final GridViewerColumn bitwiseLmtCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    bitwiseLmtCol.getColumn().setText("Bitwise limit");
    bitwiseLmtCol.getColumn().setWidth(120);

    bitwiseLmtCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        RuleDefinition ruleDef = (RuleDefinition) element;
        if (CommonUtils.isNotNull(ruleDef.getBitWise())) {
          return ruleDef.getBitWise();
        }
        return "";
      }
    });
    // Add column selection listener
    bitwiseLmtCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        bitwiseLmtCol.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.rulesSorter, this.rulesTableViewer));

    final GridViewerColumn refValCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    refValCol.getColumn().setText("Reference Value");
    refValCol.getColumn().setWidth(100);

    refValCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return ((RuleDefinition) element).getRefValueDisplayString();

      }
    });
    // Add column selection listener
    refValCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        refValCol.getColumn(), ApicUiConstants.COLUMN_INDEX_4, this.rulesSorter, this.rulesTableViewer));


    final GridViewerColumn unitCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    unitCol.getColumn().setText("Unit");
    unitCol.getColumn().setWidth(60);

    unitCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return ((RuleDefinition) element).getUnit();

      }
    });
    // Add column selection listener
    unitCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(unitCol.getColumn(),
        ApicUiConstants.COLUMN_INDEX_5, this.rulesSorter, this.rulesTableViewer));

    final GridViewerColumn rvwMtdCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    rvwMtdCol.getColumn().setText("Ready for series");
    rvwMtdCol.getColumn().setWidth(100);

    rvwMtdCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(((RuleDefinition) element).getReviewMethod())) {
          return ApicConstants.READY_FOR_SERIES.YES.uiType;
        }
        else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(((RuleDefinition) element).getReviewMethod())) {
          return ApicConstants.READY_FOR_SERIES.NO.uiType;
        }
        return "";
      }
    });
    // Add column selection listener
    rvwMtdCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        rvwMtdCol.getColumn(), ApicUiConstants.COLUMN_INDEX_6, this.rulesSorter, this.rulesTableViewer));

    final GridViewerColumn exactMatchCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    exactMatchCol.getColumn().setText("Exact Match to Reference");
    exactMatchCol.getColumn().setWidth(150);

    exactMatchCol.setLabelProvider(new ColumnLabelProvider() {


      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof RuleDefinition) {
          final RuleDefinition item = (RuleDefinition) element;
          if (item.isExactMatch()) {
            return CommonUtilConstants.DISPLAY_YES;
          }
        }
        return CommonUtilConstants.DISPLAY_NO;
      }


    });
    // Add column selection listener
    exactMatchCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        exactMatchCol.getColumn(), ApicUiConstants.COLUMN_INDEX_7, this.rulesSorter, this.rulesTableViewer));

  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   *
   */
  public void backPressed() {
    // Not applicable now

  }

  /**
   * On pressing next, set the RuleDefinition
   */
  public void nextPressed() {
    if (this.newRuleBtn.getSelection()) {
      ((AddNewConfigWizard) getWizard()).getWizardData().setRuleDefinition(null);
      CreateEditRuleWizardPage createEditRulePage =
          (CreateEditRuleWizardPage) ((AddNewConfigWizard) getWizard()).getNextPage(this);
      createEditRulePage.getRuleInfoSection().clearAllFields();
      // if it is ruleset then param properties will be disabled
      createEditRulePage.getRuleInfoSection().enableParamProp();
    }
    else {
      IStructuredSelection selection = (IStructuredSelection) this.rulesTableViewer.getSelection();
      if (selection.getFirstElement() instanceof RuleDefinition) {
        ((AddNewConfigWizard) getWizard()).getWizardData()
            .setRuleDefinition((RuleDefinition) (selection.getFirstElement()));
        CreateEditRuleWizardPage createEditRulePage =
            (CreateEditRuleWizardPage) ((AddNewConfigWizard) getWizard()).getNextPage(this);
        createEditRulePage.getRuleInfoSection()
            .setRuleDefDetails(((AddNewConfigWizard) getWizard()).getWizardData().getRuleDefinition());
        // set the selected rule details
        if (!((createEditRulePage.getRuleInfoSection().getSelectedCdrRule().getBitWiseRule() != null) &&
            "COMPLEX RULE!".equals(createEditRulePage.getRuleInfoSection().getSelectedCdrRule().getBitWiseRule()))) {
          createEditRulePage.getRuleInfoSection().getBitWiseConfigDialog()
              .setSelectedRule(createEditRulePage.getRuleInfoSection().getSelectedCdrRule());
          createEditRulePage.getRuleInfoSection().getBitWiseConfigDialog().setInput();
        }
        createEditRulePage.getRuleInfoSection().enableParamProp();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed = false;
    if (this.newRuleBtn.getSelection()) {
      this.rulesTableViewer.getControl().setEnabled(false);
      canProceed = true;
    }
    else {
      this.rulesTableViewer.getControl().setEnabled(true);
      IStructuredSelection selection = (IStructuredSelection) this.rulesTableViewer.getSelection();
      if (CommonUtils.isNotNull(selection.getFirstElement())) {
        canProceed = true;
      }
    }
    return canProceed;
  }
}
