/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author say8cob
 */
public class PidcA2lTreeStructureModel {

  /**
   * Key - Variant Id Value - Pidc Variant obj
   */
  private Map<Long, PidcVariant> pidcVariantMap = new HashMap<>();

  /**
   * ------------------------variant reponsibility information along with workpackage-----------------------------------
   * key - variant id , Value - Map of <resp id, Map<wp id , Set<QnaireRespId>>>>
   */
  private Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap = new HashMap<>();

  /**
   * key - workpackage id , value - A2LWorkpackage
   */
  private Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();

  /**
   * key - responsibility id , value - A2lResponsibility
   */
  private Map<Long, A2lResponsibility> a2lRespMap = new HashMap<>();

  /**
   * key - Qnaire Resp id , value - RvwQnaireResponse
   */
  private Map<Long, RvwQnaireResponse> rvwQnaireRespMap = new HashMap<>();

  /**
   * key - Qnaire Resp id , value - RvwQnaireRespVersion - Working Set
   */
  private Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = new HashMap<>();

  /**
   * To get the status of a2l wp responsibility combination; key - pidc variant id,value-map of key- responsibility id
   * ,value-map of key-workpackage id and WP Finished status as String
   */
  Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap = new HashMap<>();

  /**
   * To get the a2l wp responsibility combination ; key - pidc variant id,value-map of key- responsibility id ,value-map
   * of key-workpackage id and A2lWPRespModel
   */
  Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap = new HashMap<>();


  /**
   * @return the pidcVariantMap
   */
  public Map<Long, PidcVariant> getPidcVariantMap() {
    return this.pidcVariantMap;
  }


  /**
   * @param pidcVariantMap the pidcVariantMap to set
   */
  public void setPidcVariantMap(final Map<Long, PidcVariant> pidcVariantMap) {
    this.pidcVariantMap = pidcVariantMap;
  }


  /**
   * @return the varRespWpQniareMap
   */
  public Map<Long, Map<Long, Map<Long, Set<Long>>>> getVarRespWpQniareMap() {
    return this.varRespWpQniareMap;
  }


  /**
   * @param varRespWpQniareMap the varRespWpQniareMap to set
   */
  public void setVarRespWpQniareMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap) {
    this.varRespWpQniareMap = varRespWpQniareMap;
  }


  /**
   * @return the a2lWpMap
   */
  public Map<Long, A2lWorkPackage> getA2lWpMap() {
    return this.a2lWpMap;
  }


  /**
   * @param a2lWpMap the a2lWpMap to set
   */
  public void setA2lWpMap(final Map<Long, A2lWorkPackage> a2lWpMap) {
    this.a2lWpMap = a2lWpMap;
  }


  /**
   * @return the a2lRespMap
   */
  public Map<Long, A2lResponsibility> getA2lRespMap() {
    return this.a2lRespMap;
  }


  /**
   * @param a2lRespMap the a2lRespMap to set
   */
  public void setA2lRespMap(final Map<Long, A2lResponsibility> a2lRespMap) {
    this.a2lRespMap = a2lRespMap;
  }


  /**
   * @return the wpRespStatusMap
   */
  public Map<Long, Map<Long, Map<Long, String>>> getWpRespStatusMap() {
    return this.wpRespStatusMap;
  }


  /**
   * @param wpRespStatusMap the wpRespStatusMap to set
   */
  public void setWpRespStatusMap(final Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap) {
    this.wpRespStatusMap = wpRespStatusMap;
  }


  /**
   * @return the rvwQnaireRespMap
   */
  public Map<Long, RvwQnaireResponse> getRvwQnaireRespMap() {
    return this.rvwQnaireRespMap;
  }


  /**
   * @param rvwQnaireRespMap the rvwQnaireRespMap to set
   */
  public void setRvwQnaireRespMap(final Map<Long, RvwQnaireResponse> rvwQnaireRespMap) {
    this.rvwQnaireRespMap = rvwQnaireRespMap;
  }


  /**
   * @return the rvwQnaireRespVersMap
   */
  public Map<Long, RvwQnaireRespVersion> getRvwQnaireRespVersMap() {
    return this.rvwQnaireRespVersMap;
  }


  /**
   * @param rvwQnaireRespVersMap the rvwQnaireRespVersMap to set
   */
  public void setRvwQnaireRespVersMap(final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap) {
    this.rvwQnaireRespVersMap = rvwQnaireRespVersMap;
  }


  /**
   * @return the respWPA2lWpRespModelMap
   */
  public Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> getRespWPA2lWpRespModelMap() {
    return this.respWPA2lWpRespModelMap;
  }


  /**
   * @param respWPA2lWpRespModelMap the respWPA2lWpRespModelMap to set
   */
  public void setRespWPA2lWpRespModelMap(
      final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap) {
    this.respWPA2lWpRespModelMap = respWPA2lWpRespModelMap;
  }

}
