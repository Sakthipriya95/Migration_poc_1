/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.util.OutLineViewNodeImageUtil;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.ui.providers.AbstractLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;

/**
 * Label provider for OutlineTreeView
 *
 * @author dmo5cob
 */
public class OutlinePIDCTreeViewLabelProvider extends AbstractLabelProvider {

  private final OutLineViewNodeImageUtil useCaseImageUtil = new OutLineViewNodeImageUtil();

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    if (element instanceof AttrRootNode) {
      // It is AttrRootNode
      return ((AttrRootNode) element).getName();
    }
    else if (element instanceof AttrSuperGroup) {
      // It is AttrSuperGroup node
      return ((AttrSuperGroup) element).getName();

    }
    else if (element instanceof AttrGroup) {
      // It is AttrGroup node
      return ((AttrGroup) element).getName();

    }
    else if (element instanceof FavUseCaseItemNode) {
      return ((FavUseCaseItemNode) element).getName();
    }
    else if (element instanceof UseCaseRootNode) {
      return ((UseCaseRootNode) element).getName();
    }
    else if (element instanceof ProjFavUcRootNode) {
      return ((ProjFavUcRootNode) element).getName();
    }
    else if (element instanceof UserFavUcRootNode) {
      return ((UserFavUcRootNode) element).getName();
    }
    return super.getText(element);
  }

  /**
   * This method prepares the text and image for the Outline tree nodes
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    Object element = cell.getElement();
    // Create a styled string
    StyledString cellText = new StyledString();
    Image nodeImage;
    /* Attr Super groups & Grsoups */
    if (element instanceof AttrRootNode) {
      // It is AttrRootNode
      cellText.append(((AttrRootNode) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    else if (element instanceof AttrSuperGroup) {
      // It is AttrSuperGroup node
      cellText.append(((AttrSuperGroup) element).getName());
      // ICDM-931
      nodeImage = this.useCaseImageUtil.getLinkOverLayedImage(ImageKeys.SUP_GRP_28X30, ImageKeys.LINK_DECORATOR_12X12,
          (IModel) element);
    }
    else if (element instanceof AttrGroup) {
      // It is AttrGroup node
      cellText.append(((AttrGroup) element).getName());
      // ICDM-931
      nodeImage = this.useCaseImageUtil.getLinkOverLayedImage(ImageKeys.GROUP_GREEN_28X30,
          ImageKeys.LINK_DECORATOR_12X12, (IModel) element);
    }
    else {
      // Use case module types
      nodeImage = getUcItemNodeStyle(element, cellText, cell);
    }
    // set text
    cell.setText(cellText.toString());
    // set the image
    cell.setImage(nodeImage);
    super.update(cell);
  }

  /**
   * Find the style for use case item nodes
   *
   * @param element node element
   * @param cellText cell text object to which name is to be set
   * @param cell
   * @return image for the node
   */
  private Image getUcItemNodeStyle(final Object element, final StyledString cellText, final ViewerCell cell) {
    Image nodeImage = null;
    if (element instanceof UseCaseRootNode) {
      cellText.append(((UseCaseRootNode) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    else if (element instanceof UseCaseGroupClientBO) {
      cellText.append(((UseCaseGroupClientBO) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_28X30);
    }
    else if (element instanceof UsecaseClientBO) {
      UsecaseClientBO obj = (UsecaseClientBO) element;
      cellText.append(obj.getName());
      if (obj.isUpToDate()) {
        // // ICDM-931
        nodeImage = this.useCaseImageUtil.getLinkOverLayedImage(ImageKeys.UC_28X30, ImageKeys.LINK_DECORATOR_12X12,
            (IModel) element);
      }
      else {
        nodeImage = this.useCaseImageUtil.getOutdatedOverLayedImage(ImageKeys.UC_28X30, ImageKeys.OUTDATED, element);
      }
    }
    else if (element instanceof UseCaseSectionClientBO) {
      cellText.append(((UseCaseSectionClientBO) element).getName());
      // ICDM-931
      nodeImage = this.useCaseImageUtil.getLinkOverLayedImage(ImageKeys.UCS_28X30, ImageKeys.LINK_DECORATOR_12X12,
          (IModel) element);
    }
    else if (element instanceof ProjFavUcRootNode) {
      cellText.append(((ProjFavUcRootNode) element).getName());
      // ICDM-1030
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    else if (element instanceof UserFavUcRootNode) {
      cellText.append(((UserFavUcRootNode) element).getName());
      // ICDM-1028
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
    }
    else if (element instanceof FavUseCaseItemNode) {
      nodeImage = getFavUcItemNodeImage(element, cellText, cell);
    }
    return nodeImage;
  }

  /**
   * Find image for FavUcItemNode. The image corresponding to the underlying use case item is returned
   *
   * @param element Object
   * @param cellText StyledString
   * @param cell
   * @return node image
   */
  private Image getFavUcItemNodeImage(final Object element, final StyledString cellText, final ViewerCell cell) {
    FavUseCaseItemNode favUcItemNode = (FavUseCaseItemNode) element;
    cellText.append(favUcItemNode.getName());
    Image returnImg = null;
    // ICDM-1030
    if (favUcItemNode.getUseCaseItem() instanceof UseCaseGroupClientBO) {
      if (favUcItemNode.getUseCaseItem().isDeleted()) {
        returnImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      // ICDM-1091
      else if (CommonUtils.isNull(favUcItemNode.getFavUcItem())) {
        returnImg = this.useCaseImageUtil.getVirtualLinkOverLayedImage(ImageKeys.UC_GRP_28X30, favUcItemNode);
      }
      else {
        returnImg = this.useCaseImageUtil.getFavLinkOverLayedImage(ImageKeys.UC_GRP_28X30, favUcItemNode);
      }
    }
    else if (favUcItemNode.getUseCaseItem() instanceof UsecaseClientBO) {
      if (favUcItemNode.getUseCaseItem().isDeleted()) {
        returnImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      // ICDM-1091
      else if (CommonUtils.isNull(favUcItemNode.getFavUcItem())) {
        returnImg = this.useCaseImageUtil.getVirtualLinkOverLayedImage(ImageKeys.UC_28X30, favUcItemNode);
      }
      else {
        returnImg = this.useCaseImageUtil.getFavLinkOverLayedImage(ImageKeys.UC_28X30, favUcItemNode);
      }
    }
    else if (favUcItemNode.getUseCaseItem() instanceof UseCaseSectionClientBO) {
      if (favUcItemNode.getUseCaseItem().isDeleted()) {
        returnImg = ImageManager.getInstance().getRegisteredImage(ImageKeys.UCS_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      // ICDM-1091
      else if (CommonUtils.isNull(favUcItemNode.getFavUcItem())) {
        returnImg = this.useCaseImageUtil.getVirtualLinkOverLayedImage(ImageKeys.UCS_28X30, favUcItemNode);
      }
      else {
        returnImg = this.useCaseImageUtil.getFavLinkOverLayedImage(ImageKeys.UCS_28X30, favUcItemNode);
      }
    }
    return returnImg;
  }

  /**
   * Tool tip text for each node in the tree.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    if (element instanceof AttrRootNode) {
      tooltip = ((AttrRootNode) element).getToolTip();
    }
    else if (element instanceof UseCaseRootNode) {
      tooltip = ((UseCaseRootNode) element).getToolTip();
    }
    else if (element instanceof ProjFavUcRootNode) {
      tooltip = ((ProjFavUcRootNode) element).getToolTip();
    }
    else if (element instanceof UserFavUcRootNode) {
      tooltip = ((UserFavUcRootNode) element).getToolTip();
    }
    else if (element instanceof AttrSuperGroup) {
      tooltip = ((AttrSuperGroup) element).getDescription();
    }
    else if (element instanceof AttrGroup) {
      tooltip = ((AttrGroup) element).getDescription();
    }
    else if (element instanceof IUseCaseItemClientBO) {
      tooltip = ((IUseCaseItemClientBO) element).getToolTip();
    }
    else if (element instanceof FavUseCaseItemNode) {
      tooltip = ((FavUseCaseItemNode) element).getToolTip();
    }
    return tooltip;
  }
}
