/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;

/**
 * @author MKL2COB
 */
public class A2LWPRespCopy {

  /**
   * Entity Manager, if an existing entity manager should be reused
   */
  private final EntityManager entMgr;

  /**
   * Instantiates a new A2L-WP resp copy.
   *
   * @param cmdData the cmd data
   */
  public A2LWPRespCopy(final ServiceData cmdData) {
    this.entMgr = cmdData.getEntMgr();
  }


  /**
   * @param mapOfResponsibilities map of key- wp resp id, value - a2l wp resp id
   * @return true if the update is success
   */
  public boolean copyA2LWPResponsibiliities(final Map<Long, Set<Long>> mapOfResponsibilities) {

    final long startTime = System.currentTimeMillis();

    boolean updateSucess = false;


    getLogger().debug("copying parameter responsibilities for count: " + mapOfResponsibilities.values().size());

    try {
      this.entMgr.getTransaction().begin();

      for (Entry<Long, Set<Long>> respToUpdate : mapOfResponsibilities.entrySet()) {
        if (!respToUpdate.getValue().isEmpty()) {
          final StringBuilder query = new StringBuilder();
          query.append("Update T_A2L_WP_RESP set RESP_ID= ?  where A2L_WP_RESP_ID in (");
          for (Long wpRespId : respToUpdate.getValue()) {
            query.append(wpRespId);
            query.append(",");
          }
          query.deleteCharAt(query.length() - 1);
          query.append(")");
          Query nativeQuery = this.entMgr.createNativeQuery(query.toString());
          nativeQuery.setParameter(1, respToUpdate.getKey());
          nativeQuery.executeUpdate();
        }
      }
      // commit the transaction
      this.entMgr.getTransaction().commit();
      updateSucess = true;
    }
    catch (Exception exp) {
      // close the entity manager and rollback the transaction

      if (this.entMgr.getTransaction() != null) {
        this.entMgr.getTransaction().rollback();
      }

      getLogger().error(exp.getMessage(), exp);
      updateSucess = false;
    }
    getLogger().debug("time taken : " + (System.currentTimeMillis() - startTime));
    return updateSucess;
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
