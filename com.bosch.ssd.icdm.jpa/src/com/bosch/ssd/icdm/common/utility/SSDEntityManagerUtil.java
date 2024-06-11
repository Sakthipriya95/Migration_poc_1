/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.common.utility;

import javax.persistence.EntityManager;

import com.bosch.ssd.icdm.model.SSDConfigEnums.TransactionState;


/**
 * Utility class to handle all entity manager operations. Begin & Commin transaction for all the service methods should
 * happen though this class "handleTransaction" method alone and no where else.
 *
 * @author SSN9COB
 */
public final class SSDEntityManagerUtil {

  private SSDEntityManagerUtil() {
    // Hidden Constructor
  }

  /**
   * To begin the transaction if transaction is not active
   *
   * @param entityManager Entity Manager
   */
  private static void beginTransaction(final EntityManager entityManager) {
    // check if active
    if (!entityManager.getTransaction().isActive()) {
      // begin transaction
      entityManager.getTransaction().begin();
    }
  }

  /**
   * commit the current import
   *
   * @param entityManager Entity Manager
   */
  private static void commitTransaction(final EntityManager entityManager) {
    // commit the transaction
    entityManager.getTransaction().commit();
  }

  /**
   * rollback the current import
   *
   * @param entityManager Entity Manager
   */
  private static void rollBackTransaction(final EntityManager entityManager) {
    // check if active
    if (entityManager.getTransaction().isActive()) {
      // rollnback
      entityManager.getTransaction().rollback();
    }
  }

  /**
   * Handle the events of a transaction
   *
   * @param stateOfTransaction state
   * @param entityManager entityManager
   */
  public static void handleTransaction(final TransactionState stateOfTransaction, final EntityManager entityManager) {
    // Switch based on the state and invoke respective methods
    switch (stateOfTransaction) {
      case TRANSACTION_BEGIN:
        beginTransaction(entityManager);
        break;
      case TRANSACTION_COMMIT:
        commitTransaction(entityManager);
        break;
      case TRANSACTION_ROLLBACK:
        rollBackTransaction(entityManager);
        break;
      default:
        break;
    }
  }
}
