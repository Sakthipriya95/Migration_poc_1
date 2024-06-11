/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author mkl2cob ICDM-528 Properties view source class to display properties of CDRFuncParameter/RuleSetParameter
 */
public class CDRParamPropertiesViewSource extends AbstractDataObjectPropertySource<IDataObject> {

  /**
   * Ready for series
   */
  private static final String READY_FOR_SERIES = "Ready for series";

  /**
   * Code word
   */
  private static final String CODEWORD = "Codeword";

  /**
   * Class
   */
  private static final String CLASS = "Class";

  /**
   * Value Type
   */
  private static final String TYPE = "Type";
  /**
   * Maturity Level
   */
  private static final String MATURITY_LEVEL = "Maturity Level";
  /**
   * EXACT_MATCH
   */
  private static final String EXACT_MATCH = "Exact Match";

  /**
   * ParameterDataProvider from editor
   */
  private final ParameterDataProvider<IParameterAttribute, IParameter> paramDataProvider;

  /**
   * flag feild is set to false for params with dependency if there is no dependancy then we display the feilds upper ,
   * lower limit ,etc in the param level itself else it wil be displayed in the rule level
   */
  private boolean flag;

  /**
   * Constructor
   *
   * @param adaptableObject instance of CDRFuncParameter for which properties have to be displayed
   */
  public CDRParamPropertiesViewSource(final IParameter adaptableObject, final ParameterDataProvider paramDataProvider) {
    super(adaptableObject);
    this.paramDataProvider = paramDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {

    return new String[] {
        TYPE,
        CLASS,
        CODEWORD,
        CommonUIConstants.LOWER_LIMIT_VALUE,
        CommonUIConstants.UPPER_LIMIT_VALUE,
        CommonUIConstants.REF_VALUE,
        EXACT_MATCH,
        MATURITY_LEVEL,
        READY_FOR_SERIES,
        CommonUIConstants.HINT_VALUE,
        CommonUIConstants.UNIT_VALUE };


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String objID) {

    IDataObject dataObj = getDataObject();
    if (dataObj instanceof IParameter) {

      IParameter param = (IParameter) dataObj;
      return getValue(objID, param);
    }


    return null;

  }

  /**
   * @param objID
   * @param cdrFuncParam
   */
  private String getValue(final String descField, final IParameter cdrFuncParam) {
    String result = "";
    this.flag = true;
    if (this.paramDataProvider.hasDependency(cdrFuncParam)) {
      this.flag = false;
    }

    switch (descField) {
      case TYPE:
        result = cdrFuncParam.getType();
        break;
      case CLASS:
        result = cdrFuncParam.getpClassText();
        break;
      case CODEWORD:
        result = setDescForCodeword(cdrFuncParam);
        break;
      case CommonUIConstants.LOWER_LIMIT_VALUE:
        result = getLowerLimit(cdrFuncParam);
        break;
      case CommonUIConstants.UPPER_LIMIT_VALUE:
        result = getUpperLimit(cdrFuncParam);
        break;
      case CommonUIConstants.REF_VALUE:
        result = getParamRefValue(cdrFuncParam);
        break;
      case READY_FOR_SERIES:
        result = this.paramDataProvider.getReviewRule(cdrFuncParam) == null ? ""
            : ReviewRuleUtil.getReadyForSeriesUIVal(this.paramDataProvider.getReviewRule(cdrFuncParam));
        break;
      case MATURITY_LEVEL:
        result = this.paramDataProvider.getReviewRule(cdrFuncParam) == null ? ""
            : RuleMaturityLevel
                .getIcdmMaturityLvlEnumForSsdText(this.paramDataProvider.getReviewRule(cdrFuncParam).getMaturityLevel())
                .getICDMMaturityLevel();
        break;
      case EXACT_MATCH:
        result = CommonUtils.checkNull(this.paramDataProvider.getReviewRule(cdrFuncParam) == null ? ""
            : getExactMatch(this.paramDataProvider.getReviewRule(cdrFuncParam)));
        break;
      case CommonUIConstants.UNIT_VALUE:
        result = CommonUtils.checkNull(this.paramDataProvider.getReviewRule(cdrFuncParam) == null ? ""
            : this.paramDataProvider.getReviewRule(cdrFuncParam).getUnit());
        break;
      case CommonUIConstants.HINT_VALUE:
        result = CommonUtils.checkNull(this.paramDataProvider.getReviewRule(cdrFuncParam) == null ? ""
            : this.paramDataProvider.getReviewRule(cdrFuncParam).getHint());
        break;
      case PROP_TITLE:
        result = getTitle(cdrFuncParam);
        break;
      default:
        break;
    }

    return result;

  }

  /**
   * @param cdrFuncParam
   * @return
   */
  private String setDescForCodeword(final IParameter cdrFuncParam) {
    String result;
    if (ApicConstants.CODE_YES.equals(cdrFuncParam.getCodeWord())) {
      result = CommonUtilConstants.DISPLAY_YES;
    }
    else {
      result = CommonUtilConstants.DISPLAY_NO;
    }
    return result;
  }


  /**
   * @param reviewRule
   * @return
   */
  private String getExactMatch(final ReviewRule reviewRule) {
    if (reviewRule.isDcm2ssd()) {
      return CommonUtilConstants.DISPLAY_YES;
    }
    return CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * @param cdrFuncParam
   * @return
   */
  private String getTitle(final IParameter cdrFuncParam) {
    // Icdm-640 null check has been made to make sure that the active editor is available
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() != null) {
      final String tilteName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
          .getEditorSite().getPart().getTitleToolTip();
      return "PAR : " + cdrFuncParam.getName() + "- FUNC : " + tilteName;
    }
    return "";
  }

  /**
   * @param funcParam
   * @return
   */
  private String getParamRefValue(final IParameter funcParam) {
    String result = "";
    if (!this.paramDataProvider.hasDependency(funcParam) && (this.paramDataProvider.getRuleList(funcParam) != null) &&
        !this.paramDataProvider.getRuleList(funcParam).isEmpty() &&
        (this.paramDataProvider.getReviewRule(funcParam) != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString();
    }
    return result;

  }


  private String getLowerLimit(final IParameter cdrFuncParam) {
    String result = "";
    if (this.flag && (this.paramDataProvider.getReviewRule(cdrFuncParam) != null) &&
        (this.paramDataProvider.getReviewRule(cdrFuncParam).getLowerLimit() != null)) {
      result = this.paramDataProvider.getReviewRule(cdrFuncParam).getLowerLimit().toString();
    }
    return result;
  }

  private String getUpperLimit(final IParameter cdrFuncParam) {

    String result = "";
    if (this.flag && (this.paramDataProvider.getReviewRule(cdrFuncParam) != null) &&
        (this.paramDataProvider.getReviewRule(cdrFuncParam).getUpperLimit() != null)) {
      result = this.paramDataProvider.getReviewRule(cdrFuncParam).getUpperLimit().toString();
    }
    return result;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    return "PAR : " + getDataObject().getName();
  }

}
