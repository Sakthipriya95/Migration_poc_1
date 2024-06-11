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
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.FavouritesTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * Contents provider for FavoritesTreeView
 *
 * @author dja7cob
 */
public class FavoritesTreeViewContentProvider implements ITreeContentProvider {

  private final FavouritesTreeNodeHandler favTreeHandler;

  /**
   * PidcTreeNodeHandler instance
   */
  private final PidcTreeNodeHandler treeNodeHandler;

  private Object[] variantListForSelectedA2L;

  boolean noVariantFlag = false;

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
   * Flag to indicate whether inactive a2l nodes need to be displayed
   */
  private final boolean includeInactiveA2lFiles;
  /**
   * Flag to indicate whether the data is for tree view part
   */
  private final boolean isTreeViewPart;

  private Map<Long, PidcVariant> pidcVarMap;

  /**
   * Constructor
   *
   * @param treeHandler PidcTreeNodeHandler instance
   * @param favTreeHandler FavouritesTreeNodeHandler instance
   */
  public FavoritesTreeViewContentProvider(final PidcTreeNodeHandler treeHandler,
      final FavouritesTreeNodeHandler favTreeHandler, final boolean loadA2l, final boolean loadCdr,
      final boolean loadVariants, final boolean loadQnaires, final boolean isTreeViewPart) {
    this.favTreeHandler = favTreeHandler;
    this.treeNodeHandler = treeHandler;
    this.loadA2l = loadA2l;
    this.loadCdr = loadCdr;
    this.loadVariants = loadVariants;
    this.loadQnaires = loadQnaires;
    // inactive A2Ls are loaded by default for the favourites view in all cases.
    // So the value is set to true by default.
    this.includeInactiveA2lFiles = true;
    this.isTreeViewPart = isTreeViewPart;
  }

  /**
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // Not applicable
  }

  /**
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Not applicable
  }

  /**
   *
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    SortedSet<PidcTreeNode> favNodes = new TreeSet<>();
    favNodes.addAll(this.favTreeHandler.getFavPidcNodeIdNodeMap().values());
    return favNodes.toArray();
  }

  /**
   *
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof PidcTreeNode) {
      PidcTreeNode parentNode = (PidcTreeNode) parentElement;
      if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
          parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        return getPidcChildren(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        return getA2lList(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L) && ReviewResultClientBO
          .isDivIdMappedToGivComParamKey(parentNode.getPidcVersion().getId(), CommonParamKey.DIVISIONS_WITH_QNAIRES)) {
        this.treeNodeHandler.loadPidcA2lStructureModel(parentNode);
        return this.treeNodeHandler.getVariantsForPidcA2l(parentNode,
            PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
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
    if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE)) {
      return this.treeNodeHandler.getA2lWPForQnaireResponse(parentNode);
    }

    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
      return this.treeNodeHandler.getRvwQnaireRespNodes(parentNode,
          PIDTreeViewPart.isDisplayDeletedQnaireResponseEnabled());
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.CDR_VAR_NODE)) {
      return this.treeNodeHandler.getTitlesForRvwResults(parentNode).toArray();
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE)) {
      return this.treeNodeHandler.getOtherPidcVerNodes(parentNode).toArray();
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW)) {
      return getRvwVariantNodes(parentNode, PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE)) {
      return this.treeNodeHandler.getQuestionnaireVariants(parentNode,
          PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled(), this.variantListForSelectedA2L);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE)) {
      return this.treeNodeHandler.getA2LRespForQnaireResp(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE)) {
      return this.treeNodeHandler.getOtherRvwScopesNodes(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE)) {
      return this.treeNodeHandler.getOtherScopeRvwRes(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE)) {
      return this.treeNodeHandler.getWorkpackages(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE)) {
      return this.treeNodeHandler.getWPResults(parentNode);
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
    if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      return this.treeNodeHandler.getRvwQnaireRespNodesForPidcA2l(parentNode,
          PIDTreeViewPart.isDisplayDeletedQnaireResponseEnabled());
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)) {
      return this.treeNodeHandler.getA2lWPForPidcA2l(parentNode);
    }
    else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE)) {
      Object[] variantsForPidcA2l = this.treeNodeHandler.getVariantsForPidcA2l(parentNode.getParentNode(),
          PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
      return this.treeNodeHandler.getA2LRespForPidcA2l(parentNode, variantsForPidcA2l);
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
  private Object[] getPidcChildren(final PidcTreeNode parentNode) {
    if (parentNode.getChildNodesMap().isEmpty()) {
      this.treeNodeHandler.getPidcVerNodeChildren(parentNode);
    }
    SortedSet<PidcTreeNode> childSet = this.treeNodeHandler.getPidcChildren(parentNode, this.loadA2l, this.loadCdr,
        this.loadQnaires, this.loadVariants);
    if (!(childSet.isEmpty()) && (this.pidcVarMap != null) && (!this.pidcVarMap.isEmpty()) && this.pidcVarMap.values()
        .iterator().next().getPidcVersionId().equals(childSet.first().getPidcVersion().getId())) {
      SortedSet<PidcTreeNode> varSet = new TreeSet<>();
      for (PidcTreeNode pidcTreeNode : childSet) {
        if (!this.pidcVarMap.containsKey(pidcTreeNode.getPidcVariant().getId())) {
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
    this.treeNodeHandler.getReviewDetails(parentNode);
    return this.treeNodeHandler.getRvwVariantNodes(parentNode, includeDeletedVariant).toArray();
  }

  /*
   *
   */
  @Override
  public Object getParent(final Object element) {
    if (element instanceof PidcTreeNode) {
      // getting parent for pidc node
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if (null != pidcNode.getParentNodeId()) {
        if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
          return null;
        }
        return this.treeNodeHandler.getNodeIdNodeMap().get(pidcNode.getParentNodeId());
      }
    }
    return null;
  }

  /*
   *
   */
  @Override
  public boolean hasChildren(final Object element) {
    // Check if node has child nodes
    if (element instanceof PidcTreeNode) {
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        PidcTreeNode pidcVerNode = pidcNode.getParentNode();
        return (null != pidcVerNode) && (null != pidcVerNode.getSdomA2lMap()) &&
            (null != pidcVerNode.getSdomA2lMap().get(pidcNode.getSdomPver().getPverName())) &&
            !pidcVerNode.getSdomA2lMap().get(pidcNode.getSdomPver().getPverName()).isEmpty();
      }
      else if (checkForPidcNodeType(pidcNode)) {
        // if the parent node level is pidc version level, render the other pidc version nodes
        return true;
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
        return expandQnaireWpNode(pidcNode);
      }
    }

    return false;
  }

  /**
   * @param pidcNode
   * @return
   */
  private boolean checkForPidcNodeType(final PidcTreeNode pidcNode) {
    return pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION) ||
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
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE);
  }

  /**
   * @param pidcNode
   * @return
   */
  private boolean expandQnaireWpNode(final PidcTreeNode pidcNode) {
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
