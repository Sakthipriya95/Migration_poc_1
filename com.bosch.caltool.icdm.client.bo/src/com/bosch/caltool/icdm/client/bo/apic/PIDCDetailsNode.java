/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
public final class PIDCDetailsNode implements Comparable<PIDCDetailsNode>

{


  /**
   *
   */
  private static final int TT_BUF_START_SIZE = 50;

  /**
   * Node level of 'undefined' node
   */
  public static final long UNDEF_NODE_LVL = -1L;

  /**
   * Node level of variant node
   */
  public static final long VAR_NODE_LVL = -2L;

  /**
   * Separator
   */
  public static final String SEPARATOR = "__";

  /**
   * Not used Node
   */
  private static final String NOT_DEFINED = "UNDF";

  /**
   * Not defined Node
   */
  private static final String NOT_USED = "NUSE";

  /**
   * Value ID Prefix
   */
  private static final String VAL_PREFIX = "VL";

  /**
   * Variant ID prefix
   */
  private static final String VAR_PREFIX = "VR";

  /**
   * Value not assigned in this node
   */
  public static final String NOT_ASSIGNED = " <NOT_ASSIGNED>";
  /**
   * Attribute not used for pidc
   */
  public static final String NOT_USED_UI = " <NOT_USED>";

  /**
   * Type of node enumeration
   *
   * @author bne4cob
   */
  public enum NODE_TYPE {
                         /**
                          * Value undefined node when a) used flag = NOT_DEFINED b) attribute not visible due to
                          * dependency
                          */
                         // Node ID Format VL<value id>__UNDEF<struct att id> (e.g. VL100__UNDEF200) or
                         // UNDEF<struct att id>(e.g. UNDEF300)
                         VAL_UNDEF(NOT_DEFINED),
                         /**
                          * Value node
                          */
                         // Node ID Format VL<value id1>__VL<value id2> (e.g. VL100__VL110)
                         VALUE(VAL_PREFIX),
                         /**
                          * Variant node in undefined value node
                          */
                         // Node ID Format VL<value id1>__VL<value id2>__VAR<variant id> (e.g.
                         // VL100__VL110__VAR3110)
                         VAR_UNDEF(VAR_PREFIX),
                         /**
                          * Variant node
                          */
                         // Node ID Format VL<value id1>__UNDEF<struct att id>__VAR<variant id> (e.g.
                         // VL100__UNDEF200__VAR3110)
                         VARIANT(VAR_PREFIX),
                         /**
                          * Value undefined node when used flag = NO
                          */

                         VAL_NOTUSED(NOT_USED);

    private String prefix;

    /**
     * @param prefix prefix of node type
     */
    NODE_TYPE(final String prefix) {
      this.prefix = prefix;
    }

    /**
     * @return node prefix
     */
    public String getPrefix() {
      return this.prefix;
    }

    /**
     * Resolve the node type from node ID
     *
     * @param nodeID Node ID
     * @return NODE_TYPE
     */
    public static NODE_TYPE getTypeFromNodeID(final String nodeID) {
      if ((nodeID.indexOf(NOT_DEFINED) >= 0) && (nodeID.indexOf(VAR_PREFIX) >= 0)) {
        return VAR_UNDEF;
      }
      if (nodeID.indexOf(NOT_DEFINED) >= 0) {
        return VAL_UNDEF;
      }
      if (nodeID.indexOf(NOT_USED) >= 0) {
        return VAL_NOTUSED;
      }
      if (nodeID.indexOf(VAR_PREFIX) >= 0) {
        return VARIANT;
      }
      return VALUE;
    }
  }

  /**
   * Type of node
   */
  private final NODE_TYPE nodeType;

  /**
   * Node ID
   */
  private final String nodeID;

  /**
   * Attribute value object, for value nodes
   */
  private final AttributeValue attrValue;

  /**
   * Structure object, corresponding to the value nodes
   */
  private final PidcDetStructure struct;

  /**
   * Parent node. For top level nodes, this is null
   */
  private final PIDCDetailsNode parent;

  /**
   * Variant object, for variant type nodes
   */
  private PidcVariant variant;

  /**
   * If true, then the node is visible
   */
  private boolean visible;

