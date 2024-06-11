/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewDetailsData;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRResultParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class OpenRulesEditorAction extends Action {

  /**
   * Functions from A2LEditorInput
   */
  private final SortedSet<Function> functions;
  /**
   * selected element in outline view
   */
  private Object firstElement;
  /**
   * boolean to check whter rules editor is opened from result editor or not
   */
  private final boolean isResultParam;
  // ICDM-1280
  /**
   * boolean to check whter new rule has to be created or not
   */
  private final boolean createRule;

  /**
   * Review rules editor instance
   */
  private ReviewParamEditor reviewRulesEditor;
  private final com.bosch.caltool.icdm.model.cdr.RuleSet ruleSet;

  /**
   * List page num 0
   */
  private static final int LIST_PAGE_NUM = 0;

  /**
   * Detail page num 1
   */
  private static final int DETAIL_PAGE_NUM = 1;
  /**
   * Rules page num 2
   */
  private static final int RULES_PAGE_NUM = 2;

  private ReviewDetail reviewDet;

  ParamCollectionDataProvider dataProvider = new ParamCollectionDataProvider();
  ParameterDataProvider paramDataProvider;
  com.bosch.caltool.icdm.model.a2l.Function function;

  /**
   * Constructor
   *
   * @param functions Functions from A2LEditorInput
   * @param firstElement selected element in outline view
   * @param isResultParam action from result editor
   * @param createRule boolean for create new rule
   * @param ruleSet2 ruleSet
   */
  public OpenRulesEditorAction(final SortedSet<Function> functions, final Object firstElement,
      final boolean isResultParam, final com.bosch.caltool.icdm.model.a2l.Function function, final boolean createRule,
      final com.bosch.caltool.icdm.model.cdr.RuleSet ruleSet2) {
    super();
    this.functions = functions;
    this.firstElement = firstElement;
    this.isResultParam = isResultParam;
    this.createRule = createRule;
    this.ruleSet = ruleSet2;
    this.function = function;
  }


  @Override
  public void run() {
    if (this.isResultParam && !this.createRule) {
      openRulesEditor();
    }
    else if (this.isResultParam) {

      createCheckValRule();
    }
    else if (this.firstElement instanceof ReviewDetail) {
      getResultParamFromReviewData();

      createCheckValRule();
    }
    else {
      opnRulesFrmOutlineView();
    }
  }


  /**
   * @throws ApicWebServiceException
   */
  private void getResultParamFromReviewData() {
    this.reviewDet = (ReviewDetail) this.firstElement;
    String paramName = this.reviewDet.getParamName();
    CDRResultParameterServiceClient cdrResultParameterServiceClient = new CDRResultParameterServiceClient();
    ReviewDetailsData reviewDetailsData = null;

    try {
      reviewDetailsData =
          cdrResultParameterServiceClient.getReviewDetailsDataByResultId(this.reviewDet.getReviewResultId(), paramName);
      if (paramName != null) {
        this.firstElement = reviewDetailsData.getCdrResultParameter();
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   *
   */
  private void createCheckValRule() {
    CDRResultParameter param = (CDRResultParameter) this.firstElement;
    ParamCollection paramCol = null;
    Long ruleSetId = null;
    try {
      if (CommonUtils.isNotNull(this.ruleSet)) {
        paramCol = this.ruleSet;
        ruleSetId = this.ruleSet.getId();
      }
      else {
        FunctionServiceClient client = new FunctionServiceClient();
        List<String> funcNames = new ArrayList<String>();
        funcNames.add(param.getFuncName());
        Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcMap = client.getFunctionsByName(funcNames);
        if (funcMap != null) {
          paramCol = funcMap.get(param.getFuncName());
        }
      }

      if (this.dataProvider.isModifiable(paramCol)) {
        ReviewRuleServiceClient client = new ReviewRuleServiceClient();


        CreateCheckValRuleModel checkValModel = client.createCheclValueRule(param.getId(), ruleSetId);
        if (checkValModel.isCanCreateRule()) {

          if (paramCol instanceof com.bosch.caltool.icdm.model.a2l.Function) {
            this.paramDataProvider = new ParameterDataProvider(checkValModel.getParamRules());
            this.function = (com.bosch.caltool.icdm.model.a2l.Function) paramCol;
            createChkValRuleForFuncParams(param, paramCol, checkValModel);
          }
          else {
            this.paramDataProvider = new ParameterDataProvider(checkValModel.getRulesetRules());
            createChkValRuleForRuleSet(param, checkValModel);
          }
        }
        else if (checkValModel.getErroMsg() != null) {
          CDMLogger.getInstance().infoDialog(checkValModel.getErroMsg(), Activator.PLUGIN_ID);
        }

      }
      else {
        CDMLogger.getInstance().infoDialog("User does not have sufficient access rights to create a rule!",
            Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException excep) {
      CDMLogger.getInstance().errorDialog(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }

  }

  /**
   * c
   *
   * @param param
   * @param paramCol
   * @param checkValModel
   * @param cdrRuleList
   * @return
   * @throws ApicWebServiceException
   */
  private void createChkValRuleForFuncParams(final CDRResultParameter param, final ParamCollection paramCol,
      final CreateCheckValRuleModel checkValModel)
      throws ApicWebServiceException {

    openRulesEditor();
    if (this.reviewRulesEditor != null) {
      this.reviewRulesEditor.setActivePage(this.reviewRulesEditor.getListPage().getId());
    }
    // Icdm-2339 Check if the param is varaint coded is yes then remove the [] char and pass the base param.
    // Also Check for Rule Set Empty
    Parameter functionParameter = setBaseParam(param.getpType(), param.getName());
    openEditRuleDialog(functionParameter, paramCol,
        !(this.dataProvider.isModifiable(paramCol) && this.dataProvider.isRulesModifiable(paramCol)), checkValModel);


  }


  /**
   * Create Rules in Rule set editor
   *
   * @param param
   * @param checkValModel
   * @param cdrRuleList
   * @return
   * @throws ApicWebServiceException
   */
  private void createChkValRuleForRuleSet(final CDRResultParameter param, final CreateCheckValRuleModel checkValModel)
      throws ApicWebServiceException {

    Map<String, RuleSetParameter> paramMap;
    paramMap = this.dataProvider.getRulesOutput(this.ruleSet.getId()).getParamMap();
    RuleSetParameter ruleSetParam = paramMap.get(param.getName());
    openRuleSetEditor(ruleSetParam);
    openEditRuleDialog(ruleSetParam, this.ruleSet, !this.dataProvider.isModifiable(this.ruleSet), checkValModel);
  }


  /**
   * open the Rule Set Editor after reading the Rule from rule set manager
   *
   * @param ruleSetParam
   */
  private void openRuleSetEditor(final RuleSetParameter ruleSetParam) {
    try {
      ReviewParamEditorInput input = new ReviewParamEditorInput(this.ruleSet);
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        IEditorPart rulesEditor =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
        if (rulesEditor == null) {
          IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .openEditor(input, input.getEditorID());

          ((ReviewParamEditorInput) openEditor.getEditorInput()).setCdrFuncParam(ruleSetParam);
          this.reviewRulesEditor = (ReviewParamEditor) openEditor;
          this.reviewRulesEditor.setActivePage(this.reviewRulesEditor.getListPage().getId());
          this.reviewRulesEditor.getListPage().setActive(true);
        }
        else {
          ReviewParamEditor reviewParamEditor = (ReviewParamEditor) rulesEditor;
          this.reviewRulesEditor = reviewParamEditor;
          ReviewParamEditorInput alreadyOpenEditorInput = reviewParamEditor.getEditorInput();
          alreadyOpenEditorInput.setCdrFuncParam(ruleSetParam);
          if (reviewParamEditor.getActivePage() == LIST_PAGE_NUM) {
            reviewParamEditor.getListPage().setActive(true);
          }
          else if (reviewParamEditor.getActivePage() == DETAIL_PAGE_NUM) {
            reviewParamEditor.getDtlsPage().setActive(true);
          }
          else if (reviewParamEditor.getActivePage() == RULES_PAGE_NUM) {
            reviewParamEditor.getParamRulesPage().setActive(true);
          }

          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(alreadyOpenEditorInput,
              alreadyOpenEditorInput.getEditorID());
        }


      }

    }
    catch (WorkbenchException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
      CDMLogger.getInstance().debug(excep.getLocalizedMessage());
    }
  }


  /**
   * Opens the create rule dialog
   *
   * @param checkValModel
   * @param ruleSetParam
   * @param param2
   */
  private void openEditRuleDialog(final Object firstElement,
      final com.bosch.caltool.icdm.model.cdr.ParamCollection paramCollection, final boolean readOnly,
      final CreateCheckValRuleModel checkValModel) {
    ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet();
    try {
      CalData checkedValueObj = CalDataUtil.getCalDataObj(checkValModel.getCheckedValueObj());
      String pidcVerName = checkValModel.getPidcVersName();
      String resultName = checkValModel.getResultName();
      if ((checkValModel.getAttrValModel() != null) && !checkValModel.getAttrValModel().isEmpty()) {
        paramActionSet.openEditRuleDialog(firstElement, paramCollection, this.reviewRulesEditor, checkedValueObj,
            readOnly, pidcVerName, resultName, checkValModel.getAttrValModel(), this.dataProvider,
            this.paramDataProvider);
      }
      else if (checkValModel.getRuleToBeEdited() == null) {
        paramActionSet.openEditRuleDialog(firstElement, paramCollection, this.reviewRulesEditor, checkedValueObj,
            readOnly, pidcVerName, resultName, null, this.dataProvider, this.paramDataProvider);
      }
      else {
        if (ruleUpdateConfirmDialog()) {
          paramActionSet.openEditRuleDialog(checkValModel.getRuleToBeEdited(), paramCollection, this.reviewRulesEditor,
              checkedValueObj, readOnly, pidcVerName, resultName, null, this.dataProvider, this.paramDataProvider);
        }
      }
    }
    catch (ClassNotFoundException | IOException exp) {
      CDMLogger.getInstance().errorDialog("error when creating cal data object" + exp, exp, Activator.PLUGIN_ID);
    }

  }


  /**
   * @return
   */
  private boolean ruleUpdateConfirmDialog() {
    boolean canOpen;
    canOpen = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirmation",
        "The existing rule would be updated. Click ok to continue");
    return canOpen;
  }

  /**
   * To open rules editor from outline view
   */
  private void opnRulesFrmOutlineView() {
    Function func;
    func = getFunctionObject(this.firstElement);
    if ((func == null) || (func.getDefCharRefList() == null)) {
      CDMLogger.getInstance().warnDialog("No Def Characteristics found.Review Editor cannot be opened!",
          Activator.PLUGIN_ID);
    } // if Def.characteristics list not null

    else {
      ParamCollection cdrFunc = null;

      String funcName = func.getName();
      List<String> funcNameList = new ArrayList<>();
      funcNameList.add(func.getName());
      FunctionServiceClient functionServiceClient = new FunctionServiceClient();
      Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcNameMap = null;
      try {
        funcNameMap = functionServiceClient.getFunctionsByName(funcNameList);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      if (null != funcNameMap) {
        cdrFunc = funcNameMap.get(funcName);
      }
      if ((cdrFunc instanceof com.bosch.caltool.icdm.model.a2l.Function) && isBigFunction(cdrFunc)) {
        CDMLogger.getInstance().errorDialog("Function editor canot be opened for the function" + cdrFunc.getName(),
            Activator.PLUGIN_ID);
        return;
      }
      final ReviewParamEditorInput input = new ReviewParamEditorInput(cdrFunc);

      try {
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
          IEditorPart rulesEditor =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
          com.bosch.caltool.icdm.model.a2l.Parameter cdrFuncParam = null;
          if (rulesEditor == null) {
            rulesEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
                input.getEditorID());
            this.reviewRulesEditor = (ReviewParamEditor) rulesEditor;
            ((ReviewParamEditorInput) rulesEditor.getEditorInput()).setCdrFuncParam(cdrFuncParam);
            if ((cdrFunc instanceof com.bosch.caltool.icdm.model.a2l.Function) && isBigFunction(cdrFunc)) {
              ((ReviewParamEditorInput) rulesEditor.getEditorInput()).setCdrFuncVersion(func.getFunctionVersion());
            }
            ((ReviewParamEditor) rulesEditor).getListPage().setActive(true);
          }
          else {
            ReviewParamEditor reviewParamEditor = (ReviewParamEditor) rulesEditor;
            this.reviewRulesEditor = reviewParamEditor;
            ReviewParamEditorInput alreadyOpenEditorInput = reviewParamEditor.getEditorInput();
            alreadyOpenEditorInput.setCdrFuncParam(cdrFuncParam);
            alreadyOpenEditorInput.setEditorAlreadyOpened(true);
            if ((cdrFunc instanceof com.bosch.caltool.icdm.model.a2l.Function) && isBigFunction(cdrFunc)) {
              ((ReviewParamEditorInput) rulesEditor.getEditorInput()).setCdrFuncVersion(func.getFunctionVersion());
            }
            if (reviewParamEditor.getActivePage() == LIST_PAGE_NUM) {

              reviewParamEditor.getListPage().setActive(true);
            }
            else if (reviewParamEditor.getActivePage() == DETAIL_PAGE_NUM) {
              reviewParamEditor.getDetailsPage().setActive(true);
            }
            else if (reviewParamEditor.getActivePage() == RULES_PAGE_NUM) {
              reviewParamEditor.getParamRulesPage().setActive(true);
            }
            if (reviewParamEditor.getDetailsPage().getFcTableViewer() != null) {
              reviewParamEditor.getDetailsPage().setActive(true);
            }
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(alreadyOpenEditorInput,
                alreadyOpenEditorInput.getEditorID());
          }

        }
      }
      catch (PartInitException partInitEx) {
        CDMLogger.getInstance().error(partInitEx.getLocalizedMessage(), partInitEx, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * To open rules editor from result editor
   *
   * @throws ApicWebServiceException
   */
  private void openRulesEditor() {
    try {
      if (this.firstElement != null) {
        ParamCollection selectedFunction = null;
        com.bosch.caltool.icdm.model.a2l.Parameter cdrFuncParam = null;
        String funcVer = null;
        String funcName;
        if (this.firstElement instanceof CDRResultParameter) {

          selectedFunction = this.function;
          funcName = this.function.getName();
          if (CommonUtils.isEqual(funcName, ApicConstants.NOT_ASSIGNED)) {
            CDMLogger.getInstance().errorDialog(
                "Parameter not assigned to any function in the A2l. Copy param name to Clipboard.",
                Activator.PLUGIN_ID);
            return;
          }

          // Icdm-1251 Check if the param is varaint coded is yes then remove the [] char and pass the base param.
          // Also Check for Rule Set Empty
          CDRResultParameter resParam = (CDRResultParameter) this.firstElement;
          cdrFuncParam = setBaseParam(resParam.getpType(), resParam.getName());
        }
        else if (this.firstElement instanceof CDRResultFunction) {
          selectedFunction = this.function;
        }
        else if (this.firstElement instanceof A2LParameter) {
          A2LParameter param = (A2LParameter) this.firstElement;
          funcName = param.getDefFunction().getName();
          funcVer = param.getDefFunction().getFunctionVersion();
          List<String> funcNameList = new ArrayList<>();
          funcNameList.add(param.getDefFunction().getName());
          FunctionServiceClient functionServiceClient = new FunctionServiceClient();
          Map<String, com.bosch.caltool.icdm.model.a2l.Function> funcNameMap =
              functionServiceClient.getFunctionsByName(funcNameList);
          if (null != funcNameMap) {
            selectedFunction = funcNameMap.get(funcName);
          }
          cdrFuncParam = setBaseParam(param.getType(), param.getName());
        }
        ReviewParamEditorInput input = new ReviewParamEditorInput(selectedFunction);
        input.setCdrFuncParam(cdrFuncParam);
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
          IEditorPart rulesEditor =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);

          if (rulesEditor == null) {
            if ((selectedFunction instanceof com.bosch.caltool.icdm.model.a2l.Function) &&
                isBigFunction(selectedFunction)) {
              input.setCdrFuncVersion(funcVer);
            }
            rulesEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
                input.getEditorID());
            this.reviewRulesEditor = (ReviewParamEditor) rulesEditor;
            ((ReviewParamEditorInput) rulesEditor.getEditorInput()).setCdrFuncParam(cdrFuncParam);

            ((ReviewParamEditor) rulesEditor).getListPage().setActive(true);
          }
          else {
            ReviewParamEditor reviewParamEditor = (ReviewParamEditor) rulesEditor;
            this.reviewRulesEditor = reviewParamEditor;
            ReviewParamEditorInput alreadyOpenEditorInput = reviewParamEditor.getEditorInput();
            alreadyOpenEditorInput.setCdrFuncParam(cdrFuncParam);
            alreadyOpenEditorInput.setEditorAlreadyOpened(true);
            if ((selectedFunction instanceof com.bosch.caltool.icdm.model.a2l.Function) &&
                isBigFunction(selectedFunction)) {
              ((ReviewParamEditorInput) rulesEditor.getEditorInput()).setCdrFuncVersion(funcVer);
            }
            if (reviewParamEditor.getActivePage() == LIST_PAGE_NUM) {

              reviewParamEditor.getListPage().setActive(true);
            }
            else if (reviewParamEditor.getActivePage() == DETAIL_PAGE_NUM) {
              reviewParamEditor.getDetailsPage().setActive(true);
            }
            else if (reviewParamEditor.getActivePage() == RULES_PAGE_NUM) {
              reviewParamEditor.getParamRulesPage().setActive(true);
            }
            if (reviewParamEditor.getDetailsPage().getFcTableViewer() != null) {
              reviewParamEditor.getDetailsPage().setActive(true);
            }
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(alreadyOpenEditorInput,
                alreadyOpenEditorInput.getEditorID());
          }

        }
      }
    }
    catch (PartInitException partInitEx) {
      CDMLogger.getInstance().error(partInitEx.getLocalizedMessage(), partInitEx, Activator.PLUGIN_ID);
    }
    catch (WorkbenchException partInitEx) {
      CDMLogger.getInstance().error(partInitEx.getLocalizedMessage(), partInitEx, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param selectedFunction
   */
  private boolean isBigFunction(final ParamCollection selectedFunction) {
    com.bosch.caltool.icdm.model.a2l.Function function = (com.bosch.caltool.icdm.model.a2l.Function) selectedFunction;
    return (function.getBigFunc() != null) && !"N".equalsIgnoreCase(function.getBigFunc());
  }

  private Parameter setBaseParam(final String paramType, final String oldParamName) throws ApicWebServiceException {
    ParameterServiceClient client = new ParameterServiceClient();
    if (ApicUtil.isVariantCoded(oldParamName)) {
      String newParamName = ApicUtil.getBaseParamName(oldParamName);
      return client.getParameter(newParamName, paramType);
    }
    return client.getParameter(oldParamName, paramType);
  }

  /**
   * Get the Function instance from the selected object
   *
   * @param selectedElement
   * @return
   */
  private Function getFunctionObject(final Object selectedElement) {
    A2LBaseComponentFunctions a2lBCfunc;
    Function func = null;
    // Icdm 440
    if (selectedElement instanceof Function) {
      // when selected item is a function
      func = (Function) selectedElement;
    }
    else if (selectedElement instanceof A2LBaseComponentFunctions) {
      // when selected element is A2LBaseComponentFunctions
      a2lBCfunc = (A2LBaseComponentFunctions) selectedElement;
      for (Function function : this.functions) {
        if (function.getName().equals(a2lBCfunc.getName())) {
          func = function;
        }
      }
    }
    else {
      // when selected element is a String
      for (Function function : this.functions) {
        if (function.getName().equals(selectedElement.toString())) {
          func = function;
        }
      }
    }
    return func;
  }
}