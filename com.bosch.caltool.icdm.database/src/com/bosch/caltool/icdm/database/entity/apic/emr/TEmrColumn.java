package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the T_EMR_COLUMN database table.
 */
@Entity
@Table(name = "T_EMR_COLUMN")
@NamedQuery(name = "TEmrColumn.findAll", query = "SELECT t FROM TEmrColumn t")
public class TEmrColumn implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrColumn.findAll";

  /** The col id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "COL_ID")
  private long colId;

  /** The column name. */
  @Column(name = "COLUMN_NAME")
  private String columnName;

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

  /** The nomalized flag. */
  @Column(name = "NOMALIZED_FLAG")
  private String nomalizedFlag;

  /** The numeric flag. */
  @Column(name = "NUMERIC_FLAG")
  private String numericFlag;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr column values. */
  // bi-directional many-to-one association to TEmrColumnValue
  @OneToMany(mappedBy = "TEmrColumn")
  private Set<TEmrColumnValue> TEmrColumnValues;

  /** The T emr excel mappings. */
  // bi-directional many-to-one association to TEmrExcelMapping
  @OneToMany(mappedBy = "TEmrColumn")
  private Set<TEmrExcelMapping> TEmrExcelMappings;

  /** The T emr file data. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrColumn")
  private Set<TEmrFileData> TEmrFileData;

  /**
   * Instantiates a new t emr column.
   */
  public TEmrColumn() {}

  /**
   * Gets the col id.
   *
   * @return the col id
   */
  public long getColId() {
    return this.colId;
  }

  /**
   * Sets the col id.
   *
   * @param colId the new col id
   */
  public void setColId(final long colId) {
    this.colId = colId;
  }

  /**
   * Gets the column name.
   *
   * @return the column name
   */
  public String getColumnName() {
    return this.columnName;
  }

  /**
   * Sets the column name.
   *
   * @param columnName the new column name
   */
  public void setColumnName(final String columnName) {
    this.columnName = columnName;
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
   * Gets the nomalized flag.
   *
   * @return the nomalized flag
   */
  public String getNomalizedFlag() {
    return this.nomalizedFlag;
  }

  /**
   * Sets the nomalized flag.
   *
   * @param nomalizedFlag the new nomalized flag
   */
  public void setNomalizedFlag(final String nomalizedFlag) {
    this.nomalizedFlag = nomalizedFlag;
  }

  /**
   * Gets the numeric flag.
   *
   * @return the numeric flag
   */
  public String getNumericFlag() {
    return this.numericFlag;
  }

  /**
   * Sets the numeric flag.
   *
   * @param numericFlag the new numeric flag
   */
  public void setNumericFlag(final String numericFlag) {
    this.numericFlag = numericFlag;
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
   * Gets the t emr column values.
   *
   * @return the t emr column values
   */
  public Set<TEmrColumnValue> getTEmrColumnValues() {
    return this.TEmrColumnValues;
  }

  /**
   * Sets the t emr column values.
   *
   * @param TEmrColumnValues the new t emr column values
   */
  public void setTEmrColumnValues(final Set<TEmrColumnValue> TEmrColumnValues) {
    this.TEmrColumnValues = TEmrColumnValues;
  }

  /**
   * Adds the T emr column value.
   *
   * @param TEmrColumnValue the t emr column value
   * @return the t emr column value
   */
  public TEmrColumnValue addTEmrColumnValue(final TEmrColumnValue TEmrColumnValue) {
    getTEmrColumnValues().add(TEmrColumnValue);
    TEmrColumnValue.setTEmrColumn(this);

    return TEmrColumnValue;
  }

  /**
   * Removes the T emr column value.
   *
   * @param TEmrColumnValue the t emr column value
   * @return the t emr column value
   */
  public TEmrColumnValue removeTEmrColumnValue(final TEmrColumnValue TEmrColumnValue) {
    getTEmrColumnValues().remove(TEmrColumnValue);
    TEmrColumnValue.setTEmrColumn(null);

    return TEmrColumnValue;
  }

  /**
   * Gets the t emr excel mappings.
   *
   * @return the t emr excel mappings
   */
  public Set<TEmrExcelMapping> getTEmrExcelMappings() {
    return this.TEmrExcelMappings;
  }

  /**
   * Sets the t emr excel mappings.
   *
   * @param TEmrExcelMappings the new t emr excel mappings
   */
  public void setTEmrExcelMappings(final Set<TEmrExcelMapping> TEmrExcelMappings) {
    this.TEmrExcelMappings = TEmrExcelMappings;
  }

  /**
   * Adds the T emr excel mapping.
   *
   * @param TEmrExcelMapping the t emr excel mapping
   * @return the t emr excel mapping
   */
  public TEmrExcelMapping addTEmrExcelMapping(final TEmrExcelMapping TEmrExcelMapping) {
    getTEmrExcelMappings().add(TEmrExcelMapping);
    TEmrExcelMapping.setTEmrColumn(this);

    return TEmrExcelMapping;
  }

  /**
   * Removes the T emr excel mapping.
   *
   * @param TEmrExcelMapping the t emr excel mapping
   * @return the t emr excel mapping
   */
  public TEmrExcelMapping removeTEmrExcelMapping(final TEmrExcelMapping TEmrExcelMapping) {
    getTEmrExcelMappings().remove(TEmrExcelMapping);
    TEmrExcelMapping.setTEmrColumn(null);

    return TEmrExcelMapping;
  }

  /**
   * Gets the t emr file data.
   *
   * @return the t emr file data
   */
  public Set<TEmrFileData> getTEmrFileData() {
    return this.TEmrFileData;
  }

  /**
   * Sets the t emr file data.
   *
   * @param TEmrFileData the new t emr file data
   */
  public void setTEmrFileData(final Set<TEmrFileData> TEmrFileData) {
    this.TEmrFileData = TEmrFileData;
  }

  /**
   * Adds the T emr file data.
   *
   * @param temrFileData the t emr file data
   * @return the t emr file data
   */
  public TEmrFileData addTEmrFileData(final TEmrFileData temrFileData) {
    getTEmrFileData().add(temrFileData);
    temrFileData.setTEmrColumn(this);

    return temrFileData;
  }

  /**
   * Removes the T emr file data.
   *
   * @param temrFileData the t emr file data
   * @return the t emr file data
   */
  public TEmrFileData removeTEmrFileData(final TEmrFileData temrFileData) {
    getTEmrFileData().remove(temrFileData);
    temrFileData.setTEmrColumn(null);

    return temrFileData;
  }

}