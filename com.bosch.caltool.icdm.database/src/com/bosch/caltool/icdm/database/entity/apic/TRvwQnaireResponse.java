package com.bosch.caltool.icdm.database.entity.apic;

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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

// ICDM-2404
/**
 * The persistent class for the T_RVW_QNAIRE_RESPONSE database table.
 */
@Entity
@Table(name = "T_RVW_QNAIRE_RESPONSE")
@NamedQueries(value = {
    @NamedQuery(name = TRvwQnaireResponse.GET_BY_PIDCVERSID, query = "SELECT t FROM TRvwQnaireResponse t where t.qnaireRespId in ( SELECT distinct(t1.tRvwQnaireResponse.qnaireRespId) FROM TRvwQnaireRespVariant t1 where t1.tPidcVersion.pidcVersId = :pidcVersId)") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TRvwQnaireResponse.GET_MATCHING_QNAIRERESPID_IN_DEST_NOVAR, query = " SELECT a.qnaire_resp_id " +
        "                                                                                                        FROM " +
        "                                                                                                           t_rvw_qnaire_response a, " +
        "                                                                                                           t_rvw_qnaire_resp_variants b, " +
        "                                                                                                           t_rvw_qnaire_resp_versions c, " +
        "                                                                                                           t_questionnaire_version d " +
        "                                                                                                        WHERE " +
        "                                                                                                            a.qnaire_resp_id = c.qnaire_resp_id " +
        "                                                                                                            AND c.qnaire_vers_id = d.qnaire_vers_id " +
        "                                                                                                            AND a.qnaire_resp_id = b.qnaire_resp_id " +
        "                                                                                                            AND d.qnaire_id = ? " +
        "                                                                                                            AND b.a2l_resp_id = ? " +
        "                                                                                                            AND b.a2l_wp_id = ? " +
        "                                                                                                            AND b.variant_id IS NULL " +
        "                                                                                                            AND a.qnaire_resp_id != ? "),
    @NamedNativeQuery(name = TRvwQnaireResponse.GET_MATCHING_QNAIRERESPID_IN_DEST, query = " SELECT a.qnaire_resp_id " +
        "                                                                                                        FROM " +
        "                                                                                                           t_rvw_qnaire_response a, " +
        "                                                                                                           t_rvw_qnaire_resp_variants b, " +
        "                                                                                                           t_rvw_qnaire_resp_versions c, " +
        "                                                                                                           t_questionnaire_version d " +
        "                                                                                                        WHERE " +
        "                                                                                                            a.qnaire_resp_id = c.qnaire_resp_id " +
        "                                                                                                            AND c.qnaire_vers_id = d.qnaire_vers_id " +
        "                                                                                                            AND a.qnaire_resp_id = b.qnaire_resp_id " +
        "                                                                                                            AND d.qnaire_id = ? " +
        "                                                                                                            AND b.a2l_resp_id = ? " +
        "                                                                                                            AND b.a2l_wp_id = ? " +
        "                                                                                                            AND b.variant_id = ? " +
        "                                                                                                            AND a.qnaire_resp_id != ? "),
    @NamedNativeQuery(name = TRvwQnaireResponse.GET_LINKED_QNAIRE_RESP_VAR_ID, query = " SELECT qnaire_resp_var_id " +
        "                                                                                                        FROM " +
        "                                                                                                           t_rvw_qnaire_resp_variants " +
        "                                                                                                        WHERE " +
        "                                                                                                            qnaire_resp_id IN (?,?) " +
        "                                                                                                            AND a2l_resp_id = ? " +
        "                                                                                                            AND a2l_wp_id = ? " +
        "                                                                                                            AND variant_id = ? " +
        "                                                                                                        ORDER BY created_date DESC"),
    @NamedNativeQuery(name = TRvwQnaireResponse.GET_LINKED_QNAIRE_RESP_VAR_ID_NOVAR, query = " SELECT qnaire_resp_var_id " +
        "                                                                                                        FROM " +
        "                                                                                                           t_rvw_qnaire_resp_variants " +
        "                                                                                                        WHERE " +
        "                                                                                                            qnaire_resp_id IN (?,?) " +
        "                                                                                                            AND a2l_resp_id = ? " +
        "                                                                                                            AND a2l_wp_id = ? " +
        "                                                                                                            AND variant_id IS NULL " +
        "                                                                                                        ORDER BY created_date DESC") })
