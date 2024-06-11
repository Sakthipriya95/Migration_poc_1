package com.bosch.caltool.icdm.database.entity.comppkg;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_COMP_PKG_BC_FC database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_COMP_PKG_BC_FC")
@NamedQueries({@NamedQuery(name = "TCompPkgBcFc.findAll", query = "SELECT t FROM TCompPkgBcFc t"),
  @NamedQuery(name = "TCompPkgBcFc.findByBCId", query = "SELECT t FROM TCompPkgBcFc t where t.TCompPkgBc.compBcId =:compBcId")})
public class TCompPkgBcFc implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_SEQ_GENERATOR")
  @Column(name = "COMP_BC_FC_ID")
  private long compBcFcId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "FC_NAME")
  private String fcName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TCompPkgBc
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "COMP_BC_ID")
  private TCompPkgBc TCompPkgBc;

  public TCompPkgBcFc() {}

  public long getCompBcFcId() {
    return this.compBcFcId;
  }

  public void setCompBcFcId(final long compBcFcId) {
    this.compBcFcId = compBcFcId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getFcName() {
    return this.fcName;
  }

  public void setFcName(final String fcName) {
    this.fcName = fcName;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TCompPkgBc getTCompPkgBc() {
    return this.TCompPkgBc;
  }

  public void setTCompPkgBc(final TCompPkgBc TCompPkgBc) {
    this.TCompPkgBc = TCompPkgBc;
  }

}