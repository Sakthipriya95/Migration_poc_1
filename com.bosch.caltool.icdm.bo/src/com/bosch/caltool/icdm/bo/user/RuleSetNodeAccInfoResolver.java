/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author rgo7cob
 */
public class RuleSetNodeAccInfoResolver extends NodeAccessInfoResolver {

  /**
   * @param typeCode typeCode
   * @param nodeNameList nodeName
   * @param userId userId
   * @param serviceData serviceData
   * @throws IcdmException IcdmException
   */
  public RuleSetNodeAccInfoResolver(final String typeCode, final List<String> nodeNameList, final String userId,
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

    RuleSetLoader ruleSetLoader = (RuleSetLoader) this.loader;


    Set<ParamCollection> searchFunctions = ruleSetLoader.getAllRuleSets();

    for (String nodeName : this.nodeNameList) {
      for (ParamCollection ruleSet : searchFunctions) {

        if (ruleSet.getName().contains(nodeName)) {
          NodeAccess nodeAccess = new NodeAccess();
          nodeAccess.setNodeId(ruleSet.getId());
          nodeAccess.setNodeType(this.typeCode);
          nodeAccess.setOwner(false);
          nodeAccess.setGrant(false);
          nodeAccess.setRead(false);
          nodeAccess.setWrite(false);

          NodeAccessInfo nodeAccInfo = new NodeAccessInfo();
          nodeAccInfo.setNodeName(ruleSet.getName());
          nodeAccInfo.setAccess(nodeAccess);
          nodeAccInfo.setNodeDesc(ruleSet.getDescription());

          nodeAccInfoMap.put(nodeAccess.getNodeId(), nodeAccInfo);

        }
      }
    }
    return nodeAccInfoMap;
  }


}
