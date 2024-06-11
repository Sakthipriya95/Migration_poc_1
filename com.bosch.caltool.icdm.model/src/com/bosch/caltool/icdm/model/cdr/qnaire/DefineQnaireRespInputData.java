/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author dmr1cob
 */
public class DefineQnaireRespInputData {

  /**
   * Key - pidc variant id value - Pidc variant
   */
  private Map<Long, PidcVariant> allVariantMap = new HashMap<>();

  /**
   * Key - Qnaire id Value - list of qnaire resp variant
   */
  private Map<Long, List<RvwQnaireRespVariant>> qnaireRespVarMap = new HashMap<>();

  /**
   * Qnaire info for the selected pidc tree node
   */
  private PidcQnaireInfo qnaireInfo;


  /**
   * @return the allVariantMap
   */
  public Map<Long, PidcVariant> getAllVariantMap() {
    return this.allVariantMap;
  }


  /**
   * @param allVariantMap the allVariantMap to set
   */
  public void setAllVariantMap(final Map<Long, PidcVariant> allVariantMap) {
    this.allVariantMap = allVariantMap;
  }


  /**
   * @return the qnaireRespVarMap
   */
  public Map<Long, List<RvwQnaireRespVariant>> getQnaireRespVarMap() {
    return this.qnaireRespVarMap;
  }


  /**
   * @param qnaireRespVarMap the qnaireRespVarMap to set
   */
  public void setQnaireRespVarMap(final Map<Long, List<RvwQnaireRespVariant>> qnaireRespVarMap) {
    this.qnaireRespVarMap = qnaireRespVarMap;
  }


  /**
   * @return the qnaireInfo
   */
  public PidcQnaireInfo getQnaireInfo() {
    return this.qnaireInfo;
  }


  /**
   * @param qnaireInfo the qnaireInfo to set
   */
  public void setQnaireInfo(final PidcQnaireInfo qnaireInfo) {
    this.qnaireInfo = qnaireInfo;
  }


}
