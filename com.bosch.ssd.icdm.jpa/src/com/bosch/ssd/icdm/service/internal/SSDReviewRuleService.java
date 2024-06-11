/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Query;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.common.utility.DBResultUtil;
import com.bosch.ssd.icdm.common.utility.VarCodeLabelUtil;
import com.bosch.ssd.icdm.constants.JPAQueryConstants;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.entity.VLdb2Pavast;
import com.bosch.ssd.icdm.entity.VLdb2Ssd2;
import com.bosch.ssd.icdm.entity.keys.VLdb2Ssd2PK;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDRuleTypeSubType;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.utils.DBToModelUtil;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDServiceMethod;
import com.bosch.ssd.icdm.service.utility.SSDReviewRulesUtil;
import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * Service Class to handle all service calls related to review rules module
 *
 * @author SSN9COB
 */
public class SSDReviewRuleService implements SSDServiceMethod {

  private final SSDNodeInfoAccessor ssdNodeInfo;
  private String ssdLabelTyp;
  private String ssdLabelSubTyp;
  private final BigDecimal scope = new BigDecimal(3);
  private final SSDReviewRulesUtil reviewRulesUtil;

  /**
   * Default entry point for all review rules calls
   *
   * @param ssdNodeInformation Login Service Instace
   */
  public SSDReviewRuleService(final SSDNodeInfoAccessor ssdNodeInformation) {
    this.ssdNodeInfo = ssdNodeInformation;
    this.reviewRulesUtil = new SSDReviewRulesUtil(ssdNodeInformation.getDbQueryUtils());
  }


  /**
   * @return the reviewRulesUtil
   */
  public SSDReviewRulesUtil getReviewRulesUtil() {
    return this.reviewRulesUtil;
  }

