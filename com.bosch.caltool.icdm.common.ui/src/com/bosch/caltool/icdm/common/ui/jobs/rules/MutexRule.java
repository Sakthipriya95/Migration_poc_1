/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.jobs.rules;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Rule to to ensure only one job runs at a time.
 * 
 * @author bne4cob
 */
public class MutexRule implements ISchedulingRule {

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean contains(final ISchedulingRule rule) {
    return CommonUtils.isEqual(rule, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isConflicting(final ISchedulingRule rule) {
    return CommonUtils.isEqual(rule, this);
  }

}
