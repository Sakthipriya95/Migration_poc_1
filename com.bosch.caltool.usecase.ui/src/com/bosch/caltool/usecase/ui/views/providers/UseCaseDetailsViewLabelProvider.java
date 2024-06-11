/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * Label provider for Use Case Details view.
 *
 * @author adn1cob
 */
public class UseCaseDetailsViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {


  /**
   * This method prepares the text and image for the UseCase details tree nodes
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    final StyledString useCaseText = new StyledString();
    Image nodeImage = null;
    if (element instanceof UsecaseClientBO) {
      // It is Usecase node
      final UsecaseClientBO obj = (UsecaseClientBO) element;
      useCaseText.append(obj.getName());
      // ICDM-358
      nodeImage = getNodeImageForUseCase(cell, element, nodeImage, obj);
    }
    else if (element instanceof UseCaseSectionClientBO) {
      // It is Usecase section node
      useCaseText.append(((UseCaseSectionClientBO) element).getName());
      // ICDM-358
      // red colour image if deleted
      if (((UseCaseSectionClientBO) element).isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UCS_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UCS_28X30);
        cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
            .getSystemColor(SWT.COLOR_BLACK));
      }
    }
    // set cell image
    cell.setImage(nodeImage);
    // set cell text
    cell.setText(useCaseText.toString());
    // set cell style
    cell.setStyleRanges(useCaseText.getStyleRanges());
    super.update(cell);
  }

  /**
   * @param cell
   * @param element
   * @param nodeImage
   * @param obj
   * @return
   */
  private Image getNodeImageForUseCase(final ViewerCell cell, final Object element, Image nodeImage,
      final UsecaseClientBO obj) {
    // red colour image if deleted
    if (((UsecaseClientBO) element).isDeleted()) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_DEL_28X30);
      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    }
    else {

      if (obj.isUpToDate()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_28X30);
      }
      else {
        nodeImage =
            CommonUiUtils.getInstance().getOutdatedOverLayedImage(ImageKeys.UC_28X30, ImageKeys.OUTDATED, element);
      }


      cell.setForeground(
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
    }
    return nodeImage;
  }

  /**
   * This Overridden method is mandatory implementation of ILabelProvider
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  /**
   * Get the display text from use case item
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String text = null;
    if (element instanceof IUseCaseItemClientBO) {
      text = ((IUseCaseItemClientBO) element).getName();
    }

    return text;
  }

  // iCDM-350
  /**
   * Get the tooltip from use case item
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
