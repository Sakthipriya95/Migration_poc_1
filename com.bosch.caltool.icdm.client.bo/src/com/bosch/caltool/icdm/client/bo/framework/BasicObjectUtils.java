/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import com.bosch.caltool.datamodel.core.IBasicObject;

/**
 * @author bne4cob
 */
public final class BasicObjectUtils {

  private BasicObjectUtils() {
    // Private constructor
  }

  /**
   * Get the tooltip from a Basic object
   * 
   * @param obj Basic Object
   * @return tooltip
   */
  public static String getToolTip(final IBasicObject obj) {
    StringBuilder toolTip = new StringBuilder("Name : ");
    toolTip.append(obj.getName());

    String desc = obj.getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }
    return toolTip.toString();
  }
}