  /**
   * @return the SSDLogin Service which contains the entityManager
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }

 
  /**
   * @param cdrRules      cdrrules
   * @param ssdNodeId     node id
   * @param ssdVersNodeId ssd version node id
   * @param isCompPckRule comppckrule
   * @return ssdrule
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage createMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {

      for (CDRRule model : cdrRules) {
        SSDMessage ssdMessage = insertReviewRules(model, ssdNodeId, isCompPckRule);
        if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
          return ssdMessage;
        }
      }
      // SSD-335
      if (isCompPckRule) {
        SSDMessage listMsg = createUpdatedLabelList(cdrRules, ssdVersNodeId);
        if (!listMsg.equals(SSDMessage.SUCCESS)) {
          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
          return listMsg;
        }
      }
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
      return SSDMessage.SUCCESS;

    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
  }

  /**
   * to insert and update label list for review rules TODO - ssdVersNodeId to be verified with icdm team
   *
   * @param model         - model
   * @param ssdNodeId     - node id
   * @param ssdVersNodeId - ssd version id
   * @param isCompPckRule - comppckRule
   * @return ssd message
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage createSSDRule(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal ssdVersNodeId,
      final boolean isCompPckRule) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      SSDMessage ssdMessage = insertReviewRules(model, ssdNodeId, isCompPckRule);
      if (ssdMessage.equals(SSDMessage.SUCCESS)) {
        // SSD-335
        // if rule is not created before, create and update the label list
        // TODO: Get test case for CompPckg
        if (isCompPckRule) {
          List<CDRRule> cdrRules = new ArrayList<>();
          cdrRules.add(model);
          SSDMessage listMsg = createUpdatedLabelList(cdrRules, ssdVersNodeId);
          if (!listMsg.equals(SSDMessage.SUCCESS)) {

            ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
                getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
            return listMsg;
          }
        }

        ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
            getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
        return ssdMessage;
      }
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);


      return ssdMessage;

    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
  }

  /**
   * to create an updated labellist
   *
   * @param cdrRules - to get the labels
   * @param nodeID   - noe id on which the labels have to be created
   * @return -
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessage createUpdatedLabelList(final List<CDRRule> cdrRules, final BigDecimal nodeID)
      throws SSDiCDMInterfaceException {
    int result = 0;
    Object[] obj = null;

    this.reviewRulesUtil.insertIntoTemp(cdrRules, nodeID);
    List<Object[]> resultList = this.reviewRulesUtil.createLabelList(nodeID);
    if ((resultList != null) && !resultList.isEmpty()) {
      obj = resultList.get(0);
      result = (int) obj[0];
      if ((result == 1) || (result == 0)) {
        SSDiCDMInterfaceLogger.logMessage("Labellist creation done / not needed " + result, ILoggerAdapter.LEVEL_INFO,
            null);
        return SSDMessage.SUCCESS;
      }
    }
    String errorResponse = ": " + result;
    if ((obj != null) && (obj.length > 0)) {
      errorResponse += ": " + obj[1];
    }
    SSDiCDMInterfaceLogger.logMessage("Labellist creation failed " + errorResponse, ILoggerAdapter.LEVEL_INFO, null);
    return SSDMessage.LABELLISTCREATIONFAILED;
  }

  /**
   * Method to validate the rule and insert it into the database
   *
   * @param model                - to be filled
   * @param ssdNodeId            -
   * @param isCompPckRule        -
   * @param ssdReviewRuleService service
   * @return - {@link SSDMessage}
   * @throws SSDiCDMInterfaceException exception return - 0 when rule gets created and populate CDRModel with ruleid ==
   *                                     -1 when label is not defined in SSD == -2 when rule validation fails (eg-
   *                                     insert called when update should be called, or labels other than value type are
   *                                     passed) == -3 when rule already exist for given label
   */
  private SSDMessage insertReviewRules(final CDRRule model, final BigDecimal ssdNodeId, final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    BigDecimal labelId = isLabelPresent(model.getParameterName());
    SSDMessage result;
    if (labelId.intValue() != -1) {
      model.setLabelId(labelId);
      result = this.reviewRulesUtil.createRuleForLabel(model, ssdNodeId, isCompPckRule, getScope());
    }
    else {
      setTypSubTyp(model);
      SSDMessage validationResult = getCreateLabelResult(model);
      if (validationResult.getCode() == 1) {
        result = this.reviewRulesUtil.createRuleForLabel(model, ssdNodeId, isCompPckRule, getScope());
      }
      else {
        return validationResult;
      }
    }
    return result;
  }

  /**
   * Checks if the label is present in SSD or not
   *
   * @param paramName - label from icdm
   * @return - if label present in SSD then labId or return -1
   */
  public BigDecimal isLabelPresent(final String paramName) {
    VLdb2Pavast pavast = this.reviewRulesUtil.labelPresent(paramName);
    return Objects.isNull(pavast) ? new BigDecimal(-1) : pavast.getLabId();
  }

  /**
   * Sets the type and subType for the label based on the Label Types
   *
   * @param model - {@link CDRRule}
   * @return - {@link SSDMessage}
   */
  private SSDMessage setTypSubTyp(final CDRRule model) {
    // provides the type,subtype for the 'VALUE' type
    if (model.getValueType().equals(SSDiCDMInterfaceConstants.VALUE)) {
      setSsdLabelTyp(SSDRuleTypeSubType.VALUE.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.VALUE.getSubType());
    }
    else
    // provides the type,subtype for the 'VAL_BLK' type
    if (model.getValueType().equals(SSDiCDMInterfaceConstants.VAL_BLK)) {
      setSsdLabelTyp(SSDRuleTypeSubType.VAL_BLK.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.VAL_BLK.getSubType());
    }
    else
    // provides the type,subtype for the 'ASCII' type
    if (model.getValueType().equalsIgnoreCase(SSDiCDMInterfaceConstants.ASCII)) {
      setSsdLabelTyp(SSDRuleTypeSubType.ASCII.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.ASCII.getSubType());
    }
    else
    // provides the type,subtype for the 'CURVE' type
    if (model.getValueType().equalsIgnoreCase(SSDiCDMInterfaceConstants.CURVE)) {
      setSsdLabelTyp(SSDRuleTypeSubType.CURVE.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.CURVE.getSubType());
    }
    else
    // provides the type,subtype for the 'MAP' type
    if (model.getValueType().equalsIgnoreCase(SSDiCDMInterfaceConstants.MAP)) {
      setSsdLabelTyp(SSDRuleTypeSubType.MAP.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.MAP.getSubType());
    }
    else
    // provides the type,subtype for the 'VAL_BLK' type
    if (model.getValueType().equalsIgnoreCase(SSDiCDMInterfaceConstants.AXIS_PTS)) {
      setSsdLabelTyp(SSDRuleTypeSubType.AXIS_PTS.getRuleType());
      setSsdLabelSubTyp(SSDRuleTypeSubType.AXIS_PTS.getSubType());
    }
    else {
      return SSDMessage.VALUETYPNOTSUPPORTED;
    }
    return SSDMessage.SUCCESS;
  }

