package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
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

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;

/**
 * The persistent class for the T_RVW_WP_RESP database table.
 */
@Entity
@Table(name = "T_RVW_WP_RESP")
@NamedQuery(name = "TRvwWpResp.findAll", query = "SELECT t FROM TRvwWpResp t")
public class TRvwWpResp implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RVW_WP_RESP_ID")
  private long rvwWpRespId;

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
  private long version;

  // bi-directional many-to-one association to TA2lWpResponsibility
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_WP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lWorkPackage tA2lWorkPackage;

  // bi-directional many-to-one association to TA2lResponsibility
  @ManyToOne
  @JoinColumn(name = "A2L_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lResponsibility tA2lResponsibility;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESULT_ID")
  private TRvwResult tRvwResult;

  // bi-directional many-to-one association to TRvwParameter

  @OneToMany(mappedBy = "tRvwWpResp", fetch = FetchType.LAZY)
  private Set<TRvwParameter> tRvwParameter;

  /**
   *
   */
  public TRvwWpResp() {}

  /**
   * @return rvwWpRespId
   */
  public long getRvwWpRespId() {
    return this.rvwWpRespId;
  }

  /**
   * @param rvwWpRespId is set
   */
  public void setRvwWpRespId(final long rvwWpRespId) {
    this.rvwWpRespId = rvwWpRespId;
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

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TRvwResult getTRvwResult() {
    return this.tRvwResult;
  }

  public void setTRvwResult(final TRvwResult tRvwResult) {
    this.tRvwResult = tRvwResult;
  }


  /**
   * @return the tA2lResponsibility
   */
  public TA2lResponsibility gettA2lResponsibility() {
    return this.tA2lResponsibility;
  }


  /**
   * @param tA2lResponsibility the tA2lResponsibility to set
   */
  public void settA2lResponsibility(final TA2lResponsibility tA2lResponsibility) {
    this.tA2lResponsibility = tA2lResponsibility;
  }


  /**
   * @return the tA2lWorkPackage
   */
  public TA2lWorkPackage getTA2lWorkPackage() {
    return this.tA2lWorkPackage;
  }


  /**
   * @param tA2lWorkPackage the tA2lWorkPackage to set
   */
  public void setTA2lWorkPackage(final TA2lWorkPackage tA2lWorkPackage) {
    this.tA2lWorkPackage = tA2lWorkPackage;
  }

  /**
   * @return the tRvwResult
   */
  public TRvwResult gettRvwResult() {
    return this.tRvwResult;
  }


  /**
   * @param tRvwResult the tRvwResult to set
   */
  public void settRvwResult(final TRvwResult tRvwResult) {
    this.tRvwResult = tRvwResult;
  }


  /**
   * @return the tRvwParameter
   */
  public Set<TRvwParameter> gettRvwParameter() {
    return this.tRvwParameter;
  }


  /**
   * @param tRvwParameter the tRvwParameter to set
   */
  public void settRvwParameter(final Set<TRvwParameter> tRvwParameter) {
    this.tRvwParameter = tRvwParameter;
  }

  /**
   * @param TRvwParameter as input
   * @return TRvwParameter
   */
  public TRvwParameter addTRvwParameter(final TRvwParameter tRvwParameter) {
    if (gettRvwParameter() == null) {
      settRvwParameter(new HashSet<>());
    }
    gettRvwParameter().add(tRvwParameter);
    tRvwParameter.settRvwWpResp(this);

    return tRvwParameter;
  }

}