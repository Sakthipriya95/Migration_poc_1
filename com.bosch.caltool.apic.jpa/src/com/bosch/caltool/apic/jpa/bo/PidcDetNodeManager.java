/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * Manages virtual nodes of a PIDC
 *
 * @author bne4cob
 */
// ICDM-882
class PidcDetNodeManager {

  /**
   * PIDC Version
   */
  private final PIDCVersion pidcVers;

  /**
   * Root nodes
   */
  private SortedSet<PIDCDetailsNode> rootNodeSet;

  // iCDM-911
  /**
   * flag to indicate to display deleted variants/sub-varaints or not
   */
  private transient boolean includeDeleted;

  // ICDM-2198
  /**
   * flag to indicate to display deleted uncleared / missing mandatory variants or not
   */
  private transient boolean showOnlyUncleared;

  /**
   * @param pidcVers PIDCard version
   */
  public PidcDetNodeManager(final PIDCVersion pidcVers) {
    this.pidcVers = pidcVers;
  }

  /**
   * Gets the root level nodes of this project ID card version. If virtual structure is not defined for this version,
   * the method returns null
   *
   * @return PIDC Nodes at the root level
   */
  public SortedSet<PIDCDetailsNode> getRootVirtualNodes() {
    if (!this.pidcVers.hasVirtualStructure()) {
      return null;
    }

    if ((this.rootNodeSet == null) || this.rootNodeSet.isEmpty()) {
      loadNodes(this.pidcVers.getVirtualLevelAttrs());
    }

    return this.rootNodeSet;
  }

  /**
   * Define the virtual nodes for the given PIDC virtual structure
   *
   * @param structMap virtual structure, key - level, value - PIDCDetStructure object
   */
  // ICDM-882
  private void loadNodes(final Map<Long, PIDCDetStructure> structMap) {
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("Loading virtual nodes for PIDC : " + this.pidcVers.getName() + " ...");
      getLogger().debug("Total nodes before refresh :" + getDataCache().getPidcDetNodes(this.pidcVers.getID()).size());
    }

    Map<String, PIDCDetailsNode> varNodeMap = new HashMap<String, PIDCDetailsNode>();

    // ICDM-2198
    for (PIDCVariant var : this.pidcVers.getVariantsSet(this.includeDeleted, this.showOnlyUncleared)) {
      varNodeMap.putAll(createVarNodes(structMap, var));
    }
    Map<String, PIDCDetailsNode> allNodeMap = new HashMap<String, PIDCDetailsNode>(varNodeMap);

    if (this.rootNodeSet == null) {
      this.rootNodeSet = new TreeSet<PIDCDetailsNode>();
    }

