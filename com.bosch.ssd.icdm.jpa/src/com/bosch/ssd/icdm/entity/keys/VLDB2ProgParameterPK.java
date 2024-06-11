package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;

/**
 * Primary Key for VLDB2ProgParameter
 * 
 * @author SSN9COB
 */
public class VLDB2ProgParameterPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * Typ
   */
  private String typ;
  /**
   * Username
   */
  private String userName;

  /**
   * @return typ
   */
  public String getTyp() {
    return this.typ;
  }

  /**
   * @param typ type
   */
  public void setTyp(final String typ) {
    this.typ = typ;
  }

  /**
   * @return name
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName name
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.typ == null) ? 0 : this.typ.hashCode());
    result = (prime * result) + ((this.userName == null) ? 0 : this.userName.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    // if equall, true
    if (this == obj) {
      return true;
    }
    // if null, false
    if (obj == null) {
      return false;
    }
    // if different class, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLDB2ProgParameterPK other = (VLDB2ProgParameterPK) obj;
    // if typ null, false
    if (this.typ == null) {
      if (other.typ != null) {
        return false;
      }
    }
    // if type unequal, false
    else if (!this.typ.equals(other.typ)) {
      return false;
    }
    // if username null, false
    if (this.userName == null) {
      if (other.userName != null) {
        return false;
      }
    }
    // if username unequal, false
    else if (!this.userName.equals(other.userName)) {
      return false;
    }
    return true;
  }


}
