/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;


/**
 * New Content Provider for getting Values for PIDC tree
 *
 * @author rgo7cob
 */
public class ReviewDetailsContentProvider implements ITreeContentProvider {


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

    return getChildren(inputElement);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof Collection) {
      List<ReviewDetail> detailsList = new ArrayList<>();
      Collection<ReviewDetail> details = (Collection<ReviewDetail>) parentElement;
      for (ReviewDetail detailObj : details) {
        if (!detailObj.isHasParent()) {
          detailsList.add(detailObj);
        }
      }
      return detailsList.toArray();
    }
    else if (parentElement instanceof ReviewDetail) {
      ReviewDetail detail = (ReviewDetail) parentElement;
      return detail.getChildDetails().toArray();
    }
    return new Object[0];
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    if (element instanceof ReviewDetail) {
      ReviewDetail detail = (ReviewDetail) element;
      return detail.hasChildRevDetails() && !detail.isHasParent();
    }
    return false;
  }

}