public class TRvwQnaireResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  public static final String GET_BY_PIDCVERSID = "TRvwQnaireResponse.getByPidcVersId";
  /**
   * Get the Qnaire Resp Id, if same qnaire is present in the Destnation while doing qnaire linking
   */
  public static final String GET_MATCHING_QNAIRERESPID_IN_DEST = "TRvwQnaireResponse.getMatchingQnaireRespIdInDest";
  /**
   * Get the Qnaire Resp Id, if same qnaire is present in the Destnation while doing qnaire linking for No Variant PIDC
   */
  public static final String GET_MATCHING_QNAIRERESPID_IN_DEST_NOVAR =
      "TRvwQnaireResponse.getMatchingQnaireRespIdInDestNoVar";
  /**
   * Get the Qnaire Resp Variant Id, if qnaire is already in the Destnation while doing qnaire linking
   */
  public static final String GET_LINKED_QNAIRE_RESP_VAR_ID = "TRvwQnaireResponse.getLinkedQnaireRespVarId";

  /**
   * Get the Qnaire Resp Variant Id, if qnaire is already in the Destnation while doing qnaire linking for No Variant
   */
  public static final String GET_LINKED_QNAIRE_RESP_VAR_ID_NOVAR = "TRvwQnaireResponse.getLinkedQnaireRespVarIdNoVar";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "QNAIRE_RESP_ID", unique = true, nullable = false)
  private long qnaireRespId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  /**
   * @deprecated use the field from RespVersion model
   */
  @Column(name = "REVIEWED_FLAG", length = 1)
  @Deprecated
  private String reviewedFlag;

  /**
   * @deprecated use the field from RespVersion model
   */
  @Column(name = "REVIEWED_DATE")
  @Deprecated
  private Timestamp reviewedDate;

  /**
   * @deprecated use the field from RespVersion model
   */
  @Column(name = "REVIEWED_USER", length = 30)
  @Deprecated
  private String reviewedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

//bi-directional many-to-one association to TRvwQnaireRespVariant
  @OneToMany(mappedBy = "tRvwQnaireResponse", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants;

  // bi-directional many-to-one association to TRvwQnaireRespVersion
  @OneToMany(mappedBy = "tRvwQnaireResponse", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireRespVersion> tRvwQnaireRespVersions;


  @Column(name = "DELETED_FLAG", length = 1)
  private String deletedFlag;


  public TRvwQnaireResponse() {
    // Default Constructor
  }

  public long getQnaireRespId() {
    return this.qnaireRespId;
  }

  public void setQnaireRespId(final long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
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

  /**
   * @return the reviewedFlag
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public String getReviewedFlag() {
    return this.reviewedFlag;
  }


  /**
   * @param reviewedFlag the reviewedFlag to set
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public void setReviewedFlag(final String reviewedFlag) {
    this.reviewedFlag = reviewedFlag;
  }


  /**
   * @return the reviewedDate
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public Timestamp getReviewedDate() {
    return this.reviewedDate;
  }


  /**
   * @param reviewedDate the reviewedDate to set
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public void setReviewedDate(final Timestamp reviewedDate) {
    this.reviewedDate = reviewedDate;
  }


  /**
   * @return the reviewedUser
   * @deprecated use the field from RespVersion entity
   */
  @Deprecated
  public String getReviewedUser() {
    return this.reviewedUser;
  }


  /**
   * @param reviewedUser the reviewedUser to set
   * @deprecated use the field from RespVersion entity
   */
  @Deprecated
  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
  }


  /**
   * @return the tRvwQnaireRespVariants
   */
  public Set<TRvwQnaireRespVariant> getTRvwQnaireRespVariants() {
    return this.tRvwQnaireRespVariants;
  }


  /**
   * @param tRvwQnaireRespVariants the tRvwQnaireRespVariants to set
   */
  public void setTRvwQnaireRespVariants(final Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants) {
    this.tRvwQnaireRespVariants = tRvwQnaireRespVariants;
  }

  /**
   * @param tRvwQnaireRespVariant as input
   * @return TRvwQnaireRespVariant
   */
  public TRvwQnaireRespVariant addTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariant) {
    if (getTRvwQnaireRespVariants() == null) {
      setTRvwQnaireRespVariants(new HashSet<>());
    }
    getTRvwQnaireRespVariants().add(tRvwQnaireRespVariant);
    tRvwQnaireRespVariant.setTRvwQnaireResponse(this);

    return tRvwQnaireRespVariant;
  }

  /**
   * @param tRvwQnaireRespVariant as input
   * @return TRvwQnaireRespVariant
   */
  public TRvwQnaireRespVariant removeTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariant) {
    getTRvwQnaireRespVariants().remove(tRvwQnaireRespVariant);

    return tRvwQnaireRespVariant;
  }

  /**
   * @return the tRvwQnaireRespVersions
   */
  public Set<TRvwQnaireRespVersion> getTRvwQnaireRespVersions() {
    return this.tRvwQnaireRespVersions;
  }


  /**
   * @param tRvwQnaireRespVersions the tRvwQnaireRespVersions to set
   */
  public void setTRvwQnaireRespVersions(final Set<TRvwQnaireRespVersion> tRvwQnaireRespVersions) {
    this.tRvwQnaireRespVersions = tRvwQnaireRespVersions;
  }

  /**
   * @param tRvwQnaireRespVersion as input
   * @return tRvwQnaireRespVersion
   */
  public TRvwQnaireRespVersion addTRvwQnaireRespVersion(final TRvwQnaireRespVersion tRvwQnaireRespVersion) {
    if (getTRvwQnaireRespVersions() == null) {
      setTRvwQnaireRespVersions(new HashSet<>());
    }
    getTRvwQnaireRespVersions().add(tRvwQnaireRespVersion);

    tRvwQnaireRespVersion.setTRvwQnaireResponse(this);

    return tRvwQnaireRespVersion;
  }

  /**
   * @return the deletedFlag
   */
  public String getDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

}