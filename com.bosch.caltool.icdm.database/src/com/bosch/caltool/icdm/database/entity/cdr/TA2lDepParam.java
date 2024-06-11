package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_A2L_DEP_PARAMS database table.
 */
@Entity
@Table(name = "T_A2L_DEP_PARAMS")
@NamedQueries(value = {
    @NamedQuery(name = TA2lDepParam.NQ_GET_BY_A2L_FILE_ID, query = "SELECT tA2lDepParam FROM TA2lDepParam tA2lDepParam where tA2lDepParam.a2lFileId  =:a2lFileId") })
public class TA2lDepParam implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String NQ_GET_BY_A2L_FILE_ID = "getbya2lfileid";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_DEP_PARAM_ID")
  private long a2lDepParamId;

  @Column(name = "A2L_FILE_ID")
  private Long a2lFileId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "DEPENDS_ON_PARAM_NAME")
  private String dependsOnParamName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAM_NAME")
  private String paramName;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  public TA2lDepParam() {}

  public long getA2lDepParamId() {
    return this.a2lDepParamId;
  }

  public void setA2lDepParamId(final long a2lDepParamId) {
    this.a2lDepParamId = a2lDepParamId;
  }

  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
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

  public String getDependsOnParamName() {
    return this.dependsOnParamName;
  }

  public void setDependsOnParamName(final String dependsOnParamName) {
    this.dependsOnParamName = dependsOnParamName;
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

  public String getParamName() {
    return this.paramName;
  }

  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}