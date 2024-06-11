/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
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

import com.bosch.caltool.apic.ui.table.filters.AttributesFilters;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewRuleDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleEditInput;
import com.bosch.caltool.icdm.client.bo.cdr.RuleProviderResolver;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.PIDCVaraintSelDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.actions.ConfigParamToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.EditRuleAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ShowRuleAction;
import com.bosch.caltool.icdm.ruleseditor.dialogs.AddPidcAttrValDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.sorters.ConfigBasedCDRRuleSorter;
import com.bosch.caltool.icdm.ruleseditor.sorters.ConfigParamParamTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.CDRRuleFilter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ConfigCDRParamFilter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ConfigParamToolBarFilters;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewParamToolBarFilters;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RulesEditorOutlineFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamRulesLabelProvider;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamTableInputProvider;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * Third page of ReviewParam editor
 *
 * @author dmo5cob
 */
public class ConfigBasedRulesPage<D extends IParameterAttribute, P extends IParameter> extends AbstractFormPage
    implements ISelectionListener {

  /**
   * Q-SSD constant
   */
  private static final String QSSD_STR = "Qssd";

  /**
   * blacklist constant
   */
  private static final String BLACKLIST_STR = "Blacklist";

  /**
   * key - string combination for the multiple image, value - Image
   */
  private final Map<String, Image> multiImageMap = new ConcurrentHashMap<>();
  /**
   * compli constant
   */
  private static final String COMPLI_STR = "Compli";
  /**
   * column number for tooltip
   */
  private static final int COLUMN_FOR_TOOLTIP = 3;
  /**
   * Width /Height hint for the UI controls
   */
  private static final int WIDTH_HEIGHT_HINT = 25;
  /**
   *
   */
  private static final int SASHFORM_INDEX_4 = 4;
  /**
   *
   */
  private static final int SASHFORM_INDEX_3 = 3;
  /**
   * Editor instance
   */
  private final ReviewParamEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  private Composite composite;

  private Section sectionTwo;
  private Form formTwo;

  private final ParamCollection cdrFunction;

  private List<IParameter> selectedParamList = new ArrayList<>();

  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }

  /**
   * Defines selected function version
   */
  protected String selFuncVersion;
  // iCDM-845

  private Text filterTxtTwo;
  private Button searchButton;

  private CustomGridTableViewer attrsTableViewer;
  private Section sectionBottom;
  private CustomGridTableViewer rulesTableViewer;


  private CustomGridTableViewer paramTableViewer;

  /**
   * @return the paramTableViewer
   */
  public CustomGridTableViewer getParamTableViewer() {
    return this.paramTableViewer;
  }

  private Section sectionThree;
  private Form formThree;
  private Form formBottom;
  Map<Long, Set<IParameterAttribute>> attrParamMap = new HashMap<>();
  private Text filterTxtBtm;
  private Text filterTxtThree;
  private ConfigParamParamTabViewerSorter fcTabSorter;
  private ConfigBasedCDRRuleSorter ruleSorter;
  private SashForm rvwSashFormVertical;
  protected IParameterAttribute curParamAttr;
  protected Set<AttributeValueModel> attrValModelSet = new HashSet<>();
  private ConfigParamToolBarActionSet toolBarActionSet;
  private Set<IParameter> paramList = new HashSet<>();
  private ConfigParamToolBarFilters toolBarFilters;


  private static final int COL_WIDTH_1 = 100;

  private static final int COL_WIDTH_2 = 150;

  /**
   * Col width 3
   */
  private static final int COL_WIDTH_3 = 200;

  /**
   * Section three layout columns
   */
  private static final int SEC_THREE_LAYOT_COL = 3;

  /**
   * Form three Layout columns
   */
  private static final int FORM_THREE_LAYOT_COL = 3;

  /**
   * Form three Layout columns
   */
  private static final int FORM_TWO_LAYOT_COL = 3;


  /**
   * Check Box column index
   */
  public static final int CHECK_BOX_COL_IDX = 3;
  /**
   * Exact match col index
   */
  private static final int EXACT_MATCH_COL_IDX = 6;
  /**
   * Unit column index
   */
  private static final int UNIT_COL_IDX = 7;
  /**
   * ready for series column index
   */
  private static final int RVW_COL_IDX = 8;

  /**
   * Min Column Index
   */
  private static final int MIN_COL_IDX = 2;
  /**
   * Max Column Index
   */
  private static final int MAX_COL_IDX = 3;
  /**
   * Max Column Index
   */
  private static final int BITWISE_COL_IDX = 4;
  /**
   * Val Column Index
   */
  private static final int VAL_COL_IDX = 5;


  private static final String VERSION = "Version";
  private static final int INDICATOR_COL_WIDTH = 80;

  private FunctionVersionSection funcSelcSec;

  /**
   * RulesEditorOutlineFilter
   */
  private RulesEditorOutlineFilter outlineFilter;
  private ParameterDataProvider parameterDataProvider;
  private ParamCollectionDataProvider paramcolDataProvider;


  /**
   * @return the rulesTableViewer
   */
  public CustomGridTableViewer getRulesTableViewer() {
    return this.rulesTableViewer;
  }

  /**
   * @return the attrsTableViewer
   */
  public CustomGridTableViewer getAttrsTableViewer() {
    return this.attrsTableViewer;
  }

  /**
   * @return the searchButton
   */
  public Button getSearchButton() {
    return this.searchButton;
  }

  /**
   * Constructor
   *
   * @param editor -Editor instance for this page
   * @param cdrFunction cdrFunction
   */
  public ConfigBasedRulesPage(final FormEditor editor, final ParamCollection cdrFunction) {
    super(editor, "", "Configuration View");
    this.editor = (ReviewParamEditor) editor;
    this.cdrFunction = cdrFunction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(this.editor.getPartName());
    final ManagedForm mform = new ManagedForm(parent);
    this.nonScrollableForm.setText(
        CommonUtils.concatenate(this.editor.getPartName(), " - ", "Search Rules by Attribute-Value combinations"));
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    final FormToolkit formToolkit = managedForm.getToolkit();
    final ReviewParamEditor reviewParamEditor = getEditor();
    this.parameterDataProvider = reviewParamEditor.getEditorInput().getParamDataProvider();
    this.paramcolDataProvider = reviewParamEditor.getEditorInput().getDataProvider();

    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }

  /**
   * fset focus to property sheet
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    final GridLayout gridLayout = new GridLayout();

    if (this.paramcolDataProvider.hasVersions(this.cdrFunction)) {
      this.funcSelcSec =
          new FunctionVersionSection(ConfigBasedRulesPage.this, this.editor.getEditorInput(), this.composite);
      this.funcSelcSec.createFuncSelcSec(toolkit);
    }


    createSectionTwo(toolkit);
    createSectionBottom(toolkit, gridLayout);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
    final ReviewParamEditor reviewParamEditor = getEditor();
    createSectionTwoToolBarAction();
    createSectionBottomToolBarAction();


    final SelectionProviderMediator selProviderMed = reviewParamEditor.getSelectionProviderMediator();
    selProviderMed.addViewer(this.rulesTableViewer);
    getSite().setSelectionProvider(selProviderMed);

  }

  /**
   * @param attrComp
   */
  private void createButtons(final Composite attrComp) {
    GridData btnGridData = getBtnGridData();


    Button chkAllBtn = new Button(attrComp, SWT.PUSH);
    chkAllBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.TICK_16X16));
    chkAllBtn.setToolTipText("Check All");
    chkAllBtn.setLayoutData(btnGridData);
    chkAllBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        // Check all Params
        ConfigBasedRulesPage.this.toolBarActionSet.checkAllParams(ConfigBasedRulesPage.this.paramTableViewer,
            ConfigBasedRulesPage.this, ConfigBasedRulesPage.this.rulesTableViewer);
        ConfigBasedRulesPage.this.paramTableViewer.refresh();
      }
    });
    Button unchkAllBtn = new Button(attrComp, SWT.PUSH);
    unchkAllBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    unchkAllBtn.setToolTipText("UnCheck All");
    unchkAllBtn.setLayoutData(btnGridData);
    unchkAllBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        // unCheck all Params
        ConfigBasedRulesPage.this.toolBarActionSet.uncheckAllParams(ConfigBasedRulesPage.this.paramTableViewer,
            ConfigBasedRulesPage.this, ConfigBasedRulesPage.this.rulesTableViewer);
        ConfigBasedRulesPage.this.paramTableViewer.refresh();
      }
    });
  }

  /**
   * @return
   */
  private GridData getBtnGridData() {
    GridData btnGridData = new GridData(SWT.END);
    btnGridData.widthHint = WIDTH_HEIGHT_HINT;
    btnGridData.heightHint = WIDTH_HEIGHT_HINT;
    btnGridData.verticalAlignment = GridData.END;
    btnGridData.grabExcessVerticalSpace = true;
    return btnGridData;
  }


  /**
   * @param toolkit This method initializes sectionTwo
   */
  private void createSectionTwo(final FormToolkit toolkit) {

    GridLayout layout = new GridLayout();
    layout.numColumns = SEC_THREE_LAYOT_COL;
    layout.makeColumnsEqualWidth = false;

    final Form form = toolkit.createForm(this.composite);
    form.getBody().setLayout(layout);
    form.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.rvwSashFormVertical = new SashForm(form.getBody(), SWT.VERTICAL);
    this.rvwSashFormVertical.setLayout(new GridLayout());
    this.rvwSashFormVertical.setLayoutData(GridDataUtil.getInstance().getGridData());


    SashForm rvwSashFormHorizontal = new SashForm(this.rvwSashFormVertical, SWT.HORIZONTAL);
    rvwSashFormHorizontal.setLayout(new GridLayout());
    rvwSashFormHorizontal.setLayoutData(GridDataUtil.getInstance().getGridData());

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionTwo = toolkit.createSection(rvwSashFormHorizontal, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionTwo.setText("Parameters");
    this.sectionTwo.setExpanded(true);
    this.sectionTwo.getDescriptionControl().setEnabled(true);
    this.sectionTwo.setDescription("Select parameters to find the rules");
    createFormTwo(toolkit);
    this.sectionTwo.setLayoutData(gridData);
    this.sectionTwo.setClient(this.formTwo);

    this.sectionThree =
        toolkit.createSection(rvwSashFormHorizontal, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Dependent Attributes");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(true);
    this.sectionThree.setDescription(
        "Consolidated list of dependent attributes for selected parameters. Select value for each attribute.");

    createFormThree(toolkit);
    this.sectionThree.setLayoutData(gridData);
    this.sectionThree.setClient(this.formThree);

    rvwSashFormHorizontal.setWeights(new int[] { SASHFORM_INDEX_3, SASHFORM_INDEX_4 });


  }

  /**
   * @param toolkit
   */
  private void createFormThree(final FormToolkit toolkit) {

    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = false;
    gridDataFour.verticalAlignment = GridData.FILL;
    gridDataFour.grabExcessVerticalSpace = true;


    this.formThree = toolkit.createForm(this.sectionThree);
    this.filterTxtThree = toolkit.createText(this.formThree.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterTxtThree.setLayoutData(gridData);
    this.filterTxtThree.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    getNewLabel(this.formThree.getBody(), SWT.NONE);
    getNewLabel(this.formThree.getBody(), SWT.NONE);

    GridLayout layout = new GridLayout();
    layout.numColumns = FORM_THREE_LAYOT_COL;

    this.formThree.getBody().setLayout(layout);

    createAttrsTable(gridDataFour);
    Composite comp = new Composite(this.formThree.getBody(), SWT.NONE);
    comp.setLayout(new GridLayout());
    comp.setLayoutData(new GridData());
    createPidcSearch(comp);

    // Add empty space between the buttons
    LabelUtil.getInstance().createEmptyLabel(comp);
    LabelUtil.getInstance().createEmptyLabel(comp);
    LabelUtil.getInstance().createEmptyLabel(comp);
    LabelUtil.getInstance().createEmptyLabel(comp);

    createSearchButton(comp);


  }


  /**
   * @param comp
   */
  private void createPidcSearch(final Composite comp) {

    Button pidcButton = new Button(comp, SWT.PUSH);
    pidcButton.setText("Load Values");
    pidcButton.setToolTipText("Load attribute values from PIDC or Variant");
    pidcButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16));
    pidcButton.setEnabled(true);

    pidcButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((null != ConfigBasedRulesPage.this.attrParamMap) && !ConfigBasedRulesPage.this.attrParamMap.isEmpty()) {
          PIDCVaraintSelDialog paramAttrDepDialog =
              new AddPidcAttrValDialog(Display.getDefault().getActiveShell(), ConfigBasedRulesPage.this);
          paramAttrDepDialog.open();
        }
        else {
          CDMLogger.getInstance().warnDialog("Select at least one parameter to continue", Activator.PLUGIN_ID);
        }
      }
    });

  }

  /**
   *
   */
  private void addModifyTextListener(final Text filterTxt, final AbstractViewerFilter filter,
      final GridTableViewer tableViewer) {
    filterTxt.addModifyListener(event -> {
      String text = filterTxt.getText().trim();
      filter.setFilterText(text);
      tableViewer.refresh();
      if (filter instanceof ConfigCDRParamFilter) {
        setAttrTabViewerInput();
      }
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
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout
   */
  private void createSectionBottom(final FormToolkit toolkit, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionBottom =
        toolkit.createSection(this.rvwSashFormVertical, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionBottom.setText("Parameter Rules - Search Results");
    this.sectionBottom.setExpanded(true);
    this.sectionBottom.getDescriptionControl().setEnabled(true);
    this.sectionBottom.setDescription("Parameter rules satisfying above attribute-value dependencies");
    createFormBottom(toolkit, gridLayout);
    this.sectionBottom.setLayoutData(gridData);
    this.sectionBottom.setClient(this.formBottom);

    this.rvwSashFormVertical.setWeights(new int[] { SASHFORM_INDEX_4, SASHFORM_INDEX_3 });
  }

  /* *//**
        * Add filters for the table
        */
  private void addFilters() {
    this.toolBarFilters = new ConfigParamToolBarFilters();
    this.paramTableViewer.addFilter(this.toolBarFilters);

    // icdm-2266
    this.outlineFilter = new RulesEditorOutlineFilter(null, this.editor.getEditorInput());
    this.paramTableViewer.addFilter(this.outlineFilter);

  }

  /**
   * @param toolkit This method initializes form
   * @param gridLayout
   */
  private void createFormTwo(final FormToolkit toolkit) {

    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    gridDataFour.grabExcessVerticalSpace = true;

    this.formTwo = toolkit.createForm(this.sectionTwo);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    this.formTwo.getBody().setLayout(layout);
    this.formTwo.getBody().setLayoutData(gridDataFour);
    Composite subComp = new Composite(this.formTwo.getBody(), SWT.NONE);
    GridLayout btnLayout = new GridLayout();
    btnLayout.numColumns = FORM_TWO_LAYOT_COL;
    btnLayout.makeColumnsEqualWidth = false;
    subComp.setLayout(btnLayout);
    GridData treeData = new GridData(SWT.END);
    treeData.horizontalAlignment = SWT.RIGHT;
    treeData.grabExcessHorizontalSpace = true;
    subComp.setLayoutData(treeData);
    createButtons(subComp);
    this.filterTxtTwo = toolkit.createText(this.formTwo.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterTxtTwo.setLayoutData(gridData);
    this.filterTxtTwo.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.fcTabSorter = new ConfigParamParamTabViewerSorter(this.parameterDataProvider);

    createParamterTable(gridDataFour);
  }

  /**
   * @param comp2
   */
  private void createSearchButton(final Composite comp) {

    this.searchButton = new Button(comp, SWT.PUSH);
    this.searchButton.setText("Search");
    this.searchButton.setToolTipText("Search");
    this.searchButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.searchButton.setEnabled(false);
    this.searchButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
          dialog.run(true, true, monitor -> {
            monitor.beginTask("Searching. Please wait...", IProgressMonitor.UNKNOWN);
            doSearch();
            monitor.done();
            if (monitor.isCanceled()) {
              CDMLogger.getInstance().info("Search cancelled !", Activator.PLUGIN_ID);
            }
          });
        }
        catch (InvocationTargetException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        catch (InterruptedException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  /**
   *
   */
  private void doSearch() {
    try {
      Display.getDefault().asyncExec(() -> {
        List<String> labelNames = setLabelsForSearch();

        Set<AttributeValueModel> attrValueModSet = setAttrValModels();
        Map<String, List<ReviewRule>> rulesForParAttrVal;
        try {
          rulesForParAttrVal = getRulesForParAttrVal(labelNames, attrValueModSet);
        }
        catch (ApicWebServiceException exception) {
          CDMLogger.getInstance().errorDialog(exception.getMessage(), exception, Activator.PLUGIN_ID);
          ConfigBasedRulesPage.this.rulesTableViewer.setInput(null);
          return;
        }

        Set<ReviewRule> cdrRuleInp = new TreeSet<>();
        // Parameters, for which rule is available. The name is stored as all caps, to support case
        // insensitive
        Set<String> ruleAvailableParamSet = new HashSet<>();
        if ((rulesForParAttrVal != null) && !rulesForParAttrVal.isEmpty()) {
          for (Entry<String, List<ReviewRule>> entry : rulesForParAttrVal.entrySet()) {
            List<ReviewRule> ruleList = entry.getValue();
            cdrRuleInp.addAll(ruleList);

            ruleAvailableParamSet.add(entry.getKey().toUpperCase(Locale.getDefault()));
          }
        }
        createDummyRule(cdrRuleInp, ruleAvailableParamSet);

        if (CommonUtils.isNullOrEmpty(cdrRuleInp)) {
          CDMLogger.getInstance().warnDialog(
              "Rules not found for the selected parameter(s) with given search conditions", Activator.PLUGIN_ID);
        }
        ConfigBasedRulesPage.this.rulesTableViewer.setInput(cdrRuleInp);
        ConfigBasedRulesPage.this.rulesTableViewer.refresh();
        CDMLogger.getInstance().info("Rule fetch is completed", Activator.PLUGIN_ID);
      });
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param cdrRuleInp
   * @param ruleAvailableParamSet
   */
  private void createDummyRule(final Set<ReviewRule> cdrRuleInp, final Set<String> ruleAvailableParamSet) {
    for (IParameter param : ConfigBasedRulesPage.this.paramList) {
      if (!ruleAvailableParamSet.contains(param.getName().toUpperCase(Locale.getDefault()))) {
        ReviewRule dummyRule = new ReviewRule();
        dummyRule.setParameterName(param.getName());
        dummyRule.setValueType(param.getType());
        dummyRule.setReviewMethod("");
        dummyRule.setHint(RuleEditorConstants.DUMMY_RULE_HINT);
        cdrRuleInp.add(dummyRule);
      }
    }
  }

  /**
   * Create the PreDefined Filter for the List page- Icdm-502
   */
  private void createSectionTwoToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.sectionTwo);


    this.toolBarActionSet =
        new ConfigParamToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);

    final Separator separator = new Separator();

    toolBarManager.add(separator);


    // included params
    this.toolBarActionSet.includedParamFilterAction(toolBarManager, this.toolBarFilters, this.paramTableViewer);

    // not included params
    this.toolBarActionSet.notIncludedParamFilterAction(toolBarManager, this.toolBarFilters, this.paramTableViewer);

    toolBarManager.update(true);
    this.sectionTwo.setTextClient(toolbar);


  }


  /**
   * Create the PreDefined Filter for the List page- Icdm-502
   */
  private void createSectionBottomToolBarAction() {
    final ToolBarManager toolBarManager = (ToolBarManager) getToolBarManager();

    final ToolBar toolbar = toolBarManager.createControl(this.sectionBottom);

    final Separator separator = new Separator();

    toolBarManager.add(separator);

    // defaultRule
    this.toolBarActionSet.defaultRuleFilterAction(toolBarManager, this.toolBarFilters, this.rulesTableViewer);
    // not defaultRule
    this.toolBarActionSet.nonDefaultRuleFilterAction(toolBarManager, this.toolBarFilters, this.rulesTableViewer);
    // No Rule exists
    this.toolBarActionSet.ruleNotExistsFilterAction(toolBarManager, this.toolBarFilters, this.rulesTableViewer);
    toolBarManager.add(separator);

    final ReviewRuleToolBarActionSet rvwToolBarActionSet =
        new ReviewRuleToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);
    ReviewParamToolBarFilters rvwToolBarFilters = new ReviewParamToolBarFilters(this.parameterDataProvider);

    this.rulesTableViewer.addFilter(rvwToolBarFilters);
    // Filter For the Review Rule - Complete
    rvwToolBarActionSet.ruleCompleteFilterAction(toolBarManager, rvwToolBarFilters, null, this.rulesTableViewer);
    // Filter For the Review Rule - InComplete
    rvwToolBarActionSet.ruleInCompleteFilterAction(toolBarManager, rvwToolBarFilters, null, this.rulesTableViewer);

    addResetAllFiltersAction();
    toolBarManager.update(true);
    this.sectionBottom.setTextClient(toolbar);

  }

  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxtBtm);
    getRefreshComponentSet().add(this.rulesTableViewer);
    addResetFiltersAction();
  }

  /**
   * @param gridDataFour
   */
  private void createAttrsTable(final GridData gridDataFour) {
    this.attrsTableViewer = new CustomGridTableViewer(this.formThree.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    initializeEditorStatusLineManager(this.attrsTableViewer);


    this.attrsTableViewer.getGrid().setLayoutData(gridDataFour);

    this.attrsTableViewer.getGrid().setLinesVisible(true);
    this.attrsTableViewer.getGrid().setHeaderVisible(true);

    this.attrsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    createColumns();

    this.attrsTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // not applicable
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ConfigBasedRulesPage.this.attrsTableViewer);
      }
    });

    AttributesFilters attrFilter = new AttributesFilters(); // Add TableViewer filter
    this.attrsTableViewer.addFilter(attrFilter);
    addModifyTextListener(this.filterTxtThree, attrFilter, this.attrsTableViewer);


  }

  /**
   * @param gridDataFour
   */
  private void createParamterTable(final GridData gridDataFour) {
    this.paramTableViewer = new CustomGridTableViewer(this.formTwo.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    initializeEditorStatusLineManager(this.paramTableViewer);

    this.paramTableViewer.getGrid().setLayoutData(gridDataFour);

    this.paramTableViewer.getGrid().setLinesVisible(true);
    this.paramTableViewer.getGrid().setHeaderVisible(true);
    // ICDM-1348
    addKeyListener();

    createParamColumns();
    this.paramTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.paramTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        setSelectedParameter();
        final IStructuredSelection selection =
            (IStructuredSelection) ConfigBasedRulesPage.this.paramTableViewer.getSelection();
        setSelectionToPropertiesView(selection);
      }


    });
    this.paramTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // not applicable
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ConfigBasedRulesPage.this.paramTableViewer);
      }
    });

    // Add TableViewer filter
    ConfigCDRParamFilter<D, P> paramFilter = new ConfigCDRParamFilter(this.parameterDataProvider);
    this.paramTableViewer.addFilter(paramFilter);
    addModifyTextListener(this.filterTxtTwo, paramFilter, this.paramTableViewer);
    this.paramTableViewer.setComparator(this.fcTabSorter);

    addRightClickForParams();
    addFilters();
  }

  /**
   * ICDM-1348 adding key listener for ctrl+c
   */
  private void addKeyListener() {
    this.paramTableViewer.getControl().addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyevent) {
        // Not applicable
      }

      @Override
      public void keyPressed(final KeyEvent keyevent) {
        IParameter cdrParam = ConfigBasedRulesPage.this.editor.getEditorInput().getCdrFuncParam();
        // checking if the cdr result parameter is not null
        if (CommonUtils.isNotNull(cdrParam) && (keyevent.stateMask == CommonUIConstants.KEY_CTRL) &&
            (keyevent.keyCode == CommonUIConstants.KEY_COPY)) {
          CommonUiUtils.setTextContentsToClipboard(cdrParam.getName());
        }
      }

    });
  }

  /**
   * ICDM-1348
   */
  private void addRightClickForParams() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(imenumanager -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ConfigBasedRulesPage.this.paramTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (firstElement instanceof ConfigBasedParam) {
        final CommonActionSet cdrActionSet = new CommonActionSet();
        cdrActionSet.copyParamNameToClipboardAction(menuMgr,
            ((ConfigBasedParam) firstElement).getParameter().getName());
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.paramTableViewer.getGrid());
    this.paramTableViewer.getGrid().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.paramTableViewer);
  }

  /**
   * Icdm-1089 open the Rules page Right Click menu from rules table.
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ConfigBasedRulesPage.this.rulesTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (!selection.isEmpty()) && (firstElement instanceof ReviewRule)) {
        ReviewRule result = (ReviewRule) firstElement;
        new ShowRuleAction<D, P>(menuMgr, ConfigBasedRulesPage.this, result,
            ConfigBasedRulesPage.this.parameterDataProvider); // ICDM-1190

        ParamCollection paramColln = ConfigBasedRulesPage.this.cdrFunction;
        ParamCollectionDataProvider paramColDataProvider = ConfigBasedRulesPage.this.paramcolDataProvider;
        RuleEditInput input = new RuleEditInput<>(paramColln, firstElement, null, null, null,
            !(paramColDataProvider.isRulesModifiable(paramColln) && paramColDataProvider.isModifiable(paramColln)),
            ConfigBasedRulesPage.this.parameterDataProvider, paramColDataProvider, null);

        EditRuleAction<D, P> editRuleAction = new EditRuleAction<>(input, ConfigBasedRulesPage.this.editor);
        menuMgr.add(editRuleAction);
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.rulesTableViewer.getGrid());
    this.rulesTableViewer.getGrid().setMenu(menu); // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.rulesTableViewer);
  }

  /**
   * Sets the tableviewer input
   */
  void setParamTabViewerInput() {

    if (this.paramcolDataProvider != null) {
      if (this.paramcolDataProvider.hasVersions(this.cdrFunction)) {
        setTabInputForFunction();
      }
      else {
        new ParamTableContentSetter().setInputForRuleSet(false, this.paramTableViewer, this.editor, this.cdrFunction,
            this);
      }
      if ((this.paramTableViewer != null) && (this.paramTableViewer.getGrid().getItems().length > 0)) {
        this.paramTableViewer.getGrid().setSelection(0);
        setSelectedParameter();

        this.paramTableViewer.getGrid().setFocus();
      }
      setRowSelection();
    }
  }


  /**
   *
   */
  private void setTabInputForFunction() {


    final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
    ParamTableInputProvider inputProvider = new ParamTableInputProvider(this.cdrFunction, this.editor);

    if (this.funcSelcSec.getComboFuncVersion().getItem(index).equalsIgnoreCase(ApicConstants.OPTION_ALL)) {
      Map<String, ?> paramMap = null;
      this.editor.getEditorInput().setCdrFuncVersion(ApicConstants.OPTION_ALL);
      try {
        this.selFuncVersion = null;
        if (this.editor.getEditorInput().getParamMap() != null) {
          paramMap = this.editor.getEditorInput().getParamMap();
        }
        else {
          paramMap = inputProvider.getParamMap();
        }
        if (this.paramTableViewer != null) {
          this.paramTableViewer.getGrid().removeAll();
          Map<IParameter, ConfigBasedParam> setConfigParams = setConfigParams(paramMap, this.editor);
          this.paramTableViewer.setInput(setConfigParams.values());
        }
      }
      catch (Exception exp) {
        CDMLogger.getInstance().error("Error when fetching param rules: " + exp.getMessage(), exp, Activator.PLUGIN_ID);

      }


      // disable radio buttons
      this.funcSelcSec.getChkFuncVersion().setSelection(true);
      this.funcSelcSec.getChkFuncVersion().setEnabled(false);
      this.funcSelcSec.getChkAlternative().setSelection(false);
      this.funcSelcSec.getChkAlternative().setEnabled(false);
      this.funcSelcSec.getChkVariant().setSelection(false);
      this.funcSelcSec.getChkVariant().setEnabled(false);

    }
    else {
      this.selFuncVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);

      try {


        inputProvider.setVersionSel(this.selFuncVersion);
        Map<String, ?> paramMap = getParamMap(inputProvider);

        if (this.paramTableViewer != null) {
          this.paramTableViewer.getGrid().removeAll();

          this.paramTableViewer.setInput(setConfigParams(paramMap, this.editor).values());
        }

        this.editor.getEditorInput().setCdrFuncVersion(this.selFuncVersion);

        // enable radio buttons
        this.funcSelcSec.getChkFuncVersion().setEnabled(true);
        this.funcSelcSec.getChkAlternative().setEnabled(true);
        this.funcSelcSec.getChkVariant().setEnabled(true);


      }

      catch (Exception exp) {
        CDMLogger.getInstance().error("Error when fetching parameter and rules", exp);
      }
    }


  }

  /**
   * @param inputProvider
   * @return
   * @throws ApicWebServiceException
   */
  private Map<String, ?> getParamMap(final ParamTableInputProvider inputProvider) throws ApicWebServiceException {
    Map<String, ?> paramMap;
    if (this.funcSelcSec.getChkFuncVersion().getSelection()) {
      paramMap = inputProvider.getParamMap();
    }
    else if (this.funcSelcSec.getChkVariant().getSelection()) {
      inputProvider.setChkVar("true");
      paramMap = inputProvider.getParamMap();
    }
    else {
      inputProvider.setChkVar("false");
      // search for alternative
      paramMap = inputProvider.getParamMap();
    }
    return paramMap;
  }

  /**
   * @param allParamsMap
   * @param editor
   * @return
   */
  private Map<IParameter, ConfigBasedParam> setConfigParams(final Map<String, ?> allParamsMap,
      final ReviewParamEditor editor) {
    Map<IParameter, ConfigBasedParam> configParamMap = new ConcurrentHashMap<>();
    for (Object funcParam : allParamsMap.values()) {
      ConfigBasedParam configParam = new ConfigBasedParam((IParameter) funcParam);
      configParamMap.put((IParameter) funcParam, configParam);
    }
    editor.getEditorInput().setConfigParamMap(configParamMap);
    return configParamMap;
  }


  /**
   * Sets the tableviewer input
   */
  public void setAttrTabViewerInput() {

    if (this.paramTableViewer != null) {
      GridItem[] gridItems = ConfigBasedRulesPage.this.paramTableViewer.getGrid().getItems();
      List<IParameter> listParamsInTable = new ArrayList<>();
      for (GridItem gridItem : gridItems) {

        if ((gridItem.getData() instanceof ConfigBasedParam) && gridItem.getChecked(CHECK_BOX_COL_IDX)) {

          IParameter paramInTable = ((ConfigBasedParam) gridItem.getData()).getParameter();
          listParamsInTable.add(paramInTable);
        }
      }
      setAttrForSelParam(listParamsInTable);
    }
  }

  /**
   * @param listParamsInTable
   */
  private void setAttrForSelParam(final List<IParameter> listParamsInTable) {
    Set<IParameterAttribute> attrSet = new TreeSet<>();

    for (IParameter param : listParamsInTable) {
      List paramAttrs = this.parameterDataProvider.getParamAttrs(param);
      if (null != paramAttrs) {
        attrSet.addAll(paramAttrs);
        this.attrParamMap.put(param.getId(), attrSet);
      }
    }
    if (attrSet.isEmpty()) {
      this.attrParamMap.clear();
    }
    this.attrsTableViewer.getGrid().removeAll();
    this.rulesTableViewer.getGrid().removeAll();
    this.attrsTableViewer.setInput(attrSet);
    this.searchButton.setEnabled(false);
    if (!listParamsInTable.isEmpty() && attrSet.isEmpty()) {
      this.searchButton.setEnabled(true);
    }
  }

  /**
   * Sets the selected parameter
   */
  private void setSelectedParameter() {
    final IStructuredSelection selection = (IStructuredSelection) this.paramTableViewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof ConfigBasedParam) {
        final ConfigBasedParam selectedParam = (ConfigBasedParam) element;
        this.editor.getEditorInput().setCdrFuncParam(selectedParam.getParameter());
        this.editor.getEditorInput().setConfigParam(selectedParam);
      }
    }
  }

  /**
   * Icdm-1089 Sync of Both Pages set the Selected Param for rule
   */
  protected void setSelParamForRules() {
    final IStructuredSelection selection = (IStructuredSelection) this.rulesTableViewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof ReviewRule) {
        final ReviewRule selectedRule = (ReviewRule) element;
        IParamRuleResponse<D, P> paramRulesOutput = this.parameterDataProvider.getParamRulesOutput();
        IParameter cdrFuncParameter = paramRulesOutput.getParamMap().get(selectedRule.getParameterName());

        this.editor.getEditorInput().setCdrFuncParam(cdrFuncParameter);

      }
    }

  }

  /**
   *
   */
  private void setRowSelection() {
    boolean setSelection = false;
    // To synchronise the selected row in the tableviewer
    ConfigBasedParam configBasedParam = this.editor.getEditorInput().getConfigParam();
    if (configBasedParam != null) {
      this.paramTableViewer.setSelection(new StructuredSelection(configBasedParam), true);

      setSelection = true;
    }
    // setting the default selection to the first row of tableviewer
    if (!setSelection && (this.paramTableViewer.getGrid().getItems().length > 0)) {
      this.paramTableViewer.getGrid().setSelection(0);
      setSelectedParameter();
      this.paramTableViewer.getGrid().setFocus();
    }
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
   * @param toolkit This method initializes form
   * @param gridLayout
   */
  private void createFormBottom(final FormToolkit toolkit, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.formBottom = toolkit.createForm(this.sectionBottom);
    this.filterTxtBtm = toolkit.createText(this.formBottom.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.filterTxtBtm.setLayoutData(getFilterTxtGridData());
    this.filterTxtBtm.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.rulesTableViewer = new CustomGridTableViewer(this.formBottom.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, new int[] { COLUMN_FOR_TOOLTIP }/* ICDM-1200 */);
    initializeEditorStatusLineManager(this.rulesTableViewer);


    this.rulesTableViewer.getGrid().setLayoutData(gridData);
    this.rulesTableViewer.getGrid().setLinesVisible(true);
    this.rulesTableViewer.getGrid().setHeaderVisible(true);


    this.formBottom.getBody().setLayout(gridLayout);
    createParamRulesColumns();
    createOtherColumns();
    this.rulesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.rulesTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        setSelParamForRules();
      }


    });
    this.rulesTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // not applicable
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(ConfigBasedRulesPage.this.rulesTableViewer);
      }
    });

    CDRRuleFilter rulesFilter = new CDRRuleFilter(); // Add TableViewer filter
    this.rulesTableViewer.addFilter(rulesFilter);

    // icdm-2266
    this.rulesTableViewer.addFilter(this.outlineFilter);
    addModifyTextListener(this.filterTxtBtm, rulesFilter, this.rulesTableViewer);
    this.rulesTableViewer.setComparator(this.ruleSorter);
    addRightClickMenu();
    this.rulesTableViewer.addFilter(this.toolBarFilters);
  }


  /**
   * Icdm-1188 new Columns in the Screen create Exact match, unit and ready for series Column
   */
  private void createOtherColumns() {
    final GridViewerColumn exactMatch = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    exactMatch.getColumn().setWidth(COL_WIDTH_1);
    exactMatch.getColumn().setText("Exact Match");
    ParamRulesLabelProvider exactMatchColLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    exactMatch.setLabelProvider(exactMatchColLabelPrvdr);
    exactMatchColLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_5);

    // Add column selection listener
    exactMatch.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(exactMatch.getColumn(), EXACT_MATCH_COL_IDX, this.ruleSorter, this.rulesTableViewer));

    final GridViewerColumn unitCol = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    unitCol.getColumn().setText("Unit");
    unitCol.getColumn().setWidth(COL_WIDTH_1);
    ParamRulesLabelProvider unitColLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    unitCol.setLabelProvider(unitColLabelPrvdr);
    unitColLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_6);

    // Add column selection listener
    unitCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(unitCol.getColumn(),
        UNIT_COL_IDX, this.ruleSorter, this.rulesTableViewer));


    final GridViewerColumn revMethod = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);

    String labelName = "Ready for series";
    revMethod.getColumn().setText(labelName);
    revMethod.getColumn().setWidth(COL_WIDTH_2);
    ParamRulesLabelProvider revMethodColLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    revMethod.setLabelProvider(revMethodColLabelPrvdr);
    revMethodColLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_7);


    // Add column selection listener
    revMethod.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(revMethod.getColumn(), RVW_COL_IDX, this.ruleSorter, this.rulesTableViewer));

  }

  /**
   * create param Rules Column
   */
  private void createParamRulesColumns() {

    // ICDM-1200
    ColumnViewerToolTipSupport.enableFor(this.rulesTableViewer, ToolTip.NO_RECREATE);

    this.ruleSorter = new ConfigBasedCDRRuleSorter();
    final GridViewerColumn paramNameColumn = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    ParamRulesLabelProvider paramNameColLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    paramNameColumn.setLabelProvider(paramNameColLabelPrvdr);
    paramNameColLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_0);
    paramNameColumn.getColumn().setText("Parameter");
    paramNameColumn.getColumn().setWidth(COL_WIDTH_3);
    // Add column selection listener
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramNameColumn.getColumn(), 1, this.ruleSorter, this.rulesTableViewer));


    final GridViewerColumn minColumn = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    minColumn.getColumn().setText("Lower Limit");
    minColumn.getColumn().setWidth(COL_WIDTH_1);
    ParamRulesLabelProvider minColumnLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    minColumn.setLabelProvider(minColumnLabelPrvdr);
    minColumnLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_1);

    // // Add column selection listener
    minColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(minColumn.getColumn(), MIN_COL_IDX, this.ruleSorter, this.rulesTableViewer));

    final GridViewerColumn maxColumn = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    maxColumn.getColumn().setText("Upper Limit");
    maxColumn.getColumn().setWidth(COL_WIDTH_1);
    ParamRulesLabelProvider maxColumnLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    maxColumn.setLabelProvider(maxColumnLabelPrvdr);
    maxColumnLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_2);

    // // Add column selection listener
    maxColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(maxColumn.getColumn(), MAX_COL_IDX, this.ruleSorter, this.rulesTableViewer));

    final GridViewerColumn bitWiseColumn = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    bitWiseColumn.getColumn().setText("Bitwise Limit");
    bitWiseColumn.getColumn().setWidth(COL_WIDTH_2);
    ParamRulesLabelProvider bitWiseColumnLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    bitWiseColumn.setLabelProvider(bitWiseColumnLabelPrvdr);
    bitWiseColumnLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_3);

    // // Add column selection listener
    bitWiseColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(bitWiseColumn.getColumn(), BITWISE_COL_IDX, this.ruleSorter, this.rulesTableViewer));

    final GridViewerColumn valColumn = new GridViewerColumn(this.rulesTableViewer, SWT.NONE);
    valColumn.getColumn().setText("Reference Value");
    valColumn.getColumn().setWidth(COL_WIDTH_1);
    ParamRulesLabelProvider valColumnLabelPrvdr =
        new ParamRulesLabelProvider(getEditor().getEditorInput(), this.parameterDataProvider);
    valColumn.setLabelProvider(valColumnLabelPrvdr);
    valColumnLabelPrvdr.setColumnIndex(CommonUIConstants.COLUMN_INDEX_4);

    // Add column selection listener
    valColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valColumn.getColumn(), VAL_COL_IDX, this.ruleSorter, this.rulesTableViewer));

  }

  /*
   * Defines the columns of the TableViewer
   */
  private void createColumns() {


    final GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    attrNameColumn.getColumn().setText("Attribute");
    attrNameColumn.getColumn().setWidth(COL_WIDTH_3);

    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IParameterAttribute paramAttr = (IParameterAttribute) element;
        Attribute attribute = ConfigBasedRulesPage.this.parameterDataProvider.getAttribute(paramAttr);
        return attribute.getName();
      }
    });
    final GridViewerColumn valueColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(COL_WIDTH_3);


    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getText(final Object element) {
        return ApicConstants.DEFAULT_COMBO_SELECT;
      }
    });

    // Create an editor object to use for text editing
    final GridEditor gridEditor = new GridEditor(this.attrsTableViewer.getGrid());
    gridEditor.horizontalAlignment = SWT.LEFT;
    gridEditor.grabHorizontal = true;


    addSelectionListener();


    // mousedown listener
    this.attrsTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {

        disposeExistingEditor(gridEditor);

        // Locate the position of the mouse click
        Point point = new Point(event.x, event.y);

        // Determine which row was selected
        final GridItem item = ConfigBasedRulesPage.this.attrsTableViewer.getGrid().getItem(point);

        if (item != null) {
          // Identify which column was selected
          int column = getSelectedCol(point, item);

          if (column == CommonUIConstants.COLUMN_INDEX_1) {
            Object itemData = item.getData();
            if (itemData instanceof IParameterAttribute) {
              IParameterAttribute paraAttr = (IParameterAttribute) itemData;
              ConfigBasedRulesPage.this.curParamAttr = paraAttr;
              final CCombo comboObj = addDataToCombo(paraAttr);
              // Select the previously selected item
              comboObj.select(comboObj.indexOf(item.getText(column)));

              // Calculate the width for the editor and compute the column width to fit the dropdown .
              comboObj.setFocus();// Set the focus on the dropdown.
              gridEditor.setEditor(comboObj, item, column);

              // Add a listener to fix the selected item back to the cell
              final int colIndex = column;
              addSelectionListener(item, comboObj, colIndex);
            }
          }
        }

      }

      /**
       * @param paraAttr
       * @return
       */
      private CCombo addDataToCombo(final IParameterAttribute paraAttr) {
        // Create dropdown list and add data to it
        final CCombo comboObj = new CCombo(ConfigBasedRulesPage.this.attrsTableViewer.getGrid(), SWT.READ_ONLY);
        comboObj.add(ApicConstants.DEFAULT_COMBO_SELECT);
        SortedSet<AttributeValue> mappedAttrVal = new TreeSet<>();

        try {
          mappedAttrVal = ConfigBasedRulesPage.this.parameterDataProvider.getMappedAttrVal(paraAttr);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        for (AttributeValue attrVal : mappedAttrVal) {
          comboObj.add(attrVal.getName());
        }
        return comboObj;
      }


    });
  }

  /**
   *
   */
  private void addSelectionListener() {
    this.attrsTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        setSelectedParameter();
        final IStructuredSelection selection =
            (IStructuredSelection) ConfigBasedRulesPage.this.attrsTableViewer.getSelection();
        setSelectionToPropertiesView(selection);
      }


    });
  }


  /**
   * @param item
   * @param comboObj
   * @param colIndex
   */
  private void addSelectionListener(final GridItem item, final CCombo comboObj, final int colIndex) {
    comboObj.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {


        item.setText(colIndex, comboObj.getText());
        GridItem[] items = ConfigBasedRulesPage.this.attrsTableViewer.getGrid().getItems();
        ConfigBasedRulesPage.this.searchButton.setEnabled(true);
        for (GridItem gridItem : items) {
          if (ApicConstants.DEFAULT_COMBO_SELECT.equals(gridItem.getText(1)) || gridItem.getText(1).isEmpty()) {
            ConfigBasedRulesPage.this.searchButton.setEnabled(false);
          }
        }
        ConfigBasedRulesPage.this.rulesTableViewer.setInput(null);
        ConfigBasedRulesPage.this.rulesTableViewer.refresh();
        // They selected an item; end the editing session
        comboObj.dispose();


      }

    });
  }


  /**
   * @param gridEdtr
   */
  private void disposeExistingEditor(final GridEditor gridEdtr) {
    // Dispose any existing editor
    Control oldEditor = gridEdtr.getEditor();
    if (oldEditor != null) {
      oldEditor.dispose();
    }
  }

  /**
   * @param point
   * @param item
   * @return
   */
  private int getSelectedCol(final Point point, final GridItem item) {
    int column = -1;
    for (int index = 0, n = ConfigBasedRulesPage.this.attrsTableViewer.getGrid().getColumnCount(); index < n; index++) {
      Rectangle rectangle = item.getBounds(index);
      if (rectangle.contains(point)) {
        column = index;// the selected column
        break;
      }
    }
    return column;
  }

  /*
   * Defines the columns of the TableViewer
   */
  private void createParamColumns() {

    // ICDM-2439
    final GridViewerColumn ssdClassColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    ssdClassColumn.getColumn().setText("");
    ssdClassColumn.getColumn().setWidth(INDICATOR_COL_WIDTH);
    ssdClassColumn.getColumn().setResizeable(false);
    ssdClassColumn.setLabelProvider(new ColumnLabelProvider() {

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
        return getColumnImage(element, 0);
      }
    });

    final GridViewerColumn typeColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    typeColumn.getColumn().setText("");
    typeColumn.getColumn().setWidth(25);
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
        return getColumnImage(element, 1);
      }
    });

    final GridViewerColumn paramNameColumn = new GridViewerColumn(this.paramTableViewer, SWT.NONE);
    paramNameColumn.getColumn().setText("Parameter");
    paramNameColumn.getColumn().setWidth(180);
    paramNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof ConfigBasedParam) {
          final IParameter funcParam = ((ConfigBasedParam) element).getParameter();
          return funcParam.getName();
        }
        return "";
      }
    });
    // Add column selection listener
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramNameColumn.getColumn(), 1, this.fcTabSorter, this.paramTableViewer));


    final GridViewerColumn chkBoxColumn = new GridViewerColumn(this.paramTableViewer, SWT.CHECK | SWT.CENTER);
    chkBoxColumn.getColumn().setWidth(COL_WIDTH_2);
    chkBoxColumn.getColumn().setText("Include in search?");


    chkBoxColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();

        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (element instanceof ConfigBasedParam) {
          ConfigBasedParam configParam = (ConfigBasedParam) element;
          gridItem.setChecked(cell.getVisualIndex(), false);
          if (configParam.isChecked()) {
            gridItem.setChecked(cell.getVisualIndex(), true);
          }
        }
        gridItem.setCheckable(cell.getVisualIndex(), true);
      }
    });

    setCheckboxEditingSupport(chkBoxColumn);
    // // Add column selection listener

  }

  /**
   * @param chkBoxColumn
   */
  private void setCheckboxEditingSupport(final GridViewerColumn chkBoxColumn) {
    chkBoxColumn.setEditingSupport(new CheckEditingSupport(chkBoxColumn.getViewer()) {


      @Override
      public void setValue(final Object arg0, final Object arg1) {
        final boolean booleanValue = ((Boolean) arg1).booleanValue();
        if (arg0 instanceof ConfigBasedParam) {
          ConfigBasedParam configParam = (ConfigBasedParam) arg0;
          configParam.setChecked(booleanValue);
          if (booleanValue) {
            GridItem item = chkBoxColumn.getColumn().getParent().getItem(0);
            item.setChecked(booleanValue);
            item.setChecked(CHECK_BOX_COL_IDX, booleanValue);
            if (!ConfigBasedRulesPage.this.selectedParamList.contains(configParam.getParameter())) {
              ConfigBasedRulesPage.this.selectedParamList.add(configParam.getParameter());
            }
          }
          else {
            if (ConfigBasedRulesPage.this.selectedParamList.contains(configParam.getParameter())) {
              ConfigBasedRulesPage.this.selectedParamList.remove(configParam.getParameter());
            }
          }
        }
        setAttrForSelParam(ConfigBasedRulesPage.this.selectedParamList);
      }

    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    filterOutlineSelection(this.editor.getOutlinePageCreator().getOutlinePageSelection());
    if (this.paramcolDataProvider.hasVersions(this.cdrFunction)) {
      int oldFuncVersion = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
      if (this.editor.getEditorInput().getCdrFuncVersion() != null) {
        this.funcSelcSec.getComboFuncVersion()
            .select(this.funcSelcSec.getComboFuncVersion().indexOf(this.editor.getEditorInput().getCdrFuncVersion()));
      }
      boolean oldFunc = this.funcSelcSec.getChkFuncVersion().getSelection();
      boolean oldAlt = this.funcSelcSec.getChkAlternative().getSelection();
      boolean oldVar = this.funcSelcSec.getChkVariant().getSelection();

      if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          ((ReviewParamEditorInput) getEditorInput()).getSelComboTxt().equals(VERSION)) {
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
      if (checkOldData(oldFuncVersion, oldFunc, oldAlt, oldVar) || (this.rulesTableViewer.getInput() == null)) {
        setParamTabViewerInput();
        setAttrTabViewerInput();
      }
    }
    else if (this.rulesTableViewer.getInput() == null) {
      setParamTabViewerInput();
      setAttrTabViewerInput();
    }
    super.setActive(active);
    setStatusBarMessage(this.paramTableViewer);
  }

  /**
   * @param oldFuncVersion
   * @param oldFunc
   * @param oldAlt
   * @param oldVar
   * @return
   */
  private boolean checkOldData(final int oldFuncVersion, final boolean oldFunc, final boolean oldAlt,
      final boolean oldVar) {
    return (oldFuncVersion != this.funcSelcSec.getComboFuncVersion().getSelectionIndex()) ||
        (oldFunc != this.funcSelcSec.getChkFuncVersion().getSelection()) ||
        (oldAlt != this.funcSelcSec.getChkAlternative().getSelection()) ||
        (oldVar != this.funcSelcSec.getChkVariant().getSelection());
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
    if (!this.paramTableViewer.getGrid().isDisposed() && !this.rulesTableViewer.getGrid().isDisposed()) {
      this.paramTableViewer.refresh();
      this.rulesTableViewer.refresh();
      Collection<?> items = (Collection<?>) this.paramTableViewer.getInput();
      if (null != items) {
        int totalItemCount = items.size();
        int filteredItemCount = this.paramTableViewer.getGrid().getItemCount();
        this.editor.updateStatusBar(true, totalItemCount, filteredItemCount);
      }
    }
  }


  /**
   * @param element element
   * @param columnIndex columnIndex
   * @return the Column image for Type
   */
  public Image getColumnImage(final Object element, final int columnIndex) {

    Image img = null;
    if ((element instanceof ConfigBasedParam) && (columnIndex == 1)) {
      final IParameter funcParam = ((ConfigBasedParam) element).getParameter();
      if (funcParam.getType().equalsIgnoreCase(ParameterType.MAP.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.CURVE.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.VALUE.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.ASCII.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.VAL_BLK.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.AXIS_PTS.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
      }
    }

    // ICDM-2439
    if ((element instanceof ConfigBasedParam) && (columnIndex == 0)) {
      return getCompositeImage(element);
    }
    return img;
  }

  /**
   * @param element
   * @return
   */
  private Image getCompositeImage(final Object element) {
    final IParameter funcParam = ((ConfigBasedParam) element).getParameter();
    String keyString = "";
    if (this.parameterDataProvider.isComplianceParameter(funcParam)) {
      keyString = keyString.concat(COMPLI_STR);
    }
    if (funcParam.isBlackList()) {
      keyString = keyString.concat(BLACKLIST_STR);
    }
    if (funcParam.isQssdFlag()) {
      keyString = keyString.concat(QSSD_STR);
    }
    Image compositeImg = this.multiImageMap.get(keyString);
    if (null == compositeImg) {
      compositeImg = createImage(funcParam);
    }
    return compositeImg;
  }

  /**
   * @param funcParam
   * @return
   */
  private Image createImage(final IParameter funcParam) {
    Image compositeImg = new Image(Display.getCurrent(), 48, 16);
    String keyString = "";
    int iconWidth = 0;
    GC gc = new GC(compositeImg);
    if (this.parameterDataProvider.isComplianceParameter(funcParam)) {
      keyString += COMPLI_STR;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16), iconWidth, 0);
    }
    if (funcParam.isBlackList()) {
      keyString += BLACKLIST_STR;
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL), iconWidth, 0);
    }
    if (funcParam.isQssdFlag()) {
      keyString += QSSD_STR;
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL), iconWidth, 0);
    }
    this.multiImageMap.put(keyString, compositeImg);
    return compositeImg;
  }

  /**
   * Icdm-1073
   *
   * @return the label names selected from the Param Table for Search puropse
   */
  private List<String> setLabelsForSearch() {
    GridItem[] gridItems = ConfigBasedRulesPage.this.paramTableViewer.getGrid().getItems();
    List<String> labelNames = new ArrayList<>();
    this.paramList = new HashSet<>();
    for (GridItem gridItem : gridItems) {

      if ((gridItem.getData() instanceof ConfigBasedParam) && gridItem.getChecked(CHECK_BOX_COL_IDX)) {
        IParameter paramInTable = ((ConfigBasedParam) gridItem.getData()).getParameter();
        labelNames.add(paramInTable.getName());
        this.paramList.add(paramInTable);
      }
    }
    return labelNames;
  }

  /**
   * @return the Attr Val Model Set for Searching rules
   */
  private Set<AttributeValueModel> setAttrValModels() {
    GridItem[] attrGridItems = ConfigBasedRulesPage.this.attrsTableViewer.getGrid().getItems();
    Set<AttributeValueModel> attrValueModSet = new HashSet<>();
    for (GridItem gridItem : attrGridItems) {
      if (gridItem.getData() instanceof IParameterAttribute) {
        IParameterAttribute paramAttr = (IParameterAttribute) gridItem.getData();
        SortedSet<AttributeValue> attrValList = null;
        try {
          attrValList = this.parameterDataProvider.getMappedAttrVal(paramAttr);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          return Collections.emptySet();
        }
        Attribute attribute = this.parameterDataProvider.getAttribute(paramAttr);

        for (AttributeValue attrVal : attrValList) {
          if (attrVal.getName().equals(gridItem.getText(1))) {
            attrValueModSet.add(this.parameterDataProvider.createAttrValModel(attrVal, attribute));
          }
        }
        // If the attribute is not used ,then create a dummy object and set in the model
        setUsedNotUsedAttrValModel(attrValueModSet, gridItem, attribute);
      }
    }
    return attrValueModSet;
  }

  /**
   * @param attrValueModSet
   * @param gridItem
   * @param attribute
   */
  private void setUsedNotUsedAttrValModel(final Set<AttributeValueModel> attrValueModSet, final GridItem gridItem,
      final Attribute attribute) {
    AttributeValueModel attrValModel;

    if (gridItem.getText(1).equals(ApicConstants.NOT_USED)) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attribute);
      attrValModel.setValue(this.parameterDataProvider.createAttrValNotUsedObject(attribute));

      attrValueModSet.add(attrValModel);
    }
    else if (gridItem.getText(1).equals(ApicConstants.USED)) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attribute);
      attrValModel.setValue(this.parameterDataProvider.createAttrValUsedObject(attribute));

      attrValueModSet.add(attrValModel);
    }
  }

  /**
   * Send the label names and the attrValueModSet (Convert it to Feature Val Model and Invoke SSD)
   *
   * @param labelNames
   * @param attrValueModSet
   * @return
   * @throws ApicWebServiceException
   * @throws Exception
   */
  private Map<String, List<ReviewRule>> getRulesForParAttrVal(final List<String> labelNames,
      final Set<AttributeValueModel> attrValueModSet)
      throws ApicWebServiceException {
    ReviewRuleDataProvider<ParamCollection> dataProvider = new RuleProviderResolver().getRuleProvider(this.cdrFunction);
    return dataProvider
        .searchRuleForDep(new ConfigInputCreator().createconfigInput(labelNames, attrValueModSet, this.cdrFunction));
  }


  /**
   * This is an overriden method which gives the toolbar manager of the bottom section of the page {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.formBottom.getToolBarManager();
  }

  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }

  /**
   * @return the editor
   */
  @Override
  public ReviewParamEditor getEditor() {
    return this.editor;
  }

  /**
   * @param paramMap paramMap
   */
  protected void setInputToTable(final Map<String, ?> paramMap) {
    if (this.paramTableViewer != null) {
      this.paramTableViewer.getGrid().removeAll();
      this.paramTableViewer.setInput(paramMap.values());
    }
  }


  /**
   * @return the selectedParamList
   */
  public List<IParameter> getSelectedParamList() {
    return this.selectedParamList;
  }


  /**
   * @param selectedParamList the selectedParamList to set
   */
  public void setSelectedParamList(final List<IParameter> selectedParamList) {
    this.selectedParamList = selectedParamList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    super.dispose();
    for (Image image : this.multiImageMap.values()) {
      if (null != image) {
        image.dispose();
      }
    }
  }


}
