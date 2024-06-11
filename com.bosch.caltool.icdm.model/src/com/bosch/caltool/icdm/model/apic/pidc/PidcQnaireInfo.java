/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dja7cob
 */
public class PidcQnaireInfo {

  private final Map<Long, PidcVariant> varIdVarMap = new HashMap<>();

  private final Map<Long, RvwQnaireResponse> noVarQnaireInfoMap = new HashMap<>();

  private final Map<Long, Map<Long, RvwQnaireResponse>> varQnaireInfoMap = new HashMap<>();

  /**
   * ------------------------variant reponsibility information along with workpackage-----------------------------------
   * key - variant id , Value - Map of <resp id, Map<wp id , Set<QnaireRespId>>>>
   */
  private final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap = new HashMap<>();

  /**
   * key - workpackage id , value - A2LWorkpackage
   */
  private final Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();

  /**
   * key - responsibility id , value - A2lResponsibility
   */
  private final Map<Long, A2lResponsibility> a2lRespMap = new HashMap<>();

  /**
   * key - Qnaire Resp id , value - RvwQnaireResponse
   */
  private final Map<Long, RvwQnaireResponse> rvwQnaireRespMap = new HashMap<>();

  /**
   * key - Qnaire Resp id , value - RvwQnaireRespVersion - Working Set
   */
  private final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = new HashMap<>();


  /**
   * @return the varIdVarMap
   */
  public Map<Long, PidcVariant> getVarIdVarMap() {
    return this.varIdVarMap;
  }

  /**
   * @return the noVarQnaireInfoMap
   */
  public Map<Long, RvwQnaireResponse> getNoVarQnaireInfoMap() {
    return this.noVarQnaireInfoMap;
  }

  /**
   * @return the varQnaireInfoMap
   */
  public Map<Long, Map<Long, RvwQnaireResponse>> getVarQnaireInfoMap() {
    return this.varQnaireInfoMap;
  }


  /**
   * @return the varRespWpQniareMap
   */
  public Map<Long, Map<Long, Map<Long, Set<Long>>>> getVarRespWpQniareMap() {
    return this.varRespWpQniareMap;
  }


  /**
   * @return the a2lWpMap
   */
  public Map<Long, A2lWorkPackage> getA2lWpMap() {
    return this.a2lWpMap;
  }


  /**
   * @return the a2lRespMap
   */
  public Map<Long, A2lResponsibility> getA2lRespMap() {
    return this.a2lRespMap;
  }


  /**
   * @return the rvwQnaireRespMap
   */
  public Map<Long, RvwQnaireResponse> getRvwQnaireRespMap() {
    return this.rvwQnaireRespMap;
  }


  /**
   * @return the rvwQnaireRespVersMap
   */
  public Map<Long, RvwQnaireRespVersion> getRvwQnaireRespVersMap() {
    return this.rvwQnaireRespVersMap;
  }


}
