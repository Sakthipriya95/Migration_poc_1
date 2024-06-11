package com.bosch.caltool.icdm.model.bc;

import java.math.BigDecimal;

import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * SdomFc2bc Model class
 *
 * @author say8cob
 */
public class SdomFc2bc implements Comparable<SdomFc2bc> {

  /**
   * Id
   */
  private Long id;
  /**
   * Bc Id
   */
  private BigDecimal bcId;
  /**
   * Fc Id
   */
  private BigDecimal fcId;
  /**
   * Created Date
   */
  private String createdDate;

  /**
   * {@inheritDoc}
   */
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return bcId
   */
  public BigDecimal getBcId() {
    return this.bcId;
  }

  /**
   * @param bcId set bcId
   */
  public void setBcId(final BigDecimal bcId) {
    this.bcId = bcId;
  }

  /**
   * @return fcId
   */
  public BigDecimal getFcId() {
    return this.fcId;
  }

  /**
   * @param fcId set fcId
   */
  public void setFcId(final BigDecimal fcId) {
    this.fcId = fcId;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SdomFc2bc object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((SdomFc2bc) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


}
