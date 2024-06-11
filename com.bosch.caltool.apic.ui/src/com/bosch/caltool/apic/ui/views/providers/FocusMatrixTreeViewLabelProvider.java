/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * This class provides the labels for UseCase tree viewer
 *
 * @author adn1cob
 */
public class FocusMatrixTreeViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {


  /**
   * This method prepares the text and image for the Use Case tree nodes
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    Object element = cell.getElement();
    // get styled string
    StyledString useCaseText = new StyledString();
    Image nodeImage = null;
    if (element instanceof FocusMatrixUseCaseRootNode) {
      useCaseText.append(((FocusMatrixUseCaseRootNode) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_ROOT_28X30);
    }
    // Use case group
    else if (element instanceof UseCaseGroupClientBO) {
      useCaseText.append(((UseCaseGroupClientBO) element).getName());

      if (((UseCaseGroupClientBO) element).isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_28X30);
      }
    } // Use case
    else if (element instanceof UsecaseClientBO) {
      UsecaseClientBO obj = (UsecaseClientBO) element;
      useCaseText.append(obj.getName());
      if (obj.isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else if (obj.isUpToDate()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_28X30);
      }
      else {
        nodeImage =
            CommonUiUtils.getInstance().getOutdatedOverLayedImage(ImageKeys.UC_28X30, ImageKeys.OUTDATED, element);
      }
    } // Use case section
    else if (element instanceof UseCaseSectionClientBO) {
      // It is Usecase section node
      useCaseText.append(((UseCaseSectionClientBO) element).getName());

      if (((UseCaseSectionClientBO) element).isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UCS_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UCS_28X30);
      }
    }
    cell.setImage(nodeImage);
    cell.setText(useCaseText.toString());
    cell.setStyleRanges(useCaseText.getStyleRanges());
    super.update(cell);
  }

  /**
   * This Overridden method is mandatory implementation of ILabelProvider
   * <p>
   * Not used
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    // Not used
    return null;
  }

  /**
   * Get the text to be displayed in each node of the tree. Returns the name of the object as provided by
   * <code>getName()</code> method.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String text = null;

    if (element instanceof FocusMatrixUseCaseRootNode) { // Use case root node
      text = ((FocusMatrixUseCaseRootNode) element).getName();
    }
    else if (element instanceof IUseCaseItemClientBO) { // Use Case item
      text = ((IUseCaseItemClientBO) element).getName();
    }
    return text;

  }

  // iCDM-350
  /**
   * Tool tip text for each node in the tree. Provided by getToolTip() of use case item.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    if (element instanceof IUseCaseItemClientBO) {
      tooltip = ((IUseCaseItemClientBO) element).getToolTip();
    }
    return tooltip;
  }

}