  /**
   * Parent Project version of this node
   */
  private final PidcVersion pidcVers;

  /**
   * All child nodes
   */
  private final SortedSet<PIDCDetailsNode> childNodeSet =
      Collections.synchronizedSortedSet(new TreeSet<PIDCDetailsNode>());

  /**
   * Visible child nodes
   */
  private final SortedSet<PIDCDetailsNode> visChildNodeSet =
      Collections.synchronizedSortedSet(new TreeSet<PIDCDetailsNode>());

  /**
   * list of Sub Variants defined on this node
   */
  private final SortedSet<PidcSubVariant> subvarSet = Collections.synchronizedSortedSet(new TreeSet<PidcSubVariant>());

  /**
   * PidcVersionHandler
   */
  private final PidcVersionBO pidcVersionBO;


  /**
   * Constructor for variant node
   *
   * @param nodeID node ID
   * @param dataProvider apic data provider
   * @param var variant
   * @param parentNode parent node
   */
  private PIDCDetailsNode(final String nodeID, final PidcVariant var, final PIDCDetailsNode parentNode,
      final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
    this.pidcVers = pidcVersionBO.getPidcVersion();
    this.struct = null; // NOPMD
    this.attrValue = null; // NOPMD
    this.parent = parentNode;
    this.visible = false;
    this.variant = var;
    this.nodeID = nodeID;
    this.nodeType = NODE_TYPE.VARIANT;
    if (this.parent != null) {
      this.parent.childNodeSet.add(this);
    }
    addThisObjToCache();

  }

  /**
   * Create a new instance of the details node of any type using the node properties
   *
   * @param dataProvider APIC data provider
   * @param parent parent node
   * @param nodeProps node properties
   * @param pidcVersionBO
   */
  private PIDCDetailsNode(final PIDCDetailsNode parent, final PidcNodeProps nodeProps,
      final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
    this.attrValue = nodeProps.getValue();
    this.nodeID = nodeProps.getNodeID();
    this.nodeType = nodeProps.getType();
    this.pidcVers = nodeProps.getPidcVersion();
    this.struct = nodeProps.getStruct();
    this.variant = nodeProps.getVariant();
    this.parent = parent;
    if (parent != null) {
      parent.getChildNodes().add(this);
    }
    addThisObjToCache();
  }

