/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleLinkWrapperData;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author ukt1cob
 */
public class RuleLinkMasterCommand extends AbstractSimpleCommand {

  /**
   * Map of cdr rule, review rule
   */
  private Map<CDRRule, ReviewRule> cdrRuleRevRuleMap = new HashMap<>();

  /**
   * map of review rule id, review rule
   */
  private final Map<Long, ReviewRule> ruleIdRevRuleMap = new HashMap<>();

  /**
   * @param serviceData service data instance
   * @param inputRuleMap input cdrrule,review rule map
   * @throws IcdmException exception
   */
  public RuleLinkMasterCommand(final ServiceData serviceData, final Map<CDRRule, ReviewRule> inputRuleMap)
      throws IcdmException {
    super(serviceData);
    this.cdrRuleRevRuleMap = inputRuleMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    List<CDRRule> cdrList = new ArrayList<>(this.cdrRuleRevRuleMap.keySet());
    List<ReviewRule> rvwRuleListWithLinkChanges = new ArrayList<>(this.cdrRuleRevRuleMap.values());

    for (ReviewRule rvwRule : rvwRuleListWithLinkChanges) {
      RuleLinkWrapperData ruleLinkWrapperData = rvwRule.getRuleLinkWrapperData();

      for (RuleLinks ruleLinks : ruleLinkWrapperData.getListOfRuleLinksToBeCreated()) {
        RuleLinksCommand createRuleLinksCmd = new RuleLinksCommand(getServiceData(), ruleLinks, false, false);
        executeChildCommand(createRuleLinksCmd);
      }

      for (RuleLinks ruleLinks : ruleLinkWrapperData.getListOfRuleLinksToBeDel()) {
        RuleLinksCommand createRuleLinksCmd = new RuleLinksCommand(getServiceData(), ruleLinks, false, true);
        executeChildCommand(createRuleLinksCmd);
      }

      for (RuleLinks ruleLinks : ruleLinkWrapperData.getListOfRuleLinksToBeUpd()) {
        RuleLinksCommand createRuleLinksCmd = new RuleLinksCommand(getServiceData(), ruleLinks, true, false);
        executeChildCommand(createRuleLinksCmd);
      }

      for (RuleLinks ruleLinks : ruleLinkWrapperData.getListOfExistingLinksForSelRule()) {
        ruleLinks.setRevId(cdrList.get(0).getRevId().longValue());
        RuleLinksCommand createRuleLinksCmd = new RuleLinksCommand(getServiceData(), ruleLinks, false, false);
        executeChildCommand(createRuleLinksCmd);
      }
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
