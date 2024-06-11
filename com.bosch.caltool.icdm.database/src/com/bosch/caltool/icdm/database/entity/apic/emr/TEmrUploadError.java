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

import org.eclipse.persistence.annotations.OptimisticLocking;

/**
 * The persistent class for the T_EMR_UPLOAD_ERROR database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_EMR_UPLOAD_ERROR")
@NamedQuery(name = "TEmrUploadError.findAll", query = "SELECT t FROM TEmrUploadError t")
public class TEmrUploadError implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrUploadError.findAll";

  /** The error id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "ERROR_ID")
  private long errorId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The error message. */
  @Column(name = "ERROR_MESSAGE")
  private String errorMessage;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The error data. */
  @Column(name = "ERROR_DATA")
  private String errorData;

  /** The error category. */
  @Column(name = "ERROR_CATEGORY")
  private String errorCategory;

  /** The row number. */
  @Column(name = "ROW_NUMBER")
  private Long rowNumber;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The T emr file. */
  // bi-directional many-to-one association to TEmrFile
  @ManyToOne
  @JoinColumn(name = "EMR_FILE_ID")
  private TEmrFile TEmrFile;

  /**
   * Instantiates a new t emr upload error.
   */
  public TEmrUploadError() {}

  /**
   * Gets the error id.
   *
   * @return the error id
   */
  public long getErrorId() {
    return this.errorId;
  }

  /**
   * Sets the error id.
   *
   * @param errorId the new error id
   */
  public void setErrorId(final long errorId) {
    this.errorId = errorId;
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
   * Gets the error message.
   *
   * @return the error message
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * Sets the error message.
   *
   * @param errorMessage the new error message
   */
  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
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
   * Gets the row number.
   *
   * @return the row number
   */
  public Long getRowNumber() {
    return this.rowNumber;
  }

  /**
   * Sets the row number.
   *
   * @param rowNumber the new row number
   */
  public void setRowNumber(final Long rowNumber) {
    this.rowNumber = rowNumber;
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
   * @return the errorData
   */
  public String getErrorData() {
    return this.errorData;
  }


  /**
   * @param errorData the errorData to set
   */
  public void setErrorData(final String errorData) {
    this.errorData = errorData;
  }


  /**
   * @return the errorCategory
   */
  public String getErrorCategory() {
    return this.errorCategory;
  }


  /**
   * @param errorCategory the errorCategory to set
   */
  public void setErrorCategory(final String errorCategory) {
    this.errorCategory = errorCategory;
  }
}