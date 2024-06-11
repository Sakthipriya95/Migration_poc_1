/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * @author SMN6KOR
 *
 */
public class TempNonSDOMNodeListPK  implements Serializable {
  /**
  *
  */
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "PRIMARY_ID")
  private String primaryId;


  @Id
  @Column(name = "SEQ_NO")
  private String seqNo;


  
  /**
   * @return the primaryId
   */
  public String getPrimaryId() {
    return primaryId;
  }


  
  /**
   * @param primaryId the primaryId to set
   */
  public void setPrimaryId(String primaryId) {
    this.primaryId = primaryId;
  }


  
  /**
   * @return the seqNo
   */
  public String getSeqNo() {
    return seqNo;
  }


  
  /**
   * @param seqNo the seqNo to set
   */
  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  } 
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.primaryId == null) ? 0 : this.primaryId.hashCode());
    result = (prime * result) + ((this.seqNo == null) ? 0 : this.seqNo.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    // if equal, true
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
    TempNonSDOMNodeListPK other = (TempNonSDOMNodeListPK) obj;
    // if id null, false
    if (this.primaryId == null) {
      if (other.primaryId != null) {
        return false;
      }
    }
    // i id unequal, false
    else if (!this.primaryId.equals(other.primaryId)) {
      return false;
    }
    // if id null, false
    if (this.seqNo == null) {
      if (other.seqNo != null) {
        return false;
      }
    }
    // if id unequal, false
    else if (!this.seqNo.equals(other.seqNo)) {
      return false;
    }
    return true;
  }
}
