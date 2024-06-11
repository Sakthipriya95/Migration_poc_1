package com.bosch.caltool.icdm.common.ui.listeners;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * @author jvi6cob //iCDM-1241
 */
public class LinkWithEditorPartListener implements IPartListener2 {

  /**
   * define the view
   */
  private final ILinkedWithEditorView view;

  /**
   * @param view view
   */
  public LinkWithEditorPartListener(final ILinkedWithEditorView view) {
    this.view = view;
  }

  @Override
  public void partActivated(final IWorkbenchPartReference ref) {
    if (ref.getPart(true) instanceof IEditorPart) {
      // changes on editor activated
      this.view.editorActivated(this.view.getViewSite().getPage().getActiveEditor());
    }
  }

  @Override
  public void partBroughtToTop(final IWorkbenchPartReference ref) {
    if (ref.getPart(true) == this.view) {
      // changes on editor brought to top
      this.view.editorActivated(this.view.getViewSite().getPage().getActiveEditor());
    }
  }

  @Override
  public void partOpened(final IWorkbenchPartReference ref) {
    if (ref.getPart(true) == this.view) {
      // changes on editor opened
      this.view.editorActivated(this.view.getViewSite().getPage().getActiveEditor());
    }
  }

  @Override
  public void partVisible(final IWorkbenchPartReference ref) {
    if (ref.getPart(true) == this.view) {
      IEditorPart editor = this.view.getViewSite().getPage().getActiveEditor();
      if (editor != null) {
        // changes on editor visible
        this.view.editorActivated(editor);
      }
    }
  }

  @Override
  public void partClosed(final IWorkbenchPartReference ref) {
    // no implementation required
    // when part closed
  }

  @Override
  public void partDeactivated(final IWorkbenchPartReference ref) {
    // no implementation required
    // when part deactivated
  }

  @Override
  public void partHidden(final IWorkbenchPartReference ref) {
    // no implementation required
    // when part hidden
  }

  @Override
  public void partInputChanged(final IWorkbenchPartReference ref) {
    // no implementation required
    // when part input changed
  }
}