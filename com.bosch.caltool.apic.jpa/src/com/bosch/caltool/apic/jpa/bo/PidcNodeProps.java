/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;

import com.bosch.caltool.apic.jpa.bo.PIDCDetailsNode.NODE_TYPE;

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
  private PIDCDetStructure struct;
  /**
   * Attribute value, if this is a value node
   */
  private AttributeValue value;
  /**
   * Variant if this is a variant node
   */
  private PIDCVariant variant;
  /**
   * PIDC version of this node
   */
  private PIDCVersion pidcVers;

  /**
   * @param nodeID ID of this node
   */
  private PidcNodeProps(final String nodeID) {
    this.nodeID = nodeID;
  }

  /**
   * Creates an instance of this object using the the input Node ID and PIDC
   *
   * @param pidcVers PIDC version
   * @param pidStructMap pid Struct Map
   * @param nodeID Node ID generated using <code>PIDCDetailsNode.generateNodeID()</code> method
   * @return a new PidcNodeProps instance
   */
  public static PidcNodeProps getNodeProps(final PIDCVersion pidcVers, final Map<Long, PIDCDetStructure> pidStructMap,
      final String nodeID) {

    PidcNodeProps props = new PidcNodeProps(nodeID);
    props.pidcVers = pidcVers;
    props.type = NODE_TYPE.getTypeFromNodeID(nodeID);
    String nodePtsArr[] = nodeID.split(PIDCDetailsNode.SEPARATOR);

    switch (props.type) {
      case VAL_UNDEF:
      case VAL_NOTUSED:
        props.level = nodePtsArr.length;
        props.struct = pidStructMap.get(props.level);
        break;
      case VALUE:
        caseValue(pidcVers, pidStructMap, nodeID, props, nodePtsArr);
        break;

      default: // VARIANT, VAR_UNDEF types
        String varID1 = nodeID.substring(nodeID.lastIndexOf(props.type.getPrefix()) + props.type.getPrefix().length());
        props.variant = pidcVers.getDataCache().getPidcVaraint(Long.valueOf(varID1));
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
  private static void caseValue(final PIDCVersion pidcVers, final Map<Long, PIDCDetStructure> pidStructMap,
      final String nodeID, PidcNodeProps props, String[] nodePtsArr) {
    String valueID = nodeID.substring(nodeID.lastIndexOf(props.type.getPrefix()) + props.type.getPrefix().length());
    props.value = pidcVers.getDataCache().getAttrValue(Long.valueOf(valueID));
    props.level = nodePtsArr.length;
    props.struct = pidStructMap.get(props.level);
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
  public PIDCDetStructure getStruct() {
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
  public PIDCVariant getVariant() {
    return this.variant;
  }

  /**
   * @return the PID card
   */
  public PIDCVersion getPidcVersion() {
    return this.pidcVers;
  }
}