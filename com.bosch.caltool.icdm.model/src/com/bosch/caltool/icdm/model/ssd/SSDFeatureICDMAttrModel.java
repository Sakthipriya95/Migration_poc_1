/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssd;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;


/**
 * ICDM-1063 - This class holds the Attribute and AttributeValue objects corresponding to the feature and featureValue
 * in SSD
 *
 * @author dmo5cob
 */
public class SSDFeatureICDMAttrModel {

  

  private FeatureValueICDMModel feaValModel;

  private AttributeValueModel attrValModel;


  
  /**
   * @param feaValModel the feaValModel to set
   */
  public void setFeaValModel(FeatureValueICDMModel feaValModel) {
    this.feaValModel = feaValModel;
  }


  
  /**
   * @param attrValModel the attrValModel to set
   */
  public void setAttrValModel(AttributeValueModel attrValModel) {
    this.attrValModel = attrValModel;
  }


  /**
   * @return the feaValModel
   */
  public FeatureValueICDMModel getFeaValModel() {
    return feaValModel;
  }


  /**
   * @return the attrValModel
   */
  public AttributeValueModel getAttrValModel() {
    return attrValModel;
  }


}
