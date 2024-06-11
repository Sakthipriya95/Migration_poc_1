/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.apic.ApicBOUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.rcputils.propertysource.AbstractPropertySource;


/**
 * @author dmo5cob Properties view source class to display properties of CDRRule
 */
public class CDRRulePropertiesViewSource extends AbstractPropertySource {

  /**
   * Title
   */
  private static final String TITLE = "Title";

  /**
   * Description
   */
  private static final String ATTR_DEPENENCY_DESC = "Attribute Dependency";

  /**
   * LOWER_LIMIT
   */
  private static final String LOWER_LIMIT = "Lower Limit";
  /**
   * UPPER_LIMIT
   */
  private static final String UPPER_LIMIT = "Upper Limit";
  /**
   * REF_VALUE
   */
  private static final String REF_VALUE = "Reference Value";
  /**
   * EXACT_MATCH
   */
  private static final String EXACT_MATCH = "Exact Match";
  /**
   * UNIT
   */
  private static final String UNIT = "Unit";
  /**
   * RVW_METHOD
   */
  private static final String READY_FOR_SERIES = "Ready for series";
  /**
   * Created Date
   */
  private static final String CREATED_DATE = "Created Date";
  /**
   * Created User
   */
  private static final String CREATED_USER = "Created User";
  /**
   * Maturity Level
   */
  private static final String MATURITY_LEVEL = "Maturity Level";

  /**
   * Properties field
   */
  private static final String[] PROP_DESC_FIELDS = new String[] {
      ATTR_DEPENENCY_DESC,
      LOWER_LIMIT,
      UPPER_LIMIT,
      REF_VALUE,
      EXACT_MATCH,
      UNIT,
      READY_FOR_SERIES,
      MATURITY_LEVEL,
      CREATED_DATE,
      CREATED_USER };

  /**
   * CDRRule
   */
  private final ReviewRule rule;

  /**
   * Constructor
   *
   * @param rule CDRRule
   */
  public CDRRulePropertiesViewSource(final ReviewRule rule) {
    super();
    this.rule = rule;
  }

  /**
   * {@inheritDoc} Property descriptors
   */
  @Override
  public final IPropertyDescriptor[] getPropertyDescriptors() {
    return CommonUiUtils.createPropertyDescFields(PROP_DESC_FIELDS);
  }

  /**
   * {@inheritDoc} PropertyValue
   */
  @Override
  public final Object getPropertyValue(final Object objID) {
    String result;
    switch (String.valueOf(objID)) {
      case ATTR_DEPENENCY_DESC:
        result = getDescrip();
        break;
      case LOWER_LIMIT:
        result = getLowerLimit();
        break;
      case UPPER_LIMIT:
        result = getUpperLimit();
        break;
      case REF_VALUE:
        result = getRefValue();
        break;
      case EXACT_MATCH:
        result = getExactMatch();
        break;
      case UNIT:
        result = getUnit();
        break;
      case READY_FOR_SERIES:
        result = getReadyForSeries();
        break;
      case CREATED_DATE:
        result = getCreatedDate();
        break;
      case CREATED_USER:
        result = getCreatedUser();
        break;
      case MATURITY_LEVEL:
        result = getMaturityLevel();
        break;
      case TITLE:
        result = getTitleText();
        break;
      default:
        result = "";
    }
    return result;
  }

  /**
   * @param objID
   * @return
   */
  private String getDescrip() {
    // Description
    return ApicBOUtil.getAttrValueString(this.rule.getDependencyList());
  }

  /**
   * @param objID
   * @return
   */
  private String getLowerLimit() {
    // Lower limit
    if (this.rule.getLowerLimit() != null) {
      return this.rule.getLowerLimit().toString();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getUpperLimit() {
    // Upper limit
    if (this.rule.getUpperLimit() != null) {
      return this.rule.getUpperLimit().toString();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getRefValue() {
    // Reference value
    if (CommonUtils.isNotEmptyString(this.rule.getRefValueDispString())) {
      return this.rule.getRefValueDispString();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getExactMatch() {
    // Exact match
    if (this.rule.isDcm2ssd()) {
      return CommonUtilConstants.DISPLAY_YES;
    }
    return CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * @param objID
   * @return
   */
  private String getUnit() {
    // Unit
    if (this.rule.getUnit() != null) {
      return this.rule.getUnit();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getReadyForSeries() {
    // Ready for series
    if (this.rule != null) {
      if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(this.rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.YES.uiType;
      }
      else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(this.rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.NO.uiType;
      }
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getCreatedDate() { // Created date
    return String.valueOf(this.rule.getRuleCreatedDate());
  }

  /**
   * @param objID
   * @return
   */
  private String getCreatedUser() {
    // Created User
    if (this.rule != null) {
      return this.rule.getRuleCreatedUser();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getMaturityLevel() {
    // Maturity Level
    if (this.rule != null) {
      return RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(this.rule.getMaturityLevel()).getICDMMaturityLevel();
    }
    return "";
  }

  /**
   * @param objID
   * @return
   */
  private String getTitleText() {
    // Title
    return "Rule for Parameter: " + this.rule.getParameterName();
  }

  /**
   * {@inheritDoc} PropertyValue
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    return null;
  }

  /**
   * {@inheritDoc} Title
   */
  @Override
  protected String getTitle() {
    return null;
  }

}
