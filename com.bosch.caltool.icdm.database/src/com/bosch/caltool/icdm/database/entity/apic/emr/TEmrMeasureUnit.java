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
 * The persistent class for the T_EMR_MEASURE_UNIT database table.
 */
@Entity
@Table(name = "T_EMR_MEASURE_UNIT")
@NamedQuery(name = "TEmrMeasureUnit.findAll", query = "SELECT t FROM TEmrMeasureUnit t")
public class TEmrMeasureUnit implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrMeasureUnit.findAll";

  /** The mu id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "MU_ID")
  private long muId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The measure unit name. */
  @Column(name = "MEASURE_UNIT_NAME")
  private String measureUnitName;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr excel mappings. */
  // bi-directional many-to-one association to TEmrExcelMapping
  @OneToMany(mappedBy = "TEmrMeasureUnit")
  private Set<TEmrExcelMapping> TEmrExcelMappings;

  /** The T emr file data. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrMeasureUnit")
  private Set<TEmrFileData> TEmrFileData;

  /**
   * Instantiates a new t emr measure unit.
   */
  public TEmrMeasureUnit() {}

  /**
   * Gets the mu id.
   *
   * @return the mu id
   */
  public long getMuId() {
    return this.muId;
  }

  /**
   * Sets the mu id.
   *
   * @param muId the new mu id
   */
  public void setMuId(final long muId) {
    this.muId = muId;
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
   * Gets the measure unit name.
   *
   * @return the measure unit name
   */
  public String getMeasureUnitName() {
    return this.measureUnitName;
  }

  /**
   * Sets the measure unit name.
   *
   * @param measureUnitName the new measure unit name
   */
  public void setMeasureUnitName(final String measureUnitName) {
    this.measureUnitName = measureUnitName;
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
    TEmrExcelMapping.setTEmrMeasureUnit(this);

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
    TEmrExcelMapping.setTEmrMeasureUnit(null);

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
   * Adds the TEmrFileData.
   *
   * @param temrFileData the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData addTEmrFileData(final TEmrFileData temrFileData) {
    getTEmrFileData().add(temrFileData);
    temrFileData.setTEmrMeasureUnit(this);

    return temrFileData;
  }

  /**
   * Removes the TEmrFileData.
   *
   * @param temrFileData the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData removeTEmrFileData(final TEmrFileData temrFileData) {
    getTEmrFileData().remove(temrFileData);
    temrFileData.setTEmrMeasureUnit(null);

    return temrFileData;
  }

}