  /**
   * Adds this node to the data cache
   */
  private void addThisObjToCache() {
    this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().put(this.nodeID, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCDetailsNode other) {
    if ((this.nodeType == NODE_TYPE.VAL_UNDEF) || (this.nodeType == NODE_TYPE.VAL_NOTUSED)) {
      return ApicUtil.compare(this.nodeID, other.getNodeID());
    }
    if (isVariantNode()) {
      return ApicUtil.compare(this.variant, other.getPidcVariant());
    }
    return ApicUtil.compare(this.attrValue, other.getAttrValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    return getNodeID().equals(((PIDCDetailsNode) obj).getNodeID());
  }

  /**
   * @return PIDC Variant
   */
  public PidcVariant getPidcVariant() {
    return this.variant;
  }

  /**
   * @return
   */
  private AttributeValue getAttrValue() {
    return this.attrValue;
  }

  /**
   * @param pidcVersionBO
   * @return whether this node is deleted
   */
  public boolean isDeleted(final AbstractProjectObjectBO pidcVersionBO) {
    if (this.attrValue != null) {
      return this.attrValue.isDeleted() ||
          pidcVersionBO.getPidcDataHandler().getAttributeMap().get(this.attrValue.getAttributeId()).isDeleted();
    }
    if (this.struct != null) {
      return pidcVersionBO.getPidcDataHandler().getAttributeMap().get(this.struct.getAttrId()).isDeleted();
    }
    if (isVariantNode()) {
      return this.variant.isDeleted();
    }
    return false;
  }

  /**
   * @return node id
   */
  public String getNodeID() {
    return this.nodeID;
  }

  /**
   * @return name of this node
   */
  public String getName() {
    if (this.nodeType == NODE_TYPE.VAL_UNDEF) {
      return PIDCDetailsNode.NOT_ASSIGNED;
    }
    if (this.nodeType == NODE_TYPE.VAL_NOTUSED) {
      return PIDCDetailsNode.NOT_USED_UI;
    }
    if (isVariantNode()) {
      return this.variant.getName();
    }

    if (this.attrValue != null) {
      // check this
      return this.attrValue.getName();
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return CommonUtils.concatenate("PidcDetailsNode[name=", getName(), ", visible=", this.visible, ", nodeID=",
        this.nodeID, ", level=", getLevel(), ", type=", this.nodeType, "]");
  }

  /**
   * @return sorted set of child nodes
   */
  SortedSet<PIDCDetailsNode> getChildNodes() {
    return this.childNodeSet;
  }

  /**
   * @return the visibleChildNodeSet
   */
  public SortedSet<PIDCDetailsNode> getVisibleChildNodes() {
    this.visChildNodeSet.clear();
    for (PIDCDetailsNode node : getChildNodes()) {
      if (node.isVisible()) {
        this.visChildNodeSet.add(node);
      }
    }
    return this.visChildNodeSet;
  }

  /**
   * Provides the instance of PidcDetailsNode for the given input combination. If the object is not created already, a
   * new instance is returned. Use this method for nodes for variant.
   *
   * @param pidStructMap pid Struct Map
   * @param var variant
   * @param iPidcAttrMap IPIDCAttribute Map
   * @param pidcVersionBO
   * @return PidcDetailsNode instance
   */
  public static PIDCDetailsNode getPidcDetailsNode(final Map<Long, PidcDetStructure> pidStructMap,
      final PidcVariant var, final Map<Long, IProjectAttribute> iPidcAttrMap, final PidcVersionBO pidcVersionBO) {
    final String nodeID = generateNodeID(var, iPidcAttrMap, pidcVersionBO);

    PIDCDetailsNode retNode = pidcVersionBO.getPidcDataHandler().getPidcDetNodes().get(nodeID);

    if (retNode == null) {
      retNode = new PIDCDetailsNode(nodeID, var, getParentNode(pidcVersionBO, pidStructMap, nodeID), pidcVersionBO);
    }
    return retNode;
  }


  /**
   * Get the parent node of the node with the node ID
   *
   * @param dataProvider data provider
   * @param pidcVers PIDC version
   * @param nodeID Node ID
   * @return parent node
   */
  private static PIDCDetailsNode getParentNode(final PidcVersionBO pidcVersionBO,
      final Map<Long, PidcDetStructure> pidStructMap, final String nodeID) {

    PidcNodeProps nodeProps = PidcNodeProps.getNodeProps(pidcVersionBO, pidStructMap, nodeID);
    String parentNodeID = nodeProps.getParentID();
    if (CommonUtils.isEmptyString(parentNodeID)) {
      // No parent Nodes
      return null;
    }
    PIDCDetailsNode retNode = pidcVersionBO.getPidcDataHandler().getPidcDetNodes().get(parentNodeID);
    if (retNode == null) {
      // Create node
      PidcNodeProps parProps = PidcNodeProps.getNodeProps(pidcVersionBO, pidStructMap, parentNodeID);
      PIDCDetailsNode myParent = getParentNode(pidcVersionBO, pidStructMap, nodeProps.getParentID());
      retNode = new PIDCDetailsNode(myParent, parProps, pidcVersionBO);
    }
    return retNode;
  }


  /**
   * Generates the Variant node ID for the given pidc attributes
   *
   * @param variant variant
   * @param pidcAttrMap project attributes
   * @return Variant node ID for the given collection of project attributes
   */
  public static String generateNodeID(final PidcVariant variant, final Map<Long, IProjectAttribute> pidcAttrMap,
      final PidcVersionBO pidcVersionBO) {
    IProjectAttribute pidcAttr;
    Long attrID;
    String displVal;

    StringBuilder idBuilder = new StringBuilder();

    for (long level = 1; level <= pidcAttrMap.size(); level++) {
      if (level > 1) {
        idBuilder.append(SEPARATOR);
      }

      pidcAttr = pidcAttrMap.get(level);

      if (pidcAttr == null) {
        attrID = pidcVersionBO.getVirtualLevelAttrs().get(level).getAttrId();
        displVal = "";
      }
      else {
        attrID = pidcAttr.getAttrId();

        displVal = pidcAttr.getValue();
      }
      if ((pidcAttr == null) || (CommonUtils.isEmptyString(displVal) &&
          !ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag()))) {
        idBuilder.append(NOT_DEFINED);
        idBuilder.append(attrID);
        break;
      }

      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())) {
        idBuilder.append(NOT_USED);
        idBuilder.append(attrID);
        break;
      }

      idBuilder.append(VAL_PREFIX).append(pidcAttr.getValueId());

    }

    idBuilder.append(SEPARATOR).append(VAR_PREFIX).append(variant.getId());

    return idBuilder.toString();
  }

