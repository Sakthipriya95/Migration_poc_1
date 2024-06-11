/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Query;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.Session;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.common.utility.CommonUtil;
import com.bosch.ssd.icdm.common.utility.DBResultUtil;
import com.bosch.ssd.icdm.common.utility.VarCodeLabelUtil;
import com.bosch.ssd.icdm.constants.JPAParameterKeyConstants;
import com.bosch.ssd.icdm.constants.JPAQueryConstants;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.entity.TempLabel;
import com.bosch.ssd.icdm.entity.TempLabellistInterface;
import com.bosch.ssd.icdm.entity.VLdb2Feature;
import com.bosch.ssd.icdm.entity.VLdb2Pavast;
import com.bosch.ssd.icdm.entity.VLdb2Ssd2;
import com.bosch.ssd.icdm.entity.VLdb2Value;
import com.bosch.ssd.icdm.entity.keys.VLdb2Ssd2PK;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.MATURITY_LEVEL;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.utils.DBEntityCreationUtil;
import com.bosch.ssd.icdm.model.utils.DBToModelUtil;

/**
 * This class contains the CRUD operations of SSD Review Rules
 *
 * @author SSN9COB
 */
public class SSDReviewRulesUtil {

  /**
  *
  */
  private static final String DATABASE_ERROR = "Database Error ";

  // Call fromreview is used when it is for a review rule. check the setter to know where it is invoked
  private boolean callFromReview;
  private final DBQueryUtils dbQueryUtils;

  /**
   * @param dbQueryUtils util instance
   */
  public SSDReviewRulesUtil(final DBQueryUtils dbQueryUtils) {
    this.dbQueryUtils = dbQueryUtils;
  }

