/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;


/**
 * Datamodel implementations can use this class to load data from the database. The instance is accessible from the
 * implementation of data provider, command and data object classes.
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractDataLoader { // NOPMD by bne4cob on 7/14/14 4:28 PM

  /**
   * Yes
   */
  protected static final String CODE_YES = CommonUtilConstants.CODE_YES;

  /**
   * No
   */
  protected static final String CODE_NO = CommonUtilConstants.CODE_NO;

  /**
   * Checks whether the data can be loaded considering the input property value, current stage and required stages
   *
   * @param property value, defaulted to Y if null
   * @param curStage current stage
   * @param reqStage requried stage
   * @return true/false
   */
  protected final boolean canLoadData(final String property, final int curStage, final int reqStage) {
    return CODE_YES.equals(ObjectStore.getInstance().getProperty(property, CODE_YES)) && (curStage >= reqStage);
  }
}
