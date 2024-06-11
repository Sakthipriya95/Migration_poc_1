/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.JpaCache;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.IEntityType;


/**
 * Abstract class for entity provider
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractEntityProvider {

  /**
   * Monitor object for transaction
   */
  private static final Object TRANS_MONITOR_OBJ = new Object();

  /**
   * flag to check whether tramsaction has been started or not
   */
  private static boolean transStarted;

  /**
   * Wait timeout for transaction
   */
  private static final long TRANS_WAIT_TMEOUT = 1000L * 60 * 30;// 30 minutes

  /**
   * Create a new instance of Entity Provider
   *
   * @param entityTypes entity types applicable to this data model
   */
  public AbstractEntityProvider(final IEntityType<?, ?>[] entityTypes) {
    ObjectStore.getInstance().registerEntityTypes(entityTypes);
  }

  /**
   * IMPORTANT : Override this method, only to get access
   *
   * @return the entity manager
   */
  public final EntityManager getEm() {
    return ObjectStore.getInstance().getEntityManager();
  }


  /**
   * If the entity is not present in the entity manager then the entity will be added to the Em using the merge and will
   * be returned. since merge creates a new entity object it needs to be returned to the methods using the entity.
   *
   * @param entity to be refreshed
   * @return the entity after refresh
   */
  public Object refreshCacheObject(final Object entity) {
    Object retEntity = entity;

    try {
      if (!getEm().contains(entity)) {
        retEntity = getEm().merge(entity);
      }
      getEm().refresh(retEntity);
    }

    // Special case if the entity is updated and deleted. If the change notification for update comes before delete then
    // entity no longer
    // exists occurs. This exception is caught here
    catch (Exception exp) {
      ObjectStore.getInstance().getLogger()
          .error("Entity refresh for update happens after deleting the entity" + exp.getMessage(), exp);
    }
    return retEntity;
  }

  /**
   * @return JpaCache
   */
  public JpaCache getCache() {
    return (JpaCache) getEm().getEntityManagerFactory().getCache();
  }

  /**
   * Commit the open transaction If no transaction is open, do nothing
   */
  public void commitChanges() {
    synchronized (TRANS_MONITOR_OBJ) {
      if (getEm().getTransaction().isActive()) {
        getEm().getTransaction().commit();
      }
    }
  }

  /**
   * Delete an Entity from the EntityManager
   *
   * @param oldEntity The Entity to be deleted
   */
  public void deleteEntity(final Object oldEntity) {
    synchronized (TRANS_MONITOR_OBJ) {
      getEm().remove(oldEntity);
    }
  }

  /**
   * Start a new Transaction If a transaction is still open, do nothing
   * <p>
   * <b>IMPORTANT</b>: The transaction should be finished using by calling the <code>endTransaction()</code> method
   * always.
   */
  public void startTransaction() {

    synchronized (TRANS_MONITOR_OBJ) {
      // Waits until thre transaction start flag is turned off (by endTransaction() method)
      while (transStarted) {
        try {
          ObjectStore.getInstance().getLogger().debug("AbstractEntityProvider : Waiting to start transaction ...");
          TRANS_MONITOR_OBJ.wait(TRANS_WAIT_TMEOUT);
        }
        catch (InterruptedException exp) {
          ObjectStore.getInstance().getLogger().error(exp.getMessage(), exp);
        }
      }

      // Start the transaction
      if (!getEm().getTransaction().isActive()) {
        getEm().getTransaction().begin();
        ObjectStore.getInstance().addTransIDToQueue(getTransactionID());
      }
      ObjectStore.getInstance().getLogger().debug("AbstractEntityProvider : Transaction started");
      transStarted = true;
    }
  }

  /**
   * Ends the transaction. Turns off the 'transaction started' flag and notifies the other threads waiting to start the
   * transaction.
   * <p>
   * Note : This method does not have any database activity.
   */
  public void endTransaction() {
    synchronized (TRANS_MONITOR_OBJ) {
      // Turns off the transaction started flag and notify the next thread in the queue
      transStarted = false;
      TRANS_MONITOR_OBJ.notifyAll();
      ObjectStore.getInstance().getLogger().debug("AbstractEntityProvider : Transaction ended");
    }
  }

  /**
   * Register a new Entity in the EntityManager
   *
   * @param newEntity the new entity
   */
  public void registerNewEntity(final Object newEntity) {
    synchronized (TRANS_MONITOR_OBJ) {
      getEm().persist(newEntity);
    }
  }

  /**
   * @return the transaction ID of this transaction.
   */
  public String getTransactionID() {
    final Query query = getEm().createNativeQuery("select F_Get_Transaction_ID from dual");
    @SuppressWarnings("rawtypes")
    final List resList = query.getResultList();
    if (!resList.isEmpty()) {
      return resList.get(0).toString();
    }

    return "";

  }

  /**
   * roll back the transaction
   */
  public void rollBackTransaction() {
    synchronized (TRANS_MONITOR_OBJ) {
      if (getEm().getTransaction().isActive()) {
        getEm().getTransaction().rollback();
      }
    }
  }

}
