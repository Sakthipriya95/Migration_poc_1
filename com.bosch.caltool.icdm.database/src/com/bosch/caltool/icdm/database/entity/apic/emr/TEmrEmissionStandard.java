package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
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
 * The persistent class for the T_EMR_EMISSION_STANDARD database table.
 */
@Entity
@Table(name = "T_EMR_EMISSION_STANDARD")
@NamedQuery(name = "TEmrEmissionStandard.findAll", query = "SELECT t FROM TEmrEmissionStandard t")
public class TEmrEmissionStandard implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrEmissionStandard.findAll";

  /** The ems id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "EMS_ID")
  private long emsId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The emission standard flag. */
  @Column(name = "EMISSION_STANDARD_FLAG")
  private String emissionStandardFlag;

  /** The emission standard name. */
  @Column(name = "EMISSION_STANDARD_NAME")
  private String emissionStandardName;

  /** The measures flag. */
  @Column(name = "MEASURES_FLAG")
  private String measuresFlag;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The testcase flag. */
  @Column(name = "TESTCASE_FLAG")
  private String testcaseFlag;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr emission standard. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @ManyToOne
  @JoinColumn(name = "PARENT_EMS_ID")
  private TEmrEmissionStandard TEmrEmissionStandard;

  /** The T emr emission standards. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @OneToMany(mappedBy = "TEmrEmissionStandard")
  private Set<TEmrEmissionStandard> tEmrEmissionStandards;

  /** The T emr excel mappings. */
  // bi-directional many-to-one association to TEmrExcelMapping
  @OneToMany(mappedBy = "TEmrEmissionStandard")
  private Set<TEmrExcelMapping> tEmrExcelMappings;

  /** The T emr file data 1. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrEmissionStandardProcedure")
  private Set<TEmrFileData> tEmrFileData1;

  /** The T emr file data 2. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrEmissionStandardTestcase")
  private Set<TEmrFileData> tEmrFileData2;

  /** The T emr pidc variants. */
  // bi-directional many-to-one association to TEmrPidcVariant
  @OneToMany(mappedBy = "tEmrEmissionStandard")
  private Set<TEmrPidcVariant> tEmrPidcVariants;

  /**
   * Instantiates a new t emr emission standard.
   */
  public TEmrEmissionStandard() {
    // Public constructor
  }

  /**
   * Gets the ems id.
   *
   * @return the ems id
   */
  public long getEmsId() {
    return this.emsId;
  }

  /**
   * Sets the ems id.
   *
   * @param emsId the new ems id
   */
  public void setEmsId(final long emsId) {
    this.emsId = emsId;
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
   * Gets the emission standard flag.
   *
   * @return the emission standard flag
   */
  public String getEmissionStandardFlag() {
    return this.emissionStandardFlag;
  }

  /**
   * Sets the emission standard flag.
   *
   * @param emissionStandardFlag the new emission standard flag
   */
  public void setEmissionStandardFlag(final String emissionStandardFlag) {
    this.emissionStandardFlag = emissionStandardFlag;
  }

  /**
   * Gets the emission standard name.
   *
   * @return the emission standard name
   */
  public String getEmissionStandardName() {
    return this.emissionStandardName;
  }

  /**
   * Sets the emission standard name.
   *
   * @param emissionStandardName the new emission standard name
   */
  public void setEmissionStandardName(final String emissionStandardName) {
    this.emissionStandardName = emissionStandardName;
  }

  /**
   * Gets the measures flag.
   *
   * @return the measures flag
   */
  public String getMeasuresFlag() {
    return this.measuresFlag;
  }

  /**
   * Sets the measures flag.
   *
   * @param measuresFlag the new measures flag
   */
  public void setMeasuresFlag(final String measuresFlag) {
    this.measuresFlag = measuresFlag;
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
   * Gets the testcase flag.
   *
   * @return the testcase flag
   */
  public String getTestcaseFlag() {
    return this.testcaseFlag;
  }

  /**
   * Sets the testcase flag.
   *
   * @param testcaseFlag the new testcase flag
   */
  public void setTestcaseFlag(final String testcaseFlag) {
    this.testcaseFlag = testcaseFlag;
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
   * @param tEmrEmissionStandard the new t emr emission standard
   */
  public void setTEmrEmissionStandard(final TEmrEmissionStandard tEmrEmissionStandard) {
    this.TEmrEmissionStandard = tEmrEmissionStandard;
  }

  /**
   * Gets the t emr emission standards.
   *
   * @return the t emr emission standards
   */
  public Set<TEmrEmissionStandard> getTEmrEmissionStandards() {
    return this.tEmrEmissionStandards;
  }

  /**
   * Sets the t emr emission standards.
   *
   * @param tEmrEmissionStandards the new t emr emission standards
   */
  public void setTEmrEmissionStandards(final Set<TEmrEmissionStandard> tEmrEmissionStandards) {
    this.tEmrEmissionStandards = tEmrEmissionStandards;
  }

  /**
   * Adds the T emr emission standard.
   *
   * @param temrEmissionStandard the t emr emission standard
   * @return the t emr emission standard
   */
  public TEmrEmissionStandard addTEmrEmissionStandard(final TEmrEmissionStandard temrEmissionStandard) {
    getTEmrEmissionStandards().add(temrEmissionStandard);
    temrEmissionStandard.setTEmrEmissionStandard(this);

    return temrEmissionStandard;
  }

  /**
   * Removes the T emr emission standard.
   *
   * @param temrEmissionStandard the t emr emission standard
   * @return the t emr emission standard
   */
  public TEmrEmissionStandard removeTEmrEmissionStandard(final TEmrEmissionStandard temrEmissionStandard) {
    getTEmrEmissionStandards().remove(temrEmissionStandard);
    temrEmissionStandard.setTEmrEmissionStandard(null);

    return temrEmissionStandard;
  }

  /**
   * Gets the t emr excel mappings.
   *
   * @return the t emr excel mappings
   */
  public Set<TEmrExcelMapping> getTEmrExcelMappings() {
    return this.tEmrExcelMappings;
  }

  /**
   * Sets the t emr excel mappings.
   *
   * @param tEmrExcelMappings the new t emr excel mappings
   */
  public void setTEmrExcelMappings(final Set<TEmrExcelMapping> tEmrExcelMappings) {
    this.tEmrExcelMappings = tEmrExcelMappings;
  }

  /**
   * Adds the T emr excel mapping.
   *
   * @param tEmrExcelMapping the t emr excel mapping
   * @return the t emr excel mapping
   */
  public TEmrExcelMapping addTEmrExcelMapping(final TEmrExcelMapping tEmrExcelMapping) {
    getTEmrExcelMappings().add(tEmrExcelMapping);
    tEmrExcelMapping.setTEmrEmissionStandard(this);

    return tEmrExcelMapping;
  }

  /**
   * Removes the T emr excel mapping.
   *
   * @param tEmrExcelMapping the t emr excel mapping
   * @return the t emr excel mapping
   */
  public TEmrExcelMapping removeTEmrExcelMapping(final TEmrExcelMapping tEmrExcelMapping) {
    getTEmrExcelMappings().remove(tEmrExcelMapping);
    tEmrExcelMapping.setTEmrEmissionStandard(null);

    return tEmrExcelMapping;
  }

  /**
   * Gets the t emr file data 1.
   *
   * @return the t emr file data 1
   */
  public Set<TEmrFileData> getTEmrFileData1() {
    return this.tEmrFileData1;
  }

  /**
   * Sets the t emr file data 1.
   *
   * @param tEmrFileData1 the new t emr file data 1
   */
  public void setTEmrFileData1(final Set<TEmrFileData> tEmrFileData1) {
    this.tEmrFileData1 = tEmrFileData1;
  }

  /**
   * Adds the Temrfiledata1.
   *
   * @param temrFileData1 the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData addTEmrFileData1(final TEmrFileData temrFileData1) {
    getTEmrFileData1().add(temrFileData1);
    temrFileData1.setTEmrEmissionStandardProcedure(this);

    return temrFileData1;
  }

  /**
   * Removes the Temrfiledata1.
   *
   * @param temrFileData1 the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData removeTEmrFileData1(final TEmrFileData temrFileData1) {
    getTEmrFileData1().remove(temrFileData1);
    temrFileData1.setTEmrEmissionStandardProcedure(null);

    return temrFileData1;
  }

  /**
   * Gets the t emr file data 2.
   *
   * @return the t emr file data 2
   */
  public Set<TEmrFileData> getTEmrFileData2() {
    return this.tEmrFileData2;
  }

  /**
   * Sets the t emr file data 2.
   *
   * @param tEmrFileData2 the new t emr file data 2
   */
  public void setTEmrFileData2(final Set<TEmrFileData> tEmrFileData2) {
    this.tEmrFileData2 = tEmrFileData2;
  }

  /**
   * Adds the TEmrFileData 2.
   *
   * @param temrFileData2 the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData addTEmrFileData2(final TEmrFileData temrFileData2) {
    getTEmrFileData2().add(temrFileData2);
    temrFileData2.setTEmrEmissionStandardTestcase(this);

    return temrFileData2;
  }

  /**
   * Removes the TEmrFileData 2.
   *
   * @param temrFileData2 the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData removeTEmrFileData2(final TEmrFileData temrFileData2) {
    getTEmrFileData2().remove(temrFileData2);
    temrFileData2.setTEmrEmissionStandardTestcase(null);

    return temrFileData2;
  }

  /**
   * Gets the t emr pidc variants.
   *
   * @return the t emr pidc variants
   */
  public Set<TEmrPidcVariant> getTEmrPidcVariants() {
    return this.tEmrPidcVariants;
  }

  /**
   * Sets the t emr pidc variants.
   *
   * @param tEmrPidcVariants the new t emr pidc variants
   */
  public void setTEmrPidcVariants(final Set<TEmrPidcVariant> tEmrPidcVariants) {
    this.tEmrPidcVariants = tEmrPidcVariants;
  }

  /**
   * Adds the T emr pidc variant.
   *
   * @param tEmrPidcVariant the t emr pidc variant
   * @return the t emr pidc variant
   */
  public TEmrPidcVariant addTEmrPidcVariant(final TEmrPidcVariant tEmrPidcVariant) {
    if (getTEmrPidcVariants() == null) {
      this.tEmrPidcVariants = new HashSet<>();
    }
    getTEmrPidcVariants().add(tEmrPidcVariant);
    tEmrPidcVariant.setTEmrEmissionStandard(this);

    return tEmrPidcVariant;
  }

  /**
   * Removes the T emr pidc variant.
   *
   * @param tEmrPidcVariant the t emr pidc variant
   * @return the t emr pidc variant
   */
  public TEmrPidcVariant removeTEmrPidcVariant(final TEmrPidcVariant tEmrPidcVariant) {
    getTEmrPidcVariants().remove(tEmrPidcVariant);
    tEmrPidcVariant.setTEmrEmissionStandard(null);

    return tEmrPidcVariant;
  }

}