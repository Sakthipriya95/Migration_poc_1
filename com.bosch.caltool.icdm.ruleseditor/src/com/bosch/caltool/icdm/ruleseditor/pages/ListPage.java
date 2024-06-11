/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandToLevelCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.actions.ResetFiltersAction;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.ruleseditor.actions.AddRuleSetParamAction;
import com.bosch.caltool.icdm.ruleseditor.actions.DeleteRuleSetParamAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ExportRulesetAsCDFxAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ImportCalDataAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ImportRuleSetParamFromA2LAction;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.table.filters.RulesEditorOutlineFilter;
import com.bosch.caltool.icdm.ruleseditor.views.providers.ParamTableInputProvider;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * First page of ReviewParam editor
 *
 * @author mkl2cob
 */
public class ListPage<D extends IParameterAttribute, P extends IParameter> extends AbstractFormPage
    implements ISelectionListener {


  private static final String ERR_FETCHING_PARAM_AND_RULES = "Error when fetching parameter and rules";

  /**
   * Editor instance
   */
  private final ReviewParamEditor editor;


  /**
   * @return the editor
   */
  @Override
  public ReviewParamEditor getEditor() {
    return this.editor;
  }

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;

  /**
   * Page's main composite
   */
  private Composite composite;


  /**
   * IParameterCollection instance
   */
  private final ParamCollection cdrFunction;
  /**
   * Defines selected function version
   */
  protected String selFuncVersion;


  /**
   * @return the selFuncVersion
   */
  public String getSelFuncVersion() {
    return this.selFuncVersion;
  }

  // Icdm-502


  /**
   * Function version section
   */
  FunctionVersionSection funcSelcSec;


  /**
   * @return the funcSelcSec
   */
  public FunctionVersionSection getFuncSelcSec() {
    return this.funcSelcSec;
  }

  /**
   * Parameter table section
   */
  private ParamTableSection paramTabSec;
  /**
   * icdm-2266 outline filter
   */
  private RulesEditorOutlineFilter outlineFilter;

  private final ParamCollectionDataProvider dataProvider;

  private boolean isRefreshNeeded;
  private AddRuleSetParamAction addRuleSetParamAction;
  private DeleteRuleSetParamAction deleteRuleSetParamAction;
  /**
   * Action Button to export RuleSetAsCDFX file
   */
  private ExportRulesetAsCDFxAction expRulesetAsCDFxAction;

  /**
   * Action Button to ImportRuleSetParamFromA2LAction
   */
  ImportRuleSetParamFromA2LAction importRuleSetParamFromA2LAction;

  /**
   * @return the outlineFilter
   */
  public RulesEditorOutlineFilter getOutlineFilter() {
    return this.outlineFilter;
  }

  /**
   * @return the paramTabSec
   */
  public ParamTableSection getParamTabSec() {
    return this.paramTabSec;
  }

  /**
   * Constructor
   *
   * @param editor -Editor instance for this page
   * @param cdrFunction cdrFunction
   */
  public ListPage(final FormEditor editor, final ParamCollection cdrFunction) {
    super(editor, "", "Summary"); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (ReviewParamEditor) editor;
    this.cdrFunction = cdrFunction;
    this.dataProvider = this.editor.getEditorInput().getDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(
        com.bosch.caltool.icdm.common.util.CommonUtils.concatenate(this.editor.getPartName(), " - ", "Summary"));
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    // instead of editor.getToolkit().createScrolledForm(parent) in superclass
    // formToolkit is obtained from managed form to create form within section
    final ManagedForm mform = new ManagedForm(parent);
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
    final FormToolkit formToolkit = managedForm.getToolkit();


    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * Create the main composite and child objects. This method initializes composite.
   *
   * @param toolkit
   */
  private void createComposite(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    final GridLayout gridLayout = new GridLayout();

    // If parmeter collection has versions, add the function version selection section
    boolean hasFunctionVersions = this.dataProvider.hasVersions(this.cdrFunction);
    if (hasFunctionVersions) {
      this.funcSelcSec = new FunctionVersionSection(ListPage.this, this.editor.getEditorInput(), this.composite);
      this.funcSelcSec.createFuncSelcSec(toolkit);
    }

    // Create the parameter table section
    this.paramTabSec = new ParamTableSection(ListPage.this);
    this.paramTabSec.createParamTabSec(toolkit);

    boolean paramMappingModifable = !this.dataProvider.isParamMappingModifiable(this.cdrFunction);
    boolean canImportRule = this.dataProvider.isRuleImportAllowed();


    // Add buttons only if parameter(s) can be added/removed or rule(s) can be imported
    if (paramMappingModifable || canImportRule) {

      ToolBarManager tbMgr = (ToolBarManager) this.paramTabSec.getFormTwo().getToolBarManager();

      // Add Export as CDFx, Add, Remove parameter buttons
      if (paramMappingModifable) {
        // has function version will be false for RuleSets
        if (!hasFunctionVersions) {
          this.importRuleSetParamFromA2LAction =
              new ImportRuleSetParamFromA2LAction(this.cdrFunction, this, this.dataProvider);
          tbMgr.add(this.importRuleSetParamFromA2LAction);
        }

        this.expRulesetAsCDFxAction = new ExportRulesetAsCDFxAction(ListPage.this, this.cdrFunction, this.dataProvider);
        tbMgr.add(this.expRulesetAsCDFxAction);

        this.addRuleSetParamAction = new AddRuleSetParamAction(this.cdrFunction, this, this.dataProvider);
        tbMgr.add(this.addRuleSetParamAction);

        this.deleteRuleSetParamAction =
            new DeleteRuleSetParamAction(this.cdrFunction, this.editor, this.dataProvider, this);
        tbMgr.add(this.deleteRuleSetParamAction);
      }
      // Add import Rule action
      if (canImportRule) {
        // Add separator only if both flags are true
        if (paramMappingModifable) {
          tbMgr.add(new Separator());
        }

        // Adding calibration data import action
        ImportCalDataAction cmnActionSet =
            new ImportCalDataAction(this.cdrFunction, this.dataProvider, this.editor.getEditorInput());
        tbMgr.add(cmnActionSet);
      }

      tbMgr.update(true);

    }

    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);

  }


  @Override
  public ResetFiltersAction getResetFiltersAction() {
    return this.paramTabSec.getParamTab().getResetFiltersAction();
  }

  /**
   * @return the composite
   */
  public Composite getComposite() {
    return this.composite;
  }


  /* *//**
        * Add filters for the table
        */
  public void addFilters() {

    // icdm-2266
    this.outlineFilter = new RulesEditorOutlineFilter(this.paramTabSec.getParamTab().getCustomFilterGridLayer(),
        this.editor.getEditorInput());

    this.paramTabSec.getParamTab().getCustomFilterGridLayer().getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineFilter.getOutlineMatcher());
  }


  /**
   * Sets the tableviewer input
   *
   * @return the Input
   */
  public List<Object> setTabViewerInput() {
    List<Object> inputList = new ArrayList<>();

    if (this.cdrFunction instanceof Function) {
      Function func = (Function) this.cdrFunction;
      if (this.dataProvider.isBigFunction(func)) {
        final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
        ListPage.this.selFuncVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);
        if ((ListPage.this.selFuncVersion == null) || ListPage.this.selFuncVersion.equals(ApicConstants.OPTION_ALL)) {
          CDMLogger.getInstance().warnDialog(
              "The function " + func.getName() +
                  " has too many parameters. You need to select a function version to see the parameters.",
              Activator.PLUGIN_ID);
          return inputList;
        }
      }
    }
    ParamTableInputProvider inputProvider = new ParamTableInputProvider(this.cdrFunction, this.editor);

    // Settings inside version selection section
    if (this.dataProvider.hasVersions(this.cdrFunction)) {
      inputList = setInputForFunction(inputList, inputProvider);
    }
    else if (this.cdrFunction instanceof CompPackage) {
      inputList = setInputForCompPkg(inputList, inputProvider);
    }
    else {
      inputList = setInputForRuleSet(inputList, inputProvider);
    }

    return inputList;
  }

  /**
   * @param inputList
   * @param inputProvider
   * @return
   */
  private List<Object> setInputForRuleSet(List<Object> inputList, final ParamTableInputProvider inputProvider) {
    try {


      IParamRuleResponse<RuleSetParameterAttr, RuleSetParameter> paramRulesOutput =
          this.editor.getEditorInput().getDataProvider().getRulesOutput(this.cdrFunction.getId());
      ParameterDataProvider<RuleSetParameterAttr, RuleSetParameter> paramDataProvider =
          new ParameterDataProvider<>(paramRulesOutput);
      this.editor.getEditorInput().setParamRulesOutput(paramRulesOutput);
      this.editor.getEditorInput().setParamDataProvider(paramDataProvider);
      Map<String, ?> paramMap = paramRulesOutput.getParamMap();
      inputList = inputProvider.getTableInput(paramMap);


      this.editor.getEditorInput().setParamMap(paramMap);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(ERR_FETCHING_PARAM_AND_RULES, exp);
    }

    return inputList;
  }

  private List<Object> setInputForCompPkg(List<Object> inputList, final ParamTableInputProvider inputProvider) {
    try {
      IParamRuleResponse<CompPkgParamAttr, CompPkgParameter> paramRulesOutput =
          this.editor.getEditorInput().getDataProvider().getCompPkg(this.cdrFunction.getId());
      ParameterDataProvider<CompPkgParamAttr, CompPkgParameter> paramDataProvider =
          new ParameterDataProvider<>(paramRulesOutput);
      this.editor.getEditorInput().setParamRulesOutput(paramRulesOutput);
      this.editor.getEditorInput().setParamDataProvider(paramDataProvider);
      Map<String, ?> paramMap = paramRulesOutput.getParamMap();
      inputList = inputProvider.getTableInput(paramMap);
      this.editor.getEditorInput().setParamMap(paramMap);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(ERR_FETCHING_PARAM_AND_RULES, exp);
    }

    return inputList;
  }

  /**
   * @param inputList
   * @param inputProvider
   * @return
   */
  private List<Object> setInputForFunction(List<Object> inputList, final ParamTableInputProvider inputProvider) {
    final int index = this.funcSelcSec.getComboFuncVersion().getSelectionIndex();
    if (ListPage.this.funcSelcSec.getComboFuncVersion().getItem(index).equalsIgnoreCase(ApicConstants.OPTION_ALL)) {
      this.editor.getEditorInput().setCdrFuncVersion(ApicConstants.OPTION_ALL);
      try {
        ListPage.this.selFuncVersion = null;
        if ((this.editor.getEditorInput().getParamMap() != null) &&
            !this.editor.getEditorInput().getParamMap().isEmpty() && !this.isRefreshNeeded) {
          inputList = inputProvider.getTableInput(this.editor.getEditorInput().getParamMap());
        }
        else {
          final Map<String, ?> allParamsMap = inputProvider.getParamMap();
          inputList = inputProvider.getTableInput(allParamsMap);
        }
      }
      catch (ApicWebServiceException exp) {
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
      ListPage.this.selFuncVersion = this.funcSelcSec.getComboFuncVersion().getItem(index);
      try {
        inputProvider.setVersionSel(ListPage.this.selFuncVersion);
        Map<String, ?> paramMap;
        if (this.funcSelcSec.getChkFuncVersion().getSelection()) {
          paramMap = inputProvider.getParamMap();
        }
        else if (ListPage.this.funcSelcSec.getChkVariant().getSelection()) {
          inputProvider.setChkVar("true");
          paramMap = inputProvider.getParamMap();
        }
        else {
          inputProvider.setChkVar("false");
          // search for alternative
          paramMap = inputProvider.getParamMap();
        }

        this.editor.getEditorInput().setCdrFuncVersion(ListPage.this.selFuncVersion);
        inputList = inputProvider.getTableInput(paramMap);

        // enable radio buttons
        this.funcSelcSec.getChkFuncVersion().setEnabled(true);
        this.funcSelcSec.getChkAlternative().setEnabled(true);
        this.funcSelcSec.getChkVariant().setEnabled(true);

      }

      catch (Exception exp) {
        CDMLogger.getInstance().error(ERR_FETCHING_PARAM_AND_RULES, exp);
      }
    }
    return inputList;
  }


  /**
   * select the row of parameter
   */
  public void setRowSelection() {
    if (null == this.editor.getEditorInput().getRuleId()) {
      boolean setSelection = false;
      // To synchronise the selected row in the tableviewer
      IParameter cdrFuncParam = this.editor.getEditorInput().getCdrFuncParam();
      if (cdrFuncParam != null) {
        this.paramTabSec.getParamTab().getSelectionProvider().setSelection(new StructuredSelection(cdrFuncParam));
        setSelection = true;
        this.paramTabSec.getNatTable().setFocus();
      }
      // setting the default selection to the first row of tableviewer
      if (!setSelection && (this.paramTabSec.getNatTable() != null) &&
          (this.paramTabSec.getNatTable().getRowCount() > 0)) {
        this.paramTabSec.getNatTable().doCommand(new ShowRowInViewportCommand(
            this.paramTabSec.getParamTab().getCustomFilterGridLayer().getBodyLayer().getViewportLayer(), 0));
        setSelectedParameter();
        this.paramTabSec.getNatTable().setFocus();
      }
    }
  }


  /**
   * Sets the selected parameter when rule is selected
   */
  public void setSelectedRule() {
    final IStructuredSelection selection =
        (IStructuredSelection) this.paramTabSec.getParamTab().getSelectionProvider().getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      ReviewRule rule = null;
      if (element instanceof ReviewRule) {
        rule = (ReviewRule) element;
      }
      // If selected row is DefaultRuleDefinition, get the rule using getRule() method
      else if (element instanceof DefaultRuleDefinition) {
        DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
        rule = defaultRule.getReviewRule();
      }
      // If rule is obtained, get the parameter object with the details (parameter name, type) in the rule
      if (rule != null) {
        IParameter iparam = (IParameter) this.editor.getEditorInput().getParamDataProvider().getParamRulesOutput()
            .getParamMap().get(rule.getParameterName());
        ListPage.this.editor.getEditorInput().setCdrFuncParam(iparam);
      }

    }
  }

  //
  /**
   * Sets the selected parameter
   */
  public void setSelectedParameter() {
    IParameter cdrFuncParam = this.paramTabSec.getListPage().getEditor().getEditorInput().getCdrFuncParam();
    if (CommonUtils.isNull(cdrFuncParam)) {
      final IStructuredSelection selection =
          (IStructuredSelection) this.paramTabSec.getParamTab().getSelectionProvider().getSelection();
      if ((selection != null) && (!selection.isEmpty())) {
        final Object element = selection.getFirstElement();
        if (element instanceof IParameter) {
          final IParameter selectedParam = (IParameter) element;
          ListPage.this.editor.getEditorInput().setCdrFuncParam(selectedParam);
        }
      }
    }
  }


  /**
   * @return
   */
  public CustomFilterGridLayer getCustomGridFilterLayer() {
    return this.paramTabSec.getParamTab().getCustomFilterGridLayer();
  }

  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.paramTabSec.getParamTab().getGroupByHeaderLayer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {

    if (this.editor != null) {
      setFunctionSelectionBasedOnSelCombo();
      // retain the selection to show the info of the previously selected parameter in Properties view
      ParamNatTable paramNatTable = this.paramTabSec.getParamTab();
      final IStructuredSelection selection =
          (IStructuredSelection) (paramNatTable.getSelectionProvider().getSelection());
      if ((selection != null) && (!selection.isEmpty())) {
        paramNatTable.setSelectedObjectToPropertiesView(selection);
      }
      filterBasedOnOutlineSelection(this.editor.getOutlinePageCreator().getOutlinePageSelection());
      setSelectedParameter();
      setRowSelection();
      setRvwRleForExterLink(paramNatTable, this.editor.getEditorInput().getCdrFuncParam());
      paramNatTable.setStatusBarMessage(getGroupByHeaderLayer(), false);

      super.setActive(active);
    }


  }

  /**
   *
   */
  private void setFunctionSelectionBasedOnSelCombo() {
    if (this.dataProvider.hasVersions(this.cdrFunction)) {
      if (this.editor.getEditorInput().getCdrFuncVersion() != null) {
        this.funcSelcSec.getComboFuncVersion()
            .select(this.funcSelcSec.getComboFuncVersion().indexOf(this.editor.getEditorInput().getCdrFuncVersion()));
      }

      if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          FunctionVersionSection.FUNC_VERSION.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(true);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }
      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          FunctionVersionSection.ALTERNATIVE.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(true);
        this.funcSelcSec.getChkVariant().setSelection(false);
      }

      else if ((((ReviewParamEditorInput) getEditorInput()).getSelComboTxt() != null) &&
          FunctionVersionSection.VARIANT.equals(((ReviewParamEditorInput) getEditorInput()).getSelComboTxt())) {
        this.funcSelcSec.getChkFuncVersion().setSelection(false);
        this.funcSelcSec.getChkAlternative().setSelection(false);
        this.funcSelcSec.getChkVariant().setSelection(true);
      }
    }
  }

  /**
   * @param paramNatTable
   */
  private void setRvwRleForExterLink(final ParamNatTable paramNatTable, final IParameter cdrFuncParam) {
    if (CommonUtils.isNotNull(this.editor.getEditorInput().getRuleId())) {
      Long ruleId = this.editor.getEditorInput().getRuleId();
      ReviewRule rvwRule = new ReviewRule();
      rvwRule.setRuleId(new BigDecimal(ruleId));
      Object reviewRuleObject = getReviewRuleObject(paramNatTable, rvwRule);
      if (reviewRuleObject != null) {
        int indexOfRowObject =
            paramNatTable.getCustomFilterGridLayer().getBodyDataProvider().indexOfRowObject(cdrFuncParam);
        this.paramTabSec.getNatTable().doCommand(new TreeExpandToLevelCommand(indexOfRowObject, 2));
        paramNatTable.getSelectionProvider().setSelection(new StructuredSelection(reviewRuleObject));
        this.paramTabSec.getNatTable().setFocus();
      }
    }
  }

  /**
   * @param paramNatTable
   * @param rvwRule
   */
  public Object getReviewRuleObject(final ParamNatTable paramNatTable, final ReviewRule rvwRule) {
    for (Object obj : paramNatTable.getInputList()) {
      if ((obj instanceof ReviewRule) && obj.equals(rvwRule)) {
        return obj;
      }
    }
    return getDefaultReviewRuleObject(paramNatTable, rvwRule);
  }

  /**
   * @param paramNatTable
   * @param rvwRule
   */
  public DefaultRuleDefinition getDefaultReviewRuleObject(final ParamNatTable paramNatTable, final ReviewRule rvwRule) {
    DefaultRuleDefinition defaultRuleDefinition = new DefaultRuleDefinition();
    defaultRuleDefinition.setReviewRule(rvwRule);
    for (Object obj : paramNatTable.getInputList()) {
      if ((obj instanceof DefaultRuleDefinition)) {
        DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) obj;
        if (rvwRule.equals(defaultRule.getReviewRule())) {
          return defaultRule;
        }
      }
    }
    return null;
  }

  /**
   * Get the FC Table viewer
   *
   * @return the fcTableViewer
   */
  public CustomNATTable getFcTableViewer() {
    return this.paramTabSec.getNatTable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      filterBasedOnOutlineSelection(selection);
    }
  }

  /**
   * @param selection
   */
  private void filterBasedOnOutlineSelection(final ISelection selection) {
    if (this.outlineFilter != null) {
      this.outlineFilter.a2lOutlineSelectionListener(selection);
      if (!this.paramTabSec.getNatTable().isDisposed()) {
        ParameterDataProvider paramDataProvider = getEditor().getEditorInput().getParamDataProvider();
        int toltalCount = 0;
        if (null != paramDataProvider) {
          toltalCount = null != paramDataProvider.getParamRulesOutput()
              ? paramDataProvider.getParamRulesOutput().getParamMap().size() : 0;
        }
        int filteredItemCount =
            this.paramTabSec.getParamTab().getCustomFilterGridLayer().getRowHeaderLayer().getPreferredRowCount();
        this.editor.updateStatusBar(true, toltalCount, filteredItemCount);
      }

    }
  }

  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return null;
  }


  /**
   * @param isRefreshNeeded isRefreshNeeded
   */
  public void setRefreshNeeded(final boolean isRefreshNeeded) {
    this.isRefreshNeeded = isRefreshNeeded;
  }

  /**
   * @param paramDataProvider
   */
  public void setParameterDataProvider(final ParameterDataProvider paramDataProvider) {
    this.paramTabSec.getParamTab().setParameterDataProvider(paramDataProvider);

  }

  /**
   *
   */
  public void refreshListPage() {

    ParamTableInputProvider inputProvider = new ParamTableInputProvider(this.cdrFunction, this.editor);

    CustomFilterGridLayer customFilterGridLayer = this.paramTabSec.getParamTab().getCustomFilterGridLayer();
    customFilterGridLayer.getEventList().clear();
    setRefreshNeeded(true);
    List<Object> tableInput = inputProvider
        .getTableInput(this.editor.getEditorInput().getParamDataProvider().getParamRulesOutput().getParamMap());
    customFilterGridLayer.getEventList().addAll(tableInput);
    ((ParamNatInputToColConverter) getParamTabSec().getParamTab().getNatInputToColumnConverter())
        .setParameterDataProvider(this.editor.getEditorInput().getParamDataProvider());
    this.getFcTableViewer().refresh();
    setSelectedParameter();
    setRowSelection();
  }


  /**
   * @return the deleteRuleSetParamAction
   */
  public DeleteRuleSetParamAction getDeleteRuleSetParamAction() {
    return this.deleteRuleSetParamAction;
  }


  /**
   * @return the addRuleSetParamAction
   */
  public AddRuleSetParamAction getAddRuleSetParamAction() {
    return this.addRuleSetParamAction;
  }


  /**
   * @return the expRulesetAsCdfxAction
   */
  public ExportRulesetAsCDFxAction getExpRulesetAsCdfxAction() {
    return this.expRulesetAsCDFxAction;
  }


  /**
   * @return the importRuleSetParamFromA2LAction
   */
  public ImportRuleSetParamFromA2LAction getImportRuleSetParamFromA2LAction() {
    return this.importRuleSetParamFromA2LAction;
  }


  /**
   * @param importRuleSetParamFromA2LAction the importRuleSetParamFromA2LAction to set
   */
  public void setImportRuleSetParamFromA2LAction(
      final ImportRuleSetParamFromA2LAction importRuleSetParamFromA2LAction) {
    this.importRuleSetParamFromA2LAction = importRuleSetParamFromA2LAction;
  }

}
