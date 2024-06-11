/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewRuleDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleProviderResolver;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.dialogs.ProjectRulesDialog;
import com.bosch.caltool.icdm.ruleseditor.dialogs.ShapeCheckResDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.pages.CDRParameterEditSection;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.views.ProjRulesListViewPart;
import com.bosch.caltool.icdm.ruleseditor.views.RulesHistoryViewPart;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardData;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * ICDM-521 - Code Refactor New Action Set for the Review Params
 *
 * @author rgo7cob
 */
public class ReviewRuleActionSet<D extends IParameterAttribute, P extends IParameter> {

  /**
   *
   */
  private static final String REVIEW_RULES_EDITOR = "Review rules editor";
  /**
   * list size 1
   */
  private static final int SIZE_ONE = 1;
  /**
   * Icdm-1056 Constant for update of Review Rule complete
   */
  private static final String COMPLETED = " completed !";
  /**
   * Icdm-1056 Constant for update of Review Rule failed
   */
  private static final String FAILED = " failed ! ";

  /**
   * Icdm-1056 Constant for insert of Review Rule
   */
  private static final String CREATE = "CREATE Review Rule for the Parameter ";

  private static final int EDIT_RULE_DIA_WIDTH = 1000;
  private static final int EDIT_RULE_DIA_HEIGHT = 900;

  /**
   * Ssd success message Code
   */
  private static final int SSD_SUCCESS_CODE = 0;
  private ParameterDataProvider<D, P> parameterDataProvider;


  /**
   * @param paramCollDataProvider
   * @param parameterDataProvider
   */
  public ReviewRuleActionSet(final ParameterDataProvider<D, P> parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }


  /**
   *
   */
  public ReviewRuleActionSet() {

  }


