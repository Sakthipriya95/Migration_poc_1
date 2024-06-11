package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;


/**
 * The persistent class for the T_COMPLI_RVW_A2L database table.
 */
@Entity
@Table(name = "T_COMPLI_RVW_A2L")
@NamedQuery(name = "TCompliRvwA2l.findAll", query = "SELECT t FROM TCompliRvwA2l t")
public class TCompliRvwA2l implements Serializable {

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TCompliRvwA2l.findAll";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RESULT_ID")
  private long resultId;

  @Column(name = "A2L_FILE_ID")
  private Long a2lFileId;

  @Column(name = "A2L_FILE_NAME")
  private String a2lFileName;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "NUM_COMPLI")
  private Long numCompli;

  @Column(name = "NUM_QSSD")
  private Long numQssd;

  @Column(name = "SDOM_PVER_NAME")
  private String sdomPverName;

  @Column(name = "SDOM_PVER_REVISION")
  private Long sdomPverRevision;

  @Column(name = "SDOM_PVER_VARIANT")
  private String sdomPverVariant;

  @Column(name = "STATUS")
  private String status;

  @Column(name = "TIME_USED")
  private Long timeUsed;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  @Column(name = "WEB_FLOW_JOB_ID")
  private String webFlowJobId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERSION_ID")
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TCompliRvwHex
  @OneToMany(mappedBy = "TCompliRvwA2l")
  private Set<TCompliRvwHex> TCompliRvwHexs;
  // bi-directional many-to-one association to TabvIcdmFile
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FILE_ID")
  private TabvIcdmFile tabvIcdmFile;


  public TCompliRvwA2l() {}

  public long getResultId() {
    return this.resultId;
  }

  public void setResultId(final long resultId) {
    this.resultId = resultId;
  }

  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }

  public String getA2lFileName() {
    return this.a2lFileName;
  }

  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
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

  public Long getNumCompli() {
    return this.numCompli;
  }

  public void setNumCompli(final Long numCompli) {
    this.numCompli = numCompli;
  }


  public String getSdomPverName() {
    return this.sdomPverName;
  }

  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
  }

  public Long getSdomPverRevision() {
    return this.sdomPverRevision;
  }

  public void setSdomPverRevision(final Long sdomPverRevision) {
    this.sdomPverRevision = sdomPverRevision;
  }

  public String getSdomPverVariant() {
    return this.sdomPverVariant;
  }

  public void setSdomPverVariant(final String sdomPverVariant) {
    this.sdomPverVariant = sdomPverVariant;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public Long getTimeUsed() {
    return this.timeUsed;
  }

  public void setTimeUsed(final Long timeUsed) {
    this.timeUsed = timeUsed;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getWebFlowJobId() {
    return this.webFlowJobId;
  }

  public void setWebFlowJobId(final String webFlowJobId) {
    this.webFlowJobId = webFlowJobId;
  }

  public Set<TCompliRvwHex> getTCompliRvwHexs() {
    return this.TCompliRvwHexs;
  }

  public void setTCompliRvwHexs(final Set<TCompliRvwHex> TCompliRvwHexs) {
    this.TCompliRvwHexs = TCompliRvwHexs;
  }

  public TCompliRvwHex addTCompliRvwHex(final TCompliRvwHex TCompliRvwHex) {
    getTCompliRvwHexs().add(TCompliRvwHex);
    TCompliRvwHex.setTCompliRvwA2l(this);

    return TCompliRvwHex;
  }

  public TCompliRvwHex removeTCompliRvwHex(final TCompliRvwHex TCompliRvwHex) {
    getTCompliRvwHexs().remove(TCompliRvwHex);
    TCompliRvwHex.setTCompliRvwA2l(null);

    return TCompliRvwHex;
  }

  /**
   * @return the tPidcVersion
   */
  public TPidcVersion gettPidcVersion() {
    return this.tPidcVersion;
  }


  /**
   * @param tPidcVersion the tPidcVersion to set
   */
  public void settPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

  public TabvIcdmFile getTabvIcdmFile() {
    return this.tabvIcdmFile;
  }

  public void setTabvIcdmFile(final TabvIcdmFile tabvIcdmFile) {
    this.tabvIcdmFile = tabvIcdmFile;
  }


  /**
   * @return the numQssd
   */
  public Long getNumQssd() {
    return this.numQssd;
  }


  /**
   * @param numQssd the numQssd to set
   */
  public void setNumQssd(final Long numQssd) {
    this.numQssd = numQssd;
  }


}