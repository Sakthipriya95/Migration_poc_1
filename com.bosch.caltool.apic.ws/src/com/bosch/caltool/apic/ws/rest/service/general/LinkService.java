/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.LinkCommand;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ICDM_LINKS)
public class LinkService extends AbstractRestService {


  /**
   * Get the Link using id
   *
   * @param linkId link id
   * @return Link
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long linkId) throws IcdmException {

    // Create loader object
    Link ret = new LinkLoader(getServiceData()).getDataObjectByID(linkId);
    getLogger().info("LinkService.getById() completed for the linkId {}", linkId);

    return Response.ok(ret).build();
  }

  /**
   * Get all links by node
   *
   * @param nodeId node ID
   * @param nodeType Link NODE_TYPE
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_NODE)
  @CompressData
  public Response getLinksByNode(@QueryParam(value = WsCommonConstants.RWS_QP_NODE_ID) final Long nodeId,
      @QueryParam(value = WsCommonConstants.RWS_QP_NODE_TYPE) final String nodeType)
      throws IcdmException {
    LinkLoader loader = new LinkLoader(getServiceData());
    Map<Long, Link> links = loader.getLinksByNode(nodeId, nodeType);

    getLogger().info("LinkService.getLinksByNode() completed. Number of Links = {}", links.size());

    return Response.ok(links).build();
  }

  /**
   * Get all links by node
   *
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_HELP_LINKS)
  @CompressData
  public Response getHelpLinks() throws IcdmException {
    LinkLoader loader = new LinkLoader(getServiceData());
    Map<String, Link> links = loader.getHelpLinks();

    getLogger().info("LinkService.getLinksByNode() completed. Number of Links = {}", links.size());

    return Response.ok(links).build();
  }

  /**
   * Get all nodes with link
   *
   * @param nodeType Link NODE_TYPE
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_NODE_WITH_LINK)
  @CompressData
  public Response getNodesWithLink(@QueryParam(value = WsCommonConstants.RWS_QP_NODE_TYPE) final String nodeType)
      throws IcdmException {
    LinkLoader loader = new LinkLoader(getServiceData());
    Set<Long> links = loader.getNodesWithLink(nodeType);

    getLogger().info("LinkService.getNodesWithLink() completed. Number of Nodes with link = {}", links.size());

    return Response.ok(links).build();
  }

  /**
   * Get all nodesIds associated with nodeType
   *
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_NODE_ID_BY_TYPE)
  @CompressData
  public Response getAllNodeIdByType() throws IcdmException {
    getLogger().info("LinkService.getAllNodeIdByType() started");
    LinkLoader loader = new LinkLoader(getServiceData());
    Map<String, Set<Long>> retmap = loader.getAllNodeIdByType();
    getLogger().info("LinkService.getAllNodeIdByType() completed. Number of items in the map = {}", retmap.size());

    return Response.ok(retmap).build();
  }

  /**
   * Service to create new Links in database
   *
   * @param links - list of links to be created
   * @return Set of Links created
   * @throws IcdmException exception in link creation
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final List<Link> links) throws IcdmException {
    ServiceData serviceData = getServiceData();
    List<AbstractSimpleCommand> linkCmdList = new ArrayList<>();

    for (Link link : links) {
      LinkCommand cmd = new LinkCommand(serviceData, link);
      linkCmdList.add(cmd);
    }
    executeCommand(linkCmdList);

    Set<Link> linkSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : linkCmdList) {
      linkSet.add(((LinkCommand) cmd).getNewData());
    }
    return Response.ok(linkSet).build();
  }

  /**
   * Service to update the existing Links
   *
   * @param links - to be updated
   * @return Set of Links updated
   * @throws IcdmException exception in link update
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final List<Link> links) throws IcdmException {
    List<AbstractSimpleCommand> linkCmdList = new ArrayList<>();
    ServiceData serviceData = getServiceData();

    for (Link link : links) {
      LinkCommand cmd = new LinkCommand(serviceData, link, true);
      linkCmdList.add(cmd);

    }
    executeCommand(linkCmdList);
    Set<Link> linkSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : linkCmdList) {
      linkSet.add(((LinkCommand) cmd).getNewData());
    }
    WSObjectStore.getLogger().info("Updating Link is completed");
    return Response.ok(linkSet).build();
  }

  /**
   * Service to Delete the exisiting links
   *
   * @param linkIdList - id of the links to be deleted
   * @return response ok
   * @throws IcdmException exception in deletion of link
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final List<Long> linkIdList)
      throws IcdmException {

    List<AbstractSimpleCommand> linkCmdList = new ArrayList<>();

    for (Long linkId : linkIdList) {
      LinkCommand cmd =
          new LinkCommand(getServiceData(), new LinkLoader(getServiceData()).getDataObjectByID(linkId), false);
      linkCmdList.add(cmd);
    }
    executeCommand(linkCmdList);
    return Response.ok().build();

  }
}
