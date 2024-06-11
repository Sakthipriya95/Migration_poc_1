/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.WPFinishRvwDet;

/**
 * Fetch the review details from Database and create the data objects
 *
 * @author bru2cob
 */
// ICDM-1698
public class CDRReportLoader extends AbstractSimpleBusinessObject {

  /**
   * Max items to be considered for IN clause in SQL query. if the number items is greater, then separate query(s) will
   * be executed with next set of items
   */
  private static final int MAX_IN_CLAUSE_SIZE = 800;
  /**
   * Parameter reviews query 1 input for check value
   */
  private static final int QRIIDX_RVW1_FETCH_CHECKVAL = 1;
  /**
   * Parameter reviews query 1 input index - Project ID
   */
  private static final int QRIIDX_RVW1_PROJECT_ID = 2;

  /**
   * Parameter reviews query 1 input index - Project Version ID
   */
  private static final int QROIDX_RVW1_PIDVERS_ID = 3;

  /**
   * Parameter reviews query 1 input index - maximum results
   */
  private static final int QRIIDX_RVW1_MAX_RESULTS = 4;

  /*
   * Parameter result Query 2 - Input
   */
  /**
   * Parameter reviews query 1 input for check value
   */
  private static final int QRIIDX_RVW2_FETCH_CHECKVAL = 1;
  /**
   * Parameter reviews query 1 input index - Variant ID
   */
  private static final int QRIIDX_RVW2_VARIANT_ID = 2;
  /**
   * Parameter reviews query 1 input index - Project ID
   */
  private static final int QRIIDX_RVW2_PROJECT_ID = 3;
  /**
   * Parameter reviews query 1 input index - Project Version ID
   */
  private static final int QROIDX_RVW2_PIDVERS_ID = 4;
  /**
   * Parameter reviews query 1 input index - maximum results
   */
  private static final int QRIIDX_RVW2_MAX_RESULTS = 5;


  /*
   * Parameter result Query - Output
   */

  /**
   * Parameter reviews query result index - Result ID
   */
  private static final int QROIDX_RVW_RESULT_ID = 0;

  /**
   * Parameter reviews query result index - Result Date
   */
  private static final int QROIDX_RVW_DATE = 1;
  /**
   * Parameter reviews query result index - Result Source type
   */
  private static final int QROIDX_RVW_SOURCE_TYPE = 2;
  /**
   * Parameter reviews query result index - Result Description
   */
  private static final int QROIDX_RVW_DESC = 3;
  /**
   * Parameter reviews query result index - Result - Is delta review
   */
  private static final int QROIDX_RVW_DELTA_RVW = 5;
  /**
   * Parameter reviews query result index - Result - Pidc Version name
   */
  private static final int QROIDX_RVW_PIDCVERS_NAME = 6;
  /**
   * Parameter reviews query result index - Result PIDC Version ID
   */
  private static final int QROIDX_RVW_PIDVERS_ID = 7;
  /**
   * Parameter reviews query result index - Result A2l File ID
   */
  private static final int QROIDX_RVW_A2L_FILE_ID = 8;
  /**
   * Parameter reviews query result index - Result A2L file name
   */
  private static final int QROIDX_RVW_A2L_FILE_NAME = 9;
  /**
   * Parameter reviews query result index - Result SDOM PVER Variant
   */
  private static final int QROIDX_RVW_SDOMPVER_VARIANT = 10;
  /**
   * Parameter reviews query result index - Parameter name
   */
  private static final int QROIDX_PRM_NAME = 11;
  /**
   * Parameter reviews query result index - Ready for series
   */
  private static final int QROIDX_PRMRVW_METHOD = 13;
  /**
   * Parameter reviews query result index - Review result
   */
  private static final int QROIDX_PRMRVW_RESULT = 14;
  /**
   * Parameter reviews query result index - Exact match flag
   */
  private static final int QROIDX_PRMRVW_MATCH_REF_FLAG = 15;
  /**
   * Parameter reviews query result index - Review hint
   */
  private static final int QROIDX_PRMRVW_HINT = 16;
  // ICDM-1839
  /**
   * Parameter reviews query result index - Review latest function id
   */
  private static final int QROIDX_RVW_FUNC_ID = 17;
  /**
   * Parameter reviews query result index - Review Input file
   */
  private static final int QROIDX_RVW_FILE_ID = 18;
  /**
   * Parameter reviews query result index - Review Input file
   */
  private static final int QROIDX_RVW_CHECK_VAL = 19;
  /**
   * Parameter reviews query result index - Review Input file
   */
  private static final int QROIDX_RVW_REVEW_FLAG = 20;

