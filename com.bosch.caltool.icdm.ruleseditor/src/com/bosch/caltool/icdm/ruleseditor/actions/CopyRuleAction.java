/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.caltool.icdm.client.bo.cdr.CdrRuleWrapper;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author rgo7cob
 */
public class CopyRuleAction<D extends IParameterAttribute, P extends IParameter> extends Action {


  /**
   * can copy the rule
   */
  private boolean canCopyRule;
  /**
   * selected Rule
   */
  private ReviewRule selectedRule;
  private String paramName;
  private String paramType;
  private final ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * @param firstElement firstElement
   * @param paramColln paramColln
   * @param paramModifiable isModifiable
   */
  public CopyRuleAction(final Object firstElement, final boolean paramModifiable,
      final ParameterDataProvider<D, P> parameterDataProvider) {
    this.canCopyRule = paramModifiable;
    this.parameterDataProvider = parameterDataProvider;
    setModifiable(firstElement);
    setProperties();
  }

  /**
   * set the modifiable flag here
   *
   * @param firstElement
   */
  private void setModifiable(final Object firstElement) {

    IParameter parameter;
    if (this.canCopyRule) {
      if (firstElement instanceof IParameter) {
        parameter = (IParameter) firstElement;
        this.paramName = parameter.getName();
        if (this.parameterDataProvider.getReviewRule(parameter) == null) {
          this.canCopyRule = false;
        }
        else {
          this.selectedRule = this.parameterDataProvider.getReviewRule(parameter);
          this.paramType = parameter.getType();
          this.canCopyRule = true;
        }
      }
      else if (firstElement instanceof ReviewRule) {
        this.selectedRule = (ReviewRule) firstElement;
        this.paramName = this.selectedRule.getParameterName();
        this.paramType = this.selectedRule.getValueType();
      }
      else if (firstElement instanceof DefaultRuleDefinition) {
        this.selectedRule = ((DefaultRuleDefinition) firstElement).getReviewRule();
        this.paramName = this.selectedRule.getParameterName();
        this.paramType = this.selectedRule.getValueType();
      }
      else {
        this.canCopyRule = false;
      }

    }
  }

  /**
   * set the properties
   */
  private void setProperties() {
    ImageDescriptor imageDesc;

    imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_16X16);
    setText("Copy Rule");
    setEnabled(false);
    // if can copy rule is true then copy the rule.
    if (this.canCopyRule) {
      setEnabled(true);
    }
    setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    CdrRuleWrapper copiedRule = new CdrRuleWrapper(this.selectedRule, this.paramName, this.paramType);
    ICDMClipboard.getInstance().setCopiedObject(copiedRule);
    super.run();
  }


}
