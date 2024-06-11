/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.IImportRefresher;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.CDRParameterEditSection;
import com.bosch.caltool.icdm.ruleseditor.pages.ConfigBasedRulesPage;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleEditorNodeAccessPage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.icdm.ruleseditor.views.ReviewRuleEditorOutlinePageCreator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Editor for Calibration data review perspective- To set the review rules of parameters
 *
 * @author mkl2cob
 */
public class ReviewParamEditor extends AbstractFormEditor implements IImportRefresher {

  /**
   * Config rules page index
   */
  private static final int CONFIG_RULES_PAGE = 3;
  /**
   * Param rules page index
   */
  private static final int PARAM_RULES_PAGE = 2;
  /**
   * Details page index
   */
  private static final int DETAILS_PAGE = 1;
  /**
   * list page index
   */
  private static final int LIST_PAGE = 0;


  /**
   * Instances of various pages in the editor
   */
  private ListPage listPage;

  /**
   * dtlsPage can be param details page /component details page
   */
  private AbstractFormPage dtlsPage;

  /**
   * DetailsPage instance
   */
  private DetailsPage detailsPage;
  /**
   * ConfigBasedRulesPage instance
   */
  private ConfigBasedRulesPage configPage;
  /**
   * ParametersRulePage instance
   */
  private ParametersRulePage paramRulesPage;

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();
  /**
   * SelectionProviderMediator instance
   */
  private SelectionProviderMediator selProvdrMediator = new SelectionProviderMediator();
  /**
   * Icdm-1057- new Map with the FuncParam as Key and Dialog as value
   */
  private final Map<IParameter, Set<RuleInfoSection>> ruleInfoSectionMap = new HashMap<>();

  /**
   * ReviewRulesOutlinePageCreator
   */
  private ReviewRuleEditorOutlinePageCreator outlinePageCreator;


