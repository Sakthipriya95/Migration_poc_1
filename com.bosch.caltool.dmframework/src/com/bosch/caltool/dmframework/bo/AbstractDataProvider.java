/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryHandler;


/**
 * Implementation of this class acts as the single point entry to the data model.
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractDataProvider {

  /**
   * @return the data cache implementation
   */
  protected abstract AbstractDataCache getDataCache();

  /**
   * @return the entity provider implementation
   */
  protected abstract AbstractEntityProvider getEntityProvider();

  /**
   * @return the data loader implementation
   */
  protected abstract AbstractDataLoader getDataLoader();

  /**
   * @return logger
   */
  protected ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * @return TransactionSummaryHandler
   */
  protected TransactionSummaryHandler getSummaryHandler() {
    return ObjectStore.getInstance().getSummaryHandler();
  }
}
