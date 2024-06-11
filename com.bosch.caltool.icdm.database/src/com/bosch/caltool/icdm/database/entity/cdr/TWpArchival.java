/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the T_WP_ARCHIVAL database table.
 */
@Entity
@Table(name = "T_WP_ARCHIVAL")
@NamedQueries(value = {
    @NamedQuery(name = "TWpArchival.findAll", query = "SELECT t FROM TWpArchival t"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_PIDC_A2L_ID, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId = :variantId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID_ONLY, query = "SELECT t FROM TWpArchival t where t.variantId = :variantId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_NO_VARIANT_ID_ONLY, query = "SELECT t FROM TWpArchival t where t.pidcVersId =:pidcVersId AND t.variantId = :variantId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_WP_NAME, query = "SELECT t FROM TWpArchival t where t.variantId = :variantId and t.wpName = :wpName"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_RESP_ID, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId = :variantId and t.respId = :respId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID, query = "SELECT t FROM TWpArchival t where t.variantId = :variantId and t.respId = :respId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID_AND_WP_ID, query = "SELECT t FROM TWpArchival t where t.variantId = :variantId and t.respId = :respId and t.wpName = :wpName"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_WP_ID, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId = :variantId and t.wpId = :wpId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_WP_RESP_ID, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId = :variantId and t.respId = :respId and t.wpId = :wpId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_WP_RESP_NAME, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId = :variantId and t.respId = :respId and t.wpName = :wpName"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_VARIANT_ID_NO_VAR, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.variantId IS NULL"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_RESP_ID_NO_VAR, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.respId = :respId and t.variantId IS NULL"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_WP_ID_NO_VAR, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId  and t.wpId = :wpId and t.variantId IS NULL"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_WP_RESP_ID_NO_VAR, query = "SELECT t FROM TWpArchival t where t.pidcA2lId =:pidcA2lId and t.respId = :respId and t.wpId = :wpId and t.variantId IS NULL"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_PIDC_VERSION_ID, query = "SELECT t FROM TWpArchival t where t.pidcVersId =:pidcVersId"),
    @NamedQuery(name = TWpArchival.GET_BASELINES_FOR_PIDC_VERSION_ID_AND_PIDC_A2L, query = "SELECT t FROM TWpArchival t where t.pidcVersId =:pidcVersId AND t.pidcA2lId =:pidcA2lId"), })
public class TWpArchival implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID
   */
  public static final String GET_BASELINES_FOR_PIDC_A2L_ID = "TWpArchival.getBaselinesForPidcA2lId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Variant Id
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID = "TWpArchival.getBaselinesForVariantId";
  /**
   * Get Wp Archival Baseline Details for given Variant ID filtered by given Variant Id
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID_ONLY = "TWpArchival.getBaselinesForVariantIdOnly";
  /**
   * Get Wp Archival Baseline Details for given Variant ID filtered by given Variant Id
   */
  public static final String GET_BASELINES_FOR_NO_VARIANT_ID_ONLY = "TWpArchival.getBaselinesForNoVariantIdOnly";
  /**
   * Get Wp Archival Baseline Details for given Variant ID filtered by given Variant Id
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID_AND_WP_NAME = "TWpArchival.getBaselinesForVariantIdAndWpName";
  /**
   * Get Wp Archival Baseline Details for given PIDC Version Id
   */
  public static final String GET_BASELINES_FOR_PIDC_VERSION_ID = "TWpArchival.getBaselinesForPidcVersionId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID PIDC Version ID
   */
  public static final String GET_BASELINES_FOR_PIDC_VERSION_ID_AND_PIDC_A2L =
      "TWpArchival.getBaselinesForPidcVersionIdandPidcA2lId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Resp Id
   */
  public static final String GET_BASELINES_FOR_RESP_ID = "TWpArchival.getBaselinesForRespId";
  /**
   * Get Wp Archival Baseline Details for given Variant Id & Res Id
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID = "TWpArchival.getBaselinesForVarIdAndRespId";
  /**
   * Get Wp Archival Baseline Details for given Variant Id & Res Id
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID_AND_RESP_ID_AND_WP_ID =
      "TWpArchival.getBaselinesForVarIdAndRespIdAndWpId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given WP Id
   */
  public static final String GET_BASELINES_FOR_WP_ID = "TWpArchival.getBaselinesForWpId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Resp and WP Id
   */
  public static final String GET_BASELINES_FOR_WP_RESP_ID = "TWpArchival.getBaselinesForWpRespId";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Resp and WP Id
   */
  public static final String GET_BASELINES_FOR_WP_RESP_NAME = "TWpArchival.getBaselinesForWpRespName";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Variant Id - No Variant Case
   */
  public static final String GET_BASELINES_FOR_VARIANT_ID_NO_VAR = "TWpArchival.getBaselinesForVariantIdNoVar";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Resp Id - No Variant Case
   */
  public static final String GET_BASELINES_FOR_RESP_ID_NO_VAR = "TWpArchival.getBaselinesForRespIdNoVar";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given WP Id - No Variant Case
   */
  public static final String GET_BASELINES_FOR_WP_ID_NO_VAR = "TWpArchival.getBaselinesForWpIdNoVar";
  /**
   * Get Wp Archival Baseline Details for given PIDC A2L ID filtered by given Resp and WP Id - No Variant Case
   */
  public static final String GET_BASELINES_FOR_WP_RESP_ID_NO_VAR = "TWpArchival.getBaselinesForWpRespIdNoVar";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_ARCHIVAL_ID")
  private long wpArchivalId;

  @Column(name = "BASELINE_NAME")
  private String baselineName;

  @Column(name = "PIDC_VERS_ID")
  private Long pidcVersId;

  @Column(name = "PIDC_VERS_FULLNAME")
  private String pidcVersFullname;

  @Column(name = "PIDC_A2L_ID")
  private Long pidcA2lId;

  @Column(name = "A2L_FILENAME")
  private String a2lFilename;

  @Column(name = "VARIANT_ID")
  private Long variantId;

  @Column(name = "VARIANT_NAME")
  private String variantName;

  @Column(name = "RESP_ID")
  private Long respId;

  @Column(name = "RESP_NAME")
  private String respName;

  @Column(name = "WP_ID")
  private Long wpId;

  @Column(name = "WP_NAME")
  private String wpName;

  @Column(name = "WP_DEFN_VERS_ID")
  private Long wpDefnVersId;

  @Column(name = "WP_DEFN_VERS_NAME")
  private String wpDefnVersName;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "FILE_ARCHIVAL_STATUS")
  private String fileArchivalStatus;

  // bi-directional many-to-one association to TWpFiles
  @OneToMany(mappedBy = "tWpArchival")
  private List<TWpFiles> tWpFiles;


  /**
   * @return the wpArchivalId
   */
  public long getWpArchivalId() {
    return this.wpArchivalId;
  }


  /**
   * @param wpArchivalId the wpArchivalId to set
   */
  public void setWpArchivalId(final long wpArchivalId) {
    this.wpArchivalId = wpArchivalId;
  }


  /**
   * @return the baselineName
   */
  public String getBaselineName() {
    return this.baselineName;
  }


  /**
   * @param baselineName the baselineName to set
   */
  public void setBaselineName(final String baselineName) {
    this.baselineName = baselineName;
  }


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }


  /**
   * @return the pidcVersFullname
   */
  public String getPidcVersFullname() {
    return this.pidcVersFullname;
  }


  /**
   * @param pidcVersFullname the pidcVersFullname to set
   */
  public void setPidcVersFullname(final String pidcVersFullname) {
    this.pidcVersFullname = pidcVersFullname;
  }


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the a2lFilename
   */
  public String getA2lFilename() {
    return this.a2lFilename;
  }


  /**
   * @param a2lFilename the a2lFilename to set
   */
  public void setA2lFilename(final String a2lFilename) {
    this.a2lFilename = a2lFilename;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }


  /**
   * @return the respId
   */
  public Long getRespId() {
    return this.respId;
  }


  /**
   * @param respId the respId to set
   */
  public void setRespId(final Long respId) {
    this.respId = respId;
  }


  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }


  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
  }


  /**
   * @return the wpId
   */
  public Long getWpId() {
    return this.wpId;
  }


  /**
   * @param wpId the wpId to set
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }


  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }


  /**
   * @param wpDefnVersId the wpDefnVersId to set
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }


  /**
   * @return the wpDefnVersName
   */
  public String getWpDefnVersName() {
    return this.wpDefnVersName;
  }


  /**
   * @param wpDefnVersName the wpDefnVersName to set
   */
  public void setWpDefnVersName(final String wpDefnVersName) {
    this.wpDefnVersName = wpDefnVersName;
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
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the fileArchivalStatus
   */
  public String getFileArchivalStatus() {
    return this.fileArchivalStatus;
  }


  /**
   * @param fileArchivalStatus the fileArchivalStatus to set
   */
  public void setFileArchivalStatus(final String fileArchivalStatus) {
    this.fileArchivalStatus = fileArchivalStatus;
  }


  /**
   * @return the tWpFiles
   */
  public List<TWpFiles> getTWpFiles() {
    return this.tWpFiles;
  }


  /**
   * @param tWpFiles the tWpFiles to set
   */
  public void setTWpFiles(final List<TWpFiles> tWpFiles) {
    this.tWpFiles = tWpFiles;
  }
}
