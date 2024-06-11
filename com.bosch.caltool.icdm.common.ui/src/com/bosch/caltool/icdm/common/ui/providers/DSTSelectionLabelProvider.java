/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;


/**
 * Label Provider for DST Selection
 *
 * @author jvi6cob
 */
public class DSTSelectionLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  /**
   * This method prepares the text and image for the tree nodes in DSTSelectionDialog
   */
  @Override
  public void update(final ViewerCell cell) {
    // Get the element
    Object element = cell.getElement();
    // Create a styled string
    StyledString cellText = new StyledString();
    if (element instanceof VCDMApplicationProject) {
      // when the element is VCDMApplicationProject
      cellText.append(((VCDMApplicationProject) element).getAprjName());
    }
    else if (element instanceof VCDMProductKey) {
      // when the element is VCDMProductKey
      cellText.append(((VCDMProductKey) element).getVariantName());
    }
    else if (element instanceof VCDMProgramkey) {
      // when the element is VCDMProgramkey
      cellText.append(((VCDMProgramkey) element).getProgramKeyName());
    }
    else if (element instanceof VCDMDSTRevision) {
      // when the element is VCDMDSTRevision
      VCDMDSTRevision vcdmdstRevision = (VCDMDSTRevision) element;
      String displayContent =
          vcdmdstRevision.getRevisionNo().toString() + " : " + vcdmdstRevision.getCreatedDate().toString();
      cellText.append(displayContent);
    }

    // set text
    cell.setText(cellText.toString());
    super.update(cell);

  }


  /**
   * {@inheritDoc}
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
    if (element instanceof VCDMApplicationProject) {
      // when the element is VCDMApplicationProject
      return ((VCDMApplicationProject) element).getAprjName();
    }
    else if (element instanceof VCDMProductKey) {
      // when the element is VCDMProductKey
      return ((VCDMProductKey) element).getVariantName();
    }
    else if (element instanceof VCDMProgramkey) {
      // when the element is VCDMProgramkey
      return ((VCDMProgramkey) element).getProgramKeyName();
    }
    else if (element instanceof VCDMDSTRevision) {
      // when the element is VCDMDSTRevision
      return ((VCDMDSTRevision) element).getRevisionNo().toString();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    if (element instanceof VCDMApplicationProject) {
      // when the element is VCDMApplicationProject
      return ((VCDMApplicationProject) element).getAprjName();
    }
    else if (element instanceof VCDMProductKey) {
      // when the element is VCDMProductKey
      return ((VCDMProductKey) element).getVariantName();
    }
    else if (element instanceof VCDMProgramkey) {
      // when the element is VCDMProgramkey
      return ((VCDMProgramkey) element).getProgramKeyName();
    }
    else if (element instanceof VCDMDSTRevision) {
      // when the element is VCDMDSTRevision
      return ((VCDMDSTRevision) element).getRevisionNo().toString();
    }
    return null;
  }


}
