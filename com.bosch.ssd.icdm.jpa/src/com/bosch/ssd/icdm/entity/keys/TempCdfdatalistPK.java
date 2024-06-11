package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Primary Key for TempCdfdatalist
 *
 * @author SSN9COB
 */
public class TempCdfdatalistPK implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Label
   */
  @Id
  @Column(length = 255)
  private String label;

  /**
   * Unique iD
   */
  @Id
  @Column(name = "UNI_ID", length = 100)
  private String uniId;

  /**
   *
   */
  public TempCdfdatalistPK() {
    super();
  }

  /**
   * @return label
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * @param label label
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * @return unique ID
   */
  public String getUniId() {
    return this.uniId;
  }

  /**
   * @param uniId uniqueID
   */
  public void setUniId(final String uniId) {
    this.uniId = uniId;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.uniId == null) ? 0 : this.uniId.hashCode());
    result = (prime * result) + ((this.label == null) ? 0 : this.label.hashCode());
    return result;
  }


  @Override
  public boolean equals(final Object obj) {
    // if equal, true
    if (this == obj) {
      return true;
    }
    // if null false
    if (obj == null) {
      return false;
    }
    // if different class, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    TempCdfdatalistPK other = (TempCdfdatalistPK) obj;
    // if unique id null, false
    if (this.uniId == null) {
      if (other.uniId != null) {
        return false;
      }
    }
    // if uni id different, false
    else if (!this.uniId.equals(other.uniId)) {
      return false;
    }
    // if label null, false
    if (this.label == null) {
      if (other.label != null) {
        return false;
      }
    }
    // if label not equal, false
    else if (!this.label.equals(other.label)) {
      return false;
    }
    return true;
  }
}
