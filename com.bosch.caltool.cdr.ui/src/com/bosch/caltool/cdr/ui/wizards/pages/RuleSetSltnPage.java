/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.listeners.RuleSetSltnPageListener;
import com.bosch.caltool.cdr.ui.wizard.page.validator.RuleSetSltnPageValidator;
import com.bosch.caltool.cdr.ui.wizard.pages.resolver.RuleSetSltnPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.listeners.GridTableEditingSupport;
import com.bosch.caltool.icdm.common.ui.sorters.RuleSetTabSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.RuleSetTabFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * ICDM-1368 This page is to select the Rule set
 *
 * @author rgo7cob
 */
public class RuleSetSltnPage extends WizardPage {

  /**
   * Bc name col width
   */
  private static final int BC_NAME_COL_SIZE = 600;
  /**
   * List of A2LParameter objects
   */
  private final List<A2LParameter> paramList;
  /**
   * Button instance for filterValue
   */
  private Button filterValue;
  /**
   * SortedSet of A2LFilterParameter objs
   */
  private final SortedSet<A2LFilterParameter> params = new TreeSet<>();
  /**
   * Text instance
   */
  private Text primaryRuleSetTxt;
  /**
   * RuleSetTabFilter instance
   */
  private RuleSetTabFilter secRuleSetFilter;
  /**
   * RuleSetTabSorter instance
   */
  private RuleSetTabSorter primaryTableSorter;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer primaryRuleSetTabViewer;

  /**
   * List of ReviewRuleSetData
   */
  private final List<RuleSet> secondaryReviewRuleSetList;

  /**
   * gives the primary rule set instance
   */
  private RuleSet primaryReviewRuleSet;

  /**
   * true if common rules is primary
   */
  private boolean isRuleSetPrimary;
  /**
   * boolean to indicate whether it is a review wizard or not
   */
  private final boolean reviewWizard;
  /**
   * secondary rule set table viewer
   */
  private GridTableViewer secRuleSetTabViewer;
  /**
   * filter text
   */
  private Text secRuleSetTxt;
  /**
   * RuleSetTabFilter
   */
  private RuleSetTabFilter primaryRuleSetFilter;
  /**
   * RuleSetTabSorter
   */
  private RuleSetTabSorter secTableSorter;
  /**
   * Content changed
   */
  private boolean contentChanged;

  /**
   * Secondary Rule Set
   */
  private static final String SEC_RULE_SET = "Secondary Rule Sets";

  /**
   * Un-select button width
   */
  private static final int UN_SELECT_BTN_WIDTH = 200;
  /**
   * Un-select button height
   */
  private static final int UN_SELECT_BTN_HEIGHT = 27;

  private RuleSetSltnPageResolver ruleSetSltnPageResolver;

  private RuleSetSltnPageValidator ruleSetSltnPageValidator;

  private RuleSetSltnPageListener ruleSetSltnPageListener;

  private final CDRHandler cdrHandler = new CDRHandler();

  /**
   * Instance of wizard
   */
  private CalDataReviewWizard calDataReviewWizard;

  private final CurrentUserBO currentUserBO = new CurrentUserBO();

  private Button unSelectBtn;

  private GridViewerColumn bcColumn;

