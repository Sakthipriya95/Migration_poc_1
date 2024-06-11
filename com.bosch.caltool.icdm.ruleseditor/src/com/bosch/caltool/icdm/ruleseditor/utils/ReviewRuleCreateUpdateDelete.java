/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.calcomp.caldataphy2dcm.dcmwrite.DcmFromCalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewRuleDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleProviderResolver;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleLinkWrapperData;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.CDRParameterEditSection;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardData;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author rgo7cob
 */
public class ReviewRuleCreateUpdateDelete<D extends IParameterAttribute, P extends IParameter> {

  /**
   * Icdm-1056 Constant for update of Review Rule
   */
  private static final String UPDATE = "UPDATE Review Rule for the parameter ";
  /**
   * Icdm-1056 Constant for update of Review Rule complete
   */
  private static final String COMPLETED = " completed !";
  /**
   * Icdm-1056 Constant for update of Review Rule failed
   */
  private static final String FAILED = " failed ! ";


  /**
   * Ssd success message Code
   */
  private static final int SSD_SUCCESS_CODE = 0;
  private static final String DELETE = "DELETE Review Rule of a parameter";


  /**
   * Icdm-1056 Constant for insert of Review Rule
   */
  private static final String CREATE = "CREATE Review Rule for the Parameter ";

  /**
   * @param parameterDataProvider parameterDataProvider
   */
  public ReviewRuleCreateUpdateDelete(final ParameterDataProvider<D, P> parameterDataProvider) {
    super();
    this.parameterDataProvider = parameterDataProvider;
  }

  private final ParameterDataProvider<D, P> parameterDataProvider;


  /**
   * {@inheritDoc}
   *
   * @throws ApicWebServiceException
   */
  public boolean saveRules(final RuleInfoSection ruleInfoSection, final AddNewConfigWizardData wizardData)
      throws ApicWebServiceException {


    if (/* detailsPage.getSelectedParam().getType().equals(ParameterType.VALUE.getText()) && */
    ruleInfoSection.getBtnParamRefValMatch().getSelection() && "".equals(ruleInfoSection.getRefValueTxt().getText())) {
      MessageDialogUtils.getErrorMessageDialog("Mandatory Field", "Reference Value is mandatory");
      ruleInfoSection.getSaveBtn().setEnabled(false);
      return false;
    }
    if (ruleInfoSection.getParamEditSection().isParamPropertiesUpdated()) {
      // ICDM-1244
      updateParamProperties(ruleInfoSection, (P) ruleInfoSection.getSelectedParam());
    }
    if (!ruleInfoSection.isUpdate()) {
      // New Rule insertion
      return insertReviewRule(ruleInfoSection, wizardData);
    }

    // Update rules
    return updateReviewRule(ruleInfoSection, wizardData);
  }


  /**
   * @throws ApicWebServiceException ICDM-1244 @param ruleInfoSection RuleInfoSection @param cdrFuncParameter
   *           CDRFuncParameter @throws
   */
  private void updateParamProperties(final RuleInfoSection ruleInfoSection, final P cdrFuncParameter)
      throws ApicWebServiceException {

    CDRParameterEditSection paramEditSection = ruleInfoSection.getParamEditSection();
    // ICDM-916 stat Rivet
    String selectedItem =
        paramEditSection.getComboClass().getItem(paramEditSection.getComboClass().getSelectionIndex());
    ParameterClass paramClassT = ParameterClass.getParamClassT(selectedItem);

    ParameterServiceClient client = new ParameterServiceClient();
    Parameter param;
    RuleSetParameter ruleSetParameter = null;

    // Long for a parameter can be modified from rules editor Story 609878
    if (cdrFuncParameter instanceof RuleSetParameter) {
      ruleSetParameter = (RuleSetParameter) cdrFuncParameter;
      param = client.getParameterOnlyByName(ruleSetParameter.getName());
    }
    else {
      param = (Parameter) cdrFuncParameter;
    }

    param.setCodeWord(paramEditSection.getCodeWordChkBox().getSelection() ? "Y" : "N");

    if (paramClassT != null) {
      param.setpClassText(paramClassT.getText());
    }
    param.setIsBitWise(paramEditSection.getBitwiseRuleChkBox().getSelection() ? "Y" : "N");
    param.setParamHint(paramEditSection.getHintTxtArea().getText());
    param.setBlackList(paramEditSection.getBlackListChkBox().getSelection());
    param.setLongName(paramEditSection.getLongNameTxt().getText());

    client.update(param);

    if (ruleSetParameter != null) {
      ruleSetParameter.setLongName(param.getLongName());
    }
  }


