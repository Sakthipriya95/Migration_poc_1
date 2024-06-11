/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


/**
 * @author bne4cob ICDM-254
 */
public interface IPastableItem {

  /**
   * Checks whether the item can be pasted.
   * 
   * @param selectedObj object on which the item is to be pasted
   * @param copiedObj object which is copied
   * @return whether the item can be pasted on the <code>selObj</code>
   */
  boolean isPasteAllowed(Object selectedObj, Object copiedObj);

}
