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
 * The persistent class for the T_RULESET_HW_COMPONENT database table.
 */
@Entity
@Table(name = "T_RULESET_HW_COMPONENT")
@NamedQuery(name = "TRulesetHwComponent.findAll", query = "SELECT t FROM TRulesetHwComponent t")
public class TRulesetHwComponent implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRulesetHwComponent.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "HW_COMPONENT_ID")
  private long hwComponentId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "HW_COMPONENT")
  private String hwComponent;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

//bi-directional one-to-many association to TPidcVersCocWp
  @OneToMany(mappedBy = "tRulesetHwComponent", fetch = FetchType.LAZY)
  private List<TRuleSetParam> tRuleSetParam;

  public TRulesetHwComponent() {}

  public long getHwComponentId() {
    return this.hwComponentId;
  }

  public void setHwComponentId(final long hwComponentId) {
    this.hwComponentId = hwComponentId;
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

  public String getHwComponent() {
    return this.hwComponent;
  }

  public void setHwComponent(final String hwComponent) {
    this.hwComponent = hwComponent;
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

}