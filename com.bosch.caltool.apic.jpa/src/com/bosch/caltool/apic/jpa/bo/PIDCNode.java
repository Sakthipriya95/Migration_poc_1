/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.IBasicObject;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * A node in the PIDC hierarchy
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:35
 */
@Deprecated
public class PIDCNode implements IBasicObject, Comparable<PIDCNode> {

  /**
   * Data provider
   */
  @SuppressWarnings("unused")
  private final ApicDataProvider apicDataProvider;

  /**
   * Parent node
   */
  private final PIDCNode parentNode;

  /**
   * Node attribute
   */
  private final Attribute nodeAttr;

  /**
   * Node attribute value
   */
  private final AttributeValue nodeAttrValue;

  /**
   * list of child nodes
   */
  // 221715
  private final ConcurrentMap<Long, PIDCNode> nodeChildren = new ConcurrentHashMap<>();

  /**
   * Project ID cards in this leaf node
   * <p>
   * Key - PIDC ID<br>
   * Value - PIDC Business object
   */
  private final ConcurrentMap<Long, PIDCard> nodePidcMap = new ConcurrentHashMap<>();

  /**
   * level in the hierarchy root node: level 0
   */
  private final int level;

  /**
   * Constructor
   *
   * @param apicDataProvider ApicDataProvider
   * @param level node level
   * @param parentNode parent node
   * @param nodeAttr attribute of this node
   * @param nodeAttrValue value of this node
   */
  public PIDCNode(final ApicDataProvider apicDataProvider, final int level, final PIDCNode parentNode,
      final Attribute nodeAttr, final AttributeValue nodeAttrValue) {

    // TODO
    // check valid level, parent node must be not null except on root level,
    // nodeAttr must not be null, nodeAttrValue must not be null except on root level

    this.apicDataProvider = apicDataProvider;
    this.level = level;
    this.parentNode = parentNode;
    this.nodeAttr = nodeAttr;
    this.nodeAttrValue = nodeAttrValue;
  }

  /**
   * @return Parent PIDCNode instance
   */
  public PIDCNode getParent() {
    return this.parentNode;
  }

  /**
   * This method adds children to the PIDCNode
   *
   * @param pidcNodeChildren list of child nodes
   */
  public void addNodeChildren(final List<PIDCNode> pidcNodeChildren) {
    synchronized (this.nodeChildren) {
      for (PIDCNode node : pidcNodeChildren) {
        this.nodeChildren.put(node.getNodeAttrValue().getID(), node);
      }
    }
  }

  /**
   * @return Sorted set of children of the PIDCNode
   */
  public SortedSet<PIDCNode> getNodeChildren() {
    synchronized (this.nodeChildren) {
      return new TreeSet<>(this.nodeChildren.values());
    }
  }

  /**
   * @param withoutEmptyNodes not to include nodes without pidcs
   * @return Sorted set of children of the PIDCNode with pidcs
   */
  public SortedSet<PIDCNode> getNodeChildren(final boolean withoutEmptyNodes) {

    synchronized (this.nodeChildren) {
      if (withoutEmptyNodes) {
        SortedSet<PIDCNode> nodeWithChild = new TreeSet<PIDCNode>();
        for (PIDCNode pidcNode : this.nodeChildren.values()) {
          if (pidcNode.hasPidCardsBelow()) {
            nodeWithChild.add(pidcNode);
          }
        }
        return nodeWithChild;
      }
      return getNodeChildren();
    }
  }

  /**
   * @return Level of PIDC node
   */
  public int getLevel() {
    return this.level;
  }


  /**
   * Returns all PID cards added to this node
   *
   * @return Map of PIDCs. Key - PIDC ID; Value - PIDC business object
   */
  protected Map<Long, PIDCard> getPidCardMap() {
    return this.nodePidcMap;
  }

  /**
   * @return PID Cards mapped to this node, if this is a leaf node
   */
  public Set<PIDCard> getPidCards() {
    return new HashSet<PIDCard>(this.nodePidcMap.values());
  }

