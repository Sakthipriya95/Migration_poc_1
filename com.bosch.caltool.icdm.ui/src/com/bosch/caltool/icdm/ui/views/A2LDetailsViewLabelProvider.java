/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author bru2cob
 */
public class A2LDetailsViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  A2LDetailsStructureModel detailsStrucModel;

  /**
   * @param detailsStrucModel
   */
  public A2LDetailsViewLabelProvider(final A2LDetailsStructureModel detailsStrucModel) {
    this.detailsStrucModel = detailsStrucModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    if (element instanceof String) {
      return "Variants in this node use the Workpackage/Responsibility settings defined for the whole PIDC";
    }
    return super.getToolTipText(element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object arg0, final String arg1) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    if (element instanceof A2lWpDefnVersion) {
      A2lWpDefnVersion defVer = (A2lWpDefnVersion) element;
      return defVer.getName();
    }
    else if (element instanceof A2lVariantGroup) {
      A2lVariantGroup varGrp = (A2lVariantGroup) element;
      return varGrp.getName();
    }
    else if (element instanceof PidcVariant) {
      PidcVariant var = (PidcVariant) element;
      return var.getName();
    }
    else if (element instanceof String) {
      return ApicUiConstants.STRUCTURE_VIEW_DEFAULT_NODE;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    final Object element = cell.getElement();
    // Create a styled string
    final StyledString cellText = new StyledString();
    Image nodeImage = null;
    // Set text and image based on the element type
    if (element instanceof A2lVariantGroup) {
      A2lVariantGroup varGrp = (A2lVariantGroup) element;
      cellText.append(varGrp.getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
      cell.setImage(nodeImage);
    }
    else if (element instanceof PidcVariant) {
      PidcVariant var = (PidcVariant) element;
      cellText.append(var.getName());
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_28X30);
      cell.setImage(nodeImage);
    }
    else if ((element instanceof String) && ((String) element).equals(ApicUiConstants.STRUCTURE_VIEW_DEFAULT_NODE)) { // Level
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMON_28X30);
      cell.setImage(nodeImage); // nodes
      cellText.append(ApicUiConstants.STRUCTURE_VIEW_DEFAULT_NODE);
    }
    cell.setText(cellText.toString());
    super.update(cell);
  }

}
