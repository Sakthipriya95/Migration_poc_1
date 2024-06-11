package com.bosch.caltool.icdm.ws.rest.client.uc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UseCaseSectionResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Usecase Section
 *
 * @author MKL2COB
 */
public class UseCaseSectionServiceClient extends AbstractRestServiceClient {

  private static final IMapper UCS_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((UseCaseSectionResponse) obj).getUcSectionSet());
    return changeDataList;
  };

  /**
   * Constructor
   */
  public UseCaseSectionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_UC, WsCommonConstants.RWS_USECASESECTION);
  }

  /**
   * Get all Usecase Section records in system
   *
   * @return Map. Key - id, Value - UseCaseSection object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UseCaseSection> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all Usecase Section records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, UseCaseSection>> type = new GenericType<Map<Long, UseCaseSection>>() {};
    Map<Long, UseCaseSection> retMap = get(wsTarget, type);
    LOGGER.debug("Usecase Section records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Usecase Section using its id
   *
   * @param objId object's id
   * @return UseCaseSection object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseSection getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, UseCaseSection.class);
  }

  /**
   * Create a Usecase Section record
   *
   * @param obj object to create
   * @return created UseCaseSection object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseSectionResponse create(final UseCaseSection obj) throws ApicWebServiceException {
    return create(getWsBase(), obj, UseCaseSectionResponse.class, UCS_MAPPER);
  }


  /**
   * Update a Usecase Section record
   *
   * @param obj object to update
   * @return updated UseCaseSection object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseSection update(final UseCaseSection obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }


}
