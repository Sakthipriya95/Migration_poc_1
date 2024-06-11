/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;

/**
 * @author rgo7cob
 */
public class CompliParamFetcher {


  /**
   * @return the compli output
   */
  public CompliParamOutput fetchCompliPrams() {
    EntityManager entMgrToUse = null;

    CompliParamOutput output = null;

    try {
      // Create new Entity Manager for this transaction. To avoid Concurrent Modification exception.
      entMgrToUse = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
      entMgrToUse.getTransaction().begin();


      output = fetchCompliList(entMgrToUse);

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

      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    finally {

      try {
        // Close the entity manager, if it is created locally
        if ((entMgrToUse != null) && entMgrToUse.isOpen()) {
          entMgrToUse.close();
        }
      }
      catch (Exception exp) {
        // log the exception
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }


    return output;

  }

  /**
   * @param entMgrToUse entMgrToUse
   * @return TWorkpackage
   */
  private CompliParamOutput fetchCompliList(final EntityManager entMgrToUse) {

    CompliParamOutput output = new CompliParamOutput();
    List<TParameter> resultList = getCompliParamList(entMgrToUse);
    for (TParameter tparam : resultList) {
      output.getCompliParamMap().put(tparam.getName(), tparam.getPtype());
    }
    return output;
  }

  /**
   * @param entMgrToUse
   * @return
   */
  public List<TParameter> getCompliParamList(final EntityManager entMgrToUse) {
    final StringBuilder query = new StringBuilder();
    query.append("Select tparam from TParameter tparam where tparam.ssdClass='COMPLIANCE'");

    final TypedQuery<TParameter> typeQuery = entMgrToUse.createQuery(query.toString(), TParameter.class);
    List<TParameter> resultList = typeQuery.getResultList();
    return resultList;
  }

}
