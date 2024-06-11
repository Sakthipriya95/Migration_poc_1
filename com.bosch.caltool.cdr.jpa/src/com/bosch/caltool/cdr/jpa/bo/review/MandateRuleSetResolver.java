/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.RuleSet;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author rgo7cob
 */
public class MandateRuleSetResolver {

  private final ApicDataProvider apicDataProvider;
  private final CDRDataProvider cdrDataProvider;

  /**
   * @param apicDataProvider apicDataProvider
   * @param cdrDataProvider cdrDataProvider
   */
  public MandateRuleSetResolver(final ApicDataProvider apicDataProvider, final CDRDataProvider cdrDataProvider) {
    this.apicDataProvider = apicDataProvider;
    this.cdrDataProvider = cdrDataProvider;
  }

  /**
   * @param version pidc version
   * @return the pidc rule set
   */
  private RuleSet getMandateRuleSetForPIDC(final PIDCVersion version) {


    Map<Long, PIDCAttribute> attributes = version.getAttributes();

    String parameterValue = this.apicDataProvider.getParameterValue(ApicConstants.MANDATORY_RULE_SET);

    Long mandateRuleSetID = Long.valueOf(parameterValue);

    PIDCAttribute mandatePidcAttr = attributes.get(mandateRuleSetID);
    AttributeValue attributeValue = mandatePidcAttr.getAttributeValue();
    if (attributeValue != null) {
      Long pidcRuleSetID = attributeValue.getID();
      SortedSet<RuleSet> allRuleSets = this.cdrDataProvider.getAllRuleSets(false);

      for (RuleSet ruleSet : allRuleSets) {
        if (pidcRuleSetID.equals(ruleSet.getAttrValue().getID())) {
          return ruleSet;
        }
      }

    }

    return null;
  }

  /**
   * @param cdrData cdrData
   */
  public void addMandateRuleSet(final CDRData cdrData) {

    boolean addRuleSet = true;
    RuleSet mandateRuleSetForPIDC = getMandateRuleSetForPIDC(cdrData.getSelPidcVersion());


    List<ReviewRuleSetData> ruleSetDataList = cdrData.getRuleSetDataList();
    if (mandateRuleSetForPIDC != null) {

      if (availableInPrimary(cdrData, mandateRuleSetForPIDC) || mandateRuleSetForPIDC.isRestricted()) {
        addRuleSet = false;
      }

      if (addRuleSet) {
        for (ReviewRuleSetData reviewRuleSetData : ruleSetDataList) {
          if ((reviewRuleSetData.getRuleSet() != null) &&
              reviewRuleSetData.getRuleSet().getID().equals(mandateRuleSetForPIDC.getID())) {
            addRuleSet = false;
          }

        }
      }

      if (addRuleSet) {
        ReviewRuleSetData data = new ReviewRuleSetData();
        data.setRuleSet(mandateRuleSetForPIDC);
        cdrData.getRuleSetDataList().add(data);
      }

    }

  }

  /**
   * @param cdrData
   * @param mandateRuleSetForPIDC
   * @return
   */
  private boolean availableInPrimary(final CDRData cdrData, final RuleSet mandateRuleSetForPIDC) {
    if ((cdrData.getPrimaryReviewRuleSetData() != null) &&
        (cdrData.getPrimaryReviewRuleSetData().getRuleSet() != null)) {
      if (mandateRuleSetForPIDC.getID().equals(cdrData.getPrimaryReviewRuleSetData().getRuleSet().getID())) {
        return true;
      }
    }
    return false;
  }
}
