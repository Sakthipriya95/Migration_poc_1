/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * @author dja7cob
 */
@Entity
@Table(name = "V_EMR_DETAILS_ALL")
@NamedQueries(value = {
    @NamedQuery(name = VEmrDetailsAll.NQ_GET_BY_PIDC_VERS_ID, query = "SELECT v FROM VEmrDetailsAll v where coalesce(v.emrDeletedFlag,'N') ='N' and v.pidcVersId = :" +
        VEmrDetailsAll.QP_PIDC_VERS_ID),
    @NamedQuery(name = VEmrDetailsAll.NQ_GET_BY_PIDC_VAR_ID, query = "SELECT vEmrDetail FROM VEmrDetailsAll vEmrDetail, TEmrPidcVariant emrVar" +
        " WHERE coalesce(vEmrDetail.emrDeletedFlag,'N') ='N' and vEmrDetail.emrFileId = emrVar.tEmrFile.emrFileId AND vEmrDetail.emsId= emrVar.tEmrEmissionStandard.emsId AND emrVar.tabvProjectVariant.variantId = :" +
        VEmrDetailsAll.QP_VARIANT_ID) })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)

public class VEmrDetailsAll implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Query parameter for pidc version Id
   */
  public static final String QP_PIDC_VERS_ID = "pidcVersId";

  /**
   * Query parameter for pidc variant Id
   */
  public static final String QP_VARIANT_ID = "variantId";

  /**
   *
   */
  public static final String NQ_GET_BY_PIDC_VAR_ID = "VEmrDetailsAll.findByPidcVarId";

  /**
   *
   */
  public static final String NQ_GET_BY_PIDC_VERS_ID = "VEmrDetailsAll.findByPidcVersId";

  @Column(name = "CUSTOMER_BRAND")
  private String customerBrand;

  @Column(name = "PIDC_NAME")
  private String pidcName;

  @Column(name = "PIDC_VERS_ID")
  private Long pidcVersId;

  @Column(name = "ECU_USED_FOR")
  private String ecuUsedFor;

  @Column(name = "ECU_USED_IN")
  private String ecuUsedIn;

  @Column(name = "ECU_GENERATION")
  private String ecuGeneration;

  @Column(name = "CAL_PROJECT_ORGA")
  private String catProjOrg;

  @Column(name = "EMR_FILE_ID")
  private Long emrFileId;

  @Column(name = "FILE_NAME")
  private String fileName;

  @Column(name = "CAT_NAME")
  private String catName;

  @Id
  @Column(name = "FILE_DATA_ID")
  private Long fileDataId;

  @Column(name = "COLUMN_NAME")
  private String columnName;

  @Column(name = "NOMALIZED_FLAG")
  private String normalizedFlag;

  @Column(name = "NUMERIC_FLAG")
  private String numericFlag;

  @Column(name = "COL_VALUE")
  private String colValue;

  @Column(name = "VALUE_NUM")
  private BigDecimal valueNum;

  @Column(name = "VALUE_TEXT")
  private String valueText;

  @Column(name = "EMISSION_STANDARD_NAME")
  private String emissionStandardName;

  @Column(name = "MEASURE_UNIT_NAME")
  private String measureUnitName;

  @Column(name = "TESTCASE_NAME")
  private String testcaseName;

  @Column(name = "EMR_DELETED_FLAG")
  private String emrDeletedFlag;

  @Column(name = "IS_TESTCUSTOMER")
  private String isTestCustomer;

  @Column(name = "EMS_ID")
  private Long emsId;

  @Column(name = "TEST_IDENTIFIER")
  private Long testIdentifier;


  /**
   * @return the testIdentifier
   */
  public Long getTestIdentifier() {
    return this.testIdentifier;
  }


  /**
   * @param testIdentifier the testIdentifier to set
   */
  public void setTestIdentifier(final Long testIdentifier) {
    this.testIdentifier = testIdentifier;
  }

  /**
   * @return the emsId
   */
  public Long getEmsId() {
    return this.emsId;
  }

  /**
   * @param emsId the emsId to set
   */
  public void setEmsId(final Long emsId) {
    this.emsId = emsId;
  }


  /**
   * @return the customerBrand
   */
  public String getCustomerBrand() {
    return this.customerBrand;
  }


  /**
   * @param customerBrand the customerBrand to set
   */
  public void setCustomerBrand(final String customerBrand) {
    this.customerBrand = customerBrand;
  }


  /**
   * @return the pidcName
   */
  public String getPidcName() {
    return this.pidcName;
  }


  /**
   * @param pidcName the pidcName to set
   */
  public void setPidcName(final String pidcName) {
    this.pidcName = pidcName;
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
   * @return the ecuUsedFor
   */
  public String getEcuUsedFor() {
    return this.ecuUsedFor;
  }


  /**
   * @param ecuUsedFor the ecuUsedFor to set
   */
  public void setEcuUsedFor(final String ecuUsedFor) {
    this.ecuUsedFor = ecuUsedFor;
  }


  /**
   * @return the ecuUsedIn
   */
  public String getEcuUsedIn() {
    return this.ecuUsedIn;
  }


  /**
   * @param ecuUsedIn the ecuUsedIn to set
   */
  public void setEcuUsedIn(final String ecuUsedIn) {
    this.ecuUsedIn = ecuUsedIn;
  }


  /**
   * @return the ecuGeneration
   */
  public String getEcuGeneration() {
    return this.ecuGeneration;
  }


  /**
   * @param ecuGeneration the ecuGeneration to set
   */
  public void setEcuGeneration(final String ecuGeneration) {
    this.ecuGeneration = ecuGeneration;
  }


  /**
   * @return the catProjOrg
   */
  public String getCatProjOrg() {
    return this.catProjOrg;
  }


  /**
   * @param catProjOrg the catProjOrg to set
   */
  public void setCatProjOrg(final String catProjOrg) {
    this.catProjOrg = catProjOrg;
  }


  /**
   * @return the emrFileId
   */
  public Long getEmrFileId() {
    return this.emrFileId;
  }


  /**
   * @param emrFileId the emrFileId to set
   */
  public void setEmrFileId(final Long emrFileId) {
    this.emrFileId = emrFileId;
  }


  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the catName
   */
  public String getCatName() {
    return this.catName;
  }


  /**
   * @param catName the catName to set
   */
  public void setCatName(final String catName) {
    this.catName = catName;
  }


  /**
   * @return the fileDataId
   */
  public Long getFileDataId() {
    return this.fileDataId;
  }


  /**
   * @param fileDataId the fileDataId to set
   */
  public void setFileDataId(final Long fileDataId) {
    this.fileDataId = fileDataId;
  }


  /**
   * @return the columnName
   */
  public String getColumnName() {
    return this.columnName;
  }


  /**
   * @param columnName the columnName to set
   */
  public void setColumnName(final String columnName) {
    this.columnName = columnName;
  }


  /**
   * @return the normalizedFlag
   */
  public String isNormalizedFlag() {
    return this.normalizedFlag;
  }


  /**
   * @param normalizedFlag the normalizedFlag to set
   */
  public void setNormalizedFlag(final String normalizedFlag) {
    this.normalizedFlag = normalizedFlag;
  }


  /**
   * @return the numericFlag
   */
  public String isNumericFlag() {
    return this.numericFlag;
  }


  /**
   * @param numericFlag the numericFlag to set
   */
  public void setNumericFlag(final String numericFlag) {
    this.numericFlag = numericFlag;
  }


  /**
   * @return the colValue
   */
  public String getColValue() {
    return this.colValue;
  }


  /**
   * @param colValue the colValue to set
   */
  public void setColValue(final String colValue) {
    this.colValue = colValue;
  }


  /**
   * @return the valueNum
   */
  public BigDecimal getValueNum() {
    return this.valueNum;
  }


  /**
   * @param valueNum the valueNum to set
   */
  public void setValueNum(final BigDecimal valueNum) {
    this.valueNum = valueNum;
  }


  /**
   * @return the valueText
   */
  public String getValueText() {
    return this.valueText;
  }


  /**
   * @param valueText the valueText to set
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }


  /**
   * @return the emissionStandardName
   */
  public String getEmissionStandardName() {
    return this.emissionStandardName;
  }


  /**
   * @param emissionStandardName the emissionStandardName to set
   */
  public void setEmissionStandardName(final String emissionStandardName) {
    this.emissionStandardName = emissionStandardName;
  }


  /**
   * @return the measureUnitName
   */
  public String getMeasureUnitName() {
    return this.measureUnitName;
  }


  /**
   * @param measureUnitName the measureUnitName to set
   */
  public void setMeasureUnitName(final String measureUnitName) {
    this.measureUnitName = measureUnitName;
  }


  /**
   * @return the testcaseName
   */
  public String getTestcaseName() {
    return this.testcaseName;
  }


  /**
   * @param testcaseName the testcaseName to set
   */
  public void setTestcaseName(final String testcaseName) {
    this.testcaseName = testcaseName;
  }


  /**
   * @return the emrDeletedFlag
   */
  public String isEmrDeletedFlag() {
    return this.emrDeletedFlag;
  }


  /**
   * @param emrDeletedFlag the emrDeletedFlag to set
   */
  public void setEmrDeletedFlag(final String emrDeletedFlag) {
    this.emrDeletedFlag = emrDeletedFlag;
  }


  /**
   * @return the isTestCustomer
   */
  public String isTestCustomer() {
    return this.isTestCustomer;
  }


  /**
   * @param isTestCustomer the isTestCustomer to set
   */
  public void setTestCustomer(final String isTestCustomer) {
    this.isTestCustomer = isTestCustomer;
  }
}
