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
 * The persistent class for the T_RULESET_SYS_ELEMENT database table.
 */
@Entity
@Table(name = "T_RULESET_SYS_ELEMENT")
@NamedQuery(name = "TRulesetSysElement.findAll", query = "SELECT t FROM TRulesetSysElement t")
public class TRulesetSysElement implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String GET_ALL = "TRulesetSysElement.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "SYS_ELEMENT_ID")
  private long sysElementId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "SYS_ELEMENT")
  private String sysElement;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

//bi-directional one-to-many association to TPidcVersCocWp
  @OneToMany(mappedBy = "tRulesetSysElement", fetch = FetchType.LAZY)
  private List<TRuleSetParam> tRuleSetParam;


  public TRulesetSysElement() {}

  public long getSysElementId() {
    return this.sysElementId;
  }

  public void setSysElementId(final long sysElementId) {
    this.sysElementId = sysElementId;
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

  public String getSysElement() {
    return this.sysElement;
  }

  public void setSysElement(final String sysElement) {
    this.sysElement = sysElement;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}