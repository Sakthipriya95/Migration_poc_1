/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * ICDM-1063 - This class holds the Attribute and AttributeValue objects corresponding to the feature and featureValue
 * in SSD
 *
 * @author dmo5cob
 */
public class SSDFeatureICDMAttrModel implements Comparable<SSDFeatureICDMAttrModel> {


  private final FeatureValueModel feaValModel;

  private final AttributeValueModel attrValModel;

  /**
   * @param feaValModel feaValModel
   * @param attrValModel attrValModel
   */
  public SSDFeatureICDMAttrModel(final FeatureValueModel feaValModel, final AttributeValueModel attrValModel) {
    super();
    this.feaValModel = feaValModel;
    this.attrValModel = attrValModel;
  }


  /**
   * @return the feaValModel
   */
  public FeatureValueModel getFeaValModel() {
    return this.feaValModel;
  }


  /**
   * @return the attrValModel
   */
  public AttributeValueModel getAttrValModel() {
    return this.attrValModel;
  }

  /**
   * @return the attr name
   */
  public String getAttrName() {
    if (this.attrValModel != null) {
      Attribute attribute = this.attrValModel.getAttribute();
      if (attribute != null) {
        return attribute.getName();
      }
    }
    return "";
  }

  /**
   * @return the attr Val
   */
  public String getAttrVal() {

    if (this.attrValModel != null) {
      AttributeValue attrVal = this.attrValModel.getAttrValue();
      if (attrVal != null) {
        return attrVal.getName();
      }
    }
    return "";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SSDFeatureICDMAttrModel model) {
    String str1 = "";
    String str2 = "";

    if ((this.attrValModel != null) && (this.attrValModel.getAttribute() != null)) {
      str1 = this.attrValModel.getAttribute().getAttributeName();
    }
    if ((model.attrValModel != null) && (model.attrValModel.getAttribute() != null)) {
      str2 = model.attrValModel.getAttribute().getAttributeName();
    }
    if ((str1 == null) && (str2 == null)) {
      str1 = model.getFeaValModel().getFeatureText().toUpperCase();
      str2 = model.getFeaValModel().getFeatureText().toUpperCase();
    }
    return ApicUtil.compare(str1, str2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SSDFeatureICDMAttrModel) {
      SSDFeatureICDMAttrModel modelObj = (SSDFeatureICDMAttrModel) obj;
      this.feaValModel.getFeatureId().equals(modelObj.getFeaValModel().getFeatureId());
    }
    return false;
  }

}
