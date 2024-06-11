/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.util.LoaderProvider;
import com.bosch.caltool.icdm.common.bo.apic.NodeType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author rgo7cob
 */
public class NodeAccessInfoResolver extends AbstractSimpleBusinessObject {

  /**
   * node type - Pidc, USE CASE, QUES,FUNCTION, RULE SET.
   */
  protected final NodeType nodeType;
  /**
   * node name
   */
  protected List<String> nodeNameList;

  /**
   * not found node name list
   */
  protected List<String> missingNodes = new ArrayList<>();
  /**
   * user id.- NT user id.
   */
  protected final String userId;


  /**
   * loader class injected at run time
   */
  protected final AbstractBusinessObject loader;


  /**
   * type code from UI
   */
  protected final String typeCode;

  /**
   * @param typeCode - type code from the MODEL type
   * @param nodeName nodeName
   * @param userId userId
   * @param serviceData serviceData
   * @throws IcdmException IcdmException
   */
  public NodeAccessInfoResolver(final String typeCode, final java.util.List<String> nodeNameList, final String userId,
      final ServiceData serviceData) throws IcdmException {

    super(serviceData);

    this.nodeType = NodeType.getNodeType(typeCode);

    this.typeCode = typeCode;

    this.nodeNameList = nodeNameList;

    this.userId = userId;

    // Loader is provided at run time based on node typeCode.
    this.loader = new LoaderProvider(getServiceData()).createInstance(typeCode);

    // Validate not null values, this cannot happen fromUI.
    validate();

    // Remove * and trim.
    trimNodeName();

  }

  /**
   * replace the star
   */
  private void trimNodeName() {
    this.nodeNameList.replaceAll(node -> node.replace("*", ""));
    this.nodeNameList.replaceAll(String::trim);
  }

  /**
   * @param nodeType
   * @param nodeName
   * @param userId
   * @throws IcdmException
   */
  private void validate() throws IcdmException {

    if (this.nodeType == null) {
      throw new IcdmException("Node type cannot be null");
    }

  }

  /**
   * @return the node access details
   * @throws DataException DataException
   */
  private NodeAccessDetails getNodeAccDetForUserWithGrant() throws DataException {
    // Get all the node access with GRANT access for the user.
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    return nodeAccessLoader.getAllNodeAccessByNode(this.nodeType.getModelType().getTypeCode(), null, null, null);
  }


  /**
   * @param nodeAccesDetails nodeAccesDetails
   * @return the node id's
   */
  private Set<Long> getNodeIds(final NodeAccessDetails nodeAccesDetails) {
    // create Id set for the nodes
    return new HashSet<>(nodeAccesDetails.getNodeAccessMap().keySet());

  }

  /**
   * @param allNodeAccessByNode allNodeAccessByNode
   * @return the corresponding data objects map
   * @throws DataException DataException
   */
  private Map<Long, IDataObject> getAllNodeMapForUser(final NodeAccessDetails allNodeAccessByNode) {


    Set<Long> nodeIds = getNodeIds(allNodeAccessByNode);
    // This is done to avoid the issue of Node id in Access table and not in Main table - Example Use case table.
    return fillNodeAccMapForUser(nodeIds);

  }

  /**
   * @param nodeIds set of node id's
   * @return the node Access map for User
   */
  public Map<Long, IDataObject> fillNodeAccMapForUser(final Set<Long> nodeIds) {
    Map<Long, IDataObject> nodeMapForuser = new HashMap<>();
    for (Long nodeId : (nodeIds)) {
      try {
        nodeMapForuser.put(nodeId, (IDataObject) this.loader.getDataObjectByID(nodeId));
      }
      catch (DataException exp) {
        getLogger().error(exp.getMessage(), exp);
      }
    }

    return nodeMapForuser;
  }


  /**
   * @return the Node access info.
   * @throws IcdmException IcdmException
   */
  public Map<Long, NodeAccessInfo> resNodeAccInfoForUser() throws IcdmException {

    NodeAccessDetails allNodeAccessByNode = getNodeAccDetForUserWithGrant();

    Map<Long, NodeAccessInfo> nodeAccessInfoMap = new HashMap<>();

    if (!allNodeAccessByNode.getNodeAccessMap().isEmpty()) {

      Map<Long, IDataObject> allUserFuncMap = getAllNodeMapForUser(allNodeAccessByNode);
      for (String nodeName : this.nodeNameList) {
        fillNodeAccessInfoMap(allNodeAccessByNode, nodeAccessInfoMap, allUserFuncMap, nodeName);
      }
    }
    return nodeAccessInfoMap;
  }


  /**
   * @param allNodeAccessByNode allNodeAccessByNode
   * @param nodeAccessInfoMap nodeAccessInfoMap
   * @param allUserNodeMap allUserNodeMap
   */
  private void fillNodeAccessInfoMap(final NodeAccessDetails allNodeAccessByNode,
      final Map<Long, NodeAccessInfo> nodeAccessInfoMap, final Map<Long, IDataObject> allUserNodeMap,
      final String nodeName) {

    Map<Long, IDataObject> matchingDataObj = new HashMap<>();

    for (Entry<Long, IDataObject> userFunctionEntry : allUserNodeMap.entrySet()) {

      if (userFunctionEntry.getValue().getName().toUpperCase().contains(nodeName.toUpperCase())) {
        matchingDataObj.put(userFunctionEntry.getKey(), userFunctionEntry.getValue());
      }
    }

    // fill the node access info map for the matching nodes.
    for (Entry<Long, IDataObject> funcMapEntry : matchingDataObj.entrySet()) {
      Set<NodeAccess> nodeAccessSet = allNodeAccessByNode.getNodeAccessMap().get(funcMapEntry.getKey());

      NodeAccess nodeAccess = nodeAccessSet.iterator().next();
      NodeAccessInfo nodeAccInfo = new NodeAccessInfo();
      nodeAccInfo.setAccess(nodeAccess);
      nodeAccInfo.setNodeName(funcMapEntry.getValue().getName());

      nodeAccInfo.setNodeDesc(funcMapEntry.getValue().getDescription());
      nodeAccessInfoMap.put(nodeAccess.getId(), nodeAccInfo);

    }
  }


  /**
   * @return the notFoundNodeList
   */
  public java.util.List<String> getMissingNodes() {
    return this.missingNodes;
  }


  /**
   * @param missingNodes the notFoundNodeList to set
   */
  public void setMissingNodes(final java.util.List<String> missingNodes) {
    this.missingNodes = missingNodes;
  }
}
