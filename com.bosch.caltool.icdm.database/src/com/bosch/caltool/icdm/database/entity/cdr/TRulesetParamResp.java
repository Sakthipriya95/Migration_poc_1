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
 * The persistent class for the T_RULESET_PARAM_RESP database table.
 */
@Entity
@Table(name = "T_RULESET_PARAM_RESP")
@NamedQuery(name = "TRulesetParamResp.findAll", query = "SELECT t FROM TRulesetParamResp t")
public class TRulesetParamResp implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRulesetParamResp.findAll";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PARAM_RESP_ID")
  private long paramRespId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAM_RESP")
  private String paramResp;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

//bi-directional one-to-many association to TPidcVersCocWp
  @OneToMany(mappedBy = "tRulesetParamResp", fetch = FetchType.LAZY)
  private List<TRuleSetParam> tRuleSetParam;

  public TRulesetParamResp() {}

  public long getParamRespId() {
    return this.paramRespId;
  }

  public void setParamRespId(final long paramRespId) {
    this.paramRespId = paramRespId;
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

  public String getParamResp() {
    return this.paramResp;
  }

  public void setParamResp(final String paramResp) {
    this.paramResp = paramResp;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}