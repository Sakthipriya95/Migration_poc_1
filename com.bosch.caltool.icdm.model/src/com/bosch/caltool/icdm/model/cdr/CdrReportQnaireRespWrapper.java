/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author say8cob
 */
public class CdrReportQnaireRespWrapper {

  // Key - A2L_RESP_ID, Value - Key - A2L_WP_ID and Value - Set of RvwQnaireRespVersion
  Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> wpRespQnaireRespVersMap = new HashMap<>();

  // Key - A2L_RESP_ID, Value - Key - A2L_WP_ID and Value - Set of all RvwQnaireResp including not baselined
  // rvwQnaireResp
  Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap = new HashMap<>();

  // Key - A2L_RESP_ID, Value - Key - A2L_WP_ID and Value - RvwQnaireRespVersion Status
  Map<Long, Map<Long, String>> wpRespQnaireRespVersStatusMap = new HashMap<>();

  // Key - Qnaire Resp Version Id, Value - RvwQnaireResponse
  Map<Long, RvwQnaireResponse> qnaireResponseMap = new HashMap<>();

  // Key - A2L Wp Id, Value - A2L Wp Name
  private Map<Long, String> wpIdAndNameMap = new HashMap<>();

  // Key - A2L Resp Id, Value -A2L Resp Name
  private Map<Long, String> respIdAndNameMap = new HashMap<>();

  /**
   * rvwQnaireRespVersModelMap is added to avoid additional service calls to fetch RvwQnaireRespVersion related details
   * during Data Assessment Report
   */
  // Key - Rvw Qnaire Response Version Id, Value - RvwQnaireRespVersModel
  private Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap = new HashMap<>();

  /**
   * @return the wpRespQnaireRespVersion
   */
  public Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> getWpRespQnaireRespVersMap() {
    return this.wpRespQnaireRespVersMap;
  }


  /**
   * @param wpRespQnaireRespVersion the wpRespQnaireRespVersion to set
   */
  public void setWpRespQnaireRespVersMap(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> wpRespQnaireRespVersion) {
    this.wpRespQnaireRespVersMap = wpRespQnaireRespVersion;
  }


  /**
   * @return the allWpRespQnaireRespVersMap KEY -Resp Id, VALUE - <KEY - Wp id,VALUE -set of all qnaire resp including
   *         not baselined qnaire resp>
   */
  public Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> getAllWpRespQnaireRespVersMap() {
    return this.allWpRespQnaireRespVersMap;
  }


  /**
   * @param allWpRespQnaireRespVersMap the allWpRespQnaireRespVersMap to set
   */
  public void setAllWpRespQnaireRespVersMap(
      final Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap) {
    this.allWpRespQnaireRespVersMap = allWpRespQnaireRespVersMap;
  }


  /**
   * @return the qnaireResponseMap
   */
  public Map<Long, RvwQnaireResponse> getQnaireResponseMap() {
    return this.qnaireResponseMap;
  }


  /**
   * @param qnaireResponseMap the qnaireResponseMap to set
   */
  public void setQnaireResponseMap(final Map<Long, RvwQnaireResponse> qnaireResponseMap) {
    this.qnaireResponseMap = qnaireResponseMap;
  }


  /**
   * @return the wpRespQnaireRespVersStatusMap
   */
  public Map<Long, Map<Long, String>> getWpRespQnaireRespVersStatusMap() {
    return this.wpRespQnaireRespVersStatusMap;
  }


  /**
   * @param wpRespQnaireRespVersStatusMap the wpRespQnaireRespVersStatusMap to set
   */
  public void setWpRespQnaireRespVersStatusMap(final Map<Long, Map<Long, String>> wpRespQnaireRespVersStatusMap) {
    this.wpRespQnaireRespVersStatusMap = wpRespQnaireRespVersStatusMap;
  }


  /**
   * @return the wpIdAndNameMap
   */
  public Map<Long, String> getWpIdAndNameMap() {
    return this.wpIdAndNameMap;
  }


  /**
   * @param wpIdAndNameMap the wpIdAndNameMap to set
   */
  public void setWpIdAndNameMap(final Map<Long, String> wpIdAndNameMap) {
    this.wpIdAndNameMap = wpIdAndNameMap;
  }


  /**
   * @return the respIdAndNameMap
   */
  public Map<Long, String> getRespIdAndNameMap() {
    return this.respIdAndNameMap;
  }


  /**
   * @param respIdAndNameMap the respIdAndNameMap to set
   */
  public void setRespIdAndNameMap(final Map<Long, String> respIdAndNameMap) {
    this.respIdAndNameMap = respIdAndNameMap;
  }


  /**
   * @return the rvwQnaireRespVersModelMap
   */
  public Map<Long, RvwQnaireRespVersModel> getRvwQnaireRespVersModelMap() {
    return this.rvwQnaireRespVersModelMap;
  }


  /**
   * @param rvwQnaireRespVersModelMap the rvwQnaireRespVersModelMap to set
   */
  public void setRvwQnaireRespVersModelMap(final Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap) {
    this.rvwQnaireRespVersModelMap = rvwQnaireRespVersModelMap;
  }


}
