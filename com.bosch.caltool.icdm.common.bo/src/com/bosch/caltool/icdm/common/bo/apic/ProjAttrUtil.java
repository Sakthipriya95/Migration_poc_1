/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.apic;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

/**
 * @author mkl2cob
 */
public class ProjAttrUtil {


  /**
   * @param projectAttribute IProjectAttribute
   * @param attributeValue AttributeValue
   */
  public static void setValueToUsedFlag(final IProjectAttribute projectAttribute, final AttributeValue attributeValue) {

    if ("".equals(projectAttribute.getUsedFlag()) && ((attributeValue == null) || (attributeValue.getId() == null))) {
      // when new attribute value is created, attributeValue obj is null or attributeValue is not null but id is null
      projectAttribute.setUsedFlag(ApicConstants.NOT_DEFINED);
    }
    else if ((attributeValue != null) && (attributeValue.getId() != null)) {
      projectAttribute.setUsedFlag(ApicConstants.CODE_YES);
    }
    else {
      projectAttribute.setUsedFlag(projectAttribute.getUsedFlag());
    }
  }


}
