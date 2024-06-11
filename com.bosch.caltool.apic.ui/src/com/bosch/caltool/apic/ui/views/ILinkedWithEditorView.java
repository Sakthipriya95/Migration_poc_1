package com.bosch.caltool.apic.ui.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;

/**
 * ILinkedWithEditorView - Interface supporting linking editors with views
 * 
 * @author adn1cob
 * 
 */

public interface ILinkedWithEditorView {

	/**
	 * Called when an editor is activated e.g. by a click from the user.
	 * 
	 * @param The activated editor part.
	 */
	void editorActivated(IEditorPart activeEditor);

	/**
	 * GetViewState
	 * 
	 * @return The site for this view.
	 */

	IViewSite getViewSite();
}
