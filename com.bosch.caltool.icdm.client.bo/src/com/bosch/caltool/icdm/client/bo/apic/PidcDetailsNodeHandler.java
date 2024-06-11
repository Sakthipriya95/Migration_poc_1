/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * Manages virtual nodes of a PIDC
 *
 * @author bne4cob
 */
public class PidcDetailsNodeHandler {

  /**
   * PIDC Version
   */
  private final PidcVersion pidcVers;

  /**
   * PIDC Version Handler
   */
  private final PidcVersionBO pidcVersionBO;

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
   * @param pidcVersionBO
   */
  public PidcDetailsNodeHandler(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
    this.pidcVers = pidcVersionBO.getPidcVersion();
  }

  /**
   * Gets the root level nodes of this project ID card version. If virtual structure is not defined for this version,
   * the method returns null
   *
   * @return PIDC Nodes at the root level
   */
  public SortedSet<PIDCDetailsNode> getRootVirtualNodes() {
    if (!this.pidcVersionBO.hasVirtualStructure()) {
      return null;
    }

    if ((this.rootNodeSet == null) || this.rootNodeSet.isEmpty()) {
      loadNodes(this.pidcVersionBO.getVirtualLevelAttrs());
    }

    return this.rootNodeSet;
  }

  /**
   * Define the virtual nodes for the given PIDC virtual structure
   *
   * @param structMap virtual structure, key - level, value - PIDCDetStructure object
   */
  // ICDM-882
  private void loadNodes(final Map<Long, PidcDetStructure> structMap) {
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("Loading virtual nodes for PIDC : " + this.pidcVers.getName() + " ...");
      getLogger()
          .debug("Total nodes before refresh :" + this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().size());
    }

    Map<String, PIDCDetailsNode> varNodeMap = new HashMap<>();

    // ICDM-2198
    for (PidcVariant var : this.pidcVersionBO.getVariantsSet(this.includeDeleted, this.showOnlyUncleared)) {
      varNodeMap.putAll(createVarNodes(structMap, var));
    }
    Map<String, PIDCDetailsNode> allNodeMap = new HashMap<>(varNodeMap);

    if (this.rootNodeSet == null) {
      this.rootNodeSet = new TreeSet<>();
    }

    for (PIDCDetailsNode node : varNodeMap.values()) {
      allNodeMap.putAll(node.getParentNodes());
      this.rootNodeSet.add(node.getTopNode());
    }


    for (PIDCDetailsNode node : this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().values()) {
      // Hides all unused nodes. Used nodes and their parents are already set to visible above
      if (!allNodeMap.containsKey(node.getNodeID())) {
        node.setVisible(false);
      }
    }


    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("Virtual node loading completed. Total nodes after refresh :" +
          this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().size());
    }

  }

  /**
   * Create variant nodes and maps the sub variants
   *
   * @param structMap PIDC structure map
   * @param var Variant
   */
  private Map<String, PIDCDetailsNode> createVarNodes(final Map<Long, PidcDetStructure> structMap,
      final PidcVariant var) {

    Map<Long, IProjectAttribute> structVarAttrMap = new HashMap<>();
    Map<String, PIDCDetailsNode> retVarNodeMap = new HashMap<>();


    PidcVariantBO pidcVariantBO = new PidcVariantBO(this.pidcVers, var, this.pidcVersionBO.getPidcDataHandler());

    // Get the variant attributes of the structure
    Map<Long, PidcVariantAttribute> varAttrMap = pidcVariantBO.getAttributesAll();
    Attribute attr;
    for (PidcDetStructure str : structMap.values()) {
      attr = this.pidcVersionBO.getPidcDataHandler().getAttributeMap().get(str.getAttrId());
      PidcVariantAttribute varAttr = varAttrMap.get(attr.getId());
      structVarAttrMap.put(str.getPidAttrLevel(), varAttr);
    }


    // Generate nodes and map subvariants
    PIDCDetailsNode varNode;
    Map<Long, PidcSubVariant> subVarMap = pidcVariantBO.getSubVariantsMap(this.includeDeleted);
    if (subVarMap.isEmpty()) {

      varNode = PIDCDetailsNode.getPidcDetailsNode(structMap, var, structVarAttrMap, this.pidcVersionBO);
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
  private void addSVarsToNodes(final Map<Long, PidcDetStructure> structMap,
      final Map<String, PIDCDetailsNode> varNodeMap, final PidcVariant var,
      final Map<Long, IProjectAttribute> structVarAttrMap, final Map<Long, PidcSubVariant> subVarMap) {

    PIDCDetailsNode varNode;
    Map<Long, IProjectAttribute> svarStrAttrMap = new HashMap<>();
    Map<Long, PidcSubVariantAttribute> svarAllAttrMap;
    Attribute attr;

    for (PidcSubVariant subvar : subVarMap.values()) {
      svarStrAttrMap.clear();
      svarStrAttrMap.putAll(structVarAttrMap);

      PidcSubVariantBO pidcSubVarHandler =
          new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(), subvar, this.pidcVersionBO.getPidcDataHandler());
      svarAllAttrMap = pidcSubVarHandler.getAttributes();

      for (PidcDetStructure str : structMap.values()) {
        attr = this.pidcVersionBO.getPidcDataHandler().getAttributeMap().get(str.getAttrId());
        IProjectAttribute varAttr = structVarAttrMap.get(str.getPidAttrLevel());
        // Add the sub variant attribute, if variant attribute is at sub-variant level
        if ((varAttr != null) && varAttr.isAtChildLevel()) {
          svarStrAttrMap.put(str.getPidAttrLevel(), svarAllAttrMap.get(attr.getId()));
        }
      }
      varNode = PIDCDetailsNode.getPidcDetailsNode(structMap, var, svarStrAttrMap, this.pidcVersionBO);
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
    this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().clear();
  }

  /**
   * Refresh the nodes. To be used if attribute values are changed.
   */
  public void refreshNodes() {
    if (this.pidcVersionBO.hasVirtualStructure()) {
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Refreshing virtual nodes for PIDC : " + this.pidcVers.getName() + " ...");
      }

      clearRootNodes();
      for (PIDCDetailsNode node : this.pidcVersionBO.getPidcDataHandler().getPidcDetNodes().values()) {
        node.reset();
      }
      loadNodes(this.pidcVersionBO.getVirtualLevelAttrs());
    }
    else {
      // Clears the nodes if node structure is removed completely
      resetNodes();
    }
  }


  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
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
