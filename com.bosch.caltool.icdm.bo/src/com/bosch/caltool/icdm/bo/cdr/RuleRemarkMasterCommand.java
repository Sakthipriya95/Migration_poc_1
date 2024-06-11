/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleRemark;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author dja7cob
 */
public class RuleRemarkMasterCommand extends AbstractSimpleCommand {

  /**
   * Map of cdr rule, review rule
   */
  private Map<CDRRule, ReviewRule> cdrRuleRevRuleMap = new HashMap<>();

  /**
   * map of review rule id, review rule
   */
  private Map<Long, ReviewRule> ruleIdRevRuleMap = new HashMap<>();

  /**
   * @param serviceData service data instance
   * @param inputRuleMap input cdrrule,review rule map
   * @throws IcdmException exception
   */
  public RuleRemarkMasterCommand(final ServiceData serviceData, final Map<CDRRule, ReviewRule> inputRuleMap)
      throws IcdmException {
    super(serviceData);
    this.cdrRuleRevRuleMap = inputRuleMap;
    this.ruleIdRevRuleMap = this.cdrRuleRevRuleMap.values().stream()
        .collect(Collectors.toMap(rule -> rule.getRuleId().longValue(), Function.identity()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    List<CDRRule> cdrList = new ArrayList<>(this.cdrRuleRevRuleMap.keySet());

    Map<Long, RuleRemark> existingRemarksMap =
        new RuleRemarkLoader(getServiceData()).getRuleIdUnicodeRemarkMap(cdrList);

    for (CDRRule cdrRule : cdrList) {
      createUpdDelRmrk(existingRemarksMap, cdrRule);
    }
  }

  /**
   * @param existingRemarksMap
   * @param cdrRule
   * @param reviewRule
   * @throws IcdmException
   */
  private void createUpdDelRmrk(final Map<Long, RuleRemark> existingRemarksMap, final CDRRule cdrRule)
      throws IcdmException {

    ReviewRule inputReviewRule = this.ruleIdRevRuleMap.get(cdrRule.getRuleId().longValue());
    RuleRemark existingRemark = existingRemarksMap.get(cdrRule.getRuleId().longValue());
    if (null == existingRemark) {
      if (!CommonUtils.isEmptyString(inputReviewRule.getUnicodeRemarks())) {
        createRuleRemarks(inputReviewRule, cdrRule);
      }
    }
    else {
      // If a rule remark exists, but the remark is removed from UI : delete the unicode remarks
      // Else update the remarks
      if (CommonUtils.isEmptyString(inputReviewRule.getUnicodeRemarks())) {
        deleteRuleRemarks(existingRemark);
      }
      else {
        updateRuleRemarks(existingRemark, inputReviewRule);
      }

    }
  }

  /**
   * @param existingRemark
   * @throws IcdmException
   */
  private void deleteRuleRemarks(final RuleRemark existingRemark) throws IcdmException {
    RuleRemarkCommand ruleRmrkCmd = new RuleRemarkCommand(getServiceData(), existingRemark, false, true);
    executeChildCommand(ruleRmrkCmd);
  }

  /**
   * @param reviewRule
   * @param cdrRule
   * @throws IcdmException
   */
  private void createRuleRemarks(final ReviewRule reviewRule, final CDRRule cdrRule) throws IcdmException {
    RuleRemark newRuleRmrk = new RuleRemark();
    newRuleRmrk.setRuleId(reviewRule.getRuleId().longValue());
    newRuleRmrk.setRevId(cdrRule.getRevId().longValue());
    newRuleRmrk.setRemark(reviewRule.getUnicodeRemarks());

    RuleRemarkCommand ruleRmrkCmd = new RuleRemarkCommand(getServiceData(), newRuleRmrk, false, false);
    executeChildCommand(ruleRmrkCmd);
  }

  /**
   * @param existingRemark
   * @param inputReviewRule
   * @throws IcdmException
   */
  private void updateRuleRemarks(final RuleRemark existingRemark, final ReviewRule inputReviewRule)
      throws IcdmException {

    if (!CommonUtils.isEqual(existingRemark.getRemark(), inputReviewRule.getUnicodeRemarks())) {
      RuleRemark ruleRemarks = new RuleRemark();
      ruleRemarks.setId(existingRemark.getId());
      ruleRemarks.setRuleId(inputReviewRule.getRuleId().longValue());
      ruleRemarks.setRevId(inputReviewRule.getRevId().longValue());
      ruleRemarks.setRemark(inputReviewRule.getUnicodeRemarks());
      ruleRemarks.setVersion(existingRemark.getVersion());

      RuleRemarkCommand ruleRmrkCmd = new RuleRemarkCommand(getServiceData(), ruleRemarks, true, false);
      executeChildCommand(ruleRmrkCmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
//Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
