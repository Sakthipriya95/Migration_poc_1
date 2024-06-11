package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_RVW_PARAMETERS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_PARAMETERS")
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TRvwParameter.NNQ_GET_PROJDATA_NO_VAR, query = "                                                                           " +
        "SELECT                                                                                                                                       " +
        "    param_id                                                                                                                                 " +
        "  , rvw_param_id                                                                                                                             " +
        "  , rvw_rank                                                                                                                                 " +
        "  , rev_number_for_label                                                                                                                     " +
        "FROM                                                                                                                                         " +
        "  (SELECT                                                                                                                                    " +
        "      row_number() over (partition BY rvw_param.param_id, pidca2l.project_id order by results.created_date DESC) rev_number_for_label        " +
        "    , rvw_param.param_id                                                                                                                     " +
        "    , rvw_param.rvw_param_id                                                                                                                 " +
        "    , CASE                                                                                                                                   " +
        "        WHEN (results.REVIEW_TYPE = 'O') AND (NVL(results.LOCK_STATUS, 'N') = 'Y') THEN 1                                                    " +
        "        WHEN (results.REVIEW_TYPE = 'O') AND (NVL(results.LOCK_STATUS, 'N') = 'N') THEN 2                                                    " +
        "        WHEN (results.REVIEW_TYPE = 'S') AND (NVL(results.LOCK_STATUS, 'N') = 'Y') THEN 3                                                    " +
        "        WHEN (results.REVIEW_TYPE = 'S') AND (NVL(results.LOCK_STATUS, 'N') = 'N') THEN 4                                                    " +
        "        ELSE 999                                                                                                                             " +
        "      END AS RVW_RANK                                                                                                                        " +
        "   FROM                                                                                                                                      " +
        "      t_rvw_results results                                                                                                                  " +
        "    , t_rvw_parameters rvw_param                                                                                                             " +
        "    , t_rvw_variants rvw_var                                                                                                                 " +
        "    , gtt_object_names temp                                                                                                                  " +
        "    , t_pidc_a2l pidca2l                                                                                                                     " +
        "   WHERE results.result_id       = rvw_param.result_id                                                                                       " +
        "     AND results.SOURCE_TYPE    != 'MONICA'                                                                                                  " +
        "     AND results.PIDC_A2L_ID     = pidca2l.PIDC_A2L_ID                                                                                       " +
        "     AND temp.id                 = rvw_param.param_id                                                                                        " +
        "     AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8 )                                                      " +
        "     AND results.result_id       = rvw_var.result_id(+)                                                                                      " +
        "     AND rvw_var.VARIANT_ID      is null                                                                                                     " +
        "     AND results.REVIEW_TYPE    != 'T'                                                                                                       " +
        "     AND pidca2l.project_id      = ?                                                                                                         " +
        "  )                                                                                                                                          " +
        "ORDER BY rev_number_for_label                                                                                                                "),

    @NamedNativeQuery(name = TRvwParameter.NNQ_GET_PROJDATA_WITH_VAR, query = "                                                                         " +
        "SELECT                                                                                                                                       " +
        "    param_id                                                                                                                                 " +
        "  , rvw_param_id                                                                                                                             " +
        "  , rvw_rank                                                                                                                                 " +
        "  , rev_number_for_label                                                                                                                     " +
        "FROM                                                                                                                                         " +
        "  (SELECT                                                                                                                                    " +
        "       row_number() over (partition BY rvw_param.param_id, pidca2l.project_id order by results.created_date DESC) rev_number_for_label       " +
        "     , rvw_param.param_id                                                                                                                    " +
        "     , rvw_param.rvw_param_id                                                                                                                " +
        "     , CASE                                                                                                                                  " +
        "         WHEN (results.REVIEW_TYPE = 'O') AND (NVL(results.LOCK_STATUS, 'N') = 'Y') THEN 1                                                   " +
        "         WHEN (results.REVIEW_TYPE = 'O') AND (NVL(results.LOCK_STATUS, 'N') = 'N') THEN 2                                                   " +
        "         WHEN (results.REVIEW_TYPE = 'S') AND (NVL(results.LOCK_STATUS, 'N') = 'Y') THEN 3                                                   " +
        "         WHEN (results.REVIEW_TYPE = 'S') AND (NVL(results.LOCK_STATUS, 'N') = 'N') THEN 4                                                   " +
        "         ELSE 999                                                                                                                            " +
        "         END AS RVW_RANK                                                                                                                     " +
        "   FROM                                                                                                                                      " +
        "       t_rvw_results results                                                                                                                 " +
        "     , t_rvw_parameters rvw_param                                                                                                            " +
        "     , t_rvw_variants rvw_var                                                                                                                " +
        "     , gtt_object_names temp                                                                                                                 " +
        "     , t_pidc_a2l pidca2l                                                                                                                    " +
        "     , TABV_PROJECT_VARIANTS variant                                                                                                         " +
        "     , TABV_ATTR_VALUES attrval                                                                                                              " +
        "  WHERE                                                                                                                                      " +
        "        results.result_id        = rvw_param.result_id                                                                                       " +
        "     AND results.SOURCE_TYPE    != 'MONICA'                                                                                                  " +
        "     AND results.PIDC_A2L_ID     = pidca2l.PIDC_A2L_ID                                                                                       " +
        "     AND temp.id                 = rvw_param.param_id                                                                                        " +
        "     AND rvw_var.result_id       = results.result_id                                                                                         " +
        "     AND rvw_var.VARIANT_ID      = variant.VARIANT_ID                                                                                        " +
        "     AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8 )                                                      " +
        "     AND variant.value_id        = attrval.value_id                                                                                          " +
        "     AND variant.deleted_flag   != 'Y'                                                                                                       " +
        "     AND results.REVIEW_TYPE    != 'T'                                                                                                       " +
        "     AND attrval.value_id        =                                                                                                           " +
        "         (SELECT variant.value_id                                                                                                            " +
        "             FROM TABV_PROJECT_VARIANTS variant                                                                                              " +
        "             WHERE variant.variant_id = ?                                                                                                    " +
        "         )                                                                                                                                   " +
        "     AND pidca2l.project_id      = ?                                                                                                         " +
        "  )                                                                                                                                          " +
        "ORDER BY rev_number_for_label                                                                                                                "),
    @NamedNativeQuery(name = TRvwParameter.NNQ_GET_PARAM_MAPPED_TO_WPANDRESP, query = "" +
        "SELECT                                                                                                                                     " +
        "   DISTINCT(param.param_id),                                                                                                               " +
        "   resp.A2L_WP_ID,                                                                                                                     " +
        "   a2lresp.A2L_RESP_ID                                                                                                                      " +
        "   FROM                                                                                                                                    " +
        "       t_rvw_wp_resp resp,                                                                                                                 " +
        "       t_rvw_parameters param,                                                                                                             " +
        "       t_a2l_responsibility a2lresp,                                                                                                       " +
        "       t_rvw_results result                                                                                                                " +
        "   WHERE                                                                                                                                   " +
        "       resp.RVW_WP_RESP_ID = param.RVW_WP_RESP_ID                                                                                          " +
        "       AND param.result_id = result.result_id                                                                                              " +
        "       AND resp.result_id = result.result_id                                                                                               " +
        "       AND  a2lresp.A2L_RESP_ID = resp.A2L_RESP_ID                                                                                         " +
        "       AND result.result_id = ?                                                                                                            ") })