  /** 
   * Method to create label list
   *
   * @param nodeId -
   * @return - success (1/2) or failure (3) and error message if any
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<Object[]> createLabelList(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.CREATE_LABEL_LIST);
      query.setParameter(JPAParameterKeyConstants.P_NODE_ID, nodeId);
      query.setParameter(JPAParameterKeyConstants.P_USERNAME, this.dbQueryUtils.getUserName());
      return DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.CREATE_LABEL_LIST);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }


  /**
   * Method to insert rules into temp table
   *
   * @param cdrRules rules
   * @param nodeId node Id
   * @throws SSDiCDMInterfaceException Exception
   */
  public void insertIntoTemp(final List<CDRRule> cdrRules, final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    try {
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DELETE_LABEL),
          JPAQueryConstants.DELETE_LABEL);
      long index = 0;
      Set<String> labelSet = new HashSet<>();
      // insert rulesinto temp if not prsent in labelset
      for (CDRRule cdrRule : cdrRules) {
        if (!labelSet.contains(cdrRule.getParameterName().toUpperCase(Locale.ENGLISH))) {
          // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
          TempLabel label = DBEntityCreationUtil.createTempLabel(cdrRule, nodeId, new BigDecimal(index++));
          labelSet.add(label.getUpperLabel());
          this.dbQueryUtils.getEntityManager().persist(label);
        }
      }
      this.dbQueryUtils.getEntityManager().flush();
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * Method to create rule
   *
   * @param model - cdr model
   * @param ssdNodeId -
   * @param isCompPckRule -
   * @param scope scope
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException exeption
   */
  public SSDMessage createRuleForLabel(final CDRRule model, final BigDecimal ssdNodeId, final boolean isCompPckRule,
      final BigDecimal scope)
      throws SSDiCDMInterfaceException {
    try {
      SSDiCDMInterfaceLogger.logMessage("Label Id identified from SSD ", ILoggerAdapter.LEVEL_INFO, null);
      // SSD V3.2.1
      // functionality to insert the rules if there found a rule available for the same label which is deleted
      if ((model.getDependencyList() != null) && (!model.getDependencyList().isEmpty())) {
        // deletes the data from temporary table
        // TODO: TO verify with iCDM Team if they will create review rule with dependencies : TempICDMFeaVal thorws
        // table or view does not exist
        /*
         * beginTransaction(); // Delete all from Temp Table
         * this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DELETE_FEAVAL).executeUpdate(); for
         * (FeatureValueModel dep : model.getDependencyList()) { // Invoke the UTIL Method to get the TempICDMFeVal
         * Instance and persist
         * this.dbQueryUtils.getEntityManager().persist(DBEntityCreationUtil.createTempICDMFeaVal(dep,
         * model.getLabelId())); this.dbQueryUtils.getEntityManager().flush(); } // get dependency rule details Query
         * query =
         * this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DEPENDENCY_RULE_DETAILS);query
         * .setParameter(1, ssdNodeId); query.setParameter(2, model.getLabelId()); query.setParameter(3,
         * model.getLabelId()); @SuppressWarnings("unchecked") List<Object[]> ssdList = query.getResultList(); if
         * (!ssdList.isEmpty()) { model.setRuleId((BigDecimal) ssdList.get(0)[0]); model.setRevId((BigDecimal)
         * ssdList.get(0)[1]); // to validate and create a new rule for already deleted rules return
         * validateCreateForDeletedRules(model, ssdNodeId, scope); }//end
         */
      }
      else {
        // get default rule details for label
        Query query = this.dbQueryUtils.getEntityManager().createNamedQuery("VLdb2Ssd2.getDefaultRuleDetailsForLabel");
        query.setParameter(1, ssdNodeId);
        query.setParameter(2, model.getLabelId());
        query.setParameter(3, ssdNodeId);
        query.setParameter(4, model.getLabelId());
        List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.DEFAULT_RULE_DETAILS);
        if (!ssdList.isEmpty()) {
          model.setRuleId((BigDecimal) ssdList.get(0)[0]);
          model.setRevId((BigDecimal) ssdList.get(0)[1]);
          // to validate and create a new rule for already deleted rules
          return validateCreateForDeletedRules(model, ssdNodeId, scope);

        }
      }

      // to validate if the model passed for insert or update of review rule is valid
      SSDMessage validationResult = validateModel(model, ssdNodeId, isCompPckRule, scope);
      if (validationResult.getCode() == 0) {
        VLdb2Ssd2 ssd = doInsert(model, ssdNodeId);
        // returns null in case of any failures in setting values
        if (ssd != null) {
          model.setRuleId(ssd.getLabObjId());
          model.setRevId(ssd.getRevId());
          SSDiCDMInterfaceLogger.logMessage("Rule inserted for label " + model.getParameterName(),
              ILoggerAdapter.LEVEL_INFO, null);
        }
      }
      else {
        SSDiCDMInterfaceLogger.logMessage(validationResult.getDescription(), ILoggerAdapter.LEVEL_INFO, null);
        return validationResult;
      }
    }
    catch (Exception e) {
      SSDiCDMInterfaceLogger.logMessage("Error during rule creation " + e, ILoggerAdapter.LEVEL_ERROR, null);
      throw e;
    }
    return SSDMessage.SUCCESS;
  }

  /**
   * Inserts the details into the temp tables
   *
   * @param label - icdm label
   * @param typ - type of the label
   * @param subTyp - subTyp of the label
   * @param function - function of the label
   * @param parameterClass -
   * @throws SSDiCDMInterfaceException exception
   */
  public void insertLabelInTemp(final String label, final String typ, final String subTyp, final String function,
      final ParameterClass parameterClass)
      throws SSDiCDMInterfaceException {
    try {
      // deletes the data from temporary table
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DELETE_TEMP_TABLE),
          JPAQueryConstants.DELETE_TEMP_TABLE);
      // inserts the label info in temp table
      // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
      this.dbQueryUtils.getEntityManager()
          .persist(DBEntityCreationUtil.createTempLabelsList(label, function, typ, subTyp, parameterClass.toString()));
      this.dbQueryUtils.getEntityManager().flush();
      // Procedure checks if the label is present in a SDOM label
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.INSERT_INTO_TEMP_TABLE),
          JPAQueryConstants.INSERT_INTO_TEMP_TABLE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param label - label
   * @return whether the label is present or not
   */
  public VLdb2Pavast labelPresent(final String label) {

    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.CHECK_LABEL);
      query.setParameter(JPAParameterKeyConstants.LABEL, label.toUpperCase(Locale.ENGLISH));
      return (VLdb2Pavast) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.CHECK_LABEL);
    }
    catch (Exception e) {
      SSDiCDMInterfaceLogger.logMessage(DATABASE_ERROR + " @labelPresent " + e.getLocalizedMessage(),
          ILoggerAdapter.LEVEL_ERROR, null);
      return null;
    }

  }


  /**
   * Method gets the lab id of the newly inserted label
   *
   * @param label - label
   * @return - lab id of the newly created label
   */
  public BigDecimal getLabelId(final String label) {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_LAB_ID);
      query.setParameter(JPAParameterKeyConstants.LABEL, label.toUpperCase(Locale.ENGLISH));
      return (BigDecimal) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.GET_LAB_ID);
    }
    catch (Exception e) {
      SSDiCDMInterfaceLogger.logMessage(DATABASE_ERROR + " @getLabelId " + e.getLocalizedMessage(),
          ILoggerAdapter.LEVEL_ERROR, e);
      return null;
    }
  }

  /**
   * Method inserts in LDB, does validation and generate results
   *
   * @param label -label
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage insertInSSD(final String label) throws SSDiCDMInterfaceException {
    try {
      // procedure validates the label and checks for the reference and
      // structure labels and insert in SSD
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.VALIDATE_LABEL),
          JPAQueryConstants.VALIDATE_LABEL);
      // Procedure called to insert the selected labels in v-ldb2_pavast
      DBResultUtil.executeQueryUpdate(this.dbQueryUtils.getEntityManager()
          .createNamedQuery(JPAQueryConstants.INSERT_LABELS).setParameter("p_tempno", 1),
          JPAQueryConstants.INSERT_LABELS);
      // to get the error if present for the label from the temp table
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_ERROR_REPORT);
      query.setParameter(1, label);
      List<Object[]> queryResult = DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.GET_ERROR_REPORT);
      for (Object[] obj : queryResult) {
        SSDiCDMInterfaceLogger.logMessage("Error Message : " + obj[2].toString(), ILoggerAdapter.LEVEL_INFO, null);

        int errorCode = Integer.parseInt(obj[1].toString());
        switch (errorCode) {
          case 3:
            return SSDMessage.FIRSTLETTERCHARACTER;
          case 4:
            return SSDMessage.UNDERSCORESLABEL;
          case 11:
            return SSDMessage.DOTLABEL;
          case 8:
            // enable to create the union/structure first and then the parameter name
            return SSDMessage.CREATESTRUCTURELABELANDPROCEED;
          default:
            return SSDMessage.LABELVALIDATIONFAILED;
        }
      }
      return SSDMessage.LABELCREATED;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * --SSD V3.2.1 Method to validate and create a new rule for already delteted rules in which the new rule will follow
   * a continuation of the rev_ID and LAb_obj_ID
   *
   * @param dbQueryUtils
   * @param model
   * @param ssdNodeId
   * @return
   * @throws SSDiCDMInterfaceException Exception
   */
  private SSDMessage validateCreateForDeletedRules(final CDRRule model, final BigDecimal ssdNodeId,
      final BigDecimal scope)
      throws SSDiCDMInterfaceException {
    try {
      if ((model.getRuleId() != null) && (model.getRevId() != null)) {
        // to validate the rules
        VLdb2Ssd2PK key = new VLdb2Ssd2PK();
        key.setLabObjId(model.getRuleId());
        key.setRevId(model.getRevId());

        VLdb2Ssd2 oldSSD = this.dbQueryUtils.getEntityManager().find(VLdb2Ssd2.class, key);

        if (oldSSD == null) {
          return SSDMessage.NORULETOUPDATE;
        }

        SSDMessage validationResult = validateModel(model, ssdNodeId, false, scope);
        if (validationResult.getCode() == 0) {
          doUpdate(model, oldSSD, ssdNodeId, true);
          model.setRevId(model.getRevId().add(BigDecimal.ONE));
          return SSDMessage.SUCCESS;
        }
        return validationResult;

      }
      return SSDMessage.NORULETOUPDATE;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * to update/ create new revision of the rule
   *
   * @param model - cdr model
   * @param oldSSD - old rule
   * @param ssdNodeId - ssd node
   * @param deletedRule -is the rule delteted and recreated? --SSD V3.2.1
   * @return - ssd
   * @throws SSDiCDMInterfaceException Exception
   */
  public VLdb2Ssd2 doUpdate(final CDRRule model, final VLdb2Ssd2 oldSSD, final BigDecimal ssdNodeId,
      final boolean deletedRule)
      throws SSDiCDMInterfaceException {
    try {
   
      // ssd access rights not checked -
      // it is assumed that icdm technical user can insert in review(SSD) node

      oldSSD.setModUser(this.dbQueryUtils.getUserName());
      oldSSD.setHistorie("Y");


      VLdb2Ssd2 ssd =
          DBEntityCreationUtil.updateVldb2SSD2(oldSSD, model, this.dbQueryUtils.getUserName(), ssdNodeId, deletedRule);

      callUpdateRecord(ssd);
      ssd.setRevId(ssd.getRevId().add(BigDecimal.ONE));
      this.dbQueryUtils.getEntityManager().persist(ssd);
      this.dbQueryUtils.getEntityManager().flush();

      updateRelatedTables(model, ssd);
      caseHist(ssd.getLabObjId(), ssd.getRevId(), ssd.getCases());
      return ssd;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * to maintain a history of the cases while ssd record is updated with downgrading of cases ie if cases are downgraded
   * from 'MFT' to any any other case 'SSD' to App-Chk' or 'No-SSD,No-Check' 'App-Chk' to 'No-SSD,No-Check'
   *
   * @param labObjId -
   * @param revId -
   * @param cases -
   * @throws SSDiCDMInterfaceException Exception
   */
  private void caseHist(final BigDecimal labObjId, final BigDecimal revId, final String cases)
      throws SSDiCDMInterfaceException {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_CASE_HISTORY);
      query.setParameter(JPAParameterKeyConstants.LAB_OBJ_ID, labObjId);
      query.setParameter(JPAParameterKeyConstants.REV_ID, revId);
      query.setParameter(JPAParameterKeyConstants.CASES, cases);
      DBResultUtil.executeQueryUpdate(query, JPAQueryConstants.GET_CASE_HISTORY);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * to check if the model passed for insert or update of review rule is valid
   *
   * @param model - review rule
   * @param ssdNodeId - ssd node
   * @param isCompPckRule rule check
   * @param scope scope
   * @return - 0 if valid -2 when update should be called -1 otherwise
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage validateModel(final CDRRule model, final BigDecimal ssdNodeId, final boolean isCompPckRule,
      final BigDecimal scope)
      throws SSDiCDMInterfaceException {
    // Code is split into two methods to address Code Complexity
    SSDMessage message = checkModelForError(model);
    if (message == null) {
      SSDMessage messageNew = checkModelForError(model, ssdNodeId, isCompPckRule);
      if (messageNew != null) {
        return messageNew;
      }
    }
    // feature value validation after the model is found valid
    return validateDependencyForRule(model, ssdNodeId, scope);
  }

  /**
   * Refactored method to address Sonar
   */
  private SSDMessage checkModelForError(final CDRRule model, final BigDecimal ssdNodeId, final boolean isCompPckRule) {
    if ((Objects.nonNull(model.getMaturityLevel())) && !isValidMaturityLevel(model)) {
      return SSDMessage.INVALIDMATURITYLEVEL;
    }
    if (!checkDuplicateFeature(model)) {
      // if the same fetaure is inserted again
      return SSDMessage.FEATUREDUPLICATED;
    }
    if (!validFeatureId(model)) { // SSD-315
      // if the featureid not present in ssd
      return SSDMessage.FEATUREIDNOTPRESENT;
    }
    if (!validValueId(model)) { // SSD-315
      // if the valueid not present in ssd
      return SSDMessage.VALUEIDNOTPRESENT;
    }
    // SSD-330
    if ((Objects.nonNull(model.getSsdCase())) &&
        (model.getSsdCase().getCharacter().equals(SSDCase.CAL4COMP.getCharacter()) && !isComponentNode(ssdNodeId))) {
      return SSDMessage.COMPRULENOTPOSSIBLE;
    }
    if (isCompPckRule && (Objects.isNull(model.getUnit()))) {
      return SSDMessage.UNITNOTPRESENT;
    }
    return null;
  }

  /**
   * Refactored method to address Sonar
   */
  private SSDMessage checkModelForError(final CDRRule model) {
    if (Objects.isNull(model.getValueType())) {
      return SSDMessage.TYPEERROR;
    }
    // SSD-372 Value block commented for supporting new category valueblock in ssd icdm interface
    // model.getValueType().equals(SSDiCDMInterfaceConstants.VAL_BLK) ||
    if ((model.getValueType().equals(SSDiCDMInterfaceConstants.ASCII)
    // Commented for ICDM-1461 && SSD-346
    // model.getValueType().equals(SSDiCDMInterfaceConstants.AXIS_PTS)
    ) && ((Objects.nonNull(model.getUpperLimit())) || (Objects.nonNull(model.getLowerLimit())))) {
      return SSDMessage.INVALIDFIELD;
    }

    if ((Objects.nonNull(model.getLowerLimit())) && (Objects.nonNull(model.getUpperLimit())) &&
        (model.getLowerLimit().longValue() > model.getUpperLimit().longValue())) {
      return SSDMessage.MINMAXFAILURE;
    }

    if (!model.getValueType().equals(SSDiCDMInterfaceConstants.VALUE)) {

      if (Objects.nonNull(model.getUnit()) && CommonUtil.isEmptyString(model.getRefValueDCMString()) &&
          Objects.isNull(model.getLowerLimit()) && Objects.isNull(model.getUpperLimit())) {
        // when unit is specified, some ssd field has to be there mandatorily
        return SSDMessage.SSDFIELDMISSING;
      }

      if (Objects.nonNull(model.getRefValue())) {
        // reference value not applicable for complex label types
        return SSDMessage.INVALIDFIELD;
      }
    }
    return null;
  }

  /**
   * to check if the node id is marked as component
   *
   * @param nodeId - node id of the selected node
   * @return - true or false
   */
  public boolean isComponentNode(final BigDecimal nodeId) {
    Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.FIND_CUSTOMER_NODEID);
    query.setParameter(1, nodeId);
    String component = "N";
    try {
      component = (String) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.FIND_CUSTOMER_NODEID);
    }
    catch (Exception e) {
      SSDiCDMInterfaceLogger.logMessage(DATABASE_ERROR + " @isComponentNode " + e.getLocalizedMessage(),
          ILoggerAdapter.LEVEL_ERROR, e);
      // in case if scope given is 3 or if the node id is wrong, then we do not allow the rule to be entered
      return false;
    }
    return ("Y".equalsIgnoreCase(component));
  }

  /**
   * @param model
   * @return SSD-305
   * @throws SSDiCDMInterfaceException Exception
   */
  private SSDMessage validateDependencyForRule(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal scope)
      throws SSDiCDMInterfaceException {
    Integer ssdAvailable = checkFeaValConfigured(model, ssdNodeId, scope);

    if ((ssdAvailable != null) && (ssdAvailable.intValue() != 1)) {
      // error when rule already exists
      if ((model.getRevId() != null) && (model.getRevId().longValue() > 0)) {
        return SSDMessage.RULEEXIST;
      }

      if (ssdAvailable.intValue() == -1) {
        return SSDMessage.DEFAULTRULEEXISTS;
      }

      if (ssdAvailable.intValue() == -2) {
        return SSDMessage.FEATUREVALUEEXISTS;
      }
    }

    return SSDMessage.SUCCESS;
  }

  /**
   * Checks if the same feature value pair is configured already, same thing can not be configured again.
   *
   * @param rule -rule
   * @param ssdNodeId - ssd node
   * @param scope scope
   * @return - -1, -2 or 1 SSD-305
   * @throws SSDiCDMInterfaceException Exception
   */
  private Integer checkFeaValConfigured(final CDRRule rule, final BigDecimal ssdNodeId, final BigDecimal scope)
      throws SSDiCDMInterfaceException {
    try {

      insertDependencyInTemp(rule.getDependencyList(), rule.getRuleId());
      Integer isValid = checkDependencyValidity(rule.getLabelId(), ssdNodeId, scope, rule.getRuleId());
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DELETE_TEMP_FEAVAL),
          JPAQueryConstants.DELETE_TEMP_FEAVAL);
      SSDiCDMInterfaceLogger.logMessage("result from dependency check " + isValid, ILoggerAdapter.LEVEL_INFO, null);
     
      return isValid;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param labLabId -
   * @param objId1 -
   * @param scope -
   * @param labObjId -
   * @return -
   * @throws SSDiCDMInterfaceException E
   */
  private Integer checkDependencyValidity(final BigDecimal labLabId, final BigDecimal objId1, final BigDecimal scope,
      final BigDecimal labObjId)
      throws SSDiCDMInterfaceException {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.CHECK_DEPENDENCY);
      query.setParameter(JPAParameterKeyConstants.LAB_OBJ_ID, labObjId);
      query.setParameter(JPAParameterKeyConstants.LABLAB_ID, labLabId);
      query.setParameter(JPAParameterKeyConstants.OBJ_ID1, objId1);
      query.setParameter(JPAParameterKeyConstants.SCOPE, scope);
      return (Integer) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.CHECK_DEPENDENCY);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param dependencies -
   * @param ruleId -
   * @throws SSDiCDMInterfaceException Exception
   */
  private void insertDependencyInTemp(final List<FeatureValueModel> dependencies, final BigDecimal ruleId)
      throws SSDiCDMInterfaceException {
    try {
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.DELETE_TEMP_FEAVAL),
          JPAQueryConstants.DELETE_TEMP_FEAVAL);
      for (FeatureValueModel feModel : dependencies) {
        // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
        this.dbQueryUtils.getEntityManager().persist(DBEntityCreationUtil.createTSSD2TempFeaVal(feModel, ruleId));
      }

      this.dbQueryUtils.getEntityManager().flush();
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * prc_insert_button - for setting initial values for ssd
   *
   * @param model - input from calling method for creation of rule
   * @param ssdNodeId -
   * @return new ssd object to be created
   * @throws SSDiCDMInterfaceException - in case any exception is thrown from database operation
   */
  public VLdb2Ssd2 doInsert(final CDRRule model, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {
    try {

      // ssd access rights not checked -
      // it is assumed that icdm technical user can insert in review(SSD) node
      // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
      VLdb2Ssd2 ssd =
          DBEntityCreationUtil.createVldb2SSD2(model, this.dbQueryUtils.getUserName(), ssdNodeId, getScope(ssdNodeId));
      this.dbQueryUtils.getEntityManager().persist(ssd);
      this.dbQueryUtils.getEntityManager().flush();
      // added this to update the text when unicode character is used
      this.dbQueryUtils.getEntityManager().refresh(ssd);
      // update feature value dependency
      insertRuleDependencies(model, ssd);
      // update DCM data
      insertDCMData(model, ssd);
      // update maturity information
      insertMaturityValue(model.getMaturityLevel(), ssd);

      return ssd;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param nodeId -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getScope(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.FIND_SCOPE);
    query.setParameter(1, nodeId);
    return (BigDecimal) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.FIND_SCOPE);
  }

  /**
   * @param ssd -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public Integer callUpdateRecord(final VLdb2Ssd2 ssd) throws SSDiCDMInterfaceException {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.UPDATE_RECORD);
      query.setParameter(JPAParameterKeyConstants.LAB_OBJ_ID, ssd.getLabObjId());
      query.setParameter("REV_ID", ssd.getRevId());
      return (Integer) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.UPDATE_RECORD);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param ssd
   * @param model
   * @throws SSDiCDMInterfaceException 
   */
  private void updateRelatedTables(final CDRRule model, final VLdb2Ssd2 ssd) throws SSDiCDMInterfaceException {
    insertRuleDependencies(model, ssd);
    insertDCMData(model, ssd);
    insertMaturityValue(model.getMaturityLevel(), ssd);
  }

  /**
   * @param ssdReviewRuleService
   * @param model - cdr rules
   * @param ssd - newly created ssd
   * @throws SSDiCDMInterfaceException
   */
  private void insertRuleDependencies(final CDRRule model, final VLdb2Ssd2 ssd) throws SSDiCDMInterfaceException {
    for (FeatureValueModel depModel : model.getDependencyList()) {
      // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
      this.dbQueryUtils.getEntityManager().persist(DBEntityCreationUtil.createVLDB2Comp(ssd, depModel, this.dbQueryUtils));
    }
  }

  /**
   * @param ssdReviewRuleService
   * @param ssd
   * @throws SSDiCDMInterfaceException
   */
  private void insertDCMData(final CDRRule model, final VLdb2Ssd2 ssd) {
    if (model.getRefValueDCMString() == null) {
      return;
    }
    int index = 1;
    String dcmString = model.getRefValueDCMString().replace("\r", "");
    for (String str : dcmString.split("\n")) {
      // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
      this.dbQueryUtils.getEntityManager().persist(DBEntityCreationUtil.createVLDB2DcmData(ssd, str, index));
      index++;
    }
  }

  /**
   * @param model - cdr rules
   * @return true if model has valid maturity level
   */
  private boolean isValidMaturityLevel(final CDRRule model) {
    String ssdMaturity = model.getMaturityLevel();
    for (MATURITY_LEVEL value : MATURITY_LEVEL.values()) {
      if (value.name().equalsIgnoreCase(ssdMaturity)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param model - cdr rules
   */
  private void insertMaturityValue(final String ssdMaturity, final VLdb2Ssd2 ssd) {
    if ((ssdMaturity != null) && !ssdMaturity.trim().isEmpty() && !"NONE".equalsIgnoreCase(ssdMaturity)) {
      // Invoke the UTIL Method to get the VLDB2COMP Instance and persist
      this.dbQueryUtils.getEntityManager().persist(DBEntityCreationUtil.createVLDB2MaturityLevel(ssd, ssdMaturity));
    }
  }

  /**
   * Method checks if the same feature is used again
   *
   * @param rule - {@link CDRRule}
   * @return - true/false true if duplicate fetaure used
   */
  private boolean checkDuplicateFeature(final CDRRule rule) {
    HashSet<BigDecimal> featureList = new HashSet<>();
    for (FeatureValueModel feModel : rule.getDependencyList()) {
      if (!featureList.add(feModel.getFeatureId())) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param rule - {@link CDRRule}
   * @return - true/false - true if feature id is valid , false if feature id is not present
   */
  private boolean validFeatureId(final CDRRule rule) {
    for (FeatureValueModel feModel : rule.getDependencyList()) {
      VLdb2Feature valueId =
          this.dbQueryUtils.getEntityManager().find(VLdb2Feature.class, feModel.getFeatureId().toBigInteger());
      if (valueId == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param rule - {@link CDRRule}
   * @return - true/false - true if value id is valid , false if value id is not present
   */
  private boolean validValueId(final CDRRule rule) {
    for (FeatureValueModel feModel : rule.getDependencyList()) {
      if (feModel.getValueId() == null) {
        return false;
      }
      VLdb2Value valueId =
          this.dbQueryUtils.getEntityManager().find(VLdb2Value.class, feModel.getValueId().toBigInteger());
      if (valueId == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * delete the ssd rule
   *
   * @param labObjId -
   * @param revId -
   * @return - success (1) or failure
   * @throws SSDiCDMInterfaceException Exception
   */
  public Integer doDelete(final BigDecimal labObjId, final BigDecimal revId) throws SSDiCDMInterfaceException {
    try {
      JpaEntityManager jpaEntityManager = JpaHelper.getEntityManager(this.dbQueryUtils.getEntityManager());
      Session session = jpaEntityManager.getActiveSession();
      StoredFunctionCall functionCall = new StoredFunctionCall();
      functionCall.setProcedureName("pkg_ssd_edit.Fnc_Delete_Record");
      functionCall.addNamedArgument("P_LAB_OBJ_ID");
      functionCall.addNamedArgument("P_REV_ID");
      functionCall.setResult(JPAParameterKeyConstants.FUNCTION_RESULT, Integer.class);
      ValueReadQuery valueReadQuery = new ValueReadQuery();
      valueReadQuery.setCall(functionCall);
      valueReadQuery.addArgument("P_LAB_OBJ_ID");
      valueReadQuery.addArgument("P_REV_ID");
      List arg = new ArrayList<>();
      arg.add(labObjId);
      arg.add(revId);
      return (Integer) DBResultUtil.executeSessionQuery(session, valueReadQuery, arg, "pkg_ssd_edit.Fnc_Delete_Record");
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * Returns latest revId of the rule
   *
   * @param labObjId - id of the rule
   * @return the rev Id
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getLatestRevId(final BigDecimal labObjId) throws SSDiCDMInterfaceException {
    try {
      Query query =
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.V_LDB2_SSD2_CHECK_SSD_CHANGED);
      query.setParameter("labObjId", labObjId);
      return (BigDecimal) DBResultUtil.getQuerySingleResult(query, JPAQueryConstants.V_LDB2_SSD2_CHECK_SSD_CHANGED);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param labelNames label name
   * @param ssdNodeId ssd node id
   * @return ssd rule
   * @throws SSDiCDMInterfaceException exception
   
  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRulesUtil.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      boolean hasVarcodeLable = insertLabelsinTmpTbl(labelNames, ssdNodeId);
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FOR_LABEL_LIST);
      query.setParameter(1, ssdNodeId);
      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.GET_RULE_FOR_LABEL_LIST);
      Map<String, List<CDRRule>> cdrRulesMap = new HashMap<>();
      setRuleinCDRModel(ssdList, cdrRulesMap, false);

      // SSD-334 - only if varcode labels are provided run this query
      if (hasVarcodeLable) {
        Query query1 =
            this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST);
        query1.setParameter(1, ssdNodeId);
        List<Object[]> ssdList1 =
            DBResultUtil.getQueryObjArrList(query1, JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST);
        setRuleinCDRModel(ssdList1, cdrRulesMap, false);
      }
      
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRulesUtil.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
      return cdrRulesMap;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(), this.dbQueryUtils.getEntityManager());
    }
  }
*/
  /**
   * SSD-273 to read the model and populate temp table
   *
   * @param labels -
   * @param ssdNodeId -
   * @return - to know if variant coded parameter is there part of labellist
   * @throws SSDiCDMInterfaceException Exception
   */
  public boolean insertLabelsinTmpTbl(final List<String> labels, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    try {

      
      boolean hasVarCodeLabel = false;
      DBResultUtil.executeQueryUpdate(
          this.dbQueryUtils.getEntityManager().createNamedQuery("TempLabellistInterface.deleteTempValues"),
          "TempLabellistInterface.deleteTempValues");
      Set<String> label = new HashSet<>();
      
      long index = 0;
      for (String curLabel : labels) {
        // add only unique labels in the temp tables
        if (label.add(curLabel)) {
          TempLabellistInterface tempLabel = new TempLabellistInterface();
          tempLabel.setLabel(curLabel);

          if (curLabel.contains(SSDiCDMInterfaceConstants.VARCODE_SYM)) {
            hasVarCodeLabel = true;
            tempLabel.setUpperLabel(VarCodeLabelUtil.getParamName(curLabel).toUpperCase(Locale.ENGLISH));
            tempLabel.setDefaultRule("Y");
          }
          else {
            tempLabel.setUpperLabel(curLabel.toUpperCase(Locale.ENGLISH));
            tempLabel.setDefaultRule("N");
          }

          tempLabel.setNodeId(ssdNodeId);
          tempLabel.setSeqNo(new BigDecimal(index++));
          this.dbQueryUtils.getEntityManager().persist(tempLabel);
         
        }
        
      }
      return hasVarCodeLabel;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
  }

  /**
   * @param ssdList
   * @param cdrRulesMap
   * @throws SSDiCDMInterfaceException
   */
  public void setRuleinCDRModel(final List<Object[]> ssdList, final Map<String, List<CDRRule>> cdrRulesMap,
      final boolean isReviewRule)
      throws SSDiCDMInterfaceException {
    try {
      for (Object[] objects : ssdList) {
        CDRRule cdrRule = DBToModelUtil.convertToCDRRule(objects, isReviewRule);
        /**
         * A rule needs to be sent to iCDM if it satisfies the below scenarios 1. Upper/lower limit 2. Bit-wise 3. Exact
         * match & Ref value 4. Advance formula configured directly in DB ........................................
         * Changes from iCDM is based on the below modules..............................................................
         * 1. Data review : If ExactMatch=N, reference should not be fetched even though lower/upper limits are not
         * available 2. Pre-Cal : If ExactMatch=N, reference needs to be fetched
         */
//        if (!isReviewRule) {
        addToCDRRulesMap(cdrRulesMap, cdrRule);
//        }
//        else {
        // Check if review rule and if exact match = 'N', dont add the rule to return list
//          if ((checkIfExactMatch(cdrRule) || checkIfBitWiseRule(cdrRule) || checkIfComplexRule(cdrRule) ||
//              checkIfUpperLowerPresent(cdrRule))) {
//            addToCDRRulesMap(cdrRulesMap, cdrRule);
//          }
//        }
      }
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION,true);
    }
  }

  /**
   * A rule needs to be sent to iCDM in SSD File- if it satisfies the below scenarios 1. Upper/lower limit 2. Bit-wise
   * 3. Exact match & Ref value 4. Advance formula configured directly in DB ........................................
   * Changes from iCDM is based on the below modules.............................................................. 1.
   * Data review : If ExactMatch=N, reference should not be fetched even though lower/upper limits are not available 2.
   * Pre-Cal : If ExactMatch=N, reference needs to be fetched
   *
   * @param cdrRuleMap map
   * @return labels
   */
  public List<String> getValidRuleParameters(final Map<String, List<CDRRule>> cdrRuleMap) {
    List<String> labels = new ArrayList<>();
    for (List<CDRRule> rules : cdrRuleMap.values()) {
      for (CDRRule cdrRule : rules) {
        if ((checkIfExactMatch(cdrRule) || checkIfBitWiseRule(cdrRule) || checkIfComplexRule(cdrRule) ||
            checkIfUpperLowerPresent(cdrRule))) {
          labels.add(cdrRule.getParameterName());
        }
      }
    }

    return labels;
  }

  /**
   * Check if Upper/Lower Limit Present
   *
   * @param cdrRule rule Model
   * @return
   */
  private boolean checkIfUpperLowerPresent(final CDRRule cdrRule) {
    return Objects.nonNull(cdrRule.getLowerLimit()) || Objects.nonNull(cdrRule.getUpperLimit());
  }

  /**
   * Check if Exact Match & Ref Value present
   *
   * @param cdrRule
   * @return
   */
  private boolean checkIfExactMatch(final CDRRule cdrRule) {
    return cdrRule.isDcm2ssd() &&
        (Objects.nonNull(cdrRule.getRefValue()) || Objects.nonNull(cdrRule.getRefValueDCMString()));
  }

  /**
   * Check if it is a bit wise rule
   *
   * @param cdrRule
   * @return
   */
  private boolean checkIfBitWiseRule(final CDRRule cdrRule) {
    return Objects.nonNull(cdrRule.getFormulaDesc()) && cdrRule.getFormulaDesc().contains(",BIT");
    // cdrRule.isVarCodedParameterRule()
  }

  /**
   * Check if it is a complex rule
   *
   * @param cdrRule rule Model
   * @return
   */
  private boolean checkIfComplexRule(final CDRRule cdrRule) {
    return cdrRule.isRuleComplex() &&
        (Objects.nonNull(cdrRule.getBitWiseRule()) && !cdrRule.getBitWiseRule().contains(CDRRule.BIT));
  }

  /**
   * @param cdrRulesMap
   * @param cdrRule
   */
  private void addToCDRRulesMap(final Map<String, List<CDRRule>> cdrRulesMap, final CDRRule cdrRule) {
    List<CDRRule> cdrRuleList = cdrRulesMap.get(cdrRule.getParameterName());
    if (cdrRulesMap.containsKey(cdrRule.getParameterName()) && (cdrRuleList != null)) {
      cdrRuleList.add(cdrRule);
    }
    else {
      cdrRuleList = new ArrayList<>();
      cdrRuleList.add(cdrRule);
      cdrRulesMap.put(cdrRule.getParameterName(), cdrRuleList);
    }
  }

 
  /**
   * method adds the feature value to the CDR rule
   *
   * @param rule - ssd rule
   * @param ssd - ssd containing feature value
  */
  private void populateRulewithDependencies(final String dependencyValues, final CDRRule rule)  {
    ArrayList<FeatureValueModel> featureValueList = new ArrayList<>();

    String[] feaValueList = dependencyValues.split(";");
    for (String str : feaValueList) {
      String[] feaVal = str.split(",");
      FeatureValueModel model = new FeatureValueModel();
      model.setFeatureId(new BigDecimal(feaVal[0]));
      model.setValueId(new BigDecimal(feaVal[1]));
      model.setOperatorId(new BigDecimal(feaVal[2]));
         
      featureValueList.add(model);
    }
    rule.setDependencyList(featureValueList);

  }


  /**
   * @param labelName label name
   * @param ssdNodeId ssd node id
   * @return cdr rule
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readSSDRules(final String labelName, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    
    
    List<CDRRule> cdrRuleList = new ArrayList<>();
   
    Query query;
    String queryName;
    if (labelName.contains(SSDiCDMInterfaceConstants.VARCODE_SYM)) {
      query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FOR_DEF_SINGLE_LABEL);
      query.setParameter("1", ssdNodeId);
      query.setParameter("2", VarCodeLabelUtil.getParamName(labelName).toUpperCase(Locale.ENGLISH));
      queryName = JPAQueryConstants.GET_RULE_FOR_DEF_SINGLE_LABEL;
    }
    else {
      query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL);
      query.setParameter("1", ssdNodeId);
      query.setParameter("2", labelName.toUpperCase(Locale.ENGLISH));
      queryName = JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL;
    }

    List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, queryName);

    

    for (Object[] objects : ssdList) {
      CDRRule rule = DBToModelUtil.convertToCDRRule(objects, this.callFromReview);
//        VLdb2Ssd2 ssd = (VLdb2Ssd2) objects[0]
//        populateRuleinSSD(rule, ssd)
//        if (objects[3] != null) 
//          populateRulewithDependencies(objects[3].toString(), rule);// SSD-302
//        } // SSD-301
//        rule.setParameterName(labelName)
//        rule.setRefValueDCMString(objects[2] == null ? null : objects[2].toString())
//        String dcm2ssd = ssd.getDcm2ssd()
//        rule.setDcm2ssd((dcm2ssd != null) && "Y".equals(dcm2ssd))
//
//        rule.setMaturityLevel(objects[4] == null ? null : objects[4].toString())
//        VLdb2Pavast pavast = (VLdb2Pavast) objects[1]
//        rule.setParameterName(labelName)
//        rule.setValueType(CommonUtil.getValueTypeFromCategory(pavast.getCategory()))
      cdrRuleList.add(rule);
    }
    
   
//   catch(SSDiCDMInterfaceException e ) {
//    throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
//         this.dbQueryUtils.getEntityManager());
//   }
   return cdrRuleList;
 
  }

  
  /**
   * @param labObjId labObjId
   * @param revId  revId
   * @return CDRRule
   * @throws SSDiCDMInterfaceException 
   */
  public CDRRule getSingleSSDRule(final BigDecimal labObjId, final BigDecimal revId) throws SSDiCDMInterfaceException
    {
      Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FOR_LABOBJ_REVID);
      query.setParameter("1", labObjId);
      query.setParameter("2", revId);
      String queryName = JPAQueryConstants.GET_RULE_FOR_LABOBJ_REVID;
      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, queryName);
      CDRRule cdrRule = new CDRRule();

      for (Object[] objects : ssdList) {
        cdrRule = DBToModelUtil.convertToCDRRule(objects, this.callFromReview);
     }
      return cdrRule;
    }
  
  /**
   * @param labelNames - labels
   * @param dependencies - feature value list
   * @param ssdNodeId - node id
   * @return Map<String, List<CDRRule>> 
   * @throws SSDiCDMInterfaceException Exception
   */
  public Map<String, List<CDRRule>> getSSDRules(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    try {
    readReviewRuleForDependency(labelNames, dependencies, ssdNodeId);

    Query query = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FROM_TEMP);
    query.setParameter(1, ssdNodeId);
    List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.GET_RULE_FROM_TEMP);

    Map<String, List<CDRRule>> cdrRulesMap = new HashMap<>();
    setRuleinCDRModel(ssdList, cdrRulesMap, this.callFromReview);

    // SSD-334
    Query query1 = this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.GET_RULE_FROM_TEMP_VARCODE);
    query1.setParameter(1, ssdNodeId);
    List<Object[]> ssdList1 = DBResultUtil.getQueryObjArrList(query1, JPAQueryConstants.GET_RULE_FROM_TEMP_VARCODE);
    setRuleinCDRModel(ssdList1, cdrRulesMap, this.callFromReview);

    return cdrRulesMap;
    }
    catch(SSDiCDMInterfaceException e) {
      throw ExceptionUtils.createAndThrowException(e, e.getMessage(),SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, false);
    }
  }


  /**
   * @param labelNames -
   * @param dependencies -
   * @param ssdNodeId -
   * @throws SSDiCDMInterfaceException Exception
   */
  public void readReviewRuleForDependency(final List<String> labelNames, final List<FeatureValueModel> dependencies,
      final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    insertLabelsinTmpTbl(labelNames, ssdNodeId);
    insertDependencyInTemp(dependencies, null);
    deleteTempSSD();
    populateTempSSD(ssdNodeId);
  }

  /**
   * to delete temp table where rule id was selected
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  private void deleteTempSSD() throws SSDiCDMInterfaceException {
    try {
      Query query = this.dbQueryUtils.getEntityManager().createNativeQuery(JPAQueryConstants.DELETE_FROM_TEMP_SSD2);
      DBResultUtil.executeQueryUpdate(query, JPAQueryConstants.DELETE_FROM_TEMP_SSD2);
      Query query1 =
          this.dbQueryUtils.getEntityManager().createNativeQuery(JPAQueryConstants.DELETE_FROM_TEMP_SSD2_VARCODE);
      DBResultUtil.executeQueryUpdate(query1, JPAQueryConstants.DELETE_FROM_TEMP_SSD2_VARCODE);
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }

  /**
   * @param objId1 -
   * @throws SSDiCDMInterfaceException E
   */
  private void populateTempSSD(final BigDecimal objId1) throws SSDiCDMInterfaceException {
    try {

      Query query =
          this.dbQueryUtils.getEntityManager().createNamedQuery(JPAQueryConstants.T_SSD2_TEMP_FEAVAL_POPULATE_TEMP_SSD);
      query.setParameter("objId1", objId1);
      DBResultUtil.executeQueryUpdate(query, JPAQueryConstants.T_SSD2_TEMP_FEAVAL_POPULATE_TEMP_SSD);

      // Integer test = (Integer) query.getSingleResult()
      // System.out.println(test)
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }
 

  /**
   * @return the dbQueryUtils
   */
  public DBQueryUtils getDbQueryUtils() {
    return this.dbQueryUtils;
  }


  /**
   * @param callFromReview the callFromReview to set
   */
  public void setCallFromReview(final boolean callFromReview) {
    this.callFromReview = callFromReview;
  }

  /**
   *
   * @return callFromReview
   */
  public boolean getCallFromReview() {
   return this.callFromReview ;
  }
}
