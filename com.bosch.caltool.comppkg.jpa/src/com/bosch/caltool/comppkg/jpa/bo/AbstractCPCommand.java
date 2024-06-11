package com.bosch.caltool.comppkg.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractDataCommand;


/**
 * Abstract class for all CompPkg commands. Protected methods in the parent class are overridden to provide visiblity at
 * package level.
 */
public abstract class AbstractCPCommand extends AbstractDataCommand { // NOPMD by bne4cob on

  /**
   * Constructor
   * 
   * @param dataProvider the apic data provider
   */
  protected AbstractCPCommand(final CPDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @return the dataProvider
   */
  @Override
  protected final CPDataProvider getDataProvider() {
    return (CPDataProvider) super.getDataProvider();
  }

  /**
   * @return the dataLoader
   */
  @Override
  protected final CPDataLoader getDataLoader() {
    return getDataProvider().getDataLoader();
  }

  /**
   * @return the dataCache
   */
  @Override
  protected final CPDataCache getDataCache() {
    return getDataProvider().getDataCache();
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected final CPEntityProvider getEntityProvider() {
    return getDataProvider().getEntityProvider();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void rollBackDataModel();

}
