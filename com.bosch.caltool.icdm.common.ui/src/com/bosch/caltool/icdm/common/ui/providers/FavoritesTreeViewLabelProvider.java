/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;

/**
 * Label provider for FavoritesTreeView
 *
 * @author adn1cob
 */
public class FavoritesTreeViewLabelProvider extends AbstractLabelProvider {

  private final PidTreeTooltipUtil tooltipUtil = new PidTreeTooltipUtil();

  private final Styler styler;

  private final Font italicFont;


  /**
   * @param treeViewer
   */
  public FavoritesTreeViewLabelProvider(final TreeViewer treeViewer) {
    super();
    FontData[] italicFontData = getModifiedFontData(treeViewer.getTree().getFont().getFontData(), SWT.ITALIC);
    this.italicFont = new Font(Display.getCurrent(), italicFontData);
    // create a styler for italic font
    this.styler = new Styler() {

      @Override
      public void applyStyles(final TextStyle textStyle) {
        textStyle.font = FavoritesTreeViewLabelProvider.this.italicFont;
      }
    };
  }

  /**
   * Create italic font for tree
   *
   * @param originalData
   * @param additionalStyle
   * @return
   */
  private static FontData[] getModifiedFontData(final FontData[] originalData, final int additionalStyle) {
    FontData[] styleData = new FontData[originalData.length];
    for (int index = 0; index < styleData.length; index++) {
      FontData base = originalData[index];
      styleData[index] = new FontData(base.getName(), base.getHeight(), base.getStyle() | additionalStyle);
    }
    return styleData;
  }


