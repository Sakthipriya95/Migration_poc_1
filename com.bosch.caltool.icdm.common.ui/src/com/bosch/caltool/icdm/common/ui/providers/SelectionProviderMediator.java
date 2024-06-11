package com.bosch.caltool.icdm.common.ui.providers;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

/**
 * A selection provider for editor page parts with more that one viewer. Tracks the focus of the viewers to provide the
 * correct selection.
 */
public class SelectionProviderMediator implements IPostSelectionProvider {

  private StructuredViewer fViewerInFocus;
  private final ListenerList fSelectionChangedListeners;
  private final ListenerList fPostSelectionChangedListeners;

  List<StructuredViewer> fViewersList = new ArrayList<StructuredViewer>();
  private final SelectionProviderMediatorInternalListener internalListener;

  /**
   */
  public SelectionProviderMediator() {
    this.internalListener = new SelectionProviderMediatorInternalListener(this);
    this.fSelectionChangedListeners = new ListenerList();
    this.fPostSelectionChangedListeners = new ListenerList();

  }


  /**
   * @param viewer StructuredViewer
   */
  public void addViewer(final StructuredViewer viewer) {
    this.fViewersList.add(viewer);
    this.fViewerInFocus = viewer;

    viewer.addSelectionChangedListener(this.internalListener);
    viewer.addPostSelectionChangedListener(new InternalPostSelectionListener());
    Control control = viewer.getControl();
    control.addFocusListener(this.internalListener);
  }

  void doFocusChanged(final Widget control) {
    for (StructuredViewer fViewer : this.fViewersList) {
      if (fViewer.getControl() == control) {
        propagateFocusChanged(fViewer);
        return;
      }
    }
  }

  final void executeOnPostSelectionChanged(final SelectionChangedEvent event) {
    ISelectionProvider provider = event.getSelectionProvider();
    if (provider == this.fViewerInFocus) {
      onPostSelectionChanged();
    }
  }

  /**
   * @param event SelectionChangedEvent
   */
  final void doOnSelectionChange(final SelectionChangedEvent event) {
    ISelectionProvider provider = event.getSelectionProvider();
    if (provider == this.fViewerInFocus) {
      onSelectionChanged();
      IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
      if (viewPart != null) {
        PropertySheet propertySheet = (PropertySheet) viewPart;
        IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
        if (page != null) {
          page.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
              this.fViewerInFocus.getSelection());
        }
      }
    }
  }

  /**
   *
   */
  private void onPostSelectionChanged() {
    if (this.fPostSelectionChangedListeners != null) {
      SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

      Object[] listeners = this.fPostSelectionChangedListeners.getListeners();
      for (Object listener2 : listeners) {
        ISelectionChangedListener listener = (ISelectionChangedListener) listener2;
        listener.selectionChanged(event);
      }
    }
  }

  /**
   * @param viewer
   */
  final void propagateFocusChanged(final StructuredViewer viewer) {
    if (viewer != this.fViewerInFocus) {
      this.fViewerInFocus = viewer;
      onSelectionChanged();
      onPostSelectionChanged();
    }
  }

  /**
   *
   */
  private void onSelectionChanged() {
    if (this.fSelectionChangedListeners != null) {
      SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

      Object[] listeners = this.fSelectionChangedListeners.getListeners();
      for (Object listener2 : listeners) {
        ISelectionChangedListener listener = (ISelectionChangedListener) listener2;
        listener.selectionChanged(event);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    this.fSelectionChangedListeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    this.fSelectionChangedListeners.remove(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPostSelectionChangedListener(final ISelectionChangedListener listener) {
    this.fPostSelectionChangedListeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removePostSelectionChangedListener(final ISelectionChangedListener listener) {
    this.fPostSelectionChangedListeners.remove(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ISelection getSelection() {
    if (this.fViewerInFocus != null) {
      return this.fViewerInFocus.getSelection();
    }
    return StructuredSelection.EMPTY;
  }

  /**
   * @param selection ISelection
   * @param reveal flag
   */
  public void setSelection(final ISelection selection, final boolean reveal) {
    if (this.fViewerInFocus != null) {
      this.fViewerInFocus.setSelection(selection, reveal);
    }
  }

  /**
   * Returns the viewer in focus or null if no viewer has the focus
   *
   * @return returns the current viewer in focus
   */
  public StructuredViewer getViewerInFocus() {
    return this.fViewerInFocus;
  }

  /**
   *
   */
  public void invokePropertySheet() {
    // TO-DO
  }

  private class InternalPostSelectionListener implements ISelectionChangedListener {

    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
      executeOnPostSelectionChanged(event);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSelection(final ISelection selection) {
    if (this.fViewerInFocus != null) {
      this.fViewerInFocus.setSelection(selection);
    }
  }
}