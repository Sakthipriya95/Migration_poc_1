/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;

/**
 * @author DMR1COB
 */
public class A2lRespMergeData {

  /**
   * selected qnaire resp details set
   */
  private SortedSet<QnaireRespDetails> retainedQnaireRespDetailsSet = new TreeSet<>();

  /**
   * Destination a2l responsibility
   */
  private A2lResponsibility destA2lResponsibility;

  /**
   * List of source responsibility that needs to be merged with destination resp
   */
  private List<A2lResponsibility> selectedA2lWpRespList = new ArrayList<>();


  /**
   * @return the retainedQnaireRespDetailsSet
   */
  public SortedSet<QnaireRespDetails> getRetainedQnaireRespDetailsSet() {
    return this.retainedQnaireRespDetailsSet;
  }


  /**
   * @param retainedQnaireRespDetailsSet the retainedQnaireRespDetailsSet to set
   */
  public void setRetainedQnaireRespDetailsSet(final SortedSet<QnaireRespDetails> retainedQnaireRespDetailsSet) {
    this.retainedQnaireRespDetailsSet = retainedQnaireRespDetailsSet;
  }


  /**
   * @return the destA2lResponsibility
   */
  public A2lResponsibility getDestA2lResponsibility() {
    return this.destA2lResponsibility;
  }


  /**
   * @param destA2lResponsibility the destA2lResponsibility to set
   */
  public void setDestA2lResponsibility(final A2lResponsibility destA2lResponsibility) {
    this.destA2lResponsibility = destA2lResponsibility;
  }


  /**
   * @return the selectedA2lWpRespList
   */
  public List<A2lResponsibility> getSelectedA2lWpRespList() {
    return this.selectedA2lWpRespList;
  }


  /**
   * @param selectedA2lWpRespList the selectedA2lWpRespList to set
   */
  public void setSelectedA2lWpRespList(final List<A2lResponsibility> selectedA2lWpRespList) {
    this.selectedA2lWpRespList = selectedA2lWpRespList;
  }
}
