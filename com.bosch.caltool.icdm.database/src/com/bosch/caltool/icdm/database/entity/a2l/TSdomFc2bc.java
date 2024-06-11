package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_SDOM_FC2BC database table.
 */
@Entity
@Table(name = "T_SDOM_FC2BC")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TSdomFc2bc implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "BC_ID")
  private BigDecimal bcId;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  @Column(name = "FC_ID")
  private BigDecimal fcId;

  @Id
  private Long id;

  public TSdomFc2bc() {}

  public BigDecimal getBcId() {
    return this.bcId;
  }

  public void setBcId(final BigDecimal bcId) {
    this.bcId = bcId;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }

  public BigDecimal getFcId() {
    return this.fcId;
  }

  public void setFcId(final BigDecimal fcId) {
    this.fcId = fcId;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

}