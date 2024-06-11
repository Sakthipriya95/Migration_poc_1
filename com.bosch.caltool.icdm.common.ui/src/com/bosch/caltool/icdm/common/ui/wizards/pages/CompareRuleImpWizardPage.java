/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.client.bo.caldataimport.MultiCalDataImportCompObj;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.ImportParamContextMenu;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.sorters.CompareParamRulesTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.CompareParamRulesFilters;
import com.bosch.caltool.icdm.common.ui.table.filters.CompareParamRulesToolBarFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizardData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.apic.UnitServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This is the page which compares the existing values to the incoming values from the imported file will be added. This
 * page appears only if there are changes to the already existing values
 *
 * @author dmo5cob
 */
public class CompareRuleImpWizardPage extends WizardPage {


  /**
   * Defines two columns
   */
  private static final int NUM_COLS = 4;
  /**
   * First stage progress
   */
  private static final int PROG_2 = 80;
  /**
   * Second stage progress
   */
  private static final int PROG_1 = 30;
  /**
   * Entire progress
   */
  private static final int PROGRESS_COMPLETED = 100;
  /**
   * Main comp cols
   */
  private static final int COMP_COLS = 1;
  /**
   * Page title
   */
  private static final String PAGE_TITLE = "Compare Rules";
  /**
   * COLUMN WIDTH for parameter name col
   */
  private static final int COL_WIDTH_PARAM_NAME = 100;
  /**
   * COLUMN WIDTH for Dependencies col
   */
  private static final int COL_WIDTH_3 = 160;
  /**
   * COLUMN WIDTH for check box col
   */
  private static final int COL_WIDTH_4 = 25;
  /**
   * COLUMN WIDTH for update label
   */
  private static final int UPDATE_COL_WIDTH = 50;


  /**
   * To remove semicolon as the end of attr-val string
   */
  private static final int SEMICOLON_SIZE = 4;
  /**
   * Table Graph Composite instance
   */
  private CalDataTableGraphComposite calDataTableGraphComposite;
  /**
   * Page description
   */
  private static final String PAGE_DESCRIPTION = "Please select the rules to be updated";
  /**
   * Parameter col label
   */
  private static final String PARAM_NAME = "Parameter";
  /**
   * Long Name col label
   */
  private static final String DEPENDENCIES = "Dependencies";

  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer compareGridTableViewer;
  /**
   * AbstractViewerSorter instance
   */
  AbstractViewerSorter compareTableSorter;
  /**
   * SashForm instance
   */
  private SashForm topSashForm;
  /**
   * SashForm instance
   */
  private SashForm sashFormBottom;
  /**
   * CalDataFileImportWizard instance
   */
  private CalDataFileImportWizard importWizard;


  /**
   * width Hint
   */
  private static final int WIDTH_HINT = 20;
  /**
   * height Hint
   */
  private static final int HEIGHT_HINT = 20;

  /**
   * CalDataImportComparison instance
   */
  private CalDataImportComparisonModel selCompObj;

  /**
   * SashForm for the right section
   */
  private SashForm sashFormRight;
  private RuleDetailsSection ruleDtlsSection;
  private ParamPropEditSection paramEditSection;
  /**
   * CompareParamRulesToolBarFilter instance
   */
  private CompareParamRulesToolBarFilter compareToolBarFilter;
  /**
   * MultiCalDataImportCompObj
   */
  private MultiCalDataImportCompObj multiCompObj;

  /**
   * Table Graph Ui
   */
  private CompareRulesPageTableGraph tableGraphComp;


  private Button selectAllBtn;

