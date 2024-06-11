package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;


/**
 * The persistent class for the T_RVW_QNAIRE_RESP_VARIANTS database table.
 */
@Entity
@Table(name = "T_RVW_QNAIRE_RESP_VARIANTS")
@NamedQueries(value = {
    @NamedQuery(name = "TRvwQnaireRespVariant.findAll", query = "SELECT t FROM TRvwQnaireRespVariant t"),
    @NamedQuery(name = TRvwQnaireRespVariant.GET_BY_A2L_RESP_IDS, query = "SELECT t FROM TRvwQnaireRespVariant t where t.tA2lResponsibility.a2lRespId in :a2lRespIds") })
public class TRvwQnaireRespVariant implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get TRvwQnaireRespVariant entries with given A2l Resp Ids
   */
  public static final String GET_BY_A2L_RESP_IDS = "TRvwQnaireRespVariant.getByA2lRespIds";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "QNAIRE_RESP_VAR_ID", unique = true, nullable = false)
  private long qnaireRespVarId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvProjectVariant tabvProjectVariant;

  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TRvwQnaireResponse
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QNAIRE_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwQnaireResponse tRvwQnaireResponse;

  // bi-directional many-to-one association to TA2lResponsibility
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lResponsibility tA2lResponsibility;

  // bi-directional many-to-one association to TA2lWorkPackage
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_WP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lWorkPackage tA2lWorkPackage;

  public TRvwQnaireRespVariant() {
    // Default Constructor
  }

  public long getQnaireRespVarId() {
    return this.qnaireRespVarId;
  }

  public void setQnaireRespVarId(final long qnaireRespVarId) {
    this.qnaireRespVarId = qnaireRespVarId;
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

  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

  public TRvwQnaireResponse getTRvwQnaireResponse() {
    return this.tRvwQnaireResponse;
  }

  public void setTRvwQnaireResponse(final TRvwQnaireResponse tRvwQnaireResponse) {
    this.tRvwQnaireResponse = tRvwQnaireResponse;
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
  public TA2lWorkPackage gettA2lWorkPackage() {
    return this.tA2lWorkPackage;
  }


  /**
   * @param tA2lWorkPackage the tA2lWorkPackage to set
   */
  public void settA2lWorkPackage(final TA2lWorkPackage tA2lWorkPackage) {
    this.tA2lWorkPackage = tA2lWorkPackage;
  }


}