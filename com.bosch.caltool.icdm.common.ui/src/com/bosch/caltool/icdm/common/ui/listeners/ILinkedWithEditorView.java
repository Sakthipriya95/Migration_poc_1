/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;


/**
 *  //iCDM-1241
 * @author jvi6cob
 */
public interface ILinkedWithEditorView {

  /**
   * Called when an editor is activated e.g. by a click from the user.
   * @param activeEditor 
   * 
   */
  void editorActivated(IEditorPart activeEditor);

  /**
   * @return The site for this view.
   */
  IViewSite getViewSite();
}
