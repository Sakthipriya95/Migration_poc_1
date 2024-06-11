/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.HandlerRegistry;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ICnsApplicabilityCheckerChangeData;
import com.bosch.caltool.icdm.client.bo.framework.ICnsRefresherDce;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcTreeViewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
enum ApicDataCache implements IClientDataHandler {

                                                  /**
                                                   * Singleton instance
                                                   */
                                                  INSTANCE {

                                                    /**
                                                     * {@inheritDoc}
                                                     */
                                                    @Override
                                                    public String toString() {
                                                      return "ApicDataCache.INSTANCE";
                                                    }
                                                  };

  /**
   * Maximum level of PIDC structure attributes
   */
  private int pidcStructMaxLvl;

  /**
   * Level attributes map(key - level, value - attribute)
   */
  private Map<Long, Attribute> allLvlAttrByLevel = new HashMap<>();

  /**
   * Level attribute value map(key - atrr id, value - map of attribute values)
   */
  private Map<Long, Map<Long, AttributeValue>> pidTreelvlAttrValMap = new HashMap<>();


  /**
   * key - value id (level attribute ), value - Map of value dependencies(attr id, value id)
   */
  private Map<Long, Map<Long, Set<Long>>> valDepnMap;

  private final Set<Long> unlockedPidcsInSession = new HashSet<>();

  /**
   * @param pidcStructMaxLvl
   */
  private ApicDataCache() {
    refreshPidcStructMaxLvl();
    refreshLvlAttrByLevel();
    refreshPidTreeLvlAttrVal();
    refreshValDepnForLvlAttrValues();
    HandlerRegistry.INSTANCE.registerSingletonDataHandler(this);
  }

  /**
   *
   */
  private void refreshValDepnForLvlAttrValues() {
    loadValDepnForLvlAttrValues();
  }

  /**
   *
   */
  private void loadValDepnForLvlAttrValues() {
    try {
      this.valDepnMap = new AttrNValueDependencyServiceClient().getValDepnForLvlAttrValues();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void refreshPidTreeLvlAttrVal() {
    loadPidcTreeLevelAttrValues();
  }

  /**
   *
   */
  private void refreshLvlAttrByLevel() {
    loadLvlAttrByLevel();
  }

  /**
   *
   */
  private void refreshPidcStructMaxLvl() {
    loadPidcStructMaxLvl();
  }

  /**
   *
   */
  private void loadPidcStructMaxLvl() {
    PidcTreeViewServiceClient pidcTreeViewSerClient = new PidcTreeViewServiceClient();
    try {
      this.pidcStructMaxLvl = pidcTreeViewSerClient.getPidcStrucAttrMaxLevel().intValue();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
  *
  */
  private void loadLvlAttrByLevel() {
    PidcTreeViewServiceClient pidcTreeViewSerClient = new PidcTreeViewServiceClient();
    try {
      this.allLvlAttrByLevel = pidcTreeViewSerClient.getAllLvlAttrByLevel();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void loadPidcTreeLevelAttrValues() {
    PidcTreeViewServiceClient pidcTreeViewSerClient = new PidcTreeViewServiceClient();
    try {
      this.pidTreelvlAttrValMap = pidcTreeViewSerClient.getAllPidTreeLvlAttrValueSet();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ICnsRefresherDce getCnsRefresherDce() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return the pidcStructMaxLvl
   */
  public int getPidcStructMaxLvl() {
    return this.pidcStructMaxLvl;
  }

  /**
   * @param pidcStructMaxLvl the pidcStructMaxLvl to set
   */
  public void setPidcStructMaxLvl(final int pidcStructMaxLvl) {
    this.pidcStructMaxLvl = pidcStructMaxLvl;
  }

  /**
   * All Level attributes
   *
   * @return Map. Key - level, value - Attribute ID
   */
  public Map<Long, Long> getAllPidcTreeAttrIds() {
    Map<Long, Long> retMap = new HashMap<>();
    this.allLvlAttrByLevel.forEach((lvl, attr) -> {
      if (lvl > 0) {
        retMap.put(lvl, attr.getId());
      }
    });
    return retMap;
  }


  /**
   * @return the allLvlAttrByLevel
   */
  public Map<Long, Attribute> getAllLvlAttrByLevel() {
    return this.allLvlAttrByLevel;
  }


  /**
   * @param allLvlAttrByLevel the allLvlAttrByLevel to set
   */
  public void setAllLvlAttrByLevel(final Map<Long, Attribute> allLvlAttrByLevel) {
    this.allLvlAttrByLevel = allLvlAttrByLevel;
  }


  /**
   * @return the lvlAttrValMap
   */
  public Map<Long, Map<Long, AttributeValue>> getPidTreeLvlAttrValMap() {
    return this.pidTreelvlAttrValMap;
  }

  /**
   * @return the unlockedPidcsInSession
   */
  public Set<Long> getUnlockedPidcsInSession() {
    return this.unlockedPidcsInSession;
  }

  /**
   * @return the valDepnMap
   */
  public Map<Long, Map<Long, Set<Long>>> getValDepnMap() {
    return this.valDepnMap;
  }

  /**
   * @param valDepnMap the valDepnMap to set
   */
  public void setValDepnMap(final Map<Long, Map<Long, Set<Long>>> valDepnMap) {
    this.valDepnMap = valDepnMap;
  }
}