  /**
   * @param pageName pageName
   * @param paramList paramList
   * @param a2lFile A2LFileName
   * @param primaryReviewRuleSet ReviewRuleSetData
   * @param secondaryReviewRuleSetList List of secondary rules
   * @param reviewWizard true if is review wizard
   */
  public RuleSetSltnPage(final String pageName, final List<A2LParameter> paramList, final A2LFile a2lFile,
      final RuleSet primaryReviewRuleSet, final List<RuleSet> secondaryReviewRuleSetList, final boolean reviewWizard) {
    super(pageName);
    this.reviewWizard = reviewWizard;
    // Pre-Calibration Data
    if (CommonUtils.isNotNull(a2lFile)) {
      setTitle("Get Pre-Calibration Data for :  " + a2lFile.getFilename());
      setDescription("Please select the data to be fetched and filter criteria");
    } // Rule set
    else {
      setTitle("Select the rule set ");
      setDescription("Please select the rule set for getting the rules");
    }
    this.paramList = paramList;
    this.primaryReviewRuleSet = primaryReviewRuleSet;
    this.secondaryReviewRuleSetList = secondaryReviewRuleSetList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    // create composite
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite myComp = new Composite(scrollComp, SWT.NONE);
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    this.calDataReviewWizard = (CalDataReviewWizard) RuleSetSltnPage.this.getWizard();

    // create separate group
    createFltrCriteriaGrp(myComp, new GridLayout(), toolkit);
    // set control
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    this.ruleSetSltnPageValidator = new RuleSetSltnPageValidator(this.calDataReviewWizard);
    this.ruleSetSltnPageListener = new RuleSetSltnPageListener(this.calDataReviewWizard, this.ruleSetSltnPageValidator);
    this.ruleSetSltnPageResolver = new RuleSetSltnPageResolver(this.calDataReviewWizard);
    this.ruleSetSltnPageListener.createActionListeners();
    scrollComp.setContent(myComp);
    scrollComp.setMinSize(myComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
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
    createPrimaryRuleSetSection(myComp, toolkit);
    // Task 231283
    if (this.reviewWizard) {
      createSecondaryRuleSetSection(myComp, toolkit);
    }
  }


  /**
   * @param toolkit
   * @param myComp
   */
  private void createSecondaryRuleSetSection(final Composite myComp, final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout = new GridLayout();

    this.secRuleSetSection = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);


    this.secRuleSetSection.setText(SEC_RULE_SET);
    this.secRuleSetSection.setDescription("Select " + SEC_RULE_SET + " for the review");

    final GridData sectionGridData = new GridData();
    sectionGridData.grabExcessHorizontalSpace = true;
    sectionGridData.grabExcessVerticalSpace = true;

    sectionGridData.horizontalAlignment = GridData.FILL;
    sectionGridData.verticalAlignment = GridData.FILL;

    this.secRuleSetSection.setLayout(gridLayout);

    final Composite compForTabs = toolkit.createComposite(this.secRuleSetSection, SWT.NONE);
    compForTabs.setLayout(gridLayout);
    compForTabs.setLayoutData(gridData);
    // create rule set tab
    createSeondaryRuleSetTab(compForTabs, gridLayout);
    this.secRuleSetSection.setLayoutData(sectionGridData);
    this.secRuleSetSection.setClient(compForTabs);


  }

