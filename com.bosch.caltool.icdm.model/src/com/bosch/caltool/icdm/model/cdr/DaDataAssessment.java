package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * DaDataAssessment Model class
 *
 * @author say8cob
 */
public class DaDataAssessment implements Comparable<DaDataAssessment>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 117938213061562L;
  /**
   * Data Assessment Id
   */
  private Long id;
  /**
   * Baseline Name
   */
  private String baselineName;
  /**
   * Description
   */
  private String description;
  /**
   * Type Of Assignment
   */
  private String typeOfAssignment;
  /**
   * Pidc Vers Id
   */
  private BigDecimal pidcVersId;
  /**
   * Pidc Vers Fullname
   */
  private String pidcVersFullname;
  /**
   * Variant Id
   */
  private BigDecimal variantId;
  /**
   * Variant Name
   */
  private String variantName;
  /**
   * Pidc A2l Id
   */
  private BigDecimal pidcA2lId;
  /**
   * A2l Filename
   */
  private String a2lFilename;

  /**
   * Hex File Name
   */
  private String hexFileName;
  /**
   * Wp Defn Vers Id
   */
  private BigDecimal wpDefnVersId;
  /**
   * Wp Defn Vers Name
   */
  private String wpDefnVersName;
  /**
   * vcdm dst source
   */
  private String vcdmDstSource;
  /**
   * vcdm dst version id
   */
  private BigDecimal vcdmDstVersId;
  /**
   * Creation Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Flag to indicate File archival status
   */
  private String fileArchivalStatus;

  private String previousPidcVersConsidered;
  /**
   * Compli parameters in A2L
   */
  private Integer compliParamInA2L;
  /**
   * Compli parameters passed
   */
  private Integer compliParamPassed;
  /**
   * Compli parameters failed(C-SSD)
   */
  private Integer compliParamCSSDFail;
  /**
   * Compli parameters failed(NO RULE)
   */
  private Integer compliParamNoRuleFail;
  /**
   * Compli parameters failed(SSD2RV)
   */
  private Integer compliParamSSD2RVFail;
  /**
   * Q-SSD parameters failed
   */
  private Integer qssdParamFail;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return baselineName
   */
  public String getBaselineName() {
    return this.baselineName;
  }

  /**
   * @param baselineName set baselineName
   */
  public void setBaselineName(final String baselineName) {
    this.baselineName = baselineName;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description set description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return typeOfAssignment
   */
  public String getTypeOfAssignment() {
    return this.typeOfAssignment;
  }

  /**
   * @param typeOfAssignment set typeOfAssignment
   */
  public void setTypeOfAssignment(final String typeOfAssignment) {
    this.typeOfAssignment = typeOfAssignment;
  }

  /**
   * @return pidcVersId
   */
  public BigDecimal getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId set pidcVersId
   */
  public void setPidcVersId(final BigDecimal pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return pidcVersFullname
   */
  public String getPidcVersFullname() {
    return this.pidcVersFullname;
  }

  /**
   * @param pidcVersFullname set pidcVersFullname
   */
  public void setPidcVersFullname(final String pidcVersFullname) {
    this.pidcVersFullname = pidcVersFullname;
  }

  /**
   * @return variantId
   */
  public BigDecimal getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId set variantId
   */
  public void setVariantId(final BigDecimal variantId) {
    this.variantId = variantId;
  }

  /**
   * @return variantName
   */
  public String getVariantName() {
    return this.variantName;
  }

  /**
   * @param variantName set variantName
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  /**
   * @return pidcA2lId
   */
  public BigDecimal getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId set pidcA2lId
   */
  public void setPidcA2lId(final BigDecimal pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return a2lFilename
   */
  public String getA2lFilename() {
    return this.a2lFilename;
  }

  /**
   * @param a2lFilename set a2lFilename
   */
  public void setA2lFilename(final String a2lFilename) {
    this.a2lFilename = a2lFilename;
  }

  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }

  /**
   * @param hexFileName the hexFileName to set
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }

  /**
   * @return wpDefnVersId
   */
  public BigDecimal getWpDefnVersId() {
    return this.wpDefnVersId;
  }

  /**
   * @param wpDefnVersId set wpDefnVersId
   */
  public void setWpDefnVersId(final BigDecimal wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }

  /**
   * @return wpDefnVersName
   */
  public String getWpDefnVersName() {
    return this.wpDefnVersName;
  }

  /**
   * @param wpDefnVersName set wpDefnVersName
   */
  public void setWpDefnVersName(final String wpDefnVersName) {
    this.wpDefnVersName = wpDefnVersName;
  }

  /**
   * @return creationDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param creationDate set creationDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaDataAssessment object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((DaDataAssessment) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
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
