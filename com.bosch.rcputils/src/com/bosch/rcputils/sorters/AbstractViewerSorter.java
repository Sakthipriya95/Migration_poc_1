/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.sorters;

import org.eclipse.jface.viewers.ViewerSorter;


/**
 * @author mga1cob
 */
public abstract class AbstractViewerSorter extends ViewerSorter {

  /**
   * Sets the column index
   * 
   * @param index the column index
   */
  public abstract void setColumn(int index);

  /**
   * Returns the tableviewer sorted direction.
   * 
   * @return int of the tableviewer sorted direction
   */
  public abstract int getDirection();

}
