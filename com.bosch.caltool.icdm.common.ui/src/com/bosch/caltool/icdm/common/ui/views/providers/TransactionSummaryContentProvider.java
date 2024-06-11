package com.bosch.caltool.icdm.common.ui.views.providers;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class serves as the content provider for the Transaction Summary
 *
 * @author dmo5cob
 */
public class TransactionSummaryContentProvider implements ITreeContentProvider {

  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof SortedSet<?>) {
      return ((SortedSet<?>) parentElement).toArray();
    }
    return new Object[0];
  }

  @Override
  public Object getParent(final Object element) {
    return null;
  }

  @Override
  public boolean hasChildren(final Object element) {
    if (element instanceof List) {
      return !((List<?>) element).isEmpty();
    }
    return false;
  }

  @Override
  public Object[] getElements(final Object elements) {
    return getChildren(elements);
  }

  @Override
  public void dispose() {
    // TO-DO
  }

  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TO-DO
  }
}