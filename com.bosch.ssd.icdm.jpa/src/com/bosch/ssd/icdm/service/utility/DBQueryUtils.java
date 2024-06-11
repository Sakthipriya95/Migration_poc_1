/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.Session;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.common.utility.DBResultUtil;
import com.bosch.ssd.icdm.constants.JPAParameterKeyConstants;
import com.bosch.ssd.icdm.constants.JPAQueryConstants;
import com.bosch.ssd.icdm.entity.TempNonSDOMNodeList;
import com.bosch.ssd.icdm.entity.TempRuleIdOEM;
import com.bosch.ssd.icdm.entity.VLdb2ProjectRelease;
import com.bosch.ssd.icdm.entity.VLdb2ProjectRevision;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.OEMRuleDescriptionInput;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDConfigParams;
import com.bosch.ssd.icdm.model.utils.DBEntityCreationUtil;

/**
 * This class is used to create and manage all Database queries.
 *
 * @author HSU4COB
 */
//TODO for find/persist/flush - Time taken to be updated
public class DBQueryUtils {

  private final EntityManager entityManager;
  private final String userName;

  /**
   * Initialize DBQueryUtils with Entity Manager
   *
   * @param em       EntityManager
   * @param userName UserName
   */
  public DBQueryUtils(final EntityManager em, final String userName) {
    this.entityManager = em;
    this.userName = userName;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @return the entityManager
   */
  public EntityManager getEntityManager() {
    return this.entityManager;
  }


  /**
   * Create Named Query and retrieve the list of rules for the provided release ID
   *
   * @param releaseId       proRelID
   * @param isCompliRelease boolean
   * @return List of rules
   * @throws SSDiCDMInterfaceException exception
   */
  public List<Object[]> getRulesForRelease(final BigDecimal releaseId, final boolean isCompliRelease)
      throws SSDiCDMInterfaceException {
    try {
      String namedQuery = JPAQueryConstants.V_LDB2_SSD2_GET_RULES_FOR_RELEASE;
      // if it is for compli release, use the respective named query
      if (isCompliRelease) {
        namedQuery = JPAQueryConstants.V_LDB2_SSD2_GET_RULES_FOR_COMPLI_RELEASE;
      }
      Query query = this.entityManager.createNamedQuery(namedQuery);
      query.setParameter(1, releaseId);
      return DBResultUtil.getQueryObjArrList(query, namedQuery);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create name query and retrieve list of realease by Software Version Id
   *
   * @param swVersionId version id
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<Object[]> getReleaseBySWVersion(final BigDecimal swVersionId) throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_GET_RELEASE_BY_SW_VERSION);
      query.setParameter(1, swVersionId);
      return DBResultUtil.getQueryObjArrList(query,
          JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_GET_RELEASE_BY_SW_VERSION);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create named query and retrieve feature value for the release id
   *
   * @param releaseId release id
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> getFeaValForRelease(final BigDecimal releaseId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_FEATURE_VALUE);

      query.setParameter(1, releaseId);
      SSDiCDMInterfaceLogger.logMessage("Fetching Feature Value for Release ID : " + releaseId,
          ILoggerAdapter.LEVEL_DEBUG, null);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_FEATURE_VALUE);

    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create named query and retrieve feature value for the release id for Processing Release
   *
   * @param releaseId release id
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> getFeaValForReleaseProcess(final BigDecimal releaseId) throws SSDiCDMInterfaceException {
    try {

      Query query = this.entityManager.createNamedQuery("VLdb2ProjectRelease.getFeaValCompleteForRelease");
      query.setParameter(1, releaseId);
      SSDiCDMInterfaceLogger.logMessage("Fetching Feature Value for Release ID : " + releaseId,
          ILoggerAdapter.LEVEL_DEBUG, null);

      return DBResultUtil.getQueryObjArrList(query, "VLdb2ProjectRelease.getFeaValCompleteForRelease");
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create Named Query and retrieve value for label
   *
   * @param labelName labelname
   * @param ssdNodeId ssdnodeid
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<Object[]> getRuleHistoryForLabel(final String labelName, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_LABEL);
      query.setParameter(1, ssdNodeId);
      query.setParameter(2, labelName.toUpperCase(Locale.ENGLISH));
      query.setHint(QueryHints.REFRESH, HintValues.TRUE);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_LABEL);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create Named Query and retrieve value for rule
   *
   * @param rule      CDRRule
   * @param ssdNodeId ssdnodeid
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<Object[]> getRuleHistoryForRule(final CDRRule rule, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_CDR_RULE);
      query.setParameter(1, ssdNodeId);
      query.setParameter(2, rule.getParameterName().toUpperCase(Locale.ENGLISH));
      query.setParameter(3, rule.getRuleId());
      query.setHint(QueryHints.REFRESH, HintValues.TRUE);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_CDR_RULE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Create Named Query and retrieve value for rule
   *
   * @param rule CDRRule
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> getRuleHistoryForCompliRule(final CDRRule rule) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_SSD2_HISTORY_COMPLI_RULE_FOR_CDR_RULE);
      query.setParameter(1, rule.getParameterName().toUpperCase(Locale.ENGLISH));
      query.setParameter(2, rule.getRuleId());
      query.setHint(QueryHints.REFRESH, HintValues.TRUE);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_LDB2_SSD2_HISTORY_COMPLI_RULE_FOR_CDR_RULE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /*
   * Methods fo SSD Interface reports
   */

  /**
   * Method used to get the details for the release id and store in temp table for ssd file creation
   *
   * @param releaseId  - id of the release
   * @param ruleIdFlag Rule id
   * @return - primary id
   * @throws SSDiCDMInterfaceException Exception
   */
  public String dataForSSD(final BigDecimal releaseId, final boolean ruleIdFlag) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_SSD_FILE_SSD_FILE_DATA);
      query.setParameter(JPAParameterKeyConstants.PRO_REL_ID, releaseId);
      query.setParameter("pc_ssd", SSDCase.SSD.getCharacter());
      query.setParameter("pc_mft", SSDCase.MFT.getCharacter());
      query.setParameter("pc_appchK", SSDCase.APPCHK.getCharacter());
      query.setParameter("pc_comp", SSDCase.CAL4COMP.getCharacter());
      query.setParameter("refreshPb", "true");
      query.setParameter(JPAParameterKeyConstants.COMMENT_PB, "true");
      query.setParameter("pc_temporary", JPAParameterKeyConstants.FALSE);
      query.setParameter("labelSort", "true");
      query.setParameter(JPAParameterKeyConstants.USER_ID, this.userName);
      query.setParameter("commentReportComparsion", ruleIdFlag);
      return (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.TEMP_SSD_FILE_SSD_FILE_DATA);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method used to get the details for the release id and store in temp table for cdfx file creation
   *
   * @param releaseId - id of the release
   * @return - primary id
   * @throws SSDiCDMInterfaceException Exception
   */
  public String dataForCdfx(final BigDecimal releaseId) throws SSDiCDMInterfaceException { // SSD-272
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_SSD_FILE_CDF_REPORT_EXCEL);
      query.setParameter(JPAParameterKeyConstants.PRO_REL_ID, releaseId);
      query.setParameter("cdfminMaxCheck", "true");
      query.setParameter("ecutype", "EDC17");
      query.setParameter("chk_ssd", SSDCase.SSD.getCharacter());
      query.setParameter("chk_mft", SSDCase.MFT.getCharacter());
      query.setParameter("app_hint", SSDCase.APPCHK.getCharacter());
      query.setParameter("chk_cmpPck", SSDCase.CAL4COMP.getCharacter());
      query.setParameter("chk_compli", SSDCase.CSSD.getCharacter());
      query.setParameter("chk_ssd2rev", SSDCase.SSD2RV.getCharacter());// ALM-282853
      query.setParameter("chk_qssd", SSDCase.QSSD.getCharacter());// ALM-282853
      query.setParameter(JPAParameterKeyConstants.USER_ID, this.userName);
      return (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.TEMP_SSD_FILE_CDF_REPORT_EXCEL);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param primary_id - id
   * @return -list conatining the rule for the labels
   * @throws SSDiCDMInterfaceException Exception
   * @return-
   */
  public List<Object> getSSDFileData(final String primary_id) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_SSD_FILE_GET_SSD_FILE);
      query.setParameter(JPAParameterKeyConstants.UNIQUE_ID, primary_id);
      return DBResultUtil.getQueryObjList(query, JPAQueryConstants.TEMP_SSD_FILE_GET_SSD_FILE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method deletes the data from the temp table
   *
   * @param obj          - deletes the data from temporary data
   * @param reportCalled - which report is used
   * @throws SSDiCDMInterfaceException Exception
   */
  public void deleteTempReport(final String obj, final String reportCalled) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_SSD_FILE_DEL_WRITE_RELEASES_DATA);
      query.setParameter("cond", obj).setParameter("rptParam", reportCalled);
      DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.TEMP_SSD_FILE_DEL_WRITE_RELEASES_DATA);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to get the data from temp table for dcm
   *
   * @param primary_id - id
   * @return - list of dcm data
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object> getDcmListDtls(final String primary_id) throws SSDiCDMInterfaceException {// SSD-272
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_DCMDATALIST_GET_DCM_FILE_DATA);
      query.setParameter(JPAParameterKeyConstants.UNIQUE_ID, primary_id);
      return DBResultUtil.getQueryObjList(query, JPAQueryConstants.TEMP_DCMDATALIST_GET_DCM_FILE_DATA);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to get the data from temp table for cdfx
   *
   * @param primary_id - id
   * @return - list of cdfx data
   * @throws SSDiCDMInterfaceException E
   */
  public List<Object> getCdfListDtls(final String primary_id) throws SSDiCDMInterfaceException {// SSD-272
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_CDFDATALIST_GET_CDF_FILE_DATA);
      query.setParameter(JPAParameterKeyConstants.UNIQUE_ID, primary_id);
      return DBResultUtil.getQueryObjList(query, JPAQueryConstants.TEMP_CDFDATALIST_GET_CDF_FILE_DATA);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to get the data from temp table for error file
   *
   * @param primary_id - id
   * @return - list of error data for the labels
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object> getTempRelErrorLblData(final String primary_id) throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_RELEASES_ERROR_LBL_DTL_GET_TEMP_ERROR_LBL_DTLS);
      query.setParameter(JPAParameterKeyConstants.UNIQUE_ID, primary_id);
      return DBResultUtil.getQueryObjList(query, JPAQueryConstants.TEMP_RELEASES_ERROR_LBL_DTL_GET_TEMP_ERROR_LBL_DTLS);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method used to get the details for the release id and store in temp table for error details
   *
   * @param proRevId  - revision id
   * @param proRelId  - release id
   * @param ssdNodeId node id
   * @return - primary id
   * @throws SSDiCDMInterfaceException Exception
   */
  public String dataForError(final BigDecimal proRevId, final BigDecimal proRelId, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_SSD_FILE_SP_RELEASES_ERROR_TEMP);
      query.setParameter(JPAParameterKeyConstants.PRO_REL_ID, proRelId);
      query.setParameter("p_rev_id", proRevId);
      query.setParameter("sw_vers_id", ssdNodeId);
      query.setParameter("rev_id_list", "");
      query.setParameter("ecutype", "EDC17");
      query.setParameter("firstRule", "Y");
      query.setParameter(JPAParameterKeyConstants.USER_ID, this.userName);
      return (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.TEMP_SSD_FILE_SP_RELEASES_ERROR_TEMP);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Procedure gets the rule for the label
   *
   * @param nodeId - id of the node where rule defined
   * @param userId - id of the user
   * @return - primary id
   * @throws SSDiCDMInterfaceException Exception
   */
  public String ssdFileReportGeneration(final BigDecimal nodeId, final String userId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_LABELLIST_INTERFACE_GET_SSD_FILE);
      query.setParameter("pc_node_id", nodeId);
      query.setParameter(JPAParameterKeyConstants.COMMENT_PB, JPAParameterKeyConstants.FALSE);
      query.setParameter(JPAParameterKeyConstants.USER_ID, userId);
      return (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.TEMP_LABELLIST_INTERFACE_GET_SSD_FILE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Procedure gets the rule for the label for generating .ssd file with dependency
   *
   * @param nodeId - id of the node where rule defined
   * @param userId - id of the user
   * @return - primary id
   * @throws SSDiCDMInterfaceException Exception
   */
  public String ssdFileReportGenerationDependency(final BigDecimal nodeId, final String userId)
      throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_LABELLIST_INTERFACE_GET_SSD_FILE_DEPENDENCY);
      query.setParameter("pc_node_id", nodeId);
      query.setParameter(JPAParameterKeyConstants.COMMENT_PB, JPAParameterKeyConstants.FALSE);
      query.setParameter(JPAParameterKeyConstants.USER_ID, userId);
      return (String) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.TEMP_LABELLIST_INTERFACE_GET_SSD_FILE_DEPENDENCY);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * to insert lock for creation of labellist
   *
   * @param versId - sw versid
   * @param revId  - revid
   * @param uName  name
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public Integer insertLock(final BigDecimal versId, final BigDecimal revId, final String uName)
      throws SSDiCDMInterfaceException {
    try {
      Session session = JpaHelper.getEntityManager(this.entityManager).getActiveSession();
      StoredFunctionCall functionCall = new StoredFunctionCall();
      functionCall.setProcedureName("pkg_wrapper.insert_lock_with_user");
      functionCall.addNamedArgument(JPAParameterKeyConstants.PRIMARY_KEY);
      functionCall.addNamedArgument(JPAParameterKeyConstants.SECONDARY_KEY);
      functionCall.addNamedArgument("cre_user");
      functionCall.setResult(JPAParameterKeyConstants.FUNCTION_RESULT, Integer.class);
      ValueReadQuery valueReadQuery = new ValueReadQuery();
      valueReadQuery.setCall(functionCall);
      valueReadQuery.addArgument(JPAParameterKeyConstants.PRIMARY_KEY);
      valueReadQuery.addArgument(JPAParameterKeyConstants.SECONDARY_KEY);
      valueReadQuery.addArgument("cre_user");
      List<Object> arg = new ArrayList<>();
      arg.add(versId);
      arg.add(revId);
      arg.add(uName);
      return (Integer) DBResultUtil.executeSessionQuery(session, valueReadQuery, arg,
          "pkg_wrapper.insert_lock_with_user");
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * to delete lock inserted for labellist creation
   *
   * @param versId -
   * @param revId  -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public int deleteLock(final BigDecimal versId, final BigDecimal revId) throws SSDiCDMInterfaceException {
    try {
      Session session = JpaHelper.getEntityManager(this.entityManager).getActiveSession();
      StoredFunctionCall functionCall = new StoredFunctionCall();
      functionCall.setProcedureName("pkg_wrapper.delete_ssd_lock");
      functionCall.addNamedArgument(JPAParameterKeyConstants.PRIMARY_KEY);
      functionCall.addNamedArgument(JPAParameterKeyConstants.SECONDARY_KEY);
      functionCall.setResult(JPAParameterKeyConstants.FUNCTION_RESULT, Integer.class);
      ValueReadQuery valueReadQuery = new ValueReadQuery();
      valueReadQuery.setCall(functionCall);
      valueReadQuery.addArgument(JPAParameterKeyConstants.PRIMARY_KEY);
      valueReadQuery.addArgument(JPAParameterKeyConstants.SECONDARY_KEY);
      List<Object> arg = new ArrayList<>();
      arg.add(versId);
      arg.add(revId);
      return (Integer) DBResultUtil.executeSessionQuery(session, valueReadQuery, arg, "pkg_wrapper.delete_ssd_lock");
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param proRevId           - label list id
   * @param maxRelId           - max release id
   * @param isNonSDOMSwRelease releaseTyp
   * @return - newly created release entry id
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal createReleaseEntry(final BigDecimal proRevId, final BigDecimal maxRelId,
      final boolean isNonSDOMSwRelease) throws SSDiCDMInterfaceException {
    try {
      VLdb2ProjectRelease proj =
          DBEntityCreationUtil.createVldb2ProjectRelease(proRevId, maxRelId, this.userName, isNonSDOMSwRelease);
      this.entityManager.persist(proj);
      this.entityManager.flush();
      return proj.getProRelId();
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param proRevId -
   * @param proRelId -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public boolean hasPopulated(final BigDecimal proRevId, final BigDecimal proRelId) throws SSDiCDMInterfaceException {
    try {
      JpaEntityManager jpaEntityManager = JpaHelper.getEntityManager(this.entityManager);
      Session session = jpaEntityManager.getActiveSession();
      StoredFunctionCall functionCall = new StoredFunctionCall();
      functionCall.setProcedureName("pkg_wrapper.populate_icdmWrapper");
      functionCall.addNamedArgument("pn_Pro_Rev_Id");
      functionCall.addNamedArgument(JPAParameterKeyConstants.PRO_REL_ID);
      functionCall.setResult("FUNCTION_RESULT", String.class);
      ValueReadQuery valueReadQuery = new ValueReadQuery();
      valueReadQuery.setCall(functionCall);
      valueReadQuery.addArgument("pn_Pro_Rev_Id");
      valueReadQuery.addArgument(JPAParameterKeyConstants.PRO_REL_ID);
      List<Object> arg = new ArrayList<>();
      arg.add(proRevId);
      arg.add(proRelId);
      String obj =
          (String) DBResultUtil.executeSessionQuery(session, valueReadQuery, arg, "pkg_wrapper.populate_icdmWrapper");
      return "Y".equals(obj);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * to delete temp table in the session before populating
   *
   * @param releaseId -
   * @throws SSDiCDMInterfaceException Exception
   */
  public void deleteTempFeaTable(final BigDecimal releaseId) throws SSDiCDMInterfaceException {
    try {
      DBResultUtil.executeQueryUpdate(this.entityManager
          .createNamedQuery(JPAQueryConstants.TEMP_LDB2_FEAVAL_TRUNC_TABLE).setParameter("relId", releaseId),
          JPAQueryConstants.TEMP_LDB2_FEAVAL_TRUNC_TABLE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * to call release wrapper
   *
   * @param revisionId -
   * @param releaseId  -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public String callReleaseWrapper(final BigDecimal revisionId, final BigDecimal releaseId)
      throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE);
      query.setParameter("revisionId", revisionId);
      query.setParameter("releaseId", releaseId);
      return (String) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * ALM-305433 to call release wrapper
   *
   * @param revisionId -
   * @param releaseId  -
   * @return -
   * @throws SSDiCDMInterfaceException E
   */
  public String callReleaseWrapperCompli(final BigDecimal revisionId, final BigDecimal releaseId)
      throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE_COMPLI);
      query.setParameter("revisionId", revisionId);
      query.setParameter("releaseId", releaseId);
      // TODO: When 'N' is returned, release is not successful. Handle that
      return (String) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE_COMPLI);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param nodeId - node id
   * @return - valid list id
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getValidListforNode(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_FIND_VALID_LIST);
      query.setParameter("P_NODE_ID", nodeId);
      return (BigDecimal) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.V_LDB2_PROJECT_RELEASE_FIND_VALID_LIST);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Retruns the ProRevId (active label list) id for the passed node.
   *
   * @param nodeID id
   * @return value
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getProRevId(final BigDecimal nodeID) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_REVISION_GET_PRO_REV_ID);
      query.setParameter("nodeid", nodeID);
      return (BigDecimal) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.V_LDB2_PROJECT_REVISION_GET_PRO_REV_ID);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * To get the configured parameter value from the V_ldb2_prog_parameter table
   *
   * @param name name
   * @return value
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getConfigValue(final SSDConfigParams name) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROG_PARAMETER_FIND_PARAMETER);
      query.setParameter("typ", name.getKey());
      return (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.V_LDB2_PROG_PARAMETER_FIND_PARAMETER);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to get the list of nodes for the bc's from icdm
   *
   * @param bcName    - bc name
   * @param bcVersion - bc versions
   * @return - list of node information for bc variant and bc name
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> getBcNodes(final String bcName, final String bcVersion) throws SSDiCDMInterfaceException { // SSD-276
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_SDOM_SSDNODE_VARIANTE_FIND_ALL);
      query.setParameter("elName", bcName.toUpperCase(Locale.ENGLISH));
      query.setParameter("variante", bcVersion);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_SDOM_SSDNODE_VARIANTE_FIND_ALL);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to get the list of SW Version List by Sw Proj Id
   *
   * @param swProjNodeId nodeid
   * @return list
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> getSWVersionListBySwProjId(final long swProjNodeId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_VILLA_SWVER_VILLA_SWVER);
      query.setParameter(1, swProjNodeId);
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.V_LDB2_VILLA_SWVER_VILLA_SWVER);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Get OEM Rule Description Model
   *
   * @param ruleIdWithRevList rule list
   * @return object
   * @throws SSDiCDMInterfaceException exception
   */
  public List<Object> getOEMRuleDescription(final Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException {
    try {
      DBResultUtil.executeQueryUpdate(
          this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_RULE_ID_OEM_DELETE_TEMP_VALUES),
          JPAQueryConstants.TEMP_RULE_ID_OEM_DELETE_TEMP_VALUES);
      for (OEMRuleDescriptionInput ruleDetails : ruleIdWithRevList) {
        TempRuleIdOEM tempLabel =
            DBEntityCreationUtil.createTempRuleIdOEM(ruleDetails.getRuleId(), ruleDetails.getRevision());
        if (tempLabel == null) {
          throw ExceptionUtils.createAndThrowException(null,
              "Rule Id & Rev Id is not available. Cannot generate OEM Description.",
              SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION, true);
        }
        this.entityManager.persist(tempLabel);
      }
      this.entityManager.flush();
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_SSD2_GET_OEM_DESCRIPTION);
      return DBResultUtil.getQueryObjList(query, JPAQueryConstants.V_LDB2_SSD2_GET_OEM_DESCRIPTION);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param nodeId -
   * @return -
   */
  public BigDecimal getVersId(final BigDecimal nodeId) {
    Query query = this.entityManager.createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_REVISION_VERS_ID);
    query.setParameter(1, nodeId);
    return (BigDecimal) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.V_LDB2_PROJECT_REVISION_VERS_ID);
  }

  /**
   * identify max rev id for versid
   *
   * @param versId -
   * @return -
   */
  public BigDecimal findMaxRevId(final BigDecimal versId) {
    Query query = this.entityManager.createNativeQuery(JPAQueryConstants.SELECT_NEXTVAL_MAX_REV_ID);
    return (BigDecimal) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.SELECT_NEXTVAL_MAX_REV_ID);
  }


  /**
   * to retrieve user who has the lock for creation of labellist
   *
   * @param versId -
   * @param revId  -
   * @return -
   */
  public String getLockHolder(final BigDecimal versId, final BigDecimal revId) {
    Session session = JpaHelper.getEntityManager(this.entityManager).getActiveSession();
    StoredFunctionCall functionCall = new StoredFunctionCall();
    functionCall.setProcedureName("pkg_wrapper.get_lock_user");
    functionCall.addNamedArgument(JPAParameterKeyConstants.PRIMARY_KEY);
    functionCall.addNamedArgument(JPAParameterKeyConstants.SECONDARY_KEY);
    functionCall.setResult("FUNCTION_RESULT", String.class);
    ValueReadQuery valueReadQuery = new ValueReadQuery();
    valueReadQuery.setCall(functionCall);
    valueReadQuery.addArgument(JPAParameterKeyConstants.PRIMARY_KEY);
    valueReadQuery.addArgument(JPAParameterKeyConstants.SECONDARY_KEY);
    List<Object> arg = new ArrayList<>();
    arg.add(versId);
    arg.add(revId);
    return (String) DBResultUtil.executeSessionQuery(session, valueReadQuery, arg, "pkg_wrapper.get_lock_user");
  }

  /**
   * to create new project revision
   *
   * @param maxRevId -
   * @param versId   -
   * @return -
   */
  public BigDecimal createNewRevision(final BigDecimal versId, final BigDecimal maxRevId) {
    VLdb2ProjectRevision revision = DBEntityCreationUtil.createVLdb2ProjectRevision(versId, maxRevId, this.userName);
    this.entityManager.persist(revision);
    // to execute the associated triggers
    this.entityManager.flush();
    return revision.getProRevId();
  }

  /**
   * Method to check whether the operator can be set to the given feature or not
   *
   * @param featureId - Feature id
   * @return - Returns Y - valid, N - invalid
   * @throws SSDiCDMInterfaceException Exception
   */
  @SuppressWarnings("unchecked")
  public boolean checkForValidOperator(final String featureId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager.createNamedQuery(JPAQueryConstants.CHECK_VALID_OPERATOR);
      query.setParameter(1, featureId);

      return ((String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.CHECK_VALID_OPERATOR))
          .equalsIgnoreCase("Y") ? true : false;

    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * ALM-305433 to call release wrapper
   *
   * @param proRevId LabelList ID
   * @param proRelId Release ID
   * @return
   * @throws SSDiCDMInterfaceException E
   */
  public List<TempNonSDOMNodeList> checkAndGetNodesForReleaseConfig(final BigDecimal proRevId,
      final BigDecimal proRelId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.entityManager
          .createNamedQuery(JPAQueryConstants.V_LDB2_PROJECT_RELEASE_GET_NODES_FOR_NON_SDOM_SW_RELEASE);
      query.setParameter("pn_pro_rev_id", proRevId);
      query.setParameter("pn_pro_rel_id", proRelId);
      String primaryId = (String) DBResultUtil.getQuerySingleResult(query,
          JPAQueryConstants.V_LDB2_PROJECT_RELEASE_GET_NODES_FOR_NON_SDOM_SW_RELEASE);
      List<TempNonSDOMNodeList> nodeList =
          this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_ICDM_NODE_TABLE_GET_NODE_LIST)
              .setParameter("primaryId", primaryId).getResultList();


      Query deleteQuery = this.entityManager.createNamedQuery(JPAQueryConstants.TEMP_ICDM_NODE_TABLE_DELETE_NODE_LIST);
      deleteQuery.setParameter("primaryId", primaryId);
      deleteQuery.executeUpdate();
      return constructNodeList(nodeList);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * Method to construct the node list for level 1 for non SDOM release
   * 
   * @param nodeList
   */
  private List<TempNonSDOMNodeList> constructNodeList(List<TempNonSDOMNodeList> nodeList) {

    List<TempNonSDOMNodeList> nodesList = new ArrayList<>();
    List<BigDecimal> nodeIdList = new ArrayList<>();
    for (TempNonSDOMNodeList nonSDOMData : nodeList) {
// Skip adding the existing nodes and also nodes from customer node
      if (!nodeIdList.contains(nonSDOMData.getNodeId()) &&
          !nonSDOMData.getNodeScope().startsWith("Common\\Customer (SSD)\\")) {
        nodesList.add(nonSDOMData);
        nodeIdList.add(nonSDOMData.getNodeId());

      }
    }
    return nodesList;
  }

}
