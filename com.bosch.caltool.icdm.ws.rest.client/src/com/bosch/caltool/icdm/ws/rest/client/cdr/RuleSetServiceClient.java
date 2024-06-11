/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetInputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetOutputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class to fetch rule sets.
 *
 * @author rgo7cob
 */
// Task 263282
public class RuleSetServiceClient extends AbstractRestServiceClient {

  private static final IMapper RULESET_NODEACCESS_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((RuleSetOutputData) obj).getRuleSetOwnerNodeAccess());
    return changeDataList;
  };

  /**
   * initialize client constructor with the compli-param url
   */
  public RuleSetServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RULE_SET);
  }


  /**
   * @return the sorted set of rule sets
   * @throws ApicWebServiceException any error while reading file
   */
  public SortedSet<RuleSet> getAllRuleSets() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<SortedSet<RuleSet>> type = new GenericType<SortedSet<RuleSet>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param rulesetId rulesetId
   * @return the param and Rules
   * @throws ApicWebServiceException any error while reading file
   */
  public RuleSetRulesResponse getRuleSetParamRules(final Long rulesetId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PARAM_RULES)
        .queryParam(WsCommonConstants.RWS_QP_ELEMENT_ID, rulesetId);
    GenericType<RuleSetRulesResponse> type = new GenericType<RuleSetRulesResponse>() {};
    return get(wsTarget, type);
  }


  /**
   * Get Rule set by ID
   *
   * @param ruleSetId rule Set Id
   * @return Rule Set
   * @throws ApicWebServiceException any error while calling service
   */
  public RuleSet get(final Long ruleSetId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, ruleSetId);
    return get(wsTarget, RuleSet.class);
  }

  /**
   * Method to get rules
   * 
   * @param ruleSetId as ruleset id
   * @param ruleSetParameter ruleSetParameter
   * @return RuleSetRulesResponse
   * @throws ApicWebServiceException exception
   */
  public RuleSetRulesResponse getRules(final Long ruleSetId, final RuleSetParameter ruleSetParameter)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_SINGLE_PARAM_RULES)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, ruleSetId);
    GenericType<RuleSetRulesResponse> type = new GenericType<RuleSetRulesResponse>() {};
    return post(wsTarget, ruleSetParameter, type);
  }

  /**
   * This method is used to create Ruleset from ADMIN page Only user with special admin access rights can create a rule
   * with this service
   *
   * @param ruleSetModel as input model
   * @return RuleSet
   * @throws ApicWebServiceException exception
   */
  public RuleSetOutputData create(final RuleSetInputData ruleSetModel) throws ApicWebServiceException {
    GenericType<RuleSetOutputData> type = new GenericType<RuleSetOutputData>() {};
    return create(getWsBase(), ruleSetModel, type, RULESET_NODEACCESS_MAPPER);
  }

  /**
   * @param pidcElementId Long
   * @return RuleSet
   * @throws ApicWebServiceException Exception
   */
  public RuleSet getMandateRuleSetForPIDC(final Long pidcElementId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_MANDATORY_RULE_SET_BY_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcElementId);
    return get(wsTarget, RuleSet.class);
  }

  /**
   * @param pidcElementId pidcElementId
   * @param outputFileName String name of rule generated
   * @param outputFileDirectory output file directory
   * @return String file path
   * @throws ApicWebServiceException Exception
   */
  public String getSsdFileByPidcElement(final Long pidcElementId, final String outputFileName,
      final String outputFileDirectory)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_SSD_FILE_BY_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcElementId);
    return downloadFile(wsTarget, outputFileName, outputFileDirectory);
  }
}
