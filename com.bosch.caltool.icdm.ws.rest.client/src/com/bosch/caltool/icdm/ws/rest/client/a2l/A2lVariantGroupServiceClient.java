package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lVariantGroup
 *
 * @author pdh2cob
 */
public class A2lVariantGroupServiceClient extends AbstractA2lWpRestServiceClient {


  /**
   * Constructor
   */
  public A2lVariantGroupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_VARIANT_GROUP);
  }


  /**
   * Get A2lVariantGroup using its id
   *
   * @param objId object's id
   * @return A2lVariantGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lVariantGroup get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lVariantGroup.class);
  }

  /**
   * Create a A2lVariantGroup record
   *
   * @param obj object to create
   * @param pidcA2l pidc a2l
   * @return created A2lVariantGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lVariantGroup create(final A2lVariantGroup obj, final PidcA2l pidcA2l) throws ApicWebServiceException {
    return create(getWsBase(), obj, pidcA2l);
  }

  /**
   * Update a A2lVariantGroup record
   *
   * @param obj object to update
   * @param pidcA2l
   * @return updated A2lVariantGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lVariantGroup update(final A2lVariantGroup obj, final PidcA2l pidcA2l) throws ApicWebServiceException {
    return update(getWsBase(), obj, pidcA2l);
  }

  /**
   * @param a2lVarGroup a2lVarGroup
   * @param pidcA2l
   * @throws ApicWebServiceException exception form server
   */
  public void delete(final A2lVariantGroup a2lVarGroup, final PidcA2l pidcA2l) throws ApicWebServiceException {
    delete(getWsBase(), a2lVarGroup, pidcA2l);

  }

  /**
   * @param a2lWpDefVerId a2lWpDefVerId
   * @return details required for A2LDetailsStructureModel
   * @throws ApicWebServiceException
   */
  public A2LDetailsStructureModel getA2lDetailsStructureData(final Long a2lWpDefVerId) throws ApicWebServiceException {
    LOGGER.debug("Get A2l Details structure record using A2lWorkpackgeDefinitionVersion id ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_A2L_WP_DEF_VER_ID)
        .queryParam(WsCommonConstants.RWS_A2L_WP_DEF_VER_ID, a2lWpDefVerId);
    A2LDetailsStructureModel editorData = get(wsTarget, A2LDetailsStructureModel.class);
    return editorData;
  }

  /**
   * @param a2lWpDefVerId a2lWpDefVerId
   * @return details required for A2LDetailsStructureModel
   * @throws ApicWebServiceException
   */
  public Map<Long, A2lVariantGroup> getVarGrpForWpDefVer(final Long a2lWpDefVerId) throws ApicWebServiceException {
    LOGGER.debug("Get A2l Details structure record using A2lWorkpackgeDefinitionVersion id ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VAR_GRP_A2L_WP_DEF_VER_ID)
        .queryParam(WsCommonConstants.RWS_A2L_WP_DEF_VER_ID, a2lWpDefVerId);
    GenericType<Map<Long, A2lVariantGroup>> type = new GenericType<Map<Long, A2lVariantGroup>>() {};
    Map<Long, A2lVariantGroup> a2lVarGrpMap = get(wsTarget, type);
    return a2lVarGrpMap;
  }
}