  /**
   * Validates the label and inserts it in LDB
   *
   * @param model - to be filled
   * @return - messages if the label is created successfully or not
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage createLabel(final CDRRule model) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      return getCreateLabelResult(model);
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
  }


  /**
   * @param model
   * @return
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessage getCreateLabelResult(final CDRRule model) throws SSDiCDMInterfaceException {
    if ((model.getParameterName() != null) && (model.getLabelFunction() != null) && (model.getValueType() != null)) {
      SSDMessage validateResult = setTypSubTyp(model);
      if (validateResult.getCode() == 0) {
        return insertLabel(model, this.ssdLabelTyp, this.ssdLabelSubTyp);
      }
      return SSDMessage.VALUETYPNOTSUPPORTED;
    }
    SSDiCDMInterfaceLogger.logMessage("Label not created as Function,ValueType is not provided for the label",
        ILoggerAdapter.LEVEL_INFO, null);

   return SSDMessage.MISSINGVALUES;
  }

  /**
   * Inserts the label in SSD with validations
   *
   * @param model  CDRRule Model
   * @param typ    - type for the label
   * @param subTyp - subType of the label
   * @return -3 - if the label contains invalid character -1 - if the lab id for the label is not defined or if error
   *         occurs while inserting the label in SSD or if the label is already present
   * @throws SSDiCDMInterfaceException exception
   */
  private SSDMessage insertLabel(final CDRRule model, final String typ, final String subTyp)
      throws SSDiCDMInterfaceException {
    // validates and inserts the data in SSD
    SSDMessage validationResult = insertLabelinSSD(model, typ, subTyp);
    if (validationResult == SSDMessage.CREATESTRUCTURELABELANDPROCEED) {
      return createAndInsertStructureModel(model, typ, subTyp);
    }
    // For actual insert scenario when the label is normal or has iunion defined in ldb
    if (validationResult.getCode() == 1) {
      SSDiCDMInterfaceLogger.logMessage("Label Created : " + model.getParameterName(), ILoggerAdapter.LEVEL_INFO, null);
    
      return getLabelId(model);
    }
    return validationResult;
  }

