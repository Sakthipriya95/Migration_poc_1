/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa;

import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGrpParam;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;

/**
 * @author rgo7cob
 */
public class GrpParamInsertRunnable implements Runnable {

  private final Map<String, String> a2lGrouoMap;
  private final Map<String, Set<String>> characterMap;
  private final Map<String, Long> groupMap;

  /**
   * @param a2lGrpMap a2lGrpMap
   * @param charMap charMap
   * @param dbGrpMap fetchDbGroupId
   */
  public GrpParamInsertRunnable(final Map<String, String> a2lGrpMap, final Map<String, Set<String>> charMap,
      final Map<String, Long> dbGrpMap) {
    this.a2lGrouoMap = a2lGrpMap;
    this.characterMap = charMap;
    this.groupMap = dbGrpMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    insertGrpParam(this.a2lGrouoMap, this.characterMap, this.groupMap);
  }


  /**
   * @param a2lGrpMap a2lGrpMap
   * @param charMap charMap
   * @param fetchDbGroupId fetchDbGroupId
   */
  private void insertGrpParam(final Map<String, String> a2lGrpMap, final Map<String, Set<String>> charMap,
      final Map<String, Long> fetchDbGroupId) {
    EntityManager entMgrToUse = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    try {
      long recId = 0l;

      entMgrToUse.getTransaction().begin();
      for (String groupName : a2lGrpMap.keySet()) {
        recId = populateTempTable(groupName, fetchDbGroupId.get(groupName), entMgrToUse, charMap, recId);
      }

      createA2LGrpParams(entMgrToUse);
      entMgrToUse.getTransaction().commit();
    }
    catch (Exception exp) {
      // close the entity manager and rollback the transaction
      if (entMgrToUse != null) {
        if (entMgrToUse.getTransaction() != null) {
          entMgrToUse.getTransaction().rollback();
        }
        entMgrToUse.close();
      }
      getLogger().error(exp.getMessage(), exp);

    }
    finally {

      try {
        // Close the entity manager, if it is created locally
        if ((entMgrToUse != null) && entMgrToUse.isOpen()) {
          entMgrToUse.close();
        }
      }
      catch (Exception exp) {
        getLogger().error(exp.getMessage(), exp);

      }
    }

  }

  /**
   * @param groupName
   * @param groupId
   * @param entMgrToUse
   * @param charMap
   * @param entMgr
   */
  private long populateTempTable(final String groupName, final long groupId, final EntityManager entMgrToUse,
      final Map<String, Set<String>> charMap, long recID) {

    getLogger().debug("Populating temp table");

    GttParameter tempParam;

    // Create entities for all the parameters
    for (String param : charMap.get(groupName)) {
      recID = recID + 1;
      String[] split = param.split(":");
      tempParam = new GttParameter();
      tempParam.setId(recID);
      tempParam.setParamName(split[0]);
      tempParam.setType(split[1]);
      tempParam.setGroupId(groupId);
      entMgrToUse.persist(tempParam);
    }
    return recID;
  }

  /**
   * create A2LGroupParams for this ICDMA2lGroupParam
   *
   * @param entMgrToUse
   * @throws CommandException
   */
  private void createA2LGrpParams(final EntityManager entMgrToUse) {

    getLogger().debug("Insert parameters from temp table...");
    TypedQuery<Object[]> a2lGrpParamQuery = entMgrToUse.createNamedQuery(TA2lGrpParam.INSERT_GRP_PARAM, Object[].class);
    a2lGrpParamQuery.executeUpdate();

  }

  /**
   * Logger to be used
   *
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }
}
