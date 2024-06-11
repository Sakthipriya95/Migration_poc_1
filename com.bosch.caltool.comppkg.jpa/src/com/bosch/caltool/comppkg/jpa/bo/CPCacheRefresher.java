/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.EntityType;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayChangeEvent;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.notification.ICacheRefresher;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class is used for refresh of Component package related objects in data cache
 *
 * @author mkl2cob
 */
class CPCacheRefresher implements ICacheRefresher {


  /**
   * CP data provider
   */
  private final CPDataProvider dataProvider;


  /**
   * Constructor
   *
   * @param dataProvider CPDataProvider
   */
  public CPCacheRefresher(final CPDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * {@inheritDoc} ICDM-933
   */
  @Override
  public void refreshDataCache(final DisplayChangeEvent event) {

    if (event.isEmpty()) {
      return;
    }

    this.dataProvider.getLogger()
        .debug("Refreshing CP data cache for the display change event ID : " + event.getEventID());

    final SortedSet<ChangedData> changedDataSet = new TreeSet<ChangedData>(event.getChangedData().values());
    CPEntityType itemType;

    for (ChangedData item : changedDataSet) {

      if (item.getEntityType() == EntityType.TOP_LEVEL_ENTITY) {
        final Object entity =
            this.dataProvider.getEntityProvider().getEm().find(item.getEntityClass(), item.getPrimaryKey());
        final TabvTopLevelEntity toplevelEntity = (TabvTopLevelEntity) entity;
        if (toplevelEntity.getEntityName().equals(ApicConstants.COMP_PCKG)) {
          if (this.dataProvider.getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
            this.dataProvider.getLogger().debug("Processing Entity - id :" + item.getPrimaryKey() + "; type :" +
                item.getEntityType() + "; change type :" + item.getChangeType());
          }
          refreshCompPckgs(event);
        }
      }

      // If entity type is Component Package
      if (!(item.getEntityType() instanceof CPEntityType)) {
        continue;
      }

      if (this.dataProvider.getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        this.dataProvider.getLogger().debug("Processing Entity - id :" + item.getPrimaryKey() + "; type :" +
            item.getEntityType() + "; change type :" + item.getChangeType());
      }

      itemType = (CPEntityType) item.getEntityType();

      // Capture the details of old object
      final AbstractCPObject cpObj = itemType.getDataObject(this.dataProvider, item.getPrimaryKey());

      if (item.getChangeType() == ChangeType.UPDATE) {
        item.setOldDataDetails(cpObj.getObjectDetails());
      }

      refreshObject(event, itemType, item, cpObj);
    }

  }

  /**
   * ICDM-933 refreshes the component packages if new one is added
   *
   * @param event DisplayChangeEvent
   */
  private void refreshCompPckgs(final DisplayChangeEvent event) {
    Map<Long, ChangedData> changedDataMap = event.getChangedData();
    this.dataProvider.getLogger().debug("fetching all Component Package ID's from database ...");

    final Query qStrucAttrVals =
        this.dataProvider.getEntityProvider().getEm().createQuery("SELECT t.compPkgId FROM TCompPkg t");

    try {
      @SuppressWarnings("unchecked")
      final List<Long> compPckgIds = qStrucAttrVals.getResultList();

      for (Long cpID : compPckgIds) {

        CompPkg cpObj;

        if (!this.dataProvider.getDataCache().getAllCompPkgsMap().containsKey(cpID)) {
          cpObj = new CompPkg(this.dataProvider, cpID);
          this.dataProvider.getDataCache().getAllCompPkgsMap().put(cpID, cpObj);


          changedDataMap.put(cpID,
              new ChangedData(ChangeType.INSERT, cpID, TCompPkg.class, DisplayEventSource.DATABASE));


        }

      }

    }
    catch (NoResultException e) {
      this.dataProvider.getLogger().error("NO results when fetching component packages!", e);
    }

  }

  /**
   * ICDM-933 Refresh the entity and the DM object
   *
   * @param event DCE
   * @param itemType item type
   * @param item item
   * @param cpObj DM object
   */
  private void refreshObject(final DisplayChangeEvent event, final CPEntityType itemType, final ChangedData item,
      final AbstractCPObject cpObj) {
    // Refresh the entity in the entity manager and data model
    Object entity = this.dataProvider.getEntityProvider().getEm().find(item.getEntityClass(), item.getPrimaryKey());

    // TODO Refresh Data Objects

    if (item.getChangeType() == ChangeType.UPDATE) {
      // Refresh is not executed for delete as it will throw a EntityNotFound exception. Inserted records are not
      // identified by EclipseLink directly
      // TODO check these conditions for Undo operations

      final long oldVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());
      entity = this.dataProvider.getEntityProvider().refreshCacheObject(entity);
      final long newVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());
      if (oldVersion == newVersion) {
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        return;
      }
    }
    // Extra Check for null
    if (cpObj != null) {
      cpObj.refresh();
    }

