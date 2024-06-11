/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;


import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author bru2cob
 */
public class ParamNatInputToColConverter extends AbstractNatInputToColumnConverter {

  private boolean flag;
  private ParameterDataProvider parameterDataProvider;

  /**
   * @param parameterDataProvider
   */
  public ParamNatInputToColConverter(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object element, final int columnIndex) {
    String result = ApicConstants.EMPTY_STRING;
    this.flag = true;
    if (element instanceof IParameter) {
      if (element instanceof RuleSetParameter) {
        result = getRulesetParamDetails((RuleSetParameter) element, columnIndex);
      }
      else {
        result = getParamDetails((IParameter) element, columnIndex);
      }
    }

    // ICDM-1070
    else if (element instanceof ReviewRule) {
      result = getRuleDetails((ReviewRule) element, columnIndex);
    }
    else if (element instanceof DefaultRuleDefinition) {
      result = getDefaultRuleDetails((DefaultRuleDefinition) element, columnIndex);
    }
    return result;
  }


  /**
   * Label provider for default rule
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String getDefaultRuleDetails(final DefaultRuleDefinition defaultRule, final int columnIndex) {
    String result = CommonUIConstants.EMPTY_STRING;
    ReviewRule rule = defaultRule.getReviewRule();
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_3:
        result = defaultRule.getDescription();
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        if (rule.getLowerLimit() != null) {
          result = rule.getLowerLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        if (rule.getUpperLimit() != null) {
          result = rule.getUpperLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        result = rule.getBitWiseRule();
        break;
      case CommonUIConstants.COLUMN_INDEX_9:
        result = defaultRule.getReviewRule().getRefValueDispString();
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = ruleExactMatch(rule);
        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        if (rule.getUnit() != null) {
          result = rule.getUnit();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = ReviewRuleUtil.getReadyForSeriesUIVal(rule);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = CommonUtils.checkNull(rule.getHint());
        break;
      case CommonUIConstants.COLUMN_INDEX_14:
        result = CommonUtils.checkNull(rule.getUnicodeRemarks());
        break;
      default:
        result = CommonUIConstants.EMPTY_STRING;
        break;

    }
    return result;
  }


  /**
   * Label provider for rule
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String getRuleDetails(final ReviewRule rule, final int columnIndex) {
    String result = CommonUIConstants.EMPTY_STRING;
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_3:
        result = this.parameterDataProvider.getAttrValString(rule);
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        if (rule.getLowerLimit() != null) {
          result = rule.getLowerLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        if (rule.getUpperLimit() != null) {
          result = rule.getUpperLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_8:

        result = rule.getBitWiseRule();
        break;
      case CommonUIConstants.COLUMN_INDEX_9:

        result = rule.getRefValueDispString();
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = ruleExactMatch(rule);
        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        if (rule.getUnit() != null) {
          result = rule.getUnit();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = ReviewRuleUtil.getReadyForSeriesUIVal(rule);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = CommonUtils.checkNull(rule.getHint());
        break;
      case CommonUIConstants.COLUMN_INDEX_14:
        result = CommonUtils.checkNull(rule.getUnicodeRemarks());
        break;
      default:
        result = CommonUIConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  /**
   * Returns the exact match of rule
   *
   * @param rule selceted rule
   * @return exact match
   */
  private String ruleExactMatch(final ReviewRule rule) {
    String result = CommonUIConstants.EMPTY_STRING;
    if ((rule.getRefValueDispString() != null) && !rule.getRefValueDispString().isEmpty()) {
      if (rule.isDcm2ssd()) {
        result = CommonUtilConstants.DISPLAY_YES;
      }
      else {
        result = CommonUtilConstants.DISPLAY_NO;
      }
    }
    return result;
  }


