/**
 *
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;

/**
 * Content provider for ProjectID Card tree
 *
 * @author adn1cob
 */
public class FocusMatrixTreeViewContentProvider implements ITreeContentProvider {


  /**
   * This list will contain the project use cases
   */
  private List<IUseCaseItemClientBO> listOfFavItems;


  /**
   * FocusMatrixUseCaseRootNode
   */
  private final FocusMatrixUseCaseRootNode fmRootNode;


  private final FocusMatrixDataHandler focusMatrixDataHandler;


  /**
   * UseCaseTreeViewContentProvider - Constructor
   *
   * @param list FocusMatrixDataHandler
   * @param fmRootNode2 FocusMatrixUseCaseRootNode
   * @param focusMatrixDataHandler FocusMatrixDataHandler
   */
  public FocusMatrixTreeViewContentProvider(final List<IUseCaseItemClientBO> list,
      final FocusMatrixUseCaseRootNode fmRootNode2, final FocusMatrixDataHandler focusMatrixDataHandler) {

    this.fmRootNode = fmRootNode2;
    this.listOfFavItems = list;
    this.focusMatrixDataHandler = focusMatrixDataHandler;

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
      return new Object[] { this.fmRootNode };
    }
    if (inputElement instanceof FocusMatrixUseCaseRootNode) {
      List<Object> listOfFirstLevelGroups = consolidateGroupsWithChildren();
      return listOfFirstLevelGroups.toArray();
    }
    return getProjFavChildItems(inputElement);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {

    if (parentElement instanceof FocusMatrixUseCaseRootNode) {
      List<Object> listOfFirstLevelGroups = consolidateGroupsWithChildren();
      return listOfFirstLevelGroups.toArray();
    }
    return getProjFavChildItems(parentElement);

  }

  /**
   * @return
   */
  private List<Object> consolidateGroupsWithChildren() {
    List<Object> listOfFirstLevelGroups = new ArrayList<>();
    for (Object groupObject : getProjectFavGroups()) {
      if (hasChildren(groupObject)) {
        listOfFirstLevelGroups.add(groupObject);
      }
    }
    return listOfFirstLevelGroups;
  }

  /**
   * @param parentElement
   * @return
   */
  private Object[] getProjFavChildItems(final Object parentElement) {
    Set<IUseCaseItemClientBO> finalSet = new TreeSet<>();
    IUseCaseItemClientBO parentElementUCItem = (IUseCaseItemClientBO) parentElement;

    for (IUseCaseItemClientBO abstractUseCaseItem : this.focusMatrixDataHandler
        .getChildFocusMatrixItems(this.listOfFavItems, parentElementUCItem)) {
      if (this.listOfFavItems.contains(abstractUseCaseItem)) {
        finalSet.add(abstractUseCaseItem);
      }
    }
    return finalSet.toArray();
  }

  /**
   * @return
   */
  private Object[] getProjectFavGroups() {
    Set<IUseCaseItemClientBO> finalSet = new TreeSet<>();
    for (FavUseCaseItemNode favNode : this.fmRootNode.getUseCaseGroups()) {
      if (this.listOfFavItems.contains(favNode.getUseCaseItem())) {
        finalSet.add(favNode.getUseCaseItem());
      }
    }
    return finalSet.toArray();
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
    if (element instanceof FocusMatrixUseCaseRootNode) {
      return true;
    }
    return (getProjFavChildItems(element)).length > 0;

  }

}
