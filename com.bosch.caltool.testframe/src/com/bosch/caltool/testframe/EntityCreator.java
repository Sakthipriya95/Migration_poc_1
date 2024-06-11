/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.bosch.caltool.testframe.exception.InvalidTestDataException;
import com.bosch.caltool.testframe.exception.TestDataException;
import com.bosch.caltool.testframe.testdata.DataReader;
import com.bosch.caltool.testframe.testdata.InputData;


/**
 * Entity creator class.
 * <p>
 * Sample Usage : <br>
 * <code>
 *  EntityCreator creator = new EntityCreator(emf);<br>
 *  create.createEntities(new String[]{"testdata/Data_001.xml"});
 * </code>
 *
 * @author bne4cob
 */
public class EntityCreator {

  /**
   * Entity manager factory
   */
  private final EntityManagerFactory emf;
  /**
   * Root directory of test data files
   */
  private final String testdataRootDir;

  /**
   * Creates an instance of this class
   *
   * @param emf EntityManagerFactory
   */
  public EntityCreator(final EntityManagerFactory emf) {
    this.emf = emf;
    this.testdataRootDir = "";
  }

  /**
   * Creates an instance of this class
   *
   * @param emf EntityManagerFactory
   * @param testDataRootDir Root directory of test data files
   */
  public EntityCreator(final EntityManagerFactory emf, final String testDataRootDir) {
    this.emf = emf;

    StringBuilder rootDir = new StringBuilder(testDataRootDir);
    if (!testDataRootDir.endsWith(File.separator)) {
      rootDir.append(File.separator);
    }
    this.testdataRootDir = rootDir.toString();
  }

  /**
   * @param tdFiles test data files
   * @return map of entities
   * @throws TestDataException any exception while creating entities
   */
  public List<Object> createEntities(final String... tdFiles) throws TestDataException {
    return createEntities(TFConstants.DEF_INPUT_TYPE, tdFiles);
  }

  /**
   * @param inputType input type
   * @param tdFiles test data files
   * @return map of entities
   * @throws TestDataException any exception while creating entities
   */
  public List<Object> createEntities(final TFConstants.DATA_INPUT_TYPE inputType, final String... tdFiles)
      throws TestDataException {

    // Entity cache, to store the created entities for a single run of create entities
    EntityCache cache = new EntityCache();

    // The list of entities, created, in that order. This is returned bye the method
    List<Object> retEntList = new ArrayList<>();

    TestUtils.getTframeLogger().info("Entity Creator : Reading files, number of files to read - " + tdFiles.length);
    DataReader reader = new DataReader(inputType);

    List<InputData> dataList = new ArrayList<>();
    String testDataFile;
    for (String tDataFile : tdFiles) {
      testDataFile = TestUtils.checkNull(this.testdataRootDir) + tDataFile;
      dataList.addAll(reader.readData(testDataFile));
    }

    boolean batchUpdEnabled = canUpdateAsBatch(dataList);

    EntityManager entMgr = null;
    try {
      entMgr = this.emf.createEntityManager();
      entMgr.getTransaction().begin();

      for (InputData tData : dataList) {
        Object entity = createEntity(tData, cache);

        if (!batchUpdEnabled) {
          createEntityInDB(entMgr, entity);
        }

        // Add entity to tracking cache
        cache.addEntity(tData.getRefClass(), entity);

        // Add entity to ret list
        retEntList.add(entity);
      }

      TestUtils.getTframeLogger()
          .info("Entity Creator : Total number of entities to be created - " + retEntList.size());
      if (batchUpdEnabled) {
        createEntityInDB(entMgr, retEntList);
      }
      entMgr.getTransaction().commit();
      TestUtils.getTframeLogger().info("Entity Creator : Entities created successfully");
    }
    catch (TestDataException exp) {
      // No special handling here for TestDataexception
      throw exp;
    }
    catch (Exception exp) {
      // Wrap all exceptions as test data exception
      throw new TestDataException(exp.getMessage(), exp);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
    return retEntList;
  }

  /**
   * Create entity in database. This method is invoked, when entities to be persisted one by one, disabling batch
   * update.
   *
   * @param entityList list of entities extracted from the test data source
   */
  private void createEntityInDB(final EntityManager entMgr, final Object entity) {
    entMgr.persist(entity);
    entMgr.flush();
    entMgr.refresh(entity);
  }

  /**
   * Create entities in database. This persists the entities as batch
   *
   * @param entityList list of entities extracted from the test data source
   */
  private void createEntityInDB(final EntityManager entMgr, final List<Object> entityList) {
    for (Object entity : entityList) {
      entMgr.persist(entity);
    }

    entMgr.flush();

    // Refresh the entities once, so that the changes made directly in the DB are also fetched
    for (Object entity : entityList) {
      entMgr.refresh(entity);
    }

  }


  /**
   * Create the entity bean using the Data
   *
   * @param tData input test data
   * @param cache Entity Cache
   * @return the new entity
   * @throws TestDataException exception
   */
  private Object createEntity(final InputData tData, final EntityCache cache) throws TestDataException {
    Class<?> entClass;
    try {
      entClass = Class.forName(tData.getRefClass(), true, TestUtils.getClassLoader());
    }
    catch (ClassNotFoundException exp) {
      throw new TestDataException("Invalid entity name - " + tData.getRefClass(), exp);
    }
    Object newInst;
    try {
      newInst = entClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException exp) {
      throw new TestDataException(CommonProps.DEF_EXPTN_MSG, exp);
    }

    Map<String, PropertyDescriptor> propMap = TestUtils.getProperties(newInst);
    ValueFinder valFinder = new ValueFinder(this.emf, cache);

    for (Entry<String, String> entry : tData.getParams().entrySet()) {
      TestUtils.getTframeLogger().debug(entry.toString());

      String field = entry.getKey();
      PropertyDescriptor propDes = propMap.get(field.toUpperCase(Locale.getDefault()));
      if (propDes == null) {
        throw new InvalidTestDataException("The parameter '" + field + "' not found in the entity " +
            tData.getRefClass());
      }

      TestUtils.setPropertyValue(propDes, newInst, valFinder.getValueObject(propDes, entry.getValue()));

    }
    return newInst;
  }


  /**
   * Checks whether batch update can be performed for updating the records to DB
   *
   * @param dataList input data list
   * @return
   */
  private boolean canUpdateAsBatch(final List<InputData> dataList) {
    Set<String> valSet = new HashSet<>();
    for (InputData data : dataList) {
      valSet.addAll(data.getParams().values());
    }
    for (String val : valSet) {
      if (EntityReference.isPersistedValue(val)) {
        return false;
      }
    }
    return true;
  }

}
