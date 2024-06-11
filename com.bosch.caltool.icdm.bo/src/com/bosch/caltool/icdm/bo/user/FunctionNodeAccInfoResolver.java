/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author rgo7cob
 */
public class FunctionNodeAccInfoResolver extends NodeAccessInfoResolver {

  /**
   * @param typeCode typeCode
   * @param nodeNameList nodeName
   * @param userId userId
   * @param serviceData serviceData
   * @throws IcdmException IcdmException
   */
  public FunctionNodeAccInfoResolver(final String typeCode, final List<String> nodeNameList, final String userId,
      final ServiceData serviceData) throws IcdmException {
    super(typeCode, nodeNameList, userId, serviceData);


  }


  /**
   * @return the Node access info.
   * @throws IcdmException IcdmException
   */
  @Override
  public Map<Long, NodeAccessInfo> resNodeAccInfoForUser() throws IcdmException {

    Map<Long, NodeAccessInfo> nodeAccInfoMap = new HashMap<>();

    FunctionLoader funcLoader = (FunctionLoader) this.loader;

    for (String nodeName : this.nodeNameList) {
      Set<Function> searchFunctions = funcLoader.getSearchFunctions(nodeName);
      if (CommonUtils.isNullOrEmpty(searchFunctions)) {
        this.missingNodes.add(nodeName);
      }

      for (Function function : searchFunctions) {
        NodeAccess nodeAccess = new NodeAccess();
        nodeAccess.setNodeId(function.getId());
        nodeAccess.setNodeType(this.typeCode);
        nodeAccess.setOwner(false);
        nodeAccess.setGrant(false);
        nodeAccess.setRead(false);
        nodeAccess.setWrite(false);

        NodeAccessInfo nodeAccInfo = new NodeAccessInfo();
        nodeAccInfo.setNodeName(function.getName());
        nodeAccInfo.setAccess(nodeAccess);
        nodeAccInfo.setNodeDesc(function.getDescription());

        nodeAccInfoMap.put(nodeAccess.getNodeId(), nodeAccInfo);
      }
    }

    return nodeAccInfoMap;


  }


}
