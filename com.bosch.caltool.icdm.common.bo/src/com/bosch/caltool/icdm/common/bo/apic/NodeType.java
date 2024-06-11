/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.apic;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.model.MODEL_TYPE;

/**
 * @author rgo7cob
 */
public enum NodeType {


                      /**
                       * Function type.
                       */
                      FUNCTION(MODEL_TYPE.CDR_FUNCTION),
                      /**
                       * Rule set.
                       */
                      RULESET(MODEL_TYPE.CDR_RULE_SET),
                      /**
                       * PIDC
                       */
                      PIDC(MODEL_TYPE.PIDC),
                      /**
                       * Questionnaire
                       */
                      QUESTIONNAIRE(MODEL_TYPE.QUESTIONNAIRE),

                      /**
                       * Use case type
                       */
                      USE_CASE(MODEL_TYPE.USE_CASE);

  private final IModelType modelType;


  /**
   * @return the uiType
   */
  public IModelType getModelType() {
    return this.modelType;
  }


  NodeType(final IModelType modelType) {
    this.modelType = modelType;
  }

  /**
   * @return to get the list of node types as string array
   */
  public static String[] getTypeNames() {
    List<String> typecodes = new ArrayList<>();
    for (NodeType type : NodeType.values()) {
      typecodes.add(type.modelType.getTypeName());
    }
    String[] typeArray = new String[typecodes.size()];
    // converting to array
    typecodes.toArray(typeArray);
    // sorting the arrays
    java.util.Arrays.sort(typeArray);
    return typeArray;
  }

  /**
   * @param typeName as input
   * @return node type
   */
  public static NodeType getNodeTypeByTypeName(final String typeName) {
    for (NodeType type : NodeType.values()) {
      if (type.modelType.getTypeName().equals(typeName)) {
        return type;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * @param typeCode - type code from UI.
   * @return - the Node Type associated
   */
  public static NodeType getNodeType(final String typeCode) {
    for (NodeType type : NodeType.values()) {
      if (type.modelType.getTypeCode().equals(typeCode)) {
        return type;
      }
    }
    throw new IllegalArgumentException();
  }

}
