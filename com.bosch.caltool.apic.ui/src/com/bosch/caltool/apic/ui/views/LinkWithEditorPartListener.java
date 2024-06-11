package com.bosch.caltool.apic.ui.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * LinkWithEditorPartListener Listerners for editor changes
 *
 * @author adn1cob
 */

public class LinkWithEditorPartListener implements IPartListener2 {

  private final ILinkedWithEditorView viewLinkedWithEditor;

  /**
   * @param view ILinkedWithEditorView instance
   */
  public LinkWithEditorPartListener(final ILinkedWithEditorView view) {
    this.viewLinkedWithEditor = view;
  }

  @Override
  public void partActivated(final IWorkbenchPartReference ref) {
    if (ref.getPart(true) instanceof IEditorPart) {
      getActiveEditorAndActivate();
    }
  }


  @Override
  public void partBroughtToTop(final IWorkbenchPartReference ref) {
    if (checkForEditor(ref)) {
      getActiveEditorAndActivate();
    }
  }

  /**
   * @param ref
   * @return
   */
  private boolean checkForEditor(final IWorkbenchPartReference ref) {
    return ref.getPart(true) == this.viewLinkedWithEditor;
  }

  @Override
  public void partOpened(final IWorkbenchPartReference ref) {
    if (checkForEditor(ref)) {
      getActiveEditorAndActivate();
    }
  }

  @Override
  public void partVisible(final IWorkbenchPartReference ref) {
    if (checkForEditor(ref)) {
      IEditorPart editor = getActiveEditor();
      if (editor != null) {
        this.viewLinkedWithEditor.editorActivated(editor);
      }
    }
  }

  /**
   * @return
   */
  private IEditorPart getActiveEditor() {
    return this.viewLinkedWithEditor.getViewSite().getPage().getActiveEditor();
  }

  /**
   *
   */
  private void getActiveEditorAndActivate() {
    this.viewLinkedWithEditor.editorActivated(getActiveEditor());
  }

  @Override
  public void partClosed(final IWorkbenchPartReference ref) {
    IEditorReference[] editorRef = ref.getPage().getEditorReferences();
    if (editorRef.length == 0) {
      this.viewLinkedWithEditor.editorActivated(null);

    }
  }

  @Override
  public void partDeactivated(final IWorkbenchPartReference ref) {
    // TO-DO
  }

  @Override
  public void partHidden(final IWorkbenchPartReference ref) {
    // TO-DO
  }

  @Override
  public void partInputChanged(final IWorkbenchPartReference ref) {
    // TO-DO
  }
}
