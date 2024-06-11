/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;
import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;

/**
 * @author UKT1COB
 */
public class ExportCDFxInputData {

  /**
   * attr Val Model Set
   */
  private SortedSet<AttributeValueModel> attrValueModSet;
  /**
   * label names
   */
  private List<String> labelNames;
  /**
   * Param Collection object
   */
  private ReviewRuleParamCol<RuleSet> paramCol;
  /**
   * User Selected FileName to Export CDFx File
   */
  private String destFileName;
  /**
   * User Selected File Directory to Export CDFx File
   */
  private String destFileDir;


  /**
   * @return the attrValueModSet
   */
  public SortedSet<AttributeValueModel> getAttrValueModSet() {
    return this.attrValueModSet;
  }


  /**
   * @param attrValueModSet the attrValueModSet to set
   */
  public void setAttrValueModSet(final SortedSet<AttributeValueModel> attrValueModSet) {
    this.attrValueModSet = attrValueModSet;
  }


  /**
   * @return the labelNames
   */
  public List<String> getLabelNames() {
    return this.labelNames;
  }


  /**
   * @param labelNames the labelNames to set
   */
  public void setLabelNames(final List<String> labelNames) {
    this.labelNames = labelNames;
  }


  /**
   * @return the paramCol
   */
  public ReviewRuleParamCol<RuleSet> getParamCol() {
    return this.paramCol;
  }


  /**
   * @param paramCol the paramCol to set
   */
  public void setParamCol(final ReviewRuleParamCol<RuleSet> paramCol) {
    this.paramCol = paramCol;
  }


  /**
   * @return the destFileName
   */
  public String getDestFileName() {
    return this.destFileName;
  }


  /**
   * @param destFileName the destFileName to set
   */
  public void setDestFileName(final String destFileName) {
    this.destFileName = destFileName;
  }


  /**
   * @return the destFileDir
   */
  public String getDestFileDir() {
    return this.destFileDir;
  }


  /**
   * @param destFileDir the destFileDir to set
   */
  public void setDestFileDir(final String destFileDir) {
    this.destFileDir = destFileDir;
  }


}
