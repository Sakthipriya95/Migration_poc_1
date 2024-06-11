/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.cdr.ui.dialogs.HexFileSelectionDialog;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;


/**
 * New Content Provider for getting Values for PIDC tree only
 *
 * @author svj7cob
 */
public class CompliReviewPIDTreeViewContentProvider implements ITreeContentProvider {

  /**
   * pidc version
   */
  private HexFileSelectionDialog hexFileSelectionDialog;

  /**
   * data provider
   */
  private final PidcTreeNodeHandler treeNodeHandler;

  /**
   * Maximum level of PIDC structure attributes
   */
  private final int pidcStructMaxLvl;

  /**
   * PIDTreeViewContentProvider - Constructor
   */
  public CompliReviewPIDTreeViewContentProvider(final PidcTreeNodeHandler treeHandler) {
    this.treeNodeHandler = treeHandler;
    ApicDataBO apicBO = new ApicDataBO();
    this.pidcStructMaxLvl = apicBO.getPidcStructMaxLvl();

  }

  /**
   * @return true if variant required
   */
  private boolean isVariantNeeded() {
    return null != getHexFileSelectionDialog();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // Not applicable
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Not applicable
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    // input element is string
    if (inputElement instanceof String) {
      return new Object[] { this.treeNodeHandler.getRootNode() };
    }
    return new Object[0];
  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof PidcTreeNode) {
      PidcTreeNode parentNode = (PidcTreeNode) parentElement;
      if (parentNode.getLevel() == this.pidcStructMaxLvl) {
        // if the parent node level is the maximum structure attribute level, render the pidc version nodes
        SortedSet<PidcTreeNode> pidcVerNodes = new TreeSet<>();
        pidcVerNodes.addAll(parentNode.getChildNodesMap().values());
        return pidcVerNodes.toArray();
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
        return getPidcNodes(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        return getPidcNodes(parentNode);
      }
      else if (parentNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE)) {
        return this.treeNodeHandler
            .getOtherPidcVerNodes(this.treeNodeHandler.getNodeIdNodeMap().get(parentNode.getParentNodeId())).toArray();
      }
      return this.treeNodeHandler.getChildNodesSorted(parentNode, false).toArray();
    }

    if (parentElement instanceof String[]) {
      final String[] parent = (String[]) parentElement;
      String parentNodeId = parent[CommonUIConstants.COLUMN_INDEX_1];
      PidcTreeNode parentNodeElement = this.treeNodeHandler.getNodeIdNodeMap().get(parentNodeId);

      // Other version
      if (CommonUIConstants.NODE_TYPE_OTHER_VERSIONS.equals(parent[CommonUIConstants.COLUMN_INDEX_0])) {
        return this.treeNodeHandler.getOtherPidcVerNodes(parentNodeElement).toArray();
      }
    }


    return new Object[0];
  }

  /**
   * @param parentNode
   * @return
   */
  private Object[] getPidcNodes(final PidcTreeNode parentNode) {
    PidcTreeNodeChildren childNodesAvailblty = this.treeNodeHandler.getPidcNodeChildAvailblty(parentNode);
    List<Object> returnList = new ArrayList<>();

    getPidcActVerNodeChildren(parentNode, childNodesAvailblty, returnList);
    if (isVariantNeeded()) {
      returnList.addAll(this.treeNodeHandler.getPidcVariantNodes(parentNode));
    }
    return returnList.toArray();
  }

  /**
   * @param parentNode
   * @param childNodesAvailblty
   * @param returnList
   */
  private void getPidcActVerNodeChildren(final PidcTreeNode parentNode, final PidcTreeNodeChildren childNodesAvailblty,
      final List<Object> returnList) {
    if (childNodesAvailblty.isOtherPidcVerPresent() &&
        parentNode.getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
      returnList.add(createOtherVerTitleNode(parentNode));
    }

  }

  /**
   * @param parentNode parent pidc version node
   * @return return other version title node
   */
  public PidcTreeNode createOtherVerTitleNode(final PidcTreeNode parentNode) {
    PidcTreeNode othrVerTitleNode = new PidcTreeNode();
    othrVerTitleNode.setParentNodeId(parentNode.getNodeId());
    othrVerTitleNode.setPidcVersion(parentNode.getPidcVersion());
    othrVerTitleNode.setName(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType());
    othrVerTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE);
    othrVerTitleNode.setNodeId(parentNode.getNodeId() + PidcTreeNodeHandler.LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType() + parentNode.getPidcVersion().getId());
    if (null == this.treeNodeHandler.getNodeIdNodeMap().get(othrVerTitleNode.getNodeId())) {
      this.treeNodeHandler.getNodeIdNodeMap().put(othrVerTitleNode.getNodeId(), othrVerTitleNode);
    }
    return othrVerTitleNode;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    if (element instanceof PidcTreeNode) {
      // getting parent for pidc node
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
        return pidcNode.getParentNode();
      }
      return this.treeNodeHandler.getNodeIdNodeMap().get(pidcNode.getParentNodeId());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) { // Check if node has child nodes
    if (element instanceof PidcTreeNode) {
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      if (pidcNode.getLevel() == this.pidcStructMaxLvl) {
        // if the parent node level is the maximum structure attribute level, check for the availability of pidc version
        // nodes
        return (pidcNode.getLevel() == this.pidcStructMaxLvl) && (null != pidcNode.getChildNodesMap()) &&
            !pidcNode.getChildNodesMap().isEmpty();
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE)) {
        return true;
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        // for other pidc versions
        if (!isVariantNeeded()) {
          return false;
        }
        boolean varsAvailable = !this.treeNodeHandler.getPidcVariantNodes((PidcTreeNode) element).isEmpty();
        return varsAvailable;
      }
      else if (pidcNode.getLevel() == (this.pidcStructMaxLvl + 1)) {
        // for active version
        // if the parent node level is pidc version level, render the other pidc version nodes
        PidcTreeNodeChildren childNodesAvailblty =
            this.treeNodeHandler.getPidcNodeChildAvailblty((PidcTreeNode) element);
        boolean otherVersionsAvailable = childNodesAvailblty.isOtherPidcVerPresent() &&
            pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION);

        if (!isVariantNeeded()) {
          return otherVersionsAvailable;
        }
        boolean varsAvailable = !this.treeNodeHandler.getPidcVariantNodes((PidcTreeNode) element).isEmpty();
        return varsAvailable || otherVersionsAvailable;
      }

      return (pidcNode.getLevel() < this.pidcStructMaxLvl) &&
          !this.treeNodeHandler.getParentIdChildNodesMap().get(pidcNode.getNodeId()).isEmpty();
    }
    return element instanceof String[];
  }


  /**
   * @return the hexFileSelectionDialog
   */
  private HexFileSelectionDialog getHexFileSelectionDialog() {
    return this.hexFileSelectionDialog;
  }


  /**
   * @param hexFileSelectionDialog the hexFileSelectionDialog to set
   */
  public void setHexFileSelectionDialog(final HexFileSelectionDialog hexFileSelectionDialog) {
    this.hexFileSelectionDialog = hexFileSelectionDialog;
  }


}