  /**
   * @return PID Cards mapped to this node, if this is a leaf node
   */
  public Set<PIDCard> getPidCardsBelow() {

    Set<PIDCard> retSet = new HashSet<>();
    if (hasPidCards()) {
      retSet.addAll(this.nodePidcMap.values());
    }
    else {
      for (PIDCNode childNode : this.nodeChildren.values()) {
        retSet.addAll(childNode.getPidCardsBelow());
      }
    }
    return retSet;
  }

  /**
   * Provides the active versions of the PIDCs in this node
   *
   * @return sorted set of active pidc versions
   */
  public SortedSet<PIDCVersion> getActivePidcVerSet() {
    SortedSet<PIDCVersion> retSet = new TreeSet<>();
    for (PIDCard pidc : this.nodePidcMap.values()) {
      if (!pidc.getActiveVersion().isHidden()) {
        retSet.add(pidc.getActiveVersion());
      }
    }

    return retSet;
  }

  /**
   * @return true, if there are PID Cards directly below this node
   */
  public boolean hasPidCards() {
    return !this.nodePidcMap.isEmpty();
  }

  /**
   * @return true, if this node has pid cards directly below or in any child node
   */
  public boolean hasPidCardsBelow() {
    boolean ret = hasPidCards();
    if (!ret) {
      for (PIDCNode childNode : this.nodeChildren.values()) {
        ret = childNode.hasPidCardsBelow();
        if (ret) {
          break;
        }
      }
    }

    return ret;
  }

  /**
   * @return Name of PIDC Node
   */
  public String getNodeName() {
    if (this.level == ApicConstants.PIDC_ROOT_LEVEL) {
      return this.nodeAttr.getAttributeName();
    }
    return this.nodeAttrValue.getValue();
  }

  /**
   * @return Map of structure values in the Node. Key - Attribute ID; Value - Attribute Value
   */
  public Map<Long, AttributeValue> getNodeStructureValues() {
    Map<Long, AttributeValue> resultMap = new ConcurrentHashMap<Long, AttributeValue>();

    if (this.nodeAttrValue != null) {
      resultMap.put(this.nodeAttr.getAttributeID(), this.nodeAttrValue);

      resultMap.putAll(this.parentNode.getNodeStructureValues());
    }

    return resultMap;
  }

  /**
   * Compare result of two PIDC nodes
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCNode pidcNode2) {
    return ApicUtil.compare(getNodeName(), pidcNode2.getNodeName());
  }

  // ICDM-1042
  /**
   * @return the node Attr Value
   */
  public AttributeValue getNodeAttrValue() {
    return this.nodeAttrValue;
  }

  // ICDM-1042
  /**
   * @return the node Attr
   */
  public Attribute getNodeAttr() {
    return this.nodeAttr;
  }

  // iCDM-1241
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PIDCNode nodeOther = (PIDCNode) obj;
    // equals must check the level,attr,value and parent
    return (getLevel() == (nodeOther.getLevel())) && getNodeAttr().equals(nodeOther.getNodeAttr()) &&
        getNodeAttrValue().equals(nodeOther.getNodeAttrValue()) && getParent().equals(nodeOther.getParent());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return Tooltip for the nodes in the pidc tree
   */
  @Override
  public String getToolTip() {
    if (getNodeAttrValue() != null) {
      return getNodeAttrValue().getToolTip();
    }
    return getNodeAttr().getToolTip();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getNodeName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    if (this.level == ApicConstants.PIDC_ROOT_LEVEL) {
      return this.nodeAttr.getDescription();
    }
    return this.nodeAttrValue.getDescription();
  }

  /**
   * Get the child node with the given value ID
   *
   * @param valueID value ID of the child node
   * @return child node, or null if value id is invalid
   */
  // 221715
  public PIDCNode getChildNode(final long valueID) {
    return this.nodeChildren.get(valueID);
  }
}