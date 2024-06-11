package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the T_DA_DATA_ASSESSMENT database table.
 */
@Entity
@Table(name = "T_DA_DATA_ASSESSMENT")
@NamedQueries(value = {
    @NamedQuery(name = "TDaDataAssessment.findAll", query = "SELECT t FROM TDaDataAssessment t"),
    @NamedQuery(name = TDaDataAssessment.GET_BASELINES_FOR_PIDC_A2L_ID, query = "SELECT t FROM TDaDataAssessment t where t.pidcA2lId =:pidcA2lId") })
public class TDaDataAssessment implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_BASELINES_FOR_PIDC_A2L_ID = "TDaDataAssessment.getBaselinesForPidcA2lId";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DATA_ASSESSMENT_ID")
  private long dataAssessmentId;

  @Column(name = "A2L_FILENAME")
  private String a2lFilename;

  @Column(name = "HEX_FILE_NAME")
  private String hexFileName;

  @Column(name = "BASELINE_NAME")
  private String baselineName;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  private String description;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PIDC_A2L_ID")
  private BigDecimal pidcA2lId;

  @Column(name = "PIDC_VERS_FULLNAME")
  private String pidcVersFullname;

  @Column(name = "PIDC_VERS_ID")
  private BigDecimal pidcVersId;

  @Column(name = "TYPE_OF_ASSIGNMENT")
  private String typeOfAssignment;

  @Column(name = "VARIANT_ID")
  private BigDecimal variantId;

  @Column(name = "VARIANT_NAME")
  private String variantName;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "WP_DEFN_VERS_ID")
  private BigDecimal wpDefnVersId;

  @Column(name = "WP_DEFN_VERS_NAME")
  private String wpDefnVersName;

  @Column(name = "VCDM_DST_SOURCE")
  private String vcdmDstSource;

  @Column(name = "VCDM_DST_VERS_ID")
  private BigDecimal vcdmDstVersId;

  @Column(name = "FILE_ARCHIVAL_STATUS")
  private String fileArchivalStatus;

  @Column(name = "PREVIOUS_PIDC_VERS_CONSIDERED")
  private String previousPidcVersConsidered;

  // bi-directional many-to-one association to TDaFile
  @OneToMany(mappedBy = "TDaDataAssessment")
  private List<TDaFile> TDaFiles;

  // bi-directional many-to-one association to TDaWpResp
  @OneToMany(mappedBy = "TDaDataAssessment")
  private List<TDaWpResp> TDaWpResps;

  @Column(name = "COMPLI_PARAM_IN_A2L")
  private Integer compliParamInA2L;

  @Column(name = "COMPLI_PARAM_PASSED")
  private Integer compliParamPassed;

  @Column(name = "COMPLI_PARAM_CSSD_FAIL")
  private Integer compliParamCSSDFail;

  @Column(name = "COMPLI_PARAM_NO_RULE_FAIL")
  private Integer compliParamNoRuleFail;

  @Column(name = "COMPLI_PARAM_SSD2RV_FAIL")
  private Integer compliParamSSD2RVFail;

  @Column(name = "QSSD_PARAM_FAIL")
  private Integer qssdParamFail;

  public TDaDataAssessment() {}

  public long getDataAssessmentId() {
    return this.dataAssessmentId;
  }

  public void setDataAssessmentId(final long dataAssessmentId) {
    this.dataAssessmentId = dataAssessmentId;
  }

  public String getA2lFilename() {
    return this.a2lFilename;
  }

  public void setA2lFilename(final String a2lFilename) {
    this.a2lFilename = a2lFilename;
  }

  public String getHexFileName() {
    return this.hexFileName;
  }

  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }

  public String getBaselineName() {
    return this.baselineName;
  }

  public void setBaselineName(final String baselineName) {
    this.baselineName = baselineName;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
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

  public BigDecimal getPidcA2lId() {
    return this.pidcA2lId;
  }

  public void setPidcA2lId(final BigDecimal pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  public String getPidcVersFullname() {
    return this.pidcVersFullname;
  }

  public void setPidcVersFullname(final String pidcVersFullname) {
    this.pidcVersFullname = pidcVersFullname;
  }

  public BigDecimal getPidcVersId() {
    return this.pidcVersId;
  }

  public void setPidcVersId(final BigDecimal pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  public String getTypeOfAssignment() {
    return this.typeOfAssignment;
  }

  public void setTypeOfAssignment(final String typeOfAssignment) {
    this.typeOfAssignment = typeOfAssignment;
  }

  public BigDecimal getVariantId() {
    return this.variantId;
  }

  public void setVariantId(final BigDecimal variantId) {
    this.variantId = variantId;
  }

  public String getVariantName() {
    return this.variantName;
  }

  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public BigDecimal getWpDefnVersId() {
    return this.wpDefnVersId;
  }

  public void setWpDefnVersId(final BigDecimal wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }

  public String getWpDefnVersName() {
    return this.wpDefnVersName;
  }

  public void setWpDefnVersName(final String wpDefnVersName) {
    this.wpDefnVersName = wpDefnVersName;
  }

  public List<TDaFile> getTDaFiles() {
    return this.TDaFiles;
  }

  public void setTDaFiles(final List<TDaFile> TDaFiles) {
    this.TDaFiles = TDaFiles;
  }

  public TDaFile addTDaFile(final TDaFile TDaFile) {
    getTDaFiles().add(TDaFile);
    TDaFile.setTDaDataAssessment(this);

    return TDaFile;
  }

  public TDaFile removeTDaFile(final TDaFile TDaFile) {
    getTDaFiles().remove(TDaFile);
    TDaFile.setTDaDataAssessment(null);

    return TDaFile;
  }

  public List<TDaWpResp> getTDaWpResps() {
    return this.TDaWpResps;
  }

  public void setTDaWpResps(final List<TDaWpResp> TDaWpResps) {
    this.TDaWpResps = TDaWpResps;
  }

  public TDaWpResp addTDaWpResp(final TDaWpResp TDaWpResp) {
    getTDaWpResps().add(TDaWpResp);
    TDaWpResp.setTDaDataAssessment(this);

    return TDaWpResp;
  }

  public TDaWpResp removeTDaWpResp(final TDaWpResp TDaWpResp) {
    getTDaWpResps().remove(TDaWpResp);
    TDaWpResp.setTDaDataAssessment(null);

    return TDaWpResp;
  }

  /**
   * @return the vcdmDstSource
   */
  public String getVcdmDstSource() {
    return this.vcdmDstSource;
  }

  /**
   * @param vcdmDstSource the vcdmDstSource to set
   */
  public void setVcdmDstSource(final String vcdmDstSource) {
    this.vcdmDstSource = vcdmDstSource;
  }

  /**
   * @return the vcdmDstVersId
   */
  public BigDecimal getVcdmDstVersId() {
    return this.vcdmDstVersId;
  }

  /**
   * @param vcdmDstVersId the vcdmDstVersId to set
   */
  public void setVcdmDstVersId(final BigDecimal vcdmDstVersId) {
    this.vcdmDstVersId = vcdmDstVersId;
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
   * @return the previous_pidc_vers_considered
   */
  public String getPreviousPidcVersConsidered() {
    return this.previousPidcVersConsidered;
  }


  /**
   * @param previous_pidc_vers_considered the previous_pidc_vers_considered to set
   */
  public void setPreviousPidcVersConsidered(final String previous_pidc_vers_considered) {
    this.previousPidcVersConsidered = previous_pidc_vers_considered;
  }


  /**
   * @return the compliParamInA2L
   */
  public Integer getCompliParamInA2L() {
    return this.compliParamInA2L;
  }


  /**
   * @param compliParamInA2L the compliParamInA2L to set
   */
  public void setCompliParamInA2L(final Integer compliParamInA2L) {
    this.compliParamInA2L = compliParamInA2L;
  }


  /**
   * @return the compliParamPassed
   */
  public Integer getCompliParamPassed() {
    return this.compliParamPassed;
  }


  /**
   * @param compliParamPassed the compliParamPassed to set
   */
  public void setCompliParamPassed(final Integer compliParamPassed) {
    this.compliParamPassed = compliParamPassed;
  }


  /**
   * @return the compliParamCSSDFail
   */
  public Integer getCompliParamCSSDFail() {
    return this.compliParamCSSDFail;
  }


  /**
   * @param compliParamCSSDFail the compliParamCSSDFail to set
   */
  public void setCompliParamCSSDFail(final Integer compliParamCSSDFail) {
    this.compliParamCSSDFail = compliParamCSSDFail;
  }


  /**
   * @return the compliParamNoRuleFail
   */
  public Integer getCompliParamNoRuleFail() {
    return this.compliParamNoRuleFail;
  }


  /**
   * @param compliParamNoRuleFail the compliParamNoRuleFail to set
   */
  public void setCompliParamNoRuleFail(final Integer compliParamNoRuleFail) {
    this.compliParamNoRuleFail = compliParamNoRuleFail;
  }


  /**
   * @return the compliParamSSD2RVFail
   */
  public Integer getCompliParamSSD2RVFail() {
    return this.compliParamSSD2RVFail;
  }


  /**
   * @param compliParamSSD2RVFail the compliParamSSD2RVFail to set
   */
  public void setCompliParamSSD2RVFail(final Integer compliParamSSD2RVFail) {
    this.compliParamSSD2RVFail = compliParamSSD2RVFail;
  }


  /**
   * @return the qssdParamFail
   */
  public Integer getQssdParamFail() {
    return this.qssdParamFail;
  }


  /**
   * @param qssdParamFail the qssdParamFail to set
   */
  public void setQssdParamFail(final Integer qssdParamFail) {
    this.qssdParamFail = qssdParamFail;
  }

}