  /**
   * This method prepares the text and image for the PID tree nodes
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();

    StyledString nodeTxt = null;

    // Set text and image based on the element type

    Image nodeImg = null;
    if (element instanceof PidcTreeNode) {
      PidcTreeNode pidcNode = (PidcTreeNode) element;
      nodeTxt = getNodeTxt(pidcNode);
      // Root node
      if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE) &&
          (pidcNode.getLevel() == ApicConstants.PIDC_ROOT_LEVEL)) {
        nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_ROOT_28X30);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE) &&
          (pidcNode.getLevel() > ApicConstants.PIDC_ROOT_LEVEL)) {
        nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_NODE_28X30);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
          pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        nodeImg = setForeGroundAndImageForPIDC(pidcNode);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.PVER_16X16);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        nodeImg = setForegroundAndImageForA2L(cell, pidcNode);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_NODE)) {
        nodeImg = CommonUiUtils.getInstance().getImageForCDRResult(pidcNode);
      }
      else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE) ||
          pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE) ||
          pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.CDR_VAR_NODE) ||
          pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE)) {
        nodeImg = setForeGroundAndImageForVariant(cell, pidcNode);
      }
      else {
        nodeImg = checkAndAddNodeImages(cell, nodeImg, pidcNode);
      }
      checkNodesForInactiveA2l(pidcNode, cell);
    }
    // Set image
    cell.setImage(nodeImg);
    setNodeTextAndStyleToCell(cell, nodeTxt);
    super.update(cell);
  }

  /**
   * @param cell
   * @param nodeImg
   * @param pidcNode
   * @return
   */
  private Image checkAndAddNodeImages(final ViewerCell cell, Image nodeImg, final PidcTreeNode pidcNode) {
    if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE)) {
      Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = pidcNode.getQnaireInfo().getRvwQnaireRespVersMap();
      nodeImg = setForeGroundAndImageForQnaireRespNode(cell, pidcNode, rvwQnaireRespVersMap);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE)) {
      Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = pidcNode.getA2lStructureModel().getRvwQnaireRespVersMap();
      nodeImg = setForeGroundAndImageForQnaireRespNode(cell, pidcNode, rvwQnaireRespVersMap);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE)) {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.OTHER_VERSIONS_16X16);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE)) {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUESTIONARE_ICON_16X16);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW)) {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.RELEASE_28X30);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      nodeImg = getWPNodeImage(pidcNode);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)) {
      nodeImg = getResponsibilityNodeImage(pidcNode);
    }
    else if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE) ||
        pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE)) {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    return nodeImg;
  }

  /**
   * @param pidcNode
   * @return
   */
  private Image getWPNodeImage(final PidcTreeNode pidcNode) {
    Image nodeImg;
    if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) &&
        isWpFinished(pidcNode, pidcNode.getPidcVariant().getId(), pidcNode.getA2lResponsibility().getId(),
            pidcNode.getA2lWorkpackage().getId())) {
      nodeImg = setOverlayImageForWPNode();
    }
    else {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_28X30);
    }
    return nodeImg;
  }

  /**
   * @param pidcNode
   * @return
   */
  private Image getResponsibilityNodeImage(final PidcTreeNode pidcNode) {
    Image nodeImg;
    if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE) &&
        isA2lRespFinished(pidcNode, pidcNode.getPidcVariant().getId(), pidcNode.getA2lResponsibility().getId())) {
      nodeImg = setOverlayImageForRespNode();
    }
    else {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE);
    }
    return nodeImg;
  }

  /**
   * @param pidcNode
   * @return boolean
   */
  private boolean isWpFinished(final PidcTreeNode pidcNode, final Long pidcVariantId, final Long a2lRespId,
      final Long a2lWpId) {

    Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap = pidcNode.getA2lStructureModel().getWpRespStatusMap();
    Map<Long, Map<Long, String>> pidcVarWpRespStatus = wpRespStatusMap.get(pidcVariantId);

    return wpRespStatusMap.containsKey(pidcVariantId) && pidcVarWpRespStatus.containsKey(a2lRespId) &&
        pidcVarWpRespStatus.get(a2lRespId).containsKey(a2lWpId) &&
        isWPComplete(pidcVarWpRespStatus.get(a2lRespId).get(a2lWpId));
  }

  /**
   * @param pidcNode
   * @param a2lRespId
   * @param pidcVariantId
   * @return boolean
   */
  private boolean isA2lRespFinished(final PidcTreeNode pidcNode, final Long pidcVariantId, final Long a2lRespId) {

    Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap = pidcNode.getA2lStructureModel().getWpRespStatusMap();
    Map<Long, Map<Long, String>> pidcVarWpRespStatusMap = wpRespStatusMap.get(pidcVariantId);

    return wpRespStatusMap.containsKey(pidcVariantId) && pidcVarWpRespStatusMap.containsKey(a2lRespId) &&
        isRespComplete(pidcVarWpRespStatusMap.get(a2lRespId));
  }

  /**
   * @param a2lWPRespModel
   * @return boolean
   */
  private boolean isWPComplete(final String wpFinStatus) {

    return CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), wpFinStatus);
  }

  /**
   * @param a2lWpRespStatusMap
   * @return boolean
   */
  private boolean isRespComplete(final Map<Long, String> a2lWpRespStatusMap) {

    for (Entry<Long, String> wpRespStatusEntry : a2lWpRespStatusMap.entrySet()) {
      if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), wpRespStatusEntry.getValue())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param cell
   * @param pidcNode
   * @param rvwQnaireRespVersMap
   * @return
   */
  private Image setOverlayImageForRespNode() {
    Image nodeImg;

    ImageKeys[] overlayKeys = { null, null, null, null };

    overlayKeys[3] = ImageKeys.ALL_8X8;

    nodeImg = ImageManager.getMultipleDecoratedImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE, overlayKeys);
    return nodeImg;
  }

  /**
   * @param cell
   * @param pidcNode
   * @param rvwQnaireRespVersMap
   * @return
   */
  private Image setOverlayImageForWPNode() {
    Image nodeImg;

    ImageKeys[] overlayKeys = { null, null, null, null };

    overlayKeys[3] = ImageKeys.ALL_8X8;

    nodeImg = ImageManager.getMultipleDecoratedImage(ImageKeys.WP_28X30, overlayKeys);
    return nodeImg;
  }

  /**
   * @param cell
   * @param nodeTxt
   */
  private void setNodeTextAndStyleToCell(final ViewerCell cell, final StyledString nodeTxt) {
    if (nodeTxt != null) {
      // set cell text
      cell.setText(nodeTxt.toString());
      // set cell style
      cell.setStyleRanges(nodeTxt.getStyleRanges());

    }
  }

  /**
   * @param pidcNode
   * @return
   */
  private StyledString getNodeTxt(final PidcTreeNode pidcNode) {
    StyledString nodeTxt;
    if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L) && pidcNode.getPidcA2l().isWorkingSetModified()) {
      nodeTxt = new StyledString(pidcNode.getName(), this.styler);
    }
    else {
      nodeTxt = new StyledString();
      if (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
        nodeTxt.append(pidcNode.getPidc().getName());
        nodeTxt.append(" (" + pidcNode.getPidcVersion().getVersionName() + ") ", StyledString.COUNTER_STYLER);
      }
      else {
        nodeTxt.append(pidcNode.getName());
      }
    }
    return nodeTxt;
  }

  /**
   * @param pidcNode
   * @return
   */
  private Image setForeGroundAndImageForPIDC(final PidcTreeNode pidcNode) {
    Image nodeImg;
    if (pidcNode.getPidc().isDeleted()) {
      nodeImg = ImageManager.getDecoratedImage(ImageKeys.PIDC_16X16, ImageKeys.PIDC_DEL_8X8, IDecoration.BOTTOM_RIGHT);
    }
    else {
      nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16);
    }
    return nodeImg;
  }

  /**
   * @param cell
   * @param pidcNode
   * @param rvwQnaireRespVersMap
   * @return
   */
  private Image setForeGroundAndImageForQnaireRespNode(final ViewerCell cell, final PidcTreeNode pidcNode,
      final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap) {
    Image nodeImg;
    if (pidcNode.getQnaireResp().isDeletedFlag()) {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {
      cell.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
    }
    ImageKeys[] overlayKeys = { null, null, null, null };

    if (isLinkedQnaireResponse(pidcNode, pidcNode.getQnaireResp().getVariantId(),
        pidcNode.getQnaireResp().getA2lRespId(), pidcNode.getQnaireResp().getA2lWpId())) {
      overlayKeys[0] = ImageKeys.LINK_VAR_MARK_8X8;
    }
    RvwQnaireRespVersion rvwQnaireRespVersion = rvwQnaireRespVersMap.get(pidcNode.getQnaireResp().getId());
    if (CommonUtils.isNotNull(rvwQnaireRespVersion)) {
      String rvwQnaireRespersStatus = rvwQnaireRespVersion.getQnaireRespVersStatus();
      if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(rvwQnaireRespersStatus)) {
        overlayKeys[3] = ImageKeys.ALL_8X8;
      }
      else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(rvwQnaireRespersStatus) ||
          CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(rvwQnaireRespersStatus)) {
        overlayKeys[3] = ImageKeys.ALL_YELLOW_8X8;
      }
    }
    nodeImg = ImageManager.getMultipleDecoratedImage(ImageKeys.QUESTIONARE_ICON_16X16, overlayKeys);
    return nodeImg;
  }

  private boolean isLinkedQnaireResponse(final PidcTreeNode pidcNode, final Long qnaireRespVarId,
      final Long qnaireRespResponsibilityId, final Long qnaireRespWpId) {
    return CommonUtils.isNotEqual(pidcNode.getPidcVariant().getId(), qnaireRespVarId) ||
        CommonUtils.isNotEqual(pidcNode.getParentNode().getA2lResponsibility().getId(), qnaireRespResponsibilityId) ||
        CommonUtils.isNotEqual(pidcNode.getParentNode().getA2lWorkpackage().getId(), qnaireRespWpId);
  }


  /**
   * @param cell
   * @param pidcNode
   * @return
   */
  private Image setForeGroundAndImageForVariant(final ViewerCell cell, final PidcTreeNode pidcNode) {
    Image nodeImg;
    if (pidcNode.getPidcVariant().isDeleted()) {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {
      cell.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
    }
    nodeImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_28X30);
    return nodeImg;
  }

  /**
   * @param cell
   * @param pidcNode
   * @return
   */
  private Image setForegroundAndImageForA2L(final ViewerCell cell, final PidcTreeNode pidcNode) {
    Image nodeImg;
    PidcA2l pidcA2l = pidcNode.getPidcA2l();

    if (!pidcA2l.isActive()) {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
    }
    else if (pidcA2l.isWorkingSetModified()) {
      cell.setForeground(new Color(Display.getCurrent(), 139, 69, 19));
    }
    else {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }
    nodeImg = CommonUiUtils.getInstance().getImageForA2lFile(pidcA2l);
    return nodeImg;
  }

  /**
   * {@inheritDoc} returns tooltip text
   */
  @Override
  public String getToolTipText(final Object element) {
    StringBuilder toolTipText = new StringBuilder();
    if (element instanceof PidcTreeNode) {
      PidcTreeNode treeNode = (PidcTreeNode) element;
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
          treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
        this.tooltipUtil.getPidcVerToolTip(toolTipText, treeNode);
      }
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        this.tooltipUtil.getSdomPverToolTip(toolTipText, treeNode);
      }
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        this.tooltipUtil.getPidcA2lToolTip(toolTipText, treeNode);
      }
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.REV_RES_NODE)) {
        this.tooltipUtil.getRevResToolTip(toolTipText, treeNode);
      }
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE)) {
        this.tooltipUtil.getQnaireVarToolTip(toolTipText, treeNode);
      }
      if (treeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE)) {
        this.tooltipUtil.getQnaireRespToolTip(toolTipText, treeNode);
      }
    }
    String retStringElememt = toolTipText.toString().trim();
    return !retStringElememt.isEmpty() ? retStringElememt.replace("&", "&&") : null;
  }

  private void checkNodesForInactiveA2l(final PidcTreeNode pidcNode, final ViewerCell cell) {
    if ((pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE) ||
        (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) ||
            (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
                (pidcNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE))))) &&
        (CommonUtils.isNotNull(pidcNode.getPidcA2l()) && (!pidcNode.getPidcA2l().isActive()))) {
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
    }
  }

}