@NamedQueries(value = {
    @NamedQuery(name = TRvwParameter.NQ_GET_PRECALDATA_WITH_NO_VAR, query = "" +
        "SELECT rvwparam.rvwParamId, param.id, rvwparam.checkedValue " +
        "  FROM TRvwParameter rvwparam JOIN rvwparam.TParameter param JOIN rvwparam.TRvwResult res ,GttParameter gttparam, GttObjectName gttobjname " +
        "  WHERE rvwparam.reviewScore IN (8,9) AND param.id = gttparam.id AND  res.TPidcA2l.TPidcVersion.pidcVersId = gttobjname.id AND res.reviewType='O' AND res.lockStatus='Y' " +
        "   AND rvwparam.checkedValue IS NOT NULL"),
    @NamedQuery(name = TRvwParameter.NQ_GET_PRECALDATA_WITH_VARS, query = "" +
        "SELECT rvwparam.rvwParamId, param.id, rvwparam.checkedValue " +
        "  FROM TRvwParameter rvwparam JOIN rvwparam.TParameter param JOIN rvwparam.TRvwResult res JOIN res.TRvwVariants variant, GttParameter gttparam, GttObjectName gttobjname " +
        "  WHERE rvwparam.reviewScore IN (8,9) AND  param.id = gttparam.id AND  res.TPidcA2l.TPidcVersion.pidcVersId = gttobjname.id AND res.reviewType='O' AND res.lockStatus='Y' " +
        "   AND rvwparam.checkedValue IS NOT NULL"),
    @NamedQuery(name = TRvwParameter.NQ_GET_PRECALDATA_WITH_PIDC_VAR_IDS, query = "" +
        "SELECT rvwparam.rvwParamId, param.id, rvwparam.checkedValue " +
        "  FROM TRvwParameter rvwparam JOIN rvwparam.TParameter param JOIN rvwparam.TRvwResult res JOIN res.TRvwVariants variant, GttParameter gttparam, GttObjectName gttobjname " +
        "   WHERE rvwparam.reviewScore IN (8,9) AND param.id = gttparam.id AND variant.tabvProjectVariant.variantId = gttobjname.id AND res.reviewType='O' AND res.lockStatus='Y' " +
        "       AND rvwparam.checkedValue IS NOT NULL"),
    @NamedQuery(name = TRvwParameter.GETPARENTDELTAREVIEW, query = "    " +
        " SELECT b FROM TRvwResult a, TRvwParameter b ,GttObjectName temp  " +
        " WHERE a.resultId = b.TRvwResult.resultId   AND  a.resultId = temp.id " +
        " AND a.deltaReviewType = :reviewtype AND b.parentTRvwParam.rvwParamId != null"),
    @NamedQuery(name = TRvwParameter.NQ_GET_REVIEW_DATA_RESPONSE, query = "    " +
        "SELECT rvwRes from TRvwParameter rvwRes                               " +
        "   join rvwRes.TParameter param                                       " +
        "   join rvwRes.TRvwResult result                                      " +
        "WHERE  result.reviewType != 'T'                                       " +
        "   AND  rvwRes.reviewScore  IN :checkedTypes                          " +
        "   AND param.id  = :paramId                                           ") })
