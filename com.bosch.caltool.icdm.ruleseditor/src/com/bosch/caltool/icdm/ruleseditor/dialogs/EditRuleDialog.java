/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;


import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.apic.ApicBOUtil;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.utils.ReviewRuleCreateUpdateDelete;
import com.bosch.caltool.icdm.ws.rest.client.apic.UnitServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Dialog to edit the Rules Icdm-1056
 *
 * @author rgo7cob
 */
public class EditRuleDialog<D extends IParameterAttribute, P extends IParameter> extends AbstractDialog {


  /**
   * Title for rule update
   */
  private static final String EDIT_TITLE = "Edit Rule";
  /**
   * Title for rule create
   */
  private static final String NEW_TITLE = "New Rule";
  /**
   * PAGE DESCRIPTION FOR EDIT RULE DIALOG
   */
  private static final String PAGE_DESCRIPTION_FOR_EDIT = "Modify the rule information";
  /**
   * PAGE DESCRIPTION FOR NEW RULE DIALOG
   */
  private static final String PAGE_DESCRIPTION_FOR_NEW = "Enter the rule information";

  /**
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final IParameter selectedParam;
  private Button saveBtn;


  ListPage listPage;
  ParametersRulePage paramRulesPage;
  /**
   * ICDM-1081 rule info section
   */
  private final RuleInfoSection ruleInfoSection;

  private ParamCollectionDataProvider paramColDataProvider;

  private ParameterDataProvider<D, P> paramDataProvider;


  /**
   * CDRFunction instance
   */
  private final ParamCollection cdrFunc;

  private ReviewRule unmodifiedRule;
  private ReviewRule ruleToUpdate;
  private SortedSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel> attrValModel;

  private boolean isValidRefValue = true;

  /**
   * @return the unmodifiedRule
   */
  public ReviewRule getUnmodifiedRule() {
    return this.unmodifiedRule;
  }


  /**
   * @param unmodifiedRule the unmodifiedRule to set
   */
  public void setUnmodifiedRule(final ReviewRule unmodifiedRule) {
    this.unmodifiedRule = unmodifiedRule;
  }

