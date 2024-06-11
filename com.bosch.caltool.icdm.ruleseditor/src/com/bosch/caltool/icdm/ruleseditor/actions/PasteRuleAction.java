/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.CdrRuleWrapper;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizard;
import com.bosch.caltool.icdm.ruleseditor.wizards.AddNewConfigWizardDialog;


/**
 * @author rgo7cob
 * @param <D> class which extends IParameterAttribute
 * @param <P> class which extends IParameter
 */
public class PasteRuleAction<D extends IParameterAttribute, P extends IParameter> extends Action {

  /**
   * can copy the rule
   */
  private boolean canPasteRule;
  /**
   * selected Rule
   */
  private CdrRuleWrapper copiedRule;
  private final ReviewParamEditor editor;
  private final ParamCollection paramColln;
  private IParameter selectedParam;
  private boolean pasteToDep;
  private ReviewRule targetRule;
  private ReviewRule originalRule;
  private final ParamCollectionDataProvider paramColDataProvider;
  private final ParameterDataProvider<D, P> paramDataProvider;

  /**
   * @param firstElement firstElement
   * @param paramColln paramColln
   * @param editor editor
   * @param paramModifiable isModifiable
   * @param cdrFuncParam cdr function parameter
   * @param paramColDataProvider parameter column data provider
   * @param paramDataProvider parameter data provider
   */
  public PasteRuleAction(final Object firstElement, final ParamCollection paramColln, final ReviewParamEditor editor,
      final boolean paramModifiable, final IParameter cdrFuncParam,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider<D, P> paramDataProvider) {
    this.canPasteRule = paramModifiable;
    this.editor = editor;
    this.paramColln = paramColln;
    this.canPasteRule = paramModifiable;
    this.selectedParam = cdrFuncParam;
    this.paramColDataProvider = paramColDataProvider;
    this.paramDataProvider = paramDataProvider;

    setModifiable(firstElement);
    setProperties();
  }


  /**
   * set the modifiable flag here
   *
   * @param firstElement
   */
  private void setModifiable(final Object firstElement) {
    IParameter funcParam;

    if (this.canPasteRule) {
      Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
      if (copiedObject == null) {
        this.canPasteRule = false;
        return;
      }
      else if (copiedObject instanceof CdrRuleWrapper) {
        this.copiedRule = (CdrRuleWrapper) copiedObject;
      }
      else {
        this.canPasteRule = false;
      }
      // Rule is pasted to param
      if (firstElement instanceof IParameter) {
        funcParam = (IParameter) firstElement;
        this.selectedParam = funcParam;
        this.canPasteRule = true;
      }
      // Rule is pasted to rule.
      else if (firstElement instanceof ReviewRule) {
        this.pasteToDep = true;
        this.targetRule = (ReviewRule) firstElement;
        this.canPasteRule = true;
      }

      else if (firstElement instanceof DefaultRuleDefinition) {
        this.pasteToDep = true;
        this.targetRule = ((DefaultRuleDefinition) firstElement).getReviewRule();
        this.canPasteRule = true;
      }
    }
  }

