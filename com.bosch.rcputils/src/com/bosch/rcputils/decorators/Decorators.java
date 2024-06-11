package com.bosch.rcputils.decorators;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

public class Decorators {
	/**
	 * Shows error field decoration if boolean show is true, else hides
	 * decoration.
	 * 
	 * @param controlDecoration
	 * @param message
	 * @param show
	 */
	public void showErrDecoration(ControlDecoration controlDecoration,
			String message, boolean show) {
		if (show) {
			controlDecoration.setDescriptionText(message);
			FieldDecoration fieldDecoration = FieldDecorationRegistry
					.getDefault().getFieldDecoration(
							FieldDecorationRegistry.DEC_ERROR);
			controlDecoration.setImage(fieldDecoration.getImage());
			controlDecoration.show();
		} else {
			controlDecoration.hide();
		}
	}

	/**
	 * Shows required field decoration.
	 * 
	 * @param controlDecoration
	 * @param message
	 */
	public void showReqdDecoration(ControlDecoration controlDecoration,
			String message) {
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.show();
	}
}
