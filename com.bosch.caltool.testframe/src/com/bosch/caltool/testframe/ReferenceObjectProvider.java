/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.bosch.caltool.testframe.exception.InvalidTestDataException;
import com.bosch.caltool.testframe.exception.TestDataException;


/**
 * Finds the referred object in the test data.
 * <p>
 * Sample format of the reference
 * <code>{name="ObjectName";src=TDPERS;class=com.testframe.entity.ParentBean;field=id}</code>
 * 
 * @author bne4cob
 */
class ReferenceObjectProvider {

  /**
   * String buffer query size
   */
  private static final int SB_QUERY_SIZE = 60;

  /**
   * Valid number of records for the result set
   */
  private static final int VALID_REC_COUNT = 1;

  /**
   * Separator character for property key, if property is a relation
   */
  private static final char PROP_REL_SEP = '.';

  /**
   * Length of property relation array, if property belongs to current bean
   */
  private static final int CUR_BEAN_PROP_REL_LEN = 1;

  /**
   * EntityManagerFactory
   */
  private final EntityManagerFactory emf;

  /**
   * Entity cache
   */
  private final EntityCache cache;

  /**
   * Constructor
   * 
   * @param emf EntityManagerFactory
   * @param cache Entity cache instance
   */
  public ReferenceObjectProvider(final EntityManagerFactory emf, final EntityCache cache) {
    this.emf = emf;
    this.cache = cache;
  }


  /**
   * Gets the reference object for the entity field
   * 
   * @param propDes property descriptor of the field of entity being created
   * @param refText reference text in the test data
   * @return referenced entity field value
   * @throws TestDataException exception
   */
  public Object getReference(final PropertyDescriptor propDes, final String refText) throws TestDataException {
    EntityReference entRef = EntityReference.parse(refText);
    String refEntityType;
    if (TestUtils.isEmpty(entRef.getRefClass())) {
      refEntityType = propDes.getPropertyType().getName();
    }
    else {
      refEntityType = entRef.getRefClass();
    }
    Object refEntity = getRefEntity(refEntityType, entRef);
    Object refValue;
    if (TestUtils.isEmpty(entRef.getField())) {
      refValue = refEntity;
    }
    else {
      refValue = TestUtils.getPropertyValue(refEntity, entRef.getField());
    }
    return refValue;
  }

  /**
   * Retrieve the entity referred in the current entiy being processed, using the reference pattern
   * 
   * @param type entity type
   * @param ref reference string
   * @return entity
   * @throws TestDataException if entity cannot be found
   */
  private Object getRefEntity(final String type, final EntityReference entRef) throws TestDataException {
    if (entRef.getSource() == EntityReference.REF_SOURCE.DATABASE) {
      return getEntityFromDB(type, entRef.getRefProps());
    }
    return getEntityFromTD(type, entRef.getRefProps());
  }

  /**
   * Retrieve the reference from database
   * 
   * @param type entity type
   * @param refPropMap map of entity props and values
   * @return the entity from database
   * @throws InvalidTestDataException
   */
  private Object getEntityFromDB(final String type, final Map<String, String> refPropMap)
      throws InvalidTestDataException {

    StringBuilder query = new StringBuilder(SB_QUERY_SIZE);
    query.append("select ent from ").append(type).append(" ent where ");
    boolean firstProp = true;
    for (Entry<String, String> entry : refPropMap.entrySet()) {
      if (firstProp) {
        firstProp = false;
      }
      else {
        query.append(" and ");
      }
      query.append("ent.").append(entry.getKey()).append(" = '").append(entry.getValue()).append('\'');

    }

    EntityManager entMgr = null;
    try {
      entMgr = this.emf.createEntityManager();

      Query jpaQuery = entMgr.createQuery(query.toString());

      final List<?> dbRetList = jpaQuery.getResultList();

      if (dbRetList.isEmpty()) {
        throw new InvalidTestDataException("The given reference did not return any result : " + refPropMap);
      }
      if (dbRetList.size() > VALID_REC_COUNT) {
        throw new InvalidTestDataException(
            "The reference returned multiple results. Please specify a unique criteria. Input : " + refPropMap);
      }
      return dbRetList.get(0);

    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }

  }

  /**
   * Retrieve the entity from test data already collected
   * 
   * @param type entity type
   * @param refPropMap map of properies and values
   * @return entity
   * @throws TestDataException if entity cannot be found
   */
  private Object getEntityFromTD(final String type, final Map<String, String> refPropMap) throws TestDataException {

    // Return of cache.getEntityes() will not be null
    for (Object entity : this.cache.getEntities(type)) {
      if (isMatchingObject(entity, refPropMap)) {
        return entity;
      }
    }

    throw new InvalidTestDataException("Reference not defined");

  }

  /**
   * Checks whether a match can found for the given set of object properties
   * 
   * @param bean bean
   * @param prop properties and values
   * @return true, if match is not found
   * @throws TestDataException for errors
   */
  private boolean isMatchingObject(final Object bean, final Map<String, String> prop) throws TestDataException {

    for (Entry<String, String> entry : prop.entrySet()) {
      String propKey = entry.getKey();

      // Property key can point to a property in the input bean or a referential entity in the bean
      // Type 1 : e.g. bean - TabvAttrValue, property - attrValueEng
      // Type 2 : e.g. bean - TabvProjectidcard, property - tabvAttrValue.attrValueEng
      String[] propRelation = propKey.split("\\" + PROP_REL_SEP);
      String curProperty = propRelation[0];

      Object beanPropVal = TestUtils.getPropertyValue(bean, curProperty);
      String value = entry.getValue();

      // If the property referes to property in current entity, check for value match directly
      if (propRelation.length == CUR_BEAN_PROP_REL_LEN) {
        if (!value.equals(beanPropVal.toString())) {
          return false;
        }
      }
      else {
        // if the property refers to a property in the dependent entity, search in that entity
        ConcurrentMap<String, String> childPropMap = new ConcurrentHashMap<>();
        String childPropKey = propKey.substring(propKey.indexOf(PROP_REL_SEP) + 1);
        childPropMap.put(childPropKey, value);
        if (!isMatchingObject(beanPropVal, childPropMap)) {
          return false;
        }
      }

    }

    return true;
  }

}
