/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParamRulesModel;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.utils.ReviewRuleCreateUpdateDelete;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.ChooseRuleWizardPage;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.CreateEditRuleWizardPage;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.SelectAttrValWizardPage;
import com.bosch.caltool.icdm.ws.rest.client.apic.UnitServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * ICDM-1081 Wizard to add new rule/configuration
 *
 * @author mkl2cob
 */
public class AddNewConfigWizard<D extends IParameterAttribute, P extends IParameter> extends Wizard {


  private static final String NEW_RULE = "New Rule";


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performCancel() {
    if ((this.wizardData.getCdrRule() != null) && (this.wizardData.getUnModifiedTargetRule() != null)) {
      ReviewRuleUtil.updateRuleProperties(this.wizardData.getUnModifiedTargetRule(), this.wizardData.getCdrRule(),
          this.wizardData.getCdrRule().getParameterName());
    }
    return super.performCancel();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage page) {
    if (page instanceof SelectAttrValWizardPage) {
      SelectAttrValWizardPage currentPage = (SelectAttrValWizardPage) page;
      if (!currentPage.isPageComplete()) {
        return currentPage;
      }
    }
    return super.getNextPage(page);
  }

  /**
   * Data for AddNewConfigWizard
   */
  private final AddNewConfigWizardData wizardData;

  /**
   * Page 2 to choose new rule or existing rule
   */
  private ChooseRuleWizardPage chooseRulePage;

  /**
   * Page 3 to create/edit a new rule
   */
  private CreateEditRuleWizardPage createEditRulePage;


  /**
   * @return the createEditRulePage
   */
  public CreateEditRuleWizardPage getCreateEditRulePage() {
    return this.createEditRulePage;
  }

  /**
   * Page 1 to choose attr val combination
   */
  private SelectAttrValWizardPage selectAttrValPage;

  /**
   * ParametersRulePage instance
   */
  private ParametersRulePage paramRulesPage;
  /**
   * ListPage instance
   */
  private ListPage listPage;

  /**
   * flag to know whether this is a rule update
   */
  private final boolean update;

  /**
   * ICDM-1176 boolean to indicate whether the wizard is for copying existing rule to a new attr/val combination
   */
  private final boolean copyRule;

  /**
   * ICDM-1176 boolean to indicate rule is to be copied without change
   */
  private boolean copyRuleWithoutChange;

  /**
   * AddNewConfigWizardDialog instance
   */
  private AddNewConfigWizardDialog wizardDialog;

  /**
   * RuleInfoSection instance
   */
  private RuleInfoSection ruleInfoSection;
  // ICDM-1190
  private final boolean readOnlyMode;

  /**
   * CDRFunction instance
   */
  private final ParamCollection cdrFunc;


  private final boolean copyRuleFromOtherParam;

  private final ParameterDataProvider<D, P> paramDataProvider;

  private final ParamCollectionDataProvider paramColDataProvider;

