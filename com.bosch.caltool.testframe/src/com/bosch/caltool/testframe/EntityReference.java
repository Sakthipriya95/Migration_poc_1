/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.testframe.exception.InvalidTestDataException;


/**
 * @author bne4cob
 */
final class EntityReference {

  /**
   * 
   */
  private static final int DEFAULT_REF_DET_SIZE = 1;

  /**
   * entity source key identifier pattern
   */
  public static final String ENT_SRC_KEY_PTTRN = ";src=";

  /**
   * entity field key identifier pattern
   */
  public static final String ENT_FIELD_KEY_PTTRN = ";field=";

  /**
   * Item separator
   */
  private static final String ITEM_SEP = ";";
  /**
   * property separator
   */
  private static final String PROP_SEP = ",";
  /**
   * test data source of entity
   */
  private static final String ENT_SRC_TESTDATA = "TD";
  /**
   * Database source of entity
   */
  private static final String ENT_SRC_DATABASE = "DB";
  /**
   * test data source of entity, but after stored in DB
   */
  private static final String ENT_SRC_TDPERS = "TDPERS";


  /**
   * @author bne4cob
   */
  public static enum REF_SOURCE {
    /**
     * Test data file
     */
    TEST_DATA(ENT_SRC_TESTDATA),
    /**
     * database
     */
    DATABASE(ENT_SRC_DATABASE),
    /**
     * test data file, but after saved to database
     */
    PERSISTED_TEST_DATA(ENT_SRC_TDPERS);

    private String src;

    REF_SOURCE(final String src) {
      this.src = src;
    }

    /**
     * @param src src text
     * @return REF_SOURCE
     * @throws InvalidTestDataException invalid input
     */
    public static REF_SOURCE getSource(final String src) throws InvalidTestDataException {
      for (REF_SOURCE source : REF_SOURCE.values()) {
        if (TestUtils.isEqual(source.src, TestUtils.unwrap(src, "\""))) {
          return source;
        }
      }
      throw new InvalidTestDataException("Invalid reference source - " + src);
    }
  }

  /**
   * Reference source
   */
  private REF_SOURCE source = REF_SOURCE.TEST_DATA;

  /**
   * Referred field. If the reference is to a field, rather than the entity
   */
  private String field;

  /**
   * Referred class, if refernce is to a field
   */
  private String refClass;

  /**
   * properties and values with which the reference is to be identified
   */
  private final Map<String, String> refProps = new ConcurrentHashMap<>();

  /**
   * The input reference string
   */
  private String refStringInput;

  /**
   * Private constructor. Use create(String) method to create the object.
   */
  private EntityReference() {
    // Private constructor
  }

  /**
   * @return the source
   */
  public REF_SOURCE getSource() {
    return this.source;
  }


  /**
   * @return the field
   */
  public String getField() {
    return this.field;
  }


  /**
   * @return refClass
   */
  public String getRefClass() {
    return this.refClass;
  }

  /**
   * @return the refProps
   */
  public Map<String, String> getRefProps() {
    return this.refProps;
  }

  /**
   * Creates the reference object using the refString
   * 
   * @param refString reference
   * @return EntityReference
   * @throws InvalidTestDataException invalid reference pattern
   */
  public static EntityReference parse(final String refString) throws InvalidTestDataException {
    if (TestUtils.isEmpty(refString)) {
      return null;
    }

    EntityReference ref = new EntityReference();

    ref.refStringInput = refString;

    String refText = TestUtils.unwrap(refString, "{", "}");
    String[] refDetailArr = refText.split(ITEM_SEP);

    // Decode the reference object
    ref.decodeRefObjectType(refDetailArr);

    // Decode the properties of the object
    String[] keyValGrpArr = refDetailArr[0].split(PROP_SEP);
    for (String keyVal : keyValGrpArr) {
      String keyValArr[] = TestUtils.getKeyAndValue(keyVal);
      ref.refProps.put(keyValArr[0], TestUtils.unwrap(keyValArr[1], "\""));
    }

    return ref;

  }

  /**
   * Decode the reference object
   * 
   * @param ref
   * @param refDetailArr
   * @throws InvalidTestDataException
   */
  private void decodeRefObjectType(final String... refDetailArr) throws InvalidTestDataException {
    if (refDetailArr.length > DEFAULT_REF_DET_SIZE) {
      for (int index = 1; index < refDetailArr.length; index++) {
        String[] propVal = TestUtils.getKeyAndValue(refDetailArr[index]);
        if (TestUtils.isEqual("src", propVal[0])) {
          this.source = REF_SOURCE.getSource(propVal[1]);
        }
        else if (TestUtils.isEqual("field", propVal[0])) {
          this.field = TestUtils.unwrap(propVal[1], "\"");
        }
        else if (TestUtils.isEqual("class", propVal[0])) {
          this.refClass = TestUtils.unwrap(propVal[1], "\"");
        }

      }
    }

    if (!TestUtils.isEmpty(this.field) && TestUtils.isEmpty(this.refClass)) {
      throw new InvalidTestDataException(
          "Reference entity class should be provided if field is mentioned. Invalid reference - " + this.refStringInput);
    }
  }

  /**
   * @param txtValue txtValue
   * @return true if this text follows reference value pattern
   */
  public static boolean isReferenceValue(final String txtValue) {
    return txtValue.indexOf('{') == 0;
  }

  /**
   * @param txtValue text
   * @return true if the value is a reference to an entity, created as part of test data
   */
  public static boolean isPersistedValue(final String txtValue) {
    return isReferenceValue(txtValue) && (txtValue.indexOf(EntityReference.ENT_FIELD_KEY_PTTRN) > 0) &&
        (txtValue.indexOf(EntityReference.ENT_SRC_KEY_PTTRN) > 0);
  }
}
