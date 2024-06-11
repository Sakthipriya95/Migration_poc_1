package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_WP_FILES database table.
 */
@Entity
@Table(name = "T_WP_FILES")
public class TWpFiles implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_FILE_ID")
  private long wpFileId;

  @Column(name = "FILE_NAME")
  private String fileName;

  @Lob
  @Column(name = "FILE_DATA")
  private byte[] fileData;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TWpArchival
  @ManyToOne
  @JoinColumn(name = "WP_ARCHIVAL_ID")
  private TWpArchival tWpArchival;


  /**
   * @return the wpFileId
   */
  public long getWpFileId() {
    return this.wpFileId;
  }


  /**
   * @param wpFileId the wpFileId to set
   */
  public void setWpFileId(final long wpFileId) {
    this.wpFileId = wpFileId;
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
   * @return the fileData
   */
  public byte[] getFileData() {
    return this.fileData;
  }


  /**
   * @param fileData the fileData to set
   */
  public void setFileData(final byte[] fileData) {
    this.fileData = fileData;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the tWpArchival
   */
  public TWpArchival getTWpArchival() {
    return this.tWpArchival;
  }


  /**
   * @param tWpArchival the tWpArchival to set
   */
  public void setTWpArchival(final TWpArchival tWpArchival) {
    this.tWpArchival = tWpArchival;
  }


}
