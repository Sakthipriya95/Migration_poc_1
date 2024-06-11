/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;


/**
 * @author bne4cob
 */
public enum CompPkgType {
  /**
   * Normal type
   */
  NORMAL("N"),
  /**
   * Component packages for NE departments
   */
  NE("NE");

  /**
   * Type literal
   */
  private final String literal;

  /**
   * Enum constructor
   * 
   * @param literal
   */
  CompPkgType(final String literal) {
    this.literal = literal;
  }

  /**
   * @return literal of this type
   */
  public String getLiteral() {
    return this.literal;
  }

  /**
   * Resolve the type enumeration from the database value string
   * 
   * @param literal type string
   * @return type enum
   */
  public static CompPkgType getType(final String literal) {
    for (CompPkgType type : CompPkgType.values()) {
      if (type.literal.equals(literal)) {
        return type;
      }
    }
    return CompPkgType.NORMAL;
  }

}
