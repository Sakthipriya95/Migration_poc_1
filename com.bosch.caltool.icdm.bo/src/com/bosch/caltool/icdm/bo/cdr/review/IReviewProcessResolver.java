/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author bru2cob
 */
public interface IReviewProcessResolver {

  /**
   * Review process
   * 
   * @throws IcdmException
   */
  void performReview() throws IcdmException;

  /**
   * @return reviewedinfo object
   */
  ReviewedInfo getReviewOutput();

}