  /**
   * @param compForTabs
   * @param gridLayout
   */
  private void createSeondaryRuleSetTab(final Composite parent, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite ruleSetComp = new Composite(parent, SWT.NONE);
    ruleSetComp.setLayout(gridLayout);
    ruleSetComp.setLayoutData(gridData);

    createUnSelectButton(ruleSetComp);

    this.secRuleSetTxt = new Text(ruleSetComp, SWT.SINGLE | SWT.BORDER);
    this.secRuleSetTxt.setLayoutData(gridData);
    this.secRuleSetFilter = new RuleSetTabFilter();

    this.secTableSorter = new RuleSetTabSorter();

    this.secRuleSetTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(ruleSetComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    // create the columns
    createSecondaryTabColumns();
    // create type filter
    createRuleSetFilter(this.secRuleSetTxt, this.secRuleSetTabViewer, this.secRuleSetFilter);

    this.secRuleSetTabViewer.setComparator(this.secTableSorter);
    this.secRuleSetTabViewer.addFilter(this.secRuleSetFilter);
    this.secRuleSetTabViewer.setContentProvider(ArrayContentProvider.getInstance());
  }

  /**
   * Creates button for un-selection of all secondary rule set
   *
   * @param ruleSetComp the composite for button construction
   */
  // Review 238978
  private void createUnSelectButton(final Composite ruleSetComp) {
    this.unSelectBtn = new Button(ruleSetComp, SWT.PUSH);
    this.unSelectBtn
        .setLayoutData(GridDataUtil.getInstance().createGridData(UN_SELECT_BTN_WIDTH, UN_SELECT_BTN_HEIGHT));
    this.unSelectBtn.setText("Un-Select All " + SEC_RULE_SET);

  }

  /**
   * Show error message if primary rule set is restricted
   *
   * @return true if restricted
   * @throws ApicWebServiceException
   */
  public boolean checkIfPrimaryRuleSetRestricted() throws ApicWebServiceException {
    // clear error message
    setErrorMessage(null);
    // set error message if primary rule set has no access right
    final IStructuredSelection selection =
        (IStructuredSelection) RuleSetSltnPage.this.primaryRuleSetTabViewer.getSelection();
    // Check if selection exists
    if (selection != null) {
      RuleSet ruleSet = (RuleSet) selection.getFirstElement();
      if ((null != ruleSet) && isRestricted(ruleSet)) {
        RuleSetSltnPage.this.errorOccured = true;
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
  private void createSecondaryTabColumns() {

    ColumnViewerToolTipSupport.enableFor(this.secRuleSetTabViewer, ToolTip.NO_RECREATE);
    createRuleSetNameColumn(this.secRuleSetTabViewer, this.secTableSorter);
  }

  /**
  *
  */
  public void clearOtherRuleSetData() {
    List<RuleSet> listToRemove = new ArrayList<>();
    if (this.secondaryReviewRuleSetList != null) {
      listToRemove.addAll(this.secondaryReviewRuleSetList);

      for (RuleSet ruleData : listToRemove) {
        if (ruleData != null) {
          this.secondaryReviewRuleSetList.remove(ruleData);
          this.calDataReviewWizard.getCdrWizardUIModel().getSecondaryRuleSetIds().remove(ruleData.getId());
        }
      }
    }
  }


  /**
   * Checks if the Rule Set has minimum READ access
   *
   * @param ruleSet Rule Set
   * @return true if ruleset is deleted or does not have write access for the user
   * @throws ApicWebServiceException error while getting access rights
   */
  // iCDM-1522
  public boolean isRestricted(final RuleSet ruleSet) throws ApicWebServiceException {
    boolean deleted = ruleSet.isDeleted();
    if (!deleted && !ruleSet.isReadAccessFlag()) {
      return false;
    }
    NodeAccess nodeAccessRight = this.currentUserBO.getNodeAccessRight(ruleSet.getId());
    return ((nodeAccessRight == null) || !(nodeAccessRight.isRead() || nodeAccessRight.isWrite())) || deleted;
  }

  /**
   * This method initializes section for filter criteria
   *
   * @param myComp Group
   * @param toolkit FormToolkit
   */
  private void createPrimaryRuleSetSection(final Composite myComp, final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout = new GridLayout();

    this.primaryRuleSetSection = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);

    if (this.reviewWizard) {// Task 231283
      this.primaryRuleSetSection.setText("Primary Rule Set");
      this.primaryRuleSetSection.setDescription("Select the primary rule set for the review");
    }
    else {
      this.primaryRuleSetSection.setText("Rule Sets");
      this.primaryRuleSetSection.setDescription("Select Rule set for getting Pre-Calibartion data");
    }


    final GridData sectionGridData = new GridData();
    sectionGridData.grabExcessHorizontalSpace = true;
    sectionGridData.grabExcessVerticalSpace = true;

    sectionGridData.horizontalAlignment = GridData.FILL;
    sectionGridData.verticalAlignment = GridData.FILL;

    this.primaryRuleSetSection.setLayout(gridLayout);

    final Composite compForTabs = toolkit.createComposite(this.primaryRuleSetSection, SWT.NONE);
    compForTabs.setLayout(gridLayout);
    compForTabs.setLayoutData(gridData);
    // create rule set tab
    createPrimaryRuleSetTab(compForTabs, gridLayout);
    this.primaryRuleSetSection.setLayoutData(sectionGridData);
    this.primaryRuleSetSection.setClient(compForTabs);

  }

  /**
   * Creates Group for parameter tab
   *
   * @param parent
   * @param gridLayout GridLayout
   * @return Created Tab Group
   */
  private Control createPrimaryRuleSetTab(final Composite parent, final GridLayout gridLayout) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final Composite ruleSetComp = new Composite(parent, SWT.NONE);
    ruleSetComp.setLayout(gridLayout);
    ruleSetComp.setLayoutData(gridData);

    this.primaryRuleSetTxt = new Text(ruleSetComp, SWT.SINGLE | SWT.BORDER);
    this.primaryRuleSetTxt.setLayoutData(gridData);
    this.primaryRuleSetFilter = new RuleSetTabFilter();

    this.primaryTableSorter = new RuleSetTabSorter();

    this.primaryRuleSetTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(ruleSetComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    // create type filter
    createRuleSetFilter(this.primaryRuleSetTxt, this.primaryRuleSetTabViewer, this.primaryRuleSetFilter);
    // create the columns
    createPrimaryRuleSetTabColumns();

    this.primaryRuleSetTabViewer.setComparator(this.primaryTableSorter);
    this.primaryRuleSetTabViewer.addFilter(this.primaryRuleSetFilter);
    this.primaryRuleSetTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    return ruleSetComp;
  }

  /**
   * creates the columns of BC table viewer
   */
  // ICDM-1011
  private void createPrimaryRuleSetTabColumns() {
    // ICDM-886
    ColumnViewerToolTipSupport.enableFor(this.primaryRuleSetTabViewer, ToolTip.NO_RECREATE);
    createRuleSetNameColumn(this.primaryRuleSetTabViewer, this.primaryTableSorter);
  }

  /**
   * To Update the Page Buttons
   */
  public void updateContainer() {
    getContainer().updateButtons();
  }

  /**
   * creates unit column
   *
   * @param tabViewer
   * @param primaryTableSorter2
   */
  private void createRuleSetNameColumn(final GridTableViewer tabViewer, final RuleSetTabSorter tableSorter) {
    this.bcColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(tabViewer, "Rule Set Name", BC_NAME_COL_SIZE);

    this.bcColumn.setLabelProvider(new ColumnLabelProvider() {

      // ICDM-886
      /**
       * getText
       */
      @Override
      public String getText(final Object element) {
        String name = "";
        // Rule set
        if (element instanceof RuleSet) {
          name = ((RuleSet) element).getName();
        }
        return name;

      }
    });
    this.bcColumn.setEditingSupport(new GridTableEditingSupport(tabViewer, CommonUIConstants.COLUMN_INDEX_1));
    this.bcColumn.getColumn().addSelectionListener(
        GridTableViewerUtil.getInstance().getSelectionAdapter(this.bcColumn.getColumn(), 0, tableSorter, tabViewer));
  }


  /**
   * nezt pressed in case of adding the sec rule set
   */
  public void nextPressed() {

    this.ruleSetSltnPageResolver.setInput(this.calDataReviewWizard);
    this.ruleSetSltnPageResolver.processNextPressed();
    if (this.calDataReviewWizard.isDeltaReview() && !this.calDataReviewWizard.isProjectDataDeltaReview()) {
      this.calDataReviewWizard.getFilesSelWizPage().getReviewFilesSelectionPageResolver()
          .fillUIData(this.calDataReviewWizard);
    }
  }


  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {
    this.ruleSetSltnPageResolver.setInput(this.calDataReviewWizard);
  }


  /**
   * This method creates filter text
   *
   * @param filterTxt
   * @param ruleSetTabViewer
   * @param ruleSetFilter2
   */
  private void createRuleSetFilter(final Text filterTxt, final GridTableViewer ruleSetTabViewer,
      final RuleSetTabFilter ruleSetFilter) {
    final GridData gridData = getFilterTxtGridData();
    filterTxt.setLayoutData(gridData);
    filterTxt.setMessage("type filter text");
    filterTxt.addModifyListener(event -> {
      final String text = filterTxt.getText().trim();
      ruleSetFilter.setFilterText(text);
      ruleSetTabViewer.refresh();
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
    return this.ruleSetSltnPageValidator.checkNextEnabled();
  }

  /**
   * @return the ruleSetTabViewer
   */
  public GridTableViewer getPrimaryRuleSetTabViewer() {
    return this.primaryRuleSetTabViewer;
  }

  /**
   * error occured on next pressed
   */
  private boolean errorOccured;
  private Section secRuleSetSection;
  private Section primaryRuleSetSection;

  /**
   * @return the filterValue
   */
  public Button getFilterValue() {
    return this.filterValue;
  }

  /**
   * On back pressed make the selected rule set null.
   */
  public void backPressed() {
    this.ruleSetSltnPageResolver.processBackPressed();
  }


  /**
   * @param isPrimary the isRuleSetPrimary to set
   */
  public void enableDisableRuleSetPage(final boolean isPrimary) {
    this.isRuleSetPrimary = isPrimary;
    // Task 231283
    this.primaryRuleSetSection.setEnabled(!this.reviewWizard || this.isRuleSetPrimary);
    this.secRuleSetSection.setEnabled(true);
  }

  /**
   * @return GridTableViewer
   */
  public GridTableViewer getSecondaryResultTabViewer() {
    return this.secRuleSetTabViewer;
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
   * @return the primaryRuleSetTxt
   */
  public Text getPrimaryRuleSetTxt() {
    return this.primaryRuleSetTxt;
  }


  /**
   * @param primaryRuleSetTxt the primaryRuleSetTxt to set
   */
  public void setPrimaryRuleSetTxt(final Text primaryRuleSetTxt) {
    this.primaryRuleSetTxt = primaryRuleSetTxt;
  }


  /**
   * @return the secRuleSetFilter
   */
  public RuleSetTabFilter getSecRuleSetFilter() {
    return this.secRuleSetFilter;
  }


  /**
   * @param secRuleSetFilter the secRuleSetFilter to set
   */
  public void setSecRuleSetFilter(final RuleSetTabFilter secRuleSetFilter) {
    this.secRuleSetFilter = secRuleSetFilter;
  }


  /**
   * @return the primaryTableSorter
   */
  public RuleSetTabSorter getPrimaryTableSorter() {
    return this.primaryTableSorter;
  }


  /**
   * @param primaryTableSorter the primaryTableSorter to set
   */
  public void setPrimaryTableSorter(final RuleSetTabSorter primaryTableSorter) {
    this.primaryTableSorter = primaryTableSorter;
  }


  /**
   * @return the isRuleSetPrimary
   */
  public boolean isRuleSetPrimary() {
    return this.isRuleSetPrimary;
  }


  /**
   * @param isRuleSetPrimary the isRuleSetPrimary to set
   */
  public void setRuleSetPrimary(final boolean isRuleSetPrimary) {
    this.isRuleSetPrimary = isRuleSetPrimary;
  }


  /**
   * @return the secRuleSetTabViewer
   */
  public GridTableViewer getSecRuleSetTabViewer() {
    return this.secRuleSetTabViewer;
  }


  /**
   * @param secRuleSetTabViewer the secRuleSetTabViewer to set
   */
  public void setSecRuleSetTabViewer(final GridTableViewer secRuleSetTabViewer) {
    this.secRuleSetTabViewer = secRuleSetTabViewer;
  }


  /**
   * @return the secRuleSetTxt
   */
  public Text getSecRuleSetTxt() {
    return this.secRuleSetTxt;
  }


  /**
   * @param secRuleSetTxt the secRuleSetTxt to set
   */
  public void setSecRuleSetTxt(final Text secRuleSetTxt) {
    this.secRuleSetTxt = secRuleSetTxt;
  }


  /**
   * @return the primaryRuleSetFilter
   */
  public RuleSetTabFilter getPrimaryRuleSetFilter() {
    return this.primaryRuleSetFilter;
  }


  /**
   * @param primaryRuleSetFilter the primaryRuleSetFilter to set
   */
  public void setPrimaryRuleSetFilter(final RuleSetTabFilter primaryRuleSetFilter) {
    this.primaryRuleSetFilter = primaryRuleSetFilter;
  }


  /**
   * @return the secTableSorter
   */
  public RuleSetTabSorter getSecTableSorter() {
    return this.secTableSorter;
  }


  /**
   * @param secTableSorter the secTableSorter to set
   */
  public void setSecTableSorter(final RuleSetTabSorter secTableSorter) {
    this.secTableSorter = secTableSorter;
  }


  /**
   * @return the secRuleSetSection
   */
  public Section getSecRuleSetSection() {
    return this.secRuleSetSection;
  }


  /**
   * @param secRuleSetSection the secRuleSetSection to set
   */
  public void setSecRuleSetSection(final Section secRuleSetSection) {
    this.secRuleSetSection = secRuleSetSection;
  }


  /**
   * @return the primaryRuleSetSection
   */
  public Section getPrimaryRuleSetSection() {
    return this.primaryRuleSetSection;
  }


  /**
   * @param primaryRuleSetSection the primaryRuleSetSection to set
   */
  public void setPrimaryRuleSetSection(final Section primaryRuleSetSection) {
    this.primaryRuleSetSection = primaryRuleSetSection;
  }


  /**
   * @return the bcNameColSize
   */
  public static int getBcNameColSize() {
    return BC_NAME_COL_SIZE;
  }


  /**
   * @return the paramList
   */
  public List<A2LParameter> getParamList() {
    return this.paramList;
  }


  /**
   * @return the secondaryReviewRuleSetList
   */
  public List<RuleSet> getSecondaryReviewRuleSetList() {
    return this.secondaryReviewRuleSetList;
  }


  /**
   * @return the primaryReviewRuleSet
   */
  public RuleSet getPrimaryReviewRuleSet() {
    return this.primaryReviewRuleSet;
  }


  /**
   * @param primaryReviewRuleSet the primaryReviewRuleSet to set
   */
  public void setPrimaryReviewRuleSet(final RuleSet primaryReviewRuleSet) {
    this.primaryReviewRuleSet = primaryReviewRuleSet;
  }

  /**
   * @return the reviewWizard
   */
  public boolean isReviewWizard() {
    return this.reviewWizard;
  }


  /**
   * @return the secRuleSet
   */
  public static String getSecRuleSet() {
    return SEC_RULE_SET;
  }


  /**
   * @return the unSelectBtnWidth
   */
  public static int getUnSelectBtnWidth() {
    return UN_SELECT_BTN_WIDTH;
  }


  /**
   * @return the unSelectBtnHeight
   */
  public static int getUnSelectBtnHeight() {
    return UN_SELECT_BTN_HEIGHT;
  }


  /**
   * @return the currentUserBO
   */
  public CurrentUserBO getCurrentUserBO() {
    return this.currentUserBO;
  }


  /**
   * @return the errorOccured
   */
  public boolean isErrorOccured() {
    return this.errorOccured;
  }


  /**
   * @param filterValue the filterValue to set
   */
  public void setFilterValue(final Button filterValue) {
    this.filterValue = filterValue;
  }


  /**
   * @param primaryRuleSetTabViewer the primaryRuleSetTabViewer to set
   */
  public void setPrimaryRuleSetTabViewer(final GridTableViewer primaryRuleSetTabViewer) {
    this.primaryRuleSetTabViewer = primaryRuleSetTabViewer;
  }


  /**
   * @return the ruleSetSltnPageResolver
   */
  public RuleSetSltnPageResolver getRuleSetSltnPageResolver() {
    return this.ruleSetSltnPageResolver;
  }


  /**
   * @param ruleSetSltnPageResolver the ruleSetSltnPageResolver to set
   */
  public void setRuleSetSltnPageResolver(final RuleSetSltnPageResolver ruleSetSltnPageResolver) {
    this.ruleSetSltnPageResolver = ruleSetSltnPageResolver;
  }


  /**
   * @return the ruleSetSltnPageValidator
   */
  public RuleSetSltnPageValidator getRuleSetSltnPageValidator() {
    return this.ruleSetSltnPageValidator;
  }


  /**
   * @param ruleSetSltnPageValidator the ruleSetSltnPageValidator to set
   */
  public void setRuleSetSltnPageValidator(final RuleSetSltnPageValidator ruleSetSltnPageValidator) {
    this.ruleSetSltnPageValidator = ruleSetSltnPageValidator;
  }


  /**
   * @return the ruleSetSltnPageListener
   */
  public RuleSetSltnPageListener getRuleSetSltnPageListener() {
    return this.ruleSetSltnPageListener;
  }


  /**
   * @param ruleSetSltnPageListener the ruleSetSltnPageListener to set
   */
  public void setRuleSetSltnPageListener(final RuleSetSltnPageListener ruleSetSltnPageListener) {
    this.ruleSetSltnPageListener = ruleSetSltnPageListener;
  }


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @param calDataReviewWizard the calDataReviewWizard to set
   */
  public void setCalDataReviewWizard(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
  }


  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }


  /**
   * @return the unSelectBtn
   */
  public Button getUnSelectBtn() {
    return this.unSelectBtn;
  }


  /**
   * @param unSelectBtn the unSelectBtn to set
   */
  public void setUnSelectBtn(final Button unSelectBtn) {
    this.unSelectBtn = unSelectBtn;
  }


  /**
   * @return the bcColumn
   */
  public GridViewerColumn getBcColumn() {
    return this.bcColumn;
  }


  /**
   * @param bcColumn the bcColumn to set
   */
  public void setBcColumn(final GridViewerColumn bcColumn) {
    this.bcColumn = bcColumn;
  }


}
