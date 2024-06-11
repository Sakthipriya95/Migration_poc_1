/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bne4cob
 * @param <E> element type
 */
public class PageResponse<E> {

  private List<E> elementList = new ArrayList<>();
  private PageInfo pageInfo;

  /**
   * @return the elementList
   */
  public List<E> getElementList() {
    return this.elementList == null ? null : new ArrayList<>(this.elementList);
  }


  /**
   * @param elementList the elementList to set
   */
  public void setElementList(final List<E> elementList) {
    this.elementList = elementList == null ? null : new ArrayList<>(elementList);
  }


  /**
   * @return the pageInfo
   */
  public PageInfo getPageInfo() {
    return this.pageInfo;
  }


  /**
   * @param pageInfo the pageInfo to set
   */
  public void setPageInfo(final PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [pageInfo=" + this.pageInfo + "]";
  }


}