  /**
   * @param parentShell parent shell
   * @param firstElement CDRFuncParameter for which the rule has to be modified
   * @param cdrFunction function selected
   * @param listPage listPage
   * @param resultParam CDRResultParameter
   * @param readOnlyMode if user does not have access rights to that function
   */
  public EditRuleDialog(final Shell parentShell, final IParameter firstElement, final ParamCollection cdrFunction,
      final ListPage listPage, final CalData checkValueObj, final boolean readOnlyMode, final String pidcVersName,
      final String resultName, final SortedSet<AttributeValueModel> attrValModel,
      final ParameterDataProvider<D, P> paramDataProvider, final ParamCollectionDataProvider paramColDataProvider) {
    super(parentShell);
    this.selectedParam = firstElement;
    this.listPage = listPage;
    this.cdrFunc = cdrFunction;
    this.attrValModel = attrValModel;
    this.paramDataProvider = paramDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrFunction, this, checkValueObj, readOnlyMode,
        pidcVersName, resultName, attrValModel);
  }

  // ICDM-1162
  /**
   * Constructor if default rule is selected for editing. Rule to be edited is passed directly
   *
   * @param parentShell parent shell
   * @param firstElement CDRFuncParameter for which the rule has to be modified
   * @param cdrFunction function selected
   * @param listPage listPage
   * @param cdrRule rule to be edited
   */
  public EditRuleDialog(final Shell parentShell, final IParameter firstElement, final ParamCollection cdrFunction,
      final ListPage listPage, final ReviewRule cdrRule, final boolean readOnlyMode,
      final ParameterDataProvider<D, P> paraDataProvider, final ParamCollectionDataProvider paramColDataProvider) {
    super(parentShell);
    this.selectedParam = firstElement;
    this.listPage = listPage;
    this.cdrFunc = cdrFunction;
    this.ruleToUpdate = cdrRule;
    this.paramDataProvider = paraDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrRule, cdrFunction, this, readOnlyMode);
  }

  /**
   * Constructor if default rule is selected for editing from parameter rules page. Rule to be edited is passed directly
   *
   * @param parentShell parent shell
   * @param firstElement CDRFuncParameter for which the rule has to be modified
   * @param cdrFunction function selected
   * @param paramPage paramRulesPage
   * @param cdrRule rule to be edited
   * @param readOnlyMode
   */
  public EditRuleDialog(final Shell parentShell, final IParameter firstElement, final ParamCollection cdrFunction,
      final ParametersRulePage paramPage, final ReviewRule cdrRule, final boolean readOnlyMode,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider<D, P> parameterDataProvider) {
    super(parentShell);
    this.selectedParam = firstElement;
    this.paramRulesPage = paramPage;
    this.cdrFunc = cdrFunction;
    this.ruleToUpdate = cdrRule;
    this.paramColDataProvider = paramColDataProvider;
    this.paramDataProvider = parameterDataProvider;
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrRule, cdrFunction, this, readOnlyMode);
  }

  /**
   * ICDM-1081 Constructor from Add new configuration dialog
   *
   * @param parentShell Parent shell
   * @param firstElement CDRFuncParameter
   * @param cdrFunction CDRFunction
   * @param paramRulesPage ParametersRulePage
   */
  public EditRuleDialog(final Shell parentShell, final IParameter firstElement, final ParamCollection cdrFunction,
      final ParametersRulePage paramRulesPage) {
    super(parentShell);
    this.selectedParam = firstElement;
    this.paramRulesPage = paramRulesPage;
    this.cdrFunc = cdrFunction;
    this.paramColDataProvider = paramRulesPage.getParamColDataProvider();
    this.paramDataProvider = paramRulesPage.getParameterDataProvider();
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrFunction, this, null, false, null, null, null);

  }


  /**
   * New Constructor for Opening the Rules for Check Values in Rule set editor
   *
   * @param parentShell Parent shell
   * @param firstElement CDRFuncParameter
   * @param cdrFunction CDRFunction
   * @param paramRulesPage ParametersRulePage
   * @param param result Param
   */
  public EditRuleDialog(final Shell parentShell, final IParameter firstElement, final ParamCollection cdrFunction,
      final ParametersRulePage paramRulesPage, final CalData checkValueObj, final String pidcVersName,
      final String resultName) {
    super(parentShell);
    this.selectedParam = firstElement;
    this.paramRulesPage = paramRulesPage;
    this.cdrFunc = cdrFunction;
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrFunction, this, checkValueObj, false,
        pidcVersName, resultName, null);
  }

  /**
   * @param activeShell
   * @param param
   * @param cdrFunction
   * @param listPage
   * @param cdrRule
   * @param readOnlyMode
   * @param pidcVersName
   * @param resultName
   */
  public EditRuleDialog(final Shell activeShell, final IParameter param, final ParamCollection cdrFunction,
      final ListPage listPage, final ReviewRule cdrRule, final CalData checkValueObj, final String pidcVersName,
      final String resultName, final ParameterDataProvider paramDataProvider,
      final ParamCollectionDataProvider paramColDataProvider) {
    super(activeShell);
    this.selectedParam = param;
    this.listPage = listPage;
    this.cdrFunc = cdrFunction;
    this.ruleToUpdate = cdrRule;
    this.paramColDataProvider = paramColDataProvider;
    this.paramDataProvider = paramDataProvider;
    this.ruleInfoSection = new RuleInfoSection(this.selectedParam, cdrRule, cdrFunction, this, checkValueObj, false,
        pidcVersName, resultName);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    if (null != this.attrValModel) {
      // Set title
      setTitle(NEW_TITLE);
      StringBuilder desc = new StringBuilder();
      desc.append("\nRule dependency : ").append(ApicBOUtil.getAttrValueString(this.attrValModel));
      setMessage(PAGE_DESCRIPTION_FOR_NEW + desc.toString());

    }
    else if (null == this.paramDataProvider.getRuleList(this.selectedParam)) {
      // Set title
      setTitle(NEW_TITLE);

      // Set the message
      setMessage(PAGE_DESCRIPTION_FOR_NEW +
          this.paramDataProvider.getDependencyDesc(this.ruleInfoSection.getSelectedCdrRule(), this.selectedParam));


    }
    else {
      // Set title
      setTitle(EDIT_TITLE);
      // Set the message
      setMessage(PAGE_DESCRIPTION_FOR_EDIT +
          this.paramDataProvider.getDependencyDesc(this.ruleInfoSection.getSelectedCdrRule(), this.selectedParam));

    }
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(
        "Edit Rule for Parameter : " + this.selectedParam.getName() + " , Type : " + this.selectedParam.getType());
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite top = (Composite) super.createDialogArea(parent);
    top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    top.setLayoutData(gridData);
    this.ruleInfoSection.createComposite(top);
    this.ruleInfoSection.setRuleDetails(this.selectedParam);
    return top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    EditRuleDialog.this.isValidRefValue = true;
    BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {
      try {
        EditRuleDialog.this.ruleInfoSection
            .checkRefValue(EditRuleDialog.this.ruleInfoSection.getRefValueTxt().getText().trim());
      }
      catch (Exception e) {
        EditRuleDialog.this.isValidRefValue = false;
        CDMLogger.getInstance().errorDialog("Only single dot allowed as decimal seperator in numbers", e,
            Activator.PLUGIN_ID);
        return;
      }
      ReviewRuleCreateUpdateDelete rulecreator =
          new ReviewRuleCreateUpdateDelete(EditRuleDialog.this.paramDataProvider);
      try {
        // If the unit is not available in TUnits database table, unit will be created
        String selectedUnit = EditRuleDialog.this.ruleInfoSection.getSelectedUnit();
        if (CommonUtils.isNotEmptyString(selectedUnit) &&
            !EditRuleDialog.this.ruleInfoSection.getUnits().contains(selectedUnit)) {
          Unit unitToCreate = new Unit();
          unitToCreate.setUnitName(selectedUnit);
          // create unit in TUnits table
          Set<Unit> unitsToCreate = new HashSet<>();
          unitsToCreate.add(unitToCreate);
          new UnitServiceClient().create(unitsToCreate);
        }
        rulecreator.saveRules(EditRuleDialog.this.ruleInfoSection, null);
        refreshPages();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    });
    if (this.isValidRefValue) {
      super.okPressed();
    }
  }


  /**
   *
   */
  private void refreshPages() {
    if ((null != this.listPage) && (null != this.listPage.getParamTabSec())) {
      this.listPage.getEditor().refreshSelectedParamRuleData();
      this.listPage.refreshListPage();
    }
    if ((null != this.paramRulesPage) && (null != this.paramRulesPage.getFcTableViewer())) {
      this.paramRulesPage.refreshParamPage();
      if (null != this.paramRulesPage.getEditor().getListPage()) {
        this.paramRulesPage.getEditor().getListPage().refreshListPage();
      }
    }
    ReviewRuleActionSet actionSet = new ReviewRuleActionSet();
    actionSet.refreshParamPropInOtherDialogs(this.ruleInfoSection.getParamEditSection(), this.cdrFunc,
        this.selectedParam, true, this.ruleInfoSection);

    this.saveBtn.setEnabled(false);
  }

  /**
   * @return the saveBtn
   */
  public Button getSaveBtn() {
    return this.saveBtn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    ReviewRuleActionSet actionSet = new ReviewRuleActionSet();
    actionSet.refreshParamPropInOtherDialogs(this.ruleInfoSection.getParamEditSection(), this.cdrFunc,
        this.selectedParam, false, this.ruleInfoSection);
    if ((this.unmodifiedRule != null) && (this.ruleToUpdate != null)) {
      updateRuleproperties(this.unmodifiedRule, this.ruleToUpdate, this.ruleToUpdate.getParameterName());
    }
    super.cancelPressed();
  }


  /**
   * Method copied from the cdrRuleUtil.java
   *
   * @param sourceRulet
   * @param targetRule
   * @param name
   */
  private static void updateRuleproperties(final ReviewRule sourceRule, final ReviewRule targetRule,
      final String name) {
    targetRule.setDcm2ssd(sourceRule.isDcm2ssd());
    targetRule.setHint(sourceRule.getHint());
    targetRule.setLabelFunction(sourceRule.getLabelFunction());
    targetRule.setLowerLimit(sourceRule.getLowerLimit());
    targetRule.setMaturityLevel(sourceRule.getMaturityLevel());
    targetRule.setParameterName(name);
    // iCDM-2071

    targetRule.setRefValCalData(sourceRule.getRefValueCalData());
    targetRule.setRefValue(sourceRule.getRefValue());
    targetRule.setRefValueDcmString(sourceRule.getRefValueDcmString());
    targetRule.setReviewMethod(sourceRule.getReviewMethod());
    targetRule.setSsdCase(sourceRule.getSsdCase());
    targetRule.setUnit(sourceRule.getUnit());
    targetRule.setUpperLimit(sourceRule.getUpperLimit());
    targetRule.setValueType(sourceRule.getValueType());
    // ICDM-2009
    targetRule.setBitWiseRule(sourceRule.getBitWiseRule());
  }

  // Icdm-327
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    this.ruleInfoSection.enableSave();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
    setBlockOnOpen(false);
  }

  /**
   * @return the ruleInfoSection
   */
  public RuleInfoSection getRuleInfoSection() {
    return this.ruleInfoSection;
  }

  /**
   * Icdm-1057- Remove the dialog from the map if it is closed {@inheritDoc}
   */
  @Override
  public boolean close() {
    if (this.saveBtn.isEnabled() &&
        MessageDialogUtils.getConfirmMessageDialogWithYesNo("Save the Data", "Do you want to Save the Changes?")) {
      ReviewRuleCreateUpdateDelete rulecreator = new ReviewRuleCreateUpdateDelete(this.paramDataProvider);
      try {
        rulecreator.saveRules(this.ruleInfoSection, null);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
      refreshPages();
    }
    ReviewRuleActionSet actionSet = new ReviewRuleActionSet();
    actionSet.refreshParamPropInOtherDialogs(this.ruleInfoSection.getParamEditSection(), this.cdrFunc,
        this.selectedParam, false, this.ruleInfoSection);
    return super.close();

  }


  /**
   * @return the paramColDataProvider
   */
  public ParamCollectionDataProvider getParamColDataProvider() {
    return this.paramColDataProvider;
  }


  /**
   * @param paramColDataProvider the paramColDataProvider to set
   */
  public void setParamColDataProvider(final ParamCollectionDataProvider paramColDataProvider) {
    this.paramColDataProvider = paramColDataProvider;
  }


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider<D, P> getParamDataProvider() {
    return this.paramDataProvider;
  }


  /**
   * @param paramDataProvider the paramDataProvider to set
   */
  public void setParamDataProvider(final ParameterDataProvider<D, P> paramDataProvider) {
    this.paramDataProvider = paramDataProvider;
  }
}
