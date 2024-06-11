/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;

/**
 * new class to implement the logic for creating a2l params
 *
 * @author mkl2cob
 */
@Deprecated
public class A2LGrpParamsForRespCreator {


  /**
   * Creates the A 2 L resp.
   *
   * @param a2lId a2l file id
   * @param rootId group root id
   * @param typeID the type ID
   * @param pidcA2LId the pidc A 2 L id
   * @param a2lGrpMap the a 2 l grp map
   * @param charMap Map<A2LGroup, Set<Characteristic>>
   * @param mappingSource the mapping source
   * @param defaultWPId the default WP id
   * @param currentUserId the current user id
   * @param apicDataProvider the apic data provider
   * @return true if the method is sucess
   */
  public boolean createA2LResp(final Long a2lId, final Long rootId, final Long typeID, final Long pidcA2LId,
      final Map<String, String> a2lGrpMap, final Map<String, Set<String>> charMap, final Long mappingSource,
      final Long defaultWPId, final String currentUserId, final ApicDataProvider apicDataProvider) {
    final long startTime = System.currentTimeMillis();
    EntityManager entMgrToUse = null;

    boolean insertSucess = false;


    getLogger().debug("creating  parameters in T_A2L_RESP for the a2l File : " + a2lId);
    try {
      // Create new Entity Manager for this transaction. To avoid Concurrent Modification exception.
      entMgrToUse = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
      entMgrToUse.getTransaction().begin();

      A2LWPRespCreator a2lWpCreator = new A2LWPRespCreator(a2lId, rootId, typeID, pidcA2LId, a2lGrpMap, mappingSource,
          defaultWPId, currentUserId, entMgrToUse, apicDataProvider);
      boolean creatingParamsNeeded = a2lWpCreator.createA2LRespAndA2lWpResp();


      getLogger().debug("creating  parameters in T_A2L_GRP_PARAM for the a2l File : " + a2lId);

      Map<String, Long> fetchDbGroupId = fetchDbA2lGroup(a2lGrpMap.keySet(), rootId, a2lId, entMgrToUse);
      getLogger().debug("db group map size ");
      getLogger().debug(String.valueOf(fetchDbGroupId.size()));
      entMgrToUse.getTransaction().commit();
      if (creatingParamsNeeded) {
        // create parameters only if they are needed
        createPramsInThread(a2lGrpMap, charMap, fetchDbGroupId);
      }
      insertSucess = true;
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
      insertSucess = false;
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
        insertSucess = false;
      }
    }


    getLogger().debug("time taken : " + (System.currentTimeMillis() - startTime));
    return insertSucess;
  }


  /**
   * @param a2lGrpMap
   * @param charMap
   * @param fetchDbGroupId
   */
  private void createPramsInThread(final Map<String, String> a2lGrpMap, final Map<String, Set<String>> charMap,
      final Map<String, Long> fetchDbGroupId) {
    GrpParamInsertRunnable runnable = new GrpParamInsertRunnable(a2lGrpMap, charMap, fetchDbGroupId);
    Thread thread = new Thread(runnable);
    thread.start();
  }


  /**
   * @param paramNameList
   * @return
   */
  private Map<String, Long> fetchDbA2lGroup(final Set<String> grpNameSet, final Long rootId, final Long a2lId,
      final EntityManager entMgrToUse) {
    Map<String, Long> groupNameIdMap = new ConcurrentHashMap<String, Long>();

    GttParameter tempParam;
    long recID = 1;
    try {

      // Delete the existing records in this temp table, if any
      final Query delQuery = entMgrToUse.createQuery("delete from GttParameter temp");
      delQuery.executeUpdate();

      // Create entities for all the parameters
      for (String grpName : grpNameSet) {
        tempParam = new GttParameter();
        tempParam.setId(recID);
        tempParam.setParamName(grpName);
        entMgrToUse.persist(tempParam);
        recID++;
      }

      entMgrToUse.flush();
      final StringBuilder query = new StringBuilder();

      query.append(
          "Select ta2lGroup from TA2lGroup ta2lGroup,GttParameter  temp where ta2lGroup.grpName=temp.paramName and ta2lGroup.tabvAttrValue.valueId='" +
              rootId + "' and ta2lGroup.a2lId='" + a2lId + "'");
      final TypedQuery<TA2lGroup> typeQuery = entMgrToUse.createQuery(query.toString(), TA2lGroup.class);
      for (TA2lGroup ta2lGroup : typeQuery.getResultList()) {
        if (ta2lGroup.getGrpName() != null) {
          groupNameIdMap.put(ta2lGroup.getGrpName(), ta2lGroup.getGroupId());
        }
      }

    }

    catch (Exception exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    return groupNameIdMap;
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
