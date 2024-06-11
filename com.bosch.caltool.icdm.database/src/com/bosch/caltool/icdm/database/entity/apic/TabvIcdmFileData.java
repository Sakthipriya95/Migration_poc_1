package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the TABV_ICDM_FILE_DATA database table.
 */
@Entity
@Table(name = "TABV_ICDM_FILE_DATA")
@NamedQueries(value = {
    @NamedQuery(name = TabvIcdmFileData.GET_FILE_DATA, query = "select file from TabvIcdmFileData file where file.tabvIcdmFile.fileId = :fileId ") })
public class TabvIcdmFileData implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to fetch attributes.
   */
  public static final String GET_FILE_DATA = "TabvIcdmFileData.GET_FILE_DATA";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FILE_DATA_ID", unique = true, nullable = false)
  private long fileDataId;

  @Lob()
  @Column(name = "FILE_DATA", nullable = false)
  private byte[] fileData;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // Note : Batch fetch is not enabled in this relation because it will fetch the blob field unncessarily
  // bi-directional one-to-one association to TabvIcdmFile
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FILE_ID", referencedColumnName = "FILE_ID", nullable = false, insertable = true)
  private TabvIcdmFile tabvIcdmFile;

  public TabvIcdmFileData() {}

  public long getFileDataId() {
    return this.fileDataId;
  }

  public void setFileDataId(final long fileDataId) {
    this.fileDataId = fileDataId;
  }

  public byte[] getFileData() {
    return this.fileData;
  }

  public void setFileData(final byte[] fileData) {
    this.fileData = fileData;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvIcdmFile getTabvIcdmFile() {
    return this.tabvIcdmFile;
  }

  public void setTabvIcdmFile(final TabvIcdmFile tabvIcdmFile) {
    this.tabvIcdmFile = tabvIcdmFile;
  }

}