  /**
   * Insert Label in SSD And return the status
   *
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessage insertLabelinSSD(final CDRRule model, final String typ, final String subTyp)
      throws SSDiCDMInterfaceException {
    if (this.reviewRulesUtil.labelPresent(model.getParameterName()) == null) {
      // checks if invalid character is there in label
      Pattern patternMatcher = Pattern.compile(SSDiCDMInterfaceConstants.REGEX_PATTERN);
      if (patternMatcher.matcher(model.getParameterName().toUpperCase(Locale.ENGLISH)).find()) {
        SSDiCDMInterfaceLogger.logMessage("ErrorMsg :Invalid Character in the label." + model.getParameterName(),
            ILoggerAdapter.LEVEL_INFO, null);
        return SSDMessage.INVALIDLABEL;
      }
     
      // loads the label details in temporary table
      this.reviewRulesUtil.insertLabelInTemp(model.getParameterName(), typ, subTyp, model.getLabelFunction(),
          model.getParamClass());
      // validates and inserts the data in SSD
      return this.reviewRulesUtil.insertInSSD(model.getParameterName());
    }
    SSDiCDMInterfaceLogger.logMessage("Label is already present in SSD  : " + model.getParameterName(),
        ILoggerAdapter.LEVEL_INFO, null);
    return SSDMessage.LABELEXISTS;
  }

  /**
   * Create Structure Label CDRRUle model and isnert in LDB
   *
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessage createAndInsertStructureModel(final CDRRule model, final String typ, final String subTyp)
      throws SSDiCDMInterfaceException {
    // Create new label
    CDRRule structureLabelRule = new CDRRule();
    structureLabelRule.setParameterName(model.getParameterName().split(SSDiCDMInterfaceConstants.DOUBLE_SLASH)[0]);
    structureLabelRule.setParamClass(model.getParamClass());
    structureLabelRule.setLabelFunction(model.getLabelFunction());
    if (insertStructureLabel(structureLabelRule, "Cint", "KgG") == SSDMessage.LABELCREATED) {
      // reinsert new parameter with structure
      return insertLabel(model, typ, subTyp);
    }
    // proper error message
    return SSDMessage.LABELVALIDATIONFAILED;
  }

  /**
   * To insert the label (Structure) in SSD
   *
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessage insertStructureLabel(final CDRRule model, final String typ, final String subTyp)
      throws SSDiCDMInterfaceException {
    // loads the label details in temporary table
    this.reviewRulesUtil.insertLabelInTemp(model.getParameterName(), typ, subTyp, model.getLabelFunction(),
        model.getParamClass());
    // validates and inserts the data in SSD
    SSDMessage validationResult = insertLabelinSSD(model, typ, subTyp);
    if (validationResult.getCode() == 1) {
      SSDiCDMInterfaceLogger.logMessage("Structure Label Created " + model.getParameterName(),
          ILoggerAdapter.LEVEL_INFO, null);
            
      return getLabelId(model);
    }
    return validationResult;
  }

  /**
   * Method to set labelid from DB to the model
   */
  private SSDMessage getLabelId(final CDRRule model) {
    BigDecimal labId = this.reviewRulesUtil.getLabelId(model.getParameterName());
    if (labId != null) {
      model.setLabelId(labId);
      return SSDMessage.LABELCREATED;
    }
    // proper error message
    return SSDMessage.LABELVALIDATIONFAILED;
  }

  /**
   * @param cdrRules rule
   * @return CDR rule delete information
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessage deleteReviewRules(final List<CDRRule> cdrRules) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      for (CDRRule rule : cdrRules) {
        SSDMessage ssdMessage = doDelete(rule);
        if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
          return ssdMessage;
        }
      }
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
      return SSDMessage.SUCCESS;
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }
  }

  /**
   * deletion logi for a rule
   *
   * @param rule -
   * @return -
   */
  private SSDMessage doDelete(final CDRRule rule) {
    try {
      if ((this.reviewRulesUtil.getLatestRevId(rule.getRuleId()).equals(rule.getRevId())) &&
          (this.reviewRulesUtil.doDelete(rule.getRuleId(), rule.getRevId()) == 1)) {
        return SSDMessage.SUCCESS;
      }

      return SSDMessage.NOTLATESTRULE;
    }
    catch (Exception e) {
      return SSDMessage.RULENOTEXISTS;
    }

  }

