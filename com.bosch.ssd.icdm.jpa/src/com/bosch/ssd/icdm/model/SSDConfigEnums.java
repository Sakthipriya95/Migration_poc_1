/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;


/**
 * All the ENUMS used in SSD Interface module
 *
 * @author SSN9COB
 */
public class SSDConfigEnums {

  /**
   * @author SSN9COB ENUM for Config Parameters used in SSD
   */
  public enum SSDConfigParams {
                               /**
                                * For QSSD REVISION NODE ID
                                */
                               QSSD_REVISION_NODE_ID("QSSD_REVISION_NODE_ID");


    private final String key;

    SSDConfigParams(final String name) {
      this.key = name;
    }

    /**
     * @return key
     */
    public String getKey() {
      return this.key;
    }
  }

  /**
   * For SSD rule, list of Maturity Levels
   */
  public enum MATURITY_LEVEL {
                              /**
                               * Constant to define Maturity level : START
                               */
                              START,
                              /**
                               * Constant to define Maturity level : STANDARD
                               */
                              STANDARD,
                              /**
                               * Constant to define Maturity level : FIXED
                               */
                              FIXED
  }

  /**
   * @author apl1cob
   */
  public enum ParameterClass {
                              /**
                               * component type label
                               */
                              COMPONENT,
                              /**
                               * monitoring type label
                               */
                              MONITORING,
                              /**
                               * customer specific labels
                               */
                              CUSTSPEC;

    /**
     * Decode the string to the ParameterClass Enum
     *
     * @param literal literal
     * @return enumeration
     */
    public static ParameterClass getType(final String literal) {
      for (ParameterClass paramClass : ParameterClass.values()) {
        if (paramClass.name().equalsIgnoreCase(literal)) {
          return paramClass;
        }
      }
      return null;
    }

  }

  /**
   * Enum for type and subtype of rule
   *
   * @author HSU4COB
   */
  public enum SSDRuleTypeSubType {
                                  /**
                                   * type,subtype for the 'VALUE' type rule
                                   */
                                  VALUE("Kgs", "Kw"),
                                  /**
                                   * type,subtype for the 'VAL_BLK' type rule
                                   */
                                  VAL_BLK("Kgs", "Kwb"),
                                  /**
                                   * type,subtype for the 'ASCII' type rule
                                   */
                                  ASCII("Kgs", "Ktx"),
                                  /**
                                   * type,subtype for the 'CURVE' type rule
                                   */
                                  CURVE("Kgs", "Kl"),
                                  /**
                                   * type,subtype for the 'MAP' type rule
                                   */
                                  MAP("Kgs", "Kf"),
                                  /**
                                   * type,subtype for the 'AXIS_PTS' type rule
                                   */
                                  AXIS_PTS("Kgs", "GSst");

    private final String ruleType;
    private final String subType;

    private SSDRuleTypeSubType(final String ruletype, final String subtype) {
      this.ruleType = ruletype;
      this.subType = subtype;
    }

    /**
     * @return the ruleType
     */
    public String getRuleType() {
      return this.ruleType;
    }

    /**
     * @return the subType
     */
    public String getSubType() {
      return this.subType;
    }

  }

  /**
   * @author SSN9COB
   */
  public enum TransactionState {
                                /**
                                 * Begin of Transaction
                                 */
                                TRANSACTION_BEGIN,
                                /**
                                 * Commit Transaction
                                 */
                                TRANSACTION_COMMIT,
                                /**
                                 * Transaction Rollback
                                 */
                                TRANSACTION_ROLLBACK
  }

}
