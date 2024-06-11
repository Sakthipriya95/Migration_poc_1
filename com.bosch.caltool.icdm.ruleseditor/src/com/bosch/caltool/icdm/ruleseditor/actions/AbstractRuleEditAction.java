/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;


/**
 * @author rgo7cob
 */
public class AbstractRuleEditAction extends Action {


  private static final int EDIT_RULE_DIA_WIDTH = 1000;
  private static final int EDIT_RULE_DIA_HEIGHT = 900;

  /**
   * {@inheritDoc}
   */
  protected boolean checkEditDialogAlreadyOpen(final Set<RuleInfoSection> ruleInfoSectionset) {


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
      if ((editRuleDialog.getShell() != null) && editRuleDialog.getShell().getMinimized()) {
        editRuleDialog.getShell().setMaximized(true);
        editRuleDialog.getShell().setSize(EDIT_RULE_DIA_WIDTH, EDIT_RULE_DIA_HEIGHT);
      }
    }
    return dialogAlreadyOpen;
  }

  /**
   * {@inheritDoc}
   */
  protected void addToWizardRuleInfoSectionMap(final IParameter selectedParam, final AddNewConfigWizardDialog dialog,
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
  protected void addEditRuleInfoSectToMap(final ReviewParamEditor editor, final IParameter param,
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

}
