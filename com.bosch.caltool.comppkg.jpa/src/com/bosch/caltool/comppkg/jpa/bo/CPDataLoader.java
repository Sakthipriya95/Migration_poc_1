/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.comppkg.jpa.bo;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractDataLoader;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;


/**
 * This class loads the data from the database, and stores in the DataCache class. The entity manager is provided by
 * EntityProvider instance
 */
class CPDataLoader extends AbstractDataLoader {


  /**
   * CP data cache instance
   */
  private final CPDataCache dataCache;
  /**
   * CP data provider
   */
  private final CPDataProvider dataProvider;


  /**
   * Constructor
   * 
   * @param dataProvider the cp data provider
   * @param cache the data cache instance
   */
  CPDataLoader(final CPDataProvider dataProvider, final CPDataCache cache) {
    super();
    this.dataCache = cache;
    this.dataProvider = dataProvider;
    CPCacheRefresher cacheRefresher = new CPCacheRefresher(dataProvider);
    ObjectStore.getInstance().registerCacheRefresher(cacheRefresher);
  }

  /**
   * Fetch start up data
   */
  protected final void fetchStartupData() {
    // To load all BCs
    if (canLoadData(CPDataProvider.P_LOAD_SBC_START, 0, 0)) {
      this.dataProvider.getLogger().debug("Loading component package startup data...");
      getAllSdomBCs();
      this.dataProvider.getLogger().debug("Loading component package startup data completed");
    }

  }

  /**
   * Get all component packages from iCDM
   */
  protected void loadAllCompPkgs() {
    String query = "select cp from TCompPkg cp";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TCompPkg> typeQuery = entMgr.createQuery(query, TCompPkg.class);
    final List<TCompPkg> resultList = typeQuery.getResultList();
    // prepare the result set
    CompPkg compPkg;
    for (TCompPkg dbCompPkg : resultList) {
      compPkg = this.dataCache.getCompPkg(dbCompPkg.getCompPkgId());
      if (compPkg == null) {
        compPkg = new CompPkg(this.dataProvider, dbCompPkg.getCompPkgId()); // NOPMD by adn1cob on 6/30/14 10:17 AM
        // add it to all comp pakgs
        this.dataCache.getAllCompPkgsMap().put(dbCompPkg.getCompPkgId(), compPkg);
      }
    }
  }


  /**
   * Get all distinct Sdom BCS (in T_SDOM_BCS)
   * 
   * @return set of BCS
   */
  protected SortedSet<SdomBC> getAllSdomBCs() {
    String query = "select DISTINCT bc.name, bc.description FROM TSdomBc bc";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<Object[]> typeQuery = entMgr.createQuery(query, Object[].class);
    final List<Object[]> resultList = typeQuery.getResultList();
    // prepare the result set
    SdomBC bcObj;
    final SortedSet<SdomBC> retSet = new TreeSet<SdomBC>();
    for (Object[] dbBc : resultList) {
      bcObj = this.dataCache.getBC(dbBc[0].toString());
      if (bcObj == null) {
        bcObj = new SdomBC(dbBc[0].toString(), dbBc[1].toString()); // NOPMD by adn1cob on 6/30/14 10:17 AM
        this.dataCache.getAllBcs().put(dbBc[0].toString(), bcObj);
      }
      retSet.add(bcObj);
    }
    return retSet;
  }

  /**
   * Get all distinct Sdom FC's for the BC name (from T_SDOM_FCS)
   * 
   * @param bcName bcName
   * @return Set of Fcs
   */
  public SortedSet<SdomFC> getFcs(final String bcName) {
    final String query =
        "SELECT DISTINCT fc.name FROM TSdomBc bc, TSdomFc fc, TSdomFc2bc fc2bc where bc.name = '" + bcName + "'" +
            " and bc.id = fc2bc.bcId and fc.id = fc2bc.fcId ";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<String> typeQuery = entMgr.createQuery(query, String.class);
    final List<String> resultList = typeQuery.getResultList();
    // prepare the result set
    SdomFC fcObj;
    final SortedSet<SdomFC> retSet = new TreeSet<SdomFC>();
    for (String dbFC : resultList) {
      fcObj = this.dataCache.getFC(dbFC);
      if (fcObj == null) {
        fcObj = new SdomFC(dbFC, ""/* description,not available */); // NOPMD by adn1cob on 6/30/14 10:17 AM
        this.dataCache.getAllFcs().put(dbFC, fcObj);
      }
      retSet.add(fcObj);
    }
    this.dataCache.getAllBCFcs().put(bcName, retSet);
    return retSet;
  }

}