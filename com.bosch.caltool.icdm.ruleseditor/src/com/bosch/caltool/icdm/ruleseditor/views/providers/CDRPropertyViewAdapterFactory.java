/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;


/**
 * @author mkl2cob ICDM-528 Adapter factory to display properties of CDRFuncParameter
 */
public class CDRPropertyViewAdapterFactory implements IAdapterFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {
    if (adapterType == IPropertySource.class) {
      ParameterDataProvider<IParameterAttribute, IParameter> paramDataProvider = null;
      if ((adaptableObject instanceof IParameter) || (adaptableObject instanceof IParameterAttribute) ||
          (adaptableObject instanceof ConfigBasedParam)) {
        paramDataProvider = getParamDataProvider();
      }
      if (adaptableObject instanceof IParameter) {

        if (paramDataProvider != null) {
          return new CDRParamPropertiesViewSource((IParameter) adaptableObject, paramDataProvider);
        }
      }
      else if (adaptableObject instanceof ConfigBasedParam) {
        return new CDRParamPropertiesViewSource(((ConfigBasedParam) adaptableObject).getParameter(), paramDataProvider);
      }
      else if (adaptableObject instanceof ReviewRule) {
        return new CDRRulePropertiesViewSource((ReviewRule) adaptableObject);
      }
      else if (adaptableObject instanceof RuleDefinition) {
        return new CDRRulePropertiesViewSource(getReviewRule((RuleDefinition) adaptableObject));
      }
      else if (adaptableObject instanceof DefaultRuleDefinition) {
        return new CDRRulePropertiesViewSource(((DefaultRuleDefinition) adaptableObject).getReviewRule());
      }
      else if (adaptableObject instanceof IParameterAttribute) {
        return new CDRParamAttrPropertiesViewSource((IParameterAttribute) adaptableObject, paramDataProvider);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public final Class[] getAdapterList() {
    return new Class[] { IPropertySource.class };
  }


  /**
   * @param ruleDef
   * @return ReviewRule
   */
  private ReviewRule getReviewRule(final RuleDefinition ruleDef) {

    if (ruleDef.isDefaultRule()) {
      return ruleDef.getDefaultRule();
    }

    return CommonUtils.isNotEmpty(ruleDef.getRuleMapping()) ? ruleDef.getRuleMapping().values().iterator().next()
        : getCommonRule(ruleDef);
  }

  private ReviewRule getCommonRule(final RuleDefinition ruleDef) {
    ReviewRule rvwRule = new ReviewRule();
    rvwRule.setParameterName(ruleDef.getParamName());
    rvwRule.setLowerLimit(ruleDef.getLowerLimit());
    rvwRule.setUpperLimit(ruleDef.getUpperLimit());
    rvwRule.setRefValueDispString(ruleDef.getRefValueDisplayString());
    rvwRule.setDcm2ssd(ruleDef.isExactMatch());
    rvwRule.setUnit(ruleDef.getUnit());
    rvwRule.setReviewMethod(ruleDef.getReviewMethod());
    rvwRule.setMaturityLevel(ruleDef.getMaturityLevel());
    rvwRule.setRuleCreatedDate(ruleDef.getRuleCreatedDate());
    rvwRule.setRuleCreatedUser(ruleDef.getRuleCreatedUser());
    return rvwRule;
  }

  /**
   * @return instance of ParameterDataProvider
   */
  private ParameterDataProvider<IParameterAttribute, IParameter> getParamDataProvider() {
    AbstractFormEditor editor = CommonUiUtils.getInstance().getActiveEditor();

    if (editor instanceof ReviewParamEditor) {
      return ((ReviewParamEditor) editor).getEditorInput().getParamDataProvider();
    }
    return null;
  }

}
