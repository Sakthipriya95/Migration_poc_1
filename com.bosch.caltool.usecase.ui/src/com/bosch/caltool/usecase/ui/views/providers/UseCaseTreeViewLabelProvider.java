/**
 *
 */
package com.bosch.caltool.usecase.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * This class provides the labels for UseCase tree viewer
 *
 * @author adn1cob
 */
public class UseCaseTreeViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {


  /**
   * This method prepares the text and image for the Use Case tree nodes
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    Object element = cell.getElement();
    StyledString useCaseText = new StyledString();

    Image nodeImage = null;
    if (element instanceof UseCaseRootNode) {
      useCaseText.append(((UseCaseRootNode) element).getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_ROOT_28X30);
    }
    else if (element instanceof UseCaseGroupClientBO) {
      useCaseText.append(((UseCaseGroupClientBO) element).getName());
      // ICDM-358
      if (((UseCaseGroupClientBO) element).isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_GRP_28X30);
        cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
            .getSystemColor(SWT.COLOR_BLACK));
      }
    }
    else if (element instanceof UsecaseClientBO) {
      UsecaseClientBO obj = (UsecaseClientBO) element;
      useCaseText.append(obj.getName());
      // ICDM-358
      UsecaseClientBO usecase = (UsecaseClientBO) element;
      if (usecase.isDeleted()) {
        nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_DEL_28X30);
        cell.setForeground(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
      }
      else {

        if (usecase.isUpToDate()) {
          nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_28X30);
        }
        else {
          nodeImage =
              CommonUiUtils.getInstance().getOutdatedOverLayedImage(ImageKeys.UC_28X30, ImageKeys.OUTDATED, element);
        }

        cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
            .getSystemColor(SWT.COLOR_BLACK));
      }
    }
    cell.setImage(nodeImage);
    cell.setText(useCaseText.toString());
    cell.setStyleRanges(useCaseText.getStyleRanges());
    super.update(cell);
  }

  /**
   * This Overridden method is mandatory implementation of ILabelProvider
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String text = null;
    if (element instanceof UseCaseRootNode) {
      text = ((UseCaseRootNode) element).getName();
    }
    else if (element instanceof UseCaseGroupClientBO) {
      text = ((UseCaseGroupClientBO) element).getName();
    }
    else if (element instanceof UsecaseClientBO) {
      text = ((UsecaseClientBO) element).getName();
    }

    return text;

  }

  // iCDM-350
  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    if (element instanceof UseCaseRootNode) {
      tooltip = ((UseCaseRootNode) element).getToolTip();
    }
    else if (element instanceof UseCaseGroupClientBO) {
      tooltip = ((UseCaseGroupClientBO) element).getToolTip();
    }
    else if (element instanceof UsecaseClientBO) {

      tooltip = ((UsecaseClientBO) element).getToolTip();

    }

    return tooltip;
  }

}
