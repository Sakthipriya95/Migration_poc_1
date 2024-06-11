/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.service.internal.SSDReviewRuleService;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.utility.SSDReviewRulesUtil;

/**
 * Controller Class for validating the input and invoking service methods in @SSDReviewRuleService
 *
 * @author SSN9COB
 */
public class SSDReviewRulesController {

  /**
   * 
   */
  private static final String NODE_ID_IS_NOT_SET = "Node Id is not set";
  /**
   * 
   */
  private static final String RULE_DETAILS_ARE_NOT_AVAILABLE = "Rule Details are not available";
  /**
   * 
   */
  private static final String NODE_ID_CANNOT_BE_NULL = "Node ID cannot be null";
  private SSDReviewRuleService reviewRulesService;
  private final SSDNodeInfoAccessor ssdNodeInfo;

  /**
   * @param ssdNodeInfo Node Info
   */
  public SSDReviewRulesController(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the nodeInformationService
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }

  /**
   * @return service instance
   */
  public SSDReviewRuleService getSSDReviewRulesService() {
    if (Objects.isNull(this.reviewRulesService)) {
      createReviewRulesService();
    }
    return this.reviewRulesService;
  }

  /**
   * create Review Rules service from the current Entity Manger
   *
   * @throws SSDiCDMInterfaceException exception
   */
  private void createReviewRulesService() {
    this.reviewRulesService = new SSDReviewRuleService(getSSDNodeInfo());
  }

