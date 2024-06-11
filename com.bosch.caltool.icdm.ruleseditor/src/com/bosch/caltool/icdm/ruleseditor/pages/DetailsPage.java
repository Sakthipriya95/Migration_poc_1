/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.actions.AddRuleSetParamAction;
import com.bosch.caltool.icdm.ruleseditor.actions.DeleteRuleSetParamAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ExportRulesetAsCDFxAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ImportCalDataAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.dialogs.AddParamAttrDepDialog;
import com.bosch.caltool.icdm.ruleseditor.dialogs.HintEditDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.listeners.DetailsPageMenuListener;
import com.bosch.caltool.icdm.ruleseditor.sorters.CDRFuncParamTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.sorters.ParamAttrTabViewerSorter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.CDRParamFilter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ParamAttrDepFilter;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewParamToolBarFilters;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RulesEditorOutlineFilter;
import com.bosch.caltool.icdm.ruleseditor.utils.Messages;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ListPageTableLabelProvider;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamTableInputProvider;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * Second Page of ReviewParam editor
 */
public class DetailsPage<D extends IParameterAttribute, P extends IParameter> extends AbstractFormPage
    implements ISelectionListener {

  /**
   *
   */
  private static final String WARNING_MSG_FOR_DEP_ATTR =
      " Warning: If you add new attribute dependencies and rules are already existing, you have to assign a value for the new attribute to each rule. To do this, open each rule once and select a value.";
  /**
   * Alternative option
   */
  private static final String ALTERNATIVE = "Alternative";
  /**
   * Variant option
   */
  private static final String VARIANT = "Variant";
  /**
   * Class combo height
   */
  private static final int CLASS_COMBO_HEIGHT = 100;
  /**
   * Class combo weight
   */
  private static final int CLASS_COMBO_WIDTH = 50;
  /**
   * Hint ui height
   */
  private static final int HINT_HEIGHT = 100;
  /**
   * Hint ui width
   */
  private static final int HINT_WIDTH = 300;
  /**
   * Number of filler labels for hint UI
   */
  private static final int HINT_UI_FILLERS = 5;
  /**
   * Param properties section cols
   */
  private static final int PARAM_PROP_COLS = 5;
  /**
   * Param name col width
   */
  private static final int NAME_COL_WIDTH = 300;
  /**
   * Param type col width
   */
  private static final int TYPE_COL_WIDTH = 25;
  /**
   * Composite weight
   */
  private static final int BTM_COMP_WEIGHT2 = 4;
  /**
   * Composite weight
   */
  private static final int BTM_COMP_WEIGHT1 = 1;
  /**
   * Function version
   */
  private static final String VERSION = "Version";
  /**
   * Main page columns
   */
  private static final int SASHFORM_COLS = 2;

  /**
   * Param Attr Col0
   */
  private static final int PARAM_ATTR_TAB_COL0 = 0;

  /**
   * Param Attr Col1
   */
  private static final int PARAM_ATTR_TAB_COL1 = 1;

  /**
   * Param Attr Col2
   */
  private static final int PARAM_ATTR_TAB_COL2 = 2;

  /**
   * Attr Col Width
   */
  private static final int ATTR_COL_WIDTH = 150;

  /**
   * Desc Col Width
   */
  private static final int DESC_COL_WIDTH = 300;

  /**
   * Unit Col Width
   */
  private static final int UNIT_COL_WIDTH = 100;

  /**
   * Attr Dep table Grid Layout Length
   */
  private static final int ATTR_DEP_COL_LEN = 2;
  /**
   * Editor instance
   */
  private final ReviewParamEditor editor;
  FunctionVersionSection funcSelcSec;


  private FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""
  private Form nonScrollableForm;
  private SashForm mainComposite;
  private Composite compositeOne;
  private Composite compositeTwo;

  private boolean unSavedDataPresent;


  private Section sectionLeft;


  private Form leftForm;


  private Combo comboClass;

  private Text filterTxt;
  private GridTableViewer fcTableViewer;

  private Button codeWordChkBox;
  private Button blackListChkBox;
  private final ParamCollection cdrFunction;

  private CDRParamFilter paramFilter;
  private CDRFuncParamTabViewerSorter fcTabSorter;
  private StyledText nameTxt;
  private StyledText longNameTxt;
  private Composite topComposite;

  // Icdm-502 Tool bar Action Set and Filter
  private ReviewParamToolBarFilters toolBarFilters;
  private ReviewRuleToolBarActionSet toolBarActionSet;
  private IParameter selectedParam;
  private Section sectionParamProp;
  private ReviewRule selectedCdrRule;

  private CalData refValCalDataObj;


  /** Map which holds the listener(SWT.Activate) instance for the Text */
  private final Map<org.eclipse.swt.widgets.Scrollable, Map<Integer, Listener>> textListenerMap = new HashMap<>();
  private ScrolledComposite scrollComp;
  // IcDm-660

  private int selectionIndex;
  // iCDM-845
  private StyledText hintTxtArea;


  private CustomGridTableViewer paramAttrTab;

  private Text attrFilText;
  private Button edit;
  private List<D> selParamAttrs;
  private Button delDep;
  private ParamAttrDepFilter attrFilter;
  private ParamAttrTabViewerSorter paramAttrDepTabSort;
  private Button addDep;
  /**
   * RulesEditorOutlineFilter
   */
  private RulesEditorOutlineFilter outlineFilter;
  private ParamCollectionDataProvider paramColDataProvider;
  private ParameterDataProvider<D, P> parameterDataProvider;
  private List<IParameter> selectedParams;
  private static final int TEXT_FIELD_WIDTHHINT_2 = 300;
  private static final int INDICATOR_COL_WIDTH = 80;
  private AddRuleSetParamAction addRuleSetParamAction;
  private DeleteRuleSetParamAction deleteRuleSetParamAction;
  /**
   * Action Button to export RuleSetAsCDFX file
   */
  private ExportRulesetAsCDFxAction expRulesetAsCDFxAction;

  /**
   * Constructor
   *
   * @param editor -Editor instance for this page
   * @param pageId -ID for the page
   * @param title -title for the page
   * @param cdrFunction cdrFunction
   */
  public DetailsPage(final FormEditor editor, final String pageId, final String title,
      final ParamCollection cdrFunction) {
    super(editor, pageId, title);
    this.editor = (ReviewParamEditor) editor;
    this.cdrFunction = cdrFunction;
  }

  /**
   * Icdm-1057 return the editor
   *
   * @return the editor
   */
  @Override
  public ReviewParamEditor getEditor() {
    return this.editor;
  }

  /**
   * @return the hintTxtArea
   */
  public StyledText getHintTxtArea() {
    return this.hintTxtArea;
  }

  /**
   * @return the paramAttrTab
   */
  public CustomGridTableViewer getParamAttrTab() {
    return this.paramAttrTab;
  }


  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(CommonUtils.concatenate(this.editor.getPartName(), " - ", "Parameter Details"));
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SASHFORM_COLS;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;


    this.topComposite = new Composite(this.nonScrollableForm.getBody(), SWT.NONE);
    this.topComposite.setLayout(gridLayout);
    this.topComposite.setLayoutData(gridData1);


    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
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

    this.paramColDataProvider = this.editor.getEditorInput().getDataProvider();
    this.formToolkit = managedForm.getToolkit();
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      this.funcSelcSec = new FunctionVersionSection(DetailsPage.this, this.editor.getEditorInput(), this.topComposite);
      this.funcSelcSec.createFuncSelcSec(this.formToolkit);
    }

    createBottomComposites();
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    boolean setSelection = false;

    boolean versionChanged = false;
    if ((this.paramColDataProvider != null) && this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      if (this.editor.getEditorInput().getCdrFuncVersion() != null) {

        final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();

        String selVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);
        if (!selVersion.equals(this.editor.getEditorInput().getCdrFuncVersion())) {
          versionChanged = true;
        }
        this.funcSelcSec.getComboFuncVersion()
            .select(this.funcSelcSec.getComboFuncVersion().indexOf(this.editor.getEditorInput().getCdrFuncVersion()));
      }

      if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          VERSION.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(true);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }
      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          ALTERNATIVE.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(true);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }

      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          VARIANT.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(true);
      }

    }
    setTabViewerInput(versionChanged);
    if (this.outlineFilter != null) {
      filterOutlineSelection(this.editor.getOutlinePageCreator().getOutlinePageSelection());
    }

    setParamSelectionFromEditorInput(setSelection);
    super.setActive(active);
  }

  /**
   * @param setSelection boolean
   */
  private void setParamSelectionFromEditorInput(final boolean setSelection) {
    boolean paramSelected = setSelection;
    IParameter cdrFuncParam = this.editor.getEditorInput().getCdrFuncParam();
    // To synchronise the selected row in the tableviewer
    if ((cdrFuncParam != null) && (this.fcTableViewer != null)) {
      this.selectedParam = cdrFuncParam;
      this.selectedParams = new ArrayList<>();
      this.selectedParams.add(cdrFuncParam);
      this.fcTableViewer.setSelection(new StructuredSelection(cdrFuncParam), true);
      setDetails(this.selectedParam);
      paramSelected = true;
    } // If no parameter is selected then clear the fields
    if (!paramSelected) {
      this.selectedParam = null;
      setDetails(this.selectedParam);
    }
  }

  /**
   * This method initializes composite
   */
  private void createBottomComposites() {

    createLeftComposite();

    createRightComposite();

    this.mainComposite.setWeights(new int[] { BTM_COMP_WEIGHT1, BTM_COMP_WEIGHT2 });
  }


  /**
   * This method initializes compositeOne
   */
  private void createLeftComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.compositeOne = new Composite(this.mainComposite, SWT.NONE);

    final GridLayout gridLayout = new GridLayout();

    createLeftSection(this.formToolkit, gridLayout);

    this.compositeOne.setLayout(gridLayout);
    this.compositeOne.setLayoutData(gridData);

  }

  /**
   * This method initializes CompositeTwo
   */
  private void createRightComposite() {

    final GridData gridData1 = GridDataUtil.getInstance().getGridData();
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    this.compositeTwo = new Composite(this.scrollComp, SWT.NONE);

    this.compositeTwo.setLayout(new GridLayout());
    createSections();
    createToolBarAction();
    this.compositeTwo.setLayoutData(gridData1);
    this.scrollComp.setContent(this.compositeTwo);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    this.scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        // ICDM-895
        Rectangle rect = DetailsPage.this.scrollComp.getClientArea();
        DetailsPage.this.scrollComp.setMinSize(DetailsPage.this.compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  private void addListener(final StyledText nameTxt2) {


    Map<Integer, Listener> listenerMap = this.textListenerMap.get(nameTxt2);
    if (listenerMap == null) {
      listenerMap = new HashMap<>();
      this.textListenerMap.put(nameTxt2, listenerMap);
    }
    final Listener[] deActivateListeners = nameTxt2.getListeners(SWT.Deactivate);

    if (deActivateListeners.length == 0) {
      nameTxt2.addListener(SWT.Deactivate, new Listener() {

        @Override
        public void handleEvent(final Event event) {
          nameTxt2.setSelection(0);
          nameTxt2.showSelection();
        }
      });
    }
    final SelectionProviderMediator selectionProviderMediator = this.editor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.paramAttrTab);
    selectionProviderMediator.addViewer(this.fcTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);

  }

  private void removeListenerFromTextField(final StyledText refValueTxt2) {
    final Map<Integer, Listener> listenerMap = this.textListenerMap.get(refValueTxt2);
    if (listenerMap != null) {
      for (Entry<Integer, Listener> listenerMapEntry : listenerMap.entrySet()) {
        refValueTxt2.removeListener(listenerMapEntry.getKey(), listenerMapEntry.getValue());
      }
    }
  }


  /**
   * Create the PreDefined Filter for the List page- Icdm-502
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = (ToolBarManager) getToolBarManager();
    ToolBar toolbar;
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      toolbar = toolBarManager.createControl(this.funcSelcSec.getSectionOne());
    }
    else {
      toolbar = toolBarManager.createControl(this.sectionParamProp);
      toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
    }


    this.toolBarActionSet =
        new ReviewRuleToolBarActionSet(getEditorSite().getActionBars().getStatusLineManager(), this);

    // ICDM-2439
    // Filter for compliance parameters
    this.toolBarActionSet.complianceFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for compliance parameters
    this.toolBarActionSet.nonComplianceFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    toolBarManager.add(new Separator());
    // Filter for black list parameters
    this.toolBarActionSet.blackListFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for non black list parameters
    this.toolBarActionSet.nonBlackListFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    toolBarManager.add(new Separator());
    // Filter for QSSD parameters
    this.toolBarActionSet.qSSDFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for non QSSD parameters
    this.toolBarActionSet.nonQSSDFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    toolBarManager.add(new Separator());
    // Filter For the Axis point - Value Type
    this.toolBarActionSet.axisPointFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for the Ascii - Value Type
    this.toolBarActionSet.asciiFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the Value Block - Value Type
    this.toolBarActionSet.valueBlockFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for the Value- Value Type
    this.toolBarActionSet.valueFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the Curve - Value Type
    this.toolBarActionSet.curveFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter for the Map Value Type
    this.toolBarActionSet.mapFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);

    toolBarManager.add(new Separator());

    // Filter For the Rivet - Class Type
    this.toolBarActionSet.rivetFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the Nail - Class Type
    this.toolBarActionSet.nailFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the Screw - Class Type
    this.toolBarActionSet.screwFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the Undefined - Class Type
    this.toolBarActionSet.undefinedFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);

    toolBarManager.add(new Separator());

    // Filter For the Yes - Code Type
    this.toolBarActionSet.codeYesFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    // Filter For the No - Code Type
    this.toolBarActionSet.codeNoFilterAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);

    toolBarManager.add(new Separator());

    // Filter For paramters with attribute dependency
    this.toolBarActionSet.paramWithDepnAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);
    this.toolBarActionSet.paramWithNoDepnAction(toolBarManager, this.toolBarFilters, null, this.fcTableViewer);

    addResetAllFiltersAction();
    toolBarManager.update(true);

    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      this.funcSelcSec.getSectionOne().setTextClient(toolbar);
    }
    else {
      this.sectionParamProp.setTextClient(toolbar);
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
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout
   */
  private void createLeftSection(final FormToolkit toolkit, final GridLayout gridLayout) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionLeft = toolkit.createSection(this.compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionLeft.setText("Parameters");
    this.sectionLeft.setExpanded(true);
    this.sectionLeft.getDescriptionControl().setEnabled(false);
    createLeftForm(toolkit, gridLayout);

    // ICDM-1380
    createParamToolbarAction();

    this.sectionLeft.setLayoutData(gridData);
    this.sectionLeft.setClient(this.leftForm);
  }


  /**
   * create toolbar action for adding
   */
  private void createParamToolbarAction() {

    boolean paramMappingModifable = !this.paramColDataProvider.isParamMappingModifiable(this.cdrFunction);
    boolean canImportRule = this.paramColDataProvider.isRuleImportAllowed();

    if (paramMappingModifable || canImportRule) {
      ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
      final ToolBar toolbar = toolBarManager.createControl(this.sectionLeft);

      // Add Add, Remove parameter buttons
      if (paramMappingModifable) {

        this.expRulesetAsCDFxAction =
            new ExportRulesetAsCDFxAction(this.editor.getDetailsPage(), this.cdrFunction, this.paramColDataProvider);
        toolBarManager.add(this.expRulesetAsCDFxAction);

        this.addRuleSetParamAction =
            new AddRuleSetParamAction(this.cdrFunction, this.editor.getDetailsPage(), this.paramColDataProvider);
        toolBarManager.add(this.addRuleSetParamAction);

        this.deleteRuleSetParamAction = new DeleteRuleSetParamAction(this.cdrFunction, this.editor,
            this.paramColDataProvider, this.editor.getDetailsPage());
        toolBarManager.add(this.deleteRuleSetParamAction);
      }
      // Add import Rule action
      if (canImportRule) {
        // Add separator only if both flags are true
        if (paramMappingModifable) {
          toolBarManager.add(new Separator());
        }
        ImportCalDataAction cmnActionSet =
            new ImportCalDataAction(this.cdrFunction, this.paramColDataProvider, this.editor.getEditorInput());
        toolBarManager.add(cmnActionSet);
      }
      toolBarManager.update(true);
      this.sectionLeft.setTextClient(toolbar);
    }
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

    this.fcTabSorter = new CDRFuncParamTabViewerSorter(this.parameterDataProvider);

    this.fcTableViewer = new CustomGridTableViewer(this.leftForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    initializeEditorStatusLineManager(this.fcTableViewer);

    this.fcTableViewer.getGrid().setLayoutData(gridData);

    this.fcTableViewer.getGrid().setLinesVisible(true);
    this.fcTableViewer.getGrid().setHeaderVisible(true);

    this.leftForm.getBody().setLayout(gridLayout);
    createTabColumns();
    this.fcTableViewer.setContentProvider(ArrayContentProvider.getInstance());


    this.fcTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Set the selected parameter for the first time
        if (DetailsPage.this.selectedParam == null) {
          setSelectedParameter();
        }
        // Check if unsaved data is present before switching the parameter
        if (isUnSavedDataPresent()) {
          // Save changes for previous param
          if (saveChanges()) {
            clearGraphSetDetails();
          }
          else {
            DetailsPage.this.fcTableViewer.getGrid().setSelection(DetailsPage.this.selectionIndex);
            setDetails(DetailsPage.this.selectedParam);
          }
        }
        else {
          clearGraphSetDetails();
        }

      }

      /**
       * Clear the Graph area
       */
      private void clearGraphSetDetails() {
        // switch the param & set details
        setSelectedParameter();
        setDetails(DetailsPage.this.selectedParam);
      }
    });

    this.fcTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // No Implemantation
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(DetailsPage.this.fcTableViewer);
      }
    });


    // Invoke TableViewer Column sorters
    invokeColumnSorter();
    this.parameterDataProvider = getEditor().getEditorInput().getParamDataProvider();
    this.fcTableViewer.setLabelProvider(new ListPageTableLabelProvider(this.parameterDataProvider));

    // Add filters to the TableViewer
    addFilters();
    // adding context menu
    addRightClickMenu();

    // ICDM-1348
    CommonUiUtils.getInstance().addKeyListenerToCopyNameFromViewer(this.fcTableViewer);
  }

  /**
   * Right click context menu for parameters
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new DetailsPageMenuListener(this, menuMgr));
    final Menu menu = menuMgr.createContextMenu(this.fcTableViewer.getControl());
    this.fcTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.fcTableViewer);

  }

  /**
   * Sets the selected parameter
   */
  public void setSelectedParameter() {

    if (DetailsPage.this.fcTableViewer != null) {
      final IStructuredSelection selection = (IStructuredSelection) DetailsPage.this.fcTableViewer.getSelection();
      if ((selection != null) && (!selection.isEmpty())) {
        final Object element = selection.getFirstElement();
        if (element instanceof IParameter) {
          DetailsPage.this.selectedParam = (IParameter) element;

          DetailsPage.this.editor.getEditorInput().setCdrFuncParam(DetailsPage.this.selectedParam);
          this.selectionIndex = DetailsPage.this.fcTableViewer.getGrid().getSelectionIndex();
        }
        // please set the list of parameters selected
        List<IParameter> list = selection.toList();
        DetailsPage.this.selectedParams = new ArrayList<>();

        for (IParameter iparam : list) {
          DetailsPage.this.selectedParams.add(iparam);
        }
      }
    }
  }

  /**
   * Save the modifications
   */
  private boolean saveChanges() {
    final boolean result = MessageDialogUtils.getQuestionMessageDialog("Save Resource",
        "Save changes made to the parameter : " + this.selectedParam.getName() + "?");
    if (result) {
      if (DetailsPage.this.toolBarActionSet.saveReviewDataChanges()) {
        enableSave(false);
        return true;
      }
    }
    else {
      enableSave(false);
      // Then switch the param
      setSelectedParameter();
    }
    return false;
  }

  /**
   * @param cdrFuncParam IParameter<?>
   */
  public void setDetails(final IParameter cdrFuncParam) {
    if (CommonUtils.isNotNull(cdrFuncParam)) {
      DetailsPage.this.nameTxt.setText(cdrFuncParam.getName() == null ? "" : cdrFuncParam.getName());
      DetailsPage.this.hintTxtArea.setText(cdrFuncParam.getParamHint() == null ? "" : cdrFuncParam.getParamHint());
      removeListenerFromTextField(this.nameTxt);
      addListener(this.nameTxt);

      DetailsPage.this.longNameTxt.setText(cdrFuncParam.getLongName() == null ? "" : cdrFuncParam.getLongName());
      this.selectedCdrRule = this.parameterDataProvider.getReviewRule(cdrFuncParam);
      removeListenerFromTextField(this.longNameTxt);
      addListener(this.longNameTxt);
      DetailsPage.this.codeWordChkBox.setSelection(false);
      if (ApicConstants.CODE_YES.equals(cdrFuncParam.getCodeWord())) {
        DetailsPage.this.codeWordChkBox.setSelection(true);
      }
      this.blackListChkBox.setSelection(cdrFuncParam.isBlackList());
      this.comboClass.select(this.comboClass.indexOf(ParameterClass.getParamClassT(cdrFuncParam.getpClassText()) == null
          ? " " : ParameterClass.getParamClassT(cdrFuncParam.getpClassText()).getText()));

      enableDisableReviewFields();

      setParamAttrTabInp(cdrFuncParam);
      this.delDep.setEnabled(false);
    }
    else if ((this.fcTableViewer != null) && CommonUtils.isNotEmpty(this.fcTableViewer.getGrid().getItems())) {
      // set the selection to first element
      DetailsPage.this.fcTableViewer.refresh();
      DetailsPage.this.fcTableViewer.getGrid().setSelection(0);
      setSelectedParameter();
      setDetails(DetailsPage.this.selectedParam);
    }
  }

  /**
   * Icdm-1088 set the Tab input for the Param Table
   *
   * @param cdrFuncParam
   */
  private void setParamAttrTabInp(final IParameter cdrFuncParam) {
    if (CommonUtils.isNotNull(this.paramAttrTab)) {
      this.paramAttrTab.setInput(this.parameterDataProvider.getParamAttrs(cdrFuncParam));
    }
    this.delDep.setEnabled(false);
  }


  /**
   * @param cdrFuncParam
   */
  private void enableDisableReviewFields() {
    // ICDM-910
    if (this.paramColDataProvider.isParamPropsModifiable(this.cdrFunction)) {
      this.comboClass.setEnabled(this.paramColDataProvider.isModifiable(this.cdrFunction));
      // Icdm-513
      this.codeWordChkBox.setEnabled(this.paramColDataProvider.isModifiable(this.cdrFunction));
      this.blackListChkBox.setEnabled(this.paramColDataProvider.isModifiable(this.cdrFunction));
      this.edit.setEnabled(
          this.paramColDataProvider.isModifiable(this.cdrFunction) && CommonUtils.isNotNull(this.selectedParam));
    }
    else {
      this.comboClass.setEnabled(false);
      this.codeWordChkBox.setEnabled(false);
      this.blackListChkBox.setEnabled(false);
    }
    this.addDep.setEnabled(
        this.paramColDataProvider.isModifiable(this.cdrFunction) && CommonUtils.isNotNull(this.selectedParam));// ICDM-1199
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
   * Defines the columns of the TableViewer
   */
  private void createTabColumns() {

    // ICDM-2439
    final GridViewerColumn ssdClassColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    ssdClassColumn.getColumn().setText("");
    ssdClassColumn.getColumn().setWidth(INDICATOR_COL_WIDTH);
    ssdClassColumn.getColumn().setResizeable(false);

    final GridViewerColumn typeColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    typeColumn.getColumn().setText("");
    typeColumn.getColumn().setWidth(TYPE_COL_WIDTH);
    typeColumn.getColumn().setResizeable(false);


    final GridViewerColumn paramNameColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    paramNameColumn.getColumn().setText("Parameter");
    paramNameColumn.getColumn().setWidth(NAME_COL_WIDTH);

    // Add column selection listener
    paramNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(paramNameColumn.getColumn(), 1, this.fcTabSorter, this.fcTableViewer));


  }

  /**
   * ICdm-1087 - new Table for Attr dep Defines the columns of the TableViewer
   */
  private void createTabColumns2() {

    final GridViewerColumn attrColumn = new GridViewerColumn(this.paramAttrTab, SWT.NONE);
    attrColumn.getColumn().setText("Attribute");
    attrColumn.getColumn().setWidth(ATTR_COL_WIDTH);
    attrColumn.getColumn().setResizeable(true);

    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IParameterAttribute param = (IParameterAttribute) element;
        return param.getName();
      }
    });

    attrColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrColumn.getColumn(), PARAM_ATTR_TAB_COL0, this.paramAttrDepTabSort, this.paramAttrTab));

    final GridViewerColumn descColumn = new GridViewerColumn(this.paramAttrTab, SWT.NONE);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(DESC_COL_WIDTH);
    descColumn.getColumn().setResizeable(true);

    descColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IParameterAttribute param = (IParameterAttribute) element;
        Attribute attribute = DetailsPage.this.parameterDataProvider.getAttribute(param);
        return attribute.getDescription();
      }
    });

    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), PARAM_ATTR_TAB_COL1, this.paramAttrDepTabSort, this.paramAttrTab));

    final GridViewerColumn unitColumn = new GridViewerColumn(this.paramAttrTab, SWT.NONE);
    unitColumn.getColumn().setText("Unit");
    unitColumn.getColumn().setWidth(UNIT_COL_WIDTH);
    unitColumn.getColumn().setResizeable(true);

    unitColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IParameterAttribute param = (IParameterAttribute) element;
        Attribute attribute = DetailsPage.this.parameterDataProvider.getAttribute(param);
        return attribute.getUnit();
      }
    });

    unitColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(unitColumn.getColumn(), PARAM_ATTR_TAB_COL2, this.paramAttrDepTabSort, this.paramAttrTab));
  }


  /* *//**
        * Add sorter for the table columns
        */
  private void invokeColumnSorter() {
    this.fcTableViewer.setComparator(this.fcTabSorter);
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        promptToSave();
        final String text = DetailsPage.this.filterTxt.getText().trim();
        DetailsPage.this.paramFilter.setFilterText(text);
        DetailsPage.this.fcTableViewer.refresh();

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
   * This method initializes group
   */
  private void createSections() {

    createsectionParamProperties();

    createSectionAttrDep();
  }

  /**
   * Icdm-1087-new table for attr dep
   */
  private void createSectionAttrDep() {
    final Section sectionAttrDep = this.formToolkit.createSection(this.compositeTwo, ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = ATTR_DEP_COL_LEN;
    sectionAttrDep.setLayout(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    sectionAttrDep.setLayoutData(gridData);
    sectionAttrDep.setText("Attribute Dependencies");
    sectionAttrDep.setExpanded(true);

    FormText descriptionCtrl = new FormText(sectionAttrDep, SWT.READ_ONLY);
    descriptionCtrl.setText(WARNING_MSG_FOR_DEP_ATTR, false, false);
    descriptionCtrl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    sectionAttrDep.setDescriptionControl(descriptionCtrl);
    sectionAttrDep.getDescriptionControl().setEnabled(true);

    final Form formParamProperties = this.formToolkit.createForm(sectionAttrDep);
    formParamProperties.getBody().setLayout(gridLayout1);
    formParamProperties.setLayoutData(gridData);
    createParamAttrTable(formParamProperties);
    createAddDepButton(formParamProperties);
    sectionAttrDep.setClient(formParamProperties);
  }


  /**
   * @param formParamProperties
   */
  private void createAddDepButton(final Form formParamProperties) {
    final Composite btnComp = this.formToolkit.createComposite(formParamProperties.getBody());
    btnComp.setLayout(new GridLayout());
    this.addDep = new Button(btnComp, SWT.NONE);
    this.addDep.setToolTipText("Add Dependency");
    this.addDep.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addDep.setEnabled(false);
    if (this.paramColDataProvider.isModifiable(this.cdrFunction)) {
      this.addDep.setEnabled(true);
    }
    this.addDep.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Open the Dialof to add Attr dep Icdm-1088
        AddParamAttrDepDialog paramAttrDepDialog = new AddParamAttrDepDialog(Display.getCurrent().getActiveShell(),
            DetailsPage.this, DetailsPage.this.parameterDataProvider);

        paramAttrDepDialog.open();
      }

    });

    if (CommonUtils.isNull(this.selectedParam)) {
      this.addDep.setEnabled(false);
    }
    this.delDep = new Button(btnComp, SWT.NONE);
    this.delDep.setToolTipText("Delete Dependency");
    this.delDep.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        List<D> paramAttrs = DetailsPage.this.selParamAttrs;

        if (CommonUtils.isNotNull(paramAttrs)) {
          try {
            DetailsPage.this.parameterDataProvider.deleteParamAttrs(paramAttrs);
            List<D> tabParamAttrs =
                DetailsPage.this.parameterDataProvider.getParamAttrs(DetailsPage.this.selectedParam);

            tabParamAttrs.removeAll(paramAttrs);

            DetailsPage.this.paramAttrTab.setInput(tabParamAttrs);
            DetailsPage.this.getEditor().refreshSelectedParamRuleData();
            DetailsPage.this.getEditor().getListPage().refreshListPage();
            setTabViewerInput(false);
            if (null != DetailsPage.this.getEditor().getParamRulesPage()) {
              DetailsPage.this.getEditor().getParamRulesPage().setTabViewerInput(false);
            }
            DetailsPage.this.delDep.setEnabled(false);
          }
          catch (Exception exp) {
            CDMLogger.getInstance().errorDialog("Error when deleting attribute dependency", exp, Activator.PLUGIN_ID);
          }
        }
      }
    });
    this.delDep.setEnabled(false);
    this.delDep.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
  }

  /**
   * @param form
   * @param sectionAttrDep
   */
  private void createParamAttrTable(final Form form) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessHorizontalSpace = false;
    this.attrFilText = this.formToolkit.createText(form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxtForAttr();
    getNewLabel(form.getBody(), SWT.NONE);
    this.paramAttrTab = new CustomGridTableViewer(form.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    initializeEditorStatusLineManager(this.paramAttrTab);
    this.paramAttrTab.setAutoPreferredHeight(true);
    this.paramAttrTab.getGrid().setLayoutData(gridData);
    this.paramAttrTab.getGrid().setLinesVisible(true);
    this.paramAttrTab.getGrid().setHeaderVisible(true);
    this.paramAttrDepTabSort = new ParamAttrTabViewerSorter(this.parameterDataProvider);
    this.paramAttrTab.setComparator(this.paramAttrDepTabSort);
    createTabColumns2();
    this.paramAttrTab.setContentProvider(ArrayContentProvider.getInstance());
    this.paramAttrTab.setInput(null);
    createParamAttrSelLis();

    createAttrFilter();
  }


  /**
   * create the Attr Dep Filter for the Param Attribute Dep Table
   */
  private void createAttrFilter() {

    this.attrFilter = new ParamAttrDepFilter(this.parameterDataProvider); // Add TableViewer filter
    this.paramAttrTab.addFilter(this.attrFilter);

    this.attrFilText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = DetailsPage.this.attrFilText.getText().trim();
        DetailsPage.this.attrFilter.setFilterText(text);
        DetailsPage.this.paramAttrTab.refresh();
      }
    });

  }


  /**
   *
   */
  private void createParamAttrSelLis() {
    this.paramAttrTab.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection = (IStructuredSelection) DetailsPage.this.paramAttrTab.getSelection();
        if (!selection.isEmpty()) {
          List<D> listparamAttr = selection.toList();
          DetailsPage.this.selParamAttrs = new ArrayList<>();
          DetailsPage.this.selParamAttrs.addAll(listparamAttr);
          if (DetailsPage.this.paramColDataProvider.isModifiable(DetailsPage.this.cdrFunction) &&
              ((ReviewParamEditorInput) getEditorInput()).getParamDataProvider()
                  .canModifyAttributeMapping(DetailsPage.this.selectedParam)) {
            DetailsPage.this.delDep.setEnabled(true);
          }
        }
      }


    });

  }


  /**
   * Create the Filter txt for Attr Dependency table
   */
  private void createFilterTxtForAttr() {
    final GridData gridData = getFilterTxtGridData();
    gridData.horizontalSpan = 1;
    this.attrFilText.setLayoutData(gridData);
    this.attrFilText.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.attrFilText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        // To be implemented

      }
    });

  }


  /**
   * This method initializes group1
   */
  private void createsectionParamProperties() {

    this.sectionParamProp =
        this.formToolkit.createSection(this.compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = PARAM_PROP_COLS;
    this.sectionParamProp.setLayout(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    this.sectionParamProp.setLayoutData(gridData);
    this.sectionParamProp.setText("Parameter Properties");
    final Form formParamProperties = this.formToolkit.createForm(this.sectionParamProp);
    formParamProperties.getBody().setLayout(gridLayout1);
    formParamProperties.setLayoutData(gridData);
    createNameUIControls(formParamProperties);
    createClassUIControls(formParamProperties);
    createLongNameUIControls(formParamProperties);
    createCodeWordUIControls(formParamProperties);
    createHintUIControls(formParamProperties);
    createBlaclListUIControls(formParamProperties);
    this.sectionParamProp.setClient(formParamProperties);
    this.sectionParamProp.getDescriptionControl().setEnabled(false);

  }


  /**
   * Icdm-1087
   *
   * @param scComp
   */
  private void createHintUIControls(final Form form) {

    fillerLabels(form.getBody(), HINT_UI_FILLERS);
    // Icdm-621
    createLabelControl(form.getBody(), "Calibration Hint");
    // ICDM-759
    this.hintTxtArea = new StyledText(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData = GridDataUtil.getInstance().createGridData(HINT_WIDTH, HINT_HEIGHT);
    this.hintTxtArea.setLayoutData(createGridData);
    this.hintTxtArea.setEditable(false);
    if (this.paramColDataProvider.isParamPropsModifiable(this.cdrFunction)) {
      this.edit = new Button(form.getBody(), SWT.NONE);
      this.edit.setToolTipText("Edit");
      this.edit.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
      this.edit.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          createHintEditDialog();
        }

      });

      if (CommonUtils.isNull(this.selectedParam)) {
        this.edit.setEnabled(false);
      }
    }
  }

  /**
   *
   */
  private void createHintEditDialog() {
    HintEditDialog hintDialog = new HintEditDialog(Display.getCurrent().getActiveShell(), this.selectedParam, this);
    hintDialog.open();
  }


  /**
   * @param form
   */
  private void createNameUIControls(final Form form) {
    createLabelControl(form.getBody(), "Name");
    this.nameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);

  }

  /**
   * @param form
   */
  private void createClassUIControls(final Form form) {
    final Label emptyLbl = new Label(form.getBody(), SWT.NONE);
    emptyLbl.setText("       ");
    createLabelControl(form.getBody(), "Class");
    this.comboClass = new Combo(form.getBody(), SWT.READ_ONLY);
    this.comboClass.setSize(CLASS_COMBO_WIDTH, CLASS_COMBO_HEIGHT);
    this.comboClass.add(" ");
    this.comboClass.add(ParameterClass.NAIL.getText());
    this.comboClass.add(ParameterClass.SCREW.getText());
    this.comboClass.add(ParameterClass.RIVET.getText());
    // ICDM-916 stat Rivet
    this.comboClass.add(ParameterClass.STATRIVET.getText());

    // Selection listener
    this.comboClass.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (DetailsPage.this.selectedParam != null) {
          updateData(true);
        }
      }
    });
  }

  /**
   * @param newParamClass
   * @param newCodeWord
   * @param isBlack
   */
  private void updateData(final boolean isParamUpdate) {

    String newParamClass = null;
    if (isParamUpdate) {
      final int index = DetailsPage.this.comboClass.getSelectionIndex();
      newParamClass = DetailsPage.this.comboClass.getItem(index);
      // ICDM-916 stat Rivet
      if (ParameterClass.getParamClassT(newParamClass) == ParameterClass.STATRIVET) {
        MessageDialogUtils.getErrorMessageDialog("Statistical Rivet cannot be set manually",
            "Statistical Rivet class cannot be set manually");
        this.comboClass.select(this.comboClass
            .indexOf(this.selectedParam.getpClassText() == null ? " " : this.selectedParam.getpClassText()));
        return;
      }
    }
    /*
     * Icdm-645 Rivet should have exact match -if the class is rivet and the Exact match is not set then rule should not
     * be created
     */
    // ICDM-795
    if (this.selectedParam instanceof Parameter) {
      try {

        Parameter param = ((Parameter) (this.selectedParam)).clone();
        CommonUtils.shallowCopy(param, this.selectedParam);
        param.setCodeWord(CommonUtils.getBooleanCode(this.codeWordChkBox.getSelection()));
        param.setBlackList(this.blackListChkBox.getSelection());
        if (isParamUpdate) {
          param.setpClassText(ParameterClass.getParamClassT(newParamClass).getText());
        }
        ParameterServiceClient client = new ParameterServiceClient();
        Parameter updatedParam = client.update(param);
        CommonUtils.shallowCopy(this.selectedParam, updatedParam);
        refresh();
      }
      catch (ApicWebServiceException | CloneNotSupportedException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

    }
  }

  /**
   *
   */
  private void refresh() {
    setTabViewerInput(true);

    // call the method to set the values
    if (this.selectedParam != null) {
      this.fcTableViewer.setSelection(new StructuredSelection(this.selectedParam), true);
      DetailsPage.this.codeWordChkBox.setSelection(false);
      if (ApicConstants.CODE_YES.equals(this.selectedParam.getCodeWord())) {
        DetailsPage.this.codeWordChkBox.setSelection(true);
      }
      DetailsPage.this.blackListChkBox.setSelection(this.selectedParam.isBlackList());
      this.comboClass.select(this.comboClass
          .indexOf(this.selectedParam.getpClassText() == null ? " " : this.selectedParam.getpClassText()));
    }
    if (CommonUtils.isNotNull(this.paramAttrTab)) {
      this.paramAttrTab.refresh();
    }

  }

  /**
   * @param form
   */
  private void createLongNameUIControls(final Form form) {
    createLabelControl(form.getBody(), "Long Name");
    this.longNameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);

  }

  /**
   * @param form
   */
  private void createCodeWordUIControls(final Form form) {
    fillerLabels(form.getBody(), 1);
    createLabelControl(form.getBody(), "Codeword");
    this.codeWordChkBox = new Button(form.getBody(), SWT.CHECK);
    this.codeWordChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (DetailsPage.this.selectedParam != null) {
          updateData(false);
        }
      }
    });
  }

  /**
   * @param form
   */
  private void createBlaclListUIControls(final Form form) {
    createLabelControl(form.getBody(), "Black List Label");
    this.blackListChkBox = new Button(form.getBody(), SWT.CHECK);
    this.blackListChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (DetailsPage.this.selectedParam != null) {
          updateData(false);
        }
      }
    });
  }

  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void createLabelControl(final Composite composite, final String lblName) {
    LabelUtil.getInstance().createLabel(this.formToolkit, composite, lblName);
  }


  /**
   *
   */
  private void enableSave(final boolean enable) {
    DetailsPage.this.toolBarActionSet.getSaveReviewData().setEnabled(enable);
    DetailsPage.this.setUnSavedDataPresent(enable);
  }


  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return
   */
  private StyledText createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    StyledText styledTxt = new StyledText(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }


  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {
    for (int i = 0; i < limit; i++) {
      getNewLabel(grp, SWT.NONE);
    }
  }

  /**
   * Sets the tableviewer input
   *
   * @param refreshNeeded
   * @param versionChanged
   */
  public void setTabViewerInput(final boolean refreshNeeded) {
    if ((null != this.funcSelcSec) && this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      setTabInputForFunction(refreshNeeded);
    }
    else {
      new ParamTableContentSetter().setInputForRuleSet(refreshNeeded, this.fcTableViewer, this.editor, this.cdrFunction,
          this);
    }
    if (refreshNeeded && ((this.fcTableViewer != null) && (this.fcTableViewer.getGrid().getItems().length > 0))) {
      setSelectedParameter();
      setDetails(this.selectedParam);
      this.fcTableViewer.getGrid().setFocus();
    }
  }


  /**
   * set input for Functions
   *
   * @param refreshNeeded
   * @param versionChanged
   */
  private void setTabInputForFunction(final boolean refreshNeeded) {

    String selFuncVersion = null;
    final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
    ParamTableInputProvider inputProvider = new ParamTableInputProvider(this.cdrFunction, this.editor);

    if (this.funcSelcSec.getComboFuncVersion().getItem(index).equalsIgnoreCase(ApicConstants.OPTION_ALL)) {
      Map<String, ?> paramMap = null;
      this.editor.getEditorInput().setCdrFuncVersion(ApicConstants.OPTION_ALL);
      try {
        if ((this.editor.getEditorInput().getParamMap() != null) && !refreshNeeded) {
          paramMap = this.editor.getEditorInput().getParamMap();
        }
        else {
          paramMap = inputProvider.getParamMap();
        }
        if ((this.fcTableViewer != null) && (refreshNeeded || (this.fcTableViewer.getGrid().getItemCount() == 0))) {
          this.fcTableViewer.getGrid().removeAll();
          this.fcTableViewer.setInput(paramMap.values());
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
      selFuncVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);

      try {
        Map<String, ?> paramMap = null;
        if (refreshNeeded) {

          inputProvider.setVersionSel(selFuncVersion);

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


        }
        else {
          paramMap = this.editor.getEditorInput().getParamMap();
        }

        setInputToTable(paramMap);

        this.editor.getEditorInput().setCdrFuncVersion(selFuncVersion);


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
   * @param paramMap
   */
  protected void setInputToTable(final Map<String, ?> paramMap) {
    if (this.fcTableViewer != null) {
      this.fcTableViewer.getGrid().removeAll();
      this.fcTableViewer.setInput(paramMap.values());
    }
  }

  /**
   * @return the selectedParam
   */
  public IParameter getSelectedParam() {
    return this.selectedParam;
  }

  /**
   * @return selectedCdrRule
   */
  public ReviewRule getSelectedCdrRule() {
    return this.selectedCdrRule;
  }

  /**
   * @return unSavedDataPresent
   */
  public boolean isUnSavedDataPresent() {
    return this.unSavedDataPresent;
  }

  /**
   * @param unSavedDataPresent .
   */
  public void setUnSavedDataPresent(final boolean unSavedDataPresent) {
    this.unSavedDataPresent = unSavedDataPresent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canLeaveThePage() {
    promptToSave();
    return true;
  }

  /**
   * Prompt the user with a dialog if there are unsaved review data present
   */
  public void promptToSave() {
    if (isUnSavedDataPresent()) {
      saveChanges();
    }
  }

  /*  *//**
         * {@inheritDoc}
         */
  @Override
  public boolean isDirty() {
    boolean isDirty = false;
    if (isUnSavedDataPresent()) {
      isDirty = true;
    }
    return isDirty;
  }


  /**
   * @return toolBarActionSet
   */
  public ReviewRuleToolBarActionSet getToolBarActionSet() {
    return this.toolBarActionSet;
  }

  /**
   * @return the fcTableViewer
   */
  public GridTableViewer getFcTableViewer() {
    return this.fcTableViewer;
  }

  /**
   * @param selectedCdrRule selectedCdrRule
   */
  public void setSelectedCdrRule(final ReviewRule selectedCdrRule) {
    this.selectedCdrRule = selectedCdrRule;
  }

  /* *//**
        * {@inheritDoc}
        */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (checkSelection() && DetailsPage.this.paramColDataProvider.isModifiable(DetailsPage.this.cdrFunction) &&
        ((ReviewParamEditorInput) getEditorInput()).getParamDataProvider()
            .canModifyAttributeMapping(DetailsPage.this.selectedParam)) {
      DetailsPage.this.delDep.setEnabled(true);
    }
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      filterOutlineSelection(selection);
    }
  }

  /**
   * @return
   */
  private boolean checkSelection() {
    return (null != DetailsPage.this.selParamAttrs) && !DetailsPage.this.selParamAttrs.isEmpty() &&
        (null != DetailsPage.this.delDep) && !DetailsPage.this.delDep.isDisposed();
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

  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }


  /**
   * @return the refValCalDataObj
   */
  public CalData getRefValCalDataObj() {
    return this.refValCalDataObj;
  }

  /**
   * @param refValCalDataObj the refValCalDataObj to set
   */
  public void setRefValCalDataObj(final CalData refValCalDataObj) {
    this.refValCalDataObj = refValCalDataObj;
  }


  /**
   * @return the chkAlternative
   */
  protected boolean getChkAlternativeSelection() {
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      return (DetailsPage.this.funcSelcSec.getChkAlternative() != null) &&
          DetailsPage.this.funcSelcSec.getChkAlternative().getSelection();
    }
    return false;
  }


  /**
   * @return the chkVariant
   */
  protected boolean getChkVariantSelection() {
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      return (DetailsPage.this.funcSelcSec.getChkVariant() != null) &&
          DetailsPage.this.funcSelcSec.getChkVariant().getSelection();
    }
    return false;
  }


  /**
   * @return the chkFuncVersion
   */
  protected boolean getChkFuncVersionSelection() {
    if (this.paramColDataProvider.hasVersions(this.cdrFunction)) {
      return (DetailsPage.this.funcSelcSec.getChkFuncVersion() != null) &&
          DetailsPage.this.funcSelcSec.getChkFuncVersion().getSelection();
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
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
   * @return the parameterDataProvider
   */
  public ParameterDataProvider<D, P> getParameterDataProvider() {
    return this.parameterDataProvider;
  }


  /**
   * @param selectedParam the selectedParam to set
   */
  public void setSelectedParam(final IParameter selectedParam) {
    this.selectedParam = selectedParam;
  }


  /**
   * @return the selectedParams
   */
  public List<IParameter> getSelectedParams() {
    return this.selectedParams;
  }

  /**
   * @return the addRuleSetParamAction
   */
  public AddRuleSetParamAction getAddRuleSetParamAction() {
    return this.addRuleSetParamAction;
  }


  /**
   * @return the deleteRuleSetParamAction
   */
  public DeleteRuleSetParamAction getDeleteRuleSetParamAction() {
    return this.deleteRuleSetParamAction;
  }
}
