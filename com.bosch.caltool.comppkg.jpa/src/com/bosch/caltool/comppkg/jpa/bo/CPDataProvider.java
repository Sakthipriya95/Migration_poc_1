/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo; // NOPMD by bne4cob on 10/2/13 9:38 AM This is a wrapper class

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangedData;

/**
 * CPDataProvider provides data related to CDR
 */
@Deprecated
public class CPDataProvider extends AbstractDataProvider {

  /**
   * Parameter key to enable loading of SDOM BCs during startup
   */
  public static final String P_LOAD_SBC_START = "com.bosch.caltool.comppkg.loadsdombcs";

  /**
   * The CDR data loader instance
   */
  private final CPDataLoader dataLoader;

  /**
   * The CDR data cache
   */
  private final CPDataCache dataCache;

  /**
   * The entity provider
   */
  private final CPEntityProvider entityProvider;

  /**
   * The APIC data provider instance
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * The CDR data provider instance
   */
  private final CDRDataProvider cdrDataProvider;

  /**
   * This constructor is called when entity manager and laguage are provided from outside.
   *
   * @param apicDataProvider apic Data Provider
   * @param cdrDataProvider the cdr data provider
   */
  public CPDataProvider(final ApicDataProvider apicDataProvider, final CDRDataProvider cdrDataProvider) {

    super();

    this.apicDataProvider = apicDataProvider;
    this.cdrDataProvider = cdrDataProvider;

    this.entityProvider = new CPEntityProvider(CPEntityType.values());
    this.dataCache = new CPDataCache(this);

    this.dataLoader = new CPDataLoader(this, this.dataCache);
    this.dataLoader.fetchStartupData();

  }

  /**
   * @return the dataLoader
   */
  @Override
  protected CPDataLoader getDataLoader() {
    return this.dataLoader;
  }

  /**
   * @return the dataCache
   */
  @Override
  protected CPDataCache getDataCache() {
    return this.dataCache;
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected CPEntityProvider getEntityProvider() {
    return this.entityProvider;
  }

  /**
   * @return the apic Data Provider
   */
  public ApicDataProvider getApicDataProvider() {
    return this.apicDataProvider;
  }

  /**
   * @return the cdrDataProvider
   */
  public CDRDataProvider getCdrDataProvider() {
    return this.cdrDataProvider;
  }


  /**
   * Add new Changed Data to the cache. Access to this method is synchronized
   *
   * @param changedData the changed data
   */
  public void addChangedData(final Map<Long, ChangedData> changedData) {
    ObjectStore.getInstance().getChangeNotificationCache().addChangedData(changedData);
  }

  /**
   * @param entityID primary key
   * @return CompPkg
   */
  public CompPkg getCompPkg(final Long entityID) {
    return this.dataCache.getCompPkg(entityID);
  }

  /**
   * @param entityID primary key
   * @return CompPkgBc
   */
  public CompPkgBc getCompPkgBc(final Long entityID) {
    return this.dataCache.getCompPkgBc(entityID);
  }

  /**
   * @param entityID primary key
   * @return CompPkgBcFc
   */
  public CompPkgBcFc getCompPkgBcFc(final Long entityID) {
    return this.dataCache.getCompPkgBcFc(entityID);
  }

  /**
   * @param entityID primary key
   * @return CompPkgRuleAttr BO for the given ID
   */
  public CompPkgRuleAttr getCompPkgRuleAttr(final Long entityID) {
    return this.dataCache.getCompPkgRuleAttr(entityID);
  }

  /**
   * Get all Component packages
   *
   * @param includeDeleted true if the deleted component packages need to be included
   * @return sorted set of component packages
   */
  public SortedSet<CompPkg> getAllCompPkgs(final boolean includeDeleted) {
    return this.dataCache.getAllCompPkgs(includeDeleted);
  }

  /**
   * @return the logger
   */
  @Override
  protected final ILoggerAdapter getLogger() {
    return super.getLogger();
  }

  /**
   * Get all BC names
   *
   * @return sorted set of BC names
   */
  public SortedSet<SdomBC> getAllBcs() {
    if (this.dataCache.getAllBcs().isEmpty()) {
      return this.dataLoader.getAllSdomBCs();
    }
    return new TreeSet<SdomBC>(this.dataCache.getAllBcs().values());
  }


  /**
   * Get all FCs for the BC
   *
   * @param bcName BC name
   * @return FC names
   */
  public SortedSet<SdomFC> getFcs(final String bcName) {
    if ((null == this.dataCache.getFcs(bcName)) || this.dataCache.getFcs(bcName).isEmpty()) {
      return this.dataLoader.getFcs(bcName);
    }
    return new TreeSet<SdomFC>(this.dataCache.getFcs(bcName));
  }

}