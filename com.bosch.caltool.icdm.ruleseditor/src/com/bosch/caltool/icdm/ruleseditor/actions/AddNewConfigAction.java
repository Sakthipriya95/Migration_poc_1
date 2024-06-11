/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
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
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;


/**
 * @author rgo7cob
 * @param <D>
 * @param <P>
 */
public class AddNewConfigAction<D extends IParameterAttribute, P extends IParameter> extends Action {


  private final List<ReviewRule> ruleList;

  private final IParameter param;

  private final ParamCollection paramColl;

  private final AbstractFormPage page;


  private final ParameterDataProvider<D, P> paramDataProvider;
  private final ParamCollectionDataProvider paramColDataProvider;

  /**
   * @param ruleList ruleList
   * @param param param
   * @param paramColl paramColl
   * @param page page
   */
  public AddNewConfigAction(final List<ReviewRule> ruleList, final IParameter param, final ParamCollection paramColl,
      final AbstractFormPage page, final ParameterDataProvider<D, P> paramDataProvider,
      final ParamCollectionDataProvider paramColDataProvider) {
    super();
    this.ruleList = ruleList;
    this.param = param;
    this.paramColl = paramColl;
    this.page = page;
    this.paramDataProvider = paramDataProvider;
    this.paramColDataProvider = paramColDataProvider;

    setEnabled((ruleList != null) && !ruleList.isEmpty() && this.paramDataProvider.hasDependency(param) &&
        paramColDataProvider.isModifiable(paramColl));
    setText("Copy to new configuration");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_COPY_16X16);
    setImageDescriptor(imageDesc);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    ReviewParamEditor editor = (ReviewParamEditor) this.page.getEditor();
    ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
    Set<RuleInfoSection> ruleInfoSet = editor.getRuleInfoSectionMap().get(AddNewConfigAction.this.param);
    if (!reviewParamActionSet.checkWizardDialogAlreadyOpen(ruleInfoSet, AddNewConfigAction.this.ruleList)) {// ICDM-1244

      AddNewConfigWizard wizard = new AddNewConfigWizard(this.param, this.paramColl, this.page, false, this.ruleList,
          true, false, null, null, !this.paramColDataProvider.isModifiable(this.paramColl), false,
          editor.getEditorInput().getParamDataProvider(), this.paramColDataProvider);
      AddNewConfigWizardDialog dialog =
          new AddNewConfigWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      dialog.create();
      dialog.open();

      addToWizardRuleInfoSectionMap(AddNewConfigAction.this.param, dialog, editor, ruleInfoSet);

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
}
