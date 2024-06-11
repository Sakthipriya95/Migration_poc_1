package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;


/**
 * The persistent class for the T_RVW_FILES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_FILES")
@NamedQuery(name = TRvwFile.NQ_GET_FILES_BY_RESULT_AND_TYPE, query = "                      " +
    "select rvwFile.TRvwResult.resultId, rvwfile.rvwFileId, rvwFile.tabvIcdmFile.fileName " +
    "from TRvwFile rvwFile                                                                " +
    "where rvwFile.TRvwResult.resultId in :resultIDList and rvwFile.fileType= :fileType   ")
public class TRvwFile implements Serializable {

  private static final long serialVersionUID = 1L;


  /**
   * Get the reviewed files using result ID and file type
   *
   * @param resultID - list of resultIDs
   * @param fileType - file type - I/O/A etc.
   * @return List of (result id, review file ID, file name)
   */
  public static final String NQ_GET_FILES_BY_RESULT_AND_TYPE = "TRvwFile.getRvwFileNamebyResultAndType";


  @Id
  @SequenceGenerator(name = "CDR_SEQ_GENERATOR", sequenceName = "SEQV_ATTRIBUTES", allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "RVW_FILE_ID")
  private long rvwFileId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "FILE_TYPE")
  private String fileType;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TRvwParameter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RVW_PARAM_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwParameter TRvwParameter;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESULT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwResult TRvwResult;

  // bi-directional many-to-one association to TabvIcdmFile
  @ManyToOne
  @JoinColumn(name = "FILE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvIcdmFile tabvIcdmFile;
  // ICDM-1720
  // bi-directional many-to-one association to TRvwParameter
  @OneToMany(mappedBy = "TRvwFile")
  private List<TRvwParameter> TRvwParameters;

  public TRvwFile() {}

  public long getRvwFileId() {
    return this.rvwFileId;
  }

  public void setRvwFileId(final long rvwFileId) {
    this.rvwFileId = rvwFileId;
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

  public String getFileType() {
    return this.fileType;
  }

  public void setFileType(final String fileType) {
    this.fileType = fileType;
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

  public TRvwParameter getTRvwParameter() {
    return this.TRvwParameter;
  }

  public void setTRvwParameter(final TRvwParameter TRvwParameter) {
    this.TRvwParameter = TRvwParameter;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }

  public TabvIcdmFile getTabvIcdmFile() {
    return this.tabvIcdmFile;
  }

  public void setTabvIcdmFile(final TabvIcdmFile tabvIcdmFile) {
    this.tabvIcdmFile = tabvIcdmFile;
  }


  public List<TRvwParameter> getTRvwParameters() {
    return this.TRvwParameters;
  }

  public void setTRvwParameters(final List<TRvwParameter> TRvwParameters) {
    this.TRvwParameters = TRvwParameters;
  }


}