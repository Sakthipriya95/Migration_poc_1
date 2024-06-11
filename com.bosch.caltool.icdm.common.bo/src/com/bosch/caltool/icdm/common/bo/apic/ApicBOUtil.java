/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.apic;

import java.util.SortedSet;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;

/**
 * @author bne4cob
 */
public final class ApicBOUtil {

  /**
   * Attribute Separator
   */
  private static final String ATTR_SEP = "  ;  ";
  /**
   * Value separator
   */
  private static final String VAL_SEP = "  --> ";


  private ApicBOUtil() {
    // Private constructor
  }


  /**
   * Convert attribute value model collection to its string representation
   *
   * @param attrValSet set of attrbiute value model
   * @return string
   */
  public static String getAttrValueString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append(VAL_SEP).append(attrVal.getValue().getName()).append(ATTR_SEP);
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - ATTR_SEP.length()).trim();
    }
    return result;
  }
}
