package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
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

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_RVW_RESULTS_SECONDARY database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_RESULTS_SECONDARY")
@NamedQuery(name = "TRvwResultsSecondary.findAll", query = "SELECT t FROM TRvwResultsSecondary t")
public class TRvwResultsSecondary implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "SEC_REVIEW_ID", unique = true, nullable = false)
  private long secReviewId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

  // bi-directional many-to-one association to TRvwParametersSecondary
  @OneToMany(mappedBy = "TRvwResultsSecondary")
  private Set<TRvwParametersSecondary> TRvwParametersSecondaries;

  // bi-directional many-to-one association to TRuleSet
  @ManyToOne
  @JoinColumn(name = "RSET_ID")
  private TRuleSet TRuleSet;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne
  @JoinColumn(name = "RESULT_ID")
  private TRvwResult TRvwResult;

  @Column(name = "SSD_RELEASE_ID")
  private long ssdReleaseID;

  @Column(name = "SOURCE")
  private String source;

  @Column(name = "SSD_VERSION_ID")
  private long ssdVersID;


  /**
   * @return the source
   */
  public String getSource() {
    return this.source;
  }


  /**
   * @param source the source to set
   */
  public void setSource(final String source) {
    this.source = source;
  }

  public TRvwResultsSecondary() {}

  public long getSecReviewId() {
    return this.secReviewId;
  }

  public void setSecReviewId(final long secReviewId) {
    this.secReviewId = secReviewId;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TRvwParametersSecondary> getTRvwParametersSecondaries() {
    return this.TRvwParametersSecondaries;
  }

  public void setTRvwParametersSecondaries(final Set<TRvwParametersSecondary> TRvwParametersSecondaries) {
    this.TRvwParametersSecondaries = TRvwParametersSecondaries;
  }


  public TRuleSet getTRuleSet() {
    return this.TRuleSet;
  }

  public void setTRuleSet(final TRuleSet TRuleSet) {
    this.TRuleSet = TRuleSet;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }


  /**
   * @return the ssdReleaseID
   */
  public long getSsdReleaseID() {
    return this.ssdReleaseID;
  }


  /**
   * @param ssdReleaseID the ssdReleaseID to set
   */
  public void setSsdReleaseID(final long ssdReleaseID) {
    this.ssdReleaseID = ssdReleaseID;
  }


  /**
   * @return the ssdVersID
   */
  public long getSsdVersID() {
    return this.ssdVersID;
  }


  /**
   * @param ssdVersID the ssdVersID to set
   */
  public void setSsdVersID(final long ssdVersID) {
    this.ssdVersID = ssdVersID;
  }


}