  /**
   * {@inheritDoc}
   */
  public void addShowRuleHistory(final MenuManager menuMgr, final ParamCollection paramCollection,
      final Object firstElement) {
    final ReviewRule rule;
    rule = getRule(firstElement);
    final Action ruleHistoryAction = new Action() {

      @Override
      public void run() {
        if (firstElement instanceof IParameter) {
          IParameter cdrFuncParameter = (IParameter) firstElement;
          updateRuleHistoryViewUI(paramCollection, cdrFuncParameter, null, false, false, REVIEW_RULES_EDITOR);
        }
        // ICDM-1086
        else if ((firstElement instanceof ReviewRule) || (firstElement instanceof DefaultRuleDefinition)) {
          if ((rule.getRuleId() == null) || (rule.getRevId() == null)) {
            CDMLogger.getInstance().infoDialog(
                "Rule details cannot be retrieved as sufficient information is not available in iCDM",
                Activator.PLUGIN_ID);
          }
          else {
            updateRuleHistoryViewUI(paramCollection, null, rule, false, false, REVIEW_RULES_EDITOR);
          }
        }

      }
    };
    ruleHistoryAction.setText(IMessageConstants.OPEN_RULE_HISTORY);
    ruleHistoryAction.setEnabled(null != rule);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HISTORY_VIEW_16X16);
    ruleHistoryAction.setImageDescriptor(imageDesc);
    menuMgr.add(ruleHistoryAction);

  }


  /**
   * Menu to Show Rule History section
   *
   * @param menuMgr menu mgr
   * @param paramCol rule mgr
   * @param cdrResultParameter result param
   * @param menuText menu
   * @param showCompRuleHist isCompliHistory flag
   */
  public void showRuleHistoryFromResult(final MenuManager menuMgr, final ParamCollection paramCol,
      final com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter, final String menuText,
      final boolean showCompRuleHist, final boolean showQssdRuleHist) {

    final Action ruleHistoryAction = new Action() {

      @Override
      public void run() {
        getRuleHistory(paramCol, cdrResultParameter, showCompRuleHist, showQssdRuleHist);

      }
    };
    ruleHistoryAction.setText(menuText);
    ruleHistoryAction.setEnabled(true);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HISTORY_VIEW_16X16);
    ruleHistoryAction.setImageDescriptor(imageDesc);
    menuMgr.add(ruleHistoryAction);

  }


  /**
   * @param firstElement selected object
   * @param rule CDRRule
   * @return
   */
  private ReviewRule getRule(final Object firstElement) {
    ReviewRule rule = null;
    if (firstElement instanceof IParameter) {
      IParameter param = (IParameter) firstElement;
      rule = this.parameterDataProvider.getReviewRule(param);
    }
    else if (firstElement instanceof DefaultRuleDefinition) {
      rule = ((DefaultRuleDefinition) firstElement).getReviewRule();
    }
    else if (firstElement instanceof ReviewRule) {
      rule = (ReviewRule) firstElement;
    }
    return rule;
  }

  /**
   * updates the rules history view
   *
   * @param paramCollection Param collection
   * @param cdrFuncParameter CDRFuncParameter instance
   * @param rule rule
   * @param showCompRuleHist isCompli history flag
   * @param showQssdRuleHist
   * @param sourceName editor Name
   */
  protected void updateRuleHistoryViewUI(final ParamCollection paramCollection, final IParameter cdrFuncParameter,
      final ReviewRule rule, boolean showCompRuleHist, final boolean showQssdRuleHist, final String sourceName) {
    String paramName = "";
    String complirule = "";
    ReviewRule cdrRule = rule;
    // ICDM-1086
    if (cdrFuncParameter == null) {
      paramName = cdrRule.getParameterName();

    }
    else if (rule == null) {
      paramName = cdrFuncParameter.getName();
      cdrRule = this.parameterDataProvider.getReviewRule(cdrFuncParameter);
    }

    try {
      final RulesHistoryViewPart ruleHstryVwPrt = (RulesHistoryViewPart) PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage().showView(CommonUIConstants.RULE_HISTORY_VIEW_ID);
      if (showCompRuleHist) {
        complirule = " - Compliance Rule";
      }
      if (showQssdRuleHist) {
        complirule = " - Qssd Rule";
        showCompRuleHist = true;
      }
      if (!ruleHstryVwPrt.getScrolledForm().isDisposed()) {
        // Set text to srolled form
        ruleHstryVwPrt.getScrolledForm().setText("PAR: " + paramName + complirule + "\nSource : " + sourceName);
      }
      ruleHstryVwPrt.resetUIControls();
      ruleHstryVwPrt.fillRuleUISection(paramCollection, cdrRule, showCompRuleHist);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }


  /**
   * {@inheritDoc}
   *
   * @param parameterDataProvider2
   * @param paramColDataProvider
   */
  public void editRuleForParam(final MenuManager menuMgr, final Object firstElement, final ParamCollection cdrFunction,
      final ReviewParamEditor editor, final CalData checkValCalDataObj, final boolean readOnlyMode,
      final String pidcVersName, final String resultName, final ParamCollectionDataProvider paramColDataProvider,
      final ParameterDataProvider parameterDataProvider2) {
    final Action editRuleAction = new Action() {

      @Override
      public void run() {
        openEditRuleDialog(firstElement, cdrFunction, editor, checkValCalDataObj, readOnlyMode, pidcVersName,
            resultName, null, paramColDataProvider, parameterDataProvider2);
      }


    };
    editRuleAction.setEnabled(true);
    // ICDM-1190
    if (readOnlyMode) {
      editRuleAction.setText("Show rule details");
    }
    else {
      editRuleAction.setText("Show/Edit rule details");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    editRuleAction.setImageDescriptor(imageDesc);
    menuMgr.add(editRuleAction);

  }


  /**
   * /** {@inheritDoc}
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
   * {@inheritDoc}
   *
   * @param parameterDataProvider2
   * @param paramColDataProvider
   */
  public void openEditRuleDialog(final Object firstElement, final ParamCollection cdrFunction,
      final ReviewParamEditor editor, final CalData checkValCalDataObj, final boolean readOnlyMode,
      final String pidcVersName, final String resultName, final SortedSet<AttributeValueModel> attrValModel,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider parameterDataProvider2) {
    // ICDM-1162
    com.bosch.caltool.icdm.model.cdr.IParameter param = null;
    DefaultRuleDefinition ruleDef = null;
    if (firstElement instanceof DefaultRuleDefinition) {
      ruleDef = (DefaultRuleDefinition) firstElement;
      param = (IParameter) parameterDataProvider2.getParamRulesOutput().getParamMap()
          .get(ruleDef.getReviewRule().getParameterName());
    }
    else if (firstElement instanceof com.bosch.caltool.icdm.model.cdr.IParameter) {
      param = (com.bosch.caltool.icdm.model.cdr.IParameter) firstElement;
    }
    else if (firstElement instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) firstElement;
      param = (IParameter) parameterDataProvider2.getParamRulesOutput().getParamMap().get(rule.getParameterName());
    }

    Map<IParameter, Set<RuleInfoSection>> ruleInfoSectionMap;

    ruleInfoSectionMap = editor.getRuleInfoSectionMap();
    Set<RuleInfoSection> ruleInfoSectionset = ruleInfoSectionMap.get(param);

    boolean dialogAlreadyOpen;
    EditRuleDialog editRuleDialog = null;

    dialogAlreadyOpen = checkEditDialogAlreadyOpen(ruleInfoSectionset);


    if (!dialogAlreadyOpen) {
      // ICDM-1162
      // New Dialog to edit the Rule
      if ((firstElement instanceof DefaultRuleDefinition) && (ruleDef != null)) {
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
            editor.getListPage(), ruleDef.getReviewRule(), readOnlyMode, parameterDataProvider2, paramColDataProvider);
      }
      else if (firstElement instanceof IParameter) {
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
            editor.getListPage(), checkValCalDataObj, readOnlyMode, pidcVersName, resultName, attrValModel,
            parameterDataProvider2, paramColDataProvider);
      }
      else if (firstElement instanceof ReviewRule) {
        ReviewRule rule = (ReviewRule) firstElement;
        Object selectedPage = editor.getSelectedPage();
        editRuleDialog = openEditDlgForRule(cdrFunction, editor, checkValCalDataObj, readOnlyMode, pidcVersName,
            resultName, param, rule, selectedPage, paramColDataProvider, parameterDataProvider2);
      }

      if (null != editRuleDialog) {
        editRuleDialog.create();
        addEditRuleInfoSectToMap(editor, param, ruleInfoSectionset, editRuleDialog);
        editRuleDialog.open();
      }

    }

  }


  /**
   * @param cdrFunction
   * @param editor
   * @param checkValCalDataObj
   * @param readOnlyMode
   * @param pidcVersName
   * @param resultName
   * @param param
   * @param rule
   * @param selectedPage
   * @return
   */
  private EditRuleDialog openEditDlgForRule(final ParamCollection cdrFunction, final ReviewParamEditor editor,
      final CalData checkValCalDataObj, final boolean readOnlyMode, final String pidcVersName, final String resultName,
      final IParameter param, final ReviewRule rule, final Object selectedPage,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider parameterDataProvider2) {
    EditRuleDialog editRuleDialog;
    if (selectedPage instanceof ListPage) {
      if (checkValCalDataObj != null) {
        editRuleDialog =
            new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction, editor.getListPage(), rule,
                checkValCalDataObj, pidcVersName, resultName, parameterDataProvider2, paramColDataProvider);
      }
      else {
        // if the dialog is initiated from list page
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
            editor.getListPage(), rule, readOnlyMode, parameterDataProvider2, paramColDataProvider);
      }
    }
    else {
      // if the dialog is initiated from parameter rules page
      editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
          editor.getParamRulesPage(), rule, readOnlyMode, paramColDataProvider, parameterDataProvider2);
    }
    return editRuleDialog;
  }

  /**
   * {@inheritDoc}
   */
  public boolean checkEditDialogAlreadyOpen(final Set<RuleInfoSection> ruleInfoSectionset) {


    boolean dialogAlreadyOpen = false;
    EditRuleDialog editRuleDialog = null;

    if (CommonUtils.isNotNull(ruleInfoSectionset) && !ruleInfoSectionset.isEmpty()) {

      for (RuleInfoSection ruleInfoSect : ruleInfoSectionset) {
        editRuleDialog = ruleInfoSect.getEditRuleDialog();
        if (editRuleDialog != null) {
          dialogAlreadyOpen = true;
          break;
        }
      }
    }
    if (dialogAlreadyOpen) {
      // Icdm-1057 Non Modal Dialog
      if (editRuleDialog.getShell().getMinimized()) {
        editRuleDialog.getShell().setMaximized(true);
        editRuleDialog.getShell().setSize(EDIT_RULE_DIA_WIDTH, EDIT_RULE_DIA_HEIGHT);
      }
    }
    return dialogAlreadyOpen;
  }

  /**
   * {@inheritDoc}
   */
  public void addEditRuleInfoSectToMap(final ReviewParamEditor editor, final IParameter param,
      final Set<RuleInfoSection> ruleInfoSectset, final EditRuleDialog editRuleDialog) {
    Set<RuleInfoSection> ruleInfoSectionset = ruleInfoSectset;
    if (CommonUtils.isNull(ruleInfoSectionset)) {
      ruleInfoSectionset = new HashSet<>();
    }
    RuleInfoSection ruleInfoSection = editRuleDialog.getRuleInfoSection();
    // if a dialog/wizard for the same parameter is opened, then disable the parameter properties fields
    if (!ruleInfoSectionset.isEmpty()) {
      ruleInfoSection.getParamEditSection().enableFields(false);
    }
    ruleInfoSectionset.add(ruleInfoSection);
    editor.getRuleInfoSectionMap().put(param, ruleInfoSectionset);
  }

  /**
   * {@inheritDoc}
   *
   * @param ruleInfoSection2
   */
  public boolean insertReviewRule(final AddNewConfigWizardData wizardData, final RuleInfoSection ruleInfoSection) {
    // ICDM-1346
    List<ReviewRule> rulesList = new ArrayList<ReviewRule>();
    try {
      Collection values = wizardData.getAttrValModels().values();
      for (Object attrValModelObj : values) {
        Set<AttributeValueModel> attrValModel = (Set<AttributeValueModel>) attrValModelObj;
        final ReviewRule oldCdrRule = wizardData.getCdrRule();
        final ReviewRule newRviewRule = new ReviewRule();
        newRviewRule.setParameterName(oldCdrRule.getParameterName());
        newRviewRule.setValueType(oldCdrRule.getValueType());
        newRviewRule.setLowerLimit(oldCdrRule.getLowerLimit());
        newRviewRule.setUpperLimit(oldCdrRule.getUpperLimit());

        // ICDM-1253

        if (newRviewRule.getValueType().equals(ParameterType.VALUE.getText()) &&
            CommonUtils.isNull(oldCdrRule.getRefValueCalData())) {
          newRviewRule.setRefValue(oldCdrRule.getRefValue());
        }
        else {
          newRviewRule.setRefValueDcmString(oldCdrRule.getRefValueDcmString());
        }


        newRviewRule.setHint(oldCdrRule.getHint());
        newRviewRule.setLabelFunction(oldCdrRule.getLabelFunction());

        newRviewRule.setUnit(oldCdrRule.getUnit());
        newRviewRule.setReviewMethod(oldCdrRule.getReviewMethod());

        // ICDM 597
        newRviewRule.setDcm2ssd(oldCdrRule.isDcm2ssd());
        // ICDM-1081
        newRviewRule.setDependencyList(new TreeSet<>(attrValModel));
        newRviewRule.setMaturityLevel(oldCdrRule.getMaturityLevel());
        rulesList.add(newRviewRule);
      }

      ReviewRuleDataProvider dataProvider = new RuleProviderResolver().getRuleProvider(wizardData.getCdrFunction());
      ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
      parmCol.setParamCollection(ruleInfoSection.getCdrFunction());
      parmCol.setReviewRuleList(rulesList);
      SSDMessageWrapper ssdCode = dataProvider.createCdrRule(parmCol);
      // ICDM-1346
      if (ssdCode.getCode() == SSD_SUCCESS_CODE) {

        CDMLogger.getInstance().info(CREATE + wizardData.getCdrParameter().getName() + COMPLETED, Activator.PLUGIN_ID);

        return true;
      }
      CDMLogger.getInstance().errorDialog(
          CREATE + wizardData.getCdrParameter().getName() + FAILED + ssdCode.getDescription(), Activator.PLUGIN_ID);
    }
    catch (Exception exp) {
      CDMLogger.getInstance().errorDialog(CREATE + wizardData.getCdrParameter().getName() + FAILED + exp.getMessage(),
          exp, Activator.PLUGIN_ID);
      return false;
    }

    return false;
  }


  /**
   * {@inheritDoc}
   */
  public Action createEditRuleAction(final List<ReviewRule> selectedRules, final IParameter selectedParam,
      final ParamCollection cdrFunction, final AbstractFormPage page, final boolean readOnlyMode,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider<D, P> parameterDataProvider) {
    Action editRuleAction = new Action() {

      @Override
      public void run() {
        openEditRuleDialog(selectedRules, selectedParam, cdrFunction, page, readOnlyMode, paramColDataProvider,
            parameterDataProvider);
      }


    };
    // iCDM-902
    editRuleAction.setEnabled(true);
    // ICDM-1190
    if (readOnlyMode) {
      editRuleAction.setText("Show rule details");
    }
    else {
      editRuleAction.setText("Show/Edit rule details");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    editRuleAction.setImageDescriptor(imageDesc);
    return editRuleAction;
  }

  /**
   * {@inheritDoc}
   *
   * @param parameterDataProvider2
   * @param paramColDataProvider
   */
  @SuppressWarnings("javadoc")
  public void openEditRuleDialog(final List<ReviewRule> selectedRules, final IParameter selectedParam,
      final ParamCollection cdrFunction, final AbstractFormPage page, final boolean readOnlyMode,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider<D, P> parameterDataProvider) {
    StringBuilder attrDefIncomplete = new StringBuilder("false");
    Set<IParameterAttribute> depnAttrSet = new HashSet<>();
    Set<AttributeValueModel> attrValSet = new HashSet<>();
    boolean editPossible = true;
    boolean dialogAlreadyOpen;
    AddNewConfigWizardDialog dialog;
    int sizeOfSelectedRules = selectedRules.size();
    if (sizeOfSelectedRules > SIZE_ONE) {
      // check if multiple edit is possible
      editPossible = multipleEdit(selectedRules, attrDefIncomplete, selectedParam, depnAttrSet, attrValSet);
    }
    else {
      singleEdit(selectedRules, attrDefIncomplete, selectedParam, depnAttrSet, attrValSet, parameterDataProvider);
    }
    if (editPossible) {
      // ICDM-1244
      if (page.getEditor() instanceof ReviewParamEditor) {

        ReviewParamEditor editor = (ReviewParamEditor) page.getEditor();
        Set<RuleInfoSection> ruleInfoSet = editor.getRuleInfoSectionMap().get(selectedParam);

        dialogAlreadyOpen = checkWizardDialogAlreadyOpen(ruleInfoSet, selectedRules);

        if (!dialogAlreadyOpen) {
          // need not create wizard in case of errorocheck
          AddNewConfigWizard wizard = new AddNewConfigWizard(selectedParam, cdrFunction, page, true, selectedRules,
              false, Boolean.valueOf(attrDefIncomplete.toString()), depnAttrSet, attrValSet, readOnlyMode, false,
              editor.getEditorInput().getParamDataProvider(), paramColDataProvider);
          dialog =
              new AddNewConfigWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);

          dialog.create();
          dialog.open();

          addToWizardRuleInfoSectionMap(selectedParam, dialog, editor, ruleInfoSet);


        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addToWizardRuleInfoSectionMap(final IParameter selectedParam, final AddNewConfigWizardDialog dialog,
      final ReviewParamEditor editor, final Set<RuleInfoSection> ruleInfoSectionSet) {
    Set<RuleInfoSection> ruleInfoSet = ruleInfoSectionSet;
    // icdm-1244
    if (CommonUtils.isNull(ruleInfoSet)) {
      ruleInfoSet = new HashSet<>();
    }
    RuleInfoSection ruleInfoSection = dialog.getWizard().getRuleInfoSection();
    // if a dialog/wizard for the same parameter is opened, then disable the parameter properties fields
    if (!ruleInfoSet.isEmpty()) {
      ruleInfoSection.getParamEditSection().enableFields(false);
    }
    ruleInfoSet.add(ruleInfoSection);
    editor.getRuleInfoSectionMap().put(selectedParam, ruleInfoSet);
  }

  /**
   * {@inheritDoc}
   */
  public boolean checkWizardDialogAlreadyOpen(final Set<RuleInfoSection> ruleInfoSet,
      final List<ReviewRule> selectedRules) {
    boolean dialogAlreadyOpen = false;
    AddNewConfigWizardDialog dialog = null;
    if ((ruleInfoSet != null) && !ruleInfoSet.isEmpty()) {
      // iterate through the set to get the ruleinfosection with the same selected rules
      for (RuleInfoSection ruleInfoSect : ruleInfoSet) {
        if (ruleInfoSect.getWizard() != null) {
          List<ReviewRule> cdrRulesList = ruleInfoSect.getWizard().getWizardData().getCdrRulesList();
          dialogAlreadyOpen = (selectedRules != null) && (cdrRulesList != null) &&
              selectedRules.containsAll(cdrRulesList) && (selectedRules.size() == cdrRulesList.size());
          if (dialogAlreadyOpen) {
            dialog = ruleInfoSect.getWizard().getWizardDialog();
            break;
          }
        }
      }
    }

    maximiseExistingDialog(dialogAlreadyOpen, dialog);
    return dialogAlreadyOpen;
  }

  /**
   * @param dialogAlreadyOpen boolean
   * @param dialog AddNewConfigWizardDialog
   */
  private void maximiseExistingDialog(final boolean dialogAlreadyOpen, final AddNewConfigWizardDialog dialog) {
    if (dialogAlreadyOpen) {
      // if the dialog is already open , show the existing dialog
      if ((dialog != null) && dialog.getShell().getMinimized()) {
        dialog.getShell().setMaximized(true);
        dialog.getShell().setSize(EDIT_RULE_DIA_WIDTH, EDIT_RULE_DIA_HEIGHT);
      }
    }
  }

  /**
   * @param selectedRules
   * @param attrDefIncomplete
   * @param selectedParam
   * @param depnAttrSet
   * @param attrValSet
   * @param parameterDataProvider2
   */
  private void singleEdit(final List<ReviewRule> selectedRules, final StringBuilder attrDefIncomplete,
      final IParameter selectedParam, final Set<IParameterAttribute> depnAttrSet,
      final Set<AttributeValueModel> attrValSet, final ParameterDataProvider<D, P> parameterDataProvider) {

    Set<AttributeValueModel> dependencyList = new HashSet<AttributeValueModel>();
    dependencyList.addAll(selectedRules.get(0).getDependencyList());
    // ICDM-1199
    List<D> paramAttrs = parameterDataProvider.getParamAttrs(selectedParam);
    if ((paramAttrs != null) && (dependencyList.size() < paramAttrs.size())) {
      attrDefIncomplete.setLength(0);
      attrDefIncomplete.append("true");
      Set<Attribute> definedAttrSet = new HashSet<Attribute>();
      for (AttributeValueModel attrVal : dependencyList) {
        // getting the set of attributes defined
        definedAttrSet.add(attrVal.getAttr());
      }
      // get all the dependecy attributes
      for (IParameterAttribute paramAttr : paramAttrs) {
        depnAttrSet.add(paramAttr);
      }
      // remove the defined attributes
      depnAttrSet.removeAll(definedAttrSet);
      // add the attr val mapping to the wizard data
      attrValSet.addAll(dependencyList);
    }


  }

  /**
   * @param selectedRules
   * @param attrDefIncomplete
   * @param selectedParam
   */
  private boolean multipleEdit(final List<ReviewRule> selectedRules, final StringBuilder attrDefIncomplete,
      final IParameter selectedParam, final Set<IParameterAttribute> depnAttrSet,
      final Set<AttributeValueModel> attrValSet) {
    Set<AttributeValueModel> dependencyList = new TreeSet<>();
    dependencyList.addAll(selectedRules.get(0).getDependencyList());

    // ICDM-1199

    Set<Attribute> definedAttrSet = new HashSet<Attribute>();
    boolean editPossible = checkMultipleUpdatePossible(selectedRules, definedAttrSet, dependencyList);
    if (editPossible) {
      if (dependencyList.size() < this.parameterDataProvider.getParamAttrs(selectedParam).size()) {
        attrDefIncomplete.setLength(0);
        attrDefIncomplete.append("true");
      }
      // get all the dependecy attributes
      for (IParameterAttribute paramAttr : this.parameterDataProvider.getParamAttrs(selectedParam)) {

        depnAttrSet.add(paramAttr);
      }
      // remove the defined attributes
      depnAttrSet.removeAll(definedAttrSet);
      // add the attr val mapping to the wizard data
      attrValSet.addAll(dependencyList);

      return true;
    }
    return false;
  }

  /**
   * ICDM-1199 check if all the selected rules have same number of attributes to be defined
   *
   * @param selectedRules list of selected rules
   * @param definedAttrSet set of defined attributes
   * @param attrValueModel collection of attr value model
   */
  private boolean checkMultipleUpdatePossible(final List<ReviewRule> selectedRules, final Set<Attribute> definedAttrSet,
      final Collection<AttributeValueModel> attrValueModel) {

    for (AttributeValueModel attrVal : attrValueModel) {
      // getting the set of attributes defined
      definedAttrSet.add(attrVal.getAttr());
    }
    for (ReviewRule rule : selectedRules) {
      if (selectedRules.indexOf(rule) == 0) {
        continue;
      }
      if (!CommonUtils.isEqual(rule.getDependencyList().size(), definedAttrSet.size())) {
        CDMLogger.getInstance().errorDialog(
            "There is a different number of attribute values which have additionally to be defined for the selected rules!",
            Activator.PLUGIN_ID);
        return false;
      }
    }
    return true;
  }


  /**
   * @param projRulesListViewPart
   * @return
   */
  public IAction projRulesAddAction(final ProjRulesListViewPart projRulesListViewPart) {
    IAction addAction = new Action("Add") {

      @Override
      public void run() {
        ProjectRulesDialog addCpAction = new ProjectRulesDialog(Display.getCurrent().getActiveShell(), true);
        addCpAction.open();

      }
    };
    // Set image for add action
    addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    return addAction;
  }


  /**
   * @param projRulesListViewPart
   * @return
   */
  public IAction projRulesEditAction(final ProjRulesListViewPart projRulesListViewPart) {
    IAction editAction = new Action("Edit") {

      @Override
      public void run() {

        ProjectRulesDialog addCpAction = new ProjectRulesDialog(Display.getCurrent().getActiveShell(), false);
        addCpAction.open();

      }
    };
    // Set image for edit action
    editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    editAction.setEnabled(false);

    return editAction;
  }


  /**
   * @return
   */
  public IAction projRulesDelAction() {
    IAction deleteAction = new Action("Delete") {

      @Override
      public void run() {
        // TO-DO
      }
    };
    // Set image for delete action
    deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    deleteAction.setEnabled(false);
    return deleteAction;
  }


  /**
   * Get Rule History section
   *
   * @param cdrRule
   * @param showCompRuleHist
   * @param editorName
   * @param cdrReportDataHandler
   * @param funtionName
   */
  protected void getRuleHistory(final ReviewRule rule, final boolean showCompRuleHist, final boolean showQssdRuleHist,
      final String editorName, final CdrReportDataHandler cdrReportDataHandler, final String funtionName) {
    if ((rule == null) || (rule.getRuleId() == null) || (rule.getRevId() == null)) {
      CDMLogger.getInstance().infoDialog(
          "Rule history cannot be retrieved as sufficient information is not available in iCDM", Activator.PLUGIN_ID);
    }
    else {

      CDRReviewResult reviewResult = cdrReportDataHandler.getReviewResult(rule.getParameterName(), 0);

      if ((null != reviewResult) && (null != reviewResult.getRsetId())) {
        try {
          RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
          RuleSet ruleSet = ruleSetServiceClient.get(reviewResult.getRsetId());
          updateRuleHistoryViewUI(ruleSet, null, rule, showCompRuleHist, showQssdRuleHist, editorName);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else {
        FunctionServiceClient functionServiceClient = new FunctionServiceClient();
        try {
          Function function = functionServiceClient.getFunctionsByName(Arrays.asList(funtionName)).get(funtionName);
          updateRuleHistoryViewUI(function, null, rule, showCompRuleHist, showQssdRuleHist, editorName);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }

    }
  }

  /**
   * @param paramCollection
   * @param cdrResultParameter
   * @param showCompRuleHist
   * @param showQssdRuleHist
   */
  private void getRuleHistory(final ParamCollection paramCollection,
      final com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter, final boolean showCompRuleHist,
      final boolean showQssdRuleHist) {
    ReviewRule rule = new ReviewRule();
    rule.setParameterName(cdrResultParameter.getName());
    BigDecimal labObjId = null;
    BigDecimal revId = null;
    if (showCompRuleHist) {
      if (null != cdrResultParameter.getCompliLabObjId()) {
        labObjId = cdrResultParameter.getCompliLabObjId();
      }
      if (null != cdrResultParameter.getCompliRevId()) {
        revId = cdrResultParameter.getCompliRevId();
      }
    }
    else if (showQssdRuleHist) {
      if (null != cdrResultParameter.getQssdLabObjId()) {
        labObjId = cdrResultParameter.getQssdLabObjId();
      }
      if (null != cdrResultParameter.getQssdRevId()) {
        revId = cdrResultParameter.getQssdRevId();
      }
    }
    else {
      if (null != cdrResultParameter.getLabObjId()) {
        labObjId = cdrResultParameter.getLabObjId();
      }
      if (null != cdrResultParameter.getRevId()) {
        revId = cdrResultParameter.getRevId();
      }
    }
    rule.setRuleId(labObjId);
    rule.setRevId(revId);
    if ((rule.getRuleId() == null) || (rule.getRevId() == null)) {
      CDMLogger.getInstance().infoDialog(
          "Rule history cannot be retrieved as sufficient information is not available in iCDM", Activator.PLUGIN_ID);
    }
    else {
      updateRuleHistoryViewUI(paramCollection, null, rule, showCompRuleHist, showQssdRuleHist, "Review result editor");
    }
  }


  /**
   * Menu to show shape check result
   *
   * @param menuManagr
   * @param ruleManager
   * @param cdrResultParameter
   * @param openCompliRuleHistory
   */
  public void showShapeCheckResult(final MenuManager menuManagr,
      final com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter, final String menuText) {

    final Action shapeCheckAction = new Action() {

      @Override
      public void run() {
        ShapeCheckResDialog srResultDialog =
            new ShapeCheckResDialog(Display.getDefault().getActiveShell(), cdrResultParameter);
        srResultDialog.open();
      }
    };
    shapeCheckAction.setText(menuText);
    shapeCheckAction.setEnabled(true);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SHAPE_CHECK_16X16);
    shapeCheckAction.setImageDescriptor(imageDesc);
    menuManagr.add(shapeCheckAction);

  }

  /**
   * Menu to Show Rule History section
   *
   * @param menuManagr menu mananger
   * @param menuText menu text
   * @param cdrRule CDRRule instance
   * @param showCompRuleHist isCompli flag
   * @param cdrReportDataHandler CdrReportDataHandler
   * @param funtionName Function Name
   */
  public void showRuleHistory(final MenuManager menuManagr, final String menuText, final String editorName,
      final ReviewRule cdrRule, final boolean showCompRuleHist, final boolean showQssdRuleHist,
      final CdrReportDataHandler cdrReportDataHandler, final String funtionName) {
    final Action ruleHistoryAction = new Action() {

      @Override
      public void run() {
        getRuleHistory(cdrRule, showCompRuleHist, showQssdRuleHist, editorName, cdrReportDataHandler, funtionName);

      }
    };
    ruleHistoryAction.setText(menuText);
    ruleHistoryAction.setEnabled(true);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HISTORY_VIEW_16X16);
    ruleHistoryAction.setImageDescriptor(imageDesc);
    menuManagr.add(ruleHistoryAction);
  }


}