  /**
   * Parameter reviews query result index - Review Input file
   */
  private static final int QROIDX_RVW_REVEW_COMMENT = 21;
  /**
   * Parameter reviews query result index - sr_result
   */
  private static final int QROIDX_RVW_SR_RESULT = 22;
  /**
   * Parameter reviews query result index - sr_error details
   */
  private static final int QROIDX_RVW_SR_ERR_DETAILS = 23;
  /**
   * Parameter reviews query result index - sr_accepted_flag
   */
  private static final int QROIDX_RVW_SR_ACCEPTED_FLAG = 24;
  /**
   * Parameter reviews query result index - sr_accepted_user
   */
  private static final int QROIDX_RVW_SR_ACCEPTED_USER = 25;
  /**
   * Parameter reviews query result index - sr_accepted_date
   */
  private static final int QROIDX_RVW_SR_ACCEPTED_DATE = 26;
  /**
   * Parameter reviews query result index - review var name
   */
  private static final int QROIDX_RVW_VAR_NAME = 27;

  /**
   * Parameter reviews query result index - review var name
   */
  private static final int QROIDX_RVW_CREATED_USER = 28;

  /**
   * Parameter reviews query result index - review type
   */
  // ICDM-2584
  private static final int QROIDX_RVW_TYPE = 29;

  /**
   * Parameter reviews query result index - lock status
   */
  // ICDM-2584
  private static final int QROIDX_LOCK_STATUS = 30;
  /**
   * Parameter reviews query result index - Parameter id
   */
  private static final int QROIDX_PRM_ID = 31;
  /**
   * Parameter reviews query result index - ARC Release Flag
   */
  private static final int QROIDX_PRM_ARC_FLAG = 32;

  /**
   * Parameter reviews query result index - Review parameter ID
   */
  private static final int QROIDX_RVW_PARAM_ID = 33;
  /*
   * Review File Query - Input
   */
  /**
   * Review File query result input key - Result ID List
   */
  private static final String QRIIDX_RVWFILE_RESULT_ID = "resultIDList";
  /**
   * Review File query result input key - File Type
   */
  private static final String QRIIDX_RVWFILE_FILE_TYPE = "fileType";

  /*
   * Review File Query - Output
   */
  /**
   * Review File query result index - Result ID
   */
  private static final int QROIDX_RVWFILE_RESULT_ID = 0;
  /**
   * Review File query result index - Review File ID
   */
  private static final int QROIDX_RVWFILE_ID = 1;
  /**
   * Review File query result index - File name
   */
  private static final int QROIDX_RVWFILE_NAME = 2;

  /**
   * Report data
   */
  private final CdrReport reportData = new CdrReport();


  /**
   * Constructor
   *
   * @param serviceData service data
   */
  public CDRReportLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Fecthes the params of a2l which has been reviewed, no variant.
   *
   * @param cdrRprtData - wrapper class with data needed for generating Data Review Report
   * @return Cdr Report
   * @throws IcdmException error while preparing data
   */
  public CdrReport fetchCDRReportData(final CDRReportData cdrRprtData) throws IcdmException {

    getLogger().debug("Invoking fetch cdr report data");
    return doFetchCDRReportData(cdrRprtData);
  }


