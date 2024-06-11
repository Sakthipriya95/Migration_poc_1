/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.cdr.cdfx;

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
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;

/**
 * The persistent class for the T_CDFX_DELIVERY_PARAM database table.
 */
@Entity
@Table(name = "T_CDFX_DELVRY_PARAM")
public class TCDFxDeliveryParam implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CDFX_DELVRY_PARAM_ID")
  private long cdfxDelParamId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_ID")
  private TParameter param;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RVW_RESULT_ID")
  private TRvwResult rvwResult;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CDFX_DELVRY_WP_RESP_ID")
  private TCDFxDelvryWpResp tCDFxDelvryWpResp;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;


  // Adding the Version Tag
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private long version;


  /**
   * @return the cdfxDelParamId
   */
  public long getCdfxDelParamId() {
    return this.cdfxDelParamId;
  }


  /**
   * @param cdfxDelParamId the cdfxDelParamId to set
   */
  public void setCdfxDelParamId(final long cdfxDelParamId) {
    this.cdfxDelParamId = cdfxDelParamId;
  }


  /**
   * @return the param
   */
  public TParameter getParam() {
    return this.param;
  }


  /**
   * @param param the param to set
   */
  public void setParam(final TParameter param) {
    this.param = param;
  }


  /**
   * @return the rvwResult
   */
  public TRvwResult getRvwResult() {
    return this.rvwResult;
  }


  /**
   * @param rvwResult the rvwResult to set
   */
  public void setRvwResult(final TRvwResult rvwResult) {
    this.rvwResult = rvwResult;
  }


  /**
   * @return the tCDFxDelvryWpResp
   */
  public TCDFxDelvryWpResp gettCDFxDelvryWpResp() {
    return this.tCDFxDelvryWpResp;
  }


  /**
   * @param tCDFxDelvryWpResp the tCDFxDelvryWpResp to set
   */
  public void settCDFxDelvryWpResp(final TCDFxDelvryWpResp tCDFxDelvryWpResp) {
    this.tCDFxDelvryWpResp = tCDFxDelvryWpResp;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }


}
