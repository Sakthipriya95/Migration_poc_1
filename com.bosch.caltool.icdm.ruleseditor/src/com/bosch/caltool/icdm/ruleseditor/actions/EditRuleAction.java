/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleEditInput;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.dialogs.EditRuleDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;


/**
 * @author rgo7cob
 */
public class EditRuleAction<D extends IParameterAttribute, P extends IParameter> extends AbstractRuleEditAction {


  private final RuleEditInput input;


  private final ReviewParamEditor editor;

  /**
   * @param input
   * @param editor2
   */
  public EditRuleAction(final RuleEditInput input, final ReviewParamEditor editor) {
    this.input = input;
    this.editor = editor;

    setEnabled(true);
    // ICDM-1190
    if (input.isReadOnlyMode()) {
      setText("Show rule details");
    }
    else {
      setText("Show/Edit rule details");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    openEditRuleDialog(this.input);

  }


  /**
   * {@inheritDoc}
   */
  public void openEditRuleDialog(final RuleEditInput input) {
    // ICDM-1162
    IParameter param = null;
    DefaultRuleDefinition ruleDef = null;
    ParameterDataProvider<D, P> paramDataProvider = this.input.getParamDataProvider();
    if (input.getFirstElement() instanceof DefaultRuleDefinition) {
      ruleDef = (DefaultRuleDefinition) input.getFirstElement();


      param = paramDataProvider.getParamRulesOutput().getParamMap().get(ruleDef.getReviewRule().getParameterName());
    }
    else if (input.getFirstElement() instanceof IParameter) {
      param = (IParameter) input.getFirstElement();
    }
    else if (input.getFirstElement() instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) input.getFirstElement();
      param = paramDataProvider.getParamRulesOutput().getParamMap().get(rule.getParameterName());

    }

    Map<IParameter, Set<RuleInfoSection>> ruleInfoSectionMap;

    ruleInfoSectionMap = this.editor.getRuleInfoSectionMap();
    Set<RuleInfoSection> ruleInfoSectionset = ruleInfoSectionMap.get(param);

    boolean dialogAlreadyOpen;
    EditRuleDialog editRuleDialog = null;

    dialogAlreadyOpen = checkEditDialogAlreadyOpen(ruleInfoSectionset);


    if (!dialogAlreadyOpen) {
      openNewDialog(input.getFirstElement(), input.getCdrFunction(), this.editor, input.getCheckValCalDataObj(),
          input.isReadOnlyMode(), input.getPidcVersName(), input.getResultName(), input.getAttrValModel(), param,
          ruleDef, ruleInfoSectionset, editRuleDialog);

    }

  }

  /**
   * @param firstElement
   * @param cdrFunction
   * @param editor
   * @param checkValCalDataObj
   * @param readOnlyMode
   * @param pidcVersName
   * @param resultName
   * @param attrValModel
   * @param param
   * @param ruleDef
   * @param ruleInfoSectionset
   * @param editRuleDialog
   */
  private void openNewDialog(final Object firstElement, final ParamCollection cdrFunction,
      final ReviewParamEditor editor, final CalData checkValCalDataObj, final boolean readOnlyMode,
      final String pidcVersName, final String resultName, final SortedSet<AttributeValueModel> attrValModel,
      final IParameter param, final DefaultRuleDefinition ruleDef, final Set<RuleInfoSection> ruleInfoSectionset,
      EditRuleDialog editRuleDialog) {

    ParameterDataProvider<D, P> paramDataProvider = this.input.getParamDataProvider();
    // ICDM-1162
    // New Dialog to edit the Rule
    if ((firstElement instanceof DefaultRuleDefinition) && (ruleDef != null)) {
      editRuleDialog =
          new EditRuleDialog<D, P>(Display.getCurrent().getActiveShell(), param, cdrFunction, editor.getListPage(),
              ruleDef.getReviewRule(), readOnlyMode, paramDataProvider, this.input.getParamColDataProvider());
    }
    else if (firstElement instanceof IParameter) {
      editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
          editor.getListPage(), checkValCalDataObj, readOnlyMode, pidcVersName, resultName, attrValModel,
          paramDataProvider, this.input.getParamColDataProvider());
    }
    else if (firstElement instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) firstElement;
      Object selectedPage = editor.getSelectedPage();
      editRuleDialog = openEditDlgForRule(cdrFunction, editor, checkValCalDataObj, readOnlyMode, pidcVersName,
          resultName, param, rule, selectedPage);
    }
    editRuleDialog.setParamDataProvider(paramDataProvider);
    editRuleDialog.create();
    addEditRuleInfoSectToMap(editor, param, ruleInfoSectionset, editRuleDialog);
    editRuleDialog.open();

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
      final IParameter param, final ReviewRule rule, final Object selectedPage) {
    EditRuleDialog editRuleDialog;
    ParameterDataProvider<D, P> paramDataProvider = this.input.getParamDataProvider();
    if (selectedPage instanceof ListPage) {
      if (checkValCalDataObj != null) {
        editRuleDialog =
            new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction, editor.getListPage(), rule,
                checkValCalDataObj, pidcVersName, resultName, paramDataProvider, this.input.getParamColDataProvider());
      }
      else {
        // if the dialog is initiated from list page
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
            editor.getListPage(), rule, readOnlyMode, paramDataProvider, this.input.getParamColDataProvider());
      }
    }
    else {
      // if the dialog is initiated from parameter rules page
      editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
          editor.getParamRulesPage(), rule, readOnlyMode, this.input.getParamColDataProvider(), paramDataProvider);
    }
    return editRuleDialog;
  }


}
