package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;


/**
 * The persistent class for the T_RVW_VARIANTS database table.
 */
@Entity
@Table(name = "T_RVW_VARIANTS")
@NamedQueries(value = {
    @NamedQuery(name = "TRvwVariant.findAll", query = "SELECT t FROM TRvwVariant t"),
    @NamedQuery(name = TRvwVariant.FIND_BY_VERSIONID, query = "select rvwVar from TRvwVariant rvwVar where rvwVar.TRvwResult.TPidcA2l.TPidcVersion.pidcVersId =:pidcverid") })
public class TRvwVariant implements Serializable {

  // Icdm-20184- Multiple Variants for review.
  private static final long serialVersionUID = 1L;
  public static final String FIND_BY_VERSIONID = "TRvwVariant.findbyVersionId";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RVW_VAR_ID", unique = true, nullable = false, precision = 15)
  private long rvwVarId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne
  @JoinColumn(name = "VARIANT_ID")
  private TabvProjectVariant tabvProjectVariant;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne
  @JoinColumn(name = "RESULT_ID")
  private TRvwResult TRvwResult;

  public TRvwVariant() {}

  public long getRvwVarId() {
    return this.rvwVarId;
  }

  public void setRvwVarId(final long rvwVarId) {
    this.rvwVarId = rvwVarId;
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

  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }

}