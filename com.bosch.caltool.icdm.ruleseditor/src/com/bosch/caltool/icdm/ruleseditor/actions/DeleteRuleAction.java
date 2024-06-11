/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.utils.ReviewRuleCreateUpdateDelete;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author rgo7cob
 */
public class DeleteRuleAction<D extends IParameterAttribute, P extends IParameter> extends Action {


  private final List<ReviewRule> selectedRules;
  private final boolean defaultRule;
  private final ParamCollection cdrFunction;
  private final AbstractFormPage page;
  private final List list;
  private final boolean multiSelect;
  private final ParameterDataProvider<D, P> paramDataProvider;

  /**
   * @param selectedRules
   * @param defaultRule
   * @param cdrFunction
   * @param page
   * @param list
   * @param multiSelect
   */
  public DeleteRuleAction(final List<ReviewRule> selectedRules, final boolean defaultRule,
      final ParamCollection cdrFunction, final AbstractFormPage page, final List list, final boolean multiSelect,
      final ParameterDataProvider<D, P> paramDataProvider, final ParamCollectionDataProvider paramColDataProvider) {

    this.selectedRules = selectedRules;
    this.defaultRule = defaultRule;
    this.cdrFunction = cdrFunction;
    this.page = page;
    this.list = list;
    this.multiSelect = multiSelect;
    this.paramDataProvider = paramDataProvider;

    setText("Delete Rule");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    setImageDescriptor(imageDesc);

    setEnabled(paramColDataProvider.isModifiable(this.cdrFunction) && CommonUtils.isNotEmpty(selectedRules));


  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    MessageDialog dialog;
    if (!this.multiSelect || isValidMultiSelect(this.list)) {// ICDM-2351
      if (this.defaultRule) {
        dialog =
            new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Confirmation",
                null, "Do you really want to delete this default rule ?", MessageDialog.QUESTION_WITH_CANCEL,
                new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
        dialog.open();
      }
      else {
        dialog =
            new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Confirmation",
                null, "Do you really want to delete the selected rules?", MessageDialog.QUESTION_WITH_CANCEL,
                new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
        dialog.open();
      }
      final int btnSel = dialog.getReturnCode();
      if (btnSel == 0) {
        ReviewRuleCreateUpdateDelete<D, P> ruleDelete =
            new ReviewRuleCreateUpdateDelete<>(DeleteRuleAction.this.paramDataProvider);
        boolean deleteSuccess = ruleDelete.deleteRule(this.selectedRules, this.cdrFunction);
        if (deleteSuccess) {
          refreshViewer(this.page);
        }
      }
    }
    else {
      // when it is an invalid multiple select
      MessageDialogUtils.getErrorMessageDialog("Delete Not Possible",
          "The selection contains parameter which has dependency rules. Hence the rules cannot be deleted!");
    }
  }

  /**
   * ICDM-2351
   *
   * @param list
   * @return
   */
  private boolean isValidMultiSelect(final List list) {
    boolean validMultiSelect = true;
    for (Object obj : list) {
      if (obj instanceof IParameter) {
        IParameter param = (IParameter) obj;
        validMultiSelect = this.paramDataProvider.hasDependency(param) &&
            CommonUtils.isNotEmpty(this.paramDataProvider.getRuleList(param));
        return !validMultiSelect;
      }
    }
    return validMultiSelect;
  }


  /**
   * @param page
   */
  private void refreshViewer(final AbstractFormPage page) {
    if (page instanceof ParametersRulePage) {
      ParametersRulePage rulePg = (ParametersRulePage) page;
      rulePg.refreshParamPage();
      rulePg.getEditor().getListPage().refreshListPage();
    }
    else if (page instanceof ListPage) {
      ListPage listPg = (ListPage) page;
      if (null != listPg.getFcTableViewer()) {
        listPg.getEditor().refreshSelectedParamRuleData();
        listPg.refreshListPage();
        listPg.getParamTabSec().getParamTab().updateStatusBar(false);
      }
    }
  }
}
