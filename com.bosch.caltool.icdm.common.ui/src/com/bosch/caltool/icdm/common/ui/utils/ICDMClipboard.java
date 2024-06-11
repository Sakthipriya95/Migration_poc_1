/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.util.ArrayList;

import com.bosch.caltool.icdm.model.apic.IPastableItem;


/**
 * @author mga1cob
 */
// ICDM-150
public enum ICDMClipboard {

                           /**
                            * Enum constant for singleton class implimentation.
                            */
                           INSTANCE;

  /**
   * /** Defines copied object
   */
  private Object copiedObject;

  private boolean isCopyOnlyWorkingSet;

  /**
   * Get Instance method.
   *
   * @return instance of this class
   */
  public static ICDMClipboard getInstance() {
    return INSTANCE;
  }

  /**
   * @return the copiedObject
   */
  public Object getCopiedObject() {
    return this.copiedObject;
  }

  /**
   * @param copiedObject the copidObject to set
   */
  public void setCopiedObject(final Object copiedObject) {
    this.copiedObject = copiedObject;

  }

  /**
   * //ICDM-254 This method validates if paste is allowed on the selected item
   *
   * @param selectedObj selectedObject
   * @return boolean
   */
  public boolean isPasteAllowed(final Object selectedObj) {

    // When there are multiple items to be copied
    if (this.copiedObject instanceof ArrayList) {
      for (Object obj : (ArrayList<?>) this.copiedObject) {
        if (obj instanceof IPastableItem) {
          return ((IPastableItem) obj).isPasteAllowed(selectedObj, this.copiedObject);
        }
      }
      return false;
    } // When there is only a single item to be copied
    else if (this.copiedObject instanceof IPastableItem) {
      return ((IPastableItem) this.copiedObject).isPasteAllowed(selectedObj, this.copiedObject);
    }
    return false;
  }

  /**
   * @return the isCopyOnlyWorkingSet
   */
  public boolean isCopyOnlyWorkingSet() {
    return this.isCopyOnlyWorkingSet;
  }

  /**
   * @param isCopyOnlyWorkingSet the isCopyOnlyWorkingSet to set
   */
  public void setCopyOnlyWorkingSet(final boolean isCopyOnlyWorkingSet) {
    this.isCopyOnlyWorkingSet = isCopyOnlyWorkingSet;
  }


}
