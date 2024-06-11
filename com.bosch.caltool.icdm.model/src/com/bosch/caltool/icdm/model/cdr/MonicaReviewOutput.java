/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author say8cob
 */
public class MonicaReviewOutput {

  private List<MonicaReviewOutputData> monicaReviewOutputDataList;


  /**
   * @return the monicaReviewOutputDataList
   */
  public List<MonicaReviewOutputData> getMonicaReviewOutputDataList() {
    return this.monicaReviewOutputDataList;
  }


  /**
   * @param monicaReviewOutputDataList the monicaReviewOutputDataList to set
   */
  public void setMonicaReviewOutputDataList(final List<MonicaReviewOutputData> monicaReviewOutputDataList) {
    this.monicaReviewOutputDataList = monicaReviewOutputDataList;
  }


}
