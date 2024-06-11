/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.jpa;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Id;

/**
 * @author vau3cob
 *
 */
public class BCInfoId implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = -1671790754760533619L;

  /** 
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((EL_NAME == null) ? 0 : EL_NAME.hashCode());
    result = prime * result + ((EL_NUMMER == null) ? 0 : EL_NUMMER.hashCode());
    result = prime * result + ((NODE_NAME == null) ? 0 : NODE_NAME.hashCode());
    result = prime * result + ((REVISION == null) ? 0 : REVISION.hashCode());
    result = prime * result + ((VARIANTE == null) ? 0 : VARIANTE.hashCode());
    return result;
  }


  /** 
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BCInfoId other = (BCInfoId) obj;
    if (EL_NAME == null) {
      if (other.EL_NAME != null)
        return false;
    }
    else if (!EL_NAME.equals(other.EL_NAME))
      return false;
    if (EL_NUMMER == null) {
      if (other.EL_NUMMER != null)
        return false;
    }
    else if (!EL_NUMMER.equals(other.EL_NUMMER))
      return false;
    if (NODE_NAME == null) {
      if (other.NODE_NAME != null)
        return false;
    }
    else if (!NODE_NAME.equals(other.NODE_NAME))
      return false;
    if (REVISION == null) {
      if (other.REVISION != null)
        return false;
    }
    else if (!REVISION.equals(other.REVISION))
      return false;
    if (VARIANTE == null) {
      if (other.VARIANTE != null)
        return false;
    }
    else if (!VARIANTE.equals(other.VARIANTE))
      return false;
    return true;
  }


  /**
   * @return the eL_NUMMER
   */
  public BigDecimal getEL_NUMMER() {
    return EL_NUMMER;
  }

  
  /**
   * @param eL_NUMMER the eL_NUMMER to set
   */
  public void setEL_NUMMER(BigDecimal eL_NUMMER) {
    EL_NUMMER = eL_NUMMER;
  }

  
  /**
   * @return the eL_NAME
   */
  public String getEL_NAME() {
    return EL_NAME;
  }

  
  /**
   * @param eL_NAME the eL_NAME to set
   */
  public void setEL_NAME(String eL_NAME) {
    EL_NAME = eL_NAME;
  }

  
  /**
   * @return the vARIANTE
   */
  public String getVARIANTE() {
    return VARIANTE;
  }

  
  /**
   * @param vARIANTE the vARIANTE to set
   */
  public void setVARIANTE(String vARIANTE) {
    VARIANTE = vARIANTE;
  }

  
  /**
   * @return the rEVISION
   */
  public BigDecimal getREVISION() {
    return REVISION;
  }

  
  /**
   * @param rEVISION the rEVISION to set
   */
  public void setREVISION(BigDecimal rEVISION) {
    REVISION = rEVISION;
  }

  
  /**
   * @return the nODE_NAME
   */
  public String getNODE_NAME() {
    return NODE_NAME;
  }

  
  /**
   * @param nODE_NAME the nODE_NAME to set
   */
  public void setNODE_NAME(String nODE_NAME) {
    NODE_NAME = nODE_NAME;
  }

  @Id
  private BigDecimal EL_NUMMER;

  @Id
  private String EL_NAME;

  
  @Id
  private String VARIANTE;

  @Id
  private BigDecimal REVISION;
  
  @Id
  private String NODE_NAME;
}
