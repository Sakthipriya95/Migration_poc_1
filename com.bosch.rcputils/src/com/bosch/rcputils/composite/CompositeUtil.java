/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


/**
 * @author mga1cob
 */
public final class CompositeUtil {


  /**
   * CompositeUtil instance
   */
  private static CompositeUtil compositeUtil;

  /**
   * The default constructor
   */
  private CompositeUtil() {

  }

  /**
   * This method returns CompositeUtil instance
   * 
   * @return CompositeUtil
   */
  public static CompositeUtil getInstance() {
    if (compositeUtil == null) {
      compositeUtil = new CompositeUtil();
    }
    return compositeUtil;
  }


  /**
   * This method returns Composite instance
   * 
   * @param parent instance
   * @return Composite
   */
  public Composite createComposite(final Composite parent) {
    return new Composite(parent, SWT.NONE);
  }

  /**
   * This method returns Composite instance
   * 
   * @param parent instance
   * @param layoutData instance
   * @return Composite
   */
  public Composite createComposite(final Composite parent, final GridData layoutData) {
    final Composite compsosite = new Composite(parent, SWT.NONE);
    compsosite.setLayoutData(layoutData);
    return compsosite;
  }

}
