/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.List;

import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author bru2cob
 */
public interface IReviewParamResolver {

  /**
   * @return param names to be reviewed
   * @throws IcdmException
   */
  List<String> getParameters() throws IcdmException;
}