  /**
   * Fetch the data
   *
   * @param CDRReportData CDR Report data
   * @return Cdr Report
   * @throws DataException
   */
  private CdrReport doFetchCDRReportData(final CDRReportData cdrRprtData) throws IcdmException {
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(cdrRprtData.getPidcA2lId());
    Long a2lFileId = pidcA2l.getA2lFileId();
    Long pidcVerID = pidcA2l.getPidcVersId();
    Long projectID = pidcA2l.getProjectId();

    cdrRprtData.setA2lFileId(a2lFileId);
    fetchParamProps(cdrRprtData);

    EntityManager entMgr = getEntMgr();

    this.reportData.setPidcVersion(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVerID));
    this.reportData.setPidcA2l(pidcA2l);
    this.reportData.setA2lFile(new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFileId));
    this.reportData.setConsiderReviewsOfPrevPidcVers(new PidcLoader(getServiceData()).getDataObjectByID(projectID).isInclRvwOfOldVers());

    try {
      entMgr.getTransaction().begin();

      cdrRprtData.setA2lFileId(a2lFileId);
      fillTempTable(entMgr, cdrRprtData);

      TypedQuery<Object[]> reportQry = createReportQry(entMgr, projectID, pidcVerID, cdrRprtData.getVarId(),
          cdrRprtData.getMaxResults(), cdrRprtData.isToFetchCheckVal());


      List<Object[]> queryRsltList = reportQry.getResultList();
      List<Object[]> filtrdRsltList = getFilteredParamsForWpResp(cdrRprtData, queryRsltList);

      fillReviewDetails(filtrdRsltList);
      fillWpRespStatus(cdrRprtData);
    }
    finally {
      entMgr.getTransaction().rollback();
    }

    return this.reportData;
  }

  /**
   * @param cdrRprtData
   * @param queryRsltList
   * @return List<Object[]>
   * @throws IcdmException
   */
  private List<Object[]> getFilteredParamsForWpResp(final CDRReportData cdrRprtData, final List<Object[]> queryRsltList)
      throws IcdmException {
    List<Object[]> filtrdRsltList = new ArrayList<>();

    if (CommonUtils.isNotNull(cdrRprtData.getA2lRespId())) {
      List<Long> paramListWithSelRespAndWp =
          new ParameterLoader(getServiceData()).getParamListByA2lRespAndWP(cdrRprtData);
      // Filter the resultant List to get only data for parameters with selected WP and Resp
      for (Object[] obj : queryRsltList) {
        if (paramListWithSelRespAndWp.contains(Long.valueOf(obj[QROIDX_PRM_ID].toString()))) {
          filtrdRsltList.add(obj);
        }
      }
      this.reportData.setToGenDataRvwRprtForWPResp(true);
    }
    else {
      // If the report generation call is from A2l, then pass the complete list
      filtrdRsltList.addAll(queryRsltList);
    }
    return filtrdRsltList;
  }

  /**
   * @param cdrRprtData
   * @throws DataException
   */
  private void fillWpRespStatus(final CDRReportData cdrRprtData) throws DataException {
    // Get Active Wp Definition Version Id
    long activeWpDefnVersId = new A2lWpDefnVersionLoader(getServiceData())
        .getActiveA2lWPDefnVersionEntityFromA2l(cdrRprtData.getPidcA2lId()).getWpDefnVersId();
    // Get the A2l Wp Resp Status corresponding to Variant ID and Active Wp Definition Version ID
    Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpStatusMap = new A2lWpResponsibilityStatusLoader(getServiceData())
        .getA2lWpStatusByVarAndWpDefnVersId(cdrRprtData.getVarId(), activeWpDefnVersId);
    this.reportData.setWpIdRespIdAndStatusMap(wpStatusMap);
  }

  /**
   * Create report query
   *
   * @param entMgr
   * @param a2lFileId
   * @param projectID
   * @param pidcVerID
   * @param variantID
   * @param maxResults
   * @return
   * @throws DataException
   */
  private TypedQuery<Object[]> createReportQry(final EntityManager entMgr, final Long projectID, final Long pidcVerID,
      final Long variantID, final int maxResults, final boolean fetchCheckVal)
      throws DataException {

    Pidc pidc = new PidcLoader(getServiceData()).getDataObjectByID(projectID);
    TypedQuery<Object[]> reportQry;
    int checkVal = 0;
    if (fetchCheckVal) {
      checkVal = 1;
    }
    if ((variantID == null) || (variantID < 0)) {
      if (pidc.isInclRvwOfOldVers()) {
        reportQry = entMgr.createNamedQuery(TRvwResult.NNQ_GET_PRM_RVW_NO_VARIANT, Object[].class);
      }
      else {
        reportQry =
            entMgr.createNamedQuery(TRvwResult.NNQ_GET_PRM_RVW_NO_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS, Object[].class);
      }
      reportQry.setParameter(QRIIDX_RVW1_FETCH_CHECKVAL, checkVal).setParameter(QRIIDX_RVW1_PROJECT_ID, projectID)
          .setParameter(QROIDX_RVW1_PIDVERS_ID, pidcVerID).setParameter(QRIIDX_RVW1_MAX_RESULTS, maxResults);
    }
    else {
      if (pidc.isInclRvwOfOldVers()) {
        reportQry = entMgr.createNamedQuery(TRvwResult.NNQ_GET_PRM_RVW_WITH_VARIANT, Object[].class);
      }
      else {
        reportQry =
            entMgr.createNamedQuery(TRvwResult.NNQ_GET_PRM_RVW_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS, Object[].class);
      }

      reportQry.setParameter(QRIIDX_RVW2_FETCH_CHECKVAL, checkVal).setParameter(QRIIDX_RVW2_VARIANT_ID, variantID)
          .setParameter(QRIIDX_RVW2_PROJECT_ID, projectID).setParameter(QROIDX_RVW2_PIDVERS_ID, pidcVerID)
          .setParameter(QRIIDX_RVW2_MAX_RESULTS, maxResults);
    }

    return reportQry;
  }

  /**
   * Fetch the basic properties of parameter and create ParameterDetails objects
   *
   * @param cdrRprtData A2l File ID
   * @throws IcdmException
   */
  private void fetchParamProps(final CDRReportData cdrRprtData) throws IcdmException {
    this.reportData.setParamPropsMap(new ParameterLoader(getServiceData()).fetchA2lWpRespParamProps(cdrRprtData));
  }

  /**
   * Fill the temporary table GttTempTable with the a2l parameter details
   *
   * @param entMgr Entity Manager
   * @param cdrRprtData A2L file ID
   */
  private void fillTempTable(final EntityManager entMgr, final CDRReportData cdrRprtData) {
    entMgr.createNamedQuery(GttObjectName.NNS_INS_TEMP_TABLE_A2L_PARAMS).setParameter(1, cdrRprtData.getA2lFileId())
        .executeUpdate();
  }

  /**
   * Fill data review details
   *
   * @param resultList report query
   * @throws DataException
   */
  private void fillReviewDetails(final List<Object[]> resultList) throws DataException {
    Map<Long, String> paramwithARCReleaseFlagMap = new HashMap<>();
    Long resultId;
    int maxParamReviewCount = 0;

    for (Object[] resObj : resultList) {

      fillARCReleaseValue(paramwithARCReleaseFlagMap, resObj);

      // first obj is result id
      resultId = ((BigDecimal) resObj[QROIDX_RVW_RESULT_ID]).longValue();

      // create review details
      if (!this.reportData.getReviewDetMap().containsKey(resultId)) {
        ReviewDetails reviewData = new ReviewDetails();

        reviewData.setRvwID(resultId);
        reviewData.setRvwDate(timestamp2String((Timestamp) resObj[QROIDX_RVW_DATE]));
        reviewData.setSourceType((String) resObj[QROIDX_RVW_SOURCE_TYPE]);
        reviewData.setRvwDesc((String) resObj[QROIDX_RVW_DESC]);
        reviewData.setDeltaReview((String) resObj[QROIDX_RVW_DELTA_RVW]);
        reviewData.setPidcVersName((String) resObj[QROIDX_RVW_PIDCVERS_NAME]);
        Long pidcVersionId = ((BigDecimal) resObj[QROIDX_RVW_PIDVERS_ID]).longValue();
        reviewData.setPidcVersID(pidcVersionId);
        reviewData.setA2lFileID(((BigDecimal) resObj[QROIDX_RVW_A2L_FILE_ID]).longValue());
        reviewData.setA2lFileName((String) resObj[QROIDX_RVW_A2L_FILE_NAME]);
        reviewData.setSdomPverVariant((String) resObj[QROIDX_RVW_SDOMPVER_VARIANT]);
        reviewData.setVariantName((String) resObj[QROIDX_RVW_VAR_NAME]);
        reviewData.setCreatedUser((String) resObj[QROIDX_RVW_CREATED_USER]);

        // ICDM-2584
        reviewData.setReviewType((String) resObj[QROIDX_RVW_TYPE]);
        // ICDM-2584
        reviewData.setLockStatus((String) resObj[QROIDX_LOCK_STATUS]);
        this.reportData.getReviewDetMap().put(resultId, reviewData);

        // Add review result object
        this.reportData.getCdrReviewResultMap().put(resultId,
            new CDRReviewResultLoader(getServiceData()).getDataObjectByID(resultId));

        // Add PIDC A2L object
        CDRReviewResult result = this.reportData.getCdrReviewResultMap().get(resultId);
        Long pidcA2lId = result.getPidcA2lId();
        if (!this.reportData.getReviewDetA2lMap().containsKey(pidcA2lId)) {
          this.reportData.getReviewDetA2lMap().put(pidcA2lId,
              new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId));
        }

        // Add PIDC version object
        if (!this.reportData.getReviewDetPidcVersMap().containsKey(pidcVersionId)) {
          this.reportData.getReviewDetPidcVersMap().put(pidcVersionId,
              new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersionId));
        }
      }

      String paramName = (String) resObj[QROIDX_PRM_NAME];

      // create param details
      List<ParameterReviewDetails> paramRvwDetList = this.reportData.getParamRvwDetMap().get(paramName);
      if (paramRvwDetList == null) {
        paramRvwDetList = new ArrayList<>();
        this.reportData.getParamRvwDetMap().put(paramName, paramRvwDetList);
      }

      // create param review details
      ParameterReviewDetails paramReviewDetails = getParamRvwDetails(resultId, resObj);
      paramRvwDetList.add(paramReviewDetails);

      if (paramRvwDetList.size() > maxParamReviewCount) {
        maxParamReviewCount = paramRvwDetList.size();
      }
    }

    if (!this.reportData.getReviewDetMap().isEmpty()) {
      loadReviewFiles();
    }
    // ICDM-1839
    if (!this.reportData.getRvwFuncMap().isEmpty()) {
      loadLatestFuncVer();
    }
    this.reportData.setParamwithARCReleaseFlagMap(paramwithARCReleaseFlagMap);
    this.reportData.setMaxParamReviewCount(maxParamReviewCount);
  }

  /**
   * @param paramwithARCReleaseFlagMap
   * @param resObj
   */
  private void fillARCReleaseValue(final Map<Long, String> paramwithARCReleaseFlagMap, final Object[] resObj) {
    Long paramId = Long.valueOf(resObj[QROIDX_PRM_ID].toString());
    String arcReleaseFlag = (String) resObj[QROIDX_PRM_ARC_FLAG];
    if (CommonUtils.isNull(arcReleaseFlag)) {
      arcReleaseFlag = ApicConstants.CODE_NO;
    }
    paramwithARCReleaseFlagMap.put(paramId, arcReleaseFlag);
  }

  /**
   * @param resultId
   * @param resObj
   * @return
   */
  private ParameterReviewDetails getParamRvwDetails(final Long resultId, final Object[] resObj) {
    ParameterReviewDetails paramReviewDetails = new ParameterReviewDetails();

    paramReviewDetails.setRvwMethod((String) resObj[QROIDX_PRMRVW_METHOD]);
    paramReviewDetails.setReviewResult((String) resObj[QROIDX_PRMRVW_RESULT]);
    paramReviewDetails.setMatchRefFlag((String) resObj[QROIDX_PRMRVW_MATCH_REF_FLAG]);
    paramReviewDetails.setHint((String) resObj[QROIDX_PRMRVW_HINT]);
    paramReviewDetails.setRvwID(resultId);
    // ICDM-1839
    BigDecimal rvwFuncID = (BigDecimal) resObj[QROIDX_RVW_FUNC_ID];
    if (null != rvwFuncID) {
      paramReviewDetails.setFuncID(rvwFuncID.longValue());
      this.reportData.getRvwFuncMap().put(rvwFuncID.longValue(), "");
    }
    BigDecimal rvwFileID = (BigDecimal) resObj[QROIDX_RVW_FILE_ID];
    if (null != rvwFileID) {
      paramReviewDetails.setRvwFileID(rvwFileID.longValue());
    }

    // ICDM-1723
    paramReviewDetails.setCheckedVal((byte[]) resObj[QROIDX_RVW_CHECK_VAL]);
    paramReviewDetails.setReviewScore((String) resObj[QROIDX_RVW_REVEW_FLAG]);
    paramReviewDetails.setRvwComment((String) resObj[QROIDX_RVW_REVEW_COMMENT]);

    paramReviewDetails.setSrResult((String) resObj[QROIDX_RVW_SR_RESULT]);
    paramReviewDetails.setSrErrorDetails((String) resObj[QROIDX_RVW_SR_ERR_DETAILS]);
    paramReviewDetails.setSrAcceptedFlag((String) resObj[QROIDX_RVW_SR_ACCEPTED_FLAG]);
    paramReviewDetails.setSrAcceptedDate((Timestamp) resObj[QROIDX_RVW_SR_ACCEPTED_DATE]);
    paramReviewDetails.setSrAcceptedUser((String) resObj[QROIDX_RVW_SR_ACCEPTED_USER]);

    BigDecimal rvwParamID = (BigDecimal) resObj[QROIDX_RVW_PARAM_ID];
    if (rvwParamID != null) {
      paramReviewDetails.setRvwParameterId(rvwParamID.longValue());
    }

    return paramReviewDetails;
  }

  /**
   * Load reviewed files to the report data
   *
   * @param entMgr EntityManager to use
   */
  private void loadReviewFiles() {

    Long resultID;
    long rvwFileID;
    String fileName;
    int endIndex;
    int startIndex = 0;
    List<Long> rvwIdSubList;
    TypedQuery<Object[]> fileQry;

    List<Long> rvwIDList = new ArrayList<>(this.reportData.getReviewDetMap().keySet());

    // IN clause query is used. The below iteriation is to limit the number of keys in IN clause to less than 1000
    while (startIndex < (rvwIDList.size())) {
      endIndex = startIndex + MAX_IN_CLAUSE_SIZE;
      if (endIndex > (rvwIDList.size())) {
        endIndex = rvwIDList.size();
      }

      rvwIdSubList = rvwIDList.subList(startIndex, endIndex);

      fileQry = getEntMgr().createNamedQuery(TRvwFile.NQ_GET_FILES_BY_RESULT_AND_TYPE, Object[].class);
      fileQry.setParameter(QRIIDX_RVWFILE_RESULT_ID, rvwIdSubList).setParameter(QRIIDX_RVWFILE_FILE_TYPE, "I");

      for (Object[] resultRow : fileQry.getResultList()) {
        resultID = (Long) resultRow[QROIDX_RVWFILE_RESULT_ID];
        rvwFileID = (Long) resultRow[QROIDX_RVWFILE_ID];
        fileName = (String) resultRow[QROIDX_RVWFILE_NAME];
        this.reportData.getReviewDetMap().get(resultID).getRvwInputFileMap().put(rvwFileID, fileName);
      }

      startIndex = endIndex;

    }

  }

  // ICDM-1839
  /**
   * Load function verson to the report data
   *
   * @param entMgr EntityManager to use
   */
  private void loadLatestFuncVer() {
    long rvwFunID;
    String funcVer;
    int endIndex;
    int startIndex = 0;
    List<Long> funSubList;
    List<Long> funcIDList;
    TypedQuery<Object[]> fileQry;


    funcIDList = new ArrayList<>(this.reportData.getRvwFuncMap().keySet());
    // IN clause query is used. The below iteriation is to limit the number of keys in IN clause to less than 1000
    while (startIndex < (funcIDList.size())) {
      endIndex = startIndex + MAX_IN_CLAUSE_SIZE;
      if (endIndex > (funcIDList.size())) {
        endIndex = funcIDList.size();
      }

      funSubList = funcIDList.subList(startIndex, endIndex);
      fileQry = getEntMgr().createNamedQuery(TRvwFunction.GET_FUN_VER_BY_ID, Object[].class);
      fileQry.setParameter("rvwFuncID", funSubList);

      for (Object[] resultRow : fileQry.getResultList()) {
        funcVer = (String) resultRow[QROIDX_RVWFILE_RESULT_ID];
        rvwFunID = (Long) resultRow[QROIDX_RVWFILE_ID];
        if (null != funcVer) {
          this.reportData.getRvwFuncMap().put(rvwFunID, funcVer);
        }
      }

      startIndex = endIndex;

    }

  }

  /**
   * Fetch the data
   *
   * @param CDRReportData CDR Report data
   * @return Cdr Report
   * @throws DataException
   */
  public Map<Long, WPFinishRvwDet> fetchRvwDataForA2lWpRespParam(final Long projectID, final Long pidcVerID,
      final Long variantID, final Long pidcA2lID, final List<Long> paramListWithSelRespAndWp)
      throws IcdmException {

    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lID);
    Long a2lFileId = pidcA2l.getA2lFileId();
    Map<Long, WPFinishRvwDet> paramWPFinishRvwDetMap = new HashMap<>();

    EntityManager entMgr = getEntMgr();


    try {
      entMgr.getTransaction().begin();

      // fill temporary table
      entMgr.createNamedQuery(GttObjectName.NNS_INS_TEMP_TABLE_A2L_PARAMS).setParameter(1, a2lFileId).executeUpdate();

      TypedQuery<Object[]> reportQry = createReportQry(entMgr, projectID, pidcVerID, variantID, 1, false);


      List<Object[]> queryRsltList = reportQry.getResultList();
      List<Object[]> filtrdRsltList = new ArrayList<>();

      // Filter the resultant List to get only data for parameters with selected WP and Resp
      for (Object[] obj : queryRsltList) {
        if (paramListWithSelRespAndWp.contains(Long.valueOf(obj[QROIDX_PRM_ID].toString()))) {
          filtrdRsltList.add(obj);
        }
      }


      paramWPFinishRvwDetMap = fillWPFinishRvwDet(filtrdRsltList);
    }
    finally {
      entMgr.getTransaction().rollback();
    }

    return paramWPFinishRvwDetMap;
  }

  public Map<Long, WPFinishRvwDet> fillWPFinishRvwDet(final List<Object[]> filtrdRsltList) {
    Map<Long, WPFinishRvwDet> paramWPFinishRvwDetMap = new HashMap<>();
    for (Object[] resObj : filtrdRsltList) {
      WPFinishRvwDet wpFinishRvwDet = new WPFinishRvwDet();

      long paramID = ((BigDecimal) resObj[QROIDX_PRM_ID]).longValue();
      paramWPFinishRvwDetMap.put(paramID, wpFinishRvwDet);
      wpFinishRvwDet.setParamId(paramID);
      // first obj is result id
      wpFinishRvwDet.setResultId(((BigDecimal) resObj[QROIDX_RVW_RESULT_ID]).longValue());

      // ICDM-2584
      wpFinishRvwDet.setRvwType((String) resObj[QROIDX_RVW_TYPE]);
      // ICDM-2584
      wpFinishRvwDet.setLockStatus((String) resObj[QROIDX_LOCK_STATUS]);
      wpFinishRvwDet.setRvwScore((String) resObj[QROIDX_RVW_REVEW_FLAG]);
      wpFinishRvwDet.setArcReleaseFlag((String) resObj[QROIDX_PRM_ARC_FLAG]);

    }
    return paramWPFinishRvwDetMap;
  }
}