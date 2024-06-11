/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author say8cob
 */
public class NodeAccessOutput {

  private Map<Long, NodeAccessInfo> nodeAccessInfoMap = new HashMap<>();

  private List<String> missingNodes = new ArrayList<>();


  /**
   * @return the nodeAccessInfoMap
   */
  public Map<Long, NodeAccessInfo> getNodeAccessInfoMap() {
    return this.nodeAccessInfoMap;
  }


  /**
   * @param nodeAccessInfoMap the nodeAccessInfoMap to set
   */
  public void setNodeAccessInfoMap(final Map<Long, NodeAccessInfo> nodeAccessInfoMap) {
    this.nodeAccessInfoMap = nodeAccessInfoMap;
  }


  /**
   * @return the notFoundNodeList
   */
  public List<String> getMissingNodes() {
    return this.missingNodes;
  }


  /**
   * @param missingNodes the notFoundNodeList to set
   */
  public void setMissingNodes(final List<String> missingNodes) {
    this.missingNodes = missingNodes;
  }


}
