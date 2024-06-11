/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * @author mga1cob
 */
public final class TextUtil {

  /**
   * TextUtil instance
   */
  private static TextUtil txtUtil;

  /**
   * The private constructor
   */
  private TextUtil() {
    // The private constructor
  }

  /**
   * This method returns TextUtil instance
   * 
   * @return TextUtil
   */
  public static TextUtil getInstance() {
    if (txtUtil == null) {
      txtUtil = new TextUtil();
    }
    return txtUtil;
  }


  /**
   * This method returns Label instance
   * 
   * @param composite instance
   * @return Text
   */
  public Text createText(Composite composite) {
    return new Text(composite, SWT.NONE);
  }

  /**
   * This method creates filter text field
   * 
   * @param formToolkit instance
   * @param composite instance
   * @param layoutData instance
   * @param textMessage defines text message
   * @return Text instance
   */
  public Text createFilterText(FormToolkit formToolkit, Composite composite, GridData layoutData, String textMessage) {
    Text text = formToolkit.createText(composite, null, SWT.SINGLE | SWT.BORDER);
    text.setMessage(textMessage);
    text.setLayoutData(layoutData);
    return text;
  }


  /**
   * This method creates UI text field
   * 
   * @param composite instance
   * @param gridData instance
   * @param isEnable defines whether text filed should enable or not
   * @param txtName defines text filed info
   * @return Text instance
   */
  public Text createText(Composite composite, GridData gridData, boolean isEnable, String txtName) {
    Text text = new Text(composite, SWT.BORDER);
    text.setLayoutData(gridData);
    text.setEnabled(isEnable);
    text.setText(txtName);
    return text;
  }

  /**
   * This method creates UI text field
   * 
   * @param composite instance
   * @param isEnable defines whether text filed should enable or not
   * @param txtName defines text filed info
   * @return Text instance
   */
  public Text createText(Composite composite, boolean isEnable, String txtName) {
    Text text = new Text(composite, SWT.BORDER);
    text.setEnabled(isEnable);
    text.setText(txtName);
    return text;
  }

  /**
   * This method creates with or without enabled UI text filed
   * 
   * @param formToolkit instance
   * @param composite instance
   * @param isEnable defines whether text filed should enable or not
   * @param textMessage defines text message
   * @return Text instance
   */
  public Text createText(FormToolkit formToolkit, Composite composite, boolean isEnable, String textMessage) {
    Text text = formToolkit.createText(composite, null, SWT.SINGLE | SWT.BORDER);
    text.setMessage(textMessage);
    text.setEnabled(isEnable);
    return text;
  }

  /**
   * This method creates with or without editable UI text filed
   * 
   * @param formToolkit instance
   * @param composite instance
   * @param isEnable defines whether text filed should editable or not
   * @param textMessage defines text message
   * @return Text instance
   */
  public Text createEditableText(FormToolkit formToolkit, Composite composite, boolean isEnable, String textMessage) {
    Text text = formToolkit.createText(composite, null, SWT.SINGLE | SWT.BORDER);
    text.setMessage(textMessage);
    text.setEditable(isEnable);
    return text;
  }

  /**
   * This method creates text field
   * 
   * @param caldataComp
   * @return Text
   */
  public Text createTextFileldWithHorzFill(final Composite comp, final FormToolkit formToolKit) {

    final Text text = TextUtil.getInstance().createEditableText(formToolKit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

}
