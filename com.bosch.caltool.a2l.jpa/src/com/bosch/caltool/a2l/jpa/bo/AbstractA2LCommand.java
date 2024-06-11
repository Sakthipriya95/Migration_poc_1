/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.caltool.a2l.jpa.A2LDataCache;
import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.a2l.jpa.A2LEntityProvider;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;

/**
 * Abstract class for all A2L commands. Protected methods in the parent class are overridden to provide visiblity at
 * package level.
 *
 * @author mkl2cob
 */
public abstract class AbstractA2LCommand extends AbstractDataCommand {

  /**
   * @param dataProvider
   */
  protected AbstractA2LCommand(final AbstractDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @return the dataProvider
   */
  @Override
  protected final A2LDataProvider getDataProvider() {
    return (A2LDataProvider) super.getDataProvider();
  }


  /**
   * @return the dataCache
   */
  @Override
  protected final A2LDataCache getDataCache() {
    return getDataProvider().getDataCache();
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected final A2LEntityProvider getEntityProvider() {
    return getDataProvider().getEntProvider();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void rollBackDataModel();

}
