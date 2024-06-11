package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

/**
 * The persistent class for the T_EMR_FILE_DATA database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_EMR_FILE_DATA")
@NamedQuery(name = "TEmrFileData.findAll", query = "SELECT t FROM TEmrFileData t")
public class TEmrFileData implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrFileData.findAll";

  /** The file data id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FILE_DATA_ID")
  private long fileDataId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The fuel type number. */
  @Column(name = "FUEL_TYPE_NUMBER")
  private Long fuelTypeNumber;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The value num. */
  @Column(name = "VALUE_NUM")
  private BigDecimal valueNum;

  /** The value text. */
  @Column(name = "VALUE_TEXT")
  private String valueText;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr category. */
  // bi-directional many-to-one association to TEmrCategory
  @ManyToOne
  @JoinColumn(name = "CAT_ID")
  private TEmrCategory TEmrCategory;

  /** The T emr column. */
  // bi-directional many-to-one association to TEmrColumn
  @ManyToOne
  @JoinColumn(name = "COL_ID")
  private TEmrColumn TEmrColumn;

  /** The T emr column value. */
  // bi-directional many-to-one association to TEmrColumnValue
  @ManyToOne
  @JoinColumn(name = "COL_VALUE_ID")
  private TEmrColumnValue TEmrColumnValue;

  /** The T emr emission standard 1. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @ManyToOne
  @JoinColumn(name = "EMS_PROCEDURE_ID")
  private TEmrEmissionStandard TEmrEmissionStandardProcedure;

  /** The T emr emission standard 2. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @ManyToOne
  @JoinColumn(name = "EMS_TESTCASE_ID")
  private TEmrEmissionStandard TEmrEmissionStandardTestcase;

  /** The T emr file. */
  // bi-directional many-to-one association to TEmrFile
  @ManyToOne
  @JoinColumn(name = "EMR_FILE_ID")
  private TEmrFile TEmrFile;

  /** The T emr measure unit. */
  // bi-directional many-to-one association to TEmrMeasureUnit
  @ManyToOne
  @JoinColumn(name = "MU_ID")
  private TEmrMeasureUnit TEmrMeasureUnit;

  /**
   * Instantiates a new t emr file data.
   */
  public TEmrFileData() {}

  /**
   * Gets the file data id.
   *
   * @return the file data id
   */
  public long getFileDataId() {
    return this.fileDataId;
  }

  /**
   * Sets the file data id.
   *
   * @param fileDataId the new file data id
   */
  public void setFileDataId(final long fileDataId) {
    this.fileDataId = fileDataId;
  }

  /**
   * Gets the created date.
   *
   * @return the created date
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * Sets the created date.
   *
   * @param createdDate the new created date
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * Gets the created user.
   *
   * @return the created user
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * Sets the created user.
   *
   * @param createdUser the new created user
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * Gets the fuel type number.
   *
   * @return the fuel type number
   */
  public Long getFuelTypeNumber() {
    return this.fuelTypeNumber;
  }

  /**
   * Sets the fuel type number.
   *
   * @param fuelTypeNumber the new fuel type number
   */
  public void setFuelTypeNumber(final Long fuelTypeNumber) {
    this.fuelTypeNumber = fuelTypeNumber;
  }

  /**
   * Gets the modified date.
   *
   * @return the modified date
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * Sets the modified date.
   *
   * @param modifiedDate the new modified date
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * Gets the modified user.
   *
   * @return the modified user
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * Sets the modified user.
   *
   * @param modifiedUser the new modified user
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * Gets the value num.
   *
   * @return the value num
   */
  public BigDecimal getValueNum() {
    return this.valueNum;
  }

  /**
   * Sets the value num.
   *
   * @param valueNum the new value num
   */
  public void setValueNum(final BigDecimal valueNum) {
    this.valueNum = valueNum;
  }

  /**
   * Gets the value text.
   *
   * @return the value text
   */
  public String getValueText() {
    return this.valueText;
  }

  /**
   * Sets the value text.
   *
   * @param valueText the new value text
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * Gets the t emr category.
   *
   * @return the t emr category
   */
  public TEmrCategory getTEmrCategory() {
    return this.TEmrCategory;
  }

  /**
   * Sets the t emr category.
   *
   * @param TEmrCategory the new t emr category
   */
  public void setTEmrCategory(final TEmrCategory TEmrCategory) {
    this.TEmrCategory = TEmrCategory;
  }

  /**
   * Gets the t emr column.
   *
   * @return the t emr column
   */
  public TEmrColumn getTEmrColumn() {
    return this.TEmrColumn;
  }

  /**
   * Sets the t emr column.
   *
   * @param TEmrColumn the new t emr column
   */
  public void setTEmrColumn(final TEmrColumn TEmrColumn) {
    this.TEmrColumn = TEmrColumn;
  }

  /**
   * Gets the t emr column value.
   *
   * @return the t emr column value
   */
  public TEmrColumnValue getTEmrColumnValue() {
    return this.TEmrColumnValue;
  }

  /**
   * Sets the t emr column value.
   *
   * @param TEmrColumnValue the new t emr column value
   */
  public void setTEmrColumnValue(final TEmrColumnValue TEmrColumnValue) {
    this.TEmrColumnValue = TEmrColumnValue;
  }

  /**
   * Gets the t emr file.
   *
   * @return the t emr file
   */
  public TEmrFile getTEmrFile() {
    return this.TEmrFile;
  }

  /**
   * Sets the t emr file.
   *
   * @param TEmrFile the new t emr file
   */
  public void setTEmrFile(final TEmrFile TEmrFile) {
    this.TEmrFile = TEmrFile;
  }

  /**
   * Gets the t emr measure unit.
   *
   * @return the t emr measure unit
   */
  public TEmrMeasureUnit getTEmrMeasureUnit() {
    return this.TEmrMeasureUnit;
  }

  /**
   * Sets the t emr measure unit.
   *
   * @param TEmrMeasureUnit the new t emr measure unit
   */
  public void setTEmrMeasureUnit(final TEmrMeasureUnit TEmrMeasureUnit) {
    this.TEmrMeasureUnit = TEmrMeasureUnit;
  }


  /**
   * @return the tEmrEmissionStandardProcedure
   */
  public TEmrEmissionStandard getTEmrEmissionStandardProcedure() {
    return this.TEmrEmissionStandardProcedure;
  }


  /**
   * @param tEmrEmissionStandardProcedure the tEmrEmissionStandardProcedure to set
   */
  public void setTEmrEmissionStandardProcedure(final TEmrEmissionStandard tEmrEmissionStandardProcedure) {
    this.TEmrEmissionStandardProcedure = tEmrEmissionStandardProcedure;
  }


  /**
   * @return the tEmrEmissionStandardTestcase
   */
  public TEmrEmissionStandard getTEmrEmissionStandardTestcase() {
    return this.TEmrEmissionStandardTestcase;
  }


  /**
   * @param tEmrEmissionStandardTestcase the tEmrEmissionStandardTestcase to set
   */
  public void setTEmrEmissionStandardTestcase(final TEmrEmissionStandard tEmrEmissionStandardTestcase) {
    this.TEmrEmissionStandardTestcase = tEmrEmissionStandardTestcase;
  }


}