  /**
   * @return the copyRuleFromOtherParam
   */
  public boolean isCopyRuleFromOtherParam() {
    return this.copyRuleFromOtherParam;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    try {
      ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet();
      ReviewRuleCreateUpdateDelete rulecreator = new ReviewRuleCreateUpdateDelete(this.paramDataProvider);
      RuleInfoSection ruleInfoSec = ((CreateEditRuleWizardPage) getPage(NEW_RULE)).getRuleInfoSection();
      setRuleInfoSection(ruleInfoSec);
      // If the unit is not available in TUnits database table, unit will be created
      String selectedUnit = ruleInfoSec.getSelectedUnit();
      if (CommonUtils.isNotEmptyString(selectedUnit) && !ruleInfoSec.getUnits().contains(selectedUnit)) {
        Unit unitToCreate = new Unit();
        unitToCreate.setUnitName(selectedUnit);
        // create unit in TUnits table
        Set<Unit> unitsToCreate = new HashSet<>();
        unitsToCreate.add(unitToCreate);
        new UnitServiceClient().create(unitsToCreate);
      }
      if (this.copyRuleWithoutChange && !this.wizardData.isAttrValMapIncomplete()) {
        insertRule(ruleInfoSec, paramActionSet);
        refreshRulesEditor();
      }
      else if (rulecreator.saveRules(ruleInfoSec, this.wizardData)) {
        refreshRulesEditor();
      }
      else {
        return false;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   *
   */
  private void refreshRulesEditor() {
    BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), new Runnable() {

      @Override
      public void run() {
        refreshAllPage();
        refreshParamProps();
      }
    });
  }

  private void refreshAllPage() {
    if (null != this.listPage) {
      this.listPage.getEditor().refreshSelectedParamRuleData();
      this.listPage.refreshListPage();
    }
    if (null != this.paramRulesPage) {
      this.paramRulesPage.refreshParamPage();
      this.listPage = this.paramRulesPage.getEditor().getListPage();
      if (null != this.listPage) {
        this.listPage.refreshListPage();
      }
    }
  }

  private void refreshParamProps() {
    ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
    reviewParamActionSet.refreshParamPropInOtherDialogs(getRuleInfoSection().getParamEditSection(), this.cdrFunc,
        getWizardData().getCdrParameter(), true, getRuleInfoSection());
  }

  /**
   * @param ruleInfoSection2
   * @param paramActionSet
   */
  private void insertRule(final RuleInfoSection ruleInfoSection2, final ReviewRuleActionSet paramActionSet) {
    // ICDM-1176
    if (paramActionSet.insertReviewRule(this.wizardData, ruleInfoSection2)) {
      if (null != this.paramRulesPage) {
        this.paramRulesPage.setSelectedParameter();
      }
      else if (null != this.listPage) {
        this.listPage.setSelectedParameter();
        // ICDM-1346
        if (null != this.listPage.getFcTableViewer()) {
          this.listPage.getFcTableViewer().refresh();
        }
      }
    }
  }

  /**
   * Constructor
   *
   * @param cdrParameter CDRFuncParameter for which new configuration is created
   * @param cdrFunction CDRFunction
   * @param page ParametersRulePage
   * @param isUpdate whether this is a rule update
   * @param selectedRules selected cdr rules
   * @param copyRule true if the existing rule has to be copied
   * @param attrValIncomplete true if attribute value mapping is incomplete
   * @param attrSetToDefine set of attributes to be defined
   * @param attrValModel set of Attribute value model
   * @param readOnlyMode
   */
  public AddNewConfigWizard(final IParameter cdrParameter, final ParamCollection cdrFunction,
      final AbstractFormPage page, final boolean isUpdate, final List<ReviewRule> selectedRules, final boolean copyRule,
      final boolean attrValIncomplete, final Set<D> attrSetToDefine, final Set<AttributeValueModel> attrValModel,
      final boolean readOnlyMode, final boolean copyRuleFromOtherParam,
      final ParameterDataProvider<D, P> paramDataProvider, final ParamCollectionDataProvider paramColDataProvider) {
    this.copyRuleFromOtherParam = copyRuleFromOtherParam;
    this.paramColDataProvider = paramColDataProvider;
    this.paramDataProvider = paramDataProvider;
    this.cdrFunc = cdrFunction;
    if (page instanceof ParametersRulePage) {
      this.paramRulesPage = (ParametersRulePage) page;
    }
    else if (page instanceof ListPage) {
      this.listPage = (ListPage) page;
    }
    this.readOnlyMode = readOnlyMode;
    this.wizardData = new AddNewConfigWizardData(cdrParameter, cdrFunction,
        new ParamRulesModel<D, P>(cdrParameter, paramDataProvider));
    this.wizardData.setAttrValMapIncomplete(attrValIncomplete);
    this.update = isUpdate;
    if (this.update) {
      this.wizardData.setCdrRule(selectedRules.get(0));
      this.wizardData.setCdrRulesList(selectedRules);
      this.wizardData.setAttrributesIncomplete(attrSetToDefine);
      if (this.wizardData.isAttrValMapIncomplete()) {
        this.wizardData.setAttrValueModSet(attrValModel);
      }
    }
    else {
      // if not the case mentioned in ICDM-1199 , incomplete attributes set will contain all the dependency attributes
      Set<D> depnAttrSet = new HashSet<>();
      // get all the dependecy attributes
      for (D paramAttr : paramDataProvider.getParamAttrs(cdrParameter)) {
        depnAttrSet.add(paramAttr);
      }
      // store the incomplete attributes in the wizard data
      this.wizardData.setAttrributesIncomplete(depnAttrSet);
    }
    this.copyRule = copyRule;
    if (this.copyRule) {
      this.wizardData.setCdrRule(selectedRules.get(0));
    }
    if (this.copyRuleFromOtherParam) {
      this.wizardData.setCdrRule(selectedRules.get(0));
    }
  }


  @Override
  public void addPages() {
    setNeedsProgressMonitor(true);
    // Set tile
    setWindowTitle("Rule Wizard - Parameter Name:" + this.wizardData.getCdrParameter().getName() + " , Type:" +
        this.wizardData.getCdrParameter().getType());

    // ICDM-1199 if the attr value mapping is incomplete , show the first page for update
    if (!this.update || (this.update && this.wizardData.isAttrValMapIncomplete())) {
      this.selectAttrValPage = new SelectAttrValWizardPage("Attribute Value Combination");
      addPage(this.selectAttrValPage);
    }
    if (!this.update) {
      if ((this.wizardData.getParamRulesModel().getRuleDefenitionSet() != null) &&
          (this.wizardData.getParamRulesModel().getRuleDefenitionSet().size() > 0) && !this.copyRule &&
          !this.copyRuleFromOtherParam) {
        // if the parameter does not contain any existing rules, skip the second page
        this.chooseRulePage = new ChooseRuleWizardPage("Rule");
        addPage(this.chooseRulePage);
      }
    }
    // boolean to indicate rules are with same maturity levels
    boolean sameMaturityLevel = true;

    // boolean to indicate rules are with same hint
    boolean sameHint = true;

    /**
     * boolean to indicate rules are with same lower limit
     */
    boolean sameLowerLmt = true;

    /**
     * boolean to indicate rules are with same upper limit
     */
    boolean sameUpperLmt = true;

    /**
     * boolean to indicate rules are with same ref val
     */
    boolean sameRefVal = true;

    /**
     * boolean to indicate rules are with same exact match flag
     */
    boolean sameExactMatchFlag = true;

    /**
     * boolean to indicate rules are with same ready for series
     */
    boolean sameRvwMtd = true;

    /**
     * boolean to indicate rules are with same unit
     */
    boolean sameUnit = true;
    /**
     * boolean to indicate rules are with same bitwise limit
     */
    boolean sameBitWise = true;
    // ICDM-1822
    /**
     * boolean to indicate rules have same modified user
     */
    boolean sameModifiedUser = true;

    if (this.wizardData.getCdrRulesList() != null) {
      // checking if all the rules have same maturity levels
      Iterator<ReviewRule> iterator = this.wizardData.getCdrRulesList().iterator();

      ReviewRule nextRule = iterator.next();
      // initialising all variables
      String maturityLevel = nextRule.getMaturityLevel();
      String hint = nextRule.getHint();
      BigDecimal lowerLmt = nextRule.getLowerLimit();
      BigDecimal upperLmt = nextRule.getUpperLimit();
      BigDecimal refVal = null;
      CalData refCalData = null;
      if (CommonUtils.isEqual(nextRule.getValueType(), ParameterType.VALUE.getText()) &&
          CommonUtils.isNotNull(nextRule.getRefValue())) {
        refVal = nextRule.getRefValue();
      }
      else {
        refCalData = ReviewRuleUtil.getRefValue(nextRule);
      }
      boolean exactMatch = nextRule.isDcm2ssd();
      String unit = nextRule.getUnit();
      String rvwMtd = nextRule.getReviewMethod();
      String bitWiseRule = nextRule.getBitWiseRule();
      String modifiedUser = "";
      // Iterate through the rules
      while (iterator.hasNext()) {
        nextRule = iterator.next();

        if (sameMaturityLevel && !CommonUtils.isEqual(maturityLevel, nextRule.getMaturityLevel())) {
          // make the boolean false if atleast one maturity level is different
          sameMaturityLevel = false;
        }
        // ICDM-1822
        if (sameModifiedUser && !CommonUtils.isEqual(modifiedUser, modifiedUser)) {
          // make the boolean false if atleast one modified user is different
          sameModifiedUser = false;
        }
        if (sameHint && !CommonUtils.isEqual(hint, nextRule.getHint())) {
          // make the boolean false if atleast one hint is different
          sameHint = false;
        }
        if (sameLowerLmt && !CommonUtils.isEqual(lowerLmt, nextRule.getLowerLimit())) {
          // make the boolean false if atleast one lower limit is different
          sameLowerLmt = false;
        }
        if (sameUpperLmt && !CommonUtils.isEqual(upperLmt, nextRule.getUpperLimit())) {
          // make the boolean false if atleast one upper limit is different
          sameUpperLmt = false;
        }
        if (sameRefVal) {
          sameRefVal = checkIfRefValIsSame(sameRefVal, nextRule, refVal, refCalData);
        }
        if (sameExactMatchFlag && !CommonUtils.isEqual(exactMatch, nextRule.isDcm2ssd())) {
          // make the boolean false if atleast one exact match is different
          sameExactMatchFlag = false;
        }
        if (sameUnit && !CommonUtils.isEqual(unit, nextRule.getUnit())) {
          // make the boolean false if atleast one unit is different
          sameUnit = false;
        }
        if (sameRvwMtd && !CommonUtils.isEqual(rvwMtd, nextRule.getReviewMethod())) {
          // make the boolean false if atleast one ready for series db value is different
          sameRvwMtd = false;
        }
        if (sameBitWise && !CommonUtils.isEqual(bitWiseRule, nextRule.getBitWiseRule())) {
          // make the boolean false if atleast one bitwise rule is different
          sameBitWise = false;
        }
      }
    }

    // Set all rule details to wizard data
    this.wizardData.setSameMaturityLevel(sameMaturityLevel);
    this.wizardData.setSameHint(sameHint);
    this.wizardData.setSameLowerLmt(sameLowerLmt);
    this.wizardData.setSameUpperLmt(sameUpperLmt);
    this.wizardData.setSameRefVal(sameRefVal);
    this.wizardData.setSameExactMatchFlag(sameExactMatchFlag);
    this.wizardData.setSameReadyForSeries(sameRvwMtd);
    this.wizardData.setSameUnit(sameUnit);
    this.wizardData.setSameBitWise(sameBitWise);
    // ICDM-1822
    this.wizardData.setSameModifiedUser(sameModifiedUser);

    this.createEditRulePage = new CreateEditRuleWizardPage(NEW_RULE, this.update, this.wizardData.getCdrRule(), this,
        this.paramDataProvider, this.paramColDataProvider);
    addPage(this.createEditRulePage);

  }

  /**
   * @param sameRefVal boolean
   * @param nextRule CDRRule
   * @param refVal BigDecimal
   * @param refCalData CalData
   * @return true if the rule has same ref value
   */
  private boolean checkIfRefValIsSame(final boolean sameRefVal, final ReviewRule nextRule, final BigDecimal refVal,
      final CalData refCalData) {
    boolean checkRefValChanged = sameRefVal;
    if (CommonUtils.isEqual(nextRule.getValueType(), ParameterType.VALUE.getText())) {
      if (CommonUtils.isNotNull(refVal)) {
        if (!CommonUtils.isEqual(refVal, nextRule.getRefValue())) {
          // check if the big decimal value is same
          checkRefValChanged = false;
        }
      }
      else {
        // check if the ref val cal data values are same
        checkRefValChanged = checkRefValCalDataSame(nextRule, refCalData);
      }
    }
    else {
      checkRefValChanged = checkRefValCalDataSame(nextRule, refCalData);
    }
    return checkRefValChanged;
  }

  /**
   * @param nextRule CDRRule
   * @param refCalData CalData
   * @return true if the ref value cal data objects are same
   */
  private boolean checkRefValCalDataSame(final ReviewRule nextRule, final CalData refCalData) {
    if (CommonUtils.isNotNull(nextRule.getRefValue())) {
      // make the boolean false if the rule has big decimal value
      return false;
    }
    CalData currCalData = ReviewRuleUtil.getRefValue(nextRule);
    if ((refCalData != null) && (currCalData != null) &&
        !CommonUtils.isEqual(refCalData.getCalDataPhy(), currCalData.getCalDataPhy())) {
      // make the boolean false if atleast one reference value is different
      return false;
    }
    else if (CommonUtils.isNull(currCalData) && CommonUtils.isNull(refCalData)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canFinish() {
    // ICDM-1190
    if (isReadOnlyMode()) {
      return false;
    }
    if (this.selectAttrValPage == null) {
      return this.createEditRulePage.isPageComplete();
    }
    if (this.copyRule || this.wizardData.isAttrValMapIncomplete()) {
      // ICDM-1076
      return this.selectAttrValPage.isPageComplete();
    }
    return this.createEditRulePage.isPageComplete() && this.selectAttrValPage.isPageComplete();
  }

  /**
   * @return the wizardData
   */
  public AddNewConfigWizardData getWizardData() {
    return this.wizardData;
  }

  /**
   * @return the isUpdate
   */
  public boolean isUpdate() {
    return this.update;
  }

  /**
   * @param copyRuleWithoutChange the copyRuleWithoutChange to set
   */
  public void setCopyRuleWithoutChange(final boolean copyRuleWithoutChange) {
    this.copyRuleWithoutChange = copyRuleWithoutChange;
  }

  /**
   * @return the wizardDialog
   */
  public AddNewConfigWizardDialog getWizardDialog() {
    return this.wizardDialog;
  }

  /**
   * @param wizardDialog the wizardDialog to set
   */
  public void setWizardDialog(final AddNewConfigWizardDialog wizardDialog) {
    this.wizardDialog = wizardDialog;
  }

  /**
   * @return the ruleInfoSection
   */
  public RuleInfoSection getRuleInfoSection() {
    return this.ruleInfoSection;
  }

  /**
   * @param ruleInfoSection the ruleInfoSection to set
   */
  public void setRuleInfoSection(final RuleInfoSection ruleInfoSection) {
    this.ruleInfoSection = ruleInfoSection;
  }


  /**
   * @return the cdrFunc
   */
  public ParamCollection getCdrFunc() {
    return this.cdrFunc;
  }

  /**
   * @return the readOnlyMode
   */
  public boolean isReadOnlyMode() {
    return this.readOnlyMode;
  }


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider<D, P> getParamDataProvider() {
    return this.paramDataProvider;
  }


}
