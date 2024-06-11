/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.bo.apic.NodeType;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author rgo7cob
 */
public class NodeAccInfoFactory extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData serviceData
   */
  public NodeAccInfoFactory(final ServiceData serviceData) {
    super(serviceData);

  }

  /**
   * @param typeCode typeCode
   * @param nodeNameList nodeName
   * @param userId userId
   * @return the correct node acc info resolver
   * @throws IcdmException IcdmException
   */
  public NodeAccessInfoResolver createNodeAccInfoResObject(final String typeCode, final List<String> nodeNameList,
      final String userId)
      throws IcdmException {

    NodeType nodeType = NodeType.getNodeType(typeCode);

    switch (nodeType) {

      case FUNCTION:
        return new FunctionNodeAccInfoResolver(typeCode, nodeNameList, userId, getServiceData());


      case RULESET:
        return new RuleSetNodeAccInfoResolver(typeCode, nodeNameList, userId, getServiceData());

      case QUESTIONNAIRE:

        return new QnaireNodeAccInfoResolver(typeCode, nodeNameList, userId, getServiceData());

      default:
        return new NodeAccessInfoResolver(typeCode, nodeNameList, userId, getServiceData());

    }


  }

}
