/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author dja7cob
 */
public class PIDTreeViewContentProvider implements ITreeContentProvider {

  /**
   * PidcTreeNodeHandler instance
   */
  private final PidcTreeNodeHandler treeNodeHandler;

  /**
   * Maximum level of PIDC structure attributes
   */
  private final int pidcStructMaxLvl;
  /**
   * Flag to load A2l files
   */
  private final boolean loadA2l;
  /**
   * Flag to load review results
   */
  private final boolean loadCdr;
  /**
   * Flag to load variants
   */
  private final boolean loadVariants;
  /**
   * Flag to load qnaires
   */
  private final boolean loadQnaires;
  /**
   * Flag to indicate whether deleted child nodes need to be displayed
   */
  private final boolean includeDelChildren;
  /**
   * Flag to indicate whether inactive a2l nodes need to be displayed
   */
  private final boolean includeInactiveA2lFiles;
  /**
   * Flag to indicate whether the data is for tree view part
   */
  private final boolean isTreeViewPart;

  private Map<Long, PidcVariant> pidcVarMap;

  private Object[] variantListForSelectedA2L;

  Object[] pidcChildren;

  Object[] noVariantResponse;

  boolean noVariantFlag = false;

  /**
   * Constructor
   *
   * @param treeHandler pidc tree node handler instance
   * @param loadA2l flag to indicate whether a2l files has to be loaded
   * @param loadCdr flag to indicate whether cdr has to be loaded
   * @param loadVariants flag to indicate whether pidc variants has to be loaded
   * @param loadQnaires flag to indicate whether qnaires has to be loaded
   * @param includeDelChildren flag to indicate whether deleted child nodes need to be displayed
   * @param includeInactiveA2lFiles flag to indicate whether inactive a2l nodes need to be displayed
   */
  public PIDTreeViewContentProvider(final PidcTreeNodeHandler treeHandler,
      final TreeViewFlagValueProvider flagValueProvider) {
    this.treeNodeHandler = treeHandler;
    ApicDataBO apicBO = new ApicDataBO();
    this.pidcStructMaxLvl = apicBO.getPidcStructMaxLvl();
    this.loadA2l = flagValueProvider.isLoadA2l();
    this.loadCdr = flagValueProvider.isLoadCdr();
    this.loadVariants = flagValueProvider.isLoadVariants();
    this.loadQnaires = flagValueProvider.isLoadQnaires();
    this.includeDelChildren = flagValueProvider.isIncludeDelChildren();
    this.includeInactiveA2lFiles = flagValueProvider.isIncludeInactiveA2lFiles();
    this.isTreeViewPart = flagValueProvider.isTreeViewPart();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    // input element is string
    if (inputElement instanceof String) {
      return new Object[] { this.treeNodeHandler.getRootNode() };
    }
    return new Object[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof PidcTreeNode) {
      PidcTreeNode parentNode = (PidcTreeNode) parentElement;
      if (parentNode.getLevel() == this.pidcStructMaxLvl) {
        return getPidcVersionNodes(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
          parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        this.pidcChildren = getPIDCChildren(parentNode);
        return this.pidcChildren;
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        return getA2lList(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        this.treeNodeHandler.loadPidcA2lStructureModel(parentNode);
        return this.treeNodeHandler.getVariantsForPidcA2l(parentNode,
            PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.CDR_VAR_NODE)) {
        return this.treeNodeHandler.getTitlesForRvwResults(parentNode).toArray();
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
        return this.treeNodeHandler
            .getChildNodesSorted(parentNode, !PIDTreeViewPart.hideEmptyNodes() && this.loadQnaires).toArray();
      }
      else {
        return getRvwResultAndQnaireRelatedNodes(parentNode);
      }
    }
    return new Object[0];
  }


  /**
   * @param parentNode
   * @return Object[]
   */
  private Object[] getRvwResultAndQnaireRelatedNodes(final PidcTreeNode parentNode) {
    if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE)) {
      return this.treeNodeHandler.getOtherPidcVerNodes(parentNode).toArray();
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE)) {
      return this.treeNodeHandler.getA2lWPForQnaireResponse(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
      return this.treeNodeHandler.getRvwQnaireRespNodes(parentNode,
          PIDTreeViewPart.isDisplayDeletedQnaireResponseEnabled());
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW)) {
      return getRvwVariantNodes(parentNode, PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE)) {
      return this.treeNodeHandler.getOtherRvwScopesNodes(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE)) {
      return this.treeNodeHandler.getOtherScopeRvwRes(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE)) {
      return this.treeNodeHandler.getWPResults(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE)) {
      return this.treeNodeHandler.getQuestionnaireVariants(parentNode,
          PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled(), this.variantListForSelectedA2L);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE)) {
      return this.treeNodeHandler.getA2LRespForQnaireResp(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE)) {
      return this.treeNodeHandler.getWorkpackages(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE)) {
      return this.treeNodeHandler.getResponsibilities(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE)) {
      return this.treeNodeHandler.getRespWPNodes(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE)) {
      return this.treeNodeHandler.getRespWPResults(parentNode);
    }
    else {
      return getPidcA2lStructureNodes(parentNode);
    }
  }


  /**
   * @param parentNode
   */
  private Object[] getPidcA2lStructureNodes(final PidcTreeNode parentNode) {
    if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)) {
      return this.treeNodeHandler.getA2lWPForPidcA2l(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE)) {
      Object[] variantsForPidcA2l = this.treeNodeHandler.getVariantsForPidcA2l(parentNode.getParentNode(),
          PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
      return this.treeNodeHandler.getA2LRespForPidcA2l(parentNode, variantsForPidcA2l);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      return this.treeNodeHandler.getRvwQnaireRespNodesForPidcA2l(parentNode,
          PIDTreeViewPart.isDisplayDeletedQnaireResponseEnabled());
    }
    return new Object[0];
  }


  /**
   * @param parentNode
   * @return
   */
  private Object[] getA2lList(final PidcTreeNode parentNode) {
    if (this.isTreeViewPart) {
      return this.treeNodeHandler.getA2lNodeList(parentNode, PIDTreeViewPart.displayInactiveA2LFiles()).toArray();
    }
    return this.treeNodeHandler.getA2lNodeList(parentNode, this.includeInactiveA2lFiles).toArray();
  }


  /**
   * @param parentNode
   * @return
   */
  private Object[] getPIDCChildren(final PidcTreeNode parentNode) {
    if (parentNode.getChildNodesMap().isEmpty()) {
      this.treeNodeHandler.getPidcVerNodeChildren(parentNode);
    }

    SortedSet<PidcTreeNode> childSet = this.treeNodeHandler.getPidcChildren(parentNode, this.loadA2l, this.loadCdr,
        this.loadQnaires, this.loadVariants);

    if (!(childSet.isEmpty()) && (this.pidcVarMap != null) && (!this.pidcVarMap.isEmpty()) && this.pidcVarMap.values()
        .iterator().next().getPidcVersionId().equals(childSet.first().getPidcVersion().getId())) {
      SortedSet<PidcTreeNode> varSet = new TreeSet<>();
      for (PidcTreeNode pidcTreeNode : childSet) {
        if ((pidcTreeNode.getPidcVariant() != null) &&
            !this.pidcVarMap.containsKey(pidcTreeNode.getPidcVariant().getId())) {
          varSet.add(pidcTreeNode);
        }
      }
      childSet.removeAll(varSet);
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  private Object[] getRvwVariantNodes(final PidcTreeNode parentNode, final boolean includeDeletedVariant) {
    parentNode.getPidcRvwDetails();
    return this.treeNodeHandler.getRvwVariantNodes(parentNode, includeDeletedVariant).toArray();
  }


  /**
   * @param parentNode
   * @return
   */
  private Object[] getPidcVersionNodes(final PidcTreeNode parentNode) {
    // if the parent node level is the maximum structure attribute level, render the pidc version nodes
    SortedSet<PidcTreeNode> pidcVerNodes = new TreeSet<>();
    if (this.includeDelChildren) {
      pidcVerNodes.addAll(parentNode.getChildNodesMap().values());
    }
    else {
      pidcVerNodes.addAll(parentNode.getUnDelchildNodesMap().values());
    }
    return pidcVerNodes.toArray();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object element) {
    if (element instanceof PidcTreeNode) {
      // getting parent for pidc node
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if ((null != pidcNode.getNodeType()) && pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
        return pidcNode.getParentNode();
      }
      return this.treeNodeHandler.getNodeIdNodeMap().get(pidcNode.getParentNodeId());
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object element) {
    // Check if node has child nodes
    if (element instanceof PidcTreeNode) {
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if (pidcNode.getLevel() == this.pidcStructMaxLvl) {
        // if the parent node level is the maximum structure attribute level, check for the availability of pidc version
        // nodes
        return (pidcNode.getLevel() == this.pidcStructMaxLvl) && (null != pidcNode.getChildNodesMap()) &&
            !pidcNode.getChildNodesMap().isEmpty();
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        PidcTreeNode pidcVerNode = pidcNode.getParentNode();
        return (null != pidcVerNode) && (null != pidcVerNode.getSdomA2lMap()) &&
            (null != pidcVerNode.getSdomA2lMap().get(pidcNode.getSdomPver().getPverName())) &&
            !pidcVerNode.getSdomA2lMap().get(pidcNode.getSdomPver().getPverName()).isEmpty();
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
        return fetchQnaireWpChildren(pidcNode);
      }

      else if (checkForNodeType(pidcNode)) {
        return true;
      }
      // make changes here
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        return getVariants(pidcNode);
      }
      return (pidcNode.getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) &&
          !this.treeNodeHandler.getParentIdChildNodesMap().get(pidcNode.getNodeId()).isEmpty();
    }
    return false;
  }


  /**
   * @param pidcNode
   * @return
   */
  private boolean checkForNodeType(final PidcTreeNode pidcNode) {
    return pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.CDR_VAR_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE);
  }


  /**
   * @param pidcNode
   * @return
   */
  private boolean getVariants(final PidcTreeNode pidcNode) {
    if (this.loadVariants) {
      return this.treeNodeHandler.hasVariants(pidcNode);
    }
    return true;
  }


  /**
   * @param pidcNode
   * @return
   */
  private boolean fetchQnaireWpChildren(final PidcTreeNode pidcNode) {
    PidcTreeNode varNode = pidcNode.getParentNode();
    Map<Long, Map<Long, Set<Long>>> respWpMap = new HashMap<>();
    boolean expandResp = false;

    if (varNode.getQnaireInfo() != null) {
      respWpMap = varNode.getQnaireInfo().getVarRespWpQniareMap().get(varNode.getPidcVariant().getId());
    }
    if (respWpMap != null) {
      expandResp = true;
    }
    return expandResp;
  }


  /**
   * @return the treeNodeHandler
   */
  public PidcTreeNodeHandler getTreeNodeHandler() {
    return this.treeNodeHandler;
  }

  /**
   * @return the pidcVarMap
   */
  public Map<Long, PidcVariant> getPidcVarMap() {
    return this.pidcVarMap;
  }


  /**
   * @param pidcVarMap the pidcVarMap to set
   */
  public void setPidcVarMap(final Map<Long, PidcVariant> pidcVarMap) {
    this.pidcVarMap = pidcVarMap;
  }

}
