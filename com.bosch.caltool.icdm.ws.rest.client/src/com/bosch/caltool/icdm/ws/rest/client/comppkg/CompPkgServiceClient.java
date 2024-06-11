/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamInput;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.model.comppkg.CompPkgResponse;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CompPkgServiceClient extends AbstractRestServiceClient {


  private static final IMapper COMPPKG_CREATION_MAPPER =
      obj -> Arrays.asList(((CompPkgResponse) obj).getCompPackage(), ((CompPkgResponse) obj).getNodeAccess());

  /**
   * Comstructor
   */
  public CompPkgServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_COMP, WsCommonConstants.RWS_COMP_PKG);
  }

  /**
   * Fetch FC to WP definitions
   *
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<CompPackage> getAll() throws ApicWebServiceException {

    LOGGER.debug("Loading all CompPackage");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<SortedSet<CompPackage>> type = new GenericType<SortedSet<CompPackage>>() {};

    SortedSet<CompPackage> compPkgResponse = get(wsTarget, type);

    LOGGER.debug("CompPackage loaded. No. of CompPackage : {}", compPkgResponse.size());

    return compPkgResponse;

  }

  /**
   * Create a TCompPkg record
   *
   * @param obj object to create
   * @return created CompPkgBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgResponse create(final CompPackage obj) throws ApicWebServiceException {
    return create(getWsBase(), obj, CompPkgResponse.class, CompPkgServiceClient.COMPPKG_CREATION_MAPPER);
  }

  /**
   * Update a TCompPkg record
   *
   * @param obj object to update
   * @return updated CompPkgBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPackage update(final CompPackage obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Gets the CompPackage.
   *
   * @param compPkgId the comp package id
   * @return the comp package
   * @throws ApicWebServiceException the apic web service exception
   */

  public CompPackage getCompPackageById(final Long compPkgId) throws ApicWebServiceException {
    LOGGER.debug("Get CompPackage record for ID = {}", compPkgId);
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_COMP_PKG_OBJ).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, compPkgId);
    return get(wsTarget, CompPackage.class);
  }


  /**
   * @param compPackage compPackage
   * @return ReviewRule Map
   * @throws ApicWebServiceException Exception
   */
  public Map<String, List<ReviewRule>> getBySSDNodeId(final CompPackage compPackage) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_SSD_NODE_ID);
    GenericType<Map<String, List<ReviewRule>>> type = new GenericType<Map<String, List<ReviewRule>>>() {};
    return post(wsTarget, compPackage, type);
  }

  /**
   * @param paramName paramName set
   * @return CompPkgParameter Map
   * @throws ApicWebServiceException Exception
   */
  public Map<String, CompPkgParameter> getCompPkgParam(final Set<String> paramName, final Long comppkgId)
      throws ApicWebServiceException {
    CompPkgParamInput compPkgParamInput = new CompPkgParamInput();
    compPkgParamInput.setParamName(paramName);
    compPkgParamInput.setCompPkgId(comppkgId);
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_COMP_PKG_PARAM);
    GenericType<Map<String, CompPkgParameter>> type = new GenericType<Map<String, CompPkgParameter>>() {};
    return post(wsTarget, compPkgParamInput, type);
  }

  /**
   * @param compPkgId compPkgId
   * @return CompPkgRuleResponse
   * @throws ApicWebServiceException Exception
   */
  public CompPkgRuleResponse getCompPkgRule(final Long compPkgId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_COMP_PKG_ID, compPkgId);
    GenericType<CompPkgRuleResponse> type = new GenericType<CompPkgRuleResponse>() {};
    return get(wsTarget, type);
  }
}
