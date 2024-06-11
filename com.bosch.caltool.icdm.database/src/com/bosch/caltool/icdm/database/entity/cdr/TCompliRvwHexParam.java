package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the T_COMPLI_RVW_HEX_PARAMS database table.
 */
@Entity
@Table(name = "T_COMPLI_RVW_HEX_PARAMS")
@NamedQuery(name = "TCompliRvwHexParam.findAll", query = "SELECT t FROM TCompliRvwHexParam t")
public class TCompliRvwHexParam implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TCompliRvwHexParam.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "HEX_PARAMS_ID")
  private long hexParamsId;

  @Lob
  @Column(name = "CHECK_VALUE")
  private byte[] checkValue;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "LAB_OBJ_ID")
  private Long labObjId;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "COMPLI_RESULT")
  private String compliResult;

  @Column(name = "QSSD_RESULT")
  private String qssdResult;

  @Column(name = "REV_ID")
  private Long revId;

  @Column(name = "\"VERSION\"")
  private Long version;

  // bi-directional many-to-one association to TCompliRvwHex
  @ManyToOne
  @JoinColumn(name = "COMPLI_RVW_HEX_ID")
  private TCompliRvwHex TCompliRvwHex;

  // bi-directional many-to-one association to TParameter
  @ManyToOne
  @JoinColumn(name = "PARAM_ID")
  private TParameter TParameter;

  public TCompliRvwHexParam() {}

  public long getHexParamsId() {
    return this.hexParamsId;
  }

  public void setHexParamsId(final long hexParamsId) {
    this.hexParamsId = hexParamsId;
  }

  public byte[] getCheckValue() {
    return this.checkValue;
  }

  public void setCheckValue(final byte[] checkValue) {
    this.checkValue = checkValue;
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

  public Long getLabObjId() {
    return this.labObjId;
  }

  public void setLabObjId(final Long labObjId) {
    this.labObjId = labObjId;
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


  /**
   * @return the compliResult
   */
  public String getCompliResult() {
    return this.compliResult;
  }


  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
  }

  public Long getRevId() {
    return this.revId;
  }

  public void setRevId(final Long revId) {
    this.revId = revId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TCompliRvwHex getTCompliRvwHex() {
    return this.TCompliRvwHex;
  }

  public void setTCompliRvwHex(final TCompliRvwHex TCompliRvwHex) {
    this.TCompliRvwHex = TCompliRvwHex;
  }

  public TParameter getTParameter() {
    return this.TParameter;
  }

  public void setTParameter(final TParameter TParameter) {
    this.TParameter = TParameter;
  }


  /**
   * @return the qssdResult
   */
  public String getQssdResult() {
    return this.qssdResult;
  }


  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final String qssdResult) {
    this.qssdResult = qssdResult;
  }


}