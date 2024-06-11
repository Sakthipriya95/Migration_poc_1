/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.testframe.entity.ChildBean;
import com.bosch.caltool.testframe.entity.ParentBean;
import com.bosch.caltool.testframe.exception.TestDataException;


/**
 * Tests the BeanRemover class
 *
 * @author bne4cob
 */
public class TestEntityRemover extends JUnitTest {

  /**
   * Entity Manager factory
   */
  private static EntityManagerFactory emf;

  /**
   * Initialises the logger and DB connection
   */
  @BeforeClass
  public static void initialise() {
    // Initialize other loggers
    TestUtils.createLoggers(TestEntityCreator.class);

    // Initialize the EntityManagerFactory
    TestUtils.setClassLoader(TestEntityCreator.class.getClassLoader());
    emf = TestUtils.createEMF(TFTConstants.JPA_PERS_UNIT);
  }


  /**
   * Tests the BeanRemover class. The tests creates some entities using the EntityCreator and then removes them using
   * EntityRemover
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityRemover() throws TestDataException {
    clearExistingData();

    // Create the entities to be removed by the EntityRemover
    EntityCreator creator = new EntityCreator(emf);
    List<Object> entityList =
        creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/Test1X.xml" });
    assertFalse("Entity size after creation", entityList.isEmpty());

    // Delete the entities by the EntityRemover
    EntityRemover remover = new EntityRemover(emf);
    remover.deleteEntitiesFromDB(entityList);

    // Verify the database tables whether the entites are deleted
    EntityManager entMgr = null;
    try {
      entMgr = emf.createEntityManager();

      // Verify CHILD_BEAN table
      TypedQuery<ChildBean> queryC = entMgr.createQuery("select cb from ChildBean cb", ChildBean.class);
      final List<ChildBean> cList = queryC.getResultList();
      assertTrue("Verify all records are removed", cList.isEmpty());

      // Verify PARENT_BEAN table
      TypedQuery<ParentBean> queryP = entMgr.createQuery("select pb from ParentBean pb", ParentBean.class);
      final List<ParentBean> pList = queryP.getResultList();
      assertTrue("Verify all records are removed", pList.isEmpty());

    }
    catch (Exception e) {
      TESTER_LOGGER.warn(e.getMessage(), e);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
  }

  /**
   * Clear existing data in the test tables
   */
  private static void clearExistingData() {
    EntityManager entMgr = null;
    try {
      entMgr = emf.createEntityManager();
      entMgr.getTransaction().begin();

      Query queryC = entMgr.createQuery("delete from ChildBean cb");
      queryC.executeUpdate();

      Query queryP = entMgr.createQuery("delete from ParentBean pb");
      queryP.executeUpdate();

      entMgr.getTransaction().commit();
    }
    catch (Exception e) {
      TESTER_LOGGER.warn(e.getMessage(), e);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
  }


  /**
   * Clean up items after the test execution
   */
  @AfterClass
  public static void doCleanup() {
    clearExistingData();
    // Close the DB connection
    TestUtils.closeEMF(emf);
  }
}
