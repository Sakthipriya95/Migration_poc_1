/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.RuleSetParamInput;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class RuleSetParameterServiceClient extends AbstractRestServiceClient {


  /**
   *
   */
  public RuleSetParameterServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_RULEST_PARAM);
  }

  /**
   * Get all parameter names of the ruleset
   *
   * @param ruleSetId ruleset ID
   * @return set of parameter names
   * @throws ApicWebServiceException any error during service call
   */
  public Set<String> getAllParamNames(final Long ruleSetId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PARAM_NAMES).queryParam(WsCommonConstants.RWS_QP_RULESET_ID, ruleSetId);
    GenericType<Set<String>> type = new GenericType<Set<String>>() {};
    return get(wsTarget, type);
  }

  /**
   * Get Rule set Parameter by ID
   *
   * @param ruleSetParamId rule Set Parameter Id
   * @return RuleSetParameter
   * @throws ApicWebServiceException any error while calling service
   */
  public RuleSetParameter get(final Long ruleSetParamId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, ruleSetParamId);
    return get(wsTarget, RuleSetParameter.class);
  }

  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return created Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */

  public RuleSetParameter create(final RuleSetParameter obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Create a Attribute record
   *
   * @param ruleSetParamSet ruleSetParamSet
   * @return created Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */

  public Set<RuleSetParameter> createMultiple(final Set<RuleSetParameter> ruleSetParamSet)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_MULTIPLE_PARAM);
    GenericType<Set<RuleSetParameter>> type = new GenericType<Set<RuleSetParameter>>() {};
    return create(wsTarget, ruleSetParamSet, type);
  }

  /**
   * @param ruleSetId as ruleset ids
   * @param a2lFilePath as a2l File Id
   * @return Set<RuleSetParameter>
   * @throws ApicWebServiceException as Exception
   * @throws IOException as Exception
   */
  public Set<RuleSetParameter> createRuleSetParamUsingA2lFileID(final RuleSetParamInput ruleSetParamInput)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_MULTIPLE_RULESET_PARAM_USING_A2L);
    GenericType<Set<RuleSetParameter>> type = new GenericType<Set<RuleSetParameter>>() {};
    return post(wsTarget, ruleSetParamInput, type);
  }

  /**
   * @param ruleSetId as ruleset ids
   * @param a2lFilePath as a2l File Id
   * @return Set<RuleSetParameter>
   * @throws ApicWebServiceException as Exception
   * @throws IOException as Exception
   */
  public Set<RuleSetParameter> createRuleSetParamUsingA2l(final Long ruleSetId, final String a2lFilePath)
      throws ApicWebServiceException, IOException {
    if (a2lFilePath == null) {
      throw new ApicWebServiceException(WSErrorCodes.ICDM_ERROR, "A2L File path cannot be null.");
    }
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_MULTIPLE_RULESET_PARAM_USING_A2L);

    FormDataMultiPart multipart = new FormDataMultiPart();
    GenericType<Set<RuleSetParameter>> type = new GenericType<Set<RuleSetParameter>>() {};
    // add ruleset id to body part
    FormDataBodyPart ruleSetIdBodyPart =
        new FormDataBodyPart(WsCommonConstants.RWS_QP_RULESET_ID, ruleSetId.toString());
    multipart.bodyPart(ruleSetIdBodyPart);
    // create filedatabodypart with the a2l file
    final FileDataBodyPart a2lFilePart =
        new FileDataBodyPart(WsCommonConstants.A2L_FILE_MULTIPART, new File(a2lFilePath));
    multipart.bodyPart(a2lFilePart);

    Set<RuleSetParameter> ruleSetParameterSet = post(wsTarget, multipart, type);
    multipart.close();
    return ruleSetParameterSet;
  }

  /**
   * Delete a Attribute record
   *
   * @param ruleSetParameter object to create
   * @throws ApicWebServiceException exception while invoking service
   */

  public void delete(final RuleSetParameter ruleSetParameter) throws ApicWebServiceException {
    delete(getWsBase(), ruleSetParameter);
  }

  /**
   * Delete a Attribute record
   *
   * @param ruleSetParamSet ruleSetParamSet
   * @throws ApicWebServiceException exception while invoking service
   */
  public void deleteMultiple(final Set<RuleSetParameter> ruleSetParamSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DELETE_MULTIPLE_PARAM);
    delete(wsTarget, ruleSetParamSet);
  }

}