  /**
   * createReviewSSDRule - Controller method to handle all validations and pass the input to service class
   *
   * @param model input
   * @param ssdNodeId nodeId
   * @param ssdVersNodeId versId
   * @param isCompPckRule flag
   * @return response
   * @throws SSDiCDMInterfaceException return - 0 when rule gets created and populate CDRModel with ruleid == -1 when
   *           label is not defined in SSD == -2 when rule validation fails (eg- insert called when update should be
   *           called, or labels other than value type are passed) == -3 when rule already exist for given label
   */
  public SSDMessage createReviewSSDRule(final CDRRule model, final BigDecimal ssdNodeId, final BigDecimal ssdVersNodeId,
      final boolean isCompPckRule)
      throws SSDiCDMInterfaceException {
    /*
     * Null ID
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(model)) {
      throw ExceptionUtils.createAndThrowException(null, "CDR Rule Model cannot be empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(model.getParameterName())) {
      throw ExceptionUtils.createAndThrowException(null, "Parameter Name cannot be Null or Empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDReviewRulesService().createSSDRule(model, ssdNodeId, ssdVersNodeId, isCompPckRule);
  }
  /**
   * @param cdrRules      cdrrules
   * @param ssdNodeId     node id
   * @param ssdVersNodeId ssd version node id
   * @param isCompPckRule comppckrule
   * @return ssdrule
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage createMultReviewSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule) throws SSDiCDMInterfaceException {
    
    /*
     * Null ID
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(cdrRules) || cdrRules.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
   
    
    return getSSDReviewRulesService().createMultSSDRules(cdrRules, ssdNodeId, ssdVersNodeId, isCompPckRule);
  }
  /**
   * Method used to create multiple review rules and return a map containing invalid rules and their messages
   * 
   * @param cdrRules rules/model
   * @param ssdNodeId node id
   * @param ssdVersNodeId version id
   * @param isCompPckRule - whether it is compackage rule
   * @return Map with rules and message
   * @throws SSDiCDMInterfaceException Exception 
   *  
   */
  

  public Map<CDRRule, SSDMessage> createValidMultSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId,
      final BigDecimal ssdVersNodeId, final boolean isCompPckRule) throws SSDiCDMInterfaceException {
    
    /*
     * Null ID
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(cdrRules) || cdrRules.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
   
   
    return getSSDReviewRulesService().createValidMultSSDRules(cdrRules, ssdNodeId, ssdVersNodeId, isCompPckRule);
  }
  /**
   * Validates the label and inserts it in LDB
   *
   * @param model - to be filled
   * @return - messages if the label is created successfully or not
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage createLabel(final CDRRule model) throws SSDiCDMInterfaceException {
    if ((model.getParameterName() != null) && (model.getLabelFunction() != null) && (model.getValueType() != null)) {
      throw ExceptionUtils.createAndThrowException(null, "Parameter Name cannot be Null or Empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDReviewRulesService().createLabel(model);
  }
  
  /**
   * Checks if the label is present in SSD or not
   *
   * @param paramName - label from icdm
   * @return - if label present in SSD then labId or return -1
   * @throws SSDiCDMInterfaceException exception may thrown
   */
  public BigDecimal isLabelPresent(final String paramName) throws SSDiCDMInterfaceException {
    if(Objects.isNull(paramName)){
      throw ExceptionUtils.createAndThrowException(null, "Parameter Name cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDReviewRulesService().isLabelPresent(paramName);
  }
  

  /**
   * @return the reviewRulesUtil
   */
  public SSDReviewRulesUtil getReviewRulesUtil() {
    return getSSDReviewRulesService().getReviewRulesUtil();
  }
  
  /**
   * @return the scope
   */
  public BigDecimal getScope() {
    return getSSDReviewRulesService().getScope();
  }

  /**
   * * Method to read review rules from database for a single label based on node id
   *
   * @param labelName label
   * @param ssdNodeId node
   * @return CDRRule
   * @throws SSDiCDMInterfaceException Exception
   */
  public List<CDRRule> readSSDRules(final String labelName, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    /*
     * Null LabelName
     */
    if (Objects.isNull(labelName)) {
      throw ExceptionUtils.createAndThrowException(null, "Label Name cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Null ID
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    
    return getSSDReviewRulesService().readSSDRules(labelName, ssdNodeId);
  }
  
  /**
   * method to read review rule based on node id
   *
   * @param labelNames label name
   * @param ssdNodeId nodeId
   * @return SSDRule
   * @throws SSDiCDMInterfaceException exception
   */

  public Map<String, List<CDRRule>> readSSDRule(final List<String> labelNames, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
   
      /*
       * Null or Empty parameters/labels list
       */
      if (Objects.isNull(labelNames) || labelNames.isEmpty()) {
        throw ExceptionUtils.createAndThrowException(null, "Label Names List cannot be null or empty",
            SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
      }
      /*
       * Node Id is not set for the service call
       */
      if (Objects.isNull(ssdNodeId)) {
        throw ExceptionUtils.createAndThrowException(null, NODE_ID_IS_NOT_SET,
            SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
      }
      return getSSDReviewRulesService().readSSDRule(labelNames, ssdNodeId);
  }
  
  /**
   * Method to read SSDRules From Node
   * 
   * @param nodeId node Id
   * @return cdr rules
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRulesFromNode(final BigDecimal nodeId) throws SSDiCDMInterfaceException {
    
    // Node Id is not set 
    if (Objects.isNull(nodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_IS_NOT_SET,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
     
    }
    return getSSDReviewRulesService().readSSDRulesFromNode(nodeId);
  }
  
  /**
   * Method to read rules for dependency based on node id
   * 
   * @param labelNames label name
   * @param dependencies dependency
   * @param ssdNodeId nodeid
   * @return SSDRule
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    
    /*
     * Null or Empty parameters/labels list
     */
    if (Objects.isNull(labelNames) || labelNames.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, "Label Names List cannot be null or empty",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Null Dependencies list. (FV List can be empty)
     */
    if (Objects.isNull(dependencies)) {
      throw ExceptionUtils.createAndThrowException(null, "Dependencies List cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    /*
     * Node Id is not set for the service call
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_IS_NOT_SET,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    
    return getSSDReviewRulesService().readSSDRuleForDependency(labelNames, dependencies, ssdNodeId);
  }
  
  /**
   * * To read review rules for a specific function based on node id
   *
   * @param functionName - name of the function
   * @param functionVersion - version if specific, or null to select all versions
   * @param ssdNodeId id
   * @return ssd rule
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, List<CDRRule>> readSSDRuleFromFunction(final String functionName, final String functionVersion,
      final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    
    /*
     * Function name is not set for the service call
     */
    if (Objects.isNull(functionName)) {
      throw ExceptionUtils.createAndThrowException(null, "Function name cannot be null",
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
   /*
     * Node Id is not set for the service call
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_IS_NOT_SET,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    
    return getSSDReviewRulesService().readSSDRule(functionName, functionVersion, ssdNodeId);
  }
  
  /**
   * To update an exiting review/ssd rule
   * @param model     CDR rule model
   * @param ssdNodeId ssd Node
   * @return SSD Message
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessage updateReviewSSDRule(final CDRRule model, final BigDecimal ssdNodeId) throws SSDiCDMInterfaceException {
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    if (Objects.isNull(model) || Objects.isNull(model.getRuleId())) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    
    return getSSDReviewRulesService().updateSSDRule(model, ssdNodeId);
  }
  /**
   * @param cdrRules  rules
   * @param ssdNodeId nodeID
   * @return SSD Interface Message
   * @throws SSDiCDMInterfaceException exception
   */

  public SSDMessage updateMultReviewSSDRules(final List<CDRRule> cdrRules, final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    /*
     * Null ID
     */
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(cdrRules) || cdrRules.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
   return getSSDReviewRulesService().updateMultSSDRules(cdrRules, ssdNodeId);
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
    if (Objects.isNull(ssdNodeId)) {
      throw ExceptionUtils.createAndThrowException(null, NODE_ID_CANNOT_BE_NULL,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    else if (Objects.isNull(cdrRules) || cdrRules.isEmpty()) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDReviewRulesService().updateValidMultSSDRules(cdrRules, ssdNodeId);
  }
  
  /**
   * To delete Rules
   * @param cdrRules rule
   * @return CDR rule delete information
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessage deleteReviewRules(final List<CDRRule> cdrRules) throws SSDiCDMInterfaceException {
    
    if (cdrRules == null || (cdrRules.isEmpty()) ) {
      throw ExceptionUtils.createAndThrowException(null, RULE_DETAILS_ARE_NOT_AVAILABLE,
          SSDiCDMInterfaceErrorCodes.INVALID_INPUT_EXCEPTION,true);
    }
    return getSSDReviewRulesService().deleteReviewRules(cdrRules);
  }
}
