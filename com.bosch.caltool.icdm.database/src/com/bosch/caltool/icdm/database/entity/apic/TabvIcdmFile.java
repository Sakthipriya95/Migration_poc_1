package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwA2l;


/**
 * The persistent class for the TABV_ICDM_FILES database table.
 */
@Entity
@Table(name = "TABV_ICDM_FILES")

@NamedQueries(value = {
    @NamedQuery(name = TabvIcdmFile.GET_FILE, query = "select file from TabvIcdmFile file where file.nodeId = :nodeId and file.nodeType = :nodeType") })
public class TabvIcdmFile implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to fetch attributes.
   */
  public static final String GET_FILE = "TabvIcdmFile.GET_FILE";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FILE_ID", unique = true, nullable = false)
  private long fileId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 20)
  private String createdUser;

  @Column(name = "FILE_COUNT", nullable = false, precision = 3)
  private long fileCount;

  @Column(name = "FILE_NAME", nullable = false, length = 200)
  private String fileName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 20)
  private String modifiedUser;

  @Column(name = "NODE_ID", nullable = false)
  private long nodeId;

  @Column(name = "NODE_TYPE", nullable = false, length = 15)
  private String nodeType;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // Note : Batch fetch is not enabled in this relation because it will fetch the blob field unncessarily
  // bi-directional one-to-one association to TabvIcdmFileData
  @OneToOne(mappedBy = "tabvIcdmFile", fetch = FetchType.LAZY)
  private TabvIcdmFileData tabvIcdmFileData;

  // bi-directional many-to-one association to TEmrFile
  @PrivateOwned
  @OneToMany(mappedBy = "tabvIcdmFile", fetch = FetchType.LAZY)
  private Set<TEmrFile> TEmrFiles;

  // bi-directional many-to-one association to TCompliRvwA2l
  @OneToMany(mappedBy = "tabvIcdmFile")
  private List<TCompliRvwA2l> TCompliRvwA2ls;

  public TabvIcdmFile() {}

  public long getFileId() {
    return this.fileId;
  }

  public void setFileId(final long fileId) {
    this.fileId = fileId;
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

  public long getFileCount() {
    return this.fileCount;
  }

  public void setFileCount(final long fileCount) {
    this.fileCount = fileCount;
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

  public long getNodeId() {
    return this.nodeId;
  }

  public void setNodeId(final long nodeId) {
    this.nodeId = nodeId;
  }

  public String getNodeType() {
    return this.nodeType;
  }

  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvIcdmFileData getTabvIcdmFileData() {
    return this.tabvIcdmFileData;
  }

  public void setTabvIcdmFileData(final TabvIcdmFileData tabvIcdmFileData) {
    this.tabvIcdmFileData = tabvIcdmFileData;
  }

  public Set<TEmrFile> getTEmrFiles() {
    return this.TEmrFiles;
  }

  public void setTEmrFiles(final Set<TEmrFile> temrFiles) {
    this.TEmrFiles = temrFiles;
  }

  public TEmrFile addTEmrFile(final TEmrFile temrFile) {
    getTEmrFiles().add(temrFile);
    temrFile.setTabvIcdmFile(this);
    return temrFile;
  }

  public TEmrFile removeTEmrFile(final TEmrFile temrFile) {
    getTEmrFiles().remove(temrFile);
    temrFile.setTabvIcdmFile(null);
    return temrFile;
  }

  public List<TCompliRvwA2l> getTCompliRvwA2ls() {
    return this.TCompliRvwA2ls;
  }

  public void setTCompliRvwA2ls(final List<TCompliRvwA2l> TCompliRvwA2ls) {
    this.TCompliRvwA2ls = TCompliRvwA2ls;
  }

}