  /**
   * @return parent nodes of this node in the hierarchy
   */
  public Map<String, PIDCDetailsNode> getParentNodes() {
    Map<String, PIDCDetailsNode> parNodeMap = new ConcurrentHashMap<String, PIDCDetailsNode>();
    PIDCDetailsNode node = getParentNode();
    while (node != null) {
      parNodeMap.put(node.getNodeID(), node);
      node = node.getParentNode();
    }
    return parNodeMap;
  }

  /**
   * @return Top node of this node
   */
  public PIDCDetailsNode getTopNode() {
    PIDCDetailsNode parNode = getParentNode();
    PIDCDetailsNode topNode = parNode;
    while (parNode != null) {
      topNode = parNode;
      parNode = parNode.getParentNode();
    }
    return topNode;
  }

  /**
   * @param isVisible visible
   */
  public void setVisible(final boolean isVisible) {
    this.visible = isVisible;
    if (isVisible) {
      PIDCDetailsNode parentNode = getParentNode();
      while ((parentNode != null) && !parentNode.isVisible()) {
        parentNode.setVisible(true);
        parentNode = parentNode.getParentNode();
      }
    }
  }

  /**
   * @return true/false
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * @return parent node
   */
  private PIDCDetailsNode getParentNode() {
    return this.parent;
  }

  /**
   * @return the sorted set of sub variants
   */
  public SortedSet<PidcSubVariant> getSubVariants() {
    return this.subvarSet;
  }

  /**
   * @return the structure object of this node
   */
  public PidcDetStructure getStructureObject() {
    return this.struct;
  }

  /**
   * Resets this node. Sets the visiblility to false and clears the sub variants, if this a variant node
   */
  public void reset() {
    this.visible = false;
    this.subvarSet.clear();
  }

  /**
   * Checks if it is allowed to do add add variant to this node
   *
   * @param pidcVersionBO
   * @return true/false
   */
  public boolean canAddVariants(final PidcVersionBO pidcVersionBO) {
    if (isVariantNode() || isDeleted(pidcVersionBO)) {
      return false;
    }

    PIDCDetailsNode curNode;
    PIDCDetailsNode parentNode = getParentNode();
    while (parentNode != null) {
      if (parentNode.isDeleted(pidcVersionBO)) {
        return false;
      }
      curNode = parentNode;
      parentNode = curNode.getParentNode();
    }

    return true;

  }

  /**
   * @return level of this node
   */
  public Long getLevel() {

    // Applicable for value nodes and undefined nodes
    if (this.struct != null) {
      return this.struct.getPidAttrLevel();
    }

    if (isVariantNode()) {
      return VAR_NODE_LVL;
    }

    return 0L;
  }

  /**
   * Get the attribute for this node
   *
   * @return attribute, if this a value node, else null
   */
  public Attribute getNodeAttr(final AbstractProjectObjectBO pidcVersionBO) {
    if (this.struct != null) {
      return pidcVersionBO.getPidcDataHandler().getAttributeMap().get(this.struct.getAttrId());
    }
    return null;
  }

