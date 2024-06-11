/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;


/**
 * @author bne4cob
 */
public class PageInfo {

  private int pageSize;
  private boolean next;
  private boolean previous;
  private int totalRecords;
  private int startIndex;
  private int endIndex;

  /**
   * @return the pageSize
   */
  public int getPageSize() {
    return this.pageSize;
  }

  /**
   * @param pageSize the pageSize to set
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * @return the next
   */
  public boolean getNext() {
    return this.next;
  }

  /**
   * @param next the next to set
   */
  public void setNext(final boolean next) {
    this.next = next;
  }

  /**
   * @return the previous
   */
  public boolean getPrevious() {
    return this.previous;
  }

  /**
   * @param previous the previous to set
   */
  public void setPrevious(final boolean previous) {
    this.previous = previous;
  }

  /**
   * @return the totalRecords
   */
  public int getTotalRecords() {
    return this.totalRecords;
  }

  /**
   * @param totalRecords the totalRecords to set
   */
  public void setTotalRecords(final int totalRecords) {
    this.totalRecords = totalRecords;
  }

  /**
   * @return the startIndex
   */
  public int getStartIndex() {
    return this.startIndex;
  }

  /**
   * @param startIndex the startIndex to set
   */
  public void setStartIndex(final int startIndex) {
    this.startIndex = startIndex;
  }

  /**
   * @return the endIndex
   */
  public int getEndIndex() {
    return this.endIndex;
  }

  /**
   * @param endIndex the endIndex to set
   */
  public void setEndIndex(final int endIndex) {
    this.endIndex = endIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PageInfo [startIndex=" + this.startIndex + ", endIndex=" + this.endIndex + ", pageSize=" + this.pageSize +
        ", next=" + this.next + ", previous=" + this.previous + ", totalRecords=" + this.totalRecords + "]";
  }


}