  /**
   * @param ruleInfoSection insert new Rule
   * @param wizardData AddNewConfigWizardData
   */
  private boolean insertReviewRule(final RuleInfoSection ruleInfoSection, final AddNewConfigWizardData wizardData) {

    /*
     * Icdm-645 Rivet should have exact match -if the class is rivet and the Exact match is not set then rule should not
     * be created
     */

    if (checkIfRivetNotExactMatch(ruleInfoSection)) {
      MessageDialogUtils.getErrorMessageDialog("Rivet must have exact match",
          "Please create the rule with exact match for reference value for RIVET class");
      return false;
    }
    // ICDM-1346
    List<ReviewRule> rulesList = new ArrayList<>();
    try {
      createNewRule(ruleInfoSection, wizardData, rulesList);
      checkRule(rulesList);
      String paramName = ruleInfoSection.getSelectedParam().getName();

      if (!rulesList.isEmpty()) {
        ReviewRuleDataProvider dataProvider =
            new RuleProviderResolver().getRuleProvider(ruleInfoSection.getCdrFunction());
        ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
        parmCol.setParamCollection(ruleInfoSection.getCdrFunction());
        parmCol.setReviewRuleList(rulesList);
        SSDMessageWrapper ssdCode = dataProvider.createCdrRule(parmCol);
        if (ssdCode != null) {
          if ((ssdCode.getCode() == SSD_SUCCESS_CODE)) {
            CDMLogger.getInstance().info(CREATE + paramName + COMPLETED, Activator.PLUGIN_ID);

            // ICDM-723

            return true;
          }
          CDMLogger.getInstance().errorDialog(CREATE + paramName + FAILED + ssdCode.getDescription(),
              Activator.PLUGIN_ID);
        }
      }
    }
    catch (IcdmException exp) {
      CDMLogger.getInstance().errorDialog(
          CREATE + ruleInfoSection.getSelectedParam().getName() + FAILED + exp.getMessage(), exp, Activator.PLUGIN_ID);
      return false;
    }

    return false;

  }

  /**
   * @param rulesList
   */
  private void checkRule(final List<ReviewRule> rulesList) throws IcdmException {
    for (ReviewRule reviewRule : rulesList) {
      checkForSingleRule(reviewRule);

    }

  }


  /**
   * @param reviewRule
   * @throws IcdmException
   */
  private void checkForSingleRule(final ReviewRule reviewRule) throws IcdmException {
    if ((reviewRule.getUpperLimit() != null) && (reviewRule.getLowerLimit() != null) &&
        (reviewRule.getUpperLimit().floatValue() < reviewRule.getLowerLimit().floatValue())) {
      throw new IcdmException("Min value is greater than max value in rule");
    }
  }


