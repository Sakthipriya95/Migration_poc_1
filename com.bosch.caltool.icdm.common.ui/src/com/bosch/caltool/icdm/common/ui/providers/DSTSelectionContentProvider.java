/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;


/**
 * @author jvi6cob
 */
public class DSTSelectionContentProvider implements ITreeContentProvider {

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
    List<VCDMApplicationProject> aprjs = (List<VCDMApplicationProject>) inputElement;
    return aprjs.toArray();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {

    if (parentElement instanceof VCDMApplicationProject) {
      VCDMApplicationProject applicationProject = (VCDMApplicationProject) parentElement;
      return applicationProject.getVcdmVariants().values().toArray();
    }
    else if (parentElement instanceof VCDMProductKey) {
      VCDMProductKey vcdmVariant = (VCDMProductKey) parentElement;
      return vcdmVariant.getProgramKeys().values().toArray();
    }
    else if (parentElement instanceof VCDMProgramkey) {
      VCDMProgramkey vcdmProgramkey = (VCDMProgramkey) parentElement;
      return vcdmProgramkey.getvCDMDSTRevisions().values().toArray();
    }
    return new Object[] {};
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    // Not applicable
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    if (element instanceof VCDMApplicationProject) {
      VCDMApplicationProject applicationProject = (VCDMApplicationProject) element;
      return !applicationProject.getVcdmVariants().values().isEmpty();
    }
    else if (element instanceof VCDMProductKey) {
      VCDMProductKey vcdmVariant = (VCDMProductKey) element;
      return !vcdmVariant.getProgramKeys().values().isEmpty();
    }
    else if (element instanceof VCDMProgramkey) {
      VCDMProgramkey vcdmProgramkey = (VCDMProgramkey) element;
      return !vcdmProgramkey.getvCDMDSTRevisions().values().isEmpty();
    }
    return false;
  }

}