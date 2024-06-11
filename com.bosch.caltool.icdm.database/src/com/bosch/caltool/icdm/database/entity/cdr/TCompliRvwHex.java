package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_COMPLI_RVW_HEX database table.
 */
@Entity
@Table(name = "T_COMPLI_RVW_HEX")
@NamedQuery(name = "TCompliRvwHex.findAll", query = "SELECT t FROM TCompliRvwHex t")
public class TCompliRvwHex implements Serializable {

  private static final long serialVersionUID = 1L;
  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TCompliRvwHex.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "COMPLI_RVW_HEX_ID")
  private long compliRvwHexId;

  @Column(name = "COMPLI_NO_RULE")
  private Long compliNoRule;

  @Column(name = "CSSD_FAIL")
  private Long cssdFail;

  @Column(name = "SSD2RV_FAIL")
  private Long ssd2rvFail;

  @Column(name = "COMPLI_OK")
  private Long resultOk;

  @Column(name = "QSSD_NO_RULE")
  private Long qssdNoRule;

  @Column(name = "QSSD_FAIL")
  private Long qssdFail;

  @Column(name = "QSSD_OK")
  private Long qssdOk;

  @Column(name = "HEX_FILE_NAME")
  private String hexFileName;

  @Column(name = "INDEX_NUM")
  private Long indexNum;

  @Column(name = "VARIANT_ID")
  private Long variantId;

  @Column(name = "VCDM_DST_ID")
  private Long vcdmDstId;

  @Column(name = "SSD_FILE_NAME")
  private String ssdFileName;

  @Column(name = "CHECK_SSD_REPORT_NAME")
  private String checkSsdReportName;

  @Column(name = "RELEASE_ID")
  private Long releaseId;

  @Column(name = "HEX_CHECKSUM")
  private String hexChecksum;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;
  // bi-directional many-to-one association to TCompliRvwA2l
  @ManyToOne
  @JoinColumn(name = "RESULT_ID")
  private TCompliRvwA2l TCompliRvwA2l;

  // bi-directional many-to-one association to TCompliRvwHexsParam
  @OneToMany(mappedBy = "TCompliRvwHex")
  private Set<TCompliRvwHexParam> tCompliRvwHexsParam;

  public TCompliRvwHex() {}

  public Long getCompliRvwHexId() {
    return this.compliRvwHexId;
  }

  public void setCompliRvwHexId(final long compliRvwHexId) {
    this.compliRvwHexId = compliRvwHexId;
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

  public Long getCssdFail() {
    return this.cssdFail;
  }

  public void setCssdFail(final Long cssdFail) {
    this.cssdFail = cssdFail;
  }


  /**
   * @return the compliNoRule
   */
  public Long getCompliNoRule() {
    return this.compliNoRule;
  }


  /**
   * @param compliNoRule the compliNoRule to set
   */
  public void setCompliNoRule(final Long compliNoRule) {
    this.compliNoRule = compliNoRule;
  }

  public String getHexFileName() {
    return this.hexFileName;
  }

  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }

  public Long getIndexNum() {
    return this.indexNum;
  }

  public void setIndexNum(final Long indexNum) {
    this.indexNum = indexNum;
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

  public Long getSsd2rvFail() {
    return this.ssd2rvFail;
  }

  public void setSsd2rvFail(final Long ssd2rvFail) {
    this.ssd2rvFail = ssd2rvFail;
  }

  public Long getVariantVersionId() {
    return this.variantId;
  }

  public void setVariantVersionId(final Long variantVersionId) {
    this.variantId = variantVersionId;
  }

  public Long getVcdmDstId() {
    return this.vcdmDstId;
  }

  public void setVcdmDstId(final Long vcdmDstId) {
    this.vcdmDstId = vcdmDstId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TCompliRvwA2l getTCompliRvwA2l() {
    return this.TCompliRvwA2l;
  }

  public void setTCompliRvwA2l(final TCompliRvwA2l TCompliRvwA2l) {
    this.TCompliRvwA2l = TCompliRvwA2l;
  }


  /**
   * @return the resultOk
   */
  public Long getResultOk() {
    return this.resultOk;
  }


  /**
   * @param resultOk the resultOk to set
   */
  public void setResultOk(final Long resultOk) {
    this.resultOk = resultOk;
  }


  /**
   * @return the ssdFileName
   */
  public String getSsdFileName() {
    return this.ssdFileName;
  }


  /**
   * @param ssdFileName the ssdFileName to set
   */
  public void setSsdFileName(final String ssdFileName) {
    this.ssdFileName = ssdFileName;
  }


  /**
   * @return the checkSsdReportName
   */
  public String getCheckSsdReportName() {
    return this.checkSsdReportName;
  }


  /**
   * @param checkSsdReportName the checkSsdReportName to set
   */
  public void setCheckSsdReportName(final String checkSsdReportName) {
    this.checkSsdReportName = checkSsdReportName;
  }


  /**
   * @return the releaseId
   */
  public Long getReleaseId() {
    return this.releaseId;
  }


  /**
   * @param releaseId the releaseId to set
   */
  public void setReleaseId(final Long releaseId) {
    this.releaseId = releaseId;
  }


  /**
   * @return the tCompliRvwHexsParam
   */
  public Set<TCompliRvwHexParam> gettCompliRvwHexsParam() {
    return this.tCompliRvwHexsParam;
  }


  /**
   * @param tCompliRvwHexsParam the tCompliRvwHexsParam to set
   */
  public void settCompliRvwHexsParam(final Set<TCompliRvwHexParam> tCompliRvwHexsParam) {
    this.tCompliRvwHexsParam = tCompliRvwHexsParam;
  }


  /**
   * @return the hexChecksum
   */
  public String getHexChecksum() {
    return this.hexChecksum;
  }


  /**
   * @param hexChecksum the hexChecksum to set
   */
  public void setHexChecksum(final String hexChecksum) {
    this.hexChecksum = hexChecksum;
  }


  /**
   * @return the qssdFail
   */
  public Long getQssdFail() {
    return this.qssdFail;
  }


  /**
   * @param qssdFail the qssdFail to set
   */
  public void setQssdFail(final Long qssdFail) {
    this.qssdFail = qssdFail;
  }


  /**
   * @return the qssdOk
   */
  public Long getQssdOk() {
    return this.qssdOk;
  }


  /**
   * @param qssdOk the qssdOk to set
   */
  public void setQssdOk(final Long qssdOk) {
    this.qssdOk = qssdOk;
  }


  /**
   * @return the qssdNoRule
   */
  public Long getQssdNoRule() {
    return this.qssdNoRule;
  }


  /**
   * @param qssdNoRule the qssdNoRule to set
   */
  public void setQssdNoRule(final Long qssdNoRule) {
    this.qssdNoRule = qssdNoRule;
  }


}