  /**
   * @param ruleInfoSection RuleInfoSection
   * @param wizardData AddNewConfigWizardData
   */
  private boolean updateReviewRule(final RuleInfoSection ruleInfoSection,
      final AddNewConfigWizardData<D, P> wizardData) {
    try {
      /*
       * Icdm-645 Rivet should have exact match -if the class is rivet and the Exact match is not set then rule should
       * not be created
       */
      if (checkIfRivetNotExactMatch(ruleInfoSection)) {
        MessageDialogUtils.getErrorMessageDialog("Rivet must have Exact Match",
            "Please create the Rule with Exact Match for reference value for Rivet Class");
        ruleInfoSection.getBtnParamRefValMatch().setSelection(true);
        ruleInfoSection.setLimitsToNonEditable();
        return false;
      }
      // update multiple review rules
      if ((wizardData != null) && (wizardData.getCdrRulesList().size() > 1)) {
        for (ReviewRule rule : wizardData.getCdrRulesList()) {

          updateSingleRule(ruleInfoSection, rule, wizardData);
          checkForSingleRule(rule);
          ReviewRuleDataProvider dataProvider =
              new RuleProviderResolver().getRuleProvider(ruleInfoSection.getCdrFunction());
          ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
          parmCol.setParamCollection(ruleInfoSection.getCdrFunction());
          parmCol.setReviewRule(rule);
          SSDMessageWrapper ssdMessage = dataProvider.updateCdrRule(parmCol);

          if ((ssdMessage != null) && (ssdMessage.getCode() == SSD_SUCCESS_CODE)) {
            CDMLogger.getInstance().info(UPDATE + ruleInfoSection.getSelectedParam().getName() + COMPLETED,
                Activator.PLUGIN_ID);
          }
          else {
            break;
          }
        }
      }
      else {
        ReviewRule selectedCdrRule = ruleInfoSection.getSelectedCdrRule();
        checkForSingleRule(selectedCdrRule);
        updateSingleRule(ruleInfoSection, selectedCdrRule, wizardData);
        checkForSingleRule(selectedCdrRule);
        ReviewRuleDataProvider<ParamCollection> dataProvider =
            new RuleProviderResolver().getRuleProvider(ruleInfoSection.getCdrFunction());
        ReviewRuleParamCol parmCol = new ReviewRuleParamCol();
        parmCol.setParamCollection(ruleInfoSection.getCdrFunction());
        parmCol.setReviewRule(selectedCdrRule);
        SSDMessageWrapper ssdMessage = dataProvider.updateCdrRule(parmCol);


        if ((ssdMessage != null) && (ssdMessage.getCode() == SSD_SUCCESS_CODE)) {
          CDMLogger.getInstance().info(UPDATE + ruleInfoSection.getSelectedParam().getName() + COMPLETED,
              Activator.PLUGIN_ID);

          return true;
        }
      }

      return true;
    }
    catch (Exception exp) {
      CDMLogger.getInstance().errorDialog(
          CommonUtils.concatenate(UPDATE, ruleInfoSection.getSelectedParam().getName(), FAILED, exp.getMessage()), exp,
          Activator.PLUGIN_ID);
      resetSingleOrMultipleRules(ruleInfoSection, wizardData);
      return false;
    }

  }


  /**
   * @param ruleInfoSection
   * @return
   */
  private boolean checkIfRivetNotExactMatch(final RuleInfoSection ruleInfoSection) {
    return (this.parameterDataProvider.getPclass(ruleInfoSection.getSelectedParam()) == ParameterClass.RIVET) &&
        !ruleInfoSection.getBtnParamRefValMatch().getSelection();
  }

