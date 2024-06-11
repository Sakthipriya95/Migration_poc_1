/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

/**
 * Responsibility types of work packages
 *
 * @author gge6cob
 */
public enum WpRespType {
                        /**
                         * Robert Bosch
                         */
                        RB("R", "Robert Bosch", "RB"),
                        /**
                         * Customer
                         */
                        CUSTOMER("C", "Customer", "CUST"),
                        /**
                         * Others
                         */
                        OTHERS("O", "Others", "OTHER");

  /**
   * display name
   */
  private final String dispName;


  /**
   * Resp type code
   */
  private final String code;

  /**
   * Base name of alias name. Also used as the alias name for 'default' responsibility
   */
  private final String aliasBase;


  WpRespType(final String code, final String dispName, final String aliasBase) {
    this.code = code;
    this.dispName = dispName;
    this.aliasBase = aliasBase;
  }

  /**
   * @return the Resp type code
   */
  public String getCode() {
    return this.code;
  }

  /**
   * @return the display Name
   */
  public String getDispName() {
    return this.dispName;
  }

  /**
   * @return the aliasBase
   */
  public String getAliasBase() {
    return this.aliasBase;
  }

  /**
   * Return the type object for the given type code
   *
   * @param dispName display name
   * @return the user type object
   */
  public static WpRespType getTypeFromUI(final String dispName) {
    for (WpRespType type : WpRespType.values()) {
      if (type.dispName.equals(dispName)) {
        return type;
      }
    }
    return null;
  }

  /**
   * Return the type object for the given type code
   *
   * @param code code literal of type
   * @return the user type object
   */
  public static WpRespType getType(final String code) {
    for (WpRespType type : WpRespType.values()) {
      if (type.code.equals(code)) {
        return type;
      }
    }
    return null;
  }
}