/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

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


/**
 * @author bne4cob
 */
public final class PIDCDetailsNode implements Comparable<PIDCDetailsNode>, IPastableItem {

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
  public static enum NODE_TYPE {
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
   * Data provider
   */
  private final ApicDataProvider dataProvider;

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
  private final PIDCDetStructure struct;

  /**
   * Parent node. For top level nodes, this is null
   */
  private final PIDCDetailsNode parent;

  /**
   * Variant object, for variant type nodes
   */
  private final PIDCVariant variant;

  /**
   * If true, then the node is visible
   */
  private boolean visible;

  /**
   * Parent Project version of this node
   */
  private final PIDCVersion pidcVers;

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
  private final SortedSet<PIDCSubVariant> subvarSet = Collections.synchronizedSortedSet(new TreeSet<PIDCSubVariant>());


  /**
   * Constructor for variant node
   *
   * @param nodeID node ID
   * @param dataProvider apic data provider
   * @param var variant
   * @param parentNode parent node
   */
  private PIDCDetailsNode(final String nodeID, final ApicDataProvider dataProvider, final PIDCVariant var,
      final PIDCDetailsNode parentNode) {

    this.dataProvider = dataProvider;
    this.pidcVers = var.getPidcVersion();
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
   */
  private PIDCDetailsNode(final ApicDataProvider dataProvider, final PIDCDetailsNode parent,
      final PidcNodeProps nodeProps) {

    this.dataProvider = dataProvider;
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
    this.dataProvider.getDataCache().getPidcDetNodes(this.pidcVers.getID()).put(this.nodeID, this);
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
    return super.equals(obj);
  }
  /**
   * @return PIDC Variant
   */
  public PIDCVariant getPidcVariant() {
    return this.variant;
  }

  /**
   * @return
   */
  private AttributeValue getAttrValue() {
    return this.attrValue;
  }

  /**
   * @return whether this node is deleted
   */
  public boolean isDeleted() {
    if (this.attrValue != null) {
      return this.attrValue.isDeleted() || this.attrValue.getAttribute().isDeleted();
    }
    if (this.struct != null) {
      return this.dataProvider.getAttribute(this.struct.getAttrID()).isDeleted();
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
      return this.variant.getVariantName();
    }

    if (this.attrValue != null) {
      return this.attrValue.getValue(true);
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
   * @param dataProvider apic data provider
   * @param pidStructMap pid Struct Map
   * @param var variant
   * @param iPidcAttrMap IPIDCAttribute Map
   * @return PidcDetailsNode instance
   */
  public static PIDCDetailsNode getPidcDetailsNode(final ApicDataProvider dataProvider,
      final Map<Long, PIDCDetStructure> pidStructMap, final PIDCVariant var,
      final Map<Long, IPIDCAttribute> iPidcAttrMap) {
    final String nodeID = generateNodeID(var, iPidcAttrMap);
    PIDCDetailsNode retNode = dataProvider.getDataCache().getPidcDetNode(var.getPidcVersion().getID(), nodeID);
    if (retNode == null) {
      retNode = new PIDCDetailsNode(nodeID, dataProvider, var,
          getParentNode(dataProvider, var.getPidcVersion(), pidStructMap, nodeID));
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
  private static PIDCDetailsNode getParentNode(final ApicDataProvider dataProvider, final PIDCVersion pidcVers,
      final Map<Long, PIDCDetStructure> pidStructMap, final String nodeID) {

    PidcNodeProps nodeProps = PidcNodeProps.getNodeProps(pidcVers, pidStructMap, nodeID);
    String parentNodeID = nodeProps.getParentID();
    if (CommonUtils.isEmptyString(parentNodeID)) {
      // No parent Nodes
      return null;
    }

    PIDCDetailsNode retNode = dataProvider.getDataCache().getPidcDetNode(pidcVers.getID(), parentNodeID);
    if (retNode == null) {
      // Create node
      PidcNodeProps parProps = PidcNodeProps.getNodeProps(pidcVers, pidStructMap, parentNodeID);
      PIDCDetailsNode myParent = getParentNode(dataProvider, pidcVers, pidStructMap, nodeProps.getParentID());
      retNode = new PIDCDetailsNode(dataProvider, myParent, parProps);
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
  public static String generateNodeID(final PIDCVariant variant, final Map<Long, IPIDCAttribute> pidcAttrMap) {
    IPIDCAttribute pidcAttr;
    Long attrID;
    String displVal;

    StringBuilder idBuilder = new StringBuilder();

    for (long level = 1; level <= pidcAttrMap.size(); level++) {
      if (level > 1) {
        idBuilder.append(SEPARATOR);
      }

      pidcAttr = pidcAttrMap.get(level);

      if (pidcAttr == null) {
        attrID = variant.getPidcVersion().getVirtualLevelAttrs().get(level).getAttrID();
        displVal = "";
      }
      else {
        attrID = pidcAttr.getAttribute().getID();
        displVal = pidcAttr.getDefaultValueDisplayName();
      }
      if ((pidcAttr == null) || (CommonUtils.isEmptyString(displVal) &&
          !ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedInfo()))) {
        idBuilder.append(NOT_DEFINED);
        idBuilder.append(attrID);
        break;
      }

      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedInfo())) {
        idBuilder.append(NOT_USED);
        idBuilder.append(attrID);
        break;
      }


      idBuilder.append(VAL_PREFIX).append(pidcAttr.getAttributeValue().getValueID());

    }

    idBuilder.append(SEPARATOR).append(VAR_PREFIX).append(variant.getVariantID());
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
  public SortedSet<PIDCSubVariant> getSubVariants() {
    return this.subvarSet;
  }

  /**
   * @return the structure object of this node
   */
  public PIDCDetStructure getStructureObject() {
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
   * @return true/false
   */
  public boolean canAddVariants() {
    if (isVariantNode() || isDeleted()) {
      return false;
    }

    PIDCDetailsNode curNode;
    PIDCDetailsNode parentNode = getParentNode();
    while (parentNode != null) {
      if (parentNode.isDeleted()) {
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
  public Attribute getNodeAttr() {
    if (this.struct != null) {
      return this.dataProvider.getAttribute(getStructureObject().getAttrID());
    }
    return null;
  }

  /**
   * Gets the tooltip for the node
   *
   * @return 'undefined' if the node is undefined node, variant name if the node is variant, attribute name if the node
   *         is a level node
   */
  public String getToolTip() {

    // For variant nodes, return the variant name
    if (isVariantNode()) {
      return this.variant.getToolTip();// ICDM-1107
    }

    // For value nodes, return the node level, attibute name and value name

    StringBuilder valNodeTip = new StringBuilder(TT_BUF_START_SIZE);

    valNodeTip.append(" Level : ").append(getLevel());

    Attribute nodeAttr = getNodeAttr();
    valNodeTip.append("\n Attribute : ").append(nodeAttr.getName());
    if (getNodeAttr().isDeleted()) {
      valNodeTip.append(' ').append(ApicConstants.DEL_TEXT);
    }

    valNodeTip.append("\n Value : ");
    if (this.nodeType == NODE_TYPE.VAL_UNDEF) {
      valNodeTip.append(PIDCDetailsNode.NOT_ASSIGNED);
    }
    else if (this.nodeType == NODE_TYPE.VAL_NOTUSED) {
      valNodeTip.append(PIDCDetailsNode.NOT_USED_UI);
    }
    else {
      valNodeTip.append(getAttrValue().getValue(true));
      if (getAttrValue().isDeleted()) {
        valNodeTip.append(' ').append(ApicConstants.DEL_TEXT);
      }
    }

    return valNodeTip.toString();
  }

  /**
   * @return PID Card version
   */
  public PIDCVersion getPidcVersion() {
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
  public Map<Attribute, AttributeValue> getAttrValuesFromNodeTree() {
    final Map<Attribute, AttributeValue> retMap = new HashMap<Attribute, AttributeValue>();

    PIDCDetailsNode curNode = this;

    if (isVariantNode()) {
      curNode = getParentNode();
    }

    retMap.put(curNode.getNodeAttr(), curNode.getAttrValue());

    PIDCDetailsNode parentNode = getParentNode();
    while (parentNode != null) {
      retMap.put(parentNode.getNodeAttr(), parentNode.getAttrValue());
      curNode = parentNode;
      parentNode = curNode.getParentNode();
    }

    return retMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    if (((PIDCDetailsNode) copiedObj).isVariantNode()) {
      if (selectedObj instanceof PIDCDetailsNode) {
        if (((PIDCDetailsNode) copiedObj).getPidcVariant().isPasteAllowed(
            ((PIDCDetailsNode) selectedObj).getPidcVariant(), ((PIDCDetailsNode) copiedObj).getPidcVariant())) {
          return true;
        }
      }
      else if ((selectedObj instanceof PIDCVersion) && ((PIDCDetailsNode) copiedObj).getPidcVariant()
          .isPasteAllowed(selectedObj, ((PIDCDetailsNode) copiedObj).getPidcVariant())) {
        return true;
      }

    }
    return false;
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
  public Set<PIDCVariant> getVirtualNodeVars() {
    /**
     * list of Variants defined on this node
     */
    Set<PIDCVariant> varSet = new HashSet<PIDCVariant>();
    return getVariants(this.dataProvider.getDataCache().getPidcDetNodes(this.pidcVers.getID()).get(this.nodeID),
        varSet);
  }

  /**
   * Returns the set of variant nodes under the passed virtual node
   *
   * @param node virtual node
   * @param varSet varaint nodes set
   * @return set of variants under the node
   */
  private Set<PIDCVariant> getVariants(final PIDCDetailsNode node, final Set<PIDCVariant> varSet) {
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
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