  /**
   * to update an exiting review / ssd rule
   * @param model     CDR rule model
   * @param ssdNodeId ssd Node
   * @return SSD Message
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage updateSSDRule(final CDRRule model, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      SSDMessage ssdMessage = validateUpdateRules(model, ssdNodeId, this.scope);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),
          !ssdMessage.equals(SSDMessage.SUCCESS));

      return ssdMessage;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
  }

  /**
   * Method to update the rules
   *
   * @param model     - cdr model
   * @param ssdNodeId - ssd node id
   * @param ssdScope  -ssd scope
   * @return - ssd message
   * @throws SSDiCDMInterfaceException exception {@link SSDiCDMInterfaceException}
   */
  public SSDMessage validateUpdateRules(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal ssdScope)
      throws SSDiCDMInterfaceException {
    if ((model.getRuleId() != null) && (model.getRevId() != null)) {

      VLdb2Ssd2PK key = new VLdb2Ssd2PK();
      key.setLabObjId(model.getRuleId());
      key.setRevId(model.getRevId());

      VLdb2Ssd2 oldSSD = this.ssdNodeInfo.getCurrentEntityManager().find(VLdb2Ssd2.class, key);

      if (oldSSD == null) {
        return SSDMessage.NORULETOUPDATE;
      }
      else if ("Y".equals(oldSSD.getHistorie())) {
        return SSDMessage.OUTDATEDRULE;
      }

      // SSD-335
      SSDMessage validationResult = this.reviewRulesUtil.validateModel(model, ssdNodeId, false, ssdScope);
      if (validationResult.getCode() == 0) {
        // added 'false' param for change in
        // --SSD V3.2.1
        VLdb2Ssd2 ssd = this.reviewRulesUtil.doUpdate(model, oldSSD, ssdNodeId, false);
        // added this to update the text when unicode character is used
        this.ssdNodeInfo.getCurrentEntityManager().refresh(ssd);
        model.setRevId(model.getRevId().add(BigDecimal.ONE));
        return SSDMessage.SUCCESS;
      }
      return validationResult;

    }
    return SSDMessage.NORULETOUPDATE;
  }


