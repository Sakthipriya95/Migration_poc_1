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
 * The persistent class for the T_EMR_CATEGORY database table.
 */
@Entity
@Table(name = "T_EMR_CATEGORY")
@NamedQuery(name = "TEmrCategory.findAll", query = "SELECT t FROM TEmrCategory t")
public class TEmrCategory implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrCategory.findAll";

  /** The cat id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CAT_ID")
  private long catId;

  /** The cat name. */
  @Column(name = "CAT_NAME")
  private String catName;

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

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr file data. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrCategory")
  private Set<TEmrFileData> TEmrFileData;

  /**
   * Instantiates a new t emr category.
   */
  public TEmrCategory() {}

  /**
   * Gets the cat id.
   *
   * @return the cat id
   */
  public long getCatId() {
    return this.catId;
  }

  /**
   * Sets the cat id.
   *
   * @param catId the new cat id
   */
  public void setCatId(final long catId) {
    this.catId = catId;
  }

  /**
   * Gets the cat name.
   *
   * @return the cat name
   */
  public String getCatName() {
    return this.catName;
  }

  /**
   * Sets the cat name.
   *
   * @param catName the new cat name
   */
  public void setCatName(final String catName) {
    this.catName = catName;
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
    temrFileData.setTEmrCategory(this);

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
    temrFileData.setTEmrCategory(null);

    return temrFileData;
  }

}