  /**
   * {@inheritDoc} Add pages to the editor
   */
  @Override
  protected void addPages() {
    this.listPage = new ListPage(this, getEditorInput().getSelectedObject());
    this.configPage = new ConfigBasedRulesPage(this, getEditorInput().getSelectedObject());
    RuleEditorNodeAccessPage nodeAccessRightsPage = new RuleEditorNodeAccessPage(this);
    this.paramRulesPage = new ParametersRulePage(this, getEditorInput().getSelectedObject());
    try {
      addPage(this.listPage);
      ReviewParamEditorInput editorInput = getEditorInput();

      // check for customization before adding the second page
      if (editorInput.getCustomEditorObj() != null) {
        editorInput.getCustomEditorObj().setEditor(this);
        List<AbstractFormPage> pagesList = editorInput.getCustomEditorObj().getPages();
        this.dtlsPage = pagesList.get(0);
        addPage(this.dtlsPage);
      }
      else {
        this.detailsPage =
            new DetailsPage(this, "Function.label", "Parameter Details", getEditorInput().getSelectedObject());
        addPage(this.detailsPage);
        this.dtlsPage = this.detailsPage;
      }
      addPage(this.paramRulesPage);
      addPage(this.configPage);
      addPage(nodeAccessRightsPage);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc} save
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
    // This saves the changes in the details page
    if (this.detailsPage.getToolBarActionSet().saveReviewDataChanges()) {
      this.detailsPage.setUnSavedDataPresent(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {

    if (input instanceof ReviewParamEditorInput) {
      setPartName(input.getName());
      super.init(site, input);
    }
    else {
      CDMLogger.getInstance().errorDialog("Invalid input type for the review rules editor", Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewRuleEditorOutlinePageCreator getOutlinePageCreator() {

    if (this.outlinePageCreator == null) {
      // ICDM-2273


      String a2lFileId = PlatformUI.getPreferenceStore()
          .getString(RuleEditorConstants.RULE_OUTLINE_A2L + getEditorInput().getSelectedObject().getId());

      String[] split;
      if (CommonUtils.isNotEmptyString(a2lFileId)) {
        // if there is an a2l file id stored in the preference store
        split = a2lFileId.split(RuleEditorConstants.DELIMITER_MULTIPLE_ID, 3);
        A2LFile a2lFile = null;
        if (split.length == 3) {
          boolean confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Loading A2L file in Oultine view",
              "Should the A2L file " + split[2] + " be loaded in outline view?");

          loadA2l(split, a2lFile, confirm);
        }
        this.outlinePageCreator = new ReviewRuleEditorOutlinePageCreator(getEditorInput(), this);
        try {
          this.outlinePageCreator.createPage();
          this.outlinePageCreator.getReviewRulesOutlinePage().loadData();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else {
        this.outlinePageCreator = new ReviewRuleEditorOutlinePageCreator(getEditorInput(), this);
      }
    }
    return this.outlinePageCreator;
  }

  /**
   * @param split
   * @param a2lFile
   * @param confirm
   */
  private void loadA2l(String[] split, A2LFile a2lFile, boolean confirm) {
    if (confirm) {
      // only if the user wants to load the A2l file in outline view
      try {
        A2LEditorDataProvider a2LEditorDataProvider = new A2LEditorDataProvider(Long.parseLong(split[0]), true);
        getEditorInput().setA2LDataProvider(a2LEditorDataProvider);
        a2lFile = getEditorInput().getA2lDataProvider().getPidcA2LBO().getA2lFile();
      }
      catch (NumberFormatException | IcdmException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp);
      }
      getEditorInput().setA2lFile(a2lFile, Long.parseLong(split[0]));
    }
    else {
      getEditorInput().setA2lFile(null, Long.parseLong(split[0]));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewParamEditorInput getEditorInput() {
    return (ReviewParamEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();

    setStatusMessage();
    IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart");
    if ((null != viewPartObj) && (viewPartObj instanceof OutlineViewPart)) {
      ((OutlineViewPart) viewPartObj).setTitleTooltip("Filter Base Components, Functions, Work packages in A2L Editor");
    }
    // ICDM-796
    // ICDM-865
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
  }

  /**
   * Status of the focussed page is set
   */
  public void setStatusMessage() {

    if (getActivePage() == LIST_PAGE) {
      this.listPage.getParamTabSec().getParamTab().setStatusBarMessage((this.listPage).getGroupByHeaderLayer(), false);
    }
    else if (getActivePage() == DETAILS_PAGE) {
      this.detailsPage.setStatusBarMessage(this.detailsPage.getFcTableViewer());
    }
    else if (getActivePage() == PARAM_RULES_PAGE) {
      this.paramRulesPage.setStatusBarMessage(this.paramRulesPage.getFcTableViewer());
    }
    else if (getActivePage() == CONFIG_RULES_PAGE) {
      this.configPage.setStatusBarMessage(this.configPage.getParamTableViewer());
    }
    else {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setErrorMessage(null);
      statusLine.setMessage("");
    }
  }

  /**
   * @param outlineSelction
   * @param totalItemCount
   * @param filteredItemCount
   */
  public void updateStatusBar(final boolean outlineSelction, final int totalItemCount, final int filteredItemCount) {
    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine = null;
    // Updation of status based on selection in view part

    if (outlineSelction) {
      IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart");
      if (null != viewPart) {
        final IViewSite viewPartSite = (IViewSite) viewPart.getSite();
        statusLine = viewPartSite.getActionBars().getStatusLineManager();
      }
    }
    else {
      statusLine = getEditorSite().getActionBars().getStatusLineManager();
    }
    if (null != statusLine) {
      if (totalItemCount == filteredItemCount) {
        statusLine.setErrorMessage(null);
        statusLine.setMessage(buf.toString());
      }
      else {
        statusLine.setErrorMessage(buf.toString());
      }
      statusLine.update(true);
    }
  }

  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selProvdrMediator;
  }

  /**
   * @param selProvdrMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selProvdrMediator) {
    this.selProvdrMediator = selProvdrMediator;
  }

  /**
   * @return the configPage
   */
  public ConfigBasedRulesPage getConfigPage() {
    return this.configPage;
  }

  /**
   * @return the param Rules page
   */
  public ParametersRulePage getParamRulesPage() {
    return this.paramRulesPage;

  }

  /**
   * @return the ruleInfoSectionMap
   */
  public Map<IParameter, Set<RuleInfoSection>> getRuleInfoSectionMap() {
    return this.ruleInfoSectionMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doImportRefresh() {
    this.listPage.setRefreshNeeded(true);
    this.listPage.getParamTabSec().getParamTab().reconstructNatTable();
    this.listPage.setRowSelection();
    this.detailsPage.setTabViewerInput(false);
    this.paramRulesPage.setTabViewerInput(false);
  }

  /**
   * @return the dtlsPage
   */
  public AbstractFormPage getDtlsPage() {
    return this.dtlsPage;
  }


  /**
   * @return the listPage
   */
  public ListPage getListPage() {
    return this.listPage;
  }

  /**
   * @return the details page
   */
  public DetailsPage getDetailsPage() {
    return this.detailsPage;
  }


  /**
   * {@inheritDoc}
   */
  public void refreshParamPropInOtherDialogs(final CDRParameterEditSection cdrParameterEditSection,
      final ParamCollection cdrFunc, final IParameter cdrFuncParameter, final boolean refreshNeeded,
      final RuleInfoSection ruleInfoSection) {

    Map<IParameter, Set<RuleInfoSection>> ruleInfoMap;
    // getting the rule info section map from editor
    ReviewParamEditor reviewParamEditor = (ReviewParamEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().findEditor(new ReviewParamEditorInput(cdrFunc));
    ruleInfoMap = reviewParamEditor.getRuleInfoSectionMap();
    if ((null != ruleInfoMap) && ruleInfoMap.containsKey(cdrFuncParameter)) {
      Set<RuleInfoSection> ruleInfoSectionSet = ruleInfoMap.get(cdrFuncParameter);
      if (ruleInfoSectionSet.contains(ruleInfoSection)) {// ICDM-1244
        // remove the ruleInfoSection entry from the set in the editor
        ruleInfoSectionSet.remove(ruleInfoSection);
      }
      if (ruleInfoSectionSet.isEmpty()) {
        // remove the set from the map if the set is empty
        ruleInfoMap.remove(cdrFuncParameter);
      }
      else if (cdrParameterEditSection.isEnabled()) {
        enableAndRefreshInOtherDialogs(refreshNeeded, ruleInfoSectionSet);
      }
    }


  }

  /**
   * @param refreshNeeded boolean
   * @param ruleInfoSectionSet Set<RuleInfoSection>
   */
  private void enableAndRefreshInOtherDialogs(final boolean refreshNeeded,
      final Set<RuleInfoSection> ruleInfoSectionSet) {
    Iterator<RuleInfoSection> iterator = ruleInfoSectionSet.iterator();
    RuleInfoSection first = iterator.next();
    // enable fields in the first dialog
    first.getParamEditSection().enableFields(true);
    if (refreshNeeded) {
      // refresh data in all dialogs
      first.getParamEditSection().refreshData();

      while (iterator.hasNext()) {
        RuleInfoSection next = iterator.next();
        next.getParamEditSection().refreshData();
      }
    }
  }

  /**
   * Refresh
   */
  public void refreshSelectedParamRuleData() {
    if (CommonUtils.isNotNull(getEditorInput().getCdrFuncParam())) {
      String paramName = getEditorInput().getCdrFuncParam().getName();
      IParamRuleResponse paramRulesOutput = getEditorInput().getParamDataProvider().getParamRulesOutput();
      IParamRuleResponse singleParamRules = null;
      try {
        if (getEditorInput().getCdrObject() instanceof Function) {
          singleParamRules =
              new ParameterServiceClient().getSingleParamRules(paramName, getEditorInput().getCdrFuncParam().getType());
        }
        else if (getEditorInput().getCdrObject() instanceof RuleSet) {
          singleParamRules = new RuleSetServiceClient().getRules(((RuleSet) getEditorInput().getCdrObject()).getId(),
              (RuleSetParameter) getEditorInput().getCdrFuncParam());
        }
        if ((singleParamRules != null) && (paramName != null)) {
          refreshSingleParamRules(paramName, paramRulesOutput, singleParamRules);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param paramName
   * @param paramRulesOutput
   * @param singleParamRules
   */
  private void refreshSingleParamRules(String paramName, IParamRuleResponse paramRulesOutput,
      IParamRuleResponse singleParamRules) {
    if (null != singleParamRules.getReviewRuleMap().get(paramName)) {
      paramRulesOutput.getReviewRuleMap().put(paramName, singleParamRules.getReviewRuleMap().get(paramName));
    }
    else {
      paramRulesOutput.getReviewRuleMap().remove(paramName);
    }
    if (null != singleParamRules.getAttrMap().get(paramName)) {
      paramRulesOutput.getAttrMap().put(paramName, singleParamRules.getAttrMap().get(paramName));
    }
    paramRulesOutput.getAttrObjMap().putAll(singleParamRules.getAttrObjMap());
    paramRulesOutput.getParamMap().putAll(singleParamRules.getParamMap());
    getEditorInput().setParamRulesOutput(paramRulesOutput);
    getEditorInput().getParamMap().putAll(singleParamRules.getParamMap());
  }
}
