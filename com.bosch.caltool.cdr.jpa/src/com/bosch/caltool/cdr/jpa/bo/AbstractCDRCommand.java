package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractDataCommand;


/**
 * Abstract class for all CDR commands. Protected methods in the parent class are overridden to provide visiblity at
 * package level.
 * 
 * @author hef2fe
 */
public abstract class AbstractCDRCommand extends AbstractDataCommand { // NOPMD by bne4cob on

  /**
   * Constructor
   * 
   * @param dataProvider the apic data provider
   */
  protected AbstractCDRCommand(final CDRDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @return the dataProvider
   */
  @Override
  protected final CDRDataProvider getDataProvider() {
    return (CDRDataProvider) super.getDataProvider();
  }

  /**
   * @return the dataLoader
   */
  @Override
  protected final CDRDataLoader getDataLoader() {
    return getDataProvider().getDataLoader();
  }

  /**
   * @return the dataCache
   */
  @Override
  protected final CDRDataCache getDataCache() {
    return getDataProvider().getDataCache();
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected final CDREntityProvider getEntityProvider() {
    return getDataProvider().getEntityProvider();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void rollBackDataModel();

}