  /**
   * @param cdrRules  rules
   * @param ssdNodeId nodeID
   * @return SSD Interface Message
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage updateMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {

      for (CDRRule model : cdrRules) {
        SSDMessage ssdMessage = validateUpdateRules(model, ssdNodeId, this.scope);
        if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
          return ssdMessage;
        }
      }
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
      return SSDMessage.SUCCESS;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
  }

  /**
   * method to read review rule based on node id
   *
   * @param labelNames label name
   * @param ssdNodeId  nodeId
   * @return SSDRule
   * @throws SSDiCDMInterfaceException exception
   */

  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
    try {
      boolean hasVarcodeLable = this.reviewRulesUtil.insertLabelsinTmpTbl(labelNames, ssdNodeId);
      Query query = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
          .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_LABEL_LIST);
      query.setParameter(1, ssdNodeId);
      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, JPAQueryConstants.GET_RULE_FOR_LABEL_LIST);
      Map<String, List<CDRRule>> cdrRulesMap = new HashMap<>();
      this.reviewRulesUtil.setRuleinCDRModel(ssdList, cdrRulesMap, false);

      // SSD-334 - only if varcode labels are provided run this query
      if (hasVarcodeLable) {
        Query query1 = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
            .createNamedQuery(JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST);
        query1.setParameter(1, ssdNodeId);
        List<Object[]> ssdList1 =
            DBResultUtil.getQueryObjArrList(query1, JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST);
        this.reviewRulesUtil.setRuleinCDRModel(ssdList1, cdrRulesMap, false);
      }

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
      return cdrRulesMap;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }
  }

  
  /**
   * * Method to read review rules from database for a single label
   *
   * @param labelName label
   * @param ssdNodeId node
   * @return CDRRule
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readSSDRules(final String labelName, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
    List<CDRRule> cdrRuleList = new ArrayList<>();
    try {
      Query query;
      String queryName;
      if (labelName.contains(SSDiCDMInterfaceConstants.VARCODE_SYM)) {
        query = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
            .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_DEF_SINGLE_LABEL);
        query.setParameter("1", ssdNodeId);
        query.setParameter("2", VarCodeLabelUtil.getParamName(labelName).toUpperCase(Locale.ENGLISH));
        queryName = JPAQueryConstants.GET_RULE_FOR_DEF_SINGLE_LABEL;
      }
      else {
        query = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
            .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL);
        query.setParameter("1", ssdNodeId);
        query.setParameter("2", labelName.toUpperCase(Locale.ENGLISH));
        queryName = JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL;
      }

      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(query, queryName);


      for (Object[] objects : ssdList) {
        CDRRule rule = DBToModelUtil.convertToCDRRule(objects, this.reviewRulesUtil.getCallFromReview());
        cdrRuleList.add(rule);
      }

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);


      return cdrRuleList;
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }

  }

  /**
   * Method to read rules from database using Lab_obj_id and Rev_id For Testing purpose
   * 
   * @param labObjId labObjId
   * @param revId    RevId
   * @return CDRRule
   * @throws SSDiCDMInterfaceException 
   */
  public CDRRule getSingleSSDRule(final BigDecimal labObjId, final BigDecimal revId) throws SSDiCDMInterfaceException {
    return this.reviewRulesUtil.getSingleSSDRule(labObjId, revId);
  }

  /**
   * @param nodeId node Id
   * @return cdr rules
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRulesFromNode(final BigDecimal nodeId) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      Query tempQuery = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
          .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_SINGLE_NODE);
      tempQuery.setParameter(1, nodeId);
      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(tempQuery, JPAQueryConstants.GET_RULE_FOR_SINGLE_NODE);
      Map<String, List<CDRRule>> cdrRulesMap = new HashMap<>();
      this.reviewRulesUtil.setRuleinCDRModel(ssdList, cdrRulesMap, false);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
      return cdrRulesMap;
    }

    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }
  }



  /**
   * * To read review rules for a specific function
   *
   * @param functionName    - name of the function
   * @param functionVersion - version if specific, or null to select all versions
   * @param ssdNodeId       id
   * @return ssd rule
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRule(final String functionName, final String functionVersion,
      final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
    try {
      Query tempQuery;
      if (functionVersion == null) {
        tempQuery = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
            .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_ALL_VERSION);
      }
      else {
        tempQuery = this.reviewRulesUtil.getDbQueryUtils().getEntityManager()
            .createNamedQuery(JPAQueryConstants.GET_RULE_FOR_VERSION);
      }

      tempQuery.setParameter(1, ssdNodeId);
      tempQuery.setParameter(2, functionName);
      if (functionVersion != null) {
        tempQuery.setParameter(3, functionVersion);
      }
      List<Object[]> ssdList = DBResultUtil.getQueryObjArrList(tempQuery, (functionVersion == null)
          ? JPAQueryConstants.GET_RULE_FOR_ALL_VERSION : JPAQueryConstants.GET_RULE_FOR_VERSION);


      Map<String, List<CDRRule>> cdrRulesMap = new HashMap<>();
      this.reviewRulesUtil.setRuleinCDRModel(ssdList, cdrRulesMap, false);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
      return cdrRulesMap;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }

  }

 
  /**
   * @param labelNames   label name
   * @param dependencies dependency
   * @param ssdNodeId    nodeid
   * @return SSDRule
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      Map<String, List<CDRRule>> ssdRules = this.reviewRulesUtil.getSSDRules(labelNames, dependencies, ssdNodeId);

      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
      return ssdRules;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    catch (Exception e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getLocalizedMessage(), SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),true);
    }

  }

  /**
   * Method used to create multiple review rules and return a map containing invalid rules and their messages,
   * ComponentPackage: 1)for Comp package This method will be atomic for component Package means if any rule for the
   * label in the given component package fails then the none of rule will be created and also no component package will
   * be created and it will return a map containing list of invalid rules and their respective failure messages 2)If all
   * the rules are valid and if there is a failure in comp package createion then it will return a map containig a
   * single null key with the comp package failure message Normal SSD rules 1) for normal SSD Rules This method is non
   * atomic means-in case of normal SSD Rules means it will create the rules which are valid and returns a map containig
   * rules which are not valid
   *
   * @param cdrRules      rule
   * @param ssdNodeId     node id
   * @param ssdVersNodeId version id
   * @param isCompPckRule package rule
   * @return Map<CDRRule, SSDMessage> in case of invalid rules or failure during comp package creation
   * @throws SSDiCDMInterfaceException exception
   */

  public Map<CDRRule, SSDMessage> createValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule) throws SSDiCDMInterfaceException {

    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {

      Map<CDRRule, SSDMessage> invalidRulesMap = new HashMap<>();

      if (!isCompPckRule) {
        for (CDRRule model : cdrRules) {

          SSDMessage ssdMessage = insertReviewRules(model, ssdNodeId, isCompPckRule);
          if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
            ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
                getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
            invalidRulesMap.put(model, ssdMessage);
            return invalidRulesMap;
          }
//          else 
//            ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
//                getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false)
//          
        }
      }
      else {
        for (CDRRule model : cdrRules) {
          SSDMessage ssdMessage = insertReviewRules(model, ssdNodeId, isCompPckRule);
          if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
            ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
                getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
            invalidRulesMap.put(model, ssdMessage);
            return invalidRulesMap;
          }
         
        }

        createUpdatedLabelList(cdrRules, ssdVersNodeId, invalidRulesMap);
      }

