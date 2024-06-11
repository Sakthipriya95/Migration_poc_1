package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RULESET_PARAM_TYPE database table.
 */
@Entity
@Table(name = "T_RULESET_PARAM_TYPE")
@NamedQuery(name = "TRulesetParamType.findAll", query = "SELECT t FROM TRulesetParamType t")
public class TRulesetParamType implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRulesetParamType.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PARAM_TYPE_ID")
  private long paramTypeId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAM_TYPE")
  private String paramType;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

//bi-directional one-to-many association to TPidcVersCocWp
  @OneToMany(mappedBy = "tRulesetParamType", fetch = FetchType.LAZY)
  private List<TRuleSetParam> tRuleSetParam;


  public TRulesetParamType() {
//  public constructor
  }

  public long getParamTypeId() {
    return this.paramTypeId;
  }

  public void setParamTypeId(final long paramTypeId) {
    this.paramTypeId = paramTypeId;
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

  public String getParamType() {
    return this.paramType;
  }

  public void setParamType(final String paramType) {
    this.paramType = paramType;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}