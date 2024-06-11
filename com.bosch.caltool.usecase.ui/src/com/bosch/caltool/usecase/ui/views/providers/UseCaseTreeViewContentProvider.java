/**
 *
 */
package com.bosch.caltool.usecase.ui.views.providers;

import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;


/**
 * Content provider for ProjectID Card tree
 *
 * @author adn1cob
 */
public class UseCaseTreeViewContentProvider implements ITreeContentProvider {

  /**
   * UseCaseRootNode
   */
  private final UseCaseRootNode useCaseRootNode;


  /**
   * UseCaseTreeViewContentProvider - Constructor
   *
   * @param useCaseRootNode UseCaseRootNode
   */
  public UseCaseTreeViewContentProvider(final UseCaseRootNode useCaseRootNode) {
    this.useCaseRootNode = useCaseRootNode;
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
    // String object is the inidication for displaying root node
    if (inputElement instanceof String) {
      return new Object[] { this.useCaseRootNode };
    }
    if (inputElement instanceof UseCaseRootNode) {
      Set<UseCaseGroupClientBO> rootGroups = ((UseCaseRootNode) inputElement).getUseCaseGroups(true);
      return rootGroups.toArray();
    }
    if (inputElement instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO useCaseGroupClientBO = (UseCaseGroupClientBO) inputElement;
      final SortedSet<UseCaseGroupClientBO> subGroupSet = useCaseGroupClientBO.getChildGroupSet(true);
      if (!subGroupSet.isEmpty()) {
        return subGroupSet.toArray();
      }
      return useCaseGroupClientBO.getUseCaseSet(true).toArray();
    }
    return new Object[] {};
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof UseCaseRootNode) {
      Set<UseCaseGroupClientBO> rootGroups = ((UseCaseRootNode) parentElement).getUseCaseGroups(true);
      return rootGroups.toArray();
    }
    if (parentElement instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO useCaseGroupClientBO = (UseCaseGroupClientBO) parentElement;
      final SortedSet<UseCaseGroupClientBO> subGroupSet = useCaseGroupClientBO.getChildGroupSet(true);
      if (!subGroupSet.isEmpty()) {
        return subGroupSet.toArray();
      }
      return useCaseGroupClientBO.getUseCaseSet(true).toArray();
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

    if (element instanceof UseCaseRootNode) {
      return true;
    }

    if (element instanceof UseCaseGroupClientBO) {
      if (!((UseCaseGroupClientBO) element).getChildGroupSet(true).isEmpty()) {
        return true;
      }
      return !((UseCaseGroupClientBO) element).getUseCaseSet(true).isEmpty();
    }

    return false;
  }
}
