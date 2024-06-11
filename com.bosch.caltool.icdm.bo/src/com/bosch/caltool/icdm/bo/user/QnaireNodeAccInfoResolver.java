/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author rgo7cob
 */
public class QnaireNodeAccInfoResolver extends NodeAccessInfoResolver {

  /**
   * @param typeCode typeCode
   * @param nodeNameList nodeName
   * @param userId userId
   * @param serviceData serviceData
   * @throws IcdmException IcdmException
   */
  public QnaireNodeAccInfoResolver(final String typeCode, final List<String> nodeNameList, final String userId,
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

    QuestionnaireLoader qnaireLoader = (QuestionnaireLoader) this.loader;


    Map<Long, Questionnaire> qnaireSet = qnaireLoader.getAll(false, true);

    for (String nodeName : this.nodeNameList) {
      for (Questionnaire qnaire : qnaireSet.values()) {

        if (qnaire.getName().contains(nodeName)) {
          NodeAccess nodeAccess = new NodeAccess();
          nodeAccess.setNodeId(qnaire.getId());
          nodeAccess.setNodeType(this.typeCode);
          nodeAccess.setOwner(false);
          nodeAccess.setGrant(false);
          nodeAccess.setRead(false);
          nodeAccess.setWrite(false);

          NodeAccessInfo nodeAccInfo = new NodeAccessInfo();
          nodeAccInfo.setNodeName(qnaire.getName());
          nodeAccInfo.setAccess(nodeAccess);
          nodeAccInfo.setNodeDesc(qnaire.getDescription());

          nodeAccInfoMap.put(nodeAccess.getNodeId(), nodeAccInfo);

        }
      }
    }

    return nodeAccInfoMap;


  }


}
