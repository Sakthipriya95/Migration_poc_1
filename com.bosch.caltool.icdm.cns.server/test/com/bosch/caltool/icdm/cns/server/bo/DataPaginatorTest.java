/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.bosch.caltool.icdm.cns.common.model.PageResponse;
import com.bosch.caltool.icdm.cns.server.utils.DataPaginator;


/**
 * @author bne4cob
 */
public class DataPaginatorTest {

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.server.utils.DataPaginator#createPage(int, int)}.
   */
  @Test
  public void testCreatePageIntInt() {
    DataPaginator<Integer> pager = new DataPaginator<>(Arrays.asList(1, 2, 3, 4, 5));

    testDP(pager.createPage(0, 2), "[1, 2]",
        "[startIndex=0, endIndex=1, pageSize=2, next=true, previous=false, totalRecords=5]");
    testDP(pager.createPage(2, 2), "[3, 4]",
        "[startIndex=2, endIndex=3, pageSize=2, next=true, previous=true, totalRecords=5]");
    testDP(pager.createPage(4, 2), "[5]",
        "[startIndex=4, endIndex=4, pageSize=2, next=false, previous=true, totalRecords=5]");
    testDP(pager.createPage(-1, 2), "[1, 2]",
        "[startIndex=0, endIndex=1, pageSize=2, next=true, previous=false, totalRecords=5]");
    testDP(pager.createPage(0, -1), "[1, 2, 3, 4, 5]",
        "[startIndex=0, endIndex=4, pageSize=5, next=false, previous=false, totalRecords=5]");
    testDP(pager.createPage(10, 2), "[5]",
        "[startIndex=4, endIndex=4, pageSize=2, next=false, previous=true, totalRecords=5]");
    testDP(pager.createPage(10, -1), "[1, 2, 3, 4, 5]",
        "[startIndex=0, endIndex=4, pageSize=5, next=false, previous=false, totalRecords=5]");
    testDP(pager.createPage(0, 10), "[1, 2, 3, 4, 5]",
        "[startIndex=0, endIndex=4, pageSize=10, next=false, previous=false, totalRecords=5]");
    testDP(pager.createPage(0, 5), "[1, 2, 3, 4, 5]",
        "[startIndex=0, endIndex=4, pageSize=5, next=false, previous=false, totalRecords=5]");
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.utils.DataPaginator#createPage(int, int, java.util.function.Function)}.
   */
  @Test
  public void testCreatePageIntIntFunctionOfQsuperEQextendsR() {
    DataPaginator<String> pager = new DataPaginator<>(Arrays.asList("1", "2", "3", "4", "5"));
    testDP(pager.createPage(0, 2, Integer::parseInt), "[1, 2]",
        "[startIndex=0, endIndex=1, pageSize=2, next=true, previous=false, totalRecords=5]");
  }

  private void testDP(final PageResponse<Integer> pr, final String expectedE, final String expectedPI) {
    assertEquals("Elements ", expectedE, pr.getElementList().toString());
    assertEquals("page info ", "PageInfo " + expectedPI, pr.getPageInfo().toString());
  }

}
