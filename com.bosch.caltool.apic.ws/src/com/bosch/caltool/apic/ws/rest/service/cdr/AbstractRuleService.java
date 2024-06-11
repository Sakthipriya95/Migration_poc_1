/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleManagerFactory;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDMessage;

/**
 * @author dja7cob
 */
public abstract class AbstractRuleService extends AbstractRestService {

  /**
   * @param ssdMessage messsage
   * @return ssd message wrapper
   */
  protected SSDMessageWrapper toSSDMessageWrapper(final SSDMessage ssdMessage) {
    SSDMessageWrapper wrapper = new SSDMessageWrapper();
    wrapper.setCode(ssdMessage.getCode());
    wrapper.setDescription(ssdMessage.getDescription());
    return wrapper;
  }

  /**
   * Method to get param names for incoming rules
   *
   * @param cdrRuleList input cdr rules
   * @return list of param names
   */
  protected List<String> getParamNames(final List<CDRRule> cdrRuleList) {
    return cdrRuleList.stream().map(CDRRule::getParameterName).collect(Collectors.toList());
  }

  /**
   * @param inputCdrRuleMap input rules map
   * @param cdrRuleCreated new cdr rule created
   * @return map to collect rules for remarks creation
   * @throws IcdmException exception
   */
  protected Map<CDRRule, ReviewRule> collectUnicodeRmrksAndLinksForCreate(
      final Map<CDRRule, ReviewRule> inputCdrRuleMap, final Map<String, List<CDRRule>> cdrRuleCreated)
      throws IcdmException {

    Map<CDRRule, ReviewRule> ruleMap = new HashMap<>();

    for (Entry<CDRRule, ReviewRule> inputCdrEntry : inputCdrRuleMap.entrySet()) {
      ReviewRule inputRvwRule = inputCdrEntry.getValue();
      CDRRule inputCdrRule = inputCdrEntry.getKey();

      ReviewRuleAdapter adapter = newRuleAdapter();

      for (CDRRule newCdrRule : cdrRuleCreated.get(inputCdrRule.getParameterName())) {

        if (CDRRuleUtil.isSameRuleRecord(newCdrRule, inputCdrRule)) {

          ReviewRule newReviewRule = adapter.createReviewRule(newCdrRule);

          if (CommonUtils.isNotEmptyString(inputRvwRule.getUnicodeRemarks())) {
            newReviewRule.setUnicodeRemarks(inputRvwRule.getUnicodeRemarks());
          }

          // for new rule to be created, the RuleLinks - Rule Id and Rev Id will passed as empty from client side
          for (RuleLinks ruleLinks : inputRvwRule.getRuleLinkWrapperData().getListOfRuleLinksToBeCreated()) {
            ruleLinks.setRuleId(newCdrRule.getRuleId().longValue());
            ruleLinks.setRevId(newCdrRule.getRevId().longValue());
          }
          newReviewRule.setRuleLinkWrapperData(inputRvwRule.getRuleLinkWrapperData());

          ruleMap.put(newCdrRule, newReviewRule);
        }
      }
    }

    return ruleMap;
  }


  /**
   * @param ruleManager instance
   * @param inputCdrRule input cdr rule object
   * @return boolean to check whether rule update is needed
   * @throws IcdmException data retrieval error
   */
  protected boolean isRuleUpdateRequired(final CDRRule inputCdrRule, final IRuleManager ruleManager)
      throws IcdmException {
    Map<String, List<CDRRule>> paramCdrMapDb = ruleManager.readRule(getParamNames(Arrays.asList(inputCdrRule)));
    return !isCdrRuleSame(inputCdrRule, paramCdrMapDb);
  }

  /**
   * Method to check whether only unicode remarks are updated
   *
   * @param inputCdrRule input cdr rule object
   * @param inputParamCdrMap parameter cdrrule map for updated rules
   * @return boolean to check whether cdr objects are same
   */
  protected boolean isCdrRuleSame(final CDRRule inputCdrRule, final Map<String, List<CDRRule>> inputParamCdrMap) {
    CDRRule cdrRule = getUpdatedCdrRule(inputCdrRule, inputParamCdrMap);
    return (cdrRule != null) && CDRRuleUtil.isSame(cdrRule, inputCdrRule);
  }

  /**
   * Method to get the updated rules for the incoming parameters and filter the required rules for update
   *
   * @param inputRule
   * @param inputParamRuleMap
   * @return
   */
  private CDRRule getUpdatedCdrRule(final CDRRule inputRule, final Map<String, List<CDRRule>> inputParamRuleMap) {
    return inputParamRuleMap.get(inputRule.getParameterName()).stream()
        .filter(r -> CommonUtils.isEqual(r.getRuleId(), inputRule.getRuleId())).findFirst().orElse(null);
  }

  /**
   * @param inputCdrRule cdr rule object
   * @param inputReviewRule review rule object from ui
   * @param ruleManager instance
   * @param isRuleUpdated is rule updated
   * @return map of rules to update unicode remarks
   * @throws IcdmException exception
   */
  protected Map<CDRRule, ReviewRule> collectUnicodeRmrksAndLinksForUpd(final CDRRule inputCdrRule,
      final ReviewRule inputReviewRule, final IRuleManager ruleManager, final boolean isRuleUpdated)
      throws IcdmException {

    Map<CDRRule, ReviewRule> ruleForUpdateMap = new HashMap<>();

    if (isRuleUpdated) {
      CDRRule newCdrRule =
          getUpdatedCdrRule(inputCdrRule, ruleManager.readRule(getParamNames(Arrays.asList(inputCdrRule))));

      if (newCdrRule != null) {
        ReviewRule newReviewRule = newRuleAdapter().createReviewRule(newCdrRule);
        newReviewRule.setUnicodeRemarks(inputReviewRule.getUnicodeRemarks());
        newReviewRule.setRuleLinkWrapperData(inputReviewRule.getRuleLinkWrapperData());

        ruleForUpdateMap.put(newCdrRule, newReviewRule);
      }
    }
    else {
      ruleForUpdateMap.put(inputCdrRule, inputReviewRule);
    }
    return ruleForUpdateMap;
  }

  /**
   * @return ReviewRuleAdapter
   * @throws UnAuthorizedAccessException access error
   */
  protected final ReviewRuleAdapter newRuleAdapter() throws UnAuthorizedAccessException {
    return new ReviewRuleAdapter(getServiceData());
  }

  /**
   * @param reviewRulepramCol ReviewRuleParamCol
   * @return IRuleManager
   * @throws IcdmException error while creating rule manager
   */
  protected final IRuleManager newRuleManager(final ReviewRuleParamCol<?> reviewRulepramCol) throws IcdmException {
    return new RuleManagerFactory(getServiceData()).createRuleManager(reviewRulepramCol);
  }

  /**
   * @param cdrRule CDRRule
   */
  protected void validateComplexRule(final CDRRule cdrRule) {
    CDRRuleUtil.appendCommentForComplexRule(cdrRule);
  }

  /**
   * @param cdrRuleList List<CDRRule>
   */
  protected void validateComplexRules(final List<CDRRule> cdrRuleList) {
    for (CDRRule cdrRule : cdrRuleList) {
      CDRRuleUtil.appendCommentForComplexRule(cdrRule);
    }
  }
}
