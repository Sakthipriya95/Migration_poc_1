/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Utility methods for JPA
 *
 * @author bne4cob
 */
public final class JPAUtils {


  private JPAUtils() {
    // No impl for Util class
  }

  /**
   * Creates an Entity Manager using the given factory
   *
   * @param emf Entity ManagerFactory
   * @return new Entity Manager
   */
  public static EntityManager createEntityManager(final EntityManagerFactory emf) {
    return emf.createEntityManager();
  }

  /**
   * Starts the transaction, if not started already, in the given Entity Manager
   *
   * @param entMgr Entity Manager
   */
  public static void beginTransaction(final EntityManager entMgr) {
    if (!entMgr.getTransaction().isActive()) {
      entMgr.getTransaction().begin();
    }
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, rolls back the transaction
   *
   * @param entMgr Entity Manager
   */
  public static void rollbackTransaction(final EntityManager entMgr) {
    if ((entMgr != null) && entMgr.getTransaction().isActive()) {
      entMgr.getTransaction().rollback();
    }
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, commits the transaction.
   *
   * @param entMgr Entity Manager
   */
  public static void commitTransaction(final EntityManager entMgr) {
    if ((entMgr != null) && entMgr.getTransaction().isActive()) {
      entMgr.getTransaction().commit();
    }
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, rolls back the transaction and closes it.
   *
   * @param entMgr Entity Manager
   */
  public static void rollbackTransactionAndCloseEm(final EntityManager entMgr) {
    rollbackTransaction(entMgr);
    closeEntityManager(entMgr);
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, commits the transaction and closes it.
   *
   * @param entMgr Entity Manager
   */
  public static void commitTransactionAndCloseEm(final EntityManager entMgr) {
    commitTransaction(entMgr);
    closeEntityManager(entMgr);
  }

  /**
   * Closes the given entity manager, if EM is 'not null' and 'open'
   *
   * @param entMgr Entity Manager
   */
  public static void closeEntityManager(final EntityManager entMgr) {
    if ((entMgr != null) && entMgr.isOpen()) {
      entMgr.close();
    }
  }

  /**
   * Closes the given Entity Manager Factory, if 'not null' and 'open'
   *
   * @param emf Entity Manager Factory
   */
  public static void closeEMF(final EntityManagerFactory emf) {
    if ((emf != null) && emf.isOpen()) {
      emf.close();
    }
  }

}