public class TRvwParameter implements Serializable {


  /**
   *
   */
  public static final String GETPARENTDELTAREVIEW = "TRvwParameter.getparentdeltareview";


  private static final long serialVersionUID = 1L;

  /**
   * Named Native query to fetch project level review of parameters for delta review, when project version does not have
   * variants. Parameters are retrieved from temporary table.
   *
   * @param projectID PIDC ID
   * @return list of columns queried
   */
  public static final String NNQ_GET_PROJDATA_NO_VAR = "TRvwParameter.ParamProjectDataNoVariant";

  /**
   * Named Native queryto fetch project level review of parameters for delta review, when variant is available in the
   * project version. Parameters are retrieved from temporary table.
   *
   * @param variantID Project Variant ID
   * @param projectID PIDC ID
   * @return list of columns queried
   */
  public static final String NNQ_GET_PROJDATA_WITH_VAR = "TRvwParameter.ParamProjectDataWithVariant";

  /**
   * Named Native queryto fetch parameters which are mapped to a WP and RESP for a Review Result.
   */
  public static final String NNQ_GET_PARAM_MAPPED_TO_WPANDRESP = "TRvwParameter.ParamMappedToWPAndRESP";

  /**
   * JPA Query to fetch pre-caldata from rvw results with pidc version ids having no variants
   */
  // Task 243510
  public static final String NQ_GET_PRECALDATA_WITH_NO_VAR = "TRvwResults.NQPreCalDataWithNoVariants";

  /**
   * JPA Query to fetch pre-caldata from rvw results with pidc version ids having variants
   */
  public static final String NQ_GET_PRECALDATA_WITH_VARS = "TRvwResults.NQPreCalDataWithVariants";

  /**
   * JPA Query to fetch pre-caldata from rvw results with pidc variant ids
   */
  public static final String NQ_GET_PRECALDATA_WITH_PIDC_VAR_IDS = "TRvwResults.NQPreCalDataWithPidcVarIds";

