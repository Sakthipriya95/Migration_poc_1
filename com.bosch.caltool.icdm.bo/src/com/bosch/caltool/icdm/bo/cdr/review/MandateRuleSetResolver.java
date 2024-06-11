/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;


import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author rgo7cob
 */
public class MandateRuleSetResolver {


  private final ServiceData serviceData;
  final ReviewedInfo reviewedInfo;
  final ReviewInput reviewInputData;

  /**
   * @param serviceData
   * @param reviewInputData
   */
  public MandateRuleSetResolver(final ServiceData serviceData, final ReviewedInfo reviewedInfo,
      final ReviewInput reviewInputData) {
    this.serviceData = serviceData;
    this.reviewedInfo = reviewedInfo;
    this.reviewInputData = reviewInputData;
  }

  /**
   * @param version pidc version
   * @return the pidc rule set
   * @throws DataException
   */
  private RuleSet getMandateRuleSetForPIDC() throws DataException {


    Map<Long, PidcVersionAttribute> attributes = this.reviewedInfo.getPidcDetails().getPidcVersAttrMap();

    final Long mandateRuleSetID =
        Long.valueOf((new CommonParamLoader(this.serviceData)).getValue(CommonParamKey.CDR_MANDATORY_RULESET_ATTR_ID));


    PidcVersionAttribute mandatePidcAttr = attributes.get(mandateRuleSetID);

    AttributeValueLoader attrValLoader = new AttributeValueLoader(this.serviceData);
    RuleSetLoader ruleSetLoader = new RuleSetLoader(this.serviceData);
    if (mandatePidcAttr.getValueId() != null) {
      AttributeValue attributeValue = attrValLoader.getDataObjectByID(mandatePidcAttr.getValueId());
      if (attributeValue != null) {
        Long pidcRuleSetID = attributeValue.getId();
        Set<ParamCollection> allRuleSets = ruleSetLoader.getAllRuleSets();

        for (ParamCollection ruleSet : allRuleSets) {
          if (pidcRuleSetID.equals(((RuleSet) ruleSet).getAttrValId())) {
            return (RuleSet) ruleSet;
          }
        }

      }
    }
    return null;
  }

  /**
   * @param cdrData cdrData
   * @throws DataException
   */
  public void addMandateRuleSet() throws DataException {

    boolean addRuleSet = true;
    RuleSet mandateRuleSetForPIDC = getMandateRuleSetForPIDC();

    RuleSetLoader ruleSetLoader = new RuleSetLoader(this.serviceData);
    if (mandateRuleSetForPIDC != null) {

      if (availableInPrimary(mandateRuleSetForPIDC) || ruleSetLoader.isRestricted(mandateRuleSetForPIDC)) {
        addRuleSet = false;
      }

      if (addRuleSet) {
        if (this.reviewInputData.getRulesData().getSecondaryRuleSetIds() != null) {
          for (Long secondaryRuleSetId : this.reviewInputData.getRulesData().getSecondaryRuleSetIds()) {
            RuleSet secondaryRuleSet = ruleSetLoader.getDataObjectByID(secondaryRuleSetId);
            if ((secondaryRuleSet != null) && secondaryRuleSet.getId().equals(mandateRuleSetForPIDC.getId())) {
              addRuleSet = false;
            }
          }
        }
      }

      if (addRuleSet) {
        ReviewRuleSetData data = new ReviewRuleSetData();
        data.setRuleSet(mandateRuleSetForPIDC);
        this.reviewedInfo.getSecRuleSetDataList().add(data);
      }

    }

  }

  /**
   * @param cdrData
   * @param mandateRuleSetForPIDC
   * @return
   */
  private boolean availableInPrimary(final RuleSet mandateRuleSetForPIDC) {
    if (this.reviewInputData.getRulesData().getPrimaryRuleSetId() != null) {
      if (mandateRuleSetForPIDC.getId().equals(this.reviewInputData.getRulesData().getPrimaryRuleSetId())) {
        return true;
      }
    }
    return false;
  }
}
