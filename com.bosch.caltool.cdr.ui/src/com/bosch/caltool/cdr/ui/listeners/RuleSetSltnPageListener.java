/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.bosch.caltool.cdr.ui.wizard.page.validator.RuleSetSltnPageValidator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RuleSetSltnPageListener {


  private final CalDataReviewWizard calDataReviewWizard;
  private final RuleSetSltnPageValidator ruleSetPageValidator;

  private final RuleSetSltnPage ruleSetSelWizPage;


  /**
   * @param calDataReviewWizardNew as input
   * @param ruleSetPageValidator as input
   */
  public RuleSetSltnPageListener(final CalDataReviewWizard calDataReviewWizardNew,
      final RuleSetSltnPageValidator ruleSetPageValidator) {
    this.calDataReviewWizard = calDataReviewWizardNew;
    this.ruleSetPageValidator = ruleSetPageValidator;
    this.ruleSetSelWizPage = calDataReviewWizardNew.getRuleSetSelPage();
  }

  /**
  *
  */
  public void createActionListeners() {


    this.ruleSetSelWizPage.getUnSelectBtn().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // clearing all sec-rule set data
        RuleSetSltnPageListener.this.ruleSetSelWizPage.clearOtherRuleSetData();
        RuleSetSltnPageListener.this.ruleSetSelWizPage.getSecRuleSetTabViewer().setSelection(null);
        try {
          if (RuleSetSltnPageListener.this.ruleSetSelWizPage.checkIfPrimaryRuleSetRestricted()) {
            return;
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    });


    this.ruleSetSelWizPage.getPrimaryRuleSetTabViewer().getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc} on selection change
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) RuleSetSltnPageListener.this.ruleSetSelWizPage
            .getPrimaryRuleSetTabViewer().getSelection();
        // Check if selection exists
        if (selection != null) {

          RuleSet ruleSet = (RuleSet) selection.getFirstElement();
          // iCDM-1522
          RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorMessage(null);
          RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorOccured(false);
          // If param list is not empty
          if (CommonUtils.isNotNull(RuleSetSltnPageListener.this.ruleSetSelWizPage.getParamList())) {
            RuleSetSltnPageListener.this.ruleSetSelWizPage.updateContainer();
          } // If param list is empty
          else {
            // for review wizard
            if (RuleSetSltnPageListener.this.ruleSetSelWizPage.getSecRuleSetSection().isEnabled()) {
              final IStructuredSelection secTabSelection =
                  (IStructuredSelection) RuleSetSltnPageListener.this.ruleSetSelWizPage.getSecRuleSetTabViewer()
                      .getSelection();
              List<RuleSet> secTabSelctnList = secTabSelection.toList();

              // if the ruleset is already selected in secondary table viewer
              if (secTabSelctnList.contains(ruleSet)) {
                RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorOccured(true);
                RuleSetSltnPageListener.this.ruleSetSelWizPage
                    .setErrorMessage("Rule Set (" + ruleSet.getName() + "). cannot be primary as well as secondary.");
                RuleSetSltnPageListener.this.ruleSetSelWizPage.setPrimaryReviewRuleSet(ruleSet);

                RuleSetSltnPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                    .setPrimaryRuleSetId(ruleSet.getId());
                return;
              }

              addSecondaryRules(secTabSelctnList);
            }

            RuleSetSltnPageListener.this.ruleSetSelWizPage.setPrimaryReviewRuleSet(ruleSet);
            RuleSetSltnPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setPrimaryRuleSetId(ruleSet.getId());


            RuleSetSltnPageListener.this.ruleSetSelWizPage.updateContainer();
            try {
              setErrorMessageCustom();
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
          }
        }
        RuleSetSltnPageListener.this.ruleSetSelWizPage.setContentChanged(true);
      }

    });

    this.ruleSetSelWizPage.getSecRuleSetTabViewer().getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc} on selection change
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // for review wizard
        final IStructuredSelection selection = (IStructuredSelection) RuleSetSltnPageListener.this.ruleSetSelWizPage
            .getSecRuleSetTabViewer().getSelection();
        // Check if selection exists
        if (selection != null) {
          // add others to ruleSetDataList
          List selectedRuleSets = selection.toList();

          RuleSetSltnPageListener.this.ruleSetSelWizPage.clearOtherRuleSetData();


          for (Object ruleSetSelected : selectedRuleSets) {
            RuleSet ruleSet = (RuleSet) ruleSetSelected;
            RuleSet primaryRuleSet = RuleSetSltnPageListener.this.ruleSetSelWizPage.getPrimaryReviewRuleSet();
            // restricting ruleset which is already chosen for
            if ((null != primaryRuleSet) && (primaryRuleSet.getId() == ruleSet.getId())) {
              RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorOccured(true);
              RuleSetSltnPageListener.this.ruleSetSelWizPage
                  .setErrorMessage("Rule Set (" + ruleSet.getName() + "). cannot be primary as well as secondary.");
              RuleSetSltnPageListener.this.ruleSetSelWizPage.updateContainer();
              break;
            }
            // iCDM-1522
            RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorMessage(null);
            RuleSetSltnPageListener.this.ruleSetSelWizPage.setErrorOccured(false);
            RuleSetSltnPageListener.this.ruleSetSelWizPage.getSecondaryReviewRuleSetList()
                .add((RuleSet) ruleSetSelected);
            RuleSetSltnPageListener.this.calDataReviewWizard.getCdrWizardUIModel().getSecondaryRuleSetIds()
                .add(((RuleSet) ruleSetSelected).getId());

            RuleSetSltnPageListener.this.ruleSetSelWizPage.updateContainer();
            try {
              setErrorMessageCustom();
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
          }
        }
        RuleSetSltnPageListener.this.ruleSetSelWizPage.setContentChanged(true);
      }

    });


  }

  /**
   * @throws ApicWebServiceException
   */
  protected void setErrorMessageCustom() throws ApicWebServiceException {

    String ruleSetRestrictredNames = "";
    IStructuredSelection selection =
        (IStructuredSelection) this.ruleSetSelWizPage.getSecRuleSetTabViewer().getSelection();
    // Check if selection exists
    if (selection != null) {
      // add others to ruleSetDataList
      List selectedRuleSets = selection.toList();
      for (Object ruleSetSelected : selectedRuleSets) {
        RuleSet ruleSet = (RuleSet) ruleSetSelected;
        if (this.ruleSetSelWizPage.isRestricted(ruleSet)) {
          ruleSetRestrictredNames = CommonUtils.concatenate(ruleSetRestrictredNames, ruleSet.getName(), ",");
        }
      }

    }

    selection = (IStructuredSelection) this.ruleSetSelWizPage.getPrimaryRuleSetTabViewer().getSelection();
    // Check if selection exists
    if (selection != null) {
      // add others to ruleSetDataList
      List selectedRuleSets = selection.toList();
      for (Object ruleSetSelected : selectedRuleSets) {
        RuleSet ruleSet = (RuleSet) ruleSetSelected;
        if (this.ruleSetSelWizPage.isRestricted(ruleSet)) {
          ruleSetRestrictredNames = CommonUtils.concatenate(ruleSetRestrictredNames, ruleSet.getName(), ",");
        }
      }

    }

    if (ruleSetRestrictredNames.length() > 0) {
      ruleSetRestrictredNames = ruleSetRestrictredNames.substring(0, ruleSetRestrictredNames.length() - 1);
      this.ruleSetSelWizPage.setErrorMessage("Access restricted to the following Rule Set (" + ruleSetRestrictredNames +
          "). Please select a different rule set.");
      this.ruleSetSelWizPage.setErrorOccured(true);
      this.ruleSetSelWizPage.updateContainer();
    }


  }


  /**
   * @param secTabSelctnList
   */
  private void addSecondaryRules(final List<RuleSet> secTabSelctnList) {
    List<RuleSet> secondaryRuleSet = this.ruleSetSelWizPage.getSecondaryReviewRuleSetList();

    for (RuleSet secRuleSet : secTabSelctnList) {
      List<RuleSet> rulesSetToAdd = new ArrayList<>();
      if (CommonUtils.isNullOrEmpty(this.ruleSetSelWizPage.getSecondaryReviewRuleSetList())) {
        rulesSetToAdd.add(secRuleSet);
      }
      else {
        if (!secondaryRuleSet.contains(secRuleSet)) {
          rulesSetToAdd.add(secRuleSet);
        }
      }
      this.ruleSetSelWizPage.getSecondaryReviewRuleSetList().addAll(rulesSetToAdd);
      for (RuleSet ruleset : rulesSetToAdd) {
        this.calDataReviewWizard.getCdrWizardUIModel().getSecondaryRuleSetIds().add(ruleset.getId());
      }
    }
  }

  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @return the ruleSetPageValidator
   */
  public RuleSetSltnPageValidator getRuleSetPageValidator() {
    return this.ruleSetPageValidator;
  }


  /**
   * @return the ruleSetSelWizPage
   */
  public RuleSetSltnPage getRuleSetSelWizPage() {
    return this.ruleSetSelWizPage;
  }


}
