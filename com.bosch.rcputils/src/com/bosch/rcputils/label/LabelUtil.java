/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.label;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * @author mga1cob
 */
public final class LabelUtil {

  /**
   * LabelUtil instance
   */
  private static LabelUtil labelUtil;

  /**
   * The default constructor
   */
  private LabelUtil() {
    // Private constructor
  }

  /**
   * This method returns LabelUtil instance
   * 
   * @return LabelUtil
   */
  public static LabelUtil getInstance() {
    if (labelUtil == null) {
      labelUtil = new LabelUtil();
    }
    return labelUtil;
  }


  /**
   * This method returns Label instance
   * 
   * @param composite instance
   * @param lblName defines the label name
   * @return Label
   */
  public Label createLabel(final Composite composite, final String lblName) {
    final Label label = new Label(composite, SWT.NONE);
    label.setText(lblName);
    return label;
  }

  /**
   * This method returns Label instance
   * 
   * @param composite instance
   * @return Label
   */
  public Label createEmptyLabel(final Composite composite) {
    return new Label(composite, SWT.NONE);
  }


  /**
   * @param formToolkit instance
   * @param composite instance
   * @param lblName defines label name
   * @return Lable instance
   */
  public Label createLabel(final FormToolkit formToolkit, final Composite composite, final String lblName) {
    return formToolkit.createLabel(composite, lblName);
  }

  /**
   * Sets the label error style.
   *
   * @param label the new label err style
   */
  public void setErrorStyle(final Label label) {
    label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
  }

  /**
   * Reset label style.
   *
   * @param label the label
   */
  public void resetLabelStyle(final Label label) {
    label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    label.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT));
  }
}