/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.ui.forms;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * @author mga1cob
 */
public final class FormUtil {


  /**
   * FormUtil instance
   */
  private static FormUtil formUtil;

  /**
   * The private constructor
   */
  private FormUtil() {
    // The private constructor
  }

  /**
   * This method returns FormUtil instance
   * 
   * @return FormUtil
   */
  public static FormUtil getInstance() {
    if (formUtil == null) {
      formUtil = new FormUtil();
    }
    return formUtil;
  }


  /**
   * This meth method returns ScrolledForm instance
   * 
   * @param managedForm instance
   * @param formName defines the form name
   * @param image for the form layout
   * @return ScrolledForm
   */
  public ScrolledForm getScrolledForm(final IManagedForm managedForm, final String formName, final Image image) {
    ScrolledForm form = managedForm.getForm();
    form.setText(formName);
    form.setImage(image);
    return form;
  }
}
