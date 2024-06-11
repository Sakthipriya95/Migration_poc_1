/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author say8cob
 *
 */
public class CombinedReviewResultExcelExportData {
  
  private ReviewResultEditorData reviewResultEditorData;
  
  private Set<RvwWpResp> rvwWpRespSet = new HashSet<>();
  
  private PidcVersionWithDetails pidcVersionWithDetails;
  
  //Key - A2l_WPRESP_ID value - set of QnaireResponseCombinedModel for all the qnaire response under the RvwWpResp
  private Map<Long,Set<QnaireResponseCombinedModel>>  qnaireRespCombinedModelMap = new HashMap<>();

  
  /**
   * @return the reviewResultEditorData
   */
  public ReviewResultEditorData getReviewResultEditorData() {
    return reviewResultEditorData;
  }

  
  /**
   * @param reviewResultEditorData the reviewResultEditorData to set
   */
  public void setReviewResultEditorData(ReviewResultEditorData reviewResultEditorData) {
    this.reviewResultEditorData = reviewResultEditorData;
  }


  
  /**
   * @return the rvwWpRespSet
   */
  public Set<RvwWpResp> getRvwWpRespSet() {
    return rvwWpRespSet;
  }


  
  /**
   * @param rvwWpRespSet the rvwWpRespSet to set
   */
  public void setRvwWpRespSet(Set<RvwWpResp> rvwWpRespSet) {
    this.rvwWpRespSet = rvwWpRespSet;
  }


  
  /**
   * @return the qnaireRespCombinedModelMap
   */
  public Map<Long, Set<QnaireResponseCombinedModel>> getQnaireRespCombinedModelMap() {
    return qnaireRespCombinedModelMap;
  }


  
  /**
   * @param qnaireRespCombinedModelMap the qnaireRespCombinedModelMap to set
   */
  public void setQnaireRespCombinedModelMap(Map<Long, Set<QnaireResponseCombinedModel>> qnaireRespCombinedModelMap) {
    this.qnaireRespCombinedModelMap = qnaireRespCombinedModelMap;
  }


  
  /**
   * @return the pidcVersionWithDetails
   */
  public PidcVersionWithDetails getPidcVersionWithDetails() {
    return pidcVersionWithDetails;
  }


  
  /**
   * @param pidcVersionWithDetails the pidcVersionWithDetails to set
   */
  public void setPidcVersionWithDetails(PidcVersionWithDetails pidcVersionWithDetails) {
    this.pidcVersionWithDetails = pidcVersionWithDetails;
  }
  
  
  

}