    for (PIDCDetailsNode node : varNodeMap.values()) {
      allNodeMap.putAll(node.getParentNodes());
      this.rootNodeSet.add(node.getTopNode());
    }
    for (PIDCDetailsNode node : getDataCache().getPidcDetNodes(this.pidcVers.getID()).values()) {
      // Hides all unused nodes. Used nodes and their parents are already set to visible above
      if (!allNodeMap.containsKey(node.getNodeID())) {
        node.setVisible(false);
      }
    }
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("Virtual node loading completed. Total nodes after refresh :" +
          getDataCache().getPidcDetNodes(this.pidcVers.getID()).size());
    }

  }

  /**
   * Create variant nodes and maps the sub variants
   *
   * @param structMap PIDC structure map
   * @param var Variant
   */
  private Map<String, PIDCDetailsNode> createVarNodes(final Map<Long, PIDCDetStructure> structMap,
      final PIDCVariant var) {

    Map<Long, IPIDCAttribute> structVarAttrMap = new HashMap<>();
    Map<String, PIDCDetailsNode> retVarNodeMap = new HashMap<String, PIDCDetailsNode>();


    // Get the variant attributes of the structure
    Map<Long, PIDCAttributeVar> varAttrMap = var.getAttributes(true, true);
    Attribute attr;
    for (PIDCDetStructure str : structMap.values()) {
      attr = getDataCache().getAttribute(str.getAttrID());
      PIDCAttributeVar varAttr = varAttrMap.get(attr.getAttributeID());
      structVarAttrMap.put(str.getPidAttrLevel(), varAttr);
    }


    // Generate nodes and map subvariants
    PIDCDetailsNode varNode;
    Map<Long, PIDCSubVariant> subVarMap = var.getSubVariantsMap(this.includeDeleted);
    if (subVarMap.isEmpty()) {
      varNode = PIDCDetailsNode.getPidcDetailsNode(getDataProvider(), structMap, var, structVarAttrMap);
      varNode.setVisible(true);
      retVarNodeMap.put(varNode.getNodeID(), varNode);
    }
    else {
      addSVarsToNodes(structMap, retVarNodeMap, var, structVarAttrMap, subVarMap);
    }

    return retVarNodeMap;
  }

  /**
   * Add the sub variants to variant nodes
   *
   * @param structMap map of PIDCDetStructure, key - level, value - PIDCDetStructure object
   * @param varNodeMap map of variant nodes, already created in this project
   * @param var variant
   * @param structVarAttrMap values of structure attributes in the variant level
   * @param subVarMap map of sub variants
   */
  // ICDM-882
  private void addSVarsToNodes(final Map<Long, PIDCDetStructure> structMap,
      final Map<String, PIDCDetailsNode> varNodeMap, final PIDCVariant var,
      final Map<Long, IPIDCAttribute> structVarAttrMap, final Map<Long, PIDCSubVariant> subVarMap) {

    PIDCDetailsNode varNode;
    Map<Long, IPIDCAttribute> svarStrAttrMap = new HashMap<>();
    Map<Long, PIDCAttributeSubVar> svarAllAttrMap;
    Attribute attr;

    for (PIDCSubVariant subvar : subVarMap.values()) {
      svarStrAttrMap.clear();
      svarStrAttrMap.putAll(structVarAttrMap);

      svarAllAttrMap = subvar.getAttributes(true, true);

      for (PIDCDetStructure str : structMap.values()) {
        attr = getDataCache().getAttribute(str.getAttrID());
        IPIDCAttribute varAttr = structVarAttrMap.get(str.getPidAttrLevel());
        // Add the sub variant attribute, if variant attribute is at sub-variant level
        if ((varAttr != null) && varAttr.isVariant()) {
          svarStrAttrMap.put(str.getPidAttrLevel(), svarAllAttrMap.get(attr.getAttributeID()));
        }
      }
      varNode = PIDCDetailsNode.getPidcDetailsNode(getDataProvider(), structMap, var, svarStrAttrMap);
      varNode.setVisible(true);
      varNodeMap.put(varNode.getNodeID(), varNode);
      varNode.getSubVariants().add(subvar);
    }
  }

  /**
   * Clears all virtual nodes of this PIDC
   */
  private void clearRootNodes() {
    if (this.rootNodeSet != null) {
      this.rootNodeSet.clear();
    }

  }

  /**
   * Reset the virtual node structure. To be used if the virtual structure is changed
   */
  public void resetNodes() {
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("Resetting virtual nodes for PIDC : " + this.pidcVers.getName() + " ...");
    }

    clearRootNodes();
    getDataCache().getPidcDetNodes(this.pidcVers.getID()).clear();
  }

  /**
   * Refresh the nodes. To be used if attribute values are changed.
   */
  public void refreshNodes() {
    if (this.pidcVers.hasVirtualStructure()) {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Refreshing virtual nodes for PIDC : " + this.pidcVers.getName() + " ...");
      }

      clearRootNodes();
      for (PIDCDetailsNode node : getDataCache().getPidcDetNodes(this.pidcVers.getID()).values()) {
        node.reset();
      }
      loadNodes(this.pidcVers.getVirtualLevelAttrs());
    }
    else {
      // Clears the nodes if node structure is removed completely
      resetNodes();
    }
  }

  private ApicDataProvider getDataProvider() {
    return this.pidcVers.getDataCache().getDataProvider();
  }

  private DataCache getDataCache() {
    return this.pidcVers.getDataCache();
  }

  private ILoggerAdapter getLogger() {
    return this.pidcVers.getDataCache().getDataProvider().getLogger();
  }

  // iCDM-911
  /**
   * @param includeDeletedVarSubVars true to include deleted items
   */
  public void setIncludeDeletedVarSubVars(final boolean includeDeletedVarSubVars) {
    this.includeDeleted = includeDeletedVarSubVars;
  }

  /**
   * @return the showOnlyUncleared
   */
  public boolean isShowOnlyUncleared() {
    return this.showOnlyUncleared;
  }

  /**
   * @param showOnlyUncleared the boolean to show only uncleared/missing mandatory items in PIDC structure view
   */
  public void setShowOnlyUncleared(final boolean showOnlyUncleared) {
    this.showOnlyUncleared = showOnlyUncleared;
  }


}
