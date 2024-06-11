/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.HandlerRegistry;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ICnsApplicabilityCheckerChangeData;
import com.bosch.caltool.icdm.client.bo.framework.ICnsRefresherDce;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
enum A2lDataCache implements IClientDataHandler {

                                                 /**
                                                  * Singleton instance
                                                  */
                                                 INSTANCE {

                                                   /**
                                                    * {@inheritDoc}
                                                    */
                                                   @Override
                                                   public String toString() {
                                                     return "A2lDataCache.INSTANCE";
                                                   }
                                                 };

  /**
   * common parameters and values<br>
   * Key - A2LSystemConstant Name<br>
   * Value - A2LSystemConstant object
   */
  private Map<String, A2LSystemConstant> a2lSystemConstantsMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   */
  A2lDataCache() {
    refreshA2lDataCache();
    HandlerRegistry.INSTANCE.registerSingletonDataHandler(this);
  }

  /**
   * Load/Reload A2L data by calling WS
   */
  private void refreshA2lDataCache() {
    loadAllSysConstants();
  }

  /**
   *
   */
  private void loadAllSysConstants() {
    try {
      this.a2lSystemConstantsMap = new A2LFileInfoServiceClient().getAllA2LSysConstants();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in loading A2L System constants from webservice:" + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ICnsRefresherDce getCnsRefresherDce() {
    return null;
  }


  /**
   * @return the a2lSystemConstantsMap
   */
  public Map<String, A2LSystemConstant> getA2lSystemConstantsMap() {
    if (!CommonUtils.isNotEmpty(this.a2lSystemConstantsMap)) {
      refreshA2lDataCache();
    }
    return this.a2lSystemConstantsMap;
  }

}