  /**
   * @param ruleInfoSection
   * @param wizardData
   * @throws ApicWebServiceException
   */
  private void resetSingleOrMultipleRules(final RuleInfoSection ruleInfoSection,
      final AddNewConfigWizardData<D, P> wizardData) {
    try {
      // reset multiple review rules
      if ((wizardData != null) && (wizardData.getCdrRulesList() != null)) {
        for (ReviewRule rule : wizardData.getCdrRulesList()) {
          resetRule(wizardData.getCdrFunction(), rule);
        }
      }
      else {
        ReviewRule cdrRule = ruleInfoSection.getSelectedCdrRule();
        resetRule(ruleInfoSection.getCdrFunction(), cdrRule);
      }
    }

    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param ruleInfoSection
   * @param wizardData
   * @param rulesList
   */
  private void createNewRule(final RuleInfoSection ruleInfoSection, final AddNewConfigWizardData<D, P> wizardData,
      final List<ReviewRule> rulesList) {
    if ((wizardData == null) && (ruleInfoSection.getAttrValModel() == null)) {
      createNewRule(ruleInfoSection, rulesList, null);
    }
    else if (ruleInfoSection.getAttrValModel() != null) {
      createNewRule(ruleInfoSection, rulesList, ruleInfoSection.getAttrValModel());
    }
    else if (wizardData != null) {
      for (Set<AttributeValueModel> attrValModel : wizardData.getAttrValModels().values()) {
        createNewRule(ruleInfoSection, rulesList, attrValModel);
      }
    }
  }

  /**
   * @param ruleInfoSection
   * @param rulesList
   * @param attrValModel
   */
  private void createNewRule(final RuleInfoSection ruleInfoSection, final List<ReviewRule> rulesList,
      final Set<AttributeValueModel> attrValModel) {
    final ReviewRule newRviewRule = new ReviewRule();

    newRviewRule.setParameterName(ruleInfoSection.getSelectedParam().getName());
    newRviewRule.setValueType(ruleInfoSection.getSelectedParam().getType());
    newRviewRule.setLowerLimit(CommonUtils.isEmptyString(ruleInfoSection.getLowerLimitTxt().getText().trim()) ? null
        : CommonUtils.getBigDecimalValueForLocaleString(ruleInfoSection.getLowerLimitTxt().getText().trim()));
    newRviewRule.setUpperLimit(CommonUtils.isEmptyString(ruleInfoSection.getUpperLimitTxt().getText().trim()) ? null
        : CommonUtils.getBigDecimalValueForLocaleString(ruleInfoSection.getUpperLimitTxt().getText().trim()));
    // set the bitwise rule value
    newRviewRule.setBitWiseRule(CommonUtils.isEmptyString(ruleInfoSection.getBitwiseRuleTxt().getText()) ? null
        : ruleInfoSection.getBitwiseRuleTxt().getText().trim());
    // Set advanced formula for complex rule
    newRviewRule.setFormulaDesc(
        CommonUtils.isEmptyString(ruleInfoSection.getAdvancedFormula()) ? null : ruleInfoSection.getAdvancedFormula());

    setRefValueForInsert(ruleInfoSection, newRviewRule);
    newRviewRule.setHint(ruleInfoSection.getHintTxtArea().getText().trim());
    newRviewRule.setUnicodeRemarks(ruleInfoSection.getUnicodeRmrkTxtArea().getText().trim());
    newRviewRule.setLabelFunction(ruleInfoSection.getCdrFunction().getName());
    // Icdm-537 Set the Unit Value
    if (ruleInfoSection.getSelectedUnit() != null) {
      newRviewRule.setUnit(ruleInfoSection.getSelectedUnit());
    }
    if (ruleInfoSection.getReadySeries().getSelection()) {
      newRviewRule.setReviewMethod(ApicConstants.READY_FOR_SERIES.YES.getDbType());
    }
    else {
      newRviewRule.setReviewMethod(ApicConstants.READY_FOR_SERIES.NO.getDbType());// Set
      // manual
      // as default
    }
    // ICDM 597
    newRviewRule.setDcm2ssd(ruleInfoSection.getBtnParamRefValMatch().getSelection());
    // ICDM-1081
    // convert attr-val map to feature value map
    if (attrValModel != null) {

      newRviewRule.setDependencyList(new TreeSet<>(attrValModel));
    }
    newRviewRule
        .setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(ruleInfoSection.getSelectedMaturityLevel()));

    newRviewRule.setRuleLinkWrapperData(getRuleLinkWrapperData(ruleInfoSection));

    rulesList.add(newRviewRule);
  }

