/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;

/**
 * @author dja7cob
 * @param <A> project attribute
 */
public class ProjectImportAttr<A extends IProjectAttribute> {

  private A excelAttr;

  private A pidcAttr;

  private A compareAttr;

  private Attribute attr;

  private boolean validImport;

  private boolean isNewlyAddedVal;

  private boolean isCreateAttr;

  private boolean isCleared;

  private String comment;

  /**
   * @return the isCreateAttr
   */
  public boolean isCreateAttr() {
    return this.isCreateAttr;
  }


  /**
   * @param isCreateAttr the isCreateAttr to set
   */
  public void setCreateAttr(final boolean isCreateAttr) {
    this.isCreateAttr = isCreateAttr;
  }

  /**
   * @return the excelAttr
   */
  public A getExcelAttr() {
    return this.excelAttr;
  }


  /**
   * @param excelAttr the excelAttr to set
   */
  public void setExcelAttr(final A excelAttr) {
    this.excelAttr = excelAttr;
  }


  /**
   * @return the pidcAttr
   */
  public A getPidcAttr() {
    return this.pidcAttr;
  }


  /**
   * @param pidcAttr the pidcAttr to set
   */
  public void setPidcAttr(final A pidcAttr) {
    this.pidcAttr = pidcAttr;
  }


  /**
   * @return the compareAttr
   */
  public A getCompareAttr() {
    return this.compareAttr;
  }


  /**
   * @param compareAttr the compareAttr to set
   */
  public void setCompareAttr(final A compareAttr) {
    this.compareAttr = compareAttr;
  }


  /**
   * @return the validImport
   */
  public boolean isValidImport() {
    return this.validImport;
  }


  /**
   * @param validImport the validImport to set
   */
  public void setValidImport(final boolean validImport) {
    this.validImport = validImport;
  }


  /**
   * @return the comment
   */
  public String getComment() {
    return this.comment;
  }


  /**
   * @param comment the comment to set
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }


  /**
   * @return the isNewlyAddedVal
   */
  public boolean isNewlyAddedVal() {
    return this.isNewlyAddedVal;
  }


  /**
   * @param isNewlyAddedVal the isNewlyAddedVal to set
   */
  public void setNewlyAddedVal(final boolean isNewlyAddedVal) {
    this.isNewlyAddedVal = isNewlyAddedVal;
  }


  /**
   * @return the isCleared
   */
  public boolean isCleared() {
    return this.isCleared;
  }


  /**
   * @param isCleared the isCleared to set
   */
  public void setCleared(final boolean isCleared) {
    this.isCleared = isCleared;
  }


  /**
   * @return the attr
   */
  public Attribute getAttr() {
    return this.attr;
  }


  /**
   * @param attr the attr to set
   */
  public void setAttr(final Attribute attr) {
    this.attr = attr;
  }


}
