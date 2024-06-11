/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.pages.ConfigBasedRulesPage;


/**
 * @author rgo7cob
 */
public class ShowRuleAction<D extends IParameterAttribute, P extends IParameter> extends Action {


  private final ConfigBasedRulesPage<D, P> configPage;
  private final ReviewRule rule;
  private final ParameterDataProvider paramDataProvider;

  public ShowRuleAction(final IMenuManager menuManager, final ConfigBasedRulesPage<D, P> configPage,
      final ReviewRule rule, final ParameterDataProvider paramDataProvider) {

    setText("Navigate to Parameter Rules");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.REVIEW_RULES_16X16);
    setImageDescriptor(imageDesc);
    menuManager.add(this);
    this.configPage = configPage;
    this.rule = rule;
    this.paramDataProvider = paramDataProvider;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    IParamRuleResponse<D, P> paramRulesOutput = this.paramDataProvider.getParamRulesOutput();
    P param = paramRulesOutput.getParamMap().get(this.rule.getParameterName());
    ((ReviewParamEditorInput) this.configPage.getEditorInput()).setCdrFuncParam(param);
    this.configPage.getEditor().setActivePage("Parameter Rules");
    this.configPage.getEditor().getParamRulesPage().setActive(true);

  }

}