  /**
   * @param ruleInfoSection
   * @return
   */
  private RuleLinkWrapperData getRuleLinkWrapperData(final RuleInfoSection ruleInfoSection) {
    RuleLinkWrapperData ruleLinkWrapperData = new RuleLinkWrapperData();
    List<RuleLinks> listOfRuleLinksToBeCreated = new ArrayList<>();
    List<RuleLinks> listOfRuleLinksToBeUpd = new ArrayList<>();
    List<RuleLinks> listOfRuleLinksToBeDel = new ArrayList<>();

    Iterator linkDataIterator = ruleInfoSection.getRuleLinksSet().iterator();
    while (linkDataIterator.hasNext()) {
      LinkData linkData = (LinkData) linkDataIterator.next();

      if ((CommonUtils.isNull(linkData.getRuleLinkObj()) &&
          CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_EDIT)) ||
          CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_ADD)) {
        RuleLinks ruleLinks = new RuleLinks();
        ruleLinks.setDescEng(linkData.getNewDescEng());
        ruleLinks.setDescGer(linkData.getNewDescGer());
        ruleLinks.setLink(linkData.getNewLink());

        ReviewRule selectedCdrRule = ruleInfoSection.getSelectedCdrRule();
        // For new rule, the selected cdrrule will be null
        if (CommonUtils.isNotNull(selectedCdrRule) && CommonUtils.isNotNull(selectedCdrRule.getRuleId())) {
          ruleLinks.setRuleId(selectedCdrRule.getRuleId().longValue());
          ruleLinks.setRevId(selectedCdrRule.getRevId().longValue());
        }

        // add new RuleLinks to the add list
        listOfRuleLinksToBeCreated.add(ruleLinks);
      }
      else if (CommonUtils.isNotNull(linkData.getRuleLinkObj()) &&
          CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_DELETE)) {
        // add selected RuleLinks to the del list
        listOfRuleLinksToBeDel.add(linkData.getRuleLinkObj());
      }
      else if (CommonUtils.isNotNull(linkData.getRuleLinkObj()) &&
          CommonUtils.isEqual(linkData.getOprType(), CommonUIConstants.CHAR_CONSTANT_FOR_EDIT)) {
        RuleLinks ruleLinks = linkData.getRuleLinkObj();
        ruleLinks.setDescEng(linkData.getNewDescEng());
        ruleLinks.setDescGer(linkData.getNewDescGer());
        ruleLinks.setLink(linkData.getNewLink());

        // add edited RuleLinks to the upd list
        listOfRuleLinksToBeUpd.add(ruleLinks);
      }
    }

    ruleLinkWrapperData.setListOfRuleLinksToBeCreated(listOfRuleLinksToBeCreated);
    ruleLinkWrapperData.setListOfRuleLinksToBeDel(listOfRuleLinksToBeDel);
    ruleLinkWrapperData.setListOfRuleLinksToBeUpd(listOfRuleLinksToBeUpd);
    if (CommonUtils.isNotNull(ruleInfoSection.getSelectedCdrRule())) {
      ruleLinkWrapperData.setListOfExistingLinksForSelRule(
          ruleInfoSection.getSelectedCdrRule().getRuleLinkWrapperData().getListOfExistingLinksForSelRule());
    }
    return ruleLinkWrapperData;

  }


  /**
   * Icdm-1056 if any Exception reset the Rule to the previous value.
   *
   * @param cdrRule
   * @throws ApicWebServiceException
   */
  private void resetRule(final ParamCollection abstractParameterCollection, final ReviewRule cdrRule)
      throws ApicWebServiceException {

    final List<ReviewRule> cdrRuleList = new ArrayList<>();
    if (abstractParameterCollection instanceof Function) {
      ReviewRuleServiceClient client = new ReviewRuleServiceClient();
      ReviewRuleParamCol paramCol = new ReviewRuleParamCol();
      paramCol.setParamCollection(abstractParameterCollection);
      paramCol.setReviewRule(cdrRule);

      cdrRuleList.addAll(client.readRules(paramCol).getReviewRuleList());

    }
    else {
      RuleSetRuleServiceClient client = new RuleSetRuleServiceClient();
      ReviewRuleParamCol paramCol = new ReviewRuleParamCol();
      paramCol.setParamCollection(abstractParameterCollection);
      paramCol.setReviewRule(cdrRule);

      client.readRules(paramCol);
      cdrRuleList.addAll(client.readRules(paramCol).getReviewRuleList());

    }

    ReviewRule oldCdrRule = null;

    for (ReviewRule cdrRuleFromList : cdrRuleList) {
      if (CommonUtils.isEqual(cdrRuleFromList.getRuleId(), cdrRule.getRuleId())) {
        oldCdrRule = cdrRuleFromList;
        break;
      }
    }
    if (oldCdrRule != null) {
      cdrRule.setValueType(cdrRule.getValueType());
      cdrRule.setLowerLimit(oldCdrRule.getLowerLimit());
      cdrRule.setUpperLimit(oldCdrRule.getUpperLimit());

      cdrRule.setReviewMethod(oldCdrRule.getReviewMethod());

      cdrRule.setHint(oldCdrRule.getHint());
      // Icdm-537 Set the Unit Value
      cdrRule.setUnit(oldCdrRule.getUnit());

      // ICDM 597
      cdrRule.setDcm2ssd(oldCdrRule.isDcm2ssd());
      if (cdrRule.getValueType().equals(ParameterType.VALUE.getText())) {
        if (oldCdrRule.getRefValue() == null) {
          cdrRule.setRefValueDcmString(oldCdrRule.getRefValueDcmString());
        }
        else {
          cdrRule.setRefValue(oldCdrRule.getRefValue());
        }
      }
      else {
        cdrRule.setRefValueDcmString(oldCdrRule.getRefValueDcmString());
      }

      cdrRule.setMaturityLevel(
          RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(oldCdrRule.getMaturityLevel()).getSSDMaturityLevel());

      cdrRule.setRuleLinkWrapperData(oldCdrRule.getRuleLinkWrapperData());
    }
  }

  /**
   * @param ruleInfoSection
   * @param newRviewRule
   */
  private void setRefValueForInsert(final RuleInfoSection ruleInfoSection, final ReviewRule newRviewRule) {
    boolean isTextRefValue;

    String refValue = ruleInfoSection.getRefValueTxt().getText().trim();

    if (refValue != null) {
      try {
        isTextRefValue = ruleInfoSection.checkRefValue(refValue);
      }
      catch (Exception e) {
        CDMLogger.getInstance().debug(e.getMessage(), e);
        return;
      }
    }
    else {
      isTextRefValue = true;
    }

    if (ruleInfoSection.getSelectedParam().getType().equals(ParameterType.VALUE.getText())) {
      setRefValForReviewRule(ruleInfoSection, newRviewRule, isTextRefValue, refValue);
    }
    else {
      newRviewRule.setRefValueDcmString(ruleInfoSection.getRefValCalDataObj() == null ? null
          : DcmFromCalData.getDcmString(ruleInfoSection.getRefValCalDataObj()));
    }
  }


  /**
   * @param ruleInfoSection
   * @param newRviewRule
   * @param isTextRefValue
   * @param refValue
   */
  private void setRefValForReviewRule(final RuleInfoSection ruleInfoSection, final ReviewRule newRviewRule,
      final boolean isTextRefValue, final String refValue) {
    if (isTextRefValue) {
      if (CommonUtils.isEmptyString(refValue)) {
        // if the text value is empty ,set null as dcm string
        newRviewRule.setRefValueDcmString(null);
      }
      else {
        String dcmStr = com.bosch.caltool.icdm.common.util.CalDataUtil
            .createDCMStringForText(newRviewRule.getParameterName(), ruleInfoSection.getSelectedUnit(), refValue);
        newRviewRule.setRefValueDcmString(dcmStr);
      }
      newRviewRule.setRefValue(null);
    }
    else {
      newRviewRule.setRefValue(
          CommonUtils.isEmptyString(refValue) ? null : CommonUtils.getBigDecimalValueForLocaleString(refValue));
      newRviewRule.setRefValueDispString(CommonUtils.isEmptyString(refValue) ? null : refValue);
      newRviewRule.setRefValueDcmString(null);
    }
  }


  /**
   * updates a single rule
   *
   * @param ruleInfoSection RuleInfoSection
   * @param selectedCdrRule CDRRule
   * @param wizardData AddNewConfigWizardData
   * @return SSDMessage
   * @throws IcdmException
   */
  private void updateSingleRule(final RuleInfoSection ruleInfoSection, final ReviewRule selectedCdrRule,
      final AddNewConfigWizardData<D, P> wizardData) {

    selectedCdrRule.setValueType(ruleInfoSection.getSelectedParam().getType());

    if (!CommonUtils.isEqual(ruleInfoSection.getLowerLimitTxt().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // if lower limit is <use current value> ,then do not update it
      selectedCdrRule.setLowerLimit(CommonUtils.isEmptyString(ruleInfoSection.getLowerLimitTxt().getText().trim())
          ? null : CommonUtils.getBigDecimalValueForLocaleString(ruleInfoSection.getLowerLimitTxt().getText().trim())); // ICDM-1515
    }

    if (!CommonUtils.isEqual(ruleInfoSection.getUpperLimitTxt().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // if upper limit is <use current value> ,then do not update it
      selectedCdrRule.setUpperLimit(CommonUtils.isEmptyString(ruleInfoSection.getUpperLimitTxt().getText().trim())
          ? null : CommonUtils.getBigDecimalValueForLocaleString(ruleInfoSection.getUpperLimitTxt().getText().trim()));// ICDM-1515
    }

    setBitWiseReviewRule(ruleInfoSection, selectedCdrRule);

    selectedCdrRule.setFormula(null);

    setFormulaDescForRule(ruleInfoSection, selectedCdrRule);

    // If the rule info section current review method is null then update or get selection is false then update.
    updateReviewMethod(ruleInfoSection, selectedCdrRule);

    if (!CommonUtils.isEqual(ruleInfoSection.getHintTxtArea().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // if hint is <use current value> , hint is not updated
      selectedCdrRule.setHint(ruleInfoSection.getHintTxtArea().getText().trim());
    }

    // Icdm-537 Set the Unit Value
    if ((ruleInfoSection.getSelectedUnit() != null) &&
        !CommonUtils.isEqual(ruleInfoSection.getSelectedUnit(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      selectedCdrRule.setUnit(ruleInfoSection.getSelectedUnit());
    }

    // ICDM 597
    if ((ruleInfoSection.getBtnUseCurrExactMatch() == null) || ((ruleInfoSection.getBtnUseCurrExactMatch() != null) &&
        !ruleInfoSection.getBtnUseCurrExactMatch().getSelection())) {
      // update if use current value radio button is not chosen
      selectedCdrRule.setDcm2ssd(ruleInfoSection.getBtnParamRefValMatch().getSelection());
    }

    if (!CommonUtils.isEqual(ruleInfoSection.getRefValueTxt().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      setRefValueForInsert(ruleInfoSection, selectedCdrRule);
    }

    if (!CommonUtils.isEqual(ruleInfoSection.getUnicodeRmrkTxtArea().getText(),
        CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // if hint is <use current value> , hint is not updated
      selectedCdrRule.setUnicodeRemarks(ruleInfoSection.getUnicodeRmrkTxtArea().getText().trim());
    }

    setReviewRuleDependencyList(selectedCdrRule, wizardData);

    setReviewRuleMaturityValue(ruleInfoSection, selectedCdrRule);

    selectedCdrRule.setRuleLinkWrapperData(getRuleLinkWrapperData(ruleInfoSection));
  }


  /**
   * @param ruleInfoSection
   * @param selectedCdrRule
   */
  private void setReviewRuleMaturityValue(final RuleInfoSection ruleInfoSection, final ReviewRule selectedCdrRule) {
    if (ruleInfoSection.isSameMaturityLevel() ||
        !ruleInfoSection.getSelectedMaturityLevel().equals(CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      selectedCdrRule
          .setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(ruleInfoSection.getSelectedMaturityLevel()));
    }
  }


  /**
   * @param ruleInfoSection
   * @param selectedCdrRule
   */
  private void setBitWiseReviewRule(final RuleInfoSection ruleInfoSection, final ReviewRule selectedCdrRule) {
    if (!CommonUtils.isEqual(ruleInfoSection.getBitwiseRuleTxt().getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // set the bitwise rule value
      selectedCdrRule.setBitWiseRule(CommonUtils.isEmptyString(ruleInfoSection.getBitwiseRuleTxt().getText()) ? null
          : ruleInfoSection.getBitwiseRuleTxt().getText().trim());
    }
  }


  /**
   * @param ruleInfoSection
   * @param selectedCdrRule
   */
  private void setFormulaDescForRule(final RuleInfoSection ruleInfoSection, final ReviewRule selectedCdrRule) {
    if (!CommonUtils.isEqual(ruleInfoSection.getAdvancedFormula(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      // set the advanced formula
      selectedCdrRule.setFormulaDesc(CommonUtils.isEmptyString(ruleInfoSection.getAdvancedFormula()) ? null
          : ruleInfoSection.getAdvancedFormula());
    }
  }


  /**
   * @param ruleInfoSection
   * @param selectedCdrRule
   */
  private void updateReviewMethod(final RuleInfoSection ruleInfoSection, final ReviewRule selectedCdrRule) {
    if ((ruleInfoSection.getUseCurrRvwMtd() == null) || !ruleInfoSection.getUseCurrRvwMtd().getSelection()) {
      // review rule is not updated when use current value radio button is selected
      if (ruleInfoSection.getReadySeries().getSelection()) {
        selectedCdrRule.setReviewMethod(ApicConstants.READY_FOR_SERIES.YES.getDbType());
      }
      else {
        selectedCdrRule.setReviewMethod(ApicConstants.READY_FOR_SERIES.NO.getDbType());
      }
    }
  }


  /**
   * @param selectedCdrRule
   * @param wizardData
   */
  private void setReviewRuleDependencyList(final ReviewRule selectedCdrRule,
      final AddNewConfigWizardData<D, P> wizardData) {
    if (wizardData != null) {
      if (!wizardData.getAttrValModels().values().isEmpty()) {

        Set<AttributeValueModel> attrModelSet = wizardData.getAttrValModels().get(1);

        if (selectedCdrRule.getDependencyList() != null) {
          // get the dependency list and add the attribute-values that are incomplete
          selectedCdrRule.getDependencyList().addAll(attrModelSet);
        }
      }
      else if (wizardData.getAttrValueModSet() != null) {

        Set<AttributeValueModel> attrModelSet = wizardData.getAttrValueModSet();
        if (selectedCdrRule.getDependencyList() != null) {
          // get the dependency list and add the attribute-values that are incomplete
          selectedCdrRule.getDependencyList().addAll(attrModelSet);
        }
      }
    }
  }


  // ICDM-1083
  /**
   * {@inheritDoc}
   */
  public boolean deleteRule(final List<ReviewRule> selectedRules, final ParamCollection cdrFunction) {


    ReviewRuleParamCol paramCol = new ReviewRuleParamCol();
    paramCol.setReviewRuleList(selectedRules);
    paramCol.setParamCollection(cdrFunction);
    ReviewRuleDataProvider dataProvider = new RuleProviderResolver().getRuleProvider(cdrFunction);
    dataProvider.deleteRule(paramCol);
    int ssdMessage = 0;

    if (ssdMessage == SSD_SUCCESS_CODE) {
      CDMLogger.getInstance().info(DELETE + "rules" + COMPLETED, Activator.PLUGIN_ID);
      return true;
    }
    CDMLogger.getInstance().errorDialog(DELETE + "rules" + FAILED + "Message desc", Activator.PLUGIN_ID);
    return false;


  }

}
