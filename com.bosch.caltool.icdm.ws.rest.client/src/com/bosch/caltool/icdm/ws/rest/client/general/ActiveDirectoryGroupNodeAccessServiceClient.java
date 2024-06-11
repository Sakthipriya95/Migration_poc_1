package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for ActiveDirectoryGroupNodeAccess
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupNodeAccessServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public ActiveDirectoryGroupNodeAccessServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ACTIVEDIRECTORYGROUPNODEACCESS);
  }


  /**
   * Update a ActiveDirectoryGroupNodeAccess record
   *
   * @param obj object to update
   * @return updated ActiveDirectoryGroupNodeAccess object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroupNodeAccess update(final ActiveDirectoryGroupNodeAccess obj)
      throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a ActiveDirectoryGroupNodeAccess record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final ActiveDirectoryGroupNodeAccess obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

  /**
   * Create Active Directory Access Group entry
   *
   * @param adGroupName group Name
   * @param adGroupSID SID
   * @param nodeId Node ID
   * @param nodeType Node Type
   * @return Model
   * @throws ApicWebServiceException e
   */
  public ActiveDirectoryGroupNodeAccess createADGroupAccess(final String adGroupName, final String adGroupSID,
      final long nodeId, final String nodeType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_APICCREATEADGROUPACCESS)
        .queryParam(WsCommonConstants.RWS_PIDCADGRPDETAIL, adGroupName)
        .queryParam(WsCommonConstants.RWS_PIDCADGRPSID, adGroupSID)
        .queryParam(WsCommonConstants.RWS_PIDCADGRPACCESSNODEID, nodeId)
        .queryParam(WsCommonConstants.RWS_PIDCADGRPACCESSNODETYPE, nodeType);
    GenericType<ActiveDirectoryGroupNodeAccess> type = new GenericType<ActiveDirectoryGroupNodeAccess>() {};
    return create(wsTarget, adGroupName, type);
  }

  /**
   * Get node access by Node ID
   *
   * @param nodeId Node ID
   * @return map
   * @throws ApicWebServiceException exception
   */
  public Map<Long, List<ActiveDirectoryGroupNodeAccess>> getNodeAccessByNodeId(final long nodeId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_AD_GRP_ACCESS_BY_NODE_ID)
        .queryParam(WsCommonConstants.RWS_QP_NODE_ID, nodeId);
    GenericType<Map<Long, List<ActiveDirectoryGroupNodeAccess>>> type =
        new GenericType<Map<Long, List<ActiveDirectoryGroupNodeAccess>>>() {};
    return get(wsTarget, type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected <O> String getObjectIdentifier(final O model) {
    ActiveDirectoryGroupNodeAccess acc = (ActiveDirectoryGroupNodeAccess) model;
    return "Node Type = '" + acc.getNodeType() + "', Node ID = '" + acc.getNodeId() + "'";
  }
}
