/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;

/**
 * @author bru2cob
 */
public interface IReviewRulesResolver {

  /**
   * @return the rules for review
   */
  CDRRulesWithFile getRules() throws IcdmException;
}
