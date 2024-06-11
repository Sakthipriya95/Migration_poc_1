package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * CDRParamFilter.java This class handles the common filters for the CDRParam table viewer
 */
public class ConfigCDRParamFilter<D extends IParameterAttribute, P extends IParameter> extends AbstractViewerFilter {


  private final ParameterDataProvider<D, P> parameterDataProvider;

  public ConfigCDRParamFilter(final ParameterDataProvider<D, P> parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof ConfigBasedParam) {
      final IParameter funcParam = ((ConfigBasedParam) element).getParameter();

      if (checkParamProperties(funcParam)) {
        return true;
      }

      return checkRuleProperties(funcParam);
    }

    return false;

  }

  /**
   * Check parameter properties
   *
   * @param funcParam parameter
   */
  private boolean checkParamProperties(final IParameter funcParam) {
    if (matchText(funcParam.getName())) {
      return true;
    }
    if (matchText(funcParam.getLongName())) {
      return true;
    }
    if (matchText(funcParam.getpClassText())) {
      return true;
    }
    if (matchText(funcParam.getCodeWord())) {
      return true;
    }
    return false;
  }

  /**
   * Check rule properties
   *
   * @param funcParam parameter
   */
  private boolean checkRuleProperties(final IParameter funcParam) {
    // Filter Implemented For the Rule Values
    ReviewRule reviewRule = this.parameterDataProvider.getReviewRule(funcParam);
    if (reviewRule != null) {
      if ((reviewRule.getLowerLimit() != null) && matchText(reviewRule.getLowerLimit().toString())) {
        return true;
      }
      if ((reviewRule.getUpperLimit() != null) && matchText(reviewRule.getUpperLimit().toString())) {
        return true;
      }
      if (matchText(reviewRule.getBitWiseRule())) {
        return true;
      }
      // (iCDM-577)
      if (matchText(reviewRule.getRefValueDispString())) {
        return true;
      }
      if (matchText(ReviewRuleUtil.getReadyForSeriesUIVal(reviewRule))) {
        return true;
      }
      if (matchText(reviewRule.getUnit())) {
        return true;
      }
    }
    return false;
  }
}