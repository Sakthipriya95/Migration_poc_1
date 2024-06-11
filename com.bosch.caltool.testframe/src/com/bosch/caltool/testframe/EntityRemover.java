/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


/**
 * Class to delete the entities
 * 
 * @author bne4cob
 */
public class EntityRemover {

  /**
   * Entity manager factory
   */
  private final EntityManagerFactory emf;

  /**
   * Constructor
   * 
   * @param emf EntityManagerFactory
   */
  public EntityRemover(final EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Deletes the entities in the input list. The order of deletion is from bottom to top.
   * 
   * @param entityList list of entities to be deleted
   */
  public void deleteEntitiesFromDB(final List<Object> entityList) {
    if ((entityList == null) || entityList.isEmpty()) {
      return;
    }

    EntityManager entMgr = null;

    try {
      entMgr = this.emf.createEntityManager();
      entMgr.getTransaction().begin();

      Object entity;
      Object entID;
      Object entityNew;

      // Using em.find(), get the entity for current em, then delete them using em.remove() method
      // find() method ensures that the entity is still valid, ie, not deleted during test
      TestUtils.getTframeLogger().debug("No. of entities to remove - " + entityList.size());
      for (int indx = entityList.size() - 1; indx >= 0; indx--) {
        entity = entityList.get(indx);
        entID = this.emf.getPersistenceUnitUtil().getIdentifier(entity);

        entityNew = entMgr.find(entity.getClass(), entID);
        if (entityNew != null) {
          entMgr.remove(entityNew);
        }
      }

      // Commit the transactions
      entMgr.getTransaction().commit();
      TestUtils.getTframeLogger().debug("Entities deleted successfully");
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }

  }
}
