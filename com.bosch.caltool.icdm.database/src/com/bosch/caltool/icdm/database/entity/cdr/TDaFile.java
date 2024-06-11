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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_DA_FILES database table.
 */
@Entity
@Table(name = "T_DA_FILES")
@NamedQuery(name = "TDaFile.findAll", query = "SELECT t FROM TDaFile t")
public class TDaFile implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DA_FILE_ID")
  private long daFileId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Lob
  @Column(name = "FILE_DATA")
  private byte[] fileData;

  @Column(name = "FILE_NAME")
  private String fileName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TDaDataAssessment
  @ManyToOne
  @JoinColumn(name = "DATA_ASSESSMENT_ID")
  private TDaDataAssessment TDaDataAssessment;

  public TDaFile() {}

  public long getDaFileId() {
    return this.daFileId;
  }

  public void setDaFileId(final long daFileId) {
    this.daFileId = daFileId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public byte[] getFileData() {
    return this.fileData;
  }

  public void setFileData(final byte[] fileData) {
    this.fileData = fileData;
  }


  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TDaDataAssessment getTDaDataAssessment() {
    return this.TDaDataAssessment;
  }

  public void setTDaDataAssessment(final TDaDataAssessment TDaDataAssessment) {
    this.TDaDataAssessment = TDaDataAssessment;
  }

}