  /**
   * Label provider for parameter
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String getParamDetails(final IParameter param, final int columnIndex) {
    String result;

    if (this.parameterDataProvider.hasDependency(param)) {
      this.flag = false;
    }
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_2:
        result = param.getName();
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        result = param.getLongName();
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        result = param.getpClassText();
        break;
      case CommonUIConstants.COLUMN_INDEX_5:
        result = this.parameterDataProvider.getCodeWordText(param);
        break;

      // Changes Implemented For the Cdr rule values
      case CommonUIConstants.COLUMN_INDEX_6:
        result = getParmLowerLmt(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        result = getParamUpperLmt(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        result = getParamBitWiseRule(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_9:

        result = getParamRefValue(param);
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = getParamExactMatch(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        result = getParamUnit(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = getParamReviewMethod(param);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = getParamRemarks(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_14:
        result = getParamRemarksUnicode(param);
        break;
      default:
        result = CommonUIConstants.EMPTY_STRING;
        break;
    }
    return result;
  }

  private String getRulesetParamDetails(final RuleSetParameter param, final int columnIndex) {
    String result = CommonUIConstants.EMPTY_STRING;

    if (this.parameterDataProvider.hasDependency(param)) {
      this.flag = false;
    }
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_2:
        result = param.getName();
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        result = param.getLongName();
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        result = param.getpClassText();
        break;
      case CommonUIConstants.COLUMN_INDEX_5:
        result = this.parameterDataProvider.getCodeWordText(param);
        break;

      // Changes Implemented For the Cdr rule values
      case CommonUIConstants.COLUMN_INDEX_6:
        result = getParmLowerLmt(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        result = getParamUpperLmt(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        result = getParamBitWiseRule(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_9:

        result = getParamRefValue(param);
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = getParamExactMatch(param);

        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        result = getParamUnit(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = getParamReviewMethod(param);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = getParamRemarks(param);
        break;
      case CommonUIConstants.COLUMN_INDEX_14:
        result = getParamRemarksUnicode(param);
        break;

      case CommonUIConstants.COLUMN_INDEX_15:
        if (CommonUtils.isNotNull(param.getParamType())) {
          result = param.getParamType();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_16:
        if (CommonUtils.isNotNull(param.getParamResp())) {
          result = param.getParamResp();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_17:
        if (CommonUtils.isNotNull(param.getSysElement())) {
          result = param.getSysElement();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_18:
        if (CommonUtils.isNotNull(param.getHwComponent())) {
          result = param.getHwComponent();
        }
        break;
      default:
        result = CommonUIConstants.EMPTY_STRING;
        break;
    }
    return result;
  }


  /**
   * @param param
   * @return
   */
  private String getParamRefValue(final IParameter param) {
    String result = CommonUIConstants.EMPTY_STRING;
    ReviewRule reviewRule = getReviewRule(param);
    if (!this.parameterDataProvider.hasDependency(param) && (this.parameterDataProvider.getRuleList(param) != null) &&
        !this.parameterDataProvider.getRuleList(param).isEmpty() && (reviewRule != null)) {
      result = reviewRule.getRefValueDispString();
    }
    return result;

  }

  // ICDM-2152
  /**
   * @param param
   * @return
   */
  private String getParamRemarks(final IParameter param) {
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag) {
      return null == reviewRule ? CommonUIConstants.EMPTY_STRING : CommonUtils.checkNull(reviewRule.getHint());
    }
    return CommonUIConstants.EMPTY_STRING;
  }

  /**
   * @param param
   * @return
   */
  private String getParamRemarksUnicode(final IParameter param) {
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag) {
      return null == reviewRule ? CommonUIConstants.EMPTY_STRING
          : CommonUtils.checkNull(reviewRule.getUnicodeRemarks());
    }
    return CommonUIConstants.EMPTY_STRING;
  }

  /**
   * @param param
   * @return
   */
  private String getParamBitWiseRule(final IParameter param) {
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag) {
      return null == reviewRule ? CommonUIConstants.EMPTY_STRING : CommonUtils.checkNull(reviewRule.getBitWiseRule());
    }
    return CommonUIConstants.EMPTY_STRING;
  }


  /**
   * Sets param unit
   *
   * @param param sel param
   * @return unit
   */
  private String getParamUnit(final IParameter param) {
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag) {
      return null == reviewRule ? CommonUIConstants.EMPTY_STRING : CommonUtils.checkNull(reviewRule.getUnit());
    }
    return CommonUIConstants.EMPTY_STRING;
  }


  /**
   * Sets param upper limit
   *
   * @param param sel param
   * @return upper limit
   */
  private String getParamUpperLmt(final IParameter param) {
    String result = CommonUIConstants.EMPTY_STRING;
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag && (reviewRule != null) && (reviewRule.getUpperLimit() != null)) {
      result = reviewRule.getUpperLimit().toString();
    }
    return result;
  }

  /**
   * @param param
   * @return
   */
  private ReviewRule getReviewRule(final IParameter param) {
    return this.parameterDataProvider.getReviewRule(param);
  }


  /**
   * Sets param lower limit
   *
   * @param param sel param
   * @return lower limit
   */
  private String getParmLowerLmt(final IParameter param) {
    String result = CommonUIConstants.EMPTY_STRING;
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag && (reviewRule != null) && (reviewRule.getLowerLimit() != null)) {
      result = reviewRule.getLowerLimit().toString();
    }
    return result;
  }


  /**
   * Sets param ready for series
   *
   * @param param sel param
   * @return ready for series value
   */
  private String getParamReviewMethod(final IParameter param) {
    ReviewRule reviewRule = getReviewRule(param);
    if (this.flag) {
      return null == reviewRule ? CommonUIConstants.EMPTY_STRING
          : CommonUtils.checkNull(this.parameterDataProvider.getReadyForSeriesUIVal(reviewRule));
    }
    return CommonUIConstants.EMPTY_STRING;
  }


  /**
   * Returns the exact match of param rule
   *
   * @param rule selceted param
   * @return exact match
   */
  private String getParamExactMatch(final IParameter param) {
    String result = CommonUIConstants.EMPTY_STRING;
    ReviewRule reviewRule = getReviewRule(param);

    if (this.flag && (reviewRule != null) && (reviewRule.getRefValueDispString() != null)) {
      if ((reviewRule.getRefValueDispString() != null) && !reviewRule.getRefValueDispString().isEmpty() &&
          reviewRule.isDcm2ssd()) {
        result = CommonUtilConstants.DISPLAY_YES;
      }
      else {
        result = CommonUtilConstants.DISPLAY_NO;
      }
    }
    return result;
  }


  /**
   * @param parameterDataProvider the parameterDataProvider to set
   */
  public void setParameterDataProvider(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }


}
