package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
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

/**
 * The persistent class for the T_EMR_EXCEL_MAPPING database table.
 */
@Entity
@Table(name = "T_EMR_EXCEL_MAPPING")
@NamedQuery(name = "TEmrExcelMapping.findAll", query = "SELECT t FROM TEmrExcelMapping t")
public class TEmrExcelMapping implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrExcelMapping.findAll";

  /** The excel mapping id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "EXCEL_MAPPING_ID")
  private long excelMappingId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The value in excel. */
  @Column(name = "VALUE_IN_EXCEL")
  private String valueInExcel;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

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

  /** The T emr emission standard. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @ManyToOne
  @JoinColumn(name = "EMS_ID")
  private TEmrEmissionStandard TEmrEmissionStandard;

  /** The T emr measure unit. */
  // bi-directional many-to-one association to TEmrMeasureUnit
  @ManyToOne
  @JoinColumn(name = "MU_ID")
  private TEmrMeasureUnit TEmrMeasureUnit;

  /**
   * Instantiates a new t emr excel mapping.
   */
  public TEmrExcelMapping() {}

  /**
   * Gets the excel mapping id.
   *
   * @return the excel mapping id
   */
  public long getExcelMappingId() {
    return this.excelMappingId;
  }

  /**
   * Sets the excel mapping id.
   *
   * @param excelMappingId the new excel mapping id
   */
  public void setExcelMappingId(final long excelMappingId) {
    this.excelMappingId = excelMappingId;
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
   * Gets the value in excel.
   *
   * @return the value in excel
   */
  public String getValueInExcel() {
    return this.valueInExcel;
  }

  /**
   * Sets the value in excel.
   *
   * @param valueInExcel the new value in excel
   */
  public void setValueInExcel(final String valueInExcel) {
    this.valueInExcel = valueInExcel;
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
   * Gets the t emr emission standard.
   *
   * @return the t emr emission standard
   */
  public TEmrEmissionStandard getTEmrEmissionStandard() {
    return this.TEmrEmissionStandard;
  }

  /**
   * Sets the t emr emission standard.
   *
   * @param TEmrEmissionStandard the new t emr emission standard
   */
  public void setTEmrEmissionStandard(final TEmrEmissionStandard TEmrEmissionStandard) {
    this.TEmrEmissionStandard = TEmrEmissionStandard;
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

}