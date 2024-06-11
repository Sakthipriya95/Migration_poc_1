/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;

import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode.NODE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * Class to keep the properties of virtual nodes
 *
 * @author bne4cob
 */
final class PidcNodeProps {

  /**
   * Type of this node
   */
  private NODE_TYPE type;
  /**
   * Unique node ID
   */
  private final String nodeID;
  /**
   * Node ID of the parent
   */
  private String parentID;
  /**
   * Node level, applicable if this is a value node. Refers to the Node structure level
   */
  private long level;
  /**
   * Corresponding Structure object of this ndoe
   */
  private PidcDetStructure struct;
  /**
   * Attribute value, if this is a value node
   */
  private AttributeValue value;
  /**
   * Variant if this is a variant node
   */
  private PidcVariant variant;
  /**
   * PIDC version of this node
   */
  private PidcVersion pidcVers;

  /**
   * @param nodeID ID of this node
   */
  private PidcNodeProps(final String nodeID) {
    this.nodeID = nodeID;
  }

  /**
   * Creates an instance of this object using the the input Node ID and PIDC
   *
   * @param pidcVersionBO Pidc Version BO
   * @param pidcVers PIDC version
   * @param pidStructMap pid Struct Map
   * @param nodeID Node ID generated using <code>PIDCDetailsNode.generateNodeID()</code> method
   * @return a new PidcNodeProps instance
   */
  public static PidcNodeProps getNodeProps(final PidcVersionBO pidcVersionBO,
      final Map<Long, PidcDetStructure> pidStructMap, final String nodeID) {

    PidcNodeProps props = new PidcNodeProps(nodeID);
    props.pidcVers = pidcVersionBO.getPidcVersion();

    props.type = NODE_TYPE.getTypeFromNodeID(nodeID);
    String nodePtsArr[] = nodeID.split(PIDCDetailsNode.SEPARATOR);

    switch (props.type) {
      case VAL_UNDEF:
      case VAL_NOTUSED:
        props.level = nodePtsArr.length;
        props.struct = pidStructMap.get(props.level);
        break;
      case VALUE:
        caseValue(pidcVersionBO, pidStructMap, nodeID, props, nodePtsArr);
        break;

      default: // VARIANT, VAR_UNDEF types
        String varID1 = nodeID.substring(nodeID.lastIndexOf(props.type.getPrefix()) + props.type.getPrefix().length());
        props.variant = pidcVersionBO.getPidcDataHandler().getVariantMap().get(Long.valueOf(varID1));
        props.level = PIDCDetailsNode.VAR_NODE_LVL;
        break;
    }

    if (nodePtsArr.length <= 1) {
      props.parentID = "";
    }
    else {
      StringBuilder parentID = new StringBuilder();
      for (int indx = 0; indx < (nodePtsArr.length - 1); indx++) {
        String nodePt = nodePtsArr[indx];

        if (parentID.length() != 0) {
          parentID.append(PIDCDetailsNode.SEPARATOR);
        }
        parentID.append(nodePt);
      }
      props.parentID = parentID.toString();
    }

    return props;
  }

  /**
   * @param pidcVers
   * @param pidStructMap
   * @param nodeID
   * @param props
   * @param nodePtsArr
   */
  private static void caseValue(final PidcVersionBO handler, final Map<Long, PidcDetStructure> pidStructMap,
      final String nodeID, final PidcNodeProps props, final String[] nodePtsArr) {
    // need to check why null is part of string
    if (!CommonUtils.isEmptyString(nodeID) && !("null").equals(nodeID)) {
      String valueID = nodeID.substring(nodeID.lastIndexOf(props.type.getPrefix()) + props.type.getPrefix().length());
      if (!CommonUtils.isEmptyString(valueID) && !("null").equals(valueID)) {
        props.value = handler.getPidcDataHandler().getAttributeValueMap().get(Long.valueOf(valueID));
      }
      props.level = nodePtsArr.length;
      props.struct = pidStructMap.get(props.level);
    }
  }

  /**
   * @return the type
   */
  public NODE_TYPE getType() {
    return this.type;
  }


  /**
   * @return the nodeID
   */
  public String getNodeID() {
    return this.nodeID;
  }


  /**
   * @return the parentID
   */
  public String getParentID() {
    return this.parentID;
  }


  /**
   * @return the level
   */
  public long getLevel() {
    return this.level;
  }


  /**
   * @return the struct
   */
  public PidcDetStructure getStruct() {
    return this.struct;
  }


  /**
   * @return the value
   */
  public AttributeValue getValue() {
    return this.value;
  }


  /**
   * @return the variant
   */
  public PidcVariant getVariant() {
    return this.variant;
  }

  /**
   * @return the PID card
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVers;
  }
}