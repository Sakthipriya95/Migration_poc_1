/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.testframe.entity.ParentBean;
import com.bosch.caltool.testframe.entity.WokeyrefTable;
import com.bosch.caltool.testframe.exception.InvalidTestDataException;
import com.bosch.caltool.testframe.exception.TestDataException;
import com.bosch.caltool.testframe.exception.TestDataReaderException;


/**
 * Tests the entity creator
 *
 * @author bne4cob
 */
public class TestEntityCreator extends JUnitTest {

  /**
   * Entity Manager factory
   */
  private static EntityManagerFactory emf;

  /**
   * Initialise the DB connection
   */
  @BeforeClass
  public static void initialise() {
    // Initialize other loggers
   TestUtils.createLoggers(TestEntityCreator.class);

    // Initialize the EntityManagerFactory
    TestUtils.setClassLoader(TestEntityCreator.class.getClassLoader());
    emf = TestUtils.createEMF(TFTConstants.JPA_PERS_UNIT);
    createPrereqRefEntities();
  }

  /**
   * Tests the creation of entities from an XML data file
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator1() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    List<Object> entityList =
        creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/Test1X.xml" });

    // Verify the entity created
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 4, entityList.size());
  }

  /**
   * Tests the creation of entities from multiple XML data file
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator2() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    List<Object> entityList = creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML,
        new String[] { "testdata/Test1X.xml", "testdata/Test2X.xml" });

    // Verify the entity created
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 5, entityList.size());
  }

  /**
   * Tests the creation of entity with reference to an existing entity in the database. The reference entity is created
   * as a prerequisite and removed after the tests.
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator3() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    List<Object> entityList =
        creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/Test3X.xml" });

    // Verify the entity created
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 1, entityList.size());
  }

  /**
   * Test to create an WokeyrefTable with the ParentBean as reference to an created earlier, but from test data file.
   * Also the reference is only the parentbean's id, not the relation.
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator4() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    List<Object> entityList = creator.createEntities("testdata/Test4X.xml");

    // Verify the entity created
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 2, entityList.size());

    ParentBean parBean = null;
    WokeyrefTable refBean = null;

    for (Object entity : entityList) {
      if (entity instanceof ParentBean) {
        parBean = (ParentBean) entity;
      }
      else if (entity instanceof WokeyrefTable) {
        refBean = (WokeyrefTable) entity;
      }
    }

    // Verify whether entities are created
    assertNotNull("Parent entity should not be null", parBean);
    assertNotNull("Reference entity should not be null", refBean);

    // Verify the relationship between entities
    assertFalse("Reference entity's ID should not be 0L", refBean.getNodeId().longValue() == 0L);
    assertEquals("Parent bean's ID and Reference bean's Node ID should be same", parBean.getId(),
        refBean.getNodeId().longValue());


  }

  /**
   * Tests the basic creation of entity without any relation
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator6() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    List<Object> entityList =
        creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/TD-EntitySimple.xml" });
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 1, entityList.size());
  }

  /**
   * Tests the constructor that accepts root folder
   *
   * @throws TestDataException any exception
   */
  @Test
  public void testEntityCreator7() throws TestDataException {
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf, "testdata");
    List<Object> entityList =
        creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "TD-EntitySimple.xml" });
    assertFalse("Entity after creation", entityList.isEmpty());
    assertEquals("No of entities created", 1, entityList.size());
  }

  /**
   * Tests the creator when data file is given with invalid entity name (expected = TestDataReaderException.class)
   *
   * @throws TestDataException data exception
   */
  @Test
  public void testInvalidDataFile01() throws TestDataException {

    this.thrown.expectMessage("Invalid entity name - com.bosch.caltool.testframe.entity.InvalidEntity");
    this.thrown.expect(TestDataException.class);

    // Try to create entities from a file, with invalid entity name in it
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/TestInvalidDataFile-01.xml" });
  }

  /**
   * Tests the creator when data file is given with invalid entity property (expected = TestDataReaderException.class)
   *
   * @throws TestDataException data exception
   */
  @Test
  public void testInvalidDataFile02() throws TestDataException {

    this.thrown.expectMessage(
        "The parameter 'invalidparam' not found in the entity com.bosch.caltool.testframe.entity.ParentBean");
    this.thrown.expect(InvalidTestDataException.class);

    // Try to create entities from a file, with invalid entity property in it
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/TestInvalidDataFile-02.xml" });
  }

  /**
   * Tests the creator when an invalid file is provided as input (expected = TestDataReaderException.class)
   *
   * @throws TestDataException data exception
   */
  @Test
  public void testFileNotExistValidation() throws TestDataException {

    this.thrown.expectMessage("Unable to read the data file");
    this.thrown.expect(TestDataReaderException.class);

    // Try to create entities from a file, that does not exist
    EntityCreator creator = new EntityCreator(TestEntityCreator.emf);
    creator.createEntities(TFConstants.DATA_INPUT_TYPE.XML, new String[] { "testdata/InvalidFile-DoesNotExist.xml" });
  }


  /**
   * clear Existing Data, close EMF
   */
  @AfterClass
  public static void clearExistingData() {
    EntityManager entMgr = null;
    try {
      entMgr = TestEntityCreator.emf.createEntityManager();
      entMgr.getTransaction().begin();

      // Clear all ChildBean entities
      Query queryC = entMgr.createQuery("delete from ChildBean cb");
      queryC.executeUpdate();

      // Clear all ParentBean entities
      Query queryP = entMgr.createQuery("delete from ParentBean pb");
      queryP.executeUpdate();

      // Clear all WokeyrefTable entities
      Query queryW = entMgr.createQuery("delete from WokeyrefTable wob");
      queryW.executeUpdate();

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

    TestUtils.closeEMF(emf);
  }

  /**
   * Creates prerequisite data
   */
  private static void createPrereqRefEntities() {
    EntityManager entMgr = null;
    try {
      entMgr = TestEntityCreator.emf.createEntityManager();
      entMgr.getTransaction().begin();

      // Create a parent entity
      ParentBean pEntity = new ParentBean();
      pEntity.setName("Ref parent-01");
      pEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
      pEntity.setAddress("add r");
      entMgr.persist(pEntity);

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
}