  /**
   * @param pageName pg name
   */
  public CompareRuleImpWizardPage(final String pageName) {
    super(pageName);
    // Set page title
    setTitle(PAGE_TITLE);
    // set page description
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG4_67X57));
    setPageComplete(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    // create scrollable composite
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite workArea = new Composite(scrollComp, SWT.NONE);
    workArea.layout();
    // create layout for composite
    workArea.setLayout(new GridLayout(COMP_COLS, false));
    workArea.setLayoutData(GridDataUtil.getInstance().getGridData());

    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    // Create toolkit
    this.toolkit = new FormToolkit(parent.getDisplay());
    this.importWizard = (CalDataFileImportWizard) getWizard();
    // create sash form
    createTopSashForm(workArea);
    // top section
    createTableSection();
    createVerticalSashForm();
    // parameter properties section
    createParamPropSection();
    // create rules details section
    createBotttomSashForm();
    this.ruleDtlsSection = new RuleDetailsSection(this.toolkit, this);
    this.ruleDtlsSection.createRuleDetailsSection(this.sashFormBottom);
    // bottom section
    createBtmSection();
    // set weights to the sashform
    this.topSashForm.setWeights(new int[] { 5, 10 });
    this.sashFormRight.setWeights(new int[] { 3, 6 });
    this.sashFormBottom.setWeights(new int[] { 3, 2 });
    this.tableGraphComp = new CompareRulesPageTableGraph(this);

    scrollComp.setContent(workArea);
    scrollComp.setMinSize(workArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    // set control
    setControl(scrollComp);
  }

  /**
   * vertical sash form for the right section
   */
  private void createVerticalSashForm() {
    this.sashFormRight = new SashForm(this.topSashForm, SWT.VERTICAL);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    // Set the layout
    this.sashFormRight.setLayout(gridLayout);
    // Set layoutdata
    this.sashFormRight.setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   * Parameter properties section
   */
  private void createParamPropSection() {
    this.paramEditSection = new ParamPropEditSection(this.sashFormRight, this.toolkit, this);
    this.paramEditSection.createsectionParamProperties();
  }

  /**
   * Create bottom form
   */
  private void createBotttomSashForm() {
    this.sashFormBottom = new SashForm(this.sashFormRight, SWT.HORIZONTAL);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    // Set the layout
    this.sashFormBottom.setLayout(gridLayout);
    // Set layoutdata
    this.sashFormBottom.setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   * This method creates scrolled form
   *
   * @param parent
   */
  private void createTopSashForm(final Composite parent) {
    // Create sashform vertical
    this.topSashForm = new SashForm(parent, SWT.HORIZONTAL);
    // Set the layout
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    this.topSashForm.setLayout(layout);
    // Set layoutdata
    this.topSashForm.setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   * Top section
   */
  private void createTableSection() {
    Section topSection = SectionUtil.getInstance().createSection(this.topSashForm, this.toolkit,
        GridDataUtil.getInstance().getGridData(), "Parameters", false);
    GridLayout gridLayout = new GridLayout();
    final Form formTop = this.toolkit.createForm(topSection);
    formTop.getBody().setLayout(gridLayout);
    formTop.setLayoutData(GridDataUtil.getInstance().getGridData());
    // initialise the sorter
    this.compareTableSorter = new CompareParamRulesTabViewerSorter();
    // initialise the toolbar filter
    this.compareToolBarFilter = new CompareParamRulesToolBarFilter();

    Composite subComp = new Composite(formTop.getBody(), SWT.NONE);

    Composite markAll = new Composite(subComp, SWT.NONE);

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalAlignment = GridData.GRAB_HORIZONTAL;
    markAll.setLayoutData(gridData);
    markAll.setLayout(new GridLayout());

    this.selectAllBtn = new Button(markAll, SWT.CHECK);
    this.selectAllBtn.setText("Mark all parameter rules as 'Done'");

    this.selectAllBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        boolean selection = CompareRuleImpWizardPage.this.selectAllBtn.getSelection();
        GridItem[] items = CompareRuleImpWizardPage.this.compareGridTableViewer.getGrid().getItems();
        for (GridItem gridItem : items) {
          CalDataImportComparisonModel model = (CalDataImportComparisonModel) gridItem.getData();
          gridItem.setChecked(selection);
          model.setUpdateInDB(selection);
        }
        CompareRuleImpWizardPage.this.getCompareGridTableViewer().refresh();
        CompareRuleImpWizardPage.this.getRuleDtlsSection().populateRuleDetails();
      }
    });


    GridLayout btnLayout = new GridLayout();
    btnLayout.numColumns = NUM_COLS;
    btnLayout.makeColumnsEqualWidth = false;
    subComp.setLayout(btnLayout);
    GridData gData = new GridData(SWT.END);
    gData.horizontalAlignment = SWT.RIGHT;
    gData.grabExcessHorizontalSpace = true;
    subComp.setLayoutData(gData);


    createButtons(subComp);
    // create type filter
    Text filterTxt = this.toolkit.createText(formTop.getBody(), null, SWT.SINGLE | SWT.BORDER);
    filterTxt.setLayoutData(getFilterTxtGridData());
    filterTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    createTableViewer(formTop, filterTxt);
    // add right click context menu
    addRightClickMenu();
    topSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    topSection.setClient(formTop);
  }

  private void createTableViewer(final Form formTop, final Text filterTxt) {
    this.compareGridTableViewer = new GridTableViewer(formTop.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
    this.compareGridTableViewer.getGrid().setLinesVisible(true);
    this.compareGridTableViewer.getGrid().setHeaderVisible(true);
    this.compareGridTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.compareGridTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    // Create GridTableViewer columns
    createGridViewerColumns(this.compareGridTableViewer);
    // Set initial input to the table
    this.compareGridTableViewer.setInput("");
    ColumnViewerToolTipSupport.enableFor(this.compareGridTableViewer, ToolTip.NO_RECREATE);
    // Set comparator for the table
    this.compareGridTableViewer.setComparator(this.compareTableSorter);
    // Add selection changed listener
    this.compareGridTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();

        // ICDM-1999 Multi select
        if (selection.size() > 1) {
          // compare all the selected element's parameter type
          boolean disableFields = false;
          List list = selection.toList();
          CalDataImportComparisonModel firstObj = (CalDataImportComparisonModel) (list.get(0));
          String paramType = firstObj.getParamType();
          // create a MultiCalDataImportCompObj
          CompareRuleImpWizardPage.this.multiCompObj =
              new MultiCalDataImportCompObj(getImportWizard().getWizardData().getNewParamPropsMap(),
                  getImportWizard().getWizardData().getOldParamClassMap());
          // exact match
          boolean exactMatch = firstObj.getNewRule().isDcm2ssd();
          for (Object selObj : list) {
            CalDataImportComparisonModel impComparison = (CalDataImportComparisonModel) selObj;
            // add the comparison object to the multiple comp obj
            CompareRuleImpWizardPage.this.multiCompObj.getComparisonObjList().add(impComparison);
            // compare the param types
            if (!CommonUtils.isEqual(impComparison.getParamType(), paramType)) {
              disableFields = true;
              break;
            }
            if (!CommonUtils.isEqual(impComparison.getNewRule().isDcm2ssd(), exactMatch)) {
              disableFields = true;
              break;
            }
          }
          if (disableFields) {
            disableAllFields();
            setErrorMessage("Multi edit is possible for rules with same parameter type and exact match!");
          }
          else {
            try {
              CompareRuleImpWizardPage.this.multiCompObj.initialiseValues();
            }
            catch (DataException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            // populate the parameter details section based on selection
            CompareRuleImpWizardPage.this.paramEditSection.populateMultiParamDetails();
            // populate the parameter rules section based on selection
            CompareRuleImpWizardPage.this.ruleDtlsSection.populateMultiRuleDetails();
            CompareRuleImpWizardPage.this.tableGraphComp.populateMultiTableGraph(list);
          }
        }
        else {
          if (null == (CalDataImportComparisonModel) selected) {
            if (CompareRuleImpWizardPage.this.getCompareGridTableViewer().getGrid().getItemCount() > 0) {
              // The above check is to avoid ArrayIndexOutOfBounds Exception in case the table doesnot list any
              // parameters
              CompareRuleImpWizardPage.this.selCompObj = (CalDataImportComparisonModel) CompareRuleImpWizardPage.this
                  .getCompareGridTableViewer().getGrid().getItem(0).getData();
            }
          }
          else {
            CompareRuleImpWizardPage.this.selCompObj = (CalDataImportComparisonModel) selected;
          }
          // populate the table graph viewer based on selection
          CompareRuleImpWizardPage.this.tableGraphComp.populateTableGraph();
          // populate the parameter details section based on selection
          CompareRuleImpWizardPage.this.paramEditSection.populateParamDetails();
          // populate the parameter rules section based on selection
          CompareRuleImpWizardPage.this.ruleDtlsSection.populateRuleDetails();
        }
      }


    });


    CompareParamRulesFilters compareFilter = new CompareParamRulesFilters();
    // Add TableViewer filter
    this.compareGridTableViewer.addFilter(compareFilter);
    this.compareGridTableViewer.addFilter(this.compareToolBarFilter);
    // Add modify text listener to filter text
    addModifyTextListener(filterTxt, compareFilter, this.compareGridTableViewer);
  }

  /**
   * ICDM-1999 disable fields on the right side
   */
  private void disableAllFields() {
    this.paramEditSection.disableFields();
    this.ruleDtlsSection.disableFields();
    CompareRuleImpWizardPage.this.calDataTableGraphComposite.clearTableGraph();
  }

  /**
   * ICDM-1811 adding context menu
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    final ImportParamContextMenu contextMenu = new ImportParamContextMenu(this);
    menuMgr.addMenuListener(new IMenuListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        final IStructuredSelection selection =
            (IStructuredSelection) CompareRuleImpWizardPage.this.compareGridTableViewer.getSelection();
        final Object firstElement = selection.getFirstElement();


        if ((firstElement != null) && (!selection.isEmpty())) {
          // context menu for saving parameter rules as done
          contextMenu.addMarkAsDoneMenu(menuMgr, selection, CompareRuleImpWizardPage.this);
          // context menu for saving parameter rules as not done
          contextMenu.addMarkAsNotDoneMenu(menuMgr, selection, CompareRuleImpWizardPage.this);
        }
      }
    });
    // Create menu.
    final Menu menu = menuMgr.createContextMenu(this.compareGridTableViewer.getGrid());
    this.compareGridTableViewer.getGrid().setMenu(menu);
  }

  /**
   * Button griddata
   *
   * @return
   */
  private GridData getBtnGridData() {
    GridData btnGridData = new GridData(SWT.END);
    btnGridData.widthHint = WIDTH_HINT;
    btnGridData.heightHint = HEIGHT_HINT;
    btnGridData.verticalAlignment = GridData.END;
    btnGridData.grabExcessVerticalSpace = true;
    return btnGridData;
  }

  /**
   * Create buttons
   *
   * @param composite
   */
  private void createButtons(final Composite composite) {
    GridData btnGridData = getBtnGridData();

    final Button ruleExistsBtn = new Button(composite, SWT.TOGGLE);
    ruleExistsBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.TICK_16X16));
    ruleExistsBtn.setToolTipText("Rule Exists");
    ruleExistsBtn.setLayoutData(btnGridData);
    ruleExistsBtn.setSelection(true);
    ruleExistsBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CompareRuleImpWizardPage.this.compareToolBarFilter.setRuleExist(ruleExistsBtn.getSelection());
        CompareRuleImpWizardPage.this.compareGridTableViewer.refresh();
      }


    });
    final Button ruleNotExitsBtn = new Button(composite, SWT.TOGGLE);
    ruleNotExitsBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    ruleNotExitsBtn.setToolTipText("No Rule Exists");
    ruleNotExitsBtn.setLayoutData(btnGridData);
    ruleNotExitsBtn.setSelection(true);
    ruleNotExitsBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CompareRuleImpWizardPage.this.compareToolBarFilter.setNoRuleExist(ruleNotExitsBtn.getSelection());
        CompareRuleImpWizardPage.this.compareGridTableViewer.refresh();
      }
    });


  }

  /**
   * Filter text modify listener
   */
  private void addModifyTextListener(final Text filterText, final AbstractViewerFilter filter,
      final GridTableViewer tableViewer) {
    filterText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterText.getText().trim();
        filter.setFilterText(text);
        tableViewer.refresh();

      }
    });
  }

  /**
   * Bottom section
   */
  private void createBtmSection() {
    Section btmSection = SectionUtil.getInstance().createSection(this.sashFormBottom, this.toolkit,
        GridDataUtil.getInstance().getGridData(), CommonUIConstants.TABLE_GRAPH, false);

    btmSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create graph/table composite
    Composite parentComp = this.toolkit.createComposite(btmSection);
    // Set layout to graph/table composite
    parentComp.setLayout(new GridLayout());
    parentComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create graph/table composite
    Composite graphComp = this.toolkit.createComposite(parentComp);
    // Set layout to graph/table composite
    graphComp.setLayout(new GridLayout());
    graphComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    // Create table graph composite structure
    this.calDataTableGraphComposite = new CalDataTableGraphComposite(graphComp, this.topSashForm.getHorizontalBar(),
        this.topSashForm.getVerticalBar(), CDMLogger.getInstance());

    btmSection.setClient(parentComp);
  }


  /**
   * This method gridviewercolumns
   *
   * @param gridTableViewer
   */
  private void createGridViewerColumns(final GridTableViewer gridTableViewer) {
    createParamTypeColViewer(gridTableViewer);
    // Creates parameter gridviewer column
    createParamNameColViewer(gridTableViewer);
    // Creates func name gridviewer column
    createFuncNameColViewer(gridTableViewer);
    // Creates Rule LongName gridviewer column
    createRuleLongNameColViewer(gridTableViewer);
    // create rule exists column
    createRuleExistsColViewer(gridTableViewer);
    // create done column
    createDoneColViewer(gridTableViewer);
  }

  /**
   * @param gridTableViewer GridTableViewer
   */
  private void createDoneColViewer(final GridTableViewer gridTableViewer) {

    final GridViewerColumn chkBoxColumn = new GridViewerColumn(gridTableViewer, SWT.CHECK | SWT.CENTER);
    chkBoxColumn.getColumn().setWidth(UPDATE_COL_WIDTH);
    chkBoxColumn.getColumn().setText("Done");
    chkBoxColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        RuleDetailsSection ruleDetailsSection = CompareRuleImpWizardPage.this.ruleDtlsSection;
        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (element instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel compObj = (CalDataImportComparisonModel) element;


          gridItem.setChecked(cell.getVisualIndex(), false);
          if (compObj.isUpdateInDB()) {
            gridItem.setChecked(cell.getVisualIndex(), true);
            setImpValuesApplicability(ruleDetailsSection, compObj);
          }
        }
        gridItem.setCheckable(cell.getVisualIndex(), true);
      }
    });
    // Set EditingSupport to checkbox
    chkBoxColumn.setEditingSupport(new CheckEditingSupport(chkBoxColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        boolean booleanValue = ((Boolean) arg1).booleanValue();
        if (arg0 instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel comObj = (CalDataImportComparisonModel) arg0;
          boolean valid = validateUnit(comObj);// ICDM-2028
          if (valid) {
            comObj.setUpdateInDB(booleanValue);
            CompareRuleImpWizardPage.this.compareGridTableViewer.setSelection(new StructuredSelection(arg0));
          }
          getCompareGridTableViewer().update(comObj, null);
        }
      }
    });

    chkBoxColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        chkBoxColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_5, this.compareTableSorter, gridTableViewer));


  }

  /**
   * ICDM-2028
   *
   * @param compObj CalDataImportComparison
   * @return true if the unit is valid
   */
  public boolean validateUnit(final CalDataImportComparisonModel compObj) {
    boolean unitValid = true;
    if (!CommonUtils.isEqual(compObj.getParamType(), ParameterType.VALUE.getText())) {
      String unit = null;
      // get the old rule
      ReviewRule oldRule = compObj.getOldRule();
      // get the new rule
      ReviewRule newRule = compObj.getNewRule();
      // initialise upper and lower limits
      BigDecimal lowerLimit = null;
      BigDecimal upperLimit = null;
      String refValCalData = null;
      if (compObj.isUseNewUnit()) {
        unit = newRule.getUnit();
      }
      else {
        if (oldRule != null) {
          unit = oldRule.getUnit();
        }
      }
      // initialise reference value
      if (compObj.isUseNewRefVal()) {
        refValCalData = newRule.getRefValueDcmString();
      }
      else {
        if (oldRule != null) {
          refValCalData = oldRule.getRefValueDcmString();
        }
      }
      // initialise lower limit
      if (compObj.isUseNewLowLmt()) {
        lowerLimit = newRule.getLowerLimit();
      }
      else {
        if (oldRule != null) {
          lowerLimit = oldRule.getLowerLimit();
        }
      }
      // initialise upper limit
      if (compObj.isUseNewUpLmt()) {
        upperLimit = newRule.getUpperLimit();
      }
      else {
        if (oldRule != null) {
          upperLimit = oldRule.getUpperLimit();
        }
      }
      // check if atleast one field exists
      if ((unit != null) && (lowerLimit == null) && (upperLimit == null) && (refValCalData == null)) {
        // ICDM-2102 check for refVal in case of DCM
        unitValid = false;
      }
      else {
        unitValid = true;
      }
      if (!unitValid) {
        // if the unit is not valid , throw exception
        MessageDialogUtils.getErrorMessageDialog("Invalid unit",
            "When Unit is specified, min,max or dcm value has to be present");
      }
    }
    return unitValid;
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    // get the GridData for filter text
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }


  /**
   * param type col
   */
  private void createParamTypeColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn typeColumn = new GridViewerColumn(gridTableViewer, SWT.NONE);
    typeColumn.getColumn().setText("");
    typeColumn.getColumn().setWidth(COL_WIDTH_4);
    typeColumn.getColumn().setResizeable(false);
    typeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        if (element instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel obj = (CalDataImportComparisonModel) element;
          // get the image for param type
          return com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance()
              .getParamTypeImage(obj.getParamType());
        }
        return null;
      }
    });

    typeColumn.getColumn().addSelectionListener(
        // ICDM-2201
        GridTableViewerUtil.getInstance().getSelectionAdapter(typeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0,
            this.compareTableSorter, gridTableViewer));
  }

  /**
   * Update checkbox col
   */
  private void createRuleExistsColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn ruleExistsColumn = new GridViewerColumn(gridTableViewer, SWT.NONE);
    ruleExistsColumn.getColumn().setText("Rule Exists");
    ruleExistsColumn.getColumn().setWidth(70);
    ruleExistsColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel compObj = (CalDataImportComparisonModel) element;
          if (compObj.getOldRule() == null) {
            // display no if rule does not exist already
            return "No";
          }
        }
        // display yes if rule already exist
        return "Yes";
      }
    });
    ruleExistsColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        ruleExistsColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.compareTableSorter, gridTableViewer));
  }


  /**
   * @param gridTableViewer
   */
  private void createParamNameColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn paramNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, PARAM_NAME, COL_WIDTH_PARAM_NAME);

    paramNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof CalDataImportComparisonModel) {
          return String.valueOf(((CalDataImportComparisonModel) element).getParamName());
        }
        return null;
      }
    });
    // Add selection listener column
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        paramNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.compareTableSorter, gridTableViewer));

  }

  /**
   * @param gridTableViewer
   */
  private void createFuncNameColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn funcNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, "Function", COL_WIDTH_PARAM_NAME);

    funcNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel compModel = (CalDataImportComparisonModel) element;
          return compModel.getFuncNames();
        }
        return null;
      }
    });
    // Add selection listener column
    funcNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        funcNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.compareTableSorter, gridTableViewer));
    if (!(this.importWizard.getWizardData().getImportObject() instanceof Function)) {
      funcNameColumn.getColumn().setVisible(false);
    }
  }

  /**
   * @param gridTableViewer
   */
  private void createRuleLongNameColViewer(final GridTableViewer gridTableViewer) {
    final GridViewerColumn paramNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(gridTableViewer, DEPENDENCIES, COL_WIDTH_3);

    paramNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof CalDataImportComparisonModel) {
          CalDataImportComparisonModel calDataImportComparison = (CalDataImportComparisonModel) element;
          String paramDependency = "";
          // form the string to display the attr dependency and value list
          if (CommonUtils.isNotEmpty(calDataImportComparison.getDependencyList())) {
            SortedSet<AttributeValueModel> depSet = new TreeSet<>(calDataImportComparison.getDependencyList());
            paramDependency = getAttrValString(new TreeSet<>(depSet));
            calDataImportComparison.setParamDependency(paramDependency);
            return paramDependency;
          }
          calDataImportComparison.setParamDependency(paramDependency);
        }
        return null;
      }
    });
    // Add selection listener column
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        paramNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.compareTableSorter, gridTableViewer));

  }


  /**
   * @param attrValSet
   * @return
   */
  public static String getAttrValString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getValue().getName()).append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - SEMICOLON_SIZE).trim();
    }
    return result;
  }

  /**
   * Method to invoke pages when next button is pressed
   */
  public void nextPressed() {
    try {

      getContainer().run(true, true, new IRunnableWithProgress() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run(final IProgressMonitor monitor) {

          monitor.beginTask("Importing Calibration Data", PROGRESS_COMPLETED);
          monitor.worked(PROG_1);

          long startTime = System.currentTimeMillis();
          if (!(CompareRuleImpWizardPage.this.importWizard.getWizardData().getImportObject() instanceof Function)) {
            // incase of ruleset
            createParamsInInputFile();
          }
          CDMLogger.getInstance()
              .info("Time taken for insertion of parameters:" + (System.currentTimeMillis() - startTime));

          importData(monitor);
          monitor.worked(PROGRESS_COMPLETED);
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException | InterruptedException ex) {
      CDMLogger.getInstance().error(ex.getLocalizedMessage(), ex, Activator.PLUGIN_ID);
    }
    this.importWizard.getSummaryWizardPage().setPageComplete(true);
  }

  /**
   * This method adds the parameters to the ruleset which are present in the input file
   *
   * @param paramsLoader
   */
  private void createParamsInInputFile() {

    List<String> filterParamsToBeAdded =
        filterParamsToBeAdded(this.importWizard.getCalImportData().getInvalidParamSet());

    this.importWizard.getCalImportData().setParamsTobeInserted(filterParamsToBeAdded);

  }

  /**
   * @param invalidParams Set<String>
   * @return
   */
  private List<String> filterParamsToBeAdded(final Set<String> invalidParams) {
    // create a new list
    List<String> paramsToBeAdded = new ArrayList<String>();
    // iterate the whole map of getCalDataCompMap()
    for (Entry<String, List<CalDataImportComparisonModel>> paramAndCompObjs : this.importWizard.getCalImportData()
        .getCalDataCompMap().entrySet()) {
      // iterate the comparison objects for each parameter
      for (CalDataImportComparisonModel compObj : paramAndCompObjs.getValue()) {
        String paramName = paramAndCompObjs.getKey();
        if (compObj.isUpdateInDB() && invalidParams.contains(paramName)) {
          // if atleast one rule is marked as 'Done', then add the parameter to the list
          paramsToBeAdded.add(paramName);
          break;
        }
      }

    }
    return paramsToBeAdded;
  }

  /**
   * Method to set table input
   *
   * @param rulesSet input rules
   */
  public void setTabInput(final SortedSet<CalDataImportComparisonModel> rulesSet) {
    Display.getDefault().syncExec(new Runnable() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        if (CommonUtils.isNotEmpty(rulesSet)) {
          CompareRuleImpWizardPage.this.getCompareGridTableViewer().setInput(rulesSet);
          // set the selection to the first row
          CompareRuleImpWizardPage.this.getCompareGridTableViewer().getGrid().setSelection(0);
          CompareRuleImpWizardPage.this.selCompObj = (CalDataImportComparisonModel) CompareRuleImpWizardPage.this
              .getCompareGridTableViewer().getGrid().getItem(0).getData();
          // to reset the Mark all checkbox and Done checkboxes in grid table
          CompareRuleImpWizardPage.this.selectAllBtn.setSelection(false);
          GridItem[] items = CompareRuleImpWizardPage.this.compareGridTableViewer.getGrid().getItems();
          for (GridItem gridItem : items) {
            CalDataImportComparisonModel model = (CalDataImportComparisonModel) gridItem.getData();
            gridItem.setChecked(false);
            model.setUpdateInDB(false);
          }
          CompareRuleImpWizardPage.this.getCompareGridTableViewer().refresh();
          CompareRuleImpWizardPage.this.tableGraphComp.populateTableGraph();
          // populate parameter details and rule details
          CompareRuleImpWizardPage.this.paramEditSection.populateParamDetails();
          CompareRuleImpWizardPage.this.ruleDtlsSection.populateRuleDetails();
        }
      }
    });
  }

  /**
   * Imports caldata files
   *
   * @param monitor progress monitor
   */
  private void importData(final IProgressMonitor monitor) {

    if (!validateDontCareAttrValCombination(this.importWizard.getCalImportData())) {
      setPageComplete(false);
    }

    CalDataFileImportWizardData wizardData = this.importWizard.getWizardData();

    this.importWizard.getCalImportData().setNewParamPropsMap(wizardData.getNewParamPropsMap());

    this.importWizard.getCalImportData().setOldParamClassMap(wizardData.getOldParamClassMap());

    ParamCollectionDataProvider paramColDataProvider = wizardData.getParamColDataProvider();

    CalDataImporterHandler handler = new CalDataImporterHandler();

    CalDataImportSummary impSummary = null;

    try {
      Set<Unit> unitsToBeCreated = new HashSet<>();
      for (List<CalDataImportComparisonModel> calDataList : this.importWizard.getCalImportData().getCalDataCompMap()
          .values()) {
        for (CalDataImportComparisonModel calData : calDataList) {
          String unit = calData.getNewRule().getUnit();
          if (CommonUtils.isNotEmptyString(unit) && !this.ruleDtlsSection.getUnits().contains(unit)) {
            Unit unitToCreate = new Unit();
            unitToCreate.setUnitName(unit);
            unitsToBeCreated.add(unitToCreate);
          }
        }
      }
      if (!unitsToBeCreated.isEmpty()) {
        new UnitServiceClient().create(unitsToBeCreated);
      }
      impSummary = handler.saveParamAndRules(this.importWizard.getCalImportData(),
          wizardData.getImportObject().getId().toString(),
          paramColDataProvider.getObjectTypeName(wizardData.getImportObject()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    monitor.worked(PROG_2);
    CompareRuleImpWizardPage.this.importWizard.getSummaryWizardPage().refresh(impSummary);

  }


  private boolean validateDontCareAttrValCombination(final CalDataImportData importData) {
    if (CommonUtils.isNotEmpty(importData.getCalDataCompMap())) {

      for (Entry<String, List<CalDataImportComparisonModel>> caldataEntry : importData.getCalDataCompMap().entrySet()) {
        int numOfDepAttrWithDontCare = 0;
        String paramName = caldataEntry.getKey();

        for (CalDataImportComparisonModel compModelForParam : caldataEntry.getValue()) {
          numOfDepAttrWithDontCare = calculateNumOfAttrWithDontCareValues(compModelForParam);

          if (CommonUtils.isNotEmpty(compModelForParam.getDependencyList()) &&
              CommonUtils.isEqual(compModelForParam.getDependencyList().size(), numOfDepAttrWithDontCare)) {

            MessageDialogUtils.getErrorMessageDialog("Error",
                "Invalid attribute value combination for " + paramName +
                    ". Atleast one dependent attribute must have value other than " +
                    CDRConstants.DONT_CARE_ATTR_VALUE_TEXT + ".");
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * @param numOfDepAttrWithDontCare
   * @param compModelForParam
   * @return
   */
  private int calculateNumOfAttrWithDontCareValues(final CalDataImportComparisonModel compModelForParam) {
    int numOfDepAttrWithDontCare = 0;
    if (CommonUtils.isNotEmpty(compModelForParam.getDependencyList())) {
      for (AttributeValueModel valModel : compModelForParam.getDependencyList()) {
        if (CDRConstants.DONT_CARE_ATTR_VALUE_TEXT.equals(valModel.getValue().getName())) {
          numOfDepAttrWithDontCare++;
        }
      }
    }
    return numOfDepAttrWithDontCare;
  }

  /**
   * @return CalDataImportComparison
   */
  public CalDataImportComparisonModel getSelCompObj() {
    return this.selCompObj;
  }

  /**
   * @return the importWizard
   */
  public CalDataFileImportWizard getImportWizard() {
    return this.importWizard;
  }


  /**
   * @return the ruleDtlsSection
   */
  public RuleDetailsSection getRuleDtlsSection() {
    return this.ruleDtlsSection;
  }

  /**
   * @return the multiCompObj
   */
  public MultiCalDataImportCompObj getMultiCompObj() {
    return this.multiCompObj;
  }

  /**
   * @return the compareGridTableViewer
   */
  public GridTableViewer getCompareGridTableViewer() {
    return this.compareGridTableViewer;
  }

  /**
   * @return the tableGraphComp
   */
  public CompareRulesPageTableGraph getTableGraphComp() {
    return this.tableGraphComp;
  }


  /**
   * @return the calDataTableGraphComposite
   */
  public CalDataTableGraphComposite getCalDataTableGraphComposite() {
    return this.calDataTableGraphComposite;
  }

  /**
   * @param ruleDetailsSection
   * @param compObj
   */
  private void setImpValuesApplicability(final RuleDetailsSection ruleDetailsSection,
      final CalDataImportComparisonModel compObj) {
    compObj.setUseNewRefVal(ruleDetailsSection.getRefVaImpValue().getSelection());
    compObj.setUseNewMaturityLvl(ruleDetailsSection.getUseImpValueMaturityLvl().getSelection());
    compObj.setUseNewLowLmt(ruleDetailsSection.getLowLimitImpValue().getSelection());
    compObj.setUseNewRvwMtd(ruleDetailsSection.getUseImpValueRvwMtd().getSelection());
    compObj.setUseNewUnit(ruleDetailsSection.getUseUnitBtn().getSelection());
    compObj.setUseNewUpLmt(ruleDetailsSection.getUpperLimitImpValue().getSelection());
  }


}