  /**
   * Gets the tooltip for the node
   *
   * @param pidcVersionBO
   * @return 'undefined' if the node is undefined node, variant name if the node is variant, attribute name if the node
   *         is a level node
   */
  public String getToolTip(final AbstractProjectObjectBO pidcVersionBO) {

    // For variant nodes, return the variant name
    if (isVariantNode()) {
      PidcVariantBO varHandler = new PidcVariantBO(this.pidcVers, this.variant, pidcVersionBO.getPidcDataHandler());
      return varHandler.getToolTip();
    }

    // For value nodes, return the node level, attibute name and value name

    StringBuilder valNodeTip = new StringBuilder(TT_BUF_START_SIZE);

    valNodeTip.append(" Level : ").append(getLevel());

    Attribute nodeAttr = getNodeAttr(pidcVersionBO);

    if (nodeAttr != null) {
      valNodeTip.append("\n Attribute : ").append(nodeAttr.getName());
      if (nodeAttr.isDeleted()) {
        valNodeTip.append(' ').append(ApicConstants.DEL_TEXT);
      }
    }

    valNodeTip.append("\n Value : ");
    if (this.nodeType == NODE_TYPE.VAL_UNDEF) {
      valNodeTip.append(PIDCDetailsNode.NOT_ASSIGNED);
    }
    else if (this.nodeType == NODE_TYPE.VAL_NOTUSED) {
      valNodeTip.append(PIDCDetailsNode.NOT_USED_UI);
    }
    else {
      // check this
      valNodeTip.append(getAttrValue().getTextValueEng());
      if (getAttrValue().isDeleted()) {
        valNodeTip.append(' ').append(ApicConstants.DEL_TEXT);
      }
    }

    return valNodeTip.toString();
  }

  /**
   * @return PID Card version
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVers;
  }

  /**
   * @return whether this node is a variant node or not
   */
  public boolean isVariantNode() {
    return this.variant != null;
  }

  /**
   * Retrieves the attributes and their value of the node, in its hierarchy.
   *
   * @return map of attribute, value. If the current node is not a value node, an empty map is returned
   */
  public Map<Attribute, AttributeValue> getAttrValuesFromNodeTree(final PidcVersionBO pidcVersionBO) {
    final Map<Attribute, AttributeValue> retMap = new HashMap<>();

    PIDCDetailsNode curNode = this;

    if (isVariantNode()) {
      curNode = getParentNode();
    }

    retMap.put(curNode.getNodeAttr(pidcVersionBO), curNode.getAttrValue());

    PIDCDetailsNode parentNode = getParentNode();
    while (parentNode != null) {
      retMap.put(parentNode.getNodeAttr(pidcVersionBO), parentNode.getAttrValue());
      curNode = parentNode;
      parentNode = curNode.getParentNode();
    }

    return retMap;
  }


  /**
   * @param pidcVersionBO
   * @return
   */
  public Map<Attribute, AttributeValue> getLastLevelValuesFromNodeTree(final PidcVersionBO pidcVersionBO) {
    final Map<Attribute, AttributeValue> retMap = new HashMap<>();

    PIDCDetailsNode curNode = this;

    if (isVariantNode()) {
      curNode = getParentNode();
    }

    retMap.put(curNode.getNodeAttr(pidcVersionBO), curNode.getAttrValue());
    return retMap;
  }

  /**
   * @return type of this node
   */
  public NODE_TYPE getType() {
    return this.nodeType;
  }

  // ICDM-912
  /**
   * Returns the set of variant nodes under the passed virtual node
   *
   * @return set of variants under the node
   */
  public Set<PidcVariant> getVirtualNodeVars() {
    /**
     * list of Variants defined on this node
     */
    Set<PidcVariant> varSet = new HashSet<>();
    return getVariants(this, varSet);
  }

  /**
   * Returns the set of variant nodes under the passed virtual node
   *
   * @param node virtual node
   * @param varSet varaint nodes set
   * @return set of variants under the node
   */
  private Set<PidcVariant> getVariants(final PIDCDetailsNode node, final Set<PidcVariant> varSet) {
    SortedSet<PIDCDetailsNode> nodes = node.getVisibleChildNodes();
    for (PIDCDetailsNode selNode : nodes) {
      if (selNode.isVariantNode()) {
        varSet.add(selNode.getPidcVariant());
      }
      else {
        getVariants(selNode, varSet);
      }
    }
    return varSet;
  }


  /**
   * @param variant the variant to set
   */
  public void setVariant(final PidcVariant variant) {
    this.variant = variant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


}
