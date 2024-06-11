/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Comparator;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * @author rgo7cob
 */
public class SSDFeaICDMAttrComp implements Comparator<SSDFeatureICDMAttrModel> {

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final SSDFeatureICDMAttrModel obj1, final SSDFeatureICDMAttrModel obj2) {
    String attrName1 = obj1.getAttrName();
    String attrName2 = obj2.getAttrName();
    if ((CommonUtils.isEmptyString(attrName2) && CommonUtils.isEmptyString(attrName1)) ||
        (!CommonUtils.isEmptyString(attrName2) && !CommonUtils.isEmptyString(attrName1))) {
      attrName1 = obj1.getFeaValModel().getFeatureText().toUpperCase();
      attrName2 = obj2.getFeaValModel().getFeatureText().toUpperCase();
      return ApicUtil.compare(attrName1, attrName2);
    }


    return ApicUtil.compare(attrName1, attrName2);
  }

}
