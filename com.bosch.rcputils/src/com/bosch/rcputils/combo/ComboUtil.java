/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.combo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * @author mga1cob
 */
public final class ComboUtil {

  /**
   * ComboUtil instance
   */
  private static ComboUtil comboUtil;

  /**
   * The private constructor
   */
  private ComboUtil() {
    // The private constructor
  }

  /**
   * This method returns ComboUtil instance
   * 
   * @return ComboUtil
   */
  public static ComboUtil getInstance() {
    if (comboUtil == null) {
      comboUtil = new ComboUtil();
    }
    return comboUtil;
  }


  /**
   * The method creates a Combo
   * 
   * @param composite instance
   * @param style defines Combo style
   * @param gridData instance
   * @param addValue defines the value to add Combo
   * @param index defines value index where it should add in Combo
   * @param selectIndex defines to select index in Combo
   * @return Combo instance
   */
  public Combo createComboBox(final Composite composite, final int style, final GridData gridData,
      final String addValue, final int index, final int selectIndex) {
    final Combo combo = new Combo(composite, style);
    combo.setLayoutData(gridData);
    combo.add(addValue, index);
    combo.select(selectIndex);
    return combo;
  }

  /**
   * The method creates a Combo
   * 
   * @param composite instance
   * @param style defines Combo style
   * @param gridData instance
   * @param addValue defines the value to add Combo
   * @param index defines value index where it should add in Combo
   * @return Combo instance
   */
  public Combo createComboBox(final Composite composite, final int style, final GridData gridData,
      final String addValue, final int index) {
    final Combo combo = new Combo(composite, style);
    combo.setLayoutData(gridData);
    combo.add(addValue, index);
    combo.select(index);
    return combo;
  }

  /**
   * The method creates a Combo
   * 
   * @param composite instance
   * @param gridData instance
   * @param addValue defines the value to add Combo
   * @param index defines value index where it should add in Combo
   * @return Combo instance
   */
  public Combo createComboBox(final Composite composite, final GridData gridData, final String addValue, final int index) {
    final Combo combo = new Combo(composite, SWT.READ_ONLY);
    combo.setLayoutData(gridData);
    combo.add(addValue, index);
    combo.select(index);
    return combo;
  }

  /**
   * The method creates a read only Combo
   * 
   * @param composite instance
   * @param gridData instance
   * @return Combo instance
   */
  public Combo createComboBox(final Composite composite, final GridData gridData) {
    final Combo combo = new Combo(composite, SWT.READ_ONLY);
    combo.setLayoutData(gridData);
    return combo;
  }

  /**
   * The method creates a Combo
   * 
   * @param composite instance
   * @param gridData instance
   * @param style defines Combo style
   * @return Combo instance
   */
  public Combo createComboBox(final Composite composite, final GridData gridData, final int style) {
    final Combo combo = new Combo(composite, style);
    combo.setLayoutData(gridData);
    return combo;
  }

}
