/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.IElementComparer;


/**
 * @author hnu1cob
 */
public class PIDCDetailsTreeElementsComparer implements IElementComparer {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj1, final Object obj2) {
    return obj1.equals(obj2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode(final Object element) {
    return element.hashCode();
  }

}