  /**
   * set the properties
   */
  private void setProperties() {
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    setText("Paste Rule");
    setEnabled(false);
    // if can copy rule is true then copy the rule.
    if (this.canPasteRule) {
      setEnabled(true);
    }
    setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    if (!this.copiedRule.getParamType().equals(this.selectedParam.getType())) {
      CDMLogger.getInstance().errorDialog("Rule can be pasted only to the same parameter type", Activator.PLUGIN_ID);
      return;
    }

    boolean insertRule = false;
    Map<IParameter, Set<RuleInfoSection>> ruleInfoSectionMap;
    ruleInfoSectionMap = this.editor.getRuleInfoSectionMap();
    if (this.targetRule == null) {
      this.targetRule = this.paramDataProvider.getReviewRule(this.selectedParam);
    }

    ReviewRule sourceRule = this.copiedRule.getRule();
    if (this.targetRule == null) {
      this.targetRule = new ReviewRule();
      insertRule = true;
    }
    this.originalRule = new ReviewRule();
    // Copy the properties in case of Cancel update.Copy the target to
    ReviewRuleUtil.updateRuleProperties(this.targetRule, this.originalRule, this.selectedParam.getName());
    ReviewRuleUtil.updateRuleProperties(sourceRule, this.targetRule, this.selectedParam.getName());

    if (this.paramDataProvider.hasDependency(this.selectedParam)) {
      openRuleWizard(this.targetRule, this.pasteToDep);
      return;
    }
    Object selectedPage = this.editor.getSelectedPage();
    EditRuleDialog editRuleDialog;
    Set<RuleInfoSection> ruleInfoSectionset = ruleInfoSectionMap.get(this.selectedParam);
    ReviewRuleActionSet reviewRuleActionSet = new ReviewRuleActionSet();
    boolean dialogAlreadyOpen = reviewRuleActionSet.checkEditDialogAlreadyOpen(ruleInfoSectionset);
    if (!dialogAlreadyOpen) {
      // ICDM-1162
      // New Dialog to edit the Rule
      if (selectedPage instanceof ListPage) {

        // if the dialog is initiated from list page
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), this.selectedParam, this.paramColln,
            this.editor.getListPage(), this.targetRule, false, this.paramDataProvider, this.paramColDataProvider);
      }
      else {
        // if the dialog is initiated from parameter rules page
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), this.selectedParam, this.paramColln,
            this.editor.getParamRulesPage(), this.targetRule, false, this.paramColDataProvider, this.paramDataProvider);
      }
      editRuleDialog.getRuleInfoSection().setUpdate(!insertRule);
      editRuleDialog.setUnmodifiedRule(this.originalRule);
      editRuleDialog.create();
      reviewRuleActionSet.addEditRuleInfoSectToMap(this.editor, this.selectedParam, ruleInfoSectionset, editRuleDialog);
      editRuleDialog.getSaveBtn().setEnabled(true);
      editRuleDialog.open();

    }
    super.run();
  }

  /**
   * @param targetReviewRule targetRule
   * @param update update
   */
  private void openRuleWizard(final ReviewRule targetReviewRule, final boolean update) {
    List<ReviewRule> ruleList = createRuleList(targetReviewRule);
    ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
    Set<RuleInfoSection> ruleInfoSet = this.editor.getRuleInfoSectionMap().get(this.selectedParam);
    if (!reviewParamActionSet.checkWizardDialogAlreadyOpen(ruleInfoSet, null)) {
      AbstractFormPage formPage;
      if (this.editor.getSelectedPage() instanceof ListPage) {
        formPage = (ListPage) this.editor.getSelectedPage();
      }
      else if (this.editor.getSelectedPage() instanceof DetailsPage) {
        formPage = (DetailsPage) this.editor.getSelectedPage();
      }
      else {
        formPage = (ParametersRulePage) this.editor.getSelectedPage();
      }

      AddNewConfigWizard wizard =
          new AddNewConfigWizard(this.selectedParam, this.paramColln, formPage, update, ruleList, false, false, null,
              null, false, !update, this.editor.getEditorInput().getParamDataProvider(), this.paramColDataProvider);
      AddNewConfigWizardDialog dialog =
          new AddNewConfigWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      wizard.getWizardData().setUnModifiedTargetRule(this.originalRule);
      dialog.create();
      dialog.open();

      reviewParamActionSet.addToWizardRuleInfoSectionMap(this.selectedParam, dialog, this.editor, ruleInfoSet);
      wizard.getCreateEditRulePage().setPageComplete(true);

    }
  }

  /**
   * @param targetReviewRule
   * @return
   */
  private List<ReviewRule> createRuleList(final ReviewRule targetReviewRule) {
    List<ReviewRule> ruleList = new ArrayList<>();

    ruleList.add(targetReviewRule);
    return ruleList;

  }

}
