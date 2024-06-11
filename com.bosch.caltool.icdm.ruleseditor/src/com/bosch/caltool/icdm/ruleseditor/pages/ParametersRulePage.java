/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParamRulesModel;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition.SortColumns;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.actions.CopyRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.DeleteRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.EditMultiRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.EditRuleWithDepAction;
import com.bosch.caltool.icdm.ruleseditor.actions.MaturityLevelActions;
import com.bosch.caltool.icdm.ruleseditor.actions.PasteRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.RuleViewerNatMouseClickAction;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.nattable.filters.CustomDisplayColumnChooserCommandHandler;
import com.bosch.caltool.icdm.ruleseditor.sorters.CDRFuncParamTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.CDRParamFilter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewParamToolBarFilters;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RulesEditorOutlineFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ruleseditor.views.providers.AttrValColumnHeaderDataProvider;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ListPageTableLabelProvider;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamRulesColHeaderLabelAccumulator;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamRulesPageNatToolTip;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamTableInputProvider;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author bru2cob ICDM-1061
 */
public class ParametersRulePage<D extends IParameterAttribute, P extends IParameter> extends AbstractNatFormPage
    implements ISelectionListener {


  /**
   * Constant string
   */
  private static final String PARAMETER_RULES = "Parameter Rules";

  /**
   * Freeze column constant
   */
  private static final int FREEZE_ROW_NUM = 0;

  /**
   * Freeze column constant
   */
  private static final int FREEZE_COLUMN_NUM = 7;

  /**
   * Col width
   */
  private static final int REVIEW_MET_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int UNIT_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int EXACT_MATCH_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int REF_VAL_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int UPPER_LIMIT_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int LOWER_LIMIT_COL_WIDTH = 25;

  /**
   * Col width
   */
  private static final int GRID_COLUMN_COUNT_TWO = 2;

  /**
   * color blue
   */
  private static final int COLOR_BLUE = 231;

  /**
   * color green
   */
  private static final int COLOR_GREEN = 212;

  /**
   * color red
   */
  private static final int COLOR_RED = 197;

  /**
   * Constant for one
   */
  private static final int SIZE_ONE = 1;

  /**
   * sash form weight
   */
  private static final int SASH_WGHT_4 = 4;

  /**
   * sash form weight
   */
  private static final int SASH_WGHT_1 = 1;

  /**
   * Defining two cols
   */
  private static final int TWO_COLS = 2;

  /**
   * Represents all parameters of function
   */
  private static final String ALL = "<ALL>";

  /**
   * type column width
   */
  private static final int TYPE_COL_WIDTH = 25;
  /**
   * Indicator column width
   */

  private static final int INDICATOR_COL_WIDTH = 80;

  /**
   * name column width
   */
  private static final int NAME_COL_WIDTH = 300;

  /**
   * Editor instance
   */
  private final ReviewParamEditor editor;

  /**
   * Formtoolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Instance of form
   */
  private Form nonScrollableForm;
  /**
   * Sashform for below section
   */
  private SashForm mainComposite;
  /**
   * Composite for function paramaters
   */
  private Composite compositeOne;
  /**
   * form for function paramaters
   */
  private Form leftForm;

  /**
   * Composite for paramater rules
   */
  private Composite compositeTwo;
  /**
   * Section for function paramaters
   */
  private Section sectionLeft;
  /**
   * Function parameter table filtertest
   */
  private Text filterTxt;
  /**
   * Function parameter table
   */
  private GridTableViewer fcTableViewer;
  /**
   * Function parameter filter
   */
  private CDRParamFilter paramFilter;
  /**
   * Function parameter sorter
   */
  private CDRFuncParamTabViewerSorter fcTabSorter;

  /**
   * Instance for storing the selected func version
   */
  private String selFuncVersion;

  /**
   * Function version composite
   */
  private Composite topComposite;

  /**
   * Instance of selected parameter of the function
   */
  private IParameter selectedParam;
  /**
   * Instance of selected function
   */
  private final ParamCollection cdrFunction;

  /**
   * Add new attr/value button
   */
  private Button addRuleBtn;
  /**
   * Set<RuleDefinition> instance
   */
  private final Set<RuleDefinition> inputMode = new TreeSet<>();

  /**
   * CustomFilterGridLayer instance
   */
  @SuppressWarnings("rawtypes")
  private CustomFilterGridLayer ruleFilterGridLayer;
  /**
   * NatTable instance
   */
  private NatTable natTable;
  /**
   * List<AbstractParameterAttribute> instance
   */
  private final List<D> attrlist = new ArrayList<>();
  /**
   * HashMap<Integer, String> instance
   */
  private Map<Integer, String> propertyToLabelMap;

  /**
   * Form instance
   */
  private Form paramMappingForm;

  /**
   * Section instance
   */
  private Section paramMappingSection;

  /**
   * ParamRulesModel instance
   */
  private ParamRulesModel<D, P> model;

  /**
   * RowSelectionProvider<RuleDefinition> instance
   */
  private RowSelectionProvider<RuleDefinition> selectionProvider;

  /**
   * Map<Long, String> instance. Attr id is the Key
   */
  private final Map<Attribute, String> combiMap = new TreeMap<>();

  /**
   * ReviewParamToolBarFilters instance
   */
  private ReviewParamToolBarFilters toolBarFilters;

  /**
   * Button instance
   */
  private Button addDefRuleBtn;

  /**
   * Label instance
   */
  private Label chooseColsLabel;

  /**
   * ToolBarManager instance
   */
  private ToolBarManager toolBarManager;

  /**
   * ParamRulesColHeaderLabelAccumulator instance
   */
  private ParamRulesColHeaderLabelAccumulator columnLabelAccumulator;

  /**
   * CUSTOM_COMPARATOR_LABEL constant
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * FunctionVersionSection instance
   */
  FunctionVersionSection funcSelcSec;

  /**
   * RulesEditorOutlineFilter
   */
  private RulesEditorOutlineFilter outlineFilter;

  private boolean otherRulesSelected;

  private ParamCollectionDataProvider paramColDataProvider;

  private ParameterDataProvider parameterDataProvider;

  /**
   * @param editor ReviewResultEditor
   * @param abstractParameterCollection selected function
   */
  public ParametersRulePage(final FormEditor editor, final ParamCollection abstractParameterCollection) {
    super(editor, PARAMETER_RULES, PARAMETER_RULES);
    this.editor = (ReviewParamEditor) editor;
    this.cdrFunction = abstractParameterCollection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    /**
     * create the base form
     */
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(
        com.bosch.caltool.icdm.common.util.CommonUtils.concatenate(this.editor.getPartName(), " - ", PARAMETER_RULES));
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = TWO_COLS;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    /**
     * create the top composite on the form
     */
    this.topComposite = new Composite(this.nonScrollableForm.getBody(), SWT.NONE);
    this.topComposite.setLayout(gridLayout);
    this.topComposite.setLayoutData(gridData1);

    /**
     * create the bottom composite on the form with two columns
     */
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);

    /**
     * Create the form content
     */
    createFormContent(mform);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {

    this.paramColDataProvider = this.editor.getEditorInput().getDataProvider();
    this.formToolkit = managedForm.getToolkit();
    /**
     * Create func version section in first compiste
     */
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      this.funcSelcSec =
          new FunctionVersionSection(ParametersRulePage.this, this.editor.getEditorInput(), this.topComposite);
      this.funcSelcSec.createFuncSelcSec(this.formToolkit);
    }
    /**
     * Create parameters and rules in the second composite
     */
    createBottomComposites();
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }

  /**
   * This method initializes composite
   */
  private void createBottomComposites() {

    /**
     * Create function parameters composite on left side
     */
    createLeftComposite();
    /**
     * Create parameters rules composite on right side
     */
    createRightComposite();
    setTabViewerInput(false);
    createToolBarAction();
    this.mainComposite.setWeights(new int[] { SASH_WGHT_1, SASH_WGHT_4 });
  }

  /**
   * This method initializes compositeOne
   */
  private void createLeftComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.compositeOne = new Composite(this.mainComposite, SWT.NONE);

    final GridLayout gridLayout = new GridLayout();
    /**
     * Creates the function parameters section on left composite
     */
    createLeftSection(this.formToolkit, gridLayout);

    this.compositeOne.setLayout(gridLayout);
    this.compositeOne.setLayoutData(gridData);

  }

  /**
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout
   */
  private void createLeftSection(final FormToolkit toolkit, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionLeft = toolkit.createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionLeft.setText("Parameters");
    this.sectionLeft.setExpanded(true);
    this.sectionLeft.getDescriptionControl().setEnabled(false);
    /**
     * Creates the function parameters table
     */
    createLeftForm(toolkit, gridLayout);
    this.sectionLeft.setLayoutData(gridData);
    this.sectionLeft.setClient(this.leftForm);
  }


  /**
   * Create the PreDefined Filter for the page-
   */
  private void createToolBarAction() {
    this.toolBarManager = (ToolBarManager) getToolBarManager();

    ToolBar toolbar;
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      toolbar = this.toolBarManager.createControl(this.funcSelcSec.getSectionOne());
    }
    else {
      toolbar = this.toolBarManager.createControl(this.paramMappingSection);
    }

    final ReviewRuleToolBarActionSet toolBarActionSet =
        new ReviewRuleToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);


    toolBarActionSet.paramWithDepnAction(this.toolBarManager, this.toolBarFilters, null, this.fcTableViewer);

    toolBarActionSet.paramWithNoDepnAction(this.toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    addResetAllFiltersAction();

    this.toolBarManager.update(true);

    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      this.funcSelcSec.getSectionOne().setTextClient(toolbar);
    }
    else {
      this.paramMappingSection.setTextClient(toolbar);
    }

  }

  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {

    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.fcTableViewer);
    addResetFiltersAction();
  }


  /**
   * @param toolkit This method initializes form
   * @param gridLayout
   */
  private void createLeftForm(final FormToolkit toolkit, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.leftForm = toolkit.createForm(this.sectionLeft);
    this.filterTxt = toolkit.createText(this.leftForm.getBody(), null, SWT.SINGLE | SWT.BORDER);

    createFilterTxt();
    this.parameterDataProvider = getEditor().getEditorInput().getParamDataProvider();

    this.fcTabSorter = new CDRFuncParamTabViewerSorter(this.parameterDataProvider);

    this.fcTableViewer = new CustomGridTableViewer(this.leftForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    // set the status bar for the function parameters table
    initializeEditorStatusLineManager(this.fcTableViewer);

    this.fcTableViewer.getGrid().setLayoutData(gridData);

    this.fcTableViewer.getGrid().setLinesVisible(true);
    this.fcTableViewer.getGrid().setHeaderVisible(true);

    this.leftForm.getBody().setLayout(gridLayout);
    createTabColumns();

    // ICDM-1348
    com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance()
        .addKeyListenerToCopyNameFromViewer(this.fcTableViewer);
    this.fcTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.fcTableViewer.setLabelProvider(new ListPageTableLabelProvider(this.parameterDataProvider));

    this.fcTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        setSelectedParameter();
      }
    });


    /**
     * Display the status of the table when it is focussed
     */
    this.fcTableViewer.getGrid().addFocusListener(new FocusListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusLost(final FocusEvent fLost) {
        // No Implemantation
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ParametersRulePage.this.fcTableViewer);
      }
    });


    // Invoke TableViewer Column sorters
    invokeColumnSorter();

    // Add filters to the TableViewer
    addFilters();


    addRightClickMenuParamTableViewer();

    final ReviewParamEditor reviewParamEditor = getEditor();
    final SelectionProviderMediator selectionProviderMediator = reviewParamEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.fcTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);

  }

  /**
   * Right click context menu for parameters
   */
  private void addRightClickMenuParamTableViewer() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener((final IMenuManager mgr) -> {

      final IStructuredSelection selection =
          (IStructuredSelection) ParametersRulePage.this.fcTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (!selection.isEmpty())) {
        // ICDM-1201
        final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
        final CommonActionSet cdrActionSet = new CommonActionSet();
        List<Object> selectedObj = new ArrayList<>();
        selectedObj.add(firstElement);
        IParameter param = (IParameter) firstElement;
        addRuleSetContextMenuAction(menuMgr, mgr, cdrActionSet, param);

        // Add Show Series statistics menu action
        cdmActionSet.addShowSeriesStatisticsMenuAction(mgr, selectedObj, true /* To enable action */);
        menuMgr.add(new Separator());
        cdrActionSet.copyParamNameToClipboardAction(menuMgr, param.getName());// ICDM-1348
        menuMgr.add(new Separator());
        PasteRuleAction pasteRuleAction =
            new PasteRuleAction(firstElement, ParametersRulePage.this.cdrFunction, ParametersRulePage.this.editor,
                ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction),
                ParametersRulePage.this.selectedParam, ParametersRulePage.this.paramColDataProvider,
                ParametersRulePage.this.parameterDataProvider);
        menuMgr.add(pasteRuleAction);

        // Context menu to Open Links in default browser created for ruleset rules and common Rules
        List<ReviewRule> cdrRuleList = new ArrayList<>();
        if (CommonUtils.isNotEmpty(this.parameterDataProvider.getRuleList(param))) {
          cdrRuleList.addAll(this.parameterDataProvider.getRuleList(param));
        }
        List<RuleLinks> ruleLinksList = new ArrayList<>();
        for (ReviewRule rule : cdrRuleList) {
          ruleLinksList.addAll(rule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule());
        }

        cdrActionSet.openRuleLinkAction(menuMgr, ruleLinksList);
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.fcTableViewer.getControl());
    this.fcTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.fcTableViewer);

  }

  /**
   * @param menuMgr
   * @param mgr
   * @param cdrActionSet
   */
  private void addRuleSetContextMenuAction(final MenuManager menuMgr, final IMenuManager mgr,
      final CommonActionSet cdrActionSet, final IParameter parameter) {
    if (ParametersRulePage.this.editor.getEditorInput().getCdrObject() instanceof RuleSet) {

      RuleSetParameter param = (RuleSetParameter) parameter;
      ReviewRule reviewRule = this.parameterDataProvider.getReviewRule(param);
      if (CommonUtils.isNull(reviewRule)) {
        // copy RuleSet Link
        cdrActionSet.copyRuleSetLinktoClipBoard(mgr,
            (RuleSet) ParametersRulePage.this.editor.getEditorInput().getCdrObject());
        // RuleSet Link in outlook Action
        final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_SET_LINK,
            ParametersRulePage.this.editor.getEditorInput().getCdrObject());
        linkAction.setEnabled(true);
        menuMgr.add(linkAction);

      }
      else {
        //for simple rule at parameter level
        cdrActionSet.copyRuleLinkOfParametertoClipBoard(mgr,
            (RuleSet) ParametersRulePage.this.editor.getEditorInput().getCdrObject(), reviewRule, parameter);
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put(ApicConstants.RULE_ID, reviewRule.getRuleId().toString());
        additionalDetails.put(ApicConstants.RULESET_PARAM_ID, param.getId().toString());
        // Rule of a parameter Link in outlook Action
        final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_LINK_OF_PARAMETER,
            ParametersRulePage.this.editor.getEditorInput().getCdrObject(), additionalDetails);
        linkAction.setEnabled(true);
        menuMgr.add(linkAction);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    boolean setSelection = false;
    if ((this.editor.getEditorInput().getCdrFuncVersion() != null) && (this.funcSelcSec != null)) {
      this.funcSelcSec.getComboFuncVersion()
          .select(this.funcSelcSec.getComboFuncVersion().indexOf(this.editor.getEditorInput().getCdrFuncVersion()));
    }

    if (this.funcSelcSec != null) {

      if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          "Version".equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(true);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }
      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          "Alternative".equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(true);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }

      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          "Variant".equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(true);
      }
    }
    if (this.outlineFilter != null) {
      filterOutlineSelection(this.editor.getOutlinePageCreator().getOutlinePageSelection());
    }
    setTabViewerInput(false);
    IParameter cdrFuncParam = this.editor.getEditorInput().getCdrFuncParam();
    // To synchronise the selected row in the tableviewer
    if ((cdrFuncParam != null) && (this.fcTableViewer != null)) {
      this.selectedParam = cdrFuncParam;
      this.fcTableViewer.setSelection(new StructuredSelection(cdrFuncParam), true);
      this.fcTableViewer.getSelection();
      setSelection = true;
    } // If no parameter is selected then clear the fields
    if (!setSelection) {
      this.selectedParam = null;
    }
    setSelectedParameter();
    super.setActive(active);
  }

  /**
   * Sets the tableviewer input
   *
   * @param refreshNeed true if refresh needed
   */
  public void setTabViewerInput(final boolean refreshNeeded) {
    ParamCollectionDataProvider dataProvider = this.editor.getEditorInput().getDataProvider();
    this.parameterDataProvider = getEditor().getEditorInput().getParamDataProvider();
    ParamTableInputProvider inputProvider = new ParamTableInputProvider(this.cdrFunction, this.editor);

    if (dataProvider.hasVersions(this.cdrFunction) && (this.funcSelcSec != null)) {
      final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
      /**
       * Sets the table input based on the version and the radio buttons selection
       */
      if (ALL.equalsIgnoreCase(this.funcSelcSec.getComboFuncVersion().getItem(index))) {
        setTableInputForAllVers(refreshNeeded, inputProvider);
      }
      else {
        try {
          this.selFuncVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);
          inputProvider.setVersionSel(this.selFuncVersion);
          Map<String, ?> paramMap = inputProvider.getParamMap();

          inputProvider.setChkVar(this.funcSelcSec.getChkVariant().getSelection() ? "true" : "false");

          this.editor.getEditorInput().setCdrFuncVersion(ParametersRulePage.this.selFuncVersion);

          // enable radio buttons
          this.editor.getEditorInput().setCdrFuncVersion(this.selFuncVersion);
          this.funcSelcSec.getChkFuncVersion().setEnabled(true);
          this.funcSelcSec.getChkAlternative().setEnabled(true);
          this.funcSelcSec.getChkVariant().setEnabled(true);
          this.fcTableViewer.setInput(paramMap.values());

        }
        catch (Exception exp) {
          CDMLogger.getInstance().error("Error when fetching parameter and rules", exp);
        }
      }
    }
    else if (this.fcTableViewer != null) {
      new ParamTableContentSetter().setInputForRuleSet(refreshNeeded, this.fcTableViewer, this.editor, this.cdrFunction,
          this);

    }
    if (refreshNeeded && (null != this.fcTableViewer) && (this.fcTableViewer.getGrid().getItems().length > 0)) {

      setSelectedParameter();
      this.fcTableViewer.getGrid().setFocus();
    }

  }

  /**
   * @param refreshNeeded
   * @param inputProvider
   */
  private void setTableInputForAllVers(final boolean refreshNeeded, final ParamTableInputProvider inputProvider) {
    this.editor.getEditorInput().setCdrFuncVersion(ALL);
    this.fcTableViewer.getGrid().removeAll();

    Map<String, ?> allParamsMap = null;
    try {
      allParamsMap = (this.editor.getEditorInput().getParamMap() != null) && !refreshNeeded
          ? this.editor.getEditorInput().getParamMap() : inputProvider.getParamMap();

      this.fcTableViewer.setInput(allParamsMap.values());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error fetching param details: " + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // disable radio buttons
    this.funcSelcSec.getChkFuncVersion().setSelection(true);
    this.funcSelcSec.getChkFuncVersion().setEnabled(false);
    this.funcSelcSec.getChkAlternative().setSelection(false);
    this.funcSelcSec.getChkAlternative().setEnabled(false);
    this.funcSelcSec.getChkVariant().setSelection(false);
    this.funcSelcSec.getChkVariant().setEnabled(false);
  }


  /**
   * Sets the selected parameter
   */
  @SuppressWarnings("unchecked")
  public void setSelectedParameter() {

    // TODO: Check why fcTableViewer is disposed on opening Rule editor
    // second time
    if ((this.fcTableViewer != null) && this.fcTableViewer.getControl().isDisposed()) {
      this.model = null;
      if (this.ruleFilterGridLayer != null) {
        this.ruleFilterGridLayer.getEventList().clear();
      }
      return;
    }
    if (this.fcTableViewer != null) {
      final IStructuredSelection selection =
          (IStructuredSelection) ParametersRulePage.this.fcTableViewer.getSelection();
      if (!selection.isEmpty()) {
        final Object element = selection.getFirstElement();
        if (element instanceof IParameter) {
          ParametersRulePage.this.selectedParam = (IParameter) element;
          this.paramMappingSection.setText("Parameter Rules : " + this.selectedParam.getName());
          ParametersRulePage.this.editor.getEditorInput().setCdrFuncParam(ParametersRulePage.this.selectedParam);
          addParamAttrList();

          this.model = new ParamRulesModel<>(this.selectedParam, this.parameterDataProvider);
          applyFilter();
          this.addRuleBtn.setEnabled(this.parameterDataProvider.hasDependency(this.selectedParam) &&
              this.paramColDataProvider.isRulesModifiable(this.cdrFunction) &&
              this.paramColDataProvider.isModifiable(this.cdrFunction)); // ICDM-1181
          enableDisableDefRuleBtn();

        }
        if (this.natTable != null) {
          this.natTable.doCommand(new StructuralRefreshCommand());
        }
      }
    }
  }

  /**
   *
   */
  private void addParamAttrList() {
    this.attrlist.clear();
    if (this.parameterDataProvider.getParamAttrs(this.selectedParam) != null) {
      this.attrlist.addAll(this.parameterDataProvider.getParamAttrs(this.selectedParam));
    }
  }

  /**
   *
   */
  private void applyFilter() {
    if (this.model.getRuleDefenitionSet() != null) {
      if (this.natTable != null) {
        reconstructNatTable();
      }
      else {
        createParamRules();
        // layout resizes the table. This is required to refresh a disposed widget
        this.paramMappingForm.layout();
      }
      this.inputMode.clear();
      this.inputMode.addAll(this.model.getRuleDefenitionSet());
      if (this.ruleFilterGridLayer != null) {
        this.ruleFilterGridLayer.getEventList().clear();
        this.ruleFilterGridLayer.getEventList().addAll(this.inputMode);
      }
    }
    else {
      if (this.ruleFilterGridLayer != null) {
        this.ruleFilterGridLayer.getEventList().clear();
      }
    }
  }

  /**
   * enable/Disable DefRuleBtn
   */
  private void enableDisableDefRuleBtn() {
    if (this.paramColDataProvider.isRulesModifiable(this.cdrFunction) &&
        this.paramColDataProvider.isModifiable(this.cdrFunction)) {
      if (this.parameterDataProvider.hasDependency(this.selectedParam)) {
        this.addDefRuleBtn.setToolTipText(
            "Add a rule, with no feature value dependency, attached to a parameter having dependent attributes");
        this.addDefRuleBtn.setText("Add Default Rule");
        this.addDefRuleBtn.setEnabled(null == this.parameterDataProvider.getDefaultRule(this.selectedParam));
      }
      else {
        this.addDefRuleBtn.setText("    Add Rule          ");
        this.addDefRuleBtn.setToolTipText("Add a rule to the parameter");
        this.addDefRuleBtn.setEnabled(null == this.parameterDataProvider.getReviewRule(this.selectedParam));
      }
    }
    else {
      this.addDefRuleBtn.setEnabled(false);
    }
  }

  /* *//**
        * Add sorter for the table columns
        */
  private void invokeColumnSorter() {
    this.fcTableViewer.setComparator(this.fcTabSorter);
  }

  /* *//**
        * Add filters for the table
        */
  private void addFilters() {
    this.paramFilter = new CDRParamFilter(this.parameterDataProvider); // Add TableViewer filter
    this.fcTableViewer.addFilter(this.paramFilter);

    this.toolBarFilters = new ReviewParamToolBarFilters(this.parameterDataProvider);
    this.fcTableViewer.addFilter(this.toolBarFilters);
    // icdm-2266
    this.outlineFilter = new RulesEditorOutlineFilter(null, this.editor.getEditorInput());
    this.fcTableViewer.addFilter(this.outlineFilter);
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener((final ModifyEvent event) -> {
      final String text = ParametersRulePage.this.filterTxt.getText().trim();
      ParametersRulePage.this.paramFilter.setFilterText(text);
      ParametersRulePage.this.fcTableViewer.refresh();
    });
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
   * Defines the columns of the TableViewer
   */
  private void createTabColumns() {

    // ICDM-2439
    // create ssd class column
    final GridViewerColumn ssdClassColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    ssdClassColumn.getColumn().setText("");
    ssdClassColumn.getColumn().setWidth(INDICATOR_COL_WIDTH);
    ssdClassColumn.getColumn().setResizeable(false);

    // create type column
    final GridViewerColumn typeColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    typeColumn.getColumn().setText("");
    typeColumn.getColumn().setWidth(TYPE_COL_WIDTH);
    typeColumn.getColumn().setResizeable(false);

    // create name column
    final GridViewerColumn paramNameColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    paramNameColumn.getColumn().setText("Parameter");
    paramNameColumn.getColumn().setWidth(NAME_COL_WIDTH);

    // Add column selection listener
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramNameColumn.getColumn(), 1, this.fcTabSorter, this.fcTableViewer));
  }

  /**
   * This method initializes CompositeTwo
   */
  private void createRightComposite() {

    final GridData gridData1 = GridDataUtil.getInstance().getGridData();
    ScrolledComposite scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    scrollComp.setLayout(new GridLayout());
    this.compositeTwo = new Composite(scrollComp, SWT.NONE);

    this.compositeTwo.setLayout(new GridLayout());
    // section to display the parameter rules
    createSection();
    this.compositeTwo.setLayoutData(gridData1);
    scrollComp.setContent(this.compositeTwo);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setDragDetect(true);

  }

  /**
   * This method initializes group
   */
  private void createSection() {
    this.paramMappingSection =
        this.formToolkit.createSection(this.compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.paramMappingSection.setLayoutData(gridData);
    this.paramMappingSection.setText(PARAMETER_RULES);
    this.paramMappingForm = this.formToolkit.createForm(this.paramMappingSection);
    Group paramMappingFormGroup = new Group(this.paramMappingForm.getBody(), SWT.NONE);

    GridLayout grpGridLayout = new GridLayout();
    grpGridLayout.numColumns = GRID_COLUMN_COUNT_TWO;
    paramMappingFormGroup.setLayout(grpGridLayout);

    addAddConfigButton(paramMappingFormGroup);
    addAddDefaultRuleButton(paramMappingFormGroup);


    GridLayout formGridLayout = new GridLayout();
    formGridLayout.numColumns = 1;
    this.paramMappingForm.getBody().setLayout(formGridLayout);
    this.paramMappingForm.setLayoutData(gridData);


    this.chooseColsLabel = LabelUtil.getInstance().createLabel(this.paramMappingForm.getBody(), "");
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    this.chooseColsLabel.setLayoutData(gridData1);

    this.paramMappingSection.setClient(this.paramMappingForm);
    this.paramMappingSection.getDescriptionControl().setEnabled(false);

  }

  /**
   * @return the fcTableViewer
   */
  public GridTableViewer getFcTableViewer() {
    return this.fcTableViewer;
  }

  /**
   * Creates the param rules section on the right side of bottom composite
   */
  @SuppressWarnings("unchecked")
  private void createParamRules() {
    //
    // The below map is used by NatTable to Map Columns with their
    // respective names
    this.propertyToLabelMap = new HashMap<>();

    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_0, "Lower Limit");
    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_1, "Upper Limit");
    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_2, "Bitwise Rule");
    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_3, "Reference Value");
    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_4, "Exact Match");
    this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_5, "Unit");
    String labelName;
    try {
      labelName = new CommonDataBO().getMessage("CDR_RULE", "READY_FOR_SERIES");


      this.propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_6, labelName);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    // The below map is used by NatTable to Map Columns with their
    // respective widths
    Map<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_0, LOWER_LIMIT_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_1, UPPER_LIMIT_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_2, UPPER_LIMIT_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_3, REF_VAL_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_4, EXACT_MATCH_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_5, UNIT_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_6, REVIEW_MET_COL_WIDTH);

    // maps data to the respective column values
    RuleNatInputToColumnConverter natInputToColumnConverter = new RuleNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    this.selectedParam = this.editor.getEditorInput().getCdrFuncParam();
    this.model = new ParamRulesModel<>(this.selectedParam, this.parameterDataProvider);
    if (null != this.selectedParam) {
      this.inputMode.addAll(this.model.getRuleDefenitionSet());
    }

    // to be used later
    IColumnAccessor<RuleDefinition> columnAccessor = new CustomRuleMappingPropertyAccessor<>(
        this.model.getRuleDependencyMap().size() + 7, natInputToColumnConverter);

    this.ruleFilterGridLayer = new CustomFilterGridLayer<RuleDefinition>(configRegistry, this.inputMode, columnAccessor,
        columnAccessor, getRuleComparator(), natInputToColumnConverter, this, new RuleViewerNatMouseClickAction(this),
        new GroupByModel(), new AttrValColumnHeaderDataProvider(this), true);
    DataLayer bodyDataLayer = this.ruleFilterGridLayer.getBodyDataLayer();
    IRowDataProvider<RuleDefinition> bodyDataProvider =
        (IRowDataProvider<RuleDefinition>) bodyDataLayer.getDataProvider();
    bodyDataLayer.setConfigLabelAccumulator(new RulesLabelAccumulator(bodyDataProvider));

    this.columnLabelAccumulator =
        new ParamRulesColHeaderLabelAccumulator(this.ruleFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.ruleFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);

    this.natTable = new NatTable(
        this.paramMappingForm.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE |
            SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.ruleFilterGridLayer, false);
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration());
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTableObj) {
        return super.createColumnHeaderMenu(natTableObj).withColumnChooserMenuItem();
      }
    });
    this.natTable.addConfiguration(new RulesValidationEditConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureRegistry(final IConfigRegistry configureRegistry) {
        super.configureRegistry(configureRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
            GUIHelper.getColor(COLOR_RED, COLOR_GREEN, COLOR_BLUE));
        configureRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    addRightClickMenu();
    this.natTable.configure();
    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.ruleFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 6 and row position 0
    selectionLayer.setSelectedCell(FREEZE_COLUMN_NUM, FREEZE_ROW_NUM);
    this.natTable.doCommand(new FreezeSelectionCommand());

    List paramAttrs = this.parameterDataProvider.getParamAttrs(this.selectedParam);
    this.combiMap.clear();
    if (paramAttrs != null) {
      for (Object parameterAttribute : paramAttrs) {
        IParameterAttribute paramAttr = (IParameterAttribute) parameterAttribute;
        this.combiMap.put(this.parameterDataProvider.getAttribute(paramAttr), ApicConstants.NOT_USED);
        this.combiMap.put(this.parameterDataProvider.getAttribute(paramAttr), ApicConstants.ANY);
      }
    }
    CustomDefaultBodyLayerStack bodyLayer = this.ruleFilterGridLayer.getBodyLayer();
    CustomDisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new CustomDisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.ruleFilterGridLayer.getColumnHeaderLayer(), this.ruleFilterGridLayer.getColumnHeaderDataLayer(),
            this.ruleFilterGridLayer.getColumnGroupModel(), this.combiMap, this);
    bodyLayer.registerCommandHandler(columnChooserCommandHandler);
    this.ruleFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    this.selectionProvider = new RowSelectionProvider<>(this.ruleFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.ruleFilterGridLayer.getBodyDataProvider(), false);

    addSelectionListener();

    attachToolTip();

  }

  /**
  *
  */
  private void addSelectionListener() {
    this.selectionProvider.addSelectionChangedListener(event -> {

      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof RuleDefinition) {
        setSelectionToPropertiesView(selection);
      }
    });
  }


  /**
   * Method to set selection to properties view
   *
   * @param selection from nattable
   */
  private void setSelectionToPropertiesView(final IStructuredSelection selection) {
    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
    if (viewPart != null) {
      PropertySheet propertySheet = (PropertySheet) viewPart;
      IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
      if (page != null) {
        page.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
            selection);
      }
    }
  }

  /**
   * Configures the columns to be used for Sorting In parameterRulesPage columns from 0 to 5 are only sorted. </br>
   * Other columns are marked as non sorted based on the label "VALUE_HEADER_LABEL" in the class
   * {@link RulesValidationEditConfiguration}
   *
   * @return
   */
  private IConfiguration getCustomComparatorConfiguration() {

    return new AbstractRegistryConfiguration() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_0,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_0);

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_1,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_1);

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_2,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_2);

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_3,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_3);

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_4,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_4);

        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_5,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_5);


        ParametersRulePage.this.columnLabelAccumulator.registerColumnOverrides(CommonUIConstants.COLUMN_INDEX_5,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_6);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_LOWER_LIMIT), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_7);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_UPPER_LIMIT), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_BITWISE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_2);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REF_CHECKSUM_VALUE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_3);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_EXACT_MATCH), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_4);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_UNIT), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_5);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REVIEW_METHOD), NORMAL,
            CUSTOM_COMPARATOR_LABEL + CommonUIConstants.COLUMN_INDEX_6);

        // Register null comparator to disable sort
      }
    };
  }

  /**
   * Returns the comparator
   *
   * @param sortColumn column
   * @return comparator
   */
  public static Comparator<?> getParamComparator(final SortColumns sortColumn) {

    return (ruleDef1, ruleDef2) -> ((RuleDefinition) ruleDef1).relativeCompareTo((RuleDefinition) ruleDef2, sortColumn);
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {


    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void menuAboutToShow(final IMenuManager mgr) {

        final ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet();
        List<Object> selectedObj = new ArrayList<>();
        final Map<RuleDefinition, List<ReviewRule>> defruleDefnRulesMap = fetchSelectedDefaultRule();

        // TODO: Enable multiple cell selections
        final Map<RuleDefinition, List<ReviewRule>> ruleDefnRulesMap = fetchSelectedRules();
        final List<ReviewRule> rvwRuleList = new ArrayList<>();

        for (List<ReviewRule> revwRuleList : ruleDefnRulesMap.values()) {
          rvwRuleList.addAll(revwRuleList);
        }

        if (CommonUtils.isNullOrEmpty(defruleDefnRulesMap)) {
          addContextMenuForSelRules(menuMgr, paramActionSet, selectedObj, rvwRuleList);
        }
        else {
          if (ParametersRulePage.this.otherRulesSelected) {
            // ICDM-2351
            multiSelectWithDefaultRule(menuMgr, selectedObj, defruleDefnRulesMap, rvwRuleList);
          }
          else {
            // for single rule selection
            rightClickMenuForDefaultRule(menuMgr, paramActionSet, defruleDefnRulesMap);
          }
        }
      }


      /**
       * @param menuMgr
       * @param paramActionSet
       * @param selectedObj
       * @param defruleDefnRulesMap
       * @param rules
       */
      private void multiSelectWithDefaultRule(final MenuManager menuMgr, final List<Object> selectedObj,
          final Map<RuleDefinition, List<ReviewRule>> defruleDefnRulesMap, final List<ReviewRule> rules) {

        ParamCollectionDataProvider parColDataProvider = ParametersRulePage.this.paramColDataProvider;
        ParameterDataProvider parDataProvider = ParametersRulePage.this.parameterDataProvider;
        // for multiple rule selection
        // Pass the selectedRules to the appropriate actions
        // Edit Rule
        // add only one if it is a default rule
        RuleDefinition ruleDef = defruleDefnRulesMap.keySet().iterator().next();
        rules.add(defruleDefnRulesMap.get(ruleDef).get(0));

        Action editRuleAction = new EditRuleWithDepAction<D, P>(parDataProvider, parColDataProvider, rules,
            ParametersRulePage.this.selectedParam, ParametersRulePage.this.cdrFunction, ParametersRulePage.this,
            !(ParametersRulePage.this.paramColDataProvider.isModifiable(ParametersRulePage.this.cdrFunction) &&
                ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)));


        menuMgr.add(editRuleAction);
        menuMgr.add(new Separator());

        // ICDM-1083

        if (ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)) {
          // ICDM-1079
          MaturityLevelActions maturiryLevelAction = new MaturityLevelActions<>(rules,
              ParametersRulePage.this.cdrFunction, ParametersRulePage.this, parColDataProvider);
          maturiryLevelAction.createSetMaturityMenu(menuMgr);
          // add seperator
          menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
          // Delete Rule
          DeleteRuleAction deleteRuleAction =
              new DeleteRuleAction(rules, true, ParametersRulePage.this.cdrFunction, ParametersRulePage.this,
                  selectedObj, false, ParametersRulePage.this.parameterDataProvider, parColDataProvider);
          menuMgr.add(deleteRuleAction);
        }
      }


    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }

  /**
   * @param menuMgr
   * @param paramActionSet
   * @param selectedObj
   * @param rvwRuleList
   */
  private void addContextMenuForSelRules(final MenuManager menuMgr, final ReviewRuleActionSet paramActionSet,
      final List<Object> selectedObj, final List<ReviewRule> rvwRuleList) {
    if (CommonUtils.isNotEmpty(rvwRuleList)) {
      // Pass the selectedRules to the appropriate actions
      ParamCollectionDataProvider parColDataProvider = ParametersRulePage.this.paramColDataProvider;
      ParameterDataProvider parDataProvider = ParametersRulePage.this.parameterDataProvider;
      CommonActionSet actionSet = new CommonActionSet();
      ReviewRule rvwRule = rvwRuleList.get(0);

      copyRuleLinkOfParameterAction(menuMgr, actionSet, rvwRule);
      menuMgr.add(new Separator());

      // Edit Rule Action
      Action editRuleAction = new EditMultiRuleAction(ParametersRulePage.this.parameterDataProvider,
          ParametersRulePage.this.paramColDataProvider, rvwRuleList, ParametersRulePage.this.selectedParam,
          ParametersRulePage.this.cdrFunction, ParametersRulePage.this,
          !(parColDataProvider.isModifiable(ParametersRulePage.this.cdrFunction) &&
              parColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)));

      menuMgr.add(editRuleAction);
      menuMgr.add(new Separator());

      // ICDM-1083
      SelectionLayer selectionLayer = ParametersRulePage.this.ruleFilterGridLayer.getBodyLayer().getSelectionLayer();
      Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();


      if ((selectedCells != null) && (selectedCells.size() == SIZE_ONE) &&
          parColDataProvider.isModifiable(ParametersRulePage.this.cdrFunction)) {
        addContextMenuForSingleRuleSel(menuMgr, parColDataProvider, parDataProvider, rvwRule);
      }

      // Maturity Action
      if (parColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)) {
        MaturityLevelActions maturiryLevelAction = new MaturityLevelActions<>(rvwRuleList,
            ParametersRulePage.this.cdrFunction, ParametersRulePage.this, parColDataProvider);
        maturiryLevelAction.createSetMaturityMenu(menuMgr);
      }

      // add seperator
      menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      if ((selectedCells != null) && (selectedCells.size() == 1)) {
        final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
        ReviewRule rule = rvwRule;
        selectedObj.add(rule);
        cdmActionSet.addShowSeriesStatisticsMenuAction(menuMgr, selectedObj, true /* To enable action */);

        paramActionSet.addShowRuleHistory(menuMgr, ParametersRulePage.this.cdrFunction, rule);
        if (ParametersRulePage.this.cdrFunction instanceof Function) {
          // Show Review Data
          cdmActionSet.addShowReviewDataMenuAction(menuMgr, rule, true, ParametersRulePage.this.parameterDataProvider);
        }
        // add seperator
        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        // add seperator
        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      }

      if (parColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)) {
        // Delete Rule
        DeleteRuleAction deleteRuleAction =
            new DeleteRuleAction(rvwRuleList, false, ParametersRulePage.this.cdrFunction, ParametersRulePage.this,
                selectedObj, false, ParametersRulePage.this.parameterDataProvider, parColDataProvider);
        menuMgr.add(deleteRuleAction);
      }

      // Context menu to Open Links in default browser created for ruleset rules and common Rules
      new CommonActionSet().openRuleLinkAction(menuMgr,
          rvwRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule());
    }
  }

  /**
   * @param menuMgr
   * @param actionSet
   * @param rvwRule
   */
  private void copyRuleLinkOfParameterAction(final MenuManager menuMgr, final CommonActionSet actionSet,
      final ReviewRule rvwRule) {
    if (ParametersRulePage.this.getEditor().getEditorInput().getCdrObject() instanceof RuleSet) {
      IParameter cdrFuncParam = (IParameter) ParametersRulePage.this.parameterDataProvider.getParamRulesOutput()
          .getParamMap().get(rvwRule.getParameterName());

      actionSet.copyRuleLinkOfParametertoClipBoard(menuMgr,
          (RuleSet) ParametersRulePage.this.getEditor().getEditorInput().getCdrObject(), rvwRule, cdrFuncParam);

      Map<String, String> additionalDetails = new HashMap<>();
      additionalDetails.put(ApicConstants.RULE_ID, rvwRule.getRuleId().toString());
      additionalDetails.put(ApicConstants.RULESET_PARAM_ID, cdrFuncParam.getId().toString());

      // Rule of a parameter Link in outlook Action
      final Action linkAction = new SendObjectLinkAction(CommonUIConstants.SEND_RULE_LINK_OF_PARAMETER,
          ParametersRulePage.this.getEditor().getEditorInput().getCdrObject(), additionalDetails);
      linkAction.setEnabled(true);
      menuMgr.add(linkAction);
    }
  }


  /**
   * @param menuMgr
   * @param parColDataProvider
   * @param parDataProvider
   * @param rvwRule
   */
  private void addContextMenuForSingleRuleSel(final MenuManager menuMgr,
      final ParamCollectionDataProvider parColDataProvider, final ParameterDataProvider parDataProvider,
      final ReviewRule rvwRule) {

    CopyRuleAction copyRule = new CopyRuleAction(rvwRule,
        parColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction), parDataProvider);
    menuMgr.add(copyRule);
    PasteRuleAction pasteRuleAction = new PasteRuleAction(rvwRule, ParametersRulePage.this.cdrFunction,
        ParametersRulePage.this.editor, parColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction),
        ParametersRulePage.this.selectedParam, parColDataProvider, parDataProvider);
    menuMgr.add(pasteRuleAction);

    menuMgr.add(new Separator());
  }

  /**
   * This method fetches the selecetd default rule
   *
   * @return
   */
  public Map<RuleDefinition, List<ReviewRule>> fetchSelectedDefaultRule() {
    SelectionLayer selectionLayer = ParametersRulePage.this.ruleFilterGridLayer.getBodyLayer().getSelectionLayer();
    Map<RuleDefinition, List<ReviewRule>> ruleDefnRulesMap = new HashMap<>();
    PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
    for (PositionCoordinate positionCoordinate : selectedCellPositions) {
      // Fetch selected Cell Row object
      RuleDefinition ruleDefinition = (RuleDefinition) ParametersRulePage.this.ruleFilterGridLayer.getBodyDataProvider()
          .getRowObject(positionCoordinate.rowPosition);
      ReviewRule cdrRule = ruleDefinition.isDefaultRule() ? ruleDefinition.getDefaultRule()
          : this.parameterDataProvider.getReviewRule(this.selectedParam);

      if (CommonUtils.isNotNull(cdrRule)) {
        List<ReviewRule> selectedCDRRules =
            ruleDefnRulesMap.computeIfAbsent(ruleDefinition, selectedRules -> new ArrayList<>());
        selectedCDRRules.add(cdrRule);
      }
      else {
        // TODO: selected cell doesnt contain CDRRule
      }

    }
    return ruleDefnRulesMap;
  }

  /**
   * This method fetched selected rule
   *
   * @return Map<RuleDefinition, List<CDRRule>>
   */
  public Map<RuleDefinition, List<ReviewRule>> fetchSelectedRules() {
    this.otherRulesSelected = false;
    SelectionLayer selectionLayer = ParametersRulePage.this.ruleFilterGridLayer.getBodyLayer().getSelectionLayer();
    Map<RuleDefinition, List<ReviewRule>> ruleDefnRulesMap = new ConcurrentHashMap<>();
    PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
    for (PositionCoordinate positionCoordinate : selectedCellPositions) {
      // Fetch selected Cell Row object
      RuleDefinition ruleDefinition = (RuleDefinition) ParametersRulePage.this.ruleFilterGridLayer.getBodyDataProvider()
          .getRowObject(positionCoordinate.rowPosition);
      List<ReviewRule> selectedCDRRules =
          ruleDefnRulesMap.computeIfAbsent(ruleDefinition, selectedRules -> new ArrayList<>());
      ReviewRule cdrRule =
          ruleDefinition.getRuleMapping().get(String.valueOf(ParametersRulePage.this.ruleFilterGridLayer.getBodyLayer()
              .getColumnHideShowLayer().getColumnIndexByPosition(positionCoordinate.columnPosition)));

      if (CommonUtils.isNotNull(cdrRule)) {
        this.otherRulesSelected = true;
        selectedCDRRules.add(cdrRule);
      }
      else {
        // TODO: selected cell doesnt contain CDRRule
      }
    }
    return ruleDefnRulesMap;
  }


  /**
   * @param ruleBtnGrp Composite
   */
  private void addAddConfigButton(final Composite ruleBtnGrp) {
    this.addRuleBtn = new Button(ruleBtnGrp, SWT.PUSH);
    this.addRuleBtn.setEnabled(false);
    this.addRuleBtn.setToolTipText("Add review rule to a new attribute value combination");
    this.addRuleBtn.setText("Add Dependency Rule");
    this.addRuleBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addRuleBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ParametersRulePage.this.selectedParam != null) {
          ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
          Set<RuleInfoSection> ruleInfoSet =
              ParametersRulePage.this.editor.getRuleInfoSectionMap().get(ParametersRulePage.this.selectedParam);
          if (!reviewParamActionSet.checkWizardDialogAlreadyOpen(ruleInfoSet, null)) {// ICDM-1244
            AddNewConfigWizard wizard = new AddNewConfigWizard(ParametersRulePage.this.selectedParam,
                ParametersRulePage.this.cdrFunction, ParametersRulePage.this, false, null, false, false, null, null,
                !ParametersRulePage.this.paramColDataProvider.isModifiable(ParametersRulePage.this.cdrFunction), false,
                ParametersRulePage.this.editor.getEditorInput().getParamDataProvider(),
                ParametersRulePage.this.paramColDataProvider);
            AddNewConfigWizardDialog dialog =
                new AddNewConfigWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
            dialog.create();
            dialog.open();

            reviewParamActionSet.addToWizardRuleInfoSectionMap(ParametersRulePage.this.selectedParam, dialog,
                ParametersRulePage.this.editor, ruleInfoSet);


          }
        }
      }
    });
    this.addRuleBtn.setLayoutData(new GridData());
  }

  /**
   * @param menuMgr
   * @param paramActionSet
   * @param defruleDefnRulesMap
   */
  private void rightClickMenuForDefaultRule(final MenuManager menuMgr, final ReviewRuleActionSet paramActionSet,
      final Map<RuleDefinition, List<ReviewRule>> defruleDefnRulesMap) {

    ParamCollectionDataProvider parColDataProvider = ParametersRulePage.this.paramColDataProvider;
    ParameterDataProvider parDataProvider = ParametersRulePage.this.parameterDataProvider;

    final List<ReviewRule> rules = new ArrayList<>();
    for (List<ReviewRule> rvwRuleList : defruleDefnRulesMap.values()) {
      rules.addAll(rvwRuleList);
    }

    ReviewRule defaultRule = rules.get(0);

    CommonActionSet actionSet = new CommonActionSet();
    //action for Copy RuleLink for RuleSet Parameter
    copyRuleLinkOfParameterAction(menuMgr, actionSet, defaultRule);

    menuMgr.add(new Separator());

    // Edit Rule Action
    paramActionSet.editRuleForParam(menuMgr, defaultRule, ParametersRulePage.this.cdrFunction,
        ParametersRulePage.this.editor, null,
        !(ParametersRulePage.this.paramColDataProvider.isModifiable(ParametersRulePage.this.cdrFunction) &&
            ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)),
        null, null, ParametersRulePage.this.paramColDataProvider, ParametersRulePage.this.parameterDataProvider);

    menuMgr.add(new Separator());


    // Copy Rule Action
    CopyRuleAction copyRule = new CopyRuleAction(rules.get(0),
        ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction),
        parDataProvider);
    menuMgr.add(copyRule);

    // Paste Rule Action
    PasteRuleAction pasteRuleAction =
        new PasteRuleAction(rules.get(0), ParametersRulePage.this.cdrFunction, ParametersRulePage.this.editor,
            ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction),
            ParametersRulePage.this.selectedParam, parColDataProvider, parDataProvider);
    menuMgr.add(pasteRuleAction);

    // ICDM-1308
    final CDMCommonActionSet cdmActionSet = new CDMCommonActionSet();
    List<Object> selectedObj = new ArrayList<>();
    selectedObj.add(defaultRule);
    if (ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)) {
      // map to new configuration
      // Action newConfigAction = paramActionSet.createNewConfigAction(rules, ParametersRulePage.this.selectedParam,

      menuMgr.add(new Separator());
      MaturityLevelActions maturiryLevelAction = new MaturityLevelActions<>(rules, ParametersRulePage.this.cdrFunction,
          ParametersRulePage.this, parColDataProvider);
      maturiryLevelAction.createSetMaturityMenu(menuMgr);
    }
    // add seperator
    menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    cdmActionSet.addShowSeriesStatisticsMenuAction(menuMgr, selectedObj, true /* To enable action */);


    // Show Rule History
    paramActionSet.addShowRuleHistory(menuMgr, ParametersRulePage.this.cdrFunction, defaultRule);
    if (ParametersRulePage.this.cdrFunction instanceof Function) {
      // Show Review Data
      cdmActionSet.addShowReviewDataMenuAction(menuMgr, defaultRule, true, parDataProvider);
    }
    // add seperator
    menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    // Open RuleLinks created for Ruleset Rules and comonRules
    actionSet.openRuleLinkAction(menuMgr, defaultRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule());

    if (ParametersRulePage.this.paramColDataProvider.isRulesModifiable(ParametersRulePage.this.cdrFunction)) {
      List<ReviewRule> ruleList = new ArrayList<>();
      ruleList.add(defaultRule);

      // Delete Rule
      DeleteRuleAction deleteRuleAction = new DeleteRuleAction(ruleList, true, ParametersRulePage.this.cdrFunction,
          ParametersRulePage.this, selectedObj, false, ParametersRulePage.this.parameterDataProvider,
          ParametersRulePage.this.paramColDataProvider);

      menuMgr.add(deleteRuleAction);
    }
  }

  /**
   * @param ruleBtnGrp Composite // ICDM-1181
   */
  public void addAddDefaultRuleButton(final Composite ruleBtnGrp) {
    this.addDefRuleBtn = new Button(ruleBtnGrp, SWT.PUSH);
    this.addDefRuleBtn.setEnabled(false);
    this.addDefRuleBtn.setToolTipText(
        "Add a review rule, with no feature value dependency, attached to a parameter having dependent attributes");

    this.addDefRuleBtn.setText("Add Default Rule");
    this.addDefRuleBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addDefRuleBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selEvent) {
        if (ParametersRulePage.this.selectedParam != null) {
          ReviewRuleActionSet actionSet = new ReviewRuleActionSet();
          if (!actionSet.checkEditDialogAlreadyOpen(
              ParametersRulePage.this.editor.getRuleInfoSectionMap().get(ParametersRulePage.this.selectedParam))) {
            EditRuleDialog dialog = new EditRuleDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                ParametersRulePage.this.selectedParam, ParametersRulePage.this.cdrFunction, ParametersRulePage.this);
            dialog.create();
            actionSet.addEditRuleInfoSectToMap(ParametersRulePage.this.editor, ParametersRulePage.this.selectedParam,
                ParametersRulePage.this.editor.getRuleInfoSectionMap().get(ParametersRulePage.this.selectedParam),
                dialog);
            dialog.open();
          }
        }
      }
    });
    this.addDefRuleBtn.setLayoutData(new GridData());
  }

  /**
   * Refresh nat table
   */
  public void reconstructNatTable() {
    if (this.natTable != null) {
      this.natTable.dispose();
      this.propertyToLabelMap.clear();
    }

    this.ruleFilterGridLayer = null;
    // create nat table
    createParamRules();
    // layout resizes the table. This is required to refresh a disposed widget
    this.paramMappingForm.layout();
  }

  /**
   * Enables tootltip only for all cells
   */
  private void attachToolTip() {
    DefaultToolTip toolTip;
    if (null == this.parameterDataProvider.getParamAttrs(this.selectedParam)) {
      toolTip = new ParamRulesPageNatToolTip(this.natTable, 0, this);
    }
    else {
      toolTip = new ParamRulesPageNatToolTip(this.natTable,
          this.parameterDataProvider.getParamAttrs(this.selectedParam).size(), this);// ICDM-1200
    }
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));

  }


  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }


  /**
   * @return the editor
   */
  @Override
  public ReviewParamEditor getEditor() {
    return this.editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      filterOutlineSelection(selection);
    }
  }

  /**
   * @param selection
   */
  private void filterOutlineSelection(final ISelection selection) {
    this.outlineFilter.a2lOutlineSelectionListener(selection);
    if (!this.fcTableViewer.getGrid().isDisposed()) {
      this.fcTableViewer.refresh();
      Collection<?> items = (Collection<?>) this.fcTableViewer.getInput();
      int totalItemCount = null != items ? items.size() : 0;
      int filteredItemCount = this.fcTableViewer.getGrid().getItemCount();
      this.editor.updateStatusBar(true, totalItemCount, filteredItemCount);
    }
  }

  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

    }
  }

  /**
   * @return the model ParamRulesModel
   */
  public ParamRulesModel<D, P> getModel() {
    return this.model;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    if (null == this.toolBarManager) {
      this.toolBarManager = new ToolBarManager(SWT.FLAT);
    }
    return this.toolBarManager;
  }


  /**
   * @return the ruleFilterGridLayer
   */
  @SuppressWarnings("rawtypes")
  public CustomFilterGridLayer getRuleFilterGridLayer() {
    return this.ruleFilterGridLayer;
  }


  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }


  /**
   * @return the selectedParam
   */
  public IParameter getSelectedParam() {
    return this.selectedParam;
  }

  /**
   * @deprecated Use {@link #getRuleComparator()} instead
   */
  @Deprecated
  private static Comparator<?> getRuleComparator(final int columnNum) {
    return getRuleComparator();
  }

  private static Comparator<?> getRuleComparator() {

    return (arg0, arg1) -> 0;
  }

  /**
   * @return the attrlist
   */
  public List<D> getAttrlist() {
    return this.attrlist;
  }


  /**
   * @return the propertyToLabelMap
   */
  public Map<Integer, String> getPropertyToLabelMap() {
    return this.propertyToLabelMap;
  }


  /**
   * @return the combiMap
   */
  public Map<Attribute, String> getCombiMap() {
    return this.combiMap;
  }


  /**
   * @return the chooseColsLabel
   */
  public Label getChooseColsLabel() {
    return this.chooseColsLabel;
  }


  /**
   * @return the paramColDataProvider
   */
  public ParamCollectionDataProvider getParamColDataProvider() {
    return this.paramColDataProvider;
  }


  /**
   * @return the parameterDataProvider
   */
  public ParameterDataProvider getParameterDataProvider() {
    return this.parameterDataProvider;
  }

  /**
   * @param paramMap
   */
  protected void setInputToTable(final Map<String, ?> paramMap) {
    if (this.fcTableViewer != null) {
      this.fcTableViewer.getGrid().removeAll();
      this.fcTableViewer.setInput(paramMap.values());
    }
  }


  /**
   * @param parameterDataProvider the parameterDataProvider to set
   */
  public void setParameterDataProvider(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

  /**
  *
  */
  public void refreshParamPage() {
    getEditor().getEditorInput().setCdrFuncParam(this.selectedParam);
    getEditor().refreshSelectedParamRuleData();
    if (null != getFcTableViewer()) {
      getFcTableViewer().setSelection(new StructuredSelection(this.selectedParam), true);
    }
    setSelectedParameter();
    getFcTableViewer().refresh();

  }


}