//      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
//          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false)
     
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
    
    return Collections.emptyMap();
  }

  /**
   * @param cdrRules
   * @param ssdVersNodeId
   * @param invalidRulesMap
   * @throws SSDiCDMInterfaceException
   */
  private void createUpdatedLabelList(final List<CDRRule> cdrRules, final BigDecimal ssdVersNodeId,
      final Map<CDRRule, SSDMessage> invalidRulesMap) throws SSDiCDMInterfaceException {
    if (invalidRulesMap.size() == 0) {
      SSDMessage listMsg = createUpdatedLabelList(cdrRules, ssdVersNodeId);
      if (!listMsg.equals(SSDMessage.SUCCESS)) {
        
        invalidRulesMap.put(null, listMsg);
      }
     
  }
  }
  /**
   * * Normal SSD rules 1) for normal SSD Rules This method is non atomic means-in case of normal SSD Rules means it
   * will create the rules which are valid and returns a map containig rules which are not valid
   *
   * @param cdrRules  rules
   * @param ssdNodeId node id
   * @return Map<CDRRule, SSDMessage> in case of invalid rules or failure during comp package creation
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<CDRRule, SSDMessage> updateValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), true, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);

    try {
      Map<CDRRule, SSDMessage> invalidRulesMap = new HashMap<>();
      for (CDRRule model : cdrRules) {

        SSDMessage ssdMessage = validateUpdateRules(model, ssdNodeId, this.scope);
        if (!ssdMessage.equals(SSDMessage.SUCCESS)) {
          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true);
          invalidRulesMap.put(model, ssdMessage);
          return invalidRulesMap;
        }
//        else 
//          ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
//              getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false)
//        
      }
//      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
//          getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), true)
      
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.reviewRulesUtil.getDbQueryUtils().getEntityManager(),false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReviewRuleService.class.getSimpleName(),
        getCurrentMethodName(), false, this.reviewRulesUtil.getDbQueryUtils().getEntityManager(), false);
  
  return Collections.emptyMap();
  }

  /**
   * @param ssdLabelTyp ssd label type
   */
  private void setSsdLabelTyp(final String ssdLabelTyp) {
    this.ssdLabelTyp = ssdLabelTyp;
  }

  /**
   * @param ssdLabelSubTyp ssd label sub type
   */
  private void setSsdLabelSubTyp(final String ssdLabelSubTyp) {
    this.ssdLabelSubTyp = ssdLabelSubTyp;
  }

  /**
   * @return the scope
   */
  public BigDecimal getScope() {
    return this.scope;
  }

}