    // Refresh data model cache for child entities
    doRefrshForChild(event, item, entity);
  }

  /**
   * ICDM-933 this method refreshes the child objects
   *
   * @param event DisplayChangeEvent
   * @param item ChangedData
   * @param entity Entity object
   */
  private void doRefrshForChild(final DisplayChangeEvent event, final ChangedData item, final Object entity) {
    switch ((CPEntityType) item.getEntityType()) {
      case COMP_PKG:
        caseCmpPkg(event, item, entity);
        break;

      // If it is Base Component
      case COMP_PKG_BC:
        handleBcDcn(event, item, entity);
        break;

      // If it is Functional Component
      case COMP_PKG_BC_FC:
        handleFcDcn(item);
        break;

      default:
        break;


    }

  }

  /**
   * @param event
   * @param item
   * @param entity
   */
  private void caseCmpPkg(final DisplayChangeEvent event, final ChangedData item, final Object entity) {
    CompPkg compPkg = (CompPkg) item.getEntityType().getDataObject(this.dataProvider, item.getPrimaryKey());
    // fetch BC's only if the component package is stored in cache
    if ((compPkg != null) && this.dataProvider.getApicDataProvider().isObjLoaded(compPkg)) {
      fetchBCs((TCompPkg) entity, event.getChangedData());
    }
  }

  /**
   * @param item ChangedData
   */
  private void handleFcDcn(final ChangedData item) {
    CompPkgBcFc compPkgBcFc = (CompPkgBcFc) item.getEntityType().getDataObject(this.dataProvider, item.getPrimaryKey());
    if (this.dataProvider.getApicDataProvider().isObjLoaded(compPkgBcFc.getCompPkgBC().getCompPkg()) &&
        (item.getChangeType() == ChangeType.DELETE)) {
      deleteFC(compPkgBcFc);
    }
  }

  /**
   * @param event DisplayChangeEvent
   * @param item ChangedData
   * @param entity Object
   */
  private void handleBcDcn(final DisplayChangeEvent event, final ChangedData item, final Object entity) {
    CompPkgBc compPkgBc = (CompPkgBc) item.getEntityType().getDataObject(this.dataProvider, item.getPrimaryKey());
    if (this.dataProvider.getApicDataProvider().isObjLoaded(compPkgBc.getCompPkg())) {
      if (item.getChangeType() == ChangeType.DELETE) {
        deleteBC(compPkgBc);
      }
      else {
        fetchFCs((TCompPkgBc) entity, event.getChangedData());
      }
    }
  }

  /**
   * Removes the FC from parent Component package BC & data cache
   *
   * @param compPkgBcFc CompPkgBcFc
   */
  private void deleteFC(final CompPkgBcFc compPkgBcFc) {

    this.dataProvider.getDataCache().getAllCompPkgBcFcs().put(compPkgBcFc.getID(), compPkgBcFc);
    CompPkgBc parentCmpPkgBc = compPkgBcFc.getCompPkgBC();

    // Removing FC from the parent component package BC in cache

    parentCmpPkgBc.getCompPkgBcFcsMap().remove(compPkgBcFc.getID());
  }

  /**
   * Removes the BC from parent Component package & data cache
   *
   * @param compPkgBc CompPkgBc
   */
  private void deleteBC(final CompPkgBc compPkgBc) {
    this.dataProvider.getDataCache().getAllCompPkgBcs().remove(compPkgBc.getID());
    CompPkg parentCmpPkg = compPkgBc.getCompPkg();

    // Removing BC from the parent component package in cache
    parentCmpPkg.getCompPkgBcsMap().remove(compPkgBc.getID());

  }

  /**
   * ICDM-933 fetch BC's for a component package
   *
   * @param dbCmpPckg TCompPkg
   * @param changedDataMap Map of changed data
   */
  private void fetchBCs(final TCompPkg dbCmpPckg, final Map<Long, ChangedData> changedDataMap) {


    Long cmpPckgBCId;

    for (TCompPkgBc dbBC : dbCmpPckg.getTCompPkgBcs()) {
      cmpPckgBCId = Long.valueOf(dbBC.getCompBcId());

      if (this.dataProvider.getDataCache().getAllCompPkgBcs().containsKey(cmpPckgBCId)) {
        continue;
      }

      CompPkgBc compPkgBc = new CompPkgBc(this.dataProvider, cmpPckgBCId);
      CompPkg compPkg = compPkgBc.getCompPkg();

      // Adding BC to the parent component package in cache
      compPkg.getCompPkgBcsMap().put(cmpPckgBCId, compPkgBc);


      changedDataMap.put(cmpPckgBCId,
          new ChangedData(ChangeType.INSERT, cmpPckgBCId, TCompPkgBc.class, DisplayEventSource.DATABASE));

      fetchFCs(dbBC, changedDataMap);

    }


  }

  /**
   * ICDM-933 fetches FC's for a BC
   *
   * @param dbBC
   * @param changedDataMap
   */
  private void fetchFCs(final TCompPkgBc dbBC, final Map<Long, ChangedData> changedDataMap) {
    Long cmpPckgBCFCId;
    CompPkgBcFc compPkgBcFc;

    for (TCompPkgBcFc dbBCFC : dbBC.getTCompPkgBcFcs()) {

      cmpPckgBCFCId = dbBCFC.getCompBcFcId();

      if (this.dataProvider.getDataCache().getAllCompPkgBcs().containsKey(cmpPckgBCFCId)) {
        continue;
      }

      compPkgBcFc = new CompPkgBcFc(this.dataProvider, cmpPckgBCFCId);
      CompPkgBc parentCmpPkgBc = compPkgBcFc.getCompPkgBC();


      parentCmpPkgBc.getCompPkgBcFcsMap().put(cmpPckgBCFCId, compPkgBcFc);


      changedDataMap.put(cmpPckgBCFCId,
          new ChangedData(ChangeType.INSERT, cmpPckgBCFCId, TCompPkgBcFc.class, DisplayEventSource.DATABASE));


    }


  }
}
