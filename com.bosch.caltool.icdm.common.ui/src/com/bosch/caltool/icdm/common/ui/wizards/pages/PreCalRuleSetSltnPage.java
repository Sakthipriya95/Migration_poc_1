/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.listeners.GridTableEditingSupport;
import com.bosch.caltool.icdm.common.ui.sorters.RuleSetTabSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.RuleSetTabFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;

/**
 * ICDM-1368 This page is to select the Rule set
 *
 * @author rgo7cob
 */
public class PreCalRuleSetSltnPage extends WizardPage {

  /**
   * Bc name col width
   */
  private static final int BC_NAME_COL_SIZE = 600;
  /**
   * SortedSet of A2LFilterParameter objs
   */
  private final SortedSet<A2LFilterParameter> params = new TreeSet<>();
  /**
   * RuleSetTabSorter instance
   */
  private RuleSetTabSorter rsTabSorter;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer ruleSetTabViewer;

  /**
   * Content changed
   */
  private boolean contentChanged;

  /**
   * sorted set of rulesets
   */
  private SortedSet<RuleSet> allRuleSets;

  /**
   * error occured on next pressed
   */
  private boolean errorOccured;
  // instance of rule set section
  private Section rsSection;

  /**
   * @param pageName page Name
   * @param a2lFileName a2l File Name
   */
  public PreCalRuleSetSltnPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    setDescription("Please select the rule set for getting the rules");
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    // create composite
    final Composite myComp = new Composite(parent, SWT.NONE);
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    // create separate group
    createFltrCriteriaGrp(myComp, new GridLayout(), toolkit);
    // set control
    setControl(myComp);
  }

  /**
   * ICDM-879 creating group for filter group
   *
   * @param myComp Composite
   * @param gridLayout GridLayout
   * @param toolkit FormToolkit
   */
  private void createFltrCriteriaGrp(final Composite myComp, final GridLayout gridLayout, final FormToolkit toolkit) {

    myComp.setLayout(gridLayout);
    myComp.setLayoutData(new GridData());
    // create ruleset section
    createRuleSetSection(myComp, toolkit);
  }


  /**
   * Show error message if rule set is restricted
   *
   * @return true if restricted
   */
  private boolean checkIfRuleSetRestricted() {
    // clear error message
    setErrorMessage(null);
    // set error message if rule set has no access right
    final IStructuredSelection selection =
        (IStructuredSelection) PreCalRuleSetSltnPage.this.ruleSetTabViewer.getSelection();
    // Check if selection exists
    if (selection != null) {
      RuleSet ruleSet = (RuleSet) selection.getFirstElement();
      if (getDataHandler().isRuleSetRestricted(ruleSet)) {
        PreCalRuleSetSltnPage.this.errorOccured = true;
        setErrorMessage(
            "Access restricted to this Rule Set (" + ruleSet.getName() + "). Please select a different rule set.");
        getContainer().updateButtons();
        return true;
      }
    }
    return false;
  }

  /**
   *
   */
  protected void setErrorMessageCustom() {

    String ruleSetRestrictredNames = "";
    IStructuredSelection selection;

    selection = (IStructuredSelection) this.ruleSetTabViewer.getSelection();
    // Check if selection exists
    if (selection != null) {
      // add others to ruleSetDataList
      List<?> selectedRuleSets = selection.toList();
      for (Object ruleSetSelected : selectedRuleSets) {
        RuleSet ruleSet = (RuleSet) ruleSetSelected;
        if (getDataHandler().isRuleSetRestricted(ruleSet)) {
          ruleSetRestrictredNames = CommonUtils.concatenate(ruleSetRestrictredNames, ruleSet.getName(), ",");
        }
      }

    }

    if (ruleSetRestrictredNames.length() > 0) {
      ruleSetRestrictredNames = ruleSetRestrictredNames.substring(0, ruleSetRestrictredNames.length() - 1);
      setErrorMessage(
          "Access restricted to the Rule Set (" + ruleSetRestrictredNames + "). Please select a different rule set.");
      this.errorOccured = true;
      getContainer().updateButtons();
    }


  }

  /**
   * This method initializes section for filter criteria
   *
   * @param myComp Group
   * @param toolkit FormToolkit
   */
  private void createRuleSetSection(final Composite myComp, final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout = new GridLayout();

    this.rsSection = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);

    this.rsSection.setText("Rule Sets");
    this.rsSection.setDescription("Select Rule set for getting Pre-Calibartion data");

    final GridData sectionGridData = new GridData();
    sectionGridData.grabExcessHorizontalSpace = true;
    sectionGridData.grabExcessVerticalSpace = true;

    sectionGridData.horizontalAlignment = GridData.FILL;
    sectionGridData.verticalAlignment = GridData.FILL;

    this.rsSection.setLayout(gridLayout);

    final Composite compForTabs = toolkit.createComposite(this.rsSection, SWT.NONE);
    compForTabs.setLayout(gridLayout);
    compForTabs.setLayoutData(gridData);
    // create rule set tab
    createRuleSetTab(compForTabs, gridLayout);
    this.rsSection.setLayoutData(sectionGridData);
    this.rsSection.setClient(compForTabs);

  }

  /**
   * Creates Group for rule set tab
   *
   * @param parent
   * @param gridLayout GridLayout
   * @return Created Tab Group
   */
  private Control createRuleSetTab(final Composite parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite ruleSetComp = new Composite(parent, SWT.NONE);
    ruleSetComp.setLayout(gridLayout);
    ruleSetComp.setLayoutData(gridData);

    Text rsFilterTxt = new Text(ruleSetComp, SWT.SINGLE | SWT.BORDER);
    rsFilterTxt.setLayoutData(gridData);
    RuleSetTabFilter ruleSetFilter = new RuleSetTabFilter();

    this.rsTabSorter = new RuleSetTabSorter();

    this.ruleSetTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(ruleSetComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    // create type filter
    createRuleSetFilter(rsFilterTxt, ruleSetFilter);
    // create the columns
    createRuleSetTabColumns();

    this.ruleSetTabViewer.setComparator(this.rsTabSorter);
    this.ruleSetTabViewer.addFilter(ruleSetFilter);
    this.ruleSetTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    try {
      if (this.allRuleSets == null) {
        this.allRuleSets = new RuleSetServiceClient().getAllRuleSets();
      }
      this.ruleSetTabViewer.setInput(this.allRuleSets);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return ruleSetComp;
  }

  /**
   * creates the columns of BC table viewer
   */
  // ICDM-1011
  private void createRuleSetTabColumns() {
    // ICDM-886
    ColumnViewerToolTipSupport.enableFor(this.ruleSetTabViewer, ToolTip.NO_RECREATE);
    createRuleSetNameColumn(this.ruleSetTabViewer, this.rsTabSorter);
    this.ruleSetTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc} on selection change
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) PreCalRuleSetSltnPage.this.ruleSetTabViewer.getSelection();
        // Check if selection exists
        if (selection != null) {

          RuleSet ruleSet = (RuleSet) selection.getFirstElement();
          // iCDM-1522
          setErrorMessage(null);
          PreCalRuleSetSltnPage.this.errorOccured = false;
          // If param list is not empty
          if (CommonUtils.isNotNull(PreCalRuleSetSltnPage.this.getParamList())) {
            // for get pre calibration wizard
            getDataHandler().setSelectedRuleSet(ruleSet);
            getContainer().updateButtons();
            checkIfRuleSetRestricted();
          }

        }
        setContentChanged(true);
      }

    });
  }

  /**
   * creates unit column
   *
   * @param tabViewer
   * @param tableSorter table sorter
   */
  private void createRuleSetNameColumn(final GridTableViewer tabViewer, final RuleSetTabSorter tableSorter) {
    final GridViewerColumn bcColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(tabViewer, "Rule Set Name", BC_NAME_COL_SIZE);

    bcColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * getText
       */
      @Override
      public String getText(final Object element) {
        return ((RuleSet) element).getName();
      }
    });
    bcColumn.setEditingSupport(new GridTableEditingSupport(tabViewer, CommonUIConstants.COLUMN_INDEX_1));
    bcColumn.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(bcColumn.getColumn(), 0, tableSorter, tabViewer));
  }

  private final Map<Long, Set<String>> ruleSetParamNameMap = new HashMap<>();

  private Set<String> getRuleSetParams(final Long ruleSetId) {
    return this.ruleSetParamNameMap.computeIfAbsent(ruleSetId, this::fetchRuleSetParams);
  }

  private Set<String> fetchRuleSetParams(final Long ruleSetId) {
    try {
      return new RuleSetParameterServiceClient().getAllParamNames(ruleSetId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return new HashSet<>();
  }

  /**
   * fetch all the Recommended Values .Only for get pre calibration data wizard
   *
   * @param shell Shell instance
   */
  public void nextPressed(final Shell shell) {
    this.errorOccured = false;
    getDataHandler().getRuleSetParameterList().removeAll(getDataHandler().getRuleSetParameterList());
    Set<String> allParameters = getRuleSetParams(getDataHandler().getSelectedRuleSet().getId());
    List<A2LParameter> modifiedA2lParamList = new ArrayList<>();
    getDataHandler().getRuleSetParameterList().clear();
    for (A2LParameter a2lParam : getParamList()) {
      // If param name matches
      if (allParameters.contains(a2lParam.getName())) {
        modifiedA2lParamList.add(a2lParam);
        getDataHandler().getRuleSetParameterList().add(a2lParam.getName());
      }

    }
    // If none of the parameter selected is available in the Rule set
    if (getDataHandler().getRuleSetParameterList().isEmpty()) {
      CDMLogger.getInstance().errorDialog("None of the parameter selected is available in the Rule set",
          Activator.PLUGIN_ID);
      this.errorOccured = true;
      getContainer().updateButtons();
      return;
    }

  }

  /**
   * This method creates filter text
   *
   * @param filterTxt
   * @param ruleSetFilter2
   */
  private void createRuleSetFilter(final Text filterTxt, final RuleSetTabFilter ruleSetFilter) {
    final GridData gridData = getFilterTxtGridData();
    filterTxt.setLayoutData(gridData);
    filterTxt.setMessage("type filter text");
    filterTxt.addModifyListener(event -> {
      final String text = filterTxt.getText().trim();
      ruleSetFilter.setFilterText(text);
      this.ruleSetTabViewer.refresh();

    });
  }


  /**
   * ICDM-886 {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage() {
    if (this.errorOccured) {
      return this;
    }
    return super.getNextPage();
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
   * @return the params filtered params
   */
  public SortedSet<A2LFilterParameter> getParams() {
    return this.params;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    // If any one selection is made then return true
    if (this.rsSection.isEnabled() && this.ruleSetTabViewer.getGrid().isEnabled() &&
        ((this.ruleSetTabViewer.getSelection() == null) || this.ruleSetTabViewer.getSelection().isEmpty())) {
      return false;
    }

    // if an error had occurred
    return !this.errorOccured;
  }

  /**
   * @return the ruleSetTabViewer
   */
  public GridTableViewer getRuleSetTabViewer() {
    return this.ruleSetTabViewer;
  }

  /**
   * On back pressed make the selected rule set null.
   */
  public void backPressed() {
    // nothing done here
  }

  /**
   * @return the contentChanged
   */
  public boolean isContentChanged() {
    return this.contentChanged;
  }

  /**
   * @param contentChanged the contentChanged to set
   */
  public void setContentChanged(final boolean contentChanged) {
    this.contentChanged = contentChanged;
  }

  /**
   * @param errorOccured the errorOccured to set
   */
  public void setErrorOccured(final boolean errorOccured) {
    this.errorOccured = errorOccured;
  }

  /**
   * @return the paramList
   */
  private List<A2LParameter> getParamList() {
    return getDataHandler().getParamList();
  }

  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }

}
