package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * CDRParamFilter.java This class handles the common filters for the CDRParam table viewer
 */
public class CDRParamFilter<D extends IParameterAttribute, P extends IParameter> extends AbstractViewerFilter {

  private final ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * @param parameterDataProvider
   */
  public CDRParamFilter(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof IParameter) {
      final IParameter funcParam = (IParameter) element;

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
    // Check for Param name
    if (matchText(funcParam.getName())) {
      return true;
    }
    if (matchText(funcParam.getLongName())) {
      return true;
    }
    // Check for Param class
    if (matchText(funcParam.getpClassText())) {
      return true;
    }
    // Check for Param Code Word
    return (matchText(funcParam.getCodeWord()));
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
      // Check for Lower Limit
      if ((reviewRule.getLowerLimit() != null) && matchText(reviewRule.getLowerLimit().toString())) {
        return true;
      }
      // Check for Upper Limit
      if ((reviewRule.getUpperLimit() != null) && matchText(reviewRule.getUpperLimit().toString())) {
        return true;
      }
      // check for bitwise rule
      if ((reviewRule.getBitWiseRule() != null) && matchText(reviewRule.getBitWiseRule())) {
        return true;
      }
      // (iCDM-577)
      // Check for Ref Value
      if (matchText(reviewRule.getRefValueDispString())) {
        return true;
      }
      // Check for Ready for series Auto or Manual
      if (matchText(ReviewRuleUtil.getReadyForSeriesUIVal(reviewRule))) {
        return true;
      }
      // Check for Unit
      if (matchText(reviewRule.getUnit())) {
        return true;
      }
    }
    return false;
  }

}