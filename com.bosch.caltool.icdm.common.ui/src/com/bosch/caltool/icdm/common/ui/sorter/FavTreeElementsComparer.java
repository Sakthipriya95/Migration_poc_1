/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.IElementComparer;


/**
 * @author adn1cob iCDM-1241
 */

public class FavTreeElementsComparer implements IElementComparer {

  /**
   * Index 2
   */
  private static final int INDEX_2 = 2;
  /**
   * Index 1
   */
  private static final int INDEX_1 = 1;
  /**
   * Index 0
   */
  private static final int INDEX_0 = 0;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj1, final Object obj2) {
    // Comparison for elements under REVIEW RESULTS under PIDC tree
    if ((obj1 instanceof String[]) && (obj2 instanceof String[])) {
      String[] stra1 = (String[]) obj1;
      String[] stra2 = (String[]) obj2;
      return stra1[INDEX_0].equals(stra2[INDEX_0]) && stra1[INDEX_1].equals(stra2[INDEX_1]) &&
          stra1[INDEX_2].equals(stra2[INDEX_2]);
    }
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