  /**
   * JPA Query to fetch review data response
   */
  public static final String NQ_GET_REVIEW_DATA_RESPONSE = "TRvwParameter.GetReviewDataResponse";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "RVW_PARAM_ID", unique = true, nullable = false)
  private long rvwParamId;

  @Column(name = "CHANGE_FLAG", precision = 2)
  private int changeFlag;

  @Column(name = "CHECK_UNIT", length = 255)
  private String checkUnit;

  @Lob()
  @Column(name = "CHECKED_VALUE")
  private byte[] checkedValue;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 20)
  private String createdUser;

  @Column(length = 4000)
  private String hint;

  @Column(name = "LOWER_LIMIT")
  private BigDecimal lowerLimit;

  @Column(name = "MATCH_REF_FLAG", length = 1)
  private String matchRefFlag;

  @Column(name = "REVIEW_SCORE", length = 1)
  private String reviewScore;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 20)
  private String modifiedUser;

  @Lob()
  @Column(name = "REF_VALUE")
  private byte[] refValue;

  @Column(name = "\"RESULT\"", length = 1)
  private String result;

  @Column(name = "COMPLI_RESULT", length = 1)
  private String compliResult;


  @Column(name = "RVW_COMMENT", length = 4000)
  private String rvwComment;

  @Column(name = "RVW_METHOD", length = 1)
  private String rvwMethod;

  @Column(name = "REF_UNIT")
  private String refUnit;

  @Column(name = "UPPER_LIMIT")
  private BigDecimal upperLimit;

  @Column(name = "MATURITY_LEVEL", length = 10)
  private String maturityLvl;


  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TRvwFunction
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RVW_FUN_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwFunction TRvwFunction;

  // bi-directional many-to-one association to TRvwFile
  @OneToMany(mappedBy = "TRvwParameter", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwFile> TRvwFiles;

  // bi-directional many-to-one association to TParameter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TParameter TParameter;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESULT_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwResult TRvwResult;

  // ICDM-1720
  // bi-directional many-to-one association to TRvwFile
  @ManyToOne
  @JoinColumn(name = "RVW_FILE_ID")
  private TRvwFile TRvwFile;

  @Column(name = "BITWISE_LIMIT", length = 400)
  private String bitwiseLimit;

  // ICDM-2183
  // bi-directional many-to-one association to TRvwParameter
  @ManyToOne
  @JoinColumn(name = "PARENT_PARAM_ID")
  private TRvwParameter parentTRvwParam;


  @Column(name = "LAB_OBJ_ID")
  private Long labObjId;

  @Column(name = "REV_ID")
  private Long revId;

  @Column(name = "COMPLI_LAB_OBJ_ID")
  private Long compliLabObjId;

  @Column(name = "COMPLI_REV_ID")
  private Long compliRevId;

  @Column(name = "QSSD_RESULT", length = 1)
  private String qssdResult;

  @Column(name = "QSSD_LAB_OBJ_ID")
  private Long qssdLabObjId;

  @Column(name = "QSSD_REV_ID")
  private Long qssdRevId;

  @Column(name = "SECONDARY_RESULT")
  private String secondaryResult;

  // Task 236308 - adding column 'secondary result state' for considering checked state
  @Column(name = "SECONDARY_RESULT_STATE")
  private String secondaryResultState;

  @Column(name = "SR_ACCEPTED_DATE")
  private Timestamp srAcceptedDate;

  @Column(name = "SR_ACCEPTED_FLAG", length = 1)
  private String srAcceptedFlag;

  @Column(name = "SR_ACCEPTED_USER", length = 20)
  private String srAcceptedUser;

  @Column(name = "SR_ERROR_DETAILS", length = 4000)
  private String srErrorDetails;

  @Column(name = "SR_RESULT", length = 1)
  private String srResult;

  @Column(name = "ARC_RELEASED_FLAG", length = 1)
  private String arcReleasedFlag;

  // bi-directional many-to-one association to TRvwParametersSecondary
  @OneToMany(mappedBy = "TRvwParameter", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwParametersSecondary> TRvwParametersSecondaries;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RVW_WP_RESP_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwWpResp tRvwWpResp;

  @Column(name = "READ_ONLY_PARAM")
  private String readOnlyParamFlag;

  public TRvwParameter getTRvwParameter() {
    return this.parentTRvwParam;
  }

  public void setTRvwParameter(final TRvwParameter tRvwParameter) {
    this.parentTRvwParam = tRvwParameter;
  }


  @Column(length = 1)
  private String isbitwise;

  public TRvwParameter() {
    // default constructor
  }

  public long getRvwParamId() {
    return this.rvwParamId;
  }

  public void setRvwParamId(final long rvwParamId) {
    this.rvwParamId = rvwParamId;
  }

  public int getChangeFlag() {
    return this.changeFlag;
  }

  public void setChangeFlag(final int changeFlag) {
    this.changeFlag = changeFlag;
  }

  public String getCheckUnit() {
    return this.checkUnit;
  }

  public void setCheckUnit(final String checkUnit) {
    this.checkUnit = checkUnit;
  }

  public byte[] getCheckedValue() {
    return this.checkedValue;
  }

  public void setCheckedValue(final byte[] checkedValue) {
    this.checkedValue = checkedValue;
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

  public String getHint() {
    return this.hint;
  }

  public void setHint(final String hint) {
    this.hint = hint;
  }

  public BigDecimal getLowerLimit() {
    return this.lowerLimit;
  }

  public void setLowerLimit(final BigDecimal lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public String getMatchRefFlag() {
    return this.matchRefFlag;
  }

  public void setMatchRefFlag(final String matchRefFlag) {
    this.matchRefFlag = matchRefFlag;
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

  public String getRefUnit() {
    return this.refUnit;
  }

  public void setRefUnit(final String refUnit) {
    this.refUnit = refUnit;
  }

  public byte[] getRefValue() {
    return this.refValue;
  }

  public void setRefValue(final byte[] refValue) {
    this.refValue = refValue;
  }

  public String getResult() {
    return this.result;
  }

  public void setResult(final String result) {
    this.result = result;
  }


  public String getRvwComment() {
    return this.rvwComment;
  }

  public void setRvwComment(final String rvwComment) {
    this.rvwComment = rvwComment;
  }

  public String getRvwMethod() {
    return this.rvwMethod;
  }

  public void setRvwMethod(final String rvwMethod) {
    this.rvwMethod = rvwMethod;
  }


  public BigDecimal getUpperLimit() {
    return this.upperLimit;
  }

  public void setUpperLimit(final BigDecimal upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TRvwFunction getTRvwFunction() {
    return this.TRvwFunction;
  }

  public void setTRvwFunction(final TRvwFunction tRvwFunction) {
    this.TRvwFunction = tRvwFunction;
  }

  public Set<TRvwFile> getTRvwFiles() {
    return this.TRvwFiles;
  }

  public void setTRvwFiles(final Set<TRvwFile> tRvwFiles) {
    this.TRvwFiles = tRvwFiles;
  }

  public TParameter getTParameter() {
    return this.TParameter;
  }

  public void setTParameter(final TParameter tParameter) {
    this.TParameter = tParameter;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult tRvwResult) {
    this.TRvwResult = tRvwResult;
  }


  public TRvwFile getTRvwFile() {
    return this.TRvwFile;
  }

  public void setTRvwFile(final TRvwFile tRvwFile) {
    this.TRvwFile = tRvwFile;
  }

  public String getIsbitwise() {
    return this.isbitwise;
  }

  public void setIsbitwise(final String isbitwise) {
    this.isbitwise = isbitwise;
  }

  public String getBitwiseLimit() {
    return this.bitwiseLimit;
  }

  public void setBitwiseLimit(final String bitwiseLimit) {
    this.bitwiseLimit = bitwiseLimit;
  }

  public Timestamp getSrAcceptedDate() {
    return this.srAcceptedDate;
  }

  public void setSrAcceptedDate(final Timestamp srAcceptedDate) {
    this.srAcceptedDate = srAcceptedDate;
  }

  public String getSrAcceptedFlag() {
    return this.srAcceptedFlag;
  }

  public void setSrAcceptedFlag(final String srAcceptedFlag) {
    this.srAcceptedFlag = srAcceptedFlag;
  }

  public String getSrAcceptedUser() {
    return this.srAcceptedUser;
  }

  public void setSrAcceptedUser(final String srAcceptedUser) {
    this.srAcceptedUser = srAcceptedUser;
  }

  public String getSrErrorDetails() {
    return this.srErrorDetails;
  }

  public void setSrErrorDetails(final String srErrorDetails) {
    this.srErrorDetails = srErrorDetails;
  }

  public String getSrResult() {
    return this.srResult;
  }

  public void setSrResult(final String srResult) {
    this.srResult = srResult;
  }

  /**
   * @return the reviewScore
   */
  public String getReviewScore() {
    return this.reviewScore;
  }


  /**
   * @param reviewScore the reviewScore to set
   */
  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;
  }


  /**
   * @return the compliResult
   */
  public String getCompliResult() {
    return this.compliResult;
  }


  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
  }


  /**
   * @return the labObjId
   */
  public Long getLabObjId() {
    return this.labObjId;
  }


  /**
   * @param labObjId the labObjId to set
   */
  public void setLabObjId(final Long labObjId) {
    this.labObjId = labObjId;
  }


  /**
   * @return the revId
   */
  public Long getRevId() {
    return this.revId;
  }


  /**
   * @param revId the revId to set
   */
  public void setRevId(final Long revId) {
    this.revId = revId;
  }


  /**
   * @return the compliLabObjId
   */
  public Long getCompliLabObjId() {
    return this.compliLabObjId;
  }


  /**
   * @param compliLabObjId the compliLabObjId to set
   */
  public void setCompliLabObjId(final Long compliLabObjId) {
    this.compliLabObjId = compliLabObjId;
  }


  /**
   * @return the compliRevId
   */
  public Long getCompliRevId() {
    return this.compliRevId;
  }


  /**
   * @param compliRevId the compliRevId to set
   */
  public void setCompliRevId(final Long compliRevId) {
    this.compliRevId = compliRevId;
  }

  public String getSecondaryResult() {
    return this.secondaryResult;
  }

  public void setSecondaryResult(final String secondaryResult) {
    this.secondaryResult = secondaryResult;
  }


  /**
   * @return the secondaryResultState
   */
  public String getSecondaryResultState() {
    return this.secondaryResultState;
  }


  /**
   * @param secondaryResultState the secondaryResultState to set
   */
  public void setSecondaryResultState(final String secondaryResultState) {
    this.secondaryResultState = secondaryResultState;
  }

  public Set<TRvwParametersSecondary> getTRvwParametersSecondaries() {
    return this.TRvwParametersSecondaries;
  }

  public void setTRvwParametersSecondaries(final Set<TRvwParametersSecondary> tRvwParametersSecondaries) {
    this.TRvwParametersSecondaries = tRvwParametersSecondaries;
  }


  /**
   * @return the tRvwWpResp
   */
  public TRvwWpResp gettRvwWpResp() {
    return this.tRvwWpResp;
  }


  /**
   * @param tRvwWpResp the tRvwWpResp to set
   */
  public void settRvwWpResp(final TRvwWpResp tRvwWpResp) {
    this.tRvwWpResp = tRvwWpResp;
  }

  /**
   * @return the qssdResult
   */
  public String getQssdResult() {
    return this.qssdResult;
  }


  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final String qssdResult) {
    this.qssdResult = qssdResult;
  }


  /**
   * @return the qssdLabObjId
   */
  public Long getQssdLabObjId() {
    return this.qssdLabObjId;
  }


  /**
   * @param qssdLabObjId the qssdLabObjId to set
   */
  public void setQssdLabObjId(final Long qssdLabObjId) {
    this.qssdLabObjId = qssdLabObjId;
  }


  /**
   * @return the qssdRevId
   */
  public Long getQssdRevId() {
    return this.qssdRevId;
  }


  /**
   * @param qssdRevId the qssdRevId to set
   */
  public void setQssdRevId(final Long qssdRevId) {
    this.qssdRevId = qssdRevId;
  }

  /**
   * @return the maturityLvl
   */
  public String getMaturityLvl() {
    return this.maturityLvl;
  }


  /**
   * @param maturityLvl the maturityLvl to set
   */
  public void setMaturityLvl(final String maturityLvl) {
    this.maturityLvl = maturityLvl;
  }

  public String getReadOnlyParamFlag() {
    return this.readOnlyParamFlag;
  }

  public void setReadOnlyParamFlag(final String readOnlyParamFlag) {
    this.readOnlyParamFlag = readOnlyParamFlag;
  }


  /**
   * @return the arcReleasedFlag
   */
  public String getArcReleasedFlag() {
    return this.arcReleasedFlag;
  }


  /**
   * @param arcReleasedFlag the arcReleasedFlag to set
   */
  public void setArcReleasedFlag(final String arcReleasedFlag) {
    this.arcReleasedFlag = arcReleasedFlag;
  }
}