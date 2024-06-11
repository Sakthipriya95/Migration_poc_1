/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.cns.common.model.PageInfo;
import com.bosch.caltool.icdm.cns.common.model.PageResponse;

/**
 * Get page of data from the input elements
 *
 * @author bne4cob
 * @param <E> element
 */
public class DataPaginator<E> {

  /**
   * Min size of page allowed
   */
  private static final int PAGE_SIZE_MIN = 5;
  /**
   * Max size of page allowed
   */
  private static final int PAGE_SIZE_MAX = 50;

  private final List<E> elements;

  /**
   * @param elements elements
   */
  public DataPaginator(final List<E> elements) {
    this.elements = elements == null ? null : new ArrayList<>(elements);
  }

  /**
   * Create page using start index and page size, mapped to a different object type
   *
   * @param startAt start index
   * @param pageSize page size
   * @param mapper mapper to convert the element to a different object
   * @return page response with elements and page info
   */
  public <R> PageResponse<R> createPage(final int startAt, final int pageSize,
      final Function<? super E, ? extends R> mapper) {

    int totalRecs = this.elements.size();
    int size = findSize(pageSize);
    int start = findStartIndex(startAt, totalRecs, size);
    int end = findEndIndex(totalRecs, size, start);

    PageInfo pageInfo = new PageInfo();
    pageInfo.setPageSize(size);
    pageInfo.setStartIndex(start);
    pageInfo.setEndIndex(end);
    pageInfo.setTotalRecords(totalRecs);

    PageResponse<R> ret = new PageResponse<>();

    if (totalRecs <= size) {
      ret.setElementList(this.elements.stream().map(mapper).collect(Collectors.toList()));
      pageInfo.setNext(false);
      pageInfo.setPrevious(false);
    }
    else {
      int idx = start;
      List<R> elementList = new ArrayList<>();
      while ((idx < (start + size)) && (idx < totalRecs)) {
        elementList.add(mapper.apply(this.elements.get(idx)));
        idx++;
      }
      ret.setElementList(elementList);
      pageInfo.setNext(idx < pageInfo.getTotalRecords());
      pageInfo.setPrevious(0 < start);
    }

    ret.setPageInfo(pageInfo);
    return ret;

  }

  /**
   * Create page using start index and page size
   *
   * @param startAt start index
   * @param pageSize page size
   * @return page response with elements and page info
   */
  public PageResponse<E> createPage(final int startAt, final int pageSize) {
    return createPage(startAt, pageSize, e -> e);
  }

  /**
   * Find end index
   *
   * @param totalRecords
   * @param size
   * @param start
   * @return
   */
  private int findEndIndex(final int totalRecords, final int size, final int start) {
    int end = (start + size) - 1;
    if (end > (totalRecords - 1)) {
      end = totalRecords - 1;
    }
    if (end < 0) {
      end = 0;
    }
    return end;
  }

  /**
   * Find start index
   *
   * @param startAt
   * @param totalRecords
   * @param size
   * @return
   */
  private int findStartIndex(final int startAt, final int totalRecords, final int size) {
    int start = startAt;
    // If start > total records, set start index of last page
    if (start >= totalRecords) {
      start = (totalRecords / size) * size;
      if (start == totalRecords) {
        int r = totalRecords % size;
        start = (r == 0) ? totalRecords - size : totalRecords - r;
      }
    }
    // If start <0, set start index to 0
    if (start < 0) {
      start = 0;
    }
    return start;
  }

  /**
   * Find page size
   *
   * @param pageSize
   * @param totalRecords
   * @return
   */
  private int findSize(final int pageSize) {
    int size = pageSize;
    if (pageSize < 1) {
      // Min size = 5
      size = PAGE_SIZE_MIN;
    }
    else if (pageSize > PAGE_SIZE_MAX) {
      // Max size = 50
      size = PAGE_SIZE_MAX;
    }
    return size;
  }


}
