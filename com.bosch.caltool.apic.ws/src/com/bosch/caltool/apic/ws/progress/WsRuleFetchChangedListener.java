/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.progress;

import java.util.ArrayList;

import com.bosch.caltool.apic.ws.session.Session;


/**
 * @author imi2si
 */
public class WsRuleFetchChangedListener extends WsFetchChangedListener {

  private final ArrayList<Rule> rules = new ArrayList<>();

  /**
   * @param sessID
   * @param statusAsyncExecution
   * @param upperPercentageLimit
   * @param rules
   */
  public WsRuleFetchChangedListener(final Session sessionStatus, final double upperPercentageLimit, final Rule... rules) {
    super(sessionStatus, upperPercentageLimit);
    addRule(rules);
  }

  public void addRule(final Rule... rules) {
    for (Rule entry : rules) {
      if (checkRule(entry)) {
        entry
            .setInterval(super.calcInterval(entry.getMaxRowNum() - entry.getMinRowNum(), entry.getPercentageRange(), 1));
        this.rules.add(entry);
      }
      else {
        throw new IllegalArgumentException("Rule conflicts with bounds of already existing rule");
      }
    }
  }

  private boolean checkRule(final Rule rule) {

    for (Rule entry : this.rules) {
      if (!((entry.getMaxRowNum() < rule.getMinRowNum()) || (entry.getMinRowNum() > rule.getMaxRowNum()))) {
        return false;
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getInterval() {
    // TODO Auto-generated method stub
    for (Rule entry : this.rules) {
      if ((super.getHandledCalls() >= entry.getMinRowNum()) && (super.getHandledCalls() <= entry.getMaxRowNum())) {
        return entry.getInterval();
      }
    }
    return 0;
  }
}
