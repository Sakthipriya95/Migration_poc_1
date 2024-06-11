/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
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
public class EditDefaultRuleAction<D extends IParameterAttribute, P extends IParameter> extends AbstractRuleEditAction {


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    openEditRuleDialog();
  }

  private final ParameterDataProvider<D, P> parameterDataProvider;
  private final ParamCollectionDataProvider paramColDataProvider;
  private final Object firstElement;
  private final ParamCollection cdrFunction;
  private final ReviewParamEditor editor;
  private final CalData checkValCalDataObj;
  private final boolean readOnlyMode;
  private final String pidcVersName;
  private final String resultName;


  /**
   * @param parameterDataProvider parameterDataProvider
   * @param paramColDataProvider paramColDataProvider
   */
  public EditDefaultRuleAction(final ParameterDataProvider<D, P> parameterDataProvider,
      final ParamCollectionDataProvider paramColDataProvider, final MenuManager menuMgr, final Object firstElement,
      final ParamCollection cdrFunction, final ReviewParamEditor editor, final CalData checkValCalDataObj,
      final boolean readOnlyMode, final String pidcVersName, final String resultName) {
    this.parameterDataProvider = parameterDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    this.firstElement = firstElement;
    this.cdrFunction = cdrFunction;
    this.editor = editor;
    this.checkValCalDataObj = checkValCalDataObj;
    this.readOnlyMode = readOnlyMode;
    this.pidcVersName = pidcVersName;
    this.resultName = resultName;
    setEnabled(true);
    // ICDM-1190
    if (readOnlyMode) {
      setText("Show rule details");
    }
    else {
      setText("Show/Edit rule details");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    setImageDescriptor(imageDesc);
    menuMgr.add(this);
  }


  /**
   * {@inheritDoc}
   *
   * @param parameterDataProvider2
   * @param paramColDataProvider
   */
  public void openEditRuleDialog() {
    // ICDM-1162
    IParameter param = null;
    DefaultRuleDefinition ruleDef = null;
    if (this.firstElement instanceof DefaultRuleDefinition) {
      ruleDef = (DefaultRuleDefinition) this.firstElement;
      param = this.parameterDataProvider.getParamRulesOutput().getParamMap()
          .get(ruleDef.getReviewRule().getParameterName());
    }
    else if (this.firstElement instanceof IParameter) {
      param = (IParameter) this.firstElement;
    }
    else if (this.firstElement instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) this.firstElement;
      param = this.parameterDataProvider.getParamRulesOutput().getParamMap().get(rule.getParameterName());
    }

    Map<IParameter, Set<RuleInfoSection>> ruleInfoSectionMap;

    ruleInfoSectionMap = this.editor.getRuleInfoSectionMap();
    Set<RuleInfoSection> ruleInfoSectionset = ruleInfoSectionMap.get(param);

    boolean dialogAlreadyOpen;
    EditRuleDialog editRuleDialog = null;

    dialogAlreadyOpen = checkEditDialogAlreadyOpen(ruleInfoSectionset);


    if (!dialogAlreadyOpen) {
      // ICDM-1162
      // New Dialog to edit the Rule
      if ((this.firstElement instanceof DefaultRuleDefinition) && (ruleDef != null)) {
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, this.cdrFunction,
            this.editor.getListPage(), ruleDef.getReviewRule(), this.readOnlyMode, this.parameterDataProvider,
            this.paramColDataProvider);
      }
      else if (this.firstElement instanceof IParameter) {
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, this.cdrFunction,
            this.editor.getListPage(), this.checkValCalDataObj, this.readOnlyMode, this.pidcVersName, this.resultName,
            null, this.parameterDataProvider, this.paramColDataProvider);
      }
      else if (this.firstElement instanceof ReviewRule) {
        ReviewRule rule = (ReviewRule) this.firstElement;
        Object selectedPage = this.editor.getSelectedPage();
        editRuleDialog = openEditDlgForRule(this.cdrFunction, this.editor, this.checkValCalDataObj, this.readOnlyMode,
            this.pidcVersName, this.resultName, param, rule, selectedPage);
      }

      if (null != editRuleDialog) {
        editRuleDialog.create();
        addEditRuleInfoSectToMap(this.editor, param, ruleInfoSectionset, editRuleDialog);
        editRuleDialog.open();
      }

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEditRuleInfoSectToMap(final ReviewParamEditor editor, final IParameter param,
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
    if (selectedPage instanceof ListPage) {
      if (checkValCalDataObj != null) {
        editRuleDialog =
            new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction, editor.getListPage(), rule,
                checkValCalDataObj, pidcVersName, resultName, this.parameterDataProvider, this.paramColDataProvider);
      }
      else {
        // if the dialog is initiated from list page
        editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
            editor.getListPage(), rule, readOnlyMode, this.parameterDataProvider, this.paramColDataProvider);
      }
    }
    else {
      // if the dialog is initiated from parameter rules page
      editRuleDialog = new EditRuleDialog(Display.getCurrent().getActiveShell(), param, cdrFunction,
          editor.getParamRulesPage(), rule, readOnlyMode, this.paramColDataProvider, this.parameterDataProvider);
    }
    return editRuleDialog;
  }

}
