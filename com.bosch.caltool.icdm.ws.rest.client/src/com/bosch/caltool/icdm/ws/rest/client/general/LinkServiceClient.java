/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class LinkServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public LinkServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ICDM_LINKS);
  }


  /**
   * Get link by link id
   *
   * @param id link id
   * @return Link object
   * @throws ApicWebServiceException exception in getting link
   */
  public Link getById(final long id) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, id);
    return get(wsTarget, Link.class);
  }

  /**
   * Gets all link by node.
   *
   * @param nodeType iCDM Link Node Type
   * @param nodeId node ID
   * @return Map of links. Key - link id, value link
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<Long, Link> getAllLinksByNode(final Long nodeId, final IModelType nodeType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_NODE)
        .queryParam(WsCommonConstants.RWS_QP_NODE_TYPE, nodeType.getTypeCode())
        .queryParam(WsCommonConstants.RWS_QP_NODE_ID, nodeId);

    GenericType<Map<Long, Link>> users = new GenericType<Map<Long, Link>>() {};
    Map<Long, Link> retMap = get(wsTarget, users);

    LOGGER.debug("iCDM Links loaded. Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * @return Map of help links.Key - nodeType, Value - wiki link
   * @throws ApicWebServiceException exception while fetching help links
   */
  public Map<String, Link> getHelpLinks() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_HELP_LINKS);
    GenericType<Map<String, Link>> helpLinks = new GenericType<Map<String, Link>>() {};
    Map<String, Link> retMap = get(wsTarget, helpLinks);

    LOGGER.debug("iCDM Help Links loaded. Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * Gets all nodeId by nodeType.
   *
   * @return Map of nodeId. Key - nodeType id, value Set<NodeId>
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, Set<Long>> getAllNodeIdByType() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_NODE_ID_BY_TYPE);
    GenericType<Map<String, Set<Long>>> type = new GenericType<Map<String, Set<Long>>>() {};
    Map<String, Set<Long>> retMap = get(wsTarget, type);

    LOGGER.debug("iCDM Links loaded. Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * Gets all nodeIds having iCDM links - To check if a node has link.
   *
   * @param nodeType iCDM Link Node Type
   * @return Set of Node Ids which have link(s)
   * @throws ApicWebServiceException the apic web service exception
   */
  public Set<Long> getNodesWithLink(final IModelType nodeType) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_NODE_WITH_LINK)
        .queryParam(WsCommonConstants.RWS_QP_NODE_TYPE, nodeType.getTypeCode());

    GenericType<Set<Long>> users = new GenericType<Set<Long>>() {};
    Set<Long> retSet = get(wsTarget, users);

    LOGGER.debug("Nodes with iCDM Links loaded. Number of items = {}", retSet.size());

    return retSet;
  }

  /**
   * Creates new links
   *
   * @param links - the new Links to be created
   * @return created links
   * @throws ApicWebServiceException - error during webservice call
   */
  public Set<Link> create(final List<Link> links) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Set<Link>> type = new GenericType<Set<Link>>() {};
    Set<Link> retSet = create(wsTarget, links, type);
    LOGGER.debug("New Links have been created. No of Links created : {}", retSet.size());
    return retSet;
  }

  /**
   * Updates the exisiting links
   *
   * @param links - to be updated
   * @return the updated link
   * @throws ApicWebServiceException - error during webservice call
   */
  public Set<Link> update(final List<Link> links) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Set<Link>> type = new GenericType<Set<Link>>() {};
    Set<Link> retSet = update(wsTarget, links, type);
    LOGGER.debug("Links have been edited. No of Links edited : {}", retSet.size());
    return retSet;
  }

  /**
   * Deletes the selected Links
   *
   * @param links - Links to be deleted
   * @throws ApicWebServiceException - error during webservice call
   */
  public void delete(final List<Link> links) throws ApicWebServiceException {
    delete(getWsBase(), links);
    LOGGER.debug("Links have been Deleted. No of Links deleted : {}", links.size());
  }
}
